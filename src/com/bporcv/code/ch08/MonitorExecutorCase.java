package com.bporcv.code.ch08;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MonitorExecutorCase
 * @Description 监控执行器框架
 * @Author Administrator
 * @Date 2020/3/22 23:04
 * @Version 1.0
 */
public class MonitorExecutorCase {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Task task = new Task(random.nextInt(10000));
            executor.submit(task);
        }

        for (int i = 0; i < 5; i++) {
            showLog(executor);
            TimeUnit.SECONDS.sleep(1);
        }
        executor.shutdown();
        for (int i = 0; i < 5; i++) {
            showLog(executor);
            TimeUnit.SECONDS.sleep(1);
        }

        executor.awaitTermination(1, TimeUnit.DAYS);
        System.out.printf("Main: End of program.\n");
    }

    private static void showLog(ThreadPoolExecutor executor) {
        System.out.printf("************************************\n");
        System.out.printf("Main: Executor Log\n");
        System.out.printf("Main: Executor: Core Pool Size: %d\n",executor.getCorePoolSize());
        System.out.printf("Main: Executor: Pool Size: %d\n",executor.getPoolSize());
        System.out.printf("Main: Executor: Active Count:: %d\n",executor.getActiveCount());
        System.out.printf("Main: Executor: Complete Task Count: %d\n",executor.getCompletedTaskCount());
        System.out.printf("Main: Executor: ShutDown: %s\n",executor.isShutdown());
        System.out.printf("Main: Executor: Terminating: %s\n",executor.isTerminating());
        System.out.printf("Main: Executor: Terminated: %s\n",executor.isTerminated());
        System.out.printf("************************************\n");
    }

    public static class Task implements Runnable {
        private long milliseconds;

        public Task(long milliseconds) {
            this.milliseconds = milliseconds;
        }

        @Override
        public void run() {
            System.out.printf("%s: Begin\n", Thread.currentThread().getName());
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s: End\n", Thread.currentThread().getName());
        }
    }

}
