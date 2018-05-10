package com.sakuray.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;

/**
 * 写入CSV格式数据:
 * 1.写入数据小的接口
 * 2.写入大量数据的接口和测试写法
 */
public class CSVWriter {

    public static void saveWithHead(File file, List<String[]> records, String[] headers) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers);
        CSVPrinter csvPrinter = null;
        try {
            csvPrinter = csvFormat.print(file, Charset.forName("UTF-8"));
            for(String[] record : records) {
                csvPrinter.printRecord(record);
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(csvPrinter != null) {
                try {
                    csvPrinter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveBigWithHead(File file, SynchronousQueue<List<String[]>> queue, String[] headers) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers);
        CSVPrinter csvPrinter = null;
        try {
            csvPrinter = csvFormat.print(file, Charset.forName("UTF-8"));
            List<String[]> batch = queue.take();
            int i = 0;
            while(batch != null && !batch.isEmpty()) {
                for(String[] record : batch) {
                    csvPrinter.printRecord(record);
                }
                batch = queue.take();
                System.out.println("完成" + (++i));
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(csvPrinter != null) {
                try {
                    csvPrinter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Map<String, Object> createData(int start, int all) {
        String[] headers = new String[] {"name", "sex", "age", "no", "address", "telephone", "interest", "school", "class", "addition"};
        Map<String, Object> result = new HashMap<>();
        result.put("headers", headers);
        List<String[]> datas = new ArrayList<>();
        for(int i = start; i < all; i++) {
            String[] data = new String[]{"张三"+i, i % 2 == 0 ? "男" : "女", (i % 100)+"", i+"",
                "宇宙中的银河系中的太阳系中的地球中的中国中的某个地方", "13011011911",
                "不知道喜欢什么，即没有兴趣", "不知道什么的小学", "几年级都不知道，班级怎么可能知道",
                "没有什么附加信息，只是凑点字数"};
            datas.add(data);
        }
        result.put("datas", datas);
        return result;
    }

    public static void main(String[] args) {
        testSave(0, 10);
//        testSaveBig(0, 2000000);
    }

    private static void testSave(int start, int end) {
        File file = new File("D:\\test\\small.csv");
        Map<String, Object> result = createData(start, end);
        saveWithHead(file, (List<String[]>)result.get("datas"), (String[])result.get("headers"));
    }

    private static void testSaveBig(int start, int end) {
        final File file = new File("D:\\test\\test.csv");
        final SynchronousQueue<List<String[]>> queue = new SynchronousQueue<>();
        int batch = 10000; boolean begin = false;
        for(int i = start; i < end; i+=batch) {
            System.out.println("current:" + i);
            Map<String, Object> map = createData(i, i+batch);
            List<String[]> result = (List<String[]>) map.get("datas");
            final String[] headers = (String[])map.get("headers");
            if(!begin) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveBigWithHead(file, queue, headers);
                    }
                });
                thread.start();
                begin = true;
            }
            try {
                queue.put(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            queue.put(new ArrayList<String[]>());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("over!!!");
    }
}
