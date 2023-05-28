package edu.proj;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScenceFloatingWindow extends JFrame{
    private JPanel contentPane = new JPanel();
    private Point initPos = null;
    private Image blankImage;
    private Image expandImage;

    public ScenceFloatingWindow(SceneMain sceneMain, float ratio) throws MalformedURLException, IOException {
        setUndecorated(true);
        contentPane.setLayout(null);
        contentPane.setBackground(Color.WHITE);
        setIconImage(ImageIO.read(new URL("http://localhost/icon/maxwell.png")));
        setContentPane(contentPane);

        Component modelDisplay = sceneMain.getModelDisplay();
        int newWidth = (int)(modelDisplay.getWidth()*ratio);
        int newHeight = (int)(modelDisplay.getHeight()*ratio);
        int btnWidth = (int)(newWidth/5);

        blankImage = ImageIO.read(new URL("http://localhost/icon/blank.png"));
        blankImage = blankImage.getScaledInstance(btnWidth, btnWidth, Image.SCALE_DEFAULT);
        expandImage = ImageIO.read(new URL("http://localhost/icon/expand.png"));
        expandImage = expandImage.getScaledInstance(btnWidth, btnWidth, Image.SCALE_DEFAULT);

        ImageIcon btnImage = new ImageIcon(blankImage);

        JButton btn = new JButton(btnImage);
        btn.setBounds(newWidth, 0, btnWidth, btnWidth);
        btn.setBorder(null);
        contentPane.add(btn);


        modelDisplay.setLocation(0, btnWidth);
        contentPane.add(modelDisplay);
        contentPane.setPreferredSize(new Dimension(newWidth+btnWidth, newHeight+btnWidth));
        pack();
        setVisible(true);

        //action
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                if (initPos==null) {
                    initPos = evt.getPoint();
                    return;
                }

                int newPosX = getX() + evt.getX() - initPos.x;
                int newPosY = getY() + evt.getY() - initPos.y;
                setLocation(newPosX, newPosY);
            }

            @Override
            public void mouseMoved(MouseEvent evt) {}
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            @Override
            public void mouseReleased(MouseEvent evt) {
                initPos = null;
            }
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
        });

        btn.addMouseListener(new MouseListener() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnImage.setImage(expandImage);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                btnImage.setImage(blankImage);
            }
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
                dispose();
                try {
                    RequestSender.changeCanvas(1.0f);
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
                sceneMain.refreshModelDisplay();
                sceneMain.setVisible(true);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
        });
    }
}
