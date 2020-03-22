package com.bporcv.code.ch07;

import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName CustomThreadCase
 * @Description 实现ThreadFactory接口生成定制线程
 * @Author Administrator
 * @Date 2020/3/22 11:46
 * @Version 1.0
 */
public class CustomThreadCase {

    public static void main(String[] args) throws InterruptedException {
        MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
        MyTask task = new MyTask();
        Thread thread = factory.newThread(task);
        thread.start();
        thread.join();
        System.out.printf("Main: Thread information.\n");
        System.out.printf("%s\n",thread);
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
