package com.sakuray.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

/**
 * 读取CSV文件：
 * 1.读取小文件
 * 2.读取大文件的方法
 */
public class CSVReader {

    public static List<Map<String, String>> readSmallFile(File file) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        CSVParser csvParser = null;
        try {
            csvParser = csvFormat.parse(new FileReader(file));
            List<Map<String, String>> result = new ArrayList<>();
            for(CSVRecord record : csvParser) {
                result.add(record.toMap());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(csvParser != null) {
                try {
                    csvParser.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static SynchronousQueue<List<Map<String, String>>> readBigFile(final File file, int batch, int start) {
        final SynchronousQueue<List<Map<String, String>>> queue = new SynchronousQueue<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
                int i = 0, finish = i;
                try {
                    CSVParser csvParser = csvFormat.parse(new FileReader(file));
                    List<Map<String, String>> batchResult = new ArrayList<>(batch);
                    for(CSVRecord csvRecord : csvParser) {
                        if(i < start) {i++; continue;}
                        batchResult.add(csvRecord.toMap());
                        i++;
                        if(batchResult.size() == batch) {
                            queue.put(batchResult);
                            batchResult = new ArrayList<>(batch);
                            finish = i;
                            System.out.println(finish);
                        }
                    }
                    if(!batchResult.isEmpty()) {
                        queue.put(batchResult);
                        queue.put(new ArrayList<>());
                    } else {
                        queue.put(batchResult);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("读取完毕数据：" + i + ", 实际处理：" + finish);
                }
            }
        }).start();
        return queue;
    }

    public static void main(String[] args) throws Exception {
//        testSmall();
        testBig();
    }

    private static void testSmall() {
        File file = new File("D:\\test\\small.csv");
        List<Map<String, String>> result = readSmallFile(file);
        for(Map m : result) {
            System.out.println(m);
        }
    }

    private static void testBig() throws InterruptedException {
        long start = System.currentTimeMillis();
        File file = new File("D:\\test\\test.csv");
        SynchronousQueue<List<Map<String, String>>> queue = readBigFile(file, 10000, 0);
        List<Map<String, String>> result = null;
        while(!(result = queue.take()).isEmpty()) {
            // 处理逻辑
        }
        System.out.println("over!!! 耗时：" + (System.currentTimeMillis() - start));
    }
}
