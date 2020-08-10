package com.ly.java.util.concurrent.locks;


public abstract class AbstractOwnableSynchronizer implements java.io.Serializable {

    /**
     *  不具有内存可见性！！！ why???
     *  exclusiveOwnerThread标志 "拥有独占锁线程"。
     *  当判断当前线程是否为‘独占锁线程’(current == getExclusiveOwnerThread())时
     *  调用setExclusiveOwnerThread方法时具有‘线程排它性' 设置后 其他线程将无法写入修改（除非释放锁,重置exclusiveOwnerThread为NULL）
     *  当exclusiveOwnerThread被其他线程修改时  当前线程getExclusiveOwnerThread只能为null或其他线程  因为若返回为当前线程
     *   exclusiveOwnerThread为当前线程  然而setExclusiveOwnerThread具有排他性 所以不可能其他线程能修改exclusiveOwnerThread 也就是
     *  说exclusiveOwnerThread被其他线程修改时  不可能返回为当前线程   当比较current线程与 getExclusiveOwnerThread时 可见性问题无法影响结果。
     */
    private Thread exclusiveOwnerThread;

    final public void setExclusiveOwnerThread(Thread thread) {
        this.exclusiveOwnerThread = thread;
    }

    final public Thread getExclusiveOwnerThread() {
        return this.exclusiveOwnerThread;
    }

}
