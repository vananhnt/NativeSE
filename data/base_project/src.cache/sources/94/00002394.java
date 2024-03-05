package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ResponseCache.class */
public abstract class ResponseCache {
    public abstract CacheResponse get(URI uri, String str, Map<String, List<String>> map) throws IOException;

    public abstract CacheRequest put(URI uri, URLConnection uRLConnection) throws IOException;

    public ResponseCache() {
        throw new RuntimeException("Stub!");
    }

    public static ResponseCache getDefault() {
        throw new RuntimeException("Stub!");
    }

    public static void setDefault(ResponseCache responseCache) {
        throw new RuntimeException("Stub!");
    }
}