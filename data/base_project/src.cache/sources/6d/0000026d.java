package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothHeadset;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.List;

/* loaded from: BluetoothHeadset.class */
public final class BluetoothHeadset implements BluetoothProfile {
    private static final String TAG = "BluetoothHeadset";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_AUDIO_STATE_CHANGED = "android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED";
    public static final String ACTION_VENDOR_SPECIFIC_HEADSET_EVENT = "android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT";
    public static final String EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_CMD = "android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_CMD";
    public static final String EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_CMD_TYPE = "android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_CMD_TYPE";
    public static final int AT_CMD_TYPE_READ = 0;
    public static final int AT_CMD_TYPE_TEST = 1;
    public static final int AT_CMD_TYPE_SET = 2;
    public static final int AT_CMD_TYPE_BASIC = 3;
    public static final int AT_CMD_TYPE_ACTION = 4;
    public static final String EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_ARGS = "android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_ARGS";
    public static final String VENDOR_SPECIFIC_HEADSET_EVENT_COMPANY_ID_CATEGORY = "android.bluetooth.headset.intent.category.companyid";
    public static final String VENDOR_RESULT_CODE_COMMAND_ANDROID = "+ANDROID";
    public static final int STATE_AUDIO_DISCONNECTED = 10;
    public static final int STATE_AUDIO_CONNECTING = 11;
    public static final int STATE_AUDIO_CONNECTED = 12;
    private Context mContext;
    private BluetoothProfile.ServiceListener mServiceListener;
    private IBluetoothHeadset mService;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothHeadset.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean up) {
            Log.d(BluetoothHeadset.TAG, "onBluetoothStateChange: up=" + up);
            if (!up) {
                synchronized (BluetoothHeadset.this.mConnection) {
                    try {
                        BluetoothHeadset.this.mService = null;
                        BluetoothHeadset.this.mContext.unbindService(BluetoothHeadset.this.mConnection);
                    } catch (Exception re) {
                        Log.e(BluetoothHeadset.TAG, "", re);
                    }
                }
                return;
            }
            synchronized (BluetoothHeadset.this.mConnection) {
                try {
                    if (BluetoothHeadset.this.mService == null) {
                        BluetoothHeadset.this.doBind();
                    }
                } catch (Exception re2) {
                    Log.e(BluetoothHeadset.TAG, "", re2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothHeadset.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(BluetoothHeadset.TAG, "Proxy object connected");
            BluetoothHeadset.this.mService = IBluetoothHeadset.Stub.asInterface(service);
            if (BluetoothHeadset.this.mServiceListener != null) {
                BluetoothHeadset.this.mServiceListener.onServiceConnected(1, BluetoothHeadset.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            Log.d(BluetoothHeadset.TAG, "Proxy object disconnected");
            BluetoothHeadset.this.mService = null;
            if (BluetoothHeadset.this.mServiceListener != null) {
                BluetoothHeadset.this.mServiceListener.onServiceDisconnected(1);
            }
        }
    };
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothHeadset(Context context, BluetoothProfile.ServiceListener l) {
        this.mContext = context;
        this.mServiceListener = l;
        IBluetoothManager mgr = this.mAdapter.getBluetoothManager();
        if (mgr != null) {
            try {
                mgr.registerStateChangeCallback(this.mBluetoothStateChangeCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }
        }
        doBind();
    }

    boolean doBind() {
        Intent intent = new Intent(IBluetoothHeadset.class.getName());
        ComponentName comp = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(comp);
        if (comp == null || !this.mContext.bindService(intent, this.mConnection, 0)) {
            Log.e(TAG, "Could not bind to Bluetooth Headset Service with " + intent);
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        IBluetoothManager mgr = this.mAdapter.getBluetoothManager();
        if (mgr != null) {
            try {
                mgr.unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
            } catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }
        synchronized (this.mConnection) {
            if (this.mService != null) {
                try {
                    this.mService = null;
                    this.mContext.unbindService(this.mConnection);
                } catch (Exception re) {
                    Log.e(TAG, "", re);
                }
            }
        }
        this.mServiceListener = null;
    }

    public boolean connect(BluetoothDevice device) {
        log("connect(" + device + Separators.RPAREN);
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.connect(device);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    public boolean disconnect(BluetoothDevice device) {
        log("disconnect(" + device + Separators.RPAREN);
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.disconnect(device);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.getConnectedDevices();
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.getDevicesMatchingConnectionStates(states);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice device) {
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.getConnectionState(device);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return 0;
        } else {
            return 0;
        }
    }

    public boolean setPriority(BluetoothDevice device, int priority) {
        log("setPriority(" + device + ", " + priority + Separators.RPAREN);
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            if (priority != 0 && priority != 100) {
                return false;
            }
            try {
                return this.mService.setPriority(device, priority);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    public int getPriority(BluetoothDevice device) {
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.getPriority(device);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
                return 0;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return 0;
        } else {
            return 0;
        }
    }

    public boolean startVoiceRecognition(BluetoothDevice device) {
        log("startVoiceRecognition()");
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.startVoiceRecognition(device);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        }
        return false;
    }

    public boolean stopVoiceRecognition(BluetoothDevice device) {
        log("stopVoiceRecognition()");
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.stopVoiceRecognition(device);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        }
        return false;
    }

    public boolean isAudioConnected(BluetoothDevice device) {
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.isAudioConnected(device);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        }
        return false;
    }

    public int getBatteryUsageHint(BluetoothDevice device) {
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.getBatteryUsageHint(device);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return -1;
        }
        return -1;
    }

    public static boolean isBluetoothVoiceDialingEnabled(Context context) {
        return context.getResources().getBoolean(R.bool.config_bluetooth_sco_off_call);
    }

    public boolean acceptIncomingConnect(BluetoothDevice device) {
        log("acceptIncomingConnect");
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.acceptIncomingConnect(device);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean rejectIncomingConnect(BluetoothDevice device) {
        log("rejectIncomingConnect");
        if (this.mService != null) {
            try {
                return this.mService.rejectIncomingConnect(device);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public int getAudioState(BluetoothDevice device) {
        if (this.mService != null && !isDisabled()) {
            try {
                return this.mService.getAudioState(device);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return 10;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return 10;
    }

    public boolean isAudioOn() {
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.isAudioOn();
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        }
        return false;
    }

    public boolean connectAudio() {
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.connectAudio();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean disconnectAudio() {
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.disconnectAudio();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean startScoUsingVirtualVoiceCall(BluetoothDevice device) {
        log("startScoUsingVirtualVoiceCall()");
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.startScoUsingVirtualVoiceCall(device);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean stopScoUsingVirtualVoiceCall(BluetoothDevice device) {
        log("stopScoUsingVirtualVoiceCall()");
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.stopScoUsingVirtualVoiceCall(device);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
        return false;
    }

    public void phoneStateChanged(int numActive, int numHeld, int callState, String number, int type) {
        if (this.mService != null && isEnabled()) {
            try {
                this.mService.phoneStateChanged(numActive, numHeld, callState, number, type);
                return;
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
    }

    public void clccResponse(int index, int direction, int status, int mode, boolean mpty, String number, int type) {
        if (this.mService != null && isEnabled()) {
            try {
                this.mService.clccResponse(index, direction, status, mode, mpty, number, type);
                return;
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        Log.d(TAG, Log.getStackTraceString(new Throwable()));
    }

    public boolean sendVendorSpecificResultCode(BluetoothDevice device, String command, String arg) {
        log("sendVendorSpecificResultCode()");
        if (command == null) {
            throw new IllegalArgumentException("command is null");
        }
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.sendVendorSpecificResultCode(device, command, arg);
            } catch (RemoteException e) {
                Log.e(TAG, Log.getStackTraceString(new Throwable()));
            }
        }
        if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        }
        return false;
    }

    private boolean isEnabled() {
        return this.mAdapter.getState() == 12;
    }

    private boolean isDisabled() {
        return this.mAdapter.getState() == 10;
    }

    private boolean isValidDevice(BluetoothDevice device) {
        return device != null && BluetoothAdapter.checkBluetoothAddress(device.getAddress());
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}