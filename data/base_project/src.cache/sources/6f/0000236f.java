package java.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: CookieStoreImpl.class */
final class CookieStoreImpl implements CookieStore {
    private final Map<URI, List<HttpCookie>> map = new HashMap();

    CookieStoreImpl() {
    }

    @Override // java.net.CookieStore
    public synchronized void add(URI uri, HttpCookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie == null");
        }
        URI uri2 = cookiesUri(uri);
        List<HttpCookie> cookies = this.map.get(uri2);
        if (cookies == null) {
            cookies = new ArrayList<>();
            this.map.put(uri2, cookies);
        } else {
            cookies.remove(cookie);
        }
        cookies.add(cookie);
    }

    private URI cookiesUri(URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI("http", uri.getHost(), null, null);
        } catch (URISyntaxException e) {
            return uri;
        }
    }

    @Override // java.net.CookieStore
    public synchronized List<HttpCookie> get(URI uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        List<HttpCookie> result = new ArrayList<>();
        List<HttpCookie> cookiesForUri = this.map.get(uri);
        if (cookiesForUri != null) {
            Iterator<HttpCookie> i = cookiesForUri.iterator();
            while (i.hasNext()) {
                HttpCookie cookie = i.next();
                if (cookie.hasExpired()) {
                    i.remove();
                } else {
                    result.add(cookie);
                }
            }
        }
        for (Map.Entry<URI, List<HttpCookie>> entry : this.map.entrySet()) {
            if (!uri.equals(entry.getKey())) {
                List<HttpCookie> entryCookies = entry.getValue();
                Iterator<HttpCookie> i2 = entryCookies.iterator();
                while (i2.hasNext()) {
                    HttpCookie cookie2 = i2.next();
                    if (HttpCookie.domainMatches(cookie2.getDomain(), uri.getHost())) {
                        if (cookie2.hasExpired()) {
                            i2.remove();
                        } else if (!result.contains(cookie2)) {
                            result.add(cookie2);
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override // java.net.CookieStore
    public synchronized List<HttpCookie> getCookies() {
        List<HttpCookie> result = new ArrayList<>();
        for (List<HttpCookie> list : this.map.values()) {
            Iterator<HttpCookie> i = list.iterator();
            while (i.hasNext()) {
                HttpCookie cookie = i.next();
                if (cookie.hasExpired()) {
                    i.remove();
                } else if (!result.contains(cookie)) {
                    result.add(cookie);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override // java.net.CookieStore
    public synchronized List<URI> getURIs() {
        List<URI> result = new ArrayList<>(this.map.keySet());
        result.remove((Object) null);
        return Collections.unmodifiableList(result);
    }

    @Override // java.net.CookieStore
    public synchronized boolean remove(URI uri, HttpCookie cookie) {
        if (cookie == null) {
            throw new NullPointerException("cookie == null");
        }
        List<HttpCookie> cookies = this.map.get(cookiesUri(uri));
        if (cookies != null) {
            return cookies.remove(cookie);
        }
        return false;
    }

    @Override // java.net.CookieStore
    public synchronized boolean removeAll() {
        boolean result = !this.map.isEmpty();
        this.map.clear();
        return result;
    }
}