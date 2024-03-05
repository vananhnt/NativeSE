package libcore.io;

import java.io.FileDescriptor;

/* loaded from: AsynchronousCloseMonitor.class */
public final class AsynchronousCloseMonitor {
    public static native void signalBlockedThreads(FileDescriptor fileDescriptor);

    private AsynchronousCloseMonitor() {
    }
}