package com.android.internal.telephony;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.ServiceManager;
import android.telephony.Rlog;
import android.util.Log;
import com.android.internal.telephony.ISms;
import com.android.internal.telephony.cdma.CdmaSmsBroadcastConfigInfo;
import com.android.internal.telephony.gsm.SmsBroadcastConfigInfo;
import com.android.internal.telephony.uicc.IccFileHandler;
import com.android.internal.util.HexDump;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: IccSmsInterfaceManager.class */
public class IccSmsInterfaceManager extends ISms.Stub {
    static final String LOG_TAG = "IccSmsInterfaceManager";
    static final boolean DBG = true;
    protected boolean mSuccess;
    private List<SmsRawData> mSms;
    private static final int EVENT_LOAD_DONE = 1;
    private static final int EVENT_UPDATE_DONE = 2;
    protected static final int EVENT_SET_BROADCAST_ACTIVATION_DONE = 3;
    protected static final int EVENT_SET_BROADCAST_CONFIG_DONE = 4;
    private static final int SMS_CB_CODE_SCHEME_MIN = 0;
    private static final int SMS_CB_CODE_SCHEME_MAX = 255;
    protected PhoneBase mPhone;
    protected final Context mContext;
    protected final AppOpsManager mAppOps;
    protected SMSDispatcher mDispatcher;
    protected final Object mLock = new Object();
    private CellBroadcastRangeManager mCellBroadcastRangeManager = new CellBroadcastRangeManager();
    private CdmaBroadcastRangeManager mCdmaBroadcastRangeManager = new CdmaBroadcastRangeManager();
    protected Handler mHandler = new Handler() { // from class: com.android.internal.telephony.IccSmsInterfaceManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    AsyncResult ar = (AsyncResult) msg.obj;
                    synchronized (IccSmsInterfaceManager.this.mLock) {
                        if (ar.exception == null) {
                            IccSmsInterfaceManager.this.mSms = IccSmsInterfaceManager.this.buildValidRawData((ArrayList) ar.result);
                            IccSmsInterfaceManager.this.markMessagesAsRead((ArrayList) ar.result);
                        } else {
                            if (Rlog.isLoggable("SMS", 3)) {
                                IccSmsInterfaceManager.this.log("Cannot load Sms records");
                            }
                            if (IccSmsInterfaceManager.this.mSms != null) {
                                IccSmsInterfaceManager.this.mSms.clear();
                            }
                        }
                        IccSmsInterfaceManager.this.mLock.notifyAll();
                    }
                    return;
                case 2:
                    AsyncResult ar2 = (AsyncResult) msg.obj;
                    synchronized (IccSmsInterfaceManager.this.mLock) {
                        IccSmsInterfaceManager.this.mSuccess = ar2.exception == null;
                        IccSmsInterfaceManager.this.mLock.notifyAll();
                    }
                    return;
                case 3:
                case 4:
                    AsyncResult ar3 = (AsyncResult) msg.obj;
                    synchronized (IccSmsInterfaceManager.this.mLock) {
                        IccSmsInterfaceManager.this.mSuccess = ar3.exception == null;
                        IccSmsInterfaceManager.this.mLock.notifyAll();
                    }
                    return;
                default:
                    return;
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    public IccSmsInterfaceManager(PhoneBase phone) {
        this.mPhone = phone;
        this.mContext = phone.getContext();
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService(Context.APP_OPS_SERVICE);
        this.mDispatcher = new ImsSMSDispatcher(phone, phone.mSmsStorageMonitor, phone.mSmsUsageMonitor);
        if (ServiceManager.getService("isms") == null) {
            ServiceManager.addService("isms", this);
        }
    }

    protected void markMessagesAsRead(ArrayList<byte[]> messages) {
        if (messages == null) {
            return;
        }
        IccFileHandler fh = this.mPhone.getIccFileHandler();
        if (fh == null) {
            if (Rlog.isLoggable("SMS", 3)) {
                log("markMessagesAsRead - aborting, no icc card present.");
                return;
            }
            return;
        }
        int count = messages.size();
        for (int i = 0; i < count; i++) {
            byte[] ba = messages.get(i);
            if (ba[0] == 3) {
                int n = ba.length;
                byte[] nba = new byte[n - 1];
                System.arraycopy(ba, 1, nba, 0, n - 1);
                byte[] record = makeSmsRecordData(1, nba);
                fh.updateEFLinearFixed(28476, i + 1, record, (String) null, (Message) null);
                if (Rlog.isLoggable("SMS", 3)) {
                    log("SMS " + (i + 1) + " marked as read");
                }
            }
        }
    }

    public void dispose() {
        this.mDispatcher.dispose();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePhoneObject(PhoneBase phone) {
        this.mPhone = phone;
        this.mDispatcher.updatePhoneObject(phone);
    }

    protected void enforceReceiveAndSend(String message) {
        this.mContext.enforceCallingPermission(Manifest.permission.RECEIVE_SMS, message);
        this.mContext.enforceCallingPermission(Manifest.permission.SEND_SMS, message);
    }

    @Override // com.android.internal.telephony.ISms
    public boolean updateMessageOnIccEf(String callingPackage, int index, int status, byte[] pdu) {
        log("updateMessageOnIccEf: index=" + index + " status=" + status + " ==> " + Separators.LPAREN + Arrays.toString(pdu) + Separators.RPAREN);
        enforceReceiveAndSend("Updating message on Icc");
        if (this.mAppOps.noteOp(22, Binder.getCallingUid(), callingPackage) != 0) {
            return false;
        }
        synchronized (this.mLock) {
            this.mSuccess = false;
            Message response = this.mHandler.obtainMessage(2);
            if (status == 0) {
                if (1 == this.mPhone.getPhoneType()) {
                    this.mPhone.mCi.deleteSmsOnSim(index, response);
                } else {
                    this.mPhone.mCi.deleteSmsOnRuim(index, response);
                }
            } else {
                IccFileHandler fh = this.mPhone.getIccFileHandler();
                if (fh == null) {
                    response.recycle();
                    return this.mSuccess;
                }
                byte[] record = makeSmsRecordData(status, pdu);
                fh.updateEFLinearFixed(28476, index, record, (String) null, response);
            }
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
                log("interrupted while trying to update by index");
            }
            return this.mSuccess;
        }
    }

    @Override // com.android.internal.telephony.ISms
    public boolean copyMessageToIccEf(String callingPackage, int status, byte[] pdu, byte[] smsc) {
        log("copyMessageToIccEf: status=" + status + " ==> pdu=(" + Arrays.toString(pdu) + "), smsc=(" + Arrays.toString(smsc) + Separators.RPAREN);
        enforceReceiveAndSend("Copying message to Icc");
        if (this.mAppOps.noteOp(22, Binder.getCallingUid(), callingPackage) != 0) {
            return false;
        }
        synchronized (this.mLock) {
            this.mSuccess = false;
            Message response = this.mHandler.obtainMessage(2);
            if (1 == this.mPhone.getPhoneType()) {
                this.mPhone.mCi.writeSmsToSim(status, IccUtils.bytesToHexString(smsc), IccUtils.bytesToHexString(pdu), response);
            } else {
                this.mPhone.mCi.writeSmsToRuim(status, IccUtils.bytesToHexString(pdu), response);
            }
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
                log("interrupted while trying to update by index");
            }
        }
        return this.mSuccess;
    }

    @Override // com.android.internal.telephony.ISms
    public List<SmsRawData> getAllMessagesFromIccEf(String callingPackage) {
        log("getAllMessagesFromEF");
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.RECEIVE_SMS, "Reading messages from Icc");
        if (this.mAppOps.noteOp(21, Binder.getCallingUid(), callingPackage) != 0) {
            return new ArrayList();
        }
        synchronized (this.mLock) {
            IccFileHandler fh = this.mPhone.getIccFileHandler();
            if (fh == null) {
                Rlog.e(LOG_TAG, "Cannot load Sms records. No icc card?");
                if (this.mSms != null) {
                    this.mSms.clear();
                    return this.mSms;
                }
            }
            Message response = this.mHandler.obtainMessage(1);
            fh.loadEFLinearFixedAll(28476, response);
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
                log("interrupted while trying to load from the Icc");
            }
            return this.mSms;
        }
    }

    @Override // com.android.internal.telephony.ISms
    public void sendData(String callingPackage, String destAddr, String scAddr, int destPort, byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        this.mPhone.getContext().enforceCallingPermission(Manifest.permission.SEND_SMS, "Sending SMS message");
        if (Rlog.isLoggable("SMS", 2)) {
            log("sendData: destAddr=" + destAddr + " scAddr=" + scAddr + " destPort=" + destPort + " data='" + HexDump.toHexString(data) + "' sentIntent=" + sentIntent + " deliveryIntent=" + deliveryIntent);
        }
        if (this.mAppOps.noteOp(20, Binder.getCallingUid(), callingPackage) != 0) {
            return;
        }
        this.mDispatcher.sendData(destAddr, scAddr, destPort, data, sentIntent, deliveryIntent);
    }

    @Override // com.android.internal.telephony.ISms
    public void sendText(String callingPackage, String destAddr, String scAddr, String text, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        this.mPhone.getContext().enforceCallingPermission(Manifest.permission.SEND_SMS, "Sending SMS message");
        if (Rlog.isLoggable("SMS", 2)) {
            log("sendText: destAddr=" + destAddr + " scAddr=" + scAddr + " text='" + text + "' sentIntent=" + sentIntent + " deliveryIntent=" + deliveryIntent);
        }
        if (this.mAppOps.noteOp(20, Binder.getCallingUid(), callingPackage) != 0) {
            return;
        }
        this.mDispatcher.sendText(destAddr, scAddr, text, sentIntent, deliveryIntent);
    }

    @Override // com.android.internal.telephony.ISms
    public void sendMultipartText(String callingPackage, String destAddr, String scAddr, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents) {
        this.mPhone.getContext().enforceCallingPermission(Manifest.permission.SEND_SMS, "Sending SMS message");
        if (Rlog.isLoggable("SMS", 2)) {
            int i = 0;
            for (String part : parts) {
                int i2 = i;
                i++;
                log("sendMultipartText: destAddr=" + destAddr + ", srAddr=" + scAddr + ", part[" + i2 + "]=" + part);
            }
        }
        if (this.mAppOps.noteOp(20, Binder.getCallingUid(), callingPackage) != 0) {
            return;
        }
        this.mDispatcher.sendMultipartText(destAddr, scAddr, (ArrayList) parts, (ArrayList) sentIntents, (ArrayList) deliveryIntents);
    }

    @Override // com.android.internal.telephony.ISms
    public int getPremiumSmsPermission(String packageName) {
        return this.mDispatcher.getPremiumSmsPermission(packageName);
    }

    @Override // com.android.internal.telephony.ISms
    public void setPremiumSmsPermission(String packageName, int permission) {
        this.mDispatcher.setPremiumSmsPermission(packageName, permission);
    }

    protected ArrayList<SmsRawData> buildValidRawData(ArrayList<byte[]> messages) {
        int count = messages.size();
        ArrayList<SmsRawData> ret = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            byte[] ba = messages.get(i);
            if (ba[0] == 0) {
                ret.add(null);
            } else {
                ret.add(new SmsRawData(messages.get(i)));
            }
        }
        return ret;
    }

    protected byte[] makeSmsRecordData(int status, byte[] pdu) {
        byte[] data = new byte[176];
        data[0] = (byte) (status & 7);
        System.arraycopy(pdu, 0, data, 1, pdu.length);
        for (int j = pdu.length + 1; j < 176; j++) {
            data[j] = -1;
        }
        return data;
    }

    @Override // com.android.internal.telephony.ISms
    public boolean enableCellBroadcast(int messageIdentifier) {
        return enableCellBroadcastRange(messageIdentifier, messageIdentifier);
    }

    @Override // com.android.internal.telephony.ISms
    public boolean disableCellBroadcast(int messageIdentifier) {
        return disableCellBroadcastRange(messageIdentifier, messageIdentifier);
    }

    @Override // com.android.internal.telephony.ISms
    public boolean enableCellBroadcastRange(int startMessageId, int endMessageId) {
        if (1 == this.mPhone.getPhoneType()) {
            return enableGsmBroadcastRange(startMessageId, endMessageId);
        }
        return enableCdmaBroadcastRange(startMessageId, endMessageId);
    }

    @Override // com.android.internal.telephony.ISms
    public boolean disableCellBroadcastRange(int startMessageId, int endMessageId) {
        if (1 == this.mPhone.getPhoneType()) {
            return disableGsmBroadcastRange(startMessageId, endMessageId);
        }
        return disableCdmaBroadcastRange(startMessageId, endMessageId);
    }

    public synchronized boolean enableGsmBroadcastRange(int startMessageId, int endMessageId) {
        log("enableGsmBroadcastRange");
        Context context = this.mPhone.getContext();
        context.enforceCallingPermission(Manifest.permission.RECEIVE_SMS, "Enabling cell broadcast SMS");
        String client = context.getPackageManager().getNameForUid(Binder.getCallingUid());
        if (!this.mCellBroadcastRangeManager.enableRange(startMessageId, endMessageId, client)) {
            log("Failed to add cell broadcast subscription for MID range " + startMessageId + " to " + endMessageId + " from client " + client);
            return false;
        }
        log("Added cell broadcast subscription for MID range " + startMessageId + " to " + endMessageId + " from client " + client);
        setCellBroadcastActivation(!this.mCellBroadcastRangeManager.isEmpty());
        return true;
    }

    public synchronized boolean disableGsmBroadcastRange(int startMessageId, int endMessageId) {
        log("disableGsmBroadcastRange");
        Context context = this.mPhone.getContext();
        context.enforceCallingPermission(Manifest.permission.RECEIVE_SMS, "Disabling cell broadcast SMS");
        String client = context.getPackageManager().getNameForUid(Binder.getCallingUid());
        if (!this.mCellBroadcastRangeManager.disableRange(startMessageId, endMessageId, client)) {
            log("Failed to remove cell broadcast subscription for MID range " + startMessageId + " to " + endMessageId + " from client " + client);
            return false;
        }
        log("Removed cell broadcast subscription for MID range " + startMessageId + " to " + endMessageId + " from client " + client);
        setCellBroadcastActivation(!this.mCellBroadcastRangeManager.isEmpty());
        return true;
    }

    public synchronized boolean enableCdmaBroadcastRange(int startMessageId, int endMessageId) {
        log("enableCdmaBroadcastRange");
        Context context = this.mPhone.getContext();
        context.enforceCallingPermission(Manifest.permission.RECEIVE_SMS, "Enabling cdma broadcast SMS");
        String client = context.getPackageManager().getNameForUid(Binder.getCallingUid());
        if (!this.mCdmaBroadcastRangeManager.enableRange(startMessageId, endMessageId, client)) {
            log("Failed to add cdma broadcast subscription for MID range " + startMessageId + " to " + endMessageId + " from client " + client);
            return false;
        }
        log("Added cdma broadcast subscription for MID range " + startMessageId + " to " + endMessageId + " from client " + client);
        setCdmaBroadcastActivation(!this.mCdmaBroadcastRangeManager.isEmpty());
        return true;
    }

    public synchronized boolean disableCdmaBroadcastRange(int startMessageId, int endMessageId) {
        log("disableCdmaBroadcastRange");
        Context context = this.mPhone.getContext();
        context.enforceCallingPermission(Manifest.permission.RECEIVE_SMS, "Disabling cell broadcast SMS");
        String client = context.getPackageManager().getNameForUid(Binder.getCallingUid());
        if (!this.mCdmaBroadcastRangeManager.disableRange(startMessageId, endMessageId, client)) {
            log("Failed to remove cdma broadcast subscription for MID range " + startMessageId + " to " + endMessageId + " from client " + client);
            return false;
        }
        log("Removed cdma broadcast subscription for MID range " + startMessageId + " to " + endMessageId + " from client " + client);
        setCdmaBroadcastActivation(!this.mCdmaBroadcastRangeManager.isEmpty());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: IccSmsInterfaceManager$CellBroadcastRangeManager.class */
    public class CellBroadcastRangeManager extends IntRangeManager {
        private ArrayList<SmsBroadcastConfigInfo> mConfigList = new ArrayList<>();

        CellBroadcastRangeManager() {
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected void startUpdate() {
            this.mConfigList.clear();
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected void addRange(int startId, int endId, boolean selected) {
            this.mConfigList.add(new SmsBroadcastConfigInfo(startId, endId, 0, 255, selected));
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected boolean finishUpdate() {
            if (this.mConfigList.isEmpty()) {
                return true;
            }
            SmsBroadcastConfigInfo[] configs = (SmsBroadcastConfigInfo[]) this.mConfigList.toArray(new SmsBroadcastConfigInfo[this.mConfigList.size()]);
            return IccSmsInterfaceManager.this.setCellBroadcastConfig(configs);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: IccSmsInterfaceManager$CdmaBroadcastRangeManager.class */
    public class CdmaBroadcastRangeManager extends IntRangeManager {
        private ArrayList<CdmaSmsBroadcastConfigInfo> mConfigList = new ArrayList<>();

        CdmaBroadcastRangeManager() {
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected void startUpdate() {
            this.mConfigList.clear();
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected void addRange(int startId, int endId, boolean selected) {
            this.mConfigList.add(new CdmaSmsBroadcastConfigInfo(startId, endId, 1, selected));
        }

        @Override // com.android.internal.telephony.IntRangeManager
        protected boolean finishUpdate() {
            if (this.mConfigList.isEmpty()) {
                return true;
            }
            CdmaSmsBroadcastConfigInfo[] configs = (CdmaSmsBroadcastConfigInfo[]) this.mConfigList.toArray(new CdmaSmsBroadcastConfigInfo[this.mConfigList.size()]);
            return IccSmsInterfaceManager.this.setCdmaBroadcastConfig(configs);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setCellBroadcastConfig(SmsBroadcastConfigInfo[] configs) {
        log("Calling setGsmBroadcastConfig with " + configs.length + " configurations");
        synchronized (this.mLock) {
            Message response = this.mHandler.obtainMessage(4);
            this.mSuccess = false;
            this.mPhone.mCi.setGsmBroadcastConfig(configs, response);
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
                log("interrupted while trying to set cell broadcast config");
            }
        }
        return this.mSuccess;
    }

    private boolean setCellBroadcastActivation(boolean activate) {
        log("Calling setCellBroadcastActivation(" + activate + ')');
        synchronized (this.mLock) {
            Message response = this.mHandler.obtainMessage(3);
            this.mSuccess = false;
            this.mPhone.mCi.setGsmBroadcastActivation(activate, response);
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
                log("interrupted while trying to set cell broadcast activation");
            }
        }
        return this.mSuccess;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setCdmaBroadcastConfig(CdmaSmsBroadcastConfigInfo[] configs) {
        log("Calling setCdmaBroadcastConfig with " + configs.length + " configurations");
        synchronized (this.mLock) {
            Message response = this.mHandler.obtainMessage(4);
            this.mSuccess = false;
            this.mPhone.mCi.setCdmaBroadcastConfig(configs, response);
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
                log("interrupted while trying to set cdma broadcast config");
            }
        }
        return this.mSuccess;
    }

    private boolean setCdmaBroadcastActivation(boolean activate) {
        log("Calling setCdmaBroadcastActivation(" + activate + Separators.RPAREN);
        synchronized (this.mLock) {
            Message response = this.mHandler.obtainMessage(3);
            this.mSuccess = false;
            this.mPhone.mCi.setCdmaBroadcastActivation(activate, response);
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
                log("interrupted while trying to set cdma broadcast activation");
            }
        }
        return this.mSuccess;
    }

    protected void log(String msg) {
        Log.d(LOG_TAG, "[IccSmsInterfaceManager] " + msg);
    }

    @Override // com.android.internal.telephony.ISms
    public boolean isImsSmsSupported() {
        return this.mDispatcher.isIms();
    }

    @Override // com.android.internal.telephony.ISms
    public String getImsSmsFormat() {
        return this.mDispatcher.getImsSmsFormat();
    }
}