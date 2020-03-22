package com.bporcv.code.ch06.atomicVariable;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName AtomicLong
 * @Description // TODO
 * @Author Administrator
 * @Date 2020/3/22 9:43
 * @Version 1.0
 */
public class AtomicLongCase {

    public static void main(String[] args) {

        Account account = new Account();
        account.setBalance(1000);
        Company company = new Company(account);
        Bank bank = new Bank(account);
        Thread companyThread = new Thread(company);
        Thread bankThread = new Thread(bank);
        System.out.printf("Account: Initial Balance: %d\n", account.getBalance());
        companyThread.start();
        bankThread.start();

        try {
            companyThread.join();
            bankThread.join();
            System.out.printf("Account: Final Balance: %d\n", account.getBalance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static class Account {
        private AtomicLong balance;

        public Account() {
            balance = new AtomicLong();
        }

        public long getBalance() {
            return balance.get();
        }

        public void setBalance(long amount) {
            balance.getAndSet(amount);
        }

        public void addAmount(long amount) {
            balance.getAndAdd(amount);
        }

        public void subtractAmount(long amount) {
            balance.getAndAdd(-amount);
        }

    }


    public static class Company implements Runnable {
        private Account account;

        public Company(Account account) {
            this.account = account;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                account.addAmount(1000);
            }
        }
    }

    public static class Bank implements Runnable {
        private Account account;

        public Bank(Account account) {
            this.account = account;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                account.subtractAmount(1000);
            }
        }
    }

}
