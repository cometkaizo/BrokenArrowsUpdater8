package me.cometkaizo.brokenarrows;

import me.cometkaizo.io.DataSerializable;
import me.cometkaizo.io.PathSerializable;
import me.cometkaizo.io.data.CompoundData;
import me.cometkaizo.launcher.app.AppSettings;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class BrokenArrowsSettings extends AppSettings implements DataSerializable, PathSerializable {
    public static final String SAVE_NAME = "brokenarrows.properties";
    private final BrokenArrowsApp app;
    public String name = "Broken Arrows";
    public int defaultWidth = 1280, defaultHeight = 720;
    public File minecraftFolder;
    public final TimeUnit autoUpdateIntervalUnit = TimeUnit.MINUTES;
    public int defaultAutoUpdateInterval = 60, autoUpdateInterval = defaultAutoUpdateInterval; // in minutes

    public BrokenArrowsSettings(BrokenArrowsApp app) {
        this.app = app;
    }

    public boolean setMinecraftFolder(File mc) {
        if (mc != null && mc.exists() && mc.isDirectory() && app.hasLauncherProfile(mc)) {
            minecraftFolder = mc;
            return true;
        } return false;
    }

    @Override
    public CompoundData write() {
        CompoundData data = new CompoundData();
        if (minecraftFolder != null) data.putString("minecraftFolder", minecraftFolder.getAbsolutePath());
        data.putInt("autoUpdateInterval", autoUpdateInterval);
        return data;
    }

    @Override
    public void read(CompoundData data) {
        setMinecraftFolder(data.getString("minecraftFolder").map(File::new).orElse(null));
        autoUpdateInterval = data.getInt("autoUpdateInterval").orElse(defaultAutoUpdateInterval);
    }

    public void write(Path dataFolder) throws IOException {
        File file = dataFolder.resolve(SAVE_NAME).toFile();
        write().write(new DataOutputStream(new FileOutputStream(file)));
    }
    public void read(Path dataFolder) throws IOException {
        Path file = dataFolder.resolve(SAVE_NAME);
        if (file.toFile().exists()) {
            read(CompoundData.of(file));
        }
    }
}
