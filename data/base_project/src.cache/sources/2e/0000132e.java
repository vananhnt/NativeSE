package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: CellSignalStrengthWcdma.class */
public final class CellSignalStrengthWcdma extends CellSignalStrength implements Parcelable {
    private static final String LOG_TAG = "CellSignalStrengthWcdma";
    private static final boolean DBG = false;
    private static final int WCDMA_SIGNAL_STRENGTH_GREAT = 12;
    private static final int WCDMA_SIGNAL_STRENGTH_GOOD = 8;
    private static final int WCDMA_SIGNAL_STRENGTH_MODERATE = 5;
    private int mSignalStrength;
    private int mBitErrorRate;
    public static final Parcelable.Creator<CellSignalStrengthWcdma> CREATOR = new Parcelable.Creator<CellSignalStrengthWcdma>() { // from class: android.telephony.CellSignalStrengthWcdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthWcdma createFromParcel(Parcel in) {
            return new CellSignalStrengthWcdma(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthWcdma[] newArray(int size) {
            return new CellSignalStrengthWcdma[size];
        }
    };

    public CellSignalStrengthWcdma() {
        setDefaultValues();
    }

    public CellSignalStrengthWcdma(int ss, int ber) {
        initialize(ss, ber);
    }

    public CellSignalStrengthWcdma(CellSignalStrengthWcdma s) {
        copyFrom(s);
    }

    public void initialize(int ss, int ber) {
        this.mSignalStrength = ss;
        this.mBitErrorRate = ber;
    }

    protected void copyFrom(CellSignalStrengthWcdma s) {
        this.mSignalStrength = s.mSignalStrength;
        this.mBitErrorRate = s.mBitErrorRate;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthWcdma copy() {
        return new CellSignalStrengthWcdma(this);
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mSignalStrength = Integer.MAX_VALUE;
        this.mBitErrorRate = Integer.MAX_VALUE;
    }

    @Override // android.telephony.CellSignalStrength
    public int getLevel() {
        int level;
        int asu = this.mSignalStrength;
        if (asu <= 2 || asu == 99) {
            level = 0;
        } else if (asu >= 12) {
            level = 4;
        } else if (asu >= 8) {
            level = 3;
        } else {
            level = asu >= 5 ? 2 : 1;
        }
        return level;
    }

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        int dBm;
        int level = this.mSignalStrength;
        int asu = level == 99 ? Integer.MAX_VALUE : level;
        if (asu != Integer.MAX_VALUE) {
            dBm = (-113) + (2 * asu);
        } else {
            dBm = Integer.MAX_VALUE;
        }
        return dBm;
    }

    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
        int level = this.mSignalStrength;
        return level;
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return (this.mSignalStrength * 31) + (this.mBitErrorRate * 31);
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object o) {
        try {
            CellSignalStrengthWcdma s = (CellSignalStrengthWcdma) o;
            return o != null && this.mSignalStrength == s.mSignalStrength && this.mBitErrorRate == s.mBitErrorRate;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return "CellSignalStrengthWcdma: ss=" + this.mSignalStrength + " ber=" + this.mBitErrorRate;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSignalStrength);
        dest.writeInt(this.mBitErrorRate);
    }

    private CellSignalStrengthWcdma(Parcel in) {
        this.mSignalStrength = in.readInt();
        this.mBitErrorRate = in.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private static void log(String s) {
        Rlog.w(LOG_TAG, s);
    }
}