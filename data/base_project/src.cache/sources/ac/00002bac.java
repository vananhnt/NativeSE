package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.util.CharArrayBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LineParser.class */
public interface LineParser {
    ProtocolVersion parseProtocolVersion(CharArrayBuffer charArrayBuffer, ParserCursor parserCursor) throws ParseException;

    boolean hasProtocolVersion(CharArrayBuffer charArrayBuffer, ParserCursor parserCursor);

    RequestLine parseRequestLine(CharArrayBuffer charArrayBuffer, ParserCursor parserCursor) throws ParseException;

    StatusLine parseStatusLine(CharArrayBuffer charArrayBuffer, ParserCursor parserCursor) throws ParseException;

    Header parseHeader(CharArrayBuffer charArrayBuffer) throws ParseException;
}