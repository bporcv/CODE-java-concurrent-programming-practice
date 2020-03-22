package com.bporcv.code.ch07;

import sun.nio.ch.ThreadPool;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName CustomPriorityExecutorCase
 * @Description 实现基于优先级的Executor类
 * @Author Administrator
 * @Date 2020/3/22 10:44
 * @Version 1.0
 */
public class CustomPriorityExecutorCase {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 1,
                TimeUnit.SECONDS,new PriorityBlockingQueue<>());
        for (int i = 0; i < 4; i++) {
            MyPriorityTask task = new MyPriorityTask(i, "Task " + i);
            executor.execute(task);
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 4; i < 8; i++) {
            MyPriorityTask task = new MyPriorityTask(i, "Task " + i);
            executor.execute(task);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1,TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: End of the program.\n");

    }


    public static class MyPriorityTask implements Runnable,Comparable<MyPriorityTask>{


        private int priority;
        private String name;

        public MyPriorityTask(int priority, String name) {
            this.priority = priority;
            this.name = name;
        }

        public int getPriority() {
            return priority;
        }


        @Override
        public void run() {
            System.out.printf("MyPriorityTask: %s priority: %d\n",name,priority);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int compareTo(MyPriorityTask o) {
            if (this.getPriority() < o.getPriority()){
                return 1;
            }
            if (this.getPriority() > o.getPriority()){
                return -1;
            }
            return 0;
        }
    }

}
