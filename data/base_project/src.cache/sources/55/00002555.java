package java.util;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ArrayDeque.class */
public class ArrayDeque<E> extends AbstractCollection<E> implements Deque<E>, Cloneable, Serializable {
    public ArrayDeque() {
        throw new RuntimeException("Stub!");
    }

    public ArrayDeque(int numElements) {
        throw new RuntimeException("Stub!");
    }

    public ArrayDeque(Collection<? extends E> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public void addFirst(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public void addLast(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public boolean offerFirst(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public boolean offerLast(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E removeFirst() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E removeLast() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E pollFirst() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E pollLast() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E getFirst() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E getLast() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E peekFirst() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E peekLast() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public boolean removeFirstOccurrence(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public boolean removeLastOccurrence(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean add(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public boolean offer(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public E remove() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public E poll() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public E element() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public E peek() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public void push(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E pop() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public Iterator<E> descendingIterator() {
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

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
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

    /* renamed from: clone */
    public ArrayDeque<E> m1341clone() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: ArrayDeque$DeqIterator.class */
    private class DeqIterator implements Iterator<E> {
        private int cursor;
        private int fence;
        private int lastRet;

        private DeqIterator() {
            this.cursor = ArrayDeque.access$200(ArrayDeque.this);
            this.fence = ArrayDeque.access$300(ArrayDeque.this);
            this.lastRet = -1;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.cursor != this.fence;
        }

        @Override // java.util.Iterator
        public E next() {
            if (this.cursor == this.fence) {
                throw new NoSuchElementException();
            }
            E result = (E) ArrayDeque.access$400(ArrayDeque.this)[this.cursor];
            if (ArrayDeque.access$300(ArrayDeque.this) != this.fence || result == null) {
                throw new ConcurrentModificationException();
            }
            this.lastRet = this.cursor;
            this.cursor = (this.cursor + 1) & (ArrayDeque.access$400(ArrayDeque.this).length - 1);
            return result;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.lastRet < 0) {
                throw new IllegalStateException();
            }
            if (ArrayDeque.access$500(ArrayDeque.this, this.lastRet)) {
                this.cursor = (this.cursor - 1) & (ArrayDeque.access$400(ArrayDeque.this).length - 1);
                this.fence = ArrayDeque.access$300(ArrayDeque.this);
            }
            this.lastRet = -1;
        }
    }

    /* loaded from: ArrayDeque$DescendingIterator.class */
    private class DescendingIterator implements Iterator<E> {
        private int cursor;
        private int fence;
        private int lastRet;

        private DescendingIterator() {
            this.cursor = ArrayDeque.access$300(ArrayDeque.this);
            this.fence = ArrayDeque.access$200(ArrayDeque.this);
            this.lastRet = -1;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.cursor != this.fence;
        }

        @Override // java.util.Iterator
        public E next() {
            if (this.cursor == this.fence) {
                throw new NoSuchElementException();
            }
            this.cursor = (this.cursor - 1) & (ArrayDeque.access$400(ArrayDeque.this).length - 1);
            E result = (E) ArrayDeque.access$400(ArrayDeque.this)[this.cursor];
            if (ArrayDeque.access$200(ArrayDeque.this) != this.fence || result == null) {
                throw new ConcurrentModificationException();
            }
            this.lastRet = this.cursor;
            return result;
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.lastRet < 0) {
                throw new IllegalStateException();
            }
            if (!ArrayDeque.access$500(ArrayDeque.this, this.lastRet)) {
                this.cursor = (this.cursor + 1) & (ArrayDeque.access$400(ArrayDeque.this).length - 1);
                this.fence = ArrayDeque.access$200(ArrayDeque.this);
            }
            this.lastRet = -1;
        }
    }
}