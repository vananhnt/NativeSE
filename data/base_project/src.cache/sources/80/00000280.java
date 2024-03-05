package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothPan;
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

/* loaded from: BluetoothPan.class */
public final class BluetoothPan implements BluetoothProfile {
    private static final String TAG = "BluetoothPan";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED";
    public static final String EXTRA_LOCAL_ROLE = "android.bluetooth.pan.extra.LOCAL_ROLE";
    public static final int PAN_ROLE_NONE = 0;
    public static final int LOCAL_NAP_ROLE = 1;
    public static final int REMOTE_NAP_ROLE = 1;
    public static final int LOCAL_PANU_ROLE = 2;
    public static final int REMOTE_PANU_ROLE = 2;
    public static final int PAN_DISCONNECT_FAILED_NOT_CONNECTED = 1000;
    public static final int PAN_CONNECT_FAILED_ALREADY_CONNECTED = 1001;
    public static final int PAN_CONNECT_FAILED_ATTEMPT_FAILED = 1002;
    public static final int PAN_OPERATION_GENERIC_FAILURE = 1003;
    public static final int PAN_OPERATION_SUCCESS = 1004;
    private Context mContext;
    private BluetoothProfile.ServiceListener mServiceListener;
    private IBluetoothPan mPanService;
    private final IBluetoothStateChangeCallback mStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothPan.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean on) throws RemoteException {
            if (!on) {
                synchronized (BluetoothPan.this.mConnection) {
                    try {
                        BluetoothPan.this.mPanService = null;
                        BluetoothPan.this.mContext.unbindService(BluetoothPan.this.mConnection);
                    } catch (Exception re) {
                        Log.e(BluetoothPan.TAG, "", re);
                    }
                }
                return;
            }
            Log.d(BluetoothPan.TAG, "onBluetoothStateChange(on) call bindService");
            BluetoothPan.this.doBind();
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothPan.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(BluetoothPan.TAG, "BluetoothPAN Proxy object connected");
            BluetoothPan.this.mPanService = IBluetoothPan.Stub.asInterface(service);
            if (BluetoothPan.this.mServiceListener != null) {
                BluetoothPan.this.mServiceListener.onServiceConnected(5, BluetoothPan.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            Log.d(BluetoothPan.TAG, "BluetoothPAN Proxy object disconnected");
            BluetoothPan.this.mPanService = null;
            if (BluetoothPan.this.mServiceListener != null) {
                BluetoothPan.this.mServiceListener.onServiceDisconnected(5);
            }
        }
    };
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothPan(Context context, BluetoothProfile.ServiceListener l) {
        this.mContext = context;
        this.mServiceListener = l;
        try {
            this.mAdapter.getBluetoothManager().registerStateChangeCallback(this.mStateChangeCallback);
        } catch (RemoteException re) {
            Log.w(TAG, "Unable to register BluetoothStateChangeCallback", re);
        }
        doBind();
    }

    boolean doBind() {
        Intent intent = new Intent(IBluetoothPan.class.getName());
        ComponentName comp = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(comp);
        if (comp == null || !this.mContext.bindService(intent, this.mConnection, 0)) {
            Log.e(TAG, "Could not bind to Bluetooth Pan Service with " + intent);
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        IBluetoothManager mgr = this.mAdapter.getBluetoothManager();
        if (mgr != null) {
            try {
                mgr.unregisterStateChangeCallback(this.mStateChangeCallback);
            } catch (RemoteException re) {
                Log.w(TAG, "Unable to unregister BluetoothStateChangeCallback", re);
            }
        }
        synchronized (this.mConnection) {
            if (this.mPanService != null) {
                try {
                    this.mPanService = null;
                    this.mContext.unbindService(this.mConnection);
                } catch (Exception re2) {
                    Log.e(TAG, "", re2);
                }
            }
        }
        this.mServiceListener = null;
    }

    protected void finalize() {
        close();
    }

    public boolean connect(BluetoothDevice device) {
        log("connect(" + device + Separators.RPAREN);
        if (this.mPanService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mPanService.connect(device);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mPanService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    public boolean disconnect(BluetoothDevice device) {
        log("disconnect(" + device + Separators.RPAREN);
        if (this.mPanService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mPanService.disconnect(device);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return false;
            }
        } else if (this.mPanService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        if (this.mPanService != null && isEnabled()) {
            try {
                return this.mPanService.getConnectedDevices();
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (this.mPanService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        if (this.mPanService != null && isEnabled()) {
            try {
                return this.mPanService.getDevicesMatchingConnectionStates(states);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return new ArrayList();
            }
        }
        if (this.mPanService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
        return new ArrayList();
    }

    @Override // android.bluetooth.BluetoothProfile
    public int getConnectionState(BluetoothDevice device) {
        if (this.mPanService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mPanService.getConnectionState(device);
            } catch (RemoteException e) {
                Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
                return 0;
            }
        } else if (this.mPanService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return 0;
        } else {
            return 0;
        }
    }

    public void setBluetoothTethering(boolean value) {
        log("setBluetoothTethering(" + value + Separators.RPAREN);
        try {
            this.mPanService.setBluetoothTethering(value);
        } catch (RemoteException e) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
        }
    }

    public boolean isTetheringOn() {
        try {
            return this.mPanService.isTetheringOn();
        } catch (RemoteException e) {
            Log.e(TAG, "Stack:" + Log.getStackTraceString(new Throwable()));
            return false;
        }
    }

    private boolean isEnabled() {
        return this.mAdapter.getState() == 12;
    }

    private boolean isValidDevice(BluetoothDevice device) {
        return device != null && BluetoothAdapter.checkBluetoothAddress(device.getAddress());
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}