package com.android.server;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FullBackup;
import android.app.backup.FullBackupDataOutput;
import android.app.backup.WallpaperBackupHelper;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.ServiceManager;
import android.util.Slog;
import java.io.File;
import java.io.IOException;

/* loaded from: SystemBackupAgent.class */
public class SystemBackupAgent extends BackupAgentHelper {
    private static final String TAG = "SystemBackupAgent";
    private static final String WALLPAPER_IMAGE_FILENAME = "wallpaper";
    private static final String WALLPAPER_INFO_FILENAME = "wallpaper_info.xml";
    private static final String WALLPAPER_IMAGE_DIR = Environment.getUserSystemDirectory(0).getAbsolutePath();
    private static final String WALLPAPER_IMAGE = WallpaperBackupHelper.WALLPAPER_IMAGE;
    private static final String WALLPAPER_INFO_DIR = Environment.getUserSystemDirectory(0).getAbsolutePath();
    private static final String WALLPAPER_INFO = WallpaperBackupHelper.WALLPAPER_INFO;
    private static final String WALLPAPER_IMAGE_KEY = "/data/data/com.android.settings/files/wallpaper";
    private static final String WALLPAPER_INFO_KEY = "/data/system/wallpaper_info.xml";

    @Override // android.app.backup.BackupAgentHelper, android.app.backup.BackupAgent
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        WallpaperManagerService wallpaper = (WallpaperManagerService) ServiceManager.getService("wallpaper");
        String[] files = {WALLPAPER_IMAGE, WALLPAPER_INFO};
        String[] keys = {"/data/data/com.android.settings/files/wallpaper", "/data/system/wallpaper_info.xml"};
        if (wallpaper != null && wallpaper.getName() != null && wallpaper.getName().length() > 0) {
            files = new String[]{WALLPAPER_INFO};
            keys = new String[]{"/data/system/wallpaper_info.xml"};
        }
        addHelper("wallpaper", new WallpaperBackupHelper(this, files, keys));
        super.onBackup(oldState, data, newState);
    }

    @Override // android.app.backup.BackupAgent
    public void onFullBackup(FullBackupDataOutput data) throws IOException {
        fullWallpaperBackup(data);
    }

    private void fullWallpaperBackup(FullBackupDataOutput output) {
        FullBackup.backupToTar(getPackageName(), FullBackup.ROOT_TREE_TOKEN, null, WALLPAPER_INFO_DIR, WALLPAPER_INFO, output.getData());
        FullBackup.backupToTar(getPackageName(), FullBackup.ROOT_TREE_TOKEN, null, WALLPAPER_IMAGE_DIR, WALLPAPER_IMAGE, output.getData());
    }

    @Override // android.app.backup.BackupAgentHelper, android.app.backup.BackupAgent
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        addHelper("wallpaper", new WallpaperBackupHelper(this, new String[]{WALLPAPER_IMAGE, WALLPAPER_INFO}, new String[]{"/data/data/com.android.settings/files/wallpaper", "/data/system/wallpaper_info.xml"}));
        addHelper("system_files", new WallpaperBackupHelper(this, new String[]{WALLPAPER_IMAGE}, new String[]{"/data/data/com.android.settings/files/wallpaper"}));
        try {
            super.onRestore(data, appVersionCode, newState);
            WallpaperManagerService wallpaper = (WallpaperManagerService) ServiceManager.getService("wallpaper");
            wallpaper.settingsRestored();
        } catch (IOException ex) {
            Slog.d(TAG, "restore failed", ex);
            new File(WALLPAPER_IMAGE).delete();
            new File(WALLPAPER_INFO).delete();
        }
    }

    @Override // android.app.backup.BackupAgent
    public void onRestoreFile(ParcelFileDescriptor data, long size, int type, String domain, String path, long mode, long mtime) throws IOException {
        Slog.i(TAG, "Restoring file domain=" + domain + " path=" + path);
        boolean restoredWallpaper = false;
        File outFile = null;
        if (domain.equals(FullBackup.ROOT_TREE_TOKEN)) {
            if (path.equals(WALLPAPER_INFO_FILENAME)) {
                outFile = new File(WALLPAPER_INFO);
                restoredWallpaper = true;
            } else if (path.equals("wallpaper")) {
                outFile = new File(WALLPAPER_IMAGE);
                restoredWallpaper = true;
            }
        }
        if (outFile == null) {
            try {
                Slog.w(TAG, "Skipping unrecognized system file: [ " + domain + " : " + path + " ]");
            } catch (IOException e) {
                if (restoredWallpaper) {
                    new File(WALLPAPER_IMAGE).delete();
                    new File(WALLPAPER_INFO).delete();
                    return;
                }
                return;
            }
        }
        FullBackup.restoreFile(data, size, type, mode, mtime, outFile);
        if (restoredWallpaper) {
            WallpaperManagerService wallpaper = (WallpaperManagerService) ServiceManager.getService("wallpaper");
            wallpaper.settingsRestored();
        }
    }
}