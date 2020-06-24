package com.huize.migrationcore.listener;

import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.entity.Row;
import com.huize.migrationcore.event.DealDoneEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author hz20035009-逍遥
 * date   2020/6/19 14:22
 */
@Component
@Slf4j
public class ChannelListener {
    private FluxSink<Job> fluxSink;

    /**
     * 初始化标记
     */
    private boolean init;

    /**
     * 当前缓存的数据大致占用多少字节
     */
    private long currentBufferSize = 0;
    /**
     * 当前缓存最大字节容量
     */
    private final long max = Long.MAX_VALUE;
    /**
     * 每一行数据的“索引”作为key
     * 占用内存的大小作为value
     */
    private final Map<String, Long> indexBufferSizeMap = new HashMap<>();

    private final Lock monitor = new ReentrantLock();

    private final Condition buffer = monitor.newCondition();

    /**
     * 初始化数据流通道
     */
    public synchronized void initChannel() {
        AtomicReference<FluxSink<Job>> tempFluxSink = new AtomicReference<>();
        Consumer<? super FluxSink<Job>> sinkConsumer = tempFluxSink::set;
        fluxSink = tempFluxSink.get();

        Disposable disposable = Flux.create(sinkConsumer, FluxSink.OverflowStrategy.BUFFER)
                .limitRate(10000)
                //后续可以调整回压策略
                /*           .onBackpressureBuffer(5, job -> {
                               System.out.println("&&&&&&&&&&&&&&&&&&扔掉&&&&&&&&&&&&&&&&&&:" + job);

                           })*/
                .parallel(10).runOn(Schedulers.parallel())
                .subscribe(System.out::println);

        init = true;
    }


    /**
     * 添加任务，并控制内存，防止oom
     */
    public void offer(@NotNull Row row) {
        if (StringUtils.isEmpty(row.getIndex())) {
            String uuid = UUID.randomUUID().toString();
            log.warn("current row not have idx，will create uuid as idx({})", uuid);
            row.setIndex(uuid);
        }

        long tempSize = 0;
        for (String data : row.getRowData()) {
            tempSize += data.getBytes().length;
        }
        monitor.lock();
        try {
            if (currentBufferSize >= max) {
                buffer.await();
            }
            indexBufferSizeMap.put(row.getIndex(), tempSize);
            currentBufferSize += tempSize;
        } catch (Exception ignored) {
        } finally {
            monitor.unlock();
        }

        fluxSink.next(null);

        //永不停止
        //fluxSink.complete();
    }

    /**
     * 数据被消费监听，当writer处理完毕时会被通知
     * <p>
     * fix 空闲时清除
     */
    @EventListener
    public void decrease(DealDoneEvent dealDoneEvent) {
        monitor.lock();
        try {
            dealDoneEvent.getIdxList().forEach(idx -> {
                Long size = indexBufferSizeMap.get(idx);
                if (Objects.nonNull(size)) {
                    currentBufferSize -= size;
                }

            });
            if (currentBufferSize < max) {
                buffer.signalAll();
            }
        } finally {
            monitor.unlock();
        }
    }


}