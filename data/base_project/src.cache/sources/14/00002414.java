package java.nio.channels;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SocketChannel.class */
public abstract class SocketChannel extends AbstractSelectableChannel implements ByteChannel, ScatteringByteChannel, GatheringByteChannel {
    public abstract Socket socket();

    public abstract boolean isConnected();

    public abstract boolean isConnectionPending();

    public abstract boolean connect(SocketAddress socketAddress) throws IOException;

    public abstract boolean finishConnect() throws IOException;

    public abstract int read(ByteBuffer byteBuffer) throws IOException;

    public abstract long read(ByteBuffer[] byteBufferArr, int i, int i2) throws IOException;

    public abstract int write(ByteBuffer byteBuffer) throws IOException;

    public abstract long write(ByteBuffer[] byteBufferArr, int i, int i2) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public SocketChannel(SelectorProvider selectorProvider) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public static SocketChannel open() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public static SocketChannel open(SocketAddress address) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.SelectableChannel
    public final int validOps() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.ScatteringByteChannel
    public final synchronized long read(ByteBuffer[] targets) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.GatheringByteChannel
    public final synchronized long write(ByteBuffer[] sources) throws IOException {
        throw new RuntimeException("Stub!");
    }
}