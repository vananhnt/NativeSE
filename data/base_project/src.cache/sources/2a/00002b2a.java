package org.apache.http.impl.client;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BasicCredentialsProvider.class */
public class BasicCredentialsProvider implements CredentialsProvider {
    public BasicCredentialsProvider() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.client.CredentialsProvider
    public synchronized void setCredentials(AuthScope authscope, Credentials credentials) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.client.CredentialsProvider
    public synchronized Credentials getCredentials(AuthScope authscope) {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.client.CredentialsProvider
    public synchronized void clear() {
        throw new RuntimeException("Stub!");
    }
}