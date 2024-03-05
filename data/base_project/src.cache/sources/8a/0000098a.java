package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: ScanResult.class */
public class ScanResult implements Parcelable {
    public String SSID;
    public WifiSsid wifiSsid;
    public String BSSID;
    public String capabilities;
    public int level;
    public int frequency;
    public long timestamp;
    public int distanceCm;
    public int distanceSdCm;
    public static final int UNSPECIFIED = -1;
    public static final Parcelable.Creator<ScanResult> CREATOR = new Parcelable.Creator<ScanResult>() { // from class: android.net.wifi.ScanResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScanResult createFromParcel(Parcel in) {
            WifiSsid wifiSsid = null;
            if (in.readInt() == 1) {
                wifiSsid = WifiSsid.CREATOR.createFromParcel(in);
            }
            return new ScanResult(wifiSsid, in.readString(), in.readString(), in.readInt(), in.readInt(), in.readLong(), in.readInt(), in.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };

    public ScanResult(WifiSsid wifiSsid, String BSSID, String caps, int level, int frequency, long tsf) {
        this.wifiSsid = wifiSsid;
        this.SSID = wifiSsid != null ? wifiSsid.toString() : WifiSsid.NONE;
        this.BSSID = BSSID;
        this.capabilities = caps;
        this.level = level;
        this.frequency = frequency;
        this.timestamp = tsf;
        this.distanceCm = -1;
        this.distanceSdCm = -1;
    }

    public ScanResult(WifiSsid wifiSsid, String BSSID, String caps, int level, int frequency, long tsf, int distCm, int distSdCm) {
        this.wifiSsid = wifiSsid;
        this.SSID = wifiSsid != null ? wifiSsid.toString() : WifiSsid.NONE;
        this.BSSID = BSSID;
        this.capabilities = caps;
        this.level = level;
        this.frequency = frequency;
        this.timestamp = tsf;
        this.distanceCm = distCm;
        this.distanceSdCm = distSdCm;
    }

    public ScanResult(ScanResult source) {
        if (source != null) {
            this.wifiSsid = source.wifiSsid;
            this.SSID = source.SSID;
            this.BSSID = source.BSSID;
            this.capabilities = source.capabilities;
            this.level = source.level;
            this.frequency = source.frequency;
            this.timestamp = source.timestamp;
            this.distanceCm = source.distanceCm;
            this.distanceSdCm = source.distanceSdCm;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SSID: ").append(this.wifiSsid == null ? WifiSsid.NONE : this.wifiSsid).append(", BSSID: ").append(this.BSSID == null ? "<none>" : this.BSSID).append(", capabilities: ").append(this.capabilities == null ? "<none>" : this.capabilities).append(", level: ").append(this.level).append(", frequency: ").append(this.frequency).append(", timestamp: ").append(this.timestamp);
        sb.append(", distance: ").append(this.distanceCm != -1 ? Integer.valueOf(this.distanceCm) : Separators.QUESTION).append("(cm)");
        sb.append(", distanceSd: ").append(this.distanceSdCm != -1 ? Integer.valueOf(this.distanceSdCm) : Separators.QUESTION).append("(cm)");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.wifiSsid != null) {
            dest.writeInt(1);
            this.wifiSsid.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(this.BSSID);
        dest.writeString(this.capabilities);
        dest.writeInt(this.level);
        dest.writeInt(this.frequency);
        dest.writeLong(this.timestamp);
        dest.writeInt(this.distanceCm);
        dest.writeInt(this.distanceSdCm);
    }
}