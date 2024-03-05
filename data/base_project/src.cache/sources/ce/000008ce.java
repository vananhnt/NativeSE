package android.net;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.Messenger;
import android.os.ServiceManager;
import android.util.Log;
import com.android.server.net.BaseNetworkObserver;
import gov.nist.core.Separators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: EthernetDataTracker.class */
public class EthernetDataTracker extends BaseNetworkStateTracker {
    private static final String NETWORKTYPE = "ETHERNET";
    private static final String TAG = "Ethernet";
    private AtomicBoolean mTeardownRequested = new AtomicBoolean(false);
    private AtomicBoolean mPrivateDnsRouteSet = new AtomicBoolean(false);
    private AtomicInteger mDefaultGatewayAddr = new AtomicInteger(0);
    private AtomicBoolean mDefaultRouteSet = new AtomicBoolean(false);
    private static boolean mLinkUp;
    private InterfaceObserver mInterfaceObserver;
    private String mHwAddr;
    private Handler mCsHandler;
    private static EthernetDataTracker sInstance;
    private static String sIfaceMatch = "";
    private static String mIface = "";
    private INetworkManagementService mNMService;

    /* loaded from: EthernetDataTracker$InterfaceObserver.class */
    private static class InterfaceObserver extends BaseNetworkObserver {
        private EthernetDataTracker mTracker;

        InterfaceObserver(EthernetDataTracker tracker) {
            this.mTracker = tracker;
        }

        @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
        public void interfaceStatusChanged(String iface, boolean up) {
            Log.d(EthernetDataTracker.TAG, "Interface status changed: " + iface + (up ? "up" : "down"));
        }

        @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
        public void interfaceLinkStateChanged(String iface, boolean up) {
            if (EthernetDataTracker.mIface.equals(iface)) {
                Log.d(EthernetDataTracker.TAG, "Interface " + iface + " link " + (up ? "up" : "down"));
                boolean unused = EthernetDataTracker.mLinkUp = up;
                this.mTracker.mNetworkInfo.setIsAvailable(up);
                if (up) {
                    this.mTracker.reconnect();
                } else {
                    this.mTracker.disconnect();
                }
            }
        }

        @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
        public void interfaceAdded(String iface) {
            this.mTracker.interfaceAdded(iface);
        }

        @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
        public void interfaceRemoved(String iface) {
            this.mTracker.interfaceRemoved(iface);
        }
    }

    private EthernetDataTracker() {
        this.mNetworkInfo = new NetworkInfo(9, 0, NETWORKTYPE, "");
        this.mLinkProperties = new LinkProperties();
        this.mLinkCapabilities = new LinkCapabilities();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void interfaceAdded(String iface) {
        if (!iface.matches(sIfaceMatch)) {
            return;
        }
        Log.d(TAG, "Adding " + iface);
        synchronized (this) {
            if (mIface.isEmpty()) {
                mIface = iface;
                try {
                    this.mNMService.setInterfaceUp(iface);
                } catch (Exception e) {
                    Log.e(TAG, "Error upping interface " + iface + ": " + e);
                }
                this.mNetworkInfo.setIsAvailable(true);
                Message msg = this.mCsHandler.obtainMessage(NetworkStateTracker.EVENT_CONFIGURATION_CHANGED, this.mNetworkInfo);
                msg.sendToTarget();
            }
        }
    }

    public void disconnect() {
        NetworkUtils.stopDhcp(mIface);
        this.mLinkProperties.clear();
        this.mNetworkInfo.setIsAvailable(false);
        this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, null, this.mHwAddr);
        Message msg = this.mCsHandler.obtainMessage(NetworkStateTracker.EVENT_CONFIGURATION_CHANGED, this.mNetworkInfo);
        msg.sendToTarget();
        Message msg2 = this.mCsHandler.obtainMessage(458752, this.mNetworkInfo);
        msg2.sendToTarget();
        IBinder b = ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE);
        INetworkManagementService service = INetworkManagementService.Stub.asInterface(b);
        try {
            service.clearInterfaceAddresses(mIface);
        } catch (Exception e) {
            Log.e(TAG, "Failed to clear addresses or disable ipv6" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void interfaceRemoved(String iface) {
        if (!iface.equals(mIface)) {
            return;
        }
        Log.d(TAG, "Removing " + iface);
        disconnect();
        mIface = "";
    }

    private void runDhcp() {
        Thread dhcpThread = new Thread(new Runnable() { // from class: android.net.EthernetDataTracker.1
            @Override // java.lang.Runnable
            public void run() {
                DhcpResults dhcpResults = new DhcpResults();
                if (!NetworkUtils.runDhcp(EthernetDataTracker.mIface, dhcpResults)) {
                    Log.e(EthernetDataTracker.TAG, "DHCP request error:" + NetworkUtils.getDhcpError());
                    return;
                }
                EthernetDataTracker.this.mLinkProperties = dhcpResults.linkProperties;
                EthernetDataTracker.this.mNetworkInfo.setIsAvailable(true);
                EthernetDataTracker.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, null, EthernetDataTracker.this.mHwAddr);
                Message msg = EthernetDataTracker.this.mCsHandler.obtainMessage(458752, EthernetDataTracker.this.mNetworkInfo);
                msg.sendToTarget();
            }
        });
        dhcpThread.start();
    }

    public static synchronized EthernetDataTracker getInstance() {
        if (sInstance == null) {
            sInstance = new EthernetDataTracker();
        }
        return sInstance;
    }

    public Object Clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setTeardownRequested(boolean isRequested) {
        this.mTeardownRequested.set(isRequested);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean isTeardownRequested() {
        return this.mTeardownRequested.get();
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0090, code lost:
        if (r5.mHwAddr != null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0093, code lost:
        r5.mHwAddr = r0.getHardwareAddress();
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x00a0, code lost:
        if (r5.mHwAddr == null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x00a3, code lost:
        r5.mNetworkInfo.setExtraInfo(r5.mHwAddr);
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x00ae, code lost:
        android.net.NetworkUtils.stopDhcp(android.net.EthernetDataTracker.mIface);
        reconnect();
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0060, code lost:
        android.net.EthernetDataTracker.mIface = r0;
        r5.mNMService.setInterfaceUp(r0);
        r0 = r5.mNMService.getInterfaceConfig(r0);
        android.net.EthernetDataTracker.mLinkUp = r0.hasFlag("up");
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0089, code lost:
        if (r0 == null) goto L17;
     */
    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void startMonitoring(android.content.Context r6, android.os.Handler r7) {
        /*
            Method dump skipped, instructions count: 271
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.EthernetDataTracker.startMonitoring(android.content.Context, android.os.Handler):void");
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        this.mTeardownRequested.set(true);
        NetworkUtils.stopDhcp(mIface);
        return true;
    }

    @Override // android.net.NetworkStateTracker
    public boolean reconnect() {
        if (mLinkUp) {
            this.mTeardownRequested.set(false);
            runDhcp();
        }
        return mLinkUp;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckCompleted(boolean isCaptivePortal) {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public boolean setRadio(boolean turnOn) {
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public synchronized boolean isAvailable() {
        return this.mNetworkInfo.isAvailable();
    }

    public int startUsingNetworkFeature(String feature, int callingPid, int callingUid) {
        return -1;
    }

    public int stopUsingNetworkFeature(String feature, int callingPid, int callingUid) {
        return -1;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setUserDataEnable(boolean enabled) {
        Log.w(TAG, "ignoring setUserDataEnable(" + enabled + Separators.RPAREN);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void setPolicyDataEnable(boolean enabled) {
        Log.w(TAG, "ignoring setPolicyDataEnable(" + enabled + Separators.RPAREN);
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
    public synchronized NetworkInfo getNetworkInfo() {
        return this.mNetworkInfo;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public synchronized LinkProperties getLinkProperties() {
        return new LinkProperties(this.mLinkProperties);
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkCapabilities getLinkCapabilities() {
        return new LinkCapabilities(this.mLinkCapabilities);
    }

    public int getDefaultGatewayAddr() {
        return this.mDefaultGatewayAddr.get();
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
}