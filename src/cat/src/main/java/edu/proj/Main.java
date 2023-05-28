package edu.proj;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.lang.Math;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, UnsupportedPlatformException, InterruptedException, CefInitializationException {
        //http server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Runtime.getRuntime().exec("node src/server.js");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //init
        CatDB db = new CatDB();
        boolean runInit = !db.getTableNames().contains("component_size");
        Cef cef = new Cef("http://localhost/index");

        if (runInit) {
            db.initTables();
        }

        //main scene
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new SceneMain(getScalingRatio(), db, cef, runInit);
                }
                catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static float getScalingRatio() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float wRatio = (float) screenSize.getWidth() / 1360;
        float hRatio = (float) screenSize.getHeight() / 768;
        return Math.min(wRatio, hRatio);
    }
}