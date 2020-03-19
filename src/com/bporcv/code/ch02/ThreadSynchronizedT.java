package com.bporcv.code.ch02;

/**
 * @ClassName ThreadSynchronizedT
 * @Description 使用非依赖属性实现同步
 * @Author Administrator
 * @Date 2020/3/16 19:38
 * @Version 1.0
 */
public class ThreadSynchronizedT {

    public static void main(String[] args) {
        Cinema cinema = new Cinema();
        TicketOffice1 office1 = new TicketOffice1(cinema);
        Thread thread1 = new Thread(office1,"TicketOffice1");
        TicketOffice2 office2 = new TicketOffice2(cinema);
        Thread thread2 = new Thread(office2,"TicketOffice2");
        thread1.start();
        thread2.start();

        try {
            // 等待两个线程运行结束
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Room 1 Vacancies: %d\n",cinema.getVacanciesCinema1());
        System.out.printf("Room 2 Vacancies: %d\n",cinema.getVacanciesCinema2());

    }


    public static class Cinema {
        private long vacanciesCinema1;
        private long vacanciesCinema2;
        private final Object controlCinema1;
        private final Object controlCinema2;


        public Cinema() {
            controlCinema1 = new Object();
            controlCinema2 = new Object();
            vacanciesCinema1 = 20;
            vacanciesCinema2 = 20;
        }

        // 当第一个电影院有票卖出的时候调用这个方法
        public boolean sellTickets1(int number) {
            synchronized (controlCinema1) {
                if (number < vacanciesCinema1) {
                    vacanciesCinema1 -= number;
                    return true;
                } else {
                    return false;
                }
            }
        }

        // 当第二个电影院有票卖出的时候调用这个方法
        public boolean sellTickets2(int number) {
            synchronized (controlCinema2) {
                if (number < vacanciesCinema2) {
                    vacanciesCinema2 -= number;
                    return true;
                } else {
                    return false;
                }
            }
        }

        // 当第一个电影院有票退回来的时候调用这个方法
        public boolean returnTickets1(int number){
            synchronized (controlCinema1){
                vacanciesCinema1 += number;
                return true;
            }
        }

        // 当第二个电影院有票退回来的时候调用这个方法
        public boolean returnTickets2(int number){
            synchronized (controlCinema2){
                vacanciesCinema2 += number;
                return true;
            }
        }

        // 返回第一个电影院的票
        public long getVacanciesCinema1() {
            return vacanciesCinema1;
        }

        // 返回第二个电影院的票
        public long getVacanciesCinema2() {
            return vacanciesCinema2;
        }
    }

    public static class TicketOffice1 implements Runnable{
        private Cinema cinema;

        public TicketOffice1(Cinema cinema) {
            this.cinema = cinema;
        }

        @Override
        public void run() {
            cinema.sellTickets1(3);
            cinema.sellTickets1(2);
            cinema.sellTickets2(2);
            cinema.returnTickets1(3);
            cinema.sellTickets1(5);
            cinema.sellTickets2(2);
            cinema.sellTickets2(2);
            cinema.sellTickets2(2);
        }
    }

    public static class TicketOffice2 implements Runnable{
        private Cinema cinema;

        public TicketOffice2(Cinema cinema) {
            this.cinema = cinema;
        }

        @Override
        public void run() {
            cinema.sellTickets2(2);
            cinema.sellTickets2(4);
            cinema.sellTickets1(2);
            cinema.sellTickets1(1);
            cinema.returnTickets2(2);
            cinema.sellTickets1(3);
            cinema.sellTickets2(2);
            cinema.sellTickets1(2);
        }
    }

}
