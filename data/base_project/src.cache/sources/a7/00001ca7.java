package com.android.server;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.NtpTrustedTime;
import android.util.TrustedTime;
import com.android.internal.R;
import com.android.internal.telephony.TelephonyIntents;

/* loaded from: NetworkTimeUpdateService.class */
public class NetworkTimeUpdateService {
    private static final String TAG = "NetworkTimeUpdateService";
    private static final boolean DBG = false;
    private static final int EVENT_AUTO_TIME_CHANGED = 1;
    private static final int EVENT_POLL_NETWORK_TIME = 2;
    private static final int EVENT_NETWORK_CONNECTED = 3;
    private static final String ACTION_POLL = "com.android.server.NetworkTimeUpdateService.action.POLL";
    private static int POLL_REQUEST = 0;
    private static final long NOT_SET = -1;
    private Context mContext;
    private TrustedTime mTime;
    private Handler mHandler;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingPollIntent;
    private SettingsObserver mSettingsObserver;
    private final long mPollingIntervalMs;
    private final long mPollingIntervalShorterMs;
    private final int mTryAgainTimesMax;
    private final int mTimeErrorThresholdMs;
    private int mTryAgainCounter;
    private long mNitzTimeSetTime = -1;
    private long mNitzZoneSetTime = -1;
    private long mLastNtpFetchTime = -1;
    private BroadcastReceiver mNitzReceiver = new BroadcastReceiver() { // from class: com.android.server.NetworkTimeUpdateService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TelephonyIntents.ACTION_NETWORK_SET_TIME.equals(action)) {
                NetworkTimeUpdateService.this.mNitzTimeSetTime = SystemClock.elapsedRealtime();
            } else if (TelephonyIntents.ACTION_NETWORK_SET_TIMEZONE.equals(action)) {
                NetworkTimeUpdateService.this.mNitzZoneSetTime = SystemClock.elapsedRealtime();
            }
        }
    };
    private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() { // from class: com.android.server.NetworkTimeUpdateService.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = connManager.getActiveNetworkInfo();
                if (netInfo == null || netInfo.getState() != NetworkInfo.State.CONNECTED) {
                    return;
                }
                if (netInfo.getType() == 1 || netInfo.getType() == 9) {
                    NetworkTimeUpdateService.this.mHandler.obtainMessage(3).sendToTarget();
                }
            }
        }
    };

    public NetworkTimeUpdateService(Context context) {
        this.mContext = context;
        this.mTime = NtpTrustedTime.getInstance(context);
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        Intent pollIntent = new Intent(ACTION_POLL, (Uri) null);
        this.mPendingPollIntent = PendingIntent.getBroadcast(this.mContext, POLL_REQUEST, pollIntent, 0);
        this.mPollingIntervalMs = this.mContext.getResources().getInteger(R.integer.config_ntpPollingInterval);
        this.mPollingIntervalShorterMs = this.mContext.getResources().getInteger(R.integer.config_ntpPollingIntervalShorter);
        this.mTryAgainTimesMax = this.mContext.getResources().getInteger(R.integer.config_ntpRetry);
        this.mTimeErrorThresholdMs = this.mContext.getResources().getInteger(R.integer.config_ntpThreshold);
    }

    public void systemRunning() {
        registerForTelephonyIntents();
        registerForAlarms();
        registerForConnectivityIntents();
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        this.mHandler = new MyHandler(thread.getLooper());
        this.mHandler.obtainMessage(2).sendToTarget();
        this.mSettingsObserver = new SettingsObserver(this.mHandler, 1);
        this.mSettingsObserver.observe(this.mContext);
    }

    private void registerForTelephonyIntents() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyIntents.ACTION_NETWORK_SET_TIME);
        intentFilter.addAction(TelephonyIntents.ACTION_NETWORK_SET_TIMEZONE);
        this.mContext.registerReceiver(this.mNitzReceiver, intentFilter);
    }

    private void registerForAlarms() {
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.NetworkTimeUpdateService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                NetworkTimeUpdateService.this.mHandler.obtainMessage(2).sendToTarget();
            }
        }, new IntentFilter(ACTION_POLL));
    }

    private void registerForConnectivityIntents() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.mContext.registerReceiver(this.mConnectivityReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPollNetworkTime(int event) {
        if (isAutomaticTimeRequested()) {
            long refTime = SystemClock.elapsedRealtime();
            if (this.mNitzTimeSetTime != -1 && refTime - this.mNitzTimeSetTime < this.mPollingIntervalMs) {
                resetAlarm(this.mPollingIntervalMs);
                return;
            }
            long currentTime = System.currentTimeMillis();
            if (this.mLastNtpFetchTime == -1 || refTime >= this.mLastNtpFetchTime + this.mPollingIntervalMs || event == 1) {
                if (this.mTime.getCacheAge() >= this.mPollingIntervalMs) {
                    this.mTime.forceRefresh();
                }
                if (this.mTime.getCacheAge() < this.mPollingIntervalMs) {
                    long ntp = this.mTime.currentTimeMillis();
                    this.mTryAgainCounter = 0;
                    if ((Math.abs(ntp - currentTime) > this.mTimeErrorThresholdMs || this.mLastNtpFetchTime == -1) && ntp / 1000 < 2147483647L) {
                        SystemClock.setCurrentTimeMillis(ntp);
                    }
                    this.mLastNtpFetchTime = SystemClock.elapsedRealtime();
                } else {
                    this.mTryAgainCounter++;
                    if (this.mTryAgainTimesMax < 0 || this.mTryAgainCounter <= this.mTryAgainTimesMax) {
                        resetAlarm(this.mPollingIntervalShorterMs);
                        return;
                    }
                    this.mTryAgainCounter = 0;
                    resetAlarm(this.mPollingIntervalMs);
                    return;
                }
            }
            resetAlarm(this.mPollingIntervalMs);
        }
    }

    private void resetAlarm(long interval) {
        this.mAlarmManager.cancel(this.mPendingPollIntent);
        long now = SystemClock.elapsedRealtime();
        long next = now + interval;
        this.mAlarmManager.set(3, next, this.mPendingPollIntent);
    }

    private boolean isAutomaticTimeRequested() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "auto_time", 0) != 0;
    }

    /* loaded from: NetworkTimeUpdateService$MyHandler.class */
    private class MyHandler extends Handler {
        public MyHandler(Looper l) {
            super(l);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                case 2:
                case 3:
                    NetworkTimeUpdateService.this.onPollNetworkTime(msg.what);
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: NetworkTimeUpdateService$SettingsObserver.class */
    private static class SettingsObserver extends ContentObserver {
        private int mMsg;
        private Handler mHandler;

        SettingsObserver(Handler handler, int msg) {
            super(handler);
            this.mHandler = handler;
            this.mMsg = msg;
        }

        void observe(Context context) {
            ContentResolver resolver = context.getContentResolver();
            resolver.registerContentObserver(Settings.Global.getUriFor("auto_time"), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            this.mHandler.obtainMessage(this.mMsg).sendToTarget();
        }
    }
}