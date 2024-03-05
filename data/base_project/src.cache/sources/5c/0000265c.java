package java.util.concurrent;

import java.util.Collection;
import java.util.Queue;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BlockingQueue.class */
public interface BlockingQueue<E> extends Queue<E> {
    @Override // java.util.Queue, java.util.Collection
    boolean add(E e);

    @Override // java.util.Queue
    boolean offer(E e);

    void put(E e) throws InterruptedException;

    boolean offer(E e, long j, TimeUnit timeUnit) throws InterruptedException;

    E take() throws InterruptedException;

    E poll(long j, TimeUnit timeUnit) throws InterruptedException;

    int remainingCapacity();

    @Override // java.util.Collection
    boolean remove(Object obj);

    @Override // java.util.Collection
    boolean contains(Object obj);

    int drainTo(Collection<? super E> collection);

    int drainTo(Collection<? super E> collection, int i);
}