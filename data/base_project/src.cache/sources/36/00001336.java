package android.telephony;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.android.internal.telephony.IPhoneStateListener;
import java.util.List;

/* loaded from: PhoneStateListener.class */
public class PhoneStateListener {
    public static final int LISTEN_NONE = 0;
    public static final int LISTEN_SERVICE_STATE = 1;
    @Deprecated
    public static final int LISTEN_SIGNAL_STRENGTH = 2;
    public static final int LISTEN_MESSAGE_WAITING_INDICATOR = 4;
    public static final int LISTEN_CALL_FORWARDING_INDICATOR = 8;
    public static final int LISTEN_CELL_LOCATION = 16;
    public static final int LISTEN_CALL_STATE = 32;
    public static final int LISTEN_DATA_CONNECTION_STATE = 64;
    public static final int LISTEN_DATA_ACTIVITY = 128;
    public static final int LISTEN_SIGNAL_STRENGTHS = 256;
    public static final int LISTEN_OTASP_CHANGED = 512;
    public static final int LISTEN_CELL_INFO = 1024;
    IPhoneStateListener callback = new IPhoneStateListener.Stub() { // from class: android.telephony.PhoneStateListener.1
        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onServiceStateChanged(ServiceState serviceState) {
            Message.obtain(PhoneStateListener.this.mHandler, 1, 0, 0, serviceState).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onSignalStrengthChanged(int asu) {
            Message.obtain(PhoneStateListener.this.mHandler, 2, asu, 0, null).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onMessageWaitingIndicatorChanged(boolean mwi) {
            Message.obtain(PhoneStateListener.this.mHandler, 4, mwi ? 1 : 0, 0, null).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallForwardingIndicatorChanged(boolean cfi) {
            Message.obtain(PhoneStateListener.this.mHandler, 8, cfi ? 1 : 0, 0, null).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCellLocationChanged(Bundle bundle) {
            CellLocation location = CellLocation.newFromBundle(bundle);
            Message.obtain(PhoneStateListener.this.mHandler, 16, 0, 0, location).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCallStateChanged(int state, String incomingNumber) {
            Message.obtain(PhoneStateListener.this.mHandler, 32, state, 0, incomingNumber).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataConnectionStateChanged(int state, int networkType) {
            Message.obtain(PhoneStateListener.this.mHandler, 64, state, networkType).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onDataActivity(int direction) {
            Message.obtain(PhoneStateListener.this.mHandler, 128, direction, 0, null).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            Message.obtain(PhoneStateListener.this.mHandler, 256, 0, 0, signalStrength).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onOtaspChanged(int otaspMode) {
            Message.obtain(PhoneStateListener.this.mHandler, 512, otaspMode, 0).sendToTarget();
        }

        @Override // com.android.internal.telephony.IPhoneStateListener
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
            Message.obtain(PhoneStateListener.this.mHandler, 1024, 0, 0, cellInfo).sendToTarget();
        }
    };
    Handler mHandler = new Handler() { // from class: android.telephony.PhoneStateListener.2
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    PhoneStateListener.this.onServiceStateChanged((ServiceState) msg.obj);
                    return;
                case 2:
                    PhoneStateListener.this.onSignalStrengthChanged(msg.arg1);
                    return;
                case 4:
                    PhoneStateListener.this.onMessageWaitingIndicatorChanged(msg.arg1 != 0);
                    return;
                case 8:
                    PhoneStateListener.this.onCallForwardingIndicatorChanged(msg.arg1 != 0);
                    return;
                case 16:
                    PhoneStateListener.this.onCellLocationChanged((CellLocation) msg.obj);
                    return;
                case 32:
                    PhoneStateListener.this.onCallStateChanged(msg.arg1, (String) msg.obj);
                    return;
                case 64:
                    PhoneStateListener.this.onDataConnectionStateChanged(msg.arg1, msg.arg2);
                    PhoneStateListener.this.onDataConnectionStateChanged(msg.arg1);
                    return;
                case 128:
                    PhoneStateListener.this.onDataActivity(msg.arg1);
                    return;
                case 256:
                    PhoneStateListener.this.onSignalStrengthsChanged((SignalStrength) msg.obj);
                    return;
                case 512:
                    PhoneStateListener.this.onOtaspChanged(msg.arg1);
                    return;
                case 1024:
                    PhoneStateListener.this.onCellInfoChanged((List) msg.obj);
                    return;
                default:
                    return;
            }
        }
    };

    public void onServiceStateChanged(ServiceState serviceState) {
    }

    @Deprecated
    public void onSignalStrengthChanged(int asu) {
    }

    public void onMessageWaitingIndicatorChanged(boolean mwi) {
    }

    public void onCallForwardingIndicatorChanged(boolean cfi) {
    }

    public void onCellLocationChanged(CellLocation location) {
    }

    public void onCallStateChanged(int state, String incomingNumber) {
    }

    public void onDataConnectionStateChanged(int state) {
    }

    public void onDataConnectionStateChanged(int state, int networkType) {
    }

    public void onDataActivity(int direction) {
    }

    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
    }

    public void onOtaspChanged(int otaspMode) {
    }

    public void onCellInfoChanged(List<CellInfo> cellInfo) {
    }
}