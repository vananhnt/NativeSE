package com.android.server.am;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: UriPermissionOwner.class */
public final class UriPermissionOwner {
    final ActivityManagerService service;
    final Object owner;
    Binder externalToken;
    HashSet<UriPermission> readUriPermissions;
    HashSet<UriPermission> writeUriPermissions;

    /* loaded from: UriPermissionOwner$ExternalToken.class */
    class ExternalToken extends Binder {
        ExternalToken() {
        }

        UriPermissionOwner getOwner() {
            return UriPermissionOwner.this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UriPermissionOwner(ActivityManagerService _service, Object _owner) {
        this.service = _service;
        this.owner = _owner;
    }

    Binder getExternalTokenLocked() {
        if (this.externalToken == null) {
            this.externalToken = new ExternalToken();
        }
        return this.externalToken;
    }

    static UriPermissionOwner fromExternalToken(IBinder token) {
        if (token instanceof ExternalToken) {
            return ((ExternalToken) token).getOwner();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeUriPermissionsLocked() {
        removeUriPermissionsLocked(3);
    }

    void removeUriPermissionsLocked(int mode) {
        if ((mode & 1) != 0 && this.readUriPermissions != null) {
            Iterator i$ = this.readUriPermissions.iterator();
            while (i$.hasNext()) {
                UriPermission perm = i$.next();
                perm.removeReadOwner(this);
                this.service.removeUriPermissionIfNeededLocked(perm);
            }
            this.readUriPermissions = null;
        }
        if ((mode & 2) != 0 && this.writeUriPermissions != null) {
            Iterator i$2 = this.writeUriPermissions.iterator();
            while (i$2.hasNext()) {
                UriPermission perm2 = i$2.next();
                perm2.removeWriteOwner(this);
                this.service.removeUriPermissionIfNeededLocked(perm2);
            }
            this.writeUriPermissions = null;
        }
    }

    void removeUriPermissionLocked(Uri uri, int mode) {
        if ((mode & 1) != 0 && this.readUriPermissions != null) {
            Iterator<UriPermission> it = this.readUriPermissions.iterator();
            while (it.hasNext()) {
                UriPermission perm = it.next();
                if (uri.equals(perm.uri)) {
                    perm.removeReadOwner(this);
                    this.service.removeUriPermissionIfNeededLocked(perm);
                    it.remove();
                }
            }
            if (this.readUriPermissions.size() == 0) {
                this.readUriPermissions = null;
            }
        }
        if ((mode & 2) != 0 && this.writeUriPermissions != null) {
            Iterator<UriPermission> it2 = this.writeUriPermissions.iterator();
            while (it2.hasNext()) {
                UriPermission perm2 = it2.next();
                if (uri.equals(perm2.uri)) {
                    perm2.removeWriteOwner(this);
                    this.service.removeUriPermissionIfNeededLocked(perm2);
                    it2.remove();
                }
            }
            if (this.writeUriPermissions.size() == 0) {
                this.writeUriPermissions = null;
            }
        }
    }

    public void addReadPermission(UriPermission perm) {
        if (this.readUriPermissions == null) {
            this.readUriPermissions = new HashSet<>();
        }
        this.readUriPermissions.add(perm);
    }

    public void addWritePermission(UriPermission perm) {
        if (this.writeUriPermissions == null) {
            this.writeUriPermissions = new HashSet<>();
        }
        this.writeUriPermissions.add(perm);
    }

    public void removeReadPermission(UriPermission perm) {
        this.readUriPermissions.remove(perm);
        if (this.readUriPermissions.size() == 0) {
            this.readUriPermissions = null;
        }
    }

    public void removeWritePermission(UriPermission perm) {
        this.writeUriPermissions.remove(perm);
        if (this.writeUriPermissions.size() == 0) {
            this.writeUriPermissions = null;
        }
    }

    public String toString() {
        return this.owner.toString();
    }
}