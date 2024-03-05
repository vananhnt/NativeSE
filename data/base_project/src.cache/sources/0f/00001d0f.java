package com.android.server;

import android.Manifest;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.IUserSwitchObserver;
import android.app.IWallpaperManager;
import android.app.IWallpaperManagerCallback;
import android.app.WallpaperInfo;
import android.app.backup.BackupManager;
import android.app.backup.WallpaperBackupHelper;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.nfc.cardemulation.CardEmulation;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.FileUtils;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.service.wallpaper.IWallpaperConnection;
import android.service.wallpaper.IWallpaperEngine;
import android.service.wallpaper.IWallpaperService;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import android.view.Display;
import android.view.IWindowManager;
import android.view.WindowManager;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.JournaledFile;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: WallpaperManagerService.class */
public class WallpaperManagerService extends IWallpaperManager.Stub {
    static final String TAG = "WallpaperService";
    static final boolean DEBUG = false;
    static final long MIN_WALLPAPER_CRASH_TIME = 10000;
    static final String WALLPAPER = "wallpaper";
    static final String WALLPAPER_INFO = "wallpaper_info.xml";
    static final ComponentName IMAGE_WALLPAPER = new ComponentName("com.android.systemui", "com.android.systemui.ImageWallpaper");
    final Context mContext;
    WallpaperData mLastWallpaper;
    int mCurrentUserId;
    final Object mLock = new Object[0];
    SparseArray<WallpaperData> mWallpaperMap = new SparseArray<>();
    final IWindowManager mIWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
    final IPackageManager mIPackageManager = AppGlobals.getPackageManager();
    final MyPackageMonitor mMonitor = new MyPackageMonitor();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.clearWallpaperLocked(boolean, int, android.os.IRemoteCallback):void, file: WallpaperManagerService.class
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
    void clearWallpaperLocked(boolean r1, int r2, android.os.IRemoteCallback r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.clearWallpaperLocked(boolean, int, android.os.IRemoteCallback):void, file: WallpaperManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.WallpaperManagerService.clearWallpaperLocked(boolean, int, android.os.IRemoteCallback):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.hasNamedWallpaper(java.lang.String):boolean, file: WallpaperManagerService.class
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
    @Override // android.app.IWallpaperManager
    public boolean hasNamedWallpaper(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.hasNamedWallpaper(java.lang.String):boolean, file: WallpaperManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.WallpaperManagerService.hasNamedWallpaper(java.lang.String):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.setWallpaper(java.lang.String):android.os.ParcelFileDescriptor, file: WallpaperManagerService.class
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
    @Override // android.app.IWallpaperManager
    public android.os.ParcelFileDescriptor setWallpaper(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.setWallpaper(java.lang.String):android.os.ParcelFileDescriptor, file: WallpaperManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.WallpaperManagerService.setWallpaper(java.lang.String):android.os.ParcelFileDescriptor");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.setWallpaperComponent(android.content.ComponentName):void, file: WallpaperManagerService.class
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
    @Override // android.app.IWallpaperManager
    public void setWallpaperComponent(android.content.ComponentName r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.setWallpaperComponent(android.content.ComponentName):void, file: WallpaperManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.WallpaperManagerService.setWallpaperComponent(android.content.ComponentName):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.restoreNamedResourceLocked(com.android.server.WallpaperManagerService$WallpaperData):boolean, file: WallpaperManagerService.class
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
    boolean restoreNamedResourceLocked(com.android.server.WallpaperManagerService.WallpaperData r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.WallpaperManagerService.restoreNamedResourceLocked(com.android.server.WallpaperManagerService$WallpaperData):boolean, file: WallpaperManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.WallpaperManagerService.restoreNamedResourceLocked(com.android.server.WallpaperManagerService$WallpaperData):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: WallpaperManagerService$WallpaperObserver.class */
    public class WallpaperObserver extends FileObserver {
        final WallpaperData mWallpaper;
        final File mWallpaperDir;
        final File mWallpaperFile;

        public WallpaperObserver(WallpaperData wallpaper) {
            super(WallpaperManagerService.getWallpaperDir(wallpaper.userId).getAbsolutePath(), 1544);
            this.mWallpaperDir = WallpaperManagerService.getWallpaperDir(wallpaper.userId);
            this.mWallpaper = wallpaper;
            this.mWallpaperFile = new File(this.mWallpaperDir, "wallpaper");
        }

        @Override // android.os.FileObserver
        public void onEvent(int event, String path) {
            if (path == null) {
                return;
            }
            synchronized (WallpaperManagerService.this.mLock) {
                long origId = Binder.clearCallingIdentity();
                BackupManager bm = new BackupManager(WallpaperManagerService.this.mContext);
                bm.dataChanged();
                Binder.restoreCallingIdentity(origId);
                File changedFile = new File(this.mWallpaperDir, path);
                if (this.mWallpaperFile.equals(changedFile)) {
                    WallpaperManagerService.this.notifyCallbacksLocked(this.mWallpaper);
                    if (this.mWallpaper.wallpaperComponent == null || event != 8 || this.mWallpaper.imageWallpaperPending) {
                        if (event == 8) {
                            this.mWallpaper.imageWallpaperPending = false;
                        }
                        WallpaperManagerService.this.bindWallpaperComponentLocked(WallpaperManagerService.IMAGE_WALLPAPER, true, false, this.mWallpaper, null);
                        WallpaperManagerService.this.saveSettingsLocked(this.mWallpaper);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WallpaperManagerService$WallpaperData.class */
    public static class WallpaperData {
        int userId;
        File wallpaperFile;
        boolean imageWallpaperPending;
        ComponentName wallpaperComponent;
        ComponentName nextWallpaperComponent;
        WallpaperConnection connection;
        long lastDiedTime;
        boolean wallpaperUpdating;
        WallpaperObserver wallpaperObserver;
        String name = "";
        private RemoteCallbackList<IWallpaperManagerCallback> callbacks = new RemoteCallbackList<>();
        int width = -1;
        int height = -1;

        WallpaperData(int userId) {
            this.userId = userId;
            this.wallpaperFile = new File(WallpaperManagerService.getWallpaperDir(userId), "wallpaper");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: WallpaperManagerService$WallpaperConnection.class */
    public class WallpaperConnection extends IWallpaperConnection.Stub implements ServiceConnection {
        final WallpaperInfo mInfo;
        IWallpaperService mService;
        IWallpaperEngine mEngine;
        WallpaperData mWallpaper;
        IRemoteCallback mReply;
        final Binder mToken = new Binder();
        boolean mDimensionsChanged = false;

        public WallpaperConnection(WallpaperInfo info, WallpaperData wallpaper) {
            this.mInfo = info;
            this.mWallpaper = wallpaper;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (this.mWallpaper.connection == this) {
                    this.mWallpaper.lastDiedTime = SystemClock.uptimeMillis();
                    this.mService = IWallpaperService.Stub.asInterface(service);
                    WallpaperManagerService.this.attachServiceLocked(this, this.mWallpaper);
                    WallpaperManagerService.this.saveSettingsLocked(this.mWallpaper);
                }
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            synchronized (WallpaperManagerService.this.mLock) {
                this.mService = null;
                this.mEngine = null;
                if (this.mWallpaper.connection == this) {
                    Slog.w(WallpaperManagerService.TAG, "Wallpaper service gone: " + this.mWallpaper.wallpaperComponent);
                    if (!this.mWallpaper.wallpaperUpdating && this.mWallpaper.lastDiedTime + WallpaperManagerService.MIN_WALLPAPER_CRASH_TIME > SystemClock.uptimeMillis() && this.mWallpaper.userId == WallpaperManagerService.this.mCurrentUserId) {
                        Slog.w(WallpaperManagerService.TAG, "Reverting to built-in wallpaper!");
                        WallpaperManagerService.this.clearWallpaperLocked(true, this.mWallpaper.userId, null);
                    }
                }
            }
        }

        @Override // android.service.wallpaper.IWallpaperConnection
        public void attachEngine(IWallpaperEngine engine) {
            synchronized (WallpaperManagerService.this.mLock) {
                this.mEngine = engine;
                if (this.mDimensionsChanged) {
                    try {
                        this.mEngine.setDesiredSize(this.mWallpaper.width, this.mWallpaper.height);
                    } catch (RemoteException e) {
                        Slog.w(WallpaperManagerService.TAG, "Failed to set wallpaper dimensions", e);
                    }
                    this.mDimensionsChanged = false;
                }
            }
        }

        @Override // android.service.wallpaper.IWallpaperConnection
        public void engineShown(IWallpaperEngine engine) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (this.mReply != null) {
                    long ident = Binder.clearCallingIdentity();
                    try {
                        this.mReply.sendResult(null);
                    } catch (RemoteException e) {
                        Binder.restoreCallingIdentity(ident);
                    }
                    this.mReply = null;
                }
            }
        }

        @Override // android.service.wallpaper.IWallpaperConnection
        public ParcelFileDescriptor setWallpaper(String name) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (this.mWallpaper.connection == this) {
                    return WallpaperManagerService.this.updateWallpaperBitmapLocked(name, this.mWallpaper);
                }
                return null;
            }
        }
    }

    /* loaded from: WallpaperManagerService$MyPackageMonitor.class */
    class MyPackageMonitor extends PackageMonitor {
        MyPackageMonitor() {
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onPackageUpdateFinished(String packageName, int uid) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return;
                }
                WallpaperData wallpaper = WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                if (wallpaper != null && wallpaper.wallpaperComponent != null && wallpaper.wallpaperComponent.getPackageName().equals(packageName)) {
                    wallpaper.wallpaperUpdating = false;
                    ComponentName comp = wallpaper.wallpaperComponent;
                    WallpaperManagerService.this.clearWallpaperComponentLocked(wallpaper);
                    if (!WallpaperManagerService.this.bindWallpaperComponentLocked(comp, false, false, wallpaper, null)) {
                        Slog.w(WallpaperManagerService.TAG, "Wallpaper no longer available; reverting to default");
                        WallpaperManagerService.this.clearWallpaperLocked(false, wallpaper.userId, null);
                    }
                }
            }
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onPackageModified(String packageName) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return;
                }
                WallpaperData wallpaper = WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                if (wallpaper != null) {
                    if (wallpaper.wallpaperComponent == null || !wallpaper.wallpaperComponent.getPackageName().equals(packageName)) {
                        return;
                    }
                    doPackagesChangedLocked(true, wallpaper);
                }
            }
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onPackageUpdateStarted(String packageName, int uid) {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return;
                }
                WallpaperData wallpaper = WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                if (wallpaper != null && wallpaper.wallpaperComponent != null && wallpaper.wallpaperComponent.getPackageName().equals(packageName)) {
                    wallpaper.wallpaperUpdating = true;
                }
            }
        }

        @Override // com.android.internal.content.PackageMonitor
        public boolean onHandleForceStop(Intent intent, String[] packages, int uid, boolean doit) {
            synchronized (WallpaperManagerService.this.mLock) {
                boolean changed = false;
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return false;
                }
                WallpaperData wallpaper = WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                if (wallpaper != null) {
                    boolean res = doPackagesChangedLocked(doit, wallpaper);
                    changed = false | res;
                }
                return changed;
            }
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onSomePackagesChanged() {
            synchronized (WallpaperManagerService.this.mLock) {
                if (WallpaperManagerService.this.mCurrentUserId != getChangingUserId()) {
                    return;
                }
                WallpaperData wallpaper = WallpaperManagerService.this.mWallpaperMap.get(WallpaperManagerService.this.mCurrentUserId);
                if (wallpaper != null) {
                    doPackagesChangedLocked(true, wallpaper);
                }
            }
        }

        boolean doPackagesChangedLocked(boolean doit, WallpaperData wallpaper) {
            int change;
            int change2;
            boolean changed = false;
            if (wallpaper.wallpaperComponent != null && ((change2 = isPackageDisappearing(wallpaper.wallpaperComponent.getPackageName())) == 3 || change2 == 2)) {
                changed = true;
                if (doit) {
                    Slog.w(WallpaperManagerService.TAG, "Wallpaper uninstalled, removing: " + wallpaper.wallpaperComponent);
                    WallpaperManagerService.this.clearWallpaperLocked(false, wallpaper.userId, null);
                }
            }
            if (wallpaper.nextWallpaperComponent != null && ((change = isPackageDisappearing(wallpaper.nextWallpaperComponent.getPackageName())) == 3 || change == 2)) {
                wallpaper.nextWallpaperComponent = null;
            }
            if (wallpaper.wallpaperComponent != null && isPackageModified(wallpaper.wallpaperComponent.getPackageName())) {
                try {
                    WallpaperManagerService.this.mContext.getPackageManager().getServiceInfo(wallpaper.wallpaperComponent, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.w(WallpaperManagerService.TAG, "Wallpaper component gone, removing: " + wallpaper.wallpaperComponent);
                    WallpaperManagerService.this.clearWallpaperLocked(false, wallpaper.userId, null);
                }
            }
            if (wallpaper.nextWallpaperComponent != null && isPackageModified(wallpaper.nextWallpaperComponent.getPackageName())) {
                try {
                    WallpaperManagerService.this.mContext.getPackageManager().getServiceInfo(wallpaper.nextWallpaperComponent, 0);
                } catch (PackageManager.NameNotFoundException e2) {
                    wallpaper.nextWallpaperComponent = null;
                }
            }
            return changed;
        }
    }

    public WallpaperManagerService(Context context) {
        this.mContext = context;
        this.mMonitor.register(context, null, UserHandle.ALL, true);
        getWallpaperDir(0).mkdirs();
        loadSettingsLocked(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static File getWallpaperDir(int userId) {
        return Environment.getUserSystemDirectory(userId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.Binder
    public void finalize() throws Throwable {
        super.finalize();
        for (int i = 0; i < this.mWallpaperMap.size(); i++) {
            WallpaperData wallpaper = this.mWallpaperMap.valueAt(i);
            wallpaper.wallpaperObserver.stopWatching();
        }
    }

    public void systemRunning() {
        WallpaperData wallpaper = this.mWallpaperMap.get(0);
        switchWallpaper(wallpaper, null);
        wallpaper.wallpaperObserver = new WallpaperObserver(wallpaper);
        wallpaper.wallpaperObserver.startWatching();
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(Intent.ACTION_USER_REMOVED);
        userFilter.addAction(Intent.ACTION_USER_STOPPING);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.WallpaperManagerService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_USER_REMOVED.equals(action)) {
                    WallpaperManagerService.this.onRemoveUser(intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -10000));
                }
            }
        }, userFilter);
        try {
            ActivityManagerNative.getDefault().registerUserSwitchObserver(new IUserSwitchObserver.Stub() { // from class: com.android.server.WallpaperManagerService.2
                @Override // android.app.IUserSwitchObserver
                public void onUserSwitching(int newUserId, IRemoteCallback reply) {
                    WallpaperManagerService.this.switchUser(newUserId, reply);
                }

                @Override // android.app.IUserSwitchObserver
                public void onUserSwitchComplete(int newUserId) throws RemoteException {
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getName() {
        String str;
        synchronized (this.mLock) {
            str = this.mWallpaperMap.get(0).name;
        }
        return str;
    }

    void onStoppingUser(int userId) {
        if (userId < 1) {
            return;
        }
        synchronized (this.mLock) {
            WallpaperData wallpaper = this.mWallpaperMap.get(userId);
            if (wallpaper != null) {
                if (wallpaper.wallpaperObserver != null) {
                    wallpaper.wallpaperObserver.stopWatching();
                    wallpaper.wallpaperObserver = null;
                }
                this.mWallpaperMap.remove(userId);
            }
        }
    }

    void onRemoveUser(int userId) {
        if (userId < 1) {
            return;
        }
        synchronized (this.mLock) {
            onStoppingUser(userId);
            File wallpaperFile = new File(getWallpaperDir(userId), "wallpaper");
            wallpaperFile.delete();
            File wallpaperInfoFile = new File(getWallpaperDir(userId), WALLPAPER_INFO);
            wallpaperInfoFile.delete();
        }
    }

    void switchUser(int userId, IRemoteCallback reply) {
        synchronized (this.mLock) {
            this.mCurrentUserId = userId;
            WallpaperData wallpaper = this.mWallpaperMap.get(userId);
            if (wallpaper == null) {
                wallpaper = new WallpaperData(userId);
                this.mWallpaperMap.put(userId, wallpaper);
                loadSettingsLocked(userId);
            }
            if (wallpaper.wallpaperObserver == null) {
                wallpaper.wallpaperObserver = new WallpaperObserver(wallpaper);
                wallpaper.wallpaperObserver.startWatching();
            }
            switchWallpaper(wallpaper, reply);
        }
    }

    void switchWallpaper(WallpaperData wallpaper, IRemoteCallback reply) {
        ComponentName cname;
        synchronized (this.mLock) {
            RuntimeException e = null;
            try {
                cname = wallpaper.wallpaperComponent != null ? wallpaper.wallpaperComponent : wallpaper.nextWallpaperComponent;
            } catch (RuntimeException e1) {
                e = e1;
            }
            if (bindWallpaperComponentLocked(cname, true, false, wallpaper, reply)) {
                return;
            }
            Slog.w(TAG, "Failure starting previous wallpaper", e);
            clearWallpaperLocked(false, wallpaper.userId, reply);
        }
    }

    @Override // android.app.IWallpaperManager
    public void clearWallpaper() {
        synchronized (this.mLock) {
            clearWallpaperLocked(false, UserHandle.getCallingUserId(), null);
        }
    }

    @Override // android.app.IWallpaperManager
    public void setDimensionHints(int width, int height) throws RemoteException {
        checkPermission(Manifest.permission.SET_WALLPAPER_HINTS);
        synchronized (this.mLock) {
            int userId = UserHandle.getCallingUserId();
            WallpaperData wallpaper = this.mWallpaperMap.get(userId);
            if (wallpaper == null) {
                throw new IllegalStateException("Wallpaper not yet initialized for user " + userId);
            }
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("width and height must be > 0");
            }
            if (width != wallpaper.width || height != wallpaper.height) {
                wallpaper.width = width;
                wallpaper.height = height;
                saveSettingsLocked(wallpaper);
                if (this.mCurrentUserId != userId) {
                    return;
                }
                if (wallpaper.connection != null) {
                    if (wallpaper.connection.mEngine != null) {
                        try {
                            wallpaper.connection.mEngine.setDesiredSize(width, height);
                        } catch (RemoteException e) {
                        }
                        notifyCallbacksLocked(wallpaper);
                    } else if (wallpaper.connection.mService != null) {
                        wallpaper.connection.mDimensionsChanged = true;
                    }
                }
            }
        }
    }

    @Override // android.app.IWallpaperManager
    public int getWidthHint() throws RemoteException {
        int i;
        synchronized (this.mLock) {
            WallpaperData wallpaper = this.mWallpaperMap.get(UserHandle.getCallingUserId());
            i = wallpaper.width;
        }
        return i;
    }

    @Override // android.app.IWallpaperManager
    public int getHeightHint() throws RemoteException {
        int i;
        synchronized (this.mLock) {
            WallpaperData wallpaper = this.mWallpaperMap.get(UserHandle.getCallingUserId());
            i = wallpaper.height;
        }
        return i;
    }

    @Override // android.app.IWallpaperManager
    public ParcelFileDescriptor getWallpaper(IWallpaperManagerCallback cb, Bundle outParams) {
        int wallpaperUserId;
        synchronized (this.mLock) {
            int callingUid = Binder.getCallingUid();
            if (callingUid == 1000) {
                wallpaperUserId = this.mCurrentUserId;
            } else {
                wallpaperUserId = UserHandle.getUserId(callingUid);
            }
            WallpaperData wallpaper = this.mWallpaperMap.get(wallpaperUserId);
            if (outParams != null) {
                try {
                    outParams.putInt("width", wallpaper.width);
                    outParams.putInt("height", wallpaper.height);
                } catch (FileNotFoundException e) {
                    Slog.w(TAG, "Error getting wallpaper", e);
                    return null;
                }
            }
            wallpaper.callbacks.register(cb);
            File f = new File(getWallpaperDir(wallpaperUserId), "wallpaper");
            if (!f.exists()) {
                return null;
            }
            return ParcelFileDescriptor.open(f, 268435456);
        }
    }

    @Override // android.app.IWallpaperManager
    public WallpaperInfo getWallpaperInfo() {
        int userId = UserHandle.getCallingUserId();
        synchronized (this.mLock) {
            WallpaperData wallpaper = this.mWallpaperMap.get(userId);
            if (wallpaper.connection != null) {
                return wallpaper.connection.mInfo;
            }
            return null;
        }
    }

    ParcelFileDescriptor updateWallpaperBitmapLocked(String name, WallpaperData wallpaper) {
        if (name == null) {
            name = "";
        }
        try {
            File dir = getWallpaperDir(wallpaper.userId);
            if (!dir.exists()) {
                dir.mkdir();
                FileUtils.setPermissions(dir.getPath(), 505, -1, -1);
            }
            File file = new File(dir, "wallpaper");
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(file, 939524096);
            if (!SELinux.restorecon(file)) {
                return null;
            }
            wallpaper.name = name;
            return fd;
        } catch (FileNotFoundException e) {
            Slog.w(TAG, "Error setting wallpaper", e);
            return null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:47:0x013b, code lost:
        r19 = new android.app.WallpaperInfo(r11.mContext, r0.get(r22));
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    boolean bindWallpaperComponentLocked(android.content.ComponentName r12, boolean r13, boolean r14, com.android.server.WallpaperManagerService.WallpaperData r15, android.os.IRemoteCallback r16) {
        /*
            Method dump skipped, instructions count: 757
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.WallpaperManagerService.bindWallpaperComponentLocked(android.content.ComponentName, boolean, boolean, com.android.server.WallpaperManagerService$WallpaperData, android.os.IRemoteCallback):boolean");
    }

    void detachWallpaperLocked(WallpaperData wallpaper) {
        if (wallpaper.connection != null) {
            if (wallpaper.connection.mReply != null) {
                try {
                    wallpaper.connection.mReply.sendResult(null);
                } catch (RemoteException e) {
                }
                wallpaper.connection.mReply = null;
            }
            if (wallpaper.connection.mEngine != null) {
                try {
                    wallpaper.connection.mEngine.destroy();
                } catch (RemoteException e2) {
                }
            }
            this.mContext.unbindService(wallpaper.connection);
            try {
                this.mIWindowManager.removeWindowToken(wallpaper.connection.mToken);
            } catch (RemoteException e3) {
            }
            wallpaper.connection.mService = null;
            wallpaper.connection.mEngine = null;
            wallpaper.connection = null;
        }
    }

    void clearWallpaperComponentLocked(WallpaperData wallpaper) {
        wallpaper.wallpaperComponent = null;
        detachWallpaperLocked(wallpaper);
    }

    void attachServiceLocked(WallpaperConnection conn, WallpaperData wallpaper) {
        try {
            conn.mService.attach(conn, conn.mToken, WindowManager.LayoutParams.TYPE_WALLPAPER, false, wallpaper.width, wallpaper.height);
        } catch (RemoteException e) {
            Slog.w(TAG, "Failed attaching wallpaper; clearing", e);
            if (!wallpaper.wallpaperUpdating) {
                bindWallpaperComponentLocked(null, false, false, wallpaper, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCallbacksLocked(WallpaperData wallpaper) {
        int n = wallpaper.callbacks.beginBroadcast();
        for (int i = 0; i < n; i++) {
            try {
                ((IWallpaperManagerCallback) wallpaper.callbacks.getBroadcastItem(i)).onWallpaperChanged();
            } catch (RemoteException e) {
            }
        }
        wallpaper.callbacks.finishBroadcast();
        Intent intent = new Intent(Intent.ACTION_WALLPAPER_CHANGED);
        this.mContext.sendBroadcastAsUser(intent, new UserHandle(this.mCurrentUserId));
    }

    private void checkPermission(String permission) {
        if (0 != this.mContext.checkCallingOrSelfPermission(permission)) {
            throw new SecurityException("Access denied to process: " + Binder.getCallingPid() + ", must have permission " + permission);
        }
    }

    private static JournaledFile makeJournaledFile(int userId) {
        String base = new File(getWallpaperDir(userId), WALLPAPER_INFO).getAbsolutePath();
        return new JournaledFile(new File(base), new File(base + ".tmp"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveSettingsLocked(WallpaperData wallpaper) {
        JournaledFile journal = makeJournaledFile(wallpaper.userId);
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(journal.chooseForWrite(), false);
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(stream, "utf-8");
            out.startDocument(null, true);
            out.startTag(null, "wp");
            out.attribute(null, "width", Integer.toString(wallpaper.width));
            out.attribute(null, "height", Integer.toString(wallpaper.height));
            out.attribute(null, "name", wallpaper.name);
            if (wallpaper.wallpaperComponent != null && !wallpaper.wallpaperComponent.equals(IMAGE_WALLPAPER)) {
                out.attribute(null, CardEmulation.EXTRA_SERVICE_COMPONENT, wallpaper.wallpaperComponent.flattenToShortString());
            }
            out.endTag(null, "wp");
            out.endDocument();
            stream.close();
            journal.commit();
        } catch (IOException e) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e2) {
                    journal.rollback();
                }
            }
            journal.rollback();
        }
    }

    private void migrateFromOld() {
        File oldWallpaper = new File(WallpaperBackupHelper.WALLPAPER_IMAGE_KEY);
        File oldInfo = new File(WallpaperBackupHelper.WALLPAPER_INFO_KEY);
        if (oldWallpaper.exists()) {
            File newWallpaper = new File(getWallpaperDir(0), "wallpaper");
            oldWallpaper.renameTo(newWallpaper);
        }
        if (oldInfo.exists()) {
            File newInfo = new File(getWallpaperDir(0), WALLPAPER_INFO);
            oldInfo.renameTo(newInfo);
        }
    }

    private void loadSettingsLocked(int userId) {
        int type;
        JournaledFile journal = makeJournaledFile(userId);
        FileInputStream stream = null;
        File file = journal.chooseForRead();
        if (!file.exists()) {
            migrateFromOld();
        }
        WallpaperData wallpaper = this.mWallpaperMap.get(userId);
        if (wallpaper == null) {
            wallpaper = new WallpaperData(userId);
            this.mWallpaperMap.put(userId, wallpaper);
        }
        boolean success = false;
        try {
            stream = new FileInputStream(file);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, null);
            do {
                type = parser.next();
                if (type == 2) {
                    String tag = parser.getName();
                    if ("wp".equals(tag)) {
                        wallpaper.width = Integer.parseInt(parser.getAttributeValue(null, "width"));
                        wallpaper.height = Integer.parseInt(parser.getAttributeValue(null, "height"));
                        wallpaper.name = parser.getAttributeValue(null, "name");
                        String comp = parser.getAttributeValue(null, CardEmulation.EXTRA_SERVICE_COMPONENT);
                        wallpaper.nextWallpaperComponent = comp != null ? ComponentName.unflattenFromString(comp) : null;
                        if (wallpaper.nextWallpaperComponent == null || "android".equals(wallpaper.nextWallpaperComponent.getPackageName())) {
                            wallpaper.nextWallpaperComponent = IMAGE_WALLPAPER;
                        }
                    }
                }
            } while (type != 1);
            success = true;
        } catch (FileNotFoundException e) {
            Slog.w(TAG, "no current wallpaper -- first boot?");
        } catch (IOException e2) {
            Slog.w(TAG, "failed parsing " + file + Separators.SP + e2);
        } catch (IndexOutOfBoundsException e3) {
            Slog.w(TAG, "failed parsing " + file + Separators.SP + e3);
        } catch (NullPointerException e4) {
            Slog.w(TAG, "failed parsing " + file + Separators.SP + e4);
        } catch (NumberFormatException e5) {
            Slog.w(TAG, "failed parsing " + file + Separators.SP + e5);
        } catch (XmlPullParserException e6) {
            Slog.w(TAG, "failed parsing " + file + Separators.SP + e6);
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e7) {
            }
        }
        if (!success) {
            wallpaper.width = -1;
            wallpaper.height = -1;
            wallpaper.name = "";
        }
        WindowManager wm = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        int baseSize = d.getMaximumSizeDimension();
        if (wallpaper.width < baseSize) {
            wallpaper.width = baseSize;
        }
        if (wallpaper.height < baseSize) {
            wallpaper.height = baseSize;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void settingsRestored() {
        WallpaperData wallpaper;
        boolean success;
        synchronized (this.mLock) {
            loadSettingsLocked(0);
            wallpaper = this.mWallpaperMap.get(0);
            if (wallpaper.nextWallpaperComponent != null && !wallpaper.nextWallpaperComponent.equals(IMAGE_WALLPAPER)) {
                if (!bindWallpaperComponentLocked(wallpaper.nextWallpaperComponent, false, false, wallpaper, null)) {
                    bindWallpaperComponentLocked(null, false, false, wallpaper, null);
                }
                success = true;
            } else {
                if ("".equals(wallpaper.name)) {
                    success = true;
                } else {
                    success = restoreNamedResourceLocked(wallpaper);
                }
                if (success) {
                    bindWallpaperComponentLocked(wallpaper.nextWallpaperComponent, false, false, wallpaper, null);
                }
            }
        }
        if (!success) {
            Slog.e(TAG, "Failed to restore wallpaper: '" + wallpaper.name + Separators.QUOTE);
            wallpaper.name = "";
            getWallpaperDir(0).delete();
        }
        synchronized (this.mLock) {
            saveSettingsLocked(wallpaper);
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump wallpaper service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mLock) {
            pw.println("Current Wallpaper Service state:");
            for (int i = 0; i < this.mWallpaperMap.size(); i++) {
                WallpaperData wallpaper = this.mWallpaperMap.valueAt(i);
                pw.println(" User " + wallpaper.userId + Separators.COLON);
                pw.print("  mWidth=");
                pw.print(wallpaper.width);
                pw.print(" mHeight=");
                pw.println(wallpaper.height);
                pw.print("  mName=");
                pw.println(wallpaper.name);
                pw.print("  mWallpaperComponent=");
                pw.println(wallpaper.wallpaperComponent);
                if (wallpaper.connection != null) {
                    WallpaperConnection conn = wallpaper.connection;
                    pw.print("  Wallpaper connection ");
                    pw.print(conn);
                    pw.println(Separators.COLON);
                    if (conn.mInfo != null) {
                        pw.print("    mInfo.component=");
                        pw.println(conn.mInfo.getComponent());
                    }
                    pw.print("    mToken=");
                    pw.println(conn.mToken);
                    pw.print("    mService=");
                    pw.println(conn.mService);
                    pw.print("    mEngine=");
                    pw.println(conn.mEngine);
                    pw.print("    mLastDiedTime=");
                    pw.println(wallpaper.lastDiedTime - SystemClock.uptimeMillis());
                }
            }
        }
    }
}