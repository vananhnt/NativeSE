package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.SelectorProvider;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SelectableChannel.class */
public abstract class SelectableChannel extends AbstractInterruptibleChannel implements Channel {
    public abstract Object blockingLock();

    public abstract SelectableChannel configureBlocking(boolean z) throws IOException;

    public abstract boolean isBlocking();

    public abstract boolean isRegistered();

    public abstract SelectionKey keyFor(Selector selector);

    public abstract SelectorProvider provider();

    public abstract SelectionKey register(Selector selector, int i, Object obj) throws ClosedChannelException;

    public abstract int validOps();

    /* JADX INFO: Access modifiers changed from: protected */
    public SelectableChannel() {
        throw new RuntimeException("Stub!");
    }

    public final SelectionKey register(Selector selector, int operations) throws ClosedChannelException {
        throw new RuntimeException("Stub!");
    }
}