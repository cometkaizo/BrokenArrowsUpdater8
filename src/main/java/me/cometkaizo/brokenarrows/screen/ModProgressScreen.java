package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.brokenarrows.ModUpdater;
import me.cometkaizo.screen.*;
import me.cometkaizo.screen.color.Palette;
import me.cometkaizo.util.StringUtils;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ModProgressScreen extends ScreenGui {
    public static final int MARGIN = 50, MARGIN_SMALL = 20;
    public static final Length MARGIN_LEN = Length.abs(MARGIN), MARGIN_SMALL_LEN = Length.abs(MARGIN_SMALL);
    protected String title;
    private String message;
    protected Panel panel;
    protected final ModUpdater updater;

    public ModProgressScreen(ModUpdater updater, BrokenArrowsApp app) {
        super(app);
        this.updater = updater;
        this.title = "Updating Mods...";
        this.message = "";
        updater.listeners.add(this::onModUpdateEvent);
    }

    @Override
    public void init() {
        super.init();
        panel = new Panel(app);
        addNestedComponent(panel);
    }

    public void onModUpdateEvent(ModUpdater.Event event) {
        if (event instanceof ModUpdater.Event.End) {
            app.panel().removeScreen(this);
            updater.listeners.remove((Consumer<ModUpdater.Event>) this::onModUpdateEvent);
        } else {
            String desc = event.getString();
            setMessage(message + desc + "\n");
            title = desc;
        }
    }

    public class Panel extends PanelGui {
        protected Corner corner;
        protected TitleBar titleBar;
        protected SideBar sideBar;
        protected MessagePanel messagePanel;
        public Panel(BrokenArrowsApp app) {
            super(0, 0, 1D, 1D, new GuiBackground(app, Palette::dark), app);
        }

        @Override
        public void init() {
            super.init();

            corner = new Corner(app);
            titleBar = new TitleBar(app);
            sideBar = new SideBar(app);
            messagePanel = new MessagePanel(app);

            addNestedComponent(corner);
            addNestedComponent(sideBar);
            addNestedComponent(messagePanel);
            addNestedComponent(titleBar);
        }

        public class TitleBar extends PanelGui {
            public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 30);
            public TitleBar(BrokenArrowsApp app) {
                super(Coordinate.abs(Length.direct(() -> sideBar.right() + app.resolveX(MARGIN_SMALL_LEN)), MARGIN),
                        Coordinate.abs(Length.direct(() -> 1 - sideBar.width() - app.resolveX(MARGIN_LEN) * 2 - app.resolveX(MARGIN_SMALL_LEN)), 80),
                        new GuiBackground(app, Palette::light), app);
            }

            @Override
            public void render(FullResRenderer r, Graphics2D g) {
                super.render(r, g);
                g.setColor(app.palette().textLight());
                g.setFont(FONT);
                r.renderString(g, title, x() + app.resolveX(MARGIN_LEN), y() + height() / 2, 0, 0.35);
            }
        }

        public static class Corner extends PanelGui {
            public Corner(BrokenArrowsApp app) {
                super(Coordinate.abs(MARGIN, MARGIN),
                        Coordinate.abs(80, 80),
                        new GuiBackground(app, Palette::light), app);
            }
        }

        public class SideBar extends PanelGui {
            public SideBar(BrokenArrowsApp app) {
                super(Coordinate.abs(MARGIN, Length.direct(() -> corner.bottom() + app.resolveY(MARGIN_SMALL_LEN))),
                        Coordinate.abs(80, Length.direct(() -> 1 - corner.height() - app.resolveY(MARGIN_LEN) * 2 - app.resolveY(MARGIN_SMALL_LEN))),
                        new GuiBackground(app, Palette::medium), app);
            }
        }

        public class MessagePanel extends PanelGui {
            public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
            protected List<String> lines;
            protected boolean updateLines = true;
            public MessagePanel(BrokenArrowsApp app) {
                super(Coordinate.direct(() -> titleBar.x(), () -> titleBar.bottom() + app.resolveY(MARGIN_SMALL_LEN)),
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
        }
    }

    public String getMessage() {
        return message;
    }
    private void setMessage(String message) {
        this.message = message;
        if (panel != null && panel.messagePanel != null) panel.messagePanel.updateLines = true;
    }
}
