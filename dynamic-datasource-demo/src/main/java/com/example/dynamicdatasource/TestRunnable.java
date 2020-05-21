package com.example.dynamicdatasource;

import lombok.SneakyThrows;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * @author : MrLawrenc
 * @date : 2020/5/21 21:43
 * @description : TODO
 */
public class TestRunnable {


    public static void main(String[] args) throws Exception {
        testSubmit();
    }

    /**
     * 测试无返回值的任务
     */
    public static void testExecute() throws Exception {
        MyPool myPool = new MyPool();
        IntStream.range(0, 10).forEach(i -> myPool.execute(() -> {
            try {
                System.out.println("第" + i + "个任务开始执行");
                TimeUnit.MILLISECONDS.sleep(1100);
                System.out.println("第" + i + "个任务执行完毕");
            } catch (InterruptedException e) {
                System.out.println("第" + i + "个任务被中断");
            }
        }));

        List<Runnable> runnableList = myPool.shutdown();
        System.out.println("剩余任务：" + runnableList.size() + "个任务");

        FileOutputStream fileOut = new FileOutputStream("F:\\openSources\\dynamic-datasource\\dynamic-datasource-demo\\src\\main\\java\\com\\example\\dynamicdatasource\\employee.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(runnableList.get(0));
        out.close();
        fileOut.close();


        FileInputStream fileIn = new FileInputStream("F:\\openSources\\dynamic-datasource\\dynamic-datasource-demo\\src\\main\\java\\com\\example\\dynamicdatasource\\employee.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        MyRunnable runnable = (MyRunnable) in.readObject();
        in.close();
        fileIn.close();

        new MyPool().execute(runnable);

        TimeUnit.DAYS.sleep(1);

    }

    /**
     * 测试有返回值任务
     */
    public static void testSubmit() throws Exception {
        MyPool myPool = new MyPool();
        List<Future<String>> futures = IntStream.range(0, 10).mapToObj(i -> myPool.submit(() -> {
            try {
                System.out.println("第" + i + "个任务开始执行");
                TimeUnit.MILLISECONDS.sleep(1100);
                System.out.println("第" + i + "个任务执行完毕");
                return "第" + i + "个结果";
            } catch (InterruptedException e) {
                System.out.println("第" + i + "个任务被中断");
            }
            return "空";
        })).collect(toList());

        List<Runnable> runnableList = myPool.shutdown();
        System.out.println("剩余任务：" + runnableList.size() + "个任务");

        FileOutputStream fileOut = new FileOutputStream("F:\\openSources\\dynamic-datasource\\dynamic-datasource-demo\\src\\main\\java\\com\\example\\dynamicdatasource\\employee.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(runnableList.get(0));
        out.close();
        fileOut.close();


        FileInputStream fileIn = new FileInputStream("F:\\openSources\\dynamic-datasource\\dynamic-datasource-demo\\src\\main\\java\\com\\example\\dynamicdatasource\\employee.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        MyTask<?> runnable = (MyTask<?>) in.readObject();
        in.close();
        fileIn.close();

        System.out.println(new MyPool().submit(runnable.runnable).get());

    }

    static class MyPool {
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        public void execute(MyRunnable runnable) {
            pool.execute(runnable);
        }

        public <V> Future<V> submit(MyCallable<V> runnable) {
            if (runnable == null) {
                throw new NullPointerException();
            }
            MyTask<V> myTask = new MyTask<>(runnable);
            execute(myTask);
            return myTask;
        }


        public List<Runnable> shutdown() {
            return pool.shutdownNow();
        }


    }

    interface MyRunnable extends Serializable, Runnable {

    }

    interface MyCallable<V> extends Serializable, Callable<V> {

    }

    static class MyTask<V> implements MyRunnable, Future<V> {
        private final MyCallable<V> runnable;

        private V result = null;

        public MyTask(MyCallable<V> runnable) {
            this.runnable = runnable;
        }

        @SneakyThrows
        @Override
        public void run() {
            synchronized (this) {
                result = runnable.call();
                notify();
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            synchronized (this) {
                if (result == null) {
                    wait();
                }
                return result;
            }
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }
}