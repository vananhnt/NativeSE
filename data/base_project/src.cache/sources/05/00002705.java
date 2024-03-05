package java.util.concurrent.atomic;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AtomicMarkableReference.class */
public class AtomicMarkableReference<V> {
    public AtomicMarkableReference(V initialRef, boolean initialMark) {
        throw new RuntimeException("Stub!");
    }

    public V getReference() {
        throw new RuntimeException("Stub!");
    }

    public boolean isMarked() {
        throw new RuntimeException("Stub!");
    }

    public V get(boolean[] markHolder) {
        throw new RuntimeException("Stub!");
    }

    public boolean weakCompareAndSet(V expectedReference, V newReference, boolean expectedMark, boolean newMark) {
        throw new RuntimeException("Stub!");
    }

    public boolean compareAndSet(V expectedReference, V newReference, boolean expectedMark, boolean newMark) {
        throw new RuntimeException("Stub!");
    }

    public void set(V newReference, boolean newMark) {
        throw new RuntimeException("Stub!");
    }

    public boolean attemptMark(V expectedReference, boolean newMark) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: AtomicMarkableReference$Pair.class */
    private static class Pair<T> {
        final T reference;
        final boolean mark;

        private Pair(T reference, boolean mark) {
            this.reference = reference;
            this.mark = mark;
        }

        static <T> Pair<T> of(T reference, boolean mark) {
            return new Pair<>(reference, mark);
        }
    }
}