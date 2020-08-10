package com.ly;


import com.ly.java.util.concurrent.locks.AbstractQueuedSynchronizer;
import com.ly.java.util.concurrent.locks.ReentrantLock;


public class ReentrantLockTest {

    final static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        lock.lock();

        new Thread(() -> {
            lock.lock();
            System.out.println("threadA...");
            lock.unlock();
        }).start();

        new Thread(() -> {
            lock.lock();
            System.out.println("threadB...");
            lock.unlock();

        }).start();

        new Thread(() -> {
            lock.lock();
            System.out.println("threadC...");
            lock.unlock();
        }).start();

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
