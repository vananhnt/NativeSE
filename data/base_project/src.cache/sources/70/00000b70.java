package android.os;

import android.util.Slog;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

/* loaded from: SELinux.class */
public class SELinux {
    private static final String TAG = "SELinux";

    public static final native boolean isSELinuxEnabled();

    public static final native boolean isSELinuxEnforced();

    public static final native boolean setSELinuxEnforce(boolean z);

    public static final native boolean setFSCreateContext(String str);

    public static final native boolean setFileContext(String str, String str2);

    public static final native String getFileContext(String str);

    public static final native String getPeerContext(FileDescriptor fileDescriptor);

    public static final native String getContext();

    public static final native String getPidContext(int i);

    public static final native String[] getBooleanNames();

    public static final native boolean getBooleanValue(String str);

    public static final native boolean setBooleanValue(String str, boolean z);

    public static final native boolean checkSELinuxAccess(String str, String str2, String str3, String str4);

    private static native boolean native_restorecon(String str);

    public static boolean restorecon(String pathname) throws NullPointerException {
        if (pathname == null) {
            throw new NullPointerException();
        }
        return native_restorecon(pathname);
    }

    public static boolean restorecon(File file) throws NullPointerException {
        try {
            return native_restorecon(file.getCanonicalPath());
        } catch (IOException e) {
            Slog.e(TAG, "Error getting canonical path. Restorecon failed for " + file.getPath(), e);
            return false;
        }
    }
}