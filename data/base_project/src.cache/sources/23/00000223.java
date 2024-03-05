package android.app.backup;

import java.io.FileDescriptor;
import java.io.IOException;

/* loaded from: BackupDataInput.class */
public class BackupDataInput {
    int mBackupReader;
    private EntityHeader mHeader = new EntityHeader();
    private boolean mHeaderReady;

    private static native int ctor(FileDescriptor fileDescriptor);

    private static native void dtor(int i);

    private native int readNextHeader_native(int i, EntityHeader entityHeader);

    private native int readEntityData_native(int i, byte[] bArr, int i2, int i3);

    private native int skipEntityData_native(int i);

    /* loaded from: BackupDataInput$EntityHeader.class */
    private static class EntityHeader {
        String key;
        int dataSize;

        private EntityHeader() {
        }
    }

    public BackupDataInput(FileDescriptor fd) {
        if (fd == null) {
            throw new NullPointerException();
        }
        this.mBackupReader = ctor(fd);
        if (this.mBackupReader == 0) {
            throw new RuntimeException("Native initialization failed with fd=" + fd);
        }
    }

    protected void finalize() throws Throwable {
        try {
            dtor(this.mBackupReader);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public boolean readNextHeader() throws IOException {
        int result = readNextHeader_native(this.mBackupReader, this.mHeader);
        if (result == 0) {
            this.mHeaderReady = true;
            return true;
        } else if (result > 0) {
            this.mHeaderReady = false;
            return false;
        } else {
            this.mHeaderReady = false;
            throw new IOException("failed: 0x" + Integer.toHexString(result));
        }
    }

    public String getKey() {
        if (this.mHeaderReady) {
            return this.mHeader.key;
        }
        throw new IllegalStateException("Entity header not read");
    }

    public int getDataSize() {
        if (this.mHeaderReady) {
            return this.mHeader.dataSize;
        }
        throw new IllegalStateException("Entity header not read");
    }

    public int readEntityData(byte[] data, int offset, int size) throws IOException {
        if (this.mHeaderReady) {
            int result = readEntityData_native(this.mBackupReader, data, offset, size);
            if (result >= 0) {
                return result;
            }
            throw new IOException("result=0x" + Integer.toHexString(result));
        }
        throw new IllegalStateException("Entity header not read");
    }

    public void skipEntityData() throws IOException {
        if (this.mHeaderReady) {
            skipEntityData_native(this.mBackupReader);
            return;
        }
        throw new IllegalStateException("Entity header not read");
    }
}