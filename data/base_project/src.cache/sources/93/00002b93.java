package org.apache.http.io;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpTransportMetrics.class */
public interface HttpTransportMetrics {
    long getBytesTransferred();

    void reset();
}