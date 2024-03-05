package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Deque.class */
public interface Deque<E> extends Queue<E> {
    void addFirst(E e);

    void addLast(E e);

    boolean offerFirst(E e);

    boolean offerLast(E e);

    E removeFirst();

    E removeLast();

    E pollFirst();

    E pollLast();

    E getFirst();

    E getLast();

    E peekFirst();

    E peekLast();

    boolean removeFirstOccurrence(Object obj);

    boolean removeLastOccurrence(Object obj);

    @Override // java.util.Queue, java.util.Collection
    boolean add(E e);

    @Override // java.util.Queue
    boolean offer(E e);

    @Override // java.util.Queue
    E remove();

    @Override // java.util.Queue
    E poll();

    @Override // java.util.Queue
    E element();

    @Override // java.util.Queue
    E peek();

    void push(E e);

    E pop();

    @Override // java.util.Collection
    boolean remove(Object obj);

    @Override // java.util.Collection
    boolean contains(Object obj);

    @Override // java.util.Collection, java.util.List
    int size();

    @Override // java.util.Collection, java.lang.Iterable
    Iterator<E> iterator();

    Iterator<E> descendingIterator();
}