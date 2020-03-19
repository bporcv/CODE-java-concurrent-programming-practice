package com.bporcv.code.ch04;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @ClassName ThreadPoolAllTaskFinished
 * @Description 等待所有任务全部执行完成
 * @Author Administrator
 * @Date 2020/3/18 20:25
 * @Version 1.0
 */
public class ThreadPoolAllTaskFinished {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Task task = new Task("" + i);
            taskList.add(task);
        }
        List<Future<Result>> resultList = null;
        try {
            // 等待所有方法执行完成，一起返回
            resultList = executor.invokeAll(taskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        System.out.println("Main: Printing the results.");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Result> future = resultList.get(i);
            try {
                Result result = future.get();
                System.out.println(result.getName() + ": " + result.getValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


    }

    public static class Result {
        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class Task implements Callable<Result> {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public Result call() throws Exception {
            System.out.printf("%s: Starting.\n", this.name);
            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int value = 0;
            for (int i = 0; i < 5; i++) {
                value += (int) (Math.random() * 100);
            }
            Result result = new Result();
            result.setName(this.name);
            result.setValue(value);
            System.out.println(this.name + ":Ends");
            return result;
        }
    }

}
