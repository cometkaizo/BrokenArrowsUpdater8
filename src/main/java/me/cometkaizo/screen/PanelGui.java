package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class PanelGui extends ContainerGui {
    protected GuiBackground background;
    protected Rectangle2D boundingBox = new Rectangle();

    public PanelGui(double x, double y, double width, double height, GuiBackground background, BrokenArrowsApp app) {
        super(x, y, width, height, app);
        this.background = background;
    }

    public PanelGui(int x, int y, int width, int height, GuiBackground background, BrokenArrowsApp app) {
        super(x, y, width, height, app);
        this.background = background;
    }

    public PanelGui(int x, int y, double width, double height, GuiBackground background, BrokenArrowsApp app) {
        super(x, y, width, height, app);
        this.background = background;
    }

    public PanelGui(double x, double y, int width, int height, GuiBackground background, BrokenArrowsApp app) {
        super(x, y, width, height, app);
        this.background = background;
    }

    public PanelGui(Coordinate position, Coordinate size, GuiBackground background, BrokenArrowsApp app) {
        super(position, size, app);
        this.background = background;
    }

    @Override
    public void render(FullResRenderer r, Graphics2D g) {
        renderBackground(r, g);
        super.render(r, g);
    }

    public void renderBackground(FullResRenderer r, Graphics2D g) {
        background.render(r, g, boundingBox, x(), y(), width(), height());
    }

    public void setBackground(GuiBackground background) {
        this.background = background;
    }

    public void setBackgroundIfVisible(GuiBackground background) {
        if (background != null && background.isVisible()) this.background = background;
    }

    public void setBackgroundIfVisibleOrLoading(GuiBackground background) {
        if (background != null && (background.isVisible() || background.isLoading())) this.background = background;
    }

}
