package com.android.internal.telephony;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.provider.Telephony;
import android.telephony.SmsCbMessage;
import com.android.internal.location.GpsNetInitiatedHandler;

/* loaded from: CellBroadcastHandler.class */
public class CellBroadcastHandler extends WakeLockStateMachine {
    private final Context mContext;

    private CellBroadcastHandler(Context context) {
        this("CellBroadcastHandler", context);
    }

    protected CellBroadcastHandler(String debugTag, Context context) {
        super(debugTag, context);
        this.mContext = context;
    }

    public static CellBroadcastHandler makeCellBroadcastHandler(Context context) {
        CellBroadcastHandler handler = new CellBroadcastHandler(context);
        handler.start();
        return handler;
    }

    @Override // com.android.internal.telephony.WakeLockStateMachine
    protected boolean handleSmsMessage(Message message) {
        if (message.obj instanceof SmsCbMessage) {
            handleBroadcastSms((SmsCbMessage) message.obj);
            return true;
        }
        loge("handleMessage got object of type: " + message.obj.getClass().getName());
        return false;
    }

    protected void handleBroadcastSms(SmsCbMessage message) {
        Intent intent;
        String receiverPermission;
        int appOp;
        if (message.isEmergencyMessage()) {
            log("Dispatching emergency SMS CB");
            intent = new Intent(Telephony.Sms.Intents.SMS_EMERGENCY_CB_RECEIVED_ACTION);
            receiverPermission = Manifest.permission.RECEIVE_EMERGENCY_BROADCAST;
            appOp = 17;
        } else {
            log("Dispatching SMS CB");
            intent = new Intent(Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION);
            receiverPermission = Manifest.permission.RECEIVE_SMS;
            appOp = 16;
        }
        intent.putExtra(GpsNetInitiatedHandler.NI_INTENT_KEY_MESSAGE, message);
        this.mContext.sendOrderedBroadcast(intent, receiverPermission, appOp, this.mReceiver, getHandler(), -1, null, null);
    }
}