package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.params.HttpParams;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DefaultResponseParser.class */
public class DefaultResponseParser extends AbstractMessageParser {
    public DefaultResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory, HttpParams params) {
        super(null, null, null);
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.io.AbstractMessageParser
    protected HttpMessage parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException {
        throw new RuntimeException("Stub!");
    }
}