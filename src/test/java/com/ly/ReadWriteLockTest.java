package com.ly;

import com.ly.java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTest {
    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    static final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public static void main(String[] args) {

        run(()->{
            readLock.lock();
            readLock.unlock();
        },"thread1");

        run(()->{
            writeLock.lock();
            writeLock.unlock();
        },"thread2");

        run(()->{
            readLock.lock();
            readLock.unlock();
        },"thread3");

        run(()->{
            writeLock.lock();
            writeLock.unlock();
        },"thread4");

        run(()->{
            readLock.lock();
            readLock.unlock();
        },"thread5");

    }


    public static void run(Runnable run, String name) {
        new Thread(run, name).start();
    }

    public static void run(Runnable run) {
        new Thread(run).start();
    }
}
