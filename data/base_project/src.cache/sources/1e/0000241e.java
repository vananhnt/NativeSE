package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SelectorProvider.class */
public abstract class SelectorProvider {
    public abstract DatagramChannel openDatagramChannel() throws IOException;

    public abstract Pipe openPipe() throws IOException;

    public abstract AbstractSelector openSelector() throws IOException;

    public abstract ServerSocketChannel openServerSocketChannel() throws IOException;

    public abstract SocketChannel openSocketChannel() throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public SelectorProvider() {
        throw new RuntimeException("Stub!");
    }

    public static synchronized SelectorProvider provider() {
        throw new RuntimeException("Stub!");
    }

    public Channel inheritedChannel() throws IOException {
        throw new RuntimeException("Stub!");
    }
}