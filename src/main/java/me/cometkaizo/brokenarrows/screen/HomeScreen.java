package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.brokenarrows.Screens;
import me.cometkaizo.launcher.driver.ExceptionManager;
import me.cometkaizo.screen.*;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;
import me.cometkaizo.util.DownloadUtils;
import me.cometkaizo.util.StringUtils;

import java.awt.*;
import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

public class HomeScreen extends ScreenGui {
    public static final int MARGIN = 50, MARGIN_SMALL = 20;
    public static final Length MARGIN_LEN = Length.abs(MARGIN), MARGIN_SMALL_LEN = Length.abs(MARGIN_SMALL);
    protected Panel panel;
    public HomeScreen(BrokenArrowsApp app) {
        super(app);
    }

    @Override
    public void init() {
        super.init();
        panel = new Panel(app);
        addNestedComponent(panel);
    }

    public static class Panel extends PanelGui {
        protected NewsPanel newsPanel;
        protected MenuGui menu;
        protected ButtonGui updateModsButton, updateForgeButton, settingsButton;
        protected GuiBackground buttonBackground = new GuiBackground(new ColorSource(app, Palette::light));
        protected Font buttonFont = new Font(Font.DIALOG, Font.BOLD, 24);
        protected ColorSource buttonTextColor = new ColorSource(app, Palette::textLight);
        protected GuiText updateModsButtonText = new GuiText("Update Mods", buttonFont, buttonTextColor),
                updateForgeButtonText = new GuiText("Update Forge", buttonFont, buttonTextColor),
                settingsButtonText = new GuiText("Settings", buttonFont, buttonTextColor);
        public Panel(BrokenArrowsApp app) {
            super(0, 0, 1D, 1D, new GuiBackground(app, Palette::dark), app);
        }

        @Override
        public void init() {
            super.init();
            newsPanel = new NewsPanel(app);

            menu = new MenuGui.Builder(Coordinate.abs(MARGIN, MARGIN),
                    Coordinate.abs(Length.direct(() -> 1 - app.resolveX(MARGIN_LEN) * 2), 80),
                    RepeaterGui.Axis.HORIZONTAL, RepeaterGui.SpacingMode.START, MARGIN_SMALL_LEN, MARGIN_SMALL_LEN, app)
                    .setButtonShape(new Rectangle())
                    .setButtonSize(Coordinate.abs(240, 80))
                    .setAllButtonBackgrounds(buttonBackground)
                    .build();

            updateModsButton = menu.addButton(new MenuGui.ButtonBuilder().setAllText(updateModsButtonText).setAction(b -> updateMods()));
            updateForgeButton = menu.addButton(new MenuGui.ButtonBuilder().setAllText(updateForgeButtonText).setAction(b -> updateForge()));
            settingsButton = menu.addButton(new MenuGui.ButtonBuilder().setAllText(settingsButtonText).setAction(b -> openSettings()));

            addNestedComponent(newsPanel);
            addNestedComponent(menu);
        }

        private void updateMods() {
            app.updateMods(updater -> app.panel().addScreen(new ModProgressScreen(updater, app)), app::showModUpdateFeedback);
        }
        private void updateForge() {
            app.updateForge(updater -> app.panel().addScreen(new ForgeProgressScreen(updater, app)), app::showForgeUpdateFeedback);
        }

        private void openSettings() {
            app.panel().addScreen(Screens.SETTINGS.get());
        }

        public class NewsPanel extends PanelGui {
            public static final URI NEWS_DOWNLOAD_LINK = URI.create("https://www.dropbox.com/scl/fi/yn8cltakt681kdh595c2d/news.txt?rlkey=msdo20l9yxir3gxntaptc9gv0&dl=1");
            public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);

            protected String message;
            protected List<String> lines = List.of("Fetching update info...");
            protected boolean updateLines = true;

            public NewsPanel(BrokenArrowsApp app) {
                super(Coordinate.abs(MARGIN, Length.direct(() -> menu.bottom() + app.resolveY(MARGIN_SMALL_LEN))),
                        Coordinate.direct(() -> 1 - newsPanel.left() * 2, () -> 1 - app.resolveY(MARGIN_LEN) - newsPanel.top()),
                        new GuiBackground(app, Palette::medium), app);
            }

            private String downloadNews() {
                String news;
                try {
                    news = DownloadUtils.downloadStr(NEWS_DOWNLOAD_LINK.toURL());
                } catch (UnknownHostException | NoRouteToHostException e) {
                    news = "Could not connect to '" + NEWS_DOWNLOAD_LINK.getHost() + "'. The website may be down or you may not be connected to the internet";
                } catch (Exception e) {
                    news = "An Exception occurred\n\n" + StringUtils.getAbbreviatedMessage(e);
                } return news;
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
                    updateLines = false;
                    if (message == null) app.addTask(() -> updateLines(g, marginY, lineHeight, textWidth), ExceptionManager.PRINT);
                    else updateLines(g, marginY, lineHeight, textWidth);
                }

                for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                    String line = lines.get(lineIndex);

                    r.renderString(g, line,
                            x() + app.resolveX(MARGIN_LEN),
                            y() + app.resolveY(MARGIN_SMALL_LEN) + r.toPercentY(lineIndex * lineHeight),
                            0, 1);
                }
            }

            private void updateLines(Graphics2D g, int marginY, int lineHeight, int textWidth) {
                String message = getMessage();

                int maxLines = (app.resolveYAbs(size.y()) - marginY * 2) / lineHeight;
                lines = StringUtils.createLines(message, textWidth, g.getFontMetrics());
                lines = lines.subList(Math.max(0, lines.size() - maxLines), lines.size());
            }

            private String getMessage() {
                if (message == null) message = downloadNews();
                return message;
            }
        }
    }
}
