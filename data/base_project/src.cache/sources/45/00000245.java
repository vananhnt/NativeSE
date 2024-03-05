package android.app.backup;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.Display;
import android.view.WindowManager;
import java.io.File;

/* loaded from: WallpaperBackupHelper.class */
public class WallpaperBackupHelper extends FileBackupHelperBase implements BackupHelper {
    private static final String TAG = "WallpaperBackupHelper";
    private static final boolean DEBUG = false;
    public static final String WALLPAPER_IMAGE_KEY = "/data/data/com.android.settings/files/wallpaper";
    public static final String WALLPAPER_INFO_KEY = "/data/system/wallpaper_info.xml";
    Context mContext;
    String[] mFiles;
    String[] mKeys;
    double mDesiredMinWidth;
    double mDesiredMinHeight;
    public static final String WALLPAPER_IMAGE = new File(Environment.getUserSystemDirectory(0), Context.WALLPAPER_SERVICE).getAbsolutePath();
    public static final String WALLPAPER_INFO = new File(Environment.getUserSystemDirectory(0), "wallpaper_info.xml").getAbsolutePath();
    private static final String STAGE_FILE = new File(Environment.getUserSystemDirectory(0), "wallpaper-tmp").getAbsolutePath();

    @Override // android.app.backup.FileBackupHelperBase, android.app.backup.BackupHelper
    public /* bridge */ /* synthetic */ void writeNewStateDescription(ParcelFileDescriptor x0) {
        super.writeNewStateDescription(x0);
    }

    public WallpaperBackupHelper(Context context, String[] files, String[] keys) {
        super(context);
        this.mContext = context;
        this.mFiles = files;
        this.mKeys = keys;
        WallpaperManager wpm = (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
        this.mDesiredMinWidth = wpm.getDesiredMinimumWidth();
        this.mDesiredMinHeight = wpm.getDesiredMinimumHeight();
        if (this.mDesiredMinWidth <= 0.0d || this.mDesiredMinHeight <= 0.0d) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display d = wm.getDefaultDisplay();
            Point size = new Point();
            d.getSize(size);
            this.mDesiredMinWidth = size.x;
            this.mDesiredMinHeight = size.y;
        }
    }

    @Override // android.app.backup.BackupHelper
    public void performBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
        performBackup_checked(oldState, data, newState, this.mFiles, this.mKeys);
    }

    @Override // android.app.backup.BackupHelper
    public void restoreEntity(BackupDataInputStream data) {
        String key = data.getKey();
        if (isKeyInList(key, this.mKeys)) {
            if (!key.equals(WALLPAPER_IMAGE_KEY)) {
                if (key.equals(WALLPAPER_INFO_KEY)) {
                    writeFile(new File(WALLPAPER_INFO), data);
                    return;
                }
                return;
            }
            File f = new File(STAGE_FILE);
            if (writeFile(f, data)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(STAGE_FILE, options);
                double widthRatio = this.mDesiredMinWidth / options.outWidth;
                double heightRatio = this.mDesiredMinHeight / options.outHeight;
                if (widthRatio > 0.0d && widthRatio < 1.33d && heightRatio > 0.0d && heightRatio < 1.33d) {
                    f.renameTo(new File(WALLPAPER_IMAGE));
                } else {
                    f.delete();
                }
            }
        }
    }
}