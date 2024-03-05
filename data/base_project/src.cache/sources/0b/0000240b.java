package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Pipe.class */
public abstract class Pipe {
    public abstract SinkChannel sink();

    public abstract SourceChannel source();

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Pipe$SinkChannel.class */
    public static abstract class SinkChannel extends AbstractSelectableChannel implements WritableByteChannel, GatheringByteChannel {
        /* JADX INFO: Access modifiers changed from: protected */
        public SinkChannel(SelectorProvider provider) {
            super(null);
            throw new RuntimeException("Stub!");
        }

        @Override // java.nio.channels.SelectableChannel
        public final int validOps() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Pipe$SourceChannel.class */
    public static abstract class SourceChannel extends AbstractSelectableChannel implements ReadableByteChannel, ScatteringByteChannel {
        /* JADX INFO: Access modifiers changed from: protected */
        public SourceChannel(SelectorProvider provider) {
            super(null);
            throw new RuntimeException("Stub!");
        }

        @Override // java.nio.channels.SelectableChannel
        public final int validOps() {
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Pipe() {
        throw new RuntimeException("Stub!");
    }

    public static Pipe open() throws IOException {
        throw new RuntimeException("Stub!");
    }
}