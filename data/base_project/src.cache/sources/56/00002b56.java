package org.apache.http.impl.conn.tsccm;

import dalvik.system.SocketTagger;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ThreadSafeClientConnManager.class */
public class ThreadSafeClientConnManager implements ClientConnectionManager {
    protected SchemeRegistry schemeRegistry;
    protected final AbstractConnPool connectionPool;
    protected ClientConnectionOperator connOperator;

    public ThreadSafeClientConnManager(HttpParams params, SchemeRegistry schreg) {
        throw new RuntimeException("Stub!");
    }

    protected void finalize() throws Throwable {
        throw new RuntimeException("Stub!");
    }

    protected AbstractConnPool createConnectionPool(HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public SchemeRegistry getSchemeRegistry() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    public int getConnectionsInPool(HttpRoute route) {
        throw new RuntimeException("Stub!");
    }

    public int getConnectionsInPool() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public void closeIdleConnections(long idleTimeout, TimeUnit tunit) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.ClientConnectionManager
    public void closeExpiredConnections() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager$1  reason: invalid class name */
    /* loaded from: ThreadSafeClientConnManager$1.class */
    class AnonymousClass1 implements ClientConnectionRequest {
        final /* synthetic */ PoolEntryRequest val$poolRequest;
        final /* synthetic */ HttpRoute val$route;

        AnonymousClass1(PoolEntryRequest poolEntryRequest, HttpRoute httpRoute) {
            this.val$poolRequest = poolEntryRequest;
            this.val$route = httpRoute;
        }

        @Override // org.apache.http.conn.ClientConnectionRequest
        public void abortRequest() {
            this.val$poolRequest.abortRequest();
        }

        @Override // org.apache.http.conn.ClientConnectionRequest
        public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
            if (this.val$route == null) {
                throw new IllegalArgumentException("Route may not be null.");
            }
            if (ThreadSafeClientConnManager.access$000(ThreadSafeClientConnManager.this).isDebugEnabled()) {
                ThreadSafeClientConnManager.access$000(ThreadSafeClientConnManager.this).debug("ThreadSafeClientConnManager.getConnection: " + this.val$route + ", timeout = " + timeout);
            }
            BasicPoolEntry entry = this.val$poolRequest.getPoolEntry(timeout, tunit);
            try {
                Socket socket = entry.getConnection().getSocket();
                if (socket != null) {
                    SocketTagger.get().tag(socket);
                }
            } catch (IOException iox) {
                ThreadSafeClientConnManager.access$000(ThreadSafeClientConnManager.this).debug("Problem tagging socket.", iox);
            }
            return new BasicPooledConnAdapter(ThreadSafeClientConnManager.this, entry);
        }
    }
}