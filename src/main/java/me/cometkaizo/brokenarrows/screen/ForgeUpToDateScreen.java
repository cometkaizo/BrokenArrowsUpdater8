package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.*;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;

import java.awt.*;

public class ForgeUpToDateScreen extends InfoScreen {
    protected Length downloadAnywayButtonWidth = Length.abs(200), downloadAnywayButtonHeight = Length.abs(80);
    protected GuiText downloadAnywayButtonText = new GuiText("Download Anyway", Panel.MessagePanel.FONT, new ColorSource(app, Palette::textMedium));
    protected GuiBackground downloadAnywayButtonBackground = new GuiBackground(app, Palette::light);
    protected ButtonGui.Border downloadAnywayButtonBorder = null;
    protected ButtonGui downloadAnywayButton;
    public ForgeUpToDateScreen(BrokenArrowsApp app) {
        super("You're good!", "Your forge is already up to date. \n\nIf forge is not working correctly, try clicking \"Download Anyway\"", app);
    }

    @Override
    public void init() {
        super.init();
        downloadAnywayButton = new ButtonGui(Coordinate.abs(Length.direct(() -> panel.messagePanel.right() - app.resolveX(MARGIN_SMALL_LEN) - downloadAnywayButton.width()), panel.messagePanel.top() + app.resolveY(MARGIN_SMALL_LEN)),
                Coordinate.of(downloadAnywayButtonWidth, downloadAnywayButtonHeight),
                downloadAnywayButtonText, downloadAnywayButtonText, downloadAnywayButtonText,
                new Rectangle(),
                downloadAnywayButtonBackground, downloadAnywayButtonBackground, downloadAnywayButtonBackground,
                downloadAnywayButtonBorder, downloadAnywayButtonBorder, downloadAnywayButtonBorder,
                b -> redownloadForge(), app);

        addNestedComponent(downloadAnywayButton);
    }

    private void redownloadForge() {
        app.installForge(updater -> app.panel().removeAndAddScreen(this, new ForgeProgressScreen(updater, app)), app::showForgeUpdateFeedback);
    }
}
