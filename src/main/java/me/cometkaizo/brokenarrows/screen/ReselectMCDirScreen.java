package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.ButtonGui;
import me.cometkaizo.screen.Coordinate;
import me.cometkaizo.screen.Length;

import java.nio.file.Path;

public class ReselectMCDirScreen extends InfoScreen {
    protected ButtonGui setMcDirButton;
    protected final Runnable reselectAction;
    public ReselectMCDirScreen(String title, String message, Runnable reselectAction, BrokenArrowsApp app) {
        super(title, message, app);
        this.reselectAction = reselectAction;
    }

    public static ReselectMCDirScreen dirNotFound(Path selection, Runnable reselectAction, BrokenArrowsApp app) {
        return new ReselectMCDirScreen("Directory not found", "The path '" + selection + "' does not exist", reselectAction, app);
    }
    public static ReselectMCDirScreen notADir(Path selection, Runnable reselectAction, BrokenArrowsApp app) {
        return new ReselectMCDirScreen("Not a directory", "The path '" + selection + "' is not a directory", reselectAction, app);
    }
    public static ReselectMCDirScreen notAMinecraftInstall(Path selection, Runnable reselectAction, BrokenArrowsApp app) {
        return new ReselectMCDirScreen("Not a Minecraft installation", "The path '" + selection + "' is not a Minecraft installation!\n\nIf the path is right, try running the launcher first.", reselectAction, app);
    }
    public static ReselectMCDirScreen error(Path selection, Runnable reselectAction, BrokenArrowsApp app) {
        return new ReselectMCDirScreen("Error", "Minecraft installation '" + selection + "' could not be set. No further information", reselectAction, app);
    }

    @Override
    public void init() {
        super.init();
        setMcDirButton = app.buttonStyle.light()
                .setAllTextSize(24)
                .setPos(Coordinate.abs(Length.direct(() -> panel.messagePanel.right() - app.resolveX(MARGIN_SMALL_LEN) - setMcDirButton.width()), panel.messagePanel.top() + app.resolveY(MARGIN_SMALL_LEN)))
                .setSize(Coordinate.abs(200, 80))
                .setAllText("Reselect")
                .setAction(b -> selectMcDir())
                .build();

        addNestedComponent(setMcDirButton);
    }

    private void selectMcDir() {
        reselectAction.run();
        app.panel().removeScreen(this);
    }
}
