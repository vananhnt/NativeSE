package android.os;

import android.app.backup.FullBackup;
import android.os.Parcelable;
import android.util.Log;
import dalvik.system.CloseGuard;
import gov.nist.core.Separators;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteOrder;
import libcore.io.ErrnoException;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.io.Memory;
import libcore.io.OsConstants;
import libcore.io.StructStat;

/* loaded from: ParcelFileDescriptor.class */
public class ParcelFileDescriptor implements Parcelable, Closeable {
    private static final String TAG = "ParcelFileDescriptor";
    private final FileDescriptor mFd;
    private FileDescriptor mCommFd;
    private final ParcelFileDescriptor mWrapped;
    private static final int MAX_STATUS = 1024;
    private byte[] mStatusBuf;
    private Status mStatus;
    private volatile boolean mClosed;
    private final CloseGuard mGuard;
    @Deprecated
    public static final int MODE_WORLD_READABLE = 1;
    @Deprecated
    public static final int MODE_WORLD_WRITEABLE = 2;
    public static final int MODE_READ_ONLY = 268435456;
    public static final int MODE_WRITE_ONLY = 536870912;
    public static final int MODE_READ_WRITE = 805306368;
    public static final int MODE_CREATE = 134217728;
    public static final int MODE_TRUNCATE = 67108864;
    public static final int MODE_APPEND = 33554432;
    public static final Parcelable.Creator<ParcelFileDescriptor> CREATOR = new Parcelable.Creator<ParcelFileDescriptor>() { // from class: android.os.ParcelFileDescriptor.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParcelFileDescriptor createFromParcel(Parcel in) {
            FileDescriptor fd = in.readRawFileDescriptor();
            FileDescriptor commChannel = null;
            if (in.readInt() != 0) {
                commChannel = in.readRawFileDescriptor();
            }
            return new ParcelFileDescriptor(fd, commChannel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParcelFileDescriptor[] newArray(int size) {
            return new ParcelFileDescriptor[size];
        }
    };

    /* loaded from: ParcelFileDescriptor$OnCloseListener.class */
    public interface OnCloseListener {
        void onClose(IOException iOException);
    }

    public ParcelFileDescriptor(ParcelFileDescriptor wrapped) {
        this.mGuard = CloseGuard.get();
        this.mWrapped = wrapped;
        this.mFd = null;
        this.mCommFd = null;
        this.mClosed = true;
    }

    public ParcelFileDescriptor(FileDescriptor fd) {
        this(fd, null);
    }

    public ParcelFileDescriptor(FileDescriptor fd, FileDescriptor commChannel) {
        this.mGuard = CloseGuard.get();
        if (fd == null) {
            throw new NullPointerException("FileDescriptor must not be null");
        }
        this.mWrapped = null;
        this.mFd = fd;
        this.mCommFd = commChannel;
        this.mGuard.open("close");
    }

    public static ParcelFileDescriptor open(File file, int mode) throws FileNotFoundException {
        FileDescriptor fd = openInternal(file, mode);
        if (fd == null) {
            return null;
        }
        return new ParcelFileDescriptor(fd);
    }

    public static ParcelFileDescriptor open(File file, int mode, Handler handler, OnCloseListener listener) throws IOException {
        if (handler == null) {
            throw new IllegalArgumentException("Handler must not be null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener must not be null");
        }
        FileDescriptor fd = openInternal(file, mode);
        if (fd == null) {
            return null;
        }
        FileDescriptor[] comm = createCommSocketPair(true);
        ParcelFileDescriptor pfd = new ParcelFileDescriptor(fd, comm[0]);
        ListenerBridge bridge = new ListenerBridge(comm[1], handler.getLooper(), listener);
        bridge.start();
        return pfd;
    }

    private static FileDescriptor openInternal(File file, int mode) throws FileNotFoundException {
        if ((mode & MODE_READ_WRITE) == 0) {
            throw new IllegalArgumentException("Must specify MODE_READ_ONLY, MODE_WRITE_ONLY, or MODE_READ_WRITE");
        }
        String path = file.getPath();
        return Parcel.openFileDescriptor(path, mode);
    }

    public static ParcelFileDescriptor dup(FileDescriptor orig) throws IOException {
        try {
            FileDescriptor fd = Libcore.os.dup(orig);
            return new ParcelFileDescriptor(fd);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public ParcelFileDescriptor dup() throws IOException {
        if (this.mWrapped != null) {
            return this.mWrapped.dup();
        }
        return dup(getFileDescriptor());
    }

    public static ParcelFileDescriptor fromFd(int fd) throws IOException {
        FileDescriptor original = new FileDescriptor();
        original.setInt$(fd);
        try {
            FileDescriptor dup = Libcore.os.dup(original);
            return new ParcelFileDescriptor(dup);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor adoptFd(int fd) {
        FileDescriptor fdesc = new FileDescriptor();
        fdesc.setInt$(fd);
        return new ParcelFileDescriptor(fdesc);
    }

    public static ParcelFileDescriptor fromSocket(Socket socket) {
        FileDescriptor fd = socket.getFileDescriptor$();
        if (fd != null) {
            return new ParcelFileDescriptor(fd);
        }
        return null;
    }

    public static ParcelFileDescriptor fromDatagramSocket(DatagramSocket datagramSocket) {
        FileDescriptor fd = datagramSocket.getFileDescriptor$();
        if (fd != null) {
            return new ParcelFileDescriptor(fd);
        }
        return null;
    }

    public static ParcelFileDescriptor[] createPipe() throws IOException {
        try {
            FileDescriptor[] fds = Libcore.os.pipe();
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fds[0]), new ParcelFileDescriptor(fds[1])};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createReliablePipe() throws IOException {
        try {
            FileDescriptor[] comm = createCommSocketPair(false);
            FileDescriptor[] fds = Libcore.os.pipe();
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fds[0], comm[0]), new ParcelFileDescriptor(fds[1], comm[1])};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createSocketPair() throws IOException {
        try {
            FileDescriptor fd0 = new FileDescriptor();
            FileDescriptor fd1 = new FileDescriptor();
            Libcore.os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_STREAM, 0, fd0, fd1);
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fd0), new ParcelFileDescriptor(fd1)};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public static ParcelFileDescriptor[] createReliableSocketPair() throws IOException {
        try {
            FileDescriptor[] comm = createCommSocketPair(false);
            FileDescriptor fd0 = new FileDescriptor();
            FileDescriptor fd1 = new FileDescriptor();
            Libcore.os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_STREAM, 0, fd0, fd1);
            return new ParcelFileDescriptor[]{new ParcelFileDescriptor(fd0, comm[0]), new ParcelFileDescriptor(fd1, comm[1])};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    private static FileDescriptor[] createCommSocketPair(boolean blocking) throws IOException {
        try {
            FileDescriptor comm1 = new FileDescriptor();
            FileDescriptor comm2 = new FileDescriptor();
            Libcore.os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_STREAM, 0, comm1, comm2);
            IoUtils.setBlocking(comm1, blocking);
            IoUtils.setBlocking(comm2, blocking);
            return new FileDescriptor[]{comm1, comm2};
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    @Deprecated
    public static ParcelFileDescriptor fromData(byte[] data, String name) throws IOException {
        if (data == null) {
            return null;
        }
        MemoryFile file = new MemoryFile(name, data.length);
        if (data.length > 0) {
            file.writeBytes(data, 0, 0, data.length);
        }
        file.deactivate();
        FileDescriptor fd = file.getFileDescriptor();
        if (fd != null) {
            return new ParcelFileDescriptor(fd);
        }
        return null;
    }

    public static int parseMode(String mode) {
        int modeBits;
        if (FullBackup.ROOT_TREE_TOKEN.equals(mode)) {
            modeBits = 268435456;
        } else if ("w".equals(mode) || "wt".equals(mode)) {
            modeBits = 738197504;
        } else if ("wa".equals(mode)) {
            modeBits = 704643072;
        } else if ("rw".equals(mode)) {
            modeBits = 939524096;
        } else if ("rwt".equals(mode)) {
            modeBits = 1006632960;
        } else {
            throw new IllegalArgumentException("Bad mode '" + mode + Separators.QUOTE);
        }
        return modeBits;
    }

    public FileDescriptor getFileDescriptor() {
        if (this.mWrapped != null) {
            return this.mWrapped.getFileDescriptor();
        }
        return this.mFd;
    }

    public long getStatSize() {
        if (this.mWrapped != null) {
            return this.mWrapped.getStatSize();
        }
        try {
            StructStat st = Libcore.os.fstat(this.mFd);
            if (OsConstants.S_ISREG(st.st_mode) || OsConstants.S_ISLNK(st.st_mode)) {
                return st.st_size;
            }
            return -1L;
        } catch (ErrnoException e) {
            Log.w(TAG, "fstat() failed: " + e);
            return -1L;
        }
    }

    public long seekTo(long pos) throws IOException {
        if (this.mWrapped != null) {
            return this.mWrapped.seekTo(pos);
        }
        try {
            return Libcore.os.lseek(this.mFd, pos, OsConstants.SEEK_SET);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public int getFd() {
        if (this.mWrapped != null) {
            return this.mWrapped.getFd();
        }
        if (this.mClosed) {
            throw new IllegalStateException("Already closed");
        }
        return this.mFd.getInt$();
    }

    public int detachFd() {
        if (this.mWrapped != null) {
            return this.mWrapped.detachFd();
        }
        if (this.mClosed) {
            throw new IllegalStateException("Already closed");
        }
        int fd = getFd();
        Parcel.clearFileDescriptor(this.mFd);
        writeCommStatusAndClose(2, null);
        return fd;
    }

    @Override // java.io.Closeable
    public void close() throws IOException {
        if (this.mWrapped != null) {
            try {
                this.mWrapped.close();
                releaseResources();
                return;
            } catch (Throwable th) {
                releaseResources();
                throw th;
            }
        }
        closeWithStatus(0, null);
    }

    public void closeWithError(String msg) throws IOException {
        if (this.mWrapped != null) {
            try {
                this.mWrapped.closeWithError(msg);
                releaseResources();
            } catch (Throwable th) {
                releaseResources();
                throw th;
            }
        } else if (msg == null) {
            throw new IllegalArgumentException("Message must not be null");
        } else {
            closeWithStatus(1, msg);
        }
    }

    private void closeWithStatus(int status, String msg) {
        if (this.mClosed) {
            return;
        }
        this.mClosed = true;
        this.mGuard.close();
        writeCommStatusAndClose(status, msg);
        IoUtils.closeQuietly(this.mFd);
        releaseResources();
    }

    public void releaseResources() {
    }

    private byte[] getOrCreateStatusBuffer() {
        if (this.mStatusBuf == null) {
            this.mStatusBuf = new byte[1024];
        }
        return this.mStatusBuf;
    }

    private void writeCommStatusAndClose(int status, String msg) {
        if (this.mCommFd == null) {
            if (msg != null) {
                Log.w(TAG, "Unable to inform peer: " + msg);
                return;
            }
            return;
        }
        if (status == 2) {
            Log.w(TAG, "Peer expected signal when closed; unable to deliver after detach");
        }
        try {
            if (status != -1) {
                try {
                    byte[] buf = getOrCreateStatusBuffer();
                    Memory.pokeInt(buf, 0, status, ByteOrder.BIG_ENDIAN);
                    int writePtr = 0 + 4;
                    if (msg != null) {
                        byte[] rawMsg = msg.getBytes();
                        int len = Math.min(rawMsg.length, buf.length - writePtr);
                        System.arraycopy(rawMsg, 0, buf, writePtr, len);
                        writePtr += len;
                    }
                    Libcore.os.write(this.mCommFd, buf, 0, writePtr);
                } catch (ErrnoException e) {
                    Log.w(TAG, "Failed to report status: " + e);
                }
            }
            if (status != -1) {
                this.mStatus = readCommStatus(this.mCommFd, getOrCreateStatusBuffer());
            }
        } finally {
            IoUtils.closeQuietly(this.mCommFd);
            this.mCommFd = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Status readCommStatus(FileDescriptor comm, byte[] buf) {
        try {
            int n = Libcore.os.read(comm, buf, 0, buf.length);
            if (n == 0) {
                return new Status(-2);
            }
            int status = Memory.peekInt(buf, 0, ByteOrder.BIG_ENDIAN);
            if (status == 1) {
                String msg = new String(buf, 4, n - 4);
                return new Status(status, msg);
            }
            return new Status(status);
        } catch (ErrnoException e) {
            if (e.errno == OsConstants.EAGAIN) {
                return null;
            }
            Log.d(TAG, "Failed to read status; assuming dead: " + e);
            return new Status(-2);
        }
    }

    public boolean canDetectErrors() {
        if (this.mWrapped != null) {
            return this.mWrapped.canDetectErrors();
        }
        return this.mCommFd != null;
    }

    public void checkError() throws IOException {
        if (this.mWrapped != null) {
            this.mWrapped.checkError();
            return;
        }
        if (this.mStatus == null) {
            if (this.mCommFd == null) {
                Log.w(TAG, "Peer didn't provide a comm channel; unable to check for errors");
                return;
            }
            this.mStatus = readCommStatus(this.mCommFd, getOrCreateStatusBuffer());
        }
        if (this.mStatus == null || this.mStatus.status == 0) {
            return;
        }
        throw this.mStatus.asIOException();
    }

    /* loaded from: ParcelFileDescriptor$AutoCloseInputStream.class */
    public static class AutoCloseInputStream extends FileInputStream {
        private final ParcelFileDescriptor mPfd;

        public AutoCloseInputStream(ParcelFileDescriptor pfd) {
            super(pfd.getFileDescriptor());
            this.mPfd = pfd;
        }

        @Override // java.io.FileInputStream, java.io.InputStream, java.io.Closeable
        public void close() throws IOException {
            try {
                this.mPfd.close();
                super.close();
            } catch (Throwable th) {
                super.close();
                throw th;
            }
        }
    }

    /* loaded from: ParcelFileDescriptor$AutoCloseOutputStream.class */
    public static class AutoCloseOutputStream extends FileOutputStream {
        private final ParcelFileDescriptor mPfd;

        public AutoCloseOutputStream(ParcelFileDescriptor pfd) {
            super(pfd.getFileDescriptor());
            this.mPfd = pfd;
        }

        @Override // java.io.FileOutputStream, java.io.OutputStream, java.io.Closeable
        public void close() throws IOException {
            try {
                this.mPfd.close();
                super.close();
            } catch (Throwable th) {
                super.close();
                throw th;
            }
        }
    }

    public String toString() {
        if (this.mWrapped != null) {
            return this.mWrapped.toString();
        }
        return "{ParcelFileDescriptor: " + this.mFd + "}";
    }

    protected void finalize() throws Throwable {
        if (this.mWrapped != null) {
            releaseResources();
        }
        if (this.mGuard != null) {
            this.mGuard.warnIfOpen();
        }
        try {
            if (!this.mClosed) {
                closeWithStatus(3, null);
            }
        } finally {
            super.finalize();
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        if (this.mWrapped != null) {
            return this.mWrapped.describeContents();
        }
        return 1;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (this.mWrapped != null) {
            try {
                this.mWrapped.writeToParcel(out, flags);
                releaseResources();
                return;
            } catch (Throwable th) {
                releaseResources();
                throw th;
            }
        }
        out.writeFileDescriptor(this.mFd);
        if (this.mCommFd != null) {
            out.writeInt(1);
            out.writeFileDescriptor(this.mCommFd);
        } else {
            out.writeInt(0);
        }
        if ((flags & 1) != 0 && !this.mClosed) {
            closeWithStatus(-1, null);
        }
    }

    /* loaded from: ParcelFileDescriptor$FileDescriptorDetachedException.class */
    public static class FileDescriptorDetachedException extends IOException {
        private static final long serialVersionUID = 955542466045L;

        public FileDescriptorDetachedException() {
            super("Remote side is detached");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ParcelFileDescriptor$Status.class */
    public static class Status {
        public static final int DEAD = -2;
        public static final int SILENCE = -1;
        public static final int OK = 0;
        public static final int ERROR = 1;
        public static final int DETACHED = 2;
        public static final int LEAKED = 3;
        public final int status;
        public final String msg;

        public Status(int status) {
            this(status, null);
        }

        public Status(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public IOException asIOException() {
            switch (this.status) {
                case -2:
                    return new IOException("Remote side is dead");
                case -1:
                default:
                    return new IOException("Unknown status: " + this.status);
                case 0:
                    return null;
                case 1:
                    return new IOException("Remote error: " + this.msg);
                case 2:
                    return new FileDescriptorDetachedException();
                case 3:
                    return new IOException("Remote side was leaked");
            }
        }
    }

    /* loaded from: ParcelFileDescriptor$ListenerBridge.class */
    private static final class ListenerBridge extends Thread {
        private FileDescriptor mCommFd;
        private final Handler mHandler;

        public ListenerBridge(FileDescriptor comm, Looper looper, final OnCloseListener listener) {
            this.mCommFd = comm;
            this.mHandler = new Handler(looper) { // from class: android.os.ParcelFileDescriptor.ListenerBridge.1
                @Override // android.os.Handler
                public void handleMessage(Message msg) {
                    Status s = (Status) msg.obj;
                    listener.onClose(s != null ? s.asIOException() : null);
                }
            };
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                byte[] buf = new byte[1024];
                Status status = ParcelFileDescriptor.readCommStatus(this.mCommFd, buf);
                this.mHandler.obtainMessage(0, status).sendToTarget();
                IoUtils.closeQuietly(this.mCommFd);
                this.mCommFd = null;
            } catch (Throwable th) {
                IoUtils.closeQuietly(this.mCommFd);
                this.mCommFd = null;
                throw th;
            }
        }
    }
}