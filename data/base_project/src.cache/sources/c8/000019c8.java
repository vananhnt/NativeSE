package com.android.internal.backup;

import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.RestoreSet;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SELinux;
import android.util.Log;
import com.android.internal.backup.IBackupTransport;
import com.android.org.bouncycastle.util.encoders.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: LocalTransport.class */
public class LocalTransport extends IBackupTransport.Stub {
    private static final String TAG = "LocalTransport";
    private static final boolean DEBUG = true;
    private static final String TRANSPORT_DIR_NAME = "com.android.internal.backup.LocalTransport";
    private static final String TRANSPORT_DESTINATION_STRING = "Backing up to debug-only private cache";
    private static final long RESTORE_TOKEN = 1;
    private Context mContext;
    private File mDataDir = new File(Environment.getDownloadCacheDirectory(), Context.BACKUP_SERVICE);
    private PackageInfo[] mRestorePackages = null;
    private int mRestorePackage = -1;

    public LocalTransport(Context context) {
        this.mContext = context;
        this.mDataDir.mkdirs();
        if (!SELinux.restorecon(this.mDataDir)) {
            Log.e(TAG, "SELinux restorecon failed for " + this.mDataDir);
        }
    }

    @Override // com.android.internal.backup.IBackupTransport
    public Intent configurationIntent() {
        return null;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public String currentDestinationString() {
        return TRANSPORT_DESTINATION_STRING;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public String transportDirName() {
        return TRANSPORT_DIR_NAME;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public long requestBackupTime() {
        return 0L;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public int initializeDevice() {
        Log.v(TAG, "wiping all data");
        deleteContents(this.mDataDir);
        return 0;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public int performBackup(PackageInfo packageInfo, ParcelFileDescriptor data) {
        Log.v(TAG, "performBackup() pkg=" + packageInfo.packageName);
        File packageDir = new File(this.mDataDir, packageInfo.packageName);
        packageDir.mkdirs();
        BackupDataInput changeSet = new BackupDataInput(data.getFileDescriptor());
        try {
            int bufSize = 512;
            byte[] buf = new byte[512];
            while (changeSet.readNextHeader()) {
                String key = changeSet.getKey();
                String base64Key = new String(Base64.encode(key.getBytes()));
                File entityFile = new File(packageDir, base64Key);
                int dataSize = changeSet.getDataSize();
                Log.v(TAG, "Got change set key=" + key + " size=" + dataSize + " key64=" + base64Key);
                if (dataSize >= 0) {
                    if (entityFile.exists()) {
                        entityFile.delete();
                    }
                    FileOutputStream entity = new FileOutputStream(entityFile);
                    if (dataSize > bufSize) {
                        bufSize = dataSize;
                        buf = new byte[bufSize];
                    }
                    changeSet.readEntityData(buf, 0, dataSize);
                    Log.v(TAG, "  data size " + dataSize);
                    try {
                        entity.write(buf, 0, dataSize);
                        entity.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Unable to update key file " + entityFile.getAbsolutePath());
                        entity.close();
                        return 1;
                    }
                } else {
                    entityFile.delete();
                }
            }
            return 0;
        } catch (IOException e2) {
            Log.v(TAG, "Exception reading backup input:", e2);
            return 1;
        }
    }

    private void deleteContents(File dirname) {
        File[] contents = dirname.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (f.isDirectory()) {
                    deleteContents(f);
                }
                f.delete();
            }
        }
    }

    @Override // com.android.internal.backup.IBackupTransport
    public int clearBackupData(PackageInfo packageInfo) {
        Log.v(TAG, "clearBackupData() pkg=" + packageInfo.packageName);
        File packageDir = new File(this.mDataDir, packageInfo.packageName);
        File[] fileset = packageDir.listFiles();
        if (fileset != null) {
            for (File f : fileset) {
                f.delete();
            }
            packageDir.delete();
            return 0;
        }
        return 0;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public int finishBackup() {
        Log.v(TAG, "finishBackup()");
        return 0;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public RestoreSet[] getAvailableRestoreSets() throws RemoteException {
        RestoreSet set = new RestoreSet("Local disk image", "flash", 1L);
        RestoreSet[] array = {set};
        return array;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public long getCurrentRestoreSet() {
        return 1L;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public int startRestore(long token, PackageInfo[] packages) {
        Log.v(TAG, "start restore " + token);
        this.mRestorePackages = packages;
        this.mRestorePackage = -1;
        return 0;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public String nextRestorePackage() {
        String name;
        if (this.mRestorePackages == null) {
            throw new IllegalStateException("startRestore not called");
        }
        do {
            int i = this.mRestorePackage + 1;
            this.mRestorePackage = i;
            if (i < this.mRestorePackages.length) {
                name = this.mRestorePackages[this.mRestorePackage].packageName;
            } else {
                Log.v(TAG, "  no more packages to restore");
                return "";
            }
        } while (!new File(this.mDataDir, name).isDirectory());
        Log.v(TAG, "  nextRestorePackage() = " + name);
        return name;
    }

    @Override // com.android.internal.backup.IBackupTransport
    public int getRestoreData(ParcelFileDescriptor outFd) {
        if (this.mRestorePackages == null) {
            throw new IllegalStateException("startRestore not called");
        }
        if (this.mRestorePackage < 0) {
            throw new IllegalStateException("nextRestorePackage not called");
        }
        File packageDir = new File(this.mDataDir, this.mRestorePackages[this.mRestorePackage].packageName);
        File[] blobs = packageDir.listFiles();
        if (blobs == null) {
            Log.e(TAG, "Error listing directory: " + packageDir);
            return 1;
        }
        Log.v(TAG, "  getRestoreData() found " + blobs.length + " key files");
        BackupDataOutput out = new BackupDataOutput(outFd.getFileDescriptor());
        try {
            for (File f : blobs) {
                FileInputStream in = new FileInputStream(f);
                int size = (int) f.length();
                byte[] buf = new byte[size];
                in.read(buf);
                String key = new String(Base64.decode(f.getName()));
                Log.v(TAG, "    ... key=" + key + " size=" + size);
                out.writeEntityHeader(key, size);
                out.writeEntityData(buf, size);
                in.close();
            }
            return 0;
        } catch (IOException e) {
            Log.e(TAG, "Unable to read backup records", e);
            return 1;
        }
    }

    @Override // com.android.internal.backup.IBackupTransport
    public void finishRestore() {
        Log.v(TAG, "finishRestore()");
    }
}