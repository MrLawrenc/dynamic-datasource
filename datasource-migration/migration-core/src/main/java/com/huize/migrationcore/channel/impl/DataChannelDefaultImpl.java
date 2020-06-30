package com.huize.migrationcore.channel.impl;

import com.huize.migrationcore.channel.DataChannel;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : MrLawrenc
 * date  2020/6/14 10:49
 * <p>
 * 数据缓冲区
 */
@Component
public class DataChannelDefaultImpl extends DataChannel {


    private final Lock monitor = new ReentrantLock();

    /**
     * 标记缓冲区是否已满
     */
    private final AtomicBoolean full = new AtomicBoolean(false);

    /**
     * 最大容量
     */
    private final long maxSize = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().totalMemory() >> 1;

    /**
     * 当前容量
     */
    private long currentSize;

    private static final Map<Long, Long> CAPACITY_MAP = new HashMap<>(1024);
    private static long IDX = 0;
    private static final List<Long> FREE_IDX = new ArrayList<>();

    /**
     * @param row 每一行数据
     */
    public long offer(Collection<String> row) {
        monitor.lock();
        try {
            long size = 0;
            for (String s : row) {
                size += s.getBytes().length;
            }

            if ((currentSize + size) >= maxSize) {
                //标记缓冲已满
                full.set(true);
                monitor.wait();
            }
            currentSize += size;
            long idx = IDX;
            if (IDX == Long.MAX_VALUE) {
                idx = FREE_IDX.remove(0);
            } else {
                IDX++;
            }
            CAPACITY_MAP.put(idx, size);
            return idx;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        } finally {
            monitor.unlock();
        }
    }

    @Override
    public boolean isFull() {
        return full.get();
    }

    @Override
    public boolean release(long idx) {
        monitor.lock();
        try {
            Long size = CAPACITY_MAP.remove(idx);
            if (Objects.nonNull(size)) {
                currentSize -= size;
                FREE_IDX.add(idx);
                full.set(false);
                return true;
            }
        } finally {
            monitor.unlock();
        }
        return false;
    }


}