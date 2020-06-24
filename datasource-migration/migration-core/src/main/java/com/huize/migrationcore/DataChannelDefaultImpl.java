package com.huize.migrationcore;

import com.huize.migrationcommon.trans.DataChannel;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
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
public class DataChannelDefaultImpl extends DataChannel {


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

}