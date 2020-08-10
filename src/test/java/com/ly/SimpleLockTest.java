package com.ly;

import com.ly.java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class SimpleLockTest {
    static int count = 0;

    public static void main(String[] args) {

        final SimpleLock lock = new SimpleLock();

        new Thread(()->{
            lock.lock();
            System.out.println("thread1");
            lock.unLock();
        }).start();

        new Thread(()->{
            lock.lock();
            System.out.println("thread2");
            lock.unLock();
        }).start();

        new Thread(()->{
            lock.lock();
            System.out.println("thread3");
            lock.unLock();
        }).start();
    }

    static class SimpleLock extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int arg) {
            Thread thread = Thread.currentThread();
            if (thread == getExclusiveOwnerThread()) {
                setState(getState() + 1);
                return true;
            } else if (compareAndSetState(0, arg)) {
                setExclusiveOwnerThread(thread);
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            int state = getState() - arg;
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            if (state == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(state);
            return free;
        }

        public void lock() {
            this.acquire(1);
        }

        public void unLock() {
            this.release(1);
        }

    }


}
