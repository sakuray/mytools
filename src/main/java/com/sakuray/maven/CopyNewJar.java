package com.sakuray.maven;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CopyNewJar {

    public static void main(String[] args) {
        String origin = "D:\\maven\\repository";
        String dest = "D:\\maven\\new";
        String startTime = "2018-04-12 00:00:00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(startTime);
            copyNew(origin, dest, date.getTime(), new File(origin));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void copyNew(String prefix, String dest, long start, File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f : files) {
                copyNew(prefix, dest, start, f);
            }
        } else if(file.lastModified() > start){
            String relativePath = file.getAbsolutePath().replace(prefix,"");
            String destPath = dest+relativePath;
            String destDirectory = destPath.substring(0, destPath.lastIndexOf("\\"));
//            System.out.println(destDirectory);
//            System.out.println(destPath);
            File directory = new File(destDirectory);
            if(!directory.exists()) {
                directory.mkdirs();
            }
            File destFile = new File(destPath);
            if(!destFile.exists()) { // 不存在拷贝
                copyFile(file, destFile);
            } else {    // 存在校验文件是否损坏
                if(file.length() != destFile.length()) {
                    System.out.println(file.getAbsolutePath() + "文件损坏，重新拷贝");
                    destFile.delete();
                    copyFile(file, destFile);
                }
            }
        }
    }

    private static void copyFile(File origin, File dest) {
        int length = -1;
        byte[] buff = new byte[1024];
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(origin);
            fos = new FileOutputStream(dest);
            while((length = fis.read(buff)) != -1) {
                fos.write(buff, 0, length);
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(fis);
            closeStream(fos);
        }
    }

    private static void closeStream(Closeable stream) {
        if(stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
