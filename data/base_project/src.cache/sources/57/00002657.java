package java.util.concurrent;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ArrayBlockingQueue.class */
public class ArrayBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
    public ArrayBlockingQueue(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public ArrayBlockingQueue(int capacity, boolean fair) {
        throw new RuntimeException("Stub!");
    }

    public ArrayBlockingQueue(int capacity, boolean fair, Collection<? extends E> c) {
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
    public void put(E e) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
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

    @Override // java.util.AbstractCollection, java.util.Collection
    public <T> T[] toArray(T[] a) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public void clear() {
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

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ArrayBlockingQueue$Itrs.class */
    public class Itrs {
        private ArrayBlockingQueue<E>.Itrs.Node head;
        private static final int SHORT_SWEEP_PROBES = 4;
        private static final int LONG_SWEEP_PROBES = 16;
        int cycles = 0;
        private ArrayBlockingQueue<E>.Itrs.Node sweeper = null;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: ArrayBlockingQueue$Itrs$Node.class */
        public class Node extends WeakReference<ArrayBlockingQueue<E>.Itr> {
            ArrayBlockingQueue<E>.Itrs.Node next;

            Node(ArrayBlockingQueue<E>.Itr iterator, ArrayBlockingQueue<E>.Itrs.Node next) {
                super(iterator);
                this.next = next;
            }
        }

        Itrs(ArrayBlockingQueue<E>.Itr initial) {
            register(initial);
        }

        void doSomeSweeping(boolean tryHarder) {
            ArrayBlockingQueue<E>.Itrs.Node o;
            ArrayBlockingQueue<E>.Itrs.Node p;
            boolean passedGo;
            int probes = tryHarder ? 16 : 4;
            ArrayBlockingQueue<E>.Itrs.Node sweeper = this.sweeper;
            if (sweeper == null) {
                o = null;
                p = this.head;
                passedGo = true;
            } else {
                o = sweeper;
                p = o.next;
                passedGo = false;
            }
            while (probes > 0) {
                if (p == null) {
                    if (passedGo) {
                        break;
                    }
                    o = null;
                    p = this.head;
                    passedGo = true;
                }
                ArrayBlockingQueue<E>.Itr it = p.get();
                ArrayBlockingQueue<E>.Itrs.Node next = p.next;
                if (it == null || it.isDetached()) {
                    probes = 16;
                    p.clear();
                    p.next = null;
                    if (o == null) {
                        this.head = next;
                        if (next == null) {
                            ArrayBlockingQueue.this.itrs = null;
                            return;
                        }
                    } else {
                        o.next = next;
                    }
                } else {
                    o = p;
                }
                p = next;
                probes--;
            }
            this.sweeper = p == null ? null : o;
        }

        void register(ArrayBlockingQueue<E>.Itr itr) {
            this.head = new Node(itr, this.head);
        }

        void takeIndexWrapped() {
            this.cycles++;
            ArrayBlockingQueue<E>.Itrs.Node o = null;
            ArrayBlockingQueue<E>.Itrs.Node node = this.head;
            while (true) {
                ArrayBlockingQueue<E>.Itrs.Node p = node;
                if (p == null) {
                    break;
                }
                ArrayBlockingQueue<E>.Itr it = p.get();
                ArrayBlockingQueue<E>.Itrs.Node next = p.next;
                if (it == null || it.takeIndexWrapped()) {
                    p.clear();
                    p.next = null;
                    if (o == null) {
                        this.head = next;
                    } else {
                        o.next = next;
                    }
                } else {
                    o = p;
                }
                node = next;
            }
            if (this.head == null) {
                ArrayBlockingQueue.this.itrs = null;
            }
        }

        void removedAt(int removedIndex) {
            ArrayBlockingQueue<E>.Itrs.Node o = null;
            ArrayBlockingQueue<E>.Itrs.Node node = this.head;
            while (true) {
                ArrayBlockingQueue<E>.Itrs.Node p = node;
                if (p == null) {
                    break;
                }
                ArrayBlockingQueue<E>.Itr it = p.get();
                ArrayBlockingQueue<E>.Itrs.Node next = p.next;
                if (it == null || it.removedAt(removedIndex)) {
                    p.clear();
                    p.next = null;
                    if (o == null) {
                        this.head = next;
                    } else {
                        o.next = next;
                    }
                } else {
                    o = p;
                }
                node = next;
            }
            if (this.head == null) {
                ArrayBlockingQueue.this.itrs = null;
            }
        }

        void queueIsEmpty() {
            ArrayBlockingQueue<E>.Itrs.Node node = this.head;
            while (true) {
                ArrayBlockingQueue<E>.Itrs.Node p = node;
                if (p != null) {
                    ArrayBlockingQueue<E>.Itr it = p.get();
                    if (it != null) {
                        p.clear();
                        it.shutdown();
                    }
                    node = p.next;
                } else {
                    this.head = null;
                    ArrayBlockingQueue.this.itrs = null;
                    return;
                }
            }
        }

        void elementDequeued() {
            if (ArrayBlockingQueue.this.count == 0) {
                queueIsEmpty();
            } else if (ArrayBlockingQueue.this.takeIndex == 0) {
                takeIndexWrapped();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ArrayBlockingQueue$Itr.class */
    public class Itr implements Iterator<E> {
        private int cursor;
        private E nextItem;
        private int nextIndex;
        private E lastItem;
        private int lastRet = -1;
        private int prevTakeIndex;
        private int prevCycles;
        private static final int NONE = -1;
        private static final int REMOVED = -2;
        private static final int DETACHED = -3;

        Itr() {
            ReentrantLock lock = ArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                if (ArrayBlockingQueue.this.count == 0) {
                    this.cursor = -1;
                    this.nextIndex = -1;
                    this.prevTakeIndex = -3;
                } else {
                    int takeIndex = ArrayBlockingQueue.this.takeIndex;
                    this.prevTakeIndex = takeIndex;
                    this.nextIndex = takeIndex;
                    this.nextItem = (E) ArrayBlockingQueue.this.itemAt(takeIndex);
                    this.cursor = incCursor(takeIndex);
                    if (ArrayBlockingQueue.this.itrs == null) {
                        ArrayBlockingQueue.this.itrs = new Itrs(this);
                    } else {
                        ArrayBlockingQueue.this.itrs.register(this);
                        ArrayBlockingQueue.this.itrs.doSomeSweeping(false);
                    }
                    this.prevCycles = ArrayBlockingQueue.this.itrs.cycles;
                }
            } finally {
                lock.unlock();
            }
        }

        boolean isDetached() {
            return this.prevTakeIndex < 0;
        }

        private int incCursor(int index) {
            int index2 = ArrayBlockingQueue.this.inc(index);
            if (index2 == ArrayBlockingQueue.this.putIndex) {
                index2 = -1;
            }
            return index2;
        }

        private boolean invalidated(int index, int prevTakeIndex, long dequeues, int length) {
            if (index < 0) {
                return false;
            }
            int distance = index - prevTakeIndex;
            if (distance < 0) {
                distance += length;
            }
            return dequeues > ((long) distance);
        }

        private void incorporateDequeues() {
            int cycles = ArrayBlockingQueue.this.itrs.cycles;
            int takeIndex = ArrayBlockingQueue.this.takeIndex;
            int prevCycles = this.prevCycles;
            int prevTakeIndex = this.prevTakeIndex;
            if (cycles != prevCycles || takeIndex != prevTakeIndex) {
                int len = ArrayBlockingQueue.this.items.length;
                long dequeues = ((cycles - prevCycles) * len) + (takeIndex - prevTakeIndex);
                if (invalidated(this.lastRet, prevTakeIndex, dequeues, len)) {
                    this.lastRet = -2;
                }
                if (invalidated(this.nextIndex, prevTakeIndex, dequeues, len)) {
                    this.nextIndex = -2;
                }
                if (invalidated(this.cursor, prevTakeIndex, dequeues, len)) {
                    this.cursor = takeIndex;
                }
                if (this.cursor < 0 && this.nextIndex < 0 && this.lastRet < 0) {
                    detach();
                    return;
                }
                this.prevCycles = cycles;
                this.prevTakeIndex = takeIndex;
            }
        }

        private void detach() {
            if (this.prevTakeIndex >= 0) {
                this.prevTakeIndex = -3;
                ArrayBlockingQueue.this.itrs.doSomeSweeping(true);
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.nextItem != null) {
                return true;
            }
            noNext();
            return false;
        }

        private void noNext() {
            ReentrantLock lock = ArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                if (!isDetached()) {
                    incorporateDequeues();
                    if (this.lastRet >= 0) {
                        this.lastItem = (E) ArrayBlockingQueue.this.itemAt(this.lastRet);
                        detach();
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        @Override // java.util.Iterator
        public E next() {
            E x = this.nextItem;
            if (x == null) {
                throw new NoSuchElementException();
            }
            ReentrantLock lock = ArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                if (!isDetached()) {
                    incorporateDequeues();
                }
                this.lastRet = this.nextIndex;
                int cursor = this.cursor;
                if (cursor >= 0) {
                    ArrayBlockingQueue arrayBlockingQueue = ArrayBlockingQueue.this;
                    this.nextIndex = cursor;
                    this.nextItem = (E) arrayBlockingQueue.itemAt(cursor);
                    this.cursor = incCursor(cursor);
                } else {
                    this.nextIndex = -1;
                    this.nextItem = null;
                }
                return x;
            } finally {
                lock.unlock();
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            ReentrantLock lock = ArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                if (!isDetached()) {
                    incorporateDequeues();
                }
                int lastRet = this.lastRet;
                this.lastRet = -1;
                if (lastRet >= 0) {
                    if (!isDetached()) {
                        ArrayBlockingQueue.this.removeAt(lastRet);
                    } else {
                        E lastItem = this.lastItem;
                        this.lastItem = null;
                        if (ArrayBlockingQueue.this.itemAt(lastRet) == lastItem) {
                            ArrayBlockingQueue.this.removeAt(lastRet);
                        }
                    }
                } else if (lastRet == -1) {
                    throw new IllegalStateException();
                }
                if (this.cursor < 0 && this.nextIndex < 0) {
                    detach();
                }
            } finally {
                lock.unlock();
            }
        }

        void shutdown() {
            this.cursor = -1;
            if (this.nextIndex >= 0) {
                this.nextIndex = -2;
            }
            if (this.lastRet >= 0) {
                this.lastRet = -2;
                this.lastItem = null;
            }
            this.prevTakeIndex = -3;
        }

        private int distance(int index, int prevTakeIndex, int length) {
            int distance = index - prevTakeIndex;
            if (distance < 0) {
                distance += length;
            }
            return distance;
        }

        boolean removedAt(int removedIndex) {
            if (isDetached()) {
                return true;
            }
            int cycles = ArrayBlockingQueue.this.itrs.cycles;
            int takeIndex = ArrayBlockingQueue.this.takeIndex;
            int prevCycles = this.prevCycles;
            int prevTakeIndex = this.prevTakeIndex;
            int len = ArrayBlockingQueue.this.items.length;
            int cycleDiff = cycles - prevCycles;
            if (removedIndex < takeIndex) {
                cycleDiff++;
            }
            int removedDistance = (cycleDiff * len) + (removedIndex - prevTakeIndex);
            int cursor = this.cursor;
            if (cursor >= 0) {
                int x = distance(cursor, prevTakeIndex, len);
                if (x == removedDistance) {
                    if (cursor == ArrayBlockingQueue.this.putIndex) {
                        cursor = -1;
                        this.cursor = -1;
                    }
                } else if (x > removedDistance) {
                    int dec = ArrayBlockingQueue.this.dec(cursor);
                    cursor = dec;
                    this.cursor = dec;
                }
            }
            int lastRet = this.lastRet;
            if (lastRet >= 0) {
                int x2 = distance(lastRet, prevTakeIndex, len);
                if (x2 == removedDistance) {
                    lastRet = -2;
                    this.lastRet = -2;
                } else if (x2 > removedDistance) {
                    int dec2 = ArrayBlockingQueue.this.dec(lastRet);
                    lastRet = dec2;
                    this.lastRet = dec2;
                }
            }
            int nextIndex = this.nextIndex;
            if (nextIndex >= 0) {
                int x3 = distance(nextIndex, prevTakeIndex, len);
                if (x3 == removedDistance) {
                    this.nextIndex = -2;
                    return false;
                } else if (x3 > removedDistance) {
                    this.nextIndex = ArrayBlockingQueue.this.dec(nextIndex);
                    return false;
                } else {
                    return false;
                }
            } else if (cursor < 0 && nextIndex < 0 && lastRet < 0) {
                this.prevTakeIndex = -3;
                return true;
            } else {
                return false;
            }
        }

        boolean takeIndexWrapped() {
            if (isDetached()) {
                return true;
            }
            if (ArrayBlockingQueue.this.itrs.cycles - this.prevCycles > 1) {
                shutdown();
                return true;
            }
            return false;
        }
    }
}