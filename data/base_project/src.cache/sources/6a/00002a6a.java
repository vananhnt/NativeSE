package org.apache.http;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpClientConnection.class */
public interface HttpClientConnection extends HttpConnection {
    boolean isResponseAvailable(int i) throws IOException;

    void sendRequestHeader(HttpRequest httpRequest) throws HttpException, IOException;

    void sendRequestEntity(HttpEntityEnclosingRequest httpEntityEnclosingRequest) throws HttpException, IOException;

    HttpResponse receiveResponseHeader() throws HttpException, IOException;

    void receiveResponseEntity(HttpResponse httpResponse) throws HttpException, IOException;

    void flush() throws IOException;
}