package org.apache.http.conn;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpInetConnection;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: OperatedClientConnection.class */
public interface OperatedClientConnection extends HttpClientConnection, HttpInetConnection {
    HttpHost getTargetHost();

    boolean isSecure();

    Socket getSocket();

    void opening(Socket socket, HttpHost httpHost) throws IOException;

    void openCompleted(boolean z, HttpParams httpParams) throws IOException;

    void update(Socket socket, HttpHost httpHost, boolean z, HttpParams httpParams) throws IOException;
}