package com.ly.java.util.concurrent;


import com.ly.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> implements Queue<E> {

    volatile Node<E> head;

    volatile Node<E> tail;

    public ConcurrentLinkedQueue() {
        head = tail = new Node<>(null);
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    class Itr implements Iterator<E> {

        private Node<E> nextNode;

        private E nextItem;

        private Node<E> lastRet;

        public Itr(){
            advance();
        }

        private E advance(){
            lastRet = nextNode;
            E x = nextItem;
            Node<E> pred,p ;
            if(nextNode == null){
                p = first();
                pred = null;
            }else{
                pred = nextNode;
                p = succ(nextNode);
            }
            for(;;){
                if(p == null){
                    nextNode = null;
                    nextItem = null;
                    return x;
                }
                E item = p.item;
                if(item != null){
                    nextNode = p;
                    nextItem = item;
                    return x;
                }else{
                    Node<E> next = succ(p);
                    if(pred!=null && next!=null){
                        pred.casNext(p,next);
                    }
                    p = next;
                }
            }
        }


        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public E next() {
            if(nextNode == null) throw new NoSuchElementException();
            return advance();
        }
    }


    /**
     * @param
     * @return
     */
    public Node<E> succ(Node<E> p) {
        Node<E> next = p.next;
        return (p == next) ? head : next;
    }

    @Override
    public int size() {
        int count = 0;
        for (Node<E> p = first(); p != null; p = succ(p)) {
            if (p.item != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        return first() == null;
    }

    @Override
    public boolean remove(Object o) {
        if (o != null) {
            Node<E> next, pred = null;
            for (Node<E> p = first(); p != null; pred = p, p = next) {
                boolean removed = false;
                E item = p.item;
                if (item != null) {
                    if (!o.equals(item)) {
                        next = succ(p);
                        continue;
                    }
                    removed = p.casItem(item, null);
                }
                next = succ(p);
                if(pred!=null && next!=null){
                    pred.casNext(p, next);
                }
                if (removed) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return
     */
    private Node<E> first() {
        restart:
        for (; ; ) {
            for (Node<E> h = head, p = h, q; ; ) {
                E item = p.item;
                if (item != null) {
                    return p;
                } else if ((q = p.next) == null) {
                    updateHead(h, p);
                    return null;
                } else if (p == q) {
                    continue restart;
                } else {
                    p = q;
                }
            }
        }

    }

    @Override
    public boolean offer(E e) {
        Node<E> newNode = new Node<>(e);
        for (Node<E> t = tail, p = t; ; ) {
            Node<E> q = p.next;
            if (q == null) {
                // p.next==null表示为尾节点,
                if (p.next == null && p.casNext(null, newNode)) {
                    //当p!=t时 尝试更新节点
                    if (p != t) {
                        casTail(t, newNode);
                    }
                    return true;
                }
            } else if (p == q) {
                p = (t != (t = tail)) ? tail : head;
            } else {
                p = (p != t && t != (t = tail)) ? t : q;
            }
        }
    }

    public void updateHead(Node<E> h, Node<E> p) {
        if (h != p && casHead(h, p)) {
            p.lazySetNext(p);
        }
    }

    @Override
    public E poll() {
        restart:
        for (; ; ) {
            for (Node<E> h = head, p = h; ; ) {
                E item = p.item;
                Node<E> q = p.next;
                if (item != null && p.casItem(item, null)) {
                    updateHead(h, (q == null ? p : q));
                    return item;
                } else if (q == null) {
                    updateHead(h, p);
                    return null;
                } else if (p == q) {
                    continue restart;
                } else {
                    p = q;
                }
            }
        }

    }

    @Override
    public E peek() {
        restartFromHead:
        for (; ; ) {
            for (Node<E> h = head, p = h, q; ; ) {
                E item = p.item;
                if (item != null || (q = p.next) == null) {
                    updateHead(h, p);
                    return item;
                } else if (p == q)
                    continue restartFromHead;
                else
                    p = q;
            }
        }
    }

    public static void main(String[] args) {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < 3; i++) {
            final int tmp = i;
            new Thread(() -> {
                queue.offer("hello"+tmp);
            }).start();
        }
        Iterator<String> iterator = queue.iterator();
        while(iterator.hasNext()){
            String next = iterator.next();
            System.out.println(next);
        }
    }


    private boolean casTail(Node<E> cmp, Node<E> val) {
        return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
    }

    private boolean casHead(Node<E> cmp, Node<E> val) {
        return UNSAFE.compareAndSwapObject(this, headOffset, cmp, val);
    }

    private static final Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;

    static {
        try {
            UNSAFE = UnsafeUtil.UNSAFE;
            Class<?> k = ConcurrentLinkedQueue.class;
            headOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("tail"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}

