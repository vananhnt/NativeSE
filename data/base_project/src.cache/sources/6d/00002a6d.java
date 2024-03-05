package org.apache.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpEntity.class */
public interface HttpEntity {
    boolean isRepeatable();

    boolean isChunked();

    long getContentLength();

    Header getContentType();

    Header getContentEncoding();

    InputStream getContent() throws IOException, IllegalStateException;

    void writeTo(OutputStream outputStream) throws IOException;

    boolean isStreaming();

    void consumeContent() throws IOException;
}