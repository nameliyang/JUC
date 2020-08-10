package com.ly;

import com.ly.java.util.concurrent.locks.AbstractOwnableSynchronizer;
import com.ly.java.util.concurrent.locks.Node;
import com.ly.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.util.concurrent.locks.LockSupport;

public class SimpleAQS extends AbstractOwnableSynchronizer {

    private volatile int state;
    private volatile Node head;
    private volatile Node tail;

    /**
     * @param arg
     */
    public final void acquire(int arg) {
        Node node = new Node(Thread.currentThread(), Node.EXCLUSIVE);
        if (!tryAcquire(arg) &&
                acquireQueued(addWaiter(node), arg)) {
            selfInterrupt();
        }
    }

    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            unParkSuccessor(h);
            return true;
        }
        return false;
    }


    public void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    /**
     * @param node
     * @return
     */
    private Node addWaiter(Node node) {
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }

    /**
     * @param node
     * @return
     */
    private Node enq(Node node) {
        for (; ; ) {
            Node t = tail;
            if (t == null) {
                if (compareAndSetHead(new Node())) {
                    tail = head;
                }
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }

    /**
     * interrupter return true
     * @param node
     * @param arg
     * @return
     */
    public boolean acquireQueued(Node node, int arg) {
        boolean interrupted = false;
        for (; ; ) {
            Node p = node.prev;
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node)) {
                LockSupport.park(this);
                if (Thread.interrupted()) {
                    interrupted = true;
                }
            }
        }
    }

    private boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL) {
            return true;
        }
        if (ws <= 0) {
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

    /**
     * @param node
     */
    private void unParkSuccessor(Node node) {
        int ws = node.waitStatus;
        if (ws < 0) {
            compareAndSetWaitStatus(node, ws, 0);
        }
        Node s = node.next;
        if (s != null) {
            LockSupport.unpark(s.thread);
        }
    }

    public void setHead(Node node) {
        this.head = node;
        node.thread = null;
        node.prev = null;
    }

    /**
     * @param arg
     * @return
     */
    public boolean tryAcquire(int arg) {
        Thread thread = Thread.currentThread();
        int state = getState();
        if (state == 0) {
            if (compareAndSetState(0, arg)) {
                setExclusiveOwnerThread(thread);
                return true;
            }
        } else if (thread == getExclusiveOwnerThread()) {
            setState(getState() + arg);
            return true;
        }
        return false;
    }

    /**
     * @param arg
     * @return
     */
    public boolean tryRelease(int arg) {
        int state = getState() - arg;
        boolean free = false;
        if (state == 0) {
            free = true;
            setExclusiveOwnerThread(null);
        }
        setState(state);
        return free;
    }


    protected final boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    private static final Unsafe unsafe = UnsafeUtil.UNSAFE;
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            stateOffset = unsafe.objectFieldOffset
                    (SimpleAQS.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset
                    (SimpleAQS.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset
                    (SimpleAQS.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset
                    (Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset
                    (Node.class.getDeclaredField("next"));

        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    /**
     * CAS head field. Used only by enq.
     */
    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    /**
     * CAS tail field. Used only by enq.
     */
    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    /**
     * CAS waitStatus field of a node.
     */
    private static final boolean compareAndSetWaitStatus(Node node,
                                                         int expect,
                                                         int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset,
                expect, update);
    }

    /**
     * CAS next field of a node.
     */
    private static final boolean compareAndSetNext(Node node,
                                                   Node expect,
                                                   Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }



}
