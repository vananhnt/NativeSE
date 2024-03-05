package org.apache.http.impl.entity;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.entity.ContentLengthStrategy;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LaxContentLengthStrategy.class */
public class LaxContentLengthStrategy implements ContentLengthStrategy {
    public LaxContentLengthStrategy() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.entity.ContentLengthStrategy
    public long determineLength(HttpMessage message) throws HttpException {
        throw new RuntimeException("Stub!");
    }
}