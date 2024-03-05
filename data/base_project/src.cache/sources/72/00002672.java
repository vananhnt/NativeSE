package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import sun.misc.Unsafe;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConcurrentLinkedQueue.class */
public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable {
    public ConcurrentLinkedQueue() {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentLinkedQueue(Collection<? extends E> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public boolean add(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public boolean offer(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public E poll() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public E peek() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public boolean addAll(Collection<? extends E> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public Object[] toArray() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public <T> T[] toArray(T[] a) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ConcurrentLinkedQueue$Node.class */
    public static class Node<E> {
        volatile E item;
        volatile Node<E> next;
        private static final Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;

        Node(E item) {
            UNSAFE.putObject(this, itemOffset, item);
        }

        boolean casItem(E cmp, E val) {
            return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
        }

        void lazySetNext(Node<E> val) {
            UNSAFE.putOrderedObject(this, nextOffset, val);
        }

        boolean casNext(Node<E> cmp, Node<E> val) {
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                itemOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /* loaded from: ConcurrentLinkedQueue$Itr.class */
    private class Itr implements Iterator<E> {
        private Node<E> nextNode;
        private E nextItem;
        private Node<E> lastRet;

        Itr() {
            advance();
        }

        private E advance() {
            Node<E> pred;
            Node<E> p;
            this.lastRet = this.nextNode;
            E x = this.nextItem;
            if (this.nextNode == null) {
                p = ConcurrentLinkedQueue.this.first();
                pred = null;
            } else {
                pred = this.nextNode;
                p = ConcurrentLinkedQueue.this.succ(this.nextNode);
            }
            while (p != null) {
                E item = p.item;
                if (item != null) {
                    this.nextNode = p;
                    this.nextItem = item;
                    return x;
                }
                Node<E> next = ConcurrentLinkedQueue.this.succ(p);
                if (pred != null && next != null) {
                    pred.casNext(p, next);
                }
                p = next;
            }
            this.nextNode = null;
            this.nextItem = null;
            return x;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.nextNode != null;
        }

        @Override // java.util.Iterator
        public E next() {
            if (this.nextNode == null) {
                throw new NoSuchElementException();
            }
            return (E) advance();
        }

        @Override // java.util.Iterator
        public void remove() {
            Node<E> l = this.lastRet;
            if (l == null) {
                throw new IllegalStateException();
            }
            l.item = null;
            this.lastRet = null;
        }
    }
}