package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Channel;
import java.nio.channels.InterruptibleChannel;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractInterruptibleChannel.class */
public abstract class AbstractInterruptibleChannel implements Channel, InterruptibleChannel {
    protected abstract void implCloseChannel() throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractInterruptibleChannel() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.Channel
    public final synchronized boolean isOpen() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.Channel, java.io.Closeable
    public final void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void begin() {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void end(boolean success) throws AsynchronousCloseException {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.nio.channels.spi.AbstractInterruptibleChannel$1  reason: invalid class name */
    /* loaded from: AbstractInterruptibleChannel$1.class */
    class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                AbstractInterruptibleChannel.this.interrupted = true;
                AbstractInterruptibleChannel.this.close();
            } catch (IOException e) {
            }
        }
    }
}