package java.nio.channels;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: InterruptibleChannel.class */
public interface InterruptibleChannel extends Channel {
    @Override // java.nio.channels.Channel, java.io.Closeable
    void close() throws IOException;
}