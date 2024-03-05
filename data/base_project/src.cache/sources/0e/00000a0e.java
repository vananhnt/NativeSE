package android.net.wifi.p2p;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: WifiP2pInfo.class */
public class WifiP2pInfo implements Parcelable {
    public boolean groupFormed;
    public boolean isGroupOwner;
    public InetAddress groupOwnerAddress;
    public static final Parcelable.Creator<WifiP2pInfo> CREATOR = new Parcelable.Creator<WifiP2pInfo>() { // from class: android.net.wifi.p2p.WifiP2pInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pInfo createFromParcel(Parcel in) {
            WifiP2pInfo info = new WifiP2pInfo();
            info.groupFormed = in.readByte() == 1;
            info.isGroupOwner = in.readByte() == 1;
            if (in.readByte() == 1) {
                try {
                    info.groupOwnerAddress = InetAddress.getByAddress(in.createByteArray());
                } catch (UnknownHostException e) {
                }
            }
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiP2pInfo[] newArray(int size) {
            return new WifiP2pInfo[size];
        }
    };

    public WifiP2pInfo() {
    }

    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("groupFormed: ").append(this.groupFormed).append(" isGroupOwner: ").append(this.isGroupOwner).append(" groupOwnerAddress: ").append(this.groupOwnerAddress);
        return sbuf.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WifiP2pInfo(WifiP2pInfo source) {
        if (source != null) {
            this.groupFormed = source.groupFormed;
            this.isGroupOwner = source.isGroupOwner;
            this.groupOwnerAddress = source.groupOwnerAddress;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.groupFormed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGroupOwner ? (byte) 1 : (byte) 0);
        if (this.groupOwnerAddress != null) {
            dest.writeByte((byte) 1);
            dest.writeByteArray(this.groupOwnerAddress.getAddress());
            return;
        }
        dest.writeByte((byte) 0);
    }
}