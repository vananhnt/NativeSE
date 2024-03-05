package com.android.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothCallback;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.bluetooth.IBluetoothManagerCallback;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.R;
import gov.nist.core.Separators;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: BluetoothManagerService.class */
public class BluetoothManagerService extends IBluetoothManager.Stub {
    private static final String TAG = "BluetoothManagerService";
    private static final boolean DBG = true;
    private static final String BLUETOOTH_ADMIN_PERM = "android.permission.BLUETOOTH_ADMIN";
    private static final String BLUETOOTH_PERM = "android.permission.BLUETOOTH";
    private static final String ACTION_SERVICE_STATE_CHANGED = "com.android.bluetooth.btservice.action.STATE_CHANGED";
    private static final String EXTRA_ACTION = "action";
    private static final String SECURE_SETTINGS_BLUETOOTH_ADDR_VALID = "bluetooth_addr_valid";
    private static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";
    private static final String SECURE_SETTINGS_BLUETOOTH_NAME = "bluetooth_name";
    private static final int TIMEOUT_BIND_MS = 3000;
    private static final int TIMEOUT_SAVE_MS = 500;
    private static final int SERVICE_RESTART_TIME_MS = 200;
    private static final int ERROR_RESTART_TIME_MS = 3000;
    private static final int USER_SWITCHED_TIME_MS = 200;
    private static final int MESSAGE_ENABLE = 1;
    private static final int MESSAGE_DISABLE = 2;
    private static final int MESSAGE_REGISTER_ADAPTER = 20;
    private static final int MESSAGE_UNREGISTER_ADAPTER = 21;
    private static final int MESSAGE_REGISTER_STATE_CHANGE_CALLBACK = 30;
    private static final int MESSAGE_UNREGISTER_STATE_CHANGE_CALLBACK = 31;
    private static final int MESSAGE_BLUETOOTH_SERVICE_CONNECTED = 40;
    private static final int MESSAGE_BLUETOOTH_SERVICE_DISCONNECTED = 41;
    private static final int MESSAGE_RESTART_BLUETOOTH_SERVICE = 42;
    private static final int MESSAGE_BLUETOOTH_STATE_CHANGE = 60;
    private static final int MESSAGE_TIMEOUT_BIND = 100;
    private static final int MESSAGE_TIMEOUT_UNBIND = 101;
    private static final int MESSAGE_GET_NAME_AND_ADDRESS = 200;
    private static final int MESSAGE_SAVE_NAME_AND_ADDRESS = 201;
    private static final int MESSAGE_USER_SWITCHED = 300;
    private static final int MAX_SAVE_RETRIES = 3;
    private static final int MAX_ERROR_RESTART_RETRIES = 6;
    private static final int BLUETOOTH_OFF = 0;
    private static final int BLUETOOTH_ON_BLUETOOTH = 1;
    private static final int BLUETOOTH_ON_AIRPLANE = 2;
    private static final int SERVICE_IBLUETOOTH = 1;
    private static final int SERVICE_IBLUETOOTHGATT = 2;
    private final Context mContext;
    private final ContentResolver mContentResolver;
    private IBluetoothGatt mBluetoothGatt;
    private boolean mEnableExternal;
    private boolean mQuietEnable = false;
    private final IBluetoothCallback mBluetoothCallback = new IBluetoothCallback.Stub() { // from class: com.android.server.BluetoothManagerService.1
        @Override // android.bluetooth.IBluetoothCallback
        public void onBluetoothStateChange(int prevState, int newState) throws RemoteException {
            Message msg = BluetoothManagerService.this.mHandler.obtainMessage(60, prevState, newState);
            BluetoothManagerService.this.mHandler.sendMessage(msg);
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.server.BluetoothManagerService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED.equals(action)) {
                String newName = intent.getStringExtra(BluetoothAdapter.EXTRA_LOCAL_NAME);
                Log.d(BluetoothManagerService.TAG, "Bluetooth Adapter name changed to " + newName);
                if (newName != null) {
                    BluetoothManagerService.this.storeNameAndAddress(newName, null);
                }
            } else if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
                synchronized (BluetoothManagerService.this.mReceiver) {
                    if (BluetoothManagerService.this.isBluetoothPersistedStateOn()) {
                        if (BluetoothManagerService.this.isAirplaneModeOn()) {
                            BluetoothManagerService.this.persistBluetoothSetting(2);
                        } else {
                            BluetoothManagerService.this.persistBluetoothSetting(1);
                        }
                    }
                    if (BluetoothManagerService.this.isAirplaneModeOn()) {
                        BluetoothManagerService.this.sendDisableMsg();
                    } else if (BluetoothManagerService.this.mEnableExternal) {
                        BluetoothManagerService.this.sendEnableMsg(BluetoothManagerService.this.mQuietEnableExternal);
                    }
                }
            } else if (Intent.ACTION_USER_SWITCHED.equals(action)) {
                BluetoothManagerService.this.mHandler.sendMessage(BluetoothManagerService.this.mHandler.obtainMessage(300, intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0), 0));
            } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
                synchronized (BluetoothManagerService.this.mReceiver) {
                    if (BluetoothManagerService.this.mEnableExternal && BluetoothManagerService.this.isBluetoothPersistedStateOnBluetooth()) {
                        Log.d(BluetoothManagerService.TAG, "Auto-enabling Bluetooth.");
                        BluetoothManagerService.this.sendEnableMsg(BluetoothManagerService.this.mQuietEnableExternal);
                    }
                }
                if (!BluetoothManagerService.this.isNameAndAddressSet()) {
                    Log.d(BluetoothManagerService.TAG, "Retrieving Bluetooth Adapter name and address...");
                    BluetoothManagerService.this.getNameAndAddress();
                }
            }
        }
    };
    private BluetoothServiceConnection mConnection = new BluetoothServiceConnection();
    private final BluetoothHandler mHandler = new BluetoothHandler(IoThread.get().getLooper());
    private IBluetooth mBluetooth = null;
    private boolean mBinding = false;
    private boolean mUnbinding = false;
    private boolean mEnable = false;
    private int mState = 10;
    private boolean mQuietEnableExternal = false;
    private String mAddress = null;
    private String mName = null;
    private int mErrorRecoveryRetryCounter = 0;
    private final RemoteCallbackList<IBluetoothManagerCallback> mCallbacks = new RemoteCallbackList<>();
    private final RemoteCallbackList<IBluetoothStateChangeCallback> mStateChangeCallbacks = new RemoteCallbackList<>();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BluetoothManagerService.checkIfCallerIsForegroundUser():boolean, file: BluetoothManagerService.class
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
    private boolean checkIfCallerIsForegroundUser() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.BluetoothManagerService.checkIfCallerIsForegroundUser():boolean, file: BluetoothManagerService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BluetoothManagerService.checkIfCallerIsForegroundUser():boolean");
    }

    private void registerForAirplaneMode(IntentFilter filter) {
        ContentResolver resolver = this.mContext.getContentResolver();
        String airplaneModeRadios = Settings.Global.getString(resolver, "airplane_mode_radios");
        Settings.Global.getString(resolver, "airplane_mode_toggleable_radios");
        boolean mIsAirplaneSensitive = airplaneModeRadios == null ? true : airplaneModeRadios.contains("bluetooth");
        if (mIsAirplaneSensitive) {
            filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothManagerService(Context context) {
        this.mContext = context;
        this.mEnableExternal = false;
        this.mContentResolver = context.getContentResolver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        filter.addAction(Intent.ACTION_USER_SWITCHED);
        registerForAirplaneMode(filter);
        this.mContext.registerReceiver(this.mReceiver, filter);
        loadStoredNameAndAddress();
        if (isBluetoothPersistedStateOn()) {
            this.mEnableExternal = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isAirplaneModeOn() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isBluetoothPersistedStateOn() {
        return Settings.Global.getInt(this.mContentResolver, "bluetooth_on", 0) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isBluetoothPersistedStateOnBluetooth() {
        return Settings.Global.getInt(this.mContentResolver, "bluetooth_on", 0) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void persistBluetoothSetting(int value) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "bluetooth_on", value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNameAndAddressSet() {
        return this.mName != null && this.mAddress != null && this.mName.length() > 0 && this.mAddress.length() > 0;
    }

    private void loadStoredNameAndAddress() {
        Log.d(TAG, "Loading stored name and address");
        if (this.mContext.getResources().getBoolean(R.bool.config_bluetooth_address_validation) && Settings.Secure.getInt(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDR_VALID, 0) == 0) {
            Log.d(TAG, "invalid bluetooth name and address stored");
            return;
        }
        this.mName = Settings.Secure.getString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_NAME);
        this.mAddress = Settings.Secure.getString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDRESS);
        Log.d(TAG, "Stored bluetooth Name=" + this.mName + ",Address=" + this.mAddress);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void storeNameAndAddress(String name, String address) {
        if (name != null) {
            Settings.Secure.putString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_NAME, name);
            this.mName = name;
            Log.d(TAG, "Stored Bluetooth name: " + Settings.Secure.getString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_NAME));
        }
        if (address != null) {
            Settings.Secure.putString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDRESS, address);
            this.mAddress = address;
            Log.d(TAG, "Stored Bluetoothaddress: " + Settings.Secure.getString(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDRESS));
        }
        if (name != null && address != null) {
            Settings.Secure.putInt(this.mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDR_VALID, 1);
        }
    }

    @Override // android.bluetooth.IBluetoothManager
    public IBluetooth registerAdapter(IBluetoothManagerCallback callback) {
        IBluetooth iBluetooth;
        Message msg = this.mHandler.obtainMessage(20);
        msg.obj = callback;
        this.mHandler.sendMessage(msg);
        synchronized (this.mConnection) {
            iBluetooth = this.mBluetooth;
        }
        return iBluetooth;
    }

    @Override // android.bluetooth.IBluetoothManager
    public void unregisterAdapter(IBluetoothManagerCallback callback) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
        Message msg = this.mHandler.obtainMessage(21);
        msg.obj = callback;
        this.mHandler.sendMessage(msg);
    }

    @Override // android.bluetooth.IBluetoothManager
    public void registerStateChangeCallback(IBluetoothStateChangeCallback callback) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
        Message msg = this.mHandler.obtainMessage(30);
        msg.obj = callback;
        this.mHandler.sendMessage(msg);
    }

    @Override // android.bluetooth.IBluetoothManager
    public void unregisterStateChangeCallback(IBluetoothStateChangeCallback callback) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
        Message msg = this.mHandler.obtainMessage(31);
        msg.obj = callback;
        this.mHandler.sendMessage(msg);
    }

    @Override // android.bluetooth.IBluetoothManager
    public boolean isEnabled() {
        boolean z;
        if (Binder.getCallingUid() != 1000 && !checkIfCallerIsForegroundUser()) {
            Log.w(TAG, "isEnabled(): not allowed for non-active and non system user");
            return false;
        }
        synchronized (this.mConnection) {
            try {
                if (this.mBluetooth != null) {
                    z = this.mBluetooth.isEnabled();
                }
            } catch (RemoteException e) {
                Log.e(TAG, "isEnabled()", e);
                return false;
            }
        }
        return z;
    }

    public void getNameAndAddress() {
        Log.d(TAG, "getNameAndAddress(): mBluetooth = " + this.mBluetooth + " mBinding = " + this.mBinding);
        Message msg = this.mHandler.obtainMessage(200);
        this.mHandler.sendMessage(msg);
    }

    @Override // android.bluetooth.IBluetoothManager
    public boolean enableNoAutoConnect() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH_ADMIN", "Need BLUETOOTH ADMIN permission");
        Log.d(TAG, "enableNoAutoConnect():  mBluetooth =" + this.mBluetooth + " mBinding = " + this.mBinding);
        int callingAppId = UserHandle.getAppId(Binder.getCallingUid());
        if (callingAppId != 1027) {
            throw new SecurityException("no permission to enable Bluetooth quietly");
        }
        synchronized (this.mReceiver) {
            this.mQuietEnableExternal = true;
            this.mEnableExternal = true;
            sendEnableMsg(true);
        }
        return true;
    }

    @Override // android.bluetooth.IBluetoothManager
    public boolean enable() {
        if (Binder.getCallingUid() != 1000 && !checkIfCallerIsForegroundUser()) {
            Log.w(TAG, "enable(): not allowed for non-active and non system user");
            return false;
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH_ADMIN", "Need BLUETOOTH ADMIN permission");
        Log.d(TAG, "enable():  mBluetooth =" + this.mBluetooth + " mBinding = " + this.mBinding);
        synchronized (this.mReceiver) {
            this.mQuietEnableExternal = false;
            this.mEnableExternal = true;
            long callingIdentity = Binder.clearCallingIdentity();
            persistBluetoothSetting(1);
            Binder.restoreCallingIdentity(callingIdentity);
            sendEnableMsg(false);
        }
        return true;
    }

    @Override // android.bluetooth.IBluetoothManager
    public boolean disable(boolean persist) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH_ADMIN", "Need BLUETOOTH ADMIN permissicacheNameAndAddresson");
        if (Binder.getCallingUid() != 1000 && !checkIfCallerIsForegroundUser()) {
            Log.w(TAG, "disable(): not allowed for non-active and non system user");
            return false;
        }
        Log.d(TAG, "disable(): mBluetooth = " + this.mBluetooth + " mBinding = " + this.mBinding);
        synchronized (this.mReceiver) {
            if (persist) {
                long callingIdentity = Binder.clearCallingIdentity();
                persistBluetoothSetting(0);
                Binder.restoreCallingIdentity(callingIdentity);
            }
            this.mEnableExternal = false;
            sendDisableMsg();
        }
        return true;
    }

    public void unbindAndFinish() {
        Log.d(TAG, "unbindAndFinish(): " + this.mBluetooth + " mBinding = " + this.mBinding);
        synchronized (this.mConnection) {
            if (this.mUnbinding) {
                return;
            }
            this.mUnbinding = true;
            if (this.mBluetooth != null) {
                if (!this.mConnection.isGetNameAddressOnly()) {
                    try {
                        this.mBluetooth.unregisterCallback(this.mBluetoothCallback);
                    } catch (RemoteException re) {
                        Log.e(TAG, "Unable to unregister BluetoothCallback", re);
                    }
                }
                Log.d(TAG, "Sending unbind request.");
                this.mBluetooth = null;
                this.mContext.unbindService(this.mConnection);
                this.mUnbinding = false;
                this.mBinding = false;
            } else {
                this.mUnbinding = false;
            }
        }
    }

    @Override // android.bluetooth.IBluetoothManager
    public IBluetoothGatt getBluetoothGatt() {
        return this.mBluetoothGatt;
    }

    private void sendBluetoothStateCallback(boolean isUp) {
        int n = this.mStateChangeCallbacks.beginBroadcast();
        Log.d(TAG, "Broadcasting onBluetoothStateChange(" + isUp + ") to " + n + " receivers.");
        for (int i = 0; i < n; i++) {
            try {
                this.mStateChangeCallbacks.getBroadcastItem(i).onBluetoothStateChange(isUp);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to call onBluetoothStateChange() on callback #" + i, e);
            }
        }
        this.mStateChangeCallbacks.finishBroadcast();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBluetoothServiceUpCallback() {
        if (!this.mConnection.isGetNameAddressOnly()) {
            Log.d(TAG, "Calling onBluetoothServiceUp callbacks");
            int n = this.mCallbacks.beginBroadcast();
            Log.d(TAG, "Broadcasting onBluetoothServiceUp() to " + n + " receivers.");
            for (int i = 0; i < n; i++) {
                try {
                    this.mCallbacks.getBroadcastItem(i).onBluetoothServiceUp(this.mBluetooth);
                } catch (RemoteException e) {
                    Log.e(TAG, "Unable to call onBluetoothServiceUp() on callback #" + i, e);
                }
            }
            this.mCallbacks.finishBroadcast();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBluetoothServiceDownCallback() {
        if (!this.mConnection.isGetNameAddressOnly()) {
            Log.d(TAG, "Calling onBluetoothServiceDown callbacks");
            int n = this.mCallbacks.beginBroadcast();
            Log.d(TAG, "Broadcasting onBluetoothServiceDown() to " + n + " receivers.");
            for (int i = 0; i < n; i++) {
                try {
                    this.mCallbacks.getBroadcastItem(i).onBluetoothServiceDown();
                } catch (RemoteException e) {
                    Log.e(TAG, "Unable to call onBluetoothServiceDown() on callback #" + i, e);
                }
            }
            this.mCallbacks.finishBroadcast();
        }
    }

    @Override // android.bluetooth.IBluetoothManager
    public String getAddress() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
        if (Binder.getCallingUid() != 1000 && !checkIfCallerIsForegroundUser()) {
            Log.w(TAG, "getAddress(): not allowed for non-active and non system user");
            return null;
        }
        synchronized (this.mConnection) {
            if (this.mBluetooth != null) {
                try {
                    return this.mBluetooth.getAddress();
                } catch (RemoteException e) {
                    Log.e(TAG, "getAddress(): Unable to retrieve address remotely..Returning cached address", e);
                }
            }
            return this.mAddress;
        }
    }

    @Override // android.bluetooth.IBluetoothManager
    public String getName() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BLUETOOTH", "Need BLUETOOTH permission");
        if (Binder.getCallingUid() != 1000 && !checkIfCallerIsForegroundUser()) {
            Log.w(TAG, "getName(): not allowed for non-active and non system user");
            return null;
        }
        synchronized (this.mConnection) {
            if (this.mBluetooth != null) {
                try {
                    return this.mBluetooth.getName();
                } catch (RemoteException e) {
                    Log.e(TAG, "getName(): Unable to retrieve name remotely..Returning cached name", e);
                }
            }
            return this.mName;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: BluetoothManagerService$BluetoothServiceConnection.class */
    public class BluetoothServiceConnection implements ServiceConnection {
        private boolean mGetNameAddressOnly;

        private BluetoothServiceConnection() {
        }

        public void setGetNameAddressOnly(boolean getOnly) {
            this.mGetNameAddressOnly = getOnly;
        }

        public boolean isGetNameAddressOnly() {
            return this.mGetNameAddressOnly;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(BluetoothManagerService.TAG, "BluetoothServiceConnection: " + className.getClassName());
            Message msg = BluetoothManagerService.this.mHandler.obtainMessage(40);
            if (className.getClassName().equals("com.android.bluetooth.btservice.AdapterService")) {
                msg.arg1 = 1;
            } else if (className.getClassName().equals("com.android.bluetooth.gatt.GattService")) {
                msg.arg1 = 2;
            } else {
                Log.e(BluetoothManagerService.TAG, "Unknown service connected: " + className.getClassName());
                return;
            }
            msg.obj = service;
            BluetoothManagerService.this.mHandler.sendMessage(msg);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            Log.d(BluetoothManagerService.TAG, "BluetoothServiceConnection, disconnected: " + className.getClassName());
            Message msg = BluetoothManagerService.this.mHandler.obtainMessage(41);
            if (className.getClassName().equals("com.android.bluetooth.btservice.AdapterService")) {
                msg.arg1 = 1;
            } else if (className.getClassName().equals("com.android.bluetooth.gatt.GattService")) {
                msg.arg1 = 2;
            } else {
                Log.e(BluetoothManagerService.TAG, "Unknown service disconnected: " + className.getClassName());
                return;
            }
            BluetoothManagerService.this.mHandler.sendMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: BluetoothManagerService$BluetoothHandler.class */
    public class BluetoothHandler extends Handler {
        public BluetoothHandler(Looper looper) {
            super(looper);
        }

        /* JADX WARN: Finally extract failed */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Log.d(BluetoothManagerService.TAG, "Message: " + msg.what);
            switch (msg.what) {
                case 1:
                    Log.d(BluetoothManagerService.TAG, "MESSAGE_ENABLE: mBluetooth = " + BluetoothManagerService.this.mBluetooth);
                    BluetoothManagerService.this.mHandler.removeMessages(42);
                    BluetoothManagerService.this.mEnable = true;
                    BluetoothManagerService.this.handleEnable(msg.arg1 == 1);
                    return;
                case 2:
                    BluetoothManagerService.this.mHandler.removeMessages(42);
                    if (!BluetoothManagerService.this.mEnable || BluetoothManagerService.this.mBluetooth == null) {
                        BluetoothManagerService.this.mEnable = false;
                        BluetoothManagerService.this.handleDisable();
                        return;
                    }
                    BluetoothManagerService.this.waitForOnOff(true, false);
                    BluetoothManagerService.this.mEnable = false;
                    BluetoothManagerService.this.handleDisable();
                    BluetoothManagerService.this.waitForOnOff(false, false);
                    return;
                case 20:
                    IBluetoothManagerCallback callback = (IBluetoothManagerCallback) msg.obj;
                    boolean added = BluetoothManagerService.this.mCallbacks.register(callback);
                    Log.d(BluetoothManagerService.TAG, "Added callback: " + (callback == null ? "null" : callback) + Separators.COLON + added);
                    return;
                case 21:
                    IBluetoothManagerCallback callback2 = (IBluetoothManagerCallback) msg.obj;
                    boolean removed = BluetoothManagerService.this.mCallbacks.unregister(callback2);
                    Log.d(BluetoothManagerService.TAG, "Removed callback: " + (callback2 == null ? "null" : callback2) + Separators.COLON + removed);
                    return;
                case 30:
                    IBluetoothStateChangeCallback callback3 = (IBluetoothStateChangeCallback) msg.obj;
                    if (callback3 != null) {
                        BluetoothManagerService.this.mStateChangeCallbacks.register(callback3);
                        return;
                    }
                    return;
                case 31:
                    IBluetoothStateChangeCallback callback4 = (IBluetoothStateChangeCallback) msg.obj;
                    if (callback4 != null) {
                        BluetoothManagerService.this.mStateChangeCallbacks.unregister(callback4);
                        return;
                    }
                    return;
                case 40:
                    Log.d(BluetoothManagerService.TAG, "MESSAGE_BLUETOOTH_SERVICE_CONNECTED: " + msg.arg1);
                    IBinder service = (IBinder) msg.obj;
                    synchronized (BluetoothManagerService.this.mConnection) {
                        if (msg.arg1 == 2) {
                            BluetoothManagerService.this.mBluetoothGatt = IBluetoothGatt.Stub.asInterface(service);
                            return;
                        }
                        BluetoothManagerService.this.mHandler.removeMessages(100);
                        BluetoothManagerService.this.mBinding = false;
                        BluetoothManagerService.this.mBluetooth = IBluetooth.Stub.asInterface(service);
                        try {
                            boolean enableHciSnoopLog = Settings.Secure.getInt(BluetoothManagerService.this.mContentResolver, Settings.Secure.BLUETOOTH_HCI_LOG, 0) == 1;
                            if (!BluetoothManagerService.this.mBluetooth.configHciSnoopLog(enableHciSnoopLog)) {
                                Log.e(BluetoothManagerService.TAG, "IBluetooth.configHciSnoopLog return false");
                            }
                        } catch (RemoteException e) {
                            Log.e(BluetoothManagerService.TAG, "Unable to call configHciSnoopLog", e);
                        }
                        if (BluetoothManagerService.this.mConnection.isGetNameAddressOnly()) {
                            Message getMsg = BluetoothManagerService.this.mHandler.obtainMessage(200);
                            BluetoothManagerService.this.mHandler.sendMessage(getMsg);
                            if (!BluetoothManagerService.this.mEnable) {
                                return;
                            }
                        }
                        BluetoothManagerService.this.mConnection.setGetNameAddressOnly(false);
                        try {
                            BluetoothManagerService.this.mBluetooth.registerCallback(BluetoothManagerService.this.mBluetoothCallback);
                        } catch (RemoteException re) {
                            Log.e(BluetoothManagerService.TAG, "Unable to register BluetoothCallback", re);
                        }
                        BluetoothManagerService.this.sendBluetoothServiceUpCallback();
                        try {
                            if (!BluetoothManagerService.this.mQuietEnable) {
                                if (!BluetoothManagerService.this.mBluetooth.enable()) {
                                    Log.e(BluetoothManagerService.TAG, "IBluetooth.enable() returned false");
                                }
                            } else if (!BluetoothManagerService.this.mBluetooth.enableNoAutoConnect()) {
                                Log.e(BluetoothManagerService.TAG, "IBluetooth.enableNoAutoConnect() returned false");
                            }
                        } catch (RemoteException e2) {
                            Log.e(BluetoothManagerService.TAG, "Unable to call enable()", e2);
                        }
                        if (!BluetoothManagerService.this.mEnable) {
                            BluetoothManagerService.this.waitForOnOff(true, false);
                            BluetoothManagerService.this.handleDisable();
                            BluetoothManagerService.this.waitForOnOff(false, false);
                            return;
                        }
                        return;
                    }
                case 41:
                    Log.e(BluetoothManagerService.TAG, "MESSAGE_BLUETOOTH_SERVICE_DISCONNECTED: " + msg.arg1);
                    BluetoothServiceConnection bluetoothServiceConnection = BluetoothManagerService.this.mConnection;
                    BluetoothServiceConnection bluetoothServiceConnection2 = bluetoothServiceConnection;
                    synchronized (bluetoothServiceConnection) {
                        try {
                            if (msg.arg1 == 1) {
                                if (BluetoothManagerService.this.mBluetooth == null) {
                                    bluetoothServiceConnection2 = bluetoothServiceConnection2;
                                } else {
                                    BluetoothManagerService.this.mBluetooth = null;
                                    Message message = bluetoothServiceConnection2;
                                    if (BluetoothManagerService.this.mEnable) {
                                        BluetoothManagerService.this.mEnable = false;
                                        Message restartMsg = BluetoothManagerService.this.mHandler.obtainMessage(42);
                                        BluetoothManagerService.this.mHandler.sendMessageDelayed(restartMsg, 200L);
                                        message = restartMsg;
                                    }
                                    bluetoothServiceConnection2 = message;
                                    if (!BluetoothManagerService.this.mConnection.isGetNameAddressOnly()) {
                                        BluetoothManagerService.this.sendBluetoothServiceDownCallback();
                                        if (BluetoothManagerService.this.mState == 11 || BluetoothManagerService.this.mState == 12) {
                                            BluetoothManagerService.this.bluetoothStateChangeHandler(12, 13);
                                            BluetoothManagerService.this.mState = 13;
                                        }
                                        if (BluetoothManagerService.this.mState == 13) {
                                            BluetoothManagerService.this.bluetoothStateChangeHandler(13, 10);
                                        }
                                        BluetoothManagerService.this.mHandler.removeMessages(60);
                                        BluetoothManagerService.this.mState = 10;
                                        bluetoothServiceConnection2 = message;
                                    }
                                }
                            } else if (msg.arg1 == 2) {
                                BluetoothManagerService.this.mBluetoothGatt = null;
                                bluetoothServiceConnection2 = bluetoothServiceConnection2;
                            } else {
                                Log.e(BluetoothManagerService.TAG, "Bad msg.arg1: " + msg.arg1);
                                bluetoothServiceConnection2 = bluetoothServiceConnection2;
                            }
                            return;
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                case 42:
                    Log.d(BluetoothManagerService.TAG, "MESSAGE_RESTART_BLUETOOTH_SERVICE: Restart IBluetooth service");
                    BluetoothManagerService.this.mEnable = true;
                    BluetoothManagerService.this.handleEnable(BluetoothManagerService.this.mQuietEnable);
                    return;
                case 60:
                    int prevState = msg.arg1;
                    int newState = msg.arg2;
                    Log.d(BluetoothManagerService.TAG, "MESSAGE_BLUETOOTH_STATE_CHANGE: prevState = " + prevState + ", newState=" + newState);
                    BluetoothManagerService.this.mState = newState;
                    BluetoothManagerService.this.bluetoothStateChangeHandler(prevState, newState);
                    if (prevState == 11 && newState == 10 && BluetoothManagerService.this.mBluetooth != null && BluetoothManagerService.this.mEnable) {
                        BluetoothManagerService.this.recoverBluetoothServiceFromError();
                    }
                    if (newState == 12 && BluetoothManagerService.this.mErrorRecoveryRetryCounter != 0) {
                        Log.w(BluetoothManagerService.TAG, "bluetooth is recovered from error");
                        BluetoothManagerService.this.mErrorRecoveryRetryCounter = 0;
                        return;
                    }
                    return;
                case 100:
                    Log.e(BluetoothManagerService.TAG, "MESSAGE_TIMEOUT_BIND");
                    synchronized (BluetoothManagerService.this.mConnection) {
                        BluetoothManagerService.this.mBinding = false;
                    }
                    return;
                case 101:
                    Log.e(BluetoothManagerService.TAG, "MESSAGE_TIMEOUT_UNBIND");
                    synchronized (BluetoothManagerService.this.mConnection) {
                        BluetoothManagerService.this.mUnbinding = false;
                    }
                    return;
                case 200:
                    Log.d(BluetoothManagerService.TAG, "MESSAGE_GET_NAME_AND_ADDRESS");
                    synchronized (BluetoothManagerService.this.mConnection) {
                        if (BluetoothManagerService.this.mBluetooth != null || BluetoothManagerService.this.mBinding) {
                            Message saveMsg = BluetoothManagerService.this.mHandler.obtainMessage(201);
                            saveMsg.arg1 = 0;
                            if (BluetoothManagerService.this.mBluetooth != null) {
                                BluetoothManagerService.this.mHandler.sendMessage(saveMsg);
                            } else {
                                BluetoothManagerService.this.mHandler.sendMessageDelayed(saveMsg, 500L);
                            }
                        } else {
                            Log.d(BluetoothManagerService.TAG, "Binding to service to get name and address");
                            BluetoothManagerService.this.mConnection.setGetNameAddressOnly(true);
                            Message timeoutMsg = BluetoothManagerService.this.mHandler.obtainMessage(100);
                            BluetoothManagerService.this.mHandler.sendMessageDelayed(timeoutMsg, 3000L);
                            Intent i = new Intent(IBluetooth.class.getName());
                            if (!BluetoothManagerService.this.doBind(i, BluetoothManagerService.this.mConnection, 1, UserHandle.CURRENT)) {
                                BluetoothManagerService.this.mHandler.removeMessages(100);
                            } else {
                                BluetoothManagerService.this.mBinding = true;
                            }
                        }
                    }
                    return;
                case 201:
                    boolean unbind = false;
                    Log.d(BluetoothManagerService.TAG, "MESSAGE_SAVE_NAME_AND_ADDRESS");
                    synchronized (BluetoothManagerService.this.mConnection) {
                        if (!BluetoothManagerService.this.mEnable && BluetoothManagerService.this.mBluetooth != null) {
                            try {
                                BluetoothManagerService.this.mBluetooth.enable();
                            } catch (RemoteException e3) {
                                Log.e(BluetoothManagerService.TAG, "Unable to call enable()", e3);
                            }
                        }
                    }
                    if (BluetoothManagerService.this.mBluetooth != null) {
                        BluetoothManagerService.this.waitForOnOff(true, false);
                    }
                    synchronized (BluetoothManagerService.this.mConnection) {
                        if (BluetoothManagerService.this.mBluetooth == null) {
                            Message getMsg2 = BluetoothManagerService.this.mHandler.obtainMessage(200);
                            BluetoothManagerService.this.mHandler.sendMessage(getMsg2);
                        } else {
                            String name = null;
                            String address = null;
                            try {
                                name = BluetoothManagerService.this.mBluetooth.getName();
                                address = BluetoothManagerService.this.mBluetooth.getAddress();
                            } catch (RemoteException re2) {
                                Log.e(BluetoothManagerService.TAG, "", re2);
                            }
                            if (name != null && address != null) {
                                BluetoothManagerService.this.storeNameAndAddress(name, address);
                                if (BluetoothManagerService.this.mConnection.isGetNameAddressOnly()) {
                                    unbind = true;
                                }
                            } else if (msg.arg1 < 3) {
                                Message retryMsg = BluetoothManagerService.this.mHandler.obtainMessage(201);
                                retryMsg.arg1 = 1 + msg.arg1;
                                Log.d(BluetoothManagerService.TAG, "Retrying name/address remote retrieval and save.....Retry count =" + retryMsg.arg1);
                                BluetoothManagerService.this.mHandler.sendMessageDelayed(retryMsg, 500L);
                            } else {
                                Log.w(BluetoothManagerService.TAG, "Maximum name/address remote retrieval retry exceeded");
                                if (BluetoothManagerService.this.mConnection.isGetNameAddressOnly()) {
                                    unbind = true;
                                }
                            }
                            if (!BluetoothManagerService.this.mEnable) {
                                try {
                                    BluetoothManagerService.this.mBluetooth.disable();
                                } catch (RemoteException e4) {
                                    Log.e(BluetoothManagerService.TAG, "Unable to call disable()", e4);
                                }
                            }
                        }
                    }
                    if (!BluetoothManagerService.this.mEnable && BluetoothManagerService.this.mBluetooth != null) {
                        BluetoothManagerService.this.waitForOnOff(false, true);
                    }
                    if (unbind) {
                        BluetoothManagerService.this.unbindAndFinish();
                        return;
                    }
                    return;
                case 300:
                    Log.d(BluetoothManagerService.TAG, "MESSAGE_USER_SWITCHED");
                    BluetoothManagerService.this.mHandler.removeMessages(300);
                    if (!BluetoothManagerService.this.mEnable || BluetoothManagerService.this.mBluetooth == null) {
                        if (BluetoothManagerService.this.mBinding || BluetoothManagerService.this.mBluetooth != null) {
                            Message userMsg = BluetoothManagerService.this.mHandler.obtainMessage(300);
                            userMsg.arg2 = 1 + msg.arg2;
                            BluetoothManagerService.this.mHandler.sendMessageDelayed(userMsg, 200L);
                            Log.d(BluetoothManagerService.TAG, "delay MESSAGE_USER_SWITCHED " + userMsg.arg2);
                            return;
                        }
                        return;
                    }
                    synchronized (BluetoothManagerService.this.mConnection) {
                        if (BluetoothManagerService.this.mBluetooth != null) {
                            try {
                                BluetoothManagerService.this.mBluetooth.unregisterCallback(BluetoothManagerService.this.mBluetoothCallback);
                            } catch (RemoteException re3) {
                                Log.e(BluetoothManagerService.TAG, "Unable to unregister", re3);
                            }
                        }
                    }
                    if (BluetoothManagerService.this.mState == 13) {
                        BluetoothManagerService.this.bluetoothStateChangeHandler(BluetoothManagerService.this.mState, 10);
                        BluetoothManagerService.this.mState = 10;
                    }
                    if (BluetoothManagerService.this.mState == 10) {
                        BluetoothManagerService.this.bluetoothStateChangeHandler(BluetoothManagerService.this.mState, 11);
                        BluetoothManagerService.this.mState = 11;
                    }
                    BluetoothManagerService.this.waitForOnOff(true, false);
                    if (BluetoothManagerService.this.mState == 11) {
                        BluetoothManagerService.this.bluetoothStateChangeHandler(BluetoothManagerService.this.mState, 12);
                    }
                    BluetoothManagerService.this.handleDisable();
                    BluetoothManagerService.this.bluetoothStateChangeHandler(12, 13);
                    BluetoothManagerService.this.waitForOnOff(false, true);
                    BluetoothManagerService.this.bluetoothStateChangeHandler(13, 10);
                    BluetoothManagerService.this.sendBluetoothServiceDownCallback();
                    synchronized (BluetoothManagerService.this.mConnection) {
                        if (BluetoothManagerService.this.mBluetooth != null) {
                            BluetoothManagerService.this.mBluetooth = null;
                            BluetoothManagerService.this.mContext.unbindService(BluetoothManagerService.this.mConnection);
                        }
                    }
                    SystemClock.sleep(100L);
                    BluetoothManagerService.this.mHandler.removeMessages(60);
                    BluetoothManagerService.this.mState = 10;
                    BluetoothManagerService.this.handleEnable(BluetoothManagerService.this.mQuietEnable);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleEnable(boolean quietMode) {
        this.mQuietEnable = quietMode;
        synchronized (this.mConnection) {
            if (this.mBluetooth == null && !this.mBinding) {
                Message timeoutMsg = this.mHandler.obtainMessage(100);
                this.mHandler.sendMessageDelayed(timeoutMsg, 3000L);
                this.mConnection.setGetNameAddressOnly(false);
                Intent i = new Intent(IBluetooth.class.getName());
                if (!doBind(i, this.mConnection, 1, UserHandle.CURRENT)) {
                    this.mHandler.removeMessages(100);
                } else {
                    this.mBinding = true;
                }
            } else if (this.mBluetooth != null) {
                if (this.mConnection.isGetNameAddressOnly()) {
                    this.mConnection.setGetNameAddressOnly(false);
                    try {
                        this.mBluetooth.registerCallback(this.mBluetoothCallback);
                    } catch (RemoteException re) {
                        Log.e(TAG, "Unable to register BluetoothCallback", re);
                    }
                    sendBluetoothServiceUpCallback();
                }
                try {
                    if (!this.mQuietEnable) {
                        if (!this.mBluetooth.enable()) {
                            Log.e(TAG, "IBluetooth.enable() returned false");
                        }
                    } else if (!this.mBluetooth.enableNoAutoConnect()) {
                        Log.e(TAG, "IBluetooth.enableNoAutoConnect() returned false");
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "Unable to call enable()", e);
                }
            }
        }
    }

    boolean doBind(Intent intent, ServiceConnection conn, int flags, UserHandle user) {
        ComponentName comp = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(comp);
        if (comp == null || !this.mContext.bindServiceAsUser(intent, conn, flags, user)) {
            Log.e(TAG, "Fail to bind to: " + intent);
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDisable() {
        synchronized (this.mConnection) {
            if (this.mBluetooth != null && !this.mConnection.isGetNameAddressOnly()) {
                Log.d(TAG, "Sending off request.");
                try {
                    if (!this.mBluetooth.disable()) {
                        Log.e(TAG, "IBluetooth.disable() returned false");
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "Unable to call disable()", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bluetoothStateChangeHandler(int prevState, int newState) {
        if (prevState != newState) {
            if (newState == 12 || newState == 10) {
                boolean isUp = newState == 12;
                sendBluetoothStateCallback(isUp);
                if (isUp) {
                    if (this.mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        Intent i = new Intent(IBluetoothGatt.class.getName());
                        doBind(i, this.mConnection, 1, UserHandle.CURRENT);
                    }
                } else if (!isUp && canUnbindBluetoothService()) {
                    sendBluetoothServiceDownCallback();
                    unbindAndFinish();
                }
            }
            Intent intent = new Intent(BluetoothAdapter.ACTION_STATE_CHANGED);
            intent.putExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, prevState);
            intent.putExtra(BluetoothAdapter.EXTRA_STATE, newState);
            intent.addFlags(67108864);
            Log.d(TAG, "Bluetooth State Change Intent: " + prevState + " -> " + newState);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.BLUETOOTH");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0082, code lost:
        if (r5 != false) goto L24;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0086, code lost:
        if (r6 == false) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0089, code lost:
        android.os.SystemClock.sleep(300);
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0092, code lost:
        android.os.SystemClock.sleep(50);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean waitForOnOff(boolean r5, boolean r6) {
        /*
            r4 = this;
            r0 = 0
            r7 = r0
        L2:
            r0 = r7
            r1 = 10
            if (r0 >= r1) goto L9e
            r0 = r4
            com.android.server.BluetoothManagerService$BluetoothServiceConnection r0 = r0.mConnection
            r1 = r0
            r8 = r1
            monitor-enter(r0)
            r0 = r4
            android.bluetooth.IBluetooth r0 = r0.mBluetooth     // Catch: android.os.RemoteException -> L61 java.lang.Throwable -> L79
            if (r0 != 0) goto L1d
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L79
            goto L9e
        L1d:
            r0 = r5
            if (r0 == 0) goto L34
            r0 = r4
            android.bluetooth.IBluetooth r0 = r0.mBluetooth     // Catch: android.os.RemoteException -> L61 java.lang.Throwable -> L79
            int r0 = r0.getState()     // Catch: android.os.RemoteException -> L61 java.lang.Throwable -> L79
            r1 = 12
            if (r0 != r1) goto L5e
            r0 = 1
            r1 = r8
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L79
            return r0
        L34:
            r0 = r6
            if (r0 == 0) goto L4b
            r0 = r4
            android.bluetooth.IBluetooth r0 = r0.mBluetooth     // Catch: android.os.RemoteException -> L61 java.lang.Throwable -> L79
            int r0 = r0.getState()     // Catch: android.os.RemoteException -> L61 java.lang.Throwable -> L79
            r1 = 10
            if (r0 != r1) goto L5e
            r0 = 1
            r1 = r8
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L79
            return r0
        L4b:
            r0 = r4
            android.bluetooth.IBluetooth r0 = r0.mBluetooth     // Catch: android.os.RemoteException -> L61 java.lang.Throwable -> L79
            int r0 = r0.getState()     // Catch: android.os.RemoteException -> L61 java.lang.Throwable -> L79
            r1 = 12
            if (r0 == r1) goto L5e
            r0 = 1
            r1 = r8
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L79
            return r0
        L5e:
            goto L73
        L61:
            r9 = move-exception
            java.lang.String r0 = "BluetoothManagerService"
            java.lang.String r1 = "getState()"
            r2 = r9
            int r0 = android.util.Log.e(r0, r1, r2)     // Catch: java.lang.Throwable -> L79
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L79
            goto L9e
        L73:
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L79
            goto L81
        L79:
            r10 = move-exception
            r0 = r8
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L79
            r0 = r10
            throw r0
        L81:
            r0 = r5
            if (r0 != 0) goto L89
            r0 = r6
            if (r0 == 0) goto L92
        L89:
            r0 = 300(0x12c, double:1.48E-321)
            android.os.SystemClock.sleep(r0)
            goto L98
        L92:
            r0 = 50
            android.os.SystemClock.sleep(r0)
        L98:
            int r7 = r7 + 1
            goto L2
        L9e:
            java.lang.String r0 = "BluetoothManagerService"
            java.lang.String r1 = "waitForOnOff time out"
            int r0 = android.util.Log.e(r0, r1)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BluetoothManagerService.waitForOnOff(boolean, boolean):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendDisableMsg() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEnableMsg(boolean quietMode) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, quietMode ? 1 : 0, 0));
    }

    private boolean canUnbindBluetoothService() {
        synchronized (this.mConnection) {
            try {
                if (this.mEnable || this.mBluetooth == null) {
                    return false;
                }
                if (this.mHandler.hasMessages(60)) {
                    return false;
                }
                return this.mBluetooth.getState() == 10;
            } catch (RemoteException e) {
                Log.e(TAG, "getState()", e);
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recoverBluetoothServiceFromError() {
        Log.e(TAG, "recoverBluetoothServiceFromError");
        synchronized (this.mConnection) {
            if (this.mBluetooth != null) {
                try {
                    this.mBluetooth.unregisterCallback(this.mBluetoothCallback);
                } catch (RemoteException re) {
                    Log.e(TAG, "Unable to unregister", re);
                }
            }
        }
        SystemClock.sleep(500L);
        handleDisable();
        waitForOnOff(false, true);
        sendBluetoothServiceDownCallback();
        synchronized (this.mConnection) {
            if (this.mBluetooth != null) {
                this.mBluetooth = null;
                this.mContext.unbindService(this.mConnection);
            }
        }
        this.mHandler.removeMessages(60);
        this.mState = 10;
        this.mEnable = false;
        int i = this.mErrorRecoveryRetryCounter;
        this.mErrorRecoveryRetryCounter = i + 1;
        if (i < 6) {
            Message restartMsg = this.mHandler.obtainMessage(42);
            this.mHandler.sendMessageDelayed(restartMsg, 3000L);
        }
    }
}