package org.apache.http.impl.conn.tsccm;

import java.util.concurrent.TimeUnit;
import org.apache.http.conn.ConnectionPoolTimeoutException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PoolEntryRequest.class */
public interface PoolEntryRequest {
    BasicPoolEntry getPoolEntry(long j, TimeUnit timeUnit) throws InterruptedException, ConnectionPoolTimeoutException;

    void abortRequest();
}