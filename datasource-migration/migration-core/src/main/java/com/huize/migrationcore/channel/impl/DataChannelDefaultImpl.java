package com.huize.migrationcore.channel.impl;

import com.huize.migrationcore.channel.DataChannel;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
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
    private final long maxSize = Runtime.getRuntime().totalMemory() - (Runtime.getRuntime().totalMemory() >> 1);

    /**
     * 当前容量
     */
    private long currentSize;

    private static final Map<Long, Long> CAPACITY_MAP = new HashMap<>(1024);
    private static long IDX = 0;
    private static final List<Long> FREE_IDX = new ArrayList<>();

    public  void test(){
        MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
        //Heap
        System.out.println("Max:" + mxb.getHeapMemoryUsage().getMax() / 1024 / 1024 + "MB");    //Max:1776MB
        System.out.println("Init:" + mxb.getHeapMemoryUsage().getInit() / 1024 / 1024 + "MB");  //Init:126MB
        System.out.println("Committed:" + mxb.getHeapMemoryUsage().getCommitted() / 1024 / 1024 + "MB");   //Committed:121MB
        System.out.println("Used:" + mxb.getHeapMemoryUsage().getUsed() / 1024 / 1024 + "MB");  //Used:7MB
        System.out.println(mxb.getHeapMemoryUsage().toString());    //init = 132120576(129024K) used = 8076528(7887K) committed = 126877696(123904K) max = 1862270976(1818624K)

        //Non heap
        System.out.println("Max:" + mxb.getNonHeapMemoryUsage().getMax() / 1024 / 1024 + "MB");    //Max:0MB
        System.out.println("Init:" + mxb.getNonHeapMemoryUsage().getInit() / 1024 / 1024 + "MB");  //Init:2MB
        System.out.println("Committed:" + mxb.getNonHeapMemoryUsage().getCommitted() / 1024 / 1024 + "MB");   //Committed:8MB
        System.out.println("Used:" + mxb.getNonHeapMemoryUsage().getUsed() / 1024 / 1024 + "MB");  //Used:7MB
        System.out.println(mxb.getNonHeapMemoryUsage().toString());    //init = 2555904(2496K) used = 7802056(7619K) committed = 9109504(8896K) max = -1(-1K)

    }
    /**
     * @param row 每一行数据
     */
    public long offer(Collection<Object> row) {
        monitor.lock();
        try {
            long size = 0;
            for (Object s : row) {
                //RuntimeMXBean mxb = ManagementFactory.getRuntimeMXBean();

                //fix
                //size += s.getBytes().length;
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

    @Override
    public boolean release(long[] idx) {
        for (long l : idx) {
            release(l);
        }
        return true;
    }


}