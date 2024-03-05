package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractQueue.class */
public abstract class AbstractQueue<E> extends AbstractCollection<E> implements Queue<E> {
    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractQueue() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean add(E e) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public E remove() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.Queue
    public E element() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public void clear() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public boolean addAll(Collection<? extends E> c) {
        throw new RuntimeException("Stub!");
    }
}