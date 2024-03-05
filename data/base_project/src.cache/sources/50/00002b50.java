package org.apache.http.impl.conn.tsccm;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConnPoolByRoute.class */
public class ConnPoolByRoute extends AbstractConnPool {
    protected final ClientConnectionOperator operator;
    protected Queue<BasicPoolEntry> freeConnections;
    protected Queue<WaitingThread> waitingThreads;
    protected final Map<HttpRoute, RouteSpecificPool> routeToPool;
    protected final int maxTotalConnections;

    public ConnPoolByRoute(ClientConnectionOperator operator, HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    protected Queue<BasicPoolEntry> createFreeConnQueue() {
        throw new RuntimeException("Stub!");
    }

    protected Queue<WaitingThread> createWaitingThreadQueue() {
        throw new RuntimeException("Stub!");
    }

    protected Map<HttpRoute, RouteSpecificPool> createRouteToPoolMap() {
        throw new RuntimeException("Stub!");
    }

    protected RouteSpecificPool newRouteSpecificPool(HttpRoute route) {
        throw new RuntimeException("Stub!");
    }

    protected WaitingThread newWaitingThread(Condition cond, RouteSpecificPool rospl) {
        throw new RuntimeException("Stub!");
    }

    protected RouteSpecificPool getRoutePool(HttpRoute route, boolean create) {
        throw new RuntimeException("Stub!");
    }

    public int getConnectionsInPool(HttpRoute route) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.conn.tsccm.AbstractConnPool
    public PoolEntryRequest requestPoolEntry(HttpRoute route, Object state) {
        throw new RuntimeException("Stub!");
    }

    protected BasicPoolEntry getEntryBlocking(HttpRoute route, Object state, long timeout, TimeUnit tunit, WaitingThreadAborter aborter) throws ConnectionPoolTimeoutException, InterruptedException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.conn.tsccm.AbstractConnPool
    public void freeEntry(BasicPoolEntry entry, boolean reusable, long validDuration, TimeUnit timeUnit) {
        throw new RuntimeException("Stub!");
    }

    protected BasicPoolEntry getFreeEntry(RouteSpecificPool rospl, Object state) {
        throw new RuntimeException("Stub!");
    }

    protected BasicPoolEntry createEntry(RouteSpecificPool rospl, ClientConnectionOperator op) {
        throw new RuntimeException("Stub!");
    }

    protected void deleteEntry(BasicPoolEntry entry) {
        throw new RuntimeException("Stub!");
    }

    protected void deleteLeastUsedEntry() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.conn.tsccm.AbstractConnPool
    protected void handleLostEntry(HttpRoute route) {
        throw new RuntimeException("Stub!");
    }

    protected void notifyWaitingThread(RouteSpecificPool rospl) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.conn.tsccm.AbstractConnPool
    public void deleteClosedConnections() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.conn.tsccm.AbstractConnPool
    public void shutdown() {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: org.apache.http.impl.conn.tsccm.ConnPoolByRoute$1  reason: invalid class name */
    /* loaded from: ConnPoolByRoute$1.class */
    class AnonymousClass1 implements PoolEntryRequest {
        final /* synthetic */ WaitingThreadAborter val$aborter;
        final /* synthetic */ HttpRoute val$route;
        final /* synthetic */ Object val$state;

        AnonymousClass1(WaitingThreadAborter waitingThreadAborter, HttpRoute httpRoute, Object obj) {
            this.val$aborter = waitingThreadAborter;
            this.val$route = httpRoute;
            this.val$state = obj;
        }

        @Override // org.apache.http.impl.conn.tsccm.PoolEntryRequest
        public void abortRequest() {
            ConnPoolByRoute.this.poolLock.lock();
            try {
                this.val$aborter.abort();
                ConnPoolByRoute.this.poolLock.unlock();
            } catch (Throwable th) {
                ConnPoolByRoute.this.poolLock.unlock();
                throw th;
            }
        }

        @Override // org.apache.http.impl.conn.tsccm.PoolEntryRequest
        public BasicPoolEntry getPoolEntry(long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
            return ConnPoolByRoute.this.getEntryBlocking(this.val$route, this.val$state, timeout, tunit, this.val$aborter);
        }
    }
}