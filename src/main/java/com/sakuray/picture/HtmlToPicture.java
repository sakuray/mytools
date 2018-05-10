package com.sakuray.picture;

import chrriis.dj.nativeswing.swtimpl.NativeComponent;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HtmlToPicture {

    final static public String LS = System.getProperty("line.separator", "/n");

    static boolean visible = true;

    static class PrintScreen4DJNativeSwingUtils extends JPanel {

        private static final long serialVersionUID = 1L;
        // 行分隔符

        // 文件分割符
        final static public String FS = System.getProperty("file.separator", "//");
        // 当网页超出目标大小时 截取
        final static public int maxWidth = 375;
        final static public int maxHeight = 667;

        /**
         * @param file
         *            预生成的图片全路径
         * @param url
         *            网页地址
         *  width
         *            打开网页宽度 ，0 = 全屏
         *  height
         *            打开网页高度 ，0 = 全屏
         * @return boolean
         * @author AndyBao
         * @version V4.0, 2016年9月28日 下午3:55:52
         */
        public PrintScreen4DJNativeSwingUtils(final String file, final String url,
                                              final String WithResult) {
            super(new BorderLayout());
            JPanel webBrowserPanel = new JPanel(new BorderLayout());
            final JWebBrowser webBrowser = new JWebBrowser(null);
            webBrowser.setBarsVisible(visible);
            webBrowser.navigate(url);
            webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
            webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
                // 监听加载进度
                public void loadingProgressChanged(WebBrowserEvent e) {
                    // 当加载完毕时
                    if (e.getWebBrowser().getLoadingProgress() == 100) {
                        System.out.println(e.getWebBrowser().getBrowserType() + ":" + e.getWebBrowser().getBrowserVersion());
                        String result = (String) webBrowser
                                .executeJavascriptWithResult(WithResult);
                        System.out.println("网页加载完毕：" + result);
                        int index = result == null ? -1 : result.indexOf(":");
                        NativeComponent nativeComponent = webBrowser
                                .getNativeComponent();
                        Dimension originalSize = nativeComponent.getSize();
                        Dimension imageSize = new Dimension(Integer.parseInt(result
                                .substring(0, index)), Integer.parseInt(result
                                .substring(index + 1)));
                        imageSize.width = Math.max(originalSize.width,
                                imageSize.width);
                        imageSize.height = Math.max(originalSize.height,
                                imageSize.height);
                        nativeComponent.setSize(imageSize);
                        BufferedImage image = new BufferedImage(imageSize.width,
                                imageSize.height, BufferedImage.TYPE_INT_RGB);
                        nativeComponent.paintComponent(image);
//                        nativeComponent.setSize(originalSize);
                        // 当网页超出目标大小时
                        if (imageSize.width > maxWidth
                                || imageSize.height > maxHeight) {
//                             截图部分图形
                            int width = imageSize.width;
                            int height = imageSize.height;
                            if(imageSize.width > maxWidth) {
                                width = maxWidth;
                            }
                            if(imageSize.height > maxHeight) {
                                height = maxHeight;
                            }
                            System.out.println("图片大小：" + width + ":" + height);
                            image = image.getSubimage(0, 0, width, height);
                            // 此部分为使用缩略图
//                            int width = image.getWidth(), height = image.getHeight();
//                            double w = 1, h = 1;
//                            if(width > maxWidth) {
//                                w = (double) maxWidth / width;
//                            }
//                            if(height > maxHeight) {
//                                h = (double) maxHeight / height;
//                            }
//                            System.out.println("缩略比例：" + w +":" + h);
//                            AffineTransform tx = new AffineTransform();
//                            tx.scale(w, h);
//                            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR); //缩小
//                            image = op.filter(image, null);
                        }
                        try {
                            // 输出图像
                            System.out.println("开始生成图片");
                            ImageIO.write(image, "jpg", new File(file));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        try {
                            Thread.sleep(100000);
                        } catch (InterruptedException x) {
                            x.printStackTrace();
                        }
                        // 退出操作
                        System.exit(0);
                    }
                }
            });
            add(webBrowserPanel, BorderLayout.CENTER);
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            add(panel, BorderLayout.SOUTH);
        }

    }


    // 以javascript脚本获得网页全屏后大小
    public static String getScreenWidthHeight() {

        StringBuffer jsDimension = new StringBuffer();
        jsDimension.append("var width = 0;").append(LS);
        jsDimension.append("var height = 0;").append(LS);
        jsDimension.append("if(document.documentElement) {").append(LS);
        jsDimension
                .append("  width = Math.max(width, document.documentElement.scrollWidth);")
                .append(LS);
        jsDimension
                .append("  height = Math.max(height, document.documentElement.scrollHeight);")
                .append(LS);
        jsDimension.append("}").append(LS);
        jsDimension.append("if(self.innerWidth) {").append(LS);
        jsDimension.append("  width = Math.max(width, self.innerWidth);")
                .append(LS);
        jsDimension.append("  height = Math.max(height, self.innerHeight);")
                .append(LS);
        jsDimension.append("}").append(LS);
        jsDimension.append("if(document.body.scrollWidth) {").append(LS);
        jsDimension.append(
                "  width = Math.max(width, document.body.scrollWidth);")
                .append(LS);
        jsDimension.append(
                "  height = Math.max(height, document.body.scrollHeight);")
                .append(LS);
        jsDimension.append("}").append(LS);
        jsDimension.append("return width + ':' + height;");

        return jsDimension.toString();
    }

    public static boolean printUrlScreen2jpg(final String file, final String url,
                                             final int width, final int height) {
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String withResult = "var width = " + width + ";var height = "
                        + height + ";return width +':' + height;";
                if (width == 0 || height == 0)
                    withResult = getScreenWidthHeight();
                // SWT组件转Swing组件，不初始化父窗体将无法启动webBrowser
                JFrame frame = new JFrame("网页截图");
                // 加载指定页面，最大保存为640x480的截图
                frame.getContentPane().add(
                        new PrintScreen4DJNativeSwingUtils(file, url,
                                withResult), BorderLayout.CENTER);
                frame.setSize(640, 480);
                // 仅初始化，但不显示
                frame.invalidate();

                frame.setResizable(false);
                frame.setVisible(visible);
                frame.pack();

            }
        });
        NativeInterface.runEventPump();
        return true;
    }

    public static void main(String[] args) {
//        printUrlScreen2jpg("d:/zx.jpg", "http://www.cninfo.com.cn/cninfo-new/index", 0, 0);
//        printUrlScreen2jpg("d:/jqr.jpg", "http://172.19.80.224:8090/b2b/", 0, 0);
//        printUrlScreen2jpg("d:/github.jpg", "https://github.com/", 0, 0);
//        printUrlScreen2jpg("d:/cnblogs.jpg", "https://www.cnblogs.com/", 0, 0);
//        printUrlScreen2jpg("d:/jd.jpg", "https://www.jd.com/", 0, 0);
//        printUrlScreen2jpg("d:/csdn.jpg", "https://www.csdn.net/", 0, 0);

        printUrlScreen2jpg("d:/taobao.jpg", "https://www.taobao.com/", 0, 0);
//        printUrlScreen2jpg("d:/baidu.jpg", "https://www.baidu.com/", 0, 0);
    }
}
