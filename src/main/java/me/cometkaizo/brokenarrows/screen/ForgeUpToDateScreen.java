package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.ButtonGui;
import me.cometkaizo.screen.Coordinate;
import me.cometkaizo.screen.Length;

public class ForgeUpToDateScreen extends InfoScreen {
    protected Length downloadAnywayButtonWidth = Length.abs(200), downloadAnywayButtonHeight = Length.abs(80);
    protected ButtonGui downloadAnywayButton;
    public ForgeUpToDateScreen(BrokenArrowsApp app) {
        super("You're good!", "Your forge is already up to date. \n\nIf forge is not working correctly, try clicking \"Download Anyway\"", app);
    }

    @Override
    public void init() {
        super.init();
        downloadAnywayButton = app.buttonStyle.light()
                .setAllTextSize(18)
                .setPos(Coordinate.abs(Length.direct(() -> panel.messagePanel.right() - app.resolveX(MARGIN_SMALL_LEN) - downloadAnywayButton.width()), panel.messagePanel.top() + app.resolveY(MARGIN_SMALL_LEN)))
                .setSize(Coordinate.of(downloadAnywayButtonWidth, downloadAnywayButtonHeight))
                .setAllText("Download Anyway")
                .setAction(b -> redownloadForge())
                .build();

        addNestedComponent(downloadAnywayButton);
    }

    private void redownloadForge() {
        app.installForge(updater -> app.panel().removeAndAddScreen(this, new ForgeProgressScreen(updater, app)), app::showForgeUpdateFeedback);
    }
}
