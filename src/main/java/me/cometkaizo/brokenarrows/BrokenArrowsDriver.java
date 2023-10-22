package me.cometkaizo.brokenarrows;

import me.cometkaizo.io.FileEvent;
import me.cometkaizo.io.FileListener;
import me.cometkaizo.io.FileWatcher;
import me.cometkaizo.launcher.driver.SystemDriver;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class BrokenArrowsDriver extends SystemDriver {
    public static final String LIFELINE_FILE = "brokenarrows.life";
    protected final Scanner scanner = new Scanner(System.in);

    public BrokenArrowsDriver() {
        super(new BrokenArrowsApp());
        addTask(executor -> app.setup());
        addLoop(this::scanConsole, 0, 300, TimeUnit.MILLISECONDS);
        addLoop(app::tick, 0, 1000 / 20, TimeUnit.MILLISECONDS);
    }

    private void scanConsole() {
        if (scanner.hasNextLine()) {
            app().parseInput(scanner.nextLine());
        }
    }

    @Override
    protected void setup() {
        super.setup();

        File lifeline;
        try {
            lifeline = new File(app().dataFolder(), LIFELINE_FILE);
            if (!lifeline.exists()) lifeline.createNewFile();
            else {
                lifeline.delete();
                lifeline.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileWatcher watcher = new FileWatcher(app().dataFolder());
        watcher.addListener(new FileListener() {
            public void onCreated(FileEvent event) {}
            public void onModified(FileEvent event) {}
            public void onDeleted(FileEvent event) {
                if (lifeline.equals(event.getFile())) {
                    try {
                        stop();
                    } catch (Throwable e) {
                        e.printStackTrace(System.err);
                        System.exit(1);
                    }
                    System.exit(0);
                }
            }
        }).watch();
    }

    @Override
    protected void cleanup() {
        app.cleanup();
    }

    @Override
    protected BrokenArrowsApp app() {
        return (BrokenArrowsApp) app;
    }
}
