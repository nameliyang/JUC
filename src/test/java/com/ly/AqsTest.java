package com.ly;

import java.util.concurrent.locks.ReentrantLock;

public class AqsTest {

    static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        lock.lock();

        new Thread(()->{
            lock.lock();
            System.out.println("thread run");
            lock.unlock();
        }).start();

        lock.unlock();

    }

}
