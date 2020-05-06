package com.ly;


import com.ly.java.util.concurrent.locks.AbstractQueuedSynchronizer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {

    final static ReentrantLock lock = new ReentrantLock();

    final static Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
//        try{
//            lock.lock();
//        }finally {
//            lock.unlock();
//        }

        condition.await();

//        final Sync sync = new Sync();
//        new Thread(() -> {
//            sync.acquire(1);
//            System.out.println("threadA...");
//            sync.release(1);
//        }).start();
//
//        new Thread(() -> {
//            sync.acquire(1);
//            System.out.println("threadB...");
//            sync.release(1);
//
//        }).start();
//
//        new Thread(() -> {
//            sync.acquire(1);
//            System.out.println("threadC...");
//            sync.release(1);
//        }).start();

    }

    static class Sync extends AbstractQueuedSynchronizer {

        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                //getExclusiveOwnerThread属于
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }


        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
    }


}
