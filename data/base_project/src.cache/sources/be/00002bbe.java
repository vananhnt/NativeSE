package org.apache.http.protocol;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpContext.class */
public interface HttpContext {
    public static final String RESERVED_PREFIX = "http.";

    Object getAttribute(String str);

    void setAttribute(String str, Object obj);

    Object removeAttribute(String str);
}