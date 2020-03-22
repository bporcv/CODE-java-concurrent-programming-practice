package com.bporcv.code.ch06.threadSafeQueue;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @ClassName PriorityBlockingQueueCase
 * @Description 按优先级排序的阻塞式线程安全列表
 * * 添加进列表的元素必须实现Comparable接口，用来进行比较，决定插入顺序，实现有序。
 * * 阻塞式数据结构，当它的方法被调用并且不能立即执行时，调用这个方法的线程将被阻塞直到方法执行成功。
 * @Author Administrator
 * @Date 2020/3/21 23:12
 * @Version 1.0
 */
public class PriorityBlockingQueueCase {

    public static void main(String[] args) {
        PriorityBlockingQueue<Event> queue = new PriorityBlockingQueue<>();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            Task task = new Task(i, queue);
            threads[i] = new Thread(task);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Main: Queue Size: %d\n",queue.size());
        for (int i = 0; i < threads.length * 1000; i++) {
            Event event = queue.poll();
            System.out.printf("Thread %s: Priority %d\n",event.getThread(),event.getPriority());
        }
        System.out.printf("Main: Queue Size: %d\n",queue.size());
        System.out.printf("Main: End of the program.\n");
    }

    public static class Event implements Comparable<Event> {

        private int thread;
        private int priority;

        public Event(int thread, int priority) {
            this.thread = thread;
            this.priority = priority;
        }

        public int getThread() {
            return thread;
        }

        public int getPriority() {
            return priority;
        }


        @Override
        public int compareTo(Event e) {
            if (this.priority > e.getPriority()){
                return  -1;
            } else if (this.priority < e.getPriority()){
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static class Task implements Runnable {

        private int id;
        private PriorityBlockingQueue<Event> queue;

        public Task(int id, PriorityBlockingQueue<Event> queue) {
            this.id = id;
            this.queue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                Event event = new Event(id, i);
                queue.add(event);
            }

        }
    }

}
