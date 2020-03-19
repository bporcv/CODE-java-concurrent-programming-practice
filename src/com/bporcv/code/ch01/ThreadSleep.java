package com.bporcv.code.ch01;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadSleep
 * @Description 线程的休眠与恢复
 * @Author Administrator
 * @Date 2020/3/12 23:31
 * @Version 1.0
 */
public class ThreadSleep {

    public static void main(String[] args) {
        FileClock clock = new FileClock();
        Thread thread = new Thread(clock);
        thread.start();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();

    }

}

class FileClock implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s\n",new Date());
            try {
                // 当调用sleep方法之后，线程会释放CPU并且不再继续执行任务。
                // 在这段时间内，线程不占用CPU时钟，所以CPU可以执行其他任务。
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.printf("The FileClock has been interrupted.");
            }
        }
    }
}