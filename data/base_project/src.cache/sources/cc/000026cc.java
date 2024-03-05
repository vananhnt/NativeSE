package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PriorityBlockingQueue.class */
public class PriorityBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
    public PriorityBlockingQueue() {
        throw new RuntimeException("Stub!");
    }

    public PriorityBlockingQueue(int initialCapacity) {
        throw new RuntimeException("Stub!");
    }

    public PriorityBlockingQueue(int initialCapacity, Comparator<? super E> comparator) {
        throw new RuntimeException("Stub!");
    }

    public PriorityBlockingQueue(Collection<? extends E> c) {
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

    @Override // java.util.concurrent.BlockingQueue
    public void put(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public boolean offer(E e, long timeout, TimeUnit unit) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public E poll() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public E take() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public E peek() {
        throw new RuntimeException("Stub!");
    }

    public Comparator<? super E> comparator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public int remainingCapacity() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public Object[] toArray() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super E> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public void clear() {
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

    /* loaded from: PriorityBlockingQueue$Itr.class */
    final class Itr implements Iterator<E> {
        final Object[] array;
        int cursor;
        int lastRet = -1;

        Itr(Object[] array) {
            this.array = array;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.cursor < this.array.length;
        }

        @Override // java.util.Iterator
        public E next() {
            if (this.cursor >= this.array.length) {
                throw new NoSuchElementException();
            }
            this.lastRet = this.cursor;
            Object[] objArr = this.array;
            int i = this.cursor;
            this.cursor = i + 1;
            return (E) objArr[i];
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.lastRet < 0) {
                throw new IllegalStateException();
            }
            PriorityBlockingQueue.this.removeEQ(this.array[this.lastRet]);
            this.lastRet = -1;
        }
    }
}