package java.nio;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Buffer.class */
public abstract class Buffer {
    public abstract Object array();

    public abstract int arrayOffset();

    public abstract boolean hasArray();

    public abstract boolean isDirect();

    public abstract boolean isReadOnly();

    /* JADX INFO: Access modifiers changed from: package-private */
    public Buffer() {
        throw new RuntimeException("Stub!");
    }

    public final int capacity() {
        throw new RuntimeException("Stub!");
    }

    public final Buffer clear() {
        throw new RuntimeException("Stub!");
    }

    public final Buffer flip() {
        throw new RuntimeException("Stub!");
    }

    public final boolean hasRemaining() {
        throw new RuntimeException("Stub!");
    }

    public final int limit() {
        throw new RuntimeException("Stub!");
    }

    public final Buffer limit(int newLimit) {
        throw new RuntimeException("Stub!");
    }

    public final Buffer mark() {
        throw new RuntimeException("Stub!");
    }

    public final int position() {
        throw new RuntimeException("Stub!");
    }

    public final Buffer position(int newPosition) {
        throw new RuntimeException("Stub!");
    }

    public final int remaining() {
        throw new RuntimeException("Stub!");
    }

    public final Buffer reset() {
        throw new RuntimeException("Stub!");
    }

    public final Buffer rewind() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }
}