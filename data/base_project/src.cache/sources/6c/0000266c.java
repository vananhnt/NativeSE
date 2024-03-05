package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import sun.misc.Unsafe;

/* loaded from: ConcurrentLinkedDeque.class */
public class ConcurrentLinkedDeque<E> extends AbstractCollection<E> implements Deque<E>, Serializable {
    private static final long serialVersionUID = 876323262645176354L;
    private volatile transient Node<E> head;
    private volatile transient Node<E> tail;
    private static final Node<Object> PREV_TERMINATOR = new Node<>();
    private static final Node<Object> NEXT_TERMINATOR;
    private static final int HOPS = 2;
    private static final Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;

    Node<E> prevTerminator() {
        return (Node<E>) PREV_TERMINATOR;
    }

    Node<E> nextTerminator() {
        return (Node<E>) NEXT_TERMINATOR;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ConcurrentLinkedDeque$Node.class */
    public static final class Node<E> {
        volatile Node<E> prev;
        volatile E item;
        volatile Node<E> next;
        private static final Unsafe UNSAFE;
        private static final long prevOffset;
        private static final long itemOffset;
        private static final long nextOffset;

        Node() {
        }

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

        void lazySetPrev(Node<E> val) {
            UNSAFE.putOrderedObject(this, prevOffset, val);
        }

        boolean casPrev(Node<E> cmp, Node<E> val) {
            return UNSAFE.compareAndSwapObject(this, prevOffset, cmp, val);
        }

        static {
            try {
                UNSAFE = Unsafe.getUnsafe();
                prevOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("prev"));
                itemOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset(Node.class.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    private void linkFirst(E e) {
        Node<E> h;
        Node<E> p;
        checkNotNull(e);
        Node<E> newNode = new Node<>(e);
        loop0: while (true) {
            h = this.head;
            p = h;
            while (true) {
                Node<E> q = p.prev;
                if (q != null) {
                    p = q;
                    Node<E> q2 = q.prev;
                    if (q2 != null) {
                        Node<E> node = h;
                        Node<E> node2 = this.head;
                        h = node2;
                        p = node != node2 ? h : q2;
                    }
                }
                if (p.next == p) {
                    break;
                }
                newNode.lazySetNext(p);
                if (p.casPrev(null, newNode)) {
                    break loop0;
                }
            }
        }
        if (p != h) {
            casHead(h, newNode);
        }
    }

    private void linkLast(E e) {
        Node<E> t;
        Node<E> p;
        checkNotNull(e);
        Node<E> newNode = new Node<>(e);
        loop0: while (true) {
            t = this.tail;
            p = t;
            while (true) {
                Node<E> q = p.next;
                if (q != null) {
                    p = q;
                    Node<E> q2 = q.next;
                    if (q2 != null) {
                        Node<E> node = t;
                        Node<E> node2 = this.tail;
                        t = node2;
                        p = node != node2 ? t : q2;
                    }
                }
                if (p.prev == p) {
                    break;
                }
                newNode.lazySetPrev(p);
                if (p.casNext(null, newNode)) {
                    break loop0;
                }
            }
        }
        if (p != t) {
            casTail(t, newNode);
        }
    }

    void unlink(Node<E> x) {
        Node<E> activePred;
        boolean isFirst;
        Node<E> activeSucc;
        boolean isLast;
        Node<E> prev = x.prev;
        Node<E> next = x.next;
        if (prev == null) {
            unlinkFirst(x, next);
        } else if (next == null) {
            unlinkLast(x, prev);
        } else {
            int hops = 1;
            Node<E> p = prev;
            while (true) {
                if (p.item != null) {
                    activePred = p;
                    isFirst = false;
                    break;
                }
                Node<E> q = p.prev;
                if (q == null) {
                    if (p.next == p) {
                        return;
                    }
                    activePred = p;
                    isFirst = true;
                } else if (p == q) {
                    return;
                } else {
                    p = q;
                    hops++;
                }
            }
            Node<E> p2 = next;
            while (true) {
                if (p2.item != null) {
                    activeSucc = p2;
                    isLast = false;
                    break;
                }
                Node<E> q2 = p2.next;
                if (q2 == null) {
                    if (p2.prev == p2) {
                        return;
                    }
                    activeSucc = p2;
                    isLast = true;
                } else if (p2 == q2) {
                    return;
                } else {
                    p2 = q2;
                    hops++;
                }
            }
            if (hops < 2 && (isFirst | isLast)) {
                return;
            }
            skipDeletedSuccessors(activePred);
            skipDeletedPredecessors(activeSucc);
            if ((isFirst | isLast) && activePred.next == activeSucc && activeSucc.prev == activePred) {
                if (isFirst) {
                    if (activePred.prev != null) {
                        return;
                    }
                } else if (activePred.item == null) {
                    return;
                }
                if (isLast) {
                    if (activeSucc.next != null) {
                        return;
                    }
                } else if (activeSucc.item == null) {
                    return;
                }
                updateHead();
                updateTail();
                x.lazySetPrev(isFirst ? prevTerminator() : x);
                x.lazySetNext(isLast ? nextTerminator() : x);
            }
        }
    }

    private void unlinkFirst(Node<E> first, Node<E> next) {
        Node<E> p;
        Node<E> q;
        Node<E> o = null;
        Node<E> node = next;
        while (true) {
            p = node;
            if (p.item != null || (q = p.next) == null) {
                break;
            } else if (p == q) {
                return;
            } else {
                o = p;
                node = q;
            }
        }
        if (o != null && p.prev != p && first.casNext(next, p)) {
            skipDeletedPredecessors(p);
            if (first.prev == null) {
                if ((p.next == null || p.item != null) && p.prev == first) {
                    updateHead();
                    updateTail();
                    o.lazySetNext(o);
                    o.lazySetPrev(prevTerminator());
                }
            }
        }
    }

    private void unlinkLast(Node<E> last, Node<E> prev) {
        Node<E> p;
        Node<E> q;
        Node<E> o = null;
        Node<E> node = prev;
        while (true) {
            p = node;
            if (p.item != null || (q = p.prev) == null) {
                break;
            } else if (p == q) {
                return;
            } else {
                o = p;
                node = q;
            }
        }
        if (o != null && p.next != p && last.casPrev(prev, p)) {
            skipDeletedSuccessors(p);
            if (last.next == null) {
                if ((p.prev == null || p.item != null) && p.next == last) {
                    updateHead();
                    updateTail();
                    o.lazySetPrev(o);
                    o.lazySetNext(nextTerminator());
                }
            }
        }
    }

    private final void updateHead() {
        while (true) {
            Node<E> h = this.head;
            if (h.item != null) {
                return;
            }
            Node<E> node = h.prev;
            Node<E> p = node;
            if (node != null) {
                while (true) {
                    Node<E> q = p.prev;
                    if (q == null) {
                        break;
                    }
                    p = q;
                    Node<E> q2 = q.prev;
                    if (q2 == null) {
                        break;
                    } else if (h != this.head) {
                        break;
                    } else {
                        p = q2;
                    }
                }
                if (casHead(h, p)) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private final void updateTail() {
        while (true) {
            Node<E> t = this.tail;
            if (t.item != null) {
                return;
            }
            Node<E> node = t.next;
            Node<E> p = node;
            if (node != null) {
                while (true) {
                    Node<E> q = p.next;
                    if (q == null) {
                        break;
                    }
                    p = q;
                    Node<E> q2 = q.next;
                    if (q2 == null) {
                        break;
                    } else if (t != this.tail) {
                        break;
                    } else {
                        p = q2;
                    }
                }
                if (casTail(t, p)) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0021, code lost:
        if (r7.next == r7) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void skipDeletedPredecessors(java.util.concurrent.ConcurrentLinkedDeque.Node<E> r5) {
        /*
            r4 = this;
        L0:
            r0 = r5
            java.util.concurrent.ConcurrentLinkedDeque$Node<E> r0 = r0.prev
            r6 = r0
            r0 = r6
            r7 = r0
        L7:
            r0 = r7
            E r0 = r0.item
            if (r0 == 0) goto L11
            goto L36
        L11:
            r0 = r7
            java.util.concurrent.ConcurrentLinkedDeque$Node<E> r0 = r0.prev
            r8 = r0
            r0 = r8
            if (r0 != 0) goto L27
            r0 = r7
            java.util.concurrent.ConcurrentLinkedDeque$Node<E> r0 = r0.next
            r1 = r7
            if (r0 != r1) goto L36
            goto L45
        L27:
            r0 = r7
            r1 = r8
            if (r0 != r1) goto L30
            goto L45
        L30:
            r0 = r8
            r7 = r0
            goto L7
        L36:
            r0 = r6
            r1 = r7
            if (r0 == r1) goto L44
            r0 = r5
            r1 = r6
            r2 = r7
            boolean r0 = r0.casPrev(r1, r2)
            if (r0 == 0) goto L45
        L44:
            return
        L45:
            r0 = r5
            E r0 = r0.item
            if (r0 != 0) goto L0
            r0 = r5
            java.util.concurrent.ConcurrentLinkedDeque$Node<E> r0 = r0.next
            if (r0 == 0) goto L0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentLinkedDeque.skipDeletedPredecessors(java.util.concurrent.ConcurrentLinkedDeque$Node):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0021, code lost:
        if (r7.prev == r7) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void skipDeletedSuccessors(java.util.concurrent.ConcurrentLinkedDeque.Node<E> r5) {
        /*
            r4 = this;
        L0:
            r0 = r5
            java.util.concurrent.ConcurrentLinkedDeque$Node<E> r0 = r0.next
            r6 = r0
            r0 = r6
            r7 = r0
        L7:
            r0 = r7
            E r0 = r0.item
            if (r0 == 0) goto L11
            goto L36
        L11:
            r0 = r7
            java.util.concurrent.ConcurrentLinkedDeque$Node<E> r0 = r0.next
            r8 = r0
            r0 = r8
            if (r0 != 0) goto L27
            r0 = r7
            java.util.concurrent.ConcurrentLinkedDeque$Node<E> r0 = r0.prev
            r1 = r7
            if (r0 != r1) goto L36
            goto L45
        L27:
            r0 = r7
            r1 = r8
            if (r0 != r1) goto L30
            goto L45
        L30:
            r0 = r8
            r7 = r0
            goto L7
        L36:
            r0 = r6
            r1 = r7
            if (r0 == r1) goto L44
            r0 = r5
            r1 = r6
            r2 = r7
            boolean r0 = r0.casNext(r1, r2)
            if (r0 == 0) goto L45
        L44:
            return
        L45:
            r0 = r5
            E r0 = r0.item
            if (r0 != 0) goto L0
            r0 = r5
            java.util.concurrent.ConcurrentLinkedDeque$Node<E> r0 = r0.prev
            if (r0 == 0) goto L0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentLinkedDeque.skipDeletedSuccessors(java.util.concurrent.ConcurrentLinkedDeque$Node):void");
    }

    final Node<E> succ(Node<E> p) {
        Node<E> q = p.next;
        return p == q ? first() : q;
    }

    final Node<E> pred(Node<E> p) {
        Node<E> q = p.prev;
        return p == q ? last() : q;
    }

    Node<E> first() {
        Node<E> h;
        Node<E> p;
        do {
            h = this.head;
            Node<E> node = h;
            while (true) {
                p = node;
                Node<E> q = p.prev;
                if (q == null) {
                    break;
                }
                p = q;
                Node<E> q2 = q.prev;
                if (q2 == null) {
                    break;
                }
                Node<E> node2 = h;
                Node<E> node3 = this.head;
                h = node3;
                node = node2 != node3 ? h : q2;
            }
            if (p == h) {
                break;
            }
        } while (!casHead(h, p));
        return p;
    }

    Node<E> last() {
        Node<E> t;
        Node<E> p;
        do {
            t = this.tail;
            Node<E> node = t;
            while (true) {
                p = node;
                Node<E> q = p.next;
                if (q == null) {
                    break;
                }
                p = q;
                Node<E> q2 = q.next;
                if (q2 == null) {
                    break;
                }
                Node<E> node2 = t;
                Node<E> node3 = this.tail;
                t = node3;
                node = node2 != node3 ? t : q2;
            }
            if (p == t) {
                break;
            }
        } while (!casTail(t, p));
        return p;
    }

    private static void checkNotNull(Object v) {
        if (v == null) {
            throw new NullPointerException();
        }
    }

    private E screenNullResult(E v) {
        if (v == null) {
            throw new NoSuchElementException();
        }
        return v;
    }

    private ArrayList<E> toArrayList() {
        ArrayList<E> list = new ArrayList<>();
        Node<E> first = first();
        while (true) {
            Node<E> p = first;
            if (p != null) {
                E item = p.item;
                if (item != null) {
                    list.add(item);
                }
                first = succ(p);
            } else {
                return list;
            }
        }
    }

    public ConcurrentLinkedDeque() {
        Node<E> node = new Node<>(null);
        this.tail = node;
        this.head = node;
    }

    public ConcurrentLinkedDeque(Collection<? extends E> c) {
        Node<E> h = null;
        Node<E> t = null;
        for (E e : c) {
            checkNotNull(e);
            Node<E> newNode = new Node<>(e);
            if (h == null) {
                t = newNode;
                h = newNode;
            } else {
                t.lazySetNext(newNode);
                newNode.lazySetPrev(t);
                t = newNode;
            }
        }
        initHeadTail(h, t);
    }

    private void initHeadTail(Node<E> h, Node<E> t) {
        if (h == t) {
            if (h == null) {
                Node<E> node = new Node<>(null);
                t = node;
                h = node;
            } else {
                Node<E> newNode = new Node<>(null);
                t.lazySetNext(newNode);
                newNode.lazySetPrev(t);
                t = newNode;
            }
        }
        this.head = h;
        this.tail = t;
    }

    @Override // java.util.Deque
    public void addFirst(E e) {
        linkFirst(e);
    }

    @Override // java.util.Deque
    public void addLast(E e) {
        linkLast(e);
    }

    @Override // java.util.Deque
    public boolean offerFirst(E e) {
        linkFirst(e);
        return true;
    }

    @Override // java.util.Deque
    public boolean offerLast(E e) {
        linkLast(e);
        return true;
    }

    @Override // java.util.Deque
    public E peekFirst() {
        Node<E> first = first();
        while (true) {
            Node<E> p = first;
            if (p != null) {
                E item = p.item;
                if (item == null) {
                    first = succ(p);
                } else {
                    return item;
                }
            } else {
                return null;
            }
        }
    }

    @Override // java.util.Deque
    public E peekLast() {
        Node<E> last = last();
        while (true) {
            Node<E> p = last;
            if (p != null) {
                E item = p.item;
                if (item == null) {
                    last = pred(p);
                } else {
                    return item;
                }
            } else {
                return null;
            }
        }
    }

    @Override // java.util.Deque
    public E getFirst() {
        return screenNullResult(peekFirst());
    }

    @Override // java.util.Deque
    public E getLast() {
        return screenNullResult(peekLast());
    }

    @Override // java.util.Deque
    public E pollFirst() {
        Node<E> first = first();
        while (true) {
            Node<E> p = first;
            if (p != null) {
                E item = p.item;
                if (item == null || !p.casItem(item, null)) {
                    first = succ(p);
                } else {
                    unlink(p);
                    return item;
                }
            } else {
                return null;
            }
        }
    }

    @Override // java.util.Deque
    public E pollLast() {
        Node<E> last = last();
        while (true) {
            Node<E> p = last;
            if (p != null) {
                E item = p.item;
                if (item == null || !p.casItem(item, null)) {
                    last = pred(p);
                } else {
                    unlink(p);
                    return item;
                }
            } else {
                return null;
            }
        }
    }

    @Override // java.util.Deque
    public E removeFirst() {
        return screenNullResult(pollFirst());
    }

    @Override // java.util.Deque
    public E removeLast() {
        return screenNullResult(pollLast());
    }

    @Override // java.util.Deque, java.util.Queue
    public boolean offer(E e) {
        return offerLast(e);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean add(E e) {
        return offerLast(e);
    }

    @Override // java.util.Deque, java.util.Queue
    public E poll() {
        return pollFirst();
    }

    @Override // java.util.Deque, java.util.Queue
    public E remove() {
        return removeFirst();
    }

    @Override // java.util.Deque, java.util.Queue
    public E peek() {
        return peekFirst();
    }

    @Override // java.util.Deque, java.util.Queue
    public E element() {
        return getFirst();
    }

    @Override // java.util.Deque
    public void push(E e) {
        addFirst(e);
    }

    @Override // java.util.Deque
    public E pop() {
        return removeFirst();
    }

    @Override // java.util.Deque
    public boolean removeFirstOccurrence(Object o) {
        checkNotNull(o);
        Node<E> first = first();
        while (true) {
            Node<E> p = first;
            if (p != null) {
                E item = p.item;
                if (item == null || !o.equals(item) || !p.casItem(item, null)) {
                    first = succ(p);
                } else {
                    unlink(p);
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    @Override // java.util.Deque
    public boolean removeLastOccurrence(Object o) {
        checkNotNull(o);
        Node<E> last = last();
        while (true) {
            Node<E> p = last;
            if (p != null) {
                E item = p.item;
                if (item == null || !o.equals(item) || !p.casItem(item, null)) {
                    last = pred(p);
                } else {
                    unlink(p);
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        Node<E> first = first();
        while (true) {
            Node<E> p = first;
            if (p != null) {
                E item = p.item;
                if (item == null || !o.equals(item)) {
                    first = succ(p);
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        return peekFirst() == null;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        int count = 0;
        Node<E> first = first();
        while (true) {
            Node<E> p = first;
            if (p == null) {
                break;
            }
            if (p.item != null) {
                count++;
                if (count == Integer.MAX_VALUE) {
                    break;
                }
            }
            first = succ(p);
        }
        return count;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean addAll(Collection<? extends E> c) {
        Node<E> t;
        if (c == this) {
            throw new IllegalArgumentException();
        }
        Node<E> beginningOfTheEnd = null;
        Node<E> last = null;
        for (E e : c) {
            checkNotNull(e);
            Node<E> newNode = new Node<>(e);
            if (beginningOfTheEnd == null) {
                last = newNode;
                beginningOfTheEnd = newNode;
            } else {
                last.lazySetNext(newNode);
                newNode.lazySetPrev(last);
                last = newNode;
            }
        }
        if (beginningOfTheEnd == null) {
            return false;
        }
        loop1: while (true) {
            t = this.tail;
            Node<E> p = t;
            while (true) {
                Node<E> q = p.next;
                if (q != null) {
                    p = q;
                    Node<E> q2 = q.next;
                    if (q2 != null) {
                        Node<E> node = t;
                        Node<E> node2 = this.tail;
                        t = node2;
                        p = node != node2 ? t : q2;
                    }
                }
                if (p.prev == p) {
                    break;
                }
                beginningOfTheEnd.lazySetPrev(p);
                if (p.casNext(null, beginningOfTheEnd)) {
                    break loop1;
                }
            }
        }
        if (!casTail(t, last)) {
            Node<E> t2 = this.tail;
            if (last.next == null) {
                casTail(t2, last);
                return true;
            }
            return true;
        }
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        do {
        } while (pollFirst() != null);
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public Object[] toArray() {
        return toArrayList().toArray();
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public <T> T[] toArray(T[] a) {
        return (T[]) toArrayList().toArray(a);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override // java.util.Deque
    public Iterator<E> descendingIterator() {
        return new DescendingItr();
    }

    /* loaded from: ConcurrentLinkedDeque$AbstractItr.class */
    private abstract class AbstractItr implements Iterator<E> {
        private Node<E> nextNode;
        private E nextItem;
        private Node<E> lastRet;

        abstract Node<E> startNode();

        abstract Node<E> nextNode(Node<E> node);

        AbstractItr() {
            advance();
        }

        private void advance() {
            this.lastRet = this.nextNode;
            Node<E> startNode = this.nextNode == null ? startNode() : nextNode(this.nextNode);
            while (true) {
                Node<E> p = startNode;
                if (p == null) {
                    this.nextNode = null;
                    this.nextItem = null;
                    return;
                }
                E item = p.item;
                if (item == null) {
                    startNode = nextNode(p);
                } else {
                    this.nextNode = p;
                    this.nextItem = item;
                    return;
                }
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.nextItem != null;
        }

        @Override // java.util.Iterator
        public E next() {
            E item = this.nextItem;
            if (item == null) {
                throw new NoSuchElementException();
            }
            advance();
            return item;
        }

        @Override // java.util.Iterator
        public void remove() {
            Node<E> l = this.lastRet;
            if (l == null) {
                throw new IllegalStateException();
            }
            l.item = null;
            ConcurrentLinkedDeque.this.unlink(l);
            this.lastRet = null;
        }
    }

    /* loaded from: ConcurrentLinkedDeque$Itr.class */
    private class Itr extends AbstractItr {
        private Itr() {
            super();
        }

        @Override // java.util.concurrent.ConcurrentLinkedDeque.AbstractItr
        Node<E> startNode() {
            return ConcurrentLinkedDeque.this.first();
        }

        @Override // java.util.concurrent.ConcurrentLinkedDeque.AbstractItr
        Node<E> nextNode(Node<E> p) {
            return ConcurrentLinkedDeque.this.succ(p);
        }
    }

    /* loaded from: ConcurrentLinkedDeque$DescendingItr.class */
    private class DescendingItr extends AbstractItr {
        private DescendingItr() {
            super();
        }

        @Override // java.util.concurrent.ConcurrentLinkedDeque.AbstractItr
        Node<E> startNode() {
            return ConcurrentLinkedDeque.this.last();
        }

        @Override // java.util.concurrent.ConcurrentLinkedDeque.AbstractItr
        Node<E> nextNode(Node<E> p) {
            return ConcurrentLinkedDeque.this.pred(p);
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        Node<E> first = first();
        while (true) {
            Node<E> p = first;
            if (p != null) {
                E item = p.item;
                if (item != null) {
                    s.writeObject(item);
                }
                first = succ(p);
            } else {
                s.writeObject(null);
                return;
            }
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        Node<E> h = null;
        Node<E> t = null;
        while (true) {
            Object item = s.readObject();
            if (item != null) {
                Node<E> newNode = new Node<>(item);
                if (h == null) {
                    t = newNode;
                    h = newNode;
                } else {
                    t.lazySetNext(newNode);
                    newNode.lazySetPrev(t);
                    t = newNode;
                }
            } else {
                initHeadTail(h, t);
                return;
            }
        }
    }

    private boolean casHead(Node<E> cmp, Node<E> val) {
        return UNSAFE.compareAndSwapObject(this, headOffset, cmp, val);
    }

    private boolean casTail(Node<E> cmp, Node<E> val) {
        return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
    }

    static {
        PREV_TERMINATOR.next = (Node<E>) PREV_TERMINATOR;
        NEXT_TERMINATOR = new Node<>();
        NEXT_TERMINATOR.prev = (Node<E>) NEXT_TERMINATOR;
        try {
            UNSAFE = Unsafe.getUnsafe();
            headOffset = UNSAFE.objectFieldOffset(ConcurrentLinkedDeque.class.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset(ConcurrentLinkedDeque.class.getDeclaredField("tail"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}