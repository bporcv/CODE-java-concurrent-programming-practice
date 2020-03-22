package com.bporcv.code.ch08;

import java.sql.Time;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MonitorForkJoinCase
 * @Description 监控Fork/Join任务
 * @Author Administrator
 * @Date 2020/3/22 23:11
 * @Version 1.0
 */
public class MonitorForkJoinCase {

    public static void main(String[] args) throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();
        int[] array = new int[10_000];
        Task task  = new Task(array, 0, array.length);
        pool.execute(task);
        while (!task.isDone()){
            showLog(pool);
            TimeUnit.SECONDS.sleep(1);
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
        showLog(pool);
        System.out.printf("Main: End of the program.\n");
    }

    private static void showLog(ForkJoinPool pool) {
        System.out.printf("************************************\n");
        System.out.printf("Main: Fork/Join Pool log\n");
        System.out.printf("Main: Fork/Join Pool: Parallelism: %d\n",pool.getParallelism());
        System.out.printf("Main: Fork/Join Pool: Pool Size: %d\n",pool.getPoolSize());
        System.out.printf("Main: Fork/Join Pool: Active Thread Count: %d\n",pool.getActiveThreadCount());
        System.out.printf("Main: Fork/Join Pool: Running Thread Count: %d\n",pool.getRunningThreadCount());
        System.out.printf("Main: Fork/Join Pool: Queued Submission: %d\n",pool.getQueuedSubmissionCount());
        System.out.printf("Main: Fork/Join Pool: Queued Tasks: %d\n",pool.getQueuedTaskCount());
        System.out.printf("Main: Fork/Join Pool: Queued Submissions: %s\n",pool.hasQueuedSubmissions());
        System.out.printf("Main: Fork/Join Pool: Queued Steal Count: %d\n",pool.getStealCount());
        System.out.printf("Main: Fork/Join Pool: Terminated: %s\n",pool.isTerminated());
        System.out.printf("************************************\n");
    }

    public static class Task extends RecursiveAction {
        private int[] array;
        private int start;
        private int end;

        public Task(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start > 100) {
                int mid = (end + start) / 2;
                Task task = new Task(array, start, mid);
                Task task1 = new Task(array, mid, end);
//                invokeAll(task, task1);
                task.fork();
                task1.fork();
                task.join();
                task1.join();
            } else {
                for (int i = start; i < end; i++) {
                    array[i]++;
                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

}
