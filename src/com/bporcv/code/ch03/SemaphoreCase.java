package com.bporcv.code.ch03;

import java.util.concurrent.Semaphore;

/**
 * @ClassName Semaphore
 * @Description 信号量的演示
 * 二进制信号量
 * @Author Administrator
 * @Date 2020/3/16 22:08
 * @Version 1.0
 */
public class SemaphoreCase {

    public static void main(String[] args) {
        PrintQueue queue = new PrintQueue();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Job(queue), "Thread" + i);
        }
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }

    }

    public static class PrintQueue {
        private final Semaphore semaphore;

        public PrintQueue() {
            this.semaphore = new Semaphore(1);
        }

        public void printJob(Object document) {
            try {
                // 获取信号量的方法
                semaphore.acquire();
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: PrintQueue: Printing a Job during %d seconds.\n",
                        Thread.currentThread().getName(), duration);
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 打印完成后释放信号量
                semaphore.release();
            }
        }
    }

    public static class Job implements Runnable {

        private PrintQueue printQueue;

        public Job(PrintQueue printQueue) {
            this.printQueue = printQueue;
        }

        @Override
        public void run() {
            System.out.printf("%s: Going to print a job\n", Thread.currentThread().getName());
            printQueue.printJob(new Object());
            System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());
        }
    }
}
