package me.cometkaizo.launcher.app;

import me.cometkaizo.util.StringUtils;

public abstract class App {

    protected AppSettings settings;

    protected App(AppSettings settings) {
        this.settings = settings;
    }


    public void setup() {

    }

    public void cleanup() {

    }

    public void tick() {

    }

    protected AppSettings settings() {
        return settings;
    }


    public void log(String message) {
        System.out.println(message);
    }
    public void log(String message, Object... args) {
        if (message == null) log(args);
        else log(StringUtils.format(message, args));
    }
    public void log(Object... args) {
        log(StringUtils.list(args));
    }
    public void err(String message) {
        System.err.println(message);
    }
    public void err(Throwable t, String message) {
        err(message);
        if (t != null) t.printStackTrace(System.err);
    }
    public void err(String message, Object... args) {
        if (message == null) err(args);
        else err(StringUtils.format(message, args));
    }
    public void err(Throwable t, Object... args) {
        err(t, StringUtils.list(args));
    }
    public void err(Throwable t, String message, Object... args) {
        if (message == null) err(t, args);
        else err(t, StringUtils.format(message, args));
    }
    public void err(Object... args) {
        err(StringUtils.list(args));
    }

}
