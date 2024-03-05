package com.android.internal.telephony;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Downloads;
import android.provider.Telephony;
import android.telephony.Rlog;
import com.android.internal.R;
import com.android.internal.telephony.IWapPushManager;
import gov.nist.core.Separators;

/* loaded from: WapPushOverSms.class */
public class WapPushOverSms implements ServiceConnection {
    private static final String TAG = "WAP PUSH";
    private static final boolean DBG = true;
    private final Context mContext;
    private volatile IWapPushManager mWapPushManager;

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mWapPushManager = IWapPushManager.Stub.asInterface(service);
        Rlog.v(TAG, "wappush manager connected to " + hashCode());
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        this.mWapPushManager = null;
        Rlog.v(TAG, "wappush manager disconnected.");
    }

    public WapPushOverSms(Context context) {
        this.mContext = context;
        Intent intent = new Intent(IWapPushManager.class.getName());
        ComponentName comp = intent.resolveSystemService(context.getPackageManager(), 0);
        intent.setComponent(comp);
        if (comp == null || !context.bindService(intent, this, 1)) {
            Rlog.e(TAG, "bindService() for wappush manager failed");
        } else {
            Rlog.v(TAG, "bindService() for wappush manager succeeded");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispose() {
        if (this.mWapPushManager != null) {
            Rlog.v(TAG, "dispose: unbind wappush manager");
            this.mContext.unbindService(this);
            return;
        }
        Rlog.e(TAG, "dispose: not bound to a wappush manager");
    }

    public int dispatchWapPdu(byte[] pdu, BroadcastReceiver receiver, InboundSmsHandler handler) {
        byte[] intentData;
        String permission;
        int appOp;
        Rlog.d(TAG, "Rx: " + com.android.internal.telephony.uicc.IccUtils.bytesToHexString(pdu));
        int index = 0 + 1;
        int transactionId = pdu[0] & 255;
        int index2 = index + 1;
        int pduType = pdu[index] & 255;
        if (pduType != 6 && pduType != 7) {
            int index3 = this.mContext.getResources().getInteger(R.integer.config_valid_wappush_index);
            if (index3 != -1) {
                int index4 = index3 + 1;
                transactionId = pdu[index3] & 255;
                index2 = index4 + 1;
                pduType = pdu[index4] & 255;
                Rlog.d(TAG, "index = " + index2 + " PDU Type = " + pduType + " transactionID = " + transactionId);
                if (pduType != 6 && pduType != 7) {
                    Rlog.w(TAG, "Received non-PUSH WAP PDU. Type = " + pduType);
                    return 1;
                }
            } else {
                Rlog.w(TAG, "Received non-PUSH WAP PDU. Type = " + pduType);
                return 1;
            }
        }
        WspTypeDecoder pduDecoder = new WspTypeDecoder(pdu);
        if (!pduDecoder.decodeUintvarInteger(index2)) {
            Rlog.w(TAG, "Received PDU. Header Length error.");
            return 2;
        }
        int headerLength = (int) pduDecoder.getValue32();
        int index5 = index2 + pduDecoder.getDecodedDataLength();
        if (!pduDecoder.decodeContentType(index5)) {
            Rlog.w(TAG, "Received PDU. Header Content-Type error.");
            return 2;
        }
        String mimeType = pduDecoder.getValueString();
        long binaryContentType = pduDecoder.getValue32();
        int index6 = index5 + pduDecoder.getDecodedDataLength();
        byte[] header = new byte[headerLength];
        System.arraycopy(pdu, index5, header, 0, header.length);
        if (mimeType != null && mimeType.equals(WspTypeDecoder.CONTENT_TYPE_B_PUSH_CO)) {
            intentData = pdu;
        } else {
            int dataIndex = index5 + headerLength;
            intentData = new byte[pdu.length - dataIndex];
            System.arraycopy(pdu, dataIndex, intentData, 0, intentData.length);
        }
        if (pduDecoder.seekXWapApplicationId(index6, (index6 + headerLength) - 1)) {
            pduDecoder.decodeXWapApplicationId((int) pduDecoder.getValue32());
            String wapAppId = pduDecoder.getValueString();
            if (wapAppId == null) {
                wapAppId = Integer.toString((int) pduDecoder.getValue32());
            }
            String contentType = mimeType == null ? Long.toString(binaryContentType) : mimeType;
            Rlog.v(TAG, "appid found: " + wapAppId + Separators.COLON + contentType);
            try {
                boolean processFurther = true;
                IWapPushManager wapPushMan = this.mWapPushManager;
                if (wapPushMan == null) {
                    Rlog.w(TAG, "wap push manager not found!");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("transactionId", transactionId);
                    intent.putExtra("pduType", pduType);
                    intent.putExtra(Downloads.Impl.RequestHeaders.COLUMN_HEADER, header);
                    intent.putExtra("data", intentData);
                    intent.putExtra("contentTypeParameters", pduDecoder.getContentParameters());
                    int procRet = wapPushMan.processMessage(wapAppId, contentType, intent);
                    Rlog.v(TAG, "procRet:" + procRet);
                    if ((procRet & 1) > 0 && (procRet & 32768) == 0) {
                        processFurther = false;
                    }
                }
                if (!processFurther) {
                    return 1;
                }
            } catch (RemoteException e) {
                Rlog.w(TAG, "remote func failed...");
            }
        }
        Rlog.v(TAG, "fall back to existing handler");
        if (mimeType == null) {
            Rlog.w(TAG, "Header Content-Type error.");
            return 2;
        }
        if (mimeType.equals(WspTypeDecoder.CONTENT_TYPE_B_MMS)) {
            permission = Manifest.permission.RECEIVE_MMS;
            appOp = 18;
        } else {
            permission = Manifest.permission.RECEIVE_WAP_PUSH;
            appOp = 19;
        }
        Intent intent2 = new Intent(Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION);
        intent2.setType(mimeType);
        intent2.putExtra("transactionId", transactionId);
        intent2.putExtra("pduType", pduType);
        intent2.putExtra(Downloads.Impl.RequestHeaders.COLUMN_HEADER, header);
        intent2.putExtra("data", intentData);
        intent2.putExtra("contentTypeParameters", pduDecoder.getContentParameters());
        ComponentName componentName = SmsApplication.getDefaultMmsApplication(this.mContext, true);
        if (componentName != null) {
            intent2.setComponent(componentName);
            Rlog.v(TAG, "Delivering MMS to: " + componentName.getPackageName() + Separators.SP + componentName.getClassName());
        }
        handler.dispatchIntent(intent2, permission, appOp, receiver);
        return -1;
    }
}