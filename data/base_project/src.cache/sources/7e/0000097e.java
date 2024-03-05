package android.net.nsd;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: NsdServiceInfo.class */
public final class NsdServiceInfo implements Parcelable {
    private String mServiceName;
    private String mServiceType;
    private DnsSdTxtRecord mTxtRecord;
    private InetAddress mHost;
    private int mPort;
    public static final Parcelable.Creator<NsdServiceInfo> CREATOR = new Parcelable.Creator<NsdServiceInfo>() { // from class: android.net.nsd.NsdServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NsdServiceInfo createFromParcel(Parcel in) {
            NsdServiceInfo info = new NsdServiceInfo();
            info.mServiceName = in.readString();
            info.mServiceType = in.readString();
            info.mTxtRecord = (DnsSdTxtRecord) in.readParcelable(null);
            if (in.readByte() == 1) {
                try {
                    info.mHost = InetAddress.getByAddress(in.createByteArray());
                } catch (UnknownHostException e) {
                }
            }
            info.mPort = in.readInt();
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NsdServiceInfo[] newArray(int size) {
            return new NsdServiceInfo[size];
        }
    };

    public NsdServiceInfo() {
    }

    public NsdServiceInfo(String sn, String rt, DnsSdTxtRecord tr) {
        this.mServiceName = sn;
        this.mServiceType = rt;
        this.mTxtRecord = tr;
    }

    public String getServiceName() {
        return this.mServiceName;
    }

    public void setServiceName(String s) {
        this.mServiceName = s;
    }

    public String getServiceType() {
        return this.mServiceType;
    }

    public void setServiceType(String s) {
        this.mServiceType = s;
    }

    public DnsSdTxtRecord getTxtRecord() {
        return this.mTxtRecord;
    }

    public void setTxtRecord(DnsSdTxtRecord t) {
        this.mTxtRecord = new DnsSdTxtRecord(t);
    }

    public InetAddress getHost() {
        return this.mHost;
    }

    public void setHost(InetAddress s) {
        this.mHost = s;
    }

    public int getPort() {
        return this.mPort;
    }

    public void setPort(int p) {
        this.mPort = p;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("name: ").append(this.mServiceName).append("type: ").append(this.mServiceType).append("host: ").append(this.mHost).append("port: ").append(this.mPort).append("txtRecord: ").append(this.mTxtRecord);
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mServiceName);
        dest.writeString(this.mServiceType);
        dest.writeParcelable(this.mTxtRecord, flags);
        if (this.mHost != null) {
            dest.writeByte((byte) 1);
            dest.writeByteArray(this.mHost.getAddress());
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeInt(this.mPort);
    }
}