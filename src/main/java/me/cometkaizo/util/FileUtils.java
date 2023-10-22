package me.cometkaizo.util;

import me.cometkaizo.Main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;

public class FileUtils {

    public static File promptDir(File defaultPath, Component parent) {
        var chooser = new JFileChooser();
        chooser.setCurrentDirectory(defaultPath);
        chooser.setDialogTitle("");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else return null;
    }

    public static File getUserHome() {
        return new File(System.getProperty("user.home"));
    }

    public static File getAppdataDir() {
        String userHomeDir = System.getProperty("user.home", ".");
        String osType = System.getProperty("os.name").toUpperCase();
        if (osType.contains("WIN") && System.getenv("APPDATA") != null)
            return new File(System.getenv("APPDATA"));
        if (osType.contains("MAC"))
            return new File(new File(userHomeDir, "Library"), "Application Support");
        return new File(userHomeDir);
    }

    public static File thisProgramLocation() {
        try {
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
