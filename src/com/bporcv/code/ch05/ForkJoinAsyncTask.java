package com.bporcv.code.ch05;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ForkJoinAsyncTask
 * @Description 异步任务
 * 同步任务：join(),get(),采用工作窃取算法
 * 异步任务：fork(),不采用工作窃取算法
 * @Author Administrator
 * @Date 2020/3/19 22:33
 * @Version 1.0
 */
public class ForkJoinAsyncTask {

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        FolderProcessor system = new FolderProcessor("C:\\Windows", "log");
        FolderProcessor apps = new FolderProcessor("C:\\Program Files", "log");
        FolderProcessor documents = new FolderProcessor("C:\\Documents And Settings", "log");
        pool.execute(system);
        pool.execute(apps);
        pool.execute(documents);
        do {
            System.out.printf("*************************************************\n");
            System.out.printf("Main: Parallelism: %d\n",pool.getParallelism());
            System.out.printf("Main: Active Threads: %d\n",pool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n",pool.getQueuedTaskCount());
            System.out.printf("Main: Steal Count: %d\n",pool.getStealCount());
            System.out.printf("*************************************************\n");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while ((!system.isDone()) || (!apps.isDone()) || (!documents.isDone()));
        pool.shutdown();
        List<String> results;

        results = system.join();
        System.out.printf("System: %d files found\n",results.size());
        results = apps.join();
        System.out.printf("Apps: %d files found\n",results.size());
        results = documents.join();
        System.out.printf("Documents And Settings: %d files found\n",results.size());
    }

    public static class FolderProcessor extends RecursiveTask<List<String>> {

        private static final long serialVersionUID = 7880779961899182637L;

        private String path;
        private String extension;

        public FolderProcessor(String path, String extension) {
            this.path = path;
            this.extension = extension;
        }

        @Override
        protected List<String> compute() {
            List<String> list = new ArrayList<>();
            List<FolderProcessor> tasks = new ArrayList<>();

            // 获取文件夹的内容
            File file = new File(path);
            File[] content = file.listFiles();
            // 对于文件夹中的每一个元素，如果它是子文件夹，就创建一个新的FolderProcessor对象，然后调用fork()方法采用异步方式执行他
            if (content != null) {
                for (int i = 0; i < content.length; i++) {
                    if (content[i].isDirectory()) {
                        FolderProcessor task = new FolderProcessor(content[i].getAbsolutePath(), extension);
                        // 发送子任务到线程池中，因为是异步的，所以会立即返回
                        task.fork();
                        tasks.add(task);
                    } else {
                        if (checkFile(content[i].getName())) {
                            list.add(content[i].getAbsolutePath());
                        }
                    }
                }
            }
            if (tasks.size() > 50) {
                System.out.printf("%s: %d tasks ran.\n", file.getAbsolutePath(), tasks.size());
            }

            addResultFromTasks(list, tasks);
            return list;
        }

        private void addResultFromTasks(List<String> list, List<FolderProcessor> tasks) {
            for (FolderProcessor task : tasks) {
                // 主任务处理完成，等待发送到线程池中的子任务处理完成，
                list.addAll(task.join());
            }
        }

        private boolean checkFile(String name) {
            return name.endsWith(extension);
        }
    }

}
