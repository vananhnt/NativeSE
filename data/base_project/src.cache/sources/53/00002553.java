package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractSequentialList.class */
public abstract class AbstractSequentialList<E> extends AbstractList<E> {
    @Override // java.util.AbstractList, java.util.List
    public abstract ListIterator<E> listIterator(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractSequentialList() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int location, E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public boolean addAll(int location, Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public E remove(int location) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractList, java.util.List
    public E set(int location, E object) {
        throw new RuntimeException("Stub!");
    }
}