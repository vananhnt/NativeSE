package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CopyOnWriteArrayList.class */
public class CopyOnWriteArrayList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
    public CopyOnWriteArrayList() {
        throw new RuntimeException("Stub!");
    }

    public CopyOnWriteArrayList(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    public CopyOnWriteArrayList(E[] array) {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public E get(int index) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> collection) {
        throw new RuntimeException("Stub!");
    }

    public int indexOf(E object, int from) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public int indexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    public int lastIndexOf(E object, int to) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public int lastIndexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int index) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public List<E> subList(int from, int to) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public <T> T[] toArray(T[] contents) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public synchronized boolean add(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public synchronized void add(int index, E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public synchronized boolean addAll(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public synchronized boolean addAll(int index, Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    public synchronized int addAllAbsent(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    public synchronized boolean addIfAbsent(E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public synchronized void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public synchronized E remove(int index) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public synchronized boolean remove(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public synchronized boolean removeAll(Collection<?> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List, java.util.Collection
    public synchronized boolean retainAll(Collection<?> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public synchronized E set(int index, E e) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: CopyOnWriteArrayList$CowSubList.class */
    class CowSubList extends AbstractList<E> {
        private volatile Slice slice;

        public CowSubList(Object[] expectedElements, int from, int to) {
            this.slice = new Slice(expectedElements, from, to);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            Slice slice = this.slice;
            return slice.to - slice.from;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            Slice slice = this.slice;
            return slice.from == slice.to;
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int index) {
            Slice slice = this.slice;
            Object[] snapshot = CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this);
            slice.checkElementIndex(index);
            slice.checkConcurrentModification(snapshot);
            return (E) snapshot[index + slice.from];
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return listIterator(0);
        }

        @Override // java.util.AbstractList, java.util.List
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override // java.util.AbstractList, java.util.List
        public ListIterator<E> listIterator(int index) {
            Slice slice = this.slice;
            Object[] snapshot = CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this);
            slice.checkPositionIndex(index);
            slice.checkConcurrentModification(snapshot);
            CowIterator<E> result = new CowIterator<>(snapshot, slice.from, slice.to);
            ((CowIterator) result).index = slice.from + index;
            return result;
        }

        @Override // java.util.AbstractList, java.util.List
        public int indexOf(Object object) {
            Slice slice = this.slice;
            Object[] snapshot = CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this);
            slice.checkConcurrentModification(snapshot);
            int result = CopyOnWriteArrayList.indexOf(object, snapshot, slice.from, slice.to);
            if (result != -1) {
                return result - slice.from;
            }
            return -1;
        }

        @Override // java.util.AbstractList, java.util.List
        public int lastIndexOf(Object object) {
            Slice slice = this.slice;
            Object[] snapshot = CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this);
            slice.checkConcurrentModification(snapshot);
            int result = CopyOnWriteArrayList.lastIndexOf(object, snapshot, slice.from, slice.to);
            if (result != -1) {
                return result - slice.from;
            }
            return -1;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object object) {
            return indexOf(object) != -1;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            Slice slice = this.slice;
            Object[] snapshot = CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this);
            slice.checkConcurrentModification(snapshot);
            return CopyOnWriteArrayList.containsAll(collection, snapshot, slice.from, slice.to);
        }

        @Override // java.util.AbstractList, java.util.List
        public List<E> subList(int from, int to) {
            Slice slice = this.slice;
            if (from < 0 || from > to || to > size()) {
                throw new IndexOutOfBoundsException("from=" + from + ", to=" + to + ", list size=" + size());
            }
            return new CowSubList(slice.expectedElements, slice.from + from, slice.from + to);
        }

        @Override // java.util.AbstractList, java.util.List
        public E remove(int index) {
            E removed;
            synchronized (CopyOnWriteArrayList.this) {
                this.slice.checkElementIndex(index);
                this.slice.checkConcurrentModification(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this));
                removed = (E) CopyOnWriteArrayList.this.remove(this.slice.from + index);
                this.slice = new Slice(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this), this.slice.from, this.slice.to - 1);
            }
            return removed;
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection
        public void clear() {
            synchronized (CopyOnWriteArrayList.this) {
                this.slice.checkConcurrentModification(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this));
                CopyOnWriteArrayList.access$500(CopyOnWriteArrayList.this, this.slice.from, this.slice.to);
                this.slice = new Slice(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this), this.slice.from, this.slice.from);
            }
        }

        @Override // java.util.AbstractList, java.util.List
        public void add(int index, E object) {
            synchronized (CopyOnWriteArrayList.this) {
                this.slice.checkPositionIndex(index);
                this.slice.checkConcurrentModification(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this));
                CopyOnWriteArrayList.this.add(index + this.slice.from, object);
                this.slice = new Slice(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this), this.slice.from, this.slice.to + 1);
            }
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection
        public boolean add(E object) {
            synchronized (CopyOnWriteArrayList.this) {
                add(this.slice.to - this.slice.from, object);
            }
            return true;
        }

        @Override // java.util.AbstractList, java.util.List
        public boolean addAll(int index, Collection<? extends E> collection) {
            boolean result;
            synchronized (CopyOnWriteArrayList.this) {
                this.slice.checkPositionIndex(index);
                this.slice.checkConcurrentModification(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this));
                int oldSize = CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this).length;
                result = CopyOnWriteArrayList.this.addAll(index + this.slice.from, collection);
                this.slice = new Slice(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this), this.slice.from, this.slice.to + (CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this).length - oldSize));
            }
            return result;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean addAll(Collection<? extends E> collection) {
            boolean addAll;
            synchronized (CopyOnWriteArrayList.this) {
                addAll = addAll(size(), collection);
            }
            return addAll;
        }

        @Override // java.util.AbstractList, java.util.List
        public E set(int index, E object) {
            E result;
            synchronized (CopyOnWriteArrayList.this) {
                this.slice.checkElementIndex(index);
                this.slice.checkConcurrentModification(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this));
                result = (E) CopyOnWriteArrayList.this.set(index + this.slice.from, object);
                this.slice = new Slice(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this), this.slice.from, this.slice.to);
            }
            return result;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object object) {
            synchronized (CopyOnWriteArrayList.this) {
                int index = indexOf(object);
                if (index == -1) {
                    return false;
                }
                remove(index);
                return true;
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean removeAll(Collection<?> collection) {
            boolean z;
            synchronized (CopyOnWriteArrayList.this) {
                this.slice.checkConcurrentModification(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this));
                int removed = CopyOnWriteArrayList.access$600(CopyOnWriteArrayList.this, collection, false, this.slice.from, this.slice.to);
                this.slice = new Slice(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this), this.slice.from, this.slice.to - removed);
                z = removed != 0;
            }
            return z;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean retainAll(Collection<?> collection) {
            boolean z;
            synchronized (CopyOnWriteArrayList.this) {
                this.slice.checkConcurrentModification(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this));
                int removed = CopyOnWriteArrayList.access$600(CopyOnWriteArrayList.this, collection, true, this.slice.from, this.slice.to);
                this.slice = new Slice(CopyOnWriteArrayList.access$300(CopyOnWriteArrayList.this), this.slice.from, this.slice.to - removed);
                z = removed != 0;
            }
            return z;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: CopyOnWriteArrayList$Slice.class */
    public static class Slice {
        private final Object[] expectedElements;
        private final int from;
        private final int to;

        Slice(Object[] expectedElements, int from, int to) {
            this.expectedElements = expectedElements;
            this.from = from;
            this.to = to;
        }

        void checkElementIndex(int index) {
            if (index < 0 || index >= this.to - this.from) {
                throw new IndexOutOfBoundsException("index=" + index + ", size=" + (this.to - this.from));
            }
        }

        void checkPositionIndex(int index) {
            if (index < 0 || index > this.to - this.from) {
                throw new IndexOutOfBoundsException("index=" + index + ", size=" + (this.to - this.from));
            }
        }

        void checkConcurrentModification(Object[] snapshot) {
            if (this.expectedElements != snapshot) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: CopyOnWriteArrayList$CowIterator.class */
    public static class CowIterator<E> implements ListIterator<E> {
        private final Object[] snapshot;
        private final int from;
        private final int to;
        private int index;

        CowIterator(Object[] snapshot, int from, int to) {
            this.index = 0;
            this.snapshot = snapshot;
            this.from = from;
            this.to = to;
            this.index = from;
        }

        @Override // java.util.ListIterator
        public void add(E object) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return this.index < this.to;
        }

        @Override // java.util.ListIterator
        public boolean hasPrevious() {
            return this.index > this.from;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public E next() {
            if (this.index < this.to) {
                Object[] objArr = this.snapshot;
                int i = this.index;
                this.index = i + 1;
                return (E) objArr[i];
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            return this.index;
        }

        @Override // java.util.ListIterator
        public E previous() {
            if (this.index > this.from) {
                Object[] objArr = this.snapshot;
                int i = this.index - 1;
                this.index = i;
                return (E) objArr[i];
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            return this.index - 1;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.ListIterator
        public void set(E object) {
            throw new UnsupportedOperationException();
        }
    }
}