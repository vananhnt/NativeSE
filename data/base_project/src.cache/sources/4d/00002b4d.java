package org.apache.http.impl.conn.tsccm;

import java.lang.ref.ReferenceQueue;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.AbstractPoolEntry;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BasicPoolEntry.class */
public class BasicPoolEntry extends AbstractPoolEntry {
    public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route, ReferenceQueue<Object> queue) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final OperatedClientConnection getConnection() {
        throw new RuntimeException("Stub!");
    }

    protected final HttpRoute getPlannedRoute() {
        throw new RuntimeException("Stub!");
    }

    protected final BasicPoolEntryRef getWeakRef() {
        throw new RuntimeException("Stub!");
    }
}