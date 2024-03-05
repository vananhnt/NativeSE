package com.android.internal.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.android.dex.DexFormat;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/* loaded from: VpnProfile.class */
public class VpnProfile implements Cloneable, Parcelable {
    private static final String TAG = "VpnProfile";
    public static final int TYPE_PPTP = 0;
    public static final int TYPE_L2TP_IPSEC_PSK = 1;
    public static final int TYPE_L2TP_IPSEC_RSA = 2;
    public static final int TYPE_IPSEC_XAUTH_PSK = 3;
    public static final int TYPE_IPSEC_XAUTH_RSA = 4;
    public static final int TYPE_IPSEC_HYBRID_RSA = 5;
    public static final int TYPE_MAX = 5;
    public final String key;
    public String name;
    public int type;
    public String server;
    public String username;
    public String password;
    public String dnsServers;
    public String searchDomains;
    public String routes;
    public boolean mppe;
    public String l2tpSecret;
    public String ipsecIdentifier;
    public String ipsecSecret;
    public String ipsecUserCert;
    public String ipsecCaCert;
    public String ipsecServerCert;
    public boolean saveLogin;
    public static final Parcelable.Creator<VpnProfile> CREATOR = new Parcelable.Creator<VpnProfile>() { // from class: com.android.internal.net.VpnProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VpnProfile createFromParcel(Parcel in) {
            return new VpnProfile(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VpnProfile[] newArray(int size) {
            return new VpnProfile[size];
        }
    };

    public VpnProfile(String key) {
        this.name = "";
        this.type = 0;
        this.server = "";
        this.username = "";
        this.password = "";
        this.dnsServers = "";
        this.searchDomains = "";
        this.routes = "";
        this.mppe = true;
        this.l2tpSecret = "";
        this.ipsecIdentifier = "";
        this.ipsecSecret = "";
        this.ipsecUserCert = "";
        this.ipsecCaCert = "";
        this.ipsecServerCert = "";
        this.saveLogin = false;
        this.key = key;
    }

    public VpnProfile(Parcel in) {
        this.name = "";
        this.type = 0;
        this.server = "";
        this.username = "";
        this.password = "";
        this.dnsServers = "";
        this.searchDomains = "";
        this.routes = "";
        this.mppe = true;
        this.l2tpSecret = "";
        this.ipsecIdentifier = "";
        this.ipsecSecret = "";
        this.ipsecUserCert = "";
        this.ipsecCaCert = "";
        this.ipsecServerCert = "";
        this.saveLogin = false;
        this.key = in.readString();
        this.name = in.readString();
        this.type = in.readInt();
        this.server = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.dnsServers = in.readString();
        this.searchDomains = in.readString();
        this.routes = in.readString();
        this.mppe = in.readInt() != 0;
        this.l2tpSecret = in.readString();
        this.ipsecIdentifier = in.readString();
        this.ipsecSecret = in.readString();
        this.ipsecUserCert = in.readString();
        this.ipsecCaCert = in.readString();
        this.ipsecServerCert = in.readString();
        this.saveLogin = in.readInt() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.key);
        out.writeString(this.name);
        out.writeInt(this.type);
        out.writeString(this.server);
        out.writeString(this.username);
        out.writeString(this.password);
        out.writeString(this.dnsServers);
        out.writeString(this.searchDomains);
        out.writeString(this.routes);
        out.writeInt(this.mppe ? 1 : 0);
        out.writeString(this.l2tpSecret);
        out.writeString(this.ipsecIdentifier);
        out.writeString(this.ipsecSecret);
        out.writeString(this.ipsecUserCert);
        out.writeString(this.ipsecCaCert);
        out.writeString(this.ipsecServerCert);
        out.writeInt(this.saveLogin ? 1 : 0);
    }

    public static VpnProfile decode(String key, byte[] value) {
        if (key == null) {
            return null;
        }
        try {
            String[] values = new String(value, StandardCharsets.UTF_8).split(DexFormat.MAGIC_SUFFIX, -1);
            if (values.length < 14 || values.length > 15) {
                return null;
            }
            VpnProfile profile = new VpnProfile(key);
            profile.name = values[0];
            profile.type = Integer.valueOf(values[1]).intValue();
            if (profile.type < 0 || profile.type > 5) {
                return null;
            }
            profile.server = values[2];
            profile.username = values[3];
            profile.password = values[4];
            profile.dnsServers = values[5];
            profile.searchDomains = values[6];
            profile.routes = values[7];
            profile.mppe = Boolean.valueOf(values[8]).booleanValue();
            profile.l2tpSecret = values[9];
            profile.ipsecIdentifier = values[10];
            profile.ipsecSecret = values[11];
            profile.ipsecUserCert = values[12];
            profile.ipsecCaCert = values[13];
            profile.ipsecServerCert = values.length > 14 ? values[14] : "";
            profile.saveLogin = (profile.username.isEmpty() && profile.password.isEmpty()) ? false : true;
            return profile;
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] encode() {
        StringBuilder builder = new StringBuilder(this.name);
        builder.append((char) 0).append(this.type);
        builder.append((char) 0).append(this.server);
        builder.append((char) 0).append(this.saveLogin ? this.username : "");
        builder.append((char) 0).append(this.saveLogin ? this.password : "");
        builder.append((char) 0).append(this.dnsServers);
        builder.append((char) 0).append(this.searchDomains);
        builder.append((char) 0).append(this.routes);
        builder.append((char) 0).append(this.mppe);
        builder.append((char) 0).append(this.l2tpSecret);
        builder.append((char) 0).append(this.ipsecIdentifier);
        builder.append((char) 0).append(this.ipsecSecret);
        builder.append((char) 0).append(this.ipsecUserCert);
        builder.append((char) 0).append(this.ipsecCaCert);
        builder.append((char) 0).append(this.ipsecServerCert);
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public boolean isValidLockdownProfile() {
        try {
            InetAddress.parseNumericAddress(this.server);
            String[] arr$ = this.dnsServers.split(" +");
            for (String str : arr$) {
                InetAddress.parseNumericAddress(this.dnsServers);
            }
            if (TextUtils.isEmpty(this.dnsServers)) {
                Log.w(TAG, "DNS required");
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Invalid address", e);
            return false;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}