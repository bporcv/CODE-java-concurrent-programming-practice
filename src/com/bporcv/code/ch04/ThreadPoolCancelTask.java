package com.bporcv.code.ch04;

import com.sun.xml.internal.fastinfoset.stax.factory.StAXOutputFactory;

import java.util.concurrent.*;

/**
 * @ClassName ThreadPoolCancelTask
 * @Description 在执行器中取消任务
 * @Author Administrator
 * @Date 2020/3/18 20:53
 * @Version 1.0
 */
public class ThreadPoolCancelTask {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Task task = new Task();
        System.out.printf("Main: Executing the Task\n");
        Future<String> result = executor.submit(task);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Canceling the Task\n");
        // ture: 如果任务在运行，也要取消
        // false: 如果任务在运行，不取消
        result.cancel(true);
        System.out.printf("Main: Cancelled: %s\n",result.isCancelled());
        System.out.printf("Main: Done: %s\n",result.isDone());
        executor.shutdown();
        System.out.printf("Main: The executor has finished\n");


    }


    public static class Task implements Callable<String> {

        @Override
        public String call() throws Exception {
            while (true) {
                System.out.printf("Task: Test\n");
                Thread.sleep(100);
            }
        }
    }
}

