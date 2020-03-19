package com.bporcv.code.ch04;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @ClassName ThreadPoolCycleTask
 * @Description 在执行器中周期的执行任务
 * @Author Administrator
 * @Date 2020/3/18 20:41
 * @Version 1.0
 */
public class ThreadPoolCycleTask {

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        System.out.printf("Main: Starting at : %s\n", new Date());
        Task task = new Task("Task");

        /**
         * 参数一：任务
         * 参数二：第一次执行后的延迟时间
         * 参数三：两个执行的时间周期：指任务在两次执行开始的时间间隔，
         *        如果有一个周期性的任务需要执行5秒钟，但是却让它每三秒钟执行一次，那么在任务的执行过程中将会有两个任务实例同时存在
         * 参数四：参数二和参数三的时间单位
         */
        ScheduledFuture<?> result = executor.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);


        for (int i = 0; i < 10; i++) {
            System.out.printf("Main: Delay: %d\n",result.getDelay(TimeUnit.MILLISECONDS));
            // 休眠线程500毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Finished at:  %s\n",new Date());
    }

    public static class Task implements Runnable {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public void run()  {
            System.out.printf("%s: Starting at : %s\n",name,new Date());
        }
    }

}

