package com.bporcv.code.ch04;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadPoolDelayTask
 * @Description 在执行器中延时执行任务
 * @Author Administrator
 * @Date 2020/3/18 20:34
 * @Version 1.0
 */
public class ThreadPoolDelayTask {

    public static void main(String[] args) {

        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        System.out.printf("Main: Starting at : %s\n", new Date());
        for (int i = 0; i < 5; i++) {
            Task task = new Task("Task " + i);
            // 隔多长时间执行任务
            executor.schedule(task, i + 1, TimeUnit.SECONDS);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Ends at : %s\n", new Date());

    }

    public static class Task implements Callable<String> {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public String call() throws Exception {
            System.out.printf("%s: Starting at : %s\n", name, new Date());
            return "Hello,world";
        }
    }


}
