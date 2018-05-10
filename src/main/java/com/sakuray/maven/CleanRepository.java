package com.sakuray.maven;

import java.io.File;

public class CleanRepository {

    public static int emptyFolder = 0;
    public static int invalidFile = 0;
    public static boolean isDelete = true;

    public static void main(String[] args) {
        String path = "D:\\maven\\repository";
        searchFile(new File(path));
        System.out.println("emptyFolder:"+emptyFolder+"-------invalidFile:"+invalidFile);
    }

    private static void searchFile(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f : files) {
                searchFile(f);
            }
            if(file.list().length == 0) {
                System.out.println(file.getAbsolutePath());
                emptyFolder++;
                if(isDelete) {
                    file.delete();
                }
            }
        } else {
            if(file.getName().endsWith(".lastUpdated") ||
                    "_remote.repositories".equals(file.getName())) {
                System.out.println(file.getAbsolutePath());
                invalidFile++;
                if(isDelete) file.delete();
            }
        }
    }
}
