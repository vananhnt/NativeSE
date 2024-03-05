package org.apache.http;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpConnection.class */
public interface HttpConnection {
    void close() throws IOException;

    boolean isOpen();

    boolean isStale();

    void setSocketTimeout(int i);

    int getSocketTimeout();

    void shutdown() throws IOException;

    HttpConnectionMetrics getMetrics();
}