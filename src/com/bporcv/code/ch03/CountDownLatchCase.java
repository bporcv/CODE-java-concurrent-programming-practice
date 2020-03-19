package com.bporcv.code.ch03;

import com.sun.org.apache.bcel.internal.generic.DUP;
import com.sun.xml.internal.fastinfoset.stax.factory.StAXOutputFactory;

import javax.swing.text.ViewFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName CountDownLatchCase
 * @Description 等待多个并发事件的完成
 * @Author Administrator
 * @Date 2020/3/16 22:55
 * @Version 1.0
 */
public class CountDownLatchCase {

    public static void main(String[] args) {
        VideoConference conference = new VideoConference(10);
        Thread threadConference = new Thread(conference);
        threadConference.start();
        for (int i = 0; i < 10; i++) {
            Participant participant = new Participant(conference, "Participant " + i);
            new Thread(participant).start();
        }

    }

    public static class VideoConference implements Runnable {
        private final CountDownLatch countDownLatch;

        public VideoConference(int number) {
            countDownLatch = new CountDownLatch(number);
        }


        public void arrive(String name){
            System.out.printf("%s has arrived.\n",name);
            countDownLatch.countDown();
            System.out.printf("VideoConference: Waiting for %d participants.\n",countDownLatch.getCount());
        }


        @Override
        public void run() {
            System.out.printf("VideoConference: Initialization: %d participants\n",countDownLatch.getCount());
            try {
                countDownLatch.await();
                System.out.printf("VideoConference: All the  participants have come\n");
                System.out.printf("VideoConference: Let's start...\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Participant implements Runnable{

        private VideoConference conference;

        private String name;

        public Participant(VideoConference conference, String name) {
            this.conference = conference;
            this.name = name;
        }

        @Override
        public void run() {
            long duration = (long)(Math.random() * 10);
            try {
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            conference.arrive(name);
        }
    }

}
