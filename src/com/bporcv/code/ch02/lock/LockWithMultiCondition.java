package com.bporcv.code.ch02.lock;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName LockWithMultiConditon
 * @Description 在锁中使用多条件
 * @Author Administrator
 * @Date 2020/3/16 21:34
 * @Version 1.0
 */
public class LockWithMultiCondition {

    public static void main(String[] args) {
        FileMock mock = new FileMock(100,10);
        Buffer buffer = new Buffer(20);
        Producer producer = new Producer(mock,buffer);
        Thread threadProducer = new Thread(producer);
        Consumer[] consumers = new Consumer[3];
        Thread[] threadConsumers = new Thread[3];
        for (int i = 0; i < 3; i++) {
            consumers[i] = new Consumer(buffer);
            threadConsumers[i] = new Thread(consumers[i],"Consumer "+ i);
        }
        threadProducer.start();
        for (int i = 0; i < 3; i++) {
            threadConsumers[i].start();
        }

    }


    public static class FileMock {
        private String[] content;
        private int index;

        public FileMock(int size, int length) {
            content = new String[size];
            for (int i = 0; i < size; i++) {
                StringBuilder builder = new StringBuilder(length);
                for (int i1 = 0; i1 < length; i1++) {
                    int indice = (int) Math.random() * 255;
                    builder.append((char) indice);
                }
                content[i] = builder.toString();
            }
            index = 0;
        }

        public boolean hasMoreLines() {
            return index < content.length;
        }

        public String getLine() {
            if (this.hasMoreLines()) {
                System.out.println("Mock:" + (content.length - index));
                return content[index++];
            }
            return null;
        }
    }

    public static class Buffer {
        // 存放共享数据
        private LinkedList<String> buffer;
        // 存放buffer的长度
        private int maxSize;
        // 可重入锁，对用来修改buffer的代码块进行控制
        private ReentrantLock lock;
        // 条件属性
        private Condition lines;
        // 条件属性
        private Condition space;
        // 用来表明缓冲区中是否还有数据
        private boolean pendingLines;

        public Buffer(int maxSize) {
            this.maxSize = maxSize;
            buffer = new LinkedList<>();
            lock = new ReentrantLock();
            lines = lock.newCondition();
            space = lock.newCondition();
            pendingLines = true;
        }

        public void insert(String line) {
            lock.lock();
            try {
                while (buffer.size() == maxSize) {
                    // 调用space的await()方法等待空位出现，当其他线程调用条件space的signal()方法或者signalAll()方法，这个线程将被唤醒
                    space.await();
                }
                buffer.offer(line);
                System.out.printf("%s: Inserted Line: %d\n", Thread.currentThread().getName(), buffer.size());
                lines.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public String get() {
            String line = null;
            lock.lock();
            try {
                while (buffer.size() == 0 && (hasPendingLines())) {
                    lines.await();
                }
                if (hasPendingLines()) {
                    line = buffer.poll();
                    System.out.printf("%s: Line Readed: %d \n", Thread.currentThread().getName(), buffer.size());
                    space.signalAll();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            return line;
        }

        public void setPendingLines(boolean pendingLines) {
            this.pendingLines = pendingLines;
        }

        public boolean hasPendingLines() {
            return pendingLines || buffer.size() > 0;
        }
    }

    public static class Producer implements Runnable {
        private FileMock fileMock;
        private Buffer buffer;

        public Producer(FileMock fileMock, Buffer buffer) {
            this.fileMock = fileMock;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            buffer.setPendingLines(true);
            while (fileMock.hasMoreLines()) {
                String line = fileMock.getLine();
                buffer.insert(line);
            }
            buffer.setPendingLines(false);
        }
    }

    public static class Consumer implements Runnable {
        private Buffer buffer;

        public Consumer(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (buffer.hasPendingLines()) {
                String line = buffer.get();
                processLine(line);
            }
        }

        private void processLine(String line) {
            try {
                Random random = new Random();
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
