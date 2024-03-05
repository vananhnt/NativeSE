package org.apache.http.cookie;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CookieAttributeHandler.class */
public interface CookieAttributeHandler {
    void parse(SetCookie setCookie, String str) throws MalformedCookieException;

    void validate(Cookie cookie, CookieOrigin cookieOrigin) throws MalformedCookieException;

    boolean match(Cookie cookie, CookieOrigin cookieOrigin);
}