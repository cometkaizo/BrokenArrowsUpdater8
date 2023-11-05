package me.cometkaizo.brokenarrows;

import de.ralleytn.simple.json.JSONObject;
import de.ralleytn.simple.json.JSONParseException;
import me.cometkaizo.brokenarrows.jar.*;
import me.cometkaizo.util.DownloadUtils;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarInputStream;

import static me.cometkaizo.util.FileUtils.thisProgramLocation;
import static me.cometkaizo.util.JSONUtils.*;

public class ForgeUpdater extends Updater {
    public static final Function<String, String> FILE_FILTER = file ->
            file.startsWith("/net/minecraftforge") && file.endsWith(".class") && !file.contains("$") ?
                    file.replace('/', '.').substring(1, file.length() - 6) : null;
    protected JarExecutor jarExecutor;
    public final List<Consumer<Event>> listeners = new ArrayList<>(1);
    protected boolean success;
    protected boolean alreadyUpToDate;

    public ForgeUpdater(BrokenArrowsApp app) {
        super(app);
    }

    @Override
    public void update(boolean force) {
        try {
            updateOrThrow(force);
        } catch (Exception e) {
            reportError(e, "An Exception occurred");
        }
    }

    private void updateOrThrow(boolean force) {
        success = false;
        alreadyUpToDate = false;
        problems = new ArrayList<>(1);

        ForgeInfo forgeInfo = ForgeInfo.download(this);
        if (forgeInfo != null) {
            var isForgeUpToDate = hasForgeVersion(forgeInfo.namespace);
            if (isForgeUpToDate.isPresent()) {
                alreadyUpToDate = isForgeUpToDate.get();
                if (!alreadyUpToDate || force)
                    success = tryAutoInstall(forgeInfo);
            }
        }
        broadcast(new Event.End());
    }

    private Optional<Boolean> hasForgeVersion(String forgeVersion) {
        try {
            JSONObject launcherProfile = readObject(app.launcherProfile());
            JSONObject profiles = getOrCreate(launcherProfile, "profiles");

            for (JSONObject profile : getObjects(profiles)) {
                if (forgeVersion.equals(profile.getString("lastVersionId"))) return Optional.of(true);
            }
            return Optional.of(false);
        } catch (IOException e) {
            reportError(e, "Could not read current forge version");
        } catch (JSONParseException e) {
            reportError(e, "Could not read current forge version: launcher profile could not be parsed1");
        } return Optional.empty();
    }

    private boolean tryAutoInstall(ForgeInfo forgeInfo) {
        try (var jarStream = getJarStream(forgeInfo.downloadURI())) {
            if (jarStream == null) return false;

            broadcast(new Event.LoadingJarClasses());
            jarExecutor = new JarExecutor(jarStream, FILE_FILTER);
            jarExecutor.loadClasses(false);
            return autoInstall();
        } catch (ClassNotFoundException e) {
            reportError(e, "Could not auto-install forge: could not load classes");
        } catch (JarExecutionException e) {
            reportError(e, "Could not auto-install forge: could not directly execute jar file");
        } catch (IOException e) {
            reportError(e, "Could not auto-install forge: an IO exception occurred");
        } catch (Throwable e) {
            reportError(e, "Could not auto-install forge: an unexpected error occurred");
            if (e instanceof Error err) throw err;
        } return false;
    }

    private boolean autoInstall() {
        broadcast(new Event.AutoInstallForge());

        var progressCallbackClass = ClassSupplier.of("net.minecraftforge.installer.actions.ProgressCallback");
        var installV1Class = ClassSupplier.of("net.minecraftforge.installer.json.InstallV1");
        var clientInstallClass = ClassSupplier.of("net.minecraftforge.installer.actions.ClientInstall");
        var utilClass = ClassSupplier.of("net.minecraftforge.installer.json.Util");

        Object installV1 = jarExecutor.invokeStatic(
                MethodSupplier.of(utilClass, "loadInstallProfile"));
        Object monitor = jarExecutor.invokeStatic(
                MethodSupplier.of(progressCallbackClass, "withOutputs", OutputStream[].class),
                (Object) new OutputStream[] {System.out, new BufferedStringOutputStream(new ByteArrayOutputStream(), this::broadcastForgeEvent)});

        Object clientInstall = jarExecutor.invokeConstructor(
                ConstructorSupplier.of(clientInstallClass, installV1Class, progressCallbackClass),
                installV1, monitor);

        boolean success = (Boolean) jarExecutor.invokeMethod(MethodSupplier.of(clientInstallClass, "run", File.class, Predicate.class, File.class),
                clientInstall, app.minecraftFolder(), (Predicate<?>) s -> true, thisProgramLocation());
        if (!success) reportError(null, "Forge installer failed to install; no further information");
        return success;
    }

    private void broadcastForgeEvent(String message) {
        broadcast(new Event.ForgeInstallerEvent(message));
    }

    private JarInputStream getJarStream(URI downloadURI) {
        broadcast(new Event.GettingJarStream(downloadURI));
        URLConnection con;
        InputStream downloadStream;
        try {
            con = downloadURI.toURL().openConnection(); // open the url connection
            downloadStream = new DataInputStream(con.getInputStream());
            return new JarInputStream(downloadStream);
        } catch (MalformedURLException e) {
            reportError(e, "Malformed URL '" + downloadURI + "'");
        } catch (UnknownHostException | NoRouteToHostException e) {
            reportError(e, "Could not connect to '" + downloadURI.getHost() + "'. The website may be down or you may not be connected to the internet.");
        } catch (IOException e) {
            reportError(e, "An IO exception occurred");
        } catch (Exception e) {
            reportError(e, "Could not download from '" + downloadURI + "' because an exception occurred");
        } return null;
    }

    private void broadcast(Event event) {
        listeners.forEach(l -> l.accept(event));
    }

    public boolean isAlreadyUpToDate() {
        return alreadyUpToDate;
    }

    public record ForgeInfo(String namespace, String downloadURL) {
        public static final URI FORGE_VERSION_DOWNLOAD_LINK = URI.create("https://www.dropbox.com/scl/fi/55qpo84k14dlrac8vxg7o/forge.txt?rlkey=zwqs4qu1ymv1e41wmcnwavktb&dl=1");
        public static ForgeInfo download(ForgeUpdater updater) {
            updater.broadcast(new Event.DownloadForgeInfo(FORGE_VERSION_DOWNLOAD_LINK));

            String data = updater.downloadStr(FORGE_VERSION_DOWNLOAD_LINK);
            if (data == null) return null;

            return new ForgeInfo(data.split(SEPARATOR)[0], data.split(SEPARATOR)[1]);
        }
        public static ForgeInfo download() {
            try {
                String data = DownloadUtils.downloadStr(FORGE_VERSION_DOWNLOAD_LINK.toURL());
                return new ForgeInfo(data.split(SEPARATOR)[0], data.split(SEPARATOR)[1]);
            } catch (Exception e) {
                return null;
            }
        }

        public URI downloadURI() {
            return URI.create(downloadURL);
        }
    }


    public interface Event {
        String getString();


        record DownloadForgeInfo(URI downloadURI) implements Event {
            @Override
            public String getString() {
                return "Download forge information from '" + downloadURI + "'...";
            }
        }
        record GettingJarStream(URI downloadURI) implements Event {
            @Override
            public String getString() {
                return "Downloading forge installer from '" + downloadURI + "'...";
            }
        }
        record LoadingJarClasses() implements Event {
            @Override
            public String getString() {
                return "Loading forge installer classes... (This may take a while)";
            }
        }
        record AutoInstallForge() implements Event {
            @Override
            public String getString() {
                return "Trying to automatically install forge...";
            }
        }
        record ForgeInstallerEvent(String message) implements Event {
            @Override
            public String getString() {
                return "  > " + message;
            }
        }
        record End() implements Event {
            @Override
            public String getString() {
                return "End";
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForgeUpdater that = (ForgeUpdater) o;
        return success == that.success && alreadyUpToDate == that.alreadyUpToDate && Objects.equals(jarExecutor, that.jarExecutor) && Objects.equals(listeners, that.listeners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jarExecutor, listeners, success, alreadyUpToDate);
    }
}
