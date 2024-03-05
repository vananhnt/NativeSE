package org.apache.http.impl.client;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DefaultConnectionKeepAliveStrategy.class */
public class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
    public DefaultConnectionKeepAliveStrategy() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ConnectionKeepAliveStrategy
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        throw new RuntimeException("Stub!");
    }
}