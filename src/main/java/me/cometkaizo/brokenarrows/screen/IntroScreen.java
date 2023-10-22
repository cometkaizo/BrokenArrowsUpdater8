package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.brokenarrows.Screens;
import me.cometkaizo.screen.*;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;
import me.cometkaizo.util.StringUtils;

import java.awt.*;
import java.util.List;

public class IntroScreen extends ScreenGui {
    public static final int MARGIN = 50, MARGIN_SMALL = 20;
    public static final Length MARGIN_LEN = Length.abs(MARGIN), MARGIN_SMALL_LEN = Length.abs(MARGIN_SMALL);
    protected final String title;
    protected final String message;
    protected Panel panel;
    public IntroScreen(BrokenArrowsApp app) {
        super(app);
        title = "Hello there :)";
        message = "Please select your Minecraft folder";
    }

    @Override
    public void init() {
        super.init();
        panel = new Panel(app);
        addNestedComponent(panel);
    }

    public class Panel extends PanelGui {
        protected Panel.TitleBar titleBar;
        protected Panel.MessagePanel messagePanel;
        public Panel(BrokenArrowsApp app) {
            super(0, 0, 1D, 1D, new GuiBackground(app, Palette::dark), app);
        }

        @Override
        public void init() {
            super.init();

            titleBar = new Panel.TitleBar(app);
            messagePanel = new Panel.MessagePanel(app);

            addNestedComponent(messagePanel);
            addNestedComponent(titleBar);
        }

        public class TitleBar extends PanelGui {
            public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 30);
            public TitleBar(BrokenArrowsApp app) {
                super(Coordinate.abs(MARGIN, MARGIN),
                        Coordinate.abs(Length.direct(() -> 1 - app.resolveX(MARGIN_LEN) * 2), 80),
                        new GuiBackground(app, Palette::light), app);
            }

            @Override
            public void init() {
                super.init();
            }

            @Override
            public void render(FullResRenderer r, Graphics2D g) {
                super.render(r, g);
                g.setColor(app.palette().textLight());
                g.setFont(FONT);
                r.renderString(g, title, x() + app.resolveX(MARGIN_LEN), y() + height() / 2, 0, 0.35);
            }
        }

        public class MessagePanel extends PanelGui {
            protected ButtonGui setMcDirButton;
            protected Length setMcDirButtonWidth = Length.abs(200), setMcDirButtonHeight = Length.abs(80);
            protected GuiBackground setMcDirButtonBackground = new GuiBackground(new ColorSource(app, Palette::light));
            protected GuiText setMcDirButtonText = new GuiText("Select", new Font(Font.DIALOG, Font.PLAIN, 24), new ColorSource(app, Palette::textMedium));
            protected ButtonGui.Border setMcDirButtonBorder = null;
            public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
            protected List<String> lines;
            protected boolean updateLines = true;
            public MessagePanel(BrokenArrowsApp app) {
                super(Coordinate.abs(MARGIN, Length.direct(() -> titleBar.bottom() + app.resolveY(MARGIN_SMALL_LEN))),
                        Coordinate.direct(() -> 1 - messagePanel.left() * 2, () -> 1 - app.resolveY(MARGIN_LEN) - messagePanel.top()),
                        new GuiBackground(app, Palette::medium), app);
            }

            @Override
            public void init() {
                super.init();
                setMcDirButton = new ButtonGui(Coordinate.abs(Length.direct(() -> panel.messagePanel.right() - app.resolveX(MARGIN_SMALL_LEN) - setMcDirButton.width()), panel.messagePanel.top() + app.resolveY(MARGIN_SMALL_LEN)),
                        Coordinate.of(setMcDirButtonWidth, setMcDirButtonHeight),
                        setMcDirButtonText, setMcDirButtonText, setMcDirButtonText,
                        new Rectangle(),
                        setMcDirButtonBackground, setMcDirButtonBackground, setMcDirButtonBackground,
                        setMcDirButtonBorder, setMcDirButtonBorder, setMcDirButtonBorder,
                        b -> selectMcDir(), app);

                addNestedComponent(setMcDirButton);
            }

            @Override
            public void onScreenResized() {
                super.onScreenResized();
                updateLines = true;
            }

            @Override
            public void render(FullResRenderer r, Graphics2D g) {
                super.render(r, g);
                g.setFont(FONT);
                g.setColor(app.palette().textMedium());

                int margin = app.resolveXAbs(MARGIN_LEN), marginY = app.resolveYAbs(MARGIN_SMALL_LEN);
                int textLeft = app.resolveXAbs(position.x()) + margin;
                int textRight = r.toScreenX(right()) - margin;
                int textWidth = Math.max(textRight - textLeft, 100);
                int textHeight = g.getFontMetrics().getHeight();
                int lineHeight = (int) (textHeight * 0.7);

                if (updateLines) {
                    int maxLines = (app.resolveYAbs(size.y()) - marginY * 2) / lineHeight;
                    lines = StringUtils.createLines(message, textWidth, g.getFontMetrics());
                    lines = lines.subList(Math.max(0, lines.size() - maxLines), lines.size());
                }

                for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                    String line = lines.get(lineIndex);

                    r.renderString(g, line,
                            x() + app.resolveX(MARGIN_LEN),
                            y() + app.resolveY(MARGIN_SMALL_LEN) + r.toPercentY(lineIndex * lineHeight),
                            0, 1);
                }
            }


            private void selectMcDir() {
                if (app.selectMcDir()) {
                    app.panel().removeAndAddScreen(IntroScreen.this, Screens.HOME.get());
                }
            }
        }
    }
}
