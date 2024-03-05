package com.android.internal.telephony;

import android.net.LinkCapabilities;
import android.net.LinkProperties;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.CellInfo;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import java.util.List;

/* loaded from: DefaultPhoneNotifier.class */
public class DefaultPhoneNotifier implements PhoneNotifier {
    private ITelephonyRegistry mRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry"));

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyPhoneState(Phone sender) {
        Call ringingCall = sender.getRingingCall();
        String incomingNumber = "";
        if (ringingCall != null && ringingCall.getEarliestConnection() != null) {
            incomingNumber = ringingCall.getEarliestConnection().getAddress();
        }
        try {
            this.mRegistry.notifyCallState(convertCallState(sender.getState()), incomingNumber);
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyServiceState(Phone sender) {
        ServiceState ss = sender.getServiceState();
        if (ss == null) {
            ss = new ServiceState();
            ss.setStateOutOfService();
        }
        try {
            this.mRegistry.notifyServiceState(ss);
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifySignalStrength(Phone sender) {
        try {
            this.mRegistry.notifySignalStrength(sender.getSignalStrength());
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyMessageWaitingChanged(Phone sender) {
        try {
            this.mRegistry.notifyMessageWaitingChanged(sender.getMessageWaitingIndicator());
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyCallForwardingChanged(Phone sender) {
        try {
            this.mRegistry.notifyCallForwardingChanged(sender.getCallForwardingIndicator());
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyDataActivity(Phone sender) {
        try {
            this.mRegistry.notifyDataActivity(convertDataActivityState(sender.getDataActivityState()));
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyDataConnection(Phone sender, String reason, String apnType, PhoneConstants.DataState state) {
        doNotifyDataConnection(sender, reason, apnType, state);
    }

    private void doNotifyDataConnection(Phone sender, String reason, String apnType, PhoneConstants.DataState state) {
        TelephonyManager telephony = TelephonyManager.getDefault();
        LinkProperties linkProperties = null;
        LinkCapabilities linkCapabilities = null;
        boolean roaming = false;
        if (state == PhoneConstants.DataState.CONNECTED) {
            linkProperties = sender.getLinkProperties(apnType);
            linkCapabilities = sender.getLinkCapabilities(apnType);
        }
        ServiceState ss = sender.getServiceState();
        if (ss != null) {
            roaming = ss.getRoaming();
        }
        try {
            this.mRegistry.notifyDataConnection(convertDataState(state), sender.isDataConnectivityPossible(apnType), reason, sender.getActiveApnHost(apnType), apnType, linkProperties, linkCapabilities, telephony != null ? telephony.getNetworkType() : 0, roaming);
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyDataConnectionFailed(Phone sender, String reason, String apnType) {
        try {
            this.mRegistry.notifyDataConnectionFailed(reason, apnType);
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyCellLocation(Phone sender) {
        Bundle data = new Bundle();
        sender.getCellLocation().fillInNotifierBundle(data);
        try {
            this.mRegistry.notifyCellLocation(data);
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyCellInfo(Phone sender, List<CellInfo> cellInfo) {
        try {
            this.mRegistry.notifyCellInfo(cellInfo);
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.internal.telephony.PhoneNotifier
    public void notifyOtaspChanged(Phone sender, int otaspMode) {
        try {
            this.mRegistry.notifyOtaspChanged(otaspMode);
        } catch (RemoteException e) {
        }
    }

    public static int convertCallState(PhoneConstants.State state) {
        switch (state) {
            case RINGING:
                return 1;
            case OFFHOOK:
                return 2;
            default:
                return 0;
        }
    }

    public static PhoneConstants.State convertCallState(int state) {
        switch (state) {
            case 1:
                return PhoneConstants.State.RINGING;
            case 2:
                return PhoneConstants.State.OFFHOOK;
            default:
                return PhoneConstants.State.IDLE;
        }
    }

    public static int convertDataState(PhoneConstants.DataState state) {
        switch (state) {
            case CONNECTING:
                return 1;
            case CONNECTED:
                return 2;
            case SUSPENDED:
                return 3;
            default:
                return 0;
        }
    }

    public static PhoneConstants.DataState convertDataState(int state) {
        switch (state) {
            case 1:
                return PhoneConstants.DataState.CONNECTING;
            case 2:
                return PhoneConstants.DataState.CONNECTED;
            case 3:
                return PhoneConstants.DataState.SUSPENDED;
            default:
                return PhoneConstants.DataState.DISCONNECTED;
        }
    }

    public static int convertDataActivityState(Phone.DataActivityState state) {
        switch (state) {
            case DATAIN:
                return 1;
            case DATAOUT:
                return 2;
            case DATAINANDOUT:
                return 3;
            case DORMANT:
                return 4;
            default:
                return 0;
        }
    }

    public static Phone.DataActivityState convertDataActivityState(int state) {
        switch (state) {
            case 1:
                return Phone.DataActivityState.DATAIN;
            case 2:
                return Phone.DataActivityState.DATAOUT;
            case 3:
                return Phone.DataActivityState.DATAINANDOUT;
            case 4:
                return Phone.DataActivityState.DORMANT;
            default:
                return Phone.DataActivityState.NONE;
        }
    }
}