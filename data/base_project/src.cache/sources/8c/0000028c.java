package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.net.BaseNetworkStateTracker;
import android.net.DhcpResults;
import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkStateTracker;
import android.net.NetworkUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.AsyncChannel;
import gov.nist.core.Separators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: BluetoothTetheringDataTracker.class */
public class BluetoothTetheringDataTracker extends BaseNetworkStateTracker {
    private static final String NETWORKTYPE = "BLUETOOTH_TETHER";
    private static final String TAG = "BluetoothTethering";
    private static final boolean DBG = true;
    private static final boolean VDBG = true;
    private BluetoothPan mBluetoothPan;
    private static String mRevTetheredIface;
    private Handler mCsHandler;
    private static BluetoothTetheringDataTracker sInstance;
    private BtdtHandler mBtdtHandler;
    private AtomicBoolean mTeardownRequested = new AtomicBoolean(false);
    private AtomicBoolean mPrivateDnsRouteSet = new AtomicBoolean(false);
    private AtomicInteger mDefaultGatewayAddr = new AtomicInteger(0);
    private AtomicBoolean mDefaultRouteSet = new AtomicBoolean(false);
    private final Object mLinkPropertiesLock = new Object();
    private final Object mNetworkInfoLock = new Object();
    private AtomicReference<AsyncChannel> mAsyncChannel = new AtomicReference<>(null);
    private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: android.bluetooth.BluetoothTetheringDataTracker.1
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            BluetoothTetheringDataTracker.this.mBluetoothPan = (BluetoothPan) proxy;
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int profile) {
            BluetoothTetheringDataTracker.this.mBluetoothPan = null;
        }
    };

    private BluetoothTetheringDataTracker() {
        this.mNetworkInfo = new NetworkInfo(7, 0, NETWORKTYPE, "");
        this.mLinkProperties = new LinkProperties();
        this.mLinkCapabilities = new LinkCapabilities();
        this.mNetworkInfo.setIsAvailable(false);
        setTeardownRequested(false);
    }

    public static synchronized BluetoothTetheringDataTracker getInstance() {
        if (sInstance == null) {
            sInstance = new BluetoothTetheringDataTracker();
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

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void startMonitoring(Context context, Handler target) {
        Log.d(TAG, "startMonitoring: target: " + target);
        this.mContext = context;
        this.mCsHandler = target;
        Log.d(TAG, "startMonitoring: mCsHandler: " + this.mCsHandler);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.getProfileProxy(this.mContext, this.mProfileServiceListener, 5);
        }
        this.mBtdtHandler = new BtdtHandler(target.getLooper(), this);
    }

    @Override // android.net.NetworkStateTracker
    public boolean teardown() {
        this.mTeardownRequested.set(true);
        if (this.mBluetoothPan != null) {
            for (BluetoothDevice device : this.mBluetoothPan.getConnectedDevices()) {
                this.mBluetoothPan.disconnect(device);
            }
            return true;
        }
        return true;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckComplete() {
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void captivePortalCheckCompleted(boolean isCaptivePortal) {
    }

    @Override // android.net.NetworkStateTracker
    public boolean reconnect() {
        this.mTeardownRequested.set(false);
        return true;
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
    public NetworkInfo getNetworkInfo() {
        NetworkInfo networkInfo;
        synchronized (this.mNetworkInfoLock) {
            networkInfo = new NetworkInfo(this.mNetworkInfo);
        }
        return networkInfo;
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public LinkProperties getLinkProperties() {
        LinkProperties linkProperties;
        synchronized (this.mLinkPropertiesLock) {
            linkProperties = new LinkProperties(this.mLinkProperties);
        }
        return linkProperties;
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

    private static short countPrefixLength(byte[] mask) {
        short count = 0;
        for (byte b : mask) {
            for (int i = 0; i < 8; i++) {
                if ((b & (1 << i)) != 0) {
                    count = (short) (count + 1);
                }
            }
        }
        return count;
    }

    void startReverseTether(final LinkProperties linkProperties) {
        if (linkProperties == null || TextUtils.isEmpty(linkProperties.getInterfaceName())) {
            Log.e(TAG, "attempted to reverse tether with empty interface");
            return;
        }
        synchronized (this.mLinkPropertiesLock) {
            if (this.mLinkProperties.getInterfaceName() != null) {
                Log.e(TAG, "attempted to reverse tether while already in process");
                return;
            }
            this.mLinkProperties = linkProperties;
            Thread dhcpThread = new Thread(new Runnable() { // from class: android.bluetooth.BluetoothTetheringDataTracker.2
                @Override // java.lang.Runnable
                public void run() {
                    DhcpResults dhcpResults = new DhcpResults();
                    boolean success = NetworkUtils.runDhcp(linkProperties.getInterfaceName(), dhcpResults);
                    synchronized (BluetoothTetheringDataTracker.this.mLinkPropertiesLock) {
                        if (linkProperties.getInterfaceName() != BluetoothTetheringDataTracker.this.mLinkProperties.getInterfaceName()) {
                            Log.e(BluetoothTetheringDataTracker.TAG, "obsolete DHCP run aborted");
                        } else if (!success) {
                            Log.e(BluetoothTetheringDataTracker.TAG, "DHCP request error:" + NetworkUtils.getDhcpError());
                        } else {
                            BluetoothTetheringDataTracker.this.mLinkProperties = dhcpResults.linkProperties;
                            synchronized (BluetoothTetheringDataTracker.this.mNetworkInfoLock) {
                                BluetoothTetheringDataTracker.this.mNetworkInfo.setIsAvailable(true);
                                BluetoothTetheringDataTracker.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, null, null);
                                if (BluetoothTetheringDataTracker.this.mCsHandler != null) {
                                    Message msg = BluetoothTetheringDataTracker.this.mCsHandler.obtainMessage(458752, new NetworkInfo(BluetoothTetheringDataTracker.this.mNetworkInfo));
                                    msg.sendToTarget();
                                }
                            }
                        }
                    }
                }
            });
            dhcpThread.start();
        }
    }

    void stopReverseTether() {
        synchronized (this.mLinkPropertiesLock) {
            if (TextUtils.isEmpty(this.mLinkProperties.getInterfaceName())) {
                Log.e(TAG, "attempted to stop reverse tether with nothing tethered");
                return;
            }
            NetworkUtils.stopDhcp(this.mLinkProperties.getInterfaceName());
            this.mLinkProperties.clear();
            synchronized (this.mNetworkInfoLock) {
                this.mNetworkInfo.setIsAvailable(false);
                this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, null, null);
                if (this.mCsHandler != null) {
                    this.mCsHandler.obtainMessage(458752, new NetworkInfo(this.mNetworkInfo)).sendToTarget();
                }
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

    /* loaded from: BluetoothTetheringDataTracker$BtdtHandler.class */
    static class BtdtHandler extends Handler {
        private AsyncChannel mStackChannel;
        private final BluetoothTetheringDataTracker mBtdt;

        BtdtHandler(Looper looper, BluetoothTetheringDataTracker parent) {
            super(looper);
            this.mBtdt = parent;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 69632:
                    Log.d(BluetoothTetheringDataTracker.TAG, "got CMD_CHANNEL_HALF_CONNECTED");
                    if (msg.arg1 == 0) {
                        AsyncChannel ac = (AsyncChannel) msg.obj;
                        if (!this.mBtdt.mAsyncChannel.compareAndSet(null, ac)) {
                            Log.e(BluetoothTetheringDataTracker.TAG, "Trying to set mAsyncChannel twice!");
                            return;
                        } else {
                            ac.sendMessage(AsyncChannel.CMD_CHANNEL_FULL_CONNECTION);
                            return;
                        }
                    }
                    return;
                case AsyncChannel.CMD_CHANNEL_DISCONNECTED /* 69636 */:
                    Log.d(BluetoothTetheringDataTracker.TAG, "got CMD_CHANNEL_DISCONNECTED");
                    this.mBtdt.stopReverseTether();
                    this.mBtdt.mAsyncChannel.set(null);
                    return;
                case NetworkStateTracker.EVENT_NETWORK_CONNECTED /* 458756 */:
                    LinkProperties linkProperties = (LinkProperties) msg.obj;
                    Log.d(BluetoothTetheringDataTracker.TAG, "got EVENT_NETWORK_CONNECTED, " + linkProperties);
                    this.mBtdt.startReverseTether(linkProperties);
                    return;
                case NetworkStateTracker.EVENT_NETWORK_DISCONNECTED /* 458757 */:
                    Log.d(BluetoothTetheringDataTracker.TAG, "got EVENT_NETWORK_DISCONNECTED, " + ((LinkProperties) msg.obj));
                    this.mBtdt.stopReverseTether();
                    return;
                default:
                    return;
            }
        }
    }

    @Override // android.net.BaseNetworkStateTracker, android.net.NetworkStateTracker
    public void supplyMessenger(Messenger messenger) {
        if (messenger != null) {
            new AsyncChannel().connect(this.mContext, this.mBtdtHandler, messenger);
        }
    }
}