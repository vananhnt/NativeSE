package java.nio;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PlainSocketImpl;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketUtils;
import java.net.UnknownHostException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import libcore.io.ErrnoException;
import libcore.io.IoBridge;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.io.OsConstants;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: SocketChannelImpl.class */
public class SocketChannelImpl extends SocketChannel implements FileDescriptorChannel {
    private static final int SOCKET_STATUS_UNINITIALIZED = -1;
    private static final int SOCKET_STATUS_UNCONNECTED = 0;
    private static final int SOCKET_STATUS_PENDING = 1;
    private static final int SOCKET_STATUS_CONNECTED = 2;
    private static final int SOCKET_STATUS_CLOSED = 3;
    private final FileDescriptor fd;
    private SocketAdapter socket;
    private InetSocketAddress connectAddress;
    private InetAddress localAddress;
    private int localPort;
    private int status;
    private volatile boolean isBound;
    private final Object readLock;
    private final Object writeLock;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.SocketChannelImpl.connect(java.net.SocketAddress):boolean, file: SocketChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // java.nio.channels.SocketChannel
    public boolean connect(java.net.SocketAddress r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.SocketChannelImpl.connect(java.net.SocketAddress):boolean, file: SocketChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.connect(java.net.SocketAddress):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.SocketChannelImpl.finishConnect():boolean, file: SocketChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // java.nio.channels.SocketChannel
    public boolean finishConnect() throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.SocketChannelImpl.finishConnect():boolean, file: SocketChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.finishConnect():boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.SocketChannelImpl.readImpl(java.nio.ByteBuffer):int, file: SocketChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private int readImpl(java.nio.ByteBuffer r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.SocketChannelImpl.readImpl(java.nio.ByteBuffer):int, file: SocketChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.readImpl(java.nio.ByteBuffer):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.SocketChannelImpl.writeImpl(java.nio.ByteBuffer):int, file: SocketChannelImpl.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private int writeImpl(java.nio.ByteBuffer r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.SocketChannelImpl.writeImpl(java.nio.ByteBuffer):int, file: SocketChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.writeImpl(java.nio.ByteBuffer):int");
    }

    public SocketChannelImpl(SelectorProvider selectorProvider) throws IOException {
        this(selectorProvider, true);
    }

    public SocketChannelImpl(SelectorProvider selectorProvider, boolean connect) throws IOException {
        super(selectorProvider);
        this.socket = null;
        this.connectAddress = null;
        this.localAddress = null;
        this.status = -1;
        this.isBound = false;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.status = 0;
        this.fd = connect ? IoBridge.socket(true) : new FileDescriptor();
    }

    public SocketChannelImpl(SelectorProvider selectorProvider, FileDescriptor existingFd) throws IOException {
        super(selectorProvider);
        this.socket = null;
        this.connectAddress = null;
        this.localAddress = null;
        this.status = -1;
        this.isBound = false;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.status = 2;
        this.fd = existingFd;
    }

    @Override // java.nio.channels.SocketChannel
    public synchronized Socket socket() {
        if (this.socket == null) {
            try {
                InetAddress addr = null;
                int port = 0;
                if (this.connectAddress != null) {
                    addr = this.connectAddress.getAddress();
                    port = this.connectAddress.getPort();
                }
                this.socket = new SocketAdapter(new PlainSocketImpl(this.fd, this.localPort, addr, port), this);
            } catch (SocketException e) {
                return null;
            }
        }
        return this.socket;
    }

    @Override // java.nio.channels.SocketChannel
    public synchronized boolean isConnected() {
        return this.status == 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setConnected() {
        this.status = 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBound(boolean flag) {
        this.isBound = flag;
    }

    @Override // java.nio.channels.SocketChannel
    public synchronized boolean isConnectionPending() {
        return this.status == 1;
    }

    private boolean isEINPROGRESS(IOException e) {
        if (!isBlocking() && (e instanceof ConnectException)) {
            Throwable cause = e.getCause();
            return (cause instanceof ErrnoException) && ((ErrnoException) cause).errno == OsConstants.EINPROGRESS;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initLocalAddressAndPort() {
        try {
            SocketAddress sa = Libcore.os.getsockname(this.fd);
            InetSocketAddress isa = (InetSocketAddress) sa;
            this.localAddress = isa.getAddress();
            this.localPort = isa.getPort();
            if (this.socket != null) {
                this.socket.socketImpl().initLocalPort(this.localPort);
            }
        } catch (ErrnoException errnoException) {
            throw new AssertionError(errnoException);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finishAccept() {
        initLocalAddressAndPort();
    }

    @Override // java.nio.channels.SocketChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer dst) throws IOException {
        dst.checkWritable();
        checkOpenConnected();
        if (!dst.hasRemaining()) {
            return 0;
        }
        return readImpl(dst);
    }

    @Override // java.nio.channels.SocketChannel, java.nio.channels.ScatteringByteChannel
    public long read(ByteBuffer[] targets, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(targets.length, offset, length);
        checkOpenConnected();
        int totalCount = FileChannelImpl.calculateTotalRemaining(targets, offset, length, true);
        if (totalCount == 0) {
            return 0L;
        }
        byte[] readArray = new byte[totalCount];
        ByteBuffer readBuffer = ByteBuffer.wrap(readArray);
        int readCount = readImpl(readBuffer);
        readBuffer.flip();
        if (readCount > 0) {
            int left = readCount;
            int index = offset;
            while (left > 0) {
                int putLength = Math.min(targets[index].remaining(), left);
                targets[index].put(readArray, readCount - left, putLength);
                index++;
                left -= putLength;
            }
        }
        return readCount;
    }

    @Override // java.nio.channels.SocketChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer src) throws IOException {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        checkOpenConnected();
        if (!src.hasRemaining()) {
            return 0;
        }
        return writeImpl(src);
    }

    @Override // java.nio.channels.SocketChannel, java.nio.channels.GatheringByteChannel
    public long write(ByteBuffer[] sources, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(sources.length, offset, length);
        checkOpenConnected();
        int count = FileChannelImpl.calculateTotalRemaining(sources, offset, length, false);
        if (count == 0) {
            return 0L;
        }
        ByteBuffer writeBuf = ByteBuffer.allocate(count);
        for (int val = offset; val < length + offset; val++) {
            ByteBuffer source = sources[val];
            int oldPosition = source.position();
            writeBuf.put(source);
            source.position(oldPosition);
        }
        writeBuf.flip();
        int result = writeImpl(writeBuf);
        int val2 = offset;
        while (result > 0) {
            ByteBuffer source2 = sources[val2];
            int gap = Math.min(result, source2.remaining());
            source2.position(source2.position() + gap);
            val2++;
            result -= gap;
        }
        return result;
    }

    private synchronized void checkOpenConnected() throws ClosedChannelException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
        if (!isConnected()) {
            throw new NotYetConnectedException();
        }
    }

    private synchronized void checkUnconnected() throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
        if (this.status == 2) {
            throw new AlreadyConnectedException();
        }
        if (this.status == 1) {
            throw new ConnectionPendingException();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InetSocketAddress validateAddress(SocketAddress socketAddress) {
        if (socketAddress == null) {
            throw new IllegalArgumentException("socketAddress == null");
        }
        if (!(socketAddress instanceof InetSocketAddress)) {
            throw new UnsupportedAddressTypeException();
        }
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        if (inetSocketAddress.isUnresolved()) {
            throw new UnresolvedAddressException();
        }
        return inetSocketAddress;
    }

    public InetAddress getLocalAddress() throws UnknownHostException {
        return this.isBound ? this.localAddress : Inet4Address.ANY;
    }

    @Override // java.nio.channels.spi.AbstractSelectableChannel
    protected synchronized void implCloseSelectableChannel() throws IOException {
        if (this.status != 3) {
            this.status = 3;
            if (this.socket != null && !this.socket.isClosed()) {
                this.socket.close();
            } else {
                IoBridge.closeSocket(this.fd);
            }
        }
    }

    @Override // java.nio.channels.spi.AbstractSelectableChannel
    protected void implConfigureBlocking(boolean blocking) throws IOException {
        IoUtils.setBlocking(this.fd, blocking);
    }

    @Override // java.nio.FileDescriptorChannel
    public FileDescriptor getFD() {
        return this.fd;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SocketChannelImpl$SocketAdapter.class */
    public static class SocketAdapter extends Socket {
        private final SocketChannelImpl channel;
        private final PlainSocketImpl socketImpl;

        SocketAdapter(PlainSocketImpl socketImpl, SocketChannelImpl channel) throws SocketException {
            super(socketImpl);
            this.socketImpl = socketImpl;
            this.channel = channel;
            SocketUtils.setCreated(this);
        }

        PlainSocketImpl socketImpl() {
            return this.socketImpl;
        }

        @Override // java.net.Socket
        public SocketChannel getChannel() {
            return this.channel;
        }

        @Override // java.net.Socket
        public boolean isBound() {
            return this.channel.isBound;
        }

        @Override // java.net.Socket
        public boolean isConnected() {
            return this.channel.isConnected();
        }

        @Override // java.net.Socket
        public InetAddress getLocalAddress() {
            try {
                return this.channel.getLocalAddress();
            } catch (UnknownHostException e) {
                return null;
            }
        }

        @Override // java.net.Socket
        public void connect(SocketAddress remoteAddr, int timeout) throws IOException {
            if (!this.channel.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            if (isConnected()) {
                throw new AlreadyConnectedException();
            }
            super.connect(remoteAddr, timeout);
            this.channel.initLocalAddressAndPort();
            if (super.isConnected()) {
                this.channel.setConnected();
                this.channel.isBound = super.isBound();
            }
        }

        @Override // java.net.Socket
        public void bind(SocketAddress localAddr) throws IOException {
            if (this.channel.isConnected()) {
                throw new AlreadyConnectedException();
            }
            if (1 == this.channel.status) {
                throw new ConnectionPendingException();
            }
            super.bind(localAddr);
            this.channel.initLocalAddressAndPort();
            this.channel.isBound = true;
        }

        @Override // java.net.Socket
        public void close() throws IOException {
            synchronized (this.channel) {
                if (this.channel.isOpen()) {
                    this.channel.close();
                } else {
                    super.close();
                }
                this.channel.status = 3;
            }
        }

        @Override // java.net.Socket
        public OutputStream getOutputStream() throws IOException {
            checkOpenAndConnected();
            if (isOutputShutdown()) {
                throw new SocketException("Socket output is shutdown");
            }
            return new SocketChannelOutputStream(this.channel);
        }

        @Override // java.net.Socket
        public InputStream getInputStream() throws IOException {
            checkOpenAndConnected();
            if (isInputShutdown()) {
                throw new SocketException("Socket input is shutdown");
            }
            return new SocketChannelInputStream(this.channel);
        }

        private void checkOpenAndConnected() throws SocketException {
            if (!this.channel.isOpen()) {
                throw new SocketException("Socket is closed");
            }
            if (!this.channel.isConnected()) {
                throw new SocketException("Socket is not connected");
            }
        }

        public FileDescriptor getFileDescriptor$() {
            return this.socketImpl.getFD$();
        }
    }

    /* loaded from: SocketChannelImpl$SocketChannelOutputStream.class */
    private static class SocketChannelOutputStream extends OutputStream {
        private final SocketChannel channel;

        public SocketChannelOutputStream(SocketChannel channel) {
            this.channel = channel;
        }

        @Override // java.io.OutputStream, java.io.Closeable
        public void close() throws IOException {
            this.channel.close();
        }

        @Override // java.io.OutputStream
        public void write(byte[] buffer, int offset, int byteCount) throws IOException {
            Arrays.checkOffsetAndCount(buffer.length, offset, byteCount);
            ByteBuffer buf = ByteBuffer.wrap(buffer, offset, byteCount);
            if (!this.channel.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            this.channel.write(buf);
        }

        @Override // java.io.OutputStream
        public void write(int oneByte) throws IOException {
            if (!this.channel.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put(0, (byte) (oneByte & 255));
            this.channel.write(buffer);
        }
    }

    /* loaded from: SocketChannelImpl$SocketChannelInputStream.class */
    private static class SocketChannelInputStream extends InputStream {
        private final SocketChannel channel;

        public SocketChannelInputStream(SocketChannel channel) {
            this.channel = channel;
        }

        @Override // java.io.InputStream, java.io.Closeable
        public void close() throws IOException {
            this.channel.close();
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (!this.channel.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            ByteBuffer buf = ByteBuffer.allocate(1);
            int result = this.channel.read(buf);
            return result == -1 ? result : buf.get(0) & 255;
        }

        @Override // java.io.InputStream
        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            Arrays.checkOffsetAndCount(buffer.length, byteOffset, byteCount);
            if (!this.channel.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            ByteBuffer buf = ByteBuffer.wrap(buffer, byteOffset, byteCount);
            return this.channel.read(buf);
        }
    }
}