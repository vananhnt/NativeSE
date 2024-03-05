package java.lang.ref;

/* loaded from: ReferenceQueue.class */
public class ReferenceQueue<T> {
    public ReferenceQueue() {
        throw new RuntimeException("Stub!");
    }

    public synchronized Reference<? extends T> poll() {
        throw new RuntimeException("Stub!");
    }

    public Reference<? extends T> remove() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public synchronized Reference<? extends T> remove(long timeoutMillis) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }
}