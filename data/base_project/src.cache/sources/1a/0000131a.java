package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: CellIdentityWcdma.class */
public final class CellIdentityWcdma implements Parcelable {
    private static final String LOG_TAG = "CellIdentityWcdma";
    private static final boolean DBG = false;
    private final int mMcc;
    private final int mMnc;
    private final int mLac;
    private final int mCid;
    private final int mPsc;
    public static final Parcelable.Creator<CellIdentityWcdma> CREATOR = new Parcelable.Creator<CellIdentityWcdma>() { // from class: android.telephony.CellIdentityWcdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellIdentityWcdma createFromParcel(Parcel in) {
            return new CellIdentityWcdma(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellIdentityWcdma[] newArray(int size) {
            return new CellIdentityWcdma[size];
        }
    };

    public CellIdentityWcdma() {
        this.mMcc = Integer.MAX_VALUE;
        this.mMnc = Integer.MAX_VALUE;
        this.mLac = Integer.MAX_VALUE;
        this.mCid = Integer.MAX_VALUE;
        this.mPsc = Integer.MAX_VALUE;
    }

    public CellIdentityWcdma(int mcc, int mnc, int lac, int cid, int psc) {
        this.mMcc = mcc;
        this.mMnc = mnc;
        this.mLac = lac;
        this.mCid = cid;
        this.mPsc = psc;
    }

    private CellIdentityWcdma(CellIdentityWcdma cid) {
        this.mMcc = cid.mMcc;
        this.mMnc = cid.mMnc;
        this.mLac = cid.mLac;
        this.mCid = cid.mCid;
        this.mPsc = cid.mPsc;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CellIdentityWcdma copy() {
        return new CellIdentityWcdma(this);
    }

    public int getMcc() {
        return this.mMcc;
    }

    public int getMnc() {
        return this.mMnc;
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

    public int hashCode() {
        return (this.mMcc * 31) + (this.mMnc * 31) + (this.mLac * 31) + (this.mCid * 31) + (this.mPsc * 31);
    }

    public boolean equals(Object other) {
        if (super.equals(other)) {
            try {
                CellIdentityWcdma o = (CellIdentityWcdma) other;
                if (this.mMcc == o.mMcc && this.mMnc == o.mMnc && this.mLac == o.mLac && this.mCid == o.mCid) {
                    if (this.mPsc == o.mPsc) {
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
        StringBuilder sb = new StringBuilder("CellIdentityWcdma:{");
        sb.append(" mMcc=").append(this.mMcc);
        sb.append(" mMnc=").append(this.mMnc);
        sb.append(" mLac=").append(this.mLac);
        sb.append(" mCid=").append(this.mCid);
        sb.append(" mPsc=").append(this.mPsc);
        sb.append("}");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMcc);
        dest.writeInt(this.mMnc);
        dest.writeInt(this.mLac);
        dest.writeInt(this.mCid);
        dest.writeInt(this.mPsc);
    }

    private CellIdentityWcdma(Parcel in) {
        this.mMcc = in.readInt();
        this.mMnc = in.readInt();
        this.mLac = in.readInt();
        this.mCid = in.readInt();
        this.mPsc = in.readInt();
    }

    private static void log(String s) {
        Rlog.w(LOG_TAG, s);
    }
}