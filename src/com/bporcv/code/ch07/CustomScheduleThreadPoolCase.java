package com.bporcv.code.ch07;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @ClassName CustomScheduleThreadPoolCase
 * @Description 定制运行在定时线程池中的任务
 * * 延迟任务    DelayedTask
 * * 周期性任务  PeriodicTask
 * @Author Administrator
 * @Date 2020/3/22 12:30
 * @Version 1.0
 */
public class CustomScheduleThreadPoolCase {

    public static void main(String[] args) throws InterruptedException {
        MyScheduledThreadPoolExecutor executor = new MyScheduledThreadPoolExecutor(2);
        Task task = new Task();
        System.out.printf("Main: %s\n",new Date());
        // 延时任务：任务延迟一秒执行
        executor.schedule(task, 1, TimeUnit.SECONDS);

        // 主线程休眠3秒
        TimeUnit.SECONDS.sleep(3);

        task = new Task();
        System.out.printf("Main: %s\n",new Date());
        // 周期性任务： 延迟一秒后被执行，然后每3秒执行一次
        executor.scheduleAtFixedRate(task,1, 3, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(10);
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        System.out.printf("Main: End of the program");

    }

    public static class MyScheduledTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
        private RunnableScheduledFuture<V> task;
        private ScheduledThreadPoolExecutor executor;
        private long period;
        private long startDate;

        public MyScheduledTask(Runnable runnable, V result,
                               RunnableScheduledFuture<V> task, ScheduledThreadPoolExecutor executor) {
            super(runnable, result);
            this.task = task;
            this.executor = executor;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            // 是否是周期任务
            if (!isPeriodic()){
                return task.getDelay(unit);
            } else {
                if (startDate == 0){
                    return task.getDelay(unit);
                } else {
                    Date now = new Date();
                    long delay = startDate - now.getTime();
                    return unit.convert(delay, TimeUnit.MILLISECONDS);
                }
            }
        }

        @Override
        public int compareTo(Delayed o) {
            return task.compareTo(o);
        }

        @Override
        public boolean isPeriodic() {
            return task.isPeriodic();
        }

        @Override
        public void run() {
            // 如果是周期任务，则需要任务下一次执行的开始的开始时间，更新它的startDate属性，
            // 即用当前时间加上周期时间作为下一次执行的开始时间，然后，再增加到任务的ScheduledThreadPoolExecutor对象的队列中
            if (isPeriodic() && (!executor.isShutdown())){
                Date now = new Date();
                startDate = now.getTime() + period;
                executor.getQueue().add(this);
            }
            System.out.printf("Pre-MyScheduledTask: %s\n",new Date());
            System.out.printf("MyScheduledTask: Is Periodic: %s\n",isPeriodic());
            super.runAndReset();
            System.out.printf("Post-MyScheduledTask %s\n",new Date());
        }

        public void setPeriod(long period) {
            this.period = period;
        }


    }

    public static class MyScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
        public MyScheduledThreadPoolExecutor(int corePoolSize) {
            super(corePoolSize);
        }

        /**
         *
         * @param runnable 将要被执行的Runnable对象
         * @param task 用来执行这个Runnable对象
         * @param <V>
         * @return
         */
        @Override
        protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
            MyScheduledTask<V> myTask = new MyScheduledTask<>(runnable,null,task,this);
            return myTask;
        }

        // 周期性任务
        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            ScheduledFuture<?> task = super.scheduleAtFixedRate(command, initialDelay, period, unit);
            MyScheduledTask<?> myTask = (MyScheduledTask<?>) task;
            myTask.setPeriod(TimeUnit.MILLISECONDS.convert(period, unit));
            return myTask;
        }
    }

    public static class Task implements Runnable {
        @Override
        public void run() {
            System.out.printf("Task: Begin.\n");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Task: End.\n");
        }
    }
}
