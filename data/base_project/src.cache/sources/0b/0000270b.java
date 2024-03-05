package java.util.concurrent.atomic;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AtomicStampedReference.class */
public class AtomicStampedReference<V> {
    public AtomicStampedReference(V initialRef, int initialStamp) {
        throw new RuntimeException("Stub!");
    }

    public V getReference() {
        throw new RuntimeException("Stub!");
    }

    public int getStamp() {
        throw new RuntimeException("Stub!");
    }

    public V get(int[] stampHolder) {
        throw new RuntimeException("Stub!");
    }

    public boolean weakCompareAndSet(V expectedReference, V newReference, int expectedStamp, int newStamp) {
        throw new RuntimeException("Stub!");
    }

    public boolean compareAndSet(V expectedReference, V newReference, int expectedStamp, int newStamp) {
        throw new RuntimeException("Stub!");
    }

    public void set(V newReference, int newStamp) {
        throw new RuntimeException("Stub!");
    }

    public boolean attemptStamp(V expectedReference, int newStamp) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: AtomicStampedReference$Pair.class */
    private static class Pair<T> {
        final T reference;
        final int stamp;

        private Pair(T reference, int stamp) {
            this.reference = reference;
            this.stamp = stamp;
        }

        static <T> Pair<T> of(T reference, int stamp) {
            return new Pair<>(reference, stamp);
        }
    }
}