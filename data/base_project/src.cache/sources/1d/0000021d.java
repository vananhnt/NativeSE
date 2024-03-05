package android.app.backup;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import java.io.File;

/* loaded from: AbsoluteFileBackupHelper.class */
public class AbsoluteFileBackupHelper extends FileBackupHelperBase implements BackupHelper {
    private static final String TAG = "AbsoluteFileBackupHelper";
    private static final boolean DEBUG = false;
    Context mContext;
    String[] mFiles;

    @Override // android.app.backup.FileBackupHelperBase, android.app.backup.BackupHelper
    public /* bridge */ /* synthetic */ void writeNewStateDescription(ParcelFileDescriptor x0) {
        super.writeNewStateDescription(x0);
    }

    public AbsoluteFileBackupHelper(Context context, String... files) {
        super(context);
        this.mContext = context;
        this.mFiles = files;
    }

    @Override // android.app.backup.BackupHelper
    public void performBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
        performBackup_checked(oldState, data, newState, this.mFiles, this.mFiles);
    }

    @Override // android.app.backup.BackupHelper
    public void restoreEntity(BackupDataInputStream data) {
        String key = data.getKey();
        if (isKeyInList(key, this.mFiles)) {
            File f = new File(key);
            writeFile(f, data);
        }
    }
}