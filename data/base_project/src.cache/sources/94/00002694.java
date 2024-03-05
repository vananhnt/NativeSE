package java.util.concurrent;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Delayed;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DelayQueue.class */
public class DelayQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public /* bridge */ /* synthetic */ boolean add(Object x0) {
        return add((DelayQueue<E>) ((Delayed) x0));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Queue
    public /* bridge */ /* synthetic */ boolean offer(Object x0) {
        return offer((DelayQueue<E>) ((Delayed) x0));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.BlockingQueue
    public /* bridge */ /* synthetic */ boolean offer(Object x0, long x1, TimeUnit x2) throws InterruptedException {
        return offer((DelayQueue<E>) ((Delayed) x0), x1, x2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.BlockingQueue
    public /* bridge */ /* synthetic */ void put(Object x0) throws InterruptedException {
        put((DelayQueue<E>) ((Delayed) x0));
    }

    public DelayQueue() {
        throw new RuntimeException("Stub!");
    }

    public DelayQueue(Collection<? extends E> c) {
        throw new RuntimeException("Stub!");
    }

    public boolean add(E e) {
        throw new RuntimeException("Stub!");
    }

    public boolean offer(E e) {
        throw new RuntimeException("Stub!");
    }

    public void put(E e) {
        throw new RuntimeException("Stub!");
    }

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

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
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

    @Override // java.util.concurrent.BlockingQueue
    public int remainingCapacity() {
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

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: DelayQueue$Itr.class */
    private class Itr implements Iterator<E> {
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
            DelayQueue.this.removeEQ(this.array[this.lastRet]);
            this.lastRet = -1;
        }
    }
}