package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: WritableByteChannel.class */
public interface WritableByteChannel extends Channel {
    int write(ByteBuffer byteBuffer) throws IOException;
}