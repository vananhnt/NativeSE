package libcore.io;

import dalvik.system.BlockGuard;
import dalvik.system.SocketTagger;
import java.io.FileDescriptor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

/* loaded from: BlockGuardOs.class */
public class BlockGuardOs extends ForwardingOs {
    public BlockGuardOs(Os os) {
        super(os);
    }

    private FileDescriptor tagSocket(FileDescriptor fd) throws ErrnoException {
        try {
            SocketTagger.get().tag(fd);
            return fd;
        } catch (SocketException e) {
            throw new ErrnoException("socket", OsConstants.EINVAL, e);
        }
    }

    private void untagSocket(FileDescriptor fd) throws ErrnoException {
        try {
            SocketTagger.get().untag(fd);
        } catch (SocketException e) {
            throw new ErrnoException("socket", OsConstants.EINVAL, e);
        }
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public FileDescriptor accept(FileDescriptor fd, InetSocketAddress peerAddress) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return tagSocket(this.os.accept(fd, peerAddress));
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public void close(FileDescriptor fd) throws ErrnoException {
        try {
            if (OsConstants.S_ISSOCK(Libcore.os.fstat(fd).st_mode)) {
                if (isLingerSocket(fd)) {
                    BlockGuard.getThreadPolicy().onNetwork();
                }
                untagSocket(fd);
            }
        } catch (ErrnoException e) {
        }
        this.os.close(fd);
    }

    private static boolean isLingerSocket(FileDescriptor fd) throws ErrnoException {
        StructLinger linger = Libcore.os.getsockoptLinger(fd, OsConstants.SOL_SOCKET, OsConstants.SO_LINGER);
        return linger.isOn() && linger.l_linger > 0;
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public void connect(FileDescriptor fd, InetAddress address, int port) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        this.os.connect(fd, address, port);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public void fdatasync(FileDescriptor fd) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        this.os.fdatasync(fd);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public void fsync(FileDescriptor fd) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        this.os.fsync(fd);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public void ftruncate(FileDescriptor fd, long length) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        this.os.ftruncate(fd, length);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public FileDescriptor open(String path, int flags, int mode) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        if ((mode & OsConstants.O_ACCMODE) != OsConstants.O_RDONLY) {
            BlockGuard.getThreadPolicy().onWriteToDisk();
        }
        return this.os.open(path, flags, mode);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int poll(StructPollfd[] fds, int timeoutMs) throws ErrnoException {
        if (timeoutMs != 0) {
            BlockGuard.getThreadPolicy().onNetwork();
        }
        return this.os.poll(fds, timeoutMs);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int pread(FileDescriptor fd, ByteBuffer buffer, long offset) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return this.os.pread(fd, buffer, offset);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int pread(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, long offset) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return this.os.pread(fd, bytes, byteOffset, byteCount, offset);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int pwrite(FileDescriptor fd, ByteBuffer buffer, long offset) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return this.os.pwrite(fd, buffer, offset);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int pwrite(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, long offset) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return this.os.pwrite(fd, bytes, byteOffset, byteCount, offset);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int read(FileDescriptor fd, ByteBuffer buffer) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return this.os.read(fd, buffer);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int read(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return this.os.read(fd, bytes, byteOffset, byteCount);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int readv(FileDescriptor fd, Object[] buffers, int[] offsets, int[] byteCounts) throws ErrnoException {
        BlockGuard.getThreadPolicy().onReadFromDisk();
        return this.os.readv(fd, buffers, offsets, byteCounts);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int recvfrom(FileDescriptor fd, ByteBuffer buffer, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return this.os.recvfrom(fd, buffer, flags, srcAddress);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int recvfrom(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return this.os.recvfrom(fd, bytes, byteOffset, byteCount, flags, srcAddress);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int sendto(FileDescriptor fd, ByteBuffer buffer, int flags, InetAddress inetAddress, int port) throws ErrnoException, SocketException {
        BlockGuard.getThreadPolicy().onNetwork();
        return this.os.sendto(fd, buffer, flags, inetAddress, port);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int sendto(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, int flags, InetAddress inetAddress, int port) throws ErrnoException, SocketException {
        if (inetAddress != null) {
            BlockGuard.getThreadPolicy().onNetwork();
        }
        return this.os.sendto(fd, bytes, byteOffset, byteCount, flags, inetAddress, port);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public FileDescriptor socket(int domain, int type, int protocol) throws ErrnoException {
        return tagSocket(this.os.socket(domain, type, protocol));
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public void socketpair(int domain, int type, int protocol, FileDescriptor fd1, FileDescriptor fd2) throws ErrnoException {
        this.os.socketpair(domain, type, protocol, fd1, fd2);
        tagSocket(fd1);
        tagSocket(fd2);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int write(FileDescriptor fd, ByteBuffer buffer) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return this.os.write(fd, buffer);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int write(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return this.os.write(fd, bytes, byteOffset, byteCount);
    }

    @Override // libcore.io.ForwardingOs, libcore.io.Os
    public int writev(FileDescriptor fd, Object[] buffers, int[] offsets, int[] byteCounts) throws ErrnoException {
        BlockGuard.getThreadPolicy().onWriteToDisk();
        return this.os.writev(fd, buffers, offsets, byteCounts);
    }
}