package com.android.server.am;

import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.IContentProvider;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ContentProviderRecord.class */
public final class ContentProviderRecord {
    final ActivityManagerService service;
    public final ProviderInfo info;
    final int uid;
    final ApplicationInfo appInfo;
    final ComponentName name;
    final boolean singleton;
    public IContentProvider provider;
    public boolean noReleaseNeeded;
    final ArrayList<ContentProviderConnection> connections = new ArrayList<>();
    HashMap<IBinder, ExternalProcessHandle> externalProcessTokenToHandle;
    int externalProcessNoHandleCount;
    ProcessRecord proc;
    ProcessRecord launchingApp;
    String stringName;
    String shortStringName;

    public ContentProviderRecord(ActivityManagerService _service, ProviderInfo _info, ApplicationInfo ai, ComponentName _name, boolean _singleton) {
        this.service = _service;
        this.info = _info;
        this.uid = ai.uid;
        this.appInfo = ai;
        this.name = _name;
        this.singleton = _singleton;
        this.noReleaseNeeded = this.uid == 0 || this.uid == 1000;
    }

    public ContentProviderRecord(ContentProviderRecord cpr) {
        this.service = cpr.service;
        this.info = cpr.info;
        this.uid = cpr.uid;
        this.appInfo = cpr.appInfo;
        this.name = cpr.name;
        this.singleton = cpr.singleton;
        this.noReleaseNeeded = cpr.noReleaseNeeded;
    }

    public IActivityManager.ContentProviderHolder newHolder(ContentProviderConnection conn) {
        IActivityManager.ContentProviderHolder holder = new IActivityManager.ContentProviderHolder(this.info);
        holder.provider = this.provider;
        holder.noReleaseNeeded = this.noReleaseNeeded;
        holder.connection = conn;
        return holder;
    }

    public boolean canRunHere(ProcessRecord app) {
        return (this.info.multiprocess || this.info.processName.equals(app.processName)) && this.uid == app.info.uid;
    }

    public void addExternalProcessHandleLocked(IBinder token) {
        if (token == null) {
            this.externalProcessNoHandleCount++;
            return;
        }
        if (this.externalProcessTokenToHandle == null) {
            this.externalProcessTokenToHandle = new HashMap<>();
        }
        ExternalProcessHandle handle = this.externalProcessTokenToHandle.get(token);
        if (handle == null) {
            handle = new ExternalProcessHandle(token);
            this.externalProcessTokenToHandle.put(token, handle);
        }
        ExternalProcessHandle.access$008(handle);
    }

    public boolean removeExternalProcessHandleLocked(IBinder token) {
        ExternalProcessHandle handle;
        if (hasExternalProcessHandles()) {
            boolean hasHandle = false;
            if (this.externalProcessTokenToHandle != null && (handle = this.externalProcessTokenToHandle.get(token)) != null) {
                hasHandle = true;
                ExternalProcessHandle.access$010(handle);
                if (handle.mAcquisitionCount == 0) {
                    removeExternalProcessHandleInternalLocked(token);
                    return true;
                }
            }
            if (!hasHandle) {
                this.externalProcessNoHandleCount--;
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeExternalProcessHandleInternalLocked(IBinder token) {
        ExternalProcessHandle handle = this.externalProcessTokenToHandle.get(token);
        handle.unlinkFromOwnDeathLocked();
        this.externalProcessTokenToHandle.remove(token);
        if (this.externalProcessTokenToHandle.size() == 0) {
            this.externalProcessTokenToHandle = null;
        }
    }

    public boolean hasExternalProcessHandles() {
        return this.externalProcessTokenToHandle != null || this.externalProcessNoHandleCount > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix, boolean full) {
        if (full) {
            pw.print(prefix);
            pw.print("package=");
            pw.print(this.info.applicationInfo.packageName);
            pw.print(" process=");
            pw.println(this.info.processName);
        }
        pw.print(prefix);
        pw.print("proc=");
        pw.println(this.proc);
        if (this.launchingApp != null) {
            pw.print(prefix);
            pw.print("launchingApp=");
            pw.println(this.launchingApp);
        }
        if (full) {
            pw.print(prefix);
            pw.print("uid=");
            pw.print(this.uid);
            pw.print(" provider=");
            pw.println(this.provider);
        }
        if (this.singleton) {
            pw.print(prefix);
            pw.print("singleton=");
            pw.println(this.singleton);
        }
        pw.print(prefix);
        pw.print("authority=");
        pw.println(this.info.authority);
        if (full && (this.info.isSyncable || this.info.multiprocess || this.info.initOrder != 0)) {
            pw.print(prefix);
            pw.print("isSyncable=");
            pw.print(this.info.isSyncable);
            pw.print(" multiprocess=");
            pw.print(this.info.multiprocess);
            pw.print(" initOrder=");
            pw.println(this.info.initOrder);
        }
        if (full) {
            if (hasExternalProcessHandles()) {
                pw.print(prefix);
                pw.print("externals=");
                pw.println(this.externalProcessTokenToHandle.size());
            }
        } else if (this.connections.size() > 0 || this.externalProcessNoHandleCount > 0) {
            pw.print(prefix);
            pw.print(this.connections.size());
            pw.print(" connections, ");
            pw.print(this.externalProcessNoHandleCount);
            pw.println(" external handles");
        }
        if (this.connections.size() > 0) {
            if (full) {
                pw.print(prefix);
                pw.println("Connections:");
            }
            for (int i = 0; i < this.connections.size(); i++) {
                ContentProviderConnection conn = this.connections.get(i);
                pw.print(prefix);
                pw.print("  -> ");
                pw.println(conn.toClientString());
                if (conn.provider != this) {
                    pw.print(prefix);
                    pw.print("    *** WRONG PROVIDER: ");
                    pw.println(conn.provider);
                }
            }
        }
    }

    public String toString() {
        if (this.stringName != null) {
            return this.stringName;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("ContentProviderRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" u");
        sb.append(UserHandle.getUserId(this.uid));
        sb.append(' ');
        sb.append(this.name.flattenToShortString());
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }

    public String toShortString() {
        if (this.shortStringName != null) {
            return this.shortStringName;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append('/');
        sb.append(this.name.flattenToShortString());
        String sb2 = sb.toString();
        this.shortStringName = sb2;
        return sb2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ContentProviderRecord$ExternalProcessHandle.class */
    public class ExternalProcessHandle implements IBinder.DeathRecipient {
        private static final String LOG_TAG = "ExternalProcessHanldle";
        private final IBinder mToken;
        private int mAcquisitionCount;

        static /* synthetic */ int access$008(ExternalProcessHandle x0) {
            int i = x0.mAcquisitionCount;
            x0.mAcquisitionCount = i + 1;
            return i;
        }

        static /* synthetic */ int access$010(ExternalProcessHandle x0) {
            int i = x0.mAcquisitionCount;
            x0.mAcquisitionCount = i - 1;
            return i;
        }

        public ExternalProcessHandle(IBinder token) {
            this.mToken = token;
            try {
                token.linkToDeath(this, 0);
            } catch (RemoteException re) {
                Slog.e(LOG_TAG, "Couldn't register for death for token: " + this.mToken, re);
            }
        }

        public void unlinkFromOwnDeathLocked() {
            this.mToken.unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (ContentProviderRecord.this.service) {
                if (ContentProviderRecord.this.hasExternalProcessHandles() && ContentProviderRecord.this.externalProcessTokenToHandle.get(this.mToken) != null) {
                    ContentProviderRecord.this.removeExternalProcessHandleInternalLocked(this.mToken);
                }
            }
        }
    }
}