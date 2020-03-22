package com.bporcv.code.ch08;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName MonitorLockInterface
 * @Description 监控Lock接口
 * @Author Administrator
 * @Date 2020/3/22 22:04
 * @Version 1.0
 */
public class MonitorLockInterface {

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[5];
        MyLock lock = new MyLock();
        for (int i = 0; i < threads.length; i++) {
            Task task = new Task(lock);
            threads[i] = new Thread(task);
            threads[i].start();
        }


        for (int i = 0; i < 15; i++) {
            System.out.printf("Main: Logging the lock.\n");
            System.out.printf("***************************\n");
            System.out.printf("Lock: Owner: %s\n", lock.getOwnerName());
            System.out.printf("Lock: Queued Threads: %s\n", lock.hasQueuedThreads());
            if (lock.hasQueuedThreads()) {
                System.out.printf("Lock: Queue Length: %d\n", lock.getQueueLength());
                System.out.printf("Lock: Queued Threads: ");
                Collection<Thread> waitingLockThreads = lock.getThreads();
                for (Thread thread : waitingLockThreads) {
                    System.out.printf("%s ", thread.getName());
                }
                System.out.printf("\n");
            }
            // 输出锁的公平模式和状态
            System.out.printf("Lock: Fairness: %s\n", lock.isFair());
            System.out.printf("Lock: Locked: %s\n", lock.isLocked());
            System.out.printf("****************************\n");
            TimeUnit.SECONDS.sleep(1);
        }
    }


    public static class MyLock extends ReentrantLock {
        // 获取当前获得了锁（如果有）的线程名字
        public String getOwnerName() {
            if (this.getOwner() == null) {
                return "None";
            }
            return this.getOwner().getName();
        }

        // 返回正在等待获取此锁的线程列表
        public Collection<Thread> getThreads() {
            return this.getQueuedThreads();
        }

    }

    public static class Task implements Runnable {

        private Lock lock;

        public Task(Lock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                lock.lock();
                System.out.printf("%s: Get the lock.\n", Thread.currentThread().getName());
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }


}
