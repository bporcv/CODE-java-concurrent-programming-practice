package com.bporcv.code.ch03;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * @ClassName ExchangerCase
 * @Description 并发任务间的数据交换
 * Exchanger只能交换两个线程的数据，当两个线程都到达同步点，他们交换数据结构
 * @Author Administrator
 * @Date 2020/3/17 22:07
 * @Version 1.0
 */
public class ExchangerCase {

    public static void main(String[] args) {
        List<String> buffer1 = new ArrayList<>();
        List<String> buffer2 = new ArrayList<>();
        Exchanger<List<String>> exchanger = new Exchanger<>();

        Producer producer = new Producer(buffer1, exchanger);
        Consumer consumer = new Consumer(buffer2, exchanger);
        Thread threadProducer = new Thread(producer);
        Thread threadConsumer = new Thread(consumer);
        threadProducer.start();
        threadConsumer.start();

    }

    public static class Producer implements Runnable {

        private List<String> buffer;

        private final Exchanger<List<String>> exchanger;

        public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            int cycle = 1;
            for (int i = 0; i < 10; i++) {
                System.out.printf("Producer: Cycle %d\n", cycle);
                for (int j = 0; j < 10; j++) {
                    String message = "Event " + ((i * 10) + j);
                    System.out.printf("Producer: %s\n", message);
                    buffer.add(message);
                }
                try {
                    buffer = exchanger.exchange(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Producer: " + buffer.size());
                cycle++;
            }
        }
    }

    public static class Consumer implements Runnable {

        private List<String> buffer;
        private final Exchanger<List<String>> exchanger;

        public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            int cycle = 1;
            for (int i = 0; i < 10; i++) {
                System.out.printf("Consumer: Cycle %d\n", cycle);
                try {
                    buffer = exchanger.exchange(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Consumer: " + buffer.size());
                for (int j = 0; j < 10; j++) {
                    String message = buffer.get(0);
                    System.out.println("Consumer: " + message);
                    buffer.remove(0);
                }
                cycle++;
            }
        }
    }

}
