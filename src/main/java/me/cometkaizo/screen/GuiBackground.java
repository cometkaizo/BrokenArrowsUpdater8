package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;
import me.cometkaizo.util.ImageUtils;

import java.awt.*;
import java.awt.geom.RectangularShape;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.function.Function;

public class GuiBackground {
    protected ImageSource image;
    protected ColorSource glaze;

    public GuiBackground() {

    }

    public GuiBackground(Color glaze) {
        this.glaze = new ColorSource(glaze);
    }

    public GuiBackground(ColorSource glaze) {
        this.glaze = glaze;
    }

    public GuiBackground(BrokenArrowsApp app, Function<Palette, Color> colorFunc) {
        this.glaze = new ColorSource(app, colorFunc);
    }

    public GuiBackground(Image image, ColorSource glaze) {
        this.image = new ImageSource(image);
        this.glaze = glaze;
    }

    public GuiBackground(Future<? extends Image> imageSup, ColorSource glaze) {
        this.image = new ImageSource(imageSup);
        this.glaze = glaze;
    }

    public GuiBackground(ImageSource image, ColorSource glaze) {
        this.image = image;
        this.glaze = glaze;
    }

    public static GuiBackground ofImage(BrokenArrowsApp app, String path) {
        return new GuiBackground(ImageUtils.readImage(app.executor(), path), null);
    }

    public void render(FullResRenderer r, Graphics2D g, RectangularShape shape, double x, double y, double width, double height) {
        if (shouldRenderImage()) renderImage(r, g, shape, x, y, width, height);
        renderGlaze(r, g, shape, x, y, width, height);
    }

    public void renderImage(FullResRenderer r, Graphics2D g, RectangularShape shape, double x, double y, double width, double height) {
        r.setShape(shape, x, y, width, height);
        g.setClip(shape);
        r.renderImageKeepProportions(g, image(), x, y, width, height, false);
        g.setClip(null);
    }

    public void renderGlaze(FullResRenderer r, Graphics2D g, RectangularShape shape, double x, double y, double width, double height) {
        if (glaze() != null) {
            g.setColor(glaze().color());
            r.renderShapeFill(g, shape, x, y, width, height);
        }
    }

    public void render(FullResRenderer r, Graphics2D g, RectangularShape shape, int x, int y, int width, int height) {
        if (shouldRenderImage()) renderImage(r, g, shape, x, y, width, height);
        renderGlaze(r, g, shape, x, y, width, height);
    }

    public void renderImage(FullResRenderer r, Graphics2D g, RectangularShape shape, int x, int y, int width, int height) {
        if (image() == null) return;
        r.setShape(shape, x, y, width, height);
        g.setClip(shape);
        r.renderImageKeepProportions(g, image(), x, y, width, height, false);
        g.setClip(null);
    }

    public boolean shouldRenderImage() {
        return image() != null && (glaze() == null || glaze().hasTransparency());
    }

    public void renderGlaze(FullResRenderer r, Graphics2D g, RectangularShape shape, int x, int y, int width, int height) {
        if (glaze() != null) {
            g.setColor(glaze().color());
            r.renderShapeFill(g, shape, x, y, width, height);
        }
    }

    public Image image() {
        return image != null ? image.image() : null;
    }

    public ColorSource glaze() {
        return glaze;
    }

    public void setImage(Image image) {
        if (this.image == null) this.image = new ImageSource(image);
        else this.image.setImage(image);
    }

    public void setImage(Future<Image> imageSup) {
        if (this.image == null) this.image = new ImageSource(imageSup);
        else this.image.setImage(imageSup);
    }

    public void setImage(ImageSource image) {
        this.image = image;
    }

    public void setGlaze(Color glaze) {
        if (this.glaze == null) this.glaze = new ColorSource(glaze);
        else this.glaze.setColor(glaze);
    }

    public void setGlaze(BrokenArrowsApp app, Function<Palette, Color> colorFunc) {
        if (this.glaze == null) this.glaze = new ColorSource(app, colorFunc);
        else this.glaze.setColor(app, colorFunc);
    }

    public void setGlaze(ColorSource glaze) {
        this.glaze = glaze;
    }

    public boolean isVisible() {
        return (glaze != null && glaze.isVisible()) || (image != null && image.isVisible());
    }

    public boolean isLoading() {
        return image != null && image.isLoading();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GuiBackground) obj;
        return Objects.equals(this.image, that.image) &&
                Objects.equals(this.glaze, that.glaze);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, glaze);
    }

    @Override
    public String toString() {
        return "GuiBackground[" +
                "image=" + image + ", " +
                "glaze=" + glaze + ']';
    }
}
