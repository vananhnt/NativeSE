package java.nio;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketImpl;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import libcore.io.ErrnoException;
import libcore.io.IoUtils;
import libcore.io.OsConstants;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ServerSocketChannelImpl.class */
public final class ServerSocketChannelImpl extends ServerSocketChannel implements FileDescriptorChannel {
    private final ServerSocketAdapter socket;
    private final SocketImpl impl;
    private boolean isBound;
    private final Object acceptLock;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.ServerSocketChannelImpl.accept():java.nio.channels.SocketChannel, file: ServerSocketChannelImpl.class
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
    @Override // java.nio.channels.ServerSocketChannel
    public java.nio.channels.SocketChannel accept() throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.ServerSocketChannelImpl.accept():java.nio.channels.SocketChannel, file: ServerSocketChannelImpl.class
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.ServerSocketChannelImpl.accept():java.nio.channels.SocketChannel");
    }

    public ServerSocketChannelImpl(SelectorProvider sp) throws IOException {
        super(sp);
        this.isBound = false;
        this.acceptLock = new Object();
        this.socket = new ServerSocketAdapter(this);
        this.impl = this.socket.getImpl$();
    }

    @Override // java.nio.channels.ServerSocketChannel
    public ServerSocket socket() {
        return this.socket;
    }

    private boolean shouldThrowSocketTimeoutExceptionFromAccept(SocketTimeoutException e) {
        if (isBlocking()) {
            return true;
        }
        Throwable cause = e.getCause();
        if ((cause instanceof ErrnoException) && ((ErrnoException) cause).errno == OsConstants.EAGAIN) {
            return false;
        }
        return true;
    }

    @Override // java.nio.channels.spi.AbstractSelectableChannel
    protected void implConfigureBlocking(boolean blocking) throws IOException {
        IoUtils.setBlocking(this.impl.getFD$(), blocking);
    }

    @Override // java.nio.channels.spi.AbstractSelectableChannel
    protected synchronized void implCloseSelectableChannel() throws IOException {
        if (!this.socket.isClosed()) {
            this.socket.close();
        }
    }

    @Override // java.nio.FileDescriptorChannel
    public FileDescriptor getFD() {
        return this.impl.getFD$();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ServerSocketChannelImpl$ServerSocketAdapter.class */
    public static class ServerSocketAdapter extends ServerSocket {
        private final ServerSocketChannelImpl channelImpl;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.ServerSocketChannelImpl.ServerSocketAdapter.implAccept(java.nio.SocketChannelImpl):java.net.Socket, file: ServerSocketChannelImpl$ServerSocketAdapter.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        public java.net.Socket implAccept(java.nio.SocketChannelImpl r1) throws java.io.IOException {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: java.nio.ServerSocketChannelImpl.ServerSocketAdapter.implAccept(java.nio.SocketChannelImpl):java.net.Socket, file: ServerSocketChannelImpl$ServerSocketAdapter.class
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.ServerSocketChannelImpl.ServerSocketAdapter.implAccept(java.nio.SocketChannelImpl):java.net.Socket");
        }

        ServerSocketAdapter(ServerSocketChannelImpl aChannelImpl) throws IOException {
            this.channelImpl = aChannelImpl;
        }

        @Override // java.net.ServerSocket
        public void bind(SocketAddress localAddress, int backlog) throws IOException {
            super.bind(localAddress, backlog);
            this.channelImpl.isBound = true;
        }

        @Override // java.net.ServerSocket
        public Socket accept() throws IOException {
            if (!this.channelImpl.isBound) {
                throw new IllegalBlockingModeException();
            }
            SocketChannel sc = this.channelImpl.accept();
            if (sc == null) {
                throw new IllegalBlockingModeException();
            }
            return sc.socket();
        }

        @Override // java.net.ServerSocket
        public ServerSocketChannel getChannel() {
            return this.channelImpl;
        }

        @Override // java.net.ServerSocket
        public boolean isBound() {
            return this.channelImpl.isBound;
        }

        @Override // java.net.ServerSocket
        public void bind(SocketAddress localAddress) throws IOException {
            super.bind(localAddress);
            this.channelImpl.isBound = true;
        }

        @Override // java.net.ServerSocket
        public void close() throws IOException {
            synchronized (this.channelImpl) {
                if (this.channelImpl.isOpen()) {
                    this.channelImpl.close();
                } else {
                    super.close();
                }
            }
        }
    }
}