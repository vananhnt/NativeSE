package libcore.io;

import dalvik.bytecode.Opcodes;
import java.io.FileDescriptor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.NioUtils;
import libcore.util.MutableInt;
import libcore.util.MutableLong;

/* loaded from: Posix.class */
public final class Posix implements Os {
    @Override // libcore.io.Os
    public native FileDescriptor accept(FileDescriptor fileDescriptor, InetSocketAddress inetSocketAddress) throws ErrnoException, SocketException;

    @Override // libcore.io.Os
    public native boolean access(String str, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native void bind(FileDescriptor fileDescriptor, InetAddress inetAddress, int i) throws ErrnoException, SocketException;

    @Override // libcore.io.Os
    public native void chmod(String str, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native void chown(String str, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native void close(FileDescriptor fileDescriptor) throws ErrnoException;

    @Override // libcore.io.Os
    public native void connect(FileDescriptor fileDescriptor, InetAddress inetAddress, int i) throws ErrnoException, SocketException;

    @Override // libcore.io.Os
    public native FileDescriptor dup(FileDescriptor fileDescriptor) throws ErrnoException;

    @Override // libcore.io.Os
    public native FileDescriptor dup2(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native String[] environ();

    @Override // libcore.io.Os
    public native void execv(String str, String[] strArr) throws ErrnoException;

    @Override // libcore.io.Os
    public native void execve(String str, String[] strArr, String[] strArr2) throws ErrnoException;

    @Override // libcore.io.Os
    public native void fchmod(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native void fchown(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native int fcntlVoid(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native int fcntlLong(FileDescriptor fileDescriptor, int i, long j) throws ErrnoException;

    @Override // libcore.io.Os
    public native int fcntlFlock(FileDescriptor fileDescriptor, int i, StructFlock structFlock) throws ErrnoException;

    @Override // libcore.io.Os
    public native void fdatasync(FileDescriptor fileDescriptor) throws ErrnoException;

    @Override // libcore.io.Os
    public native StructStat fstat(FileDescriptor fileDescriptor) throws ErrnoException;

    @Override // libcore.io.Os
    public native StructStatVfs fstatvfs(FileDescriptor fileDescriptor) throws ErrnoException;

    @Override // libcore.io.Os
    public native void fsync(FileDescriptor fileDescriptor) throws ErrnoException;

    @Override // libcore.io.Os
    public native void ftruncate(FileDescriptor fileDescriptor, long j) throws ErrnoException;

    @Override // libcore.io.Os
    public native String gai_strerror(int i);

    @Override // libcore.io.Os
    public native InetAddress[] getaddrinfo(String str, StructAddrinfo structAddrinfo) throws GaiException;

    @Override // libcore.io.Os
    public native int getegid();

    @Override // libcore.io.Os
    public native int geteuid();

    @Override // libcore.io.Os
    public native int getgid();

    @Override // libcore.io.Os
    public native String getenv(String str);

    @Override // libcore.io.Os
    public native String getnameinfo(InetAddress inetAddress, int i) throws GaiException;

    @Override // libcore.io.Os
    public native SocketAddress getpeername(FileDescriptor fileDescriptor) throws ErrnoException;

    @Override // libcore.io.Os
    public native int getpid();

    @Override // libcore.io.Os
    public native int getppid();

    @Override // libcore.io.Os
    public native StructPasswd getpwnam(String str) throws ErrnoException;

    @Override // libcore.io.Os
    public native StructPasswd getpwuid(int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native SocketAddress getsockname(FileDescriptor fileDescriptor) throws ErrnoException;

    @Override // libcore.io.Os
    public native int getsockoptByte(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native InetAddress getsockoptInAddr(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native int getsockoptInt(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native StructLinger getsockoptLinger(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native StructTimeval getsockoptTimeval(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native StructUcred getsockoptUcred(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native int gettid();

    @Override // libcore.io.Os
    public native int getuid();

    @Override // libcore.io.Os
    public native String if_indextoname(int i);

    @Override // libcore.io.Os
    public native InetAddress inet_pton(int i, String str);

    @Override // libcore.io.Os
    public native InetAddress ioctlInetAddress(FileDescriptor fileDescriptor, int i, String str) throws ErrnoException;

    @Override // libcore.io.Os
    public native int ioctlInt(FileDescriptor fileDescriptor, int i, MutableInt mutableInt) throws ErrnoException;

    @Override // libcore.io.Os
    public native boolean isatty(FileDescriptor fileDescriptor);

    @Override // libcore.io.Os
    public native void kill(int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native void lchown(String str, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native void listen(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native long lseek(FileDescriptor fileDescriptor, long j, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native StructStat lstat(String str) throws ErrnoException;

    @Override // libcore.io.Os
    public native void mincore(long j, long j2, byte[] bArr) throws ErrnoException;

    @Override // libcore.io.Os
    public native void mkdir(String str, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native void mlock(long j, long j2) throws ErrnoException;

    @Override // libcore.io.Os
    public native long mmap(long j, long j2, int i, int i2, FileDescriptor fileDescriptor, long j3) throws ErrnoException;

    @Override // libcore.io.Os
    public native void msync(long j, long j2, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native void munlock(long j, long j2) throws ErrnoException;

    @Override // libcore.io.Os
    public native void munmap(long j, long j2) throws ErrnoException;

    @Override // libcore.io.Os
    public native FileDescriptor open(String str, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native FileDescriptor[] pipe() throws ErrnoException;

    @Override // libcore.io.Os
    public native int poll(StructPollfd[] structPollfdArr, int i) throws ErrnoException;

    private native int preadBytes(FileDescriptor fileDescriptor, Object obj, int i, int i2, long j) throws ErrnoException;

    private native int pwriteBytes(FileDescriptor fileDescriptor, Object obj, int i, int i2, long j) throws ErrnoException;

    private native int readBytes(FileDescriptor fileDescriptor, Object obj, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native int readv(FileDescriptor fileDescriptor, Object[] objArr, int[] iArr, int[] iArr2) throws ErrnoException;

    private native int recvfromBytes(FileDescriptor fileDescriptor, Object obj, int i, int i2, int i3, InetSocketAddress inetSocketAddress) throws ErrnoException, SocketException;

    @Override // libcore.io.Os
    public native void remove(String str) throws ErrnoException;

    @Override // libcore.io.Os
    public native void rename(String str, String str2) throws ErrnoException;

    @Override // libcore.io.Os
    public native long sendfile(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, MutableLong mutableLong, long j) throws ErrnoException;

    private native int sendtoBytes(FileDescriptor fileDescriptor, Object obj, int i, int i2, int i3, InetAddress inetAddress, int i4) throws ErrnoException, SocketException;

    @Override // libcore.io.Os
    public native void setegid(int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native void setenv(String str, String str2, boolean z) throws ErrnoException;

    @Override // libcore.io.Os
    public native void seteuid(int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native void setgid(int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native int setsid() throws ErrnoException;

    @Override // libcore.io.Os
    public native void setsockoptByte(FileDescriptor fileDescriptor, int i, int i2, int i3) throws ErrnoException;

    @Override // libcore.io.Os
    public native void setsockoptIfreq(FileDescriptor fileDescriptor, int i, int i2, String str) throws ErrnoException;

    @Override // libcore.io.Os
    public native void setsockoptInt(FileDescriptor fileDescriptor, int i, int i2, int i3) throws ErrnoException;

    @Override // libcore.io.Os
    public native void setsockoptIpMreqn(FileDescriptor fileDescriptor, int i, int i2, int i3) throws ErrnoException;

    @Override // libcore.io.Os
    public native void setsockoptGroupReq(FileDescriptor fileDescriptor, int i, int i2, StructGroupReq structGroupReq) throws ErrnoException;

    @Override // libcore.io.Os
    public native void setsockoptLinger(FileDescriptor fileDescriptor, int i, int i2, StructLinger structLinger) throws ErrnoException;

    @Override // libcore.io.Os
    public native void setsockoptTimeval(FileDescriptor fileDescriptor, int i, int i2, StructTimeval structTimeval) throws ErrnoException;

    @Override // libcore.io.Os
    public native void setuid(int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native void shutdown(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    @Override // libcore.io.Os
    public native FileDescriptor socket(int i, int i2, int i3) throws ErrnoException;

    @Override // libcore.io.Os
    public native void socketpair(int i, int i2, int i3, FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2) throws ErrnoException;

    @Override // libcore.io.Os
    public native StructStat stat(String str) throws ErrnoException;

    @Override // libcore.io.Os
    public native StructStatVfs statvfs(String str) throws ErrnoException;

    @Override // libcore.io.Os
    public native String strerror(int i);

    @Override // libcore.io.Os
    public native String strsignal(int i);

    @Override // libcore.io.Os
    public native void symlink(String str, String str2) throws ErrnoException;

    @Override // libcore.io.Os
    public native long sysconf(int i);

    @Override // libcore.io.Os
    public native void tcdrain(FileDescriptor fileDescriptor) throws ErrnoException;

    @Override // libcore.io.Os
    public native void tcsendbreak(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    private native int umaskImpl(int i);

    @Override // libcore.io.Os
    public native StructUtsname uname();

    @Override // libcore.io.Os
    public native void unsetenv(String str) throws ErrnoException;

    @Override // libcore.io.Os
    public native int waitpid(int i, MutableInt mutableInt, int i2) throws ErrnoException;

    private native int writeBytes(FileDescriptor fileDescriptor, Object obj, int i, int i2) throws ErrnoException;

    @Override // libcore.io.Os
    public native int writev(FileDescriptor fileDescriptor, Object[] objArr, int[] iArr, int[] iArr2) throws ErrnoException;

    @Override // libcore.io.Os
    public int pread(FileDescriptor fd, ByteBuffer buffer, long offset) throws ErrnoException {
        if (buffer.isDirect()) {
            return preadBytes(fd, buffer, buffer.position(), buffer.remaining(), offset);
        }
        return preadBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining(), offset);
    }

    @Override // libcore.io.Os
    public int pread(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, long offset) throws ErrnoException {
        return preadBytes(fd, bytes, byteOffset, byteCount, offset);
    }

    @Override // libcore.io.Os
    public int pwrite(FileDescriptor fd, ByteBuffer buffer, long offset) throws ErrnoException {
        if (buffer.isDirect()) {
            return pwriteBytes(fd, buffer, buffer.position(), buffer.remaining(), offset);
        }
        return pwriteBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining(), offset);
    }

    @Override // libcore.io.Os
    public int pwrite(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, long offset) throws ErrnoException {
        return pwriteBytes(fd, bytes, byteOffset, byteCount, offset);
    }

    @Override // libcore.io.Os
    public int read(FileDescriptor fd, ByteBuffer buffer) throws ErrnoException {
        if (buffer.isDirect()) {
            return readBytes(fd, buffer, buffer.position(), buffer.remaining());
        }
        return readBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining());
    }

    @Override // libcore.io.Os
    public int read(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount) throws ErrnoException {
        return readBytes(fd, bytes, byteOffset, byteCount);
    }

    @Override // libcore.io.Os
    public int recvfrom(FileDescriptor fd, ByteBuffer buffer, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException {
        if (buffer.isDirect()) {
            return recvfromBytes(fd, buffer, buffer.position(), buffer.remaining(), flags, srcAddress);
        }
        return recvfromBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining(), flags, srcAddress);
    }

    @Override // libcore.io.Os
    public int recvfrom(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException {
        return recvfromBytes(fd, bytes, byteOffset, byteCount, flags, srcAddress);
    }

    @Override // libcore.io.Os
    public int sendto(FileDescriptor fd, ByteBuffer buffer, int flags, InetAddress inetAddress, int port) throws ErrnoException, SocketException {
        if (buffer.isDirect()) {
            return sendtoBytes(fd, buffer, buffer.position(), buffer.remaining(), flags, inetAddress, port);
        }
        return sendtoBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining(), flags, inetAddress, port);
    }

    @Override // libcore.io.Os
    public int sendto(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, int flags, InetAddress inetAddress, int port) throws ErrnoException, SocketException {
        return sendtoBytes(fd, bytes, byteOffset, byteCount, flags, inetAddress, port);
    }

    @Override // libcore.io.Os
    public int umask(int mask) {
        if ((mask & Opcodes.OP_CHECK_CAST_JUMBO) != mask) {
            throw new IllegalArgumentException("Invalid umask: " + mask);
        }
        return umaskImpl(mask);
    }

    @Override // libcore.io.Os
    public int write(FileDescriptor fd, ByteBuffer buffer) throws ErrnoException {
        if (buffer.isDirect()) {
            return writeBytes(fd, buffer, buffer.position(), buffer.remaining());
        }
        return writeBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining());
    }

    @Override // libcore.io.Os
    public int write(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount) throws ErrnoException {
        return writeBytes(fd, bytes, byteOffset, byteCount);
    }
}