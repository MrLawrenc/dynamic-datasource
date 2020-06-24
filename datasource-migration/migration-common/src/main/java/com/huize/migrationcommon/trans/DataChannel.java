package com.huize.migrationcommon.trans;

import com.huize.migrationcommon.entity.Row;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : MrLawrenc
 * date  2020/6/14 10:49
 * <p>
 * 数据缓冲区
 */
@Data
@Component
public abstract class DataChannel {


    private Lock monitor = new ReentrantLock();

    /**
     * 缓冲区已满
     */
    private boolean full;

    /**
     * 最大容量
     */
    private long maxSize;

    /**
     * 当前容量
     */
    private long currentSize;


    /**
     * 数据存储结构
     */
    private Map<String, List<Row>> buffer = new HashMap<>();

    public void offer(String targetDatasourceName, String targetTableName, Map<String, String> row) {
        monitor.lock();
        try {
            String[] values = row.values().toArray(new String[]{});
            long size = 0;
            for (String value : values) {
                size += value.getBytes().length;
            }

            if ((currentSize + size) >= maxSize) {
                //标记缓冲已满
                this.full = true;
                monitor.wait();
            }

            currentSize += size;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            monitor.unlock();
        }
    }

    //fix
    public void offer(String targetDatasourceName, String targetTableName, List<Row> rows, int rowSize) {
        monitor.lock();
        try {
            int size = rowSize * rows.size();
            if ((currentSize + size) >= maxSize) {
                //标记缓冲已满
                this.full = true;
                monitor.wait();
            }


            String key = targetDatasourceName + ":" + targetTableName;
            List<Row> rowList = buffer.get(key);
            if (rowList == null) {
                rowList = new ArrayList<>(rows.size());
            }
            rowList.addAll(rows);
            buffer.put(key, rowList);
            currentSize += size;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            monitor.unlock();
        }
    }

    public List<Row> take(String targetDatasourceName, String targetTableName) {
        monitor.lock();
        try {
            String key = targetDatasourceName + ":" + targetTableName;
            return buffer.get(key);
        } finally {
            monitor.unlock();
            monitor.notifyAll();
        }
    }
}