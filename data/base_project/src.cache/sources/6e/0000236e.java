package java.net;

import java.util.List;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CookieStore.class */
public interface CookieStore {
    void add(URI uri, HttpCookie httpCookie);

    List<HttpCookie> get(URI uri);

    List<HttpCookie> getCookies();

    List<URI> getURIs();

    boolean remove(URI uri, HttpCookie httpCookie);

    boolean removeAll();
}