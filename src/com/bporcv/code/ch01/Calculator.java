package com.bporcv.code.ch01;

/**
 * @ClassName Calculator
 * @Description 计算器
 * @Author Administrator
 * @Date 2020/3/12 22:26
 * @Version 1.0
 */
public class Calculator implements Runnable {

    private int number;

    public Calculator(int number) {
        this.number = number;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            System.out.printf("%s: %d * %d = %d\n", Thread.currentThread().getName(), number, i, i * number);
        }
    }
}

class Main {

    public static void main(String[] args) {

        for (int i = 1; i <= 10; i++) {
            Calculator calculator = new Calculator(i);
            Thread thread = new Thread(calculator);
            // 只有调用start方法才能创建线程
            thread.start();
        }
    }
}
