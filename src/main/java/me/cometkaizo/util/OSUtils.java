package me.cometkaizo.util;

public class OSUtils {

    public static String os() {
        return System.getProperty("os.name").toUpperCase();
    }

    public static boolean isWindows() {
        return os().contains("WIN");
    }
    public static boolean isMac() {
        return os().contains("MAC");
    }
    public static boolean isLinux() {
        return os().contains("LINUX");
    }
}
