package com.example.dynamicdatasource;

import lombok.SneakyThrows;
import lombok.ToString;
import org.nustaq.serialization.FSTConfiguration;

import java.io.*;
import java.util.ArrayList;
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

    static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public static void main(String[] args) throws Exception {
        //testExecute();
        //readTask4File();

        testFst();
    }


    public static void testFst() throws Exception {
        MyPool myPool = new MyPool();
        IntStream.range(0, 10).mapToObj(i -> new A(i, "sssas结果")).forEach(obj -> {
            myPool.execute(() -> {
                System.out.println("开始处理任务" + obj);
                try {
                    TimeUnit.SECONDS.sleep(1);
                    obj.name = "我是新的值";
                    System.out.println("任务结束" + obj);
                } catch (InterruptedException e) {
                    System.out.println("第" + obj.index + "个任务被中断");
                }

            });
        });

        List<Runnable> runnableList = myPool.shutdown();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("剩余任务：" + runnableList.size() + "个任务");


        byte[] barray = conf.asByteArray(runnableList.get(0));


        MyRunnable runnable = (MyRunnable) conf.asObject(barray);
        new MyPool().execute(runnable);

        TimeUnit.DAYS.sleep(1);

    }

    /**
     * 读取序列化文件，进行反序列化
     */
    public static void readTask4File() throws Exception {
        FileInputStream fileIn = new FileInputStream("F:\\openSources\\dynamic-datasource\\dynamic-datasource-demo\\src\\main\\java\\com\\example\\dynamicdatasource\\employee.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        MyRunnable runnable = (MyRunnable) in.readObject();
        in.close();
        fileIn.close();

        new MyPool().execute(runnable);
    }


    /**
     * 测试无返回值的任务
     */
    public static void testExecute() throws Exception {
        MyPool myPool = new MyPool();
        IntStream.range(0, 10).mapToObj(i -> new A(i, "sssas结果")).forEach(obj -> {
            myPool.execute(() -> {
                System.out.println("开始处理任务" + obj);
                try {
                    TimeUnit.SECONDS.sleep(1);
                    obj.name = "我是新的值";
                    System.out.println("任务结束" + obj);
                } catch (InterruptedException e) {
                    System.out.println("第" + obj.index + "个任务被中断");
                }

            });
        });

        List<Runnable> runnableList = myPool.shutdown();
        TimeUnit.SECONDS.sleep(1);
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

        /**
         * 等到正在执行的任务全部执行完毕再关闭线程池
         */
        public List<Runnable> shutdownUntilOver() throws Exception {
            pool.shutdown();
            BlockingQueue<Runnable> queue = pool.getQueue();
            List<Runnable> result = new ArrayList<>(queue.size());
            Runnable poll = queue.poll();
            while (poll != null) {
                result.add(poll);
                poll = queue.poll();
            }

            while (true) {
                if (pool.isTerminated()) {
                    System.out.println("任务全部执行完毕,保存队列任务数量:" + result.size());
                    break;
                }
                Thread.sleep(200);
            }

            return result;
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

    /**
     * 没有实现序列化接口会报错
     */
    @ToString
    static class A implements Serializable {
        public String name;
        public int index;

        public A(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }
}