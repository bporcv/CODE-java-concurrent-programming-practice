package com.bporcv.code.ch06.threadSafeQueue;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName LinkedBlockingDequeCase
 * @Description 阻塞式线程安全列表
 * * 阻塞式列表在插入和删除元素的时候，如果列表已满或为空，操作不会被立即执行。而是调用这个操作的线程阻塞队列，直到操作可以执行成功。
 * @Author Administrator
 * @Date 2020/3/20 23:23
 * @Version 1.0
 */
public class LinkedBlockingDequeCase {

    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingDeque<String> list = new LinkedBlockingDeque<>(3);
        Client client = new Client(list);
        Thread thread = new Thread(client);
        thread.start();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                String request = list.take();
                System.out.printf("Main: Request %s at %s.Size: %d\n",request,new Date(),list.size());
            }
            TimeUnit.MILLISECONDS.sleep(300);
        }
        System.out.printf("Main: End of the program.\n");

    }

    public static class Client implements Runnable {
        private LinkedBlockingDeque<String> requestList;

        public Client(LinkedBlockingDeque<String> requestList) {
            this.requestList = requestList;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    StringBuilder request = new StringBuilder();
                    request.append(i).append(":").append(j);
                    try {
                        requestList.put(request.toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("Clent: %s at %s.\n",request,new Date());
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.printf("Client: End.\n");
        }
    }

}
