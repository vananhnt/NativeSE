package com.android.internal.telephony;

/* loaded from: MmiCode.class */
public interface MmiCode {

    /* loaded from: MmiCode$State.class */
    public enum State {
        PENDING,
        CANCELLED,
        COMPLETE,
        FAILED
    }

    State getState();

    CharSequence getMessage();

    void cancel();

    boolean isUssdRequest();

    boolean isCancelable();
}