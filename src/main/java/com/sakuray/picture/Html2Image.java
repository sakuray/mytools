package com.sakuray.picture;

import gui.ava.html.image.generator.HtmlImageGenerator;

public class Html2Image {

    public static void main(String[] args) {
        HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
        imageGenerator.loadUrl("https://www.taobao.com/");
        imageGenerator.getBufferedImage();
        imageGenerator.saveAsImage("d:/html.png");
    }
}
