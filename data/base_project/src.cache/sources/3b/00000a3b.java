package android.net.wifi.p2p;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;

/* loaded from: WifiP2pWfdInfo.class */
public class WifiP2pWfdInfo implements Parcelable {
    private static final String TAG = "WifiP2pWfdInfo";
    private boolean mWfdEnabled;
    private int mDeviceInfo;
    public static final int WFD_SOURCE = 0;
    public static final int PRIMARY_SINK = 1;
    public static final int SECONDARY_SINK = 2;
    public static final int SOURCE_OR_PRIMARY_SINK = 3;
    private static final int DEVICE_TYPE = 3;
    private static final int COUPLED_SINK_SUPPORT_AT_SOURCE = 4;
    private static final int COUPLED_SINK_SUPPORT_AT_SINK = 8;
    private static final int SESSION_AVAILABLE = 48;
    private static final int SESSION_AVAILABLE_BIT1 = 16;
    private static final int SESSION_AVAILABLE_BIT2 = 32;
    private int mCtrlPort;
    private int mMaxThroughput;
    public static final Parcelable.Creator<WifiP2pWfdInfo> CREATOR = new Parcelable.Creator<WifiP2pWfdInfo>() { // from class: android.net.wifi.p2p.WifiP2pWfdInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pWfdInfo createFromParcel(Parcel in) {
            WifiP2pWfdInfo device = new WifiP2pWfdInfo();
            device.readFromParcel(in);
            return device;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pWfdInfo[] newArray(int size) {
            return new WifiP2pWfdInfo[size];
        }
    };

    public WifiP2pWfdInfo() {
    }

    public WifiP2pWfdInfo(int devInfo, int ctrlPort, int maxTput) {
        this.mWfdEnabled = true;
        this.mDeviceInfo = devInfo;
        this.mCtrlPort = ctrlPort;
        this.mMaxThroughput = maxTput;
    }

    public boolean isWfdEnabled() {
        return this.mWfdEnabled;
    }

    public void setWfdEnabled(boolean enabled) {
        this.mWfdEnabled = enabled;
    }

    public int getDeviceType() {
        return this.mDeviceInfo & 3;
    }

    public boolean setDeviceType(int deviceType) {
        if (deviceType >= 0 && deviceType <= 3) {
            this.mDeviceInfo |= deviceType;
            return true;
        }
        return false;
    }

    public boolean isCoupledSinkSupportedAtSource() {
        return (this.mDeviceInfo & 8) != 0;
    }

    public void setCoupledSinkSupportAtSource(boolean enabled) {
        if (enabled) {
            this.mDeviceInfo |= 8;
        } else {
            this.mDeviceInfo &= -9;
        }
    }

    public boolean isCoupledSinkSupportedAtSink() {
        return (this.mDeviceInfo & 8) != 0;
    }

    public void setCoupledSinkSupportAtSink(boolean enabled) {
        if (enabled) {
            this.mDeviceInfo |= 8;
        } else {
            this.mDeviceInfo &= -9;
        }
    }

    public boolean isSessionAvailable() {
        return (this.mDeviceInfo & 48) != 0;
    }

    public void setSessionAvailable(boolean enabled) {
        if (enabled) {
            this.mDeviceInfo |= 16;
            this.mDeviceInfo &= -33;
            return;
        }
        this.mDeviceInfo &= -49;
    }

    public int getControlPort() {
        return this.mCtrlPort;
    }

    public void setControlPort(int port) {
        this.mCtrlPort = port;
    }

    public void setMaxThroughput(int maxThroughput) {
        this.mMaxThroughput = maxThroughput;
    }

    public int getMaxThroughput() {
        return this.mMaxThroughput;
    }

    public String getDeviceInfoHex() {
        return String.format(Locale.US, "%04x%04x%04x%04x", 6, Integer.valueOf(this.mDeviceInfo), Integer.valueOf(this.mCtrlPort), Integer.valueOf(this.mMaxThroughput));
    }

    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("WFD enabled: ").append(this.mWfdEnabled);
        sbuf.append("WFD DeviceInfo: ").append(this.mDeviceInfo);
        sbuf.append("\n WFD CtrlPort: ").append(this.mCtrlPort);
        sbuf.append("\n WFD MaxThroughput: ").append(this.mMaxThroughput);
        return sbuf.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WifiP2pWfdInfo(WifiP2pWfdInfo source) {
        if (source != null) {
            this.mWfdEnabled = source.mWfdEnabled;
            this.mDeviceInfo = source.mDeviceInfo;
            this.mCtrlPort = source.mCtrlPort;
            this.mMaxThroughput = source.mMaxThroughput;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mWfdEnabled ? 1 : 0);
        dest.writeInt(this.mDeviceInfo);
        dest.writeInt(this.mCtrlPort);
        dest.writeInt(this.mMaxThroughput);
    }

    public void readFromParcel(Parcel in) {
        this.mWfdEnabled = in.readInt() == 1;
        this.mDeviceInfo = in.readInt();
        this.mCtrlPort = in.readInt();
        this.mMaxThroughput = in.readInt();
    }
}