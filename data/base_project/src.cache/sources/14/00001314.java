package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: CellIdentityCdma.class */
public final class CellIdentityCdma implements Parcelable {
    private static final String LOG_TAG = "CellSignalStrengthCdma";
    private static final boolean DBG = false;
    private final int mNetworkId;
    private final int mSystemId;
    private final int mBasestationId;
    private final int mLongitude;
    private final int mLatitude;
    public static final Parcelable.Creator<CellIdentityCdma> CREATOR = new Parcelable.Creator<CellIdentityCdma>() { // from class: android.telephony.CellIdentityCdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellIdentityCdma createFromParcel(Parcel in) {
            return new CellIdentityCdma(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellIdentityCdma[] newArray(int size) {
            return new CellIdentityCdma[size];
        }
    };

    public CellIdentityCdma() {
        this.mNetworkId = Integer.MAX_VALUE;
        this.mSystemId = Integer.MAX_VALUE;
        this.mBasestationId = Integer.MAX_VALUE;
        this.mLongitude = Integer.MAX_VALUE;
        this.mLatitude = Integer.MAX_VALUE;
    }

    public CellIdentityCdma(int nid, int sid, int bid, int lon, int lat) {
        this.mNetworkId = nid;
        this.mSystemId = sid;
        this.mBasestationId = bid;
        this.mLongitude = lon;
        this.mLatitude = lat;
    }

    private CellIdentityCdma(CellIdentityCdma cid) {
        this.mNetworkId = cid.mNetworkId;
        this.mSystemId = cid.mSystemId;
        this.mBasestationId = cid.mBasestationId;
        this.mLongitude = cid.mLongitude;
        this.mLatitude = cid.mLatitude;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CellIdentityCdma copy() {
        return new CellIdentityCdma(this);
    }

    public int getNetworkId() {
        return this.mNetworkId;
    }

    public int getSystemId() {
        return this.mSystemId;
    }

    public int getBasestationId() {
        return this.mBasestationId;
    }

    public int getLongitude() {
        return this.mLongitude;
    }

    public int getLatitude() {
        return this.mLatitude;
    }

    public int hashCode() {
        return (this.mNetworkId * 31) + (this.mSystemId * 31) + (this.mBasestationId * 31) + (this.mLatitude * 31) + (this.mLongitude * 31);
    }

    public boolean equals(Object other) {
        if (super.equals(other)) {
            try {
                CellIdentityCdma o = (CellIdentityCdma) other;
                if (this.mNetworkId == o.mNetworkId && this.mSystemId == o.mSystemId && this.mBasestationId == o.mBasestationId && this.mLatitude == o.mLatitude) {
                    if (this.mLongitude == o.mLongitude) {
                        return true;
                    }
                }
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }
        return false;
    }

    public String toString() {
        return "CellIdentityCdma:{ mNetworkId=" + this.mNetworkId + " mSystemId=" + this.mSystemId + " mBasestationId=" + this.mBasestationId + " mLongitude=" + this.mLongitude + " mLatitude=" + this.mLatitude + "}";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mNetworkId);
        dest.writeInt(this.mSystemId);
        dest.writeInt(this.mBasestationId);
        dest.writeInt(this.mLongitude);
        dest.writeInt(this.mLatitude);
    }

    private CellIdentityCdma(Parcel in) {
        this.mNetworkId = in.readInt();
        this.mSystemId = in.readInt();
        this.mBasestationId = in.readInt();
        this.mLongitude = in.readInt();
        this.mLatitude = in.readInt();
    }

    private static void log(String s) {
        Rlog.w(LOG_TAG, s);
    }
}