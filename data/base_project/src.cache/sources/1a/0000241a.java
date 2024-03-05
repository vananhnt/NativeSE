package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractSelectableChannel.class */
public abstract class AbstractSelectableChannel extends SelectableChannel {
    protected abstract void implCloseSelectableChannel() throws IOException;

    protected abstract void implConfigureBlocking(boolean z) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractSelectableChannel(SelectorProvider selectorProvider) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectableChannel
    public final SelectorProvider provider() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectableChannel
    public final synchronized boolean isRegistered() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectableChannel
    public final synchronized SelectionKey keyFor(Selector selector) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectableChannel
    public final SelectionKey register(Selector selector, int interestSet, Object attachment) throws ClosedChannelException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.spi.AbstractInterruptibleChannel
    protected final synchronized void implCloseChannel() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectableChannel
    public final boolean isBlocking() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectableChannel
    public final Object blockingLock() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectableChannel
    public final SelectableChannel configureBlocking(boolean blockingMode) throws IOException {
        throw new RuntimeException("Stub!");
    }
}