package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: EofSensorWatcher.class */
public interface EofSensorWatcher {
    boolean eofDetected(InputStream inputStream) throws IOException;

    boolean streamClosed(InputStream inputStream) throws IOException;

    boolean streamAbort(InputStream inputStream) throws IOException;
}