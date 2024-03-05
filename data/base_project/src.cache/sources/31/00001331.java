package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: NeighboringCellInfo.class */
public class NeighboringCellInfo implements Parcelable {
    public static final int UNKNOWN_RSSI = 99;
    public static final int UNKNOWN_CID = -1;
    private int mRssi;
    private int mCid;
    private int mLac;
    private int mPsc;
    private int mNetworkType;
    public static final Parcelable.Creator<NeighboringCellInfo> CREATOR = new Parcelable.Creator<NeighboringCellInfo>() { // from class: android.telephony.NeighboringCellInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NeighboringCellInfo createFromParcel(Parcel in) {
            return new NeighboringCellInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NeighboringCellInfo[] newArray(int size) {
            return new NeighboringCellInfo[size];
        }
    };

    @Deprecated
    public NeighboringCellInfo() {
        this.mRssi = 99;
        this.mLac = -1;
        this.mCid = -1;
        this.mPsc = -1;
        this.mNetworkType = 0;
    }

    @Deprecated
    public NeighboringCellInfo(int rssi, int cid) {
        this.mRssi = rssi;
        this.mCid = cid;
    }

    public NeighboringCellInfo(int rssi, String location, int radioType) {
        this.mRssi = rssi;
        this.mNetworkType = 0;
        this.mPsc = -1;
        this.mLac = -1;
        this.mCid = -1;
        int l = location.length();
        if (l > 8) {
            return;
        }
        if (l < 8) {
            for (int i = 0; i < 8 - l; i++) {
                location = "0" + location;
            }
        }
        try {
            switch (radioType) {
                case 1:
                case 2:
                    this.mNetworkType = radioType;
                    if (!location.equalsIgnoreCase("FFFFFFFF")) {
                        this.mCid = Integer.valueOf(location.substring(4), 16).intValue();
                        this.mLac = Integer.valueOf(location.substring(0, 4), 16).intValue();
                        break;
                    }
                    break;
                case 3:
                case 8:
                case 9:
                case 10:
                    this.mNetworkType = radioType;
                    this.mPsc = Integer.valueOf(location, 16).intValue();
                    break;
            }
        } catch (NumberFormatException e) {
            this.mPsc = -1;
            this.mLac = -1;
            this.mCid = -1;
            this.mNetworkType = 0;
        }
    }

    public NeighboringCellInfo(Parcel in) {
        this.mRssi = in.readInt();
        this.mLac = in.readInt();
        this.mCid = in.readInt();
        this.mPsc = in.readInt();
        this.mNetworkType = in.readInt();
    }

    public int getRssi() {
        return this.mRssi;
    }

    public int getLac() {
        return this.mLac;
    }

    public int getCid() {
        return this.mCid;
    }

    public int getPsc() {
        return this.mPsc;
    }

    public int getNetworkType() {
        return this.mNetworkType;
    }

    @Deprecated
    public void setCid(int cid) {
        this.mCid = cid;
    }

    @Deprecated
    public void setRssi(int rssi) {
        this.mRssi = rssi;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (this.mPsc != -1) {
            sb.append(Integer.toHexString(this.mPsc)).append(Separators.AT).append(this.mRssi == 99 ? "-" : Integer.valueOf(this.mRssi));
        } else if (this.mLac != -1 && this.mCid != -1) {
            sb.append(Integer.toHexString(this.mLac)).append(Integer.toHexString(this.mCid)).append(Separators.AT).append(this.mRssi == 99 ? "-" : Integer.valueOf(this.mRssi));
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRssi);
        dest.writeInt(this.mLac);
        dest.writeInt(this.mCid);
        dest.writeInt(this.mPsc);
        dest.writeInt(this.mNetworkType);
    }
}