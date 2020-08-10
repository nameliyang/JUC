package com.ly;

import com.ly.java.util.concurrent.locks.ReadWriteLock;
import com.ly.java.util.concurrent.locks.ReentrantReadWriteLock;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {

        ReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().lock();
        lock.readLock().unlock();

    }

}
