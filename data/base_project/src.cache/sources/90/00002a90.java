package org.apache.http.auth;

import java.security.Principal;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Credentials.class */
public interface Credentials {
    Principal getUserPrincipal();

    String getPassword();
}