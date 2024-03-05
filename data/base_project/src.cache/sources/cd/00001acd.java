package com.android.internal.telephony;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Telephony;
import android.telephony.Rlog;
import android.util.TimedRemoteCaller;

/* loaded from: SmsStorageMonitor.class */
public final class SmsStorageMonitor extends Handler {
    private static final String TAG = "SmsStorageMonitor";
    private static final int EVENT_ICC_FULL = 1;
    private static final int EVENT_REPORT_MEMORY_STATUS_DONE = 2;
    private static final int EVENT_RADIO_ON = 3;
    private final Context mContext;
    private PowerManager.WakeLock mWakeLock;
    private boolean mReportMemoryStatusPending;
    final CommandsInterface mCi;
    private static final int WAKE_LOCK_TIMEOUT = 5000;
    boolean mStorageAvailable = true;
    private final BroadcastReceiver mResultReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.SmsStorageMonitor.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_DEVICE_STORAGE_FULL)) {
                SmsStorageMonitor.this.mStorageAvailable = false;
                SmsStorageMonitor.this.mCi.reportSmsMemoryStatus(false, SmsStorageMonitor.this.obtainMessage(2));
            } else if (intent.getAction().equals(Intent.ACTION_DEVICE_STORAGE_NOT_FULL)) {
                SmsStorageMonitor.this.mStorageAvailable = true;
                SmsStorageMonitor.this.mCi.reportSmsMemoryStatus(true, SmsStorageMonitor.this.obtainMessage(2));
            }
        }
    };

    public SmsStorageMonitor(PhoneBase phone) {
        this.mContext = phone.getContext();
        this.mCi = phone.mCi;
        createWakelock();
        this.mCi.setOnIccSmsFull(this, 1, null);
        this.mCi.registerForOn(this, 3, null);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_FULL);
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_NOT_FULL);
        this.mContext.registerReceiver(this.mResultReceiver, filter);
    }

    public void dispose() {
        this.mCi.unSetOnIccSmsFull(this);
        this.mCi.unregisterForOn(this);
        this.mContext.unregisterReceiver(this.mResultReceiver);
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                handleIccFull();
                return;
            case 2:
                AsyncResult ar = (AsyncResult) msg.obj;
                if (ar.exception != null) {
                    this.mReportMemoryStatusPending = true;
                    Rlog.v(TAG, "Memory status report to modem pending : mStorageAvailable = " + this.mStorageAvailable);
                    return;
                }
                this.mReportMemoryStatusPending = false;
                return;
            case 3:
                if (this.mReportMemoryStatusPending) {
                    Rlog.v(TAG, "Sending pending memory status report : mStorageAvailable = " + this.mStorageAvailable);
                    this.mCi.reportSmsMemoryStatus(this.mStorageAvailable, obtainMessage(2));
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void createWakelock() {
        PowerManager pm = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(1, TAG);
        this.mWakeLock.setReferenceCounted(true);
    }

    private void handleIccFull() {
        Intent intent = new Intent(Telephony.Sms.Intents.SIM_FULL_ACTION);
        this.mWakeLock.acquire(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
        this.mContext.sendBroadcast(intent, Manifest.permission.RECEIVE_SMS);
    }

    public boolean isStorageAvailable() {
        return this.mStorageAvailable;
    }
}