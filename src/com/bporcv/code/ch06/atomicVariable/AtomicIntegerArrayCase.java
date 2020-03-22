package com.bporcv.code.ch06.atomicVariable;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @ClassName AtomicIntegerArrayCase
 * @Description 原子数组
 * @Author Administrator
 * @Date 2020/3/22 10:19
 * @Version 1.0
 */
public class AtomicIntegerArrayCase {

    public static void main(String[] args) {
        final int THREADS = 100;
        AtomicIntegerArray vector = new AtomicIntegerArray(1000);
        Increment increment = new Increment(vector);
        Decrement decrement = new Decrement(vector);
        Thread[] threadIncrements = new Thread[THREADS];
        Thread[] threadDecrements = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threadIncrements[i] = new Thread(increment);
            threadDecrements[i] = new Thread(decrement);
            threadIncrements[i].start();
            threadDecrements[i].start();
        }

        for (int i = 0; i < THREADS; i++) {
            try {
                threadIncrements[i].join();
                threadDecrements[i].join();
                ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < vector.length(); i++) {
            if (vector.get(i) != 0) {
                System.out.println("Vector[" + i + "] : " + vector.get(i));
            }
        }
        System.out.printf("Main: End of the program");
    }


    public static class Increment implements Runnable {
        private AtomicIntegerArray vector;

        public Increment(AtomicIntegerArray vector) {
            this.vector = vector;
        }

        @Override
        public void run() {
            for (int i = 0; i < vector.length(); i++) {
                vector.getAndIncrement(i);
            }
        }
    }

    public static class Decrement implements Runnable {
        private AtomicIntegerArray vector;

        public Decrement(AtomicIntegerArray vector) {
            this.vector = vector;
        }

        @Override
        public void run() {
            for (int i = 0; i < vector.length(); i++) {
                vector.getAndDecrement(i);
            }
        }
    }
}
