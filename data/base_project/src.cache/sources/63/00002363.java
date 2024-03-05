package java.net;

import java.io.IOException;
import java.io.OutputStream;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CacheRequest.class */
public abstract class CacheRequest {
    public abstract void abort();

    public abstract OutputStream getBody() throws IOException;

    public CacheRequest() {
        throw new RuntimeException("Stub!");
    }
}