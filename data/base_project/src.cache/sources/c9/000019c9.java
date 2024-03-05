package com.android.internal.content;

import android.os.Build;
import android.util.Slog;
import java.io.File;

/* loaded from: NativeLibraryHelper.class */
public class NativeLibraryHelper {
    private static final String TAG = "NativeHelper";
    private static final boolean DEBUG_NATIVE = false;

    private static native long nativeSumNativeBinaries(String str, String str2, String str3);

    private static native int nativeCopyNativeBinaries(String str, String str2, String str3, String str4);

    public static long sumNativeBinariesLI(File apkFile) {
        String cpuAbi = Build.CPU_ABI;
        String cpuAbi2 = Build.CPU_ABI2;
        return nativeSumNativeBinaries(apkFile.getPath(), cpuAbi, cpuAbi2);
    }

    public static int copyNativeBinariesIfNeededLI(File apkFile, File sharedLibraryDir) {
        String cpuAbi = Build.CPU_ABI;
        String cpuAbi2 = Build.CPU_ABI2;
        return nativeCopyNativeBinaries(apkFile.getPath(), sharedLibraryDir.getPath(), cpuAbi, cpuAbi2);
    }

    public static boolean removeNativeBinariesLI(String nativeLibraryPath) {
        return removeNativeBinariesFromDirLI(new File(nativeLibraryPath));
    }

    public static boolean removeNativeBinariesFromDirLI(File nativeLibraryDir) {
        File[] binaries;
        boolean deletedFiles = false;
        if (nativeLibraryDir.exists() && (binaries = nativeLibraryDir.listFiles()) != null) {
            for (int nn = 0; nn < binaries.length; nn++) {
                if (!binaries[nn].delete()) {
                    Slog.w(TAG, "Could not delete native binary: " + binaries[nn].getPath());
                } else {
                    deletedFiles = true;
                }
            }
        }
        return deletedFiles;
    }
}