package com.bporcv.code.ch05;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ForkJoinException
 * @Description 在任务中抛出异常
 * 在ForkJoinPool对象和ForkJoinTask对象开发一个程序时，它们是会抛出异常的，如果不想要这种行为，就要采用其他形式
 * @Author Administrator
 * @Date 2020/3/19 23:15
 * @Version 1.0
 */
public class ForkJoinException {

    public static void main(String[] args) {
        int[] array = new int[100];
        Task task = new Task(array, 0, 100);
        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 该方法检查主任务或者它的子任务之一是否抛出了异常
        if (task.isCompletedAbnormally()){
            System.out.printf("Main: An exception has occurred\n");
            System.out.printf("Main: %s\n",task.getException());

        }
        System.out.printf("Main: Result: %d",task.join());

    }


    public static class Task extends RecursiveTask<Integer> {
        private int[] array;
        private int start;
        private int end;

        public Task(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            System.out.printf("Task: Start from %d to %d\n", start, end);
            if (end - start < 10) {
                if ((3 > start) && (3 < end)) {
                    throw new RuntimeException("This task throws an Exception: Task from " + start + " to " + end);
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                int mid = (end + start) / 2;
                Task task = new Task(array, start, mid);
                Task task1 = new Task(array, mid, end);
                invokeAll(task, task1);
            }
            System.out.printf("Task: End from %d to %d \n",start,end);
            return 0;
        }
    }
}
