package com.bporcv.code.ch07;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @ClassName ExecutorUseCustomThreadFactoryCase
 * @Description 在Executor对象中使用ThreadFactory
 * @Author Administrator
 * @Date 2020/3/22 10:29
 * @Version 1.0
 */
public class ExecutorUseCustomThreadFactoryCase {

    public static void main(String[] args) throws InterruptedException {
        MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
        ExecutorService executor = Executors.newCachedThreadPool(factory);
        MyTask task = new MyTask();
        executor.submit(task);
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        System.out.printf("Main: End of the example.\n");
    }

    public static class MyThread extends Thread {
        private Date creationDate;
        private Date startDate;
        private Date finishDate;

        public MyThread(Runnable target, String name) {
            super(target, name);
            setCreationDate();
        }

        private void setCreationDate() {
            this.creationDate = new Date();
        }

        @Override
        public void run() {
            setStartDate();
            super.run();
            setFinishDate();
        }

        private void setStartDate() {
            this.startDate = new Date();
        }

        private void setFinishDate() {
            this.finishDate = new Date();
        }


        public long getExecutionTime() {
            return finishDate.getTime() - startDate.getTime();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(getName()).append(": ")
                    .append(" Creation Date: ")
                    .append(creationDate)
                    .append(" : Running time: ")
                    .append(getExecutionTime())
                    .append(" Milliseconds.");
            return builder.toString();
        }
    }

    public static class MyThreadFactory implements ThreadFactory {

        private int counter;
        private String prefix;

        public MyThreadFactory(String prefix) {
            this.prefix = prefix;
            this.counter = 1;
        }


        @Override
        public Thread newThread(Runnable r) {
            MyThread myThread = new MyThread(r, prefix + "-" + counter);
            counter++;
            return myThread;
        }
    }


    public static class MyTask implements Runnable{

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
