package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ReadableByteChannel.class */
public interface ReadableByteChannel extends Channel {
    int read(ByteBuffer byteBuffer) throws IOException;
}