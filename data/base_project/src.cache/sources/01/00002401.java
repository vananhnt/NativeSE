package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: GatheringByteChannel.class */
public interface GatheringByteChannel extends WritableByteChannel {
    long write(ByteBuffer[] byteBufferArr) throws IOException;

    long write(ByteBuffer[] byteBufferArr, int i, int i2) throws IOException;
}