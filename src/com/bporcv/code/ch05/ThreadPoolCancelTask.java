package com.bporcv.code.ch05;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadPoolCancelTask
 * @Description 取消任务
 * 在任务开始执行前可以取消它
 * * ForkJoinPool类不提供任何方法来取消线程池中正在运行或者等待运行的所有任务
 * * 取消任务时，不能取消已经被执行的任务
 * @Author Administrator
 * @Date 2020/3/19 23:25
 * @Version 1.0
 */
public class ThreadPoolCancelTask {

    public static void main(String[] args) {
        ArrayGenerator generator = new ArrayGenerator();
        int[] array = generator.generateArray(1000);
        TaskManager taskManager = new TaskManager();
        ForkJoinPool pool = new ForkJoinPool();
        SearchNumberTask task = new SearchNumberTask(array, 0, 1000, 5, taskManager);
        pool.execute(task);
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: The program has finished.\n");
    }


    public static class ArrayGenerator {
        public int[] generateArray(int size) {
            int[] array = new int[size];
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt(10);
            }
            return array;
        }
    }


    public static class TaskManager {
        private List<ForkJoinTask<Integer>> tasks;

        public TaskManager() {
            tasks = new ArrayList<>();
        }

        public void addTask(ForkJoinTask<Integer> task) {
            tasks.add(task);
        }

        public void cancelTasks(ForkJoinTask<Integer> cancelTask) {
            for (ForkJoinTask<Integer> task : tasks) {
                if (task != cancelTask) {
                    task.cancel(true);
                    ((SearchNumberTask) task).writeCancelMessage();
                }
            }
        }
    }


    public static class SearchNumberTask extends RecursiveTask<Integer> {
        private int[] numbers;
        private int start;
        private int end;
        private int number;
        private TaskManager manager;

        private final static int NOT_FOUND = -1;

        public SearchNumberTask(int[] numbers, int start, int end, int number, TaskManager manager) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
            this.number = number;
            this.manager = manager;
        }

        @Override
        protected Integer compute() {
            System.out.printf("Task: " + start + ":" + end + "\n");
            int ret;
            if (end - start > 10) {
                ret = launchTasks();
            } else {
                ret = lookForNumber();
            }
            return ret;
        }

        private int launchTasks() {
            int mid = (start + end) / 2;
            SearchNumberTask task1 = new SearchNumberTask(numbers, start, mid, number, manager);
            SearchNumberTask task2 = new SearchNumberTask(numbers, mid, end, number, manager);
            manager.addTask(task1);
            manager.addTask(task2);
            task1.fork();
            task2.fork();
            int returnValue;
            returnValue = task1.join();
            if (returnValue != -1) {
                return returnValue;
            }
            returnValue = task2.join();
            return returnValue;
        }

        private int lookForNumber() {
            for (int i = start; i < end; i++) {
                if (numbers[i] == number) {
                    System.out.printf("Task: Number %d found in position %d\n", number, i);
                    manager.cancelTasks(this);
                    return i;
                }

            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return NOT_FOUND;
        }

        public void writeCancelMessage() {
            System.out.printf("Task: Cancelled task from %d to %d\n", start, end);
        }
    }


}
