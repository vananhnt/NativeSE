package java.util.concurrent;

/* loaded from: TransferQueue.class */
public interface TransferQueue<E> extends BlockingQueue<E> {
    boolean tryTransfer(E e);

    void transfer(E e) throws InterruptedException;

    boolean tryTransfer(E e, long j, TimeUnit timeUnit) throws InterruptedException;

    boolean hasWaitingConsumer();

    int getWaitingConsumerCount();
}