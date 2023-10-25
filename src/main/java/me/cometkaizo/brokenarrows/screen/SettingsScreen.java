package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.*;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;
import me.cometkaizo.util.MathUtils;
import me.cometkaizo.util.StringUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsScreen extends ScreenGui {
    public static final int MARGIN = 50, MARGIN_SMALL = 20;
    public static final Length MARGIN_LEN = Length.abs(MARGIN), MARGIN_SMALL_LEN = Length.abs(MARGIN_SMALL);
    protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#########");
    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
    }
    protected final String title;
    protected Panel panel;

    protected int prevAutoUpdateInterval;
    public SettingsScreen(BrokenArrowsApp app) {
        super(app);
        this.title = "Settings";
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

    public void close() {
        app.panel().removeScreen(this);
        app.save();
        if (prevAutoUpdateInterval != app.settings().autoUpdateInterval) app.rescheduleAutoUpdate();
        prevAutoUpdateInterval = -1;
    }

    public class Panel extends PanelGui {
        protected TitleBar titleBar;
        protected SideBar sideBar;
        protected AllSettingsPanel allSettingsPanel;
        protected ButtonGui closeButton;
        protected GuiBackground closeBackground = new GuiBackground(new ColorSource(app, Palette::light));
        protected GuiText closeButtonText = new GuiText("<", new Font(Font.DIALOG, Font.BOLD, 24), new ColorSource(app, Palette::textMedium));
        protected ButtonGui.Border closeButtonBorder = null;
        public Panel(BrokenArrowsApp app) {
            super(0, 0, 1D, 1D, new GuiBackground(app, Palette::dark), app);
        }

        @Override
        public void init() {
            super.init();
            closeButton = new ButtonGui(
                    Coordinate.abs(MARGIN, MARGIN),
                    Coordinate.abs(80, 80),
                    closeButtonText, closeButtonText, closeButtonText,
                    new Rectangle(),
                    closeBackground, closeBackground, closeBackground,
                    closeButtonBorder, closeButtonBorder, closeButtonBorder,
                    b -> close(), app);

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
            protected List<SettingPanel> settings = new ArrayList<>(3);
            public AllSettingsPanel(BrokenArrowsApp app) {
                super(Coordinate.direct(() -> titleBar.x(), () -> closeButton.bottom() + app.resolveY(MARGIN_SMALL_LEN)),
                        Coordinate.direct(() -> titleBar.width(), () -> 1 - app.resolveY(MARGIN_LEN) - allSettingsPanel.top()),
                        new GuiBackground(app, Palette::medium), app);
            }

            @Override
            public void init() {
                super.init();
                add(new AutoUpdateIntervalSetting(app));
                add(new MinecraftFolderSetting(app));
                add(new UpdateOnStartSetting(app));

                settings.get(0).updateY();
                settings.forEach(this::addNestedComponent);
            }

            private void add(SettingPanel panel) {
                panel.index = settings.size();
                settings.add(panel);
            }

            public class SettingPanel extends PanelGui {
                public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
                protected int index;
                private String message;
                protected List<String> lines;
                public SettingPanel(String message, BrokenArrowsApp app) {
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
                    SettingPanel prevSetting = prevSetting();
                    position.setY(Length.direct(() -> (prevSetting != null ? prevSetting.bottom() : allSettingsPanel.y()) + app.resolveY(MARGIN_SMALL_LEN)));

                    updateNextSettingY();
                }

                protected void updateNextSettingY() {
                    SettingPanel nextSetting = nextSetting();
                    if (nextSetting != null) nextSetting.updateY();
                }

                protected SettingPanel prevSetting() {
                    return index == 0 ? null : settings.get(index - 1);
                }
                protected SettingPanel nextSetting() {
                    return index == settings.size() - 1 ? null : settings.get(index + 1);
                }

                public void setMessage(String message) {
                    if (this.message.equals(message)) return;
                    this.message = message;
                    updateLines = true;
                }
            }
            
            public class AutoUpdateIntervalSetting extends SettingPanel {
                public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
                protected Length changeIntervalButtonWidth = Length.abs(40), changeIntervalButtonHeight = Length.abs(40);
                protected GuiText increaseIntervalButtonText = new GuiText("+", FONT, new ColorSource(app, Palette::textMedium)),
                        decreaseIntervalButtonText = new GuiText("-", FONT, new ColorSource(app, Palette::textMedium));
                protected GuiBackground changeIntervalButtonBackground = new GuiBackground(app, Palette::medium);
                protected ButtonGui.Border changeIntervalButtonBorder = null;
                protected ButtonGui increaseIntervalButton, decreaseIntervalButton;

                protected static final List<Integer> INTERVALS = List.of(5, 15, 30, 60, 90, 60 * 3, 60 * 24);
                public AutoUpdateIntervalSetting(BrokenArrowsApp app) {
                    super(getMessage(app), app);
                }

                @Override
                public void init() {
                    super.init();
                    increaseIntervalButton = new ButtonGui(Coordinate.abs(Length.direct(() -> right() - app.resolveX(MARGIN_SMALL_LEN) - increaseIntervalButton.width()), top() + app.resolveY(MARGIN_SMALL_LEN)),
                            Coordinate.of(changeIntervalButtonWidth, changeIntervalButtonHeight),
                            increaseIntervalButtonText, increaseIntervalButtonText, increaseIntervalButtonText,
                            new Rectangle(),
                            changeIntervalButtonBackground, changeIntervalButtonBackground, changeIntervalButtonBackground,
                            changeIntervalButtonBorder, changeIntervalButtonBorder, changeIntervalButtonBorder,
                            b -> increaseInterval(), app);
                    decreaseIntervalButton = new ButtonGui(Coordinate.abs(Length.direct(() -> increaseIntervalButton.left() - app.resolveX(MARGIN_SMALL_LEN) - decreaseIntervalButton.width()), top() + app.resolveY(MARGIN_SMALL_LEN)),
                            Coordinate.of(changeIntervalButtonWidth, changeIntervalButtonHeight),
                            decreaseIntervalButtonText, decreaseIntervalButtonText, decreaseIntervalButtonText,
                            new Rectangle(),
                            changeIntervalButtonBackground, changeIntervalButtonBackground, changeIntervalButtonBackground,
                            changeIntervalButtonBorder, changeIntervalButtonBorder, changeIntervalButtonBorder,
                            b -> decreaseInterval(), app);

                    addNestedComponent(increaseIntervalButton);
                    addNestedComponent(decreaseIntervalButton);
                }

                @Override
                protected void updateHeight(int textHeight, int lineHeight) {
                    size.setY(app.resolveYAbs(changeIntervalButtonHeight) + app.resolveYAbs(MARGIN_SMALL_LEN) * 2);
                }

                private void increaseInterval() {
                    int currentIndex = Collections.binarySearch(INTERVALS, app.settings().autoUpdateInterval);
                    int nextIndex = MathUtils.clamp(currentIndex >= 0 ? currentIndex + 1 : -currentIndex - 1, 0, INTERVALS.size() - 1);
                    app.settings().autoUpdateInterval = INTERVALS.get(nextIndex);
                    setMessage(getMessage(app));
                }

                private void decreaseInterval() {
                    int currentIndex = Collections.binarySearch(INTERVALS, app.settings().autoUpdateInterval);
                    int nextIndex = MathUtils.clamp(currentIndex >= 0 ? currentIndex - 1 : -currentIndex - 2, 0, INTERVALS.size() - 1);
                    app.settings().autoUpdateInterval = INTERVALS.get(nextIndex);
                    setMessage(getMessage(app));
                }

                private static String getMessage(BrokenArrowsApp app) {
                    int interval = app.settings().autoUpdateInterval;
                    if (interval < 60) {
                        return "Auto update every " + interval + " minutes";
                    } else {
                        float hours = interval / 60F;
                        return "Auto update every " + format(hours) + " hour" + (hours != 1 ? "s" : "");
                    }
                }

                private static String format(float hours) {
                    return DECIMAL_FORMAT.format(Math.abs(hours));
                }
            }

            public class MinecraftFolderSetting extends SettingPanel {
                public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
                protected Length setMCButtonWidth = Length.abs(100), setMCButtonHeight = Length.abs(40);
                protected GuiText setMCButtonText = new GuiText("Change", FONT, new ColorSource(app, Palette::textMedium));
                protected GuiBackground setMCButtonBackground = new GuiBackground(app, Palette::medium);
                protected ButtonGui.Border setMCButtonBorder = null;
                protected ButtonGui setMCButton;

                public MinecraftFolderSetting(BrokenArrowsApp app) {
                    super(getMessage(app), app);
                }

                @Override
                public void init() {
                    super.init();
                    setMCButton = new ButtonGui(Coordinate.direct(() -> right() - app.resolveX(MARGIN_SMALL_LEN) - setMCButton.width(), () -> top() + app.resolveY(MARGIN_SMALL_LEN)),
                            Coordinate.of(setMCButtonWidth, setMCButtonHeight),
                            setMCButtonText, setMCButtonText, setMCButtonText,
                            new Rectangle(),
                            setMCButtonBackground, setMCButtonBackground, setMCButtonBackground,
                            setMCButtonBorder, setMCButtonBorder, setMCButtonBorder,
                            b -> changeMcDir(), app);

                    addNestedComponent(setMCButton);
                }

                private void changeMcDir() {
                    if (app.selectMcDir()) {
                        setMessage(getMessage(app));
                    }
                }

                @Override
                protected void updateHeight(int textHeight, int lineHeight) {
                    size.setY(app.resolveYAbs(setMCButtonHeight) + app.resolveYAbs(MARGIN_SMALL_LEN) * 2);
                }

                private static String getMessage(BrokenArrowsApp app) {
                    String mcDir = app.minecraftFolder().getAbsolutePath();
                    return "This app updates mods at " + mcDir;
                }
            }

            public class UpdateOnStartSetting extends SettingPanel {
                public static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
                protected Length toggleButtonWidth = Length.abs(100), toggleButtonHeight = Length.abs(40);
                protected GuiText toggleButtonTurnOnText = new GuiText("Turn On", FONT, new ColorSource(app, Palette::textMedium)),
                        toggleButtonTurnOffText = new GuiText("Turn Off", FONT, new ColorSource(app, Palette::textMedium));
                protected GuiBackground toggleButtonBackground = new GuiBackground(app, Palette::medium);
                protected ButtonGui.Border toggleButtonBorder = null;
                protected ButtonGui toggleButton;

                public UpdateOnStartSetting(BrokenArrowsApp app) {
                    super(getMessage(app), app);
                }

                @Override
                public void init() {
                    super.init();
                    toggleButton = new ButtonGui(Coordinate.direct(() -> right() - app.resolveX(MARGIN_SMALL_LEN) - toggleButton.width(), () -> top() + app.resolveY(MARGIN_SMALL_LEN)),
                            Coordinate.of(toggleButtonWidth, toggleButtonHeight),
                            null, null, null,
                            new Rectangle(),
                            toggleButtonBackground, toggleButtonBackground, toggleButtonBackground,
                            toggleButtonBorder, toggleButtonBorder, toggleButtonBorder,
                            b -> toggle(), app);
                    updateState();

                    addNestedComponent(toggleButton);
                }

                private void updateState() {
                    var text = app.settings().updateOnStart ? toggleButtonTurnOffText : toggleButtonTurnOnText;
                    toggleButton.text = toggleButton.hoverText = toggleButton.pressText = text;

                    setMessage(getMessage(app));
                }

                private void toggle() {
                    app.settings().updateOnStart = !app.settings().updateOnStart;
                    updateState();
                }

                @Override
                protected void updateHeight(int textHeight, int lineHeight) {
                    size.setY(app.resolveYAbs(toggleButtonHeight) + app.resolveYAbs(MARGIN_SMALL_LEN) * 2);
                }

                private static String getMessage(BrokenArrowsApp app) {
                    String state = app.settings().updateOnStart ? "Yes" : "No";
                    return "Automatically update upon starting the program? " + state;
                }
            }
        }
    }
}
