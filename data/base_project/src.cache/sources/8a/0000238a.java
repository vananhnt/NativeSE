package java.net;

import java.io.FileDescriptor;
import java.io.IOException;

/* loaded from: PlainServerSocketImpl.class */
public class PlainServerSocketImpl extends PlainSocketImpl {
    public PlainServerSocketImpl() {
    }

    public PlainServerSocketImpl(FileDescriptor fd) {
        super(fd);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.net.PlainSocketImpl, java.net.SocketImpl
    public void create(boolean isStreaming) throws IOException {
        super.create(isStreaming);
        if (isStreaming) {
            setOption(4, Boolean.TRUE);
        }
    }
}