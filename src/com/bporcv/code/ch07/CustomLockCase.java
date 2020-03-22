package com.bporcv.code.ch07;

import com.bporcv.code.ch01.Calculator;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @ClassName CustomLockCase
 * @Description 自定义锁
 * @Author Administrator
 * @Date 2020/3/22 18:09
 * @Version 1.0
 */
public class CustomLockCase {

    public static void main(String[] args) {
        MyLock lock = new MyLock();
        for (int i = 0; i < 10; i++) {
            Task task = new Task(lock, "Task-" + i);
            Thread thread = new Thread(task);
            thread.start();

        }

        boolean value;
        do {
            try {
                value = lock.tryLock(1,TimeUnit.SECONDS);
                if (!value){
                    System.out.printf("Main: Trying to get the lock\n");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                value = false;
            }

        } while (!value);
        System.out.printf("Main: Got the lock\n");
        lock.unlock();
        System.out.printf("Main: End of the program.\n");
    }

    public static class MyAbstractQueuedSynchronizer extends AbstractQueuedSynchronizer {
        private AtomicInteger state;

        public MyAbstractQueuedSynchronizer() {
            state = new AtomicInteger();
        }

        @Override
        protected boolean tryAcquire(int arg) {
            return state.compareAndSet(0, 1);
        }

        @Override
        protected boolean tryRelease(int arg) {
            return state.compareAndSet(1,0);
        }

    }

    public static class MyLock implements Lock {

        private AbstractQueuedSynchronizer sync;

        public MyLock() {
            this.sync = new MyAbstractQueuedSynchronizer();
        }

        @Override
        public void lock() {
            sync.acquire(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            sync.acquireInterruptibly(1);
        }

        @Override
        public boolean tryLock() {
            try {
                return this.sync.tryAcquireNanos(1, 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return this.sync.tryAcquireNanos(1, TimeUnit.NANOSECONDS.convert(time, unit));
        }

        @Override
        public void unlock() {
            this.sync.release(1);
        }

        @Override
        public Condition newCondition() {
            return sync.new ConditionObject();
        }

    }

    public static class Task implements Runnable{
        private MyLock lock;

        private String name;

        public Task(MyLock lock, String name) {
            this.lock = lock;
            this.name = name;
        }

        @Override
        public void run() {
            lock.lock();
            System.out.printf("Task: %s: Take the lock.\n",name);
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.printf("Task: %s: Free the lock.\n",name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        }
    }

}

