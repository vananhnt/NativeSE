package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SingleClientConnManager.class */
public class SingleClientConnManager implements ClientConnectionManager {
    public static final String MISUSE_MESSAGE = "Invalid use of SingleClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
    protected SchemeRegistry schemeRegistry;
    protected ClientConnectionOperator connOperator;
    protected PoolEntry uniquePoolEntry;
    protected ConnAdapter managedConn;
    protected long lastReleaseTime;
    protected long connectionExpiresTime;
    protected boolean alwaysShutDown;
    protected volatile boolean isShutDown;

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: SingleClientConnManager$PoolEntry.class */
    protected class PoolEntry extends AbstractPoolEntry {
        protected PoolEntry() {
            super(null, null);
            throw new RuntimeException("Stub!");
        }

        protected void close() throws IOException {
            throw new RuntimeException("Stub!");
        }

        protected void shutdown() throws IOException {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: SingleClientConnManager$ConnAdapter.class */
    protected class ConnAdapter extends AbstractPooledConnAdapter {
        protected ConnAdapter(PoolEntry entry, HttpRoute route) {
            super(null, null);
            throw new RuntimeException("Stub!");
        }
    }

    public SingleClientConnManager(HttpParams params, SchemeRegistry schreg) {
        throw new RuntimeException("Stub!");
    }

    protected void finalize() throws Throwable {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public SchemeRegistry getSchemeRegistry() {
        throw new RuntimeException("Stub!");
    }

    protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        throw new RuntimeException("Stub!");
    }

    protected final void assertStillUp() throws IllegalStateException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public final ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
        throw new RuntimeException("Stub!");
    }

    public ManagedClientConnection getConnection(HttpRoute route, Object state) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public void closeExpiredConnections() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public void closeIdleConnections(long idletime, TimeUnit tunit) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    protected void revokeConnection() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: org.apache.http.impl.conn.SingleClientConnManager$1  reason: invalid class name */
    /* loaded from: SingleClientConnManager$1.class */
    class AnonymousClass1 implements ClientConnectionRequest {
        final /* synthetic */ HttpRoute val$route;
        final /* synthetic */ Object val$state;

        AnonymousClass1(HttpRoute httpRoute, Object obj) {
            this.val$route = httpRoute;
            this.val$state = obj;
        }

        @Override // org.apache.http.conn.ClientConnectionRequest
        public void abortRequest() {
        }

        @Override // org.apache.http.conn.ClientConnectionRequest
        public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) {
            return SingleClientConnManager.this.getConnection(this.val$route, this.val$state);
        }
    }
}