package android.net;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: DhcpInfo.class */
public class DhcpInfo implements Parcelable {
    public int ipAddress;
    public int gateway;
    public int netmask;
    public int dns1;
    public int dns2;
    public int serverAddress;
    public int leaseDuration;
    public static final Parcelable.Creator<DhcpInfo> CREATOR = new Parcelable.Creator<DhcpInfo>() { // from class: android.net.DhcpInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpInfo createFromParcel(Parcel in) {
            DhcpInfo info = new DhcpInfo();
            info.ipAddress = in.readInt();
            info.gateway = in.readInt();
            info.netmask = in.readInt();
            info.dns1 = in.readInt();
            info.dns2 = in.readInt();
            info.serverAddress = in.readInt();
            info.leaseDuration = in.readInt();
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DhcpInfo[] newArray(int size) {
            return new DhcpInfo[size];
        }
    };

    public DhcpInfo() {
    }

    public DhcpInfo(DhcpInfo source) {
        if (source != null) {
            this.ipAddress = source.ipAddress;
            this.gateway = source.gateway;
            this.netmask = source.netmask;
            this.dns1 = source.dns1;
            this.dns2 = source.dns2;
            this.serverAddress = source.serverAddress;
            this.leaseDuration = source.leaseDuration;
        }
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("ipaddr ");
        putAddress(str, this.ipAddress);
        str.append(" gateway ");
        putAddress(str, this.gateway);
        str.append(" netmask ");
        putAddress(str, this.netmask);
        str.append(" dns1 ");
        putAddress(str, this.dns1);
        str.append(" dns2 ");
        putAddress(str, this.dns2);
        str.append(" DHCP server ");
        putAddress(str, this.serverAddress);
        str.append(" lease ").append(this.leaseDuration).append(" seconds");
        return str.toString();
    }

    private static void putAddress(StringBuffer buf, int addr) {
        buf.append(NetworkUtils.intToInetAddress(addr).getHostAddress());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ipAddress);
        dest.writeInt(this.gateway);
        dest.writeInt(this.netmask);
        dest.writeInt(this.dns1);
        dest.writeInt(this.dns2);
        dest.writeInt(this.serverAddress);
        dest.writeInt(this.leaseDuration);
    }
}