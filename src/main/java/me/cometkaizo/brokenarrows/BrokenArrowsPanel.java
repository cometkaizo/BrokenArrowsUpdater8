package me.cometkaizo.brokenarrows;

import me.cometkaizo.screen.FullResRenderer;
import me.cometkaizo.screen.ScreenGui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static me.cometkaizo.util.CollectionUtils.addUnique;

public class BrokenArrowsPanel extends JPanel {

    private final List<ScreenGui> screens = Collections.synchronizedList(new ArrayList<>(1));
    private final FullResRenderer renderer;
    public double mouseX;
    public double mouseY;
    private ScreenGui prevTopMostScreen;

    public BrokenArrowsPanel(Dimension size) {
        setPreferredSize(size);
        setBackground(Color.BLACK);

        renderer = new FullResRenderer(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        ScreenGui topMostScreen = topMostScreen();
        if (topMostScreen != null) {
            if (topMostScreen != prevTopMostScreen) {
                topMostScreen.onShown();
                if (prevTopMostScreen != null) prevTopMostScreen.onHidden();
            }
            topMostScreen.render(renderer, (Graphics2D) g);
        }
        prevTopMostScreen = topMostScreen;

        g.dispose();
    }

    public List<ScreenGui> getScreens() {
        return screens;
    }

    public ScreenGui topMostScreen() {
        return screens.isEmpty() ? null : screens.get(screens.size() - 1);
    }
    public boolean isTopmost(ScreenGui screen) {
        return screen != null && screen.equals(topMostScreen());
    }

    public void addScreen(ScreenGui screen) {
        if (addUnique(screens, screen)) screen.onAdded();
    }
    public void removeScreen(ScreenGui screen) {
        if (screens.remove(screen)) screen.onRemoved();
    }
    public void clearScreens() {
        screens.clear();
    }
    public void removeAndAddScreen(ScreenGui from, ScreenGui to) {
        removeScreen(from);
        addScreen(to);
    }

    public void onResized() {
        forEachScreen(ScreenGui::onScreenResized);
    }

    public void tick() {
        forEachScreen(ScreenGui::tick);
    }
    public void forceTick() {
        forEachScreen(ScreenGui::forceTick);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void forEachScreen(Consumer<ScreenGui> task) {
        for (int i = 0; i < screens.size(); i++) {
            task.accept(screens.get(i));
        }
    }

}
