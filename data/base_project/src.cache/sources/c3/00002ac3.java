package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BasicEofSensorWatcher.class */
public class BasicEofSensorWatcher implements EofSensorWatcher {
    protected ManagedClientConnection managedConn;
    protected boolean attemptReuse;

    public BasicEofSensorWatcher(ManagedClientConnection conn, boolean reuse) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.EofSensorWatcher
    public boolean eofDetected(InputStream wrapped) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.EofSensorWatcher
    public boolean streamClosed(InputStream wrapped) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.EofSensorWatcher
    public boolean streamAbort(InputStream wrapped) throws IOException {
        throw new RuntimeException("Stub!");
    }
}