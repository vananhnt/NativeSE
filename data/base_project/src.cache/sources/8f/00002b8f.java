package org.apache.http.impl.io;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SocketInputBuffer.class */
public class SocketInputBuffer extends AbstractSessionInputBuffer {
    public SocketInputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.io.SessionInputBuffer
    public boolean isDataAvailable(int timeout) throws IOException {
        throw new RuntimeException("Stub!");
    }
}