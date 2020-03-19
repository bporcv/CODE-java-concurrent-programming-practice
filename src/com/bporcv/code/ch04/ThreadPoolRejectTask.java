package com.bporcv.code.ch04;

import javax.sound.midi.Soundbank;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadPoolRejectTask
 * @Description 处理在执行器中被拒绝的任务
 * RejectedExecutionHandler 接口
 * @Author Administrator
 * @Date 2020/3/18 22:16
 * @Version 1.0
 */
public class ThreadPoolRejectTask {

    public static void main(String[] args) {
        RejectedTaskController controller = new RejectedTaskController();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.setRejectedExecutionHandler(controller);
        System.out.printf("Main: Starting.\n");
        for (int i = 0; i < 3; i++) {
            Task task = new Task("Task " + i);
            executor.submit(task);
        }

        System.out.printf("Main: Shutting down the Executor.\n");
        executor.shutdown();
        System.out.printf("Main: Sending another Task.\n");
        Task rejectedTask = new Task("RejectedTask");
        executor.submit(rejectedTask);
        System.out.println("Main: End");
        System.out.printf("Main: End.\n");

    }

    public static class RejectedTaskController implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.printf("RejectedTaskController: The task %s has been rejected\n",r.toString());
            System.out.printf("RejectedTaskController: %s\n",executor.toString());
            System.out.printf("RejectedTaskController: isTerminating: %s\n",executor.isTerminating());
            System.out.printf("RejectedTaskController: Terminated: %s\n",executor.isTerminated());
        }
    }

    public static class Task implements Runnable {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println("Task " + name + ": Starting");
            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("Task %s: ReportGenerator: Generating a report during %d seconds\n", this.name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.printf("Task %s: Ending\n",this.name);
        }

        @Override
        public String toString() {
            return "Task{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
