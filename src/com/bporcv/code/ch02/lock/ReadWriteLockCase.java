package com.bporcv.code.ch02.lock;

import com.sun.jmx.snmp.tasks.ThreadService;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @ClassName ReadWriteLockCase
 * @Description 读写锁实现数据同步访问，
 * 使用读操作锁的时候允许多个线程同时读，使用写操作锁的时候只允许一个线程写
 * @Author Administrator
 * @Date 2020/3/16 20:28
 * @Version 1.0
 */
public class ReadWriteLockCase {


    public static void main(String[] args) {
        PricesInfo pricesInfo = new PricesInfo();
        Reader[] readers = new Reader[5];
        Thread[] threadsReader = new Thread[5];
        for (int i = 0; i < 5; i++) {
            readers[i] = new Reader(pricesInfo);
            threadsReader[i] = new Thread(readers[i]);
        }
        Writer writer = new Writer(pricesInfo);
        Thread threadWriter = new Thread(writer);


        for (int i = 0; i < 5; i++) {
            threadsReader[i].start();
        }
        threadWriter.start();

    }

    public static class PricesInfo {

        private double price1;

        private double price2;

        // 声明读写锁对象
        private ReadWriteLock lock;

        public PricesInfo() {
            this.price1 = 1.0;
            this.price2 = 2.0;
            this.lock = new ReentrantReadWriteLock();
        }

        public double getPrice1() {
            lock.readLock().lock();
            double value = price1;
            lock.readLock().unlock();
            return value;
        }

        public double getPrice2() {
            lock.readLock().lock();
            double value = price2;
            lock.readLock().unlock();
            return value;
        }

        public void setPrices(double price1, double price2) {
            lock.writeLock().lock();
            this.price1 = price1;
            this.price2 = price2;
            lock.writeLock().unlock();
        }
    }

    public static class Reader implements Runnable {

        private PricesInfo pricesInfo;

        public Reader(PricesInfo pricesInfo) {
            this.pricesInfo = pricesInfo;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.printf("%s: Price 1 : %f \n", Thread.currentThread().getName(), pricesInfo.getPrice1());
                System.out.printf("%s: Price 2 : %f \n", Thread.currentThread().getName(), pricesInfo.getPrice2());
            }

        }
    }

    public static class Writer implements Runnable {

        private PricesInfo pricesInfo;

        public Writer(PricesInfo pricesInfo) {
            this.pricesInfo = pricesInfo;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                System.out.printf("Writer: Attempt to modify the prices.\n");
                pricesInfo.setPrices(Math.random() * 10, Math.random() * 8);
                System.out.printf("Writer: Prices have been modified.\n");
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
