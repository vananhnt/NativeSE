package org.apache.http.conn;

import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ClientConnectionManagerFactory.class */
public interface ClientConnectionManagerFactory {
    ClientConnectionManager newInstance(HttpParams httpParams, SchemeRegistry schemeRegistry);
}