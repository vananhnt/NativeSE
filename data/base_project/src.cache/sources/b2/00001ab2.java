package com.android.internal.telephony;

/* loaded from: RestrictedState.class */
public class RestrictedState {
    private boolean mPsRestricted;
    private boolean mCsNormalRestricted;
    private boolean mCsEmergencyRestricted;

    public RestrictedState() {
        setPsRestricted(false);
        setCsNormalRestricted(false);
        setCsEmergencyRestricted(false);
    }

    public void setCsEmergencyRestricted(boolean csEmergencyRestricted) {
        this.mCsEmergencyRestricted = csEmergencyRestricted;
    }

    public boolean isCsEmergencyRestricted() {
        return this.mCsEmergencyRestricted;
    }

    public void setCsNormalRestricted(boolean csNormalRestricted) {
        this.mCsNormalRestricted = csNormalRestricted;
    }

    public boolean isCsNormalRestricted() {
        return this.mCsNormalRestricted;
    }

    public void setPsRestricted(boolean psRestricted) {
        this.mPsRestricted = psRestricted;
    }

    public boolean isPsRestricted() {
        return this.mPsRestricted;
    }

    public boolean isCsRestricted() {
        return this.mCsNormalRestricted && this.mCsEmergencyRestricted;
    }

    public boolean equals(Object o) {
        try {
            RestrictedState s = (RestrictedState) o;
            return o != null && this.mPsRestricted == s.mPsRestricted && this.mCsNormalRestricted == s.mCsNormalRestricted && this.mCsEmergencyRestricted == s.mCsEmergencyRestricted;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String csString = "none";
        if (this.mCsEmergencyRestricted && this.mCsNormalRestricted) {
            csString = "all";
        } else if (this.mCsEmergencyRestricted && !this.mCsNormalRestricted) {
            csString = "emergency";
        } else if (!this.mCsEmergencyRestricted && this.mCsNormalRestricted) {
            csString = "normal call";
        }
        return "Restricted State CS: " + csString + " PS:" + this.mPsRestricted;
    }
}