package libcore.io;

import java.io.FileDescriptor;

/* loaded from: StructPollfd.class */
public final class StructPollfd {
    public FileDescriptor fd;
    public short events;
    public short revents;
    public Object userData;

    public String toString() {
        return "StructPollfd[fd=" + this.fd + ",events=" + ((int) this.events) + ",revents=" + ((int) this.revents) + "]";
    }
}