package com.bporcv.code.ch03;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PhaserCase2
 * @Description 并发阶段任务中的阶段切换
 * 模拟学生考试，共三道题，必须所有的学生都完成同一道题后才能开始下一道题
 * @Author Administrator
 * @Date 2020/3/17 20:57
 * @Version 1.0
 */
public class PhaserCase2 {

    public static void main(String[] args) {
        MyPhaser myPhaser = new MyPhaser();
        Student[] students = new Student[5];
        for (int i = 0; i < students.length; i++) {
            students[i] = new Student(myPhaser);
            // 注册参与者
            myPhaser.register();
        }
        Thread[] threads = new Thread[students.length];
        for (int i = 0; i < students.length; i++) {
            threads[i] = new Thread(students[i],"Student " + i);
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Main: The phaser has finished: %s.\n",myPhaser.isTerminated());

    }

    public static class MyPhaser extends Phaser {

        /**
         * 参数一：
         *
         * @param phase             当前的阶段数
         * @param registeredParties 注册的参与者的数量
         *
         * @return
         */
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            switch (phase) {
                case 0:
                    return studentsArrived();
                case 1:
                    return finishFirstExercise();
                case 2:
                    return finishSecondExercise();
                case 3:
                    return finishExam();
                default:
                    return true;
            }
        }

        private boolean studentsArrived() {
            System.out.printf("Phaser: The exam are going to start.The students are ready.\n");
            System.out.printf("Phaser: We have %d students.\n", getRegisteredParties());
            // 返回false表明phaser已经开始
            return false;
        }

        private boolean finishFirstExercise() {
            System.out.printf("Phaser: All the students have finished the first exercise.\n");
            System.out.printf("Phaser: It's time for the second one.\n");
            // 返回false表明phaser还在执行中，继续下一阶段
            return false;
        }

        private boolean finishSecondExercise() {
            System.out.printf("Phaser: All the students have finished the second exercise.\n");
            System.out.printf("Phaser：It's time for the third one.\n");
            // 返回false表明phaser还在执行中，继续下一阶段
            return false;
        }

        private boolean finishExam() {
            System.out.printf("Phaser: All the students have finished the exam.\n");
            System.out.printf("Phaser: Thank you for your time.\n");
            return true;
        }
    }

    public static class Student implements Runnable {
        private Phaser phaser;

        public Student(Phaser phaser) {
            this.phaser = phaser;
        }

        @Override
        public void run() {
            System.out.printf("%s: Has arrived to do the exam.\n", Thread.currentThread().getName());
            phaser.arriveAndAwaitAdvance();
            System.out.printf("%s: Is going to do the first exercise. %s\n", Thread.currentThread().getName(),new Date());
            doExercise1();
            System.out.printf("%s: Has done the first exercise. %s\n", Thread.currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();

            System.out.printf("%s: Is going to do the second exercise. %s\n", Thread.currentThread().getName(),new Date());
            doExercise2();
            System.out.printf("%s: Has done the second exercise. %s\n", Thread.currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();

            System.out.printf("%s: Is going to do the third exercise. %s\n", Thread.currentThread().getName(),new Date());
            doExercise3();
            System.out.printf("%s: Has done the third exercise. %s\n", Thread.currentThread().getName(), new Date());
            phaser.arriveAndAwaitAdvance();
        }

        private void doExercise1() {
            try {
                long duration = (long) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        private void doExercise2() {
            try {
                long duration = (long) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        private void doExercise3() {
            try {
                long duration = (long) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
