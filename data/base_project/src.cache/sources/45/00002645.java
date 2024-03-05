package java.util;

import java.io.Serializable;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TreeSet.class */
public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, Serializable {
    public TreeSet() {
        throw new RuntimeException("Stub!");
    }

    public TreeSet(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    public TreeSet(Comparator<? super E> comparator) {
        throw new RuntimeException("Stub!");
    }

    public TreeSet(SortedSet<E> set) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean add(E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean addAll(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedSet
    public Comparator<? super E> comparator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean contains(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public Iterator<E> descendingIterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedSet
    public E first() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.SortedSet
    public E last() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E pollFirst() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E pollLast() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E higher(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E lower(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E ceiling(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E floor(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> descendingSet() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> subSet(E start, boolean startInclusive, E end, boolean endInclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> headSet(E end, boolean endInclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> tailSet(E start, boolean startInclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet, java.util.SortedSet
    public SortedSet<E> subSet(E start, E end) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet, java.util.SortedSet
    public SortedSet<E> headSet(E end) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet, java.util.SortedSet
    public SortedSet<E> tailSet(E start) {
        throw new RuntimeException("Stub!");
    }
}