package com.android.server;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.text.format.Formatter;
import android.util.EventLog;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.R;
import com.android.server.location.LocationFudger;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* loaded from: DeviceStorageMonitorService.class */
public class DeviceStorageMonitorService extends Binder {
    private static final String TAG = "DeviceStorageMonitorService";
    private static final boolean DEBUG = false;
    private static final boolean localLOGV = false;
    private static final int DEVICE_MEMORY_WHAT = 1;
    private static final int MONITOR_INTERVAL = 1;
    private static final int LOW_MEMORY_NOTIFICATION_ID = 1;
    private static final int DEFAULT_FREE_STORAGE_LOG_INTERVAL_IN_MINUTES = 720;
    private static final long DEFAULT_DISK_FREE_CHANGE_REPORTING_THRESHOLD = 2097152;
    private static final long DEFAULT_CHECK_INTERVAL = 60000;
    private long mFreeMem;
    private long mFreeMemAfterLastCacheClear;
    private long mLastReportedFreeMem;
    private Context mContext;
    private ContentResolver mResolver;
    private static final File DATA_PATH = Environment.getDataDirectory();
    private static final File SYSTEM_PATH = Environment.getRootDirectory();
    private static final File CACHE_PATH = Environment.getDownloadCacheDirectory();
    private boolean mClearingCache;
    private Intent mStorageOkIntent;
    private Intent mStorageFullIntent;
    private Intent mStorageNotFullIntent;
    private CachePackageDataObserver mClearCacheObserver;
    private final CacheFileDeletedObserver mCacheFileDeletedObserver;
    private static final int _TRUE = 1;
    private static final int _FALSE = 0;
    private long mMemLowThreshold;
    private long mMemCacheStartTrimThreshold;
    private long mMemCacheTrimToThreshold;
    private long mMemFullThreshold;
    public static final String SERVICE = "devicestoragemonitor";
    private boolean mLowMemFlag = false;
    private boolean mMemFullFlag = false;
    private long mThreadStartTime = -1;
    private boolean mClearSucceeded = false;
    Handler mHandler = new Handler() { // from class: com.android.server.DeviceStorageMonitorService.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                DeviceStorageMonitorService.this.checkMemory(msg.arg1 == 1);
            } else {
                Slog.e(DeviceStorageMonitorService.TAG, "Will not process invalid message");
            }
        }
    };
    private long mLastReportedFreeMemTime = 0;
    private StatFs mDataFileStats = new StatFs(DATA_PATH.getAbsolutePath());
    private StatFs mSystemFileStats = new StatFs(SYSTEM_PATH.getAbsolutePath());
    private StatFs mCacheFileStats = new StatFs(CACHE_PATH.getAbsolutePath());
    private long mTotalMemory = this.mDataFileStats.getBlockCount() * this.mDataFileStats.getBlockSize();
    private Intent mStorageLowIntent = new Intent(Intent.ACTION_DEVICE_STORAGE_LOW);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: DeviceStorageMonitorService$CachePackageDataObserver.class */
    public class CachePackageDataObserver extends IPackageDataObserver.Stub {
        CachePackageDataObserver() {
        }

        @Override // android.content.pm.IPackageDataObserver
        public void onRemoveCompleted(String packageName, boolean succeeded) {
            DeviceStorageMonitorService.this.mClearSucceeded = succeeded;
            DeviceStorageMonitorService.this.mClearingCache = false;
            DeviceStorageMonitorService.this.postCheckMemoryMsg(false, 0L);
        }
    }

    private final void restatDataDir() {
        try {
            this.mDataFileStats.restat(DATA_PATH.getAbsolutePath());
            this.mFreeMem = this.mDataFileStats.getAvailableBlocks() * this.mDataFileStats.getBlockSize();
        } catch (IllegalArgumentException e) {
        }
        String debugFreeMem = SystemProperties.get("debug.freemem");
        if (!"".equals(debugFreeMem)) {
            this.mFreeMem = Long.parseLong(debugFreeMem);
        }
        long freeMemLogInterval = Settings.Global.getLong(this.mResolver, Settings.Global.SYS_FREE_STORAGE_LOG_INTERVAL, 720L) * 60 * 1000;
        long currTime = SystemClock.elapsedRealtime();
        if (this.mLastReportedFreeMemTime == 0 || currTime - this.mLastReportedFreeMemTime >= freeMemLogInterval) {
            this.mLastReportedFreeMemTime = currTime;
            long mFreeSystem = -1;
            long mFreeCache = -1;
            try {
                this.mSystemFileStats.restat(SYSTEM_PATH.getAbsolutePath());
                mFreeSystem = this.mSystemFileStats.getAvailableBlocks() * this.mSystemFileStats.getBlockSize();
            } catch (IllegalArgumentException e2) {
            }
            try {
                this.mCacheFileStats.restat(CACHE_PATH.getAbsolutePath());
                mFreeCache = this.mCacheFileStats.getAvailableBlocks() * this.mCacheFileStats.getBlockSize();
            } catch (IllegalArgumentException e3) {
            }
            EventLog.writeEvent((int) EventLogTags.FREE_STORAGE_LEFT, Long.valueOf(this.mFreeMem), Long.valueOf(mFreeSystem), Long.valueOf(mFreeCache));
        }
        long threshold = Settings.Global.getLong(this.mResolver, Settings.Global.DISK_FREE_CHANGE_REPORTING_THRESHOLD, DEFAULT_DISK_FREE_CHANGE_REPORTING_THRESHOLD);
        long delta = this.mFreeMem - this.mLastReportedFreeMem;
        if (delta > threshold || delta < (-threshold)) {
            this.mLastReportedFreeMem = this.mFreeMem;
            EventLog.writeEvent((int) EventLogTags.FREE_STORAGE_CHANGED, this.mFreeMem);
        }
    }

    private final void clearCache() {
        if (this.mClearCacheObserver == null) {
            this.mClearCacheObserver = new CachePackageDataObserver();
        }
        this.mClearingCache = true;
        try {
            IPackageManager.Stub.asInterface(ServiceManager.getService(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME)).freeStorageAndNotify(this.mMemCacheTrimToThreshold, this.mClearCacheObserver);
        } catch (RemoteException e) {
            Slog.w(TAG, "Failed to get handle for PackageManger Exception: " + e);
            this.mClearingCache = false;
            this.mClearSucceeded = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void checkMemory(boolean checkCache) {
        if (this.mClearingCache) {
            long diffTime = System.currentTimeMillis() - this.mThreadStartTime;
            if (diffTime > LocationFudger.FASTEST_INTERVAL_MS) {
                Slog.w(TAG, "Thread that clears cache file seems to run for ever");
            }
        } else {
            restatDataDir();
            if (this.mFreeMem < this.mMemLowThreshold) {
                if (checkCache) {
                    if (this.mFreeMem < this.mMemCacheStartTrimThreshold && this.mFreeMemAfterLastCacheClear - this.mFreeMem >= (this.mMemLowThreshold - this.mMemCacheStartTrimThreshold) / 4) {
                        this.mThreadStartTime = System.currentTimeMillis();
                        this.mClearSucceeded = false;
                        clearCache();
                    }
                } else {
                    this.mFreeMemAfterLastCacheClear = this.mFreeMem;
                    if (!this.mLowMemFlag) {
                        Slog.i(TAG, "Running low on memory. Sending notification");
                        sendNotification();
                        this.mLowMemFlag = true;
                    }
                }
            } else {
                this.mFreeMemAfterLastCacheClear = this.mFreeMem;
                if (this.mLowMemFlag) {
                    Slog.i(TAG, "Memory available. Cancelling notification");
                    cancelNotification();
                    this.mLowMemFlag = false;
                }
            }
            if (this.mFreeMem < this.mMemFullThreshold) {
                if (!this.mMemFullFlag) {
                    sendFullNotification();
                    this.mMemFullFlag = true;
                }
            } else if (this.mMemFullFlag) {
                cancelFullNotification();
                this.mMemFullFlag = false;
            }
        }
        postCheckMemoryMsg(true, 60000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postCheckMemoryMsg(boolean clearCache, long delay) {
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, clearCache ? 1 : 0, 0), delay);
    }

    public DeviceStorageMonitorService(Context context) {
        this.mContext = context;
        this.mResolver = this.mContext.getContentResolver();
        this.mStorageLowIntent.addFlags(67108864);
        this.mStorageOkIntent = new Intent(Intent.ACTION_DEVICE_STORAGE_OK);
        this.mStorageOkIntent.addFlags(67108864);
        this.mStorageFullIntent = new Intent(Intent.ACTION_DEVICE_STORAGE_FULL);
        this.mStorageFullIntent.addFlags(67108864);
        this.mStorageNotFullIntent = new Intent(Intent.ACTION_DEVICE_STORAGE_NOT_FULL);
        this.mStorageNotFullIntent.addFlags(67108864);
        StorageManager sm = StorageManager.from(context);
        this.mMemLowThreshold = sm.getStorageLowBytes(DATA_PATH);
        this.mMemFullThreshold = sm.getStorageFullBytes(DATA_PATH);
        this.mMemCacheStartTrimThreshold = ((this.mMemLowThreshold * 3) + this.mMemFullThreshold) / 4;
        this.mMemCacheTrimToThreshold = this.mMemLowThreshold + ((this.mMemLowThreshold - this.mMemCacheStartTrimThreshold) * 2);
        this.mFreeMemAfterLastCacheClear = this.mTotalMemory;
        checkMemory(true);
        this.mCacheFileDeletedObserver = new CacheFileDeletedObserver();
        this.mCacheFileDeletedObserver.startWatching();
    }

    private final void sendNotification() {
        EventLog.writeEvent((int) EventLogTags.LOW_STORAGE, this.mFreeMem);
        Intent lowMemIntent = new Intent(Environment.isExternalStorageEmulated() ? Settings.ACTION_INTERNAL_STORAGE_SETTINGS : Intent.ACTION_MANAGE_PACKAGE_STORAGE);
        lowMemIntent.putExtra("memory", this.mFreeMem);
        lowMemIntent.addFlags(268435456);
        NotificationManager mNotificationMgr = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence title = this.mContext.getText(R.string.low_internal_storage_view_title);
        CharSequence details = this.mContext.getText(R.string.low_internal_storage_view_text);
        PendingIntent intent = PendingIntent.getActivityAsUser(this.mContext, 0, lowMemIntent, 0, null, UserHandle.CURRENT);
        Notification notification = new Notification();
        notification.icon = R.drawable.stat_notify_disk_full;
        notification.tickerText = title;
        notification.flags |= 32;
        notification.setLatestEventInfo(this.mContext, title, details, intent);
        mNotificationMgr.notifyAsUser(null, 1, notification, UserHandle.ALL);
        this.mContext.sendStickyBroadcastAsUser(this.mStorageLowIntent, UserHandle.ALL);
    }

    private final void cancelNotification() {
        NotificationManager mNotificationMgr = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationMgr.cancelAsUser(null, 1, UserHandle.ALL);
        this.mContext.removeStickyBroadcastAsUser(this.mStorageLowIntent, UserHandle.ALL);
        this.mContext.sendBroadcastAsUser(this.mStorageOkIntent, UserHandle.ALL);
    }

    private final void sendFullNotification() {
        this.mContext.sendStickyBroadcastAsUser(this.mStorageFullIntent, UserHandle.ALL);
    }

    private final void cancelFullNotification() {
        this.mContext.removeStickyBroadcastAsUser(this.mStorageFullIntent, UserHandle.ALL);
        this.mContext.sendBroadcastAsUser(this.mStorageNotFullIntent, UserHandle.ALL);
    }

    public void updateMemory() {
        int callingUid = getCallingUid();
        if (callingUid != 1000) {
            return;
        }
        postCheckMemoryMsg(true, 0L);
    }

    public long getMemoryLowThreshold() {
        return this.mMemLowThreshold;
    }

    public boolean isMemoryLow() {
        return this.mLowMemFlag;
    }

    /* loaded from: DeviceStorageMonitorService$CacheFileDeletedObserver.class */
    public static class CacheFileDeletedObserver extends FileObserver {
        public CacheFileDeletedObserver() {
            super(Environment.getDownloadCacheDirectory().getAbsolutePath(), 512);
        }

        @Override // android.os.FileObserver
        public void onEvent(int event, String path) {
            EventLogTags.writeCacheFileDeleted(path);
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump devicestoragemonitor from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("Current DeviceStorageMonitor state:");
        pw.print("  mFreeMem=");
        pw.print(Formatter.formatFileSize(this.mContext, this.mFreeMem));
        pw.print(" mTotalMemory=");
        pw.println(Formatter.formatFileSize(this.mContext, this.mTotalMemory));
        pw.print("  mFreeMemAfterLastCacheClear=");
        pw.println(Formatter.formatFileSize(this.mContext, this.mFreeMemAfterLastCacheClear));
        pw.print("  mLastReportedFreeMem=");
        pw.print(Formatter.formatFileSize(this.mContext, this.mLastReportedFreeMem));
        pw.print(" mLastReportedFreeMemTime=");
        TimeUtils.formatDuration(this.mLastReportedFreeMemTime, SystemClock.elapsedRealtime(), pw);
        pw.println();
        pw.print("  mLowMemFlag=");
        pw.print(this.mLowMemFlag);
        pw.print(" mMemFullFlag=");
        pw.println(this.mMemFullFlag);
        pw.print("  mClearSucceeded=");
        pw.print(this.mClearSucceeded);
        pw.print(" mClearingCache=");
        pw.println(this.mClearingCache);
        pw.print("  mMemLowThreshold=");
        pw.print(Formatter.formatFileSize(this.mContext, this.mMemLowThreshold));
        pw.print(" mMemFullThreshold=");
        pw.println(Formatter.formatFileSize(this.mContext, this.mMemFullThreshold));
        pw.print("  mMemCacheStartTrimThreshold=");
        pw.print(Formatter.formatFileSize(this.mContext, this.mMemCacheStartTrimThreshold));
        pw.print(" mMemCacheTrimToThreshold=");
        pw.println(Formatter.formatFileSize(this.mContext, this.mMemCacheTrimToThreshold));
    }
}