package java.nio;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PlainDatagramSocketImpl;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import libcore.io.ErrnoException;
import libcore.io.IoBridge;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.util.EmptyArray;

/* loaded from: DatagramChannelImpl.class */
class DatagramChannelImpl extends DatagramChannel implements FileDescriptorChannel {
    private final FileDescriptor fd;
    private DatagramSocket socket;
    InetSocketAddress connectAddress;
    private int localPort;
    boolean connected;
    boolean isBound;
    private final Object readLock;
    private final Object writeLock;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.connect(java.net.SocketAddress):java.nio.channels.DatagramChannel, file: DatagramChannelImpl.class
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
    @Override // java.nio.channels.DatagramChannel
    public synchronized java.nio.channels.DatagramChannel connect(java.net.SocketAddress r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.connect(java.net.SocketAddress):java.nio.channels.DatagramChannel, file: DatagramChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.DatagramChannelImpl.connect(java.net.SocketAddress):java.nio.channels.DatagramChannel");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.receive(java.nio.ByteBuffer):java.net.SocketAddress, file: DatagramChannelImpl.class
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
    @Override // java.nio.channels.DatagramChannel
    public java.net.SocketAddress receive(java.nio.ByteBuffer r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.receive(java.nio.ByteBuffer):java.net.SocketAddress, file: DatagramChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.DatagramChannelImpl.receive(java.nio.ByteBuffer):java.net.SocketAddress");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.send(java.nio.ByteBuffer, java.net.SocketAddress):int, file: DatagramChannelImpl.class
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
    @Override // java.nio.channels.DatagramChannel
    public int send(java.nio.ByteBuffer r1, java.net.SocketAddress r2) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.send(java.nio.ByteBuffer, java.net.SocketAddress):int, file: DatagramChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.DatagramChannelImpl.send(java.nio.ByteBuffer, java.net.SocketAddress):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.readImpl(java.nio.ByteBuffer):int, file: DatagramChannelImpl.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.readImpl(java.nio.ByteBuffer):int, file: DatagramChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.DatagramChannelImpl.readImpl(java.nio.ByteBuffer):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.writeImpl(java.nio.ByteBuffer):int, file: DatagramChannelImpl.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.DatagramChannelImpl.writeImpl(java.nio.ByteBuffer):int, file: DatagramChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.DatagramChannelImpl.writeImpl(java.nio.ByteBuffer):int");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DatagramChannelImpl(SelectorProvider selectorProvider) throws IOException {
        super(selectorProvider);
        this.socket = null;
        this.connectAddress = null;
        this.connected = false;
        this.isBound = false;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.fd = IoBridge.socket(false);
    }

    private DatagramChannelImpl() {
        super(SelectorProvider.provider());
        this.socket = null;
        this.connectAddress = null;
        this.connected = false;
        this.isBound = false;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.fd = new FileDescriptor();
        this.connectAddress = new InetSocketAddress(0);
    }

    @Override // java.nio.channels.DatagramChannel
    public synchronized DatagramSocket socket() {
        if (this.socket == null) {
            this.socket = new DatagramSocketAdapter(new PlainDatagramSocketImpl(this.fd, this.localPort), this);
        }
        return this.socket;
    }

    @Override // java.nio.channels.DatagramChannel
    public synchronized boolean isConnected() {
        return this.connected;
    }

    @Override // java.nio.channels.DatagramChannel
    public synchronized DatagramChannel disconnect() throws IOException {
        if (!isConnected() || !isOpen()) {
            return this;
        }
        this.connected = false;
        this.connectAddress = null;
        try {
            Libcore.os.connect(this.fd, InetAddress.UNSPECIFIED, 0);
            if (this.socket != null) {
                this.socket.disconnect();
            }
            return this;
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    private SocketAddress receiveImpl(ByteBuffer target, boolean loop) throws IOException {
        DatagramPacket receivePacket;
        SocketAddress retAddr = null;
        int oldposition = target.position();
        if (target.hasArray()) {
            receivePacket = new DatagramPacket(target.array(), target.position() + target.arrayOffset(), target.remaining());
        } else {
            receivePacket = new DatagramPacket(new byte[target.remaining()], target.remaining());
        }
        while (true) {
            int received = IoBridge.recvfrom(false, this.fd, receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), 0, receivePacket, isConnected());
            if (receivePacket != null && receivePacket.getAddress() != null) {
                if (received > 0) {
                    if (target.hasArray()) {
                        target.position(oldposition + received);
                    } else {
                        target.put(receivePacket.getData(), 0, received);
                    }
                }
                retAddr = receivePacket.getSocketAddress();
            } else if (!loop) {
                break;
            }
        }
        return retAddr;
    }

    private SocketAddress receiveDirectImpl(ByteBuffer target, boolean loop) throws IOException {
        SocketAddress retAddr = null;
        DatagramPacket receivePacket = new DatagramPacket(EmptyArray.BYTE, 0);
        int oldposition = target.position();
        while (true) {
            int received = IoBridge.recvfrom(false, this.fd, target, 0, receivePacket, isConnected());
            if (receivePacket != null && receivePacket.getAddress() != null) {
                if (received > 0) {
                    target.position(oldposition + received);
                }
                retAddr = receivePacket.getSocketAddress();
            } else if (!loop) {
                break;
            }
        }
        return retAddr;
    }

    @Override // java.nio.channels.DatagramChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer target) throws IOException {
        int readCount;
        target.checkWritable();
        checkOpenConnected();
        if (!target.hasRemaining()) {
            return 0;
        }
        if (target.isDirect() || target.hasArray()) {
            readCount = readImpl(target);
            if (readCount > 0) {
                target.position(target.position() + readCount);
            }
        } else {
            byte[] readArray = new byte[target.remaining()];
            ByteBuffer readBuffer = ByteBuffer.wrap(readArray);
            readCount = readImpl(readBuffer);
            if (readCount > 0) {
                target.put(readArray, 0, readCount);
            }
        }
        return readCount;
    }

    @Override // java.nio.channels.DatagramChannel, java.nio.channels.ScatteringByteChannel
    public long read(ByteBuffer[] targets, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(targets.length, offset, length);
        checkOpenConnected();
        int totalCount = FileChannelImpl.calculateTotalRemaining(targets, offset, length, true);
        if (totalCount == 0) {
            return 0L;
        }
        ByteBuffer readBuffer = ByteBuffer.allocate(totalCount);
        int readCount = readImpl(readBuffer);
        int left = readCount;
        int index = offset;
        byte[] readArray = readBuffer.array();
        while (left > 0) {
            int putLength = Math.min(targets[index].remaining(), left);
            targets[index].put(readArray, readCount - left, putLength);
            index++;
            left -= putLength;
        }
        return readCount;
    }

    @Override // java.nio.channels.DatagramChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer src) throws IOException {
        checkNotNull(src);
        checkOpenConnected();
        if (!src.hasRemaining()) {
            return 0;
        }
        int writeCount = writeImpl(src);
        if (writeCount > 0) {
            src.position(src.position() + writeCount);
        }
        return writeCount;
    }

    @Override // java.nio.channels.DatagramChannel, java.nio.channels.GatheringByteChannel
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

    @Override // java.nio.channels.spi.AbstractSelectableChannel
    protected synchronized void implCloseSelectableChannel() throws IOException {
        this.connected = false;
        if (this.socket != null && !this.socket.isClosed()) {
            this.socket.close();
        } else {
            IoBridge.closeSocket(this.fd);
        }
    }

    @Override // java.nio.channels.spi.AbstractSelectableChannel
    protected void implConfigureBlocking(boolean blocking) throws IOException {
        IoUtils.setBlocking(this.fd, blocking);
    }

    private void checkOpen() throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
    }

    private void checkOpenConnected() throws IOException {
        checkOpen();
        if (!isConnected()) {
            throw new NotYetConnectedException();
        }
    }

    private void checkNotNull(ByteBuffer source) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
    }

    @Override // java.nio.FileDescriptorChannel
    public FileDescriptor getFD() {
        return this.fd;
    }

    /* loaded from: DatagramChannelImpl$DatagramSocketAdapter.class */
    private static class DatagramSocketAdapter extends DatagramSocket {
        private DatagramChannelImpl channelImpl;

        DatagramSocketAdapter(DatagramSocketImpl socketimpl, DatagramChannelImpl channelImpl) {
            super(socketimpl);
            this.channelImpl = channelImpl;
        }

        @Override // java.net.DatagramSocket
        public DatagramChannel getChannel() {
            return this.channelImpl;
        }

        @Override // java.net.DatagramSocket
        public boolean isBound() {
            return this.channelImpl.isBound;
        }

        @Override // java.net.DatagramSocket
        public boolean isConnected() {
            return this.channelImpl.isConnected();
        }

        @Override // java.net.DatagramSocket
        public InetAddress getInetAddress() {
            if (this.channelImpl.connectAddress == null) {
                return null;
            }
            return this.channelImpl.connectAddress.getAddress();
        }

        @Override // java.net.DatagramSocket
        public InetAddress getLocalAddress() {
            try {
                return IoBridge.getSocketLocalAddress(this.channelImpl.fd);
            } catch (SocketException e) {
                return null;
            }
        }

        @Override // java.net.DatagramSocket
        public int getPort() {
            if (this.channelImpl.connectAddress == null) {
                return -1;
            }
            return this.channelImpl.connectAddress.getPort();
        }

        @Override // java.net.DatagramSocket
        public void bind(SocketAddress localAddr) throws SocketException {
            if (this.channelImpl.isConnected()) {
                throw new AlreadyConnectedException();
            }
            super.bind(localAddr);
            this.channelImpl.isBound = true;
        }

        @Override // java.net.DatagramSocket
        public void receive(DatagramPacket packet) throws IOException {
            if (!this.channelImpl.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            super.receive(packet);
        }

        @Override // java.net.DatagramSocket
        public void send(DatagramPacket packet) throws IOException {
            if (!this.channelImpl.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            super.send(packet);
        }

        @Override // java.net.DatagramSocket
        public void close() {
            synchronized (this.channelImpl) {
                if (this.channelImpl.isOpen()) {
                    try {
                        this.channelImpl.close();
                    } catch (IOException e) {
                    }
                }
                super.close();
            }
        }

        @Override // java.net.DatagramSocket
        public void disconnect() {
            try {
                this.channelImpl.disconnect();
            } catch (IOException e) {
            }
            super.disconnect();
        }
    }
}