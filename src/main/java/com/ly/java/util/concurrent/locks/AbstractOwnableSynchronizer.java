package com.ly.java.util.concurrent.locks;


public abstract class AbstractOwnableSynchronizer implements java.io.Serializable{

    /**
     * 不具有内存可见性
     */
    private Thread exclusiveOwnerThread;

    final public  void setExclusiveOwnerThread(Thread thread){
        this.exclusiveOwnerThread = thread;
    }

    final public Thread getExclusiveOwnerThread(){
        return this.exclusiveOwnerThread;
    }

}
