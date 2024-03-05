package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ScatteringByteChannel.class */
public interface ScatteringByteChannel extends ReadableByteChannel {
    long read(ByteBuffer[] byteBufferArr) throws IOException;

    long read(ByteBuffer[] byteBufferArr, int i, int i2) throws IOException;
}