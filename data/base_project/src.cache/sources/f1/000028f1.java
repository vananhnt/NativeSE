package libcore.io;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import libcore.util.MutableInt;
import libcore.util.MutableLong;

/* loaded from: Os.class */
public interface Os {
    FileDescriptor accept(FileDescriptor fileDescriptor, InetSocketAddress inetSocketAddress) throws ErrnoException, SocketException;

    boolean access(String str, int i) throws ErrnoException;

    void bind(FileDescriptor fileDescriptor, InetAddress inetAddress, int i) throws ErrnoException, SocketException;

    void chmod(String str, int i) throws ErrnoException;

    void chown(String str, int i, int i2) throws ErrnoException;

    void close(FileDescriptor fileDescriptor) throws ErrnoException;

    void connect(FileDescriptor fileDescriptor, InetAddress inetAddress, int i) throws ErrnoException, SocketException;

    FileDescriptor dup(FileDescriptor fileDescriptor) throws ErrnoException;

    FileDescriptor dup2(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    String[] environ();

    void execv(String str, String[] strArr) throws ErrnoException;

    void execve(String str, String[] strArr, String[] strArr2) throws ErrnoException;

    void fchmod(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    void fchown(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    int fcntlVoid(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    int fcntlLong(FileDescriptor fileDescriptor, int i, long j) throws ErrnoException;

    int fcntlFlock(FileDescriptor fileDescriptor, int i, StructFlock structFlock) throws ErrnoException;

    void fdatasync(FileDescriptor fileDescriptor) throws ErrnoException;

    StructStat fstat(FileDescriptor fileDescriptor) throws ErrnoException;

    StructStatVfs fstatvfs(FileDescriptor fileDescriptor) throws ErrnoException;

    void fsync(FileDescriptor fileDescriptor) throws ErrnoException;

    void ftruncate(FileDescriptor fileDescriptor, long j) throws ErrnoException;

    String gai_strerror(int i);

    InetAddress[] getaddrinfo(String str, StructAddrinfo structAddrinfo) throws GaiException;

    int getegid();

    int geteuid();

    int getgid();

    String getenv(String str);

    String getnameinfo(InetAddress inetAddress, int i) throws GaiException;

    SocketAddress getpeername(FileDescriptor fileDescriptor) throws ErrnoException;

    int getpid();

    int getppid();

    StructPasswd getpwnam(String str) throws ErrnoException;

    StructPasswd getpwuid(int i) throws ErrnoException;

    SocketAddress getsockname(FileDescriptor fileDescriptor) throws ErrnoException;

    int getsockoptByte(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    InetAddress getsockoptInAddr(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    int getsockoptInt(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    StructLinger getsockoptLinger(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    StructTimeval getsockoptTimeval(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    StructUcred getsockoptUcred(FileDescriptor fileDescriptor, int i, int i2) throws ErrnoException;

    int gettid();

    int getuid();

    String if_indextoname(int i);

    InetAddress inet_pton(int i, String str);

    InetAddress ioctlInetAddress(FileDescriptor fileDescriptor, int i, String str) throws ErrnoException;

    int ioctlInt(FileDescriptor fileDescriptor, int i, MutableInt mutableInt) throws ErrnoException;

    boolean isatty(FileDescriptor fileDescriptor);

    void kill(int i, int i2) throws ErrnoException;

    void lchown(String str, int i, int i2) throws ErrnoException;

    void listen(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    long lseek(FileDescriptor fileDescriptor, long j, int i) throws ErrnoException;

    StructStat lstat(String str) throws ErrnoException;

    void mincore(long j, long j2, byte[] bArr) throws ErrnoException;

    void mkdir(String str, int i) throws ErrnoException;

    void mlock(long j, long j2) throws ErrnoException;

    long mmap(long j, long j2, int i, int i2, FileDescriptor fileDescriptor, long j3) throws ErrnoException;

    void msync(long j, long j2, int i) throws ErrnoException;

    void munlock(long j, long j2) throws ErrnoException;

    void munmap(long j, long j2) throws ErrnoException;

    FileDescriptor open(String str, int i, int i2) throws ErrnoException;

    FileDescriptor[] pipe() throws ErrnoException;

    int poll(StructPollfd[] structPollfdArr, int i) throws ErrnoException;

    int pread(FileDescriptor fileDescriptor, ByteBuffer byteBuffer, long j) throws ErrnoException;

    int pread(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2, long j) throws ErrnoException;

    int pwrite(FileDescriptor fileDescriptor, ByteBuffer byteBuffer, long j) throws ErrnoException;

    int pwrite(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2, long j) throws ErrnoException;

    int read(FileDescriptor fileDescriptor, ByteBuffer byteBuffer) throws ErrnoException;

    int read(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2) throws ErrnoException;

    int readv(FileDescriptor fileDescriptor, Object[] objArr, int[] iArr, int[] iArr2) throws ErrnoException;

    int recvfrom(FileDescriptor fileDescriptor, ByteBuffer byteBuffer, int i, InetSocketAddress inetSocketAddress) throws ErrnoException, SocketException;

    int recvfrom(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2, int i3, InetSocketAddress inetSocketAddress) throws ErrnoException, SocketException;

    void remove(String str) throws ErrnoException;

    void rename(String str, String str2) throws ErrnoException;

    int sendto(FileDescriptor fileDescriptor, ByteBuffer byteBuffer, int i, InetAddress inetAddress, int i2) throws ErrnoException, SocketException;

    int sendto(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2, int i3, InetAddress inetAddress, int i4) throws ErrnoException, SocketException;

    long sendfile(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, MutableLong mutableLong, long j) throws ErrnoException;

    void setegid(int i) throws ErrnoException;

    void setenv(String str, String str2, boolean z) throws ErrnoException;

    void seteuid(int i) throws ErrnoException;

    void setgid(int i) throws ErrnoException;

    int setsid() throws ErrnoException;

    void setsockoptByte(FileDescriptor fileDescriptor, int i, int i2, int i3) throws ErrnoException;

    void setsockoptIfreq(FileDescriptor fileDescriptor, int i, int i2, String str) throws ErrnoException;

    void setsockoptInt(FileDescriptor fileDescriptor, int i, int i2, int i3) throws ErrnoException;

    void setsockoptIpMreqn(FileDescriptor fileDescriptor, int i, int i2, int i3) throws ErrnoException;

    void setsockoptGroupReq(FileDescriptor fileDescriptor, int i, int i2, StructGroupReq structGroupReq) throws ErrnoException;

    void setsockoptLinger(FileDescriptor fileDescriptor, int i, int i2, StructLinger structLinger) throws ErrnoException;

    void setsockoptTimeval(FileDescriptor fileDescriptor, int i, int i2, StructTimeval structTimeval) throws ErrnoException;

    void setuid(int i) throws ErrnoException;

    void shutdown(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    FileDescriptor socket(int i, int i2, int i3) throws ErrnoException;

    void socketpair(int i, int i2, int i3, FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2) throws ErrnoException;

    StructStat stat(String str) throws ErrnoException;

    StructStatVfs statvfs(String str) throws ErrnoException;

    String strerror(int i);

    String strsignal(int i);

    void symlink(String str, String str2) throws ErrnoException;

    long sysconf(int i);

    void tcdrain(FileDescriptor fileDescriptor) throws ErrnoException;

    void tcsendbreak(FileDescriptor fileDescriptor, int i) throws ErrnoException;

    int umask(int i);

    StructUtsname uname();

    void unsetenv(String str) throws ErrnoException;

    int waitpid(int i, MutableInt mutableInt, int i2) throws ErrnoException;

    int write(FileDescriptor fileDescriptor, ByteBuffer byteBuffer) throws ErrnoException;

    int write(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2) throws ErrnoException;

    int writev(FileDescriptor fileDescriptor, Object[] objArr, int[] iArr, int[] iArr2) throws ErrnoException;
}