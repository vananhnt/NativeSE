package java.nio;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import libcore.io.ErrnoException;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.io.OsConstants;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PipeImpl.class */
public final class PipeImpl extends Pipe {
    private final PipeSinkChannel sink;
    private final PipeSourceChannel source;

    public PipeImpl(SelectorProvider selectorProvider) throws IOException {
        try {
            FileDescriptor fd1 = new FileDescriptor();
            FileDescriptor fd2 = new FileDescriptor();
            Libcore.os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_STREAM, 0, fd1, fd2);
            this.sink = new PipeSinkChannel(selectorProvider, fd1);
            this.source = new PipeSourceChannel(selectorProvider, fd2);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    @Override // java.nio.channels.Pipe
    public Pipe.SinkChannel sink() {
        return this.sink;
    }

    @Override // java.nio.channels.Pipe
    public Pipe.SourceChannel source() {
        return this.source;
    }

    /* loaded from: PipeImpl$PipeSourceChannel.class */
    private class PipeSourceChannel extends Pipe.SourceChannel implements FileDescriptorChannel {
        private final FileDescriptor fd;
        private final SocketChannel channel;

        private PipeSourceChannel(SelectorProvider selectorProvider, FileDescriptor fd) throws IOException {
            super(selectorProvider);
            this.fd = fd;
            this.channel = new SocketChannelImpl(selectorProvider, fd);
        }

        @Override // java.nio.channels.spi.AbstractSelectableChannel
        protected void implCloseSelectableChannel() throws IOException {
            this.channel.close();
        }

        @Override // java.nio.channels.spi.AbstractSelectableChannel
        protected void implConfigureBlocking(boolean blocking) throws IOException {
            IoUtils.setBlocking(getFD(), blocking);
        }

        @Override // java.nio.channels.ReadableByteChannel
        public int read(ByteBuffer buffer) throws IOException {
            return this.channel.read(buffer);
        }

        @Override // java.nio.channels.ScatteringByteChannel
        public long read(ByteBuffer[] buffers) throws IOException {
            return this.channel.read(buffers);
        }

        @Override // java.nio.channels.ScatteringByteChannel
        public long read(ByteBuffer[] buffers, int offset, int length) throws IOException {
            return this.channel.read(buffers, offset, length);
        }

        @Override // java.nio.FileDescriptorChannel
        public FileDescriptor getFD() {
            return this.fd;
        }
    }

    /* loaded from: PipeImpl$PipeSinkChannel.class */
    private class PipeSinkChannel extends Pipe.SinkChannel implements FileDescriptorChannel {
        private final FileDescriptor fd;
        private final SocketChannel channel;

        private PipeSinkChannel(SelectorProvider selectorProvider, FileDescriptor fd) throws IOException {
            super(selectorProvider);
            this.fd = fd;
            this.channel = new SocketChannelImpl(selectorProvider, fd);
        }

        @Override // java.nio.channels.spi.AbstractSelectableChannel
        protected void implCloseSelectableChannel() throws IOException {
            this.channel.close();
        }

        @Override // java.nio.channels.spi.AbstractSelectableChannel
        protected void implConfigureBlocking(boolean blocking) throws IOException {
            IoUtils.setBlocking(getFD(), blocking);
        }

        @Override // java.nio.channels.WritableByteChannel
        public int write(ByteBuffer buffer) throws IOException {
            return this.channel.write(buffer);
        }

        @Override // java.nio.channels.GatheringByteChannel
        public long write(ByteBuffer[] buffers) throws IOException {
            return this.channel.write(buffers);
        }

        @Override // java.nio.channels.GatheringByteChannel
        public long write(ByteBuffer[] buffers, int offset, int length) throws IOException {
            return this.channel.write(buffers, offset, length);
        }

        @Override // java.nio.FileDescriptorChannel
        public FileDescriptor getFD() {
            return this.fd;
        }
    }
}