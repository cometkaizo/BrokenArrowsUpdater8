package me.cometkaizo.screen;

import java.awt.*;
import java.awt.geom.RectangularShape;

public class FullResRenderer {
    protected Component host;

    public FullResRenderer() {}
    public FullResRenderer(Component host) {
        bind(host);
    }





    public <S extends RectangularShape> S renderShape(Graphics2D g, S shape, double x, double y, double width, double height) {
        setShape(shape, x, y, width, height);
        g.draw(shape);
        return shape;
    }

    public <S extends RectangularShape> S renderShapeFill(Graphics2D g, S shape, double x, double y, double width, double height) {
        setShape(shape, x, y, width, height);
        g.fill(shape);
        return shape;
    }

    public <S extends RectangularShape> S setShape(S shape, double x, double y, double width, double height) {
        return setShape(shape, toScreenX(x), toScreenY(y), toScreenWidth(width), toScreenHeight(height));
    }

    public void renderImage(Graphics2D g, Image image, double x, double y, double width, double height) {
        renderImage(g, image, toScreenX(x), toScreenY(y), toScreenWidth(width), toScreenHeight(height));
    }

    public void renderImageKeepProportions(Graphics2D g, Image image, double x, double y, double width, double height, boolean fitAll) {
        int screenX = toScreenX(x);
        int screenY = toScreenY(y);
        int screenWidth = toScreenWidth(width);
        int screenHeight = toScreenHeight(height);

        renderImageKeepProportions(g, image, screenX, screenY, screenWidth, screenHeight, fitAll);
    }

    public void renderString(Graphics2D g, String string, double x, double y, double xDeltaFactor, double yDeltaFactor) {
        int screenX = toScreenX(x);
        int screenY = toScreenY(y);
        renderString(g, string, screenX, screenY, xDeltaFactor, yDeltaFactor);
    }

    public <S extends RectangularShape> S renderShape(Graphics2D g, S shape, int screenX, int screenY, int screenWidth, int screenHeight) {
        setShape(shape, screenX, screenY, screenWidth, screenHeight);
        g.draw(shape);
        return shape;
    }

    public <S extends RectangularShape> S renderShapeFill(Graphics2D g, S shape, int screenX, int screenY, int screenWidth, int screenHeight) {
        setShape(shape, screenX, screenY, screenWidth, screenHeight);
        g.fill(shape);
        return shape;
    }

    public <S extends RectangularShape> S setShape(S shape, int screenX, int screenY, int screenWidth, int screenHeight) {
        shape.setFrame(screenX, screenY, screenWidth, screenHeight);
        return shape;
    }

    public void renderImage(Graphics2D g, Image image, int screenX, int screenY, int screenWidth, int screenHeight) {
        g.drawImage(image, screenX, screenY, screenWidth, screenHeight, null);
    }

    public void renderImageKeepProportions(Graphics2D g, Image image, int screenX, int screenY, int screenWidth, int screenHeight, boolean fitAll) {
        double imageWidth = image.getWidth(null);
        double imageHeight = image.getHeight(null);
        if ((imageWidth / screenWidth > imageHeight / screenHeight) == fitAll) {
            int actualHeight = (int) (screenWidth / imageWidth * imageHeight);
            renderImage(g, image, screenX, (int) (screenY - (actualHeight - screenHeight) / 2D), screenWidth, actualHeight);
        } else {
            int actualWidth = (int) (screenHeight / imageHeight * imageWidth);
            renderImage(g, image, (int) (screenX - (actualWidth - screenWidth) / 2D), screenY, actualWidth, screenHeight);
        }
    }

    public void renderString(Graphics2D g, String string, int screenX, int screenY, double xDeltaFactor, double yDeltaFactor) {
        int textWidth = g.getFontMetrics().stringWidth(string);
        double textHeight = g.getFontMetrics().getAscent();
        double actualX = screenX + textWidth * xDeltaFactor;
        double actualY = screenY + textHeight * yDeltaFactor;
        g.drawString(string, (int) actualX, (int) actualY);
    }


    public void bind(Component host) {
        this.host = host;
    }

    public int width() {
        return host == null ? 0 : host.getWidth();
    }

    public int height() {
        return host == null ? 0 : host.getHeight();
    }

    public int toScreenHeight(double height) {
        return (int) (height() * height);
    }

    public int toScreenWidth(double width) {
        return (int) (width() * width);
    }

    public int toScreenY(double y) {
        return (int) (height() * y);
    }

    public int toScreenX(double x) {
        return (int) (width() * x);
    }

    public double toPercentX(int pixels) {
        return pixels / (double)width();
    }
    public double toPercentY(int pixels) {
        return pixels / (double)height();
    }
}
