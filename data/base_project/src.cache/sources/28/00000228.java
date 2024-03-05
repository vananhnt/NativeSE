package android.app.backup;

import android.os.ParcelFileDescriptor;

/* loaded from: BackupHelper.class */
public interface BackupHelper {
    void performBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2);

    void restoreEntity(BackupDataInputStream backupDataInputStream);

    void writeNewStateDescription(ParcelFileDescriptor parcelFileDescriptor);
}