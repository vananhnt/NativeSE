package org.apache.http.message;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: BasicStatusLine.class */
public class BasicStatusLine implements StatusLine, Cloneable {
    public BasicStatusLine(ProtocolVersion version, int statusCode, String reasonPhrase) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.StatusLine
    public int getStatusCode() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.StatusLine
    public ProtocolVersion getProtocolVersion() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.StatusLine
    public String getReasonPhrase() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }
}