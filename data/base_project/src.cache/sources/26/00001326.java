package android.telephony;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.android.internal.telephony.ITelephony;

/* loaded from: CellLocation.class */
public abstract class CellLocation {
    public abstract void fillInNotifierBundle(Bundle bundle);

    public abstract boolean isEmpty();

    public static void requestLocationUpdate() {
        try {
            ITelephony phone = ITelephony.Stub.asInterface(ServiceManager.getService("phone"));
            if (phone != null) {
                phone.updateServiceLocation();
            }
        } catch (RemoteException e) {
        }
    }

    public static CellLocation newFromBundle(Bundle bundle) {
        switch (TelephonyManager.getDefault().getCurrentPhoneType()) {
            case 1:
                return new GsmCellLocation(bundle);
            case 2:
                return new CdmaCellLocation(bundle);
            default:
                return null;
        }
    }

    public static CellLocation getEmpty() {
        switch (TelephonyManager.getDefault().getCurrentPhoneType()) {
            case 1:
                return new GsmCellLocation();
            case 2:
                return new CdmaCellLocation();
            default:
                return null;
        }
    }
}