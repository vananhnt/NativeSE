package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.spi.AbstractInterruptibleChannel;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FileChannel.class */
public abstract class FileChannel extends AbstractInterruptibleChannel implements GatheringByteChannel, ScatteringByteChannel, ByteChannel {
    public abstract void force(boolean z) throws IOException;

    public abstract FileLock lock(long j, long j2, boolean z) throws IOException;

    public abstract MappedByteBuffer map(MapMode mapMode, long j, long j2) throws IOException;

    public abstract long position() throws IOException;

    public abstract FileChannel position(long j) throws IOException;

    public abstract int read(ByteBuffer byteBuffer) throws IOException;

    public abstract int read(ByteBuffer byteBuffer, long j) throws IOException;

    public abstract long read(ByteBuffer[] byteBufferArr, int i, int i2) throws IOException;

    public abstract long size() throws IOException;

    public abstract long transferFrom(ReadableByteChannel readableByteChannel, long j, long j2) throws IOException;

    public abstract long transferTo(long j, long j2, WritableByteChannel writableByteChannel) throws IOException;

    public abstract FileChannel truncate(long j) throws IOException;

    public abstract FileLock tryLock(long j, long j2, boolean z) throws IOException;

    public abstract int write(ByteBuffer byteBuffer) throws IOException;

    public abstract int write(ByteBuffer byteBuffer, long j) throws IOException;

    public abstract long write(ByteBuffer[] byteBufferArr, int i, int i2) throws IOException;

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: FileChannel$MapMode.class */
    public static class MapMode {
        public static final MapMode PRIVATE = null;
        public static final MapMode READ_ONLY = null;
        public static final MapMode READ_WRITE = null;

        MapMode() {
            throw new RuntimeException("Stub!");
        }

        public String toString() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FileChannel() {
        throw new RuntimeException("Stub!");
    }

    public final FileLock lock() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.ScatteringByteChannel
    public final long read(ByteBuffer[] buffers) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public final FileLock tryLock() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.channels.GatheringByteChannel
    public final long write(ByteBuffer[] buffers) throws IOException {
        throw new RuntimeException("Stub!");
    }
}