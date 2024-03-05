package android.app.backup;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import java.io.File;

/* loaded from: FileBackupHelper.class */
public class FileBackupHelper extends FileBackupHelperBase implements BackupHelper {
    private static final String TAG = "FileBackupHelper";
    private static final boolean DEBUG = false;
    Context mContext;
    File mFilesDir;
    String[] mFiles;

    @Override // android.app.backup.FileBackupHelperBase, android.app.backup.BackupHelper
    public /* bridge */ /* synthetic */ void writeNewStateDescription(ParcelFileDescriptor x0) {
        super.writeNewStateDescription(x0);
    }

    public FileBackupHelper(Context context, String... files) {
        super(context);
        this.mContext = context;
        this.mFilesDir = context.getFilesDir();
        this.mFiles = files;
    }

    @Override // android.app.backup.BackupHelper
    public void performBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
        String[] files = this.mFiles;
        File base = this.mContext.getFilesDir();
        int N = files.length;
        String[] fullPaths = new String[N];
        for (int i = 0; i < N; i++) {
            fullPaths[i] = new File(base, files[i]).getAbsolutePath();
        }
        performBackup_checked(oldState, data, newState, fullPaths, files);
    }

    @Override // android.app.backup.BackupHelper
    public void restoreEntity(BackupDataInputStream data) {
        String key = data.getKey();
        if (isKeyInList(key, this.mFiles)) {
            File f = new File(this.mFilesDir, key);
            writeFile(f, data);
        }
    }
}