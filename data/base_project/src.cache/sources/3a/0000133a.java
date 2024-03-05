package android.telephony;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;
import gov.nist.core.Separators;
import javax.sip.header.SubscriptionStateHeader;

/* loaded from: ServiceState.class */
public class ServiceState implements Parcelable {
    static final String LOG_TAG = "PHONE";
    static final boolean DBG = true;
    public static final int STATE_IN_SERVICE = 0;
    public static final int STATE_OUT_OF_SERVICE = 1;
    public static final int STATE_EMERGENCY_ONLY = 2;
    public static final int STATE_POWER_OFF = 3;
    public static final int RIL_REG_STATE_NOT_REG = 0;
    public static final int RIL_REG_STATE_HOME = 1;
    public static final int RIL_REG_STATE_SEARCHING = 2;
    public static final int RIL_REG_STATE_DENIED = 3;
    public static final int RIL_REG_STATE_UNKNOWN = 4;
    public static final int RIL_REG_STATE_ROAMING = 5;
    public static final int RIL_REG_STATE_NOT_REG_EMERGENCY_CALL_ENABLED = 10;
    public static final int RIL_REG_STATE_SEARCHING_EMERGENCY_CALL_ENABLED = 12;
    public static final int RIL_REG_STATE_DENIED_EMERGENCY_CALL_ENABLED = 13;
    public static final int RIL_REG_STATE_UNKNOWN_EMERGENCY_CALL_ENABLED = 14;
    public static final int RIL_RADIO_TECHNOLOGY_UNKNOWN = 0;
    public static final int RIL_RADIO_TECHNOLOGY_GPRS = 1;
    public static final int RIL_RADIO_TECHNOLOGY_EDGE = 2;
    public static final int RIL_RADIO_TECHNOLOGY_UMTS = 3;
    public static final int RIL_RADIO_TECHNOLOGY_IS95A = 4;
    public static final int RIL_RADIO_TECHNOLOGY_IS95B = 5;
    public static final int RIL_RADIO_TECHNOLOGY_1xRTT = 6;
    public static final int RIL_RADIO_TECHNOLOGY_EVDO_0 = 7;
    public static final int RIL_RADIO_TECHNOLOGY_EVDO_A = 8;
    public static final int RIL_RADIO_TECHNOLOGY_HSDPA = 9;
    public static final int RIL_RADIO_TECHNOLOGY_HSUPA = 10;
    public static final int RIL_RADIO_TECHNOLOGY_HSPA = 11;
    public static final int RIL_RADIO_TECHNOLOGY_EVDO_B = 12;
    public static final int RIL_RADIO_TECHNOLOGY_EHRPD = 13;
    public static final int RIL_RADIO_TECHNOLOGY_LTE = 14;
    public static final int RIL_RADIO_TECHNOLOGY_HSPAP = 15;
    public static final int RIL_RADIO_TECHNOLOGY_GSM = 16;
    public static final int REGISTRATION_STATE_NOT_REGISTERED_AND_NOT_SEARCHING = 0;
    public static final int REGISTRATION_STATE_HOME_NETWORK = 1;
    public static final int REGISTRATION_STATE_NOT_REGISTERED_AND_SEARCHING = 2;
    public static final int REGISTRATION_STATE_REGISTRATION_DENIED = 3;
    public static final int REGISTRATION_STATE_UNKNOWN = 4;
    public static final int REGISTRATION_STATE_ROAMING = 5;
    private int mVoiceRegState;
    private int mDataRegState;
    private boolean mRoaming;
    private String mOperatorAlphaLong;
    private String mOperatorAlphaShort;
    private String mOperatorNumeric;
    private boolean mIsManualNetworkSelection;
    private boolean mIsEmergencyOnly;
    private int mRilVoiceRadioTechnology;
    private int mRilDataRadioTechnology;
    private boolean mCssIndicator;
    private int mNetworkId;
    private int mSystemId;
    private int mCdmaRoamingIndicator;
    private int mCdmaDefaultRoamingIndicator;
    private int mCdmaEriIconIndex;
    private int mCdmaEriIconMode;
    public static final Parcelable.Creator<ServiceState> CREATOR = new Parcelable.Creator<ServiceState>() { // from class: android.telephony.ServiceState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceState createFromParcel(Parcel in) {
            return new ServiceState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ServiceState[] newArray(int size) {
            return new ServiceState[size];
        }
    };

    public static ServiceState newFromBundle(Bundle m) {
        ServiceState ret = new ServiceState();
        ret.setFromNotifierBundle(m);
        return ret;
    }

    public ServiceState() {
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
    }

    public ServiceState(ServiceState s) {
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
        copyFrom(s);
    }

    protected void copyFrom(ServiceState s) {
        this.mVoiceRegState = s.mVoiceRegState;
        this.mDataRegState = s.mDataRegState;
        this.mRoaming = s.mRoaming;
        this.mOperatorAlphaLong = s.mOperatorAlphaLong;
        this.mOperatorAlphaShort = s.mOperatorAlphaShort;
        this.mOperatorNumeric = s.mOperatorNumeric;
        this.mIsManualNetworkSelection = s.mIsManualNetworkSelection;
        this.mRilVoiceRadioTechnology = s.mRilVoiceRadioTechnology;
        this.mRilDataRadioTechnology = s.mRilDataRadioTechnology;
        this.mCssIndicator = s.mCssIndicator;
        this.mNetworkId = s.mNetworkId;
        this.mSystemId = s.mSystemId;
        this.mCdmaRoamingIndicator = s.mCdmaRoamingIndicator;
        this.mCdmaDefaultRoamingIndicator = s.mCdmaDefaultRoamingIndicator;
        this.mCdmaEriIconIndex = s.mCdmaEriIconIndex;
        this.mCdmaEriIconMode = s.mCdmaEriIconMode;
        this.mIsEmergencyOnly = s.mIsEmergencyOnly;
    }

    public ServiceState(Parcel in) {
        this.mVoiceRegState = 1;
        this.mDataRegState = 1;
        this.mVoiceRegState = in.readInt();
        this.mDataRegState = in.readInt();
        this.mRoaming = in.readInt() != 0;
        this.mOperatorAlphaLong = in.readString();
        this.mOperatorAlphaShort = in.readString();
        this.mOperatorNumeric = in.readString();
        this.mIsManualNetworkSelection = in.readInt() != 0;
        this.mRilVoiceRadioTechnology = in.readInt();
        this.mRilDataRadioTechnology = in.readInt();
        this.mCssIndicator = in.readInt() != 0;
        this.mNetworkId = in.readInt();
        this.mSystemId = in.readInt();
        this.mCdmaRoamingIndicator = in.readInt();
        this.mCdmaDefaultRoamingIndicator = in.readInt();
        this.mCdmaEriIconIndex = in.readInt();
        this.mCdmaEriIconMode = in.readInt();
        this.mIsEmergencyOnly = in.readInt() != 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mVoiceRegState);
        out.writeInt(this.mDataRegState);
        out.writeInt(this.mRoaming ? 1 : 0);
        out.writeString(this.mOperatorAlphaLong);
        out.writeString(this.mOperatorAlphaShort);
        out.writeString(this.mOperatorNumeric);
        out.writeInt(this.mIsManualNetworkSelection ? 1 : 0);
        out.writeInt(this.mRilVoiceRadioTechnology);
        out.writeInt(this.mRilDataRadioTechnology);
        out.writeInt(this.mCssIndicator ? 1 : 0);
        out.writeInt(this.mNetworkId);
        out.writeInt(this.mSystemId);
        out.writeInt(this.mCdmaRoamingIndicator);
        out.writeInt(this.mCdmaDefaultRoamingIndicator);
        out.writeInt(this.mCdmaEriIconIndex);
        out.writeInt(this.mCdmaEriIconMode);
        out.writeInt(this.mIsEmergencyOnly ? 1 : 0);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getState() {
        return getVoiceRegState();
    }

    public int getVoiceRegState() {
        return this.mVoiceRegState;
    }

    public int getDataRegState() {
        return this.mDataRegState;
    }

    public boolean getRoaming() {
        return this.mRoaming;
    }

    public boolean isEmergencyOnly() {
        return this.mIsEmergencyOnly;
    }

    public int getCdmaRoamingIndicator() {
        return this.mCdmaRoamingIndicator;
    }

    public int getCdmaDefaultRoamingIndicator() {
        return this.mCdmaDefaultRoamingIndicator;
    }

    public int getCdmaEriIconIndex() {
        return this.mCdmaEriIconIndex;
    }

    public int getCdmaEriIconMode() {
        return this.mCdmaEriIconMode;
    }

    public String getOperatorAlphaLong() {
        return this.mOperatorAlphaLong;
    }

    public String getOperatorAlphaShort() {
        return this.mOperatorAlphaShort;
    }

    public String getOperatorNumeric() {
        return this.mOperatorNumeric;
    }

    public boolean getIsManualSelection() {
        return this.mIsManualNetworkSelection;
    }

    public int hashCode() {
        return (this.mVoiceRegState * 31) + (this.mDataRegState * 37) + (this.mRoaming ? 1 : 0) + (this.mIsManualNetworkSelection ? 1 : 0) + (null == this.mOperatorAlphaLong ? 0 : this.mOperatorAlphaLong.hashCode()) + (null == this.mOperatorAlphaShort ? 0 : this.mOperatorAlphaShort.hashCode()) + (null == this.mOperatorNumeric ? 0 : this.mOperatorNumeric.hashCode()) + this.mCdmaRoamingIndicator + this.mCdmaDefaultRoamingIndicator + (this.mIsEmergencyOnly ? 1 : 0);
    }

    public boolean equals(Object o) {
        try {
            ServiceState s = (ServiceState) o;
            return o != null && this.mVoiceRegState == s.mVoiceRegState && this.mDataRegState == s.mDataRegState && this.mRoaming == s.mRoaming && this.mIsManualNetworkSelection == s.mIsManualNetworkSelection && equalsHandlesNulls(this.mOperatorAlphaLong, s.mOperatorAlphaLong) && equalsHandlesNulls(this.mOperatorAlphaShort, s.mOperatorAlphaShort) && equalsHandlesNulls(this.mOperatorNumeric, s.mOperatorNumeric) && equalsHandlesNulls(Integer.valueOf(this.mRilVoiceRadioTechnology), Integer.valueOf(s.mRilVoiceRadioTechnology)) && equalsHandlesNulls(Integer.valueOf(this.mRilDataRadioTechnology), Integer.valueOf(s.mRilDataRadioTechnology)) && equalsHandlesNulls(Boolean.valueOf(this.mCssIndicator), Boolean.valueOf(s.mCssIndicator)) && equalsHandlesNulls(Integer.valueOf(this.mNetworkId), Integer.valueOf(s.mNetworkId)) && equalsHandlesNulls(Integer.valueOf(this.mSystemId), Integer.valueOf(s.mSystemId)) && equalsHandlesNulls(Integer.valueOf(this.mCdmaRoamingIndicator), Integer.valueOf(s.mCdmaRoamingIndicator)) && equalsHandlesNulls(Integer.valueOf(this.mCdmaDefaultRoamingIndicator), Integer.valueOf(s.mCdmaDefaultRoamingIndicator)) && this.mIsEmergencyOnly == s.mIsEmergencyOnly;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public static String rilRadioTechnologyToString(int rt) {
        String rtString;
        switch (rt) {
            case 0:
                rtString = SubscriptionStateHeader.UNKNOWN;
                break;
            case 1:
                rtString = "GPRS";
                break;
            case 2:
                rtString = "EDGE";
                break;
            case 3:
                rtString = "UMTS";
                break;
            case 4:
                rtString = "CDMA-IS95A";
                break;
            case 5:
                rtString = "CDMA-IS95B";
                break;
            case 6:
                rtString = "1xRTT";
                break;
            case 7:
                rtString = "EvDo-rev.0";
                break;
            case 8:
                rtString = "EvDo-rev.A";
                break;
            case 9:
                rtString = "HSDPA";
                break;
            case 10:
                rtString = "HSUPA";
                break;
            case 11:
                rtString = "HSPA";
                break;
            case 12:
                rtString = "EvDo-rev.B";
                break;
            case 13:
                rtString = "eHRPD";
                break;
            case 14:
                rtString = "LTE";
                break;
            case 15:
                rtString = "HSPAP";
                break;
            case 16:
                rtString = "GSM";
                break;
            default:
                rtString = "Unexpected";
                Rlog.w(LOG_TAG, "Unexpected radioTechnology=" + rt);
                break;
        }
        return rtString;
    }

    public String toString() {
        String radioTechnology = rilRadioTechnologyToString(this.mRilVoiceRadioTechnology);
        String dataRadioTechnology = rilRadioTechnologyToString(this.mRilDataRadioTechnology);
        return this.mVoiceRegState + Separators.SP + this.mDataRegState + Separators.SP + (this.mRoaming ? "roaming" : CalendarContract.CalendarCache.TIMEZONE_TYPE_HOME) + Separators.SP + this.mOperatorAlphaLong + Separators.SP + this.mOperatorAlphaShort + Separators.SP + this.mOperatorNumeric + Separators.SP + (this.mIsManualNetworkSelection ? "(manual)" : "") + Separators.SP + radioTechnology + Separators.SP + dataRadioTechnology + Separators.SP + (this.mCssIndicator ? "CSS supported" : "CSS not supported") + Separators.SP + this.mNetworkId + Separators.SP + this.mSystemId + " RoamInd=" + this.mCdmaRoamingIndicator + " DefRoamInd=" + this.mCdmaDefaultRoamingIndicator + " EmergOnly=" + this.mIsEmergencyOnly;
    }

    private void setNullState(int state) {
        Rlog.d(LOG_TAG, "[ServiceState] setNullState=" + state);
        this.mVoiceRegState = state;
        this.mDataRegState = state;
        this.mRoaming = false;
        this.mOperatorAlphaLong = null;
        this.mOperatorAlphaShort = null;
        this.mOperatorNumeric = null;
        this.mIsManualNetworkSelection = false;
        this.mRilVoiceRadioTechnology = 0;
        this.mRilDataRadioTechnology = 0;
        this.mCssIndicator = false;
        this.mNetworkId = -1;
        this.mSystemId = -1;
        this.mCdmaRoamingIndicator = -1;
        this.mCdmaDefaultRoamingIndicator = -1;
        this.mCdmaEriIconIndex = -1;
        this.mCdmaEriIconMode = -1;
        this.mIsEmergencyOnly = false;
    }

    public void setStateOutOfService() {
        setNullState(1);
    }

    public void setStateOff() {
        setNullState(3);
    }

    public void setState(int state) {
        setVoiceRegState(state);
        Rlog.e(LOG_TAG, "[ServiceState] setState deprecated use setVoiceRegState()");
    }

    public void setVoiceRegState(int state) {
        this.mVoiceRegState = state;
        Rlog.d(LOG_TAG, "[ServiceState] setVoiceRegState=" + this.mVoiceRegState);
    }

    public void setDataRegState(int state) {
        this.mDataRegState = state;
        Rlog.d(LOG_TAG, "[ServiceState] setDataRegState=" + this.mDataRegState);
    }

    public void setRoaming(boolean roaming) {
        this.mRoaming = roaming;
    }

    public void setEmergencyOnly(boolean emergencyOnly) {
        this.mIsEmergencyOnly = emergencyOnly;
    }

    public void setCdmaRoamingIndicator(int roaming) {
        this.mCdmaRoamingIndicator = roaming;
    }

    public void setCdmaDefaultRoamingIndicator(int roaming) {
        this.mCdmaDefaultRoamingIndicator = roaming;
    }

    public void setCdmaEriIconIndex(int index) {
        this.mCdmaEriIconIndex = index;
    }

    public void setCdmaEriIconMode(int mode) {
        this.mCdmaEriIconMode = mode;
    }

    public void setOperatorName(String longName, String shortName, String numeric) {
        this.mOperatorAlphaLong = longName;
        this.mOperatorAlphaShort = shortName;
        this.mOperatorNumeric = numeric;
    }

    public void setOperatorAlphaLong(String longName) {
        this.mOperatorAlphaLong = longName;
    }

    public void setIsManualSelection(boolean isManual) {
        this.mIsManualNetworkSelection = isManual;
    }

    private static boolean equalsHandlesNulls(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    private void setFromNotifierBundle(Bundle m) {
        this.mVoiceRegState = m.getInt("voiceRegState");
        this.mDataRegState = m.getInt("dataRegState");
        this.mRoaming = m.getBoolean("roaming");
        this.mOperatorAlphaLong = m.getString("operator-alpha-long");
        this.mOperatorAlphaShort = m.getString("operator-alpha-short");
        this.mOperatorNumeric = m.getString("operator-numeric");
        this.mIsManualNetworkSelection = m.getBoolean("manual");
        this.mRilVoiceRadioTechnology = m.getInt("radioTechnology");
        this.mRilVoiceRadioTechnology = m.getInt("dataRadioTechnology");
        this.mCssIndicator = m.getBoolean("cssIndicator");
        this.mNetworkId = m.getInt("networkId");
        this.mSystemId = m.getInt("systemId");
        this.mCdmaRoamingIndicator = m.getInt("cdmaRoamingIndicator");
        this.mCdmaDefaultRoamingIndicator = m.getInt("cdmaDefaultRoamingIndicator");
        this.mIsEmergencyOnly = m.getBoolean("emergencyOnly");
    }

    public void fillInNotifierBundle(Bundle m) {
        m.putInt("voiceRegState", this.mVoiceRegState);
        m.putInt("dataRegState", this.mDataRegState);
        m.putBoolean("roaming", Boolean.valueOf(this.mRoaming).booleanValue());
        m.putString("operator-alpha-long", this.mOperatorAlphaLong);
        m.putString("operator-alpha-short", this.mOperatorAlphaShort);
        m.putString("operator-numeric", this.mOperatorNumeric);
        m.putBoolean("manual", Boolean.valueOf(this.mIsManualNetworkSelection).booleanValue());
        m.putInt("radioTechnology", this.mRilVoiceRadioTechnology);
        m.putInt("dataRadioTechnology", this.mRilDataRadioTechnology);
        m.putBoolean("cssIndicator", this.mCssIndicator);
        m.putInt("networkId", this.mNetworkId);
        m.putInt("systemId", this.mSystemId);
        m.putInt("cdmaRoamingIndicator", this.mCdmaRoamingIndicator);
        m.putInt("cdmaDefaultRoamingIndicator", this.mCdmaDefaultRoamingIndicator);
        m.putBoolean("emergencyOnly", Boolean.valueOf(this.mIsEmergencyOnly).booleanValue());
    }

    public void setRilVoiceRadioTechnology(int rt) {
        this.mRilVoiceRadioTechnology = rt;
    }

    public void setRilDataRadioTechnology(int rt) {
        this.mRilDataRadioTechnology = rt;
        Rlog.d(LOG_TAG, "[ServiceState] setDataRadioTechnology=" + this.mRilDataRadioTechnology);
    }

    public void setCssIndicator(int css) {
        this.mCssIndicator = css != 0;
    }

    public void setSystemAndNetworkId(int systemId, int networkId) {
        this.mSystemId = systemId;
        this.mNetworkId = networkId;
    }

    public int getRilVoiceRadioTechnology() {
        return this.mRilVoiceRadioTechnology;
    }

    public int getRilDataRadioTechnology() {
        return this.mRilDataRadioTechnology;
    }

    public int getRadioTechnology() {
        Rlog.e(LOG_TAG, "ServiceState.getRadioTechnology() DEPRECATED will be removed *******");
        return getRilDataRadioTechnology();
    }

    private int rilRadioTechnologyToNetworkType(int rt) {
        switch (rt) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
            case 5:
                return 4;
            case 6:
                return 7;
            case 7:
                return 5;
            case 8:
                return 6;
            case 9:
                return 8;
            case 10:
                return 9;
            case 11:
                return 10;
            case 12:
                return 12;
            case 13:
                return 14;
            case 14:
                return 13;
            case 15:
                return 15;
            default:
                return 0;
        }
    }

    public int getNetworkType() {
        Rlog.e(LOG_TAG, "ServiceState.getNetworkType() DEPRECATED will be removed *******");
        return rilRadioTechnologyToNetworkType(this.mRilVoiceRadioTechnology);
    }

    public int getDataNetworkType() {
        return rilRadioTechnologyToNetworkType(this.mRilDataRadioTechnology);
    }

    public int getVoiceNetworkType() {
        return rilRadioTechnologyToNetworkType(this.mRilVoiceRadioTechnology);
    }

    public int getCssIndicator() {
        return this.mCssIndicator ? 1 : 0;
    }

    public int getNetworkId() {
        return this.mNetworkId;
    }

    public int getSystemId() {
        return this.mSystemId;
    }

    public static boolean isGsm(int radioTechnology) {
        return radioTechnology == 1 || radioTechnology == 2 || radioTechnology == 3 || radioTechnology == 9 || radioTechnology == 10 || radioTechnology == 11 || radioTechnology == 14 || radioTechnology == 15 || radioTechnology == 16;
    }

    public static boolean isCdma(int radioTechnology) {
        return radioTechnology == 4 || radioTechnology == 5 || radioTechnology == 6 || radioTechnology == 7 || radioTechnology == 8 || radioTechnology == 12 || radioTechnology == 13;
    }
}