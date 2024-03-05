package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PlainSocketFactory.class */
public final class PlainSocketFactory implements SocketFactory {
    public PlainSocketFactory(HostNameResolver nameResolver) {
        throw new RuntimeException("Stub!");
    }

    public PlainSocketFactory() {
        throw new RuntimeException("Stub!");
    }

    public static PlainSocketFactory getSocketFactory() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.scheme.SocketFactory
    public Socket createSocket() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.scheme.SocketFactory
    public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.conn.scheme.SocketFactory
    public final boolean isSecure(Socket sock) throws IllegalArgumentException {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object obj) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }
}