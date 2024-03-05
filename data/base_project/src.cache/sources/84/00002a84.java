package org.apache.http;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RequestLine.class */
public interface RequestLine {
    String getMethod();

    ProtocolVersion getProtocolVersion();

    String getUri();
}