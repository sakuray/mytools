package com.sakuray.picture;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class JFrameJavaFx {

    static WebView webView = null;

    private static void gotoURL(final String url) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().load(url);
            }
        });
    }

    /**
     * @param args
     *            the command line arguments
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        // TODO code application logic here
        final JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        final JFXPanel jFXPanel = new JFXPanel();
        frame.add(jFXPanel, "Center");

        final JPanel controlPanel = new JPanel();
        frame.add(controlPanel, "North");
        final JTextField urlField = new JTextField();
        final JButton goButton = new JButton("GO");
        ///////////////////////////////////////////////////////////////////////////////////
        urlField.setText("http://www.baidu.com");

        controlPanel.setLayout(new BorderLayout());
        urlField.setPreferredSize(new Dimension(frame.getWidth() - 100, 1));
        controlPanel.add(urlField, BorderLayout.WEST);
        controlPanel.add(goButton, BorderLayout.EAST);

        controlPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                controlPanel.setLayout(new BorderLayout());
                urlField.setPreferredSize(new Dimension(frame.getWidth() - 100, 1));
                controlPanel.add(urlField, BorderLayout.WEST);
                controlPanel.add(goButton, BorderLayout.EAST);
            }

        });
        frame.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                controlPanel.setLayout(new BorderLayout());
                urlField.setPreferredSize(new Dimension(frame.getWidth() - 100, 1));
                controlPanel.add(urlField, BorderLayout.WEST);
                controlPanel.add(goButton, BorderLayout.EAST);
            }
        });
        goButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String urlString = urlField.getText();
                gotoURL(urlString);
            }

        });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView = new WebView();
                jFXPanel.setScene(new Scene(webView));
                webView.getEngine().load("http://www.baidu.com");
            }
        });

        final JWindow splashWindow = new JWindow();
        splashWindow.setSize(1024, 768);
        splashWindow.setLocationRelativeTo(null);
        splashWindow.setLayout(new BorderLayout());
//        File file = new File(JFrameJavaFx.class.getResource("fox.png").toURI());
//        ImageIcon icon = new ImageIcon(file.toURL());
//        JLabel label = new JLabel(icon);
//        splashWindow.add(label);
        Thread t = new Thread() {
            public void run() {
                frame.setVisible(false);
                splashWindow.setVisible(true);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
//                    Logger.getLogger(SwingFinal.class.getName()).log(Level.SEVERE, null, ex);
                }
                splashWindow.setVisible(false);
                frame.setVisible(true);

            }
        };
        t.setDaemon(true);
        t.start();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(null, "xxx", "yyy", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }

        });


    }
}
