package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NavigableSet.class */
public interface NavigableSet<E> extends SortedSet<E> {
    E lower(E e);

    E floor(E e);

    E ceiling(E e);

    E higher(E e);

    E pollFirst();

    E pollLast();

    @Override // java.util.Set, java.util.Collection, java.lang.Iterable
    Iterator<E> iterator();

    NavigableSet<E> descendingSet();

    Iterator<E> descendingIterator();

    NavigableSet<E> subSet(E e, boolean z, E e2, boolean z2);

    NavigableSet<E> headSet(E e, boolean z);

    NavigableSet<E> tailSet(E e, boolean z);

    @Override // java.util.SortedSet
    SortedSet<E> subSet(E e, E e2);

    @Override // java.util.SortedSet
    SortedSet<E> headSet(E e);

    @Override // java.util.SortedSet
    SortedSet<E> tailSet(E e);
}