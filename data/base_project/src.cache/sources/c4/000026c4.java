package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;

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
/* loaded from: LinkedBlockingQueue.class */
public class LinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
    public LinkedBlockingQueue() {
        throw new RuntimeException("Stub!");
    }

    public LinkedBlockingQueue(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public LinkedBlockingQueue(Collection<? extends E> c) {
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

    @Override // java.util.concurrent.BlockingQueue
    public void put(E e) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.concurrent.BlockingQueue
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
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

    @Override // java.util.Queue
    public E peek() {
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

    /* loaded from: LinkedBlockingQueue$Node.class */
    static class Node<E> {
        E item;
        Node<E> next;

        Node(E x) {
            this.item = x;
        }
    }

    /* loaded from: LinkedBlockingQueue$Itr.class */
    private class Itr implements Iterator<E> {
        private Node<E> current;
        private Node<E> lastRet;
        private E currentElement;
        final /* synthetic */ LinkedBlockingQueue this$0;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingQueue.Itr.<init>(java.util.concurrent.LinkedBlockingQueue):void, file: LinkedBlockingQueue$Itr.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        Itr(java.util.concurrent.LinkedBlockingQueue r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingQueue.Itr.<init>(java.util.concurrent.LinkedBlockingQueue):void, file: LinkedBlockingQueue$Itr.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.LinkedBlockingQueue.Itr.<init>(java.util.concurrent.LinkedBlockingQueue):void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingQueue.Itr.next():E, file: LinkedBlockingQueue$Itr.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.util.Iterator
        public E next() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingQueue.Itr.next():E, file: LinkedBlockingQueue$Itr.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.LinkedBlockingQueue.Itr.next():java.lang.Object");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingQueue.Itr.remove():void, file: LinkedBlockingQueue$Itr.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
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
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.util.concurrent.LinkedBlockingQueue.Itr.remove():void, file: LinkedBlockingQueue$Itr.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.LinkedBlockingQueue.Itr.remove():void");
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.current != null;
        }

        private Node<E> nextNode(Node<E> p) {
            Node<E> s;
            while (true) {
                s = p.next;
                if (s == p) {
                    return this.this$0.head.next;
                }
                if (s == null || s.item != null) {
                    break;
                }
                p = s;
            }
            return s;
        }
    }
}