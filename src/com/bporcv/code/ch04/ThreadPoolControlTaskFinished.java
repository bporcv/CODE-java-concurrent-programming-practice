package com.bporcv.code.ch04;

import java.lang.reflect.Executable;
import java.util.concurrent.*;

/**
 * @ClassName ThreadPoolControlTaskFinished
 * @Description 在执行器中控制任务的完成
 * @Author Administrator
 * @Date 2020/3/18 21:23
 * @Version 1.0
 */
public class ThreadPoolControlTaskFinished {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        ResultTask[] tasks = new ResultTask[5];
        for (int i = 0; i < 5; i++) {
            ExecutableTask executableTask = new ExecutableTask("Task " + i);
            tasks[i] = new ResultTask(executableTask);
            executor.submit(tasks[i]);
        }
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 5; i++) {
            try {
                if (!tasks[i].isCancelled()){
                    System.out.printf("%s\n",tasks[i].get());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

    }

    public static class ExecutableTask implements Callable<String> {

        private String name;

        public ExecutableTask(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String call() throws Exception {
            try {
                long duration = (long)(Math.random() * 10);
                System.out.printf("%s: Waiting %d seconds for results.\n",this.name,duration);
                TimeUnit.SECONDS.sleep(duration);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return "Hello,world.I'm " + name;
        }

    }

    public static class ResultTask extends FutureTask<String>{

        private String name;

        public ResultTask(Callable<String> callable) {
            super(callable);
            this.name = ((ExecutableTask)callable).getName();
        }

        @Override
        protected void done() {
            if (isCancelled()){
                System.out.printf("%s: Has been canceled\n",name);
            } else {
                System.out.printf("%s: Has finished\n",name);
            }
        }
    }


}
