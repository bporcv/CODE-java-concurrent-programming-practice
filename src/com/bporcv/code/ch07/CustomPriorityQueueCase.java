package com.bporcv.code.ch07;

import sun.misc.ThreadGroupUtils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName CustomPriorityQueueCase
 * @Description 实现基于优先级的传输队列
 * @Author Administrator
 * @Date 2020/3/22 20:14
 * @Version 1.0
 */
public class CustomPriorityQueueCase {

    public static void main(String[] args) throws InterruptedException {
        MyPriorityTransferQueue<Event> queue = new MyPriorityTransferQueue<>();
        Producer producer = new Producer(queue);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(producer);
            threads[i].start();
        }

        Consumer consumer = new Consumer(queue);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
        // 实际的消费者数
        System.out.printf("Main: Queue: Consumer count: %d\n",queue.getWaitingConsumerCount());

        Event event = new Event("Core Event 1", 0);
        try {
            queue.transfer(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Main: Event has been transfered.\n");
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Queue: Consumer count: %d\n",queue.getWaitingConsumerCount());

        event = new Event("Core Event 2",0);
        queue.transfer(event);
        consumerThread.join();
        System.out.printf("Main: End of the program\n");
    }

    public static class MyPriorityTransferQueue<E> extends PriorityBlockingQueue<E> implements TransferQueue<E> {

        private AtomicInteger counter;
        private LinkedBlockingDeque<E> transfered;
        private ReentrantLock lock;

        public MyPriorityTransferQueue() {
            counter = new AtomicInteger(0);
            lock = new ReentrantLock();
            transfered = new LinkedBlockingDeque<>();
        }

        // 尝试立即将元素发往一个正在等待的消费者，如果没有等待中的消费者，该方法返回false
        @Override
        public boolean tryTransfer(E e) {
            lock.lock();
            boolean value;
            if (counter.get() == 0) {
                value = false;
            } else {
               put(e);
               value = true;
            }
            lock.unlock();
            return value;
        }

        // 尝试立即将元素发往一个正在等待的消费者，如果没有等待中的消费者，存储到transfered队列中，
        // 并等待出现试图获得元素的第一个消费者，在这之前，线程将被阻塞
        @Override
        public void transfer(E e) throws InterruptedException {
            lock.lock();
            if (counter.get() != 0){
                put(e);
                lock.unlock();
            } else {
                transfered.add(e);
                lock.unlock();
                synchronized (e){
                    e.wait();
                }
            }
        }

        /**
         *  如果有消费者在等待，就立即发送元素，否则，将参数指定的时间转换为毫秒并使用wait()方法让线程休眠。
         *  当消费者取元素的时候，如果线程仍在waite()方法中休眠，将使用notify()方法去唤醒它
         * @param e 标识生产和消费的元素
         * @param timeout 标识如果没有消费者则等待一个消费者的时间
         * @param unit 等待时间的单位
         * @return
         * @throws InterruptedException
         */
        @Override
        public boolean tryTransfer(E e, long timeout, TimeUnit unit) throws InterruptedException {
            lock.lock();
            if (counter.get() != 0){
                put(e);
                lock.unlock();
                return true;
            } else {
                transfered.add(e);
                long newTimeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
                lock.unlock();
                e.wait(newTimeout);
                lock.lock();
                if (transfered.contains(e)) {
                    transfered.remove(e);
                    lock.unlock();
                    return false;
                } else {
                    lock.unlock();
                    return true;
                }
            }
        }

        @Override
        public boolean hasWaitingConsumer() {
            return (counter.get() !=0);
        }

        @Override
        public int getWaitingConsumerCount() {
            return counter.get();
        }

        /**
         * 该方法为准备消费元素的消费者调用
         * @return
         * @throws InterruptedException
         */
        @Override
        public E take() throws InterruptedException {
            lock.lock();
            // 增加正在等待的消费者的数量
            counter.incrementAndGet();
            // 如果transfered队列中没有元素，则释放锁并尝试使用take()方法从队列中取的一个元素并再次获得锁
            // 如果队列中没有元素，让线程休眠直至有元素可被消费
            E value = transfered.poll();
            if (value == null){
                lock.unlock();
                value = super.take();
                lock.lock();
            } else {
                // 从transfered队列中取出value元素，并唤醒可能在等待元素被消费的线程
                synchronized (value){
                    value.notify();
                }
            }
            // 减少正在等待的消费者的数量并释放锁
            counter.decrementAndGet();
            lock.unlock();
            return value;
        }

    }

    public static class Event implements Comparable<Event>{

        private String thread;
        private int priority;

        public Event(String thread, int priority) {
            this.thread = thread;
            this.priority = priority;
        }

        public String getThread() {
            return thread;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public int compareTo(Event o) {
            if (this.priority > o.getPriority()){
                return -1;
            } else if (this.getPriority() < o.getPriority()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    public static class Producer implements Runnable {
        private MyPriorityTransferQueue<Event> queue;

        public Producer(MyPriorityTransferQueue<Event> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                Event event = new Event(Thread.currentThread().getName(), i);
                queue.put(event);
            }
        }
    }
    
    public static class Consumer implements Runnable {
        private MyPriorityTransferQueue<Event> queue;

        public Consumer(MyPriorityTransferQueue<Event> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1002; i++) {
                try {
                    Event event = queue.take();
                    System.out.printf("Consumer: %s: %d\n",event.getThread(),event.getPriority());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


