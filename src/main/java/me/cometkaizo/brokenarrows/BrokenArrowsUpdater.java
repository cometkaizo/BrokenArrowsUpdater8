package me.cometkaizo.brokenarrows;

import me.cometkaizo.util.FileUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class BrokenArrowsUpdater extends Updater {

    protected File updatedFile;

    public BrokenArrowsUpdater(BrokenArrowsApp app) {
        super(app);
    }

    @Override
    public void update(boolean force) {
        problems = new ArrayList<>(0);
        updatedFile = null;
        UpdateInfo info = UpdateInfo.download(this);

        if (info != null) {
            if (!app.version.equals(info.version()) || force) {
                downloadUpdater(info, force);
            }
        }
    }

    private void downloadUpdater(UpdateInfo info, boolean force) {
        String fileName;
        URI downloadSource;
        File downloadDest;

        fileName = getFileName(info);
        downloadDest = new File(FileUtils.thisProgramLocation().getParentFile(), fileName);
        if (!force && downloadDest.exists()) updatedFile = downloadDest;
        else try (OutputStream output = new FileOutputStream(downloadDest)) {
            downloadSource = new URI(info.downloadURL());
            boolean success = download(downloadSource, output);
            if (success) updatedFile = downloadDest;
        } catch (FileNotFoundException e) {
            reportError(e, "Could not write file '" + fileName + "' downloaded from '" + info.downloadURL() + "' to '" + downloadDest + "' because either the destination 1. is a directory, 2. cannot be created, or 3. cannot be opened");
        } catch (URISyntaxException e) {
            reportError(e, "Could not download '" + fileName + "'; Malformed URI '" + info.downloadURL() + "'");
        } catch (IOException e) {
            reportError(e, "An IO exception occurred");
        }
    }

    private String getFileName(UpdateInfo info) {
        return app.artifactId + "-" + info.version + ".jar";
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

    public File getUpdatedFile() {
        return updatedFile;
    }

    public record UpdateInfo(String version, String downloadURL) {
        public static final URI DOWNLOAD_LINK = URI.create("https://www.dropbox.com/scl/fi/p871zlmy0axawzzc01bpu/updater.txt?rlkey=1e3aadqxk47wp4o7hcczhb009&dl=1");
        public static UpdateInfo download(BrokenArrowsUpdater updater) {
            String data = updater.downloadStr(DOWNLOAD_LINK);
            if (data == null) return null;
            return new UpdateInfo(data.split(SEPARATOR)[0], data.split(SEPARATOR)[1]);
        }

        public static UpdateInfo of(String info) {
            String[] fileInfo = info.split("@");
            return new UpdateInfo(fileInfo[0], fileInfo[1]);
        }
    }
}
