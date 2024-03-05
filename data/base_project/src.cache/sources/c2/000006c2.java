package android.location;

/* loaded from: GpsSatellite.class */
public final class GpsSatellite {
    boolean mValid;
    boolean mHasEphemeris;
    boolean mHasAlmanac;
    boolean mUsedInFix;
    int mPrn;
    float mSnr;
    float mElevation;
    float mAzimuth;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GpsSatellite(int prn) {
        this.mPrn = prn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStatus(GpsSatellite satellite) {
        this.mValid = satellite.mValid;
        this.mHasEphemeris = satellite.mHasEphemeris;
        this.mHasAlmanac = satellite.mHasAlmanac;
        this.mUsedInFix = satellite.mUsedInFix;
        this.mSnr = satellite.mSnr;
        this.mElevation = satellite.mElevation;
        this.mAzimuth = satellite.mAzimuth;
    }

    public int getPrn() {
        return this.mPrn;
    }

    public float getSnr() {
        return this.mSnr;
    }

    public float getElevation() {
        return this.mElevation;
    }

    public float getAzimuth() {
        return this.mAzimuth;
    }

    public boolean hasEphemeris() {
        return this.mHasEphemeris;
    }

    public boolean hasAlmanac() {
        return this.mHasAlmanac;
    }

    public boolean usedInFix() {
        return this.mUsedInFix;
    }
}