package org.apache.http.impl.conn.tsccm;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.AbstractPoolEntry;
import org.apache.http.impl.conn.AbstractPooledConnAdapter;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BasicPooledConnAdapter.class */
public class BasicPooledConnAdapter extends AbstractPooledConnAdapter {
    /* JADX INFO: Access modifiers changed from: protected */
    public BasicPooledConnAdapter(ThreadSafeClientConnManager tsccm, AbstractPoolEntry entry) {
        super(null, null);
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.conn.AbstractClientConnAdapter
    protected ClientConnectionManager getManager() {
        throw new RuntimeException("Stub!");
    }

    protected AbstractPoolEntry getPoolEntry() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.conn.AbstractPooledConnAdapter, org.apache.http.impl.conn.AbstractClientConnAdapter
    protected void detach() {
        throw new RuntimeException("Stub!");
    }
}