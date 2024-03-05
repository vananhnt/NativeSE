package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: CellInfoWcdma.class */
public final class CellInfoWcdma extends CellInfo implements Parcelable {
    private static final String LOG_TAG = "CellInfoWcdma";
    private static final boolean DBG = false;
    private CellIdentityWcdma mCellIdentityWcdma;
    private CellSignalStrengthWcdma mCellSignalStrengthWcdma;
    public static final Parcelable.Creator<CellInfoWcdma> CREATOR = new Parcelable.Creator<CellInfoWcdma>() { // from class: android.telephony.CellInfoWcdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellInfoWcdma createFromParcel(Parcel in) {
            in.readInt();
            return CellInfoWcdma.createFromParcelBody(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellInfoWcdma[] newArray(int size) {
            return new CellInfoWcdma[size];
        }
    };

    public CellInfoWcdma() {
        this.mCellIdentityWcdma = new CellIdentityWcdma();
        this.mCellSignalStrengthWcdma = new CellSignalStrengthWcdma();
    }

    public CellInfoWcdma(CellInfoWcdma ci) {
        super(ci);
        this.mCellIdentityWcdma = ci.mCellIdentityWcdma.copy();
        this.mCellSignalStrengthWcdma = ci.mCellSignalStrengthWcdma.copy();
    }

    public CellIdentityWcdma getCellIdentity() {
        return this.mCellIdentityWcdma;
    }

    public void setCellIdentity(CellIdentityWcdma cid) {
        this.mCellIdentityWcdma = cid;
    }

    public CellSignalStrengthWcdma getCellSignalStrength() {
        return this.mCellSignalStrengthWcdma;
    }

    public void setCellSignalStrength(CellSignalStrengthWcdma css) {
        this.mCellSignalStrengthWcdma = css;
    }

    @Override // android.telephony.CellInfo
    public int hashCode() {
        return super.hashCode() + this.mCellIdentityWcdma.hashCode() + this.mCellSignalStrengthWcdma.hashCode();
    }

    @Override // android.telephony.CellInfo
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        try {
            CellInfoWcdma o = (CellInfoWcdma) other;
            if (this.mCellIdentityWcdma.equals(o.mCellIdentityWcdma)) {
                if (this.mCellSignalStrengthWcdma.equals(o.mCellSignalStrengthWcdma)) {
                    return true;
                }
            }
            return false;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override // android.telephony.CellInfo
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CellInfoWcdma:{");
        sb.append(super.toString());
        sb.append(Separators.SP).append(this.mCellIdentityWcdma);
        sb.append(Separators.SP).append(this.mCellSignalStrengthWcdma);
        sb.append("}");
        return sb.toString();
    }

    @Override // android.telephony.CellInfo, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.CellInfo, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags, 4);
        this.mCellIdentityWcdma.writeToParcel(dest, flags);
        this.mCellSignalStrengthWcdma.writeToParcel(dest, flags);
    }

    private CellInfoWcdma(Parcel in) {
        super(in);
        this.mCellIdentityWcdma = CellIdentityWcdma.CREATOR.createFromParcel(in);
        this.mCellSignalStrengthWcdma = CellSignalStrengthWcdma.CREATOR.createFromParcel(in);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellInfoWcdma createFromParcelBody(Parcel in) {
        return new CellInfoWcdma(in);
    }

    private static void log(String s) {
        Rlog.w(LOG_TAG, s);
    }
}