package java.nio.channels;

import java.io.Closeable;
import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Channel.class */
public interface Channel extends Closeable {
    boolean isOpen();

    @Override // java.io.Closeable
    void close() throws IOException;
}