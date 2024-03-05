package org.apache.http;

import org.apache.http.util.CharArrayBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: FormattedHeader.class */
public interface FormattedHeader extends Header {
    CharArrayBuffer getBuffer();

    int getValuePos();
}