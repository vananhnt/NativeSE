package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConcurrentSkipListSet.class */
public class ConcurrentSkipListSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, Serializable {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.NavigableSet, java.util.SortedSet
    public /* bridge */ /* synthetic */ SortedSet tailSet(Object x0) {
        return tailSet((ConcurrentSkipListSet<E>) x0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.NavigableSet, java.util.SortedSet
    public /* bridge */ /* synthetic */ SortedSet headSet(Object x0) {
        return headSet((ConcurrentSkipListSet<E>) x0);
    }

    public ConcurrentSkipListSet() {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentSkipListSet(Comparator<? super E> comparator) {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentSkipListSet(Collection<? extends E> c) {
        throw new RuntimeException("Stub!");
    }

    public ConcurrentSkipListSet(SortedSet<E> s) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: clone */
    public ConcurrentSkipListSet<E> m1360clone() {
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
    public boolean contains(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean add(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean remove(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
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

    @Override // java.util.AbstractSet, java.util.Collection
    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractSet, java.util.AbstractCollection, java.util.Collection
    public boolean removeAll(Collection<?> c) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E lower(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E floor(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E ceiling(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public E higher(E e) {
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

    @Override // java.util.SortedSet
    public Comparator<? super E> comparator() {
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
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet, java.util.SortedSet
    public NavigableSet<E> subSet(E fromElement, E toElement) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet, java.util.SortedSet
    public NavigableSet<E> headSet(E toElement) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet, java.util.SortedSet
    public NavigableSet<E> tailSet(E fromElement) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.NavigableSet
    public NavigableSet<E> descendingSet() {
        throw new RuntimeException("Stub!");
    }
}