package android.media;

import java.io.Closeable;

/* loaded from: DataSource.class */
public interface DataSource extends Closeable {
    int readAt(long j, byte[] bArr, int i);

    long getSize();
}