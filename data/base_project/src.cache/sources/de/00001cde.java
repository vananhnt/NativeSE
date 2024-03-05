package com.android.server;

import android.Manifest;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.statusbar.StatusBarIconList;
import com.android.server.wm.WindowManagerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: StatusBarManagerService.class */
public class StatusBarManagerService extends IStatusBarService.Stub implements WindowManagerService.OnHardKeyboardStatusChangeListener {
    static final String TAG = "StatusBarManagerService";
    static final boolean SPEW = false;
    final Context mContext;
    final WindowManagerService mWindowManager;
    NotificationCallbacks mNotificationCallbacks;
    volatile IStatusBar mBar;
    int mImeBackDisposition;
    int mCurrentUserId;
    Handler mHandler = new Handler();
    StatusBarIconList mIcons = new StatusBarIconList();
    HashMap<IBinder, StatusBarNotification> mNotifications = new HashMap<>();
    final ArrayList<DisableRecord> mDisableRecords = new ArrayList<>();
    IBinder mSysUiVisToken = new Binder();
    int mDisabled = 0;
    Object mLock = new Object();
    int mSystemUiVisibility = 0;
    boolean mMenuVisible = false;
    int mImeWindowVis = 0;
    IBinder mImeToken = null;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.StatusBarManagerService.7
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action) || Intent.ACTION_SCREEN_OFF.equals(action)) {
                StatusBarManagerService.this.collapsePanels();
            }
        }
    };

    /* loaded from: StatusBarManagerService$NotificationCallbacks.class */
    public interface NotificationCallbacks {
        void onSetDisabled(int i);

        void onClearAll();

        void onNotificationClick(String str, String str2, int i);

        void onNotificationClear(String str, String str2, int i);

        void onPanelRevealed();

        void onNotificationError(String str, String str2, int i, int i2, int i3, String str3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: StatusBarManagerService$DisableRecord.class */
    public class DisableRecord implements IBinder.DeathRecipient {
        int userId;
        String pkg;
        int what;
        IBinder token;

        private DisableRecord() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Slog.i(StatusBarManagerService.TAG, "binder died for pkg=" + this.pkg);
            StatusBarManagerService.this.disableInternal(this.userId, 0, this.token, this.pkg);
            this.token.unlinkToDeath(this, 0);
        }
    }

    public StatusBarManagerService(Context context, WindowManagerService windowManager) {
        this.mContext = context;
        this.mWindowManager = windowManager;
        this.mWindowManager.setOnHardKeyboardStatusChangeListener(this);
        Resources res = context.getResources();
        this.mIcons.defineSlots(res.getStringArray(R.array.config_statusBarIcons));
    }

    public void setNotificationCallbacks(NotificationCallbacks listener) {
        this.mNotificationCallbacks = listener;
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void expandNotificationsPanel() {
        enforceExpandStatusBar();
        if (this.mBar != null) {
            try {
                this.mBar.animateExpandNotificationsPanel();
            } catch (RemoteException e) {
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void collapsePanels() {
        enforceExpandStatusBar();
        if (this.mBar != null) {
            try {
                this.mBar.animateCollapsePanels();
            } catch (RemoteException e) {
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void expandSettingsPanel() {
        enforceExpandStatusBar();
        if (this.mBar != null) {
            try {
                this.mBar.animateExpandSettingsPanel();
            } catch (RemoteException e) {
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void disable(int what, IBinder token, String pkg) {
        disableInternal(this.mCurrentUserId, what, token, pkg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disableInternal(int userId, int what, IBinder token, String pkg) {
        enforceStatusBar();
        synchronized (this.mLock) {
            disableLocked(userId, what, token, pkg);
        }
    }

    private void disableLocked(int userId, int what, IBinder token, String pkg) {
        manageDisableListLocked(userId, what, token, pkg);
        final int net = gatherDisableActionsLocked(this.mCurrentUserId);
        if (net != this.mDisabled) {
            this.mDisabled = net;
            this.mHandler.post(new Runnable() { // from class: com.android.server.StatusBarManagerService.1
                @Override // java.lang.Runnable
                public void run() {
                    StatusBarManagerService.this.mNotificationCallbacks.onSetDisabled(net);
                }
            });
            if (this.mBar != null) {
                try {
                    this.mBar.disable(net);
                } catch (RemoteException e) {
                }
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void setIcon(String slot, String iconPackage, int iconId, int iconLevel, String contentDescription) {
        enforceStatusBar();
        synchronized (this.mIcons) {
            int index = this.mIcons.getSlotIndex(slot);
            if (index < 0) {
                throw new SecurityException("invalid status bar icon slot: " + slot);
            }
            StatusBarIcon icon = new StatusBarIcon(iconPackage, UserHandle.OWNER, iconId, iconLevel, 0, contentDescription);
            this.mIcons.setIcon(index, icon);
            if (this.mBar != null) {
                try {
                    this.mBar.setIcon(index, icon);
                } catch (RemoteException e) {
                }
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void setIconVisibility(String slot, boolean visible) {
        enforceStatusBar();
        synchronized (this.mIcons) {
            int index = this.mIcons.getSlotIndex(slot);
            if (index < 0) {
                throw new SecurityException("invalid status bar icon slot: " + slot);
            }
            StatusBarIcon icon = this.mIcons.getIcon(index);
            if (icon == null) {
                return;
            }
            if (icon.visible != visible) {
                icon.visible = visible;
                if (this.mBar != null) {
                    try {
                        this.mBar.setIcon(index, icon);
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void removeIcon(String slot) {
        enforceStatusBar();
        synchronized (this.mIcons) {
            int index = this.mIcons.getSlotIndex(slot);
            if (index < 0) {
                throw new SecurityException("invalid status bar icon slot: " + slot);
            }
            this.mIcons.removeIcon(index);
            if (this.mBar != null) {
                try {
                    this.mBar.removeIcon(index);
                } catch (RemoteException e) {
                }
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void topAppWindowChanged(final boolean menuVisible) {
        enforceStatusBar();
        synchronized (this.mLock) {
            this.mMenuVisible = menuVisible;
            this.mHandler.post(new Runnable() { // from class: com.android.server.StatusBarManagerService.2
                @Override // java.lang.Runnable
                public void run() {
                    if (StatusBarManagerService.this.mBar != null) {
                        try {
                            StatusBarManagerService.this.mBar.topAppWindowChanged(menuVisible);
                        } catch (RemoteException e) {
                        }
                    }
                }
            });
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void setImeWindowStatus(final IBinder token, final int vis, final int backDisposition) {
        enforceStatusBar();
        synchronized (this.mLock) {
            this.mImeWindowVis = vis;
            this.mImeBackDisposition = backDisposition;
            this.mImeToken = token;
            this.mHandler.post(new Runnable() { // from class: com.android.server.StatusBarManagerService.3
                @Override // java.lang.Runnable
                public void run() {
                    if (StatusBarManagerService.this.mBar != null) {
                        try {
                            StatusBarManagerService.this.mBar.setImeWindowStatus(token, vis, backDisposition);
                        } catch (RemoteException e) {
                        }
                    }
                }
            });
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void setSystemUiVisibility(int vis, int mask) {
        enforceStatusBarService();
        synchronized (this.mLock) {
            updateUiVisibilityLocked(vis, mask);
            disableLocked(this.mCurrentUserId, vis & StatusBarManager.DISABLE_MASK, this.mSysUiVisToken, "WindowManager.LayoutParams");
        }
    }

    private void updateUiVisibilityLocked(final int vis, final int mask) {
        if (this.mSystemUiVisibility != vis) {
            this.mSystemUiVisibility = vis;
            this.mHandler.post(new Runnable() { // from class: com.android.server.StatusBarManagerService.4
                @Override // java.lang.Runnable
                public void run() {
                    if (StatusBarManagerService.this.mBar != null) {
                        try {
                            StatusBarManagerService.this.mBar.setSystemUiVisibility(vis, mask);
                        } catch (RemoteException e) {
                        }
                    }
                }
            });
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void setHardKeyboardEnabled(final boolean enabled) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.StatusBarManagerService.5
            @Override // java.lang.Runnable
            public void run() {
                StatusBarManagerService.this.mWindowManager.setHardKeyboardEnabled(enabled);
            }
        });
    }

    @Override // com.android.server.wm.WindowManagerService.OnHardKeyboardStatusChangeListener
    public void onHardKeyboardStatusChange(final boolean available, final boolean enabled) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.StatusBarManagerService.6
            @Override // java.lang.Runnable
            public void run() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.setHardKeyboardStatus(available, enabled);
                    } catch (RemoteException e) {
                    }
                }
            }
        });
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void toggleRecentApps() {
        if (this.mBar != null) {
            try {
                this.mBar.toggleRecentApps();
            } catch (RemoteException e) {
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void preloadRecentApps() {
        if (this.mBar != null) {
            try {
                this.mBar.preloadRecentApps();
            } catch (RemoteException e) {
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void cancelPreloadRecentApps() {
        if (this.mBar != null) {
            try {
                this.mBar.cancelPreloadRecentApps();
            } catch (RemoteException e) {
            }
        }
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void setCurrentUser(int newUserId) {
        this.mCurrentUserId = newUserId;
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void setWindowState(int window, int state) {
        if (this.mBar != null) {
            try {
                this.mBar.setWindowState(window, state);
            } catch (RemoteException e) {
            }
        }
    }

    private void enforceStatusBar() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.STATUS_BAR, TAG);
    }

    private void enforceExpandStatusBar() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.EXPAND_STATUS_BAR, TAG);
    }

    private void enforceStatusBarService() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.STATUS_BAR_SERVICE, TAG);
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void registerStatusBar(IStatusBar bar, StatusBarIconList iconList, List<IBinder> notificationKeys, List<StatusBarNotification> notifications, int[] switches, List<IBinder> binders) {
        enforceStatusBarService();
        Slog.i(TAG, "registerStatusBar bar=" + bar);
        this.mBar = bar;
        synchronized (this.mIcons) {
            iconList.copyFrom(this.mIcons);
        }
        synchronized (this.mNotifications) {
            for (Map.Entry<IBinder, StatusBarNotification> e : this.mNotifications.entrySet()) {
                notificationKeys.add(e.getKey());
                notifications.add(e.getValue());
            }
        }
        synchronized (this.mLock) {
            switches[0] = gatherDisableActionsLocked(this.mCurrentUserId);
            switches[1] = this.mSystemUiVisibility;
            switches[2] = this.mMenuVisible ? 1 : 0;
            switches[3] = this.mImeWindowVis;
            switches[4] = this.mImeBackDisposition;
            binders.add(this.mImeToken);
        }
        switches[5] = this.mWindowManager.isHardKeyboardAvailable() ? 1 : 0;
        switches[6] = this.mWindowManager.isHardKeyboardEnabled() ? 1 : 0;
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void onPanelRevealed() {
        enforceStatusBarService();
        this.mNotificationCallbacks.onPanelRevealed();
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void onNotificationClick(String pkg, String tag, int id) {
        enforceStatusBarService();
        this.mNotificationCallbacks.onNotificationClick(pkg, tag, id);
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void onNotificationError(String pkg, String tag, int id, int uid, int initialPid, String message) {
        enforceStatusBarService();
        this.mNotificationCallbacks.onNotificationError(pkg, tag, id, uid, initialPid, message);
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void onNotificationClear(String pkg, String tag, int id) {
        enforceStatusBarService();
        this.mNotificationCallbacks.onNotificationClear(pkg, tag, id);
    }

    @Override // com.android.internal.statusbar.IStatusBarService
    public void onClearAllNotifications() {
        enforceStatusBarService();
        this.mNotificationCallbacks.onClearAll();
    }

    public IBinder addNotification(StatusBarNotification notification) {
        IBinder key;
        synchronized (this.mNotifications) {
            key = new Binder();
            this.mNotifications.put(key, notification);
            if (this.mBar != null) {
                try {
                    this.mBar.addNotification(key, notification);
                } catch (RemoteException e) {
                }
            }
        }
        return key;
    }

    public void updateNotification(IBinder key, StatusBarNotification notification) {
        synchronized (this.mNotifications) {
            if (!this.mNotifications.containsKey(key)) {
                throw new IllegalArgumentException("updateNotification key not found: " + key);
            }
            this.mNotifications.put(key, notification);
            if (this.mBar != null) {
                try {
                    this.mBar.updateNotification(key, notification);
                } catch (RemoteException e) {
                }
            }
        }
    }

    public void removeNotification(IBinder key) {
        synchronized (this.mNotifications) {
            StatusBarNotification n = this.mNotifications.remove(key);
            if (n == null) {
                Slog.e(TAG, "removeNotification key not found: " + key);
                return;
            }
            if (this.mBar != null) {
                try {
                    this.mBar.removeNotification(key);
                } catch (RemoteException e) {
                }
            }
        }
    }

    void manageDisableListLocked(int userId, int what, IBinder token, String pkg) {
        int N = this.mDisableRecords.size();
        DisableRecord tok = null;
        int i = 0;
        while (true) {
            if (i >= N) {
                break;
            }
            DisableRecord t = this.mDisableRecords.get(i);
            if (t.token != token || t.userId != userId) {
                i++;
            } else {
                tok = t;
                break;
            }
        }
        if (what == 0 || !token.isBinderAlive()) {
            if (tok != null) {
                this.mDisableRecords.remove(i);
                tok.token.unlinkToDeath(tok, 0);
                return;
            }
            return;
        }
        if (tok == null) {
            tok = new DisableRecord();
            tok.userId = userId;
            try {
                token.linkToDeath(tok, 0);
                this.mDisableRecords.add(tok);
            } catch (RemoteException e) {
                return;
            }
        }
        tok.what = what;
        tok.token = token;
        tok.pkg = pkg;
    }

    int gatherDisableActionsLocked(int userId) {
        int N = this.mDisableRecords.size();
        int net = 0;
        for (int i = 0; i < N; i++) {
            DisableRecord rec = this.mDisableRecords.get(i);
            if (rec.userId == userId) {
                net |= rec.what;
            }
        }
        return net;
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump StatusBar from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mIcons) {
            this.mIcons.dump(pw);
        }
        synchronized (this.mNotifications) {
            int i = 0;
            pw.println("Notification list:");
            for (Map.Entry<IBinder, StatusBarNotification> e : this.mNotifications.entrySet()) {
                pw.printf("  %2d: %s\n", Integer.valueOf(i), e.getValue().toString());
                i++;
            }
        }
        synchronized (this.mLock) {
            pw.println("  mDisabled=0x" + Integer.toHexString(this.mDisabled));
            int N = this.mDisableRecords.size();
            pw.println("  mDisableRecords.size=" + N);
            for (int i2 = 0; i2 < N; i2++) {
                DisableRecord tok = this.mDisableRecords.get(i2);
                pw.println("    [" + i2 + "] userId=" + tok.userId + " what=0x" + Integer.toHexString(tok.what) + " pkg=" + tok.pkg + " token=" + tok.token);
            }
        }
    }
}