package java.nio.charset.spi;

import java.nio.charset.Charset;
import java.util.Iterator;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CharsetProvider.class */
public abstract class CharsetProvider {
    public abstract Iterator<Charset> charsets();

    public abstract Charset charsetForName(String str);

    protected CharsetProvider() {
        throw new RuntimeException("Stub!");
    }
}