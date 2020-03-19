package com.bporcv.code.ch03;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PhaserCase
 * @Description 并发阶段任务的运行
 * 当我们与并发任务并且需要分解成几步执行时，使用Phaser就很适合
 * 其机制是在没一步结束的位置对线程进行同步，当所有的线程都完成了这一步，才允许执行下一步
 * arriveAndAwaitAdvance ：调用这个方法，说明在等待同步，即所有的线程都同步到这里，然后开启下一个阶段的运行
 * arriveAndDeregister ： 调用这个方法，说明该线程已经结束注册，不参与后面的阶段的运行
 * @Author Administrator
 * @Date 2020/3/17 20:20
 * @Version 1.0
 */
public class PhaserCase {

    public static void main(String[] args) {
        // 创建一个Phaser，需要指出有多少参与者，这里是3个参与者
        Phaser phaser = new Phaser(3);
        FileSearch system = new FileSearch("C:\\Windows", "log", phaser);
        FileSearch app = new FileSearch("C:\\Program Files", "log", phaser);
        FileSearch documents = new FileSearch("C:\\Documents And Settings", "log", phaser);

        Thread thread = new Thread(system, "System");
        thread.start();
        Thread apps = new Thread(app, "Apps");
        apps.start();
        Thread documents1 = new Thread(documents, "Documents");
        documents1.start();


        try {
            thread.join();
            apps.join();
            documents1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Terminated： " + phaser.isTerminated());

    }


    public static class FileSearch implements Runnable {

        private String initPath;

        private String end;

        private List<String> results;

        private Phaser phaser;


        public FileSearch(String initPath, String end, Phaser phaser) {
            this.initPath = initPath;
            this.end = end;
            this.phaser = phaser;
            results = new ArrayList<>();
        }

        @Override
        public void run() {
            // 调用这个方法，Phaser对象将减1，并且把这个线程置于休眠状态，直到所有其他线程完成这个阶段
            // 阶段一：这样可以保证跑在同一起跑线上
            phaser.arriveAndAwaitAdvance();
            System.out.printf("%s: Starting.\n", Thread.currentThread().getName());
            File file = new File(initPath);
            if (file.isDirectory()) {
                directoryProcess(file);
            }
            if (!checkResults()) {
                return;
            }
            filterResults();
            if (!checkResults()) {
                return;
            }
            showInfo();
            phaser.arriveAndDeregister();
            System.out.printf("%s: Work completed.\n", Thread.currentThread().getName());
        }

        private void directoryProcess(File file) {
            File[] list = file.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isDirectory()) {
                        directoryProcess(list[i]);
                    } else {
                        fileProcess(list[i]);
                    }
                }
            }
        }

        private void fileProcess(File file) {
            if (file.getName().endsWith(end)) {
                results.add(file.getAbsolutePath());
            }
        }

        private void filterResults() {
            List<String> newResults = new ArrayList<>();
            long actualDate = new Date().getTime();
            for (int i = 0; i < results.size(); i++) {
                File file = new File(results.get(i));
                long fileDate = file.lastModified();
                if (actualDate - fileDate < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                    newResults.add(results.get(i));
                }
            }
            results = newResults;
        }

        // 阶段二
        private boolean checkResults() {
            if (results.isEmpty()) {
                System.out.printf("%s: Phase %d: 0 results.\n", Thread.currentThread().getName(), phaser.getPhase());
                System.out.printf("%s: Phase %d: End.\n", Thread.currentThread().getName(), phaser.getPhase());
                // 通知当前线程已经结束这个阶段，并且将不再参与接下来的阶段操作
                phaser.arriveAndDeregister();
                return false;
            } else {
                System.out.printf("%s: Phase %d: %d results.\n", Thread.currentThread().getName(), phaser.getPhase(), results.size());
                // 通知phaser对象当前线程已经完成了当前阶段，需要被阻塞直到其他线程也都完成当前阶段
                phaser.arriveAndAwaitAdvance();
                return true;
            }
        }

        // 阶段三：
        private void showInfo() {
            for (int i = 0; i < results.size(); i++) {
                File file = new File(results.get(i));
                System.out.printf("%s: %s.\n", Thread.currentThread().getName(), file.getAbsolutePath());
            }
            phaser.arriveAndAwaitAdvance();
        }
    }

}

