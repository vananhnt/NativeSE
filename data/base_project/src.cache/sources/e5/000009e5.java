package android.net.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.BaseNetworkStateTracker;
import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.net.LinkQualityInfo;
import android.net.NetworkInfo;
import android.net.NetworkStateTracker;
import android.net.SamplingDataTracker;
import android.net.WifiLinkQualityInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Slog;
import gov.nist.core.Separators;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: WifiStateTracker.class */
public class WifiStateTracker extends BaseNetworkStateTracker {
    private static final String NETWORKTYPE = "WIFI";
    private static final String TAG = "WifiStateTracker";
    private static final boolean LOGV = true;
    private WifiInfo mWifiInfo;
    private Handler mCsHandler;
    private BroadcastReceiver mWifiStateReceiver;
    private WifiManager mWifiManager;
    private AtomicBoolean mTeardownRequested = new AtomicBoolean(false);
    private AtomicBoolean mPrivateDnsRouteSet = new AtomicBoolean(false);
    private AtomicBoolean mDefaultRouteSet = new AtomicBoolean(false);
    private NetworkInfo.State mLastState = NetworkInfo.State.UNKNOWN;
    private SamplingDataTracker mSamplingDataTracker = new SamplingDataTracker();

    public WifiStateTracker(int netType, String networkName) {
        this.mNetworkInfo = new NetworkInfo(netType, 0, networkName, "");
        this.mLinkProperties = new LinkProperties();
        this.mLinkCapabilities = new LinkCapabilities();
        this.mNetworkInfo.setIsAvailable(false);
        setTeardownRequested(false);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setTeardownRequested(boolean isRequested) {
        this.mTeardownRequested.set(isRequested);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isTeardownRequested() {
        return this.mTeardownRequested.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void startMonitoring(Context context, Handler target) {
        this.mCsHandler = target;
        this.mContext = context;
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        this.mWifiStateReceiver = new WifiStateReceiver();
        this.mContext.registerReceiver(this.mWifiStateReceiver, filter);
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        this.mTeardownRequested.set(true);
        this.mWifiManager.stopWifi();
        return true;
    }

    @Override // android.net.NetworkStateTracker
    public boolean reconnect() {
        this.mTeardownRequested.set(false);
        this.mWifiManager.startWifi();
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
        this.mWifiManager.captivePortalCheckComplete();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckCompleted(boolean isCaptivePortal) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean setRadio(boolean turnOn) {
        this.mWifiManager.setWifiEnabled(turnOn);
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isAvailable() {
        return this.mNetworkInfo.isAvailable();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setUserDataEnable(boolean enabled) {
        Slog.w(TAG, "ignoring setUserDataEnable(" + enabled + Separators.RPAREN);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setPolicyDataEnable(boolean enabled) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isPrivateDnsRouteSet() {
        return this.mPrivateDnsRouteSet.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void privateDnsRouteSet(boolean enabled) {
        this.mPrivateDnsRouteSet.set(enabled);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public NetworkInfo getNetworkInfo() {
        return new NetworkInfo(this.mNetworkInfo);
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
    public LinkQualityInfo getLinkQualityInfo() {
        if (this.mNetworkInfo == null) {
            return null;
        }
        WifiLinkQualityInfo li = new WifiLinkQualityInfo();
        li.setNetworkType(this.mNetworkInfo.getType());
        synchronized (this.mSamplingDataTracker.mSamplingDataLock) {
            this.mSamplingDataTracker.setCommonLinkQualityInfoFields(li);
            li.setTxGood(this.mSamplingDataTracker.getSampledTxPacketCount());
            li.setTxBad(this.mSamplingDataTracker.getSampledTxPacketErrorCount());
        }
        if (this.mWifiInfo != null) {
            li.setBssid(this.mWifiInfo.getBSSID());
            int rssi = this.mWifiInfo.getRssi();
            li.setRssi(rssi);
            WifiManager wifiManager = this.mWifiManager;
            li.setNormalizedSignalStrength(WifiManager.calculateSignalLevel(rssi, 100));
        }
        return li;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isDefaultRouteSet() {
        return this.mDefaultRouteSet.get();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void defaultRouteSet(boolean enabled) {
        this.mDefaultRouteSet.set(enabled);
    }

    @Override // android.net.NetworkStateTracker
    public String getTcpBufferSizesPropName() {
        return BaseNetworkStateTracker.PROP_TCP_BUFFER_WIFI;
    }

    /* loaded from: WifiStateTracker$WifiStateReceiver.class */
    private class WifiStateReceiver extends BroadcastReceiver {
        private WifiStateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                if (intent.getAction().equals(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION)) {
                    WifiStateTracker.this.mLinkProperties = (LinkProperties) intent.getParcelableExtra("linkProperties");
                    Message msg = WifiStateTracker.this.mCsHandler.obtainMessage(NetworkStateTracker.EVENT_CONFIGURATION_CHANGED, WifiStateTracker.this.mNetworkInfo);
                    msg.sendToTarget();
                    return;
                }
                return;
            }
            WifiStateTracker.this.mNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            WifiStateTracker.this.mLinkProperties = (LinkProperties) intent.getParcelableExtra("linkProperties");
            if (WifiStateTracker.this.mLinkProperties == null) {
                WifiStateTracker.this.mLinkProperties = new LinkProperties();
            }
            WifiStateTracker.this.mLinkCapabilities = (LinkCapabilities) intent.getParcelableExtra("linkCapabilities");
            if (WifiStateTracker.this.mLinkCapabilities == null) {
                WifiStateTracker.this.mLinkCapabilities = new LinkCapabilities();
            }
            WifiStateTracker.this.mWifiInfo = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            NetworkInfo.State state = WifiStateTracker.this.mNetworkInfo.getState();
            if (WifiStateTracker.this.mLastState != state || WifiStateTracker.this.mNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK) {
                WifiStateTracker.this.mLastState = state;
                WifiStateTracker.this.mSamplingDataTracker.resetSamplingData();
                Message msg2 = WifiStateTracker.this.mCsHandler.obtainMessage(458752, new NetworkInfo(WifiStateTracker.this.mNetworkInfo));
                msg2.sendToTarget();
            }
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setDependencyMet(boolean met) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void addStackedLink(LinkProperties link) {
        this.mLinkProperties.addStackedLink(link);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void removeStackedLink(LinkProperties link) {
        this.mLinkProperties.removeStackedLink(link);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void supplyMessenger(Messenger messenger) {
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