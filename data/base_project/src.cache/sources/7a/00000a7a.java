package android.nfc.cardemulation;

import android.app.ActivityThread;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.nfc.INfcCardEmulation;
import android.nfc.NfcAdapter;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import java.util.HashMap;
import java.util.List;

/* loaded from: CardEmulation.class */
public final class CardEmulation {
    static final String TAG = "CardEmulation";
    public static final String ACTION_CHANGE_DEFAULT = "android.nfc.cardemulation.action.ACTION_CHANGE_DEFAULT";
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_SERVICE_COMPONENT = "component";
    public static final String CATEGORY_PAYMENT = "payment";
    public static final String CATEGORY_OTHER = "other";
    public static final int SELECTION_MODE_PREFER_DEFAULT = 0;
    public static final int SELECTION_MODE_ALWAYS_ASK = 1;
    public static final int SELECTION_MODE_ASK_IF_CONFLICT = 2;
    static boolean sIsInitialized = false;
    static HashMap<Context, CardEmulation> sCardEmus = new HashMap<>();
    static INfcCardEmulation sService;
    final Context mContext;

    private CardEmulation(Context context, INfcCardEmulation service) {
        this.mContext = context.getApplicationContext();
        sService = service;
    }

    public static synchronized CardEmulation getInstance(NfcAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("NfcAdapter is null");
        }
        Context context = adapter.getContext();
        if (context == null) {
            Log.e(TAG, "NfcAdapter context is null.");
            throw new UnsupportedOperationException();
        }
        if (!sIsInitialized) {
            IPackageManager pm = ActivityThread.getPackageManager();
            if (pm == null) {
                Log.e(TAG, "Cannot get PackageManager");
                throw new UnsupportedOperationException();
            }
            try {
                if (!pm.hasSystemFeature("android.hardware.nfc.hce")) {
                    Log.e(TAG, "This device does not support card emulation");
                    throw new UnsupportedOperationException();
                }
                sIsInitialized = true;
            } catch (RemoteException e) {
                Log.e(TAG, "PackageManager query failed.");
                throw new UnsupportedOperationException();
            }
        }
        CardEmulation manager = sCardEmus.get(context);
        if (manager == null) {
            INfcCardEmulation service = adapter.getCardEmulationService();
            manager = new CardEmulation(context, service);
            sCardEmus.put(context, manager);
        }
        return manager;
    }

    public boolean isDefaultServiceForCategory(ComponentName service, String category) {
        try {
            return sService.isDefaultServiceForCategory(UserHandle.myUserId(), service, category);
        } catch (RemoteException e) {
            recoverService();
            if (sService == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return sService.isDefaultServiceForCategory(UserHandle.myUserId(), service, category);
            } catch (RemoteException e2) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
        }
    }

    public boolean isDefaultServiceForAid(ComponentName service, String aid) {
        try {
            return sService.isDefaultServiceForAid(UserHandle.myUserId(), service, aid);
        } catch (RemoteException e) {
            recoverService();
            if (sService == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return sService.isDefaultServiceForAid(UserHandle.myUserId(), service, aid);
            } catch (RemoteException e2) {
                Log.e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public int getSelectionModeForCategory(String category) {
        if (CATEGORY_PAYMENT.equals(category)) {
            String defaultComponent = Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.NFC_PAYMENT_DEFAULT_COMPONENT);
            if (defaultComponent != null) {
                return 0;
            }
            return 1;
        }
        return 2;
    }

    public boolean setDefaultServiceForCategory(ComponentName service, String category) {
        try {
            return sService.setDefaultServiceForCategory(UserHandle.myUserId(), service, category);
        } catch (RemoteException e) {
            recoverService();
            if (sService == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return sService.setDefaultServiceForCategory(UserHandle.myUserId(), service, category);
            } catch (RemoteException e2) {
                Log.e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public boolean setDefaultForNextTap(ComponentName service) {
        try {
            return sService.setDefaultForNextTap(UserHandle.myUserId(), service);
        } catch (RemoteException e) {
            recoverService();
            if (sService == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return false;
            }
            try {
                return sService.setDefaultForNextTap(UserHandle.myUserId(), service);
            } catch (RemoteException e2) {
                Log.e(TAG, "Failed to reach CardEmulationService.");
                return false;
            }
        }
    }

    public List<ApduServiceInfo> getServices(String category) {
        try {
            return sService.getServices(UserHandle.myUserId(), category);
        } catch (RemoteException e) {
            recoverService();
            if (sService == null) {
                Log.e(TAG, "Failed to recover CardEmulationService.");
                return null;
            }
            try {
                return sService.getServices(UserHandle.myUserId(), category);
            } catch (RemoteException e2) {
                Log.e(TAG, "Failed to reach CardEmulationService.");
                return null;
            }
        }
    }

    void recoverService() {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this.mContext);
        sService = adapter.getCardEmulationService();
    }
}