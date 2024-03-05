package org.apache.http.io;

import java.io.IOException;
import org.apache.http.util.CharArrayBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SessionInputBuffer.class */
public interface SessionInputBuffer {
    int read(byte[] bArr, int i, int i2) throws IOException;

    int read(byte[] bArr) throws IOException;

    int read() throws IOException;

    int readLine(CharArrayBuffer charArrayBuffer) throws IOException;

    String readLine() throws IOException;

    boolean isDataAvailable(int i) throws IOException;

    HttpTransportMetrics getMetrics();
}