package android.net;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Slog;

/* loaded from: DummyDataStateTracker.class */
public class DummyDataStateTracker extends BaseNetworkStateTracker {
    private static final String TAG = "DummyDataStateTracker";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    private Handler mTarget;
    private boolean mTeardownRequested = false;
    private boolean mPrivateDnsRouteSet = false;
    private boolean mDefaultRouteSet = false;
    private boolean mIsDefaultOrHipri = false;

    public DummyDataStateTracker(int netType, String tag) {
        this.mNetworkInfo = new NetworkInfo(netType);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void startMonitoring(Context context, Handler target) {
        this.mTarget = target;
        this.mContext = context;
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

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isAvailable() {
        return true;
    }

    @Override // android.net.NetworkStateTracker
    public String getTcpBufferSizesPropName() {
        return BaseNetworkStateTracker.PROP_TCP_BUFFER_UNKNOWN;
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        setDetailedState(NetworkInfo.DetailedState.DISCONNECTING, "disabled", null);
        setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, "disabled", null);
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckCompleted(boolean isCaptivePortal) {
    }

    private void setDetailedState(NetworkInfo.DetailedState state, String reason, String extraInfo) {
        log("setDetailed state, old =" + this.mNetworkInfo.getDetailedState() + " and new state=" + state);
        this.mNetworkInfo.setDetailedState(state, reason, extraInfo);
        Message msg = this.mTarget.obtainMessage(458752, this.mNetworkInfo);
        msg.sendToTarget();
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
        setDetailedState(NetworkInfo.DetailedState.CONNECTING, "enabled", null);
        setDetailedState(NetworkInfo.DetailedState.CONNECTED, "enabled", null);
        setTeardownRequested(false);
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean setRadio(boolean turnOn) {
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setUserDataEnable(boolean enabled) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setPolicyDataEnable(boolean enabled) {
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Dummy data state: none, dummy!");
        return sb.toString();
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

    private static void log(String s) {
        Slog.d(TAG, s);
    }

    private static void loge(String s) {
        Slog.e(TAG, s);
    }
}