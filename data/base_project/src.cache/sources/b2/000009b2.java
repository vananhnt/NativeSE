package android.net.wifi;

import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import gov.nist.core.Separators;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.Locale;

/* loaded from: WifiInfo.class */
public class WifiInfo implements Parcelable {
    private static final String TAG = "WifiInfo";
    private static final EnumMap<SupplicantState, NetworkInfo.DetailedState> stateMap = new EnumMap<>(SupplicantState.class);
    private SupplicantState mSupplicantState;
    private String mBSSID;
    private WifiSsid mWifiSsid;
    private int mNetworkId;
    private boolean mHiddenSSID;
    private int mRssi;
    public static final String LINK_SPEED_UNITS = "Mbps";
    private int mLinkSpeed;
    private InetAddress mIpAddress;
    private String mMacAddress;
    private boolean mMeteredHint;
    public static final Parcelable.Creator<WifiInfo> CREATOR;

    static {
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.DISCONNECTED, (SupplicantState) NetworkInfo.DetailedState.DISCONNECTED);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.INTERFACE_DISABLED, (SupplicantState) NetworkInfo.DetailedState.DISCONNECTED);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.INACTIVE, (SupplicantState) NetworkInfo.DetailedState.IDLE);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.SCANNING, (SupplicantState) NetworkInfo.DetailedState.SCANNING);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.AUTHENTICATING, (SupplicantState) NetworkInfo.DetailedState.CONNECTING);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.ASSOCIATING, (SupplicantState) NetworkInfo.DetailedState.CONNECTING);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.ASSOCIATED, (SupplicantState) NetworkInfo.DetailedState.CONNECTING);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.FOUR_WAY_HANDSHAKE, (SupplicantState) NetworkInfo.DetailedState.AUTHENTICATING);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.GROUP_HANDSHAKE, (SupplicantState) NetworkInfo.DetailedState.AUTHENTICATING);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.COMPLETED, (SupplicantState) NetworkInfo.DetailedState.OBTAINING_IPADDR);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.DORMANT, (SupplicantState) NetworkInfo.DetailedState.DISCONNECTED);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.UNINITIALIZED, (SupplicantState) NetworkInfo.DetailedState.IDLE);
        stateMap.put((EnumMap<SupplicantState, NetworkInfo.DetailedState>) SupplicantState.INVALID, (SupplicantState) NetworkInfo.DetailedState.FAILED);
        CREATOR = new Parcelable.Creator<WifiInfo>() { // from class: android.net.wifi.WifiInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public WifiInfo createFromParcel(Parcel in) {
                WifiInfo info = new WifiInfo();
                info.setNetworkId(in.readInt());
                info.setRssi(in.readInt());
                info.setLinkSpeed(in.readInt());
                if (in.readByte() == 1) {
                    try {
                        info.setInetAddress(InetAddress.getByAddress(in.createByteArray()));
                    } catch (UnknownHostException e) {
                    }
                }
                if (in.readInt() == 1) {
                    info.mWifiSsid = WifiSsid.CREATOR.createFromParcel(in);
                }
                info.mBSSID = in.readString();
                info.mMacAddress = in.readString();
                info.mMeteredHint = in.readInt() != 0;
                info.mSupplicantState = SupplicantState.CREATOR.createFromParcel(in);
                return info;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public WifiInfo[] newArray(int size) {
                return new WifiInfo[size];
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiInfo() {
        this.mWifiSsid = null;
        this.mBSSID = null;
        this.mNetworkId = -1;
        this.mSupplicantState = SupplicantState.UNINITIALIZED;
        this.mRssi = -9999;
        this.mLinkSpeed = -1;
        this.mHiddenSSID = false;
    }

    public WifiInfo(WifiInfo source) {
        if (source != null) {
            this.mSupplicantState = source.mSupplicantState;
            this.mBSSID = source.mBSSID;
            this.mWifiSsid = source.mWifiSsid;
            this.mNetworkId = source.mNetworkId;
            this.mHiddenSSID = source.mHiddenSSID;
            this.mRssi = source.mRssi;
            this.mLinkSpeed = source.mLinkSpeed;
            this.mIpAddress = source.mIpAddress;
            this.mMacAddress = source.mMacAddress;
            this.mMeteredHint = source.mMeteredHint;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSSID(WifiSsid wifiSsid) {
        this.mWifiSsid = wifiSsid;
        this.mHiddenSSID = false;
    }

    public String getSSID() {
        if (this.mWifiSsid != null) {
            String unicode = this.mWifiSsid.toString();
            if (!TextUtils.isEmpty(unicode)) {
                return Separators.DOUBLE_QUOTE + unicode + Separators.DOUBLE_QUOTE;
            }
            return this.mWifiSsid.getHexString();
        }
        return WifiSsid.NONE;
    }

    public WifiSsid getWifiSsid() {
        return this.mWifiSsid;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBSSID(String BSSID) {
        this.mBSSID = BSSID;
    }

    public String getBSSID() {
        return this.mBSSID;
    }

    public int getRssi() {
        return this.mRssi;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRssi(int rssi) {
        this.mRssi = rssi;
    }

    public int getLinkSpeed() {
        return this.mLinkSpeed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLinkSpeed(int linkSpeed) {
        this.mLinkSpeed = linkSpeed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMacAddress(String macAddress) {
        this.mMacAddress = macAddress;
    }

    public String getMacAddress() {
        return this.mMacAddress;
    }

    public void setMeteredHint(boolean meteredHint) {
        this.mMeteredHint = meteredHint;
    }

    public boolean getMeteredHint() {
        return this.mMeteredHint;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNetworkId(int id) {
        this.mNetworkId = id;
    }

    public int getNetworkId() {
        return this.mNetworkId;
    }

    public SupplicantState getSupplicantState() {
        return this.mSupplicantState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSupplicantState(SupplicantState state) {
        this.mSupplicantState = state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInetAddress(InetAddress address) {
        this.mIpAddress = address;
    }

    public int getIpAddress() {
        int result = 0;
        if (this.mIpAddress instanceof Inet4Address) {
            result = NetworkUtils.inetAddressToInt((Inet4Address) this.mIpAddress);
        }
        return result;
    }

    public boolean getHiddenSSID() {
        return this.mHiddenSSID;
    }

    public void setHiddenSSID(boolean hiddenSSID) {
        this.mHiddenSSID = hiddenSSID;
    }

    public static NetworkInfo.DetailedState getDetailedStateOf(SupplicantState suppState) {
        return stateMap.get(suppState);
    }

    void setSupplicantState(String stateName) {
        this.mSupplicantState = valueOf(stateName);
    }

    static SupplicantState valueOf(String stateName) {
        if ("4WAY_HANDSHAKE".equalsIgnoreCase(stateName)) {
            return SupplicantState.FOUR_WAY_HANDSHAKE;
        }
        try {
            return SupplicantState.valueOf(stateName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return SupplicantState.INVALID;
        }
    }

    public static String removeDoubleQuotes(String string) {
        if (string == null) {
            return null;
        }
        int length = string.length();
        if (length > 1 && string.charAt(0) == '\"' && string.charAt(length - 1) == '\"') {
            return string.substring(1, length - 1);
        }
        return string;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SSID: ").append(this.mWifiSsid == null ? WifiSsid.NONE : this.mWifiSsid).append(", BSSID: ").append(this.mBSSID == null ? "<none>" : this.mBSSID).append(", MAC: ").append(this.mMacAddress == null ? "<none>" : this.mMacAddress).append(", Supplicant state: ").append(this.mSupplicantState == null ? "<none>" : this.mSupplicantState).append(", RSSI: ").append(this.mRssi).append(", Link speed: ").append(this.mLinkSpeed).append(", Net ID: ").append(this.mNetworkId).append(", Metered hint: ").append(this.mMeteredHint);
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mNetworkId);
        dest.writeInt(this.mRssi);
        dest.writeInt(this.mLinkSpeed);
        if (this.mIpAddress != null) {
            dest.writeByte((byte) 1);
            dest.writeByteArray(this.mIpAddress.getAddress());
        } else {
            dest.writeByte((byte) 0);
        }
        if (this.mWifiSsid != null) {
            dest.writeInt(1);
            this.mWifiSsid.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(this.mBSSID);
        dest.writeString(this.mMacAddress);
        dest.writeInt(this.mMeteredHint ? 1 : 0);
        this.mSupplicantState.writeToParcel(dest, flags);
    }
}