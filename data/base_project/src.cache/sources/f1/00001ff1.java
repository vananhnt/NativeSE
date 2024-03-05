package com.android.server.wifi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiStateMachine;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.internal.R;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WifiNotificationController.class */
public final class WifiNotificationController {
    private static final int ICON_NETWORKS_AVAILABLE = 17302922;
    private final long NOTIFICATION_REPEAT_DELAY_MS;
    private boolean mNotificationEnabled;
    private NotificationEnabledSettingObserver mNotificationEnabledSettingObserver;
    private long mNotificationRepeatTime;
    private Notification mNotification;
    private boolean mNotificationShown;
    private static final int NUM_SCANS_BEFORE_ACTUALLY_SCANNING = 3;
    private int mNumScansSinceNetworkStateChange;
    private final Context mContext;
    private final WifiStateMachine mWifiStateMachine;
    private NetworkInfo mNetworkInfo;
    private volatile int mWifiState = 4;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiNotificationController(Context context, WifiStateMachine wsm) {
        this.mContext = context;
        this.mWifiStateMachine = wsm;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.wifi.WifiNotificationController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    WifiNotificationController.this.mWifiState = intent.getIntExtra("wifi_state", 4);
                    WifiNotificationController.this.resetNotification();
                } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    WifiNotificationController.this.mNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    switch (AnonymousClass2.$SwitchMap$android$net$NetworkInfo$DetailedState[WifiNotificationController.this.mNetworkInfo.getDetailedState().ordinal()]) {
                        case 1:
                        case 2:
                        case 3:
                            WifiNotificationController.this.resetNotification();
                            return;
                        default:
                            return;
                    }
                } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    WifiNotificationController.this.checkAndSetNotification(WifiNotificationController.this.mNetworkInfo, WifiNotificationController.this.mWifiStateMachine.syncGetScanResultsList());
                }
            }
        }, filter);
        this.NOTIFICATION_REPEAT_DELAY_MS = Settings.Global.getInt(context.getContentResolver(), "wifi_networks_available_repeat_delay", MediaPlayer.MEDIA_INFO_TIMED_TEXT_ERROR) * 1000;
        this.mNotificationEnabledSettingObserver = new NotificationEnabledSettingObserver(new Handler());
        this.mNotificationEnabledSettingObserver.register();
    }

    /* renamed from: com.android.server.wifi.WifiNotificationController$2  reason: invalid class name */
    /* loaded from: WifiNotificationController$2.class */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$net$NetworkInfo$DetailedState = new int[NetworkInfo.DetailedState.values().length];

        static {
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.CONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.DISCONNECTED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void checkAndSetNotification(NetworkInfo networkInfo, List<ScanResult> scanResults) {
        if (this.mNotificationEnabled && networkInfo != null && this.mWifiState == 3) {
            NetworkInfo.State state = networkInfo.getState();
            if ((state == NetworkInfo.State.DISCONNECTED || state == NetworkInfo.State.UNKNOWN) && scanResults != null) {
                int numOpenNetworks = 0;
                for (int i = scanResults.size() - 1; i >= 0; i--) {
                    ScanResult scanResult = scanResults.get(i);
                    if (scanResult.capabilities != null && scanResult.capabilities.equals("[ESS]")) {
                        numOpenNetworks++;
                    }
                }
                if (numOpenNetworks > 0) {
                    int i2 = this.mNumScansSinceNetworkStateChange + 1;
                    this.mNumScansSinceNetworkStateChange = i2;
                    if (i2 >= 3) {
                        setNotificationVisible(true, numOpenNetworks, false, 0);
                        return;
                    }
                    return;
                }
            }
            setNotificationVisible(false, 0, false, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void resetNotification() {
        this.mNotificationRepeatTime = 0L;
        this.mNumScansSinceNetworkStateChange = 0;
        setNotificationVisible(false, 0, false, 0);
    }

    private void setNotificationVisible(boolean visible, int numNetworks, boolean force, int delay) {
        if (!visible && !this.mNotificationShown && !force) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (visible) {
            if (System.currentTimeMillis() < this.mNotificationRepeatTime) {
                return;
            }
            if (this.mNotification == null) {
                this.mNotification = new Notification();
                this.mNotification.when = 0L;
                this.mNotification.icon = 17302922;
                this.mNotification.flags = 16;
                this.mNotification.contentIntent = TaskStackBuilder.create(this.mContext).addNextIntentWithParentStack(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)).getPendingIntent(0, 0, null, UserHandle.CURRENT);
            }
            CharSequence title = this.mContext.getResources().getQuantityText(R.plurals.wifi_available, numNetworks);
            CharSequence details = this.mContext.getResources().getQuantityText(R.plurals.wifi_available_detailed, numNetworks);
            this.mNotification.tickerText = title;
            this.mNotification.setLatestEventInfo(this.mContext, title, details, this.mNotification.contentIntent);
            this.mNotificationRepeatTime = System.currentTimeMillis() + this.NOTIFICATION_REPEAT_DELAY_MS;
            notificationManager.notifyAsUser(null, 17302922, this.mNotification, UserHandle.ALL);
        } else {
            notificationManager.cancelAsUser(null, 17302922, UserHandle.ALL);
        }
        this.mNotificationShown = visible;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("mNotificationEnabled " + this.mNotificationEnabled);
        pw.println("mNotificationRepeatTime " + this.mNotificationRepeatTime);
        pw.println("mNotificationShown " + this.mNotificationShown);
        pw.println("mNumScansSinceNetworkStateChange " + this.mNumScansSinceNetworkStateChange);
    }

    /* loaded from: WifiNotificationController$NotificationEnabledSettingObserver.class */
    private class NotificationEnabledSettingObserver extends ContentObserver {
        public NotificationEnabledSettingObserver(Handler handler) {
            super(handler);
        }

        public void register() {
            ContentResolver cr = WifiNotificationController.this.mContext.getContentResolver();
            cr.registerContentObserver(Settings.Global.getUriFor("wifi_networks_available_notification_on"), true, this);
            synchronized (WifiNotificationController.this) {
                WifiNotificationController.this.mNotificationEnabled = getValue();
            }
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized (WifiNotificationController.this) {
                WifiNotificationController.this.mNotificationEnabled = getValue();
                WifiNotificationController.this.resetNotification();
            }
        }

        private boolean getValue() {
            return Settings.Global.getInt(WifiNotificationController.this.mContext.getContentResolver(), "wifi_networks_available_notification_on", 1) == 1;
        }
    }
}