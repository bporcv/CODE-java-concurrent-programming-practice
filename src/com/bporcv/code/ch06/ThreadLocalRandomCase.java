package com.bporcv.code.ch06;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName ThreadLocalRandomCase
 * @Description 生成并发随机数
 * ThreadLocalRandomCase 线程本地变量，每个生成随机数的线程都有一个不同的生成器，但是都是在同一个类中被管理
 * 相对于使用共享的Rnadom对象为所有线程生成随机数，这种机制具有更好的性能
 * @Author Administrator
 * @Date 2020/3/22 9:34
 * @Version 1.0
 */
public class ThreadLocalRandomCase {

    public static void main(String[] args) {
        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            TaskLocalRandom task = new TaskLocalRandom();
            threads[i] = new Thread(task);
            threads[i].start();
        }
    }

    public static class TaskLocalRandom implements Runnable{

        public TaskLocalRandom() {
            //在构造器中，使用current()方法为当前线程初始化随机数生成器,并与当前线程建立关联
            ThreadLocalRandom.current();
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            for (int i = 0; i < 10; i++) {
                // 生成随机数
                System.out.printf("%s: %d\n",name,ThreadLocalRandom.current().nextInt(10));
            }
        }
    }

}
