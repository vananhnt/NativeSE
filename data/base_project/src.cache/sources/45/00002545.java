package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractList.class */
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
    protected transient int modCount;

    public abstract E get(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractList() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public void add(int location, E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean add(E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public boolean addAll(int location, Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public int indexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public int lastIndexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public E remove(int location) {
        throw new RuntimeException("Stub!");
    }

    protected void removeRange(int start, int end) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public E set(int location, E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.List
    public List<E> subList(int start, int end) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: AbstractList$SimpleListIterator.class */
    private class SimpleListIterator implements Iterator<E> {
        int expectedModCount;
        int pos = -1;
        int lastPosition = -1;

        SimpleListIterator() {
            this.expectedModCount = AbstractList.this.modCount;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.pos + 1 < AbstractList.this.size();
        }

        @Override // java.util.Iterator
        public E next() {
            if (this.expectedModCount == AbstractList.this.modCount) {
                try {
                    E result = (E) AbstractList.this.get(this.pos + 1);
                    int i = this.pos + 1;
                    this.pos = i;
                    this.lastPosition = i;
                    return result;
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.lastPosition == -1) {
                throw new IllegalStateException();
            }
            if (this.expectedModCount != AbstractList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            try {
                AbstractList.this.remove(this.lastPosition);
                this.expectedModCount = AbstractList.this.modCount;
                if (this.pos == this.lastPosition) {
                    this.pos--;
                }
                this.lastPosition = -1;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /* loaded from: AbstractList$FullListIterator.class */
    private final class FullListIterator extends AbstractList<E>.SimpleListIterator implements ListIterator<E> {
        FullListIterator(int start) {
            super();
            if (start >= 0 && start <= AbstractList.this.size()) {
                this.pos = start - 1;
                return;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override // java.util.ListIterator
        public void add(E object) {
            if (this.expectedModCount == AbstractList.this.modCount) {
                try {
                    AbstractList.this.add(this.pos + 1, object);
                    this.pos++;
                    this.lastPosition = -1;
                    if (AbstractList.this.modCount != this.expectedModCount) {
                        this.expectedModCount = AbstractList.this.modCount;
                        return;
                    }
                    return;
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.ListIterator
        public boolean hasPrevious() {
            return this.pos >= 0;
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            return this.pos + 1;
        }

        @Override // java.util.ListIterator
        public E previous() {
            if (this.expectedModCount == AbstractList.this.modCount) {
                try {
                    E result = (E) AbstractList.this.get(this.pos);
                    this.lastPosition = this.pos;
                    this.pos--;
                    return result;
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            return this.pos;
        }

        @Override // java.util.ListIterator
        public void set(E object) {
            if (this.expectedModCount == AbstractList.this.modCount) {
                try {
                    AbstractList.this.set(this.lastPosition, object);
                    return;
                } catch (IndexOutOfBoundsException e) {
                    throw new IllegalStateException();
                }
            }
            throw new ConcurrentModificationException();
        }
    }

    /* loaded from: AbstractList$SubAbstractListRandomAccess.class */
    private static final class SubAbstractListRandomAccess<E> extends SubAbstractList<E> implements RandomAccess {
        SubAbstractListRandomAccess(AbstractList<E> list, int start, int end) {
            super(list, start, end);
        }
    }

    /* loaded from: AbstractList$SubAbstractList.class */
    private static class SubAbstractList<E> extends AbstractList<E> {
        private final AbstractList<E> fullList;
        private int offset;
        private int size;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: AbstractList$SubAbstractList$SubAbstractListIterator.class */
        public static final class SubAbstractListIterator<E> implements ListIterator<E> {
            private final SubAbstractList<E> subList;
            private final ListIterator<E> iterator;
            private int start;
            private int end;

            SubAbstractListIterator(ListIterator<E> it, SubAbstractList<E> list, int offset, int length) {
                this.iterator = it;
                this.subList = list;
                this.start = offset;
                this.end = this.start + length;
            }

            @Override // java.util.ListIterator
            public void add(E object) {
                this.iterator.add(object);
                this.subList.sizeChanged(true);
                this.end++;
            }

            @Override // java.util.ListIterator, java.util.Iterator
            public boolean hasNext() {
                return this.iterator.nextIndex() < this.end;
            }

            @Override // java.util.ListIterator
            public boolean hasPrevious() {
                return this.iterator.previousIndex() >= this.start;
            }

            @Override // java.util.ListIterator, java.util.Iterator
            public E next() {
                if (this.iterator.nextIndex() < this.end) {
                    return this.iterator.next();
                }
                throw new NoSuchElementException();
            }

            @Override // java.util.ListIterator
            public int nextIndex() {
                return this.iterator.nextIndex() - this.start;
            }

            @Override // java.util.ListIterator
            public E previous() {
                if (this.iterator.previousIndex() >= this.start) {
                    return this.iterator.previous();
                }
                throw new NoSuchElementException();
            }

            @Override // java.util.ListIterator
            public int previousIndex() {
                int previous = this.iterator.previousIndex();
                if (previous >= this.start) {
                    return previous - this.start;
                }
                return -1;
            }

            @Override // java.util.ListIterator, java.util.Iterator
            public void remove() {
                this.iterator.remove();
                this.subList.sizeChanged(false);
                this.end--;
            }

            @Override // java.util.ListIterator
            public void set(E object) {
                this.iterator.set(object);
            }
        }

        SubAbstractList(AbstractList<E> list, int start, int end) {
            this.fullList = list;
            this.modCount = this.fullList.modCount;
            this.offset = start;
            this.size = end - start;
        }

        @Override // java.util.AbstractList, java.util.List
        public void add(int location, E object) {
            if (this.modCount == this.fullList.modCount) {
                if (location >= 0 && location <= this.size) {
                    this.fullList.add(location + this.offset, object);
                    this.size++;
                    this.modCount = this.fullList.modCount;
                    return;
                }
                throw new IndexOutOfBoundsException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.AbstractList, java.util.List
        public boolean addAll(int location, Collection<? extends E> collection) {
            if (this.modCount == this.fullList.modCount) {
                if (location >= 0 && location <= this.size) {
                    boolean result = this.fullList.addAll(location + this.offset, collection);
                    if (result) {
                        this.size += collection.size();
                        this.modCount = this.fullList.modCount;
                    }
                    return result;
                }
                throw new IndexOutOfBoundsException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean addAll(Collection<? extends E> collection) {
            if (this.modCount == this.fullList.modCount) {
                boolean result = this.fullList.addAll(this.offset + this.size, collection);
                if (result) {
                    this.size += collection.size();
                    this.modCount = this.fullList.modCount;
                }
                return result;
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int location) {
            if (this.modCount == this.fullList.modCount) {
                if (location >= 0 && location < this.size) {
                    return this.fullList.get(location + this.offset);
                }
                throw new IndexOutOfBoundsException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return listIterator(0);
        }

        @Override // java.util.AbstractList, java.util.List
        public ListIterator<E> listIterator(int location) {
            if (this.modCount == this.fullList.modCount) {
                if (location >= 0 && location <= this.size) {
                    return new SubAbstractListIterator(this.fullList.listIterator(location + this.offset), this, this.offset, this.size);
                }
                throw new IndexOutOfBoundsException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.AbstractList, java.util.List
        public E remove(int location) {
            if (this.modCount == this.fullList.modCount) {
                if (location >= 0 && location < this.size) {
                    E result = this.fullList.remove(location + this.offset);
                    this.size--;
                    this.modCount = this.fullList.modCount;
                    return result;
                }
                throw new IndexOutOfBoundsException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.AbstractList
        protected void removeRange(int start, int end) {
            if (start != end) {
                if (this.modCount == this.fullList.modCount) {
                    this.fullList.removeRange(start + this.offset, end + this.offset);
                    this.size -= end - start;
                    this.modCount = this.fullList.modCount;
                    return;
                }
                throw new ConcurrentModificationException();
            }
        }

        @Override // java.util.AbstractList, java.util.List
        public E set(int location, E object) {
            if (this.modCount == this.fullList.modCount) {
                if (location >= 0 && location < this.size) {
                    return this.fullList.set(location + this.offset, object);
                }
                throw new IndexOutOfBoundsException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            if (this.modCount == this.fullList.modCount) {
                return this.size;
            }
            throw new ConcurrentModificationException();
        }

        void sizeChanged(boolean increment) {
            if (increment) {
                this.size++;
            } else {
                this.size--;
            }
            this.modCount = this.fullList.modCount;
        }
    }
}