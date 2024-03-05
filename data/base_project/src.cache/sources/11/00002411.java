package java.nio.channels;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SelectionKey.class */
public abstract class SelectionKey {
    public static final int OP_ACCEPT = 16;
    public static final int OP_CONNECT = 8;
    public static final int OP_READ = 1;
    public static final int OP_WRITE = 4;

    public abstract void cancel();

    public abstract SelectableChannel channel();

    public abstract int interestOps();

    public abstract SelectionKey interestOps(int i);

    public abstract boolean isValid();

    public abstract int readyOps();

    public abstract Selector selector();

    /* JADX INFO: Access modifiers changed from: protected */
    public SelectionKey() {
        throw new RuntimeException("Stub!");
    }

    public final Object attach(Object anObject) {
        throw new RuntimeException("Stub!");
    }

    public final Object attachment() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isAcceptable() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isConnectable() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isReadable() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isWritable() {
        throw new RuntimeException("Stub!");
    }
}