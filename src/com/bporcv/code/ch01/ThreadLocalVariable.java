package com.bporcv.code.ch01;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadLocalVariable
 * @Description 线程中的局部变量
 * @Author Administrator
 * @Date 2020/3/13 22:40
 * @Version 1.0
 */
public class ThreadLocalVariable {
    /**
     * 不安全的线程变量，所有的线程共享相同的属性，如果在一个线程中改变了一个属性，那么多有的线程都会被这个改变影响
     * Starting Thread: 12 : Fri Mar 13 22:48:12 CST 2020
     * Starting Thread: 13 : Fri Mar 13 22:48:14 CST 2020
     * Starting Thread: 14 : Fri Mar 13 22:48:16 CST 2020
     * Thread Finished: 13 : Fri Mar 13 22:48:16 CST 2020
     * Thread Finished: 12 : Fri Mar 13 22:48:16 CST 2020
     */
    static class UnsafeTask implements Runnable {
        private Date startDate;

        @Override
        public void run() {
            startDate = new Date();
            System.out.printf("Starting Thread: %s : %s\n", Thread.currentThread().getId(), startDate);
            try {
                TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread Finished: %s : %s\n", Thread.currentThread().getId(), startDate);
        }

        public static void main(String[] args) {
            UnsafeTask task = new UnsafeTask();
            for (int i = 0; i < 10; i++) {
                Thread thread = new Thread(task);
                thread.start();
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class SafeTask implements Runnable {
        // 将会为每个线程存储各自的线程值，并提供给每个线程使用，使用get()方法获取值，使用set()方法设置值，使用initialValue()方法设置初始值
        private static ThreadLocal<Date> startDate = ThreadLocal.withInitial(() -> new Date());

        @Override
        public void run() {
            System.out.printf("Starting Thread: %s : %s\n", Thread.currentThread().getId(), startDate.get());
            try {
                TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread Finished: %s : %s\n", Thread.currentThread().getId(), startDate.get());
        }

        public static void main(String[] args) {
            SafeTask task = new SafeTask();
            for (int i = 0; i < 10; i++) {
                Thread thread = new Thread(task);
                thread.start();
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}




