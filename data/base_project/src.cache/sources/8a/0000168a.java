package android.view.textservice;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.textservice.SpellCheckerSession;
import com.android.internal.textservice.ITextServicesManager;
import java.util.Locale;

/* loaded from: TextServicesManager.class */
public final class TextServicesManager {
    private static final String TAG = TextServicesManager.class.getSimpleName();
    private static final boolean DBG = false;
    private static TextServicesManager sInstance;
    private static ITextServicesManager sService;

    private TextServicesManager() {
        if (sService == null) {
            IBinder b = ServiceManager.getService(Context.TEXT_SERVICES_MANAGER_SERVICE);
            sService = ITextServicesManager.Stub.asInterface(b);
        }
    }

    public static TextServicesManager getInstance() {
        synchronized (TextServicesManager.class) {
            if (sInstance != null) {
                return sInstance;
            }
            sInstance = new TextServicesManager();
            return sInstance;
        }
    }

    public SpellCheckerSession newSpellCheckerSession(Bundle bundle, Locale locale, SpellCheckerSession.SpellCheckerSessionListener listener, boolean referToSpellCheckerLanguageSettings) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (!referToSpellCheckerLanguageSettings && locale == null) {
            throw new IllegalArgumentException("Locale should not be null if you don't refer settings.");
        }
        if (referToSpellCheckerLanguageSettings && !isSpellCheckerEnabled()) {
            return null;
        }
        try {
            SpellCheckerInfo sci = sService.getCurrentSpellChecker(null);
            if (sci == null) {
                return null;
            }
            SpellCheckerSubtype subtypeInUse = null;
            if (referToSpellCheckerLanguageSettings) {
                subtypeInUse = getCurrentSpellCheckerSubtype(true);
                if (subtypeInUse == null) {
                    return null;
                }
                if (locale != null) {
                    String subtypeLocale = subtypeInUse.getLocale();
                    String inputLocale = locale.toString();
                    if (subtypeLocale.length() < 2 || inputLocale.length() < 2 || !subtypeLocale.substring(0, 2).equals(inputLocale.substring(0, 2))) {
                        return null;
                    }
                }
            } else {
                String localeStr = locale.toString();
                int i = 0;
                while (true) {
                    if (i >= sci.getSubtypeCount()) {
                        break;
                    }
                    SpellCheckerSubtype subtype = sci.getSubtypeAt(i);
                    String tempSubtypeLocale = subtype.getLocale();
                    if (tempSubtypeLocale.equals(localeStr)) {
                        subtypeInUse = subtype;
                        break;
                    }
                    if (localeStr.length() >= 2 && tempSubtypeLocale.length() >= 2 && localeStr.startsWith(tempSubtypeLocale)) {
                        subtypeInUse = subtype;
                    }
                    i++;
                }
            }
            if (subtypeInUse == null) {
                return null;
            }
            SpellCheckerSession session = new SpellCheckerSession(sci, sService, listener, subtypeInUse);
            try {
                sService.getSpellCheckerService(sci.getId(), subtypeInUse.getLocale(), session.getTextServicesSessionListener(), session.getSpellCheckerSessionListener(), bundle);
                return session;
            } catch (RemoteException e) {
                return null;
            }
        } catch (RemoteException e2) {
            return null;
        }
    }

    public SpellCheckerInfo[] getEnabledSpellCheckers() {
        try {
            SpellCheckerInfo[] retval = sService.getEnabledSpellCheckers();
            return retval;
        } catch (RemoteException e) {
            Log.e(TAG, "Error in getEnabledSpellCheckers: " + e);
            return null;
        }
    }

    public SpellCheckerInfo getCurrentSpellChecker() {
        try {
            return sService.getCurrentSpellChecker(null);
        } catch (RemoteException e) {
            return null;
        }
    }

    public void setCurrentSpellChecker(SpellCheckerInfo sci) {
        try {
            if (sci == null) {
                throw new NullPointerException("SpellCheckerInfo is null.");
            }
            sService.setCurrentSpellChecker(null, sci.getId());
        } catch (RemoteException e) {
            Log.e(TAG, "Error in setCurrentSpellChecker: " + e);
        }
    }

    public SpellCheckerSubtype getCurrentSpellCheckerSubtype(boolean allowImplicitlySelectedSubtype) {
        try {
            if (sService == null) {
                Log.e(TAG, "sService is null.");
                return null;
            }
            return sService.getCurrentSpellCheckerSubtype(null, allowImplicitlySelectedSubtype);
        } catch (RemoteException e) {
            Log.e(TAG, "Error in getCurrentSpellCheckerSubtype: " + e);
            return null;
        }
    }

    public void setSpellCheckerSubtype(SpellCheckerSubtype subtype) {
        int hashCode;
        if (subtype == null) {
            hashCode = 0;
        } else {
            try {
                hashCode = subtype.hashCode();
            } catch (RemoteException e) {
                Log.e(TAG, "Error in setSpellCheckerSubtype:" + e);
                return;
            }
        }
        sService.setCurrentSpellCheckerSubtype(null, hashCode);
    }

    public void setSpellCheckerEnabled(boolean enabled) {
        try {
            sService.setSpellCheckerEnabled(enabled);
        } catch (RemoteException e) {
            Log.e(TAG, "Error in setSpellCheckerEnabled:" + e);
        }
    }

    public boolean isSpellCheckerEnabled() {
        try {
            return sService.isSpellCheckerEnabled();
        } catch (RemoteException e) {
            Log.e(TAG, "Error in isSpellCheckerEnabled:" + e);
            return false;
        }
    }
}