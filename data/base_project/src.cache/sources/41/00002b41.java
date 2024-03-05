package org.apache.http.impl.conn;

import java.util.concurrent.TimeUnit;
import org.apache.http.HttpConnection;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: IdleConnectionHandler.class */
public class IdleConnectionHandler {
    public IdleConnectionHandler() {
        throw new RuntimeException("Stub!");
    }

    public void add(HttpConnection connection, long validDuration, TimeUnit unit) {
        throw new RuntimeException("Stub!");
    }

    public boolean remove(HttpConnection connection) {
        throw new RuntimeException("Stub!");
    }

    public void removeAll() {
        throw new RuntimeException("Stub!");
    }

    public void closeIdleConnections(long idleTime) {
        throw new RuntimeException("Stub!");
    }

    public void closeExpiredConnections() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: IdleConnectionHandler$TimeValues.class */
    private static class TimeValues {
        private final long timeAdded;
        private final long timeExpires;

        TimeValues(long now, long validDuration, TimeUnit validUnit) {
            this.timeAdded = now;
            if (validDuration > 0) {
                this.timeExpires = now + validUnit.toMillis(validDuration);
            } else {
                this.timeExpires = Long.MAX_VALUE;
            }
        }
    }
}