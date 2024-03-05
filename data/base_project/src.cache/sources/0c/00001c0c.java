package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.IPackageManager;
import android.os.Build;
import android.os.DropBoxManager;
import android.os.FileObserver;
import android.os.FileUtils;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Downloads;
import android.provider.Telephony;
import android.util.Slog;
import gov.nist.core.Separators;
import java.io.File;
import java.io.IOException;

/* loaded from: BootReceiver.class */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private static final int LOG_SIZE;
    private static final File TOMBSTONE_DIR;
    private static final String OLD_UPDATER_PACKAGE = "com.google.android.systemupdater";
    private static final String OLD_UPDATER_CLASS = "com.google.android.systemupdater.SystemUpdateReceiver";
    private static FileObserver sTombstoneObserver;

    static {
        LOG_SIZE = SystemProperties.getInt("ro.debuggable", 0) == 1 ? 98304 : 65536;
        TOMBSTONE_DIR = new File("/data/tombstones");
        sTombstoneObserver = null;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, Intent intent) {
        new Thread() { // from class: com.android.server.BootReceiver.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    BootReceiver.this.logBootEvents(context);
                } catch (Exception e) {
                    Slog.e(BootReceiver.TAG, "Can't log boot events", e);
                }
                boolean onlyCore = false;
                try {
                    try {
                        onlyCore = IPackageManager.Stub.asInterface(ServiceManager.getService(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME)).isOnlyCoreApps();
                    } catch (RemoteException e2) {
                    }
                    if (!onlyCore) {
                        BootReceiver.this.removeOldUpdatePackages(context);
                    }
                } catch (Exception e3) {
                    Slog.e(BootReceiver.TAG, "Can't remove old update packages", e3);
                }
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeOldUpdatePackages(Context context) {
        Downloads.removeAllDownloadsByPackage(context, OLD_UPDATER_PACKAGE, OLD_UPDATER_CLASS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logBootEvents(Context ctx) throws IOException {
        final DropBoxManager db = (DropBoxManager) ctx.getSystemService(Context.DROPBOX_SERVICE);
        final SharedPreferences prefs = ctx.getSharedPreferences("log_files", 0);
        final String headers = new StringBuilder(512).append("Build: ").append(Build.FINGERPRINT).append(Separators.RETURN).append("Hardware: ").append(Build.BOARD).append(Separators.RETURN).append("Revision: ").append(SystemProperties.get("ro.revision", "")).append(Separators.RETURN).append("Bootloader: ").append(Build.BOOTLOADER).append(Separators.RETURN).append("Radio: ").append(Build.RADIO).append(Separators.RETURN).append("Kernel: ").append(FileUtils.readTextFile(new File("/proc/version"), 1024, "...\n")).append(Separators.RETURN).toString();
        String recovery = RecoverySystem.handleAftermath();
        if (recovery != null && db != null) {
            db.addText("SYSTEM_RECOVERY_LOG", headers + recovery);
        }
        if (SystemProperties.getLong("ro.runtime.firstboot", 0L) == 0) {
            String now = Long.toString(System.currentTimeMillis());
            SystemProperties.set("ro.runtime.firstboot", now);
            if (db != null) {
                db.addText("SYSTEM_BOOT", headers);
            }
            addFileToDropBox(db, prefs, headers, "/proc/last_kmsg", -LOG_SIZE, "SYSTEM_LAST_KMSG");
            addFileToDropBox(db, prefs, headers, "/cache/recovery/log", -LOG_SIZE, "SYSTEM_RECOVERY_LOG");
            addFileToDropBox(db, prefs, headers, "/data/dontpanic/apanic_console", -LOG_SIZE, "APANIC_CONSOLE");
            addFileToDropBox(db, prefs, headers, "/data/dontpanic/apanic_threads", -LOG_SIZE, "APANIC_THREADS");
            addAuditErrorsToDropBox(db, prefs, headers, -LOG_SIZE, "SYSTEM_AUDIT");
            addFsckErrorsToDropBox(db, prefs, headers, -LOG_SIZE, "SYSTEM_FSCK");
        } else if (db != null) {
            db.addText("SYSTEM_RESTART", headers);
        }
        File[] tombstoneFiles = TOMBSTONE_DIR.listFiles();
        for (int i = 0; tombstoneFiles != null && i < tombstoneFiles.length; i++) {
            addFileToDropBox(db, prefs, headers, tombstoneFiles[i].getPath(), LOG_SIZE, "SYSTEM_TOMBSTONE");
        }
        sTombstoneObserver = new FileObserver(TOMBSTONE_DIR.getPath(), 8) { // from class: com.android.server.BootReceiver.2
            @Override // android.os.FileObserver
            public void onEvent(int event, String path) {
                try {
                    String filename = new File(BootReceiver.TOMBSTONE_DIR, path).getPath();
                    BootReceiver.addFileToDropBox(db, prefs, headers, filename, BootReceiver.LOG_SIZE, "SYSTEM_TOMBSTONE");
                } catch (IOException e) {
                    Slog.e(BootReceiver.TAG, "Can't log tombstone", e);
                }
            }
        };
        sTombstoneObserver.startWatching();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void addFileToDropBox(DropBoxManager db, SharedPreferences prefs, String headers, String filename, int maxSize, String tag) throws IOException {
        if (db == null || !db.isTagEnabled(tag)) {
            return;
        }
        File file = new File(filename);
        long fileTime = file.lastModified();
        if (fileTime <= 0) {
            return;
        }
        if (prefs != null) {
            long lastTime = prefs.getLong(filename, 0L);
            if (lastTime == fileTime) {
                return;
            }
            prefs.edit().putLong(filename, fileTime).apply();
        }
        Slog.i(TAG, "Copying " + filename + " to DropBox (" + tag + Separators.RPAREN);
        db.addText(tag, headers + FileUtils.readTextFile(file, maxSize, "[[TRUNCATED]]\n"));
    }

    private static void addAuditErrorsToDropBox(DropBoxManager db, SharedPreferences prefs, String headers, int maxSize, String tag) throws IOException {
        if (db == null || !db.isTagEnabled(tag)) {
            return;
        }
        Slog.i(TAG, "Copying audit failures to DropBox");
        File file = new File("/proc/last_kmsg");
        long fileTime = file.lastModified();
        if (fileTime <= 0) {
            return;
        }
        if (prefs != null) {
            long lastTime = prefs.getLong(tag, 0L);
            if (lastTime == fileTime) {
                return;
            }
            prefs.edit().putLong(tag, fileTime).apply();
        }
        String log = FileUtils.readTextFile(file, maxSize, "[[TRUNCATED]]\n");
        StringBuilder sb = new StringBuilder();
        String[] arr$ = log.split(Separators.RETURN);
        for (String line : arr$) {
            if (line.contains("audit")) {
                sb.append(line + Separators.RETURN);
            }
        }
        Slog.i(TAG, "Copied " + sb.toString().length() + " worth of audits to DropBox");
        db.addText(tag, headers + sb.toString());
    }

    private static void addFsckErrorsToDropBox(DropBoxManager db, SharedPreferences prefs, String headers, int maxSize, String tag) throws IOException {
        boolean upload_needed = false;
        if (db == null || !db.isTagEnabled(tag)) {
            return;
        }
        Slog.i(TAG, "Checking for fsck errors");
        File file = new File("/dev/fscklogs/log");
        long fileTime = file.lastModified();
        if (fileTime <= 0) {
            return;
        }
        String log = FileUtils.readTextFile(file, maxSize, "[[TRUNCATED]]\n");
        new StringBuilder();
        String[] arr$ = log.split(Separators.RETURN);
        int len$ = arr$.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            String line = arr$[i$];
            if (!line.contains("FILE SYSTEM WAS MODIFIED")) {
                i$++;
            } else {
                upload_needed = true;
                break;
            }
        }
        if (upload_needed) {
            addFileToDropBox(db, prefs, headers, "/dev/fscklogs/log", maxSize, tag);
        }
        file.delete();
    }
}