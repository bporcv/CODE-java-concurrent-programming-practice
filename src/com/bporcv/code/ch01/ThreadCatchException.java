package com.bporcv.code.ch01;

/**
 * @ClassName ThreadCatchException
 * @Description 线程中不可控异常的处理
 * JVM寻找线程中的异常的三种方式：
 * 1. 查找线程对象的未捕获异常处理器
 * 2. 如果未找到1中定义的处理器，则查找线程对象所在的线程组（Thread Group）的未捕获异常处理器
 * 3. 如果既没有找到1，也没有找到2中的异常处理器，则会查找默认的为捕获异常处理器。
 * @Author Administrator
 * @Date 2020/3/13 22:30
 * @Version 1.0
 */
public class ThreadCatchException {

    public static void main(String[] args) {
        Task task = new Task();
        Thread thread = new Thread(task);
        thread.setUncaughtExceptionHandler(new ExceptionHandler());
        thread.start();

    }

}

/**
 * 用于捕捉线程中的异常
 */
class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("An exception has been captured\n");
        System.out.printf("Thread: %s \n", t.getId());
        System.out.printf("Exception: %s: %s\n", e.getClass().getName(), e.getMessage());
        System.out.printf("Stack Trace: \n");
        e.printStackTrace(System.out);
        System.out.printf("Thread status： %s\n", t.getState());
    }
}

class Task implements Runnable {
    @Override
    public void run() {
        int numero = Integer.parseInt("TTT");
    }
}