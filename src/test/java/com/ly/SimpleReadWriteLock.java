package com.ly;

import com.ly.java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class SimpleReadWriteLock {


    public static void main(String[] args) {
        System.out.println((1 << 2) - 1);
    }


    static abstract class Sync extends AbstractQueuedSynchronizer {

        static final int SHARED_SHIFT = 16;
        static final int EXCLUSIVE_MASK = (1 << 16) - 1;

        static int sharedCount(int c) {
            return c >>> SHARED_SHIFT;
        }

        static int exclusiveCount(int c) {
            return c & EXCLUSIVE_MASK;
        }


        public final boolean tryRelease(int releases) {
            if (!isHeldExclusively()) {
                throw new IllegalMonitorStateException();
            }
            int next = getState() - releases;
            boolean free = exclusiveCount(next) == 0;
            if (free) {
                setExclusiveOwnerThread(null);
            }
            setState(next);
            return free;
        }


        /**
         * @param acquires
         * @return
         */
        public final boolean tryAcquire(int acquires) {
            int c = getState();
            Thread current = Thread.currentThread();
            // c>0表示存在读写锁 若存在读锁或者独占锁非当前线程 则获取写锁失败
            int w = exclusiveCount(c);
            if (c > 0) {
                if (w == 0 || current != getExclusiveOwnerThread()) {
                    return false;
                }
                setState(c + acquires);
            }

            if (writerShouldBlock() || !compareAndSetState(c, c + acquires)) {
                return false;
            }
            setExclusiveOwnerThread(current);
            return true;
        }


        public abstract boolean writerShouldBlock();

        public abstract boolean readShouldBlock();


        public final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

    }


}
