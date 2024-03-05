package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SynchronousQueue.class */
public class SynchronousQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
    public SynchronousQueue() {
        throw new RuntimeException("Stub!");
    }

    public SynchronousQueue(boolean fair) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public void put(E o) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public boolean offer(E e) {
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
    public E poll() {
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

    @Override // java.util.concurrent.BlockingQueue
    public int remainingCapacity() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
    public void clear() {
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
    public boolean containsAll(Collection<?> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean removeAll(Collection<?> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean retainAll(Collection<?> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public E peek() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
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

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super E> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super E> c, int maxElements) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: SynchronousQueue$Transferer.class */
    static abstract class Transferer<E> {
        abstract E transfer(E e, boolean z, long j);

        Transferer() {
        }
    }

    /* loaded from: SynchronousQueue$TransferStack.class */
    static final class TransferStack<E> extends Transferer<E> {
        static final int REQUEST = 0;
        static final int DATA = 1;
        static final int FULFILLING = 2;
        volatile SNode head;
        private static final Unsafe UNSAFE;
        private static final long headOffset;

        TransferStack() {
        }

        static boolean isFulfilling(int m) {
            return (m & 2) != 0;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: SynchronousQueue$TransferStack$SNode.class */
        public static final class SNode {
            volatile SNode next;
            volatile SNode match;
            volatile Thread waiter;
            Object item;
            int mode;
            private static final Unsafe UNSAFE;
            private static final long matchOffset;
            private static final long nextOffset;

            SNode(Object item) {
                this.item = item;
            }

            boolean casNext(SNode cmp, SNode val) {
                return cmp == this.next && UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
            }

            boolean tryMatch(SNode s) {
                if (this.match != null || !UNSAFE.compareAndSwapObject(this, matchOffset, null, s)) {
                    return this.match == s;
                }
                Thread w = this.waiter;
                if (w != null) {
                    this.waiter = null;
                    LockSupport.unpark(w);
                    return true;
                }
                return true;
            }

            void tryCancel() {
                UNSAFE.compareAndSwapObject(this, matchOffset, null, this);
            }

            boolean isCancelled() {
                return this.match == this;
            }

            static {
                try {
                    UNSAFE = Unsafe.getUnsafe();
                    matchOffset = UNSAFE.objectFieldOffset(SNode.class.getDeclaredField("match"));
                    nextOffset = UNSAFE.objectFieldOffset(SNode.class.getDeclaredField("next"));
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }

        boolean casHead(SNode h, SNode nh) {
            return h == this.head && UNSAFE.compareAndSwapObject(this, headOffset, h, nh);
        }

        static SNode snode(SNode s, Object e, SNode next, int mode) {
            if (s == null) {
                s = new SNode(e);
            }
            s.mode = mode;
            s.next = next;
            return s;
        }

        @Override // java.util.concurrent.SynchronousQueue.Transferer
        E transfer(E e, boolean timed, long nanos) {
            SNode s = null;
            int mode = e == null ? 0 : 1;
            while (true) {
                SNode h = this.head;
                if (h == null || h.mode == mode) {
                    if (timed && nanos <= 0) {
                        if (h != null && h.isCancelled()) {
                            casHead(h, h.next);
                        } else {
                            return null;
                        }
                    } else {
                        SNode snode = snode(s, e, h, mode);
                        s = snode;
                        if (casHead(h, snode)) {
                            SNode m = awaitFulfill(s, timed, nanos);
                            if (m == s) {
                                clean(s);
                                return null;
                            }
                            SNode h2 = this.head;
                            if (h2 != null && h2.next == s) {
                                casHead(h2, s.next);
                            }
                            return mode == 0 ? (E) m.item : (E) s.item;
                        }
                    }
                } else if (!isFulfilling(h.mode)) {
                    if (h.isCancelled()) {
                        casHead(h, h.next);
                    } else {
                        SNode snode2 = snode(s, e, h, 2 | mode);
                        s = snode2;
                        if (casHead(h, snode2)) {
                            while (true) {
                                SNode m2 = s.next;
                                if (m2 == null) {
                                    casHead(s, null);
                                    s = null;
                                    break;
                                }
                                SNode mn = m2.next;
                                if (m2.tryMatch(s)) {
                                    casHead(s, mn);
                                    return mode == 0 ? (E) m2.item : (E) s.item;
                                }
                                s.casNext(m2, mn);
                            }
                        } else {
                            continue;
                        }
                    }
                } else {
                    SNode m3 = h.next;
                    if (m3 == null) {
                        casHead(h, null);
                    } else {
                        SNode mn2 = m3.next;
                        if (m3.tryMatch(h)) {
                            casHead(h, mn2);
                        } else {
                            h.casNext(m3, mn2);
                        }
                    }
                }
            }
        }

        SNode awaitFulfill(SNode s, boolean timed, long nanos) {
            long deadline = timed ? System.nanoTime() + nanos : 0L;
            Thread w = Thread.currentThread();
            int spins = shouldSpin(s) ? timed ? SynchronousQueue.maxTimedSpins : SynchronousQueue.maxUntimedSpins : 0;
            while (true) {
                if (w.isInterrupted()) {
                    s.tryCancel();
                }
                SNode m = s.match;
                if (m != null) {
                    return m;
                }
                if (timed) {
                    nanos = deadline - System.nanoTime();
                    if (nanos <= 0) {
                        s.tryCancel();
                    }
                }
                if (spins > 0) {
                    spins = shouldSpin(s) ? spins - 1 : 0;
                } else if (s.waiter == null) {
                    s.waiter = w;
                } else if (!timed) {
                    LockSupport.park(this);
                } else if (nanos > 1000) {
                    LockSupport.parkNanos(this, nanos);
                }
            }
        }

        boolean shouldSpin(SNode s) {
            SNode h = this.head;
            return h == s || h == null || isFulfilling(h.mode);
        }

        void clean(SNode s) {
            SNode p;
            s.item = null;
            s.waiter = null;
            SNode past = s.next;
            if (past != null && past.isCancelled()) {
                past = past.next;
            }
            while (true) {
                SNode sNode = this.head;
                p = sNode;
                if (sNode == null || p == past || !p.isCancelled()) {
                    break;
                }
                casHead(p, p.next);
            }
            while (p != null && p != past) {
                SNode n = p.next;
                if (n != null && n.isCancelled()) {
                    p.casNext(n, n.next);
                } else {
                    p = n;
                }
            }
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                headOffset = UNSAFE.objectFieldOffset(TransferStack.class.getDeclaredField("head"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /* loaded from: SynchronousQueue$TransferQueue.class */
    static final class TransferQueue<E> extends Transferer<E> {
        volatile transient QNode head;
        volatile transient QNode tail;
        volatile transient QNode cleanMe;
        private static final Unsafe UNSAFE;
        private static final long headOffset;
        private static final long tailOffset;
        private static final long cleanMeOffset;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: SynchronousQueue$TransferQueue$QNode.class */
        public static final class QNode {
            volatile QNode next;
            volatile Object item;
            volatile Thread waiter;
            final boolean isData;
            private static final Unsafe UNSAFE;
            private static final long itemOffset;
            private static final long nextOffset;

            QNode(Object item, boolean isData) {
                this.item = item;
                this.isData = isData;
            }

            boolean casNext(QNode cmp, QNode val) {
                return this.next == cmp && UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
            }

            boolean casItem(Object cmp, Object val) {
                return this.item == cmp && UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
            }

            void tryCancel(Object cmp) {
                UNSAFE.compareAndSwapObject(this, itemOffset, cmp, this);
            }

            boolean isCancelled() {
                return this.item == this;
            }

            boolean isOffList() {
                return this.next == this;
            }

            static {
                try {
                    UNSAFE = Unsafe.getUnsafe();
                    itemOffset = UNSAFE.objectFieldOffset(QNode.class.getDeclaredField("item"));
                    nextOffset = UNSAFE.objectFieldOffset(QNode.class.getDeclaredField("next"));
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }

        TransferQueue() {
            QNode h = new QNode(null, false);
            this.head = h;
            this.tail = h;
        }

        void advanceHead(QNode h, QNode nh) {
            if (h == this.head && UNSAFE.compareAndSwapObject(this, headOffset, h, nh)) {
                h.next = h;
            }
        }

        void advanceTail(QNode t, QNode nt) {
            if (this.tail == t) {
                UNSAFE.compareAndSwapObject(this, tailOffset, t, nt);
            }
        }

        boolean casCleanMe(QNode cmp, QNode val) {
            return this.cleanMe == cmp && UNSAFE.compareAndSwapObject(this, cleanMeOffset, cmp, val);
        }

        @Override // java.util.concurrent.SynchronousQueue.Transferer
        E transfer(E e, boolean timed, long nanos) {
            QNode s = null;
            boolean isData = e != null;
            while (true) {
                QNode t = this.tail;
                QNode h = this.head;
                if (t != null && h != null) {
                    if (h == t || t.isData == isData) {
                        QNode tn = t.next;
                        if (t != this.tail) {
                            continue;
                        } else if (tn != null) {
                            advanceTail(t, tn);
                        } else if (timed && nanos <= 0) {
                            return null;
                        } else {
                            if (s == null) {
                                s = new QNode(e, isData);
                            }
                            if (t.casNext(null, s)) {
                                advanceTail(t, s);
                                E e2 = (E) awaitFulfill(s, e, timed, nanos);
                                if (e2 == s) {
                                    clean(t, s);
                                    return null;
                                }
                                if (!s.isOffList()) {
                                    advanceHead(t, s);
                                    if (e2 != null) {
                                        s.item = s;
                                    }
                                    s.waiter = null;
                                }
                                return e2 != null ? e2 : e;
                            }
                        }
                    } else {
                        QNode m = h.next;
                        if (t == this.tail && m != null && h == this.head) {
                            E e3 = (E) m.item;
                            if (isData == (e3 != null) || e3 == m || !m.casItem(e3, e)) {
                                advanceHead(h, m);
                            } else {
                                advanceHead(h, m);
                                LockSupport.unpark(m.waiter);
                                return e3 != null ? e3 : e;
                            }
                        }
                    }
                }
            }
        }

        Object awaitFulfill(QNode s, E e, boolean timed, long nanos) {
            long deadline = timed ? System.nanoTime() + nanos : 0L;
            Thread w = Thread.currentThread();
            int spins = this.head.next == s ? timed ? SynchronousQueue.maxTimedSpins : SynchronousQueue.maxUntimedSpins : 0;
            while (true) {
                if (w.isInterrupted()) {
                    s.tryCancel(e);
                }
                Object x = s.item;
                if (x != e) {
                    return x;
                }
                if (timed) {
                    nanos = deadline - System.nanoTime();
                    if (nanos <= 0) {
                        s.tryCancel(e);
                    }
                }
                if (spins > 0) {
                    spins--;
                } else if (s.waiter == null) {
                    s.waiter = w;
                } else if (!timed) {
                    LockSupport.park(this);
                } else if (nanos > 1000) {
                    LockSupport.parkNanos(this, nanos);
                }
            }
        }

        void clean(QNode pred, QNode s) {
            QNode dn;
            QNode sn;
            s.waiter = null;
            while (pred.next == s) {
                QNode h = this.head;
                QNode hn = h.next;
                if (hn != null && hn.isCancelled()) {
                    advanceHead(h, hn);
                } else {
                    QNode t = this.tail;
                    if (t == h) {
                        return;
                    }
                    QNode tn = t.next;
                    if (t != this.tail) {
                        continue;
                    } else if (tn != null) {
                        advanceTail(t, tn);
                    } else if (s != t && ((sn = s.next) == s || pred.casNext(s, sn))) {
                        return;
                    } else {
                        QNode dp = this.cleanMe;
                        if (dp != null) {
                            QNode d = dp.next;
                            if (d == null || d == dp || !d.isCancelled() || (d != t && (dn = d.next) != null && dn != d && dp.casNext(d, dn))) {
                                casCleanMe(dp, null);
                            }
                            if (dp == pred) {
                                return;
                            }
                        } else if (casCleanMe(null, pred)) {
                            return;
                        }
                    }
                }
            }
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                headOffset = UNSAFE.objectFieldOffset(TransferQueue.class.getDeclaredField("head"));
                tailOffset = UNSAFE.objectFieldOffset(TransferQueue.class.getDeclaredField("tail"));
                cleanMeOffset = UNSAFE.objectFieldOffset(TransferQueue.class.getDeclaredField("cleanMe"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /* loaded from: SynchronousQueue$EmptyIterator.class */
    private static class EmptyIterator<E> implements Iterator<E> {
        static final EmptyIterator<Object> EMPTY_ITERATOR = new EmptyIterator<>();

        private EmptyIterator() {
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return false;
        }

        @Override // java.util.Iterator
        public E next() {
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new IllegalStateException();
        }
    }

    /* loaded from: SynchronousQueue$WaitQueue.class */
    static class WaitQueue implements Serializable {
        WaitQueue() {
        }
    }

    /* loaded from: SynchronousQueue$LifoWaitQueue.class */
    static class LifoWaitQueue extends WaitQueue {
        private static final long serialVersionUID = -3633113410248163686L;

        LifoWaitQueue() {
        }
    }

    /* loaded from: SynchronousQueue$FifoWaitQueue.class */
    static class FifoWaitQueue extends WaitQueue {
        private static final long serialVersionUID = -3623113410248163686L;

        FifoWaitQueue() {
        }
    }
}