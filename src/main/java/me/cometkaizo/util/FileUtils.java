package me.cometkaizo.util;

import me.cometkaizo.Main;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

@SuppressWarnings("unused")
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
        if (OSUtils.isWindows() && System.getenv("APPDATA") != null)
            return new File(System.getenv("APPDATA"));
        if (OSUtils.isMac())
            return new File(new File(userHomeDir, "Library"), "Application Support");
        return new File(userHomeDir);
    }

    public static File getDesktopDir() {
        return FileSystemView.getFileSystemView().getHomeDirectory();
    }

    public static File thisProgramLocation() {
        try {
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void run(File file) throws IOException {
        Desktop.getDesktop().open(file);
    }

    public static Process runBat(File file) throws IOException {
        return Runtime.getRuntime().exec("cmd /c start \"\" \"" + file.getAbsolutePath() + "\"");
    }

    public static boolean exists(File file) {
        return file != null && file.exists();
    }

    public static File resolve(File parent, String... children) {
        if (parent == null) return null;
        File result = parent;
        for (String child : children) {
            result = new File(result, child);
        }
        return result;
    }

    public static String readStr(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
