package me.cometkaizo.brokenarrows;

import me.cometkaizo.util.DownloadUtils;

import java.net.*;
import java.util.List;

public abstract class Updater {
    public static final String SEPARATOR = "@";
    protected final BrokenArrowsApp app;
    protected List<Diagnostic> problems;

    public Updater(BrokenArrowsApp app) {
        this.app = app;
    }

    public abstract void update(boolean force);

    protected byte[] downloadBytes(URI uri) {
        try {
            return DownloadUtils.download(getUrl(uri));
        } catch (UnknownHostException | NoRouteToHostException e) {
            reportError(e, "Could not connect to '" + uri.getHost() + "'. The website may be down or you may not be connected to the internet.");
        } catch (Exception e) {
            reportError(e, "Could not download from '" + uri + "' because an exception occurred");
        } return null;
    }
    protected String downloadStr(URI uri) {
        try {
            return DownloadUtils.downloadStr(getUrl(uri));
        } catch (UnknownHostException | NoRouteToHostException e) {
            reportError(e, "Could not connect to '" + uri.getHost() + "'. The website may be down or you may not be connected to the internet.");
        } catch (Exception e) {
            reportError(e, "Could not download from '" + uri + "' because an exception occurred");
        } return null;
    }

    protected URL getUrl(URI uri) {
        try {
            return uri == null ? null : uri.toURL();
        } catch (IllegalArgumentException e) {
            reportError(e, "URI has no scheme: " + uri);
        } catch (MalformedURLException e) {
            reportError(e, "Malformed URL '" + uri + "'");
        } return null;
    }

    protected void reportError(Throwable e, String s) {
        app.err(e, s);
        problems.add(new Diagnostic.Error(e, s));
    }

    public List<Diagnostic> getProblems() {
        return List.copyOf(problems);
    }
}
