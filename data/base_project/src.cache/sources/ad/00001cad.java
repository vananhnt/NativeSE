package com.android.server;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.INotificationManager;
import android.app.ITransientNotification;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.media.IAudioService;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.notification.INotificationListener;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AtomicFile;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.R;
import com.android.internal.notification.NotificationScorer;
import com.android.server.LightsService;
import com.android.server.StatusBarManagerService;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/* loaded from: NotificationManagerService.class */
public class NotificationManagerService extends INotificationManager.Stub {
    private static final String TAG = "NotificationService";
    private static final boolean DBG = false;
    private static final int MAX_PACKAGE_NOTIFICATIONS = 50;
    private static final int MESSAGE_TIMEOUT = 2;
    private static final int LONG_DELAY = 3500;
    private static final int SHORT_DELAY = 2000;
    private static final long[] DEFAULT_VIBRATE_PATTERN = {0, 250, 250, 250};
    private static final int VIBRATE_PATTERN_MAXLEN = 17;
    private static final int DEFAULT_STREAM_TYPE = 5;
    private static final boolean SCORE_ONGOING_HIGHER = false;
    private static final int JUNK_SCORE = -1000;
    private static final int NOTIFICATION_PRIORITY_MULTIPLIER = 10;
    private static final int SCORE_DISPLAY_THRESHOLD = -20;
    private static final int SCORE_INTERRUPTION_THRESHOLD = -10;
    private static final boolean ENABLE_BLOCKED_NOTIFICATIONS = true;
    private static final boolean ENABLE_BLOCKED_TOASTS = true;
    private static final String ENABLED_NOTIFICATION_LISTENERS_SEPARATOR = ":";
    final Context mContext;
    final UserManager mUserManager;
    private StatusBarManagerService mStatusBar;
    private LightsService.Light mNotificationLight;
    private LightsService.Light mAttentionLight;
    private int mDefaultNotificationColor;
    private int mDefaultNotificationLedOn;
    private int mDefaultNotificationLedOff;
    private long[] mDefaultVibrationPattern;
    private long[] mFallbackVibrationPattern;
    private boolean mSystemReady;
    private int mDisabledNotifications;
    private NotificationRecord mSoundNotification;
    private NotificationRecord mVibrateNotification;
    private IAudioService mAudioService;
    private Vibrator mVibrator;
    private boolean mNotificationPulseEnabled;
    private NotificationRecord mLedNotification;
    private final AppOpsManager mAppOps;
    private AtomicFile mPolicyFile;
    private static final int DB_VERSION = 1;
    private static final String TAG_BODY = "notification-policy";
    private static final String ATTR_VERSION = "version";
    private static final String TAG_BLOCKED_PKGS = "blocked-packages";
    private static final String TAG_PACKAGE = "package";
    private static final String ATTR_NAME = "name";
    private SettingsObserver mSettingsObserver;
    final IBinder mForegroundToken = new Binder();
    private boolean mScreenOn = true;
    private boolean mInCall = false;
    private final ArrayList<NotificationRecord> mNotificationList = new ArrayList<>();
    private ArrayList<NotificationRecord> mLights = new ArrayList<>();
    private ArrayList<NotificationListenerInfo> mListeners = new ArrayList<>();
    private ArrayList<String> mServicesBinding = new ArrayList<>();
    private HashSet<ComponentName> mEnabledListenersForCurrentUser = new HashSet<>();
    private HashSet<String> mEnabledListenerPackageNames = new HashSet<>();
    private HashSet<String> mBlockedPackages = new HashSet<>();
    private final ArrayList<NotificationScorer> mScorers = new ArrayList<>();
    Archive mArchive = new Archive();
    private StatusBarManagerService.NotificationCallbacks mNotificationCallbacks = new StatusBarManagerService.NotificationCallbacks() { // from class: com.android.server.NotificationManagerService.4
        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.4.onSetDisabled(int):void, file: NotificationManagerService$4.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // com.android.server.StatusBarManagerService.NotificationCallbacks
        public void onSetDisabled(int r1) {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.4.onSetDisabled(int):void, file: NotificationManagerService$4.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.AnonymousClass4.onSetDisabled(int):void");
        }

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.4.onPanelRevealed():void, file: NotificationManagerService$4.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // com.android.server.StatusBarManagerService.NotificationCallbacks
        public void onPanelRevealed() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.4.onPanelRevealed():void, file: NotificationManagerService$4.class
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.AnonymousClass4.onPanelRevealed():void");
        }

        @Override // com.android.server.StatusBarManagerService.NotificationCallbacks
        public void onClearAll() {
            NotificationManagerService.this.cancelAll(ActivityManager.getCurrentUser());
        }

        @Override // com.android.server.StatusBarManagerService.NotificationCallbacks
        public void onNotificationClick(String pkg, String tag, int id) {
            NotificationManagerService.this.cancelNotification(pkg, tag, id, 16, 64, false, ActivityManager.getCurrentUser());
        }

        @Override // com.android.server.StatusBarManagerService.NotificationCallbacks
        public void onNotificationClear(String pkg, String tag, int id) {
            NotificationManagerService.this.cancelNotification(pkg, tag, id, 0, 66, true, ActivityManager.getCurrentUser());
        }

        @Override // com.android.server.StatusBarManagerService.NotificationCallbacks
        public void onNotificationError(String pkg, String tag, int id, int uid, int initialPid, String message) {
            Slog.d(NotificationManagerService.TAG, "onNotification error pkg=" + pkg + " tag=" + tag + " id=" + id + "; will crashApplication(uid=" + uid + ", pid=" + initialPid + Separators.RPAREN);
            NotificationManagerService.this.cancelNotification(pkg, tag, id, 0, 0, false, UserHandle.getUserId(uid));
            long ident = Binder.clearCallingIdentity();
            try {
                ActivityManagerNative.getDefault().crashApplication(uid, initialPid, pkg, "Bad notification posted from package " + pkg + ": " + message);
            } catch (RemoteException e) {
            }
            Binder.restoreCallingIdentity(ident);
        }
    };
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.NotificationManagerService.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String pkgName;
            String[] pkgList;
            String action = intent.getAction();
            boolean queryRestart = false;
            boolean queryRemove = false;
            boolean packageChanged = false;
            boolean cancelNotifications = true;
            if (!action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                boolean equals = action.equals(Intent.ACTION_PACKAGE_REMOVED);
                queryRemove = equals;
                if (!equals && !action.equals(Intent.ACTION_PACKAGE_RESTARTED)) {
                    boolean equals2 = action.equals(Intent.ACTION_PACKAGE_CHANGED);
                    packageChanged = equals2;
                    if (!equals2) {
                        boolean equals3 = action.equals(Intent.ACTION_QUERY_PACKAGE_RESTART);
                        queryRestart = equals3;
                        if (!equals3 && !action.equals("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE")) {
                            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                                NotificationManagerService.this.mScreenOn = true;
                                return;
                            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                                NotificationManagerService.this.mScreenOn = false;
                                return;
                            } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                                NotificationManagerService.this.mInCall = intent.getStringExtra("state").equals(TelephonyManager.EXTRA_STATE_OFFHOOK);
                                NotificationManagerService.this.updateNotificationPulse();
                                return;
                            } else if (action.equals(Intent.ACTION_USER_STOPPED)) {
                                int userHandle = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
                                if (userHandle >= 0) {
                                    NotificationManagerService.this.cancelAllNotificationsInt(null, 0, 0, true, userHandle);
                                    return;
                                }
                                return;
                            } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
                                NotificationManagerService.this.mNotificationLight.turnOff();
                                return;
                            } else if (action.equals(Intent.ACTION_USER_SWITCHED)) {
                                NotificationManagerService.this.mSettingsObserver.update(null);
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
            boolean queryReplace = queryRemove && intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
            if (action.equals("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE")) {
                pkgList = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
            } else if (queryRestart) {
                pkgList = intent.getStringArrayExtra(Intent.EXTRA_PACKAGES);
            } else {
                Uri uri = intent.getData();
                if (uri == null || (pkgName = uri.getSchemeSpecificPart()) == null) {
                    return;
                }
                if (packageChanged) {
                    try {
                        int enabled = NotificationManagerService.this.mContext.getPackageManager().getApplicationEnabledSetting(pkgName);
                        cancelNotifications = (enabled == 1 || enabled == 0) ? false : false;
                    } catch (IllegalArgumentException e) {
                    }
                }
                pkgList = new String[]{pkgName};
            }
            boolean anyListenersInvolved = false;
            if (pkgList != null && pkgList.length > 0) {
                String[] arr$ = pkgList;
                for (String pkgName2 : arr$) {
                    if (cancelNotifications) {
                        NotificationManagerService.this.cancelAllNotificationsInt(pkgName2, 0, 0, !queryRestart, -1);
                    }
                    if (NotificationManagerService.this.mEnabledListenerPackageNames.contains(pkgName2)) {
                        anyListenersInvolved = true;
                    }
                }
            }
            if (anyListenersInvolved) {
                if (!queryReplace) {
                    NotificationManagerService.this.disableNonexistentListeners();
                }
                NotificationManagerService.this.rebindListenerServices();
            }
        }
    };
    final IActivityManager mAm = ActivityManagerNative.getDefault();
    private ArrayList<ToastRecord> mToastQueue = new ArrayList<>();
    private WorkerHandler mHandler = new WorkerHandler();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.loadBlockDb():void, file: NotificationManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void loadBlockDb() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.loadBlockDb():void, file: NotificationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.loadBlockDb():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.cancelAllNotificationsFromListener(android.service.notification.INotificationListener):void, file: NotificationManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.app.INotificationManager
    public void cancelAllNotificationsFromListener(android.service.notification.INotificationListener r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.cancelAllNotificationsFromListener(android.service.notification.INotificationListener):void, file: NotificationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.cancelAllNotificationsFromListener(android.service.notification.INotificationListener):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.cancelNotificationFromListener(android.service.notification.INotificationListener, java.lang.String, java.lang.String, int):void, file: NotificationManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.app.INotificationManager
    public void cancelNotificationFromListener(android.service.notification.INotificationListener r1, java.lang.String r2, java.lang.String r3, int r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.cancelNotificationFromListener(android.service.notification.INotificationListener, java.lang.String, java.lang.String, int):void, file: NotificationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.cancelNotificationFromListener(android.service.notification.INotificationListener, java.lang.String, java.lang.String, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.enqueueToast(java.lang.String, android.app.ITransientNotification, int):void, file: NotificationManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.app.INotificationManager
    public void enqueueToast(java.lang.String r1, android.app.ITransientNotification r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.enqueueToast(java.lang.String, android.app.ITransientNotification, int):void, file: NotificationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.enqueueToast(java.lang.String, android.app.ITransientNotification, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.cancelToast(java.lang.String, android.app.ITransientNotification):void, file: NotificationManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.app.INotificationManager
    public void cancelToast(java.lang.String r1, android.app.ITransientNotification r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.cancelToast(java.lang.String, android.app.ITransientNotification):void, file: NotificationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.cancelToast(java.lang.String, android.app.ITransientNotification):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.cancelNotificationLocked(com.android.server.NotificationManagerService$NotificationRecord, boolean):void, file: NotificationManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    /* JADX INFO: Access modifiers changed from: private */
    public void cancelNotificationLocked(com.android.server.NotificationManagerService.NotificationRecord r1, boolean r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.cancelNotificationLocked(com.android.server.NotificationManagerService$NotificationRecord, boolean):void, file: NotificationManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.cancelNotificationLocked(com.android.server.NotificationManagerService$NotificationRecord, boolean):void");
    }

    static /* synthetic */ ArrayList access$100(NotificationManagerService x0) {
        return x0.mNotificationList;
    }

    static /* synthetic */ int access$502(NotificationManagerService x0, int x1) {
        x0.mDisabledNotifications = x1;
        return x1;
    }

    static /* synthetic */ int access$500(NotificationManagerService x0) {
        return x0.mDisabledNotifications;
    }

    static /* synthetic */ IAudioService access$600(NotificationManagerService x0) {
        return x0.mAudioService;
    }

    static /* synthetic */ NotificationRecord access$902(NotificationManagerService x0, NotificationRecord x1) {
        x0.mSoundNotification = x1;
        return x1;
    }

    static /* synthetic */ int access$2300(int x0, int x1, int x2) {
        return clamp(x0, x1, x2);
    }

    static /* synthetic */ ArrayList access$2400(NotificationManagerService x0) {
        return x0.mScorers;
    }

    static /* synthetic */ boolean access$2500(NotificationManagerService x0, String x1, int x2) {
        return x0.noteNotificationOp(x1, x2);
    }

    static /* synthetic */ int access$2600(NotificationManagerService x0, String x1, String x2, int x3, int x4) {
        return x0.indexOfNotificationLocked(x1, x2, x3, x4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NotificationManagerService$NotificationListenerInfo.class */
    public class NotificationListenerInfo implements IBinder.DeathRecipient {
        INotificationListener listener;
        ComponentName component;
        int userid;
        boolean isSystem;
        ServiceConnection connection;

        public NotificationListenerInfo(INotificationListener listener, ComponentName component, int userid, boolean isSystem) {
            this.listener = listener;
            this.component = component;
            this.userid = userid;
            this.isSystem = isSystem;
            this.connection = null;
        }

        public NotificationListenerInfo(INotificationListener listener, ComponentName component, int userid, ServiceConnection connection) {
            this.listener = listener;
            this.component = component;
            this.userid = userid;
            this.isSystem = false;
            this.connection = connection;
        }

        boolean enabledAndUserMatches(StatusBarNotification sbn) {
            int nid = sbn.getUserId();
            if (isEnabledForCurrentUser()) {
                return this.userid == -1 || nid == -1 || nid == this.userid;
            }
            return false;
        }

        public void notifyPostedIfUserMatch(StatusBarNotification sbn) {
            if (!enabledAndUserMatches(sbn)) {
                return;
            }
            try {
                this.listener.onNotificationPosted(sbn);
            } catch (RemoteException ex) {
                Log.e(NotificationManagerService.TAG, "unable to notify listener (posted): " + this.listener, ex);
            }
        }

        public void notifyRemovedIfUserMatch(StatusBarNotification sbn) {
            if (enabledAndUserMatches(sbn)) {
                try {
                    this.listener.onNotificationRemoved(sbn);
                } catch (RemoteException ex) {
                    Log.e(NotificationManagerService.TAG, "unable to notify listener (removed): " + this.listener, ex);
                }
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            if (this.connection == null) {
                NotificationManagerService.this.unregisterListener(this.listener, this.userid);
            }
        }

        public boolean isEnabledForCurrentUser() {
            if (this.isSystem) {
                return true;
            }
            if (this.connection == null) {
                return false;
            }
            return NotificationManagerService.this.mEnabledListenersForCurrentUser.contains(this.component);
        }
    }

    /* loaded from: NotificationManagerService$Archive.class */
    private static class Archive {
        static final int BUFFER_SIZE = 250;
        ArrayDeque<StatusBarNotification> mBuffer = new ArrayDeque<>(250);

        public String toString() {
            StringBuilder sb = new StringBuilder();
            int N = this.mBuffer.size();
            sb.append("Archive (");
            sb.append(N);
            sb.append(" notification");
            sb.append(N == 1 ? Separators.RPAREN : "s)");
            return sb.toString();
        }

        public void record(StatusBarNotification nr) {
            if (this.mBuffer.size() == 250) {
                this.mBuffer.removeFirst();
            }
            this.mBuffer.addLast(nr.cloneLight());
        }

        public void clear() {
            this.mBuffer.clear();
        }

        public Iterator<StatusBarNotification> descendingIterator() {
            return this.mBuffer.descendingIterator();
        }

        public Iterator<StatusBarNotification> ascendingIterator() {
            return this.mBuffer.iterator();
        }

        public Iterator<StatusBarNotification> filter(final Iterator<StatusBarNotification> iter, final String pkg, final int userId) {
            return new Iterator<StatusBarNotification>() { // from class: com.android.server.NotificationManagerService.Archive.1
                StatusBarNotification mNext = findNext();

                private StatusBarNotification findNext() {
                    while (iter.hasNext()) {
                        StatusBarNotification nr = (StatusBarNotification) iter.next();
                        if (pkg == null || nr.getPackageName() == pkg) {
                            if (userId == -1 || nr.getUserId() == userId) {
                                return nr;
                            }
                        }
                    }
                    return null;
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.mNext == null;
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.Iterator
                public StatusBarNotification next() {
                    StatusBarNotification next = this.mNext;
                    if (next == null) {
                        throw new NoSuchElementException();
                    }
                    this.mNext = findNext();
                    return next;
                }

                @Override // java.util.Iterator
                public void remove() {
                    iter.remove();
                }
            };
        }

        public StatusBarNotification[] getArray(int count) {
            if (count == 0) {
                count = 250;
            }
            StatusBarNotification[] a = new StatusBarNotification[Math.min(count, this.mBuffer.size())];
            Iterator<StatusBarNotification> iter = descendingIterator();
            int i = 0;
            while (iter.hasNext() && i < count) {
                int i2 = i;
                i++;
                a[i2] = iter.next();
            }
            return a;
        }

        public StatusBarNotification[] getArray(int count, String pkg, int userId) {
            if (count == 0) {
                count = 250;
            }
            StatusBarNotification[] a = new StatusBarNotification[Math.min(count, this.mBuffer.size())];
            Iterator<StatusBarNotification> iter = filter(descendingIterator(), pkg, userId);
            int i = 0;
            while (iter.hasNext() && i < count) {
                int i2 = i;
                i++;
                a[i2] = iter.next();
            }
            return a;
        }
    }

    @Override // android.app.INotificationManager
    public boolean areNotificationsEnabledForPackage(String pkg, int uid) {
        checkCallerIsSystem();
        return this.mAppOps.checkOpNoThrow(11, uid, pkg) == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean noteNotificationOp(String pkg, int uid) {
        if (this.mAppOps.noteOpNoThrow(11, uid, pkg) != 0) {
            Slog.v(TAG, "notifications are disabled by AppOps for " + pkg);
            return false;
        }
        return true;
    }

    @Override // android.app.INotificationManager
    public void setNotificationsEnabledForPackage(String pkg, int uid, boolean enabled) {
        checkCallerIsSystem();
        Slog.v(TAG, (enabled ? "en" : "dis") + "abling notifications for " + pkg);
        this.mAppOps.setMode(11, uid, pkg, enabled ? 0 : 1);
        if (!enabled) {
            cancelAllNotificationsInt(pkg, 0, 0, true, UserHandle.getUserId(uid));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String idDebugString(Context baseContext, String packageName, int id) {
        Context c;
        if (packageName != null) {
            try {
                c = baseContext.createPackageContext(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                c = baseContext;
            }
        } else {
            c = baseContext;
        }
        Resources r = c.getResources();
        try {
            return r.getResourceName(id);
        } catch (Resources.NotFoundException e2) {
            return "<name unknown>";
        }
    }

    @Override // android.app.INotificationManager
    public StatusBarNotification[] getActiveNotifications(String callingPkg) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_NOTIFICATIONS, "NotificationManagerService.getActiveNotifications");
        StatusBarNotification[] tmp = null;
        int uid = Binder.getCallingUid();
        if (this.mAppOps.noteOpNoThrow(25, uid, callingPkg) == 0) {
            synchronized (this.mNotificationList) {
                tmp = new StatusBarNotification[this.mNotificationList.size()];
                int N = this.mNotificationList.size();
                for (int i = 0; i < N; i++) {
                    tmp[i] = this.mNotificationList.get(i).sbn;
                }
            }
        }
        return tmp;
    }

    @Override // android.app.INotificationManager
    public StatusBarNotification[] getHistoricalNotifications(String callingPkg, int count) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_NOTIFICATIONS, "NotificationManagerService.getHistoricalNotifications");
        StatusBarNotification[] tmp = null;
        int uid = Binder.getCallingUid();
        if (this.mAppOps.noteOpNoThrow(25, uid, callingPkg) == 0) {
            synchronized (this.mArchive) {
                tmp = this.mArchive.getArray(count);
            }
        }
        return tmp;
    }

    void disableNonexistentListeners() {
        int currentUser = ActivityManager.getCurrentUser();
        String flatIn = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), Settings.Secure.ENABLED_NOTIFICATION_LISTENERS, currentUser);
        if (!TextUtils.isEmpty(flatIn)) {
            PackageManager pm = this.mContext.getPackageManager();
            List<ResolveInfo> installedServices = pm.queryIntentServicesAsUser(new Intent(NotificationListenerService.SERVICE_INTERFACE), 132, currentUser);
            Set<ComponentName> installed = new HashSet<>();
            int count = installedServices.size();
            for (int i = 0; i < count; i++) {
                ResolveInfo resolveInfo = installedServices.get(i);
                ServiceInfo info = resolveInfo.serviceInfo;
                if (!Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE.equals(info.permission)) {
                    Slog.w(TAG, "Skipping notification listener service " + info.packageName + Separators.SLASH + info.name + ": it does not require the permission " + Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE);
                } else {
                    installed.add(new ComponentName(info.packageName, info.name));
                }
            }
            String flatOut = "";
            if (!installed.isEmpty()) {
                String[] enabled = flatIn.split(":");
                ArrayList<String> remaining = new ArrayList<>(enabled.length);
                for (int i2 = 0; i2 < enabled.length; i2++) {
                    ComponentName enabledComponent = ComponentName.unflattenFromString(enabled[i2]);
                    if (installed.contains(enabledComponent)) {
                        remaining.add(enabled[i2]);
                    }
                }
                flatOut = TextUtils.join(":", remaining);
            }
            if (!flatIn.equals(flatOut)) {
                Settings.Secure.putStringForUser(this.mContext.getContentResolver(), Settings.Secure.ENABLED_NOTIFICATION_LISTENERS, flatOut, currentUser);
            }
        }
    }

    void rebindListenerServices() {
        NotificationListenerInfo[] toRemove;
        ArrayList<ComponentName> toAdd;
        int currentUser = ActivityManager.getCurrentUser();
        String flat = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), Settings.Secure.ENABLED_NOTIFICATION_LISTENERS, currentUser);
        NotificationListenerInfo[] toRemove2 = new NotificationListenerInfo[this.mListeners.size()];
        synchronized (this.mNotificationList) {
            toRemove = (NotificationListenerInfo[]) this.mListeners.toArray(toRemove2);
            toAdd = new ArrayList<>();
            HashSet<ComponentName> newEnabled = new HashSet<>();
            HashSet<String> newPackages = new HashSet<>();
            if (flat != null) {
                String[] components = flat.split(":");
                for (String str : components) {
                    ComponentName component = ComponentName.unflattenFromString(str);
                    if (component != null) {
                        newEnabled.add(component);
                        toAdd.add(component);
                        newPackages.add(component.getPackageName());
                    }
                }
                this.mEnabledListenersForCurrentUser = newEnabled;
                this.mEnabledListenerPackageNames = newPackages;
            }
        }
        for (NotificationListenerInfo info : toRemove) {
            ComponentName component2 = info.component;
            int oldUser = info.userid;
            Slog.v(TAG, "disabling notification listener for user " + oldUser + ": " + component2);
            unregisterListenerService(component2, info.userid);
        }
        int N = toAdd.size();
        for (int i = 0; i < N; i++) {
            ComponentName component3 = toAdd.get(i);
            Slog.v(TAG, "enabling notification listener for user " + currentUser + ": " + component3);
            registerListenerService(component3, currentUser);
        }
    }

    @Override // android.app.INotificationManager
    public void registerListener(INotificationListener listener, ComponentName component, int userid) {
        checkCallerIsSystem();
        synchronized (this.mNotificationList) {
            try {
                NotificationListenerInfo info = new NotificationListenerInfo(listener, component, userid, true);
                listener.asBinder().linkToDeath(info, 0);
                this.mListeners.add(info);
            } catch (RemoteException e) {
            }
        }
    }

    private void registerListenerService(ComponentName name, final int userid) {
        checkCallerIsSystem();
        synchronized (this.mNotificationList) {
            final String servicesBindingTag = name.toString() + Separators.SLASH + userid;
            if (this.mServicesBinding.contains(servicesBindingTag)) {
                return;
            }
            this.mServicesBinding.add(servicesBindingTag);
            int N = this.mListeners.size();
            for (int i = N - 1; i >= 0; i--) {
                NotificationListenerInfo info = this.mListeners.get(i);
                if (name.equals(info.component) && info.userid == userid) {
                    this.mListeners.remove(i);
                    if (info.connection != null) {
                        this.mContext.unbindService(info.connection);
                    }
                }
            }
            Intent intent = new Intent(NotificationListenerService.SERVICE_INTERFACE);
            intent.setComponent(name);
            intent.putExtra(Intent.EXTRA_CLIENT_LABEL, R.string.notification_listener_binding_label);
            intent.putExtra(Intent.EXTRA_CLIENT_INTENT, PendingIntent.getActivity(this.mContext, 0, new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), 0));
            try {
                if (!this.mContext.bindServiceAsUser(intent, new ServiceConnection() { // from class: com.android.server.NotificationManagerService.1
                    INotificationListener mListener;

                    @Override // android.content.ServiceConnection
                    public void onServiceConnected(ComponentName name2, IBinder service) {
                        synchronized (NotificationManagerService.this.mNotificationList) {
                            NotificationManagerService.this.mServicesBinding.remove(servicesBindingTag);
                            try {
                                this.mListener = INotificationListener.Stub.asInterface(service);
                                NotificationListenerInfo info2 = new NotificationListenerInfo(this.mListener, name2, userid, this);
                                service.linkToDeath(info2, 0);
                                NotificationManagerService.this.mListeners.add(info2);
                            } catch (RemoteException e) {
                            }
                        }
                    }

                    @Override // android.content.ServiceConnection
                    public void onServiceDisconnected(ComponentName name2) {
                        Slog.v(NotificationManagerService.TAG, "notification listener connection lost: " + name2);
                    }
                }, 1, new UserHandle(userid))) {
                    this.mServicesBinding.remove(servicesBindingTag);
                    Slog.w(TAG, "Unable to bind listener service: " + intent);
                }
            } catch (SecurityException ex) {
                Slog.e(TAG, "Unable to bind listener service: " + intent, ex);
            }
        }
    }

    @Override // android.app.INotificationManager
    public void unregisterListener(INotificationListener listener, int userid) {
        synchronized (this.mNotificationList) {
            int N = this.mListeners.size();
            for (int i = N - 1; i >= 0; i--) {
                NotificationListenerInfo info = this.mListeners.get(i);
                if (info.listener.asBinder() == listener.asBinder() && info.userid == userid) {
                    this.mListeners.remove(i);
                    if (info.connection != null) {
                        this.mContext.unbindService(info.connection);
                    }
                }
            }
        }
    }

    private void unregisterListenerService(ComponentName name, int userid) {
        checkCallerIsSystem();
        synchronized (this.mNotificationList) {
            int N = this.mListeners.size();
            for (int i = N - 1; i >= 0; i--) {
                NotificationListenerInfo info = this.mListeners.get(i);
                if (name.equals(info.component) && info.userid == userid) {
                    this.mListeners.remove(i);
                    if (info.connection != null) {
                        try {
                            this.mContext.unbindService(info.connection);
                        } catch (IllegalArgumentException ex) {
                            Slog.e(TAG, "Listener " + name + " could not be unbound: " + ex);
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyPostedLocked(NotificationRecord n) {
        final StatusBarNotification sbn = n.sbn.m644clone();
        Iterator i$ = this.mListeners.iterator();
        while (i$.hasNext()) {
            final NotificationListenerInfo info = i$.next();
            this.mHandler.post(new Runnable() { // from class: com.android.server.NotificationManagerService.2
                @Override // java.lang.Runnable
                public void run() {
                    info.notifyPostedIfUserMatch(sbn);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyRemovedLocked(NotificationRecord n) {
        final StatusBarNotification sbn_light = n.sbn.cloneLight();
        Iterator i$ = this.mListeners.iterator();
        while (i$.hasNext()) {
            final NotificationListenerInfo info = i$.next();
            this.mHandler.post(new Runnable() { // from class: com.android.server.NotificationManagerService.3
                @Override // java.lang.Runnable
                public void run() {
                    info.notifyRemovedIfUserMatch(sbn_light);
                }
            });
        }
    }

    private NotificationListenerInfo checkListenerToken(INotificationListener listener) {
        IBinder token = listener.asBinder();
        int N = this.mListeners.size();
        for (int i = 0; i < N; i++) {
            NotificationListenerInfo info = this.mListeners.get(i);
            if (info.listener.asBinder() == token) {
                return info;
            }
        }
        throw new SecurityException("Disallowed call from unknown listener: " + listener);
    }

    @Override // android.app.INotificationManager
    public StatusBarNotification[] getActiveNotificationsFromListener(INotificationListener token) {
        NotificationListenerInfo info = checkListenerToken(token);
        StatusBarNotification[] result = new StatusBarNotification[0];
        ArrayList<StatusBarNotification> list = new ArrayList<>();
        synchronized (this.mNotificationList) {
            int N = this.mNotificationList.size();
            for (int i = 0; i < N; i++) {
                StatusBarNotification sbn = this.mNotificationList.get(i).sbn;
                if (info.enabledAndUserMatches(sbn)) {
                    list.add(sbn);
                }
            }
        }
        return (StatusBarNotification[]) list.toArray(result);
    }

    /* loaded from: NotificationManagerService$NotificationRecord.class */
    public static final class NotificationRecord {
        final StatusBarNotification sbn;
        IBinder statusBarKey;

        NotificationRecord(StatusBarNotification sbn) {
            this.sbn = sbn;
        }

        public Notification getNotification() {
            return this.sbn.getNotification();
        }

        public int getFlags() {
            return this.sbn.getNotification().flags;
        }

        public int getUserId() {
            return this.sbn.getUserId();
        }

        void dump(PrintWriter pw, String prefix, Context baseContext) {
            Notification notification = this.sbn.getNotification();
            pw.println(prefix + this);
            pw.println(prefix + "  uid=" + this.sbn.getUid() + " userId=" + this.sbn.getUserId());
            pw.println(prefix + "  icon=0x" + Integer.toHexString(notification.icon) + " / " + NotificationManagerService.idDebugString(baseContext, this.sbn.getPackageName(), notification.icon));
            pw.println(prefix + "  pri=" + notification.priority + " score=" + this.sbn.getScore());
            pw.println(prefix + "  contentIntent=" + notification.contentIntent);
            pw.println(prefix + "  deleteIntent=" + notification.deleteIntent);
            pw.println(prefix + "  tickerText=" + ((Object) notification.tickerText));
            pw.println(prefix + "  contentView=" + notification.contentView);
            pw.println(prefix + String.format("  defaults=0x%08x flags=0x%08x", Integer.valueOf(notification.defaults), Integer.valueOf(notification.flags)));
            pw.println(prefix + "  sound=" + notification.sound);
            pw.println(prefix + "  vibrate=" + Arrays.toString(notification.vibrate));
            pw.println(prefix + String.format("  led=0x%08x onMs=%d offMs=%d", Integer.valueOf(notification.ledARGB), Integer.valueOf(notification.ledOnMS), Integer.valueOf(notification.ledOffMS)));
            if (notification.actions != null && notification.actions.length > 0) {
                pw.println(prefix + "  actions={");
                int N = notification.actions.length;
                for (int i = 0; i < N; i++) {
                    Notification.Action action = notification.actions[i];
                    pw.println(String.format("%s    [%d] \"%s\" -> %s", prefix, Integer.valueOf(i), action.title, action.actionIntent.toString()));
                }
                pw.println(prefix + "  }");
            }
            if (notification.extras != null && notification.extras.size() > 0) {
                pw.println(prefix + "  extras={");
                for (String key : notification.extras.keySet()) {
                    pw.print(prefix + "    " + key + Separators.EQUALS);
                    Object val = notification.extras.get(key);
                    if (val == null) {
                        pw.println("null");
                    } else {
                        pw.print(val.toString());
                        if (val instanceof Bitmap) {
                            pw.print(String.format(" (%dx%d)", Integer.valueOf(((Bitmap) val).getWidth()), Integer.valueOf(((Bitmap) val).getHeight())));
                        } else if (val.getClass().isArray()) {
                            pw.println(" {");
                            int N2 = Array.getLength(val);
                            for (int i2 = 0; i2 < N2; i2++) {
                                if (i2 > 0) {
                                    pw.println(Separators.COMMA);
                                }
                                pw.print(prefix + "      " + Array.get(val, i2));
                            }
                            pw.print(Separators.RETURN + prefix + "    }");
                        }
                        pw.println();
                    }
                }
                pw.println(prefix + "  }");
            }
        }

        public final String toString() {
            return String.format("NotificationRecord(0x%08x: pkg=%s user=%s id=%d tag=%s score=%d: %s)", Integer.valueOf(System.identityHashCode(this)), this.sbn.getPackageName(), this.sbn.getUser(), Integer.valueOf(this.sbn.getId()), this.sbn.getTag(), Integer.valueOf(this.sbn.getScore()), this.sbn.getNotification());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NotificationManagerService$ToastRecord.class */
    public static final class ToastRecord {
        final int pid;
        final String pkg;
        final ITransientNotification callback;
        int duration;

        ToastRecord(int pid, String pkg, ITransientNotification callback, int duration) {
            this.pid = pid;
            this.pkg = pkg;
            this.callback = callback;
            this.duration = duration;
        }

        void update(int duration) {
            this.duration = duration;
        }

        void dump(PrintWriter pw, String prefix) {
            pw.println(prefix + this);
        }

        public final String toString() {
            return "ToastRecord{" + Integer.toHexString(System.identityHashCode(this)) + " pkg=" + this.pkg + " callback=" + this.callback + " duration=" + this.duration;
        }
    }

    /* loaded from: NotificationManagerService$SettingsObserver.class */
    class SettingsObserver extends ContentObserver {
        private final Uri NOTIFICATION_LIGHT_PULSE_URI;
        private final Uri ENABLED_NOTIFICATION_LISTENERS_URI;

        SettingsObserver(Handler handler) {
            super(handler);
            this.NOTIFICATION_LIGHT_PULSE_URI = Settings.System.getUriFor(Settings.System.NOTIFICATION_LIGHT_PULSE);
            this.ENABLED_NOTIFICATION_LISTENERS_URI = Settings.Secure.getUriFor(Settings.Secure.ENABLED_NOTIFICATION_LISTENERS);
        }

        void observe() {
            ContentResolver resolver = NotificationManagerService.this.mContext.getContentResolver();
            resolver.registerContentObserver(this.NOTIFICATION_LIGHT_PULSE_URI, false, this, -1);
            resolver.registerContentObserver(this.ENABLED_NOTIFICATION_LISTENERS_URI, false, this, -1);
            update(null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            update(uri);
        }

        public void update(Uri uri) {
            ContentResolver resolver = NotificationManagerService.this.mContext.getContentResolver();
            if (uri == null || this.NOTIFICATION_LIGHT_PULSE_URI.equals(uri)) {
                boolean pulseEnabled = Settings.System.getInt(resolver, Settings.System.NOTIFICATION_LIGHT_PULSE, 0) != 0;
                if (NotificationManagerService.this.mNotificationPulseEnabled != pulseEnabled) {
                    NotificationManagerService.this.mNotificationPulseEnabled = pulseEnabled;
                    NotificationManagerService.this.updateNotificationPulse();
                }
            }
            if (uri == null || this.ENABLED_NOTIFICATION_LISTENERS_URI.equals(uri)) {
                NotificationManagerService.this.rebindListenerServices();
            }
        }
    }

    static long[] getLongArray(Resources r, int resid, int maxlen, long[] def) {
        int[] ar = r.getIntArray(resid);
        if (ar == null) {
            return def;
        }
        int len = ar.length > maxlen ? maxlen : ar.length;
        long[] out = new long[len];
        for (int i = 0; i < len; i++) {
            out[i] = ar[i];
        }
        return out;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationManagerService(Context context, StatusBarManagerService statusBar, LightsService lights) {
        this.mContext = context;
        this.mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        importOldBlockDb();
        this.mStatusBar = statusBar;
        statusBar.setNotificationCallbacks(this.mNotificationCallbacks);
        this.mNotificationLight = lights.getLight(4);
        this.mAttentionLight = lights.getLight(5);
        Resources resources = this.mContext.getResources();
        this.mDefaultNotificationColor = resources.getColor(R.color.config_defaultNotificationColor);
        this.mDefaultNotificationLedOn = resources.getInteger(R.integer.config_defaultNotificationLedOn);
        this.mDefaultNotificationLedOff = resources.getInteger(R.integer.config_defaultNotificationLedOff);
        this.mDefaultVibrationPattern = getLongArray(resources, R.array.config_defaultNotificationVibePattern, 17, DEFAULT_VIBRATE_PATTERN);
        this.mFallbackVibrationPattern = getLongArray(resources, R.array.config_notificationFallbackVibePattern, 17, DEFAULT_VIBRATE_PATTERN);
        if (0 == Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0)) {
            this.mDisabledNotifications = 262144;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_USER_STOPPED);
        filter.addAction(Intent.ACTION_USER_SWITCHED);
        this.mContext.registerReceiver(this.mIntentReceiver, filter);
        IntentFilter pkgFilter = new IntentFilter();
        pkgFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        pkgFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        pkgFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        pkgFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        pkgFilter.addAction(Intent.ACTION_QUERY_PACKAGE_RESTART);
        pkgFilter.addDataScheme("package");
        this.mContext.registerReceiver(this.mIntentReceiver, pkgFilter);
        IntentFilter sdFilter = new IntentFilter("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        this.mContext.registerReceiver(this.mIntentReceiver, sdFilter);
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        this.mSettingsObserver.observe();
        String[] notificationScorerNames = resources.getStringArray(R.array.config_notificationScorers);
        for (String scorerName : notificationScorerNames) {
            try {
                Class<?> scorerClass = this.mContext.getClassLoader().loadClass(scorerName);
                NotificationScorer scorer = (NotificationScorer) scorerClass.newInstance();
                scorer.initialize(this.mContext);
                this.mScorers.add(scorer);
            } catch (ClassNotFoundException e) {
                Slog.w(TAG, "Couldn't find scorer " + scorerName + Separators.DOT, e);
            } catch (IllegalAccessException e2) {
                Slog.w(TAG, "Problem accessing scorer " + scorerName + Separators.DOT, e2);
            } catch (InstantiationException e3) {
                Slog.w(TAG, "Couldn't instantiate scorer " + scorerName + Separators.DOT, e3);
            }
        }
    }

    private void importOldBlockDb() {
        loadBlockDb();
        PackageManager pm = this.mContext.getPackageManager();
        Iterator i$ = this.mBlockedPackages.iterator();
        while (i$.hasNext()) {
            String pkg = i$.next();
            try {
                PackageInfo info = pm.getPackageInfo(pkg, 0);
                setNotificationsEnabledForPackage(pkg, info.applicationInfo.uid, false);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        this.mBlockedPackages.clear();
        if (this.mPolicyFile != null) {
            this.mPolicyFile.delete();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void systemReady() {
        this.mAudioService = IAudioService.Stub.asInterface(ServiceManager.getService(Context.AUDIO_SERVICE));
        this.mSystemReady = true;
        rebindListenerServices();
    }

    private void showNextToastLocked() {
        ToastRecord toastRecord = this.mToastQueue.get(0);
        while (true) {
            ToastRecord record = toastRecord;
            if (record != null) {
                try {
                    record.callback.show();
                    scheduleTimeoutLocked(record);
                    return;
                } catch (RemoteException e) {
                    Slog.w(TAG, "Object died trying to show notification " + record.callback + " in package " + record.pkg);
                    int index = this.mToastQueue.indexOf(record);
                    if (index >= 0) {
                        this.mToastQueue.remove(index);
                    }
                    keepProcessAliveLocked(record.pid);
                    if (this.mToastQueue.size() > 0) {
                        toastRecord = this.mToastQueue.get(0);
                    } else {
                        toastRecord = null;
                    }
                }
            } else {
                return;
            }
        }
    }

    private void cancelToastLocked(int index) {
        ToastRecord record = this.mToastQueue.get(index);
        try {
            record.callback.hide();
        } catch (RemoteException e) {
            Slog.w(TAG, "Object died trying to hide notification " + record.callback + " in package " + record.pkg);
        }
        this.mToastQueue.remove(index);
        keepProcessAliveLocked(record.pid);
        if (this.mToastQueue.size() > 0) {
            showNextToastLocked();
        }
    }

    private void scheduleTimeoutLocked(ToastRecord r) {
        this.mHandler.removeCallbacksAndMessages(r);
        Message m = Message.obtain(this.mHandler, 2, r);
        long delay = r.duration == 1 ? 3500L : 2000L;
        this.mHandler.sendMessageDelayed(m, delay);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTimeout(ToastRecord record) {
        synchronized (this.mToastQueue) {
            int index = indexOfToastLocked(record.pkg, record.callback);
            if (index >= 0) {
                cancelToastLocked(index);
            }
        }
    }

    private int indexOfToastLocked(String pkg, ITransientNotification callback) {
        IBinder cbak = callback.asBinder();
        ArrayList<ToastRecord> list = this.mToastQueue;
        int len = list.size();
        for (int i = 0; i < len; i++) {
            ToastRecord r = list.get(i);
            if (r.pkg.equals(pkg) && r.callback.asBinder() == cbak) {
                return i;
            }
        }
        return -1;
    }

    private void keepProcessAliveLocked(int pid) {
        int toastCount = 0;
        ArrayList<ToastRecord> list = this.mToastQueue;
        int N = list.size();
        for (int i = 0; i < N; i++) {
            ToastRecord r = list.get(i);
            if (r.pid == pid) {
                toastCount++;
            }
        }
        try {
            this.mAm.setProcessForeground(this.mForegroundToken, pid, toastCount > 0);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: NotificationManagerService$WorkerHandler.class */
    public final class WorkerHandler extends Handler {
        private WorkerHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    NotificationManagerService.this.handleTimeout((ToastRecord) msg.obj);
                    return;
                default:
                    return;
            }
        }
    }

    @Override // android.app.INotificationManager
    public void enqueueNotificationWithTag(String pkg, String basePkg, String tag, int id, Notification notification, int[] idOut, int userId) {
        enqueueNotificationInternal(pkg, basePkg, Binder.getCallingUid(), Binder.getCallingPid(), tag, id, notification, idOut, userId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final int clamp(int x, int low, int high) {
        return x < low ? low : x > high ? high : x;
    }

    public void enqueueNotificationInternal(final String pkg, String basePkg, final int callingUid, final int callingPid, final String tag, final int id, final Notification notification, int[] idOut, int incomingUserId) {
        checkCallerIsSystemOrSameApp(pkg);
        final boolean isSystemNotification = isUidSystem(callingUid) || "android".equals(pkg);
        final int userId = ActivityManager.handleIncomingUser(callingPid, callingUid, incomingUserId, true, false, "enqueueNotification", pkg);
        final UserHandle user = new UserHandle(userId);
        if (!isSystemNotification) {
            synchronized (this.mNotificationList) {
                int count = 0;
                int N = this.mNotificationList.size();
                for (int i = 0; i < N; i++) {
                    NotificationRecord r = this.mNotificationList.get(i);
                    if (r.sbn.getPackageName().equals(pkg) && r.sbn.getUserId() == userId) {
                        count++;
                        if (count >= 50) {
                            Slog.e(TAG, "Package has already posted " + count + " notifications.  Not showing more.  package=" + pkg);
                            return;
                        }
                    }
                }
            }
        }
        if (!pkg.equals("com.android.providers.downloads") || Log.isLoggable("DownloadManager", 2)) {
            EventLog.writeEvent((int) EventLogTags.NOTIFICATION_ENQUEUE, pkg, Integer.valueOf(id), tag, Integer.valueOf(userId), notification.toString());
        }
        if (pkg == null || notification == null) {
            throw new IllegalArgumentException("null not allowed: pkg=" + pkg + " id=" + id + " notification=" + notification);
        }
        if (notification.icon != 0 && notification.contentView == null) {
            throw new IllegalArgumentException("contentView required: pkg=" + pkg + " id=" + id + " notification=" + notification);
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.NotificationManagerService.6
            /*  JADX ERROR: Method load error
                jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.6.run():void, file: NotificationManagerService$6.class
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
                	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
                	at jadx.core.ProcessClass.process(ProcessClass.java:67)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
                Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
                	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
                	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
                	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
                	... 6 more
                */
            @Override // java.lang.Runnable
            public void run() {
                /*
                // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.NotificationManagerService.6.run():void, file: NotificationManagerService$6.class
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.AnonymousClass6.run():void");
            }
        });
        idOut[0] = id;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAccessibilityEvent(Notification notification, CharSequence packageName) {
        AccessibilityManager manager = AccessibilityManager.getInstance(this.mContext);
        if (!manager.isEnabled()) {
            return;
        }
        AccessibilityEvent event = AccessibilityEvent.obtain(64);
        event.setPackageName(packageName);
        event.setClassName(Notification.class.getName());
        event.setParcelableData(notification);
        CharSequence tickerText = notification.tickerText;
        if (!TextUtils.isEmpty(tickerText)) {
            event.getText().add(tickerText);
        }
        manager.sendAccessibilityEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelNotification(final String pkg, final String tag, final int id, final int mustHaveFlags, final int mustNotHaveFlags, final boolean sendDelete, final int userId) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.NotificationManagerService.7
            @Override // java.lang.Runnable
            public void run() {
                EventLog.writeEvent((int) EventLogTags.NOTIFICATION_CANCEL, pkg, Integer.valueOf(id), tag, Integer.valueOf(userId), Integer.valueOf(mustHaveFlags), Integer.valueOf(mustNotHaveFlags));
                synchronized (NotificationManagerService.this.mNotificationList) {
                    int index = NotificationManagerService.this.indexOfNotificationLocked(pkg, tag, id, userId);
                    if (index >= 0) {
                        NotificationRecord r = (NotificationRecord) NotificationManagerService.this.mNotificationList.get(index);
                        if ((r.getNotification().flags & mustHaveFlags) != mustHaveFlags) {
                            return;
                        }
                        if ((r.getNotification().flags & mustNotHaveFlags) != 0) {
                            return;
                        }
                        NotificationManagerService.this.mNotificationList.remove(index);
                        NotificationManagerService.this.cancelNotificationLocked(r, sendDelete);
                        NotificationManagerService.this.updateLightsLocked();
                    }
                }
            }
        });
    }

    private boolean notificationMatchesUserId(NotificationRecord r, int userId) {
        return userId == -1 || r.getUserId() == -1 || r.getUserId() == userId;
    }

    boolean cancelAllNotificationsInt(String pkg, int mustHaveFlags, int mustNotHaveFlags, boolean doit, int userId) {
        EventLog.writeEvent((int) EventLogTags.NOTIFICATION_CANCEL_ALL, pkg, Integer.valueOf(userId), Integer.valueOf(mustHaveFlags), Integer.valueOf(mustNotHaveFlags));
        synchronized (this.mNotificationList) {
            int N = this.mNotificationList.size();
            boolean canceledSomething = false;
            for (int i = N - 1; i >= 0; i--) {
                NotificationRecord r = this.mNotificationList.get(i);
                if (notificationMatchesUserId(r, userId) && ((r.getUserId() != -1 || pkg != null) && (r.getFlags() & mustHaveFlags) == mustHaveFlags && (r.getFlags() & mustNotHaveFlags) == 0 && (pkg == null || r.sbn.getPackageName().equals(pkg)))) {
                    canceledSomething = true;
                    if (!doit) {
                        return true;
                    }
                    this.mNotificationList.remove(i);
                    cancelNotificationLocked(r, false);
                }
            }
            if (canceledSomething) {
                updateLightsLocked();
            }
            return canceledSomething;
        }
    }

    @Override // android.app.INotificationManager
    public void cancelNotificationWithTag(String pkg, String tag, int id, int userId) {
        checkCallerIsSystemOrSameApp(pkg);
        cancelNotification(pkg, tag, id, 0, Binder.getCallingUid() == 1000 ? 0 : 64, false, ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, true, false, "cancelNotificationWithTag", pkg));
    }

    @Override // android.app.INotificationManager
    public void cancelAllNotifications(String pkg, int userId) {
        checkCallerIsSystemOrSameApp(pkg);
        cancelAllNotificationsInt(pkg, 0, 64, true, ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, true, false, "cancelAllNotifications", pkg));
    }

    boolean isUidSystem(int uid) {
        int appid = UserHandle.getAppId(uid);
        return appid == 1000 || appid == 1001 || uid == 0;
    }

    boolean isCallerSystem() {
        return isUidSystem(Binder.getCallingUid());
    }

    void checkCallerIsSystem() {
        if (isCallerSystem()) {
            return;
        }
        throw new SecurityException("Disallowed call for uid " + Binder.getCallingUid());
    }

    void checkCallerIsSystemOrSameApp(String pkg) {
        if (isCallerSystem()) {
            return;
        }
        int uid = Binder.getCallingUid();
        try {
            ApplicationInfo ai = AppGlobals.getPackageManager().getApplicationInfo(pkg, 0, UserHandle.getCallingUserId());
            if (!UserHandle.isSameApp(ai.uid, uid)) {
                throw new SecurityException("Calling uid " + uid + " gave package" + pkg + " which is owned by uid " + ai.uid);
            }
        } catch (RemoteException re) {
            throw new SecurityException("Unknown package " + pkg + Separators.RETURN + re);
        }
    }

    void cancelAll(int userId) {
        synchronized (this.mNotificationList) {
            int N = this.mNotificationList.size();
            for (int i = N - 1; i >= 0; i--) {
                NotificationRecord r = this.mNotificationList.get(i);
                if (notificationMatchesUserId(r, userId) && (r.getFlags() & 34) == 0) {
                    this.mNotificationList.remove(i);
                    cancelNotificationLocked(r, true);
                }
            }
            updateLightsLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLightsLocked() {
        int n;
        if (this.mLedNotification == null && (n = this.mLights.size()) > 0) {
            this.mLedNotification = this.mLights.get(n - 1);
        }
        if (this.mLedNotification == null || this.mInCall || this.mScreenOn) {
            this.mNotificationLight.turnOff();
            return;
        }
        Notification ledno = this.mLedNotification.sbn.getNotification();
        int ledARGB = ledno.ledARGB;
        int ledOnMS = ledno.ledOnMS;
        int ledOffMS = ledno.ledOffMS;
        if ((ledno.defaults & 4) != 0) {
            ledARGB = this.mDefaultNotificationColor;
            ledOnMS = this.mDefaultNotificationLedOn;
            ledOffMS = this.mDefaultNotificationLedOff;
        }
        if (this.mNotificationPulseEnabled) {
            this.mNotificationLight.setFlashing(ledARGB, 1, ledOnMS, ledOffMS);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0070 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0073 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int indexOfNotificationLocked(java.lang.String r5, java.lang.String r6, int r7, int r8) {
        /*
            r4 = this;
            r0 = r4
            java.util.ArrayList<com.android.server.NotificationManagerService$NotificationRecord> r0 = r0.mNotificationList
            r9 = r0
            r0 = r9
            int r0 = r0.size()
            r10 = r0
            r0 = 0
            r11 = r0
        L10:
            r0 = r11
            r1 = r10
            if (r0 >= r1) goto L79
            r0 = r9
            r1 = r11
            java.lang.Object r0 = r0.get(r1)
            com.android.server.NotificationManagerService$NotificationRecord r0 = (com.android.server.NotificationManagerService.NotificationRecord) r0
            r12 = r0
            r0 = r4
            r1 = r12
            r2 = r8
            boolean r0 = r0.notificationMatchesUserId(r1, r2)
            if (r0 == 0) goto L73
            r0 = r12
            android.service.notification.StatusBarNotification r0 = r0.sbn
            int r0 = r0.getId()
            r1 = r7
            if (r0 == r1) goto L3d
            goto L73
        L3d:
            r0 = r6
            if (r0 != 0) goto L4f
            r0 = r12
            android.service.notification.StatusBarNotification r0 = r0.sbn
            java.lang.String r0 = r0.getTag()
            if (r0 == 0) goto L61
            goto L73
        L4f:
            r0 = r6
            r1 = r12
            android.service.notification.StatusBarNotification r1 = r1.sbn
            java.lang.String r1 = r1.getTag()
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L61
            goto L73
        L61:
            r0 = r12
            android.service.notification.StatusBarNotification r0 = r0.sbn
            java.lang.String r0 = r0.getPackageName()
            r1 = r5
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L73
            r0 = r11
            return r0
        L73:
            int r11 = r11 + 1
            goto L10
        L79:
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NotificationManagerService.indexOfNotificationLocked(java.lang.String, java.lang.String, int, int):int");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotificationPulse() {
        synchronized (this.mNotificationList) {
            updateLightsLocked();
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump NotificationManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("Current Notification Manager state:");
        pw.println("  Listeners (" + this.mEnabledListenersForCurrentUser.size() + ") enabled for current user:");
        Iterator i$ = this.mEnabledListenersForCurrentUser.iterator();
        while (i$.hasNext()) {
            ComponentName cmpt = i$.next();
            pw.println("    " + cmpt);
        }
        pw.println("  Live listeners (" + this.mListeners.size() + "):");
        Iterator i$2 = this.mListeners.iterator();
        while (i$2.hasNext()) {
            NotificationListenerInfo info = i$2.next();
            pw.println("    " + info.component + " (user " + info.userid + "): " + info.listener + (info.isSystem ? " SYSTEM" : ""));
        }
        synchronized (this.mToastQueue) {
            int N = this.mToastQueue.size();
            if (N > 0) {
                pw.println("  Toast Queue:");
                for (int i = 0; i < N; i++) {
                    this.mToastQueue.get(i).dump(pw, "    ");
                }
                pw.println("  ");
            }
        }
        synchronized (this.mNotificationList) {
            int N2 = this.mNotificationList.size();
            if (N2 > 0) {
                pw.println("  Notification List:");
                for (int i2 = 0; i2 < N2; i2++) {
                    this.mNotificationList.get(i2).dump(pw, "    ", this.mContext);
                }
                pw.println("  ");
            }
            int N3 = this.mLights.size();
            if (N3 > 0) {
                pw.println("  Lights List:");
                for (int i3 = 0; i3 < N3; i3++) {
                    pw.println("    " + this.mLights.get(i3));
                }
                pw.println("  ");
            }
            pw.println("  mSoundNotification=" + this.mSoundNotification);
            pw.println("  mVibrateNotification=" + this.mVibrateNotification);
            pw.println("  mDisabledNotifications=0x" + Integer.toHexString(this.mDisabledNotifications));
            pw.println("  mSystemReady=" + this.mSystemReady);
            pw.println("  mArchive=" + this.mArchive.toString());
            Iterator<StatusBarNotification> iter = this.mArchive.descendingIterator();
            int i4 = 0;
            while (true) {
                if (!iter.hasNext()) {
                    break;
                }
                pw.println("    " + iter.next());
                i4++;
                if (i4 >= 5) {
                    if (iter.hasNext()) {
                        pw.println("    ...");
                    }
                }
            }
        }
    }
}