package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractCollection.class */
public abstract class AbstractCollection<E> implements Collection<E> {
    @Override // java.util.Collection, java.lang.Iterable
    public abstract Iterator<E> iterator();

    @Override // java.util.Collection, java.util.List
    public abstract int size();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCollection() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public boolean add(E object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends E> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public boolean contains(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public boolean containsAll(Collection<?> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public boolean remove(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public boolean removeAll(Collection<?> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public boolean retainAll(Collection<?> collection) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public Object[] toArray() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] contents) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}