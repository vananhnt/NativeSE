package org.apache.http.client;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CredentialsProvider.class */
public interface CredentialsProvider {
    void setCredentials(AuthScope authScope, Credentials credentials);

    Credentials getCredentials(AuthScope authScope);

    void clear();
}