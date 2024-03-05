package com.android.server.net;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.security.Credentials;
import android.security.KeyStore;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.internal.util.Preconditions;
import com.android.server.ConnectivityService;
import com.android.server.EventLogTags;
import com.android.server.connectivity.Vpn;
import java.util.List;

/* loaded from: LockdownVpnTracker.class */
public class LockdownVpnTracker {
    private static final String TAG = "LockdownVpnTracker";
    private static final int MAX_ERROR_COUNT = 4;
    private static final String ACTION_LOCKDOWN_RESET = "com.android.server.action.LOCKDOWN_RESET";
    private static final String ACTION_VPN_SETTINGS = "android.net.vpn.SETTINGS";
    private static final String EXTRA_PICK_LOCKDOWN = "android.net.vpn.PICK_LOCKDOWN";
    private final Context mContext;
    private final INetworkManagementService mNetService;
    private final ConnectivityService mConnService;
    private final Vpn mVpn;
    private final VpnProfile mProfile;
    private final PendingIntent mConfigIntent;
    private final PendingIntent mResetIntent;
    private String mAcceptedEgressIface;
    private String mAcceptedIface;
    private List<LinkAddress> mAcceptedSourceAddr;
    private int mErrorCount;
    private final Object mStateLock = new Object();
    private BroadcastReceiver mResetReceiver = new BroadcastReceiver() { // from class: com.android.server.net.LockdownVpnTracker.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            LockdownVpnTracker.this.reset();
        }
    };

    public static boolean isEnabled() {
        return KeyStore.getInstance().contains(Credentials.LOCKDOWN_VPN);
    }

    public LockdownVpnTracker(Context context, INetworkManagementService netService, ConnectivityService connService, Vpn vpn, VpnProfile profile) {
        this.mContext = (Context) Preconditions.checkNotNull(context);
        this.mNetService = (INetworkManagementService) Preconditions.checkNotNull(netService);
        this.mConnService = (ConnectivityService) Preconditions.checkNotNull(connService);
        this.mVpn = (Vpn) Preconditions.checkNotNull(vpn);
        this.mProfile = (VpnProfile) Preconditions.checkNotNull(profile);
        Intent configIntent = new Intent(ACTION_VPN_SETTINGS);
        configIntent.putExtra(EXTRA_PICK_LOCKDOWN, true);
        this.mConfigIntent = PendingIntent.getActivity(this.mContext, 0, configIntent, 0);
        Intent resetIntent = new Intent(ACTION_LOCKDOWN_RESET);
        resetIntent.addFlags(1073741824);
        this.mResetIntent = PendingIntent.getBroadcast(this.mContext, 0, resetIntent, 0);
    }

    private void handleStateChangedLocked() {
        Slog.d(TAG, "handleStateChanged()");
        NetworkInfo egressInfo = this.mConnService.getActiveNetworkInfoUnfiltered();
        LinkProperties egressProp = this.mConnService.getActiveLinkProperties();
        NetworkInfo vpnInfo = this.mVpn.getNetworkInfo();
        VpnConfig vpnConfig = this.mVpn.getLegacyVpnConfig();
        boolean egressDisconnected = egressInfo == null || NetworkInfo.State.DISCONNECTED.equals(egressInfo.getState());
        boolean egressChanged = egressProp == null || !TextUtils.equals(this.mAcceptedEgressIface, egressProp.getInterfaceName());
        if (egressDisconnected || egressChanged) {
            clearSourceRulesLocked();
            this.mAcceptedEgressIface = null;
            this.mVpn.stopLegacyVpn();
        }
        if (egressDisconnected) {
            hideNotification();
            return;
        }
        int egressType = egressInfo.getType();
        if (vpnInfo.getDetailedState() == NetworkInfo.DetailedState.FAILED) {
            EventLogTags.writeLockdownVpnError(egressType);
        }
        if (this.mErrorCount > 4) {
            showNotification(R.string.vpn_lockdown_error, R.drawable.vpn_disconnected);
        } else if (egressInfo.isConnected() && !vpnInfo.isConnectedOrConnecting()) {
            if (this.mProfile.isValidLockdownProfile()) {
                Slog.d(TAG, "Active network connected; starting VPN");
                EventLogTags.writeLockdownVpnConnecting(egressType);
                showNotification(R.string.vpn_lockdown_connecting, R.drawable.vpn_disconnected);
                this.mAcceptedEgressIface = egressProp.getInterfaceName();
                try {
                    this.mVpn.startLegacyVpn(this.mProfile, KeyStore.getInstance(), egressProp);
                    return;
                } catch (IllegalStateException e) {
                    this.mAcceptedEgressIface = null;
                    Slog.e(TAG, "Failed to start VPN", e);
                    showNotification(R.string.vpn_lockdown_error, R.drawable.vpn_disconnected);
                    return;
                }
            }
            Slog.e(TAG, "Invalid VPN profile; requires IP-based server and DNS");
            showNotification(R.string.vpn_lockdown_error, R.drawable.vpn_disconnected);
        } else if (vpnInfo.isConnected() && vpnConfig != null) {
            String iface = vpnConfig.interfaze;
            List<LinkAddress> sourceAddrs = vpnConfig.addresses;
            if (TextUtils.equals(iface, this.mAcceptedIface) && sourceAddrs.equals(this.mAcceptedSourceAddr)) {
                return;
            }
            Slog.d(TAG, "VPN connected using iface=" + iface + ", sourceAddr=" + sourceAddrs.toString());
            EventLogTags.writeLockdownVpnConnected(egressType);
            showNotification(R.string.vpn_lockdown_connected, R.drawable.vpn_connected);
            try {
                clearSourceRulesLocked();
                this.mNetService.setFirewallInterfaceRule(iface, true);
                for (LinkAddress addr : sourceAddrs) {
                    this.mNetService.setFirewallEgressSourceRule(addr.toString(), true);
                }
                this.mErrorCount = 0;
                this.mAcceptedIface = iface;
                this.mAcceptedSourceAddr = sourceAddrs;
                this.mConnService.sendConnectedBroadcast(augmentNetworkInfo(egressInfo));
            } catch (RemoteException e2) {
                throw new RuntimeException("Problem setting firewall rules", e2);
            }
        }
    }

    public void init() {
        synchronized (this.mStateLock) {
            initLocked();
        }
    }

    private void initLocked() {
        Slog.d(TAG, "initLocked()");
        this.mVpn.setEnableNotifications(false);
        this.mVpn.setEnableTeardown(false);
        IntentFilter resetFilter = new IntentFilter(ACTION_LOCKDOWN_RESET);
        this.mContext.registerReceiver(this.mResetReceiver, resetFilter, Manifest.permission.CONNECTIVITY_INTERNAL, null);
        try {
            this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 500, true);
            this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 4500, true);
            this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 1701, true);
            synchronized (this.mStateLock) {
                handleStateChangedLocked();
            }
        } catch (RemoteException e) {
            throw new RuntimeException("Problem setting firewall rules", e);
        }
    }

    public void shutdown() {
        synchronized (this.mStateLock) {
            shutdownLocked();
        }
    }

    private void shutdownLocked() {
        Slog.d(TAG, "shutdownLocked()");
        this.mAcceptedEgressIface = null;
        this.mErrorCount = 0;
        this.mVpn.stopLegacyVpn();
        try {
            this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 500, false);
            this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 4500, false);
            this.mNetService.setFirewallEgressDestRule(this.mProfile.server, 1701, false);
            clearSourceRulesLocked();
            hideNotification();
            this.mContext.unregisterReceiver(this.mResetReceiver);
            this.mVpn.setEnableNotifications(true);
            this.mVpn.setEnableTeardown(true);
        } catch (RemoteException e) {
            throw new RuntimeException("Problem setting firewall rules", e);
        }
    }

    public void reset() {
        synchronized (this.mStateLock) {
            shutdownLocked();
            initLocked();
            handleStateChangedLocked();
        }
    }

    private void clearSourceRulesLocked() {
        try {
            if (this.mAcceptedIface != null) {
                this.mNetService.setFirewallInterfaceRule(this.mAcceptedIface, false);
                this.mAcceptedIface = null;
            }
            if (this.mAcceptedSourceAddr != null) {
                for (LinkAddress addr : this.mAcceptedSourceAddr) {
                    this.mNetService.setFirewallEgressSourceRule(addr.toString(), false);
                }
                this.mAcceptedSourceAddr = null;
            }
        } catch (RemoteException e) {
            throw new RuntimeException("Problem setting firewall rules", e);
        }
    }

    public void onNetworkInfoChanged(NetworkInfo info) {
        synchronized (this.mStateLock) {
            handleStateChangedLocked();
        }
    }

    public void onVpnStateChanged(NetworkInfo info) {
        if (info.getDetailedState() == NetworkInfo.DetailedState.FAILED) {
            this.mErrorCount++;
        }
        synchronized (this.mStateLock) {
            handleStateChangedLocked();
        }
    }

    public NetworkInfo augmentNetworkInfo(NetworkInfo info) {
        if (info.isConnected()) {
            NetworkInfo vpnInfo = this.mVpn.getNetworkInfo();
            info = new NetworkInfo(info);
            info.setDetailedState(vpnInfo.getDetailedState(), vpnInfo.getReason(), null);
        }
        return info;
    }

    private void showNotification(int titleRes, int iconRes) {
        Notification.Builder builder = new Notification.Builder(this.mContext);
        builder.setWhen(0L);
        builder.setSmallIcon(iconRes);
        builder.setContentTitle(this.mContext.getString(titleRes));
        builder.setContentText(this.mContext.getString(R.string.vpn_lockdown_config));
        builder.setContentIntent(this.mConfigIntent);
        builder.setPriority(-1);
        builder.setOngoing(true);
        builder.addAction(R.drawable.ic_menu_refresh, this.mContext.getString(R.string.reset), this.mResetIntent);
        NotificationManager.from(this.mContext).notify(TAG, 0, builder.build());
    }

    private void hideNotification() {
        NotificationManager.from(this.mContext).cancel(TAG, 0);
    }
}