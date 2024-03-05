package android.content;

import android.accounts.Account;
import android.content.ISyncAdapter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.Trace;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: AbstractThreadedSyncAdapter.class */
public abstract class AbstractThreadedSyncAdapter {
    @Deprecated
    public static final int LOG_SYNC_DETAILS = 2743;
    private final Context mContext;
    private final AtomicInteger mNumSyncStarts;
    private final ISyncAdapterImpl mISyncAdapterImpl;
    private final HashMap<Account, SyncThread> mSyncThreads;
    private final Object mSyncThreadLock;
    private final boolean mAutoInitialize;
    private boolean mAllowParallelSyncs;

    public abstract void onPerformSync(Account account, Bundle bundle, String str, ContentProviderClient contentProviderClient, SyncResult syncResult);

    public AbstractThreadedSyncAdapter(Context context, boolean autoInitialize) {
        this(context, autoInitialize, false);
    }

    public AbstractThreadedSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        this.mSyncThreads = new HashMap<>();
        this.mSyncThreadLock = new Object();
        this.mContext = context;
        this.mISyncAdapterImpl = new ISyncAdapterImpl();
        this.mNumSyncStarts = new AtomicInteger(0);
        this.mAutoInitialize = autoInitialize;
        this.mAllowParallelSyncs = allowParallelSyncs;
    }

    public Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Account toSyncKey(Account account) {
        if (this.mAllowParallelSyncs) {
            return account;
        }
        return null;
    }

    /* loaded from: AbstractThreadedSyncAdapter$ISyncAdapterImpl.class */
    private class ISyncAdapterImpl extends ISyncAdapter.Stub {
        private ISyncAdapterImpl() {
        }

        @Override // android.content.ISyncAdapter
        public void startSync(ISyncContext syncContext, String authority, Account account, Bundle extras) {
            boolean alreadyInProgress;
            SyncContext syncContextClient = new SyncContext(syncContext);
            Account threadsKey = AbstractThreadedSyncAdapter.this.toSyncKey(account);
            synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                if (!AbstractThreadedSyncAdapter.this.mSyncThreads.containsKey(threadsKey)) {
                    if (AbstractThreadedSyncAdapter.this.mAutoInitialize && extras != null && extras.getBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, false)) {
                        if (ContentResolver.getIsSyncable(account, authority) < 0) {
                            ContentResolver.setIsSyncable(account, authority, 1);
                        }
                        syncContextClient.onFinished(new SyncResult());
                        return;
                    }
                    SyncThread syncThread = new SyncThread("SyncAdapterThread-" + AbstractThreadedSyncAdapter.this.mNumSyncStarts.incrementAndGet(), syncContextClient, authority, account, extras);
                    AbstractThreadedSyncAdapter.this.mSyncThreads.put(threadsKey, syncThread);
                    syncThread.start();
                    alreadyInProgress = false;
                } else {
                    alreadyInProgress = true;
                }
                if (alreadyInProgress) {
                    syncContextClient.onFinished(SyncResult.ALREADY_IN_PROGRESS);
                }
            }
        }

        @Override // android.content.ISyncAdapter
        public void cancelSync(ISyncContext syncContext) {
            SyncThread info = null;
            synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                Iterator i$ = AbstractThreadedSyncAdapter.this.mSyncThreads.values().iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    SyncThread current = (SyncThread) i$.next();
                    if (current.mSyncContext.getSyncContextBinder() == syncContext.asBinder()) {
                        info = current;
                        break;
                    }
                }
            }
            if (info != null) {
                if (AbstractThreadedSyncAdapter.this.mAllowParallelSyncs) {
                    AbstractThreadedSyncAdapter.this.onSyncCanceled(info);
                } else {
                    AbstractThreadedSyncAdapter.this.onSyncCanceled();
                }
            }
        }

        @Override // android.content.ISyncAdapter
        public void initialize(Account account, String authority) throws RemoteException {
            Bundle extras = new Bundle();
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
            startSync(null, authority, account, extras);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AbstractThreadedSyncAdapter$SyncThread.class */
    public class SyncThread extends Thread {
        private final SyncContext mSyncContext;
        private final String mAuthority;
        private final Account mAccount;
        private final Bundle mExtras;
        private final Account mThreadsKey;

        private SyncThread(String name, SyncContext syncContext, String authority, Account account, Bundle extras) {
            super(name);
            this.mSyncContext = syncContext;
            this.mAuthority = authority;
            this.mAccount = account;
            this.mExtras = extras;
            this.mThreadsKey = AbstractThreadedSyncAdapter.this.toSyncKey(account);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Process.setThreadPriority(10);
            Trace.traceBegin(128L, this.mAuthority);
            SyncResult syncResult = new SyncResult();
            ContentProviderClient provider = null;
            try {
                if (!isCanceled()) {
                    ContentProviderClient provider2 = AbstractThreadedSyncAdapter.this.mContext.getContentResolver().acquireContentProviderClient(this.mAuthority);
                    if (provider2 != null) {
                        AbstractThreadedSyncAdapter.this.onPerformSync(this.mAccount, this.mExtras, this.mAuthority, provider2, syncResult);
                    } else {
                        syncResult.databaseError = true;
                    }
                    Trace.traceEnd(128L);
                    if (provider2 != null) {
                        provider2.release();
                    }
                    if (!isCanceled()) {
                        this.mSyncContext.onFinished(syncResult);
                    }
                    synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                        AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                    }
                    return;
                }
                Trace.traceEnd(128L);
                if (0 != 0) {
                    provider.release();
                }
                if (!isCanceled()) {
                    this.mSyncContext.onFinished(syncResult);
                }
                synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                    AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                }
            } catch (Throwable th) {
                Trace.traceEnd(128L);
                if (0 != 0) {
                    provider.release();
                }
                if (!isCanceled()) {
                    this.mSyncContext.onFinished(syncResult);
                }
                synchronized (AbstractThreadedSyncAdapter.this.mSyncThreadLock) {
                    AbstractThreadedSyncAdapter.this.mSyncThreads.remove(this.mThreadsKey);
                    throw th;
                }
            }
        }

        private boolean isCanceled() {
            return Thread.currentThread().isInterrupted();
        }
    }

    public final IBinder getSyncAdapterBinder() {
        return this.mISyncAdapterImpl.asBinder();
    }

    public void onSyncCanceled() {
        SyncThread syncThread;
        synchronized (this.mSyncThreadLock) {
            syncThread = this.mSyncThreads.get(null);
        }
        if (syncThread != null) {
            syncThread.interrupt();
        }
    }

    public void onSyncCanceled(Thread thread) {
        thread.interrupt();
    }
}