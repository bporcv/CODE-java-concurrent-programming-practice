package com.bporcv.code.ch01;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadInterruptControl
 * @Description 控制线程的中断，利用InterruptException
 * @Author Administrator
 * @Date 2020/3/12 23:20
 * @Version 1.0
 */
public class ThreadInterruptControl {

    public static void main(String[] args) {
        FileSearch search = new FileSearch("C:\\", "autoexec.bat");
        Thread thread = new Thread(search);
        thread.start();

        // 等待10秒钟，中断FileSearch线程
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();

    }

}

class FileSearch implements Runnable {
    // 初始文件夹
    private String initPath;
    // 要查找的文件名称
    private String fileName;

    public FileSearch(String initPath, String fileName) {
        this.initPath = initPath;
        this.fileName = fileName;
    }


    @Override
    public void run() {
        File file = new File(initPath);
        if (file.isDirectory()) {
            try {
                directoryProcess(file);
            } catch (InterruptedException e) {
                System.out.printf("%s: The search has been interrupted", Thread.currentThread().getName());
            }
        }
    }

    /**
     * 递归获取某个目录下的所有文件的方法
     *
     * @param file 初始目录
     */
    private void directoryProcess(File file) throws InterruptedException {
        File[] files = file.listFiles();
        if (files != null) {
            for (File temp : files) {
                if (temp.isDirectory()) {
                    directoryProcess(temp);
                } else {
                    fileProcess(temp);
                }
            }
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

    }

    /**
     * 对文件进行处理
     *
     * @param file
     */
    private void fileProcess(File file) throws InterruptedException {
        if (file.getName().equals(fileName)) {
            System.out.printf("%s : %s \n", Thread.currentThread().getName(), file.getAbsolutePath());
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
}
