package com.huize.migrationcore.schedule;

import io.netty.util.HashedWheelTimer;

import java.util.concurrent.TimeUnit;

/**
 * @author : MrLawrenc
 * date  2020/6/12 23:44
 * <p>
 * 核心执行任务线程池
 */
public class CoreWorkEventLoopGroup {

    public void init() {
        HashedWheelTimer wheelTimer = new HashedWheelTimer(1, TimeUnit.SECONDS, 60 * 60 * 12);
        wheelTimer.newTimeout(o -> {
        }, 1000, TimeUnit.MINUTES);
    }
}