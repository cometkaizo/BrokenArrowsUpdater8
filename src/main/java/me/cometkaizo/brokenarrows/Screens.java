package me.cometkaizo.brokenarrows;

import me.cometkaizo.brokenarrows.screen.HomeScreen;
import me.cometkaizo.brokenarrows.screen.IntroScreen;
import me.cometkaizo.brokenarrows.screen.SettingsScreen;
import me.cometkaizo.registry.Registry;
import me.cometkaizo.screen.ScreenGui;

import java.util.function.Supplier;

public class Screens {
    public static final Registry<BrokenArrowsApp, ScreenGui> SCREENS = new Registry<>();

    public static final Supplier<IntroScreen> INTRO = SCREENS.register("intro", IntroScreen::new);
    public static final Supplier<HomeScreen> HOME = SCREENS.register("home", HomeScreen::new);
    public static final Supplier<SettingsScreen> SETTINGS = SCREENS.register("settings", SettingsScreen::new);
}
