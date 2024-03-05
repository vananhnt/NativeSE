package java.lang;

/* loaded from: InheritableThreadLocal.class */
public class InheritableThreadLocal<T> extends ThreadLocal<T> {
    public InheritableThreadLocal() {
        throw new RuntimeException("Stub!");
    }

    protected T childValue(T parentValue) {
        throw new RuntimeException("Stub!");
    }
}