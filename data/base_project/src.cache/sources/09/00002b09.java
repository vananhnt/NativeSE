package org.apache.http.entity;

import java.io.IOException;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ContentProducer.class */
public interface ContentProducer {
    void writeTo(OutputStream outputStream) throws IOException;
}