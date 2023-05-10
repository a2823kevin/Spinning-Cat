package edu.proj;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.OS;
import org.cef.CefApp.CefAppState;
import org.cef.CefSettings.LogSeverity;
import org.cef.browser.CefBrowser;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler;

public class Cef {
    private CefAppBuilder builder = new CefAppBuilder();
    private CefBrowser browser;

    public Cef(String url) throws IOException, UnsupportedPlatformException, InterruptedException, CefInitializationException {
        initCefBuilder();
        CefApp app = builder.build();
        CefClient client = app.createClient();
        browser = client.createBrowser(url, OS.isLinux(), false);
    }

    private void initCefBuilder() {
        builder.setInstallDir(new File("../../lib/jcef-bundle"));
        builder.setProgressHandler(new ConsoleProgressHandler());
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
                @Override
                public void stateHasChanged(CefAppState state) {
                    if (state == CefAppState.TERMINATED) {
                        System.exit(0);
                    }
                }
            }
        );
        builder.getCefSettings().windowless_rendering_enabled = OS.isLinux();
        builder.getCefSettings().log_severity = LogSeverity.LOGSEVERITY_DISABLE;
        builder.addJcefArgs("--autoplay-policy=no-user-gesture-required");
    }

    public Component toAwtComponent() {
        return browser.getUIComponent();
    }
}
