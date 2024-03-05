package java.lang;

/* loaded from: Object.class */
public class Object {
    public final native Class<?> getClass();

    public native int hashCode();

    public final native void notify();

    public final native void notifyAll();

    public final native void wait(long j, int i) throws InterruptedException;

    public Object() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finalize() throws Throwable {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public final void wait() throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public final void wait(long millis) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }
}