package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.brokenarrows.Diagnostic;
import me.cometkaizo.screen.*;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;
import me.cometkaizo.util.StringUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ControlsScreen extends ScreenGui {
    public static final int MARGIN = 50, MARGIN_SMALL = 20;
    public static final Length MARGIN_LEN = Length.abs(MARGIN), MARGIN_SMALL_LEN = Length.abs(MARGIN_SMALL);
    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#########");
    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
    }
    protected final String title;
    protected Panel panel;

    protected int prevAutoUpdateInterval;
    public ControlsScreen(BrokenArrowsApp app) {
        super(app);
        this.title = "Control Panel";
    }

    @Override
    public void init() {
        super.init();
        panel = new Panel(app);
        addNestedComponent(panel);
    }

    @Override
    public void onAdded() {
        super.onAdded();
        prevAutoUpdateInterval = app.settings().autoUpdateInterval;
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        app.save();
        if (prevAutoUpdateInterval != app.settings().autoUpdateInterval) app.rescheduleAutoUpdate();
        prevAutoUpdateInterval = -1;
    }

    public class Panel extends PanelGui {
        protected TitleBar titleBar;
        protected SideBar sideBar;
        protected AllSettingsPanel allSettingsPanel;
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
            allSettingsPanel = new AllSettingsPanel(app);

            addNestedComponent(closeButton);
            addNestedComponent(sideBar);
            addNestedComponent(allSettingsPanel);
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
        
        public class AllSettingsPanel extends PanelGui {
            protected boolean updateLines = true;
            protected List<ControlPanel> settings = new ArrayList<>(3);
            public AllSettingsPanel(BrokenArrowsApp app) {
                super(Coordinate.direct(() -> titleBar.x(), () -> closeButton.bottom() + app.resolveY(MARGIN_SMALL_LEN)),
                        Coordinate.direct(() -> titleBar.width(), () -> 1 - app.resolveY(MARGIN_LEN) - allSettingsPanel.top()),
                        new GuiBackground(app, Palette::medium), app);
            }

            @Override
            public void init() {
                super.init();
                add(new UpdateModsControl(app));
                add(new UpdateForgeControl(app));
                add(new StealMinecraftBinControl(app));

                settings.get(0).updateY();
                settings.forEach(this::addNestedComponent);
            }

            private void add(ControlPanel panel) {
                panel.index = settings.size();
                settings.add(panel);
            }

            public class ControlPanel extends PanelGui {
                public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
                protected int index;
                private String message;
                protected List<String> lines;
                public ControlPanel(String message, BrokenArrowsApp app) {
                    super(Coordinate.abs(app.resolveXAbs(allSettingsPanel.position.x()) + app.resolveXAbs(MARGIN_SMALL_LEN), 0),
                            Coordinate.abs(0, 0),
                            new GuiBackground(app, Palette::light), app);
                    this.message = message;
                }

                @Override
                public void init() {
                    super.init();
                    size.setX(Length.direct(() -> 1 - left() - (1 - allSettingsPanel.right()) - app.resolveX(MARGIN_SMALL_LEN)));
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

                    if (updateLines) {
                        lines = StringUtils.createLines(message, textWidth, g.getFontMetrics());
                        updateHeight(textHeight, lineHeight);
                        updateNextSettingY();
                    }

                    for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                        String line = lines.get(lineIndex);

                        r.renderString(g, line, allSettingsPanel.x() + app.resolveX(MARGIN_LEN), y() + app.resolveY(MARGIN_SMALL_LEN) + r.toPercentY(lineIndex * lineHeight), 0, 1);
                    }
                }

                protected void updateHeight(int textHeight, int lineHeight) {
                    size.setY(textHeight + (lines.size() - 1) * lineHeight + app.resolveYAbs(MARGIN_SMALL_LEN) * 2);
                }

                protected void updateY() {
                    ControlPanel prevSetting = prevSetting();
                    position.setY(Length.direct(() -> (prevSetting != null ? prevSetting.bottom() : allSettingsPanel.y()) + app.resolveY(MARGIN_SMALL_LEN)));

                    updateNextSettingY();
                }

                protected void updateNextSettingY() {
                    ControlPanel nextSetting = nextSetting();
                    if (nextSetting != null) nextSetting.updateY();
                }

                protected ControlPanel prevSetting() {
                    return index == 0 ? null : settings.get(index - 1);
                }
                protected ControlPanel nextSetting() {
                    return index == settings.size() - 1 ? null : settings.get(index + 1);
                }

                public void setMessage(String message) {
                    if (this.message.equals(message)) return;
                    this.message = message;
                    updateLines = true;
                }
            }

            public class UpdateModsControl extends ControlPanel {
                protected ButtonGui updateButton;

                public UpdateModsControl(BrokenArrowsApp app) {
                    super(getMessage(), app);
                }

                @Override
                public void init() {
                    super.init();
                    updateButton = app.buttonStyle.medium()
                            .setAllTextSize(18)
                            .setPos(Coordinate.direct(() -> right() - app.resolveX(MARGIN_SMALL_LEN) - updateButton.width(), () -> top() + app.resolveY(MARGIN_SMALL_LEN)))
                            .setSize(Coordinate.abs(90, 40))
                            .setAllText("Update")
                            .setAction(b -> updateMods())
                            .build();

                    addNestedComponent(updateButton);
                }

                @Override
                protected void updateHeight(int textHeight, int lineHeight) {
                    size.setY(app.resolveYAbs(Length.abs(40)) + app.resolveYAbs(MARGIN_SMALL_LEN) * 2);
                }

                private void updateMods() {
                    if (app.isUpdatingMods()) return;
                    app.updateMods(updater -> app.panel().addScreen(new ModProgressScreen(updater, app)), app::showModUpdateFeedback);
                }

                private static String getMessage() {
                    return "Update mods";
                }
            }

            public class UpdateForgeControl extends ControlPanel {
                protected ButtonGui updateButton;

                public UpdateForgeControl(BrokenArrowsApp app) {
                    super(getMessage(), app);
                }

                @Override
                public void init() {
                    super.init();
                    updateButton = app.buttonStyle.medium()
                            .setAllTextSize(18)
                            .setPos(Coordinate.direct(() -> right() - app.resolveX(MARGIN_SMALL_LEN) - updateButton.width(), () -> top() + app.resolveY(MARGIN_SMALL_LEN)))
                            .setSize(Coordinate.abs(90, 40))
                            .setAllText("Update")
                            .setAction(b -> updateForge())
                            .build();

                    addNestedComponent(updateButton);
                }

                @Override
                protected void updateHeight(int textHeight, int lineHeight) {
                    size.setY(app.resolveYAbs(Length.abs(40)) + app.resolveYAbs(MARGIN_SMALL_LEN) * 2);
                }

                private void updateForge() {
                    if (app.isUpdatingForge()) return;
                    app.updateForge(updater -> app.panel().addScreen(new ForgeProgressScreen(updater, app)), app::showForgeUpdateFeedback);
                }

                private static String getMessage() {
                    return "Update forge";
                }
            }

            public class StealMinecraftBinControl extends ControlPanel {
                protected ButtonGui stealButton;

                public StealMinecraftBinControl(BrokenArrowsApp app) {
                    super(getMessage(), app);
                }

                @Override
                public void init() {
                    super.init();
                    stealButton = app.buttonStyle.medium()
                            .setAllTextSize(18)
                            .setPos(Coordinate.direct(() -> right() - app.resolveX(MARGIN_SMALL_LEN) - stealButton.width(), () -> top() + app.resolveY(MARGIN_SMALL_LEN)))
                            .setSize(Coordinate.abs(90, 40))
                            .setAllText("Setup")
                            .setAction(b -> steal())
                            .build();

                    addNestedComponent(stealButton);
                }

                @Override
                protected void updateHeight(int textHeight, int lineHeight) {
                    size.setY(app.resolveYAbs(Length.abs(40)) + app.resolveYAbs(MARGIN_SMALL_LEN) * 2);
                }

                private void steal() {
                    List<Diagnostic> problems = new ArrayList<>(1);
                    app.minecraftLauncher().setupBypass(problems);

                    showLauncherFeedback(problems);
                }
                public void showLauncherFeedback(List<Diagnostic> problems) {
                    if (problems.isEmpty()) app.panel().addScreen(app.getLauncherSuccessScreen());
                    else app.panel().addScreen(app.getLauncherErrorScreen(problems));
                }

                private static String getMessage() {
                    return "Setup launcher bypass\nMinecraft must be running (on the correct version)";
                }
            }
        }
    }
}
