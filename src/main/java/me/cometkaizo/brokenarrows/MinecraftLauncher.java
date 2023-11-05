package me.cometkaizo.brokenarrows;

import me.cometkaizo.util.FileUtils;
import me.cometkaizo.util.OSUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

import static me.cometkaizo.util.FileUtils.exists;

public class MinecraftLauncher {
    public static final String FOLDER_NAME = "launcherBypass", BYPASS_NAME = "bypass.bat";
    protected final BrokenArrowsApp app;

    public MinecraftLauncher(BrokenArrowsApp app) {
        this.app = app;
    }

    public Process launch() throws IOException {
        var bypass = getBypass();
        if (!exists(bypass)) return null;

        return FileUtils.runBat(bypass);
    }

    public File getBypass(String version) {
        return FileUtils.resolve(bypassFolder(), version, BYPASS_NAME);
    }

    public File getBypass() {
        return getBypass(getVersion());
    }

    public void stealMinecraftBin(List<Diagnostic> problems) {
        try {
            tryStealMinecraftBin(problems);
        } catch (Exception e) {
            problems.add(new Diagnostic.Error(e, "Could not get Minecraft bin"));
        }
    }

    public void tryStealMinecraftBin(List<Diagnostic> problems) throws IOException {
        if (!OSUtils.isWindows()) return; // TODO: 2023-11-04 Try making this compatible with unix-y os's (ps -e)?

        String commandLine = tryGetMinecraftCommandLine(problems);
        if (commandLine == null) return;

        File libraryFolder = getLibraryFile(commandLine, problems);
        if (!exists(libraryFolder)) return;

        commandLine = setLibraryPath(commandLine, copyLibrary(libraryFolder).toAbsolutePath().toString(), problems);
        if (commandLine == null) return;

        writeBypass(commandLine);
    }

    private void writeBypass(String commandLine) throws IOException {
        File bypass = getBypass(getVersion(commandLine));
        bypass.getParentFile().mkdirs();
        bypass.createNewFile();

        Files.writeString(bypass.toPath(), commandLine);
    }

    private Path copyLibrary(File libraryFolder) throws IOException {
        Path targetFolder = bypassFolder().toPath().resolve(libraryFolder.getName());
        Files.copy(libraryFolder.toPath(), targetFolder, StandardCopyOption.REPLACE_EXISTING);
        return targetFolder;
    }

    private String getVersion() {
        var forgeInfo = ForgeUpdater.ForgeInfo.download();
        if (forgeInfo == null) return null;

        return forgeInfo.namespace();
    }
    private String getVersion(String commandLine) {
        try {
            return commandLine.split("versions")[1].split("\\\\")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private String getLibraryPath(String commandLine, List<Diagnostic> problems) {
        try {
            return commandLine.split("-Djava\\.library\\.path=")[1].split(" ")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            problems.add(new Diagnostic.Error(e, "Incorrect format: could not get library path"));
            return null;
        }
    }
    private File getLibraryFile(String commandLine, List<Diagnostic> problems) {
        String path = getLibraryPath(commandLine, problems);
        if (path == null) return null;
        File file = new File(path);
        if (!file.exists()) problems.add(new Diagnostic.Error(null, "No library found at " + file.getAbsolutePath()));
        return file;
    }
    private String setLibraryPath(String commandLine, String path, List<Diagnostic> problems) {
        try {
            String label = "-Djava\\.library\\.path=";
            String[] beforeAfter = commandLine.split(label);
            String beforeLabel = beforeAfter[0];
            String afterLabel = beforeAfter[1];
            String afterValue = afterLabel.substring(afterLabel.indexOf(" "));
            return beforeLabel + label + path + afterValue;
        } catch (ArrayIndexOutOfBoundsException e) {
            problems.add(new Diagnostic.Error(e, "Incorrect format: could not set library path"));
            return null;
        }
    }

    private String tryGetMinecraftCommandLine(List<Diagnostic> problems) {
        try {
            return getMinecraftCommandLine(problems);
        } catch (Exception e) {
            problems.add(new Diagnostic.Error(e, "Could not get Minecraft bin"));
            return null;
        }
    }

    private String getMinecraftCommandLine(List<Diagnostic> problems) throws IOException, InterruptedException {
        StringBuilder result = new StringBuilder();
        Process p = Runtime.getRuntime().exec("wmic process where(name=\"javaw.exe\") get commandline");
        Scanner scanner = new Scanner(new InputStreamReader(p.getInputStream()));

        while (scanner.hasNext()) {
            String line = scanner.nextLine().trim();
            if (!line.equals("CommandLine")) result.append(line);
        }

        scanner.close();
        String str = result.toString();
        return isValidCommandLine(str, problems) ? str : null;
    }

    private boolean isValidCommandLine(String commandLine, List<Diagnostic> problems) {
        if (commandLine.toUpperCase().contains("NO INSTANCE(S) AVAILABLE.")) {
            problems.add(new Diagnostic.Warning(null, "No instances of javaw.exe available"));
        } else {
            String actualVer = getVersion(commandLine);
            String requiredVer = getVersion();

            boolean isCorrectVersion = actualVer != null && actualVer.equals(requiredVer);
            if (!isCorrectVersion) problems.add(new Diagnostic.Warning(null, "Incorrect forge version: required: " + requiredVer + ", found: " + actualVer + "\n\nCommand line:\n" + commandLine));
            return isCorrectVersion;
        }
        return false;
    }

    public File bypassFolder() {
        File dir = new File(app.dataFolder(), FOLDER_NAME);
        dir.mkdirs();
        return dir;
    }

    public boolean canLaunch() {
        return getBypass().exists();
    }
}
