package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Collection.class */
public interface Collection<E> extends Iterable<E> {
    boolean add(E e);

    boolean addAll(Collection<? extends E> collection);

    void clear();

    boolean contains(Object obj);

    boolean containsAll(Collection<?> collection);

    boolean equals(Object obj);

    int hashCode();

    boolean isEmpty();

    @Override // java.lang.Iterable
    Iterator<E> iterator();

    boolean remove(Object obj);

    boolean removeAll(Collection<?> collection);

    boolean retainAll(Collection<?> collection);

    int size();

    Object[] toArray();

    <T> T[] toArray(T[] tArr);
}