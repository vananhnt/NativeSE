package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

/* loaded from: LinkedTransferQueue.class */
public class LinkedTransferQueue<E> extends AbstractQueue<E> implements TransferQueue<E>, Serializable {
    private static final long serialVersionUID = -3223113410248163686L;
    private static final boolean MP;
    private static final int FRONT_SPINS = 128;
    private static final int CHAINED_SPINS = 64;
    static final int SWEEP_THRESHOLD = 32;
    volatile transient Node head;
    private volatile transient Node tail;
    private volatile transient int sweepVotes;
    private static final int NOW = 0;
    private static final int ASYNC = 1;
    private static final int SYNC = 2;
    private static final int TIMED = 3;
    private static final Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long sweepVotesOffset;

    static {
        MP = Runtime.getRuntime().availableProcessors() > 1;
        try {
            UNSAFE = Unsafe.getUnsafe();
            headOffset = UNSAFE.objectFieldOffset(LinkedTransferQueue.class.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset(LinkedTransferQueue.class.getDeclaredField("tail"));
            sweepVotesOffset = UNSAFE.objectFieldOffset(LinkedTransferQueue.class.getDeclaredField("sweepVotes"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LinkedTransferQueue$Node.class */
    public static final class Node {
        final boolean isData;
        volatile Object item;
        volatile Node next;
        volatile Thread waiter;
        private static final long serialVersionUID = -3375979862319811754L;
        private static final Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;
        private static final long waiterOffset;

        final boolean casNext(Node cmp, Node val) {
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        final boolean casItem(Object cmp, Object val) {
            return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
        }

        Node(Object item, boolean isData) {
            UNSAFE.putObject(this, itemOffset, item);
            this.isData = isData;
        }

        final void forgetNext() {
            UNSAFE.putObject(this, nextOffset, this);
        }

        final void forgetContents() {
            UNSAFE.putObject(this, itemOffset, this);
            UNSAFE.putObject(this, waiterOffset, null);
        }

        final boolean isMatched() {
            Object x = this.item;
            if (x != this) {
                if ((x == null) != this.isData) {
                    return false;
                }
            }
            return true;
        }

        final boolean isUnmatchedRequest() {
            return !this.isData && this.item == null;
        }

        final boolean cannotPrecede(boolean haveData) {
            Object x;
            boolean d = this.isData;
            if (d != haveData && (x = this.item) != this) {
                if ((x != null) == d) {
                    return true;
                }
            }
            return false;
        }

        final boolean tryMatchData() {
            Object x = this.item;
            if (x != null && x != this && casItem(x, null)) {
                LockSupport.unpark(this.waiter);
                return true;
            }
            return false;
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                itemOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("next"));
                waiterOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("waiter"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    private boolean casTail(Node cmp, Node val) {
        return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
    }

    private boolean casHead(Node cmp, Node val) {
        return UNSAFE.compareAndSwapObject(this, headOffset, cmp, val);
    }

    private boolean casSweepVotes(int cmp, int val) {
        return UNSAFE.compareAndSwapInt(this, sweepVotesOffset, cmp, val);
    }

    /* JADX WARN: Multi-variable type inference failed */
    static <E> E cast(Object item) {
        return item;
    }

    private E xfer(E e, boolean haveData, int how, long nanos) {
        if (haveData && e == null) {
            throw new NullPointerException();
        }
        Node s = null;
        while (true) {
            Node h = this.head;
            Node node = h;
            while (true) {
                Node p = node;
                if (p == null) {
                    break;
                }
                boolean isData = p.isData;
                Object item = p.item;
                if (item != p) {
                    if ((item != null) == isData) {
                        if (isData == haveData) {
                            break;
                        } else if (p.casItem(item, e)) {
                            Node q = p;
                            while (true) {
                                if (q == h) {
                                    break;
                                }
                                Node n = q.next;
                                if (this.head == h) {
                                    if (casHead(h, n == null ? q : n)) {
                                        h.forgetNext();
                                        break;
                                    }
                                }
                                Node node2 = this.head;
                                h = node2;
                                if (node2 == null) {
                                    break;
                                }
                                Node node3 = h.next;
                                q = node3;
                                if (node3 != null) {
                                    if (!q.isMatched()) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            LockSupport.unpark(p.waiter);
                            return (E) cast(item);
                        }
                    }
                }
                Node n2 = p.next;
                if (p != n2) {
                    node = n2;
                } else {
                    node = this.head;
                    h = node;
                }
            }
            if (how == 0) {
                break;
            }
            if (s == null) {
                s = new Node(e, haveData);
            }
            Node pred = tryAppend(s, haveData);
            if (pred != null) {
                if (how != 1) {
                    return awaitMatch(s, pred, e, how == 3, nanos);
                }
            }
        }
        return e;
    }

    private Node tryAppend(Node s, boolean haveData) {
        Node node;
        Node s2;
        Node t = this.tail;
        Node p = t;
        while (true) {
            if (p == null) {
                Node node2 = this.head;
                p = node2;
                if (node2 == null) {
                    if (casHead(null, s)) {
                        return s;
                    }
                }
            }
            if (p.cannotPrecede(haveData)) {
                return null;
            }
            Node n = p.next;
            if (n != null) {
                if (p != t) {
                    Node node3 = t;
                    Node u = this.tail;
                    if (node3 != u) {
                        node = u;
                        t = node;
                        p = node;
                    }
                }
                node = p != n ? n : null;
                p = node;
            } else if (!p.casNext(null, s)) {
                p = p.next;
            } else {
                if (p != t) {
                    do {
                        if (this.tail != t || !casTail(t, s)) {
                            Node node4 = this.tail;
                            t = node4;
                            if (node4 != null && (s2 = t.next) != null) {
                                Node node5 = s2.next;
                                s = node5;
                                if (node5 == null) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    } while (s != t);
                }
                return p;
            }
        }
    }

    private E awaitMatch(Node s, Node pred, E e, boolean timed, long nanos) {
        long deadline = timed ? System.nanoTime() + nanos : 0L;
        Thread w = Thread.currentThread();
        int spins = -1;
        ThreadLocalRandom randomYields = null;
        while (true) {
            Object item = s.item;
            if (item != e) {
                s.forgetContents();
                return (E) cast(item);
            } else if ((w.isInterrupted() || (timed && nanos <= 0)) && s.casItem(e, s)) {
                unsplice(pred, s);
                return e;
            } else if (spins < 0) {
                int spinsFor = spinsFor(pred, s.isData);
                spins = spinsFor;
                if (spinsFor > 0) {
                    randomYields = ThreadLocalRandom.current();
                }
            } else if (spins > 0) {
                spins--;
                if (randomYields.nextInt(64) == 0) {
                    Thread.yield();
                }
            } else if (s.waiter == null) {
                s.waiter = w;
            } else if (timed) {
                nanos = deadline - System.nanoTime();
                if (nanos > 0) {
                    LockSupport.parkNanos(this, nanos);
                }
            } else {
                LockSupport.park(this);
            }
        }
    }

    private static int spinsFor(Node pred, boolean haveData) {
        if (MP && pred != null) {
            if (pred.isData != haveData) {
                return 192;
            }
            if (pred.isMatched()) {
                return 128;
            }
            if (pred.waiter == null) {
                return 64;
            }
            return 0;
        }
        return 0;
    }

    final Node succ(Node p) {
        Node next = p.next;
        return p == next ? this.head : next;
    }

    private Node firstOfMode(boolean isData) {
        Node node = this.head;
        while (true) {
            Node p = node;
            if (p != null) {
                if (p.isMatched()) {
                    node = succ(p);
                } else if (p.isData == isData) {
                    return p;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private E firstDataItem() {
        Node node = this.head;
        while (true) {
            Node p = node;
            if (p != null) {
                Object item = p.item;
                if (p.isData) {
                    if (item != null && item != p) {
                        return (E) cast(item);
                    }
                } else if (item == null) {
                    return null;
                }
                node = succ(p);
            } else {
                return null;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0045, code lost:
        return r5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int countOfMode(boolean r4) {
        /*
            r3 = this;
            r0 = 0
            r5 = r0
            r0 = r3
            java.util.concurrent.LinkedTransferQueue$Node r0 = r0.head
            r6 = r0
        L7:
            r0 = r6
            if (r0 == 0) goto L44
            r0 = r6
            boolean r0 = r0.isMatched()
            if (r0 != 0) goto L28
            r0 = r6
            boolean r0 = r0.isData
            r1 = r4
            if (r0 == r1) goto L1c
            r0 = 0
            return r0
        L1c:
            int r5 = r5 + 1
            r0 = r5
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r1) goto L28
            goto L44
        L28:
            r0 = r6
            java.util.concurrent.LinkedTransferQueue$Node r0 = r0.next
            r7 = r0
            r0 = r7
            r1 = r6
            if (r0 == r1) goto L3a
            r0 = r7
            r6 = r0
            goto L41
        L3a:
            r0 = 0
            r5 = r0
            r0 = r3
            java.util.concurrent.LinkedTransferQueue$Node r0 = r0.head
            r6 = r0
        L41:
            goto L7
        L44:
            r0 = r5
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.LinkedTransferQueue.countOfMode(boolean):int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LinkedTransferQueue$Itr.class */
    public final class Itr implements Iterator<E> {
        private Node nextNode;
        private E nextItem;
        private Node lastRet;
        private Node lastPred;

        /* JADX WARN: Code restructure failed: missing block: B:56:0x0100, code lost:
            r4.nextNode = null;
            r4.nextItem = null;
         */
        /* JADX WARN: Code restructure failed: missing block: B:57:0x010a, code lost:
            return;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private void advance(java.util.concurrent.LinkedTransferQueue.Node r5) {
            /*
                Method dump skipped, instructions count: 267
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.LinkedTransferQueue.Itr.advance(java.util.concurrent.LinkedTransferQueue$Node):void");
        }

        Itr() {
            advance(null);
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            return this.nextNode != null;
        }

        @Override // java.util.Iterator
        public final E next() {
            Node p = this.nextNode;
            if (p == null) {
                throw new NoSuchElementException();
            }
            E e = this.nextItem;
            advance(p);
            return e;
        }

        @Override // java.util.Iterator
        public final void remove() {
            Node lastRet = this.lastRet;
            if (lastRet == null) {
                throw new IllegalStateException();
            }
            this.lastRet = null;
            if (lastRet.tryMatchData()) {
                LinkedTransferQueue.this.unsplice(this.lastPred, lastRet);
            }
        }
    }

    final void unsplice(Node pred, Node s) {
        s.forgetContents();
        if (pred != null && pred != s && pred.next == s) {
            Node n = s.next;
            if (n != null && (n == s || !pred.casNext(s, n) || !pred.isMatched())) {
                return;
            }
            while (true) {
                Node h = this.head;
                if (h == pred || h == s || h == null) {
                    return;
                }
                if (h.isMatched()) {
                    Node hn = h.next;
                    if (hn == null) {
                        return;
                    }
                    if (hn != h && casHead(h, hn)) {
                        h.forgetNext();
                    }
                } else if (pred.next == pred || s.next == s) {
                    return;
                } else {
                    while (true) {
                        int v = this.sweepVotes;
                        if (v < 32) {
                            if (casSweepVotes(v, v + 1)) {
                                return;
                            }
                        } else if (casSweepVotes(v, 0)) {
                            sweep();
                            return;
                        }
                    }
                }
            }
        }
    }

    private void sweep() {
        Node s;
        Node p = this.head;
        while (p != null && (s = p.next) != null) {
            if (!s.isMatched()) {
                p = s;
            } else {
                Node n = s.next;
                if (n != null) {
                    if (s == n) {
                        p = this.head;
                    } else {
                        p.casNext(s, n);
                    }
                } else {
                    return;
                }
            }
        }
    }

    private boolean findAndRemove(Object e) {
        if (e != null) {
            Node pred = null;
            Node p = this.head;
            while (p != null) {
                Object item = p.item;
                if (p.isData) {
                    if (item != null && item != p && e.equals(item) && p.tryMatchData()) {
                        unsplice(pred, p);
                        return true;
                    }
                } else if (item == null) {
                    return false;
                }
                pred = p;
                Node node = p.next;
                p = node;
                if (node == pred) {
                    pred = null;
                    p = this.head;
                }
            }
            return false;
        }
        return false;
    }

    public LinkedTransferQueue() {
    }

    public LinkedTransferQueue(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    @Override // java.util.concurrent.BlockingQueue
    public void put(E e) {
        xfer(e, true, 1, 0L);
    }

    @Override // java.util.concurrent.BlockingQueue
    public boolean offer(E e, long timeout, TimeUnit unit) {
        xfer(e, true, 1, 0L);
        return true;
    }

    @Override // java.util.Queue
    public boolean offer(E e) {
        xfer(e, true, 1, 0L);
        return true;
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public boolean add(E e) {
        xfer(e, true, 1, 0L);
        return true;
    }

    @Override // java.util.concurrent.TransferQueue
    public boolean tryTransfer(E e) {
        return xfer(e, true, 0, 0L) == null;
    }

    @Override // java.util.concurrent.TransferQueue
    public void transfer(E e) throws InterruptedException {
        if (xfer(e, true, 2, 0L) != null) {
            Thread.interrupted();
            throw new InterruptedException();
        }
    }

    @Override // java.util.concurrent.TransferQueue
    public boolean tryTransfer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if (xfer(e, true, 3, unit.toNanos(timeout)) == null) {
            return true;
        }
        if (!Thread.interrupted()) {
            return false;
        }
        throw new InterruptedException();
    }

    @Override // java.util.concurrent.BlockingQueue
    public E take() throws InterruptedException {
        E e = xfer(null, false, 2, 0L);
        if (e != null) {
            return e;
        }
        Thread.interrupted();
        throw new InterruptedException();
    }

    @Override // java.util.concurrent.BlockingQueue
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E e = xfer(null, false, 3, unit.toNanos(timeout));
        if (e != null || !Thread.interrupted()) {
            return e;
        }
        throw new InterruptedException();
    }

    @Override // java.util.Queue
    public E poll() {
        return xfer(null, false, 0, 0L);
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        int n = 0;
        while (true) {
            E e = poll();
            if (e != null) {
                c.add(e);
                n++;
            } else {
                return n;
            }
        }
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super E> c, int maxElements) {
        E e;
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        int n = 0;
        while (n < maxElements && (e = poll()) != null) {
            c.add(e);
            n++;
        }
        return n;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override // java.util.Queue
    public E peek() {
        return firstDataItem();
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        Node node = this.head;
        while (true) {
            Node p = node;
            if (p != null) {
                if (!p.isMatched()) {
                    return !p.isData;
                }
                node = succ(p);
            } else {
                return true;
            }
        }
    }

    @Override // java.util.concurrent.TransferQueue
    public boolean hasWaitingConsumer() {
        return firstOfMode(false) != null;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return countOfMode(true);
    }

    @Override // java.util.concurrent.TransferQueue
    public int getWaitingConsumerCount() {
        return countOfMode(false);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object o) {
        return findAndRemove(o);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        Node node = this.head;
        while (true) {
            Node p = node;
            if (p != null) {
                Object item = p.item;
                if (p.isData) {
                    if (item != null && item != p && o.equals(item)) {
                        return true;
                    }
                } else if (item == null) {
                    return false;
                }
                node = succ(p);
            } else {
                return false;
            }
        }
    }

    @Override // java.util.concurrent.BlockingQueue
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            E e = i$.next();
            s.writeObject(e);
        }
        s.writeObject(null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        while (true) {
            Object readObject = s.readObject();
            if (readObject != null) {
                offer(readObject);
            } else {
                return;
            }
        }
    }
}