package edu.proj;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;

public class SceneMain extends JFrame{
    JPanel contentPane = new JPanel();
    CatDB db;
    float scalingRatio;

    Component modelUI;
    JFXPanel rcUI;
    JFXPanel pcUI;

    public SceneMain(float scalingRatio, CatDB db, Cef cef, boolean runSizeRecording) throws IOException, UnsupportedPlatformException, InterruptedException, CefInitializationException {
        this.db = db;
        this.scalingRatio = scalingRatio;

        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //record component size
        if (runSizeRecording) {
            System.out.println("staring initialize...");
            //to monitor if all component has shown
            boolean[] componentReady = {false, false, false};
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        int readyCount = 0;
                        for (int i=0; i<componentReady.length; i++) {
                            if (componentReady[i]==true) {
                                readyCount += 1;
                            }
                        }
                        if (readyCount==componentReady.length) {
                            break;
                        }
                    }
                    try {
                        recordComponentSize();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("ending initialize...");
                    setVisible(false);

                    try {
                        new SceneMain(scalingRatio, db, cef, false);
                    }
                    catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            modelUI = cef.toAwtComponent();
            modelUI.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    componentReady[0] = true;
                }
            });
            contentPane.add(modelUI, BorderLayout.CENTER);

            Platform.startup(new Runnable() {
                @Override
                public void run() {
                    rcUI = new RotationControlUI(1.0, db).toAwtComponent();
                    rcUI.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            componentReady[1] = true;
                        }
                    });
                    contentPane.add(rcUI, BorderLayout.SOUTH);

                    pcUI = new PlayControlUI(1.0, db).toAwtComponent();
                    pcUI.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            componentReady[2] = true;
                        }
                    });
                    contentPane.add(pcUI, BorderLayout.EAST);
                    setSize(775, 695);
                    setVisible(true);
                }
            });
        }

        //general run
        else {
            modelUI = cef.toAwtComponent();
            contentPane.add(modelUI, BorderLayout.CENTER);

            try {
                Platform.startup(new Runnable() {
                    @Override
                    public void run() {
                        rcUI = new RotationControlUI(scalingRatio, db).toAwtComponent();
                        contentPane.add(rcUI, BorderLayout.SOUTH);
    
                        pcUI = new PlayControlUI(scalingRatio, db).toAwtComponent();
                        contentPane.add(pcUI, BorderLayout.EAST);
    
                        setSize((int)(775*scalingRatio), (int)(695*scalingRatio));
                        try {
                            resizeComponents();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        setVisible(true);
                    }
                });
            }
            catch (IllegalStateException e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        rcUI = new RotationControlUI(scalingRatio, db).toAwtComponent();
                        contentPane.add(rcUI, BorderLayout.SOUTH);
    
                        pcUI = new PlayControlUI(scalingRatio, db).toAwtComponent();
                        contentPane.add(pcUI, BorderLayout.EAST);
    
                        setSize((int)(775*scalingRatio), (int)(695*scalingRatio));
                        try {
                            resizeComponents();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        setVisible(true);
                    }
                });
            }
        }
    }

    private void recordComponentSize() throws SQLException {
        db.insertComponentSize("model_UI", modelUI.getWidth(), modelUI.getHeight());
        db.insertComponentSize("rotation_control_UI", rcUI.getWidth(), rcUI.getHeight());
        db.insertComponentSize("play_control_UI", pcUI.getWidth(), pcUI.getHeight());
    }

    private void resizeComponents() throws SQLException {
        modelUI.setPreferredSize(db.getComponentSize("model_UI", scalingRatio));
        rcUI.setPreferredSize(db.getComponentSize("rotation_control_UI", scalingRatio));
        pcUI.setPreferredSize(db.getComponentSize("play_control_UI", scalingRatio));
    }
}