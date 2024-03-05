package android.app.backup;

import java.io.FileDescriptor;
import java.io.IOException;

/* loaded from: BackupDataOutput.class */
public class BackupDataOutput {
    int mBackupWriter;

    private static native int ctor(FileDescriptor fileDescriptor);

    private static native void dtor(int i);

    private static native int writeEntityHeader_native(int i, String str, int i2);

    private static native int writeEntityData_native(int i, byte[] bArr, int i2);

    private static native void setKeyPrefix_native(int i, String str);

    public BackupDataOutput(FileDescriptor fd) {
        if (fd == null) {
            throw new NullPointerException();
        }
        this.mBackupWriter = ctor(fd);
        if (this.mBackupWriter == 0) {
            throw new RuntimeException("Native initialization failed with fd=" + fd);
        }
    }

    public int writeEntityHeader(String key, int dataSize) throws IOException {
        int result = writeEntityHeader_native(this.mBackupWriter, key, dataSize);
        if (result >= 0) {
            return result;
        }
        throw new IOException("result=0x" + Integer.toHexString(result));
    }

    public int writeEntityData(byte[] data, int size) throws IOException {
        int result = writeEntityData_native(this.mBackupWriter, data, size);
        if (result >= 0) {
            return result;
        }
        throw new IOException("result=0x" + Integer.toHexString(result));
    }

    public void setKeyPrefix(String keyPrefix) {
        setKeyPrefix_native(this.mBackupWriter, keyPrefix);
    }

    protected void finalize() throws Throwable {
        try {
            dtor(this.mBackupWriter);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }
}