package com.huize.migrationcore;

import com.huize.migrationcommon.entity.Job;
import lombok.SneakyThrows;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

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
        for (int i = 0; i < 1000000; i++) {
            Job job = new Job();
            job.setCron("" + i);
            job.setCondition(new String(new byte[1024]));
            sink.next(job);
        }
        sink.complete();
    };

    public static void main(String[] args) {


        //create 方法 支持同步和异步消息产生，并且可以在一次调用中产生多个元素，第二个参数指定回压方式
        Flux.create(fluxSink, FluxSink.OverflowStrategy.IGNORE)
                .limitRate(10)
                //后续可以调整回压策略
     /*           .onBackpressureBuffer(5, job -> {
                    System.out.println("&&&&&&&&&&&&&&&&&&扔掉&&&&&&&&&&&&&&&&&&:" + job);

                })*/
                //.parallel(1).runOn(Schedulers.single())
                /* .subscribe(r -> {
                     //todo writer
                     try {
                         TimeUnit.MILLISECONDS.sleep(1000);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                     System.out.println("结果:" + r.getCron());
                 });*/
                .subscribe(new Subscriber<Job>() {
                    Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        subscription.request(10);
                    }

                    @SneakyThrows
                    @Override
                    public void onNext(Job job) {
                        System.out.println("进入..........");

                        subscription.request(1000);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("完成。。。。。。。。。。。");
                    }

                });
    }
}