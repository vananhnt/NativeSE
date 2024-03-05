package com.android.internal.telephony;

import android.telephony.Rlog;
import com.android.internal.telephony.Call;
import gov.nist.core.Separators;

/* loaded from: Connection.class */
public abstract class Connection {
    protected String mCnapName;
    protected int mCnapNamePresentation = PhoneConstants.PRESENTATION_ALLOWED;
    private static String LOG_TAG = "Connection";
    Object mUserData;

    /* loaded from: Connection$DisconnectCause.class */
    public enum DisconnectCause {
        NOT_DISCONNECTED,
        INCOMING_MISSED,
        NORMAL,
        LOCAL,
        BUSY,
        CONGESTION,
        MMI,
        INVALID_NUMBER,
        NUMBER_UNREACHABLE,
        SERVER_UNREACHABLE,
        INVALID_CREDENTIALS,
        OUT_OF_NETWORK,
        SERVER_ERROR,
        TIMED_OUT,
        LOST_SIGNAL,
        LIMIT_EXCEEDED,
        INCOMING_REJECTED,
        POWER_OFF,
        OUT_OF_SERVICE,
        ICC_ERROR,
        CALL_BARRED,
        FDN_BLOCKED,
        CS_RESTRICTED,
        CS_RESTRICTED_NORMAL,
        CS_RESTRICTED_EMERGENCY,
        UNOBTAINABLE_NUMBER,
        CDMA_LOCKED_UNTIL_POWER_CYCLE,
        CDMA_DROP,
        CDMA_INTERCEPT,
        CDMA_REORDER,
        CDMA_SO_REJECT,
        CDMA_RETRY_ORDER,
        CDMA_ACCESS_FAILURE,
        CDMA_PREEMPTED,
        CDMA_NOT_EMERGENCY,
        CDMA_ACCESS_BLOCKED,
        ERROR_UNSPECIFIED
    }

    /* loaded from: Connection$PostDialState.class */
    public enum PostDialState {
        NOT_STARTED,
        STARTED,
        WAIT,
        WILD,
        COMPLETE,
        CANCELLED,
        PAUSE
    }

    public abstract String getAddress();

    public abstract Call getCall();

    public abstract long getCreateTime();

    public abstract long getConnectTime();

    public abstract long getDisconnectTime();

    public abstract long getDurationMillis();

    public abstract long getHoldDurationMillis();

    public abstract DisconnectCause getDisconnectCause();

    public abstract boolean isIncoming();

    public abstract void hangup() throws CallStateException;

    public abstract void separate() throws CallStateException;

    public abstract PostDialState getPostDialState();

    public abstract String getRemainingPostDialString();

    public abstract void proceedAfterWaitChar();

    public abstract void proceedAfterWildChar(String str);

    public abstract void cancelPostDial();

    public abstract int getNumberPresentation();

    public abstract UUSInfo getUUSInfo();

    public String getCnapName() {
        return this.mCnapName;
    }

    public String getOrigDialString() {
        return null;
    }

    public int getCnapNamePresentation() {
        return this.mCnapNamePresentation;
    }

    public Call.State getState() {
        Call c = getCall();
        if (c == null) {
            return Call.State.IDLE;
        }
        return c.getState();
    }

    public boolean isAlive() {
        return getState().isAlive();
    }

    public boolean isRinging() {
        return getState().isRinging();
    }

    public Object getUserData() {
        return this.mUserData;
    }

    public void setUserData(Object userdata) {
        this.mUserData = userdata;
    }

    public void clearUserData() {
        this.mUserData = null;
    }

    public String toString() {
        StringBuilder str = new StringBuilder(128);
        if (Rlog.isLoggable(LOG_TAG, 3)) {
            str.append("addr: " + getAddress()).append(" pres.: " + getNumberPresentation()).append(" dial: " + getOrigDialString()).append(" postdial: " + getRemainingPostDialString()).append(" cnap name: " + getCnapName()).append(Separators.LPAREN + getCnapNamePresentation() + Separators.RPAREN);
        }
        str.append(" incoming: " + isIncoming()).append(" state: " + getState()).append(" post dial state: " + getPostDialState());
        return str.toString();
    }
}