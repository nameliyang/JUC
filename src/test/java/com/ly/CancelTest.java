package com.ly;

import com.ly.java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.locks.LockSupport;

public class CancelTest {

    public static void main(String[] args) {
      /*  ReentrantLock lock = new ReentrantLock();

        Thread thread1 =new Thread(()->{
            try{
                lock.lock();
                System.out.println("thread1");
            }finally {
                lock.unlock();
            }
        },"thread1");
        thread1.start();

        Thread thread2 =new Thread(()->{
            try{
                lock.lock();
                System.out.println("thread2");
            }finally {
                lock.unlock();
            }
        },"thread2");
        thread2.start();


        thread2.interrupt();
*/
        System.out.println(test());
    }

    public static int test(){
        int v = 50;
        try{
            return v;
        }finally {
            System.out.println("hello");
            v = 23;
        }
    }


}
