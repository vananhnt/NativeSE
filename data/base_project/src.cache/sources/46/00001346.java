package android.telephony;

import android.app.ActivityThread;
import android.app.PendingIntent;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.text.TextUtils;
import com.android.internal.telephony.ISms;
import com.android.internal.telephony.SmsRawData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: SmsManager.class */
public final class SmsManager {
    private static final SmsManager sInstance = new SmsManager();
    public static final int STATUS_ON_ICC_FREE = 0;
    public static final int STATUS_ON_ICC_READ = 1;
    public static final int STATUS_ON_ICC_UNREAD = 3;
    public static final int STATUS_ON_ICC_SENT = 5;
    public static final int STATUS_ON_ICC_UNSENT = 7;
    public static final int RESULT_ERROR_GENERIC_FAILURE = 1;
    public static final int RESULT_ERROR_RADIO_OFF = 2;
    public static final int RESULT_ERROR_NULL_PDU = 3;
    public static final int RESULT_ERROR_NO_SERVICE = 4;
    public static final int RESULT_ERROR_LIMIT_EXCEEDED = 5;
    public static final int RESULT_ERROR_FDN_CHECK_FAILURE = 6;

    public void sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        if (TextUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("Invalid destinationAddress");
        }
        if (TextUtils.isEmpty(text)) {
            throw new IllegalArgumentException("Invalid message body");
        }
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                iccISms.sendText(ActivityThread.currentPackageName(), destinationAddress, scAddress, text, sentIntent, deliveryIntent);
            }
        } catch (RemoteException e) {
        }
    }

    public ArrayList<String> divideMessage(String text) {
        if (null == text) {
            throw new IllegalArgumentException("text is null");
        }
        return SmsMessage.fragmentText(text);
    }

    public void sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents) {
        if (TextUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("Invalid destinationAddress");
        }
        if (parts == null || parts.size() < 1) {
            throw new IllegalArgumentException("Invalid message body");
        }
        if (parts.size() > 1) {
            try {
                ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
                if (iccISms != null) {
                    iccISms.sendMultipartText(ActivityThread.currentPackageName(), destinationAddress, scAddress, parts, sentIntents, deliveryIntents);
                }
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        PendingIntent sentIntent = null;
        PendingIntent deliveryIntent = null;
        if (sentIntents != null && sentIntents.size() > 0) {
            sentIntent = sentIntents.get(0);
        }
        if (deliveryIntents != null && deliveryIntents.size() > 0) {
            deliveryIntent = deliveryIntents.get(0);
        }
        sendTextMessage(destinationAddress, scAddress, parts.get(0), sentIntent, deliveryIntent);
    }

    public void sendDataMessage(String destinationAddress, String scAddress, short destinationPort, byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        if (TextUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("Invalid destinationAddress");
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Invalid message data");
        }
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                iccISms.sendData(ActivityThread.currentPackageName(), destinationAddress, scAddress, destinationPort & 65535, data, sentIntent, deliveryIntent);
            }
        } catch (RemoteException e) {
        }
    }

    public static SmsManager getDefault() {
        return sInstance;
    }

    private SmsManager() {
    }

    public boolean copyMessageToIcc(byte[] smsc, byte[] pdu, int status) {
        boolean success = false;
        if (null == pdu) {
            throw new IllegalArgumentException("pdu is NULL");
        }
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                success = iccISms.copyMessageToIccEf(ActivityThread.currentPackageName(), status, pdu, smsc);
            }
        } catch (RemoteException e) {
        }
        return success;
    }

    public boolean deleteMessageFromIcc(int messageIndex) {
        boolean success = false;
        byte[] pdu = new byte[175];
        Arrays.fill(pdu, (byte) -1);
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                success = iccISms.updateMessageOnIccEf(ActivityThread.currentPackageName(), messageIndex, 0, pdu);
            }
        } catch (RemoteException e) {
        }
        return success;
    }

    public boolean updateMessageOnIcc(int messageIndex, int newStatus, byte[] pdu) {
        boolean success = false;
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                success = iccISms.updateMessageOnIccEf(ActivityThread.currentPackageName(), messageIndex, newStatus, pdu);
            }
        } catch (RemoteException e) {
        }
        return success;
    }

    public static ArrayList<SmsMessage> getAllMessagesFromIcc() {
        List<SmsRawData> records = null;
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                records = iccISms.getAllMessagesFromIccEf(ActivityThread.currentPackageName());
            }
        } catch (RemoteException e) {
        }
        return createMessageListFromRawRecords(records);
    }

    public boolean enableCellBroadcast(int messageIdentifier) {
        boolean success = false;
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                success = iccISms.enableCellBroadcast(messageIdentifier);
            }
        } catch (RemoteException e) {
        }
        return success;
    }

    public boolean disableCellBroadcast(int messageIdentifier) {
        boolean success = false;
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                success = iccISms.disableCellBroadcast(messageIdentifier);
            }
        } catch (RemoteException e) {
        }
        return success;
    }

    public boolean enableCellBroadcastRange(int startMessageId, int endMessageId) {
        boolean success = false;
        if (endMessageId < startMessageId) {
            throw new IllegalArgumentException("endMessageId < startMessageId");
        }
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                success = iccISms.enableCellBroadcastRange(startMessageId, endMessageId);
            }
        } catch (RemoteException e) {
        }
        return success;
    }

    public boolean disableCellBroadcastRange(int startMessageId, int endMessageId) {
        boolean success = false;
        if (endMessageId < startMessageId) {
            throw new IllegalArgumentException("endMessageId < startMessageId");
        }
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                success = iccISms.disableCellBroadcastRange(startMessageId, endMessageId);
            }
        } catch (RemoteException e) {
        }
        return success;
    }

    private static ArrayList<SmsMessage> createMessageListFromRawRecords(List<SmsRawData> records) {
        SmsMessage sms;
        ArrayList<SmsMessage> messages = new ArrayList<>();
        if (records != null) {
            int count = records.size();
            for (int i = 0; i < count; i++) {
                SmsRawData data = records.get(i);
                if (data != null && (sms = SmsMessage.createFromEfRecord(i + 1, data.getBytes())) != null) {
                    messages.add(sms);
                }
            }
        }
        return messages;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isImsSmsSupported() {
        boolean boSupported = false;
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                boSupported = iccISms.isImsSmsSupported();
            }
        } catch (RemoteException e) {
        }
        return boSupported;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getImsSmsFormat() {
        String format = "unknown";
        try {
            ISms iccISms = ISms.Stub.asInterface(ServiceManager.getService("isms"));
            if (iccISms != null) {
                format = iccISms.getImsSmsFormat();
            }
        } catch (RemoteException e) {
        }
        return format;
    }
}