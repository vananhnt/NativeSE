package java.nio.channels.spi;

import java.nio.channels.SelectionKey;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractSelectionKey.class */
public abstract class AbstractSelectionKey extends SelectionKey {
    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractSelectionKey() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectionKey
    public final boolean isValid() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectionKey
    public final void cancel() {
        throw new RuntimeException("Stub!");
    }
}