package com.android.internal.net;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.LinkAddress;
import android.net.RouteInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import gov.nist.core.Separators;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/* loaded from: VpnConfig.class */
public class VpnConfig implements Parcelable {
    public static final String SERVICE_INTERFACE = "android.net.VpnService";
    public static final String DIALOGS_PACKAGE = "com.android.vpndialogs";
    public static final String LEGACY_VPN = "[Legacy VPN]";
    public String user;
    public String interfaze;
    public String session;
    public List<String> dnsServers;
    public List<String> searchDomains;
    public PendingIntent configureIntent;
    public boolean legacy;
    public static final Parcelable.Creator<VpnConfig> CREATOR = new Parcelable.Creator<VpnConfig>() { // from class: com.android.internal.net.VpnConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VpnConfig createFromParcel(Parcel in) {
            VpnConfig config = new VpnConfig();
            config.user = in.readString();
            config.interfaze = in.readString();
            config.session = in.readString();
            config.mtu = in.readInt();
            in.readTypedList(config.addresses, LinkAddress.CREATOR);
            in.readTypedList(config.routes, RouteInfo.CREATOR);
            config.dnsServers = in.createStringArrayList();
            config.searchDomains = in.createStringArrayList();
            config.configureIntent = (PendingIntent) in.readParcelable(null);
            config.startTime = in.readLong();
            config.legacy = in.readInt() != 0;
            return config;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VpnConfig[] newArray(int size) {
            return new VpnConfig[size];
        }
    };
    public int mtu = -1;
    public List<LinkAddress> addresses = new ArrayList();
    public List<RouteInfo> routes = new ArrayList();
    public long startTime = -1;

    public static Intent getIntentForConfirmation() {
        Intent intent = new Intent();
        intent.setClassName(DIALOGS_PACKAGE, "com.android.vpndialogs.ConfirmDialog");
        return intent;
    }

    public static PendingIntent getIntentForStatusPanel(Context context) {
        Intent intent = new Intent();
        intent.setClassName(DIALOGS_PACKAGE, "com.android.vpndialogs.ManageDialog");
        intent.addFlags(1350565888);
        return PendingIntent.getActivityAsUser(context, 0, intent, 0, null, UserHandle.CURRENT);
    }

    public void addLegacyRoutes(String routesStr) {
        if (routesStr.trim().equals("")) {
            return;
        }
        String[] routes = routesStr.trim().split(Separators.SP);
        for (String route : routes) {
            String[] split = route.split(Separators.SLASH);
            RouteInfo info = new RouteInfo(new LinkAddress(InetAddress.parseNumericAddress(split[0]), Integer.parseInt(split[1])), null);
            this.routes.add(info);
        }
    }

    public void addLegacyAddresses(String addressesStr) {
        if (addressesStr.trim().equals("")) {
            return;
        }
        String[] addresses = addressesStr.trim().split(Separators.SP);
        for (String address : addresses) {
            String[] split = address.split(Separators.SLASH);
            LinkAddress addr = new LinkAddress(InetAddress.parseNumericAddress(split[0]), Integer.parseInt(split[1]));
            this.addresses.add(addr);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.user);
        out.writeString(this.interfaze);
        out.writeString(this.session);
        out.writeInt(this.mtu);
        out.writeTypedList(this.addresses);
        out.writeTypedList(this.routes);
        out.writeStringList(this.dnsServers);
        out.writeStringList(this.searchDomains);
        out.writeParcelable(this.configureIntent, flags);
        out.writeLong(this.startTime);
        out.writeInt(this.legacy ? 1 : 0);
    }
}