package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothInputDevice;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.List;

/* loaded from: BluetoothInputDevice.class */
public final class BluetoothInputDevice implements BluetoothProfile {
    private static final String TAG = "BluetoothInputDevice";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_PROTOCOL_MODE_CHANGED = "android.bluetooth.input.profile.action.PROTOCOL_MODE_CHANGED";
    public static final String ACTION_VIRTUAL_UNPLUG_STATUS = "android.bluetooth.input.profile.action.VIRTUAL_UNPLUG_STATUS";
    public static final int INPUT_DISCONNECT_FAILED_NOT_CONNECTED = 5000;
    public static final int INPUT_CONNECT_FAILED_ALREADY_CONNECTED = 5001;
    public static final int INPUT_CONNECT_FAILED_ATTEMPT_FAILED = 5002;
    public static final int INPUT_OPERATION_GENERIC_FAILURE = 5003;
    public static final int INPUT_OPERATION_SUCCESS = 5004;
    public static final int PROTOCOL_REPORT_MODE = 0;
    public static final int PROTOCOL_BOOT_MODE = 1;
    public static final int PROTOCOL_UNSUPPORTED_MODE = 255;
    public static final byte REPORT_TYPE_INPUT = 0;
    public static final byte REPORT_TYPE_OUTPUT = 1;
    public static final byte REPORT_TYPE_FEATURE = 2;
    public static final int VIRTUAL_UNPLUG_STATUS_SUCCESS = 0;
    public static final int VIRTUAL_UNPLUG_STATUS_FAIL = 1;
    public static final String EXTRA_PROTOCOL_MODE = "android.bluetooth.BluetoothInputDevice.extra.PROTOCOL_MODE";
    public static final String EXTRA_REPORT_TYPE = "android.bluetooth.BluetoothInputDevice.extra.REPORT_TYPE";
    public static final String EXTRA_REPORT_ID = "android.bluetooth.BluetoothInputDevice.extra.REPORT_ID";
    public static final String EXTRA_REPORT_BUFFER_SIZE = "android.bluetooth.BluetoothInputDevice.extra.REPORT_BUFFER_SIZE";
    public static final String EXTRA_REPORT = "android.bluetooth.BluetoothInputDevice.extra.REPORT";
    public static final String EXTRA_VIRTUAL_UNPLUG_STATUS = "android.bluetooth.BluetoothInputDevice.extra.VIRTUAL_UNPLUG_STATUS";
    private Context mContext;
    private BluetoothProfile.ServiceListener mServiceListener;
    private IBluetoothInputDevice mService;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothInputDevice.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean up) {
            Log.d(BluetoothInputDevice.TAG, "onBluetoothStateChange: up=" + up);
            if (!up) {
                synchronized (BluetoothInputDevice.this.mConnection) {
                    try {
                        BluetoothInputDevice.this.mService = null;
                        BluetoothInputDevice.this.mContext.unbindService(BluetoothInputDevice.this.mConnection);
                    } catch (Exception re) {
                        Log.e(BluetoothInputDevice.TAG, "", re);
                    }
                }
                return;
            }
            synchronized (BluetoothInputDevice.this.mConnection) {
                try {
                    if (BluetoothInputDevice.this.mService == null) {
                        BluetoothInputDevice.this.doBind();
                    }
                } catch (Exception re2) {
                    Log.e(BluetoothInputDevice.TAG, "", re2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothInputDevice.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(BluetoothInputDevice.TAG, "Proxy object connected");
            BluetoothInputDevice.this.mService = IBluetoothInputDevice.Stub.asInterface(service);
            if (BluetoothInputDevice.this.mServiceListener != null) {
                BluetoothInputDevice.this.mServiceListener.onServiceConnected(4, BluetoothInputDevice.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            Log.d(BluetoothInputDevice.TAG, "Proxy object disconnected");
            BluetoothInputDevice.this.mService = null;
            if (BluetoothInputDevice.this.mServiceListener != null) {
                BluetoothInputDevice.this.mServiceListener.onServiceDisconnected(4);
            }
        }
    };
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothInputDevice(Context context, BluetoothProfile.ServiceListener l) {
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
        Intent intent = new Intent(IBluetoothInputDevice.class.getName());
        ComponentName comp = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(comp);
        if (comp == null || !this.mContext.bindService(intent, this.mConnection, 0)) {
            Log.e(TAG, "Could not bind to Bluetooth HID Service with " + intent);
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
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
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
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
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
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
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
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
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
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
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
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
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
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return 0;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return 0;
        } else {
            return 0;
        }
    }

    private boolean isEnabled() {
        return this.mAdapter.getState() == 12;
    }

    private boolean isValidDevice(BluetoothDevice device) {
        return device != null && BluetoothAdapter.checkBluetoothAddress(device.getAddress());
    }

    public boolean virtualUnplug(BluetoothDevice device) {
        log("virtualUnplug(" + device + Separators.RPAREN);
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.virtualUnplug(device);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    public boolean getProtocolMode(BluetoothDevice device) {
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.getProtocolMode(device);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    public boolean setProtocolMode(BluetoothDevice device, int protocolMode) {
        log("setProtocolMode(" + device + Separators.RPAREN);
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.setProtocolMode(device, protocolMode);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    public boolean getReport(BluetoothDevice device, byte reportType, byte reportId, int bufferSize) {
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.getReport(device, reportType, reportId, bufferSize);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    public boolean setReport(BluetoothDevice device, byte reportType, String report) {
        log("setReport(" + device + "), reportType=" + ((int) reportType) + " report=" + report);
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.setReport(device, reportType, report);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    public boolean sendData(BluetoothDevice device, String report) {
        log("sendData(" + device + "), report=" + report);
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.sendData(device, report);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}