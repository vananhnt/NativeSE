package org.apache.http.conn;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConnectionReleaseTrigger.class */
public interface ConnectionReleaseTrigger {
    void releaseConnection() throws IOException;

    void abortConnection() throws IOException;
}