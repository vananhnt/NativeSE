package org.apache.http;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpRequestFactory.class */
public interface HttpRequestFactory {
    HttpRequest newHttpRequest(RequestLine requestLine) throws MethodNotSupportedException;

    HttpRequest newHttpRequest(String str, String str2) throws MethodNotSupportedException;
}