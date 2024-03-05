package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.util.CharArrayBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LineFormatter.class */
public interface LineFormatter {
    CharArrayBuffer appendProtocolVersion(CharArrayBuffer charArrayBuffer, ProtocolVersion protocolVersion);

    CharArrayBuffer formatRequestLine(CharArrayBuffer charArrayBuffer, RequestLine requestLine);

    CharArrayBuffer formatStatusLine(CharArrayBuffer charArrayBuffer, StatusLine statusLine);

    CharArrayBuffer formatHeader(CharArrayBuffer charArrayBuffer, Header header);
}