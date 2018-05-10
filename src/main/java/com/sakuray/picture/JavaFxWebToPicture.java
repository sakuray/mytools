package com.sakuray.picture;

import com.sun.javafx.tk.Toolkit;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;

public class JavaFxWebToPicture extends Application {

    public static void htmlToPicture(String html, final String file, final Stage stage, final String json) {
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                if("variables:ready".equals(event.getData())) {
                    System.out.println("document loaded");
                    PauseTransition ptr = new PauseTransition(Duration.seconds(3));
                    ptr.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println(printHtml(webEngine.getDocument()));
                            snapshot(webView, file);
                        }
                    });
                    ptr.play();
                }
            }
        });
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                System.out.println(oldValue.toString() + ":::" + newValue.toString());
                if (newValue == Worker.State.SUCCEEDED) {
                    System.out.println("加载完成" + new Date());
                    JSObject jsObject = (JSObject) webEngine.executeScript("window");
                    jsObject.setMember("java", new JavaApp());
                    webEngine.executeScript("ready()");
                }
            }
        });
//        URL url = JavaFxWebToPicture.class.getClassLoader().getResource("index.html");
//        System.out.println(url);
//        webEngine.load(url.toExternalForm());
        webEngine.loadContent(html);
        stage.setScene(new Scene(webView));
//        stage.show();
        stage.hide();
//        reflectInvisible(stage, true);
        System.out.println("网页加载完毕");

//        HTMLEditor htmlEditor = new HTMLEditor();
//        htmlEditor.setHtmlText(html);
//        Scene scene = new Scene(htmlEditor);
//        WritableImage image = htmlEditor.snapshot(new SnapshotParameters(), null);
//        try {
//            // 输出图像
//            System.out.println("开始生成图片");
//            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(file));
//            System.out.println("生成图片完成");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        stage.setScene(scene);
//        stage.show();
    }

    public static void toPicture(String url, final String file, Stage stage) {
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                System.out.println(oldValue.toString() + ":::" + newValue.toString());
                if (newValue == Worker.State.SUCCEEDED) {
//                    System.out.println("加载完成" + new Date());
//                    System.out.println(webView.getWidth() + ";;;" + webView.getHeight() + "...." +new Date());
//                    double width = Double.parseDouble(webEngine.executeScript("document.documentElement.scrollWidth").toString());
//                    double height = Double.parseDouble(webEngine.executeScript("document.documentElement.scrollHeight").toString());
//                    webEngine.executeScript("document.body.style.overflow = 'hidden';");
//                    System.out.println(width+":::+"+height);
////                    webView.resize(width, height);
//                    System.out.println(webView.getWidth() + ";;;" + webView.getHeight() + "...." +new Date());
                    System.out.println(printHtml(webEngine.getDocument()));
//                    AnimationTimer animationTimer = new AnimationTimer() {
//                        private int pulseCounter;
//
//                        @Override
//                        public void handle(long now) {
//                            pulseCounter++;
//                            if(pulseCounter > 2) {
////                                        stage.close();
//                                WritableImage image = webView.snapshot(new SnapshotParameters(), null);
//                                try {
//                                    System.out.println("开始生成图片");
//                                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(file));
//                                    System.out.println("生成完成图片");
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    };
//                    animationTimer.start();

                }
            }
        });
        webEngine.load(url);
//        stage.setScene(new Scene(webView));
//        stage.show();
//        reflectInvisible(stage, false);
//        stage.hide();
        System.out.println("网页加载完毕");
    }

    public static void main(String[] args) {
        launch(args);
//        System.out.println(wrapJS());
    }

    public static String wrapJS() {
        StringBuilder sb = new StringBuilder();
        sb.append("function getWAH() {");
        sb.append(HtmlToPicture.LS);
        sb.append(HtmlToPicture.getScreenWidthHeight());
        sb.append(HtmlToPicture.LS);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
//        toPicture("https://github.com/", "d:/github.jpg",primaryStage);
        toPicture("https://www.taobao.com/", "d:/taobao.jpg",primaryStage);
//        toPicture("https://www.baidu.com/", "d:/baidu.jpg", primaryStage);
//        toPicture("http://172.19.80.224:8090/b2b/", "d:/jqr.jpg", primaryStage);
//        InputStream inputStream = JavaFxWebToPicture.class.getClassLoader().getResourceAsStream("index.html");
//        int lenght = -1;
//        byte[] tmp = new byte[1024];
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        while((lenght = inputStream.read(tmp)) != -1) {
//            baos.write(tmp, 0, lenght);
//        }
//        String html = new String(baos.toByteArray());
////        System.out.println(html);
//        htmlToPicture(html, "d:/test.png", primaryStage, "你好不是");
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

    public static void reflectInvisible(Stage stage, boolean isVisible) {
        Class<?> clazz = stage.getClass().getSuperclass();
        try {
            Method method = clazz.getMethod("setShowing", Boolean.class);
            method.setAccessible(true);
            method.invoke(stage, isVisible);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
