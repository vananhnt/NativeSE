package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Selector.class */
public abstract class Selector {
    public abstract void close() throws IOException;

    public abstract boolean isOpen();

    public abstract Set<SelectionKey> keys();

    public abstract SelectorProvider provider();

    public abstract int select() throws IOException;

    public abstract int select(long j) throws IOException;

    public abstract Set<SelectionKey> selectedKeys();

    public abstract int selectNow() throws IOException;

    public abstract Selector wakeup();

    /* JADX INFO: Access modifiers changed from: protected */
    public Selector() {
        throw new RuntimeException("Stub!");
    }

    public static Selector open() throws IOException {
        throw new RuntimeException("Stub!");
    }
}