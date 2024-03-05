package org.apache.http.impl.client;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TunnelRefusedException.class */
public class TunnelRefusedException extends HttpException {
    public TunnelRefusedException(String message, HttpResponse response) {
        throw new RuntimeException("Stub!");
    }

    public HttpResponse getResponse() {
        throw new RuntimeException("Stub!");
    }
}