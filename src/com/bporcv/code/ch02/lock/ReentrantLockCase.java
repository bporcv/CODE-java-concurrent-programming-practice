package com.bporcv.code.ch02.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName PrintQueue
 * @Description 打印队列
 * @Author Administrator
 * @Date 2020/3/16 20:17
 * @Version 1.0
 */
public class ReentrantLockCase {

    // 可重入锁
    private final Lock queueLock = new ReentrantLock();

    public void printJob(Object document) {
        // 获取锁
        queueLock.lock();
        try {
            long duration = (long) (Math.random() * 10_000);
            System.out.println(Thread.currentThread().getName()
                    + ":PrintQueue: Printing a Job during" + (duration / 1000) + " seconds");
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放锁的控制
            queueLock.unlock();
        }
    }

    public static class Job implements Runnable {

        private ReentrantLockCase printQueue;

        public Job(ReentrantLockCase printQueue) {
            this.printQueue = printQueue;
        }

        @Override
        public void run() {
            System.out.printf("%s:Going to print a document\n", Thread.currentThread().getName());
            printQueue.printJob(new Object());
            System.out.printf("%s:The document has been printed\n", Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        ReentrantLockCase printQueue = new ReentrantLockCase();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Job(printQueue), "Thread" + i);
        }
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }

    }

}
