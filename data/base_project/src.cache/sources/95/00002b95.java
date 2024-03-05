package org.apache.http.io;

import java.io.IOException;
import org.apache.http.util.CharArrayBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SessionOutputBuffer.class */
public interface SessionOutputBuffer {
    void write(byte[] bArr, int i, int i2) throws IOException;

    void write(byte[] bArr) throws IOException;

    void write(int i) throws IOException;

    void writeLine(String str) throws IOException;

    void writeLine(CharArrayBuffer charArrayBuffer) throws IOException;

    void flush() throws IOException;

    HttpTransportMetrics getMetrics();
}