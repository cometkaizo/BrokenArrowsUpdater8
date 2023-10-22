package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.*;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;

import java.awt.*;

public class ModsUpToDateScreen extends InfoScreen {
    protected Length downloadAnywayButtonWidth = Length.abs(200), downloadAnywayButtonHeight = Length.abs(80);
    protected GuiText downloadAnywayButtonText = new GuiText("Download Anyway", Panel.MessagePanel.FONT, new ColorSource(app, Palette::textMedium));
    protected GuiBackground downloadAnywayButtonBackground = new GuiBackground(app, Palette::light);
    protected ButtonGui.Border downloadAnywayButtonBorder = null;
    protected ButtonGui downloadAnywayButton;
    public ModsUpToDateScreen(BrokenArrowsApp app) {
        super("You're good!", "All mods are already up to date. \n\nIf the mods are not working, try clicking \"Download Anyway\"", app);
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
                b -> redownloadMods(), app);

        addNestedComponent(downloadAnywayButton);
    }

    private void redownloadMods() {
        app.installMods(updater -> app.panel().removeAndAddScreen(this, new ModProgressScreen(updater, app)), app::showModUpdateFeedback);
    }
}
