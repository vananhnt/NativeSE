package java.nio.channels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import libcore.io.Streams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Channels.class */
public final class Channels {
    Channels() {
        throw new RuntimeException("Stub!");
    }

    public static InputStream newInputStream(ReadableByteChannel channel) {
        throw new RuntimeException("Stub!");
    }

    public static OutputStream newOutputStream(WritableByteChannel channel) {
        throw new RuntimeException("Stub!");
    }

    public static ReadableByteChannel newChannel(InputStream inputStream) {
        throw new RuntimeException("Stub!");
    }

    public static WritableByteChannel newChannel(OutputStream outputStream) {
        throw new RuntimeException("Stub!");
    }

    public static Reader newReader(ReadableByteChannel channel, CharsetDecoder decoder, int minBufferCapacity) {
        throw new RuntimeException("Stub!");
    }

    public static Reader newReader(ReadableByteChannel channel, String charsetName) {
        throw new RuntimeException("Stub!");
    }

    public static Writer newWriter(WritableByteChannel channel, CharsetEncoder encoder, int minBufferCapacity) {
        throw new RuntimeException("Stub!");
    }

    public static Writer newWriter(WritableByteChannel channel, String charsetName) {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: Channels$ChannelInputStream.class */
    private static class ChannelInputStream extends InputStream {
        private final ReadableByteChannel channel;

        ChannelInputStream(ReadableByteChannel channel) {
            if (channel == null) {
                throw new NullPointerException("channel == null");
            }
            this.channel = channel;
        }

        @Override // java.io.InputStream
        public synchronized int read() throws IOException {
            return Streams.readSingleByte(this);
        }

        @Override // java.io.InputStream
        public synchronized int read(byte[] target, int byteOffset, int byteCount) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap(target, byteOffset, byteCount);
            Channels.checkBlocking(this.channel);
            return this.channel.read(buffer);
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            if (this.channel instanceof FileChannel) {
                FileChannel fileChannel = (FileChannel) this.channel;
                long result = fileChannel.size() - fileChannel.position();
                if (result > 2147483647L) {
                    return Integer.MAX_VALUE;
                }
                return (int) result;
            }
            return super.available();
        }

        @Override // java.io.InputStream, java.io.Closeable
        public synchronized void close() throws IOException {
            this.channel.close();
        }
    }

    /* loaded from: Channels$ChannelOutputStream.class */
    private static class ChannelOutputStream extends OutputStream {
        private final WritableByteChannel channel;

        ChannelOutputStream(WritableByteChannel channel) {
            if (channel == null) {
                throw new NullPointerException("channel == null");
            }
            this.channel = channel;
        }

        @Override // java.io.OutputStream
        public synchronized void write(int oneByte) throws IOException {
            byte[] wrappedByte = {(byte) oneByte};
            write(wrappedByte);
        }

        @Override // java.io.OutputStream
        public synchronized void write(byte[] source, int offset, int length) throws IOException {
            ByteBuffer buffer = ByteBuffer.wrap(source, offset, length);
            Channels.checkBlocking(this.channel);
            int i = 0;
            while (true) {
                int total = i;
                if (total < length) {
                    i = total + this.channel.write(buffer);
                } else {
                    return;
                }
            }
        }

        @Override // java.io.OutputStream, java.io.Closeable
        public synchronized void close() throws IOException {
            this.channel.close();
        }
    }

    /* loaded from: Channels$InputStreamChannel.class */
    private static class InputStreamChannel extends AbstractInterruptibleChannel implements ReadableByteChannel {
        private final InputStream inputStream;

        InputStreamChannel(InputStream inputStream) {
            if (inputStream == null) {
                throw new NullPointerException("inputStream == null");
            }
            this.inputStream = inputStream;
        }

        @Override // java.nio.channels.ReadableByteChannel
        public synchronized int read(ByteBuffer target) throws IOException {
            if (!isOpen()) {
                throw new ClosedChannelException();
            }
            int bytesRemain = target.remaining();
            byte[] bytes = new byte[bytesRemain];
            int readCount = 0;
            try {
                begin();
                readCount = this.inputStream.read(bytes);
                end(readCount >= 0);
                if (readCount > 0) {
                    target.put(bytes, 0, readCount);
                }
                return readCount;
            } catch (Throwable th) {
                end(readCount >= 0);
                throw th;
            }
        }

        @Override // java.nio.channels.spi.AbstractInterruptibleChannel
        protected void implCloseChannel() throws IOException {
            this.inputStream.close();
        }
    }

    /* loaded from: Channels$OutputStreamChannel.class */
    private static class OutputStreamChannel extends AbstractInterruptibleChannel implements WritableByteChannel {
        private final OutputStream outputStream;

        OutputStreamChannel(OutputStream outputStream) {
            if (outputStream == null) {
                throw new NullPointerException("outputStream == null");
            }
            this.outputStream = outputStream;
        }

        @Override // java.nio.channels.WritableByteChannel
        public synchronized int write(ByteBuffer source) throws IOException {
            if (!isOpen()) {
                throw new ClosedChannelException();
            }
            int bytesRemain = source.remaining();
            if (bytesRemain == 0) {
                return 0;
            }
            byte[] buf = new byte[bytesRemain];
            source.get(buf);
            try {
                begin();
                this.outputStream.write(buf, 0, bytesRemain);
                end(bytesRemain >= 0);
                return bytesRemain;
            } catch (Throwable th) {
                end(bytesRemain >= 0);
                throw th;
            }
        }

        @Override // java.nio.channels.spi.AbstractInterruptibleChannel
        protected void implCloseChannel() throws IOException {
            this.outputStream.close();
        }
    }
}