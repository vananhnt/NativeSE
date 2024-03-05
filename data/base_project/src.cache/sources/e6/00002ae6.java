package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HostNameResolver.class */
public interface HostNameResolver {
    InetAddress resolve(String str) throws IOException;
}