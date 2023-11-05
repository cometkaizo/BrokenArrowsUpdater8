package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.brokenarrows.ModUpdater;
import me.cometkaizo.screen.*;

import java.util.function.Consumer;

public class ModProgressScreen extends InfoScreen {
    public static final Length MARGIN_SMALL_LEN = Length.abs(MARGIN_SMALL);
    protected final ModUpdater updater;

    public ModProgressScreen(ModUpdater updater, BrokenArrowsApp app) {
        super("Updating Mods...", "", app);
        this.updater = updater;
        updater.listeners.add(this::onModUpdateEvent);
    }

    public void onModUpdateEvent(ModUpdater.Event event) {
        if (event instanceof ModUpdater.Event.End) {
            app.panel().removeScreen(this);
            updater.listeners.remove((Consumer<ModUpdater.Event>) this::onModUpdateEvent);
        } else {
            String desc = event.getString();
            setMessage(message + desc + "\n");
            title = desc;
        }
    }
}
