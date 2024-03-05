package java.util;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ArrayList.class */
public class ArrayList<E> extends AbstractList<E> implements Cloneable, Serializable, RandomAccess {
    public ArrayList(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public ArrayList() {
        throw new RuntimeException("Stub!");
    }

    public ArrayList(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection
    public boolean add(E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int index, E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean addAll(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public boolean addAll(int index, Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public void ensureCapacity(int minimumCapacity) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int index) {
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

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public E remove(int index) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList
    protected void removeRange(int fromIndex, int toIndex) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public E set(int index, E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public Object[] toArray() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public <T> T[] toArray(T[] contents) {
        throw new RuntimeException("Stub!");
    }

    public void trimToSize() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.Collection
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.Collection
    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: ArrayList$ArrayListIterator.class */
    private class ArrayListIterator implements Iterator<E> {
        private int remaining;
        private int removalIndex;
        private int expectedModCount;

        private ArrayListIterator() {
            this.remaining = ArrayList.this.size;
            this.removalIndex = -1;
            this.expectedModCount = ArrayList.this.modCount;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.remaining != 0;
        }

        @Override // java.util.Iterator
        public E next() {
            ArrayList<E> ourList = ArrayList.this;
            int rem = this.remaining;
            if (ourList.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (rem == 0) {
                throw new NoSuchElementException();
            }
            this.remaining = rem - 1;
            Object[] objArr = ourList.array;
            int i = ourList.size - rem;
            this.removalIndex = i;
            return (E) objArr[i];
        }

        @Override // java.util.Iterator
        public void remove() {
            Object[] a = ArrayList.this.array;
            int removalIdx = this.removalIndex;
            if (ArrayList.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (removalIdx < 0) {
                throw new IllegalStateException();
            }
            System.arraycopy(a, removalIdx + 1, a, removalIdx, this.remaining);
            ArrayList arrayList = ArrayList.this;
            int i = arrayList.size - 1;
            arrayList.size = i;
            a[i] = null;
            this.removalIndex = -1;
            ArrayList arrayList2 = ArrayList.this;
            int i2 = arrayList2.modCount + 1;
            arrayList2.modCount = i2;
            this.expectedModCount = i2;
        }
    }
}