package org.apache.http.impl.cookie;

import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RFC2109VersionHandler.class */
public class RFC2109VersionHandler extends AbstractCookieAttributeHandler {
    public RFC2109VersionHandler() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.cookie.CookieAttributeHandler
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.cookie.AbstractCookieAttributeHandler, org.apache.http.cookie.CookieAttributeHandler
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        throw new RuntimeException("Stub!");
    }
}