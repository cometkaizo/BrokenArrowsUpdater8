package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;

public abstract class ScreenGui extends ContainerGui {

    public ScreenGui(BrokenArrowsApp app) {
        super(0D, 0D, 1D, 1D, app);
    }

    /**
     * Performs tasks to adjust to the new window size.
     */
    @Override
    public void onScreenResized() {
        super.onScreenResized();
    }

    protected double getMouseX() {
        return app.panel().mouseX;
    }

    protected double getMouseY() {
        return app.panel().mouseY;
    }
}
