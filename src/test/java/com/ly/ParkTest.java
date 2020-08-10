package com.ly;

import java.util.concurrent.locks.LockSupport;

public class ParkTest {

    public static void main(String[] args) {
        LockSupport.unpark(Thread.currentThread());
        LockSupport.park("test");

    }


}
