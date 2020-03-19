package com.bporcv.code.ch04;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @ClassName ThreadPoolFirstResult
 * @Description 运行多个任务并处理第一个结果
 * 同事运行多个任务，只要第一个任务的结果返回，那么其他任务就可以停止执行了
 * @Author Administrator
 * @Date 2020/3/17 23:12
 * @Version 1.0
 */
public class ThreadPoolFirstResult {

    public static void main(String[] args) {
        String username = "test";
        String password = "test";
        UserValidator idapValidator = new UserValidator("LDAP");
        UserValidator dbValidator = new UserValidator("DataBase");
        TaskValidator idapTask = new TaskValidator(idapValidator, username, password);
        TaskValidator dbTask = new TaskValidator(dbValidator, username, password);
        List<TaskValidator> taskList = new ArrayList<>();
        taskList.add(idapTask);
        taskList.add(dbTask);
        ExecutorService executor = Executors.newCachedThreadPool();
        String result;
        try {
            // 返回第一个完成任务并且没有抛出异常的任务的执行结果
            result = executor.invokeAny(taskList);
            System.out.printf("Main: Result: %s\n",result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        System.out.printf("Main: End of the Execution\n");
    }


    public static class UserValidator {
        private String name;

        public UserValidator(String name) {
            this.name = name;
        }

        public boolean validate(String name,String password){
            Random random = new Random();
            try {
                long duration = (long)(Math.random() * 10);
                System.out.printf("Validator %s: Validating a user during %d seconds\n",this.name,duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return random.nextBoolean();
        }

        public String getName() {
            return name;
        }

    }

    public static class TaskValidator implements Callable<String> {
        private UserValidator userValidator;

        private String user;
        private String password;

        public TaskValidator(UserValidator userValidator, String user, String password) {
            this.userValidator = userValidator;
            this.user = user;
            this.password = password;
        }

        @Override
        public String call() throws Exception {
            if (!userValidator.validate(user, password)) {
                System.out.printf("%s: The user has not been found\n",userValidator.getName());
                throw new Exception("Error validating user");
            }
            System.out.printf("%s: The user has been found\n", userValidator.getName());
            return userValidator.getName();
        }
    }
}
