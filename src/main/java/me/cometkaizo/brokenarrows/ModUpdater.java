package me.cometkaizo.brokenarrows;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ModUpdater extends Updater {

    protected List<String> downloadedMods;
    public final List<Consumer<Event>> listeners = new ArrayList<>(1);

    public ModUpdater(BrokenArrowsApp app) {
        super(app);
    }

    @Override
    public void update(boolean force) {
        problems = new ArrayList<>(1);
        downloadedMods = new ArrayList<>(1);

        try {
            updateOrThrow(force);
        } catch (Exception e) {
            reportError(e, "An Exception occurred");
        }

        broadcast(new Event.End());
    }

    private void updateOrThrow(boolean force) throws IOException {
        for (ModInfo mod : ModInfo.download(this)) {
            boolean upToDate = deleteOutdated(mod);

            if (!upToDate || force) {
                downloadMod(mod);
            }
        }
    }

    private boolean deleteOutdated(ModInfo info) throws IOException {
        boolean upToDate = false;
        for (File localFile : getAllModFiles(info)) {
            if (info.is(localFile)) {
                broadcast(new Event.ModUpToDate(info.fullName()));
                upToDate = true;
            } else {
                broadcast(new Event.DeletingOutdatedLocalMod(localFile));
                Files.deleteIfExists(localFile.toPath());
            }
        }
        return upToDate;
    }

    private File[] getAllModFiles(ModInfo info) {
        String prefix = info.prefix(), suffix = info.suffix;
        File[] files = app.modsFolder().listFiles((__, name) -> name.matches("^" + prefix + ".*" + suffix + "$"));
        return files == null ? new File[0] : files;
    }

    private void downloadMod(ModInfo mod) {
        broadcast(new Event.DownloadingMod(mod));
        String fullName = mod.fullName();
        URI downloadSource;
        File downloadDest;

        downloadDest = app.modsFolder().toPath().resolve(fullName).toFile();
        try (OutputStream output = new FileOutputStream(downloadDest)) {
            downloadSource = new URI(mod.downloadURL());
            boolean success = download(downloadSource, output);
            if (success) {
                downloadedMods.add(fullName);
                broadcast(new Event.DownloadedMod(fullName));
            }
        } catch (FileNotFoundException e) {
            reportError(e, "Could not write file '" + fullName + "' downloaded from '" +
                    mod.downloadURL() + "' to '" + downloadDest +
                    "' because either the destination 1. is a directory, 2. cannot be created, or 3. cannot be opened");
        } catch (URISyntaxException e) {
            reportError(e, "Could not download '" + fullName + "'; Malformed URI '" + mod.downloadURL() + "'");
        } catch (IOException e) {
            reportError(e, "An IO exception occurred");
        }
    }

    private boolean download(URI uri, OutputStream output) {
        try {
            byte[] data = downloadBytes(uri);
            if (data != null) {
                output.write(data);
                return true;
            }
        } catch (Exception e) {
            reportError(e, "Could not download from '" + uri + "' because an exception occurred");
        } return false;
    }

    public List<String> getDownloadedMods() {
        return downloadedMods;
    }

    private void broadcast(Event event) {
        listeners.forEach(l -> l.accept(event));
    }


    public interface Event {
        String getString();

        record DownloadingModInfo(URI downloadURI) implements Event {
            @Override
            public String getString() {
                return "Downloading mod info from '" + downloadURI + "'...";
            }
        }
        record ScanningModInfo(String info) implements Event {
            @Override
            public String getString() {
                return "Scanning mod info '" + info + "'";
            }
        }
        record ModUpToDate(String fullName) implements Event {
            @Override
            public String getString() {
                return "Up to date: '" + fullName + "'";
            }
        }
        record DeletingOutdatedLocalMod(File localMod) implements Event {
            @Override
            public String getString() {
                return "Deleting outdated local mod '" + localMod.getName() + "'";
            }
        }
        record DownloadingMod(ModInfo mod) implements Event {
            @Override
            public String getString() {
                return "Downloading '" + mod.fullName() + "' from '" + mod.downloadURL() + "'";
            }
        }
        record DownloadedMod(String fullName) implements Event {
            @Override
            public String getString() {
                return "Downloaded: '" + fullName + "'";
            }
        }
        record End() implements Event {
            @Override
            public String getString() {
                return "End";
            }
        }
    }

    public record ModInfo(String prefix, String version, String suffix, String downloadURL) {
        public static final URI MOD_NAMES_DOWNLOAD_LINK = URI.create("https://www.dropbox.com/scl/fi/tl4eja1qdro0seg643u3s/files.txt?rlkey=91fbx9lgwnpl5roo1y4sq7nh0&dl=1");
        public static List<ModInfo> download(ModUpdater updater) {

            updater.broadcast(new Event.DownloadingModInfo(MOD_NAMES_DOWNLOAD_LINK));
            String data = updater.downloadStr(MOD_NAMES_DOWNLOAD_LINK);
            if (data == null) return List.of();

            return data.lines()
                    .map(l -> {
                        updater.broadcast(new Event.ScanningModInfo(l));
                        return of(l);
                    })
                    .toList();
        }

        public static ModInfo of(String info) {
            String[] fileInfo = info.split("@");
            return new ModInfo(fileInfo[0], fileInfo[1], fileInfo[2], fileInfo[3]);
        }

        public String fullName() {
            return prefix + version + suffix;
        }

        public boolean is(File file) {
            return Objects.equals(fullName(), file.getName());
        }
    }
}
