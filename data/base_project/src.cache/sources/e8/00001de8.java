package com.android.server.connectivity;

import android.content.Context;
import android.net.IConnectivityManager;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkStateTracker;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.net.BaseNetworkObserver;
import java.net.Inet4Address;

/* loaded from: Nat464Xlat.class */
public class Nat464Xlat extends BaseNetworkObserver {
    private Context mContext;
    private INetworkManagementService mNMService;
    private IConnectivityManager mConnService;
    private NetworkStateTracker mTracker;
    private Handler mHandler;
    private boolean mIsStarted = false;
    private boolean mIsRunning = false;
    private LinkProperties mLP = new LinkProperties();
    private static final String CLAT_INTERFACE_NAME = "clat4";
    private static final String TAG = "Nat464Xlat";

    public Nat464Xlat(Context context, INetworkManagementService nmService, IConnectivityManager connService, Handler handler) {
        this.mContext = context;
        this.mNMService = nmService;
        this.mConnService = connService;
        this.mHandler = handler;
    }

    public boolean requiresClat(int netType, NetworkStateTracker tracker) {
        LinkProperties lp = tracker.getLinkProperties();
        Slog.d(TAG, "requiresClat: netType=" + netType + ", hasIPv4Address=" + lp.hasIPv4Address());
        return netType == 0 && !lp.hasIPv4Address();
    }

    public static boolean isRunningClat(LinkProperties lp) {
        return lp != null && lp.getAllInterfaceNames().contains(CLAT_INTERFACE_NAME);
    }

    public void startClat(NetworkStateTracker tracker) {
        if (this.mIsStarted) {
            Slog.e(TAG, "startClat: already started");
            return;
        }
        this.mTracker = tracker;
        LinkProperties lp = this.mTracker.getLinkProperties();
        String iface = lp.getInterfaceName();
        Slog.i(TAG, "Starting clatd on " + iface + ", lp=" + lp);
        try {
            this.mNMService.startClatd(iface);
        } catch (RemoteException e) {
            Slog.e(TAG, "Error starting clat daemon: " + e);
        }
        this.mIsStarted = true;
    }

    public void stopClat() {
        if (this.mIsStarted) {
            Slog.i(TAG, "Stopping clatd");
            try {
                this.mNMService.stopClatd();
            } catch (RemoteException e) {
                Slog.e(TAG, "Error stopping clat daemon: " + e);
            }
            this.mIsStarted = false;
            this.mIsRunning = false;
            this.mTracker = null;
            this.mLP.clear();
            return;
        }
        Slog.e(TAG, "stopClat: already stopped");
    }

    public boolean isStarted() {
        return this.mIsStarted;
    }

    public boolean isRunning() {
        return this.mIsRunning;
    }

    @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
    public void interfaceAdded(String iface) {
        if (iface.equals(CLAT_INTERFACE_NAME)) {
            Slog.i(TAG, "interface clat4 added, mIsRunning = " + this.mIsRunning + " -> true");
            this.mIsRunning = true;
            try {
                InterfaceConfiguration config = this.mNMService.getInterfaceConfig(iface);
                LinkAddress clatAddress = config.getLinkAddress();
                this.mLP.clear();
                this.mLP.setInterfaceName(iface);
                RouteInfo ipv4Default = new RouteInfo(new LinkAddress(Inet4Address.ANY, 0), clatAddress.getAddress(), iface);
                this.mLP.addRoute(ipv4Default);
                this.mLP.addLinkAddress(clatAddress);
                this.mTracker.addStackedLink(this.mLP);
                Slog.i(TAG, "Adding stacked link. tracker LP: " + this.mTracker.getLinkProperties());
            } catch (RemoteException e) {
                Slog.e(TAG, "Error getting link properties: " + e);
            }
            Message msg = this.mHandler.obtainMessage(NetworkStateTracker.EVENT_CONFIGURATION_CHANGED, this.mTracker.getNetworkInfo());
            Slog.i(TAG, "sending message to ConnectivityService: " + msg);
            msg.sendToTarget();
        }
    }

    @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
    public void interfaceRemoved(String iface) {
        if (iface == CLAT_INTERFACE_NAME) {
            if (this.mIsRunning) {
                NetworkUtils.resetConnections(CLAT_INTERFACE_NAME, 1);
            }
            Slog.i(TAG, "interface clat4 removed, mIsRunning = " + this.mIsRunning + " -> false");
            this.mIsRunning = false;
            this.mTracker.removeStackedLink(this.mLP);
            this.mLP.clear();
            Slog.i(TAG, "mLP = " + this.mLP);
        }
    }
}