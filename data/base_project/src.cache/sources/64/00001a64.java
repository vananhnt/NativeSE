package com.android.internal.telephony;

import android.speech.srec.Recognizer;
import android.telephony.PhoneNumberUtils;
import android.telephony.Rlog;
import gov.nist.core.Separators;

/* loaded from: DriverCall.class */
public class DriverCall implements Comparable<DriverCall> {
    static final String LOG_TAG = "DriverCall";
    public int index;
    public boolean isMT;
    public State state;
    public boolean isMpty;
    public String number;
    public int TOA;
    public boolean isVoice;
    public boolean isVoicePrivacy;
    public int als;
    public int numberPresentation;
    public String name;
    public int namePresentation;
    public UUSInfo uusInfo;

    /* loaded from: DriverCall$State.class */
    public enum State {
        ACTIVE,
        HOLDING,
        DIALING,
        ALERTING,
        INCOMING,
        WAITING
    }

    static DriverCall fromCLCCLine(String line) {
        DriverCall ret = new DriverCall();
        ATResponseParser p = new ATResponseParser(line);
        try {
            ret.index = p.nextInt();
            ret.isMT = p.nextBoolean();
            ret.state = stateFromCLCC(p.nextInt());
            ret.isVoice = 0 == p.nextInt();
            ret.isMpty = p.nextBoolean();
            ret.numberPresentation = PhoneConstants.PRESENTATION_ALLOWED;
            if (p.hasMore()) {
                ret.number = PhoneNumberUtils.extractNetworkPortionAlt(p.nextString());
                if (ret.number.length() == 0) {
                    ret.number = null;
                }
                ret.TOA = p.nextInt();
                ret.number = PhoneNumberUtils.stringFromStringAndTOA(ret.number, ret.TOA);
            }
            return ret;
        } catch (ATParseEx e) {
            Rlog.e(LOG_TAG, "Invalid CLCC line: '" + line + Separators.QUOTE);
            return null;
        }
    }

    public String toString() {
        return "id=" + this.index + Separators.COMMA + this.state + Separators.COMMA + "toa=" + this.TOA + Separators.COMMA + (this.isMpty ? Recognizer.KEY_CONFIDENCE : "norm") + Separators.COMMA + (this.isMT ? "mt" : "mo") + Separators.COMMA + this.als + Separators.COMMA + (this.isVoice ? "voc" : "nonvoc") + Separators.COMMA + (this.isVoicePrivacy ? "evp" : "noevp") + Separators.COMMA + ",cli=" + this.numberPresentation + Separators.COMMA + Separators.COMMA + this.namePresentation;
    }

    public static State stateFromCLCC(int state) throws ATParseEx {
        switch (state) {
            case 0:
                return State.ACTIVE;
            case 1:
                return State.HOLDING;
            case 2:
                return State.DIALING;
            case 3:
                return State.ALERTING;
            case 4:
                return State.INCOMING;
            case 5:
                return State.WAITING;
            default:
                throw new ATParseEx("illegal call state " + state);
        }
    }

    public static int presentationFromCLIP(int cli) throws ATParseEx {
        switch (cli) {
            case 0:
                return PhoneConstants.PRESENTATION_ALLOWED;
            case 1:
                return PhoneConstants.PRESENTATION_RESTRICTED;
            case 2:
                return PhoneConstants.PRESENTATION_UNKNOWN;
            case 3:
                return PhoneConstants.PRESENTATION_PAYPHONE;
            default:
                throw new ATParseEx("illegal presentation " + cli);
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(DriverCall dc) {
        if (this.index < dc.index) {
            return -1;
        }
        if (this.index == dc.index) {
            return 0;
        }
        return 1;
    }
}