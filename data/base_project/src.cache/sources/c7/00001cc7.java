package com.android.server;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Slog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: PackageManagerBackupAgent.class */
public class PackageManagerBackupAgent extends BackupAgent {
    private static final String TAG = "PMBA";
    private static final boolean DEBUG = false;
    private static final String GLOBAL_METADATA_KEY = "@meta@";
    private List<PackageInfo> mAllPackages;
    private PackageManager mPackageManager;
    private int mStoredSdkVersion;
    private String mStoredIncrementalVersion;
    private HashMap<String, Metadata> mStateVersions = new HashMap<>();
    private final HashSet<String> mExisting = new HashSet<>();
    private HashMap<String, Metadata> mRestoredSignatures = null;
    private boolean mHasMetadata = false;

    /* loaded from: PackageManagerBackupAgent$Metadata.class */
    public class Metadata {
        public int versionCode;
        public Signature[] signatures;

        Metadata(int version, Signature[] sigs) {
            this.versionCode = version;
            this.signatures = sigs;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PackageManagerBackupAgent(PackageManager packageMgr, List<PackageInfo> packages) {
        this.mPackageManager = packageMgr;
        this.mAllPackages = packages;
    }

    public boolean hasMetadata() {
        return this.mHasMetadata;
    }

    public Metadata getRestoredMetadata(String packageName) {
        if (this.mRestoredSignatures == null) {
            Slog.w(TAG, "getRestoredMetadata() before metadata read!");
            return null;
        }
        return this.mRestoredSignatures.get(packageName);
    }

    public Set<String> getRestoredPackages() {
        if (this.mRestoredSignatures == null) {
            Slog.w(TAG, "getRestoredPackages() before metadata read!");
            return null;
        }
        return this.mRestoredSignatures.keySet();
    }

    @Override // android.app.backup.BackupAgent
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        DataOutputStream outputBufferStream = new DataOutputStream(outputBuffer);
        parseStateFile(oldState);
        if (this.mStoredIncrementalVersion == null || !this.mStoredIncrementalVersion.equals(Build.VERSION.INCREMENTAL)) {
            Slog.i(TAG, "Previous metadata " + this.mStoredIncrementalVersion + " mismatch vs " + Build.VERSION.INCREMENTAL + " - rewriting");
            this.mExisting.clear();
        }
        try {
            if (!this.mExisting.contains(GLOBAL_METADATA_KEY)) {
                outputBufferStream.writeInt(Build.VERSION.SDK_INT);
                outputBufferStream.writeUTF(Build.VERSION.INCREMENTAL);
                writeEntity(data, GLOBAL_METADATA_KEY, outputBuffer.toByteArray());
            } else {
                this.mExisting.remove(GLOBAL_METADATA_KEY);
            }
            for (PackageInfo pkg : this.mAllPackages) {
                String packName = pkg.packageName;
                if (!packName.equals(GLOBAL_METADATA_KEY)) {
                    try {
                        PackageInfo info = this.mPackageManager.getPackageInfo(packName, 64);
                        if (this.mExisting.contains(packName)) {
                            this.mExisting.remove(packName);
                            if (info.versionCode == this.mStateVersions.get(packName).versionCode) {
                            }
                        }
                        if (info.signatures == null || info.signatures.length == 0) {
                            Slog.w(TAG, "Not backing up package " + packName + " since it appears to have no signatures.");
                        } else {
                            outputBuffer.reset();
                            outputBufferStream.writeInt(info.versionCode);
                            writeSignatureArray(outputBufferStream, info.signatures);
                            writeEntity(data, packName, outputBuffer.toByteArray());
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        this.mExisting.add(packName);
                    }
                }
            }
            Iterator i$ = this.mExisting.iterator();
            while (i$.hasNext()) {
                String app = i$.next();
                try {
                    data.writeEntityHeader(app, -1);
                } catch (IOException e2) {
                    Slog.e(TAG, "Unable to write package deletions!");
                    return;
                }
            }
            writeStateFile(this.mAllPackages, newState);
        } catch (IOException e3) {
            Slog.e(TAG, "Unable to write package backup data file!");
        }
    }

    private static void writeEntity(BackupDataOutput data, String key, byte[] bytes) throws IOException {
        data.writeEntityHeader(key, bytes.length);
        data.writeEntityData(bytes, bytes.length);
    }

    @Override // android.app.backup.BackupAgent
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        List<ApplicationInfo> restoredApps = new ArrayList<>();
        HashMap<String, Metadata> sigMap = new HashMap<>();
        while (data.readNextHeader()) {
            String key = data.getKey();
            int dataSize = data.getDataSize();
            byte[] inputBytes = new byte[dataSize];
            data.readEntityData(inputBytes, 0, dataSize);
            ByteArrayInputStream inputBuffer = new ByteArrayInputStream(inputBytes);
            DataInputStream inputBufferStream = new DataInputStream(inputBuffer);
            if (key.equals(GLOBAL_METADATA_KEY)) {
                int storedSdkVersion = inputBufferStream.readInt();
                if (-1 > Build.VERSION.SDK_INT) {
                    Slog.w(TAG, "Restore set was from a later version of Android; not restoring");
                    return;
                }
                this.mStoredSdkVersion = storedSdkVersion;
                this.mStoredIncrementalVersion = inputBufferStream.readUTF();
                this.mHasMetadata = true;
            } else {
                int versionCode = inputBufferStream.readInt();
                Signature[] sigs = readSignatureArray(inputBufferStream);
                if (sigs == null || sigs.length == 0) {
                    Slog.w(TAG, "Not restoring package " + key + " since it appears to have no signatures.");
                } else {
                    ApplicationInfo app = new ApplicationInfo();
                    app.packageName = key;
                    restoredApps.add(app);
                    sigMap.put(key, new Metadata(versionCode, sigs));
                }
            }
        }
        this.mRestoredSignatures = sigMap;
    }

    private static void writeSignatureArray(DataOutputStream out, Signature[] sigs) throws IOException {
        out.writeInt(sigs.length);
        for (Signature sig : sigs) {
            byte[] flat = sig.toByteArray();
            out.writeInt(flat.length);
            out.write(flat);
        }
    }

    private static Signature[] readSignatureArray(DataInputStream in) {
        try {
            try {
                int num = in.readInt();
                if (num > 20) {
                    Slog.e(TAG, "Suspiciously large sig count in restore data; aborting");
                    throw new IllegalStateException("Bad restore state");
                }
                Signature[] sigs = new Signature[num];
                for (int i = 0; i < num; i++) {
                    int len = in.readInt();
                    byte[] flatSig = new byte[len];
                    in.read(flatSig);
                    sigs[i] = new Signature(flatSig);
                }
                return sigs;
            } catch (EOFException e) {
                Slog.w(TAG, "Read empty signature block");
                return null;
            }
        } catch (IOException e2) {
            Slog.e(TAG, "Unable to read signatures");
            return null;
        }
    }

    private void parseStateFile(ParcelFileDescriptor stateFile) {
        this.mExisting.clear();
        this.mStateVersions.clear();
        this.mStoredSdkVersion = 0;
        this.mStoredIncrementalVersion = null;
        FileInputStream instream = new FileInputStream(stateFile.getFileDescriptor());
        DataInputStream in = new DataInputStream(instream);
        byte[] bArr = new byte[256];
        try {
            if (in.readUTF().equals(GLOBAL_METADATA_KEY)) {
                this.mStoredSdkVersion = in.readInt();
                this.mStoredIncrementalVersion = in.readUTF();
                this.mExisting.add(GLOBAL_METADATA_KEY);
                while (true) {
                    String pkg = in.readUTF();
                    int versionCode = in.readInt();
                    this.mExisting.add(pkg);
                    this.mStateVersions.put(pkg, new Metadata(versionCode, null));
                }
            } else {
                Slog.e(TAG, "No global metadata in state file!");
            }
        } catch (EOFException e) {
        } catch (IOException e2) {
            Slog.e(TAG, "Unable to read Package Manager state file: " + e2);
        }
    }

    private void writeStateFile(List<PackageInfo> pkgs, ParcelFileDescriptor stateFile) {
        FileOutputStream outstream = new FileOutputStream(stateFile.getFileDescriptor());
        DataOutputStream out = new DataOutputStream(outstream);
        try {
            out.writeUTF(GLOBAL_METADATA_KEY);
            out.writeInt(Build.VERSION.SDK_INT);
            out.writeUTF(Build.VERSION.INCREMENTAL);
            for (PackageInfo pkg : pkgs) {
                out.writeUTF(pkg.packageName);
                out.writeInt(pkg.versionCode);
            }
        } catch (IOException e) {
            Slog.e(TAG, "Unable to write package manager state file!");
        }
    }
}