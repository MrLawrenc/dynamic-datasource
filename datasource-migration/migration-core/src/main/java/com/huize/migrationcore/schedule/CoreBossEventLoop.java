package com.huize.migrationcore.schedule;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.huize.migrationcore.config.HashedWheelTimerConfig;
import io.netty.util.HashedWheelTimer;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : MrLawrenc
 * date  2020/6/12 23:44
 * <p>
 * 核心调度程序
 */
@Component
@Data
public class CoreBossEventLoop {

    @Autowired
    private HashedWheelTimerConfig timerConfig;


    private HashedWheelTimer wheelTimer;
    private ThreadPoolExecutor boss;


    /**
     * 60*60*12
     * ticksPerWheel 当前时间轮的格数 如果传入的不是2的N次方，则会调整为大于等于该参数的第一个2的N次方，好处是可以优化hash值的计算
     * tickDuration和unit 每格的时间间隔  unit默认s
     */
    @PostConstruct
    public void init() {
        this.wheelTimer = new HashedWheelTimer(
                new ThreadFactoryBuilder().setNameFormat(" hashed-wheel-timer-%d").build(),
                timerConfig.getTickDuration(),
                TimeUnit.SECONDS,
                timerConfig.getTicksPerWheel());


        this.boss = new ThreadPoolExecutor(
                10,
                15,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadFactoryBuilder().setNameFormat("guava-%d").build(),
                new ThreadPoolExecutor.DiscardPolicy());
    }




    /**
     * 根据cron表达式和上次任务执行时间，计算出下次任务执行的延时时间
     *
     * @param cron     cron
     * @param lastDate 上次任务执行时间
     * @return 延时时间 单位秒
     */
    public long parseCron4Delay(String cron, Date lastDate) {
        CronSequenceGenerator generator = new CronSequenceGenerator(cron);
        Date nextDate = generator.next(lastDate);
        return nextDate.getTime() - lastDate.getTime() / 1000;
    }

}