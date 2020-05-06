package com.ly;


import com.ly.java.util.concurrent.locks.Condition;
import com.ly.java.util.concurrent.locks.ReentrantLock;

public class ConditionTest {
    final static ReentrantLock lock = new ReentrantLock();
    final static Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            try{
                lock.lock();
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
        condition.signal();

    }
}
