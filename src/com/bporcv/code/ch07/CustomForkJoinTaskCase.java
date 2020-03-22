package com.bporcv.code.ch07;

import com.sun.corba.se.internal.Interceptors.PIORB;
import sun.misc.ThreadGroupUtils;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName CustomForkJoinTaskCase
 * @Description 定制运行在Fork/Join框架中的任务
 * @Author Administrator
 * @Date 2020/3/22 16:13
 * @Version 1.0
 */
public class CustomForkJoinTaskCase {

    public static void main(String[] args) {
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = 1;
        }
        ForkJoinPool pool = new ForkJoinPool();
        Task task = new Task("Task", array, 0, array.length);
        pool.invoke(task);
        pool.shutdown();
        System.out.printf("Main: End of the program.\n");
    }


    public abstract static class MyWorkerTask extends ForkJoinTask<Void> {

        private String name;

        public MyWorkerTask(String name) {
            this.name = name;
        }

        // 抽象方法：当任务不返会任何结果时，这个方法必须返回null值
        @Override
        public Void getRawResult() {
            return null;
        }

        // 抽象方法：用于当任务不返会任何结果时，设置方法体为空
        @Override
        protected void setRawResult(Void value) {

        }

        // 主方法，将任务的逻辑委托到compute()方法
        @Override
        protected boolean exec() {
            Date startDate = new Date();
            compute();
            Date finishDate = new Date();
            long diff = finishDate.getTime() - startDate.getTime();
            System.out.printf("MyWorkerThread: %s: %d Milliseconds to complete.\n", name, diff);
            return true;
        }

        protected abstract void compute();

        public String getName() {
            return name;
        }
    }

    public static class Task extends MyWorkerTask {

        private int[] array;

        private int start;
        private int end;

        public Task(String name, int[] array, int start, int end) {
            super(name);
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start > 100) {
                int mid = (end + start) / 2;
                Task task1 = new Task(this.getName(), array, start, mid);
                Task task2 = new Task(this.getName(), array, mid,end);
                invokeAll(task1, task2);
            } else {
                for (int i = start; i < end; i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
