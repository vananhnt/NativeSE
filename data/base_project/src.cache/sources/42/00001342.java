package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: SmsCbLocation.class */
public class SmsCbLocation implements Parcelable {
    private final String mPlmn;
    private final int mLac;
    private final int mCid;
    public static final Parcelable.Creator<SmsCbLocation> CREATOR = new Parcelable.Creator<SmsCbLocation>() { // from class: android.telephony.SmsCbLocation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SmsCbLocation createFromParcel(Parcel in) {
            return new SmsCbLocation(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SmsCbLocation[] newArray(int size) {
            return new SmsCbLocation[size];
        }
    };

    public SmsCbLocation() {
        this.mPlmn = "";
        this.mLac = -1;
        this.mCid = -1;
    }

    public SmsCbLocation(String plmn) {
        this.mPlmn = plmn;
        this.mLac = -1;
        this.mCid = -1;
    }

    public SmsCbLocation(String plmn, int lac, int cid) {
        this.mPlmn = plmn;
        this.mLac = lac;
        this.mCid = cid;
    }

    public SmsCbLocation(Parcel in) {
        this.mPlmn = in.readString();
        this.mLac = in.readInt();
        this.mCid = in.readInt();
    }

    public String getPlmn() {
        return this.mPlmn;
    }

    public int getLac() {
        return this.mLac;
    }

    public int getCid() {
        return this.mCid;
    }

    public int hashCode() {
        int hash = this.mPlmn.hashCode();
        return (((hash * 31) + this.mLac) * 31) + this.mCid;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof SmsCbLocation)) {
            return false;
        }
        SmsCbLocation other = (SmsCbLocation) o;
        return this.mPlmn.equals(other.mPlmn) && this.mLac == other.mLac && this.mCid == other.mCid;
    }

    public String toString() {
        return '[' + this.mPlmn + ',' + this.mLac + ',' + this.mCid + ']';
    }

    public boolean isInLocationArea(SmsCbLocation area) {
        if (this.mCid != -1 && this.mCid != area.mCid) {
            return false;
        }
        if (this.mLac != -1 && this.mLac != area.mLac) {
            return false;
        }
        return this.mPlmn.equals(area.mPlmn);
    }

    public boolean isInLocationArea(String plmn, int lac, int cid) {
        if (!this.mPlmn.equals(plmn)) {
            return false;
        }
        if (this.mLac != -1 && this.mLac != lac) {
            return false;
        }
        if (this.mCid != -1 && this.mCid != cid) {
            return false;
        }
        return true;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPlmn);
        dest.writeInt(this.mLac);
        dest.writeInt(this.mCid);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}