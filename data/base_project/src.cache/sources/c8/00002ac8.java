package org.apache.http.conn;

import java.util.concurrent.TimeUnit;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ClientConnectionRequest.class */
public interface ClientConnectionRequest {
    ManagedClientConnection getConnection(long j, TimeUnit timeUnit) throws InterruptedException, ConnectionPoolTimeoutException;

    void abortRequest();
}