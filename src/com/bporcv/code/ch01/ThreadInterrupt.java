package com.bporcv.code.ch01;

/**
 * @ClassName ThreadInterrupt
 * @Description 线程中断，正常的打断操作
 * @Author Administrator
 * @Date 2020/3/12 22:53
 * @Version 1.0
 */
public class ThreadInterrupt {

    public static void main(String[] args) {
        PrimeGenerator generator = new PrimeGenerator();
        generator.start();

        // 这里让Main线程释放资源5秒后进行generator的打断操作
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 这里会设置线程内部的标记是否中断的属性值为true
        generator.interrupt();

    }

}

class PrimeGenerator extends Thread {

    @Override
    public void run() {
        long number = 1L;
        while (true) {
            if (isPrime(number)) {
                System.out.printf("Number %d is prime\n", number);
            }
            // 用于判断一个线程是否被打断
            if (isInterrupted()) {
                System.out.printf("The Prime Generator has been Interrupted");
                return;
            }
            number++;
        }
    }

    // 判断一个数字是否为质数
    private boolean isPrime(long number) {
        if (number <= 2) {
            return true;
        }
        for (long i = 2; i < number; i++) {
            if ((number % i) == 0) {
                return false;
            }
        }
        return true;
    }
}


