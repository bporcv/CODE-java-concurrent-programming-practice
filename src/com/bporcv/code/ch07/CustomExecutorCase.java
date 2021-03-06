package com.bporcv.code.ch07;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @ClassName CustomExecutorCase
 * @Description 定制化ThreadPoolExecutor
 * @Author Administrator
 * @Date 2020/3/22 10:29
 * @Version 1.0
 */
public class CustomExecutorCase {

    public static void main(String[] args) {
        MyExecutor myExecutor = new MyExecutor(2, 4,
                1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        List<Future<String>> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SleepTwoSecondsTask task = new SleepTwoSecondsTask();
            Future<String> result = myExecutor.submit(task);
            results.add(result);
        }

        for (int i = 0; i < 5; i++) {
            try {
                String result = results.get(i).get();
                System.out.printf("Main: Result for Task %d: %s\n", i, result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        myExecutor.shutdown();
        for (int i = 5; i < 10; i++) {
            try {
                String result = results.get(i).get();
                System.out.printf("Main: Result for Task %d: %s\n", i, result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        try {
            myExecutor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: End of the program.\n");
    }

    public static class MyExecutor extends ThreadPoolExecutor {
        private ConcurrentHashMap<String, Date> startTimes;

        public MyExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                          BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
            this.startTimes = new ConcurrentHashMap<>();
        }

        @Override
        public void shutdown() {
            System.out.printf("MyExecutor: Going to shutdown.\n");
            System.out.printf("MyExecutor: Executed tasks: %d\n", getCompletedTaskCount());
            System.out.printf("MyExecutor: Running tasks: %d\n", getActiveCount());
            System.out.printf("MyExecutor: Pending tasks: %d\n", getQueue().size());
            super.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            System.out.printf("MyExecutor: Going to immediately shutdown.\n");
            System.out.printf("MyExecutor: Executed tasks: %d\n", getCompletedTaskCount());
            System.out.printf("MyExecutor: Running tasks: %d\n", getActiveCount());
            System.out.printf("MyExecutor: Pending tasks: %d\n", getQueue().size());
            return super.shutdownNow();
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            System.out.printf("MyExecutor: A task is beginning: %s : %s\n", t.getName(), r.hashCode());
            startTimes.put(String.valueOf(r.hashCode()), new Date());
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            Future<?> result = (Future<?>) r;
            try {
                System.out.printf("***********************************************\n");
                System.out.printf("MyExecutor: A task is finishing.\n");
                System.out.printf("MyExecutor: Result: %s\n", result.get());
                Date startDate = startTimes.remove(String.valueOf(r.hashCode()));
                Date finishDate = new Date();
                long diff = finishDate.getTime() - startDate.getTime();
                System.out.printf("MyExecutor: Duration: %d\n", diff);
                System.out.printf("***********************************************\n");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SleepTwoSecondsTask implements Callable<String> {

        @Override
        public String call() throws Exception {
            TimeUnit.SECONDS.sleep(2);
            return new Date().toString();
        }
    }

}
