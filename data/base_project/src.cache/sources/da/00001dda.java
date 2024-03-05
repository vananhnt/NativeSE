package com.android.server.am;

import android.net.Uri;
import android.os.UserHandle;
import android.util.Log;
import com.google.android.collect.Sets;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: UriPermission.class */
public final class UriPermission {
    private static final String TAG = "UriPermission";
    public static final int STRENGTH_NONE = 0;
    public static final int STRENGTH_OWNED = 1;
    public static final int STRENGTH_GLOBAL = 2;
    public static final int STRENGTH_PERSISTABLE = 3;
    final int userHandle;
    final String sourcePkg;
    final String targetPkg;
    final int targetUid;
    final Uri uri;
    int modeFlags = 0;
    int ownedModeFlags = 0;
    int globalModeFlags = 0;
    int persistableModeFlags = 0;
    int persistedModeFlags = 0;
    long persistedCreateTime = Long.MIN_VALUE;
    private static final long INVALID_TIME = Long.MIN_VALUE;
    private HashSet<UriPermissionOwner> mReadOwners;
    private HashSet<UriPermissionOwner> mWriteOwners;
    private String stringName;

    UriPermission(String sourcePkg, String targetPkg, int targetUid, Uri uri) {
        this.userHandle = UserHandle.getUserId(targetUid);
        this.sourcePkg = sourcePkg;
        this.targetPkg = targetPkg;
        this.targetUid = targetUid;
        this.uri = uri;
    }

    private void updateModeFlags() {
        this.modeFlags = this.ownedModeFlags | this.globalModeFlags | this.persistableModeFlags | this.persistedModeFlags;
    }

    void initPersistedModes(int modeFlags, long createdTime) {
        this.persistableModeFlags = modeFlags;
        this.persistedModeFlags = modeFlags;
        this.persistedCreateTime = createdTime;
        updateModeFlags();
    }

    void grantModes(int modeFlags, boolean persistable, UriPermissionOwner owner) {
        if (persistable) {
            this.persistableModeFlags |= modeFlags;
        }
        if (owner == null) {
            this.globalModeFlags |= modeFlags;
        } else {
            if ((modeFlags & 1) != 0) {
                addReadOwner(owner);
            }
            if ((modeFlags & 2) != 0) {
                addWriteOwner(owner);
            }
        }
        updateModeFlags();
    }

    boolean takePersistableModes(int modeFlags) {
        if ((modeFlags & this.persistableModeFlags) != modeFlags) {
            throw new SecurityException("Requested flags 0x" + Integer.toHexString(modeFlags) + ", but only 0x" + Integer.toHexString(this.persistableModeFlags) + " are allowed");
        }
        int before = this.persistedModeFlags;
        this.persistedModeFlags |= this.persistableModeFlags & modeFlags;
        if (this.persistedModeFlags != 0) {
            this.persistedCreateTime = System.currentTimeMillis();
        }
        updateModeFlags();
        return this.persistedModeFlags != before;
    }

    boolean releasePersistableModes(int modeFlags) {
        int before = this.persistedModeFlags;
        this.persistableModeFlags &= modeFlags ^ (-1);
        this.persistedModeFlags &= modeFlags ^ (-1);
        if (this.persistedModeFlags == 0) {
            this.persistedCreateTime = Long.MIN_VALUE;
        }
        updateModeFlags();
        return this.persistedModeFlags != before;
    }

    boolean clearModes(int modeFlags, boolean persistable) {
        int before = this.persistedModeFlags;
        if ((modeFlags & 1) != 0) {
            if (persistable) {
                this.persistableModeFlags &= -2;
                this.persistedModeFlags &= -2;
            }
            this.globalModeFlags &= -2;
            if (this.mReadOwners != null) {
                this.ownedModeFlags &= -2;
                Iterator i$ = this.mReadOwners.iterator();
                while (i$.hasNext()) {
                    UriPermissionOwner r = i$.next();
                    r.removeReadPermission(this);
                }
                this.mReadOwners = null;
            }
        }
        if ((modeFlags & 2) != 0) {
            if (persistable) {
                this.persistableModeFlags &= -3;
                this.persistedModeFlags &= -3;
            }
            this.globalModeFlags &= -3;
            if (this.mWriteOwners != null) {
                this.ownedModeFlags &= -3;
                Iterator i$2 = this.mWriteOwners.iterator();
                while (i$2.hasNext()) {
                    UriPermissionOwner r2 = i$2.next();
                    r2.removeWritePermission(this);
                }
                this.mWriteOwners = null;
            }
        }
        if (this.persistedModeFlags == 0) {
            this.persistedCreateTime = Long.MIN_VALUE;
        }
        updateModeFlags();
        return this.persistedModeFlags != before;
    }

    public int getStrength(int modeFlags) {
        if ((this.persistableModeFlags & modeFlags) == modeFlags) {
            return 3;
        }
        if ((this.globalModeFlags & modeFlags) == modeFlags) {
            return 2;
        }
        if ((this.ownedModeFlags & modeFlags) == modeFlags) {
            return 1;
        }
        return 0;
    }

    private void addReadOwner(UriPermissionOwner owner) {
        if (this.mReadOwners == null) {
            this.mReadOwners = Sets.newHashSet();
            this.ownedModeFlags |= 1;
            updateModeFlags();
        }
        if (this.mReadOwners.add(owner)) {
            owner.addReadPermission(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeReadOwner(UriPermissionOwner owner) {
        if (!this.mReadOwners.remove(owner)) {
            Log.wtf(TAG, "Unknown read owner " + owner + " in " + this);
        }
        if (this.mReadOwners.size() == 0) {
            this.mReadOwners = null;
            this.ownedModeFlags &= -2;
            updateModeFlags();
        }
    }

    private void addWriteOwner(UriPermissionOwner owner) {
        if (this.mWriteOwners == null) {
            this.mWriteOwners = Sets.newHashSet();
            this.ownedModeFlags |= 2;
            updateModeFlags();
        }
        if (this.mWriteOwners.add(owner)) {
            owner.addWritePermission(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeWriteOwner(UriPermissionOwner owner) {
        if (!this.mWriteOwners.remove(owner)) {
            Log.wtf(TAG, "Unknown write owner " + owner + " in " + this);
        }
        if (this.mWriteOwners.size() == 0) {
            this.mWriteOwners = null;
            this.ownedModeFlags &= -3;
            updateModeFlags();
        }
    }

    public String toString() {
        if (this.stringName != null) {
            return this.stringName;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("UriPermission{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.uri);
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }

    void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("userHandle=" + this.userHandle);
        pw.print(" sourcePkg=" + this.sourcePkg);
        pw.println(" targetPkg=" + this.targetPkg);
        pw.print(prefix);
        pw.print("mode=0x" + Integer.toHexString(this.modeFlags));
        pw.print(" owned=0x" + Integer.toHexString(this.ownedModeFlags));
        pw.print(" global=0x" + Integer.toHexString(this.globalModeFlags));
        pw.print(" persistable=0x" + Integer.toHexString(this.persistableModeFlags));
        pw.print(" persisted=0x" + Integer.toHexString(this.persistedModeFlags));
        if (this.persistedCreateTime != Long.MIN_VALUE) {
            pw.print(" persistedCreate=" + this.persistedCreateTime);
        }
        pw.println();
        if (this.mReadOwners != null) {
            pw.print(prefix);
            pw.println("readOwners:");
            Iterator i$ = this.mReadOwners.iterator();
            while (i$.hasNext()) {
                UriPermissionOwner owner = i$.next();
                pw.print(prefix);
                pw.println("  * " + owner);
            }
        }
        if (this.mWriteOwners != null) {
            pw.print(prefix);
            pw.println("writeOwners:");
            Iterator i$2 = this.mReadOwners.iterator();
            while (i$2.hasNext()) {
                UriPermissionOwner owner2 = i$2.next();
                pw.print(prefix);
                pw.println("  * " + owner2);
            }
        }
    }

    /* loaded from: UriPermission$PersistedTimeComparator.class */
    public static class PersistedTimeComparator implements Comparator<UriPermission> {
        @Override // java.util.Comparator
        public int compare(UriPermission lhs, UriPermission rhs) {
            return Long.compare(lhs.persistedCreateTime, rhs.persistedCreateTime);
        }
    }

    /* loaded from: UriPermission$Snapshot.class */
    public static class Snapshot {
        final int userHandle;
        final String sourcePkg;
        final String targetPkg;
        final Uri uri;
        final int persistedModeFlags;
        final long persistedCreateTime;

        private Snapshot(UriPermission perm) {
            this.userHandle = perm.userHandle;
            this.sourcePkg = perm.sourcePkg;
            this.targetPkg = perm.targetPkg;
            this.uri = perm.uri;
            this.persistedModeFlags = perm.persistedModeFlags;
            this.persistedCreateTime = perm.persistedCreateTime;
        }
    }

    public Snapshot snapshot() {
        return new Snapshot();
    }

    public android.content.UriPermission buildPersistedPublicApiObject() {
        return new android.content.UriPermission(this.uri, this.persistedModeFlags, this.persistedCreateTime);
    }
}