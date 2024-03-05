package org.apache.http.io;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpMessageWriter.class */
public interface HttpMessageWriter {
    void write(HttpMessage httpMessage) throws IOException, HttpException;
}