package java.util;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Vector.class */
public class Vector<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
    protected int elementCount;
    protected Object[] elementData = null;
    protected int capacityIncrement;

    public Vector() {
        throw new RuntimeException("Stub!");
    }

    public Vector(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public Vector(int capacity, int capacityIncrement) {
        throw new RuntimeException("Stub!");
    }

    public Vector(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int location, E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection
    public synchronized boolean add(E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public synchronized boolean addAll(int location, Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public synchronized boolean addAll(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void addElement(E object) {
        throw new RuntimeException("Stub!");
    }

    public synchronized int capacity() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Object clone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public synchronized boolean containsAll(Collection<?> collection) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void copyInto(Object[] elements) {
        throw new RuntimeException("Stub!");
    }

    public synchronized E elementAt(int location) {
        throw new RuntimeException("Stub!");
    }

    public Enumeration<E> elements() {
        throw new RuntimeException("Stub!");
    }

    public synchronized void ensureCapacity(int minimumCapacity) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.Collection
    public synchronized boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    public synchronized E firstElement() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.Collection
    public synchronized int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    public synchronized int indexOf(Object object, int location) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void insertElementAt(E object, int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public synchronized boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    public synchronized E lastElement() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public synchronized int lastIndexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    public synchronized int lastIndexOf(Object object, int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public synchronized E remove(int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public synchronized boolean removeAll(Collection<?> collection) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void removeAllElements() {
        throw new RuntimeException("Stub!");
    }

    public synchronized boolean removeElement(Object object) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void removeElementAt(int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList
    protected void removeRange(int start, int end) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public synchronized boolean retainAll(Collection<?> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public synchronized E set(int location, E object) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void setElementAt(E object, int location) {
        throw new RuntimeException("Stub!");
    }

    public synchronized void setSize(int length) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public synchronized int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public synchronized List<E> subList(int start, int end) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public synchronized Object[] toArray() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public synchronized <T> T[] toArray(T[] contents) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection
    public synchronized String toString() {
        throw new RuntimeException("Stub!");
    }

    public synchronized void trimToSize() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.Vector$1  reason: invalid class name */
    /* loaded from: Vector$1.class */
    class AnonymousClass1 implements Enumeration<E> {
        int pos = 0;

        AnonymousClass1() {
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.pos < Vector.this.elementCount;
        }

        @Override // java.util.Enumeration
        public E nextElement() {
            synchronized (Vector.this) {
                if (this.pos < Vector.this.elementCount) {
                    Object[] objArr = Vector.this.elementData;
                    int i = this.pos;
                    this.pos = i + 1;
                    return (E) objArr[i];
                }
                throw new NoSuchElementException();
            }
        }
    }
}