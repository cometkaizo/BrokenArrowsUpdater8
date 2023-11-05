package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.brokenarrows.Screens;
import me.cometkaizo.screen.ButtonGui;
import me.cometkaizo.screen.Coordinate;
import me.cometkaizo.screen.Length;

public class IntroScreen extends InfoScreen {
    protected ButtonGui setMcDirButton;
    public IntroScreen(BrokenArrowsApp app) {
        super("Hello there :)", "Please select your Minecraft folder (by default .minecraft)", app);
    }

    @Override
    public void init() {
        super.init();
        setMcDirButton = app.buttonStyle.light()
                .setAllTextSize(24)
                .setPos(Coordinate.abs(Length.direct(() -> panel.messagePanel.right() - app.resolveX(MARGIN_SMALL_LEN) - setMcDirButton.width()), panel.messagePanel.top() + app.resolveY(MARGIN_SMALL_LEN)))
                .setSize(Coordinate.abs(200, 80))
                .setAllText("Select")
                .setAction(b -> selectMcDir())
                .build();

        addNestedComponent(setMcDirButton);
    }

    public void selectMcDir() {
        app.selectMcDir(f -> switchToHomeScreen());
    }

    private void switchToHomeScreen() {
        app.panel().removeAndAddScreen(IntroScreen.this, Screens.HOME.get());
        app.onSetupComplete();
    }

}
