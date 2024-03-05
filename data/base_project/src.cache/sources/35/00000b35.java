package android.os;

import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: MemoryFile.class */
public class MemoryFile {
    private static String TAG = "MemoryFile";
    private static final int PROT_READ = 1;
    private static final int PROT_WRITE = 2;
    private FileDescriptor mFD;
    private int mAddress;
    private int mLength;
    private boolean mAllowPurging = false;

    private static native FileDescriptor native_open(String str, int i) throws IOException;

    private static native int native_mmap(FileDescriptor fileDescriptor, int i, int i2) throws IOException;

    private static native void native_munmap(int i, int i2) throws IOException;

    private static native void native_close(FileDescriptor fileDescriptor);

    private static native int native_read(FileDescriptor fileDescriptor, int i, byte[] bArr, int i2, int i3, int i4, boolean z) throws IOException;

    private static native void native_write(FileDescriptor fileDescriptor, int i, byte[] bArr, int i2, int i3, int i4, boolean z) throws IOException;

    private static native void native_pin(FileDescriptor fileDescriptor, boolean z) throws IOException;

    private static native int native_get_size(FileDescriptor fileDescriptor) throws IOException;

    public MemoryFile(String name, int length) throws IOException {
        this.mLength = length;
        this.mFD = native_open(name, length);
        if (length > 0) {
            this.mAddress = native_mmap(this.mFD, length, 3);
        } else {
            this.mAddress = 0;
        }
    }

    public void close() {
        deactivate();
        if (!isClosed()) {
            native_close(this.mFD);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void deactivate() {
        if (!isDeactivated()) {
            try {
                native_munmap(this.mAddress, this.mLength);
                this.mAddress = 0;
            } catch (IOException ex) {
                Log.e(TAG, ex.toString());
            }
        }
    }

    private boolean isDeactivated() {
        return this.mAddress == 0;
    }

    private boolean isClosed() {
        return !this.mFD.valid();
    }

    protected void finalize() {
        if (!isClosed()) {
            Log.e(TAG, "MemoryFile.finalize() called while ashmem still open");
            close();
        }
    }

    public int length() {
        return this.mLength;
    }

    public boolean isPurgingAllowed() {
        return this.mAllowPurging;
    }

    public synchronized boolean allowPurging(boolean allowPurging) throws IOException {
        boolean oldValue = this.mAllowPurging;
        if (oldValue != allowPurging) {
            native_pin(this.mFD, !allowPurging);
            this.mAllowPurging = allowPurging;
        }
        return oldValue;
    }

    public InputStream getInputStream() {
        return new MemoryInputStream();
    }

    public OutputStream getOutputStream() {
        return new MemoryOutputStream();
    }

    public int readBytes(byte[] buffer, int srcOffset, int destOffset, int count) throws IOException {
        if (isDeactivated()) {
            throw new IOException("Can't read from deactivated memory file.");
        }
        if (destOffset < 0 || destOffset > buffer.length || count < 0 || count > buffer.length - destOffset || srcOffset < 0 || srcOffset > this.mLength || count > this.mLength - srcOffset) {
            throw new IndexOutOfBoundsException();
        }
        return native_read(this.mFD, this.mAddress, buffer, srcOffset, destOffset, count, this.mAllowPurging);
    }

    public void writeBytes(byte[] buffer, int srcOffset, int destOffset, int count) throws IOException {
        if (isDeactivated()) {
            throw new IOException("Can't write to deactivated memory file.");
        }
        if (srcOffset < 0 || srcOffset > buffer.length || count < 0 || count > buffer.length - srcOffset || destOffset < 0 || destOffset > this.mLength || count > this.mLength - destOffset) {
            throw new IndexOutOfBoundsException();
        }
        native_write(this.mFD, this.mAddress, buffer, srcOffset, destOffset, count, this.mAllowPurging);
    }

    public FileDescriptor getFileDescriptor() throws IOException {
        return this.mFD;
    }

    public static int getSize(FileDescriptor fd) throws IOException {
        return native_get_size(fd);
    }

    /* loaded from: MemoryFile$MemoryInputStream.class */
    private class MemoryInputStream extends InputStream {
        private int mMark;
        private int mOffset;
        private byte[] mSingleByte;

        private MemoryInputStream() {
            this.mMark = 0;
            this.mOffset = 0;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            if (this.mOffset < MemoryFile.this.mLength) {
                return MemoryFile.this.mLength - this.mOffset;
            }
            return 0;
        }

        @Override // java.io.InputStream
        public boolean markSupported() {
            return true;
        }

        @Override // java.io.InputStream
        public void mark(int readlimit) {
            this.mMark = this.mOffset;
        }

        @Override // java.io.InputStream
        public void reset() throws IOException {
            this.mOffset = this.mMark;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (this.mSingleByte == null) {
                this.mSingleByte = new byte[1];
            }
            int result = read(this.mSingleByte, 0, 1);
            if (result != 1) {
                return -1;
            }
            return this.mSingleByte[0];
        }

        @Override // java.io.InputStream
        public int read(byte[] buffer, int offset, int count) throws IOException {
            if (offset < 0 || count < 0 || offset + count > buffer.length) {
                throw new IndexOutOfBoundsException();
            }
            int count2 = Math.min(count, available());
            if (count2 < 1) {
                return -1;
            }
            int result = MemoryFile.this.readBytes(buffer, this.mOffset, offset, count2);
            if (result > 0) {
                this.mOffset += result;
            }
            return result;
        }

        @Override // java.io.InputStream
        public long skip(long n) throws IOException {
            if (this.mOffset + n > MemoryFile.this.mLength) {
                n = MemoryFile.this.mLength - this.mOffset;
            }
            this.mOffset = (int) (this.mOffset + n);
            return n;
        }
    }

    /* loaded from: MemoryFile$MemoryOutputStream.class */
    private class MemoryOutputStream extends OutputStream {
        private int mOffset;
        private byte[] mSingleByte;

        private MemoryOutputStream() {
            this.mOffset = 0;
        }

        @Override // java.io.OutputStream
        public void write(byte[] buffer, int offset, int count) throws IOException {
            MemoryFile.this.writeBytes(buffer, offset, this.mOffset, count);
            this.mOffset += count;
        }

        @Override // java.io.OutputStream
        public void write(int oneByte) throws IOException {
            if (this.mSingleByte == null) {
                this.mSingleByte = new byte[1];
            }
            this.mSingleByte[0] = (byte) oneByte;
            write(this.mSingleByte, 0, 1);
        }
    }
}