package com.huize.migrationcore.channel.impl;

import com.huize.migrationcore.DataDispatcher;
import com.huize.migrationcore.channel.DataChannel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;
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
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private DataDispatcher dataDispatcher;

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
     * @param row 每一行数据
     */
    public void offer(Collection<String> row) {
        monitor.lock();
        try {
            long size = 0;
            for (String s : row) {
                size += s.getBytes().length;
            }

            if ((currentSize + size) >= maxSize) {
                //标记缓冲已满
                this.full = true;
                monitor.wait();
            }

            currentSize += size;
            //推送数据
            dataDispatcher.dispatcher();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            monitor.unlock();
        }
    }

}