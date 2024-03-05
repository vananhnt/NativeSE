package org.apache.http;

import java.net.InetAddress;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpInetConnection.class */
public interface HttpInetConnection extends HttpConnection {
    InetAddress getLocalAddress();

    int getLocalPort();

    InetAddress getRemoteAddress();

    int getRemotePort();
}