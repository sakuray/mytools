package com.sakuray.picture;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class JavaFxWebBrowser extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception {
//        primaryStage.setWidth(400);
//        primaryStage.setHeight(600);
        Scene scene = new Scene(new Group());

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.setContent(browser);

        webEngine.getLoadWorker().stateProperty()
                .addListener(new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {

                        if (newState == Worker.State.SUCCEEDED) {
                            primaryStage.setTitle(webEngine.getLocation());
                            System.out.println("加载完成");
                            System.out.println(browser.getWidth() + ";;;" + browser.getHeight());
                            Image image = browser.snapshot(null, null);
                            try {
                                // 输出图像
                                System.out.println("开始生成图片");
                                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "jpg", new File("d:/taobao.jpg"));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            // 退出操作
                            System.exit(0);
                        }

                    }
                });
        webEngine.load("https://www.taobao.com/");

        scene.setRoot(browser);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
