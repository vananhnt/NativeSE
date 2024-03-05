package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import java.io.File;

/* loaded from: ContextCompat.class */
public class ContextCompat {
    private static final String DIR_ANDROID = "Android";
    private static final String DIR_CACHE = "cache";
    private static final String DIR_DATA = "data";
    private static final String DIR_FILES = "files";
    private static final String DIR_OBB = "obb";
    private static final String TAG = "ContextCompat";

    private static File buildPath(File file, String... strArr) {
        for (String str : strArr) {
            if (file == null) {
                file = new File(str);
            } else if (str != null) {
                file = new File(file, str);
            }
        }
        return file;
    }

    private static File createFilesDir(File file) {
        synchronized (ContextCompat.class) {
            try {
                if (file.exists() || file.mkdirs()) {
                    return file;
                }
                if (file.exists()) {
                    return file;
                }
                Log.w(TAG, "Unable to create files subdir " + file.getPath());
                return null;
            } finally {
            }
        }
    }

    public static final Drawable getDrawable(Context context, int i) {
        return Build.VERSION.SDK_INT >= 21 ? ContextCompatApi21.getDrawable(context, i) : context.getResources().getDrawable(i);
    }

    public static File[] getExternalCacheDirs(Context context) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 19) {
            return ContextCompatKitKat.getExternalCacheDirs(context);
        }
        return new File[]{i >= 8 ? ContextCompatFroyo.getExternalCacheDir(context) : buildPath(Environment.getExternalStorageDirectory(), "Android", "data", context.getPackageName(), DIR_CACHE)};
    }

    public static File[] getExternalFilesDirs(Context context, String str) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 19) {
            return ContextCompatKitKat.getExternalFilesDirs(context, str);
        }
        return new File[]{i >= 8 ? ContextCompatFroyo.getExternalFilesDir(context, str) : buildPath(Environment.getExternalStorageDirectory(), "Android", "data", context.getPackageName(), DIR_FILES, str)};
    }

    public static File[] getObbDirs(Context context) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 19) {
            return ContextCompatKitKat.getObbDirs(context);
        }
        return new File[]{i >= 11 ? ContextCompatHoneycomb.getObbDir(context) : buildPath(Environment.getExternalStorageDirectory(), "Android", "obb", context.getPackageName())};
    }

    public static boolean startActivities(Context context, Intent[] intentArr) {
        return startActivities(context, intentArr, null);
    }

    public static boolean startActivities(Context context, Intent[] intentArr, Bundle bundle) {
        int i = Build.VERSION.SDK_INT;
        if (i >= 16) {
            ContextCompatJellybean.startActivities(context, intentArr, bundle);
            return true;
        } else if (i >= 11) {
            ContextCompatHoneycomb.startActivities(context, intentArr);
            return true;
        } else {
            return false;
        }
    }

    public final File getCodeCacheDir(Context context) {
        return Build.VERSION.SDK_INT >= 21 ? ContextCompatApi21.getCodeCacheDir(context) : createFilesDir(new File(context.getApplicationInfo().dataDir, "code_cache"));
    }

    public final File getNoBackupFilesDir(Context context) {
        return Build.VERSION.SDK_INT >= 21 ? ContextCompatApi21.getNoBackupFilesDir(context) : createFilesDir(new File(context.getApplicationInfo().dataDir, "no_backup"));
    }
}