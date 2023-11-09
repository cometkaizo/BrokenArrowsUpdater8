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
import java.util.Objects;
import java.util.Scanner;

import static me.cometkaizo.util.FileUtils.exists;

public class MinecraftLauncher {
    public static final String FOLDER_NAME = "launcherBypass", BYPASS_BAT_NAME = "bypass.bat", BYPASS_MODS_NAME = "bypass_mods.txt", BYPASS_FORGE_NAME = "bypass_forge.txt";
    public static final String MINECRAFT_LAUNCHER_NAME = "Microsoft.4297127D64EC6_8wekyb3d8bbwe";
    protected final BrokenArrowsApp app;

    public MinecraftLauncher(BrokenArrowsApp app) {
        this.app = app;
    }

    public Process launch() throws IOException {
        var bypass = getBypassBat();
        if (!exists(bypass)) return null;

        return FileUtils.runBat(bypass);
    }

    public File getBypassBat() {
        File bypass = getBypass();
        return bypass == null ? null : getBypassBat(bypass);
    }

    public File getBypass() {
        String version = getVersion();
        return version == null ? null : getBypass(version);
    }

    public File getBypassBat(File bypass) {
        return FileUtils.resolve(bypass, BYPASS_BAT_NAME);
    }

    public File getBypass(String version) {
        return FileUtils.resolve(bypassFolder(), version);
    }

    public boolean setupBypass(List<Diagnostic> problems) {
        if (!OSUtils.isWindows()) return false;
        try {
            trySetupBypass(problems);
            return true;
        } catch (Exception e) {
            problems.add(new Diagnostic.Error(e, "Could not get Minecraft bin"));
        } return false;
    }

    public void trySetupBypass(List<Diagnostic> problems) throws IOException {
        if (!OSUtils.isWindows()) return; // TODO: 2023-11-04 Try making this compatible with unix-y os's (ps -e)?

        String commandLine = tryGetMinecraftCommandLine(problems);
        if (commandLine == null) return;

        File libraryFolder = getLibraryFile(commandLine, problems);
        if (!exists(libraryFolder)) return;

        commandLine = setLibraryPath(commandLine, copyLibrary(libraryFolder).toAbsolutePath().toString(), problems);
        if (commandLine == null) return;

        String version = getVersion(commandLine);
        File bypass = getBypass(version);
        writeBypassBat(bypass, commandLine);
        writeBypassForge(bypass, version);
        writeBypassMods(bypass);
    }

    private void writeBypassBat(File bypass, String commandLine) throws IOException {
        File bat = getBypassBat(bypass);
        bat.getParentFile().mkdirs();
        bat.createNewFile();

        Files.writeString(bat.toPath(), commandLine);
    }
    private void writeBypassForge(File bypass, String version) throws IOException {
        File bypassForge = getBypassForgeFile(bypass);
        bypassForge.createNewFile();

        Files.writeString(bypassForge.toPath(), version);
    }
    private void writeBypassMods(File bypass) throws IOException {
        File bypassMods = getBypassModsFile(bypass);
        bypassMods.createNewFile();

        Files.writeString(bypassMods.toPath(), String.join("\n", app.getCurrentMods()));
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
            problems.add(new Diagnostic.Error(e, "Incorrect format: could not get library path\n\n" + commandLine));
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
            problems.add(new Diagnostic.Error(e, "Incorrect format: could not set library path: " + path + "\n\n" + commandLine));
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

    private String getMinecraftCommandLine(List<Diagnostic> problems) throws IOException {
        String minecraftCommandLine = null;
        Process p = Runtime.getRuntime().exec("wmic process where(name=\"javaw.exe\") get commandline");
        Scanner scanner = new Scanner(new InputStreamReader(p.getInputStream()));

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.contains(MINECRAFT_LAUNCHER_NAME)) {
                minecraftCommandLine = line;
                break;
            }
        }

        scanner.close();
        return isValidCommandLine(minecraftCommandLine, problems) ? minecraftCommandLine : null;
    }

    private boolean isValidCommandLine(String commandLine, List<Diagnostic> problems) {
        if (commandLine == null) {
            problems.add(new Diagnostic.Warning(null, "No command line found; Is Minecraft running?"));
            return false;
        } else {
            String actualVer = getVersion(commandLine);
            String requiredVer = getVersion();

            boolean isCorrectVersion = actualVer != null && actualVer.equals(requiredVer);
            if (!isCorrectVersion) problems.add(new Diagnostic.Warning(null, "Incorrect Minecraft version is running: required: " + requiredVer + ", found: " + actualVer + "\n\nCommand line:\n" + commandLine));
            return isCorrectVersion;
        }
    }

    public File bypassFolder() {
        File dir = new File(app.dataFolder(), FOLDER_NAME);
        dir.mkdirs();
        return dir;
    }

    public boolean canLaunch() {
        File bypass = getBypass();
        return exists(bypass) && isBypassUpToDate(bypass);
    }

    private boolean isBypassUpToDate(File bypass) {
        return areBypassModsUpToDate(bypass) && isBypassForgeUpToDate(bypass);
    }

    private boolean isBypassForgeUpToDate(File bypass) {
        String bypassForge = getBypassForge(bypass);
        return Objects.equals(bypassForge, app.getForgeVersion());
    }

    private boolean areBypassModsUpToDate(File bypass) {
        List<String> bypassMods = getBypassMods(bypass);
        return bypassMods != null && app.currentModsEquals(bypassMods);
    }

    private List<String> getBypassMods(File bypass) {
        File versionFile = getBypassModsFile(bypass);
        if (!versionFile.exists() || !versionFile.isFile()) return null;
        return FileUtils.readStr(versionFile).lines().toList();
    }

    private File getBypassModsFile(File bypass) {
        return new File(bypass, BYPASS_MODS_NAME);
    }

    private String getBypassForge(File bypass) {
        File versionFile = getBypassForgeFile(bypass);
        if (!versionFile.exists() || !versionFile.isFile()) return null;
        return FileUtils.readStr(versionFile);
    }

    private File getBypassForgeFile(File bypass) {
        return new File(bypass, BYPASS_FORGE_NAME);
    }
}
