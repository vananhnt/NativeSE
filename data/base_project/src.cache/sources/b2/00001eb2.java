package com.android.server.input;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.input.IInputDevicesChangedListener;
import android.hardware.input.IInputManager;
import android.hardware.input.InputManager;
import android.hardware.input.KeyboardLayout;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Telephony;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.IInputFilter;
import android.view.IInputFilterHost;
import android.view.InputChannel;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.PointerIcon;
import android.view.ViewConfiguration;
import android.widget.Toast;
import com.android.internal.R;
import com.android.server.Watchdog;
import com.android.server.display.DisplayManagerService;
import com.android.server.display.DisplayViewport;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import libcore.io.Streams;

/* loaded from: InputManagerService.class */
public class InputManagerService extends IInputManager.Stub implements Watchdog.Monitor, DisplayManagerService.InputManagerFuncs {
    static final String TAG = "InputManager";
    static final boolean DEBUG = false;
    private static final String EXCLUDED_DEVICES_PATH = "etc/excluded-input-devices.xml";
    private static final int MSG_DELIVER_INPUT_DEVICES_CHANGED = 1;
    private static final int MSG_SWITCH_KEYBOARD_LAYOUT = 2;
    private static final int MSG_RELOAD_KEYBOARD_LAYOUTS = 3;
    private static final int MSG_UPDATE_KEYBOARD_LAYOUTS = 4;
    private static final int MSG_RELOAD_DEVICE_ALIASES = 5;
    private final int mPtr;
    private final Context mContext;
    private final InputManagerHandler mHandler;
    private WindowManagerCallbacks mWindowManagerCallbacks;
    private WiredAccessoryCallbacks mWiredAccessoryCallbacks;
    private boolean mSystemReady;
    private NotificationManager mNotificationManager;
    private boolean mInputDevicesChangedPending;
    private boolean mKeyboardLayoutNotificationShown;
    private PendingIntent mKeyboardLayoutIntent;
    private Toast mSwitchedKeyboardLayoutToast;
    private int mNextVibratorTokenValue;
    IInputFilter mInputFilter;
    InputFilterHost mInputFilterHost;
    private static final int INPUT_EVENT_INJECTION_SUCCEEDED = 0;
    private static final int INPUT_EVENT_INJECTION_PERMISSION_DENIED = 1;
    private static final int INPUT_EVENT_INJECTION_FAILED = 2;
    private static final int INPUT_EVENT_INJECTION_TIMED_OUT = 3;
    private static final int INJECTION_TIMEOUT_MILLIS = 30000;
    public static final int KEY_STATE_UNKNOWN = -1;
    public static final int KEY_STATE_UP = 0;
    public static final int KEY_STATE_DOWN = 1;
    public static final int KEY_STATE_VIRTUAL = 2;
    public static final int BTN_MOUSE = 272;
    public static final int SW_LID = 0;
    public static final int SW_KEYPAD_SLIDE = 10;
    public static final int SW_HEADPHONE_INSERT = 2;
    public static final int SW_MICROPHONE_INSERT = 4;
    public static final int SW_JACK_PHYSICAL_INSERT = 7;
    public static final int SW_LID_BIT = 1;
    public static final int SW_KEYPAD_SLIDE_BIT = 1024;
    public static final int SW_HEADPHONE_INSERT_BIT = 4;
    public static final int SW_MICROPHONE_INSERT_BIT = 16;
    public static final int SW_JACK_PHYSICAL_INSERT_BIT = 128;
    public static final int SW_JACK_BITS = 148;
    final boolean mUseDevInputEventForAudioJack;
    private final PersistentDataStore mDataStore = new PersistentDataStore();
    private Object mInputDevicesLock = new Object();
    private InputDevice[] mInputDevices = new InputDevice[0];
    private final SparseArray<InputDevicesChangedListenerRecord> mInputDevicesChangedListeners = new SparseArray<>();
    private final ArrayList<InputDevicesChangedListenerRecord> mTempInputDevicesChangedListenersToNotify = new ArrayList<>();
    private final ArrayList<InputDevice> mTempFullKeyboards = new ArrayList<>();
    private Object mVibratorLock = new Object();
    private HashMap<IBinder, VibratorToken> mVibratorTokens = new HashMap<>();
    final Object mInputFilterLock = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputManagerService$KeyboardLayoutVisitor.class */
    public interface KeyboardLayoutVisitor {
        void visitKeyboardLayout(Resources resources, String str, String str2, String str3, int i);
    }

    /* loaded from: InputManagerService$WindowManagerCallbacks.class */
    public interface WindowManagerCallbacks {
        void notifyConfigurationChanged();

        void notifyLidSwitchChanged(long j, boolean z);

        void notifyInputChannelBroken(InputWindowHandle inputWindowHandle);

        long notifyANR(InputApplicationHandle inputApplicationHandle, InputWindowHandle inputWindowHandle, String str);

        int interceptKeyBeforeQueueing(KeyEvent keyEvent, int i, boolean z);

        int interceptMotionBeforeQueueingWhenScreenOff(int i);

        long interceptKeyBeforeDispatching(InputWindowHandle inputWindowHandle, KeyEvent keyEvent, int i);

        KeyEvent dispatchUnhandledKey(InputWindowHandle inputWindowHandle, KeyEvent keyEvent, int i);

        int getPointerLayer();
    }

    /* loaded from: InputManagerService$WiredAccessoryCallbacks.class */
    public interface WiredAccessoryCallbacks {
        void notifyWiredAccessoryChanged(long j, int i, int i2);
    }

    private static native int nativeInit(InputManagerService inputManagerService, Context context, MessageQueue messageQueue);

    private static native void nativeStart(int i);

    private static native void nativeSetDisplayViewport(int i, boolean z, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13);

    private static native int nativeGetScanCodeState(int i, int i2, int i3, int i4);

    private static native int nativeGetKeyCodeState(int i, int i2, int i3, int i4);

    private static native int nativeGetSwitchState(int i, int i2, int i3, int i4);

    private static native boolean nativeHasKeys(int i, int i2, int i3, int[] iArr, boolean[] zArr);

    private static native void nativeRegisterInputChannel(int i, InputChannel inputChannel, InputWindowHandle inputWindowHandle, boolean z);

    private static native void nativeUnregisterInputChannel(int i, InputChannel inputChannel);

    private static native void nativeSetInputFilterEnabled(int i, boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int nativeInjectInputEvent(int i, InputEvent inputEvent, int i2, int i3, int i4, int i5, int i6);

    private static native void nativeSetInputWindows(int i, InputWindowHandle[] inputWindowHandleArr);

    private static native void nativeSetInputDispatchMode(int i, boolean z, boolean z2);

    private static native void nativeSetSystemUiVisibility(int i, int i2);

    private static native void nativeSetFocusedApplication(int i, InputApplicationHandle inputApplicationHandle);

    private static native boolean nativeTransferTouchFocus(int i, InputChannel inputChannel, InputChannel inputChannel2);

    private static native void nativeSetPointerSpeed(int i, int i2);

    private static native void nativeSetShowTouches(int i, boolean z);

    private static native void nativeVibrate(int i, int i2, long[] jArr, int i3, int i4);

    private static native void nativeCancelVibrate(int i, int i2, int i3);

    private static native void nativeReloadKeyboardLayouts(int i);

    private static native void nativeReloadDeviceAliases(int i);

    private static native String nativeDump(int i);

    private static native void nativeMonitor(int i);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.injectInputEvent(android.view.InputEvent, int):boolean, file: InputManagerService.class
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
    @Override // android.hardware.input.IInputManager
    public boolean injectInputEvent(android.view.InputEvent r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.injectInputEvent(android.view.InputEvent, int):boolean, file: InputManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputManagerService.injectInputEvent(android.view.InputEvent, int):boolean");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.updateKeyboardLayouts():void, file: InputManagerService.class
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
    public void updateKeyboardLayouts() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.updateKeyboardLayouts():void, file: InputManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputManagerService.updateKeyboardLayouts():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.visitKeyboardLayoutsInPackage(android.content.pm.PackageManager, android.content.pm.ActivityInfo, java.lang.String, com.android.server.input.InputManagerService$KeyboardLayoutVisitor):void, file: InputManagerService.class
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
    private void visitKeyboardLayoutsInPackage(android.content.pm.PackageManager r1, android.content.pm.ActivityInfo r2, java.lang.String r3, com.android.server.input.InputManagerService.KeyboardLayoutVisitor r4) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.visitKeyboardLayoutsInPackage(android.content.pm.PackageManager, android.content.pm.ActivityInfo, java.lang.String, com.android.server.input.InputManagerService$KeyboardLayoutVisitor):void, file: InputManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputManagerService.visitKeyboardLayoutsInPackage(android.content.pm.PackageManager, android.content.pm.ActivityInfo, java.lang.String, com.android.server.input.InputManagerService$KeyboardLayoutVisitor):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.setCurrentKeyboardLayoutForInputDevice(java.lang.String, java.lang.String):void, file: InputManagerService.class
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
    @Override // android.hardware.input.IInputManager
    public void setCurrentKeyboardLayoutForInputDevice(java.lang.String r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.setCurrentKeyboardLayoutForInputDevice(java.lang.String, java.lang.String):void, file: InputManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputManagerService.setCurrentKeyboardLayoutForInputDevice(java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.addKeyboardLayoutForInputDevice(java.lang.String, java.lang.String):void, file: InputManagerService.class
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
    @Override // android.hardware.input.IInputManager
    public void addKeyboardLayoutForInputDevice(java.lang.String r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.addKeyboardLayoutForInputDevice(java.lang.String, java.lang.String):void, file: InputManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputManagerService.addKeyboardLayoutForInputDevice(java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.removeKeyboardLayoutForInputDevice(java.lang.String, java.lang.String):void, file: InputManagerService.class
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
    @Override // android.hardware.input.IInputManager
    public void removeKeyboardLayoutForInputDevice(java.lang.String r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.removeKeyboardLayoutForInputDevice(java.lang.String, java.lang.String):void, file: InputManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputManagerService.removeKeyboardLayoutForInputDevice(java.lang.String, java.lang.String):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.handleSwitchKeyboardLayout(int, int):void, file: InputManagerService.class
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
    public void handleSwitchKeyboardLayout(int r1, int r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.handleSwitchKeyboardLayout(int, int):void, file: InputManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputManagerService.handleSwitchKeyboardLayout(int, int):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.getExcludedDeviceNames():java.lang.String[], file: InputManagerService.class
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
    private java.lang.String[] getExcludedDeviceNames() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.InputManagerService.getExcludedDeviceNames():java.lang.String[], file: InputManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.InputManagerService.getExcludedDeviceNames():java.lang.String[]");
    }

    public InputManagerService(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = new InputManagerHandler(handler.getLooper());
        this.mUseDevInputEventForAudioJack = context.getResources().getBoolean(R.bool.config_useDevInputEventForAudioJack);
        Slog.i(TAG, "Initializing input manager, mUseDevInputEventForAudioJack=" + this.mUseDevInputEventForAudioJack);
        this.mPtr = nativeInit(this, this.mContext, this.mHandler.getLooper().getQueue());
    }

    public void setWindowManagerCallbacks(WindowManagerCallbacks callbacks) {
        this.mWindowManagerCallbacks = callbacks;
    }

    public void setWiredAccessoryCallbacks(WiredAccessoryCallbacks callbacks) {
        this.mWiredAccessoryCallbacks = callbacks;
    }

    public void start() {
        Slog.i(TAG, "Starting input manager");
        nativeStart(this.mPtr);
        Watchdog.getInstance().addMonitor(this);
        registerPointerSpeedSettingObserver();
        registerShowTouchesSettingObserver();
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.input.InputManagerService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                InputManagerService.this.updatePointerSpeedFromSettings();
                InputManagerService.this.updateShowTouchesFromSettings();
            }
        }, new IntentFilter(Intent.ACTION_USER_SWITCHED), null, this.mHandler);
        updatePointerSpeedFromSettings();
        updateShowTouchesFromSettings();
    }

    public void systemRunning() {
        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mSystemReady = true;
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.input.InputManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                InputManagerService.this.updateKeyboardLayouts();
            }
        }, filter, null, this.mHandler);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.input.InputManagerService.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                InputManagerService.this.reloadDeviceAliases();
            }
        }, new IntentFilter(BluetoothDevice.ACTION_ALIAS_CHANGED), null, this.mHandler);
        this.mHandler.sendEmptyMessage(5);
        this.mHandler.sendEmptyMessage(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reloadKeyboardLayouts() {
        nativeReloadKeyboardLayouts(this.mPtr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reloadDeviceAliases() {
        nativeReloadDeviceAliases(this.mPtr);
    }

    @Override // com.android.server.display.DisplayManagerService.InputManagerFuncs
    public void setDisplayViewports(DisplayViewport defaultViewport, DisplayViewport externalTouchViewport) {
        if (defaultViewport.valid) {
            setDisplayViewport(false, defaultViewport);
        }
        if (externalTouchViewport.valid) {
            setDisplayViewport(true, externalTouchViewport);
        } else if (defaultViewport.valid) {
            setDisplayViewport(true, defaultViewport);
        }
    }

    private void setDisplayViewport(boolean external, DisplayViewport viewport) {
        nativeSetDisplayViewport(this.mPtr, external, viewport.displayId, viewport.orientation, viewport.logicalFrame.left, viewport.logicalFrame.top, viewport.logicalFrame.right, viewport.logicalFrame.bottom, viewport.physicalFrame.left, viewport.physicalFrame.top, viewport.physicalFrame.right, viewport.physicalFrame.bottom, viewport.deviceWidth, viewport.deviceHeight);
    }

    public int getKeyCodeState(int deviceId, int sourceMask, int keyCode) {
        return nativeGetKeyCodeState(this.mPtr, deviceId, sourceMask, keyCode);
    }

    public int getScanCodeState(int deviceId, int sourceMask, int scanCode) {
        return nativeGetScanCodeState(this.mPtr, deviceId, sourceMask, scanCode);
    }

    public int getSwitchState(int deviceId, int sourceMask, int switchCode) {
        return nativeGetSwitchState(this.mPtr, deviceId, sourceMask, switchCode);
    }

    @Override // android.hardware.input.IInputManager
    public boolean hasKeys(int deviceId, int sourceMask, int[] keyCodes, boolean[] keyExists) {
        if (keyCodes == null) {
            throw new IllegalArgumentException("keyCodes must not be null.");
        }
        if (keyExists == null || keyExists.length < keyCodes.length) {
            throw new IllegalArgumentException("keyExists must not be null and must be at least as large as keyCodes.");
        }
        return nativeHasKeys(this.mPtr, deviceId, sourceMask, keyCodes, keyExists);
    }

    public InputChannel monitorInput(String inputChannelName) {
        if (inputChannelName == null) {
            throw new IllegalArgumentException("inputChannelName must not be null.");
        }
        InputChannel[] inputChannels = InputChannel.openInputChannelPair(inputChannelName);
        nativeRegisterInputChannel(this.mPtr, inputChannels[0], null, true);
        inputChannels[0].dispose();
        return inputChannels[1];
    }

    public void registerInputChannel(InputChannel inputChannel, InputWindowHandle inputWindowHandle) {
        if (inputChannel == null) {
            throw new IllegalArgumentException("inputChannel must not be null.");
        }
        nativeRegisterInputChannel(this.mPtr, inputChannel, inputWindowHandle, false);
    }

    public void unregisterInputChannel(InputChannel inputChannel) {
        if (inputChannel == null) {
            throw new IllegalArgumentException("inputChannel must not be null.");
        }
        nativeUnregisterInputChannel(this.mPtr, inputChannel);
    }

    public void setInputFilter(IInputFilter filter) {
        synchronized (this.mInputFilterLock) {
            IInputFilter oldFilter = this.mInputFilter;
            if (oldFilter == filter) {
                return;
            }
            if (oldFilter != null) {
                this.mInputFilter = null;
                this.mInputFilterHost.disconnectLocked();
                this.mInputFilterHost = null;
                try {
                    oldFilter.uninstall();
                } catch (RemoteException e) {
                }
            }
            if (filter != null) {
                this.mInputFilter = filter;
                this.mInputFilterHost = new InputFilterHost();
                try {
                    filter.install(this.mInputFilterHost);
                } catch (RemoteException e2) {
                }
            }
            nativeSetInputFilterEnabled(this.mPtr, filter != null);
        }
    }

    @Override // android.hardware.input.IInputManager
    public InputDevice getInputDevice(int deviceId) {
        synchronized (this.mInputDevicesLock) {
            int count = this.mInputDevices.length;
            for (int i = 0; i < count; i++) {
                InputDevice inputDevice = this.mInputDevices[i];
                if (inputDevice.getId() == deviceId) {
                    return inputDevice;
                }
            }
            return null;
        }
    }

    @Override // android.hardware.input.IInputManager
    public int[] getInputDeviceIds() {
        int[] ids;
        synchronized (this.mInputDevicesLock) {
            int count = this.mInputDevices.length;
            ids = new int[count];
            for (int i = 0; i < count; i++) {
                ids[i] = this.mInputDevices[i].getId();
            }
        }
        return ids;
    }

    public InputDevice[] getInputDevices() {
        InputDevice[] inputDeviceArr;
        synchronized (this.mInputDevicesLock) {
            inputDeviceArr = this.mInputDevices;
        }
        return inputDeviceArr;
    }

    @Override // android.hardware.input.IInputManager
    public void registerInputDevicesChangedListener(IInputDevicesChangedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mInputDevicesLock) {
            int callingPid = Binder.getCallingPid();
            if (this.mInputDevicesChangedListeners.get(callingPid) != null) {
                throw new SecurityException("The calling process has already registered an InputDevicesChangedListener.");
            }
            InputDevicesChangedListenerRecord record = new InputDevicesChangedListenerRecord(callingPid, listener);
            try {
                IBinder binder = listener.asBinder();
                binder.linkToDeath(record, 0);
                this.mInputDevicesChangedListeners.put(callingPid, record);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInputDevicesChangedListenerDied(int pid) {
        synchronized (this.mInputDevicesLock) {
            this.mInputDevicesChangedListeners.remove(pid);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deliverInputDevicesChanged(InputDevice[] oldInputDevices) {
        int numFullKeyboardsAdded = 0;
        this.mTempInputDevicesChangedListenersToNotify.clear();
        this.mTempFullKeyboards.clear();
        synchronized (this.mInputDevicesLock) {
            if (this.mInputDevicesChangedPending) {
                this.mInputDevicesChangedPending = false;
                int numListeners = this.mInputDevicesChangedListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    this.mTempInputDevicesChangedListenersToNotify.add(this.mInputDevicesChangedListeners.valueAt(i));
                }
                int numDevices = this.mInputDevices.length;
                int[] deviceIdAndGeneration = new int[numDevices * 2];
                for (int i2 = 0; i2 < numDevices; i2++) {
                    InputDevice inputDevice = this.mInputDevices[i2];
                    deviceIdAndGeneration[i2 * 2] = inputDevice.getId();
                    deviceIdAndGeneration[(i2 * 2) + 1] = inputDevice.getGeneration();
                    if (!inputDevice.isVirtual() && inputDevice.isFullKeyboard()) {
                        if (!containsInputDeviceWithDescriptor(oldInputDevices, inputDevice.getDescriptor())) {
                            int i3 = numFullKeyboardsAdded;
                            numFullKeyboardsAdded++;
                            this.mTempFullKeyboards.add(i3, inputDevice);
                        } else {
                            this.mTempFullKeyboards.add(inputDevice);
                        }
                    }
                }
                for (int i4 = 0; i4 < numListeners; i4++) {
                    this.mTempInputDevicesChangedListenersToNotify.get(i4).notifyInputDevicesChanged(deviceIdAndGeneration);
                }
                this.mTempInputDevicesChangedListenersToNotify.clear();
                if (this.mNotificationManager != null) {
                    int numFullKeyboards = this.mTempFullKeyboards.size();
                    boolean missingLayoutForExternalKeyboard = false;
                    boolean missingLayoutForExternalKeyboardAdded = false;
                    synchronized (this.mDataStore) {
                        for (int i5 = 0; i5 < numFullKeyboards; i5++) {
                            if (this.mDataStore.getCurrentKeyboardLayout(this.mTempFullKeyboards.get(i5).getDescriptor()) == null) {
                                missingLayoutForExternalKeyboard = true;
                                if (i5 < numFullKeyboardsAdded) {
                                    missingLayoutForExternalKeyboardAdded = true;
                                }
                            }
                        }
                    }
                    if (missingLayoutForExternalKeyboard) {
                        if (missingLayoutForExternalKeyboardAdded) {
                            showMissingKeyboardLayoutNotification();
                        }
                    } else if (this.mKeyboardLayoutNotificationShown) {
                        hideMissingKeyboardLayoutNotification();
                    }
                }
                this.mTempFullKeyboards.clear();
            }
        }
    }

    private void showMissingKeyboardLayoutNotification() {
        if (!this.mKeyboardLayoutNotificationShown) {
            if (this.mKeyboardLayoutIntent == null) {
                Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                intent.setFlags(337641472);
                this.mKeyboardLayoutIntent = PendingIntent.getActivityAsUser(this.mContext, 0, intent, 0, null, UserHandle.CURRENT);
            }
            Resources r = this.mContext.getResources();
            Notification notification = new Notification.Builder(this.mContext).setContentTitle(r.getString(R.string.select_keyboard_layout_notification_title)).setContentText(r.getString(R.string.select_keyboard_layout_notification_message)).setContentIntent(this.mKeyboardLayoutIntent).setSmallIcon(R.drawable.ic_settings_language).setPriority(-1).build();
            this.mNotificationManager.notifyAsUser(null, R.string.select_keyboard_layout_notification_title, notification, UserHandle.ALL);
            this.mKeyboardLayoutNotificationShown = true;
        }
    }

    private void hideMissingKeyboardLayoutNotification() {
        if (this.mKeyboardLayoutNotificationShown) {
            this.mKeyboardLayoutNotificationShown = false;
            this.mNotificationManager.cancelAsUser(null, R.string.select_keyboard_layout_notification_title, UserHandle.ALL);
        }
    }

    private static boolean containsInputDeviceWithDescriptor(InputDevice[] inputDevices, String descriptor) {
        for (InputDevice inputDevice : inputDevices) {
            if (inputDevice.getDescriptor().equals(descriptor)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.hardware.input.IInputManager
    public KeyboardLayout[] getKeyboardLayouts() {
        final ArrayList<KeyboardLayout> list = new ArrayList<>();
        visitAllKeyboardLayouts(new KeyboardLayoutVisitor() { // from class: com.android.server.input.InputManagerService.5
            @Override // com.android.server.input.InputManagerService.KeyboardLayoutVisitor
            public void visitKeyboardLayout(Resources resources, String descriptor, String label, String collection, int keyboardLayoutResId) {
                list.add(new KeyboardLayout(descriptor, label, collection));
            }
        });
        return (KeyboardLayout[]) list.toArray(new KeyboardLayout[list.size()]);
    }

    @Override // android.hardware.input.IInputManager
    public KeyboardLayout getKeyboardLayout(String keyboardLayoutDescriptor) {
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        final KeyboardLayout[] result = new KeyboardLayout[1];
        visitKeyboardLayout(keyboardLayoutDescriptor, new KeyboardLayoutVisitor() { // from class: com.android.server.input.InputManagerService.6
            @Override // com.android.server.input.InputManagerService.KeyboardLayoutVisitor
            public void visitKeyboardLayout(Resources resources, String descriptor, String label, String collection, int keyboardLayoutResId) {
                result[0] = new KeyboardLayout(descriptor, label, collection);
            }
        });
        if (result[0] == null) {
            Log.w(TAG, "Could not get keyboard layout with descriptor '" + keyboardLayoutDescriptor + "'.");
        }
        return result[0];
    }

    private void visitAllKeyboardLayouts(KeyboardLayoutVisitor visitor) {
        PackageManager pm = this.mContext.getPackageManager();
        Intent intent = new Intent(InputManager.ACTION_QUERY_KEYBOARD_LAYOUTS);
        for (ResolveInfo resolveInfo : pm.queryBroadcastReceivers(intent, 128)) {
            visitKeyboardLayoutsInPackage(pm, resolveInfo.activityInfo, null, visitor);
        }
    }

    private void visitKeyboardLayout(String keyboardLayoutDescriptor, KeyboardLayoutVisitor visitor) {
        KeyboardLayoutDescriptor d = KeyboardLayoutDescriptor.parse(keyboardLayoutDescriptor);
        if (d != null) {
            PackageManager pm = this.mContext.getPackageManager();
            try {
                ActivityInfo receiver = pm.getReceiverInfo(new ComponentName(d.packageName, d.receiverName), 128);
                visitKeyboardLayoutsInPackage(pm, receiver, d.keyboardLayoutName, visitor);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
    }

    @Override // android.hardware.input.IInputManager
    public String getCurrentKeyboardLayoutForInputDevice(String inputDeviceDescriptor) {
        String currentKeyboardLayout;
        if (inputDeviceDescriptor == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        synchronized (this.mDataStore) {
            currentKeyboardLayout = this.mDataStore.getCurrentKeyboardLayout(inputDeviceDescriptor);
        }
        return currentKeyboardLayout;
    }

    @Override // android.hardware.input.IInputManager
    public String[] getKeyboardLayoutsForInputDevice(String inputDeviceDescriptor) {
        String[] keyboardLayouts;
        if (inputDeviceDescriptor == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        synchronized (this.mDataStore) {
            keyboardLayouts = this.mDataStore.getKeyboardLayouts(inputDeviceDescriptor);
        }
        return keyboardLayouts;
    }

    public void switchKeyboardLayout(int deviceId, int direction) {
        this.mHandler.obtainMessage(2, deviceId, direction).sendToTarget();
    }

    public void setInputWindows(InputWindowHandle[] windowHandles) {
        nativeSetInputWindows(this.mPtr, windowHandles);
    }

    public void setFocusedApplication(InputApplicationHandle application) {
        nativeSetFocusedApplication(this.mPtr, application);
    }

    public void setInputDispatchMode(boolean enabled, boolean frozen) {
        nativeSetInputDispatchMode(this.mPtr, enabled, frozen);
    }

    public void setSystemUiVisibility(int visibility) {
        nativeSetSystemUiVisibility(this.mPtr, visibility);
    }

    public boolean transferTouchFocus(InputChannel fromChannel, InputChannel toChannel) {
        if (fromChannel == null) {
            throw new IllegalArgumentException("fromChannel must not be null.");
        }
        if (toChannel == null) {
            throw new IllegalArgumentException("toChannel must not be null.");
        }
        return nativeTransferTouchFocus(this.mPtr, fromChannel, toChannel);
    }

    @Override // android.hardware.input.IInputManager
    public void tryPointerSpeed(int speed) {
        if (!checkCallingPermission(Manifest.permission.SET_POINTER_SPEED, "tryPointerSpeed()")) {
            throw new SecurityException("Requires SET_POINTER_SPEED permission");
        }
        if (speed < -7 || speed > 7) {
            throw new IllegalArgumentException("speed out of range");
        }
        setPointerSpeedUnchecked(speed);
    }

    public void updatePointerSpeedFromSettings() {
        int speed = getPointerSpeedSetting();
        setPointerSpeedUnchecked(speed);
    }

    private void setPointerSpeedUnchecked(int speed) {
        nativeSetPointerSpeed(this.mPtr, Math.min(Math.max(speed, -7), 7));
    }

    private void registerPointerSpeedSettingObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.POINTER_SPEED), true, new ContentObserver(this.mHandler) { // from class: com.android.server.input.InputManagerService.7
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                InputManagerService.this.updatePointerSpeedFromSettings();
            }
        }, -1);
    }

    private int getPointerSpeedSetting() {
        int speed = 0;
        try {
            speed = Settings.System.getIntForUser(this.mContext.getContentResolver(), Settings.System.POINTER_SPEED, -2);
        } catch (Settings.SettingNotFoundException e) {
        }
        return speed;
    }

    public void updateShowTouchesFromSettings() {
        int setting = getShowTouchesSetting(0);
        nativeSetShowTouches(this.mPtr, setting != 0);
    }

    private void registerShowTouchesSettingObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SHOW_TOUCHES), true, new ContentObserver(this.mHandler) { // from class: com.android.server.input.InputManagerService.8
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                InputManagerService.this.updateShowTouchesFromSettings();
            }
        }, -1);
    }

    private int getShowTouchesSetting(int defaultValue) {
        int result = defaultValue;
        try {
            result = Settings.System.getIntForUser(this.mContext.getContentResolver(), Settings.System.SHOW_TOUCHES, -2);
        } catch (Settings.SettingNotFoundException e) {
        }
        return result;
    }

    @Override // android.hardware.input.IInputManager
    public void vibrate(int deviceId, long[] pattern, int repeat, IBinder token) {
        VibratorToken v;
        if (repeat >= pattern.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        synchronized (this.mVibratorLock) {
            v = this.mVibratorTokens.get(token);
            if (v == null) {
                int i = this.mNextVibratorTokenValue;
                this.mNextVibratorTokenValue = i + 1;
                v = new VibratorToken(deviceId, token, i);
                try {
                    token.linkToDeath(v, 0);
                    this.mVibratorTokens.put(token, v);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        synchronized (v) {
            v.mVibrating = true;
            nativeVibrate(this.mPtr, deviceId, pattern, repeat, v.mTokenValue);
        }
    }

    @Override // android.hardware.input.IInputManager
    public void cancelVibrate(int deviceId, IBinder token) {
        synchronized (this.mVibratorLock) {
            VibratorToken v = this.mVibratorTokens.get(token);
            if (v == null || v.mDeviceId != deviceId) {
                return;
            }
            cancelVibrateIfNeeded(v);
        }
    }

    void onVibratorTokenDied(VibratorToken v) {
        synchronized (this.mVibratorLock) {
            this.mVibratorTokens.remove(v.mToken);
        }
        cancelVibrateIfNeeded(v);
    }

    private void cancelVibrateIfNeeded(VibratorToken v) {
        synchronized (v) {
            if (v.mVibrating) {
                nativeCancelVibrate(this.mPtr, v.mDeviceId, v.mTokenValue);
                v.mVibrating = false;
            }
        }
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump InputManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("INPUT MANAGER (dumpsys input)\n");
        String dumpStr = nativeDump(this.mPtr);
        if (dumpStr != null) {
            pw.println(dumpStr);
        }
    }

    private boolean checkCallingPermission(String permission, String func) {
        if (Binder.getCallingPid() == Process.myPid() || this.mContext.checkCallingPermission(permission) == 0) {
            return true;
        }
        String msg = "Permission Denial: " + func + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + permission;
        Slog.w(TAG, msg);
        return false;
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        synchronized (this.mInputFilterLock) {
        }
        nativeMonitor(this.mPtr);
    }

    private void notifyConfigurationChanged(long whenNanos) {
        this.mWindowManagerCallbacks.notifyConfigurationChanged();
    }

    private void notifyInputDevicesChanged(InputDevice[] inputDevices) {
        synchronized (this.mInputDevicesLock) {
            if (!this.mInputDevicesChangedPending) {
                this.mInputDevicesChangedPending = true;
                this.mHandler.obtainMessage(1, this.mInputDevices).sendToTarget();
            }
            this.mInputDevices = inputDevices;
        }
    }

    private void notifySwitch(long whenNanos, int switchValues, int switchMask) {
        if ((switchMask & 1) != 0) {
            boolean lidOpen = (switchValues & 1) == 0;
            this.mWindowManagerCallbacks.notifyLidSwitchChanged(whenNanos, lidOpen);
        }
        if (this.mUseDevInputEventForAudioJack && (switchMask & 148) != 0) {
            this.mWiredAccessoryCallbacks.notifyWiredAccessoryChanged(whenNanos, switchValues, switchMask);
        }
    }

    private void notifyInputChannelBroken(InputWindowHandle inputWindowHandle) {
        this.mWindowManagerCallbacks.notifyInputChannelBroken(inputWindowHandle);
    }

    private long notifyANR(InputApplicationHandle inputApplicationHandle, InputWindowHandle inputWindowHandle, String reason) {
        return this.mWindowManagerCallbacks.notifyANR(inputApplicationHandle, inputWindowHandle, reason);
    }

    final boolean filterInputEvent(InputEvent event, int policyFlags) {
        synchronized (this.mInputFilterLock) {
            if (this.mInputFilter != null) {
                try {
                    this.mInputFilter.filterInputEvent(event, policyFlags);
                } catch (RemoteException e) {
                }
                return false;
            }
            event.recycle();
            return true;
        }
    }

    private int interceptKeyBeforeQueueing(KeyEvent event, int policyFlags, boolean isScreenOn) {
        return this.mWindowManagerCallbacks.interceptKeyBeforeQueueing(event, policyFlags, isScreenOn);
    }

    private int interceptMotionBeforeQueueingWhenScreenOff(int policyFlags) {
        return this.mWindowManagerCallbacks.interceptMotionBeforeQueueingWhenScreenOff(policyFlags);
    }

    private long interceptKeyBeforeDispatching(InputWindowHandle focus, KeyEvent event, int policyFlags) {
        return this.mWindowManagerCallbacks.interceptKeyBeforeDispatching(focus, event, policyFlags);
    }

    private KeyEvent dispatchUnhandledKey(InputWindowHandle focus, KeyEvent event, int policyFlags) {
        return this.mWindowManagerCallbacks.dispatchUnhandledKey(focus, event, policyFlags);
    }

    private boolean checkInjectEventsPermission(int injectorPid, int injectorUid) {
        return this.mContext.checkPermission(Manifest.permission.INJECT_EVENTS, injectorPid, injectorUid) == 0;
    }

    private int getVirtualKeyQuietTimeMillis() {
        return this.mContext.getResources().getInteger(R.integer.config_virtualKeyQuietTimeMillis);
    }

    private int getKeyRepeatTimeout() {
        return ViewConfiguration.getKeyRepeatTimeout();
    }

    private int getKeyRepeatDelay() {
        return ViewConfiguration.getKeyRepeatDelay();
    }

    private int getHoverTapTimeout() {
        return ViewConfiguration.getHoverTapTimeout();
    }

    private int getHoverTapSlop() {
        return ViewConfiguration.getHoverTapSlop();
    }

    private int getDoubleTapTimeout() {
        return ViewConfiguration.getDoubleTapTimeout();
    }

    private int getLongPressTimeout() {
        return ViewConfiguration.getLongPressTimeout();
    }

    private int getPointerLayer() {
        return this.mWindowManagerCallbacks.getPointerLayer();
    }

    private PointerIcon getPointerIcon() {
        return PointerIcon.getDefaultIcon(this.mContext);
    }

    private String[] getKeyboardLayoutOverlay(String inputDeviceDescriptor) {
        String keyboardLayoutDescriptor;
        if (!this.mSystemReady || (keyboardLayoutDescriptor = getCurrentKeyboardLayoutForInputDevice(inputDeviceDescriptor)) == null) {
            return null;
        }
        final String[] result = new String[2];
        visitKeyboardLayout(keyboardLayoutDescriptor, new KeyboardLayoutVisitor() { // from class: com.android.server.input.InputManagerService.9
            @Override // com.android.server.input.InputManagerService.KeyboardLayoutVisitor
            public void visitKeyboardLayout(Resources resources, String descriptor, String label, String collection, int keyboardLayoutResId) {
                try {
                    result[0] = descriptor;
                    result[1] = Streams.readFully(new InputStreamReader(resources.openRawResource(keyboardLayoutResId)));
                } catch (Resources.NotFoundException e) {
                } catch (IOException e2) {
                }
            }
        });
        if (result[0] == null) {
            Log.w(TAG, "Could not get keyboard layout with descriptor '" + keyboardLayoutDescriptor + "'.");
            return null;
        }
        return result;
    }

    private String getDeviceAlias(String uniqueId) {
        if (BluetoothAdapter.checkBluetoothAddress(uniqueId)) {
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputManagerService$InputManagerHandler.class */
    public final class InputManagerHandler extends Handler {
        public InputManagerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    InputManagerService.this.deliverInputDevicesChanged((InputDevice[]) msg.obj);
                    return;
                case 2:
                    InputManagerService.this.handleSwitchKeyboardLayout(msg.arg1, msg.arg2);
                    return;
                case 3:
                    InputManagerService.this.reloadKeyboardLayouts();
                    return;
                case 4:
                    InputManagerService.this.updateKeyboardLayouts();
                    return;
                case 5:
                    InputManagerService.this.reloadDeviceAliases();
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputManagerService$InputFilterHost.class */
    public final class InputFilterHost extends IInputFilterHost.Stub {
        private boolean mDisconnected;

        private InputFilterHost() {
        }

        public void disconnectLocked() {
            this.mDisconnected = true;
        }

        @Override // android.view.IInputFilterHost
        public void sendInputEvent(InputEvent event, int policyFlags) {
            if (event == null) {
                throw new IllegalArgumentException("event must not be null");
            }
            synchronized (InputManagerService.this.mInputFilterLock) {
                if (!this.mDisconnected) {
                    InputManagerService.nativeInjectInputEvent(InputManagerService.this.mPtr, event, 0, 0, 0, 0, policyFlags | 67108864);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputManagerService$KeyboardLayoutDescriptor.class */
    public static final class KeyboardLayoutDescriptor {
        public String packageName;
        public String receiverName;
        public String keyboardLayoutName;

        private KeyboardLayoutDescriptor() {
        }

        public static String format(String packageName, String receiverName, String keyboardName) {
            return packageName + Separators.SLASH + receiverName + Separators.SLASH + keyboardName;
        }

        public static KeyboardLayoutDescriptor parse(String descriptor) {
            int pos2;
            int pos = descriptor.indexOf(47);
            if (pos < 0 || pos + 1 == descriptor.length() || (pos2 = descriptor.indexOf(47, pos + 1)) < pos + 2 || pos2 + 1 == descriptor.length()) {
                return null;
            }
            KeyboardLayoutDescriptor result = new KeyboardLayoutDescriptor();
            result.packageName = descriptor.substring(0, pos);
            result.receiverName = descriptor.substring(pos + 1, pos2);
            result.keyboardLayoutName = descriptor.substring(pos2 + 1);
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputManagerService$InputDevicesChangedListenerRecord.class */
    public final class InputDevicesChangedListenerRecord implements IBinder.DeathRecipient {
        private final int mPid;
        private final IInputDevicesChangedListener mListener;

        public InputDevicesChangedListenerRecord(int pid, IInputDevicesChangedListener listener) {
            this.mPid = pid;
            this.mListener = listener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            InputManagerService.this.onInputDevicesChangedListenerDied(this.mPid);
        }

        public void notifyInputDevicesChanged(int[] info) {
            try {
                this.mListener.onInputDevicesChanged(info);
            } catch (RemoteException ex) {
                Slog.w(InputManagerService.TAG, "Failed to notify process " + this.mPid + " that input devices changed, assuming it died.", ex);
                binderDied();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputManagerService$VibratorToken.class */
    public final class VibratorToken implements IBinder.DeathRecipient {
        public final int mDeviceId;
        public final IBinder mToken;
        public final int mTokenValue;
        public boolean mVibrating;

        public VibratorToken(int deviceId, IBinder token, int tokenValue) {
            this.mDeviceId = deviceId;
            this.mToken = token;
            this.mTokenValue = tokenValue;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            InputManagerService.this.onVibratorTokenDied(this);
        }
    }
}