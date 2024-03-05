package android.app.backup;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;

/* loaded from: FileBackupHelperBase.class */
class FileBackupHelperBase {
    private static final String TAG = "FileBackupHelperBase";
    int mPtr = ctor();
    Context mContext;
    boolean mExceptionLogged;

    private static native int ctor();

    private static native void dtor(int i);

    private static native int performBackup_native(FileDescriptor fileDescriptor, int i, FileDescriptor fileDescriptor2, String[] strArr, String[] strArr2);

    private static native int writeFile_native(int i, String str, int i2);

    private static native int writeSnapshot_native(int i, FileDescriptor fileDescriptor);

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileBackupHelperBase(Context context) {
        this.mContext = context;
    }

    protected void finalize() throws Throwable {
        try {
            dtor(this.mPtr);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void performBackup_checked(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState, String[] files, String[] keys) {
        if (files.length == 0) {
            return;
        }
        for (String f : files) {
            if (f.charAt(0) != '/') {
                throw new RuntimeException("files must have all absolute paths: " + f);
            }
        }
        if (files.length != keys.length) {
            throw new RuntimeException("files.length=" + files.length + " keys.length=" + keys.length);
        }
        FileDescriptor oldStateFd = oldState != null ? oldState.getFileDescriptor() : null;
        FileDescriptor newStateFd = newState.getFileDescriptor();
        if (newStateFd == null) {
            throw new NullPointerException();
        }
        int err = performBackup_native(oldStateFd, data.mBackupWriter, newStateFd, files, keys);
        if (err != 0) {
            throw new RuntimeException("Backup failed 0x" + Integer.toHexString(err));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean writeFile(File f, BackupDataInputStream in) {
        File parent = f.getParentFile();
        parent.mkdirs();
        int result = writeFile_native(this.mPtr, f.getAbsolutePath(), in.mData.mBackupReader);
        if (result != 0 && !this.mExceptionLogged) {
            Log.e(TAG, "Failed restoring file '" + f + "' for app '" + this.mContext.getPackageName() + "' result=0x" + Integer.toHexString(result));
            this.mExceptionLogged = true;
        }
        return result == 0;
    }

    public void writeNewStateDescription(ParcelFileDescriptor fd) {
        writeSnapshot_native(this.mPtr, fd.getFileDescriptor());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isKeyInList(String key, String[] list) {
        for (String s : list) {
            if (s.equals(key)) {
                return true;
            }
        }
        return false;
    }
}