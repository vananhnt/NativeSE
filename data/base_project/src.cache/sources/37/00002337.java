package java.lang.ref;

/* loaded from: PhantomReference.class */
public class PhantomReference<T> extends Reference<T> {
    public PhantomReference(T r, ReferenceQueue<? super T> q) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.ref.Reference
    public T get() {
        throw new RuntimeException("Stub!");
    }
}