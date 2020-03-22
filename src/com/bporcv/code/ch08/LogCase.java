package com.bporcv.code.ch08;


import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

/**
 * @ClassName LogCase
 * @Description 输出高效的日志信息
 * @Author Administrator
 * @Date 2020/3/22 23:25
 * @Version 1.0
 */
public class LogCase {

    public static void main(String[] args) {
        Logger logger = MyLogger.getLogger("Core");
        logger.entering("Core", "main()", args);
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            logger.log(Level.INFO, "Launching thread:" + i);
            Task task = new Task();
            threads[i] = new Thread(task);
            logger.log(Level.INFO, "Thread created: " + threads[i].getName());
            threads[i].start();
        }
        logger.log(Level.INFO, "Five Threads created.Waiting fo its finalization");
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
                logger.log(Level.INFO, "Thread has finished its execution", threads[i]);
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, "Exception", e);
            }

        }
        logger.exiting("Core","main()");

    }

    public static class MyFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append("[" + record.getLevel() + "] - ");
            sb.append(new Date(record.getMillis()) + " :");
            sb.append(record.getSourceClassName() + "." + record.getSourceMethodName() + " : ");
            sb.append(record.getMessage() + "\n");
            return sb.toString();
        }
    }

    public static class MyLogger {
        private static Handler handler;

        public static Logger getLogger(String name) {
            Logger logger = Logger.getLogger(name);
            logger.setLevel(Level.ALL);
            try {
                if (handler == null) {
                    handler = new FileHandler("./logs/recipe8.log");
                    Formatter formatter = new MyFormatter();
                    handler.setFormatter(formatter);
                }
                if (logger.getHandlers().length == 0) {
                    logger.addHandler(handler);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return logger;
        }
    }

    public static class Task implements Runnable {

        @Override
        public void run() {
            Logger logger = MyLogger.getLogger(this.getClass().getName());
            logger.entering(Thread.currentThread().getName(), "run()");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.exiting(Thread.currentThread().getName(), "run()", Thread.currentThread());
        }

    }
}

