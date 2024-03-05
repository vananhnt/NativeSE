package android.net.wifi.p2p;

import android.net.wifi.WpsInfo;
import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: WifiP2pConfig.class */
public class WifiP2pConfig implements Parcelable {
    public String deviceAddress;
    public WpsInfo wps;
    public static final int MAX_GROUP_OWNER_INTENT = 15;
    public static final int MIN_GROUP_OWNER_INTENT = 0;
    public int groupOwnerIntent;
    public int netId;
    public static final Parcelable.Creator<WifiP2pConfig> CREATOR = new Parcelable.Creator<WifiP2pConfig>() { // from class: android.net.wifi.p2p.WifiP2pConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pConfig createFromParcel(Parcel in) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = in.readString();
            config.wps = (WpsInfo) in.readParcelable(null);
            config.groupOwnerIntent = in.readInt();
            config.netId = in.readInt();
            return config;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pConfig[] newArray(int size) {
            return new WifiP2pConfig[size];
        }
    };

    public WifiP2pConfig() {
        this.deviceAddress = "";
        this.groupOwnerIntent = -1;
        this.netId = -2;
        this.wps = new WpsInfo();
        this.wps.setup = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate() {
        this.deviceAddress = "";
    }

    public WifiP2pConfig(String supplicantEvent) throws IllegalArgumentException {
        int devPasswdId;
        this.deviceAddress = "";
        this.groupOwnerIntent = -1;
        this.netId = -2;
        String[] tokens = supplicantEvent.split(Separators.SP);
        if (tokens.length < 2 || !tokens[0].equals("P2P-GO-NEG-REQUEST")) {
            throw new IllegalArgumentException("Malformed supplicant event");
        }
        this.deviceAddress = tokens[1];
        this.wps = new WpsInfo();
        if (tokens.length > 2) {
            String[] nameVal = tokens[2].split(Separators.EQUALS);
            try {
                devPasswdId = Integer.parseInt(nameVal[1]);
            } catch (NumberFormatException e) {
                devPasswdId = 0;
            }
            switch (devPasswdId) {
                case 1:
                    this.wps.setup = 1;
                    return;
                case 2:
                case 3:
                default:
                    this.wps.setup = 0;
                    return;
                case 4:
                    this.wps.setup = 0;
                    return;
                case 5:
                    this.wps.setup = 2;
                    return;
            }
        }
    }

    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("\n address: ").append(this.deviceAddress);
        sbuf.append("\n wps: ").append(this.wps);
        sbuf.append("\n groupOwnerIntent: ").append(this.groupOwnerIntent);
        sbuf.append("\n persist: ").append(this.netId);
        return sbuf.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WifiP2pConfig(WifiP2pConfig source) {
        this.deviceAddress = "";
        this.groupOwnerIntent = -1;
        this.netId = -2;
        if (source != null) {
            this.deviceAddress = source.deviceAddress;
            this.wps = new WpsInfo(source.wps);
            this.groupOwnerIntent = source.groupOwnerIntent;
            this.netId = source.netId;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceAddress);
        dest.writeParcelable(this.wps, flags);
        dest.writeInt(this.groupOwnerIntent);
        dest.writeInt(this.netId);
    }
}