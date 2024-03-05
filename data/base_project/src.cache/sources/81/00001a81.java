package com.android.internal.telephony;

/* loaded from: IccCardConstants.class */
public class IccCardConstants {
    public static final String INTENT_KEY_ICC_STATE = "ss";
    public static final String INTENT_VALUE_ICC_UNKNOWN = "UNKNOWN";
    public static final String INTENT_VALUE_ICC_NOT_READY = "NOT_READY";
    public static final String INTENT_VALUE_ICC_ABSENT = "ABSENT";
    public static final String INTENT_VALUE_ICC_LOCKED = "LOCKED";
    public static final String INTENT_VALUE_ICC_READY = "READY";
    public static final String INTENT_VALUE_ICC_IMSI = "IMSI";
    public static final String INTENT_VALUE_ICC_LOADED = "LOADED";
    public static final String INTENT_KEY_LOCKED_REASON = "reason";
    public static final String INTENT_VALUE_LOCKED_ON_PIN = "PIN";
    public static final String INTENT_VALUE_LOCKED_ON_PUK = "PUK";
    public static final String INTENT_VALUE_LOCKED_NETWORK = "NETWORK";
    public static final String INTENT_VALUE_ABSENT_ON_PERM_DISABLED = "PERM_DISABLED";

    /* loaded from: IccCardConstants$State.class */
    public enum State {
        UNKNOWN,
        ABSENT,
        PIN_REQUIRED,
        PUK_REQUIRED,
        NETWORK_LOCKED,
        READY,
        NOT_READY,
        PERM_DISABLED;

        public boolean isPinLocked() {
            return this == PIN_REQUIRED || this == PUK_REQUIRED;
        }

        public boolean iccCardExist() {
            return this == PIN_REQUIRED || this == PUK_REQUIRED || this == NETWORK_LOCKED || this == READY || this == PERM_DISABLED;
        }
    }
}