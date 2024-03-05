package android.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.OsConstants;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: LocalSocketImpl.class */
public class LocalSocketImpl {
    private SocketInputStream fis;
    private SocketOutputStream fos;
    private Object readMonitor = new Object();
    private Object writeMonitor = new Object();
    private FileDescriptor fd;
    private boolean mFdCreatedInternally;
    FileDescriptor[] inboundFileDescriptors;
    FileDescriptor[] outboundFileDescriptors;

    /* JADX INFO: Access modifiers changed from: private */
    public native int pending_native(FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int available_native(FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int read_native(FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native int readba_native(byte[] bArr, int i, int i2, FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native void writeba_native(byte[] bArr, int i, int i2, FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: private */
    public native void write_native(int i, FileDescriptor fileDescriptor) throws IOException;

    private native void connectLocal(FileDescriptor fileDescriptor, String str, int i) throws IOException;

    private native void bindLocal(FileDescriptor fileDescriptor, String str, int i) throws IOException;

    private native void listen_native(FileDescriptor fileDescriptor, int i) throws IOException;

    private native void shutdown(FileDescriptor fileDescriptor, boolean z);

    private native Credentials getPeerCredentials_native(FileDescriptor fileDescriptor) throws IOException;

    private native int getOption_native(FileDescriptor fileDescriptor, int i) throws IOException;

    private native void setOption_native(FileDescriptor fileDescriptor, int i, int i2, int i3) throws IOException;

    private native FileDescriptor accept(FileDescriptor fileDescriptor, LocalSocketImpl localSocketImpl) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LocalSocketImpl$SocketInputStream.class */
    public class SocketInputStream extends InputStream {
        SocketInputStream() {
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            return LocalSocketImpl.this.available_native(LocalSocketImpl.this.fd);
        }

        @Override // java.io.InputStream, java.io.Closeable
        public void close() throws IOException {
            LocalSocketImpl.this.close();
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int ret;
            synchronized (LocalSocketImpl.this.readMonitor) {
                FileDescriptor myFd = LocalSocketImpl.this.fd;
                if (myFd == null) {
                    throw new IOException("socket closed");
                }
                ret = LocalSocketImpl.this.read_native(myFd);
            }
            return ret;
        }

        @Override // java.io.InputStream
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            int ret;
            synchronized (LocalSocketImpl.this.readMonitor) {
                FileDescriptor myFd = LocalSocketImpl.this.fd;
                if (myFd == null) {
                    throw new IOException("socket closed");
                }
                if (off >= 0 && len >= 0 && off + len <= b.length) {
                    ret = LocalSocketImpl.this.readba_native(b, off, len, myFd);
                } else {
                    throw new ArrayIndexOutOfBoundsException();
                }
            }
            return ret;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: LocalSocketImpl$SocketOutputStream.class */
    public class SocketOutputStream extends OutputStream {
        SocketOutputStream() {
        }

        @Override // java.io.OutputStream, java.io.Closeable
        public void close() throws IOException {
            LocalSocketImpl.this.close();
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            synchronized (LocalSocketImpl.this.writeMonitor) {
                FileDescriptor myFd = LocalSocketImpl.this.fd;
                if (myFd == null) {
                    throw new IOException("socket closed");
                }
                if (off >= 0 && len >= 0 && off + len <= b.length) {
                    LocalSocketImpl.this.writeba_native(b, off, len, myFd);
                } else {
                    throw new ArrayIndexOutOfBoundsException();
                }
            }
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            synchronized (LocalSocketImpl.this.writeMonitor) {
                FileDescriptor myFd = LocalSocketImpl.this.fd;
                if (myFd == null) {
                    throw new IOException("socket closed");
                }
                LocalSocketImpl.this.write_native(b, myFd);
            }
        }

        @Override // java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            FileDescriptor myFd = LocalSocketImpl.this.fd;
            if (myFd == null) {
                throw new IOException("socket closed");
            }
            while (LocalSocketImpl.this.pending_native(myFd) > 0) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocalSocketImpl() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocalSocketImpl(FileDescriptor fd) throws IOException {
        this.fd = fd;
    }

    public String toString() {
        return super.toString() + " fd:" + this.fd;
    }

    public void create(int sockType) throws IOException {
        int osType;
        if (this.fd == null) {
            switch (sockType) {
                case 1:
                    osType = OsConstants.SOCK_DGRAM;
                    break;
                case 2:
                    osType = OsConstants.SOCK_STREAM;
                    break;
                case 3:
                    osType = OsConstants.SOCK_SEQPACKET;
                    break;
                default:
                    throw new IllegalStateException("unknown sockType");
            }
            try {
                this.fd = Libcore.os.socket(OsConstants.AF_UNIX, osType, 0);
                this.mFdCreatedInternally = true;
            } catch (ErrnoException e) {
                e.rethrowAsIOException();
            }
        }
    }

    public void close() throws IOException {
        synchronized (this) {
            if (this.fd == null || !this.mFdCreatedInternally) {
                this.fd = null;
                return;
            }
            try {
                Libcore.os.close(this.fd);
            } catch (ErrnoException e) {
                e.rethrowAsIOException();
            }
            this.fd = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void connect(LocalSocketAddress address, int timeout) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        connectLocal(this.fd, address.getName(), address.getNamespace().getId());
    }

    public void bind(LocalSocketAddress endpoint) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        bindLocal(this.fd, endpoint.getName(), endpoint.getNamespace().getId());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void listen(int backlog) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        listen_native(this.fd, backlog);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void accept(LocalSocketImpl s) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        s.fd = accept(this.fd, s);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public InputStream getInputStream() throws IOException {
        SocketInputStream socketInputStream;
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        synchronized (this) {
            if (this.fis == null) {
                this.fis = new SocketInputStream();
            }
            socketInputStream = this.fis;
        }
        return socketInputStream;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public OutputStream getOutputStream() throws IOException {
        SocketOutputStream socketOutputStream;
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        synchronized (this) {
            if (this.fos == null) {
                this.fos = new SocketOutputStream();
            }
            socketOutputStream = this.fos;
        }
        return socketOutputStream;
    }

    protected int available() throws IOException {
        return getInputStream().available();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void shutdownInput() throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        shutdown(this.fd, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void shutdownOutput() throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        shutdown(this.fd, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FileDescriptor getFileDescriptor() {
        return this.fd;
    }

    protected boolean supportsUrgentData() {
        return false;
    }

    protected void sendUrgentData(int data) throws IOException {
        throw new RuntimeException("not impled");
    }

    public Object getOption(int optID) throws IOException {
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        if (optID == 4102) {
            return 0;
        }
        int value = getOption_native(this.fd, optID);
        switch (optID) {
            case 4:
            default:
                return Integer.valueOf(value);
            case 4097:
            case 4098:
                return Integer.valueOf(value);
        }
    }

    public void setOption(int optID, Object value) throws IOException {
        int boolValue = -1;
        int intValue = 0;
        if (this.fd == null) {
            throw new IOException("socket not created");
        }
        if (value instanceof Integer) {
            intValue = ((Integer) value).intValue();
        } else if (value instanceof Boolean) {
            boolValue = ((Boolean) value).booleanValue() ? 1 : 0;
        } else {
            throw new IOException("bad value: " + value);
        }
        setOption_native(this.fd, optID, boolValue, intValue);
    }

    public void setFileDescriptorsForSend(FileDescriptor[] fds) {
        synchronized (this.writeMonitor) {
            this.outboundFileDescriptors = fds;
        }
    }

    public FileDescriptor[] getAncillaryFileDescriptors() throws IOException {
        FileDescriptor[] result;
        synchronized (this.readMonitor) {
            result = this.inboundFileDescriptors;
            this.inboundFileDescriptors = null;
        }
        return result;
    }

    public Credentials getPeerCredentials() throws IOException {
        return getPeerCredentials_native(this.fd);
    }

    public LocalSocketAddress getSockAddress() throws IOException {
        return null;
    }

    protected void finalize() throws IOException {
        close();
    }
}