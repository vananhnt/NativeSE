package com.android.server.net;

import android.net.INetworkManagementEventObserver;

/* loaded from: BaseNetworkObserver.class */
public class BaseNetworkObserver extends INetworkManagementEventObserver.Stub {
    public void interfaceStatusChanged(String iface, boolean up) {
    }

    public void interfaceRemoved(String iface) {
    }

    public void addressUpdated(String address, String iface, int flags, int scope) {
    }

    public void addressRemoved(String address, String iface, int flags, int scope) {
    }

    public void interfaceLinkStateChanged(String iface, boolean up) {
    }

    public void interfaceAdded(String iface) {
    }

    public void interfaceClassDataActivityChanged(String label, boolean active) {
    }

    @Override // android.net.INetworkManagementEventObserver
    public void limitReached(String limitName, String iface) {
    }
}