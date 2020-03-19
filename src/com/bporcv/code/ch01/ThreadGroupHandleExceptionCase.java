package com.bporcv.code.ch01;

import java.util.Random;

/**
 * @ClassName ThreadHandleExceptionCase
 * @Description 线程组中不可控异常的处理
 * @Author Administrator
 * @Date 2020/3/13 23:16
 * @Version 1.0
 */
public class ThreadGroupHandleExceptionCase {

    public static void main(String[] args) {
        MyThreadGroup threadGroup = new MyThreadGroup("MyThreadGroup");
        Task task = new Task();
        for (int i = 0; i < 2; i++) {
            Thread thread = new Thread(threadGroup, task);
            thread.start();
        }

    }


    static class MyThreadGroup extends ThreadGroup {

        public MyThreadGroup(String name) {
            super(name);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.printf("The thread %s has thrown an Exception\n", t.getId());
            e.printStackTrace(System.out);
            System.out.printf("Terminating the rest of the Threads\n");
            interrupt();
        }
    }

    static class Task implements Runnable {

        @Override
        public void run() {
            int result;
            Random random = new Random(Thread.currentThread().getId());
            while (true){
                result = 1000 / ((int)(random.nextDouble() * 1000));
                System.out.printf("%s : %d\n",Thread.currentThread().getId(),result);
                if (Thread.currentThread().isInterrupted()){
                    System.out.printf("%d : Interrupted\n",Thread.currentThread().getId());
                    return;
                }
            }
        }
    }
}
