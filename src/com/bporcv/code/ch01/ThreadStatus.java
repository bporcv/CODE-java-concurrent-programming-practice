package com.bporcv.code.ch01;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * @ClassName ThreadStatus
 * @Description 线程的状态
 * @Author Administrator
 * @Date 2020/3/12 22:34
 * @Version 1.0
 */
public class ThreadStatus {

    public static void main(String[] args) {
        Thread[] threads = new Thread[10];
        Thread.State[] states = new Thread.State[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new CalculatorThread(i));
            if (i % 2 == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("Thread-" + i);
        }
        try (FileWriter fileWriter = new FileWriter(".\\logs\\log.txt");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            // 写入10个线程的状态
            for (int i = 0; i < 10; i++) {
                printWriter.println("Main: State of Thread " + i + " : " + threads[i].getState());
                states[i] = threads[i].getState();
            }
            // 开始执行10个线程
            for (int i = 0; i < 10; i++) {
                threads[i].start();
            }
            boolean finish = false;
            while (!finish){
                for (int i = 0; i < 10; i++) {
                    if (threads[i].getState() != states[i]){
                        writeThreadInfo(printWriter,threads[i],states[i]);
                        states[i] = threads[i].getState();
                    }
                }
                finish = true;
                for (int i = 0; i < 10; i++) {
                    finish = finish && (threads[i].getState() == Thread.State.TERMINATED);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void writeThreadInfo(PrintWriter printWriter, Thread thread, Thread.State state) {
        printWriter.printf("Main : Id %d - %s \n", thread.getId(),thread.getName());
        printWriter.printf("Main : Priority: %d\n", thread.getPriority());
        printWriter.printf("Main : Old State: %s \n", state);
        printWriter.printf("Main : New State: %s \n", thread.getState());
        printWriter.printf("Main : ******************************************\n");
    }

}

class CalculatorThread implements Runnable {

    private int number;

    public CalculatorThread(int number) {
        this.number = number;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            System.out.printf("%s: %d * %d = %d\n", Thread.currentThread().getName(), number, i, i * number);
        }
    }
}