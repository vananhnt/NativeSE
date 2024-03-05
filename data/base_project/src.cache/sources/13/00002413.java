package java.nio.channels;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ServerSocketChannel.class */
public abstract class ServerSocketChannel extends AbstractSelectableChannel {
    public abstract ServerSocket socket();

    public abstract SocketChannel accept() throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public ServerSocketChannel(SelectorProvider selectorProvider) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public static ServerSocketChannel open() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectableChannel
    public final int validOps() {
        throw new RuntimeException("Stub!");
    }
}