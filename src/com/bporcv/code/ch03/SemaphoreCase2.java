package com.bporcv.code.ch03;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName SemaphoreCase2
 * @Description 使用信号量来进行资源的多副本的并发访问控制
 * @Author Administrator
 * @Date 2020/3/16 22:42
 * @Version 1.0
 */
public class SemaphoreCase2 {

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

        //存放打印机的装填，即空闲或正在打印
        private boolean freePrinters[];

        // 声明一个锁对象，用来保护freePrinters数组的访问
        private Lock lockPrinters;

        public PrintQueue() {
            // 三台打印机共同
            semaphore = new Semaphore(3);
            freePrinters = new boolean[3];
            for (int i = 0; i < 3; i++) {
                freePrinters[i] = true;
            }
            lockPrinters = new ReentrantLock();
        }

        public void printJob(Object document) {
            try {
                // 获取信号量的方法
                semaphore.acquire();
                // 获得分配工作的打印机的编号
                int assignedPrinter = getPrinter();
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: PrintQueue: Printing a Job in Printer %d during %d seconds.\n",
                        Thread.currentThread().getName(), assignedPrinter, duration);
                TimeUnit.SECONDS.sleep(duration);
                freePrinters[assignedPrinter] = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 打印完成后释放信号量
                semaphore.release();
            }
        }

        private int getPrinter() {
            int ret = -1;
            try {
                lockPrinters.lock();
                for (int i = 0; i < freePrinters.length; i++) {
                    if (freePrinters[i]) {
                        ret = i;
                        freePrinters[i] = false;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lockPrinters.unlock();
            }
            return ret;
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
