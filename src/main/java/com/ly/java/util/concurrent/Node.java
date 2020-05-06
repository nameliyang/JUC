package com.ly.java.util.concurrent;

import com.ly.util.UnsafeUtil;

public class Node<E> {

    public volatile E item;

    public volatile Node<E> next;

    public Node<E> getNext() {
        return this.next;
    }

    /**
     * Constructs a new node.  Uses relaxed write because item can
     * only be seen after publication via casNext.
     */
    public Node(E item) {
        UNSAFE.putObject(this, itemOffset, item);
    }

    boolean casItem(E cmp, E val) {
        return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
    }

    void lazySetNext(Node<E> val) {
        UNSAFE.putOrderedObject(this, nextOffset, val);
    }

    public boolean casNext(Node<E> cmp, Node<E> val) {
        boolean suc = UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        return suc;
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long itemOffset;
    private static final long nextOffset;

    @Override
    public String toString() {
        return item == null ? "Null" : item.toString();
    }

    static {
        try {
            UNSAFE = UnsafeUtil.UNSAFE;
            Class<?> k = Node.class;
            itemOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("item"));
            nextOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("next"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}