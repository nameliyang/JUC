package com.ly.java.util.concurrent.locks;

public class Node {

    static final Node SHARED = new Node();

    static final Node EXCLUSIVE = null;

    static final int CANCELLED = 1;

    static final int SIGNAL = -1;

    static final int CONDITION = -2;

    static final int PROPAGATE = -3;

    volatile int waitStatus;

    public volatile Node prev;

    public volatile Node next;

    public volatile Thread thread;

    public volatile Thread oldThread;

    Node nextWaiter;


    final boolean isShared() {
        return nextWaiter == SHARED;
    }

    final Node predecessor() throws NullPointerException {
        Node p = prev;
        if (p == null) {
            throw new NullPointerException();
        } else {
            return p;
        }
    }

    public Node() {
    }

    public Node(Thread thread, Node mode) {
        this.nextWaiter = mode;
        this.thread = thread;
        this.oldThread = this.thread;
    }

    public Node(Thread thread, int waitStatus) {
        this.waitStatus = waitStatus;
        this.thread = thread;
        this.oldThread = this.thread;
    }

    @Override
    public String toString() {
        return thread==null?"空-"+oldThread:thread.getName()+"-"+oldThread.getName();
    }
}
