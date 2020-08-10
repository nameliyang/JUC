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
                System.out.println("lock ...");
            } finally {
                lock.unlock();
            }
        },"thread1").start();

        Thread thread2 = new Thread(()->{
            try{
                lock.lock();
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        },"thread2");
        thread2.start();

        new Thread(()->{
            try{
                lock.lock();
                condition.signal();
            } finally {
                lock.unlock();
            }
        },"thread5").start();


//
//        new Thread(()->{
//            try{
//                lock.lock();
//                condition.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                lock.unlock();
//            }
//        },"thread3").start();

//        new Thread(()->{
//            try{
//                lock.lock();
//                condition.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                lock.unlock();
//            }
//        },"thread4").start();
//


    }
}
