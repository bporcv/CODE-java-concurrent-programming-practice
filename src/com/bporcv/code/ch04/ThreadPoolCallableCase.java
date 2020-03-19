package com.bporcv.code.ch04;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @ClassName ThreadPoolCallableCase
 * @Description 在执行器中执行任务并返回结果
 * @Author Administrator
 * @Date 2020/3/17 23:01
 * @Version 1.0
 */
public class ThreadPoolCallableCase {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        List<Future<Integer>> resultList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Integer number = random.nextInt(10);
            FactorialCalculator calculator = new FactorialCalculator(number);
            Future<Integer> result = executor.submit(calculator);
            resultList.add(result);
        }

        do {
            System.out.printf("Main: Number of Completed Tasks:%d\n",executor.getCompletedTaskCount());
            for (int i = 0; i < resultList.size(); i++) {
                Future<Integer> result = resultList.get(i);
                System.out.printf("Main: Task %d: %s\n",i,result.isDone());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (executor.getCompletedTaskCount() < resultList.size());

        System.out.printf("Main: Result\n");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Integer> result = resultList.get(i);
            Integer number = null;
            try {
                number = result.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.printf("Main: Task %d: %d\n",i,number);
            executor.shutdown();
        }
    }

    public static class FactorialCalculator implements Callable<Integer> {

        private Integer number;

        public FactorialCalculator(Integer number) {
            this.number = number;
        }

        @Override
        public Integer call() throws Exception {
            int result = 1;
            if ((number == 0) || (number == 1)){
                result = 1;
            } else {
                for (Integer i = 2; i <= number; i++) {
                    result *= i;
                    TimeUnit.MILLISECONDS.sleep(20);
                }
            }
            System.out.printf("%s: %d\n",Thread.currentThread().getName(),result);
            return result;
        }
    }

}
