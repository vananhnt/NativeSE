package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AbstractMessageWriter.class */
public abstract class AbstractMessageWriter implements HttpMessageWriter {
    protected final SessionOutputBuffer sessionBuffer;
    protected final CharArrayBuffer lineBuf;
    protected final LineFormatter lineFormatter;

    protected abstract void writeHeadLine(HttpMessage httpMessage) throws IOException;

    public AbstractMessageWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.io.HttpMessageWriter
    public void write(HttpMessage message) throws IOException, HttpException {
        throw new RuntimeException("Stub!");
    }
}