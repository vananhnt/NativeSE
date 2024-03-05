package android.app;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import com.android.internal.statusbar.IStatusBarService;

/* loaded from: StatusBarManager.class */
public class StatusBarManager {
    public static final int DISABLE_EXPAND = 65536;
    public static final int DISABLE_NOTIFICATION_ICONS = 131072;
    public static final int DISABLE_NOTIFICATION_ALERTS = 262144;
    public static final int DISABLE_NOTIFICATION_TICKER = 524288;
    public static final int DISABLE_SYSTEM_INFO = 1048576;
    public static final int DISABLE_HOME = 2097152;
    public static final int DISABLE_RECENT = 16777216;
    public static final int DISABLE_BACK = 4194304;
    public static final int DISABLE_CLOCK = 8388608;
    public static final int DISABLE_SEARCH = 33554432;
    @Deprecated
    public static final int DISABLE_NAVIGATION = 18874368;
    public static final int DISABLE_NONE = 0;
    public static final int DISABLE_MASK = 67043328;
    public static final int NAVIGATION_HINT_BACK_NOP = 1;
    public static final int NAVIGATION_HINT_HOME_NOP = 2;
    public static final int NAVIGATION_HINT_RECENT_NOP = 4;
    public static final int NAVIGATION_HINT_BACK_ALT = 8;
    public static final int WINDOW_STATUS_BAR = 1;
    public static final int WINDOW_NAVIGATION_BAR = 2;
    public static final int WINDOW_STATE_SHOWING = 0;
    public static final int WINDOW_STATE_HIDING = 1;
    public static final int WINDOW_STATE_HIDDEN = 2;
    private Context mContext;
    private IStatusBarService mService;
    private IBinder mToken = new Binder();

    /* JADX INFO: Access modifiers changed from: package-private */
    public StatusBarManager(Context context) {
        this.mContext = context;
    }

    private synchronized IStatusBarService getService() {
        if (this.mService == null) {
            this.mService = IStatusBarService.Stub.asInterface(ServiceManager.getService(Context.STATUS_BAR_SERVICE));
            if (this.mService == null) {
                Slog.w("StatusBarManager", "warning: no STATUS_BAR_SERVICE");
            }
        }
        return this.mService;
    }

    public void disable(int what) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.disable(what, this.mToken, this.mContext.getPackageName());
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void expandNotificationsPanel() {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.expandNotificationsPanel();
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void collapsePanels() {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.collapsePanels();
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void expandSettingsPanel() {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.expandSettingsPanel();
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setIcon(String slot, int iconId, int iconLevel, String contentDescription) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.setIcon(slot, this.mContext.getPackageName(), iconId, iconLevel, contentDescription);
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void removeIcon(String slot) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.removeIcon(slot);
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setIconVisibility(String slot, boolean visible) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.setIconVisibility(slot, visible);
            }
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String windowStateToString(int state) {
        return state == 1 ? "WINDOW_STATE_HIDING" : state == 2 ? "WINDOW_STATE_HIDDEN" : state == 0 ? "WINDOW_STATE_SHOWING" : "WINDOW_STATE_UNKNOWN";
    }
}