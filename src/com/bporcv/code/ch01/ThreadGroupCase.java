package com.bporcv.code.ch01;


import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadGroup
 * @Description 线程组, 对线程进行分组，对同组的线程可以执行批量操作
 * @Author Administrator
 * @Date 2020/3/13 22:57
 * @Version 1.0
 * Function:
 * 创建10个线程并让他们休眠一个随机时间，当其中一个线程查找成功时，中断其他9个线程
 */
public class ThreadGroupCase {

    // 用来存储先执行完的线程
    static class Result {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    static class SearchTask implements Runnable {
        private Result result;

        public SearchTask(Result result) {
            this.result = result;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            System.out.printf("Thread %s: Start\n",name);
            try {
                doTask();
                result.setName(name);
            } catch (InterruptedException e){
                e.printStackTrace();
                System.out.printf("Thread %s： Interrupted\n",name);
                return;
            }
            System.out.printf("Thread %s: End\n",name);
        }

        // 让线程随机sleep
        private void doTask() throws InterruptedException {
            Random random = new Random(new Date().getTime());
            int value = (int)(random.nextDouble()  * 100);
            System.out.printf("Thread %s: %d\n",Thread.currentThread().getName(),value);
            TimeUnit.SECONDS.sleep(value);
        }
    }

    public static void main(String[] args) {
        ThreadGroup threadGroup = new ThreadGroup("Searcher");
        Result result = new Result();
        SearchTask searchTask = new SearchTask(result);
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(threadGroup, searchTask);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Number of Threads: %d\n",threadGroup.activeCount());
        System.out.printf("Information about the Thread Group\n");
        // 通过list方法打印线程组对象的信息
        threadGroup.list();
        Thread[] threads = new Thread[threadGroup.activeCount()];
        // 获取线程组包含的线程列表
        threadGroup.enumerate(threads);
        for (int i = 0; i < threadGroup.activeCount(); i++) {
            System.out.printf("Thread %s : %s\n",threads[i].getName(),threads[i].getState());
        }
        // 如果其中有一个线程结束了，便结束了
        waitFinish(threadGroup);
        // 中断这个组中的其余线程
        threadGroup.interrupt();

    }

    private static void waitFinish(ThreadGroup threadGroup) {
        while (threadGroup.activeCount() > 9){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
