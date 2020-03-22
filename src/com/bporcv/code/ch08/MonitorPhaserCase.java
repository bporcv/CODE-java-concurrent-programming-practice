package com.bporcv.code.ch08;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MonitorPhaserCase
 * @Description // TODO
 * @Author Administrator
 * @Date 2020/3/22 22:57
 * @Version 1.0
 */
public class MonitorPhaserCase {

    public static void main(String[] args) {
        Phaser phaser = new Phaser(3);
        for (int i = 0; i < 3; i++) {
            Task task = new Task(i + 1, phaser);
            Thread thread = new Thread(task);
            thread.start();
        }

        for (int i = 0; i < 10; i++) {
            System.out.printf("**************************\n");
            System.out.printf("Main: Phaser Log \n");
            System.out.printf("Main: Phaser: Phase: %d\n",phaser.getPhase());
            System.out.printf("Main: Phaser: Registered Parties: %d\n",phaser.getRegisteredParties());
            System.out.printf("Main: Phaser: Arrived Parties: %d\n",phaser.getArrivedParties());
            System.out.printf("Main: Phaser: Unarrived Parties: %d\n",phaser.getUnarrivedParties());
            System.out.printf("**************************\n");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public static class Task implements Runnable {
        private int time;
        private Phaser phaser;

        public Task(int time, Phaser phaser) {
            this.time = time;
            this.phaser = phaser;
        }

        @Override
        public void run() {
            phaser.arrive();
            System.out.printf("%s: Entering phase 1.\n", Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s: Finishing phase 1.\n", Thread.currentThread().getName());
            phaser.arriveAndAwaitAdvance();

            System.out.printf("%s: Entering phase 2.\n", Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s: Finishing phase 2.\n", Thread.currentThread().getName());
            phaser.arriveAndAwaitAdvance();

            System.out.printf("%s: Entering phase 3.\n", Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s: Finishing phase 3.\n", Thread.currentThread().getName());
            phaser.arriveAndDeregister();

        }
    }
}
