package com.bporcv.code.ch02;

import java.time.temporal.Temporal;

/**
 * @ClassName ThreadSynchronized
 * @Description 使用synchronized实现同步方法
 * @Author Administrator
 * @Date 2020/3/16 0:03
 * @Version 1.0
 */
public class ThreadSynchronized {

    public static void main(String[] args) {
        Account account = new Account();
        account.setBalance(1000);
        Company company = new Company(account);
        Thread companyThread = new Thread(company);
        Bank bank = new Bank(account);
        Thread bankThread = new Thread(bank);
        System.out.printf("Account: Initial Balance: %f\n",account.getBalance());
        companyThread.start();
        bankThread.start();

        try {
            companyThread.join();
            bankThread.join();
            System.out.printf("Account: Final Balance: %f\n",account.getBalance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    static class Account {
        private double balance;

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public synchronized void addAmount(double amount){
            double tmp = balance;
            try {
                Thread.sleep(10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            tmp += amount;
            balance = amount;
        }

        public synchronized void subtractAmount(double amount){
            double tmp = balance;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tmp -= amount;
            balance = tmp;
        }

    }

    static class Bank implements Runnable{
        private Account account;

        public Bank(Account account) {
            this.account = account;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                account.subtractAmount(1000);
            }
        }
    }

    static class Company implements Runnable {
        private Account account;

        public Company(Account account) {
            this.account = account;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                account.addAmount(1000);
            }
        }
    }
}


