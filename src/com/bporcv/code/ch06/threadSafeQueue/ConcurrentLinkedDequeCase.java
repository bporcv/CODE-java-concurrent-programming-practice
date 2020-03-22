package com.bporcv.code.ch06.threadSafeQueue;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @ClassName ConcurrentLinkedDequeCase
 * @Description 非阻塞式线程安全列表
 * @Author Administrator
 * @Date 2020/3/20 23:11
 * @Version 1.0
 */
public class ConcurrentLinkedDequeCase {

    public static void main(String[] args) {
        ConcurrentLinkedDeque<String> list = new ConcurrentLinkedDeque<>();
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            AddTask addTask = new AddTask(list);
            threads[i] = new Thread(addTask);
            threads[i].start();
        }

        System.out.printf("Main: %d AddTask threads have been launched.\n", threads.length);

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Main: Size of the List: %d\n", list.size());

        for (int i = 0; i < threads.length; i++) {
            PollTask task = new PollTask(list);
            threads[i] = new Thread(task);
            threads[i].start();
        }

        System.out.printf("Main: %d PollTask threads have been launched\n", threads.length);
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Main: Size of the List: %d\n", list.size());

    }

    public static class AddTask implements Runnable {

        private ConcurrentLinkedDeque<String> list;

        public AddTask(ConcurrentLinkedDeque<String> list) {
            this.list = list;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            for (int i = 0; i < 10_000; i++) {
                list.add(name + ": Element" + i);
            }
        }
    }

    public static class PollTask implements Runnable {

        private ConcurrentLinkedDeque<String> list;

        public PollTask(ConcurrentLinkedDeque<String> list) {
            this.list = list;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5_000; i++) {
                list.pollLast();
                list.pollFirst();
            }
        }
    }


}
