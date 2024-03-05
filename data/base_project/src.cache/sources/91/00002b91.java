package org.apache.http.io;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpMessageParser.class */
public interface HttpMessageParser {
    HttpMessage parse() throws IOException, HttpException;
}