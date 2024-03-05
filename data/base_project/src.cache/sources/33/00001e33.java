package com.android.server.display;

import android.Manifest;
import android.content.Context;
import android.hardware.display.IDisplayManager;
import android.hardware.display.IDisplayManagerCallback;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import android.util.SparseArray;
import android.view.DisplayInfo;
import com.android.internal.R;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.UiThread;
import com.android.server.display.DisplayAdapter;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: DisplayManagerService.class */
public final class DisplayManagerService extends IDisplayManager.Stub {
    private static final String TAG = "DisplayManagerService";
    private static final boolean DEBUG = false;
    private static final String FORCE_WIFI_DISPLAY_ENABLE = "persist.debug.wfd.enable";
    private static final String SYSTEM_HEADLESS = "ro.config.headless";
    private static final long WAIT_FOR_DEFAULT_DISPLAY_TIMEOUT = 10000;
    private static final int MSG_REGISTER_DEFAULT_DISPLAY_ADAPTER = 1;
    private static final int MSG_REGISTER_ADDITIONAL_DISPLAY_ADAPTERS = 2;
    private static final int MSG_DELIVER_DISPLAY_EVENT = 3;
    private static final int MSG_REQUEST_TRAVERSAL = 4;
    private static final int MSG_UPDATE_VIEWPORT = 5;
    private static final int DISPLAY_BLANK_STATE_UNKNOWN = 0;
    private static final int DISPLAY_BLANK_STATE_BLANKED = 1;
    private static final int DISPLAY_BLANK_STATE_UNBLANKED = 2;
    private final Context mContext;
    private final DisplayManagerHandler mHandler;
    private WindowManagerFuncs mWindowManagerFuncs;
    private InputManagerFuncs mInputManagerFuncs;
    public boolean mSafeMode;
    public boolean mOnlyCore;
    private boolean mPendingTraversal;
    private WifiDisplayAdapter mWifiDisplayAdapter;
    private VirtualDisplayAdapter mVirtualDisplayAdapter;
    private final SyncRoot mSyncRoot = new SyncRoot();
    public final SparseArray<CallbackRecord> mCallbacks = new SparseArray<>();
    private final ArrayList<DisplayAdapter> mDisplayAdapters = new ArrayList<>();
    private final ArrayList<DisplayDevice> mDisplayDevices = new ArrayList<>();
    private final SparseArray<LogicalDisplay> mLogicalDisplays = new SparseArray<>();
    private int mNextNonDefaultDisplayId = 1;
    private final CopyOnWriteArrayList<DisplayTransactionListener> mDisplayTransactionListeners = new CopyOnWriteArrayList<>();
    private int mAllDisplayBlankStateFromPowerManager = 0;
    private final DisplayViewport mDefaultViewport = new DisplayViewport();
    private final DisplayViewport mExternalTouchViewport = new DisplayViewport();
    private final PersistentDataStore mPersistentDataStore = new PersistentDataStore();
    private final ArrayList<CallbackRecord> mTempCallbacks = new ArrayList<>();
    private final DisplayInfo mTempDisplayInfo = new DisplayInfo();
    private final DisplayViewport mTempDefaultViewport = new DisplayViewport();
    private final DisplayViewport mTempExternalTouchViewport = new DisplayViewport();
    private final boolean mHeadless = SystemProperties.get(SYSTEM_HEADLESS).equals("1");
    private final Handler mUiHandler = UiThread.getHandler();
    private final DisplayAdapterListener mDisplayAdapterListener = new DisplayAdapterListener();
    private final boolean mSingleDisplayDemoMode = SystemProperties.getBoolean("persist.demo.singledisplay", false);

    /* loaded from: DisplayManagerService$InputManagerFuncs.class */
    public interface InputManagerFuncs {
        void setDisplayViewports(DisplayViewport displayViewport, DisplayViewport displayViewport2);
    }

    /* loaded from: DisplayManagerService$SyncRoot.class */
    public static final class SyncRoot {
    }

    /* loaded from: DisplayManagerService$WindowManagerFuncs.class */
    public interface WindowManagerFuncs {
        void requestTraversal();
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.getDisplayInfo(int):android.view.DisplayInfo, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public android.view.DisplayInfo getDisplayInfo(int r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.getDisplayInfo(int):android.view.DisplayInfo, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.getDisplayInfo(int):android.view.DisplayInfo");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.getDisplayIds():int[], file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public int[] getDisplayIds() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.getDisplayIds():int[], file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.getDisplayIds():int[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.scanWifiDisplays():void, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public void scanWifiDisplays() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.scanWifiDisplays():void, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.scanWifiDisplays():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.connectWifiDisplay(java.lang.String):void, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public void connectWifiDisplay(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.connectWifiDisplay(java.lang.String):void, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.connectWifiDisplay(java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.pauseWifiDisplay():void, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public void pauseWifiDisplay() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.pauseWifiDisplay():void, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.pauseWifiDisplay():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.resumeWifiDisplay():void, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public void resumeWifiDisplay() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.resumeWifiDisplay():void, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.resumeWifiDisplay():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.disconnectWifiDisplay():void, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public void disconnectWifiDisplay() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.disconnectWifiDisplay():void, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.disconnectWifiDisplay():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.renameWifiDisplay(java.lang.String, java.lang.String):void, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public void renameWifiDisplay(java.lang.String r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.renameWifiDisplay(java.lang.String, java.lang.String):void, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.renameWifiDisplay(java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.forgetWifiDisplay(java.lang.String):void, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public void forgetWifiDisplay(java.lang.String r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.forgetWifiDisplay(java.lang.String):void, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.forgetWifiDisplay(java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.getWifiDisplayStatus():android.hardware.display.WifiDisplayStatus, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public android.hardware.display.WifiDisplayStatus getWifiDisplayStatus() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.getWifiDisplayStatus():android.hardware.display.WifiDisplayStatus, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.getWifiDisplayStatus():android.hardware.display.WifiDisplayStatus");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.createVirtualDisplay(android.os.IBinder, java.lang.String, java.lang.String, int, int, int, android.view.Surface, int):int, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public int createVirtualDisplay(android.os.IBinder r1, java.lang.String r2, java.lang.String r3, int r4, int r5, int r6, android.view.Surface r7, int r8) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.createVirtualDisplay(android.os.IBinder, java.lang.String, java.lang.String, int, int, int, android.view.Surface, int):int, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.createVirtualDisplay(android.os.IBinder, java.lang.String, java.lang.String, int, int, int, android.view.Surface, int):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.releaseVirtualDisplay(android.os.IBinder):void, file: DisplayManagerService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    @Override // android.hardware.display.IDisplayManager
    public void releaseVirtualDisplay(android.os.IBinder r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.display.DisplayManagerService.releaseVirtualDisplay(android.os.IBinder):void, file: DisplayManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.releaseVirtualDisplay(android.os.IBinder):void");
    }

    public DisplayManagerService(Context context, Handler mainHandler) {
        this.mContext = context;
        this.mHandler = new DisplayManagerHandler(mainHandler.getLooper());
        this.mHandler.sendEmptyMessage(1);
    }

    public boolean waitForDefaultDisplay() {
        synchronized (this.mSyncRoot) {
            long timeout = SystemClock.uptimeMillis() + WAIT_FOR_DEFAULT_DISPLAY_TIMEOUT;
            while (this.mLogicalDisplays.get(0) == null) {
                long delay = timeout - SystemClock.uptimeMillis();
                if (delay <= 0) {
                    return false;
                }
                try {
                    this.mSyncRoot.wait(delay);
                } catch (InterruptedException e) {
                }
            }
            return true;
        }
    }

    public void setWindowManager(WindowManagerFuncs windowManagerFuncs) {
        synchronized (this.mSyncRoot) {
            this.mWindowManagerFuncs = windowManagerFuncs;
            scheduleTraversalLocked(false);
        }
    }

    public void setInputManager(InputManagerFuncs inputManagerFuncs) {
        synchronized (this.mSyncRoot) {
            this.mInputManagerFuncs = inputManagerFuncs;
            scheduleTraversalLocked(false);
        }
    }

    public void systemReady(boolean safeMode, boolean onlyCore) {
        synchronized (this.mSyncRoot) {
            this.mSafeMode = safeMode;
            this.mOnlyCore = onlyCore;
        }
        this.mHandler.sendEmptyMessage(2);
    }

    public boolean isHeadless() {
        return this.mHeadless;
    }

    public void registerDisplayTransactionListener(DisplayTransactionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        this.mDisplayTransactionListeners.add(listener);
    }

    public void unregisterDisplayTransactionListener(DisplayTransactionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        this.mDisplayTransactionListeners.remove(listener);
    }

    public void setDisplayInfoOverrideFromWindowManager(int displayId, DisplayInfo info) {
        synchronized (this.mSyncRoot) {
            LogicalDisplay display = this.mLogicalDisplays.get(displayId);
            if (display != null && display.setDisplayInfoOverrideFromWindowManagerLocked(info)) {
                sendDisplayEventLocked(displayId, 2);
                scheduleTraversalLocked(false);
            }
        }
    }

    public void performTraversalInTransactionFromWindowManager() {
        synchronized (this.mSyncRoot) {
            if (this.mPendingTraversal) {
                this.mPendingTraversal = false;
                performTraversalInTransactionLocked();
                Iterator i$ = this.mDisplayTransactionListeners.iterator();
                while (i$.hasNext()) {
                    DisplayTransactionListener listener = i$.next();
                    listener.onDisplayTransaction();
                }
            }
        }
    }

    public void blankAllDisplaysFromPowerManager() {
        synchronized (this.mSyncRoot) {
            if (this.mAllDisplayBlankStateFromPowerManager != 1) {
                this.mAllDisplayBlankStateFromPowerManager = 1;
                updateAllDisplayBlankingLocked();
                scheduleTraversalLocked(false);
            }
        }
    }

    public void unblankAllDisplaysFromPowerManager() {
        synchronized (this.mSyncRoot) {
            if (this.mAllDisplayBlankStateFromPowerManager != 2) {
                this.mAllDisplayBlankStateFromPowerManager = 2;
                updateAllDisplayBlankingLocked();
                scheduleTraversalLocked(false);
            }
        }
    }

    @Override // android.hardware.display.IDisplayManager
    public void registerCallback(IDisplayManagerCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mSyncRoot) {
            int callingPid = Binder.getCallingPid();
            if (this.mCallbacks.get(callingPid) != null) {
                throw new SecurityException("The calling process has already registered an IDisplayManagerCallback.");
            }
            CallbackRecord record = new CallbackRecord(callingPid, callback);
            try {
                IBinder binder = callback.asBinder();
                binder.linkToDeath(record, 0);
                this.mCallbacks.put(callingPid, record);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCallbackDied(int pid) {
        synchronized (this.mSyncRoot) {
            this.mCallbacks.remove(pid);
        }
    }

    private boolean canCallerConfigureWifiDisplay() {
        return this.mContext.checkCallingPermission(Manifest.permission.CONFIGURE_WIFI_DISPLAY) == 0;
    }

    private boolean validatePackageName(int uid, String packageName) {
        String[] packageNames;
        if (packageName != null && (packageNames = this.mContext.getPackageManager().getPackagesForUid(uid)) != null) {
            for (String n : packageNames) {
                if (n.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerDefaultDisplayAdapter() {
        synchronized (this.mSyncRoot) {
            if (this.mHeadless) {
                registerDisplayAdapterLocked(new HeadlessDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener));
            } else {
                registerDisplayAdapterLocked(new LocalDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerAdditionalDisplayAdapters() {
        synchronized (this.mSyncRoot) {
            if (shouldRegisterNonEssentialDisplayAdaptersLocked()) {
                registerOverlayDisplayAdapterLocked();
                registerWifiDisplayAdapterLocked();
                registerVirtualDisplayAdapterLocked();
            }
        }
    }

    private void registerOverlayDisplayAdapterLocked() {
        registerDisplayAdapterLocked(new OverlayDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener, this.mUiHandler));
    }

    private void registerWifiDisplayAdapterLocked() {
        if (this.mContext.getResources().getBoolean(R.bool.config_enableWifiDisplay) || SystemProperties.getInt(FORCE_WIFI_DISPLAY_ENABLE, -1) == 1) {
            this.mWifiDisplayAdapter = new WifiDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener, this.mPersistentDataStore);
            registerDisplayAdapterLocked(this.mWifiDisplayAdapter);
        }
    }

    private void registerVirtualDisplayAdapterLocked() {
        this.mVirtualDisplayAdapter = new VirtualDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener);
        registerDisplayAdapterLocked(this.mVirtualDisplayAdapter);
    }

    private boolean shouldRegisterNonEssentialDisplayAdaptersLocked() {
        return (this.mSafeMode || this.mOnlyCore) ? false : true;
    }

    private void registerDisplayAdapterLocked(DisplayAdapter adapter) {
        this.mDisplayAdapters.add(adapter);
        adapter.registerLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayDeviceAdded(DisplayDevice device) {
        synchronized (this.mSyncRoot) {
            handleDisplayDeviceAddedLocked(device);
        }
    }

    private void handleDisplayDeviceAddedLocked(DisplayDevice device) {
        if (this.mDisplayDevices.contains(device)) {
            Slog.w(TAG, "Attempted to add already added display device: " + device.getDisplayDeviceInfoLocked());
            return;
        }
        Slog.i(TAG, "Display device added: " + device.getDisplayDeviceInfoLocked());
        this.mDisplayDevices.add(device);
        addLogicalDisplayLocked(device);
        updateDisplayBlankingLocked(device);
        scheduleTraversalLocked(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayDeviceChanged(DisplayDevice device) {
        synchronized (this.mSyncRoot) {
            if (!this.mDisplayDevices.contains(device)) {
                Slog.w(TAG, "Attempted to change non-existent display device: " + device.getDisplayDeviceInfoLocked());
                return;
            }
            Slog.i(TAG, "Display device changed: " + device.getDisplayDeviceInfoLocked());
            device.applyPendingDisplayDeviceInfoChangesLocked();
            if (updateLogicalDisplaysLocked()) {
                scheduleTraversalLocked(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisplayDeviceRemoved(DisplayDevice device) {
        synchronized (this.mSyncRoot) {
            handleDisplayDeviceRemovedLocked(device);
        }
    }

    private void handleDisplayDeviceRemovedLocked(DisplayDevice device) {
        if (!this.mDisplayDevices.remove(device)) {
            Slog.w(TAG, "Attempted to remove non-existent display device: " + device.getDisplayDeviceInfoLocked());
            return;
        }
        Slog.i(TAG, "Display device removed: " + device.getDisplayDeviceInfoLocked());
        updateLogicalDisplaysLocked();
        scheduleTraversalLocked(false);
    }

    private void updateAllDisplayBlankingLocked() {
        int count = this.mDisplayDevices.size();
        for (int i = 0; i < count; i++) {
            DisplayDevice device = this.mDisplayDevices.get(i);
            updateDisplayBlankingLocked(device);
        }
    }

    private void updateDisplayBlankingLocked(DisplayDevice device) {
        DisplayDeviceInfo info = device.getDisplayDeviceInfoLocked();
        if ((info.flags & 32) == 0) {
            switch (this.mAllDisplayBlankStateFromPowerManager) {
                case 1:
                    device.blankLocked();
                    return;
                case 2:
                    device.unblankLocked();
                    return;
                default:
                    return;
            }
        }
    }

    private void addLogicalDisplayLocked(DisplayDevice device) {
        DisplayDeviceInfo deviceInfo = device.getDisplayDeviceInfoLocked();
        boolean isDefault = (deviceInfo.flags & 1) != 0;
        if (isDefault && this.mLogicalDisplays.get(0) != null) {
            Slog.w(TAG, "Ignoring attempt to add a second default display: " + deviceInfo);
            isDefault = false;
        }
        if (!isDefault && this.mSingleDisplayDemoMode) {
            Slog.i(TAG, "Not creating a logical display for a secondary display  because single display demo mode is enabled: " + deviceInfo);
            return;
        }
        int displayId = assignDisplayIdLocked(isDefault);
        int layerStack = assignLayerStackLocked(displayId);
        LogicalDisplay display = new LogicalDisplay(displayId, layerStack, device);
        display.updateLocked(this.mDisplayDevices);
        if (!display.isValidLocked()) {
            Slog.w(TAG, "Ignoring display device because the logical display created from it was not considered valid: " + deviceInfo);
            return;
        }
        this.mLogicalDisplays.put(displayId, display);
        if (isDefault) {
            this.mSyncRoot.notifyAll();
        }
        sendDisplayEventLocked(displayId, 1);
    }

    private int assignDisplayIdLocked(boolean isDefault) {
        if (isDefault) {
            return 0;
        }
        int i = this.mNextNonDefaultDisplayId;
        this.mNextNonDefaultDisplayId = i + 1;
        return i;
    }

    private int assignLayerStackLocked(int displayId) {
        return displayId;
    }

    private boolean updateLogicalDisplaysLocked() {
        boolean changed = false;
        int i = this.mLogicalDisplays.size();
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                int displayId = this.mLogicalDisplays.keyAt(i);
                LogicalDisplay display = this.mLogicalDisplays.valueAt(i);
                this.mTempDisplayInfo.copyFrom(display.getDisplayInfoLocked());
                display.updateLocked(this.mDisplayDevices);
                if (!display.isValidLocked()) {
                    this.mLogicalDisplays.removeAt(i);
                    sendDisplayEventLocked(displayId, 3);
                    changed = true;
                } else if (!this.mTempDisplayInfo.equals(display.getDisplayInfoLocked())) {
                    sendDisplayEventLocked(displayId, 2);
                    changed = true;
                }
            } else {
                return changed;
            }
        }
    }

    private void performTraversalInTransactionLocked() {
        clearViewportsLocked();
        int count = this.mDisplayDevices.size();
        for (int i = 0; i < count; i++) {
            DisplayDevice device = this.mDisplayDevices.get(i);
            configureDisplayInTransactionLocked(device);
            device.performTraversalInTransactionLocked();
        }
        if (this.mInputManagerFuncs != null) {
            this.mHandler.sendEmptyMessage(5);
        }
    }

    public void setDisplayHasContent(int displayId, boolean hasContent, boolean inTraversal) {
        synchronized (this.mSyncRoot) {
            LogicalDisplay display = this.mLogicalDisplays.get(displayId);
            if (display != null && display.hasContentLocked() != hasContent) {
                display.setHasContentLocked(hasContent);
                scheduleTraversalLocked(inTraversal);
            }
        }
    }

    private void clearViewportsLocked() {
        this.mDefaultViewport.valid = false;
        this.mExternalTouchViewport.valid = false;
    }

    private void configureDisplayInTransactionLocked(DisplayDevice device) {
        DisplayDeviceInfo info = device.getDisplayDeviceInfoLocked();
        boolean isPrivate = (info.flags & 16) != 0;
        LogicalDisplay display = findLogicalDisplayForDeviceLocked(device);
        if (!isPrivate) {
            if (display != null && !display.hasContentLocked()) {
                display = null;
            }
            if (display == null) {
                display = this.mLogicalDisplays.get(0);
            }
        }
        if (display == null) {
            Slog.w(TAG, "Missing logical display to use for physical display device: " + device.getDisplayDeviceInfoLocked());
            return;
        }
        boolean isBlanked = this.mAllDisplayBlankStateFromPowerManager == 1 && (info.flags & 32) == 0;
        display.configureDisplayInTransactionLocked(device, isBlanked);
        if (!this.mDefaultViewport.valid && (info.flags & 1) != 0) {
            setViewportLocked(this.mDefaultViewport, display, device);
        }
        if (!this.mExternalTouchViewport.valid && info.touch == 2) {
            setViewportLocked(this.mExternalTouchViewport, display, device);
        }
    }

    private static void setViewportLocked(DisplayViewport viewport, LogicalDisplay display, DisplayDevice device) {
        viewport.valid = true;
        viewport.displayId = display.getDisplayIdLocked();
        device.populateViewportLocked(viewport);
    }

    private LogicalDisplay findLogicalDisplayForDeviceLocked(DisplayDevice device) {
        int count = this.mLogicalDisplays.size();
        for (int i = 0; i < count; i++) {
            LogicalDisplay display = this.mLogicalDisplays.valueAt(i);
            if (display.getPrimaryDisplayDeviceLocked() == device) {
                return display;
            }
        }
        return null;
    }

    private void sendDisplayEventLocked(int displayId, int event) {
        Message msg = this.mHandler.obtainMessage(3, displayId, event);
        this.mHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleTraversalLocked(boolean inTraversal) {
        if (!this.mPendingTraversal && this.mWindowManagerFuncs != null) {
            this.mPendingTraversal = true;
            if (!inTraversal) {
                this.mHandler.sendEmptyMessage(4);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deliverDisplayEvent(int displayId, int event) {
        int count;
        synchronized (this.mSyncRoot) {
            count = this.mCallbacks.size();
            this.mTempCallbacks.clear();
            for (int i = 0; i < count; i++) {
                this.mTempCallbacks.add(this.mCallbacks.valueAt(i));
            }
        }
        for (int i2 = 0; i2 < count; i2++) {
            this.mTempCallbacks.get(i2).notifyDisplayEventAsync(displayId, event);
        }
        this.mTempCallbacks.clear();
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext == null || this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump DisplayManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("DISPLAY MANAGER (dumpsys display)");
        synchronized (this.mSyncRoot) {
            pw.println("  mHeadless=" + this.mHeadless);
            pw.println("  mOnlyCode=" + this.mOnlyCore);
            pw.println("  mSafeMode=" + this.mSafeMode);
            pw.println("  mPendingTraversal=" + this.mPendingTraversal);
            pw.println("  mAllDisplayBlankStateFromPowerManager=" + this.mAllDisplayBlankStateFromPowerManager);
            pw.println("  mNextNonDefaultDisplayId=" + this.mNextNonDefaultDisplayId);
            pw.println("  mDefaultViewport=" + this.mDefaultViewport);
            pw.println("  mExternalTouchViewport=" + this.mExternalTouchViewport);
            pw.println("  mSingleDisplayDemoMode=" + this.mSingleDisplayDemoMode);
            IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "    ");
            ipw.increaseIndent();
            pw.println();
            pw.println("Display Adapters: size=" + this.mDisplayAdapters.size());
            Iterator i$ = this.mDisplayAdapters.iterator();
            while (i$.hasNext()) {
                DisplayAdapter adapter = i$.next();
                pw.println("  " + adapter.getName());
                adapter.dumpLocked(ipw);
            }
            pw.println();
            pw.println("Display Devices: size=" + this.mDisplayDevices.size());
            Iterator i$2 = this.mDisplayDevices.iterator();
            while (i$2.hasNext()) {
                DisplayDevice device = i$2.next();
                pw.println("  " + device.getDisplayDeviceInfoLocked());
                device.dumpLocked(ipw);
            }
            int logicalDisplayCount = this.mLogicalDisplays.size();
            pw.println();
            pw.println("Logical Displays: size=" + logicalDisplayCount);
            for (int i = 0; i < logicalDisplayCount; i++) {
                int displayId = this.mLogicalDisplays.keyAt(i);
                LogicalDisplay display = this.mLogicalDisplays.valueAt(i);
                pw.println("  Display " + displayId + Separators.COLON);
                display.dumpLocked(ipw);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DisplayManagerService$DisplayManagerHandler.class */
    public final class DisplayManagerHandler extends Handler {
        public DisplayManagerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DisplayManagerService.this.registerDefaultDisplayAdapter();
                    return;
                case 2:
                    DisplayManagerService.this.registerAdditionalDisplayAdapters();
                    return;
                case 3:
                    DisplayManagerService.this.deliverDisplayEvent(msg.arg1, msg.arg2);
                    return;
                case 4:
                    DisplayManagerService.this.mWindowManagerFuncs.requestTraversal();
                    return;
                case 5:
                    synchronized (DisplayManagerService.this.mSyncRoot) {
                        DisplayManagerService.this.mTempDefaultViewport.copyFrom(DisplayManagerService.this.mDefaultViewport);
                        DisplayManagerService.this.mTempExternalTouchViewport.copyFrom(DisplayManagerService.this.mExternalTouchViewport);
                    }
                    DisplayManagerService.this.mInputManagerFuncs.setDisplayViewports(DisplayManagerService.this.mTempDefaultViewport, DisplayManagerService.this.mTempExternalTouchViewport);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DisplayManagerService$DisplayAdapterListener.class */
    public final class DisplayAdapterListener implements DisplayAdapter.Listener {
        private DisplayAdapterListener() {
        }

        @Override // com.android.server.display.DisplayAdapter.Listener
        public void onDisplayDeviceEvent(DisplayDevice device, int event) {
            switch (event) {
                case 1:
                    DisplayManagerService.this.handleDisplayDeviceAdded(device);
                    return;
                case 2:
                    DisplayManagerService.this.handleDisplayDeviceChanged(device);
                    return;
                case 3:
                    DisplayManagerService.this.handleDisplayDeviceRemoved(device);
                    return;
                default:
                    return;
            }
        }

        @Override // com.android.server.display.DisplayAdapter.Listener
        public void onTraversalRequested() {
            synchronized (DisplayManagerService.this.mSyncRoot) {
                DisplayManagerService.this.scheduleTraversalLocked(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: DisplayManagerService$CallbackRecord.class */
    public final class CallbackRecord implements IBinder.DeathRecipient {
        private final int mPid;
        private final IDisplayManagerCallback mCallback;

        public CallbackRecord(int pid, IDisplayManagerCallback callback) {
            this.mPid = pid;
            this.mCallback = callback;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            DisplayManagerService.this.onCallbackDied(this.mPid);
        }

        public void notifyDisplayEventAsync(int displayId, int event) {
            try {
                this.mCallback.onDisplayEvent(displayId, event);
            } catch (RemoteException ex) {
                Slog.w(DisplayManagerService.TAG, "Failed to notify process " + this.mPid + " that displays changed, assuming it died.", ex);
                binderDied();
            }
        }
    }
}