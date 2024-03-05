package java.util;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PriorityQueue.class */
public class PriorityQueue<E> extends AbstractQueue<E> implements Serializable {
    public PriorityQueue() {
        throw new RuntimeException("Stub!");
    }

    public PriorityQueue(int initialCapacity) {
        throw new RuntimeException("Stub!");
    }

    public PriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        throw new RuntimeException("Stub!");
    }

    public PriorityQueue(Collection<? extends E> c) {
        throw new RuntimeException("Stub!");
    }

    public PriorityQueue(PriorityQueue<? extends E> c) {
        throw new RuntimeException("Stub!");
    }

    public PriorityQueue(SortedSet<? extends E> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public boolean offer(E o) {
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

    public Comparator<? super E> comparator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public boolean add(E o) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: PriorityQueue$PriorityIterator.class */
    private class PriorityIterator implements Iterator<E> {
        private int currentIndex;
        private boolean allowRemove;

        private PriorityIterator() {
            this.currentIndex = -1;
            this.allowRemove = false;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.currentIndex < PriorityQueue.access$100(PriorityQueue.this) - 1;
        }

        @Override // java.util.Iterator
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            this.allowRemove = true;
            Object[] access$200 = PriorityQueue.access$200(PriorityQueue.this);
            int i = this.currentIndex + 1;
            this.currentIndex = i;
            return (E) access$200[i];
        }

        @Override // java.util.Iterator
        public void remove() {
            if (!this.allowRemove) {
                throw new IllegalStateException();
            }
            this.allowRemove = false;
            PriorityQueue priorityQueue = PriorityQueue.this;
            int i = this.currentIndex;
            this.currentIndex = i - 1;
            PriorityQueue.access$300(priorityQueue, i);
        }
    }
}