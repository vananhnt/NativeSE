package android.telephony;

import android.os.Parcel;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.gsm.SmsMessage;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: SmsMessage.class */
public class SmsMessage {
    private static final String LOG_TAG = "SmsMessage";
    public static final int ENCODING_UNKNOWN = 0;
    public static final int ENCODING_7BIT = 1;
    public static final int ENCODING_8BIT = 2;
    public static final int ENCODING_16BIT = 3;
    public static final int ENCODING_KSC5601 = 4;
    public static final int MAX_USER_DATA_BYTES = 140;
    public static final int MAX_USER_DATA_BYTES_WITH_HEADER = 134;
    public static final int MAX_USER_DATA_SEPTETS = 160;
    public static final int MAX_USER_DATA_SEPTETS_WITH_HEADER = 153;
    public static final String FORMAT_3GPP = "3gpp";
    public static final String FORMAT_3GPP2 = "3gpp2";
    public SmsMessageBase mWrappedSmsMessage;

    /* loaded from: SmsMessage$MessageClass.class */
    public enum MessageClass {
        UNKNOWN,
        CLASS_0,
        CLASS_1,
        CLASS_2,
        CLASS_3
    }

    /* loaded from: SmsMessage$SubmitPdu.class */
    public static class SubmitPdu {
        public byte[] encodedScAddress;
        public byte[] encodedMessage;

        public String toString() {
            return "SubmitPdu: encodedScAddress = " + Arrays.toString(this.encodedScAddress) + ", encodedMessage = " + Arrays.toString(this.encodedMessage);
        }

        protected SubmitPdu(SmsMessageBase.SubmitPduBase spb) {
            this.encodedMessage = spb.encodedMessage;
            this.encodedScAddress = spb.encodedScAddress;
        }
    }

    private SmsMessage(SmsMessageBase smb) {
        this.mWrappedSmsMessage = smb;
    }

    public static SmsMessage createFromPdu(byte[] pdu) {
        int activePhone = TelephonyManager.getDefault().getCurrentPhoneType();
        String format = 2 == activePhone ? "3gpp2" : "3gpp";
        SmsMessage message = createFromPdu(pdu, format);
        if (null == message || null == message.mWrappedSmsMessage) {
            String format2 = 2 == activePhone ? "3gpp" : "3gpp2";
            message = createFromPdu(pdu, format2);
        }
        return message;
    }

    public static SmsMessage createFromPdu(byte[] pdu, String format) {
        com.android.internal.telephony.gsm.SmsMessage createFromPdu;
        if ("3gpp2".equals(format)) {
            createFromPdu = com.android.internal.telephony.cdma.SmsMessage.createFromPdu(pdu);
        } else if ("3gpp".equals(format)) {
            createFromPdu = com.android.internal.telephony.gsm.SmsMessage.createFromPdu(pdu);
        } else {
            Rlog.e(LOG_TAG, "createFromPdu(): unsupported message format " + format);
            return null;
        }
        return new SmsMessage(createFromPdu);
    }

    public static SmsMessage newFromCMT(String[] lines) {
        return new SmsMessage(com.android.internal.telephony.gsm.SmsMessage.newFromCMT(lines));
    }

    public static SmsMessage newFromParcel(Parcel p) {
        return new SmsMessage(com.android.internal.telephony.cdma.SmsMessage.newFromParcel(p));
    }

    public static SmsMessage createFromEfRecord(int index, byte[] data) {
        com.android.internal.telephony.gsm.SmsMessage createFromEfRecord;
        if (isCdmaVoice()) {
            createFromEfRecord = com.android.internal.telephony.cdma.SmsMessage.createFromEfRecord(index, data);
        } else {
            createFromEfRecord = com.android.internal.telephony.gsm.SmsMessage.createFromEfRecord(index, data);
        }
        if (createFromEfRecord != null) {
            return new SmsMessage(createFromEfRecord);
        }
        return null;
    }

    public static int getTPLayerLengthForPDU(String pdu) {
        if (isCdmaVoice()) {
            return com.android.internal.telephony.cdma.SmsMessage.getTPLayerLengthForPDU(pdu);
        }
        return com.android.internal.telephony.gsm.SmsMessage.getTPLayerLengthForPDU(pdu);
    }

    public static int[] calculateLength(CharSequence msgBody, boolean use7bitOnly) {
        GsmAlphabet.TextEncodingDetails ted = useCdmaFormatForMoSms() ? com.android.internal.telephony.cdma.SmsMessage.calculateLength(msgBody, use7bitOnly) : com.android.internal.telephony.gsm.SmsMessage.calculateLength(msgBody, use7bitOnly);
        int[] ret = {ted.msgCount, ted.codeUnitCount, ted.codeUnitsRemaining, ted.codeUnitSize};
        return ret;
    }

    public static ArrayList<String> fragmentText(String text) {
        int limit;
        int nextPos;
        int udhLength;
        GsmAlphabet.TextEncodingDetails ted = useCdmaFormatForMoSms() ? com.android.internal.telephony.cdma.SmsMessage.calculateLength(text, false) : com.android.internal.telephony.gsm.SmsMessage.calculateLength(text, false);
        if (ted.codeUnitSize == 1) {
            if (ted.languageTable != 0 && ted.languageShiftTable != 0) {
                udhLength = 7;
            } else if (ted.languageTable != 0 || ted.languageShiftTable != 0) {
                udhLength = 4;
            } else {
                udhLength = 0;
            }
            if (ted.msgCount > 1) {
                udhLength += 6;
            }
            if (udhLength != 0) {
                udhLength++;
            }
            limit = 160 - udhLength;
        } else if (ted.msgCount > 1) {
            limit = 134;
        } else {
            limit = 140;
        }
        int textLen = text.length();
        ArrayList<String> result = new ArrayList<>(ted.msgCount);
        for (int pos = 0; pos < textLen; pos = nextPos) {
            if (ted.codeUnitSize == 1) {
                if (useCdmaFormatForMoSms() && ted.msgCount == 1) {
                    nextPos = pos + Math.min(limit, textLen - pos);
                } else {
                    nextPos = GsmAlphabet.findGsmSeptetLimitIndex(text, pos, limit, ted.languageTable, ted.languageShiftTable);
                }
            } else {
                nextPos = pos + Math.min(limit / 2, textLen - pos);
            }
            if (nextPos <= pos || nextPos > textLen) {
                Rlog.e(LOG_TAG, "fragmentText failed (" + pos + " >= " + nextPos + " or " + nextPos + " >= " + textLen + Separators.RPAREN);
                break;
            }
            result.add(text.substring(pos, nextPos));
        }
        return result;
    }

    public static int[] calculateLength(String messageBody, boolean use7bitOnly) {
        return calculateLength((CharSequence) messageBody, use7bitOnly);
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, String message, boolean statusReportRequested) {
        SmsMessage.SubmitPdu submitPdu;
        if (useCdmaFormatForMoSms()) {
            submitPdu = com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(scAddress, destinationAddress, message, statusReportRequested, (SmsHeader) null);
        } else {
            submitPdu = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(scAddress, destinationAddress, message, statusReportRequested);
        }
        return new SubmitPdu(submitPdu);
    }

    public static SubmitPdu getSubmitPdu(String scAddress, String destinationAddress, short destinationPort, byte[] data, boolean statusReportRequested) {
        SmsMessage.SubmitPdu submitPdu;
        if (useCdmaFormatForMoSms()) {
            submitPdu = com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(scAddress, destinationAddress, destinationPort, data, statusReportRequested);
        } else {
            submitPdu = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(scAddress, destinationAddress, destinationPort, data, statusReportRequested);
        }
        return new SubmitPdu(submitPdu);
    }

    public String getServiceCenterAddress() {
        return this.mWrappedSmsMessage.getServiceCenterAddress();
    }

    public String getOriginatingAddress() {
        return this.mWrappedSmsMessage.getOriginatingAddress();
    }

    public String getDisplayOriginatingAddress() {
        return this.mWrappedSmsMessage.getDisplayOriginatingAddress();
    }

    public String getMessageBody() {
        return this.mWrappedSmsMessage.getMessageBody();
    }

    public MessageClass getMessageClass() {
        switch (this.mWrappedSmsMessage.getMessageClass()) {
            case CLASS_0:
                return MessageClass.CLASS_0;
            case CLASS_1:
                return MessageClass.CLASS_1;
            case CLASS_2:
                return MessageClass.CLASS_2;
            case CLASS_3:
                return MessageClass.CLASS_3;
            default:
                return MessageClass.UNKNOWN;
        }
    }

    public String getDisplayMessageBody() {
        return this.mWrappedSmsMessage.getDisplayMessageBody();
    }

    public String getPseudoSubject() {
        return this.mWrappedSmsMessage.getPseudoSubject();
    }

    public long getTimestampMillis() {
        return this.mWrappedSmsMessage.getTimestampMillis();
    }

    public boolean isEmail() {
        return this.mWrappedSmsMessage.isEmail();
    }

    public String getEmailBody() {
        return this.mWrappedSmsMessage.getEmailBody();
    }

    public String getEmailFrom() {
        return this.mWrappedSmsMessage.getEmailFrom();
    }

    public int getProtocolIdentifier() {
        return this.mWrappedSmsMessage.getProtocolIdentifier();
    }

    public boolean isReplace() {
        return this.mWrappedSmsMessage.isReplace();
    }

    public boolean isCphsMwiMessage() {
        return this.mWrappedSmsMessage.isCphsMwiMessage();
    }

    public boolean isMWIClearMessage() {
        return this.mWrappedSmsMessage.isMWIClearMessage();
    }

    public boolean isMWISetMessage() {
        return this.mWrappedSmsMessage.isMWISetMessage();
    }

    public boolean isMwiDontStore() {
        return this.mWrappedSmsMessage.isMwiDontStore();
    }

    public byte[] getUserData() {
        return this.mWrappedSmsMessage.getUserData();
    }

    public byte[] getPdu() {
        return this.mWrappedSmsMessage.getPdu();
    }

    @Deprecated
    public int getStatusOnSim() {
        return this.mWrappedSmsMessage.getStatusOnIcc();
    }

    public int getStatusOnIcc() {
        return this.mWrappedSmsMessage.getStatusOnIcc();
    }

    @Deprecated
    public int getIndexOnSim() {
        return this.mWrappedSmsMessage.getIndexOnIcc();
    }

    public int getIndexOnIcc() {
        return this.mWrappedSmsMessage.getIndexOnIcc();
    }

    public int getStatus() {
        return this.mWrappedSmsMessage.getStatus();
    }

    public boolean isStatusReportMessage() {
        return this.mWrappedSmsMessage.isStatusReportMessage();
    }

    public boolean isReplyPathPresent() {
        return this.mWrappedSmsMessage.isReplyPathPresent();
    }

    private static boolean useCdmaFormatForMoSms() {
        if (!SmsManager.getDefault().isImsSmsSupported()) {
            return isCdmaVoice();
        }
        return "3gpp2".equals(SmsManager.getDefault().getImsSmsFormat());
    }

    private static boolean isCdmaVoice() {
        int activePhone = TelephonyManager.getDefault().getCurrentPhoneType();
        return 2 == activePhone;
    }
}