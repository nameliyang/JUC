package com.ly;

import com.ly.java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.locks.LockSupport;

public class CancelTest {

    public static void main(String[] args) {

        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        Thread thread0 =new Thread(()->{
            try{
                lock.lock();
                System.out.println("thread1");
            }finally {
                lock.unlock();
            }
        },"thread0");
        thread0.start();

        Thread thread1 =new Thread(()->{
            try{
                lock.lockInterruptibly();
                System.out.println("thread1");
            }catch(Exception e){
                System.out.println(e);
            }finally {
                lock.unlock();
            }
        },"thread1");
        thread1.start();

        Thread thread2 =new Thread(()->{
            try{
                lock.lockInterruptibly();
                System.out.println("thread2");
            }catch(Exception e){
                System.out.println(e);
            }finally {
                lock.unlock();
            }
        },"thread2");
        thread2.start();


        thread1.interrupt();
        lock.unlock();
    }


}
