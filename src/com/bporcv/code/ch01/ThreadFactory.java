package com.bporcv.code.ch01;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadFactory
 * @Description 使用工厂类创建线程
 * @Author Administrator
 * @Date 2020/3/13 23:24
 * @Version 1.0
 */
public class ThreadFactory {

    public static void main(String[] args) {
        MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
        Task task = new Task();
        Thread thread;
        System.out.printf("Starting the Threads\n");
        for (int i = 0; i < 10; i++) {
            thread = factory.newThread(task);
            thread.start();
        }
        System.out.printf("Factory stats:\n");
        System.out.printf("%s\n",factory.getStats());

    }

    static class MyThreadFactory implements java.util.concurrent.ThreadFactory {
        private int counter;//存储线程对象的数量
        private String name;//存储每个线程的名称
        private List<String> stats;//存放线程对象的统计数据

        public MyThreadFactory(String name) {
            this.name = name;
            counter = 0;
            stats = new ArrayList<>();
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, name + "-Thread_" + counter);
            counter++;
            stats.add(String.format("Create thread %d with name %s on %s\n", t.getId(), t.getName(), new Date()));
            return t;
        }

        public String getStats() {
            StringBuffer buffer = new StringBuffer();
            Iterator<String> iterator = stats.iterator();
            while (iterator.hasNext()){
                buffer.append(iterator.next());
                buffer.append("\n");
            }
            return buffer.toString();
        }
    }

    static class Task implements Runnable {
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
