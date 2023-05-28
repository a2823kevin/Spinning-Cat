package edu.proj;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;

public class SceneMain extends JFrame{
    private JPanel contentPane = new JPanel();
    private CatDB db;
    private float scalingRatio;

    private Component modelDisplay;
    private JFXPanel rcUI;
    private JFXPanel pcUI;

    public SceneMain(float scalingRatio, CatDB db, Cef cef, boolean runSizeRecording) throws IOException, UnsupportedPlatformException, InterruptedException, CefInitializationException {
        this.db = db;
        this.scalingRatio = scalingRatio;
        SceneMain scene = this;

        contentPane.setLayout(new BorderLayout());
        setIconImage(ImageIO.read(new URL("http://localhost/icon/maxwell.png")));
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        modelDisplay = cef.toAwtComponent();
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

            modelDisplay.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    componentReady[0] = true;
                }
            });
            contentPane.add(modelDisplay, BorderLayout.CENTER);

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

                    pcUI = new PlayControlUI(1.0, db, null).toAwtComponent();
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
            contentPane.add(modelDisplay, BorderLayout.CENTER);

            try {
                Platform.startup(new Runnable() {
                    @Override
                    public void run() {
                        rcUI = new RotationControlUI(scalingRatio, db).toAwtComponent();
                        contentPane.add(rcUI, BorderLayout.SOUTH);
    
                        pcUI = new PlayControlUI(scalingRatio, db, scene).toAwtComponent();
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
    
                        pcUI = new PlayControlUI(scalingRatio, db, scene).toAwtComponent();
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
        db.insertComponentSize("model_UI", modelDisplay.getWidth(), modelDisplay.getHeight());
        db.insertComponentSize("rotation_control_UI", rcUI.getWidth(), rcUI.getHeight());
        db.insertComponentSize("play_control_UI", pcUI.getWidth(), pcUI.getHeight());
    }

    private void resizeComponents() throws SQLException {
        modelDisplay.setPreferredSize(db.getComponentSize("model_UI", scalingRatio));
        rcUI.setPreferredSize(db.getComponentSize("rotation_control_UI", scalingRatio));
        pcUI.setPreferredSize(db.getComponentSize("play_control_UI", scalingRatio));
    }

    public Component getModelDisplay() {
        return modelDisplay;
    }

    public void refreshModelDisplay() {
        contentPane.remove(modelDisplay);
        contentPane.add(modelDisplay, BorderLayout.CENTER);
    }
}