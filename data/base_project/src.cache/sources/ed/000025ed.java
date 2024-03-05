package java.util;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LinkedList.class */
public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Queue<E>, Cloneable, Serializable {
    public LinkedList() {
        throw new RuntimeException("Stub!");
    }

    public LinkedList(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public void add(int location, E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection
    public boolean add(E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public boolean addAll(int location, Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean addAll(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    public void addFirst(E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public void addLast(E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public E get(int location) {
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

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public int lastIndexOf(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public ListIterator<E> listIterator(int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public E remove(int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object object) {
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
    public Iterator<E> descendingIterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public boolean offerFirst(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public boolean offerLast(E e) {
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

    @Override // java.util.Deque
    public E pollFirst() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E pollLast() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public E pop() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public void push(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public boolean removeFirstOccurrence(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque
    public boolean removeLastOccurrence(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractSequentialList, java.util.AbstractList, java.util.List
    public E set(int location, E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public boolean offer(E o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public E poll() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public E remove() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public E peek() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Deque, java.util.Queue
    public E element() {
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: LinkedList$Link.class */
    public static final class Link<ET> {
        ET data;
        Link<ET> previous;
        Link<ET> next;

        Link(ET o, Link<ET> p, Link<ET> n) {
            this.data = o;
            this.previous = p;
            this.next = n;
        }
    }

    /* loaded from: LinkedList$LinkIterator.class */
    private static final class LinkIterator<ET> implements ListIterator<ET> {
        int pos;
        int expectedModCount;
        final LinkedList<ET> list;
        Link<ET> link;
        Link<ET> lastLink;

        LinkIterator(LinkedList<ET> object, int location) {
            this.list = object;
            this.expectedModCount = this.list.modCount;
            if (location >= 0 && location <= this.list.size) {
                this.link = this.list.voidLink;
                if (location < this.list.size / 2) {
                    this.pos = -1;
                    while (this.pos + 1 < location) {
                        this.link = this.link.next;
                        this.pos++;
                    }
                    return;
                }
                this.pos = this.list.size;
                while (this.pos >= location) {
                    this.link = this.link.previous;
                    this.pos--;
                }
                return;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override // java.util.ListIterator
        public void add(ET object) {
            if (this.expectedModCount == this.list.modCount) {
                Link<ET> next = this.link.next;
                Link<ET> newLink = new Link<>(object, this.link, next);
                this.link.next = newLink;
                next.previous = newLink;
                this.link = newLink;
                this.lastLink = null;
                this.pos++;
                this.expectedModCount++;
                this.list.size++;
                this.list.modCount++;
                return;
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return this.link.next != this.list.voidLink;
        }

        @Override // java.util.ListIterator
        public boolean hasPrevious() {
            return this.link != this.list.voidLink;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public ET next() {
            if (this.expectedModCount == this.list.modCount) {
                Link<ET> next = this.link.next;
                if (next != this.list.voidLink) {
                    this.link = next;
                    this.lastLink = next;
                    this.pos++;
                    return this.link.data;
                }
                throw new NoSuchElementException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            return this.pos + 1;
        }

        @Override // java.util.ListIterator
        public ET previous() {
            if (this.expectedModCount == this.list.modCount) {
                if (this.link != this.list.voidLink) {
                    this.lastLink = this.link;
                    this.link = this.link.previous;
                    this.pos--;
                    return this.lastLink.data;
                }
                throw new NoSuchElementException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            return this.pos;
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public void remove() {
            if (this.expectedModCount == this.list.modCount) {
                if (this.lastLink != null) {
                    Link<ET> next = this.lastLink.next;
                    Link<ET> previous = this.lastLink.previous;
                    next.previous = previous;
                    previous.next = next;
                    if (this.lastLink == this.link) {
                        this.pos--;
                    }
                    this.link = previous;
                    this.lastLink = null;
                    this.expectedModCount++;
                    this.list.size--;
                    this.list.modCount++;
                    return;
                }
                throw new IllegalStateException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.ListIterator
        public void set(ET object) {
            if (this.expectedModCount == this.list.modCount) {
                if (this.lastLink != null) {
                    this.lastLink.data = object;
                    return;
                }
                throw new IllegalStateException();
            }
            throw new ConcurrentModificationException();
        }
    }

    /* loaded from: LinkedList$ReverseLinkIterator.class */
    private class ReverseLinkIterator<ET> implements Iterator<ET> {
        private int expectedModCount;
        private final LinkedList<ET> list;
        private Link<ET> link;
        private boolean canRemove = false;

        ReverseLinkIterator(LinkedList<ET> linkedList) {
            this.list = linkedList;
            this.expectedModCount = this.list.modCount;
            this.link = this.list.voidLink;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.link.previous != this.list.voidLink;
        }

        @Override // java.util.Iterator
        public ET next() {
            if (this.expectedModCount == this.list.modCount) {
                if (hasNext()) {
                    this.link = this.link.previous;
                    this.canRemove = true;
                    return this.link.data;
                }
                throw new NoSuchElementException();
            }
            throw new ConcurrentModificationException();
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.expectedModCount == this.list.modCount) {
                if (this.canRemove) {
                    Link<ET> next = this.link.previous;
                    Link<ET> previous = this.link.next;
                    next.next = previous;
                    previous.previous = next;
                    this.link = previous;
                    this.list.size--;
                    this.list.modCount++;
                    this.expectedModCount++;
                    this.canRemove = false;
                    return;
                }
                throw new IllegalStateException();
            }
            throw new ConcurrentModificationException();
        }
    }
}