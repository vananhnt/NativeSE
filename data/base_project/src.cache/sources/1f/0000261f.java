package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SortedSet.class */
public interface SortedSet<E> extends Set<E> {
    Comparator<? super E> comparator();

    E first();

    SortedSet<E> headSet(E e);

    E last();

    SortedSet<E> subSet(E e, E e2);

    SortedSet<E> tailSet(E e);
}