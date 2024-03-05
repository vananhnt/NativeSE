package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CacheResponse.class */
public abstract class CacheResponse {
    public abstract InputStream getBody() throws IOException;

    public abstract Map<String, List<String>> getHeaders() throws IOException;

    public CacheResponse() {
        throw new RuntimeException("Stub!");
    }
}