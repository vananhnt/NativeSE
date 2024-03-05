package com.android.server.content;

import android.accounts.Account;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import com.android.server.content.SyncStorageEngine;
import gov.nist.core.Separators;

/* loaded from: SyncOperation.class */
public class SyncOperation implements Comparable {
    public static final int REASON_BACKGROUND_DATA_SETTINGS_CHANGED = -1;
    public static final int REASON_ACCOUNTS_UPDATED = -2;
    public static final int REASON_SERVICE_CHANGED = -3;
    public static final int REASON_PERIODIC = -4;
    public static final int REASON_IS_SYNCABLE = -5;
    public static final int REASON_SYNC_AUTO = -6;
    public static final int REASON_MASTER_SYNC_AUTO = -7;
    public static final int REASON_USER_START = -8;
    private static String[] REASON_NAMES = {"DataSettingsChanged", "AccountsUpdated", "ServiceChanged", "Periodic", "IsSyncable", "AutoSync", "MasterSyncAuto", "UserStart"};
    public final Account account;
    public final String authority;
    public final ComponentName service;
    public final int userId;
    public final int reason;
    public int syncSource;
    public final boolean allowParallelSyncs;
    public Bundle extras;
    public final String key;
    public boolean expedited;
    public SyncStorageEngine.PendingOperation pendingOperation;
    public long latestRunTime;
    public Long backoff;
    public long delayUntil;
    public long effectiveRunTime;
    public long flexTime;

    public SyncOperation(Account account, int userId, int reason, int source, String authority, Bundle extras, long runTimeFromNow, long flexTime, long backoff, long delayUntil, boolean allowParallelSyncs) {
        this.service = null;
        this.account = account;
        this.authority = authority;
        this.userId = userId;
        this.reason = reason;
        this.syncSource = source;
        this.allowParallelSyncs = allowParallelSyncs;
        this.extras = new Bundle(extras);
        cleanBundle(this.extras);
        this.delayUntil = delayUntil;
        this.backoff = Long.valueOf(backoff);
        long now = SystemClock.elapsedRealtime();
        if (runTimeFromNow < 0 || isExpedited()) {
            this.expedited = true;
            this.latestRunTime = now;
            this.flexTime = 0L;
        } else {
            this.expedited = false;
            this.latestRunTime = now + runTimeFromNow;
            this.flexTime = flexTime;
        }
        updateEffectiveRunTime();
        this.key = toKey();
    }

    private void cleanBundle(Bundle bundle) {
        removeFalseExtra(bundle, ContentResolver.SYNC_EXTRAS_UPLOAD);
        removeFalseExtra(bundle, "force");
        removeFalseExtra(bundle, ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS);
        removeFalseExtra(bundle, ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF);
        removeFalseExtra(bundle, ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY);
        removeFalseExtra(bundle, ContentResolver.SYNC_EXTRAS_DISCARD_LOCAL_DELETIONS);
        removeFalseExtra(bundle, ContentResolver.SYNC_EXTRAS_EXPEDITED);
        removeFalseExtra(bundle, ContentResolver.SYNC_EXTRAS_OVERRIDE_TOO_MANY_DELETIONS);
        removeFalseExtra(bundle, ContentResolver.SYNC_EXTRAS_DISALLOW_METERED);
        bundle.remove(ContentResolver.SYNC_EXTRAS_EXPECTED_UPLOAD);
        bundle.remove(ContentResolver.SYNC_EXTRAS_EXPECTED_DOWNLOAD);
    }

    private void removeFalseExtra(Bundle bundle, String extraName) {
        if (!bundle.getBoolean(extraName, false)) {
            bundle.remove(extraName);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SyncOperation(SyncOperation other) {
        this.service = other.service;
        this.account = other.account;
        this.authority = other.authority;
        this.userId = other.userId;
        this.reason = other.reason;
        this.syncSource = other.syncSource;
        this.extras = new Bundle(other.extras);
        this.expedited = other.expedited;
        this.latestRunTime = SystemClock.elapsedRealtime();
        this.flexTime = 0L;
        this.backoff = other.backoff;
        this.allowParallelSyncs = other.allowParallelSyncs;
        updateEffectiveRunTime();
        this.key = toKey();
    }

    public String toString() {
        return dump(null, true);
    }

    public String dump(PackageManager pm, boolean useOneLine) {
        StringBuilder sb = new StringBuilder().append(this.account.name).append(" u").append(this.userId).append(" (").append(this.account.type).append(Separators.RPAREN).append(", ").append(this.authority).append(", ").append(SyncStorageEngine.SOURCES[this.syncSource]).append(", latestRunTime ").append(this.latestRunTime);
        if (this.expedited) {
            sb.append(", EXPEDITED");
        }
        sb.append(", reason: ");
        sb.append(reasonToString(pm, this.reason));
        if (!useOneLine && !this.extras.keySet().isEmpty()) {
            sb.append("\n    ");
            extrasToStringBuilder(this.extras, sb);
        }
        return sb.toString();
    }

    public static String reasonToString(PackageManager pm, int reason) {
        if (reason >= 0) {
            if (pm != null) {
                String[] packages = pm.getPackagesForUid(reason);
                if (packages != null && packages.length == 1) {
                    return packages[0];
                }
                String name = pm.getNameForUid(reason);
                if (name != null) {
                    return name;
                }
                return String.valueOf(reason);
            }
            return String.valueOf(reason);
        }
        int index = (-reason) - 1;
        if (index >= REASON_NAMES.length) {
            return String.valueOf(reason);
        }
        return REASON_NAMES[index];
    }

    public boolean isMeteredDisallowed() {
        return this.extras.getBoolean(ContentResolver.SYNC_EXTRAS_DISALLOW_METERED, false);
    }

    public boolean isInitialization() {
        return this.extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false);
    }

    public boolean isExpedited() {
        return this.extras.getBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, false) || this.expedited;
    }

    public boolean ignoreBackoff() {
        return this.extras.getBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, false);
    }

    private String toKey() {
        StringBuilder sb = new StringBuilder();
        if (this.service == null) {
            sb.append("authority: ").append(this.authority);
            sb.append(" account {name=" + this.account.name + ", user=" + this.userId + ", type=" + this.account.type + "}");
        } else {
            sb.append("service {package=").append(this.service.getPackageName()).append(" user=").append(this.userId).append(", class=").append(this.service.getClassName()).append("}");
        }
        sb.append(" extras: ");
        extrasToStringBuilder(this.extras, sb);
        return sb.toString();
    }

    public static void extrasToStringBuilder(Bundle bundle, StringBuilder sb) {
        sb.append("[");
        for (String key : bundle.keySet()) {
            sb.append(key).append(Separators.EQUALS).append(bundle.get(key)).append(Separators.SP);
        }
        sb.append("]");
    }

    public void updateEffectiveRunTime() {
        this.effectiveRunTime = ignoreBackoff() ? this.latestRunTime : Math.max(Math.max(this.latestRunTime, this.delayUntil), this.backoff.longValue());
    }

    @Override // java.lang.Comparable
    public int compareTo(Object o) {
        SyncOperation other = (SyncOperation) o;
        if (this.expedited != other.expedited) {
            return this.expedited ? -1 : 1;
        }
        long thisIntervalStart = Math.max(this.effectiveRunTime - this.flexTime, 0L);
        long otherIntervalStart = Math.max(other.effectiveRunTime - other.flexTime, 0L);
        if (thisIntervalStart < otherIntervalStart) {
            return -1;
        }
        if (otherIntervalStart < thisIntervalStart) {
            return 1;
        }
        return 0;
    }
}