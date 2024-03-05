package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*  JADX ERROR: NullPointerException in pass: ExtractFieldInit
    java.lang.NullPointerException
    	at jadx.core.utils.BlockUtils.isAllBlocksEmpty(BlockUtils.java:977)
    	at jadx.core.dex.visitors.ExtractFieldInit.getConstructorsList(ExtractFieldInit.java:379)
    	at jadx.core.dex.visitors.ExtractFieldInit.moveCommonFieldsInit(ExtractFieldInit.java:128)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:51)
    	at jadx.core.dex.visitors.ExtractFieldInit.visit(ExtractFieldInit.java:47)
    */
/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LinkedBlockingDeque.class */
public class LinkedBlockingDeque<E> extends AbstractQueue<E> implements BlockingDeque<E>, Serializable {
    public LinkedBlockingDeque() {
        throw new RuntimeException("Stub!");
    }

    public LinkedBlockingDeque(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public LinkedBlockingDeque(Collection<? extends E> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque, java.util.Deque
    public void addFirst(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque, java.util.Deque
    public void addLast(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque, java.util.Deque
    public boolean offerFirst(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque, java.util.Deque
    public boolean offerLast(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque
    public void putFirst(E e) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque
    public void putLast(E e) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque
    public boolean offerFirst(E e, long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque
    public boolean offerLast(E e, long timeout, TimeUnit unit) throws InterruptedException {
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

    @Override // java.util.concurrent.BlockingDeque
    public E takeFirst() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque
    public E takeLast() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque
    public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque
    public E pollLast(long timeout, TimeUnit unit) throws InterruptedException {
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

    @Override // java.util.concurrent.BlockingDeque, java.util.Deque
    public boolean removeFirstOccurrence(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque, java.util.Deque
    public boolean removeLastOccurrence(Object o) {
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

    @Override // java.util.concurrent.BlockingDeque, java.util.concurrent.BlockingQueue
    public void put(E e) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque, java.util.concurrent.BlockingQueue
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractQueue, java.util.Queue
    public E remove() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public E poll() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque, java.util.concurrent.BlockingQueue
    public E take() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingDeque, java.util.concurrent.BlockingQueue
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractQueue, java.util.Queue
    public E element() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public E peek() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public int remainingCapacity() {
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

    @Override // java.util.concurrent.BlockingDeque, java.util.Deque
    public void push(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E pop() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object o) {
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

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public Iterator<E> descendingIterator() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LinkedBlockingDeque$Node.class */
    public static final class Node<E> {
        E item;
        Node<E> prev;
        Node<E> next;

        Node(E x) {
            this.item = x;
        }
    }

    /* loaded from: LinkedBlockingDeque$AbstractItr.class */
    private abstract class AbstractItr implements Iterator<E> {
        Node<E> next;
        E nextItem;
        private Node<E> lastRet;
        final /* synthetic */ LinkedBlockingDeque this$0;

        abstract Node<E> firstNode();

        abstract Node<E> nextNode(Node<E> node);

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingDeque.AbstractItr.<init>(java.util.concurrent.LinkedBlockingDeque):void, file: LinkedBlockingDeque$AbstractItr.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        AbstractItr(java.util.concurrent.LinkedBlockingDeque r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingDeque.AbstractItr.<init>(java.util.concurrent.LinkedBlockingDeque):void, file: LinkedBlockingDeque$AbstractItr.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.LinkedBlockingDeque.AbstractItr.<init>(java.util.concurrent.LinkedBlockingDeque):void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingDeque.AbstractItr.advance():void, file: LinkedBlockingDeque$AbstractItr.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        void advance() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingDeque.AbstractItr.advance():void, file: LinkedBlockingDeque$AbstractItr.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.LinkedBlockingDeque.AbstractItr.advance():void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingDeque.AbstractItr.remove():void, file: LinkedBlockingDeque$AbstractItr.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.Iterator
        public void remove() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingDeque.AbstractItr.remove():void, file: LinkedBlockingDeque$AbstractItr.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.LinkedBlockingDeque.AbstractItr.remove():void");
        }

        private Node<E> succ(Node<E> n) {
            while (true) {
                Node<E> s = nextNode(n);
                if (s == null) {
                    return null;
                }
                if (s.item != null) {
                    return s;
                }
                if (s == n) {
                    return firstNode();
                }
                n = s;
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.next != null;
        }

        @Override // java.util.Iterator
        public E next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.lastRet = this.next;
            E x = this.nextItem;
            advance();
            return x;
        }
    }

    /* loaded from: LinkedBlockingDeque$Itr.class */
    private class Itr extends AbstractItr {
        private Itr() {
            super(LinkedBlockingDeque.this);
        }

        @Override // java.util.concurrent.LinkedBlockingDeque.AbstractItr
        Node<E> firstNode() {
            return LinkedBlockingDeque.this.first;
        }

        @Override // java.util.concurrent.LinkedBlockingDeque.AbstractItr
        Node<E> nextNode(Node<E> n) {
            return n.next;
        }
    }

    /* loaded from: LinkedBlockingDeque$DescendingItr.class */
    private class DescendingItr extends AbstractItr {
        private DescendingItr() {
            super(LinkedBlockingDeque.this);
        }

        @Override // java.util.concurrent.LinkedBlockingDeque.AbstractItr
        Node<E> firstNode() {
            return LinkedBlockingDeque.this.last;
        }

        @Override // java.util.concurrent.LinkedBlockingDeque.AbstractItr
        Node<E> nextNode(Node<E> n) {
            return n.prev;
        }
    }
}