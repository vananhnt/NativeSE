package android.net;

import java.io.FileDescriptor;
import java.io.IOException;

/* loaded from: LocalServerSocket.class */
public class LocalServerSocket {
    private final LocalSocketImpl impl;
    private final LocalSocketAddress localAddress;
    private static final int LISTEN_BACKLOG = 50;

    public LocalServerSocket(String name) throws IOException {
        this.impl = new LocalSocketImpl();
        this.impl.create(2);
        this.localAddress = new LocalSocketAddress(name);
        this.impl.bind(this.localAddress);
        this.impl.listen(50);
    }

    public LocalServerSocket(FileDescriptor fd) throws IOException {
        this.impl = new LocalSocketImpl(fd);
        this.impl.listen(50);
        this.localAddress = this.impl.getSockAddress();
    }

    public LocalSocketAddress getLocalSocketAddress() {
        return this.localAddress;
    }

    public LocalSocket accept() throws IOException {
        LocalSocketImpl acceptedImpl = new LocalSocketImpl();
        this.impl.accept(acceptedImpl);
        return new LocalSocket(acceptedImpl, 0);
    }

    public FileDescriptor getFileDescriptor() {
        return this.impl.getFileDescriptor();
    }

    public void close() throws IOException {
        this.impl.close();
    }
}