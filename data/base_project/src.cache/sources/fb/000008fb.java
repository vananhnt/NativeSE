package android.net;

import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.SamplingDataTracker;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.telephony.DctConstants;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.TelephonyIntents;
import com.android.internal.util.AsyncChannel;
import gov.nist.core.Separators;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: MobileDataStateTracker.class */
public class MobileDataStateTracker extends BaseNetworkStateTracker {
    private static final String TAG = "MobileDataStateTracker";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    private PhoneConstants.DataState mMobileDataState;
    private ITelephony mPhoneService;
    private String mApnType;
    private NetworkInfo mNetworkInfo;
    private Handler mTarget;
    private Context mContext;
    private LinkProperties mLinkProperties;
    private LinkCapabilities mLinkCapabilities;
    private Handler mHandler;
    private AsyncChannel mDataConnectionTrackerAc;
    private SignalStrength mSignalStrength;
    private static final int UNKNOWN = Integer.MAX_VALUE;
    private static NetworkDataEntry[] mTheoreticalBWTable = {new NetworkDataEntry(2, 237, 118, Integer.MAX_VALUE), new NetworkDataEntry(1, 48, 40, Integer.MAX_VALUE), new NetworkDataEntry(3, 384, 64, Integer.MAX_VALUE), new NetworkDataEntry(8, 14400, Integer.MAX_VALUE, Integer.MAX_VALUE), new NetworkDataEntry(9, 14400, 5760, Integer.MAX_VALUE), new NetworkDataEntry(10, 14400, 5760, Integer.MAX_VALUE), new NetworkDataEntry(15, 21000, 5760, Integer.MAX_VALUE), new NetworkDataEntry(4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), new NetworkDataEntry(7, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), new NetworkDataEntry(5, 2468, 153, Integer.MAX_VALUE), new NetworkDataEntry(6, 3072, BluetoothClass.Device.WEARABLE_PAGER, Integer.MAX_VALUE), new NetworkDataEntry(12, 14700, BluetoothClass.Device.WEARABLE_PAGER, Integer.MAX_VALUE), new NetworkDataEntry(11, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), new NetworkDataEntry(13, UserHandle.PER_USER_RANGE, 50000, Integer.MAX_VALUE), new NetworkDataEntry(14, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)};
    private boolean mTeardownRequested = false;
    private boolean mPrivateDnsRouteSet = false;
    private boolean mDefaultRouteSet = false;
    protected boolean mUserDataEnabled = true;
    protected boolean mPolicyDataEnabled = true;
    private AtomicBoolean mIsCaptivePortal = new AtomicBoolean(false);
    private SamplingDataTracker mSamplingDataTracker = new SamplingDataTracker();
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: android.net.MobileDataStateTracker.1
        @Override // android.telephony.PhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            MobileDataStateTracker.this.mSignalStrength = signalStrength;
        }
    };

    public MobileDataStateTracker(int netType, String tag) {
        this.mNetworkInfo = new NetworkInfo(netType, TelephonyManager.getDefault().getNetworkType(), tag, TelephonyManager.getDefault().getNetworkTypeName());
        this.mApnType = networkTypeToApnType(netType);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void startMonitoring(Context context, Handler target) {
        this.mTarget = target;
        this.mContext = context;
        this.mHandler = new MdstHandler(target.getLooper(), this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
        filter.addAction(TelephonyIntents.ACTION_DATA_CONNECTION_CONNECTED_TO_PROVISIONING_APN);
        filter.addAction(TelephonyIntents.ACTION_DATA_CONNECTION_FAILED);
        this.mContext.registerReceiver(new MobileDataStateReceiver(), filter);
        this.mMobileDataState = PhoneConstants.DataState.DISCONNECTED;
        TelephonyManager tm = (TelephonyManager) this.mContext.getSystemService("phone");
        tm.listen(this.mPhoneStateListener, 256);
    }

    /* loaded from: MobileDataStateTracker$MdstHandler.class */
    static class MdstHandler extends Handler {
        private MobileDataStateTracker mMdst;

        MdstHandler(Looper looper, MobileDataStateTracker mdst) {
            super(looper);
            this.mMdst = mdst;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 69632:
                    if (msg.arg1 == 0) {
                        this.mMdst.mDataConnectionTrackerAc = (AsyncChannel) msg.obj;
                        return;
                    }
                    return;
                case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                    this.mMdst.mDataConnectionTrackerAc = null;
                    return;
                default:
                    return;
            }
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isPrivateDnsRouteSet() {
        return this.mPrivateDnsRouteSet;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void privateDnsRouteSet(boolean enabled) {
        this.mPrivateDnsRouteSet = enabled;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public NetworkInfo getNetworkInfo() {
        return this.mNetworkInfo;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isDefaultRouteSet() {
        return this.mDefaultRouteSet;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void defaultRouteSet(boolean enabled) {
        this.mDefaultRouteSet = enabled;
    }

    public void releaseWakeLock() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLinkProperitesAndCapatilities(Intent intent) {
        this.mLinkProperties = (LinkProperties) intent.getParcelableExtra("linkProperties");
        if (this.mLinkProperties == null) {
            loge("CONNECTED event did not supply link properties.");
            this.mLinkProperties = new LinkProperties();
        }
        this.mLinkProperties.setMtu(this.mContext.getResources().getInteger(R.integer.config_mobile_mtu));
        this.mLinkCapabilities = (LinkCapabilities) intent.getParcelableExtra("linkCapabilities");
        if (this.mLinkCapabilities == null) {
            loge("CONNECTED event did not supply link capabilities.");
            this.mLinkCapabilities = new LinkCapabilities();
        }
    }

    /* loaded from: MobileDataStateTracker$MobileDataStateReceiver.class */
    private class MobileDataStateReceiver extends BroadcastReceiver {
        private MobileDataStateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TelephonyIntents.ACTION_DATA_CONNECTION_CONNECTED_TO_PROVISIONING_APN)) {
                String apnName = intent.getStringExtra("apn");
                String apnType = intent.getStringExtra("apnType");
                if (TextUtils.equals(MobileDataStateTracker.this.mApnType, apnType)) {
                    MobileDataStateTracker.this.log("Broadcast received: " + intent.getAction() + " apnType=" + apnType + " apnName=" + apnName);
                    MobileDataStateTracker.this.mMobileDataState = PhoneConstants.DataState.CONNECTING;
                    MobileDataStateTracker.this.updateLinkProperitesAndCapatilities(intent);
                    MobileDataStateTracker.this.mNetworkInfo.setIsConnectedToProvisioningNetwork(true);
                    MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.SUSPENDED, "", apnName);
                }
            } else if (!intent.getAction().equals(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED)) {
                if (!intent.getAction().equals(TelephonyIntents.ACTION_DATA_CONNECTION_FAILED)) {
                    MobileDataStateTracker.this.log("Broadcast received: ignore " + intent.getAction());
                    return;
                }
                String apnType2 = intent.getStringExtra("apnType");
                if (!TextUtils.equals(apnType2, MobileDataStateTracker.this.mApnType)) {
                    MobileDataStateTracker.this.log(String.format("Broadcast received: ACTION_ANY_DATA_CONNECTION_FAILED ignore, mApnType=%s != received apnType=%s", MobileDataStateTracker.this.mApnType, apnType2));
                    return;
                }
                MobileDataStateTracker.this.mNetworkInfo.setIsConnectedToProvisioningNetwork(false);
                String reason = intent.getStringExtra("reason");
                String apnName2 = intent.getStringExtra("apn");
                MobileDataStateTracker.this.log(new StringBuilder().append("Broadcast received: ").append(intent.getAction()).append(" reason=").append(reason).toString() == null ? "null" : reason);
                MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.FAILED, reason, apnName2);
            } else {
                String apnType3 = intent.getStringExtra("apnType");
                if (TextUtils.equals(apnType3, MobileDataStateTracker.this.mApnType)) {
                    MobileDataStateTracker.this.mNetworkInfo.setIsConnectedToProvisioningNetwork(false);
                    MobileDataStateTracker.this.log("Broadcast received: " + intent.getAction() + " apnType=" + apnType3);
                    int oldSubtype = MobileDataStateTracker.this.mNetworkInfo.getSubtype();
                    int newSubType = TelephonyManager.getDefault().getNetworkType();
                    String subTypeName = TelephonyManager.getDefault().getNetworkTypeName();
                    MobileDataStateTracker.this.mNetworkInfo.setSubtype(newSubType, subTypeName);
                    if (newSubType != oldSubtype && MobileDataStateTracker.this.mNetworkInfo.isConnected()) {
                        Message msg = MobileDataStateTracker.this.mTarget.obtainMessage(NetworkStateTracker.EVENT_NETWORK_SUBTYPE_CHANGED, oldSubtype, 0, MobileDataStateTracker.this.mNetworkInfo);
                        msg.sendToTarget();
                    }
                    PhoneConstants.DataState state = (PhoneConstants.DataState) Enum.valueOf(PhoneConstants.DataState.class, intent.getStringExtra("state"));
                    String reason2 = intent.getStringExtra("reason");
                    String apnName3 = intent.getStringExtra("apn");
                    MobileDataStateTracker.this.mNetworkInfo.setRoaming(intent.getBooleanExtra(PhoneConstants.DATA_NETWORK_ROAMING_KEY, false));
                    MobileDataStateTracker.this.mNetworkInfo.setIsAvailable(!intent.getBooleanExtra(PhoneConstants.NETWORK_UNAVAILABLE_KEY, false));
                    MobileDataStateTracker.this.log("Received state=" + state + ", old=" + MobileDataStateTracker.this.mMobileDataState + ", reason=" + (reason2 == null ? "(unspecified)" : reason2));
                    if (MobileDataStateTracker.this.mMobileDataState != state) {
                        MobileDataStateTracker.this.mMobileDataState = state;
                        switch (state) {
                            case DISCONNECTED:
                                if (MobileDataStateTracker.this.isTeardownRequested()) {
                                    MobileDataStateTracker.this.setTeardownRequested(false);
                                }
                                MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, reason2, apnName3);
                                break;
                            case CONNECTING:
                                MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.CONNECTING, reason2, apnName3);
                                break;
                            case SUSPENDED:
                                MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.SUSPENDED, reason2, apnName3);
                                break;
                            case CONNECTED:
                                MobileDataStateTracker.this.updateLinkProperitesAndCapatilities(intent);
                                MobileDataStateTracker.this.setDetailedState(NetworkInfo.DetailedState.CONNECTED, reason2, apnName3);
                                break;
                        }
                        MobileDataStateTracker.this.mSamplingDataTracker.resetSamplingData();
                    } else if (TextUtils.equals(reason2, PhoneConstants.REASON_LINK_PROPERTIES_CHANGED)) {
                        MobileDataStateTracker.this.mLinkProperties = (LinkProperties) intent.getParcelableExtra("linkProperties");
                        if (MobileDataStateTracker.this.mLinkProperties == null) {
                            MobileDataStateTracker.this.loge("No link property in LINK_PROPERTIES change event.");
                            MobileDataStateTracker.this.mLinkProperties = new LinkProperties();
                        }
                        MobileDataStateTracker.this.mNetworkInfo.setDetailedState(MobileDataStateTracker.this.mNetworkInfo.getDetailedState(), reason2, MobileDataStateTracker.this.mNetworkInfo.getExtraInfo());
                        Message msg2 = MobileDataStateTracker.this.mTarget.obtainMessage(NetworkStateTracker.EVENT_CONFIGURATION_CHANGED, MobileDataStateTracker.this.mNetworkInfo);
                        msg2.sendToTarget();
                    }
                }
            }
        }
    }

    private void getPhoneService(boolean forceRefresh) {
        if (this.mPhoneService == null || forceRefresh) {
            this.mPhoneService = ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isAvailable() {
        return this.mNetworkInfo.isAvailable();
    }

    @Override // android.net.NetworkStateTracker
    public String getTcpBufferSizesPropName() {
        String networkTypeStr = "unknown";
        TelephonyManager tm = new TelephonyManager(this.mContext);
        switch (tm.getNetworkType()) {
            case 1:
                networkTypeStr = "gprs";
                break;
            case 2:
                networkTypeStr = "edge";
                break;
            case 3:
                networkTypeStr = "umts";
                break;
            case 4:
                networkTypeStr = "cdma";
                break;
            case 5:
                networkTypeStr = "evdo";
                break;
            case 6:
                networkTypeStr = "evdo";
                break;
            case 7:
                networkTypeStr = "1xrtt";
                break;
            case 8:
                networkTypeStr = "hsdpa";
                break;
            case 9:
                networkTypeStr = "hsupa";
                break;
            case 10:
                networkTypeStr = "hspa";
                break;
            case 11:
                networkTypeStr = "iden";
                break;
            case 12:
                networkTypeStr = "evdo";
                break;
            case 13:
                networkTypeStr = "lte";
                break;
            case 14:
                networkTypeStr = "ehrpd";
                break;
            case 15:
                networkTypeStr = "hspap";
                break;
            default:
                loge("unknown network type: " + tm.getNetworkType());
                break;
        }
        return "net.tcp.buffersize." + networkTypeStr;
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        setTeardownRequested(true);
        return setEnableApn(this.mApnType, false) != 3;
    }

    public boolean isReady() {
        return this.mDataConnectionTrackerAc != null;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckCompleted(boolean isCaptivePortal) {
        if (this.mIsCaptivePortal.getAndSet(isCaptivePortal) != isCaptivePortal) {
            setEnableFailFastMobileData(isCaptivePortal ? 1 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDetailedState(NetworkInfo.DetailedState state, String reason, String extraInfo) {
        log("setDetailed state, old =" + this.mNetworkInfo.getDetailedState() + " and new state=" + state);
        if (state != this.mNetworkInfo.getDetailedState()) {
            boolean wasConnecting = this.mNetworkInfo.getState() == NetworkInfo.State.CONNECTING;
            String lastReason = this.mNetworkInfo.getReason();
            if (wasConnecting && state == NetworkInfo.DetailedState.CONNECTED && reason == null && lastReason != null) {
                reason = lastReason;
            }
            this.mNetworkInfo.setDetailedState(state, reason, extraInfo);
            Message msg = this.mTarget.obtainMessage(458752, new NetworkInfo(this.mNetworkInfo));
            msg.sendToTarget();
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setTeardownRequested(boolean isRequested) {
        this.mTeardownRequested = isRequested;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isTeardownRequested() {
        return this.mTeardownRequested;
    }

    @Override // android.net.NetworkStateTracker
    public boolean reconnect() {
        boolean retValue = false;
        setTeardownRequested(false);
        switch (setEnableApn(this.mApnType, true)) {
            case 0:
                retValue = true;
                break;
            case 1:
                this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.IDLE, null, null);
                retValue = true;
                break;
            case 2:
            case 3:
                break;
            default:
                loge("Error in reconnect - unexpected response.");
                break;
        }
        return retValue;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0037, code lost:
        r1 = new java.lang.StringBuilder().append("Could not set radio power to ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0045, code lost:
        if (r5 == false) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0048, code lost:
        r2 = android.hardware.Camera.Parameters.FLASH_MODE_ON;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x004d, code lost:
        r2 = "off";
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x004f, code lost:
        loge(r1.append(r2).toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0059, code lost:
        return false;
     */
    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean setRadio(boolean r5) {
        /*
            r4 = this;
            r0 = r4
            r1 = 0
            r0.getPhoneService(r1)
            r0 = 0
            r6 = r0
        L7:
            r0 = r6
            r1 = 2
            if (r0 >= r1) goto L37
            r0 = r4
            com.android.internal.telephony.ITelephony r0 = r0.mPhoneService
            if (r0 != 0) goto L1c
            r0 = r4
            java.lang.String r1 = "Ignoring mobile radio request because could not acquire PhoneService"
            r0.loge(r1)
            goto L37
        L1c:
            r0 = r4
            com.android.internal.telephony.ITelephony r0 = r0.mPhoneService     // Catch: android.os.RemoteException -> L27
            r1 = r5
            boolean r0 = r0.setRadio(r1)     // Catch: android.os.RemoteException -> L27
            return r0
        L27:
            r7 = move-exception
            r0 = r6
            if (r0 != 0) goto L31
            r0 = r4
            r1 = 1
            r0.getPhoneService(r1)
        L31:
            int r6 = r6 + 1
            goto L7
        L37:
            r0 = r4
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r2 = r1
            r2.<init>()
            java.lang.String r2 = "Could not set radio power to "
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r5
            if (r2 == 0) goto L4d
            java.lang.String r2 = "on"
            goto L4f
        L4d:
            java.lang.String r2 = "off"
        L4f:
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.loge(r1)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.MobileDataStateTracker.setRadio(boolean):boolean");
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setUserDataEnable(boolean enabled) {
        log("setUserDataEnable: E enabled=" + enabled);
        AsyncChannel channel = this.mDataConnectionTrackerAc;
        if (channel != null) {
            channel.sendMessage(DctConstants.CMD_SET_USER_DATA_ENABLE, enabled ? 1 : 0);
            this.mUserDataEnabled = enabled;
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setPolicyDataEnable(boolean enabled) {
        log("setPolicyDataEnable(enabled=" + enabled + Separators.RPAREN);
        AsyncChannel channel = this.mDataConnectionTrackerAc;
        if (channel != null) {
            channel.sendMessage(DctConstants.CMD_SET_POLICY_DATA_ENABLE, enabled ? 1 : 0);
            this.mPolicyDataEnabled = enabled;
        }
    }

    public void setEnableFailFastMobileData(int enabled) {
        log("setEnableFailFastMobileData(enabled=" + enabled + Separators.RPAREN);
        AsyncChannel channel = this.mDataConnectionTrackerAc;
        if (channel != null) {
            channel.sendMessage(DctConstants.CMD_SET_ENABLE_FAIL_FAST_MOBILE_DATA, enabled);
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setDependencyMet(boolean met) {
        Bundle bundle = Bundle.forPair("apnType", this.mApnType);
        try {
            log("setDependencyMet: E met=" + met);
            Message msg = Message.obtain();
            msg.what = DctConstants.CMD_SET_DEPENDENCY_MET;
            msg.arg1 = met ? 1 : 0;
            msg.setData(bundle);
            this.mDataConnectionTrackerAc.sendMessage(msg);
        } catch (NullPointerException e) {
            loge("setDependencyMet: X mAc was null" + e);
        }
    }

    public void enableMobileProvisioning(String url) {
        log("enableMobileProvisioning(url=" + url + Separators.RPAREN);
        AsyncChannel channel = this.mDataConnectionTrackerAc;
        if (channel != null) {
            Message msg = Message.obtain();
            msg.what = DctConstants.CMD_ENABLE_MOBILE_PROVISIONING;
            msg.setData(Bundle.forPair(DctConstants.PROVISIONING_URL_KEY, url));
            channel.sendMessage(msg);
        }
    }

    public boolean isProvisioningNetwork() {
        boolean retVal;
        try {
            Message msg = Message.obtain();
            msg.what = DctConstants.CMD_IS_PROVISIONING_APN;
            msg.setData(Bundle.forPair("apnType", this.mApnType));
            Message result = this.mDataConnectionTrackerAc.sendMessageSynchronously(msg);
            retVal = result.arg1 == 1;
        } catch (NullPointerException e) {
            loge("isProvisioningNetwork: X " + e);
            retVal = false;
        }
        log("isProvisioningNetwork: retVal=" + retVal);
        return retVal;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void addStackedLink(LinkProperties link) {
        this.mLinkProperties.addStackedLink(link);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void removeStackedLink(LinkProperties link) {
        this.mLinkProperties.removeStackedLink(link);
    }

    public String toString() {
        CharArrayWriter writer = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(writer);
        pw.print("Mobile data state: ");
        pw.println(this.mMobileDataState);
        pw.print("Data enabled: user=");
        pw.print(this.mUserDataEnabled);
        pw.print(", policy=");
        pw.println(this.mPolicyDataEnabled);
        return writer.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0047, code lost:
        r1 = new java.lang.StringBuilder().append("Could not ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0055, code lost:
        if (r6 == false) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0058, code lost:
        r2 = "enable";
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x005d, code lost:
        r2 = "disable";
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x005f, code lost:
        loge(r1.append(r2).append(" APN type \"").append(r5).append(gov.nist.core.Separators.DOUBLE_QUOTE).toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0077, code lost:
        return 3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int setEnableApn(java.lang.String r5, boolean r6) {
        /*
            r4 = this;
            r0 = r4
            r1 = 0
            r0.getPhoneService(r1)
            r0 = 0
            r7 = r0
        L7:
            r0 = r7
            r1 = 2
            if (r0 >= r1) goto L47
            r0 = r4
            com.android.internal.telephony.ITelephony r0 = r0.mPhoneService
            if (r0 != 0) goto L1c
            r0 = r4
            java.lang.String r1 = "Ignoring feature request because could not acquire PhoneService"
            r0.loge(r1)
            goto L47
        L1c:
            r0 = r6
            if (r0 == 0) goto L2b
            r0 = r4
            com.android.internal.telephony.ITelephony r0 = r0.mPhoneService     // Catch: android.os.RemoteException -> L36
            r1 = r5
            int r0 = r0.enableApnType(r1)     // Catch: android.os.RemoteException -> L36
            return r0
        L2b:
            r0 = r4
            com.android.internal.telephony.ITelephony r0 = r0.mPhoneService     // Catch: android.os.RemoteException -> L36
            r1 = r5
            int r0 = r0.disableApnType(r1)     // Catch: android.os.RemoteException -> L36
            return r0
        L36:
            r8 = move-exception
            r0 = r7
            if (r0 != 0) goto L41
            r0 = r4
            r1 = 1
            r0.getPhoneService(r1)
        L41:
            int r7 = r7 + 1
            goto L7
        L47:
            r0 = r4
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r2 = r1
            r2.<init>()
            java.lang.String r2 = "Could not "
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r6
            if (r2 == 0) goto L5d
            java.lang.String r2 = "enable"
            goto L5f
        L5d:
            java.lang.String r2 = "disable"
        L5f:
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = " APN type \""
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r5
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = "\""
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.loge(r1)
            r0 = 3
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.MobileDataStateTracker.setEnableApn(java.lang.String, boolean):int");
    }

    public static String networkTypeToApnType(int netType) {
        switch (netType) {
            case 0:
                return "default";
            case 1:
            case 6:
            case 7:
            case 8:
            case 9:
            case 13:
            default:
                sloge("Error mapping networkType " + netType + " to apnType.");
                return null;
            case 2:
                return PhoneConstants.APN_TYPE_MMS;
            case 3:
                return PhoneConstants.APN_TYPE_SUPL;
            case 4:
                return PhoneConstants.APN_TYPE_DUN;
            case 5:
                return PhoneConstants.APN_TYPE_HIPRI;
            case 10:
                return PhoneConstants.APN_TYPE_FOTA;
            case 11:
                return PhoneConstants.APN_TYPE_IMS;
            case 12:
                return PhoneConstants.APN_TYPE_CBS;
            case 14:
                return PhoneConstants.APN_TYPE_IA;
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkProperties getLinkProperties() {
        return new LinkProperties(this.mLinkProperties);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkCapabilities getLinkCapabilities() {
        return new LinkCapabilities(this.mLinkCapabilities);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void supplyMessenger(Messenger messenger) {
        AsyncChannel ac = new AsyncChannel();
        ac.connect(this.mContext, this.mHandler, messenger);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String s) {
        Slog.d(TAG, this.mApnType + ": " + s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String s) {
        Slog.e(TAG, this.mApnType + ": " + s);
    }

    private static void sloge(String s) {
        Slog.e(TAG, s);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkQualityInfo getLinkQualityInfo() {
        if (this.mNetworkInfo == null || this.mNetworkInfo.getType() == -1) {
            return null;
        }
        MobileLinkQualityInfo li = new MobileLinkQualityInfo();
        li.setNetworkType(this.mNetworkInfo.getType());
        this.mSamplingDataTracker.setCommonLinkQualityInfoFields(li);
        if (this.mNetworkInfo.getSubtype() != 0) {
            li.setMobileNetworkType(this.mNetworkInfo.getSubtype());
            NetworkDataEntry entry = getNetworkDataEntry(this.mNetworkInfo.getSubtype());
            if (entry != null) {
                li.setTheoreticalRxBandwidth(entry.downloadBandwidth);
                li.setTheoreticalRxBandwidth(entry.uploadBandwidth);
                li.setTheoreticalLatency(entry.latency);
            }
            if (this.mSignalStrength != null) {
                li.setNormalizedSignalStrength(getNormalizedSignalStrength(li.getMobileNetworkType(), this.mSignalStrength));
            }
        }
        SignalStrength ss = this.mSignalStrength;
        if (ss != null) {
            li.setRssi(ss.getGsmSignalStrength());
            li.setGsmErrorRate(ss.getGsmBitErrorRate());
            li.setCdmaDbm(ss.getCdmaDbm());
            li.setCdmaEcio(ss.getCdmaEcio());
            li.setEvdoDbm(ss.getEvdoDbm());
            li.setEvdoEcio(ss.getEvdoEcio());
            li.setEvdoSnr(ss.getEvdoSnr());
            li.setLteSignalStrength(ss.getLteSignalStrength());
            li.setLteRsrp(ss.getLteRsrp());
            li.setLteRsrq(ss.getLteRsrq());
            li.setLteRssnr(ss.getLteRssnr());
            li.setLteCqi(ss.getLteCqi());
        }
        return li;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MobileDataStateTracker$NetworkDataEntry.class */
    public static class NetworkDataEntry {
        public int networkType;
        public int downloadBandwidth;
        public int uploadBandwidth;
        public int latency;

        NetworkDataEntry(int i1, int i2, int i3, int i4) {
            this.networkType = i1;
            this.downloadBandwidth = i2;
            this.uploadBandwidth = i3;
            this.latency = i4;
        }
    }

    private static NetworkDataEntry getNetworkDataEntry(int networkType) {
        NetworkDataEntry[] arr$ = mTheoreticalBWTable;
        for (NetworkDataEntry entry : arr$) {
            if (entry.networkType == networkType) {
                return entry;
            }
        }
        Slog.e(TAG, "Could not find Theoretical BW entry for " + String.valueOf(networkType));
        return null;
    }

    private static int getNormalizedSignalStrength(int networkType, SignalStrength ss) {
        int level;
        switch (networkType) {
            case 1:
            case 2:
            case 3:
            case 8:
            case 9:
            case 10:
            case 15:
                level = ss.getGsmLevel();
                break;
            case 4:
            case 7:
                level = ss.getCdmaLevel();
                break;
            case 5:
            case 6:
            case 12:
                level = ss.getEvdoLevel();
                break;
            case 11:
            case 14:
            default:
                return Integer.MAX_VALUE;
            case 13:
                level = ss.getLteLevel();
                break;
        }
        return (level * 100) / 5;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void startSampling(SamplingDataTracker.SamplingSnapshot s) {
        this.mSamplingDataTracker.startSampling(s);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void stopSampling(SamplingDataTracker.SamplingSnapshot s) {
        this.mSamplingDataTracker.stopSampling(s);
    }
}