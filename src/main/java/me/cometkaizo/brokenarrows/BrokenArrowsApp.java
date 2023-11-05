package me.cometkaizo.brokenarrows;

import me.cometkaizo.Main;
import me.cometkaizo.brokenarrows.command.ExitCommand;
import me.cometkaizo.brokenarrows.command.TestCommand;
import me.cometkaizo.brokenarrows.screen.*;
import me.cometkaizo.command.CommandGroup;
import me.cometkaizo.command.CommandSyntaxException;
import me.cometkaizo.command.UnknownCommandException;
import me.cometkaizo.io.data.DataTypes;
import me.cometkaizo.launcher.app.App;
import me.cometkaizo.launcher.driver.ExceptionManager;
import me.cometkaizo.screen.Length;
import me.cometkaizo.screen.color.Palette;
import me.cometkaizo.util.FileUtils;
import me.cometkaizo.util.ImageUtils;
import me.cometkaizo.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static me.cometkaizo.util.FileUtils.exists;

public class BrokenArrowsApp extends App {
    public static final String DATA_FOLDER_NAME = "BrokenArrows";

    private final CommandGroup commandGroup = new CommandGroup(
            ExitCommand::new,
            TestCommand::new
    );
    protected Properties properties;
    protected String version, artifactId;
    protected BrokenArrowsInfo info;

    protected Image icon;
    protected JFrame frame;
    protected BrokenArrowsPanel panel;
    protected TrayIcon trayIcon;
    protected File dataFolder;
    protected Future<?> autoUpdateLoop;
    protected ExecutorService executor = Executors.newFixedThreadPool(2);
    protected ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    protected Palette palette;
    public ButtonStyle buttonStyle;

    protected boolean firstTimeOpening;

    protected final List<ModUpdater> modUpdaters = Collections.synchronizedList(new ArrayList<>(1));
    protected final List<ForgeUpdater> forgeUpdaters = Collections.synchronizedList(new ArrayList<>(1));
    protected final MinecraftLauncher minecraftLauncher;
    protected ScheduledFuture<?> stealMinecraftBinLoop;

    protected BrokenArrowsApp() {
        super(null);
        settings = new BrokenArrowsSettings(this);
        info = new BrokenArrowsInfo();
        properties = new Properties();
        buttonStyle = new ButtonStyle(this);
        minecraftLauncher = new MinecraftLauncher(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (panel != null) panel.forceTick();
        if (frame != null && frame.isFocused()) {
            panel.tick();
            frame.repaint();
        }
    }


    public void updateForge(Consumer<ForgeUpdater> preTask, Consumer<ForgeUpdater> postTask) {
        installForge(preTask, postTask, false);
    }
    public void installForge(Consumer<ForgeUpdater> preTask, Consumer<ForgeUpdater> postTask) {
        installForge(preTask, postTask, true);
    }
    private void installForge(Consumer<ForgeUpdater> preTask, Consumer<ForgeUpdater> postTask, boolean force) {
        if (!checkForMinecraftFolder()) return;

        ForgeUpdater forgeUpdater = new ForgeUpdater(this);
        forgeUpdaters.add(forgeUpdater);
        try {
            preTask.accept(forgeUpdater);
            addTask(() -> {
                try {
                    forgeUpdater.update(force);
                    postTask.accept(forgeUpdater);
                } finally {
                    forgeUpdaters.remove(forgeUpdater);
                }
            }, ExceptionManager.PRINT);
        } catch (Exception e) {
            forgeUpdaters.remove(forgeUpdater);
        }
    }

    public void showForgeUpdateFeedback(ForgeUpdater forgeUpdater) {
        if (forgeUpdater.success) panel.addScreen(getForgeSuccessScreen());
        else if (forgeUpdater.isAlreadyUpToDate()) panel.addScreen(getForgeUpToDateScreen());
        else panel.addScreen(getForgeErrorScreen(forgeUpdater.getProblems()));
    }

    public void updateMods(Consumer<ModUpdater> preTask, Consumer<ModUpdater> postTask) {
        installMods(preTask, postTask, false);
    }
    public void installMods(Consumer<ModUpdater> preTask, Consumer<ModUpdater> postTask) {
        installMods(preTask, postTask, true);
    }
    private void installMods(Consumer<ModUpdater> preTask, Consumer<ModUpdater> postTask, boolean force) {
        if (!checkForMinecraftFolder()) return;

        ModUpdater modUpdater = new ModUpdater(this);
        modUpdaters.add(modUpdater);
        try {
            preTask.accept(modUpdater);
            addTask(() -> {
                try {
                    modUpdater.update(force);
                    postTask.accept(modUpdater);
                } finally {
                    modUpdaters.remove(modUpdater);
                }
            }, ExceptionManager.PRINT);
        } catch (Exception e) {
            modUpdaters.remove(modUpdater);
        }
    }

    public void showModUpdateFeedback(ModUpdater modUpdater) {
        var problems = modUpdater.getProblems();
        var downloadedMods = modUpdater.getDownloadedMods();

        if (downloadedMods.isEmpty() && problems.isEmpty()) panel.addScreen(getModUpToDateScreen());
        else if (problems.isEmpty()) panel.addScreen(getModSuccessScreen(downloadedMods));
        else panel.addScreen(getModErrorScreen(problems, downloadedMods));
    }

    private AlertScreen getModUpToDateScreen() {
        return new ModsUpToDateScreen(this);
    }
    private AlertScreen getModSuccessScreen(List<String> downloadedMods) {
        return new AlertScreen("Success!", "Installed mods:\n  > " +
                String.join("\n  > ", downloadedMods) + "\n\nIf you have Minecraft or the Minecraft Launcher open, " +
                "you will need to restart it.", this);
    }
    private AlertScreen getModErrorScreen(List<Diagnostic> problems, List<String> downloadedMods) {
        String modsStr = downloadedMods.isEmpty() ? "" : "Installed mods:\n  > " + String.join("\n  > ", downloadedMods) + "\n\n";
        String problemsStr = problems.stream().map(Diagnostic::getString).collect(Collectors.joining("\n\n"));

        return new AlertScreen("Encountered a problem :/",
                modsStr + problemsStr, this);
    }

    private AlertScreen getForgeUpToDateScreen() {
        return new ForgeUpToDateScreen(this);
    }
    private AlertScreen getForgeSuccessScreen() {
        return new AlertScreen("Success!", """
                Installed Forge.

                If you have Minecraft or the Minecraft Launcher open, you will need to restart it to see the Forge installation.""", this);
    }
    private AlertScreen getForgeErrorScreen(List<Diagnostic> problems) {
        String problemsStr = problems.stream().map(Diagnostic::getString).collect(Collectors.joining("\n\n"));
        return new AlertScreen("Could not install Forge :/", problemsStr, this);
    }


    @Override
    public void setup() {
        super.setup();
        log("Broken Arrows Installer");

        loadProperties();

        setDataFolderIn(FileUtils.getAppdataDir());
        icon = ImageUtils.readImage("/icon.png");

        Screens.SCREENS.register(this);
        DataTypes.DATA_TYPES.register(this);
        Palettes.PALETTES.register(this);
        palette = Palettes.DARK.get();
        initWindow();

        load();
        tryUpdateThis();

        if (panel.topMostScreen() == Screens.HOME.get()) onSetupComplete();
    }

    public void onSetupComplete() {
        rescheduleAutoUpdate();
        tryAutoUpdateOnStart();

        if (firstTimeOpening) {
            settings().updateOnStart = true;
            save();
        }

        if (!minecraftLauncher.canLaunch()) {
            scheduleStealMinecraftBin();
        }
    }

    private void scheduleStealMinecraftBin() {
        if (stealMinecraftBinLoop != null) return;
        stealMinecraftBinLoop = scheduler.scheduleAtFixedRate(() -> {
            try {
                List<Diagnostic> problems = new ArrayList<>(1);
                minecraftLauncher.stealMinecraftBin(problems);
                if (problems.isEmpty()) stealMinecraftBinLoop.cancel(false);
                showLauncherFeedback(problems);
            } catch (Throwable e) {
                err(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void showLauncherFeedback(List<Diagnostic> problems) {
        if (problems.size() > 1 || problems.size() == 1 && problems.get(0) instanceof Diagnostic.Error)
            panel.addScreen(getLauncherErrorScreen(problems));
    }
    private AlertScreen getLauncherErrorScreen(List<Diagnostic> problems) {
        String problemsStr = problems.stream().map(Diagnostic::getString).collect(Collectors.joining("\n\n"));

        return new AlertScreen("Could not get Minecraft bin :/", problemsStr, this);
    }

    private void tryAutoUpdateOnStart() {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), info.lastAutoUpdateTime);
        boolean shouldAutoUpdate = minutes >= settings().autoUpdateInterval;
        if (settings().updateOnStart || shouldAutoUpdate) updateAll();
    }

    private void tryUpdateThis() {
        try {
            updateThis();
        } catch (Exception e) {
            reportError(e, "Could not update this installer :/", "");
        }
    }

    private void updateThis() throws IOException {
        var updater = new BrokenArrowsUpdater(this);
        updater.update(false);

        if (!updater.getProblems().isEmpty()) {
            String problemsStr = updater.getProblems().stream().map(Diagnostic::getString).collect(Collectors.joining("\n\n"));
            reportError(null, "Could not update this installer :/", problemsStr);
        } if (updater.updatedFile != null && updater.updatedFile.exists()) {
            runAndExit(updater.getUpdatedFile().getAbsolutePath());
        }
    }

    private void runAndExit(String path) throws IOException {
        Runtime.getRuntime().exec("java -jar " + path);

        try {
            cleanup();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        System.exit(0);
    }

    private void loadProperties() {
        try {
            properties.load(Main.class.getResourceAsStream("/project.properties"));

            version = properties.getProperty("version");
            artifactId = properties.getProperty("artifactId");
        } catch (Exception e) {
            reportError(e, "Could not load properties");
        }
    }

    private void initWindow() {
        settings().name += " " + version;
        frame = new JFrame(settings().name);
        panel = new BrokenArrowsPanel(new Dimension(settings().defaultWidth, settings().defaultHeight));

        if (!hasData()) panel.addScreen(Screens.INTRO.get());
        else panel.addScreen(Screens.HOME.get());

        initTrayIcon();

        InputListener listener = new InputListener();
        panel.addComponentListener(listener);
        panel.addMouseListener(listener);
        panel.addMouseMotionListener(listener);
        panel.addMouseWheelListener(listener);
        frame.addKeyListener(listener);

        frame.setIconImage(icon);
        frame.add(panel);
        frame.setBackground(Color.BLACK);
        frame.pack();
        frame.setVisible(true);
    }

    private void initTrayIcon() {
        trayIcon = new TrayIcon(icon, "Broken Arrows Updater");
        trayIcon.setImageAutoSize(true);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showWindow();
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                save();
                hideWindow();
            }
        });
    }

    public void showWindow() {
        if (frame.isVisible()) return;
        frame.setVisible(true);
        try {
            SystemTray.getSystemTray().remove(trayIcon);
        } catch (Exception e) {
            reportError(e, "Could not remove tray icon from system tray");
        }
    }

    public void hideWindow() {
        if (!frame.isVisible()) return;
        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (Exception e) {
            reportError(e, "Could not add tray icon to system tray");
        }
        frame.setVisible(false);
    }

    private boolean hasData() {
        File[] files = dataFolder.listFiles();
        return files != null && files.length > 0;
    }

    public void parseInput(String input) {
        try {
            commandGroup.execute(input);
        } catch (CommandSyntaxException | UnknownCommandException e) {
            log(e.getMessage());
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        save();
    }

    @Override
    public BrokenArrowsSettings settings() {
        return (BrokenArrowsSettings) super.settings();
    }
    public BrokenArrowsPanel panel() {
        return panel;
    }
    public Palette palette() {
        return palette;
    }
    public ExecutorService executor() {
        return executor;
    }

    public boolean isUpdatingMods() {
        return !modUpdaters.isEmpty();
    }

    public boolean isUpdatingForge() {
        return !forgeUpdaters.isEmpty();
    }

    public boolean checkForMinecraftFolder() {
        if (minecraftFolder() == null) {
            panel.addScreen(new ActionPromptScreen("No Minecraft directory found", "Please re-select your minecraft directory.",
                    "Reselect", close -> selectMcDir(done -> close.run()), this));
            return false;
        }
        return true;
    }

    public void selectMcDir(Consumer<File> onDone) {
        var selection = FileUtils.promptDir(defaultMinecraftFolder(), panel);
        if (selection != null) {
            var path = selection.toPath();
            if (!selection.exists()) {
                panel().addScreen(ReselectMCDirScreen.dirNotFound(path, () -> selectMcDir(onDone), this));
            } else if (!selection.isDirectory()) {
                panel().addScreen(ReselectMCDirScreen.notADir(path, () -> selectMcDir(onDone), this));
            } else if (!hasLauncherProfile(selection)) {
                panel().addScreen(ReselectMCDirScreen.notAMinecraftInstall(path, () -> selectMcDir(onDone), this));
            } else if (setMinecraftFolder(selection)) {
                if (onDone != null) onDone.accept(selection);
            } else {
                panel().addScreen(ReselectMCDirScreen.error(path, () -> selectMcDir(onDone), this));
            }
        }
    }

    public File defaultMinecraftFolder() {
        String mcDir = System.getProperty("os.name").toUpperCase().contains("MAC") ? "minecraft" : ".minecraft";
        return new File(FileUtils.getAppdataDir(), mcDir);
    }
    public File minecraftFolder() {
        return settings().minecraftFolder;
    }
    public File modsFolder() {
        File modsFolder = minecraftFolder().toPath().resolve("mods").toFile();

        if (modsFolder.exists()) return modsFolder;
        else try {
            modsFolder.mkdir();
            return modsFolder;
        } catch (SecurityException e) {
            reportError(e, "Could not create mods folder at '" + modsFolder.getPath() + "' because write access was denied");
        } return null;
    }
    public boolean setMinecraftFolder(File mc) {
        boolean success = settings().setMinecraftFolder(mc);
        if (success) save();
        return success;
    }

    public boolean hasLauncherProfile(File file) {
        return new File(file, "launcher_profiles.json").exists();
    }

    public File launcherProfile() {
        return new File(settings().minecraftFolder, "launcher_profiles.json");
    }

    public void rescheduleAutoUpdate() {
        if (autoUpdateLoop != null) autoUpdateLoop.cancel(false);
        autoUpdateLoop = scheduler.scheduleAtFixedRate(this::updateAll, settings().autoUpdateInterval, settings().autoUpdateInterval, settings().autoUpdateIntervalUnit);
    }

    public void updateAll() {
        info.lastAutoUpdateTime = LocalDateTime.now();
        if (!isUpdatingMods()) updateMods(__ -> {}, updater -> {
            if (!updater.getDownloadedMods().isEmpty() || !updater.getProblems().isEmpty()) {
                showModUpdateFeedback(updater);
                showWindow();
            }
        });
        if (!isUpdatingForge()) updateForge(__ -> {}, updater -> {
            if (!updater.isAlreadyUpToDate()) {
                showForgeUpdateFeedback(updater);
                showWindow();
            }
        });
    }

    public void launch() {
        List<Throwable> problems = new ArrayList<>(1);
        var info = new InfoScreen("Launching Minecraft...", "Opening Minecraft Launcher...", this);
        panel.addScreen(info);

        try {
            if (openMinecraft()) {
                info.close();
                return;
            }
        } catch (Exception e) {
            problems.add(e);
        }

        if (!openLauncher(problems)) {
            if (problems.isEmpty()) reportError(null, "Could not find Minecraft launcher");
            problems.forEach(e -> reportError(e, "Could not open Minecraft launcher"));
        }

        info.close();
    }

    private boolean openMinecraft() throws IOException, InterruptedException {
        if (minecraftLauncher.canLaunch()) {
            var p = minecraftLauncher.launch();
            if (p != null) p.waitFor();
            return p != null;
        }
        scheduleStealMinecraftBin();
        return false;
    }

    private boolean openLauncher(List<Throwable> problems) {
        File minecraftLauncher = getMinecraftLauncherDesktopShortcut();
        if (exists(minecraftLauncher)) {
            try {
                FileUtils.run(minecraftLauncher);
                return true;
            } catch (Exception e) {
                problems.add(e);
            }
        }
        return false;
    }

    public static File getMinecraftLauncherDesktopShortcut() {
        File desktop = FileUtils.getDesktopDir();
        if (!exists(desktop)) return null;
        return new File(desktop, "Minecraft Launcher.lnk");
    }

    private void setDataFolderIn(File parent) {
        try {
            if (exists(parent)) {
                this.dataFolder = new File(parent, DATA_FOLDER_NAME);
            } else {
                this.dataFolder = new File(FileUtils.thisProgramLocation(), DATA_FOLDER_NAME);
            }
            if (!dataFolder.exists() && !dataFolder.mkdirs())
                reportError(null, "Could not create data folder at " + dataFolder.getPath());
        } catch (SecurityException e) {
            dataFolder = null;
            reportError(e, "Could not set data folder in " + parent);
        }
    }

    public File dataFolder() {
        return dataFolder;
    }

    public void save() {
        if (dataFolder == null) {
            reportError(null, "Could not save settings because data folder is null");
        } else try {
            settings().write(dataFolder.toPath());
            info.write(dataFolder.toPath());
        } catch (Exception e) {
            reportError(e, "Could not save settings: " + settings);
        }
    }
    public void load() {
        if (dataFolder == null) {
            reportError(null, "Could not load settings because data folder is null");
        } else try {
            settings().read(dataFolder.toPath());
            info.read(dataFolder.toPath());

            if (settings().minecraftFolder == null) {
                panel.removeAndAddScreen(Screens.HOME.get(), Screens.INTRO.get());
                firstTimeOpening = true;
            }
        } catch (Exception e) {
            if (settings().minecraftFolder == null)
                panel.removeAndAddScreen(Screens.HOME.get(), Screens.INTRO.get());
            reportError(e, "Could not load settings");
        }
    }

    public double resolveX(Length length) {
        return length.per(panel.getWidth(), settings().defaultWidth);
    }
    public double resolveY(Length length) {
        return length.per(panel.getHeight(), settings().defaultHeight);
    }

    public int resolveXAbs(Length length) {
        return length.abs(panel.getWidth(), settings().defaultWidth);
    }
    public int resolveYAbs(Length length) {
        return length.abs(panel.getHeight(), settings().defaultHeight);
    }

    public Future<?> addTask(Runnable task, ExceptionManager exceptionManager) {
        return executor.submit(() -> {
            try {
                task.run();
            } catch (Throwable e) {
                exceptionManager.handle(e);
            }
        });
    }

    public void reportError(Throwable e, String message) {
        reportError(e, "Error", message);
    }
    public void reportError(Throwable e, String title, String message) {
        err(e, message);
        panel.addScreen(new AlertScreen(title, message + "\n\n" + StringUtils.getAbbreviatedMessage(e), this));
    }

    private class InputListener implements ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (panel.hasNoScreens()) return;
            panel.topMostScreen().mouseReleased(e.getButton(), (double) e.getX() / panel.getWidth(), (double) e.getY() / panel.getHeight());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (panel.hasNoScreens()) return;
            panel.topMostScreen().mouseClicked(e.getButton(), (double) e.getX() / panel.getWidth(), (double) e.getY() / panel.getHeight());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (panel.hasNoScreens()) return;
            panel.topMostScreen().mousePressed(e.getButton(), (double) e.getX() / panel.getWidth(), (double) e.getY() / panel.getHeight());
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (panel.hasNoScreens()) return;
            panel.topMostScreen().mouseEntered((double) e.getX() / panel.getWidth(), (double) e.getY() / panel.getHeight());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (panel.hasNoScreens()) return;
            panel.topMostScreen().mouseExited((double) e.getX() / panel.getWidth(), (double) e.getY() / panel.getHeight());
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (panel.hasNoScreens()) return;
            double x = (double) e.getX() / panel.getWidth();
            double y = (double) e.getY() / panel.getHeight();
            panel.mouseX = x;
            panel.mouseY = y;
            panel.topMostScreen().mouseMoved(x, y);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (panel.hasNoScreens()) return;
            double x = (double) e.getX() / panel.getWidth();
            double y = (double) e.getY() / panel.getHeight();
            panel.mouseX = x;
            panel.mouseY = y;
            panel.topMostScreen().mouseDragged(x, y);
        }

        @Override
        public void componentResized(ComponentEvent e) {
            panel.onResized();
        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {

        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }

        @Override
        public void keyTyped(KeyEvent e) {
            if (panel.hasNoScreens()) return;
            panel.topMostScreen().keyTyped(e.getKeyCode());
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_F4 && !e.isAltDown()) System.exit(130);
            if (panel.hasNoScreens()) return;
            panel.topMostScreen().keyPressed(e.getKeyCode());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (panel.hasNoScreens()) return;
            panel.topMostScreen().keyReleased(e.getKeyCode());
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (panel.hasNoScreens()) return;
            panel.topMostScreen().mouseWheelMoved(e);
        }
    }
}
