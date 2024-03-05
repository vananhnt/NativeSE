package org.apache.http.impl;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.TokenIterator;
import org.apache.http.protocol.HttpContext;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DefaultConnectionReuseStrategy.class */
public class DefaultConnectionReuseStrategy implements ConnectionReuseStrategy {
    public DefaultConnectionReuseStrategy() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.ConnectionReuseStrategy
    public boolean keepAlive(HttpResponse response, HttpContext context) {
        throw new RuntimeException("Stub!");
    }

    protected TokenIterator createTokenIterator(HeaderIterator hit) {
        throw new RuntimeException("Stub!");
    }
}