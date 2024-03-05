package java.nio;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelectionKey;

/* loaded from: SelectionKeyImpl.class */
final class SelectionKeyImpl extends AbstractSelectionKey {
    private AbstractSelectableChannel channel;
    private int interestOps;
    private int readyOps;
    private SelectorImpl selector;

    public SelectionKeyImpl(AbstractSelectableChannel channel, int operations, Object attachment, SelectorImpl selector) {
        this.channel = channel;
        this.interestOps = operations;
        this.selector = selector;
        attach(attachment);
    }

    @Override // java.nio.channels.SelectionKey
    public SelectableChannel channel() {
        return this.channel;
    }

    @Override // java.nio.channels.SelectionKey
    public int interestOps() {
        int i;
        checkValid();
        synchronized (this.selector.keysLock) {
            i = this.interestOps;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int interestOpsNoCheck() {
        int i;
        synchronized (this.selector.keysLock) {
            i = this.interestOps;
        }
        return i;
    }

    @Override // java.nio.channels.SelectionKey
    public SelectionKey interestOps(int operations) {
        checkValid();
        if ((operations & (channel().validOps() ^ (-1))) != 0) {
            throw new IllegalArgumentException();
        }
        synchronized (this.selector.keysLock) {
            this.interestOps = operations;
        }
        return this;
    }

    @Override // java.nio.channels.SelectionKey
    public int readyOps() {
        checkValid();
        return this.readyOps;
    }

    @Override // java.nio.channels.SelectionKey
    public Selector selector() {
        return this.selector;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setReadyOps(int readyOps) {
        this.readyOps = readyOps & this.interestOps;
    }

    private void checkValid() {
        if (!isValid()) {
            throw new CancelledKeyException();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isConnected() {
        return !(this.channel instanceof SocketChannel) || ((SocketChannel) this.channel).isConnected();
    }
}