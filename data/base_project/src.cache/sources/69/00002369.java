package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CookieManager.class */
public class CookieManager extends CookieHandler {
    public CookieManager() {
        throw new RuntimeException("Stub!");
    }

    public CookieManager(CookieStore store, CookiePolicy cookiePolicy) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.net.CookieHandler
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.net.CookieHandler
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void setCookiePolicy(CookiePolicy cookiePolicy) {
        throw new RuntimeException("Stub!");
    }

    public CookieStore getCookieStore() {
        throw new RuntimeException("Stub!");
    }
}