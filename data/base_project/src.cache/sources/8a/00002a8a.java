package org.apache.http.auth;

import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AuthSchemeFactory.class */
public interface AuthSchemeFactory {
    AuthScheme newInstance(HttpParams httpParams);
}