package android.app.backup;

import android.app.QueuedWork;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import java.io.File;

/* loaded from: SharedPreferencesBackupHelper.class */
public class SharedPreferencesBackupHelper extends FileBackupHelperBase implements BackupHelper {
    private static final String TAG = "SharedPreferencesBackupHelper";
    private static final boolean DEBUG = false;
    private Context mContext;
    private String[] mPrefGroups;

    @Override // android.app.backup.FileBackupHelperBase, android.app.backup.BackupHelper
    public /* bridge */ /* synthetic */ void writeNewStateDescription(ParcelFileDescriptor x0) {
        super.writeNewStateDescription(x0);
    }

    public SharedPreferencesBackupHelper(Context context, String... prefGroups) {
        super(context);
        this.mContext = context;
        this.mPrefGroups = prefGroups;
    }

    @Override // android.app.backup.BackupHelper
    public void performBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
        Context context = this.mContext;
        QueuedWork.waitToFinish();
        String[] prefGroups = this.mPrefGroups;
        int N = prefGroups.length;
        String[] files = new String[N];
        for (int i = 0; i < N; i++) {
            files[i] = context.getSharedPrefsFile(prefGroups[i]).getAbsolutePath();
        }
        performBackup_checked(oldState, data, newState, files, prefGroups);
    }

    @Override // android.app.backup.BackupHelper
    public void restoreEntity(BackupDataInputStream data) {
        Context context = this.mContext;
        String key = data.getKey();
        if (isKeyInList(key, this.mPrefGroups)) {
            File f = context.getSharedPrefsFile(key).getAbsoluteFile();
            writeFile(f, data);
        }
    }
}