package org.apache.http.conn.params;

import org.apache.http.conn.routing.HttpRoute;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConnPerRoute.class */
public interface ConnPerRoute {
    int getMaxForRoute(HttpRoute httpRoute);
}