package com.sakuray.picture;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JavaFxWebToPicture2 {

    public static JFrame jFrame;
    public static JFXPanel jfxPanel;

    public static void main(String args[]) {
        jFrame = new JFrame("Demo Browser");
        jfxPanel = new JFXPanel();
        jFrame.add(jfxPanel);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        WebView browser = new WebView();
                        jfxPanel.setScene(browser.getScene());
                        jFrame.setSize((int) browser.getWidth(), (int) browser.getHeight());

                        browser.getEngine().getLoadWorker().stateProperty().addListener(
                                new ChangeListener<Worker.State>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                                        if (Worker.State.SUCCEEDED == newValue) {
                                            captureView();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private static void captureView() {
        BufferedImage bi = new BufferedImage(jfxPanel.getWidth(), jfxPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bi.createGraphics();
        jfxPanel.paint(graphics);
        try {
            ImageIO.write(bi, "PNG", new File("d:/demo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphics.dispose();
        bi.flush();
    }
}
