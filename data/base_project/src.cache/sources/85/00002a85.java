package org.apache.http;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: StatusLine.class */
public interface StatusLine {
    ProtocolVersion getProtocolVersion();

    int getStatusCode();

    String getReasonPhrase();
}