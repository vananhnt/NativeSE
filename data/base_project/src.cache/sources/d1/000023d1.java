package java.nio;

import java.io.FileDescriptor;
import java.io.IOException;
import libcore.io.ErrnoException;
import libcore.io.Libcore;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: IoVec.class */
public final class IoVec {
    private final ByteBuffer[] byteBuffers;
    private final int offset;
    private final int bufferCount;
    private final Object[] ioBuffers;
    private final int[] offsets;
    private final int[] byteCounts;
    private final Direction direction;

    /* loaded from: IoVec$Direction.class */
    enum Direction {
        READV,
        WRITEV
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IoVec(ByteBuffer[] byteBuffers, int offset, int bufferCount, Direction direction) {
        this.byteBuffers = byteBuffers;
        this.offset = offset;
        this.bufferCount = bufferCount;
        this.direction = direction;
        this.ioBuffers = new Object[bufferCount];
        this.offsets = new int[bufferCount];
        this.byteCounts = new int[bufferCount];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int init() {
        int totalRemaining = 0;
        for (int i = 0; i < this.bufferCount; i++) {
            ByteBuffer b = this.byteBuffers[i + this.offset];
            if (this.direction == Direction.READV) {
                b.checkWritable();
            }
            int remaining = b.remaining();
            if (b.isDirect()) {
                this.ioBuffers[i] = b;
                this.offsets[i] = b.position();
            } else {
                this.ioBuffers[i] = NioUtils.unsafeArray(b);
                this.offsets[i] = NioUtils.unsafeArrayOffset(b) + b.position();
            }
            this.byteCounts[i] = remaining;
            totalRemaining += remaining;
        }
        return totalRemaining;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int doTransfer(FileDescriptor fd) throws IOException {
        try {
            if (this.direction == Direction.READV) {
                int result = Libcore.os.readv(fd, this.ioBuffers, this.offsets, this.byteCounts);
                if (result == 0) {
                    result = -1;
                }
                return result;
            }
            return Libcore.os.writev(fd, this.ioBuffers, this.offsets, this.byteCounts);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    void didTransfer(int byteCount) {
        int i;
        for (int i2 = 0; byteCount > 0 && i2 < this.bufferCount; i2++) {
            ByteBuffer b = this.byteBuffers[i2 + this.offset];
            if (this.byteCounts[i2] < byteCount) {
                b.position(b.limit());
                i = byteCount - this.byteCounts[i2];
            } else {
                b.position((this.direction == Direction.WRITEV ? b.position() : 0) + byteCount);
                i = 0;
            }
            byteCount = i;
        }
    }
}