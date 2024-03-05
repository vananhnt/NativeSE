package com.android.internal.telephony;

/* loaded from: PhoneConstants.class */
public class PhoneConstants {
    public static final String STATE_KEY = "state";
    public static final int PHONE_TYPE_NONE = 0;
    public static final int PHONE_TYPE_GSM = 1;
    public static final int PHONE_TYPE_CDMA = 2;
    public static final int PHONE_TYPE_SIP = 3;
    public static final int LTE_ON_CDMA_UNKNOWN = -1;
    public static final int LTE_ON_CDMA_FALSE = 0;
    public static final int LTE_ON_CDMA_TRUE = 1;
    public static int PRESENTATION_ALLOWED = 1;
    public static int PRESENTATION_RESTRICTED = 2;
    public static int PRESENTATION_UNKNOWN = 3;
    public static int PRESENTATION_PAYPHONE = 4;
    public static final String PHONE_NAME_KEY = "phoneName";
    public static final String FAILURE_REASON_KEY = "reason";
    public static final String STATE_CHANGE_REASON_KEY = "reason";
    public static final String DATA_APN_TYPE_KEY = "apnType";
    public static final String DATA_APN_KEY = "apn";
    public static final String DATA_LINK_PROPERTIES_KEY = "linkProperties";
    public static final String DATA_LINK_CAPABILITIES_KEY = "linkCapabilities";
    public static final String DATA_IFACE_NAME_KEY = "iface";
    public static final String NETWORK_UNAVAILABLE_KEY = "networkUnvailable";
    public static final String DATA_NETWORK_ROAMING_KEY = "networkRoaming";
    public static final String PHONE_IN_ECM_STATE = "phoneinECMState";
    public static final String REASON_LINK_PROPERTIES_CHANGED = "linkPropertiesChanged";
    public static final int APN_ALREADY_ACTIVE = 0;
    public static final int APN_REQUEST_STARTED = 1;
    public static final int APN_TYPE_NOT_AVAILABLE = 2;
    public static final int APN_REQUEST_FAILED = 3;
    public static final int APN_ALREADY_INACTIVE = 4;
    public static final String APN_TYPE_ALL = "*";
    public static final String APN_TYPE_DEFAULT = "default";
    public static final String APN_TYPE_MMS = "mms";
    public static final String APN_TYPE_SUPL = "supl";
    public static final String APN_TYPE_DUN = "dun";
    public static final String APN_TYPE_HIPRI = "hipri";
    public static final String APN_TYPE_FOTA = "fota";
    public static final String APN_TYPE_IMS = "ims";
    public static final String APN_TYPE_CBS = "cbs";
    public static final String APN_TYPE_IA = "ia";

    /* loaded from: PhoneConstants$DataState.class */
    public enum DataState {
        CONNECTED,
        CONNECTING,
        DISCONNECTED,
        SUSPENDED
    }

    /* loaded from: PhoneConstants$State.class */
    public enum State {
        IDLE,
        RINGING,
        OFFHOOK
    }
}