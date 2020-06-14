package com.huize.migrationcore;

import com.huize.migrationcommon.entity.Job;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author : MrLawrenc
 * date  2020/6/13 9:30
 */
public class Reactor {


    static AtomicReference<FluxSink<Job>> task = new AtomicReference<>();

    static Consumer<? super FluxSink<Job>> fluxSink = sink -> {
        task.set(sink);
        for (int i = 0; i < 200; i++) {
            Job job = new Job();
            job.setCron("" + i);
            job.setCondition(new String(new byte[1024]));
            sink.next(job);
        }

    };

    public static void main(String[] args) {
        task.get().complete();


        //create 方法 支持同步和异步消息产生，并且可以在一次调用中产生多个元素
        Flux.create(fluxSink, FluxSink.OverflowStrategy.BUFFER)
                .onBackpressureBuffer(1, job -> {
                    System.out.println("扔掉:" + job);
                })
                .parallel(31)
                .subscribe(r -> {
                    //todo writer
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("结果:" + r.getCron());
                });
    }
}