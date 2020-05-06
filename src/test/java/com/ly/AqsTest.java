package com.ly;

import com.ly.java.util.concurrent.locks.Node;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class AqsTest {

    volatile Node head;

    volatile Node tail;

    AtomicReferenceFieldUpdater<AqsTest, Node> headUpdate = AtomicReferenceFieldUpdater.newUpdater(AqsTest.class, Node.class, "head");

    AtomicReferenceFieldUpdater<AqsTest, Node> tailUpdate = AtomicReferenceFieldUpdater.newUpdater(AqsTest.class, Node.class, "tail");

    /**
     *
     * @param node
     * @return
     */
    public Node enq(Node node) {
        for (; ; ) {
            Node t = tail;
            if (t == null) {
                if (headUpdate.compareAndSet(this, null, new Node())) {
                    tail = head;
                }
            } else {
                node.prev = t;
                if (tailUpdate.compareAndSet(this, t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }

    public static void main(String[] args) {

    }
}
