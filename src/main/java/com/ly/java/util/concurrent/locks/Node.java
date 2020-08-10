package com.ly.java.util.concurrent.locks;

public class Node {

    public static final Node SHARED = new Node();

    public static final Node EXCLUSIVE = null;

    public static final int CANCELLED = 1;

    public static final int SIGNAL = -1;

    public static final int CONDITION = -2;

    public static final int PROPAGATE = -3;

    public volatile int waitStatus;

    public volatile Node prev;

    public volatile Node next;

    public volatile Thread thread;

//    public volatile Thread oldThread;

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
//        this.oldThread = this.thread;
    }

    public Node(Thread thread, int waitStatus) {
        this.waitStatus = waitStatus;
        this.thread = thread;
//        this.oldThread = this.thread;
    }

    @Override
    public String toString() {
        return thread == null ? "空" : thread.getName();
    }
}
