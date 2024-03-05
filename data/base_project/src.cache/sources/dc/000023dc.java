package java.nio;

import java.io.FileDescriptor;
import java.nio.channels.FileChannel;

/* loaded from: NioUtils.class */
public final class NioUtils {
    private NioUtils() {
    }

    public static void freeDirectBuffer(ByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        ((DirectByteBuffer) buffer).free();
    }

    public static FileDescriptor getFD(FileChannel fc) {
        return ((FileChannelImpl) fc).getFD();
    }

    public static FileChannel newFileChannel(Object stream, FileDescriptor fd, int mode) {
        return new FileChannelImpl(stream, fd, mode);
    }

    public static byte[] unsafeArray(ByteBuffer b) {
        return ((ByteArrayBuffer) b).backingArray;
    }

    public static int unsafeArrayOffset(ByteBuffer b) {
        return ((ByteArrayBuffer) b).arrayOffset;
    }
}