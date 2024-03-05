package java.util;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Queue.class */
public interface Queue<E> extends Collection<E> {
    @Override // java.util.Collection
    boolean add(E e);

    boolean offer(E e);

    E remove();

    E poll();

    E element();

    E peek();
}