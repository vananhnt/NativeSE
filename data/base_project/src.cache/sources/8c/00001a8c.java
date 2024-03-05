package com.android.internal.telephony;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncResult;
import android.os.Message;
import android.telephony.Rlog;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.SMSDispatcher;
import com.android.internal.telephony.cdma.CdmaInboundSmsHandler;
import com.android.internal.telephony.cdma.CdmaSMSDispatcher;
import com.android.internal.telephony.cdma.SmsMessage;
import com.android.internal.telephony.gsm.GsmInboundSmsHandler;
import com.android.internal.telephony.gsm.GsmSMSDispatcher;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: ImsSMSDispatcher.class */
public final class ImsSMSDispatcher extends SMSDispatcher {
    private static final String TAG = "RIL_ImsSms";
    private SMSDispatcher mCdmaDispatcher;
    private SMSDispatcher mGsmDispatcher;
    GsmInboundSmsHandler mGsmInboundSmsHandler;
    CdmaInboundSmsHandler mCdmaInboundSmsHandler;
    private boolean mIms;
    private String mImsSmsFormat;

    public ImsSMSDispatcher(PhoneBase phone, SmsStorageMonitor storageMonitor, SmsUsageMonitor usageMonitor) {
        super(phone, usageMonitor);
        this.mIms = false;
        this.mImsSmsFormat = "unknown";
        Rlog.d(TAG, "ImsSMSDispatcher created");
        this.mCdmaDispatcher = new CdmaSMSDispatcher(phone, storageMonitor, usageMonitor, this);
        this.mGsmInboundSmsHandler = GsmInboundSmsHandler.makeInboundSmsHandler(phone.getContext(), storageMonitor, phone);
        this.mCdmaInboundSmsHandler = CdmaInboundSmsHandler.makeInboundSmsHandler(phone.getContext(), storageMonitor, phone, this.mCdmaDispatcher);
        this.mGsmDispatcher = new GsmSMSDispatcher(phone, storageMonitor, usageMonitor, this, this.mGsmInboundSmsHandler);
        Thread broadcastThread = new Thread(new SmsBroadcastUndelivered(phone.getContext(), this.mGsmInboundSmsHandler, this.mCdmaInboundSmsHandler));
        broadcastThread.start();
        this.mCi.registerForOn(this, 11, null);
        this.mCi.registerForImsNetworkStateChanged(this, 12, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    public void updatePhoneObject(PhoneBase phone) {
        Rlog.d(TAG, "In IMS updatePhoneObject ");
        super.updatePhoneObject(phone);
        this.mCdmaDispatcher.updatePhoneObject(phone);
        this.mGsmDispatcher.updatePhoneObject(phone);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    public void dispose() {
        this.mCi.unregisterForOn(this);
        this.mCi.unregisterForImsNetworkStateChanged(this);
        this.mGsmDispatcher.dispose();
        this.mCdmaDispatcher.dispose();
        this.mGsmInboundSmsHandler.dispose();
        this.mCdmaInboundSmsHandler.dispose();
    }

    @Override // com.android.internal.telephony.SMSDispatcher, android.os.Handler
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 11:
            case 12:
                this.mCi.getImsRegistrationState(obtainMessage(13));
                return;
            case 13:
                AsyncResult ar = (AsyncResult) msg.obj;
                if (ar.exception == null) {
                    updateImsInfo(ar);
                    return;
                } else {
                    Rlog.e(TAG, "IMS State query failed with exp " + ar.exception);
                    return;
                }
            default:
                super.handleMessage(msg);
                return;
        }
    }

    private void setImsSmsFormat(int format) {
        switch (format) {
            case 1:
                this.mImsSmsFormat = "3gpp";
                return;
            case 2:
                this.mImsSmsFormat = "3gpp2";
                return;
            default:
                this.mImsSmsFormat = "unknown";
                return;
        }
    }

    private void updateImsInfo(AsyncResult ar) {
        int[] responseArray = (int[]) ar.result;
        this.mIms = false;
        if (responseArray[0] == 1) {
            Rlog.d(TAG, "IMS is registered!");
            this.mIms = true;
        } else {
            Rlog.d(TAG, "IMS is NOT registered!");
        }
        setImsSmsFormat(responseArray[1]);
        if ("unknown".equals(this.mImsSmsFormat)) {
            Rlog.e(TAG, "IMS format was unknown!");
            this.mIms = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    public void sendData(String destAddr, String scAddr, int destPort, byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        if (isCdmaMo()) {
            this.mCdmaDispatcher.sendData(destAddr, scAddr, destPort, data, sentIntent, deliveryIntent);
        } else {
            this.mGsmDispatcher.sendData(destAddr, scAddr, destPort, data, sentIntent, deliveryIntent);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    public void sendMultipartText(String destAddr, String scAddr, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents) {
        if (isCdmaMo()) {
            this.mCdmaDispatcher.sendMultipartText(destAddr, scAddr, parts, sentIntents, deliveryIntents);
        } else {
            this.mGsmDispatcher.sendMultipartText(destAddr, scAddr, parts, sentIntents, deliveryIntents);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    public void sendSms(SMSDispatcher.SmsTracker tracker) {
        Rlog.e(TAG, "sendSms should never be called from here!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    public void sendText(String destAddr, String scAddr, String text, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        Rlog.d(TAG, "sendText");
        if (isCdmaMo()) {
            this.mCdmaDispatcher.sendText(destAddr, scAddr, text, sentIntent, deliveryIntent);
        } else {
            this.mGsmDispatcher.sendText(destAddr, scAddr, text, sentIntent, deliveryIntent);
        }
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    public void sendRetrySms(SMSDispatcher.SmsTracker tracker) {
        String oldFormat = tracker.mFormat;
        String newFormat = 2 == this.mPhone.getPhoneType() ? this.mCdmaDispatcher.getFormat() : this.mGsmDispatcher.getFormat();
        if (oldFormat.equals(newFormat)) {
            if (isCdmaFormat(newFormat)) {
                Rlog.d(TAG, "old format matched new format (cdma)");
                this.mCdmaDispatcher.sendSms(tracker);
                return;
            }
            Rlog.d(TAG, "old format matched new format (gsm)");
            this.mGsmDispatcher.sendSms(tracker);
            return;
        }
        HashMap map = tracker.mData;
        if (!map.containsKey("scAddr") || !map.containsKey("destAddr") || (!map.containsKey("text") && (!map.containsKey("data") || !map.containsKey("destPort")))) {
            Rlog.e(TAG, "sendRetrySms failed to re-encode per missing fields!");
            if (tracker.mSentIntent != null) {
                try {
                    tracker.mSentIntent.send(this.mContext, 1, (Intent) null);
                    return;
                } catch (PendingIntent.CanceledException e) {
                    return;
                }
            }
            return;
        }
        String scAddr = (String) map.get("scAddr");
        String destAddr = (String) map.get("destAddr");
        SmsMessage.SubmitPdu submitPdu = null;
        if (map.containsKey("text")) {
            Rlog.d(TAG, "sms failed was text");
            String text = (String) map.get("text");
            if (isCdmaFormat(newFormat)) {
                Rlog.d(TAG, "old format (gsm) ==> new format (cdma)");
                submitPdu = SmsMessage.getSubmitPdu(scAddr, destAddr, text, tracker.mDeliveryIntent != null, (SmsHeader) null);
            } else {
                Rlog.d(TAG, "old format (cdma) ==> new format (gsm)");
                submitPdu = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(scAddr, destAddr, text, tracker.mDeliveryIntent != null, (byte[]) null);
            }
        } else if (map.containsKey("data")) {
            Rlog.d(TAG, "sms failed was data");
            byte[] data = (byte[]) map.get("data");
            Integer destPort = (Integer) map.get("destPort");
            if (isCdmaFormat(newFormat)) {
                Rlog.d(TAG, "old format (gsm) ==> new format (cdma)");
                submitPdu = SmsMessage.getSubmitPdu(scAddr, destAddr, destPort.intValue(), data, tracker.mDeliveryIntent != null);
            } else {
                Rlog.d(TAG, "old format (cdma) ==> new format (gsm)");
                submitPdu = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(scAddr, destAddr, destPort.intValue(), data, tracker.mDeliveryIntent != null);
            }
        }
        map.put("smsc", submitPdu.encodedScAddress);
        map.put("pdu", submitPdu.encodedMessage);
        SMSDispatcher dispatcher = isCdmaFormat(newFormat) ? this.mCdmaDispatcher : this.mGsmDispatcher;
        tracker.mFormat = dispatcher.getFormat();
        dispatcher.sendSms(tracker);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    public String getFormat() {
        Rlog.e(TAG, "getFormat should never be called from here!");
        return "unknown";
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected GsmAlphabet.TextEncodingDetails calculateLength(CharSequence messageBody, boolean use7bitOnly) {
        Rlog.e(TAG, "Error! Not implemented for IMS.");
        return null;
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected void sendNewSubmitPdu(String destinationAddress, String scAddress, String message, SmsHeader smsHeader, int format, PendingIntent sentIntent, PendingIntent deliveryIntent, boolean lastPart) {
        Rlog.e(TAG, "Error! Not implemented for IMS.");
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    public boolean isIms() {
        return this.mIms;
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    public String getImsSmsFormat() {
        return this.mImsSmsFormat;
    }

    private boolean isCdmaMo() {
        if (isIms()) {
            return isCdmaFormat(this.mImsSmsFormat);
        }
        return 2 == this.mPhone.getPhoneType();
    }

    private boolean isCdmaFormat(String format) {
        return this.mCdmaDispatcher.getFormat().equals(format);
    }
}