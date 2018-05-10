package com.sakuray.picture;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Date;

public class JFrameBrowser {

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setLayout(new BorderLayout());
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        final JFXPanel jfxPanel = new JFXPanel();
        jFrame.add(jfxPanel, BorderLayout.CENTER);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView webView = new WebView();
                jfxPanel.setScene(new Scene(webView));
                init(webView, "d:/test.png");
//                webView.getEngine().load("http://www.baidu.com");
//                URL url = JFrameBrowser.class.getClassLoader().getResource("index.html");
//                webView.getEngine().loadContent(url.toExternalForm());
                InputStream inputStream = JavaFxWebToPicture.class.getClassLoader().getResourceAsStream("index.html");
                int lenght = -1;
                byte[] tmp = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    while ((lenght = inputStream.read(tmp)) != -1) {
                        baos.write(tmp, 0, lenght);
                    }
                    String html = new String(baos.toByteArray());
                    webView.getEngine().loadContent(html);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        jFrame.setVisible(false);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(null, "xxx", "yyy", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }

        });

    }

    private static void init(final WebView webView, final String file) {
        webView.getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                if("variables:ready".equals(event.getData())) {
                    System.out.println("document loaded");
                    PauseTransition ptr = new PauseTransition(Duration.seconds(3));
                    ptr.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println(printHtml(webView.getEngine().getDocument()));
                            snapshot(webView, file);
                        }
                    });
                    ptr.play();
                }
            }
        });
        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                System.out.println(oldValue.toString() + ":::" + newValue.toString());
                if (newValue == Worker.State.SUCCEEDED) {
                    System.out.println("加载完成" + new Date());
                    JSObject jsObject = (JSObject) webView.getEngine().executeScript("window");
                    jsObject.setMember("java", new JavaApp());
                    webView.getEngine().executeScript("ready()");
                }
            }
        });
    }

    public static String printHtml(Document document) {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transFormer = null;
        try {
            transFormer = transFactory.newTransformer();
//            transFormer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
            DOMSource domSource = new DOMSource(document);

            StringWriter sw = new StringWriter();
            StreamResult xmlResult = new StreamResult(sw);

            transFormer.transform(domSource, xmlResult);
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void snapshot(WebView webView, String file) {
        WritableImage image = webView.snapshot(new SnapshotParameters(), null);
        try {
            // 输出图像
            System.out.println("开始生成图片");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(file));
            System.out.println("生成图片完成");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
