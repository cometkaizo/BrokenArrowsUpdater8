package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ContainerGui extends Gui {
    protected final List<Gui> components = Collections.synchronizedList(new ArrayList<>(2));

    public ContainerGui(double x, double y, double width, double height, BrokenArrowsApp app) {
        super(x, y, width, height, app);
    }
    public ContainerGui(int x, int y, int width, int height, BrokenArrowsApp app) {
        super(x, y, width, height, app);
    }
    public ContainerGui(int x, int y, double width, double height, BrokenArrowsApp app) {
        super(x, y, width, height, app);
    }
    public ContainerGui(double x, double y, int width, int height, BrokenArrowsApp app) {
        super(x, y, width, height, app);
    }
    public ContainerGui(Coordinate position, Coordinate size, BrokenArrowsApp app) {
        super(position, size, app);
    }

    /**
     * Adds the specified component with size & position <strong>not</strong> relative to this component.
     * @param component the component to add
     */
    protected void addComponent(Gui component) {
        component.init();
        components.add(component);
    }

    /**
     * Adds the specified component with size & position <strong>not</strong> relative to this component.
     * @param component the component to add
     */
    protected void addComponent(int index, Gui component) {
        component.init();
        components.add(index, component);
    }

    /**
     * Adds the specified component with size & position relative to this component.
     * @param component the component to add
     */
    protected void addNestedComponent(Gui component) {
        component.setParent(this);
        component.update();
        addComponent(component);
    }

    /**
     * Adds the specified component with size & position relative to this component.
     * @param component the component to add
     */
    protected void addNestedComponent(int index, Gui component) {
        component.setParent(this);
        component.update();
        addComponent(index, component);
    }

    @Override
    public void tick() {
        super.tick();
        for (var c : components) c.tick();
    }
    @Override
    public void forceTick() {
        super.forceTick();
        for (var c : components) c.forceTick();
    }

    @Override
    public void update() {
        super.update();
        for (var c : components) c.update();
    }

    @Override
    public void onScreenResized() {
        super.onScreenResized();
        for (var c : components) c.onScreenResized();
    }

    @Override
    public void onShown() {
        super.onShown();
        for (var c : components) c.onShown();
    }

    @Override
    public void onHidden() {
        super.onHidden();
        for (var c : components) c.onHidden();
    }

    @Override
    public void onAdded() {
        super.onAdded();
        for (var c : components) c.onAdded();
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        for (var c : components) c.onRemoved();
    }

    @Override
    public void render(FullResRenderer r, Graphics2D g) {
        super.render(r, g);
        for (var c : components) c.render(r, g);
    }

    @Override
    public void mouseClicked(int button, double x, double y) {
        super.mouseClicked(button, x, y);
        for (var c : components) c.mouseClicked(button, x, y);
    }

    @Override
    public void mousePressed(int button, double x, double y) {
        super.mousePressed(button, x, y);
        for (var c : components) c.mousePressed(button, x, y);
    }

    @Override
    public void mouseReleased(int button, double x, double y) {
        super.mouseReleased(button, x, y);
        for (var c : components) c.mouseReleased(button, x, y);
    }

    @Override
    public void mouseEntered(double x, double y) {
        super.mouseEntered(x, y);
        for (var c : components) c.mouseEntered(x, y);
    }

    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);
        for (var c : components) c.mouseMoved(x, y);
    }

    @Override
    public void mouseDragged(double x, double y) {
        super.mouseDragged(x, y);
        for (var c : components) c.mouseDragged(x, y);
    }

    @Override
    public void mouseExited(double x, double y) {
        super.mouseExited(x, y);
        for (var c : components) c.mouseExited(x, y);
    }

    public void keyTyped(int key) {
        super.keyTyped(key);
        for (var c : components) c.keyTyped(key);
    }

    public void keyPressed(int key) {
        super.keyPressed(key);
        for (var c : components) c.keyPressed(key);
    }

    public void keyReleased(int key) {
        super.keyReleased(key);
        for (var c : components) c.keyReleased(key);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        for (var c : components) c.mouseWheelMoved(e);
    }
}
