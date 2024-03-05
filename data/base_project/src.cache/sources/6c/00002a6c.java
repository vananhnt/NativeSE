package org.apache.http;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpConnectionMetrics.class */
public interface HttpConnectionMetrics {
    long getRequestCount();

    long getResponseCount();

    long getSentBytesCount();

    long getReceivedBytesCount();

    Object getMetric(String str);

    void reset();
}