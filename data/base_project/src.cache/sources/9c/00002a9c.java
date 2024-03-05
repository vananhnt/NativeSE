package org.apache.http.client;

import java.util.Date;
import java.util.List;
import org.apache.http.cookie.Cookie;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CookieStore.class */
public interface CookieStore {
    void addCookie(Cookie cookie);

    List<Cookie> getCookies();

    boolean clearExpired(Date date);

    void clear();
}