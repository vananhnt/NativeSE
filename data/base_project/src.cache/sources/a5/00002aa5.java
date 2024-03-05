package org.apache.http.client;

import java.io.IOException;
import org.apache.http.HttpResponse;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ResponseHandler.class */
public interface ResponseHandler<T> {
    T handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException;
}