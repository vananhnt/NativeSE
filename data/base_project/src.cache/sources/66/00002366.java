package java.net;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ContentHandler.class */
public abstract class ContentHandler {
    public abstract Object getContent(URLConnection uRLConnection) throws IOException;

    public ContentHandler() {
        throw new RuntimeException("Stub!");
    }

    public Object getContent(URLConnection uConn, Class[] types) throws IOException {
        throw new RuntimeException("Stub!");
    }
}