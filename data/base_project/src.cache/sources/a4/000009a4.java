package android.net.wifi;

import android.net.LinkProperties;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;
import android.text.TextUtils;
import gov.nist.core.Separators;
import java.util.BitSet;

/* loaded from: WifiConfiguration.class */
public class WifiConfiguration implements Parcelable {
    private static final String TAG = "WifiConfiguration";
    public static final String ssidVarName = "ssid";
    public static final String bssidVarName = "bssid";
    public static final String pskVarName = "psk";
    public static final String wepTxKeyIdxVarName = "wep_tx_keyidx";
    public static final String priorityVarName = "priority";
    public static final String hiddenSSIDVarName = "scan_ssid";
    public static final int INVALID_NETWORK_ID = -1;
    public static final int DISABLED_UNKNOWN_REASON = 0;
    public static final int DISABLED_DNS_FAILURE = 1;
    public static final int DISABLED_DHCP_FAILURE = 2;
    public static final int DISABLED_AUTH_FAILURE = 3;
    public static final int DISABLED_ASSOCIATION_REJECT = 4;
    public int networkId;
    public int status;
    public int disableReason;
    public String SSID;
    public String BSSID;
    public String preSharedKey;
    public String[] wepKeys;
    public int wepTxKeyIndex;
    public int priority;
    public boolean hiddenSSID;
    public BitSet allowedKeyManagement;
    public BitSet allowedProtocols;
    public BitSet allowedAuthAlgorithms;
    public BitSet allowedPairwiseCiphers;
    public BitSet allowedGroupCiphers;
    public WifiEnterpriseConfig enterpriseConfig;
    public IpAssignment ipAssignment;
    public ProxySettings proxySettings;
    public LinkProperties linkProperties;
    public static final String[] wepKeyVarNames = {"wep_key0", "wep_key1", "wep_key2", "wep_key3"};
    public static final Parcelable.Creator<WifiConfiguration> CREATOR = new Parcelable.Creator<WifiConfiguration>() { // from class: android.net.wifi.WifiConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiConfiguration createFromParcel(Parcel in) {
            WifiConfiguration config = new WifiConfiguration();
            config.networkId = in.readInt();
            config.status = in.readInt();
            config.disableReason = in.readInt();
            config.SSID = in.readString();
            config.BSSID = in.readString();
            config.preSharedKey = in.readString();
            for (int i = 0; i < config.wepKeys.length; i++) {
                config.wepKeys[i] = in.readString();
            }
            config.wepTxKeyIndex = in.readInt();
            config.priority = in.readInt();
            config.hiddenSSID = in.readInt() != 0;
            config.allowedKeyManagement = WifiConfiguration.readBitSet(in);
            config.allowedProtocols = WifiConfiguration.readBitSet(in);
            config.allowedAuthAlgorithms = WifiConfiguration.readBitSet(in);
            config.allowedPairwiseCiphers = WifiConfiguration.readBitSet(in);
            config.allowedGroupCiphers = WifiConfiguration.readBitSet(in);
            config.enterpriseConfig = (WifiEnterpriseConfig) in.readParcelable(null);
            config.ipAssignment = IpAssignment.valueOf(in.readString());
            config.proxySettings = ProxySettings.valueOf(in.readString());
            config.linkProperties = (LinkProperties) in.readParcelable(null);
            return config;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiConfiguration[] newArray(int size) {
            return new WifiConfiguration[size];
        }
    };

    /* loaded from: WifiConfiguration$IpAssignment.class */
    public enum IpAssignment {
        STATIC,
        DHCP,
        UNASSIGNED
    }

    /* loaded from: WifiConfiguration$ProxySettings.class */
    public enum ProxySettings {
        NONE,
        STATIC,
        UNASSIGNED,
        PAC
    }

    /* loaded from: WifiConfiguration$KeyMgmt.class */
    public static class KeyMgmt {
        public static final int NONE = 0;
        public static final int WPA_PSK = 1;
        public static final int WPA_EAP = 2;
        public static final int IEEE8021X = 3;
        public static final int WPA2_PSK = 4;
        public static final String varName = "key_mgmt";
        public static final String[] strings = {"NONE", "WPA_PSK", "WPA_EAP", "IEEE8021X", "WPA2_PSK"};

        private KeyMgmt() {
        }
    }

    /* loaded from: WifiConfiguration$Protocol.class */
    public static class Protocol {
        public static final int WPA = 0;
        public static final int RSN = 1;
        public static final String varName = "proto";
        public static final String[] strings = {"WPA", "RSN"};

        private Protocol() {
        }
    }

    /* loaded from: WifiConfiguration$AuthAlgorithm.class */
    public static class AuthAlgorithm {
        public static final int OPEN = 0;
        public static final int SHARED = 1;
        public static final int LEAP = 2;
        public static final String varName = "auth_alg";
        public static final String[] strings = {"OPEN", "SHARED", "LEAP"};

        private AuthAlgorithm() {
        }
    }

    /* loaded from: WifiConfiguration$PairwiseCipher.class */
    public static class PairwiseCipher {
        public static final int NONE = 0;
        public static final int TKIP = 1;
        public static final int CCMP = 2;
        public static final String varName = "pairwise";
        public static final String[] strings = {"NONE", "TKIP", "CCMP"};

        private PairwiseCipher() {
        }
    }

    /* loaded from: WifiConfiguration$GroupCipher.class */
    public static class GroupCipher {
        public static final int WEP40 = 0;
        public static final int WEP104 = 1;
        public static final int TKIP = 2;
        public static final int CCMP = 3;
        public static final String varName = "group";
        public static final String[] strings = {"WEP40", "WEP104", "TKIP", "CCMP"};

        private GroupCipher() {
        }
    }

    /* loaded from: WifiConfiguration$Status.class */
    public static class Status {
        public static final int CURRENT = 0;
        public static final int DISABLED = 1;
        public static final int ENABLED = 2;
        public static final String[] strings = {Telephony.Carriers.CURRENT, "disabled", "enabled"};

        private Status() {
        }
    }

    public WifiConfiguration() {
        this.networkId = -1;
        this.SSID = null;
        this.BSSID = null;
        this.priority = 0;
        this.hiddenSSID = false;
        this.disableReason = 0;
        this.allowedKeyManagement = new BitSet();
        this.allowedProtocols = new BitSet();
        this.allowedAuthAlgorithms = new BitSet();
        this.allowedPairwiseCiphers = new BitSet();
        this.allowedGroupCiphers = new BitSet();
        this.wepKeys = new String[4];
        for (int i = 0; i < this.wepKeys.length; i++) {
            this.wepKeys[i] = null;
        }
        this.enterpriseConfig = new WifiEnterpriseConfig();
        this.ipAssignment = IpAssignment.UNASSIGNED;
        this.proxySettings = ProxySettings.UNASSIGNED;
        this.linkProperties = new LinkProperties();
    }

    public boolean isValid() {
        if (this.allowedKeyManagement.cardinality() > 1) {
            if (this.allowedKeyManagement.cardinality() != 2 || !this.allowedKeyManagement.get(2)) {
                return false;
            }
            if (!this.allowedKeyManagement.get(3) && !this.allowedKeyManagement.get(1)) {
                return false;
            }
            return true;
        }
        return true;
    }

    public String toString() {
        StringBuilder sbuf = new StringBuilder();
        if (this.status == 0) {
            sbuf.append("* ");
        } else if (this.status == 1) {
            sbuf.append("- DSBLE: ").append(this.disableReason).append(Separators.SP);
        }
        sbuf.append("ID: ").append(this.networkId).append(" SSID: ").append(this.SSID).append(" BSSID: ").append(this.BSSID).append(" PRIO: ").append(this.priority).append('\n');
        sbuf.append(" KeyMgmt:");
        for (int k = 0; k < this.allowedKeyManagement.size(); k++) {
            if (this.allowedKeyManagement.get(k)) {
                sbuf.append(Separators.SP);
                if (k < KeyMgmt.strings.length) {
                    sbuf.append(KeyMgmt.strings[k]);
                } else {
                    sbuf.append("??");
                }
            }
        }
        sbuf.append(" Protocols:");
        for (int p = 0; p < this.allowedProtocols.size(); p++) {
            if (this.allowedProtocols.get(p)) {
                sbuf.append(Separators.SP);
                if (p < Protocol.strings.length) {
                    sbuf.append(Protocol.strings[p]);
                } else {
                    sbuf.append("??");
                }
            }
        }
        sbuf.append('\n');
        sbuf.append(" AuthAlgorithms:");
        for (int a = 0; a < this.allowedAuthAlgorithms.size(); a++) {
            if (this.allowedAuthAlgorithms.get(a)) {
                sbuf.append(Separators.SP);
                if (a < AuthAlgorithm.strings.length) {
                    sbuf.append(AuthAlgorithm.strings[a]);
                } else {
                    sbuf.append("??");
                }
            }
        }
        sbuf.append('\n');
        sbuf.append(" PairwiseCiphers:");
        for (int pc = 0; pc < this.allowedPairwiseCiphers.size(); pc++) {
            if (this.allowedPairwiseCiphers.get(pc)) {
                sbuf.append(Separators.SP);
                if (pc < PairwiseCipher.strings.length) {
                    sbuf.append(PairwiseCipher.strings[pc]);
                } else {
                    sbuf.append("??");
                }
            }
        }
        sbuf.append('\n');
        sbuf.append(" GroupCiphers:");
        for (int gc = 0; gc < this.allowedGroupCiphers.size(); gc++) {
            if (this.allowedGroupCiphers.get(gc)) {
                sbuf.append(Separators.SP);
                if (gc < GroupCipher.strings.length) {
                    sbuf.append(GroupCipher.strings[gc]);
                } else {
                    sbuf.append("??");
                }
            }
        }
        sbuf.append('\n').append(" PSK: ");
        if (this.preSharedKey != null) {
            sbuf.append('*');
        }
        sbuf.append(this.enterpriseConfig);
        sbuf.append('\n');
        sbuf.append("IP assignment: " + this.ipAssignment.toString());
        sbuf.append(Separators.RETURN);
        sbuf.append("Proxy settings: " + this.proxySettings.toString());
        sbuf.append(Separators.RETURN);
        sbuf.append(this.linkProperties.toString());
        sbuf.append(Separators.RETURN);
        return sbuf.toString();
    }

    public String getPrintableSsid() {
        if (this.SSID == null) {
            return "";
        }
        int length = this.SSID.length();
        if (length > 2 && this.SSID.charAt(0) == '\"' && this.SSID.charAt(length - 1) == '\"') {
            return this.SSID.substring(1, length - 1);
        }
        if (length > 3 && this.SSID.charAt(0) == 'P' && this.SSID.charAt(1) == '\"' && this.SSID.charAt(length - 1) == '\"') {
            WifiSsid wifiSsid = WifiSsid.createFromAsciiEncoded(this.SSID.substring(2, length - 1));
            return wifiSsid.toString();
        }
        return this.SSID;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getKeyIdForCredentials(WifiConfiguration current) {
        String keyMgmt = null;
        try {
            if (TextUtils.isEmpty(this.SSID)) {
                this.SSID = current.SSID;
            }
            if (this.allowedKeyManagement.cardinality() == 0) {
                this.allowedKeyManagement = current.allowedKeyManagement;
            }
            if (this.allowedKeyManagement.get(2)) {
                keyMgmt = KeyMgmt.strings[2];
            }
            if (this.allowedKeyManagement.get(3)) {
                keyMgmt = keyMgmt + KeyMgmt.strings[3];
            }
            if (TextUtils.isEmpty(keyMgmt)) {
                throw new IllegalStateException("Not an EAP network");
            }
            return trimStringForKeyId(this.SSID) + "_" + keyMgmt + "_" + trimStringForKeyId(this.enterpriseConfig.getKeyId(current != null ? current.enterpriseConfig : null));
        } catch (NullPointerException e) {
            throw new IllegalStateException("Invalid config details");
        }
    }

    private String trimStringForKeyId(String string) {
        return string.replace(Separators.DOUBLE_QUOTE, "").replace(Separators.SP, "");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static BitSet readBitSet(Parcel src) {
        int cardinality = src.readInt();
        BitSet set = new BitSet();
        for (int i = 0; i < cardinality; i++) {
            set.set(src.readInt());
        }
        return set;
    }

    private static void writeBitSet(Parcel dest, BitSet set) {
        int nextSetBit = -1;
        dest.writeInt(set.cardinality());
        while (true) {
            int nextSetBit2 = set.nextSetBit(nextSetBit + 1);
            nextSetBit = nextSetBit2;
            if (nextSetBit2 != -1) {
                dest.writeInt(nextSetBit);
            } else {
                return;
            }
        }
    }

    public int getAuthType() {
        if (!isValid()) {
            throw new IllegalStateException("Invalid configuration");
        }
        if (this.allowedKeyManagement.get(1)) {
            return 1;
        }
        if (this.allowedKeyManagement.get(4)) {
            return 4;
        }
        if (this.allowedKeyManagement.get(2)) {
            return 2;
        }
        if (this.allowedKeyManagement.get(3)) {
            return 3;
        }
        return 0;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WifiConfiguration(WifiConfiguration source) {
        if (source != null) {
            this.networkId = source.networkId;
            this.status = source.status;
            this.disableReason = source.disableReason;
            this.SSID = source.SSID;
            this.BSSID = source.BSSID;
            this.preSharedKey = source.preSharedKey;
            this.wepKeys = new String[4];
            for (int i = 0; i < this.wepKeys.length; i++) {
                this.wepKeys[i] = source.wepKeys[i];
            }
            this.wepTxKeyIndex = source.wepTxKeyIndex;
            this.priority = source.priority;
            this.hiddenSSID = source.hiddenSSID;
            this.allowedKeyManagement = (BitSet) source.allowedKeyManagement.clone();
            this.allowedProtocols = (BitSet) source.allowedProtocols.clone();
            this.allowedAuthAlgorithms = (BitSet) source.allowedAuthAlgorithms.clone();
            this.allowedPairwiseCiphers = (BitSet) source.allowedPairwiseCiphers.clone();
            this.allowedGroupCiphers = (BitSet) source.allowedGroupCiphers.clone();
            this.enterpriseConfig = new WifiEnterpriseConfig(source.enterpriseConfig);
            this.ipAssignment = source.ipAssignment;
            this.proxySettings = source.proxySettings;
            this.linkProperties = new LinkProperties(source.linkProperties);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.networkId);
        dest.writeInt(this.status);
        dest.writeInt(this.disableReason);
        dest.writeString(this.SSID);
        dest.writeString(this.BSSID);
        dest.writeString(this.preSharedKey);
        String[] arr$ = this.wepKeys;
        for (String wepKey : arr$) {
            dest.writeString(wepKey);
        }
        dest.writeInt(this.wepTxKeyIndex);
        dest.writeInt(this.priority);
        dest.writeInt(this.hiddenSSID ? 1 : 0);
        writeBitSet(dest, this.allowedKeyManagement);
        writeBitSet(dest, this.allowedProtocols);
        writeBitSet(dest, this.allowedAuthAlgorithms);
        writeBitSet(dest, this.allowedPairwiseCiphers);
        writeBitSet(dest, this.allowedGroupCiphers);
        dest.writeParcelable(this.enterpriseConfig, flags);
        dest.writeString(this.ipAssignment.name());
        dest.writeString(this.proxySettings.name());
        dest.writeParcelable(this.linkProperties, flags);
    }
}