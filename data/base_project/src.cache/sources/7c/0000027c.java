package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothMap;
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

/* loaded from: BluetoothMap.class */
public final class BluetoothMap implements BluetoothProfile {
    private static final String TAG = "BluetoothMap";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.map.profile.action.CONNECTION_STATE_CHANGED";
    private IBluetoothMap mService;
    private final Context mContext;
    private BluetoothProfile.ServiceListener mServiceListener;
    private BluetoothAdapter mAdapter;
    public static final int STATE_ERROR = -1;
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_CANCELED = 2;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothMap.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean up) {
            Log.d(BluetoothMap.TAG, "onBluetoothStateChange: up=" + up);
            if (!up) {
                synchronized (BluetoothMap.this.mConnection) {
                    try {
                        BluetoothMap.this.mService = null;
                        BluetoothMap.this.mContext.unbindService(BluetoothMap.this.mConnection);
                    } catch (Exception re) {
                        Log.e(BluetoothMap.TAG, "", re);
                    }
                }
                return;
            }
            synchronized (BluetoothMap.this.mConnection) {
                try {
                    if (BluetoothMap.this.mService == null) {
                        BluetoothMap.this.doBind();
                    }
                } catch (Exception re2) {
                    Log.e(BluetoothMap.TAG, "", re2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothMap.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothMap.log("Proxy object connected");
            BluetoothMap.this.mService = IBluetoothMap.Stub.asInterface(service);
            if (BluetoothMap.this.mServiceListener != null) {
                BluetoothMap.this.mServiceListener.onServiceConnected(9, BluetoothMap.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            BluetoothMap.log("Proxy object disconnected");
            BluetoothMap.this.mService = null;
            if (BluetoothMap.this.mServiceListener != null) {
                BluetoothMap.this.mServiceListener.onServiceDisconnected(9);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothMap(Context context, BluetoothProfile.ServiceListener l) {
        Log.d(TAG, "Create BluetoothMap proxy object");
        this.mContext = context;
        this.mServiceListener = l;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
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
        Intent intent = new Intent(IBluetoothMap.class.getName());
        ComponentName comp = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(comp);
        if (comp == null || !this.mContext.bindService(intent, this.mConnection, 0)) {
            Log.e(TAG, "Could not bind to Bluetooth MAP Service with " + intent);
            return false;
        }
        return true;
    }

    protected void finalize() throws Throwable {
        try {
            close();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    public synchronized void close() {
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

    public int getState() {
        if (this.mService != null) {
            try {
                return this.mService.getState();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return -1;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        log(Log.getStackTraceString(new Throwable()));
        return -1;
    }

    public BluetoothDevice getClient() {
        if (this.mService != null) {
            try {
                return this.mService.getClient();
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        log(Log.getStackTraceString(new Throwable()));
        return null;
    }

    public boolean isConnected(BluetoothDevice device) {
        if (this.mService != null) {
            try {
                return this.mService.isConnected(device);
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        log(Log.getStackTraceString(new Throwable()));
        return false;
    }

    public boolean connect(BluetoothDevice device) {
        log("connect(" + device + Separators.RPAREN + "not supported for MAPS");
        return false;
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

    public static boolean doesClassMatchSink(BluetoothClass btClass) {
        switch (btClass.getDeviceClass()) {
            case 256:
            case 260:
            case 264:
            case 268:
                return true;
            default:
                return false;
        }
    }

    @Override // android.bluetooth.BluetoothProfile
    public List<BluetoothDevice> getConnectedDevices() {
        log("getConnectedDevices()");
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
        log("getDevicesMatchingStates()");
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
        log("getConnectionState(" + device + Separators.RPAREN);
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

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String msg) {
        Log.d(TAG, msg);
    }

    private boolean isEnabled() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || adapter.getState() != 12) {
            log("Bluetooth is Not enabled");
            return false;
        }
        return true;
    }

    private boolean isValidDevice(BluetoothDevice device) {
        return device != null && BluetoothAdapter.checkBluetoothAddress(device.getAddress());
    }
}