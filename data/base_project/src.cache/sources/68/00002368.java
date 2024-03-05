package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CookieHandler.class */
public abstract class CookieHandler {
    public abstract Map<String, List<String>> get(URI uri, Map<String, List<String>> map) throws IOException;

    public abstract void put(URI uri, Map<String, List<String>> map) throws IOException;

    public CookieHandler() {
        throw new RuntimeException("Stub!");
    }

    public static CookieHandler getDefault() {
        throw new RuntimeException("Stub!");
    }

    public static void setDefault(CookieHandler cHandler) {
        throw new RuntimeException("Stub!");
    }
}