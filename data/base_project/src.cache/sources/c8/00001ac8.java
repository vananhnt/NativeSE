package com.android.internal.telephony;

import android.provider.Telephony;
import com.android.internal.telephony.SmsConstants;
import java.util.Arrays;

/* loaded from: SmsMessageBase.class */
public abstract class SmsMessageBase {
    protected String mScAddress;
    protected SmsAddress mOriginatingAddress;
    protected String mMessageBody;
    protected String mPseudoSubject;
    protected String mEmailFrom;
    protected String mEmailBody;
    protected boolean mIsEmail;
    protected long mScTimeMillis;
    protected byte[] mPdu;
    protected byte[] mUserData;
    protected SmsHeader mUserDataHeader;
    protected boolean mIsMwi;
    protected boolean mMwiSense;
    protected boolean mMwiDontStore;
    protected int mStatusOnIcc = -1;
    protected int mIndexOnIcc = -1;
    public int mMessageRef;

    public abstract SmsConstants.MessageClass getMessageClass();

    public abstract int getProtocolIdentifier();

    public abstract boolean isReplace();

    public abstract boolean isCphsMwiMessage();

    public abstract boolean isMWIClearMessage();

    public abstract boolean isMWISetMessage();

    public abstract boolean isMwiDontStore();

    public abstract int getStatus();

    public abstract boolean isStatusReportMessage();

    public abstract boolean isReplyPathPresent();

    /* loaded from: SmsMessageBase$SubmitPduBase.class */
    public static abstract class SubmitPduBase {
        public byte[] encodedScAddress;
        public byte[] encodedMessage;

        public String toString() {
            return "SubmitPdu: encodedScAddress = " + Arrays.toString(this.encodedScAddress) + ", encodedMessage = " + Arrays.toString(this.encodedMessage);
        }
    }

    public String getServiceCenterAddress() {
        return this.mScAddress;
    }

    public String getOriginatingAddress() {
        if (this.mOriginatingAddress == null) {
            return null;
        }
        return this.mOriginatingAddress.getAddressString();
    }

    public String getDisplayOriginatingAddress() {
        if (this.mIsEmail) {
            return this.mEmailFrom;
        }
        return getOriginatingAddress();
    }

    public String getMessageBody() {
        return this.mMessageBody;
    }

    public String getDisplayMessageBody() {
        if (this.mIsEmail) {
            return this.mEmailBody;
        }
        return getMessageBody();
    }

    public String getPseudoSubject() {
        return this.mPseudoSubject == null ? "" : this.mPseudoSubject;
    }

    public long getTimestampMillis() {
        return this.mScTimeMillis;
    }

    public boolean isEmail() {
        return this.mIsEmail;
    }

    public String getEmailBody() {
        return this.mEmailBody;
    }

    public String getEmailFrom() {
        return this.mEmailFrom;
    }

    public byte[] getUserData() {
        return this.mUserData;
    }

    public SmsHeader getUserDataHeader() {
        return this.mUserDataHeader;
    }

    public byte[] getPdu() {
        return this.mPdu;
    }

    public int getStatusOnIcc() {
        return this.mStatusOnIcc;
    }

    public int getIndexOnIcc() {
        return this.mIndexOnIcc;
    }

    protected void parseMessageBody() {
        if (this.mOriginatingAddress != null && this.mOriginatingAddress.couldBeEmailGateway()) {
            extractEmailAddressFromMessageBody();
        }
    }

    protected void extractEmailAddressFromMessageBody() {
        String[] parts = this.mMessageBody.split("( /)|( )", 2);
        if (parts.length < 2) {
            return;
        }
        this.mEmailFrom = parts[0];
        this.mEmailBody = parts[1];
        this.mIsEmail = Telephony.Mms.isEmailAddress(this.mEmailFrom);
    }
}