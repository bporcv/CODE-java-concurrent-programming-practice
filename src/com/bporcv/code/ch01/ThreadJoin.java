package com.bporcv.code.ch01;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadJoin
 * @Description </br>
 * 等待线程的终止，join()方法，当一个线程对象的join()方法被调用时，调用它的线程会被挂起，
 * 直到这个线程对象完成他的任务。
 * @Author Administrator
 * @Date 2020/3/12 23:35
 * @Version 1.0
 */
public class ThreadJoin {

    public static void main(String[] args) {
        DataSourceLoader dsLoader = new DataSourceLoader();
        Thread dsThread = new Thread(dsLoader,"DataSourceThread");
        NetWorkConnectionsLoader ncLoader = new NetWorkConnectionsLoader();
        Thread ncThread = new Thread(ncLoader,"NetworkConnectionLoader");
        dsThread.start();
        ncThread.start();

        try {
            dsThread.join();
            ncThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main：Configuration has been loaded: %s\n",new Date());
    }

}

class DataSourceLoader implements Runnable{

    @Override
    public void run() {
        System.out.printf("Beginning data sources loading: %s\n",new Date());
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Data sources loading has finished: %s\n",new Date());
    }
}

class NetWorkConnectionsLoader implements Runnable{

    @Override
    public void run() {
        System.out.printf("Beginning network connection loading: %s\n",new Date());
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Network connection loading has finished: %s\n",new Date());
    }
}

