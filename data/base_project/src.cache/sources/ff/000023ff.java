package java.nio.channels;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FileLock.class */
public abstract class FileLock {
    public abstract boolean isValid();

    public abstract void release() throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public FileLock(FileChannel channel, long position, long size, boolean shared) {
        throw new RuntimeException("Stub!");
    }

    public final FileChannel channel() {
        throw new RuntimeException("Stub!");
    }

    public final long position() {
        throw new RuntimeException("Stub!");
    }

    public final long size() {
        throw new RuntimeException("Stub!");
    }

    public final boolean isShared() {
        throw new RuntimeException("Stub!");
    }

    public final boolean overlaps(long start, long length) {
        throw new RuntimeException("Stub!");
    }

    public final String toString() {
        throw new RuntimeException("Stub!");
    }
}