package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.*;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;
import me.cometkaizo.util.StringUtils;

import java.awt.*;
import java.util.List;

public class InfoScreen extends ScreenGui {
    public static final int MARGIN = 50, MARGIN_SMALL = 20;
    public static final Length MARGIN_LEN = Length.abs(MARGIN), MARGIN_SMALL_LEN = Length.abs(MARGIN_SMALL);
    protected final String title;
    protected final String message;
    protected Panel panel;
    public InfoScreen(String title, String message, BrokenArrowsApp app) {
        super(app);
        this.title = modify(title);
        this.message = modify(message);
    }

    private static String modify(String text) {
        return text.replaceAll("\t", "    ");
    }

    @Override
    public void init() {
        super.init();
        panel = new Panel(app);
        addNestedComponent(panel);
    }

    public void close() {
        app.panel().removeScreen(this);
    }

    public class Panel extends PanelGui {
        protected TitleBar titleBar;
        protected SideBar sideBar;
        protected MessagePanel messagePanel;
        protected ButtonGui closeButton;
        public Panel(BrokenArrowsApp app) {
            super(0, 0, 1D, 1D, new GuiBackground(app, Palette::dark), app);
        }

        @Override
        public void init() {
            super.init();
            closeButton = app.buttonStyle.light()
                    .setAllTextSize(24)
                    .setPos(Coordinate.abs(MARGIN, MARGIN))
                    .setSize(Coordinate.abs(80, 80))
                    .setAllText("<")
                    .setAction(b -> close())
                    .build();

            titleBar = new TitleBar(app);
            sideBar = new SideBar(app);
            messagePanel = new MessagePanel(app);

            addNestedComponent(closeButton);
            addNestedComponent(sideBar);
            addNestedComponent(messagePanel);
            addNestedComponent(titleBar);
        }

        public class TitleBar extends PanelGui {
            public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 30);
            protected GuiText text;
            public TitleBar(BrokenArrowsApp app) {
                super(Coordinate.abs(Length.direct(() -> closeButton.right() + app.resolveX(MARGIN_SMALL_LEN)), MARGIN),
                        Coordinate.abs(Length.direct(() -> 1 - closeButton.width() - app.resolveX(MARGIN_LEN) * 2 - app.resolveX(MARGIN_SMALL_LEN)), 80),
                        new GuiBackground(app, Palette::light), app);
            }

            @Override
            public void init() {
                super.init();
                text = new GuiText(title, FONT, new ColorSource(app, Palette::textLight), 0, 0.35);
            }

            @Override
            public void render(FullResRenderer r, Graphics2D g) {
                super.render(r, g);
                text.render(r, g, x() + app.resolveX(MARGIN_LEN), y() + height() / 2, 0, 0);
            }
        }

        public class SideBar extends PanelGui {
            public SideBar(BrokenArrowsApp app) {
                super(Coordinate.abs(MARGIN, Length.direct(() -> closeButton.bottom() + app.resolveY(MARGIN_SMALL_LEN))),
                        Coordinate.abs(80, Length.direct(() -> 1 - closeButton.height() - app.resolveY(MARGIN_LEN) * 2 - app.resolveY(MARGIN_SMALL_LEN))),
                        new GuiBackground(app, Palette::medium), app);
            }
        }

        public class MessagePanel extends PanelGui {
            public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
            protected List<String> lines;
            protected boolean updateLines = true;
            public MessagePanel(BrokenArrowsApp app) {
                super(Coordinate.direct(() -> titleBar.x(), () -> closeButton.bottom() + app.resolveY(MARGIN_SMALL_LEN)),
                        Coordinate.direct(() -> titleBar.width(), () -> 1 - app.resolveY(MARGIN_LEN) - messagePanel.top()),
                        new GuiBackground(app, Palette::medium), app);
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

                int margin = app.resolveXAbs(MARGIN_LEN);
                int textLeft = app.resolveXAbs(position.x()) + margin;
                int textRight = r.toScreenX(right()) - margin;
                int textWidth = Math.max(textRight - textLeft, 100);
                int textHeight = g.getFontMetrics().getHeight();
                int lineHeight = (int) (textHeight * 0.7);

                if (updateLines) lines = StringUtils.createLines(message, textWidth, g.getFontMetrics());

                for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                    String line = lines.get(lineIndex);

                    r.renderString(g, line, x() + app.resolveX(MARGIN_LEN), y() + app.resolveY(MARGIN_SMALL_LEN) + r.toPercentY(lineIndex * lineHeight), 0, 1);
                }
            }
        }
    }
}
