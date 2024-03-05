package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: CellSignalStrengthCdma.class */
public final class CellSignalStrengthCdma extends CellSignalStrength implements Parcelable {
    private static final String LOG_TAG = "CellSignalStrengthCdma";
    private static final boolean DBG = false;
    private int mCdmaDbm;
    private int mCdmaEcio;
    private int mEvdoDbm;
    private int mEvdoEcio;
    private int mEvdoSnr;
    public static final Parcelable.Creator<CellSignalStrengthCdma> CREATOR = new Parcelable.Creator<CellSignalStrengthCdma>() { // from class: android.telephony.CellSignalStrengthCdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthCdma createFromParcel(Parcel in) {
            return new CellSignalStrengthCdma(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellSignalStrengthCdma[] newArray(int size) {
            return new CellSignalStrengthCdma[size];
        }
    };

    public CellSignalStrengthCdma() {
        setDefaultValues();
    }

    public CellSignalStrengthCdma(int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr) {
        initialize(cdmaDbm, cdmaEcio, evdoDbm, evdoEcio, evdoSnr);
    }

    public CellSignalStrengthCdma(CellSignalStrengthCdma s) {
        copyFrom(s);
    }

    public void initialize(int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr) {
        this.mCdmaDbm = cdmaDbm;
        this.mCdmaEcio = cdmaEcio;
        this.mEvdoDbm = evdoDbm;
        this.mEvdoEcio = evdoEcio;
        this.mEvdoSnr = evdoSnr;
    }

    protected void copyFrom(CellSignalStrengthCdma s) {
        this.mCdmaDbm = s.mCdmaDbm;
        this.mCdmaEcio = s.mCdmaEcio;
        this.mEvdoDbm = s.mEvdoDbm;
        this.mEvdoEcio = s.mEvdoEcio;
        this.mEvdoSnr = s.mEvdoSnr;
    }

    @Override // android.telephony.CellSignalStrength
    public CellSignalStrengthCdma copy() {
        return new CellSignalStrengthCdma(this);
    }

    @Override // android.telephony.CellSignalStrength
    public void setDefaultValues() {
        this.mCdmaDbm = Integer.MAX_VALUE;
        this.mCdmaEcio = Integer.MAX_VALUE;
        this.mEvdoDbm = Integer.MAX_VALUE;
        this.mEvdoEcio = Integer.MAX_VALUE;
        this.mEvdoSnr = Integer.MAX_VALUE;
    }

    @Override // android.telephony.CellSignalStrength
    public int getLevel() {
        int level;
        int cdmaLevel = getCdmaLevel();
        int evdoLevel = getEvdoLevel();
        if (evdoLevel == 0) {
            level = getCdmaLevel();
        } else if (cdmaLevel == 0) {
            level = getEvdoLevel();
        } else {
            level = cdmaLevel < evdoLevel ? cdmaLevel : evdoLevel;
        }
        return level;
    }

    @Override // android.telephony.CellSignalStrength
    public int getAsuLevel() {
        int cdmaAsuLevel;
        int ecioAsuLevel;
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        if (cdmaDbm >= -75) {
            cdmaAsuLevel = 16;
        } else if (cdmaDbm >= -82) {
            cdmaAsuLevel = 8;
        } else if (cdmaDbm >= -90) {
            cdmaAsuLevel = 4;
        } else if (cdmaDbm >= -95) {
            cdmaAsuLevel = 2;
        } else {
            cdmaAsuLevel = cdmaDbm >= -100 ? 1 : 99;
        }
        if (cdmaEcio >= -90) {
            ecioAsuLevel = 16;
        } else if (cdmaEcio >= -100) {
            ecioAsuLevel = 8;
        } else if (cdmaEcio >= -115) {
            ecioAsuLevel = 4;
        } else if (cdmaEcio >= -130) {
            ecioAsuLevel = 2;
        } else {
            ecioAsuLevel = cdmaEcio >= -150 ? 1 : 99;
        }
        int level = cdmaAsuLevel < ecioAsuLevel ? cdmaAsuLevel : ecioAsuLevel;
        return level;
    }

    public int getCdmaLevel() {
        int levelDbm;
        int levelEcio;
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        if (cdmaDbm >= -75) {
            levelDbm = 4;
        } else if (cdmaDbm >= -85) {
            levelDbm = 3;
        } else if (cdmaDbm >= -95) {
            levelDbm = 2;
        } else {
            levelDbm = cdmaDbm >= -100 ? 1 : 0;
        }
        if (cdmaEcio >= -90) {
            levelEcio = 4;
        } else if (cdmaEcio >= -110) {
            levelEcio = 3;
        } else if (cdmaEcio >= -130) {
            levelEcio = 2;
        } else {
            levelEcio = cdmaEcio >= -150 ? 1 : 0;
        }
        int level = levelDbm < levelEcio ? levelDbm : levelEcio;
        return level;
    }

    public int getEvdoLevel() {
        int levelEvdoDbm;
        int levelEvdoSnr;
        int evdoDbm = getEvdoDbm();
        int evdoSnr = getEvdoSnr();
        if (evdoDbm >= -65) {
            levelEvdoDbm = 4;
        } else if (evdoDbm >= -75) {
            levelEvdoDbm = 3;
        } else if (evdoDbm >= -90) {
            levelEvdoDbm = 2;
        } else {
            levelEvdoDbm = evdoDbm >= -105 ? 1 : 0;
        }
        if (evdoSnr >= 7) {
            levelEvdoSnr = 4;
        } else if (evdoSnr >= 5) {
            levelEvdoSnr = 3;
        } else if (evdoSnr >= 3) {
            levelEvdoSnr = 2;
        } else {
            levelEvdoSnr = evdoSnr >= 1 ? 1 : 0;
        }
        int level = levelEvdoDbm < levelEvdoSnr ? levelEvdoDbm : levelEvdoSnr;
        return level;
    }

    @Override // android.telephony.CellSignalStrength
    public int getDbm() {
        int cdmaDbm = getCdmaDbm();
        int evdoDbm = getEvdoDbm();
        return cdmaDbm < evdoDbm ? cdmaDbm : evdoDbm;
    }

    public int getCdmaDbm() {
        return this.mCdmaDbm;
    }

    public void setCdmaDbm(int cdmaDbm) {
        this.mCdmaDbm = cdmaDbm;
    }

    public int getCdmaEcio() {
        return this.mCdmaEcio;
    }

    public void setCdmaEcio(int cdmaEcio) {
        this.mCdmaEcio = cdmaEcio;
    }

    public int getEvdoDbm() {
        return this.mEvdoDbm;
    }

    public void setEvdoDbm(int evdoDbm) {
        this.mEvdoDbm = evdoDbm;
    }

    public int getEvdoEcio() {
        return this.mEvdoEcio;
    }

    public void setEvdoEcio(int evdoEcio) {
        this.mEvdoEcio = evdoEcio;
    }

    public int getEvdoSnr() {
        return this.mEvdoSnr;
    }

    public void setEvdoSnr(int evdoSnr) {
        this.mEvdoSnr = evdoSnr;
    }

    @Override // android.telephony.CellSignalStrength
    public int hashCode() {
        return (this.mCdmaDbm * 31) + (this.mCdmaEcio * 31) + (this.mEvdoDbm * 31) + (this.mEvdoEcio * 31) + (this.mEvdoSnr * 31);
    }

    @Override // android.telephony.CellSignalStrength
    public boolean equals(Object o) {
        try {
            CellSignalStrengthCdma s = (CellSignalStrengthCdma) o;
            return o != null && this.mCdmaDbm == s.mCdmaDbm && this.mCdmaEcio == s.mCdmaEcio && this.mEvdoDbm == s.mEvdoDbm && this.mEvdoEcio == s.mEvdoEcio && this.mEvdoSnr == s.mEvdoSnr;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return "CellSignalStrengthCdma: cdmaDbm=" + this.mCdmaDbm + " cdmaEcio=" + this.mCdmaEcio + " evdoDbm=" + this.mEvdoDbm + " evdoEcio=" + this.mEvdoEcio + " evdoSnr=" + this.mEvdoSnr;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCdmaDbm * (-1));
        dest.writeInt(this.mCdmaEcio * (-1));
        dest.writeInt(this.mEvdoDbm * (-1));
        dest.writeInt(this.mEvdoEcio * (-1));
        dest.writeInt(this.mEvdoSnr);
    }

    private CellSignalStrengthCdma(Parcel in) {
        this.mCdmaDbm = in.readInt() * (-1);
        this.mCdmaEcio = in.readInt() * (-1);
        this.mEvdoDbm = in.readInt() * (-1);
        this.mEvdoEcio = in.readInt() * (-1);
        this.mEvdoSnr = in.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private static void log(String s) {
        Rlog.w(LOG_TAG, s);
    }
}