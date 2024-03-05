package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractSelector.class */
public abstract class AbstractSelector extends Selector {
    protected abstract void implCloseSelector() throws IOException;

    protected abstract SelectionKey register(AbstractSelectableChannel abstractSelectableChannel, int i, Object obj);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractSelector(SelectorProvider selectorProvider) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.Selector
    public final void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.Selector
    public final boolean isOpen() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.Selector
    public final SelectorProvider provider() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Set<SelectionKey> cancelledKeys() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void deregister(AbstractSelectionKey key) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void begin() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void end() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.nio.channels.spi.AbstractSelector$1  reason: invalid class name */
    /* loaded from: AbstractSelector$1.class */
    class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            AbstractSelector.this.wakeup();
        }
    }
}