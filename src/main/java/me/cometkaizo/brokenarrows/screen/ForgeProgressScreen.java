package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.brokenarrows.ForgeUpdater;

import java.util.function.Consumer;

public class ForgeProgressScreen extends InfoScreen {
    protected final ForgeUpdater updater;

    public ForgeProgressScreen(ForgeUpdater updater, BrokenArrowsApp app) {
        super("Installing Forge...", "", app);
        this.updater = updater;
        updater.listeners.add(this::onForgeUpdateEvent);
    }

    public void onForgeUpdateEvent(ForgeUpdater.Event event) {
        if (event instanceof ForgeUpdater.Event.End) {
            app.panel().removeScreen(this);
            updater.listeners.remove((Consumer<ForgeUpdater.Event>) this::onForgeUpdateEvent);
        } else {
            String desc = event.getString();
            setMessage(message + desc + "\n");
            title = desc;
        }
    }
}
