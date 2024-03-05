package android.telephony;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: SignalStrength.class */
public class SignalStrength implements Parcelable {
    private static final String LOG_TAG = "SignalStrength";
    private static final boolean DBG = false;
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static final int NUM_SIGNAL_STRENGTH_BINS = 5;
    public static final int INVALID = Integer.MAX_VALUE;
    private int mGsmSignalStrength;
    private int mGsmBitErrorRate;
    private int mCdmaDbm;
    private int mCdmaEcio;
    private int mEvdoDbm;
    private int mEvdoEcio;
    private int mEvdoSnr;
    private int mLteSignalStrength;
    private int mLteRsrp;
    private int mLteRsrq;
    private int mLteRssnr;
    private int mLteCqi;
    private boolean isGsm;
    public static final String[] SIGNAL_STRENGTH_NAMES = {"none", "poor", "moderate", "good", "great"};
    public static final Parcelable.Creator<SignalStrength> CREATOR = new Parcelable.Creator() { // from class: android.telephony.SignalStrength.1
        @Override // android.os.Parcelable.Creator
        public SignalStrength createFromParcel(Parcel in) {
            return new SignalStrength(in);
        }

        @Override // android.os.Parcelable.Creator
        public SignalStrength[] newArray(int size) {
            return new SignalStrength[size];
        }
    };

    public static SignalStrength newFromBundle(Bundle m) {
        SignalStrength ret = new SignalStrength();
        ret.setFromNotifierBundle(m);
        return ret;
    }

    public SignalStrength() {
        this.mGsmSignalStrength = 99;
        this.mGsmBitErrorRate = -1;
        this.mCdmaDbm = -1;
        this.mCdmaEcio = -1;
        this.mEvdoDbm = -1;
        this.mEvdoEcio = -1;
        this.mEvdoSnr = -1;
        this.mLteSignalStrength = 99;
        this.mLteRsrp = Integer.MAX_VALUE;
        this.mLteRsrq = Integer.MAX_VALUE;
        this.mLteRssnr = Integer.MAX_VALUE;
        this.mLteCqi = Integer.MAX_VALUE;
        this.isGsm = true;
    }

    public SignalStrength(boolean gsmFlag) {
        this.mGsmSignalStrength = 99;
        this.mGsmBitErrorRate = -1;
        this.mCdmaDbm = -1;
        this.mCdmaEcio = -1;
        this.mEvdoDbm = -1;
        this.mEvdoEcio = -1;
        this.mEvdoSnr = -1;
        this.mLteSignalStrength = 99;
        this.mLteRsrp = Integer.MAX_VALUE;
        this.mLteRsrq = Integer.MAX_VALUE;
        this.mLteRssnr = Integer.MAX_VALUE;
        this.mLteCqi = Integer.MAX_VALUE;
        this.isGsm = gsmFlag;
    }

    public SignalStrength(int gsmSignalStrength, int gsmBitErrorRate, int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr, int lteSignalStrength, int lteRsrp, int lteRsrq, int lteRssnr, int lteCqi, boolean gsmFlag) {
        initialize(gsmSignalStrength, gsmBitErrorRate, cdmaDbm, cdmaEcio, evdoDbm, evdoEcio, evdoSnr, lteSignalStrength, lteRsrp, lteRsrq, lteRssnr, lteCqi, gsmFlag);
    }

    public SignalStrength(int gsmSignalStrength, int gsmBitErrorRate, int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr, boolean gsmFlag) {
        initialize(gsmSignalStrength, gsmBitErrorRate, cdmaDbm, cdmaEcio, evdoDbm, evdoEcio, evdoSnr, 99, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, gsmFlag);
    }

    public SignalStrength(SignalStrength s) {
        copyFrom(s);
    }

    public void initialize(int gsmSignalStrength, int gsmBitErrorRate, int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr, boolean gsm) {
        initialize(gsmSignalStrength, gsmBitErrorRate, cdmaDbm, cdmaEcio, evdoDbm, evdoEcio, evdoSnr, 99, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, gsm);
    }

    public void initialize(int gsmSignalStrength, int gsmBitErrorRate, int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr, int lteSignalStrength, int lteRsrp, int lteRsrq, int lteRssnr, int lteCqi, boolean gsm) {
        this.mGsmSignalStrength = gsmSignalStrength;
        this.mGsmBitErrorRate = gsmBitErrorRate;
        this.mCdmaDbm = cdmaDbm;
        this.mCdmaEcio = cdmaEcio;
        this.mEvdoDbm = evdoDbm;
        this.mEvdoEcio = evdoEcio;
        this.mEvdoSnr = evdoSnr;
        this.mLteSignalStrength = lteSignalStrength;
        this.mLteRsrp = lteRsrp;
        this.mLteRsrq = lteRsrq;
        this.mLteRssnr = lteRssnr;
        this.mLteCqi = lteCqi;
        this.isGsm = gsm;
    }

    protected void copyFrom(SignalStrength s) {
        this.mGsmSignalStrength = s.mGsmSignalStrength;
        this.mGsmBitErrorRate = s.mGsmBitErrorRate;
        this.mCdmaDbm = s.mCdmaDbm;
        this.mCdmaEcio = s.mCdmaEcio;
        this.mEvdoDbm = s.mEvdoDbm;
        this.mEvdoEcio = s.mEvdoEcio;
        this.mEvdoSnr = s.mEvdoSnr;
        this.mLteSignalStrength = s.mLteSignalStrength;
        this.mLteRsrp = s.mLteRsrp;
        this.mLteRsrq = s.mLteRsrq;
        this.mLteRssnr = s.mLteRssnr;
        this.mLteCqi = s.mLteCqi;
        this.isGsm = s.isGsm;
    }

    public SignalStrength(Parcel in) {
        this.mGsmSignalStrength = in.readInt();
        this.mGsmBitErrorRate = in.readInt();
        this.mCdmaDbm = in.readInt();
        this.mCdmaEcio = in.readInt();
        this.mEvdoDbm = in.readInt();
        this.mEvdoEcio = in.readInt();
        this.mEvdoSnr = in.readInt();
        this.mLteSignalStrength = in.readInt();
        this.mLteRsrp = in.readInt();
        this.mLteRsrq = in.readInt();
        this.mLteRssnr = in.readInt();
        this.mLteCqi = in.readInt();
        this.isGsm = in.readInt() != 0;
    }

    public static SignalStrength makeSignalStrengthFromRilParcel(Parcel in) {
        SignalStrength ss = new SignalStrength();
        ss.mGsmSignalStrength = in.readInt();
        ss.mGsmBitErrorRate = in.readInt();
        ss.mCdmaDbm = in.readInt();
        ss.mCdmaEcio = in.readInt();
        ss.mEvdoDbm = in.readInt();
        ss.mEvdoEcio = in.readInt();
        ss.mEvdoSnr = in.readInt();
        ss.mLteSignalStrength = in.readInt();
        ss.mLteRsrp = in.readInt();
        ss.mLteRsrq = in.readInt();
        ss.mLteRssnr = in.readInt();
        ss.mLteCqi = in.readInt();
        return ss;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mGsmSignalStrength);
        out.writeInt(this.mGsmBitErrorRate);
        out.writeInt(this.mCdmaDbm);
        out.writeInt(this.mCdmaEcio);
        out.writeInt(this.mEvdoDbm);
        out.writeInt(this.mEvdoEcio);
        out.writeInt(this.mEvdoSnr);
        out.writeInt(this.mLteSignalStrength);
        out.writeInt(this.mLteRsrp);
        out.writeInt(this.mLteRsrq);
        out.writeInt(this.mLteRssnr);
        out.writeInt(this.mLteCqi);
        out.writeInt(this.isGsm ? 1 : 0);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void validateInput() {
        this.mGsmSignalStrength = this.mGsmSignalStrength >= 0 ? this.mGsmSignalStrength : 99;
        this.mCdmaDbm = this.mCdmaDbm > 0 ? -this.mCdmaDbm : -120;
        this.mCdmaEcio = this.mCdmaEcio > 0 ? -this.mCdmaEcio : -160;
        this.mEvdoDbm = this.mEvdoDbm > 0 ? -this.mEvdoDbm : -120;
        this.mEvdoEcio = this.mEvdoEcio >= 0 ? -this.mEvdoEcio : -1;
        this.mEvdoSnr = (this.mEvdoSnr <= 0 || this.mEvdoSnr > 8) ? -1 : this.mEvdoSnr;
        this.mLteSignalStrength = this.mLteSignalStrength >= 0 ? this.mLteSignalStrength : 99;
        this.mLteRsrp = (this.mLteRsrp < 44 || this.mLteRsrp > 140) ? Integer.MAX_VALUE : -this.mLteRsrp;
        this.mLteRsrq = (this.mLteRsrq < 3 || this.mLteRsrq > 20) ? Integer.MAX_VALUE : -this.mLteRsrq;
        this.mLteRssnr = (this.mLteRssnr < -200 || this.mLteRssnr > 300) ? Integer.MAX_VALUE : this.mLteRssnr;
    }

    public void setGsm(boolean gsmFlag) {
        this.isGsm = gsmFlag;
    }

    public int getGsmSignalStrength() {
        return this.mGsmSignalStrength;
    }

    public int getGsmBitErrorRate() {
        return this.mGsmBitErrorRate;
    }

    public int getCdmaDbm() {
        return this.mCdmaDbm;
    }

    public int getCdmaEcio() {
        return this.mCdmaEcio;
    }

    public int getEvdoDbm() {
        return this.mEvdoDbm;
    }

    public int getEvdoEcio() {
        return this.mEvdoEcio;
    }

    public int getEvdoSnr() {
        return this.mEvdoSnr;
    }

    public int getLteSignalStrength() {
        return this.mLteSignalStrength;
    }

    public int getLteRsrp() {
        return this.mLteRsrp;
    }

    public int getLteRsrq() {
        return this.mLteRsrq;
    }

    public int getLteRssnr() {
        return this.mLteRssnr;
    }

    public int getLteCqi() {
        return this.mLteCqi;
    }

    public int getLevel() {
        int level;
        if (this.isGsm) {
            level = getLteLevel();
            if (level == 0) {
                level = getGsmLevel();
            }
        } else {
            int cdmaLevel = getCdmaLevel();
            int evdoLevel = getEvdoLevel();
            if (evdoLevel == 0) {
                level = cdmaLevel;
            } else if (cdmaLevel == 0) {
                level = evdoLevel;
            } else {
                level = cdmaLevel < evdoLevel ? cdmaLevel : evdoLevel;
            }
        }
        return level;
    }

    public int getAsuLevel() {
        int asuLevel;
        if (this.isGsm) {
            if (getLteLevel() == 0) {
                asuLevel = getGsmAsuLevel();
            } else {
                asuLevel = getLteAsuLevel();
            }
        } else {
            int cdmaAsuLevel = getCdmaAsuLevel();
            int evdoAsuLevel = getEvdoAsuLevel();
            if (evdoAsuLevel == 0) {
                asuLevel = cdmaAsuLevel;
            } else if (cdmaAsuLevel == 0) {
                asuLevel = evdoAsuLevel;
            } else {
                asuLevel = cdmaAsuLevel < evdoAsuLevel ? cdmaAsuLevel : evdoAsuLevel;
            }
        }
        return asuLevel;
    }

    public int getDbm() {
        int dBm;
        if (isGsm()) {
            if (getLteLevel() == 0) {
                dBm = getGsmDbm();
            } else {
                dBm = getLteDbm();
            }
            return dBm;
        }
        int cdmaDbm = getCdmaDbm();
        int evdoDbm = getEvdoDbm();
        if (evdoDbm == -120) {
            return cdmaDbm;
        }
        if (cdmaDbm != -120 && cdmaDbm < evdoDbm) {
            return cdmaDbm;
        }
        return evdoDbm;
    }

    public int getGsmDbm() {
        int dBm;
        int gsmSignalStrength = getGsmSignalStrength();
        int asu = gsmSignalStrength == 99 ? -1 : gsmSignalStrength;
        if (asu != -1) {
            dBm = (-113) + (2 * asu);
        } else {
            dBm = -1;
        }
        return dBm;
    }

    public int getGsmLevel() {
        int level;
        int asu = getGsmSignalStrength();
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

    public int getGsmAsuLevel() {
        int level = getGsmSignalStrength();
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

    public int getCdmaAsuLevel() {
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

    public int getEvdoAsuLevel() {
        int levelEvdoDbm;
        int levelEvdoSnr;
        int evdoDbm = getEvdoDbm();
        int evdoSnr = getEvdoSnr();
        if (evdoDbm >= -65) {
            levelEvdoDbm = 16;
        } else if (evdoDbm >= -75) {
            levelEvdoDbm = 8;
        } else if (evdoDbm >= -85) {
            levelEvdoDbm = 4;
        } else if (evdoDbm >= -95) {
            levelEvdoDbm = 2;
        } else {
            levelEvdoDbm = evdoDbm >= -105 ? 1 : 99;
        }
        if (evdoSnr >= 7) {
            levelEvdoSnr = 16;
        } else if (evdoSnr >= 6) {
            levelEvdoSnr = 8;
        } else if (evdoSnr >= 5) {
            levelEvdoSnr = 4;
        } else if (evdoSnr >= 3) {
            levelEvdoSnr = 2;
        } else {
            levelEvdoSnr = evdoSnr >= 1 ? 1 : 99;
        }
        int level = levelEvdoDbm < levelEvdoSnr ? levelEvdoDbm : levelEvdoSnr;
        return level;
    }

    public int getLteDbm() {
        return this.mLteRsrp;
    }

    public int getLteLevel() {
        int rssiIconLevel = 0;
        int rsrpIconLevel = -1;
        int snrIconLevel = -1;
        if (this.mLteRsrp > -44) {
            rsrpIconLevel = -1;
        } else if (this.mLteRsrp >= -85) {
            rsrpIconLevel = 4;
        } else if (this.mLteRsrp >= -95) {
            rsrpIconLevel = 3;
        } else if (this.mLteRsrp >= -105) {
            rsrpIconLevel = 2;
        } else if (this.mLteRsrp >= -115) {
            rsrpIconLevel = 1;
        } else if (this.mLteRsrp >= -140) {
            rsrpIconLevel = 0;
        }
        if (this.mLteRssnr > 300) {
            snrIconLevel = -1;
        } else if (this.mLteRssnr >= 130) {
            snrIconLevel = 4;
        } else if (this.mLteRssnr >= 45) {
            snrIconLevel = 3;
        } else if (this.mLteRssnr >= 10) {
            snrIconLevel = 2;
        } else if (this.mLteRssnr >= -30) {
            snrIconLevel = 1;
        } else if (this.mLteRssnr >= -200) {
            snrIconLevel = 0;
        }
        if (snrIconLevel != -1 && rsrpIconLevel != -1) {
            return rsrpIconLevel < snrIconLevel ? rsrpIconLevel : snrIconLevel;
        } else if (snrIconLevel != -1) {
            return snrIconLevel;
        } else {
            if (rsrpIconLevel != -1) {
                return rsrpIconLevel;
            }
            if (this.mLteSignalStrength > 63) {
                rssiIconLevel = 0;
            } else if (this.mLteSignalStrength >= 12) {
                rssiIconLevel = 4;
            } else if (this.mLteSignalStrength >= 8) {
                rssiIconLevel = 3;
            } else if (this.mLteSignalStrength >= 5) {
                rssiIconLevel = 2;
            } else if (this.mLteSignalStrength >= 0) {
                rssiIconLevel = 1;
            }
            return rssiIconLevel;
        }
    }

    public int getLteAsuLevel() {
        int lteDbm = getLteDbm();
        int lteAsuLevel = lteDbm == Integer.MAX_VALUE ? 255 : lteDbm + 140;
        return lteAsuLevel;
    }

    public boolean isGsm() {
        return this.isGsm;
    }

    public int hashCode() {
        return (this.mGsmSignalStrength * 31) + (this.mGsmBitErrorRate * 31) + (this.mCdmaDbm * 31) + (this.mCdmaEcio * 31) + (this.mEvdoDbm * 31) + (this.mEvdoEcio * 31) + (this.mEvdoSnr * 31) + (this.mLteSignalStrength * 31) + (this.mLteRsrp * 31) + (this.mLteRsrq * 31) + (this.mLteRssnr * 31) + (this.mLteCqi * 31) + (this.isGsm ? 1 : 0);
    }

    public boolean equals(Object o) {
        try {
            SignalStrength s = (SignalStrength) o;
            return o != null && this.mGsmSignalStrength == s.mGsmSignalStrength && this.mGsmBitErrorRate == s.mGsmBitErrorRate && this.mCdmaDbm == s.mCdmaDbm && this.mCdmaEcio == s.mCdmaEcio && this.mEvdoDbm == s.mEvdoDbm && this.mEvdoEcio == s.mEvdoEcio && this.mEvdoSnr == s.mEvdoSnr && this.mLteSignalStrength == s.mLteSignalStrength && this.mLteRsrp == s.mLteRsrp && this.mLteRsrq == s.mLteRsrq && this.mLteRssnr == s.mLteRssnr && this.mLteCqi == s.mLteCqi && this.isGsm == s.isGsm;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return "SignalStrength: " + this.mGsmSignalStrength + Separators.SP + this.mGsmBitErrorRate + Separators.SP + this.mCdmaDbm + Separators.SP + this.mCdmaEcio + Separators.SP + this.mEvdoDbm + Separators.SP + this.mEvdoEcio + Separators.SP + this.mEvdoSnr + Separators.SP + this.mLteSignalStrength + Separators.SP + this.mLteRsrp + Separators.SP + this.mLteRsrq + Separators.SP + this.mLteRssnr + Separators.SP + this.mLteCqi + Separators.SP + (this.isGsm ? "gsm|lte" : "cdma");
    }

    private void setFromNotifierBundle(Bundle m) {
        this.mGsmSignalStrength = m.getInt("GsmSignalStrength");
        this.mGsmBitErrorRate = m.getInt("GsmBitErrorRate");
        this.mCdmaDbm = m.getInt("CdmaDbm");
        this.mCdmaEcio = m.getInt("CdmaEcio");
        this.mEvdoDbm = m.getInt("EvdoDbm");
        this.mEvdoEcio = m.getInt("EvdoEcio");
        this.mEvdoSnr = m.getInt("EvdoSnr");
        this.mLteSignalStrength = m.getInt("LteSignalStrength");
        this.mLteRsrp = m.getInt("LteRsrp");
        this.mLteRsrq = m.getInt("LteRsrq");
        this.mLteRssnr = m.getInt("LteRssnr");
        this.mLteCqi = m.getInt("LteCqi");
        this.isGsm = m.getBoolean("isGsm");
    }

    public void fillInNotifierBundle(Bundle m) {
        m.putInt("GsmSignalStrength", this.mGsmSignalStrength);
        m.putInt("GsmBitErrorRate", this.mGsmBitErrorRate);
        m.putInt("CdmaDbm", this.mCdmaDbm);
        m.putInt("CdmaEcio", this.mCdmaEcio);
        m.putInt("EvdoDbm", this.mEvdoDbm);
        m.putInt("EvdoEcio", this.mEvdoEcio);
        m.putInt("EvdoSnr", this.mEvdoSnr);
        m.putInt("LteSignalStrength", this.mLteSignalStrength);
        m.putInt("LteRsrp", this.mLteRsrp);
        m.putInt("LteRsrq", this.mLteRsrq);
        m.putInt("LteRssnr", this.mLteRssnr);
        m.putInt("LteCqi", this.mLteCqi);
        m.putBoolean("isGsm", Boolean.valueOf(this.isGsm).booleanValue());
    }

    private static void log(String s) {
        Rlog.w(LOG_TAG, s);
    }
}