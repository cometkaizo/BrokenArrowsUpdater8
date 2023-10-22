package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Gui {
    protected Coordinate position, size;
    protected Gui parent;
    protected final AtomicBoolean initialized = new AtomicBoolean();
    protected final BrokenArrowsApp app;

    public Gui(double x, double y, double width, double height, BrokenArrowsApp app) {
        this(Coordinate.relative(x, y), Coordinate.relative(width, height), app);
    }
    public Gui(int x, int y, int width, int height, BrokenArrowsApp app) {
        this(Coordinate.abs(x, y), Coordinate.abs(width, height), app);
    }
    public Gui(int x, int y, double width, double height, BrokenArrowsApp app) {
        this(Coordinate.abs(x, y), Coordinate.relative(width, height), app);
    }
    public Gui(double x, double y, int width, int height, BrokenArrowsApp app) {
        this(Coordinate.relative(x, y), Coordinate.abs(width, height), app);
    }
    public Gui(Coordinate position, Coordinate size, BrokenArrowsApp app) {
        this.position = position;
        this.size = size;
        this.app = app;
    }

    public void init() {
        initialized.compareAndSet(false, true);
    }

    public void tick() {

    }

    public void update() {

    }

    /**
     * Performs tasks to adjust to the resized screen that contains this gui component.
     */
    public void onScreenResized() {

    }

    /**
     * Performs tasks when this gui element is shown.
     */
    public void onShown() {
        if (!initialized.get()) init();
    }
    /**
     * Performs tasks when this gui element is hidden.
     */
    public void onHidden() {

    }

    /**
     * Performs tasks when this gui element is added.
     */
    public void onAdded() {

    }
    /**
     * Performs tasks when this gui element is removed.
     */
    public void onRemoved() {

    }

    public void render(FullResRenderer r, Graphics2D g) {
        if (!initialized.get()) init();
    }

    /**
     * Gets the x position of this gui component as a fraction from 0 to 1 relative to the
     * parent gui component's width (or to the screen width if there is no parent).
     * @return the x position
     */
    public double xRaw() {
        return position.xPer(app.panel().getWidth(), app.settings().defaultWidth);
    }
    /**
     * Gets the y position of this gui component as a fraction from 0 to 1 relative to the
     * parent gui component's height (or to the screen height if there is no parent).
     * @return the y position
     */
    public double yRaw() {
        return position.yPer(app.panel().getHeight(), app.settings().defaultHeight);
    }

    /**
     * Gets the x position of this gui component as a fraction from 0 to 1 relative to the screen width.
     * @return the x position
     */
    public double x() {
        double xRaw = xRaw();
        return position.isXAbsolute() || parent == null ? xRaw : parent.getXOfPercent(xRaw);
    }
    /**
     * Gets the y position of this gui component as a fraction from 0 to 1 relative to the screen height.
     * @return the y position
     */
    public double y() {
        double yRaw = yRaw();
        return position.isYAbsolute() || parent == null ? yRaw : parent.getYOfPercent(yRaw);
    }

    /**
     * Gets the width of this gui component as a fraction from 0 to 1 relative to the
     * parent gui component's width (or to the screen width if there is no parent).
     * @return the width
     */
    public double widthRaw() {
        return size.xPer(app.panel().getWidth(), app.settings().defaultWidth);
    }
    /**
     * Gets the height of this gui component as a fraction from 0 to 1 relative to the
     * parent gui component's height (or to the screen height if there is no parent).
     * @return the height
     */
    public double heightRaw() {
        return size.yPer(app.panel().getHeight(), app.settings().defaultHeight);
    }
    /**
     * Gets the width of this gui component as a fraction from 0 to 1 relative to the screen width.
     * @return the width
     */
    public double width() {
        double widthRaw = widthRaw();
        return size.isXAbsolute() || parent == null ? widthRaw : parent.width() * widthRaw;
    }
    /**
     * Gets the height of this gui component as a fraction from 0 to 1 relative to the screen height.
     * @return the height
     */
    public double height() {
        double heightRaw = heightRaw();
        return size.isYAbsolute() || parent == null ? heightRaw : parent.height() * heightRaw;
    }

    public void setParent(Gui parent) {
        this.parent = parent;
    }

    public void setX(double x) {
        position.setX(x);
    }
    public void setX(int x) {
        position.setX(x);
    }
    public void setX(Length x) {
        position.setX(x);
    }
    public void setY(double y) {
        position.setY(y);
    }
    public void setY(int y) {
        position.setY(y);
    }
    public void setY(Length y) {
        position.setY(y);
    }
    public void setWidth(double w) {
        size.setX(w);
    }
    public void setWidth(Length w) {
        size.setX(w);
    }
    public void setWidth(int w) {
        size.setX(w);
    }
    public void setHeight(double h) {
        size.setY(h);
    }
    public void setHeight(int h) {
        size.setY(h);
    }
    public void setHeight(Length h) {
        size.setY(h);
    }

    public double getXOfPercent(double percentX) {
        return x() + percentX * width();
    }
    public double getYOfPercent(double percentY) {
        return y() + height() * percentY;
    }

    public double left() {
        return x();
    }
    public double right() {
        return x() + width();
    }
    public double top() {
        return y();
    }
    public double bottom() {
        return y() + height();
    }

    public boolean contains(double x, double y) {
        return x >= left() && x <= right() && y >= top() && y <= bottom();
    }

    public void mouseClicked(int button, double x, double y) {

    }

    public void mousePressed(int button, double x, double y) {

    }

    public void mouseReleased(int button, double x, double y) {

    }

    public void mouseMoved(double x, double y) {

    }

    public void mouseDragged(double x, double y) {

    }

    public void mouseEntered(double x, double y) {

    }

    public void mouseExited(double x, double y) {

    }

    public void keyTyped(int key) {

    }

    public void keyPressed(int key) {

    }

    public void keyReleased(int key) {

    }

    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
