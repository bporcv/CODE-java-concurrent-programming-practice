package com.bporcv.code.ch02;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName ThreadProducerAndConsumer
 * @Description 使用synchronized关键字,wait(),notify(),notifyAll()方法模拟生产者和消费者
 * @Author Administrator
 * @Date 2020/3/16 19:54
 * @Version 1.0
 */
public class ThreadProducerAndConsumer {

    public static void main(String[] args) {
        EventStorage storage = new EventStorage();

        Producer producer = new Producer(storage);
        Thread producerThread = new Thread(producer);

        Consumer consumer = new Consumer(storage);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

    }


    public static class EventStorage{
        private int maxSize;
        private List<Date> storage;

        public EventStorage() {
            this.maxSize = 10;
            this.storage = new LinkedList<>();
        }

        public synchronized void set(){
            // 1.当缓冲区的大小等于最大大小时
            while (storage.size() == maxSize){
                try {
                    // 2，释放当前线程的锁，进入休眠，等待空余空间出现
                    wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            // 3.存在空余空间，加入到缓冲区
            storage.add(new Date());
            System.out.printf("Set: %d\n",storage.size());
            // 4.唤醒所有因为wait休眠的线程
            notifyAll();
        }

        public synchronized void get(){
            while (storage.size() == 0){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.printf("Get: %d:%s \n",storage.size(),((LinkedList)storage).poll());
            notifyAll();
        }
    }


    /**
     * 生产者
     */
    public static class Producer implements Runnable{

        private EventStorage eventStorage;

        public Producer(EventStorage eventStorage) {
            this.eventStorage = eventStorage;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                eventStorage.set();
            }
        }
    }

    public static class Consumer implements Runnable{

        private EventStorage eventStorage;

        public Consumer(EventStorage eventStorage) {
            this.eventStorage = eventStorage;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                eventStorage.get();
            }
        }
    }
}
