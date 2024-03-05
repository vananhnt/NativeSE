package com.android.server.dreams;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.util.Slog;
import com.android.internal.util.DumpUtils;
import com.android.server.dreams.DreamController;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import libcore.util.Objects;

/* loaded from: DreamManagerService.class */
public final class DreamManagerService extends IDreamManager.Stub {
    private static final boolean DEBUG = false;
    private static final String TAG = "DreamManagerService";
    private final Context mContext;
    private final DreamHandler mHandler;
    private final DreamController mController;
    private final PowerManager mPowerManager;
    private Binder mCurrentDreamToken;
    private ComponentName mCurrentDreamName;
    private int mCurrentDreamUserId;
    private boolean mCurrentDreamIsTest;
    private final Object mLock = new Object();
    private final DreamController.Listener mControllerListener = new DreamController.Listener() { // from class: com.android.server.dreams.DreamManagerService.5
        @Override // com.android.server.dreams.DreamController.Listener
        public void onDreamStopped(Binder token) {
            synchronized (DreamManagerService.this.mLock) {
                if (DreamManagerService.this.mCurrentDreamToken == token) {
                    DreamManagerService.this.cleanupDreamLocked();
                }
            }
        }
    };

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.getDreamComponents():android.content.ComponentName[], file: DreamManagerService.class
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
    @Override // android.service.dreams.IDreamManager
    public android.content.ComponentName[] getDreamComponents() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.getDreamComponents():android.content.ComponentName[], file: DreamManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.dreams.DreamManagerService.getDreamComponents():android.content.ComponentName[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.setDreamComponents(android.content.ComponentName[]):void, file: DreamManagerService.class
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
    @Override // android.service.dreams.IDreamManager
    public void setDreamComponents(android.content.ComponentName[] r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.setDreamComponents(android.content.ComponentName[]):void, file: DreamManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.dreams.DreamManagerService.setDreamComponents(android.content.ComponentName[]):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.getDefaultDreamComponent():android.content.ComponentName, file: DreamManagerService.class
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
    @Override // android.service.dreams.IDreamManager
    public android.content.ComponentName getDefaultDreamComponent() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.getDefaultDreamComponent():android.content.ComponentName, file: DreamManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.dreams.DreamManagerService.getDefaultDreamComponent():android.content.ComponentName");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.dream():void, file: DreamManagerService.class
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
    @Override // android.service.dreams.IDreamManager
    public void dream() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.dream():void, file: DreamManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.dreams.DreamManagerService.dream():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.testDream(android.content.ComponentName):void, file: DreamManagerService.class
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
    @Override // android.service.dreams.IDreamManager
    public void testDream(android.content.ComponentName r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.testDream(android.content.ComponentName):void, file: DreamManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.dreams.DreamManagerService.testDream(android.content.ComponentName):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.awaken():void, file: DreamManagerService.class
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
    @Override // android.service.dreams.IDreamManager
    public void awaken() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.awaken():void, file: DreamManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.dreams.DreamManagerService.awaken():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.finishSelf(android.os.IBinder):void, file: DreamManagerService.class
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
    @Override // android.service.dreams.IDreamManager
    public void finishSelf(android.os.IBinder r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.dreams.DreamManagerService.finishSelf(android.os.IBinder):void, file: DreamManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.dreams.DreamManagerService.finishSelf(android.os.IBinder):void");
    }

    public DreamManagerService(Context context, Handler mainHandler) {
        this.mContext = context;
        this.mHandler = new DreamHandler(mainHandler.getLooper());
        this.mController = new DreamController(context, this.mHandler, this.mControllerListener);
        this.mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    public void systemRunning() {
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.dreams.DreamManagerService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                synchronized (DreamManagerService.this.mLock) {
                    DreamManagerService.this.stopDreamLocked();
                }
            }
        }, new IntentFilter(Intent.ACTION_USER_SWITCHED), null, this.mHandler);
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump DreamManager from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("DREAM MANAGER (dumpsys dreams)");
        pw.println();
        pw.println("mCurrentDreamToken=" + this.mCurrentDreamToken);
        pw.println("mCurrentDreamName=" + this.mCurrentDreamName);
        pw.println("mCurrentDreamUserId=" + this.mCurrentDreamUserId);
        pw.println("mCurrentDreamIsTest=" + this.mCurrentDreamIsTest);
        pw.println();
        DumpUtils.dumpAsync(this.mHandler, new DumpUtils.Dump() { // from class: com.android.server.dreams.DreamManagerService.2
            @Override // com.android.internal.util.DumpUtils.Dump
            public void dump(PrintWriter pw2) {
                DreamManagerService.this.mController.dump(pw2);
            }
        }, pw, 200L);
    }

    @Override // android.service.dreams.IDreamManager
    public boolean isDreaming() {
        boolean z;
        checkPermission(Manifest.permission.READ_DREAM_STATE);
        synchronized (this.mLock) {
            z = (this.mCurrentDreamToken == null || this.mCurrentDreamIsTest) ? false : true;
        }
        return z;
    }

    public void startDream() {
        int userId = ActivityManager.getCurrentUser();
        ComponentName dream = chooseDreamForUser(userId);
        if (dream != null) {
            synchronized (this.mLock) {
                startDreamLocked(dream, false, userId);
            }
        }
    }

    public void stopDream() {
        synchronized (this.mLock) {
            stopDreamLocked();
        }
    }

    private ComponentName chooseDreamForUser(int userId) {
        ComponentName[] dreams = getDreamComponentsForUser(userId);
        if (dreams == null || dreams.length == 0) {
            return null;
        }
        return dreams[0];
    }

    private ComponentName[] getDreamComponentsForUser(int userId) {
        ComponentName defaultDream;
        String names = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), Settings.Secure.SCREENSAVER_COMPONENTS, userId);
        ComponentName[] components = componentsFromString(names);
        List<ComponentName> validComponents = new ArrayList<>();
        if (components != null) {
            for (ComponentName component : components) {
                if (serviceExists(component)) {
                    validComponents.add(component);
                } else {
                    Slog.w(TAG, "Dream " + component + " does not exist");
                }
            }
        }
        if (validComponents.isEmpty() && (defaultDream = getDefaultDreamComponent()) != null) {
            Slog.w(TAG, "Falling back to default dream " + defaultDream);
            validComponents.add(defaultDream);
        }
        return (ComponentName[]) validComponents.toArray(new ComponentName[validComponents.size()]);
    }

    private boolean serviceExists(ComponentName name) {
        if (name != null) {
            try {
                if (this.mContext.getPackageManager().getServiceInfo(name, 0) != null) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }
        return false;
    }

    private void startDreamLocked(final ComponentName name, final boolean isTest, final int userId) {
        if (Objects.equal(this.mCurrentDreamName, name) && this.mCurrentDreamIsTest == isTest && this.mCurrentDreamUserId == userId) {
            return;
        }
        stopDreamLocked();
        final Binder newToken = new Binder();
        this.mCurrentDreamToken = newToken;
        this.mCurrentDreamName = name;
        this.mCurrentDreamIsTest = isTest;
        this.mCurrentDreamUserId = userId;
        this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamManagerService.3
            @Override // java.lang.Runnable
            public void run() {
                DreamManagerService.this.mController.startDream(newToken, name, isTest, userId);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopDreamLocked() {
        if (this.mCurrentDreamToken != null) {
            cleanupDreamLocked();
            this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamManagerService.4
                @Override // java.lang.Runnable
                public void run() {
                    DreamManagerService.this.mController.stopDream();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanupDreamLocked() {
        this.mCurrentDreamToken = null;
        this.mCurrentDreamName = null;
        this.mCurrentDreamIsTest = false;
        this.mCurrentDreamUserId = 0;
    }

    private void checkPermission(String permission) {
        if (this.mContext.checkCallingOrSelfPermission(permission) != 0) {
            throw new SecurityException("Access denied to process: " + Binder.getCallingPid() + ", must have permission " + permission);
        }
    }

    private static String componentsToString(ComponentName[] componentNames) {
        StringBuilder names = new StringBuilder();
        if (componentNames != null) {
            for (ComponentName componentName : componentNames) {
                if (names.length() > 0) {
                    names.append(',');
                }
                names.append(componentName.flattenToString());
            }
        }
        return names.toString();
    }

    private static ComponentName[] componentsFromString(String names) {
        if (names == null) {
            return null;
        }
        String[] namesArray = names.split(Separators.COMMA);
        ComponentName[] componentNames = new ComponentName[namesArray.length];
        for (int i = 0; i < namesArray.length; i++) {
            componentNames[i] = ComponentName.unflattenFromString(namesArray[i]);
        }
        return componentNames;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DreamManagerService$DreamHandler.class */
    public final class DreamHandler extends Handler {
        public DreamHandler(Looper looper) {
            super(looper, null, true);
        }
    }
}