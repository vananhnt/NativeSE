package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import gov.nist.core.Separators;

/* loaded from: CellInfoCdma.class */
public final class CellInfoCdma extends CellInfo implements Parcelable {
    private static final String LOG_TAG = "CellInfoCdma";
    private static final boolean DBG = false;
    private CellIdentityCdma mCellIdentityCdma;
    private CellSignalStrengthCdma mCellSignalStrengthCdma;
    public static final Parcelable.Creator<CellInfoCdma> CREATOR = new Parcelable.Creator<CellInfoCdma>() { // from class: android.telephony.CellInfoCdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellInfoCdma createFromParcel(Parcel in) {
            in.readInt();
            return CellInfoCdma.createFromParcelBody(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellInfoCdma[] newArray(int size) {
            return new CellInfoCdma[size];
        }
    };

    public CellInfoCdma() {
        this.mCellIdentityCdma = new CellIdentityCdma();
        this.mCellSignalStrengthCdma = new CellSignalStrengthCdma();
    }

    public CellInfoCdma(CellInfoCdma ci) {
        super(ci);
        this.mCellIdentityCdma = ci.mCellIdentityCdma.copy();
        this.mCellSignalStrengthCdma = ci.mCellSignalStrengthCdma.copy();
    }

    public CellIdentityCdma getCellIdentity() {
        return this.mCellIdentityCdma;
    }

    public void setCellIdentity(CellIdentityCdma cid) {
        this.mCellIdentityCdma = cid;
    }

    public CellSignalStrengthCdma getCellSignalStrength() {
        return this.mCellSignalStrengthCdma;
    }

    public void setCellSignalStrength(CellSignalStrengthCdma css) {
        this.mCellSignalStrengthCdma = css;
    }

    @Override // android.telephony.CellInfo
    public int hashCode() {
        return super.hashCode() + this.mCellIdentityCdma.hashCode() + this.mCellSignalStrengthCdma.hashCode();
    }

    @Override // android.telephony.CellInfo
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        try {
            CellInfoCdma o = (CellInfoCdma) other;
            if (this.mCellIdentityCdma.equals(o.mCellIdentityCdma)) {
                if (this.mCellSignalStrengthCdma.equals(o.mCellSignalStrengthCdma)) {
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
        sb.append("CellInfoCdma:{");
        sb.append(super.toString());
        sb.append(Separators.SP).append(this.mCellIdentityCdma);
        sb.append(Separators.SP).append(this.mCellSignalStrengthCdma);
        sb.append("}");
        return sb.toString();
    }

    @Override // android.telephony.CellInfo, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.CellInfo, android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags, 2);
        this.mCellIdentityCdma.writeToParcel(dest, flags);
        this.mCellSignalStrengthCdma.writeToParcel(dest, flags);
    }

    private CellInfoCdma(Parcel in) {
        super(in);
        this.mCellIdentityCdma = CellIdentityCdma.CREATOR.createFromParcel(in);
        this.mCellSignalStrengthCdma = CellSignalStrengthCdma.CREATOR.createFromParcel(in);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellInfoCdma createFromParcelBody(Parcel in) {
        return new CellInfoCdma(in);
    }

    private static void log(String s) {
        Rlog.w(LOG_TAG, s);
    }
}