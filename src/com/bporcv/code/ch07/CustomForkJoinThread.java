package com.bporcv.code.ch07;

import java.util.concurrent.*;

/**
 * @ClassName CustomForkJoinThread
 * @Description 通过实现ThreadFactory接口为Fork/Join框架生成定制线程
 * @Author Administrator
 * @Date 2020/3/22 15:07
 * @Version 1.0
 */
public class CustomForkJoinThread {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        MyWorkerThreadFactory factory = new MyWorkerThreadFactory();
        ForkJoinPool pool = new ForkJoinPool(4, factory, null, false);
        int[] array = new int[100_000];
        for (int i = 0; i < array.length; i++) {
            array[i] = 1;
        }
        MyRecursiveTask task = new MyRecursiveTask(array, 0, array.length);
        pool.execute(task);
        task.join();
        pool.shutdown();
        // 等待执行器结束
        pool.awaitTermination(1, TimeUnit.DAYS);
        System.out.printf("Main: Result: %d\n", task.get());
        System.out.printf("Main: End of the program.\n");


    }

    public static class MyWorkerThread extends ForkJoinWorkerThread {
        private static ThreadLocal<Integer> taskCounter = new ThreadLocal<>();

        public MyWorkerThread(ForkJoinPool pool) {
            super(pool);
        }

        @Override
        protected void onStart() {
            super.onStart();
            System.out.printf("MyWorkerThread %d: Initializing task counter.\n", getId());
            taskCounter.set(0);
        }

        @Override
        protected void onTermination(Throwable exception) {
            System.out.printf("MyWorkerThread %d: %d\n", getId(), taskCounter.get());
            super.onTermination(exception);
        }

        public void addTask() {
            int counter = taskCounter.get().intValue();
            counter++;
            taskCounter.set(counter);
        }

    }

    public static class MyWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new MyWorkerThread(pool);
        }

    }

    public static class MyRecursiveTask extends RecursiveTask<Integer> {
        private int[] array;
        private int start;
        private int end;

        public MyRecursiveTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            Integer ret;
            MyWorkerThread thread = (MyWorkerThread) Thread.currentThread();
            thread.addTask();
            if (end - start < 10) {
                Integer result = 0;
                for (int i = start; i < end; i++) {
                    result += array[i];
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return result;
            } else {
                int mid = (end + start) / 2;
                MyRecursiveTask task1 = new MyRecursiveTask(array, start, mid);
                MyRecursiveTask task2 = new MyRecursiveTask(array, mid, end);
                invokeAll(task1, task2);
                ret = addResults(task1, task2);
            }
            return ret;
        }

        private Integer addResults(MyRecursiveTask task1, MyRecursiveTask task2) {
            int value;
            try {
                value = task1.get().intValue() + task2.get().intValue();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                value = 0;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return value;
        }
    }


}
