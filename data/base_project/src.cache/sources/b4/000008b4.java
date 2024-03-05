package android.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

/* loaded from: CaptivePortalTracker.class */
public class CaptivePortalTracker extends StateMachine {
    private static final boolean DBG = true;
    private static final String TAG = "CaptivePortalTracker";
    private static final String DEFAULT_SERVER = "clients3.google.com";
    private static final int SOCKET_TIMEOUT_MS = 10000;
    public static final String ACTION_NETWORK_CONDITIONS_MEASURED = "android.net.conn.NETWORK_CONDITIONS_MEASURED";
    public static final String EXTRA_CONNECTIVITY_TYPE = "extra_connectivity_type";
    public static final String EXTRA_NETWORK_TYPE = "extra_network_type";
    public static final String EXTRA_RESPONSE_RECEIVED = "extra_response_received";
    public static final String EXTRA_IS_CAPTIVE_PORTAL = "extra_is_captive_portal";
    public static final String EXTRA_CELL_ID = "extra_cellid";
    public static final String EXTRA_SSID = "extra_ssid";
    public static final String EXTRA_BSSID = "extra_bssid";
    public static final String EXTRA_REQUEST_TIMESTAMP_MS = "extra_request_timestamp_ms";
    public static final String EXTRA_RESPONSE_TIMESTAMP_MS = "extra_response_timestamp_ms";
    private static final String PERMISSION_ACCESS_NETWORK_CONDITIONS = "android.permission.ACCESS_NETWORK_CONDITIONS";
    private String mServer;
    private String mUrl;
    private boolean mIsCaptivePortalCheckEnabled;
    private IConnectivityManager mConnService;
    private TelephonyManager mTelephonyManager;
    private WifiManager mWifiManager;
    private Context mContext;
    private NetworkInfo mNetworkInfo;
    private static final int CMD_DETECT_PORTAL = 0;
    private static final int CMD_CONNECTIVITY_CHANGE = 1;
    private static final int CMD_DELAYED_CAPTIVE_CHECK = 2;
    private static final int DELAYED_CHECK_INTERVAL_MS = 10000;
    private int mDelayedCheckToken;
    private State mDefaultState;
    private State mNoActiveNetworkState;
    private State mActiveNetworkState;
    private State mDelayedCaptiveCheckState;
    private static final String SETUP_WIZARD_PACKAGE = "com.google.android.setupwizard";
    private boolean mDeviceProvisioned;
    private ProvisioningObserver mProvisioningObserver;
    private final BroadcastReceiver mReceiver;

    static /* synthetic */ int access$2204(CaptivePortalTracker x0) {
        int i = x0.mDelayedCheckToken + 1;
        x0.mDelayedCheckToken = i;
        return i;
    }

    private CaptivePortalTracker(Context context, IConnectivityManager cs) {
        super(TAG);
        this.mIsCaptivePortalCheckEnabled = false;
        this.mDelayedCheckToken = 0;
        this.mDefaultState = new DefaultState();
        this.mNoActiveNetworkState = new NoActiveNetworkState();
        this.mActiveNetworkState = new ActiveNetworkState();
        this.mDelayedCaptiveCheckState = new DelayedCaptiveCheckState();
        this.mDeviceProvisioned = false;
        this.mReceiver = new BroadcastReceiver() { // from class: android.net.CaptivePortalTracker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if ((CaptivePortalTracker.this.mDeviceProvisioned && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) || (!CaptivePortalTracker.this.mDeviceProvisioned && action.equals(ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE))) {
                    NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    CaptivePortalTracker.this.sendMessage(CaptivePortalTracker.this.obtainMessage(1, info));
                }
            }
        };
        this.mContext = context;
        this.mConnService = cs;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mProvisioningObserver = new ProvisioningObserver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION_IMMEDIATE);
        this.mContext.registerReceiver(this.mReceiver, filter);
        this.mServer = Settings.Global.getString(this.mContext.getContentResolver(), Settings.Global.CAPTIVE_PORTAL_SERVER);
        if (this.mServer == null) {
            this.mServer = DEFAULT_SERVER;
        }
        this.mIsCaptivePortalCheckEnabled = Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.CAPTIVE_PORTAL_DETECTION_ENABLED, 1) == 1;
        addState(this.mDefaultState);
        addState(this.mNoActiveNetworkState, this.mDefaultState);
        addState(this.mActiveNetworkState, this.mDefaultState);
        addState(this.mDelayedCaptiveCheckState, this.mActiveNetworkState);
        setInitialState(this.mNoActiveNetworkState);
    }

    /* loaded from: CaptivePortalTracker$ProvisioningObserver.class */
    private class ProvisioningObserver extends ContentObserver {
        ProvisioningObserver() {
            super(new Handler());
            CaptivePortalTracker.this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this);
            onChange(false);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            CaptivePortalTracker.this.mDeviceProvisioned = Settings.Global.getInt(CaptivePortalTracker.this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
        }
    }

    public static CaptivePortalTracker makeCaptivePortalTracker(Context context, IConnectivityManager cs) {
        CaptivePortalTracker captivePortal = new CaptivePortalTracker(context, cs);
        captivePortal.start();
        return captivePortal;
    }

    public void detectCaptivePortal(NetworkInfo info) {
        sendMessage(obtainMessage(0, info));
    }

    /* loaded from: CaptivePortalTracker$DefaultState.class */
    private class DefaultState extends State {
        private DefaultState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            CaptivePortalTracker.this.log(getName() + message.toString());
            switch (message.what) {
                case 0:
                    NetworkInfo info = (NetworkInfo) message.obj;
                    CaptivePortalTracker.this.notifyPortalCheckComplete(info);
                    return true;
                case 1:
                case 2:
                    return true;
                default:
                    CaptivePortalTracker.this.loge("Ignoring " + message);
                    return true;
            }
        }
    }

    /* loaded from: CaptivePortalTracker$NoActiveNetworkState.class */
    private class NoActiveNetworkState extends State {
        private NoActiveNetworkState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            CaptivePortalTracker.this.setNotificationOff();
            CaptivePortalTracker.this.mNetworkInfo = null;
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            CaptivePortalTracker.this.log(getName() + message.toString());
            switch (message.what) {
                case 1:
                    NetworkInfo info = (NetworkInfo) message.obj;
                    if (info.getType() != 1) {
                        CaptivePortalTracker.this.log(getName() + " not a wifi connectivity change, ignore");
                        return true;
                    } else if (info.isConnected() && CaptivePortalTracker.this.isActiveNetwork(info)) {
                        CaptivePortalTracker.this.mNetworkInfo = info;
                        CaptivePortalTracker.this.transitionTo(CaptivePortalTracker.this.mDelayedCaptiveCheckState);
                        return true;
                    } else {
                        return true;
                    }
                default:
                    return false;
            }
        }
    }

    /* loaded from: CaptivePortalTracker$ActiveNetworkState.class */
    private class ActiveNetworkState extends State {
        private ActiveNetworkState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 1:
                    NetworkInfo info = (NetworkInfo) message.obj;
                    if (info.isConnected() || info.getType() != CaptivePortalTracker.this.mNetworkInfo.getType()) {
                        if (info.getType() != CaptivePortalTracker.this.mNetworkInfo.getType() && info.isConnected() && CaptivePortalTracker.this.isActiveNetwork(info)) {
                            CaptivePortalTracker.this.log("Active network switched " + info);
                            CaptivePortalTracker.this.deferMessage(message);
                            CaptivePortalTracker.this.transitionTo(CaptivePortalTracker.this.mNoActiveNetworkState);
                            return true;
                        }
                        return true;
                    }
                    CaptivePortalTracker.this.log("Disconnected from active network " + info);
                    CaptivePortalTracker.this.transitionTo(CaptivePortalTracker.this.mNoActiveNetworkState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: CaptivePortalTracker$DelayedCaptiveCheckState.class */
    private class DelayedCaptiveCheckState extends State {
        private DelayedCaptiveCheckState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            Message message = CaptivePortalTracker.this.obtainMessage(2, CaptivePortalTracker.access$2204(CaptivePortalTracker.this), 0);
            if (CaptivePortalTracker.this.mDeviceProvisioned) {
                CaptivePortalTracker.this.sendMessageDelayed(message, 10000L);
            } else {
                CaptivePortalTracker.this.sendMessage(message);
            }
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            CaptivePortalTracker.this.log(getName() + message.toString());
            switch (message.what) {
                case 2:
                    CaptivePortalTracker.this.setNotificationOff();
                    if (message.arg1 == CaptivePortalTracker.this.mDelayedCheckToken) {
                        InetAddress server = CaptivePortalTracker.this.lookupHost(CaptivePortalTracker.this.mServer);
                        boolean captive = server != null && CaptivePortalTracker.this.isCaptivePortal(server);
                        if (captive) {
                            CaptivePortalTracker.this.log("Captive network " + CaptivePortalTracker.this.mNetworkInfo);
                        } else {
                            CaptivePortalTracker.this.log("Not captive network " + CaptivePortalTracker.this.mNetworkInfo);
                        }
                        CaptivePortalTracker.this.notifyPortalCheckCompleted(CaptivePortalTracker.this.mNetworkInfo, captive);
                        if (CaptivePortalTracker.this.mDeviceProvisioned) {
                            if (captive) {
                                try {
                                    CaptivePortalTracker.this.mConnService.setProvisioningNotificationVisible(true, CaptivePortalTracker.this.mNetworkInfo.getType(), CaptivePortalTracker.this.mNetworkInfo.getExtraInfo(), CaptivePortalTracker.this.mUrl);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Intent intent = new Intent(ConnectivityManager.ACTION_CAPTIVE_PORTAL_TEST_COMPLETED);
                            intent.putExtra(ConnectivityManager.EXTRA_IS_CAPTIVE_PORTAL, captive);
                            intent.setPackage(CaptivePortalTracker.SETUP_WIZARD_PACKAGE);
                            CaptivePortalTracker.this.mContext.sendBroadcast(intent);
                        }
                        CaptivePortalTracker.this.transitionTo(CaptivePortalTracker.this.mActiveNetworkState);
                        return true;
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyPortalCheckComplete(NetworkInfo info) {
        if (info == null) {
            loge("notifyPortalCheckComplete on null");
            return;
        }
        try {
            log("notifyPortalCheckComplete: ni=" + info);
            this.mConnService.captivePortalCheckComplete(info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyPortalCheckCompleted(NetworkInfo info, boolean isCaptivePortal) {
        if (info == null) {
            loge("notifyPortalCheckComplete on null");
            return;
        }
        try {
            log("notifyPortalCheckCompleted: captive=" + isCaptivePortal + " ni=" + info);
            this.mConnService.captivePortalCheckCompleted(info, isCaptivePortal);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isActiveNetwork(NetworkInfo info) {
        try {
            NetworkInfo active = this.mConnService.getActiveNetworkInfo();
            if (active != null) {
                if (active.getType() == info.getType()) {
                    return true;
                }
                return false;
            }
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setNotificationOff() {
        try {
            if (this.mNetworkInfo != null) {
                this.mConnService.setProvisioningNotificationVisible(false, this.mNetworkInfo.getType(), null, null);
            }
        } catch (RemoteException e) {
            log("setNotificationOff: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCaptivePortal(InetAddress server) {
        HttpURLConnection urlConnection = null;
        if (this.mIsCaptivePortalCheckEnabled) {
            this.mUrl = "http://" + server.getHostAddress() + "/generate_204";
            log("Checking " + this.mUrl);
            long requestTimestamp = -1;
            try {
                try {
                    URL url = new URL(this.mUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setInstanceFollowRedirects(false);
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setUseCaches(false);
                    requestTimestamp = SystemClock.elapsedRealtime();
                    urlConnection.getInputStream();
                    long responseTimestamp = SystemClock.elapsedRealtime();
                    int rspCode = urlConnection.getResponseCode();
                    boolean isCaptivePortal = rspCode != 204;
                    sendNetworkConditionsBroadcast(true, isCaptivePortal, requestTimestamp, responseTimestamp);
                    log("isCaptivePortal: ret=" + isCaptivePortal + " rspCode=" + rspCode);
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    return isCaptivePortal;
                } catch (IOException e) {
                    log("Probably not a portal: exception " + e);
                    if (requestTimestamp != -1) {
                        sendFailedCaptivePortalCheckBroadcast(requestTimestamp);
                    }
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    return false;
                }
            } catch (Throwable th) {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                throw th;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public InetAddress lookupHost(String hostname) {
        try {
            InetAddress[] inetAddress = InetAddress.getAllByName(hostname);
            for (InetAddress a : inetAddress) {
                if (a instanceof Inet4Address) {
                    return a;
                }
            }
            sendFailedCaptivePortalCheckBroadcast(SystemClock.elapsedRealtime());
            return null;
        } catch (UnknownHostException e) {
            sendFailedCaptivePortalCheckBroadcast(SystemClock.elapsedRealtime());
            return null;
        }
    }

    private void sendFailedCaptivePortalCheckBroadcast(long requestTimestampMs) {
        sendNetworkConditionsBroadcast(false, false, requestTimestampMs, 0L);
    }

    private void sendNetworkConditionsBroadcast(boolean responseReceived, boolean isCaptivePortal, long requestTimestampMs, long responseTimestampMs) {
        if (Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE, 0) == 0) {
            log("Don't send network conditions - lacking user consent.");
            return;
        }
        Intent latencyBroadcast = new Intent(ACTION_NETWORK_CONDITIONS_MEASURED);
        switch (this.mNetworkInfo.getType()) {
            case 0:
                latencyBroadcast.putExtra(EXTRA_NETWORK_TYPE, this.mTelephonyManager.getNetworkType());
                List<CellInfo> info = this.mTelephonyManager.getAllCellInfo();
                if (info != null) {
                    new StringBuffer();
                    int numRegisteredCellInfo = 0;
                    for (CellInfo cellInfo : info) {
                        if (cellInfo.isRegistered()) {
                            numRegisteredCellInfo++;
                            if (numRegisteredCellInfo > 1) {
                                log("more than one registered CellInfo.  Can't tell which is active.  Bailing.");
                                return;
                            } else if (cellInfo instanceof CellInfoCdma) {
                                CellIdentityCdma cellId = ((CellInfoCdma) cellInfo).getCellIdentity();
                                latencyBroadcast.putExtra(EXTRA_CELL_ID, cellId);
                            } else if (cellInfo instanceof CellInfoGsm) {
                                CellIdentityGsm cellId2 = ((CellInfoGsm) cellInfo).getCellIdentity();
                                latencyBroadcast.putExtra(EXTRA_CELL_ID, cellId2);
                            } else if (cellInfo instanceof CellInfoLte) {
                                CellIdentityLte cellId3 = ((CellInfoLte) cellInfo).getCellIdentity();
                                latencyBroadcast.putExtra(EXTRA_CELL_ID, cellId3);
                            } else if (cellInfo instanceof CellInfoWcdma) {
                                CellIdentityWcdma cellId4 = ((CellInfoWcdma) cellInfo).getCellIdentity();
                                latencyBroadcast.putExtra(EXTRA_CELL_ID, cellId4);
                            } else {
                                logw("Registered cellinfo is unrecognized");
                                return;
                            }
                        }
                    }
                    break;
                } else {
                    return;
                }
            case 1:
                WifiInfo currentWifiInfo = this.mWifiManager.getConnectionInfo();
                if (currentWifiInfo != null) {
                    latencyBroadcast.putExtra(EXTRA_SSID, currentWifiInfo.getSSID());
                    latencyBroadcast.putExtra(EXTRA_BSSID, currentWifiInfo.getBSSID());
                    break;
                } else {
                    logw("network info is TYPE_WIFI but no ConnectionInfo found");
                    return;
                }
            default:
                return;
        }
        latencyBroadcast.putExtra(EXTRA_CONNECTIVITY_TYPE, this.mNetworkInfo.getType());
        latencyBroadcast.putExtra(EXTRA_RESPONSE_RECEIVED, responseReceived);
        latencyBroadcast.putExtra(EXTRA_REQUEST_TIMESTAMP_MS, requestTimestampMs);
        if (responseReceived) {
            latencyBroadcast.putExtra(EXTRA_IS_CAPTIVE_PORTAL, isCaptivePortal);
            latencyBroadcast.putExtra(EXTRA_RESPONSE_TIMESTAMP_MS, responseTimestampMs);
        }
        this.mContext.sendBroadcast(latencyBroadcast, "android.permission.ACCESS_NETWORK_CONDITIONS");
    }
}