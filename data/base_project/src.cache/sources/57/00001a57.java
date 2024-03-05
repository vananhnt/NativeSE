package com.android.internal.telephony;

import android.telephony.Rlog;

/* loaded from: CommandException.class */
public class CommandException extends RuntimeException {
    private Error mError;

    /* loaded from: CommandException$Error.class */
    public enum Error {
        INVALID_RESPONSE,
        RADIO_NOT_AVAILABLE,
        GENERIC_FAILURE,
        PASSWORD_INCORRECT,
        SIM_PIN2,
        SIM_PUK2,
        REQUEST_NOT_SUPPORTED,
        OP_NOT_ALLOWED_DURING_VOICE_CALL,
        OP_NOT_ALLOWED_BEFORE_REG_NW,
        SMS_FAIL_RETRY,
        SIM_ABSENT,
        SUBSCRIPTION_NOT_AVAILABLE,
        MODE_NOT_SUPPORTED,
        FDN_CHECK_FAILURE,
        ILLEGAL_SIM_OR_ME
    }

    public CommandException(Error e) {
        super(e.toString());
        this.mError = e;
    }

    public static CommandException fromRilErrno(int ril_errno) {
        switch (ril_errno) {
            case -1:
                return new CommandException(Error.INVALID_RESPONSE);
            case 0:
                return null;
            case 1:
                return new CommandException(Error.RADIO_NOT_AVAILABLE);
            case 2:
                return new CommandException(Error.GENERIC_FAILURE);
            case 3:
                return new CommandException(Error.PASSWORD_INCORRECT);
            case 4:
                return new CommandException(Error.SIM_PIN2);
            case 5:
                return new CommandException(Error.SIM_PUK2);
            case 6:
                return new CommandException(Error.REQUEST_NOT_SUPPORTED);
            case 7:
            default:
                Rlog.e("GSM", "Unrecognized RIL errno " + ril_errno);
                return new CommandException(Error.INVALID_RESPONSE);
            case 8:
                return new CommandException(Error.OP_NOT_ALLOWED_DURING_VOICE_CALL);
            case 9:
                return new CommandException(Error.OP_NOT_ALLOWED_BEFORE_REG_NW);
            case 10:
                return new CommandException(Error.SMS_FAIL_RETRY);
            case 11:
                return new CommandException(Error.SIM_ABSENT);
            case 12:
                return new CommandException(Error.SUBSCRIPTION_NOT_AVAILABLE);
            case 13:
                return new CommandException(Error.MODE_NOT_SUPPORTED);
            case 14:
                return new CommandException(Error.FDN_CHECK_FAILURE);
            case 15:
                return new CommandException(Error.ILLEGAL_SIM_OR_ME);
        }
    }

    public Error getCommandError() {
        return this.mError;
    }
}