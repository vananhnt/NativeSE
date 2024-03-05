package com.android.server;

import android.Manifest;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.app.admin.IDevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.security.KeyChain;
import android.util.AtomicFile;
import android.util.PrintWriterPrinter;
import android.util.Printer;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import android.view.IWindowManager;
import com.android.internal.R;
import com.android.internal.os.storage.ExternalStorageFormatter;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.XmlUtils;
import com.android.internal.widget.LockPatternUtils;
import gov.nist.core.Separators;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: DevicePolicyManagerService.class */
public class DevicePolicyManagerService extends IDevicePolicyManager.Stub {
    private static final String TAG = "DevicePolicyManagerService";
    private static final String DEVICE_POLICIES_XML = "device_policies.xml";
    private static final int REQUEST_EXPIRE_PASSWORD = 5571;
    private static final long MS_PER_DAY = 86400000;
    private static final long EXPIRATION_GRACE_PERIOD_MS = 432000000;
    protected static final String ACTION_EXPIRED_PASSWORD_NOTIFICATION = "com.android.server.ACTION_EXPIRED_PASSWORD_NOTIFICATION";
    private static final int MONITORING_CERT_NOTIFICATION_ID = 17039535;
    private static final boolean DBG = false;
    final Context mContext;
    final PowerManager.WakeLock mWakeLock;
    IPowerManager mIPowerManager;
    IWindowManager mIWindowManager;
    NotificationManager mNotificationManager;
    private DeviceOwner mDeviceOwner;
    private boolean mHasFeature;
    final SparseArray<DevicePolicyData> mUserData = new SparseArray<>();
    Handler mHandler = new Handler();
    BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.server.DevicePolicyManagerService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final int userHandle = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, getSendingUserId());
            if (Intent.ACTION_BOOT_COMPLETED.equals(action) || DevicePolicyManagerService.ACTION_EXPIRED_PASSWORD_NOTIFICATION.equals(action)) {
                DevicePolicyManagerService.this.mHandler.post(new Runnable() { // from class: com.android.server.DevicePolicyManagerService.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DevicePolicyManagerService.this.handlePasswordExpirationNotification(DevicePolicyManagerService.this.getUserData(userHandle));
                    }
                });
            }
            if (Intent.ACTION_BOOT_COMPLETED.equals(action) || KeyChain.ACTION_STORAGE_CHANGED.equals(action)) {
                DevicePolicyManagerService.this.manageMonitoringCertificateNotification(intent);
            }
            if (Intent.ACTION_USER_REMOVED.equals(action)) {
                DevicePolicyManagerService.this.removeUserData(userHandle);
            } else if (Intent.ACTION_USER_STARTED.equals(action) || Intent.ACTION_PACKAGE_CHANGED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action) || "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
                if (Intent.ACTION_USER_STARTED.equals(action)) {
                    synchronized (DevicePolicyManagerService.this) {
                        DevicePolicyManagerService.this.mUserData.remove(userHandle);
                    }
                }
                DevicePolicyManagerService.this.handlePackagesChanged(userHandle);
            }
        }
    };
    public static final String SYSTEM_PROP_DISABLE_CAMERA = "sys.secpolicy.camera.disabled";

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.setExpirationAlarmCheckLocked(android.content.Context, com.android.server.DevicePolicyManagerService$DevicePolicyData):void, file: DevicePolicyManagerService.class
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
    protected void setExpirationAlarmCheckLocked(android.content.Context r1, com.android.server.DevicePolicyManagerService.DevicePolicyData r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.setExpirationAlarmCheckLocked(android.content.Context, com.android.server.DevicePolicyManagerService$DevicePolicyData):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.setExpirationAlarmCheckLocked(android.content.Context, com.android.server.DevicePolicyManagerService$DevicePolicyData):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.sendChangedNotification(int):void, file: DevicePolicyManagerService.class
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
    private void sendChangedNotification(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.sendChangedNotification(int):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.sendChangedNotification(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.syncDeviceCapabilitiesLocked(com.android.server.DevicePolicyManagerService$DevicePolicyData):void, file: DevicePolicyManagerService.class
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
    void syncDeviceCapabilitiesLocked(com.android.server.DevicePolicyManagerService.DevicePolicyData r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.syncDeviceCapabilitiesLocked(com.android.server.DevicePolicyManagerService$DevicePolicyData):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.syncDeviceCapabilitiesLocked(com.android.server.DevicePolicyManagerService$DevicePolicyData):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.setActiveAdmin(android.content.ComponentName, boolean, int):void, file: DevicePolicyManagerService.class
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
    @Override // android.app.admin.IDevicePolicyManager
    public void setActiveAdmin(android.content.ComponentName r1, boolean r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.setActiveAdmin(android.content.ComponentName, boolean, int):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.setActiveAdmin(android.content.ComponentName, boolean, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.removeActiveAdmin(android.content.ComponentName, int):void, file: DevicePolicyManagerService.class
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
    @Override // android.app.admin.IDevicePolicyManager
    public void removeActiveAdmin(android.content.ComponentName r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.removeActiveAdmin(android.content.ComponentName, int):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.removeActiveAdmin(android.content.ComponentName, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.resetPassword(java.lang.String, int, int):boolean, file: DevicePolicyManagerService.class
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
    @Override // android.app.admin.IDevicePolicyManager
    public boolean resetPassword(java.lang.String r1, int r2, int r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.resetPassword(java.lang.String, int, int):boolean, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.resetPassword(java.lang.String, int, int):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.updateMaximumTimeToLockLocked(com.android.server.DevicePolicyManagerService$DevicePolicyData):void, file: DevicePolicyManagerService.class
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
    void updateMaximumTimeToLockLocked(com.android.server.DevicePolicyManagerService.DevicePolicyData r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.updateMaximumTimeToLockLocked(com.android.server.DevicePolicyManagerService$DevicePolicyData):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.updateMaximumTimeToLockLocked(com.android.server.DevicePolicyManagerService$DevicePolicyData):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.lockNowUnchecked():void, file: DevicePolicyManagerService.class
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
    private void lockNowUnchecked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.lockNowUnchecked():void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.lockNowUnchecked():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.installCaCert(byte[]):boolean, file: DevicePolicyManagerService.class
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
    @Override // android.app.admin.IDevicePolicyManager
    public boolean installCaCert(byte[] r1) throws android.os.RemoteException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.installCaCert(byte[]):boolean, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.installCaCert(byte[]):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.uninstallCaCert(byte[]):void, file: DevicePolicyManagerService.class
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
    @Override // android.app.admin.IDevicePolicyManager
    public void uninstallCaCert(byte[] r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.uninstallCaCert(byte[]):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.uninstallCaCert(byte[]):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.wipeData(int, int):void, file: DevicePolicyManagerService.class
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
    @Override // android.app.admin.IDevicePolicyManager
    public void wipeData(int r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.wipeData(int, int):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.wipeData(int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.setActivePasswordState(int, int, int, int, int, int, int, int, int):void, file: DevicePolicyManagerService.class
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
    @Override // android.app.admin.IDevicePolicyManager
    public void setActivePasswordState(int r1, int r2, int r3, int r4, int r5, int r6, int r7, int r8, int r9) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.setActivePasswordState(int, int, int, int, int, int, int, int, int):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.setActivePasswordState(int, int, int, int, int, int, int, int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.reportFailedPasswordAttempt(int):void, file: DevicePolicyManagerService.class
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
    @Override // android.app.admin.IDevicePolicyManager
    public void reportFailedPasswordAttempt(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.reportFailedPasswordAttempt(int):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.reportFailedPasswordAttempt(int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.reportSuccessfulPasswordAttempt(int):void, file: DevicePolicyManagerService.class
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
    @Override // android.app.admin.IDevicePolicyManager
    public void reportSuccessfulPasswordAttempt(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.DevicePolicyManagerService.reportSuccessfulPasswordAttempt(int):void, file: DevicePolicyManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DevicePolicyManagerService.reportSuccessfulPasswordAttempt(int):void");
    }

    /* loaded from: DevicePolicyManagerService$DevicePolicyData.class */
    public static class DevicePolicyData {
        int mUserHandle;
        int mActivePasswordQuality = 0;
        int mActivePasswordLength = 0;
        int mActivePasswordUpperCase = 0;
        int mActivePasswordLowerCase = 0;
        int mActivePasswordLetters = 0;
        int mActivePasswordNumeric = 0;
        int mActivePasswordSymbols = 0;
        int mActivePasswordNonLetter = 0;
        int mFailedPasswordAttempts = 0;
        int mPasswordOwner = -1;
        long mLastMaximumTimeToLock = -1;
        final HashMap<ComponentName, ActiveAdmin> mAdminMap = new HashMap<>();
        final ArrayList<ActiveAdmin> mAdminList = new ArrayList<>();

        public DevicePolicyData(int userHandle) {
            this.mUserHandle = userHandle;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: DevicePolicyManagerService$ActiveAdmin.class */
    public static class ActiveAdmin {
        final DeviceAdminInfo info;
        static final int DEF_MINIMUM_PASSWORD_LENGTH = 0;
        static final int DEF_PASSWORD_HISTORY_LENGTH = 0;
        static final int DEF_MINIMUM_PASSWORD_UPPER_CASE = 0;
        static final int DEF_MINIMUM_PASSWORD_LOWER_CASE = 0;
        static final int DEF_MINIMUM_PASSWORD_LETTERS = 1;
        static final int DEF_MINIMUM_PASSWORD_NUMERIC = 1;
        static final int DEF_MINIMUM_PASSWORD_SYMBOLS = 1;
        static final int DEF_MINIMUM_PASSWORD_NON_LETTER = 0;
        static final long DEF_MAXIMUM_TIME_TO_UNLOCK = 0;
        static final int DEF_MAXIMUM_FAILED_PASSWORDS_FOR_WIPE = 0;
        static final long DEF_PASSWORD_EXPIRATION_TIMEOUT = 0;
        static final long DEF_PASSWORD_EXPIRATION_DATE = 0;
        static final int DEF_KEYGUARD_FEATURES_DISABLED = 0;
        int passwordQuality = 0;
        int minimumPasswordLength = 0;
        int passwordHistoryLength = 0;
        int minimumPasswordUpperCase = 0;
        int minimumPasswordLowerCase = 0;
        int minimumPasswordLetters = 1;
        int minimumPasswordNumeric = 1;
        int minimumPasswordSymbols = 1;
        int minimumPasswordNonLetter = 0;
        long maximumTimeToUnlock = 0;
        int maximumFailedPasswordsForWipe = 0;
        long passwordExpirationTimeout = 0;
        long passwordExpirationDate = 0;
        int disabledKeyguardFeatures = 0;
        boolean encryptionRequested = false;
        boolean disableCamera = false;
        boolean specifiesGlobalProxy = false;
        String globalProxySpec = null;
        String globalProxyExclusionList = null;

        ActiveAdmin(DeviceAdminInfo _info) {
            this.info = _info;
        }

        int getUid() {
            return this.info.getActivityInfo().applicationInfo.uid;
        }

        public UserHandle getUserHandle() {
            return new UserHandle(UserHandle.getUserId(this.info.getActivityInfo().applicationInfo.uid));
        }

        void writeToXml(XmlSerializer out) throws IllegalArgumentException, IllegalStateException, IOException {
            out.startTag(null, "policies");
            this.info.writePoliciesToXml(out);
            out.endTag(null, "policies");
            if (this.passwordQuality != 0) {
                out.startTag(null, "password-quality");
                out.attribute(null, "value", Integer.toString(this.passwordQuality));
                out.endTag(null, "password-quality");
                if (this.minimumPasswordLength != 0) {
                    out.startTag(null, "min-password-length");
                    out.attribute(null, "value", Integer.toString(this.minimumPasswordLength));
                    out.endTag(null, "min-password-length");
                }
                if (this.passwordHistoryLength != 0) {
                    out.startTag(null, "password-history-length");
                    out.attribute(null, "value", Integer.toString(this.passwordHistoryLength));
                    out.endTag(null, "password-history-length");
                }
                if (this.minimumPasswordUpperCase != 0) {
                    out.startTag(null, "min-password-uppercase");
                    out.attribute(null, "value", Integer.toString(this.minimumPasswordUpperCase));
                    out.endTag(null, "min-password-uppercase");
                }
                if (this.minimumPasswordLowerCase != 0) {
                    out.startTag(null, "min-password-lowercase");
                    out.attribute(null, "value", Integer.toString(this.minimumPasswordLowerCase));
                    out.endTag(null, "min-password-lowercase");
                }
                if (this.minimumPasswordLetters != 1) {
                    out.startTag(null, "min-password-letters");
                    out.attribute(null, "value", Integer.toString(this.minimumPasswordLetters));
                    out.endTag(null, "min-password-letters");
                }
                if (this.minimumPasswordNumeric != 1) {
                    out.startTag(null, "min-password-numeric");
                    out.attribute(null, "value", Integer.toString(this.minimumPasswordNumeric));
                    out.endTag(null, "min-password-numeric");
                }
                if (this.minimumPasswordSymbols != 1) {
                    out.startTag(null, "min-password-symbols");
                    out.attribute(null, "value", Integer.toString(this.minimumPasswordSymbols));
                    out.endTag(null, "min-password-symbols");
                }
                if (this.minimumPasswordNonLetter > 0) {
                    out.startTag(null, "min-password-nonletter");
                    out.attribute(null, "value", Integer.toString(this.minimumPasswordNonLetter));
                    out.endTag(null, "min-password-nonletter");
                }
            }
            if (this.maximumTimeToUnlock != 0) {
                out.startTag(null, "max-time-to-unlock");
                out.attribute(null, "value", Long.toString(this.maximumTimeToUnlock));
                out.endTag(null, "max-time-to-unlock");
            }
            if (this.maximumFailedPasswordsForWipe != 0) {
                out.startTag(null, "max-failed-password-wipe");
                out.attribute(null, "value", Integer.toString(this.maximumFailedPasswordsForWipe));
                out.endTag(null, "max-failed-password-wipe");
            }
            if (this.specifiesGlobalProxy) {
                out.startTag(null, "specifies-global-proxy");
                out.attribute(null, "value", Boolean.toString(this.specifiesGlobalProxy));
                out.endTag(null, "specifies_global_proxy");
                if (this.globalProxySpec != null) {
                    out.startTag(null, "global-proxy-spec");
                    out.attribute(null, "value", this.globalProxySpec);
                    out.endTag(null, "global-proxy-spec");
                }
                if (this.globalProxyExclusionList != null) {
                    out.startTag(null, "global-proxy-exclusion-list");
                    out.attribute(null, "value", this.globalProxyExclusionList);
                    out.endTag(null, "global-proxy-exclusion-list");
                }
            }
            if (this.passwordExpirationTimeout != 0) {
                out.startTag(null, "password-expiration-timeout");
                out.attribute(null, "value", Long.toString(this.passwordExpirationTimeout));
                out.endTag(null, "password-expiration-timeout");
            }
            if (this.passwordExpirationDate != 0) {
                out.startTag(null, "password-expiration-date");
                out.attribute(null, "value", Long.toString(this.passwordExpirationDate));
                out.endTag(null, "password-expiration-date");
            }
            if (this.encryptionRequested) {
                out.startTag(null, "encryption-requested");
                out.attribute(null, "value", Boolean.toString(this.encryptionRequested));
                out.endTag(null, "encryption-requested");
            }
            if (this.disableCamera) {
                out.startTag(null, "disable-camera");
                out.attribute(null, "value", Boolean.toString(this.disableCamera));
                out.endTag(null, "disable-camera");
            }
            if (this.disabledKeyguardFeatures != 0) {
                out.startTag(null, "disable-keyguard-features");
                out.attribute(null, "value", Integer.toString(this.disabledKeyguardFeatures));
                out.endTag(null, "disable-keyguard-features");
            }
        }

        void readFromXml(XmlPullParser parser) throws XmlPullParserException, IOException {
            int outerDepth = parser.getDepth();
            while (true) {
                int type = parser.next();
                if (type == 1) {
                    return;
                }
                if (type != 3 || parser.getDepth() > outerDepth) {
                    if (type != 3 && type != 4) {
                        String tag = parser.getName();
                        if ("policies".equals(tag)) {
                            this.info.readPoliciesFromXml(parser);
                        } else if ("password-quality".equals(tag)) {
                            this.passwordQuality = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("min-password-length".equals(tag)) {
                            this.minimumPasswordLength = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("password-history-length".equals(tag)) {
                            this.passwordHistoryLength = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("min-password-uppercase".equals(tag)) {
                            this.minimumPasswordUpperCase = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("min-password-lowercase".equals(tag)) {
                            this.minimumPasswordLowerCase = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("min-password-letters".equals(tag)) {
                            this.minimumPasswordLetters = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("min-password-numeric".equals(tag)) {
                            this.minimumPasswordNumeric = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("min-password-symbols".equals(tag)) {
                            this.minimumPasswordSymbols = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("min-password-nonletter".equals(tag)) {
                            this.minimumPasswordNonLetter = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("max-time-to-unlock".equals(tag)) {
                            this.maximumTimeToUnlock = Long.parseLong(parser.getAttributeValue(null, "value"));
                        } else if ("max-failed-password-wipe".equals(tag)) {
                            this.maximumFailedPasswordsForWipe = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else if ("specifies-global-proxy".equals(tag)) {
                            this.specifiesGlobalProxy = Boolean.parseBoolean(parser.getAttributeValue(null, "value"));
                        } else if ("global-proxy-spec".equals(tag)) {
                            this.globalProxySpec = parser.getAttributeValue(null, "value");
                        } else if ("global-proxy-exclusion-list".equals(tag)) {
                            this.globalProxyExclusionList = parser.getAttributeValue(null, "value");
                        } else if ("password-expiration-timeout".equals(tag)) {
                            this.passwordExpirationTimeout = Long.parseLong(parser.getAttributeValue(null, "value"));
                        } else if ("password-expiration-date".equals(tag)) {
                            this.passwordExpirationDate = Long.parseLong(parser.getAttributeValue(null, "value"));
                        } else if ("encryption-requested".equals(tag)) {
                            this.encryptionRequested = Boolean.parseBoolean(parser.getAttributeValue(null, "value"));
                        } else if ("disable-camera".equals(tag)) {
                            this.disableCamera = Boolean.parseBoolean(parser.getAttributeValue(null, "value"));
                        } else if ("disable-keyguard-features".equals(tag)) {
                            this.disabledKeyguardFeatures = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } else {
                            Slog.w(DevicePolicyManagerService.TAG, "Unknown admin tag: " + tag);
                        }
                        XmlUtils.skipCurrentTag(parser);
                    }
                } else {
                    return;
                }
            }
        }

        void dump(String prefix, PrintWriter pw) {
            pw.print(prefix);
            pw.print("uid=");
            pw.println(getUid());
            pw.print(prefix);
            pw.println("policies:");
            ArrayList<DeviceAdminInfo.PolicyInfo> pols = this.info.getUsedPolicies();
            if (pols != null) {
                for (int i = 0; i < pols.size(); i++) {
                    pw.print(prefix);
                    pw.print("  ");
                    pw.println(pols.get(i).tag);
                }
            }
            pw.print(prefix);
            pw.print("passwordQuality=0x");
            pw.println(Integer.toHexString(this.passwordQuality));
            pw.print(prefix);
            pw.print("minimumPasswordLength=");
            pw.println(this.minimumPasswordLength);
            pw.print(prefix);
            pw.print("passwordHistoryLength=");
            pw.println(this.passwordHistoryLength);
            pw.print(prefix);
            pw.print("minimumPasswordUpperCase=");
            pw.println(this.minimumPasswordUpperCase);
            pw.print(prefix);
            pw.print("minimumPasswordLowerCase=");
            pw.println(this.minimumPasswordLowerCase);
            pw.print(prefix);
            pw.print("minimumPasswordLetters=");
            pw.println(this.minimumPasswordLetters);
            pw.print(prefix);
            pw.print("minimumPasswordNumeric=");
            pw.println(this.minimumPasswordNumeric);
            pw.print(prefix);
            pw.print("minimumPasswordSymbols=");
            pw.println(this.minimumPasswordSymbols);
            pw.print(prefix);
            pw.print("minimumPasswordNonLetter=");
            pw.println(this.minimumPasswordNonLetter);
            pw.print(prefix);
            pw.print("maximumTimeToUnlock=");
            pw.println(this.maximumTimeToUnlock);
            pw.print(prefix);
            pw.print("maximumFailedPasswordsForWipe=");
            pw.println(this.maximumFailedPasswordsForWipe);
            pw.print(prefix);
            pw.print("specifiesGlobalProxy=");
            pw.println(this.specifiesGlobalProxy);
            pw.print(prefix);
            pw.print("passwordExpirationTimeout=");
            pw.println(this.passwordExpirationTimeout);
            pw.print(prefix);
            pw.print("passwordExpirationDate=");
            pw.println(this.passwordExpirationDate);
            if (this.globalProxySpec != null) {
                pw.print(prefix);
                pw.print("globalProxySpec=");
                pw.println(this.globalProxySpec);
            }
            if (this.globalProxyExclusionList != null) {
                pw.print(prefix);
                pw.print("globalProxyEclusionList=");
                pw.println(this.globalProxyExclusionList);
            }
            pw.print(prefix);
            pw.print("encryptionRequested=");
            pw.println(this.encryptionRequested);
            pw.print(prefix);
            pw.print("disableCamera=");
            pw.println(this.disableCamera);
            pw.print(prefix);
            pw.print("disabledKeyguardFeatures=");
            pw.println(this.disabledKeyguardFeatures);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePackagesChanged(int userHandle) {
        boolean removed = false;
        DevicePolicyData policy = getUserData(userHandle);
        IPackageManager pm = AppGlobals.getPackageManager();
        for (int i = policy.mAdminList.size() - 1; i >= 0; i--) {
            ActiveAdmin aa = policy.mAdminList.get(i);
            try {
                if (pm.getPackageInfo(aa.info.getPackageName(), 0, userHandle) == null || pm.getReceiverInfo(aa.info.getComponent(), 0, userHandle) == null) {
                    removed = true;
                    policy.mAdminList.remove(i);
                }
            } catch (RemoteException e) {
            }
        }
        if (removed) {
            validatePasswordOwnerLocked(policy);
            syncDeviceCapabilitiesLocked(policy);
            saveSettingsLocked(policy.mUserHandle);
        }
    }

    public DevicePolicyManagerService(Context context) {
        this.mContext = context;
        this.mHasFeature = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_DEVICE_ADMIN);
        this.mWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "DPM");
        if (!this.mHasFeature) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(ACTION_EXPIRED_PASSWORD_NOTIFICATION);
        filter.addAction(Intent.ACTION_USER_REMOVED);
        filter.addAction(Intent.ACTION_USER_STARTED);
        filter.addAction(KeyChain.ACTION_STORAGE_CHANGED);
        context.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, filter, null, this.mHandler);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter2.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        filter2.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter2.addDataScheme(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        context.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, filter2, null, this.mHandler);
    }

    DevicePolicyData getUserData(int userHandle) {
        DevicePolicyData devicePolicyData;
        synchronized (this) {
            DevicePolicyData policy = this.mUserData.get(userHandle);
            if (policy == null) {
                policy = new DevicePolicyData(userHandle);
                this.mUserData.append(userHandle, policy);
                loadSettingsLocked(policy, userHandle);
            }
            devicePolicyData = policy;
        }
        return devicePolicyData;
    }

    void removeUserData(int userHandle) {
        synchronized (this) {
            if (userHandle == 0) {
                Slog.w(TAG, "Tried to remove device policy file for user 0! Ignoring.");
                return;
            }
            DevicePolicyData policy = this.mUserData.get(userHandle);
            if (policy != null) {
                this.mUserData.remove(userHandle);
            }
            File policyFile = new File(Environment.getUserSystemDirectory(userHandle), DEVICE_POLICIES_XML);
            policyFile.delete();
            Slog.i(TAG, "Removed device policy file " + policyFile.getAbsolutePath());
        }
    }

    void loadDeviceOwner() {
        synchronized (this) {
            if (DeviceOwner.isRegistered()) {
                this.mDeviceOwner = new DeviceOwner();
            }
        }
    }

    private IPowerManager getIPowerManager() {
        if (this.mIPowerManager == null) {
            IBinder b = ServiceManager.getService(Context.POWER_SERVICE);
            this.mIPowerManager = IPowerManager.Stub.asInterface(b);
        }
        return this.mIPowerManager;
    }

    private IWindowManager getWindowManager() {
        if (this.mIWindowManager == null) {
            IBinder b = ServiceManager.getService(Context.WINDOW_SERVICE);
            this.mIWindowManager = IWindowManager.Stub.asInterface(b);
        }
        return this.mIWindowManager;
    }

    private NotificationManager getNotificationManager() {
        if (this.mNotificationManager == null) {
            this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return this.mNotificationManager;
    }

    ActiveAdmin getActiveAdminUncheckedLocked(ComponentName who, int userHandle) {
        ActiveAdmin admin = getUserData(userHandle).mAdminMap.get(who);
        if (admin != null && who.getPackageName().equals(admin.info.getActivityInfo().packageName) && who.getClassName().equals(admin.info.getActivityInfo().name)) {
            return admin;
        }
        return null;
    }

    ActiveAdmin getActiveAdminForCallerLocked(ComponentName who, int reqPolicy) throws SecurityException {
        int callingUid = Binder.getCallingUid();
        int userHandle = UserHandle.getUserId(callingUid);
        DevicePolicyData policy = getUserData(userHandle);
        if (who != null) {
            ActiveAdmin admin = policy.mAdminMap.get(who);
            if (admin == null) {
                throw new SecurityException("No active admin " + who);
            }
            if (admin.getUid() != callingUid) {
                throw new SecurityException("Admin " + who + " is not owned by uid " + Binder.getCallingUid());
            }
            if (!admin.info.usesPolicy(reqPolicy)) {
                throw new SecurityException("Admin " + admin.info.getComponent() + " did not specify uses-policy for: " + admin.info.getTagForPolicy(reqPolicy));
            }
            return admin;
        }
        int N = policy.mAdminList.size();
        for (int i = 0; i < N; i++) {
            ActiveAdmin admin2 = policy.mAdminList.get(i);
            if (admin2.getUid() == callingUid && admin2.info.usesPolicy(reqPolicy)) {
                return admin2;
            }
        }
        throw new SecurityException("No active admin owned by uid " + Binder.getCallingUid() + " for policy #" + reqPolicy);
    }

    void sendAdminCommandLocked(ActiveAdmin admin, String action) {
        sendAdminCommandLocked(admin, action, (BroadcastReceiver) null);
    }

    void sendAdminCommandLocked(ActiveAdmin admin, String action, BroadcastReceiver result) {
        Intent intent = new Intent(action);
        intent.setComponent(admin.info.getComponent());
        if (action.equals(DeviceAdminReceiver.ACTION_PASSWORD_EXPIRING)) {
            intent.putExtra("expiration", admin.passwordExpirationDate);
        }
        if (result != null) {
            this.mContext.sendOrderedBroadcastAsUser(intent, admin.getUserHandle(), null, result, this.mHandler, -1, null, null);
        } else {
            this.mContext.sendBroadcastAsUser(intent, UserHandle.OWNER);
        }
    }

    void sendAdminCommandLocked(String action, int reqPolicy, int userHandle) {
        DevicePolicyData policy = getUserData(userHandle);
        int count = policy.mAdminList.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                ActiveAdmin admin = policy.mAdminList.get(i);
                if (admin.info.usesPolicy(reqPolicy)) {
                    sendAdminCommandLocked(admin, action);
                }
            }
        }
    }

    void removeActiveAdminLocked(final ComponentName adminReceiver, int userHandle) {
        final ActiveAdmin admin = getActiveAdminUncheckedLocked(adminReceiver, userHandle);
        if (admin != null) {
            sendAdminCommandLocked(admin, DeviceAdminReceiver.ACTION_DEVICE_ADMIN_DISABLED, new BroadcastReceiver() { // from class: com.android.server.DevicePolicyManagerService.2
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    synchronized (DevicePolicyManagerService.this) {
                        int userHandle2 = admin.getUserHandle().getIdentifier();
                        DevicePolicyData policy = DevicePolicyManagerService.this.getUserData(userHandle2);
                        boolean doProxyCleanup = admin.info.usesPolicy(5);
                        policy.mAdminList.remove(admin);
                        policy.mAdminMap.remove(adminReceiver);
                        DevicePolicyManagerService.this.validatePasswordOwnerLocked(policy);
                        DevicePolicyManagerService.this.syncDeviceCapabilitiesLocked(policy);
                        if (doProxyCleanup) {
                            DevicePolicyManagerService.this.resetGlobalProxyLocked(DevicePolicyManagerService.this.getUserData(userHandle2));
                        }
                        DevicePolicyManagerService.this.saveSettingsLocked(userHandle2);
                        DevicePolicyManagerService.this.updateMaximumTimeToLockLocked(policy);
                    }
                }
            });
        }
    }

    public DeviceAdminInfo findAdmin(ComponentName adminName, int userHandle) {
        if (!this.mHasFeature) {
            return null;
        }
        enforceCrossUserPermission(userHandle);
        Intent resolveIntent = new Intent();
        resolveIntent.setComponent(adminName);
        List<ResolveInfo> infos = this.mContext.getPackageManager().queryBroadcastReceivers(resolveIntent, 32896, userHandle);
        if (infos == null || infos.size() <= 0) {
            throw new IllegalArgumentException("Unknown admin: " + adminName);
        }
        try {
            return new DeviceAdminInfo(this.mContext, infos.get(0));
        } catch (IOException e) {
            Slog.w(TAG, "Bad device admin requested for user=" + userHandle + ": " + adminName, e);
            return null;
        } catch (XmlPullParserException e2) {
            Slog.w(TAG, "Bad device admin requested for user=" + userHandle + ": " + adminName, e2);
            return null;
        }
    }

    private static JournaledFile makeJournaledFile(int userHandle) {
        String base = userHandle == 0 ? "/data/system/device_policies.xml" : new File(Environment.getUserSystemDirectory(userHandle), DEVICE_POLICIES_XML).getAbsolutePath();
        return new JournaledFile(new File(base), new File(base + ".tmp"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveSettingsLocked(int userHandle) {
        DevicePolicyData policy = getUserData(userHandle);
        JournaledFile journal = makeJournaledFile(userHandle);
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(journal.chooseForWrite(), false);
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(stream, "utf-8");
            out.startDocument(null, true);
            out.startTag(null, "policies");
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin ap = policy.mAdminList.get(i);
                if (ap != null) {
                    out.startTag(null, "admin");
                    out.attribute(null, "name", ap.info.getComponent().flattenToString());
                    ap.writeToXml(out);
                    out.endTag(null, "admin");
                }
            }
            if (policy.mPasswordOwner >= 0) {
                out.startTag(null, "password-owner");
                out.attribute(null, "value", Integer.toString(policy.mPasswordOwner));
                out.endTag(null, "password-owner");
            }
            if (policy.mFailedPasswordAttempts != 0) {
                out.startTag(null, "failed-password-attempts");
                out.attribute(null, "value", Integer.toString(policy.mFailedPasswordAttempts));
                out.endTag(null, "failed-password-attempts");
            }
            if (policy.mActivePasswordQuality != 0 || policy.mActivePasswordLength != 0 || policy.mActivePasswordUpperCase != 0 || policy.mActivePasswordLowerCase != 0 || policy.mActivePasswordLetters != 0 || policy.mActivePasswordNumeric != 0 || policy.mActivePasswordSymbols != 0 || policy.mActivePasswordNonLetter != 0) {
                out.startTag(null, "active-password");
                out.attribute(null, "quality", Integer.toString(policy.mActivePasswordQuality));
                out.attribute(null, "length", Integer.toString(policy.mActivePasswordLength));
                out.attribute(null, "uppercase", Integer.toString(policy.mActivePasswordUpperCase));
                out.attribute(null, "lowercase", Integer.toString(policy.mActivePasswordLowerCase));
                out.attribute(null, "letters", Integer.toString(policy.mActivePasswordLetters));
                out.attribute(null, Telephony.Carriers.NUMERIC, Integer.toString(policy.mActivePasswordNumeric));
                out.attribute(null, "symbols", Integer.toString(policy.mActivePasswordSymbols));
                out.attribute(null, "nonletter", Integer.toString(policy.mActivePasswordNonLetter));
                out.endTag(null, "active-password");
            }
            out.endTag(null, "policies");
            out.endDocument();
            stream.close();
            journal.commit();
            sendChangedNotification(userHandle);
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

    private void loadSettingsLocked(DevicePolicyData policy, int userHandle) {
        XmlPullParser parser;
        String tag;
        JournaledFile journal = makeJournaledFile(userHandle);
        FileInputStream stream = null;
        File file = journal.chooseForRead();
        try {
            stream = new FileInputStream(file);
            parser = Xml.newPullParser();
            parser.setInput(stream, null);
            while (true) {
                int type = parser.next();
                if (type == 1 || type == 2) {
                    break;
                }
            }
            tag = parser.getName();
        } catch (FileNotFoundException e) {
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
        if (!"policies".equals(tag)) {
            throw new XmlPullParserException("Settings do not start with policies tag: found " + tag);
        }
        parser.next();
        int outerDepth = parser.getDepth();
        while (true) {
            int type2 = parser.next();
            if (type2 == 1 || (type2 == 3 && parser.getDepth() <= outerDepth)) {
                break;
            } else if (type2 != 3 && type2 != 4) {
                String tag2 = parser.getName();
                if ("admin".equals(tag2)) {
                    String name = parser.getAttributeValue(null, "name");
                    try {
                        DeviceAdminInfo dai = findAdmin(ComponentName.unflattenFromString(name), userHandle);
                        if (dai != null) {
                            ActiveAdmin ap = new ActiveAdmin(dai);
                            ap.readFromXml(parser);
                            policy.mAdminMap.put(ap.info.getComponent(), ap);
                            policy.mAdminList.add(ap);
                        }
                    } catch (RuntimeException e7) {
                        Slog.w(TAG, "Failed loading admin " + name, e7);
                    }
                } else if ("failed-password-attempts".equals(tag2)) {
                    policy.mFailedPasswordAttempts = Integer.parseInt(parser.getAttributeValue(null, "value"));
                    XmlUtils.skipCurrentTag(parser);
                } else if ("password-owner".equals(tag2)) {
                    policy.mPasswordOwner = Integer.parseInt(parser.getAttributeValue(null, "value"));
                    XmlUtils.skipCurrentTag(parser);
                } else if ("active-password".equals(tag2)) {
                    policy.mActivePasswordQuality = Integer.parseInt(parser.getAttributeValue(null, "quality"));
                    policy.mActivePasswordLength = Integer.parseInt(parser.getAttributeValue(null, "length"));
                    policy.mActivePasswordUpperCase = Integer.parseInt(parser.getAttributeValue(null, "uppercase"));
                    policy.mActivePasswordLowerCase = Integer.parseInt(parser.getAttributeValue(null, "lowercase"));
                    policy.mActivePasswordLetters = Integer.parseInt(parser.getAttributeValue(null, "letters"));
                    policy.mActivePasswordNumeric = Integer.parseInt(parser.getAttributeValue(null, Telephony.Carriers.NUMERIC));
                    policy.mActivePasswordSymbols = Integer.parseInt(parser.getAttributeValue(null, "symbols"));
                    policy.mActivePasswordNonLetter = Integer.parseInt(parser.getAttributeValue(null, "nonletter"));
                    XmlUtils.skipCurrentTag(parser);
                } else {
                    Slog.w(TAG, "Unknown tag: " + tag2);
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e8) {
            }
        }
        LockPatternUtils utils = new LockPatternUtils(this.mContext);
        if (utils.getActivePasswordQuality() < policy.mActivePasswordQuality) {
            Slog.w(TAG, "Active password quality 0x" + Integer.toHexString(policy.mActivePasswordQuality) + " does not match actual quality 0x" + Integer.toHexString(utils.getActivePasswordQuality()));
            policy.mActivePasswordQuality = 0;
            policy.mActivePasswordLength = 0;
            policy.mActivePasswordUpperCase = 0;
            policy.mActivePasswordLowerCase = 0;
            policy.mActivePasswordLetters = 0;
            policy.mActivePasswordNumeric = 0;
            policy.mActivePasswordSymbols = 0;
            policy.mActivePasswordNonLetter = 0;
        }
        validatePasswordOwnerLocked(policy);
        syncDeviceCapabilitiesLocked(policy);
        updateMaximumTimeToLockLocked(policy);
    }

    static void validateQualityConstant(int quality) {
        switch (quality) {
            case 0:
            case 32768:
            case 65536:
            case 131072:
            case 262144:
            case 327680:
            case 393216:
                return;
            default:
                throw new IllegalArgumentException("Invalid quality constant: 0x" + Integer.toHexString(quality));
        }
    }

    void validatePasswordOwnerLocked(DevicePolicyData policy) {
        if (policy.mPasswordOwner >= 0) {
            boolean haveOwner = false;
            int i = policy.mAdminList.size() - 1;
            while (true) {
                if (i < 0) {
                    break;
                } else if (policy.mAdminList.get(i).getUid() != policy.mPasswordOwner) {
                    i--;
                } else {
                    haveOwner = true;
                    break;
                }
            }
            if (!haveOwner) {
                Slog.w(TAG, "Previous password owner " + policy.mPasswordOwner + " no longer active; disabling");
                policy.mPasswordOwner = -1;
            }
        }
    }

    public void systemReady() {
        if (!this.mHasFeature) {
            return;
        }
        synchronized (this) {
            loadSettingsLocked(getUserData(0), 0);
            loadDeviceOwner();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePasswordExpirationNotification(DevicePolicyData policy) {
        synchronized (this) {
            long now = System.currentTimeMillis();
            int N = policy.mAdminList.size();
            if (N <= 0) {
                return;
            }
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin = policy.mAdminList.get(i);
                if (admin.info.usesPolicy(6) && admin.passwordExpirationTimeout > 0 && admin.passwordExpirationDate > 0 && now >= admin.passwordExpirationDate - EXPIRATION_GRACE_PERIOD_MS) {
                    sendAdminCommandLocked(admin, DeviceAdminReceiver.ACTION_PASSWORD_EXPIRING);
                }
            }
            setExpirationAlarmCheckLocked(this.mContext, policy);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void manageMonitoringCertificateNotification(Intent intent) {
        String contentText;
        int smallIconId;
        NotificationManager notificationManager = getNotificationManager();
        boolean hasCert = DevicePolicyManager.hasAnyCaCertsInstalled();
        if (!hasCert) {
            if (intent.getAction().equals(KeyChain.ACTION_STORAGE_CHANGED)) {
                UserManager um = (UserManager) this.mContext.getSystemService("user");
                for (UserInfo user : um.getUsers()) {
                    notificationManager.cancelAsUser(null, 17039535, user.getUserHandle());
                }
                return;
            }
            return;
        }
        boolean isManaged = getDeviceOwner() != null;
        if (isManaged) {
            contentText = this.mContext.getString(R.string.ssl_ca_cert_noti_managed, getDeviceOwnerName());
            smallIconId = 17302954;
        } else {
            contentText = this.mContext.getString(R.string.ssl_ca_cert_noti_by_unknown);
            smallIconId = 17301642;
        }
        Intent dialogIntent = new Intent(Settings.ACTION_MONITORING_CERT_INFO);
        dialogIntent.setFlags(268468224);
        dialogIntent.setPackage("com.android.settings");
        PendingIntent notifyIntent = PendingIntent.getActivityAsUser(this.mContext, 0, dialogIntent, 134217728, null, UserHandle.CURRENT);
        Notification noti = new Notification.Builder(this.mContext).setSmallIcon(smallIconId).setContentTitle(this.mContext.getString(17039535)).setContentText(contentText).setContentIntent(notifyIntent).setPriority(1).setShowWhen(false).build();
        if (intent.getAction().equals(KeyChain.ACTION_STORAGE_CHANGED)) {
            UserManager um2 = (UserManager) this.mContext.getSystemService("user");
            for (UserInfo user2 : um2.getUsers()) {
                notificationManager.notifyAsUser(null, 17039535, noti, user2.getUserHandle());
            }
            return;
        }
        notificationManager.notifyAsUser(null, 17039535, noti, UserHandle.CURRENT);
    }

    @Override // android.app.admin.IDevicePolicyManager
    public boolean isAdminActive(ComponentName adminReceiver, int userHandle) {
        boolean z;
        if (!this.mHasFeature) {
            return false;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            z = getActiveAdminUncheckedLocked(adminReceiver, userHandle) != null;
        }
        return z;
    }

    @Override // android.app.admin.IDevicePolicyManager
    public boolean hasGrantedPolicy(ComponentName adminReceiver, int policyId, int userHandle) {
        boolean usesPolicy;
        if (!this.mHasFeature) {
            return false;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            ActiveAdmin administrator = getActiveAdminUncheckedLocked(adminReceiver, userHandle);
            if (administrator == null) {
                throw new SecurityException("No active admin " + adminReceiver);
            }
            usesPolicy = administrator.info.usesPolicy(policyId);
        }
        return usesPolicy;
    }

    @Override // android.app.admin.IDevicePolicyManager
    public List<ComponentName> getActiveAdmins(int userHandle) {
        if (!this.mHasFeature) {
            return Collections.EMPTY_LIST;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            if (N <= 0) {
                return null;
            }
            ArrayList<ComponentName> res = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                res.add(policy.mAdminList.get(i).info.getComponent());
            }
            return res;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public boolean packageHasActiveAdmins(String packageName, int userHandle) {
        if (!this.mHasFeature) {
            return false;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                if (policy.mAdminList.get(i).info.getPackageName().equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordQuality(ComponentName who, int quality, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        validateQualityConstant(quality);
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0);
            if (ap.passwordQuality != quality) {
                ap.passwordQuality = quality;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getPasswordQuality(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            int mode = 0;
            DevicePolicyData policy = getUserData(userHandle);
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.passwordQuality : 0;
            }
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (mode < admin2.passwordQuality) {
                    mode = admin2.passwordQuality;
                }
            }
            return mode;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordMinimumLength(ComponentName who, int length, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0);
            if (ap.minimumPasswordLength != length) {
                ap.minimumPasswordLength = length;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getPasswordMinimumLength(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            DevicePolicyData policy = getUserData(userHandle);
            int length = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.minimumPasswordLength : 0;
            }
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (length < admin2.minimumPasswordLength) {
                    length = admin2.minimumPasswordLength;
                }
            }
            return length;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordHistoryLength(ComponentName who, int length, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0);
            if (ap.passwordHistoryLength != length) {
                ap.passwordHistoryLength = length;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getPasswordHistoryLength(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            DevicePolicyData policy = getUserData(userHandle);
            int length = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.passwordHistoryLength : 0;
            }
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (length < admin2.passwordHistoryLength) {
                    length = admin2.passwordHistoryLength;
                }
            }
            return length;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordExpirationTimeout(ComponentName who, long timeout, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            if (timeout < 0) {
                throw new IllegalArgumentException("Timeout must be >= 0 ms");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 6);
            long expiration = timeout > 0 ? timeout + System.currentTimeMillis() : 0L;
            ap.passwordExpirationDate = expiration;
            ap.passwordExpirationTimeout = timeout;
            if (timeout > 0) {
                Slog.w(TAG, "setPasswordExpiration(): password will expire on " + DateFormat.getDateTimeInstance(2, 2).format(new Date(expiration)));
            }
            saveSettingsLocked(userHandle);
            setExpirationAlarmCheckLocked(this.mContext, getUserData(userHandle));
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public long getPasswordExpirationTimeout(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0L;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.passwordExpirationTimeout : 0L;
            }
            long timeout = 0;
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (timeout == 0 || (admin2.passwordExpirationTimeout != 0 && timeout > admin2.passwordExpirationTimeout)) {
                    timeout = admin2.passwordExpirationTimeout;
                }
            }
            return timeout;
        }
    }

    private long getPasswordExpirationLocked(ComponentName who, int userHandle) {
        if (who != null) {
            ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
            if (admin != null) {
                return admin.passwordExpirationDate;
            }
            return 0L;
        }
        long timeout = 0;
        DevicePolicyData policy = getUserData(userHandle);
        int N = policy.mAdminList.size();
        for (int i = 0; i < N; i++) {
            ActiveAdmin admin2 = policy.mAdminList.get(i);
            if (timeout == 0 || (admin2.passwordExpirationDate != 0 && timeout > admin2.passwordExpirationDate)) {
                timeout = admin2.passwordExpirationDate;
            }
        }
        return timeout;
    }

    @Override // android.app.admin.IDevicePolicyManager
    public long getPasswordExpiration(ComponentName who, int userHandle) {
        long passwordExpirationLocked;
        if (!this.mHasFeature) {
            return 0L;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            passwordExpirationLocked = getPasswordExpirationLocked(who, userHandle);
        }
        return passwordExpirationLocked;
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordMinimumUpperCase(ComponentName who, int length, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0);
            if (ap.minimumPasswordUpperCase != length) {
                ap.minimumPasswordUpperCase = length;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getPasswordMinimumUpperCase(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            int length = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.minimumPasswordUpperCase : 0;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (length < admin2.minimumPasswordUpperCase) {
                    length = admin2.minimumPasswordUpperCase;
                }
            }
            return length;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordMinimumLowerCase(ComponentName who, int length, int userHandle) {
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0);
            if (ap.minimumPasswordLowerCase != length) {
                ap.minimumPasswordLowerCase = length;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getPasswordMinimumLowerCase(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            int length = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.minimumPasswordLowerCase : 0;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (length < admin2.minimumPasswordLowerCase) {
                    length = admin2.minimumPasswordLowerCase;
                }
            }
            return length;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordMinimumLetters(ComponentName who, int length, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0);
            if (ap.minimumPasswordLetters != length) {
                ap.minimumPasswordLetters = length;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getPasswordMinimumLetters(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            int length = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.minimumPasswordLetters : 0;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (length < admin2.minimumPasswordLetters) {
                    length = admin2.minimumPasswordLetters;
                }
            }
            return length;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordMinimumNumeric(ComponentName who, int length, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0);
            if (ap.minimumPasswordNumeric != length) {
                ap.minimumPasswordNumeric = length;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getPasswordMinimumNumeric(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            int length = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.minimumPasswordNumeric : 0;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (length < admin2.minimumPasswordNumeric) {
                    length = admin2.minimumPasswordNumeric;
                }
            }
            return length;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordMinimumSymbols(ComponentName who, int length, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0);
            if (ap.minimumPasswordSymbols != length) {
                ap.minimumPasswordSymbols = length;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getPasswordMinimumSymbols(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            int length = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.minimumPasswordSymbols : 0;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (length < admin2.minimumPasswordSymbols) {
                    length = admin2.minimumPasswordSymbols;
                }
            }
            return length;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setPasswordMinimumNonLetter(ComponentName who, int length, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 0);
            if (ap.minimumPasswordNonLetter != length) {
                ap.minimumPasswordNonLetter = length;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getPasswordMinimumNonLetter(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            int length = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.minimumPasswordNonLetter : 0;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (length < admin2.minimumPasswordNonLetter) {
                    length = admin2.minimumPasswordNonLetter;
                }
            }
            return length;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public boolean isActivePasswordSufficient(int userHandle) {
        if (!this.mHasFeature) {
            return true;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            DevicePolicyData policy = getUserData(userHandle);
            getActiveAdminForCallerLocked(null, 0);
            if (policy.mActivePasswordQuality < getPasswordQuality(null, userHandle) || policy.mActivePasswordLength < getPasswordMinimumLength(null, userHandle)) {
                return false;
            }
            if (policy.mActivePasswordQuality != 393216) {
                return true;
            }
            return policy.mActivePasswordUpperCase >= getPasswordMinimumUpperCase(null, userHandle) && policy.mActivePasswordLowerCase >= getPasswordMinimumLowerCase(null, userHandle) && policy.mActivePasswordLetters >= getPasswordMinimumLetters(null, userHandle) && policy.mActivePasswordNumeric >= getPasswordMinimumNumeric(null, userHandle) && policy.mActivePasswordSymbols >= getPasswordMinimumSymbols(null, userHandle) && policy.mActivePasswordNonLetter >= getPasswordMinimumNonLetter(null, userHandle);
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getCurrentFailedPasswordAttempts(int userHandle) {
        int i;
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            getActiveAdminForCallerLocked(null, 1);
            i = getUserData(userHandle).mFailedPasswordAttempts;
        }
        return i;
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setMaximumFailedPasswordsForWipe(ComponentName who, int num, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            getActiveAdminForCallerLocked(who, 4);
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 1);
            if (ap.maximumFailedPasswordsForWipe != num) {
                ap.maximumFailedPasswordsForWipe = num;
                saveSettingsLocked(userHandle);
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getMaximumFailedPasswordsForWipe(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            DevicePolicyData policy = getUserData(userHandle);
            int count = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.maximumFailedPasswordsForWipe : 0;
            }
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (count == 0) {
                    count = admin2.maximumFailedPasswordsForWipe;
                } else if (admin2.maximumFailedPasswordsForWipe != 0 && count > admin2.maximumFailedPasswordsForWipe) {
                    count = admin2.maximumFailedPasswordsForWipe;
                }
            }
            return count;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setMaximumTimeToLock(ComponentName who, long timeMs, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 3);
            if (ap.maximumTimeToUnlock != timeMs) {
                ap.maximumTimeToUnlock = timeMs;
                saveSettingsLocked(userHandle);
                updateMaximumTimeToLockLocked(getUserData(userHandle));
            }
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public long getMaximumTimeToLock(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0L;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            long time = 0;
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.maximumTimeToUnlock : 0L;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin2 = policy.mAdminList.get(i);
                if (time == 0) {
                    time = admin2.maximumTimeToUnlock;
                } else if (admin2.maximumTimeToUnlock != 0 && time > admin2.maximumTimeToUnlock) {
                    time = admin2.maximumTimeToUnlock;
                }
            }
            return time;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void lockNow() {
        if (!this.mHasFeature) {
            return;
        }
        synchronized (this) {
            getActiveAdminForCallerLocked(null, 3);
            lockNowUnchecked();
        }
    }

    private boolean isExtStorageEncrypted() {
        String state = SystemProperties.get("vold.decrypt");
        return !"".equals(state);
    }

    private static X509Certificate parseCert(byte[] certBuffer) throws CertificateException, IOException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(certBuffer));
    }

    void wipeDataLocked(int flags) {
        boolean forceExtWipe = !Environment.isExternalStorageRemovable() && isExtStorageEncrypted();
        boolean wipeExtRequested = (flags & 1) != 0;
        if ((forceExtWipe || wipeExtRequested) && !Environment.isExternalStorageEmulated()) {
            Intent intent = new Intent("com.android.internal.os.storage.FORMAT_AND_FACTORY_RESET");
            intent.putExtra("always_reset", true);
            intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
            this.mWakeLock.acquire(10000L);
            this.mContext.startService(intent);
            return;
        }
        try {
            RecoverySystem.rebootWipeUserData(this.mContext);
        } catch (IOException e) {
            Slog.w(TAG, "Failed requesting data wipe", e);
        }
    }

    private void wipeDeviceOrUserLocked(int flags, final int userHandle) {
        if (userHandle == 0) {
            wipeDataLocked(flags);
            return;
        }
        lockNowUnchecked();
        this.mHandler.post(new Runnable() { // from class: com.android.server.DevicePolicyManagerService.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ActivityManagerNative.getDefault().switchUser(0);
                    ((UserManager) DevicePolicyManagerService.this.mContext.getSystemService("user")).removeUser(userHandle);
                } catch (RemoteException e) {
                }
            }
        });
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void getRemoveWarning(ComponentName comp, final RemoteCallback result, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.BIND_DEVICE_ADMIN, null);
        synchronized (this) {
            ActiveAdmin admin = getActiveAdminUncheckedLocked(comp, userHandle);
            if (admin == null) {
                try {
                    result.sendResult(null);
                } catch (RemoteException e) {
                }
                return;
            }
            Intent intent = new Intent(DeviceAdminReceiver.ACTION_DEVICE_ADMIN_DISABLE_REQUESTED);
            intent.setComponent(admin.info.getComponent());
            this.mContext.sendOrderedBroadcastAsUser(intent, new UserHandle(userHandle), null, new BroadcastReceiver() { // from class: com.android.server.DevicePolicyManagerService.4
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent2) {
                    try {
                        result.sendResult(getResultExtras(false));
                    } catch (RemoteException e2) {
                    }
                }
            }, null, -1, null, null);
        }
    }

    private void updatePasswordExpirationsLocked(int userHandle) {
        DevicePolicyData policy = getUserData(userHandle);
        int N = policy.mAdminList.size();
        if (N > 0) {
            for (int i = 0; i < N; i++) {
                ActiveAdmin admin = policy.mAdminList.get(i);
                if (admin.info.usesPolicy(6)) {
                    long timeout = admin.passwordExpirationTimeout;
                    long expiration = timeout > 0 ? timeout + System.currentTimeMillis() : 0L;
                    admin.passwordExpirationDate = expiration;
                }
            }
            saveSettingsLocked(userHandle);
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public ComponentName setGlobalProxy(ComponentName who, String proxySpec, String exclusionList, int userHandle) {
        if (!this.mHasFeature) {
            return null;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            DevicePolicyData policy = getUserData(0);
            ActiveAdmin admin = getActiveAdminForCallerLocked(who, 5);
            Set<ComponentName> compSet = policy.mAdminMap.keySet();
            for (ComponentName component : compSet) {
                ActiveAdmin ap = policy.mAdminMap.get(component);
                if (ap.specifiesGlobalProxy && !component.equals(who)) {
                    return component;
                }
            }
            if (UserHandle.getCallingUserId() != 0) {
                Slog.w(TAG, "Only the owner is allowed to set the global proxy. User " + userHandle + " is not permitted.");
                return null;
            }
            if (proxySpec == null) {
                admin.specifiesGlobalProxy = false;
                admin.globalProxySpec = null;
                admin.globalProxyExclusionList = null;
            } else {
                admin.specifiesGlobalProxy = true;
                admin.globalProxySpec = proxySpec;
                admin.globalProxyExclusionList = exclusionList;
            }
            long origId = Binder.clearCallingIdentity();
            resetGlobalProxyLocked(policy);
            Binder.restoreCallingIdentity(origId);
            return null;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public ComponentName getGlobalProxyAdmin(int userHandle) {
        if (!this.mHasFeature) {
            return null;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            DevicePolicyData policy = getUserData(0);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                ActiveAdmin ap = policy.mAdminList.get(i);
                if (ap.specifiesGlobalProxy) {
                    return ap.info.getComponent();
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetGlobalProxyLocked(DevicePolicyData policy) {
        int N = policy.mAdminList.size();
        for (int i = 0; i < N; i++) {
            ActiveAdmin ap = policy.mAdminList.get(i);
            if (ap.specifiesGlobalProxy) {
                saveGlobalProxyLocked(ap.globalProxySpec, ap.globalProxyExclusionList);
                return;
            }
        }
        saveGlobalProxyLocked(null, null);
    }

    private void saveGlobalProxyLocked(String proxySpec, String exclusionList) {
        if (exclusionList == null) {
            exclusionList = "";
        }
        if (proxySpec == null) {
            proxySpec = "";
        }
        String[] data = proxySpec.trim().split(Separators.COLON);
        int proxyPort = 8080;
        if (data.length > 1) {
            try {
                proxyPort = Integer.parseInt(data[1]);
            } catch (NumberFormatException e) {
            }
        }
        String exclusionList2 = exclusionList.trim();
        ContentResolver res = this.mContext.getContentResolver();
        Settings.Global.putString(res, Settings.Global.GLOBAL_HTTP_PROXY_HOST, data[0]);
        Settings.Global.putInt(res, Settings.Global.GLOBAL_HTTP_PROXY_PORT, proxyPort);
        Settings.Global.putString(res, Settings.Global.GLOBAL_HTTP_PROXY_EXCLUSION_LIST, exclusionList2);
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int setStorageEncryption(ComponentName who, boolean encrypt, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            if (userHandle != 0 || UserHandle.getCallingUserId() != 0) {
                Slog.w(TAG, "Only owner is allowed to set storage encryption. User " + UserHandle.getCallingUserId() + " is not permitted.");
                return 0;
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 7);
            if (!isEncryptionSupported()) {
                return 0;
            }
            if (ap.encryptionRequested != encrypt) {
                ap.encryptionRequested = encrypt;
                saveSettingsLocked(userHandle);
            }
            DevicePolicyData policy = getUserData(0);
            boolean newRequested = false;
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                newRequested |= policy.mAdminList.get(i).encryptionRequested;
            }
            setEncryptionRequested(newRequested);
            return newRequested ? 3 : 1;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public boolean getStorageEncryption(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return false;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who != null) {
                ActiveAdmin ap = getActiveAdminUncheckedLocked(who, userHandle);
                return ap != null ? ap.encryptionRequested : false;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                if (policy.mAdminList.get(i).encryptionRequested) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getStorageEncryptionStatus(int userHandle) {
        if (!this.mHasFeature) {
        }
        enforceCrossUserPermission(userHandle);
        return getEncryptionStatus();
    }

    private boolean isEncryptionSupported() {
        return getEncryptionStatus() != 0;
    }

    private int getEncryptionStatus() {
        String status = SystemProperties.get("ro.crypto.state", "unsupported");
        if ("encrypted".equalsIgnoreCase(status)) {
            return 3;
        }
        if ("unencrypted".equalsIgnoreCase(status)) {
            return 1;
        }
        return 0;
    }

    private void setEncryptionRequested(boolean encrypt) {
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setCameraDisabled(ComponentName who, boolean disabled, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 8);
            if (ap.disableCamera != disabled) {
                ap.disableCamera = disabled;
                saveSettingsLocked(userHandle);
            }
            syncDeviceCapabilitiesLocked(getUserData(userHandle));
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public boolean getCameraDisabled(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return false;
        }
        synchronized (this) {
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.disableCamera : false;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            for (int i = 0; i < N; i++) {
                if (policy.mAdminList.get(i).disableCamera) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public void setKeyguardDisabledFeatures(ComponentName who, int which, int userHandle) {
        if (!this.mHasFeature) {
            return;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who == null) {
                throw new NullPointerException("ComponentName is null");
            }
            ActiveAdmin ap = getActiveAdminForCallerLocked(who, 9);
            if (ap.disabledKeyguardFeatures != which) {
                ap.disabledKeyguardFeatures = which;
                saveSettingsLocked(userHandle);
            }
            syncDeviceCapabilitiesLocked(getUserData(userHandle));
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public int getKeyguardDisabledFeatures(ComponentName who, int userHandle) {
        if (!this.mHasFeature) {
            return 0;
        }
        enforceCrossUserPermission(userHandle);
        synchronized (this) {
            if (who != null) {
                ActiveAdmin admin = getActiveAdminUncheckedLocked(who, userHandle);
                return admin != null ? admin.disabledKeyguardFeatures : 0;
            }
            DevicePolicyData policy = getUserData(userHandle);
            int N = policy.mAdminList.size();
            int which = 0;
            for (int i = 0; i < N; i++) {
                which |= policy.mAdminList.get(i).disabledKeyguardFeatures;
            }
            return which;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public boolean setDeviceOwner(String packageName, String ownerName) {
        if (!this.mHasFeature) {
            return false;
        }
        if (packageName == null || !DeviceOwner.isInstalled(packageName, this.mContext.getPackageManager())) {
            throw new IllegalArgumentException("Invalid package name " + packageName + " for device owner");
        }
        synchronized (this) {
            if (this.mDeviceOwner == null && !isDeviceProvisioned()) {
                this.mDeviceOwner = new DeviceOwner(packageName, ownerName);
                this.mDeviceOwner.writeOwnerFile();
            } else {
                throw new IllegalStateException("Trying to set device owner to " + packageName + ", owner=" + this.mDeviceOwner.getPackageName() + ", device_provisioned=" + isDeviceProvisioned());
            }
        }
        return true;
    }

    @Override // android.app.admin.IDevicePolicyManager
    public boolean isDeviceOwner(String packageName) {
        boolean z;
        if (!this.mHasFeature) {
            return false;
        }
        synchronized (this) {
            z = this.mDeviceOwner != null && this.mDeviceOwner.getPackageName().equals(packageName);
        }
        return z;
    }

    @Override // android.app.admin.IDevicePolicyManager
    public String getDeviceOwner() {
        if (!this.mHasFeature) {
            return null;
        }
        synchronized (this) {
            if (this.mDeviceOwner != null) {
                return this.mDeviceOwner.getPackageName();
            }
            return null;
        }
    }

    @Override // android.app.admin.IDevicePolicyManager
    public String getDeviceOwnerName() {
        if (!this.mHasFeature) {
            return null;
        }
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_USERS, null);
        synchronized (this) {
            if (this.mDeviceOwner != null) {
                return this.mDeviceOwner.getName();
            }
            return null;
        }
    }

    private boolean isDeviceProvisioned() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) > 0;
    }

    private void enforceCrossUserPermission(int userHandle) {
        if (userHandle < 0) {
            throw new IllegalArgumentException("Invalid userId " + userHandle);
        }
        int callingUid = Binder.getCallingUid();
        if (userHandle != UserHandle.getUserId(callingUid) && callingUid != 1000 && callingUid != 0) {
            this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL, "Must be system or have INTERACT_ACROSS_USERS_FULL permission");
        }
    }

    private void enableIfNecessary(String packageName, int userId) {
        try {
            IPackageManager ipm = AppGlobals.getPackageManager();
            ApplicationInfo ai = ipm.getApplicationInfo(packageName, 32768, userId);
            if (ai.enabledSetting == 4) {
                ipm.setApplicationEnabledSetting(packageName, 0, 1, userId, "DevicePolicyManager");
            }
        } catch (RemoteException e) {
        }
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump DevicePolicyManagerService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        Printer p = new PrintWriterPrinter(pw);
        synchronized (this) {
            p.println("Current Device Policy Manager state:");
            int userCount = this.mUserData.size();
            for (int u = 0; u < userCount; u++) {
                DevicePolicyData policy = getUserData(this.mUserData.keyAt(u));
                p.println("  Enabled Device Admins (User " + policy.mUserHandle + "):");
                int N = policy.mAdminList.size();
                for (int i = 0; i < N; i++) {
                    ActiveAdmin ap = policy.mAdminList.get(i);
                    if (ap != null) {
                        pw.print("  ");
                        pw.print(ap.info.getComponent().flattenToShortString());
                        pw.println(Separators.COLON);
                        ap.dump("    ", pw);
                    }
                }
                pw.println(Separators.SP);
                pw.print("  mPasswordOwner=");
                pw.println(policy.mPasswordOwner);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: DevicePolicyManagerService$DeviceOwner.class */
    public static class DeviceOwner {
        private static final String DEVICE_OWNER_XML = "device_owner.xml";
        private static final String TAG_DEVICE_OWNER = "device-owner";
        private static final String ATTR_NAME = "name";
        private static final String ATTR_PACKAGE = "package";
        private String mPackageName;
        private String mOwnerName;

        DeviceOwner() {
            readOwnerFile();
        }

        DeviceOwner(String packageName, String ownerName) {
            this.mPackageName = packageName;
            this.mOwnerName = ownerName;
        }

        static boolean isRegistered() {
            return new File(Environment.getSystemSecureDirectory(), DEVICE_OWNER_XML).exists();
        }

        String getPackageName() {
            return this.mPackageName;
        }

        String getName() {
            return this.mOwnerName;
        }

        static boolean isInstalled(String packageName, PackageManager pm) {
            try {
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                if (pi != null) {
                    if ((pi.applicationInfo.flags & 1) != 0) {
                        return true;
                    }
                    return false;
                }
                return false;
            } catch (PackageManager.NameNotFoundException e) {
                Slog.w(DevicePolicyManagerService.TAG, "Device Owner package " + packageName + " not installed.");
                return false;
            }
        }

        void readOwnerFile() {
            AtomicFile file = new AtomicFile(new File(Environment.getSystemSecureDirectory(), DEVICE_OWNER_XML));
            try {
                FileInputStream input = file.openRead();
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(input, null);
                while (true) {
                    int type = parser.next();
                    if (type == 1 || type == 2) {
                        break;
                    }
                }
                String tag = parser.getName();
                if (!TAG_DEVICE_OWNER.equals(tag)) {
                    throw new XmlPullParserException("Device Owner file does not start with device-owner tag: found " + tag);
                }
                this.mPackageName = parser.getAttributeValue(null, "package");
                this.mOwnerName = parser.getAttributeValue(null, "name");
                input.close();
            } catch (IOException ioe) {
                Slog.e(DevicePolicyManagerService.TAG, "IO Exception when reading device-owner file\n" + ioe);
            } catch (XmlPullParserException xppe) {
                Slog.e(DevicePolicyManagerService.TAG, "Error parsing device-owner file\n" + xppe);
            }
        }

        void writeOwnerFile() {
            synchronized (this) {
                writeOwnerFileLocked();
            }
        }

        private void writeOwnerFileLocked() {
            AtomicFile file = new AtomicFile(new File(Environment.getSystemSecureDirectory(), DEVICE_OWNER_XML));
            try {
                FileOutputStream output = file.startWrite();
                XmlSerializer out = new FastXmlSerializer();
                out.setOutput(output, "utf-8");
                out.startDocument(null, true);
                out.startTag(null, TAG_DEVICE_OWNER);
                out.attribute(null, "package", this.mPackageName);
                if (this.mOwnerName != null) {
                    out.attribute(null, "name", this.mOwnerName);
                }
                out.endTag(null, TAG_DEVICE_OWNER);
                out.endDocument();
                out.flush();
                file.finishWrite(output);
            } catch (IOException ioe) {
                Slog.e(DevicePolicyManagerService.TAG, "IO Exception when writing device-owner file\n" + ioe);
            }
        }
    }
}