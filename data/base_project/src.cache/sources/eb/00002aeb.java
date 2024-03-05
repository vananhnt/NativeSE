package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SocketFactory.class */
public interface SocketFactory {
    Socket createSocket() throws IOException;

    Socket connectSocket(Socket socket, String str, int i, InetAddress inetAddress, int i2, HttpParams httpParams) throws IOException, UnknownHostException, ConnectTimeoutException;

    boolean isSecure(Socket socket) throws IllegalArgumentException;
}