package org.apache.http.conn.params;

import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConnManagerParams.class */
public final class ConnManagerParams implements ConnManagerPNames {
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;

    public ConnManagerParams() {
        throw new RuntimeException("Stub!");
    }

    public static long getTimeout(HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    public static void setTimeout(HttpParams params, long timeout) {
        throw new RuntimeException("Stub!");
    }

    public static void setMaxConnectionsPerRoute(HttpParams params, ConnPerRoute connPerRoute) {
        throw new RuntimeException("Stub!");
    }

    public static ConnPerRoute getMaxConnectionsPerRoute(HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    public static void setMaxTotalConnections(HttpParams params, int maxTotalConnections) {
        throw new RuntimeException("Stub!");
    }

    public static int getMaxTotalConnections(HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: org.apache.http.conn.params.ConnManagerParams$1  reason: invalid class name */
    /* loaded from: ConnManagerParams$1.class */
    static class AnonymousClass1 implements ConnPerRoute {
        AnonymousClass1() {
        }

        @Override // org.apache.http.conn.params.ConnPerRoute
        public int getMaxForRoute(HttpRoute route) {
            return 2;
        }
    }
}