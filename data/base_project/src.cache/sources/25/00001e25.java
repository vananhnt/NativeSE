package com.android.server.content;

import android.accounts.Account;
import android.content.SyncAdapterType;
import android.content.SyncAdaptersCache;
import android.content.pm.PackageManager;
import android.content.pm.RegisteredServicesCache;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import com.android.internal.telephony.IccCardConstants;
import com.android.server.content.SyncStorageEngine;
import com.google.android.collect.Maps;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: SyncQueue.class */
public class SyncQueue {
    private static final String TAG = "SyncManager";
    private final SyncStorageEngine mSyncStorageEngine;
    private final SyncAdaptersCache mSyncAdapters;
    private final PackageManager mPackageManager;
    private final HashMap<String, SyncOperation> mOperationsMap = Maps.newHashMap();

    public SyncQueue(PackageManager packageManager, SyncStorageEngine syncStorageEngine, SyncAdaptersCache syncAdapters) {
        this.mPackageManager = packageManager;
        this.mSyncStorageEngine = syncStorageEngine;
        this.mSyncAdapters = syncAdapters;
    }

    public void addPendingOperations(int userId) {
        Iterator i$ = this.mSyncStorageEngine.getPendingOperations().iterator();
        while (i$.hasNext()) {
            SyncStorageEngine.PendingOperation op = i$.next();
            if (op.userId == userId) {
                Pair<Long, Long> backoff = this.mSyncStorageEngine.getBackoff(op.account, op.userId, op.authority);
                RegisteredServicesCache.ServiceInfo<SyncAdapterType> syncAdapterInfo = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(op.authority, op.account.type), op.userId);
                if (syncAdapterInfo == null) {
                    Log.w(TAG, "Missing sync adapter info for authority " + op.authority + ", userId " + op.userId);
                } else {
                    SyncOperation syncOperation = new SyncOperation(op.account, op.userId, op.reason, op.syncSource, op.authority, op.extras, 0L, 0L, backoff != null ? backoff.first.longValue() : 0L, this.mSyncStorageEngine.getDelayUntilTime(op.account, op.userId, op.authority), syncAdapterInfo.type.allowParallelSyncs());
                    syncOperation.expedited = op.expedited;
                    syncOperation.pendingOperation = op;
                    add(syncOperation, op);
                }
            }
        }
    }

    public boolean add(SyncOperation operation) {
        return add(operation, null);
    }

    private boolean add(SyncOperation operation, SyncStorageEngine.PendingOperation pop) {
        String operationKey = operation.key;
        SyncOperation existingOperation = this.mOperationsMap.get(operationKey);
        if (existingOperation != null) {
            boolean changed = false;
            if (operation.compareTo(existingOperation) <= 0) {
                existingOperation.expedited = operation.expedited;
                long newRunTime = Math.min(existingOperation.latestRunTime, operation.latestRunTime);
                existingOperation.latestRunTime = newRunTime;
                existingOperation.flexTime = operation.flexTime;
                changed = true;
            }
            return changed;
        }
        operation.pendingOperation = pop;
        if (operation.pendingOperation == null) {
            SyncStorageEngine.PendingOperation pop2 = new SyncStorageEngine.PendingOperation(operation.account, operation.userId, operation.reason, operation.syncSource, operation.authority, operation.extras, operation.expedited);
            SyncStorageEngine.PendingOperation pop3 = this.mSyncStorageEngine.insertIntoPending(pop2);
            if (pop3 == null) {
                throw new IllegalStateException("error adding pending sync operation " + operation);
            }
            operation.pendingOperation = pop3;
        }
        this.mOperationsMap.put(operationKey, operation);
        return true;
    }

    public void removeUser(int userId) {
        ArrayList<SyncOperation> opsToRemove = new ArrayList<>();
        for (SyncOperation op : this.mOperationsMap.values()) {
            if (op.userId == userId) {
                opsToRemove.add(op);
            }
        }
        Iterator i$ = opsToRemove.iterator();
        while (i$.hasNext()) {
            remove(i$.next());
        }
    }

    public void remove(SyncOperation operation) {
        SyncOperation operationToRemove = this.mOperationsMap.remove(operation.key);
        if (operationToRemove != null && !this.mSyncStorageEngine.deleteFromPending(operationToRemove.pendingOperation)) {
            String errorMessage = "unable to find pending row for " + operationToRemove;
            Log.e(TAG, errorMessage, new IllegalStateException(errorMessage));
        }
    }

    public void onBackoffChanged(Account account, int userId, String providerName, long backoff) {
        for (SyncOperation op : this.mOperationsMap.values()) {
            if (op.account.equals(account) && op.authority.equals(providerName) && op.userId == userId) {
                op.backoff = Long.valueOf(backoff);
                op.updateEffectiveRunTime();
            }
        }
    }

    public void onDelayUntilTimeChanged(Account account, String providerName, long delayUntil) {
        for (SyncOperation op : this.mOperationsMap.values()) {
            if (op.account.equals(account) && op.authority.equals(providerName)) {
                op.delayUntil = delayUntil;
                op.updateEffectiveRunTime();
            }
        }
    }

    public void remove(Account account, int userId, String authority) {
        Iterator<Map.Entry<String, SyncOperation>> entries = this.mOperationsMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, SyncOperation> entry = entries.next();
            SyncOperation syncOperation = entry.getValue();
            if (account == null || syncOperation.account.equals(account)) {
                if (authority == null || syncOperation.authority.equals(authority)) {
                    if (userId == syncOperation.userId) {
                        entries.remove();
                        if (!this.mSyncStorageEngine.deleteFromPending(syncOperation.pendingOperation)) {
                            String errorMessage = "unable to find pending row for " + syncOperation;
                            Log.e(TAG, errorMessage, new IllegalStateException(errorMessage));
                        }
                    }
                }
            }
        }
    }

    public Collection<SyncOperation> getOperations() {
        return this.mOperationsMap.values();
    }

    public void dump(StringBuilder sb) {
        long now = SystemClock.elapsedRealtime();
        sb.append("SyncQueue: ").append(this.mOperationsMap.size()).append(" operation(s)\n");
        for (SyncOperation operation : this.mOperationsMap.values()) {
            sb.append("  ");
            if (operation.effectiveRunTime <= now) {
                sb.append(IccCardConstants.INTENT_VALUE_ICC_READY);
            } else {
                sb.append(DateUtils.formatElapsedTime((operation.effectiveRunTime - now) / 1000));
            }
            sb.append(" - ");
            sb.append(operation.dump(this.mPackageManager, false)).append(Separators.RETURN);
        }
    }
}