package me.cometkaizo.util;

public class IOUtils {

    public static String toNamespace(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }

}
