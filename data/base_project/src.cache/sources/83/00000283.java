package android.bluetooth;

import android.bluetooth.IBluetoothPbap;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/* loaded from: BluetoothPbap.class */
public class BluetoothPbap {
    private static final String TAG = "BluetoothPbap";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    public static final String PBAP_STATE = "android.bluetooth.pbap.intent.PBAP_STATE";
    public static final String PBAP_PREVIOUS_STATE = "android.bluetooth.pbap.intent.PBAP_PREVIOUS_STATE";
    public static final String PBAP_STATE_CHANGED_ACTION = "android.bluetooth.pbap.intent.action.PBAP_STATE_CHANGED";
    private IBluetoothPbap mService;
    private final Context mContext;
    private ServiceListener mServiceListener;
    public static final int STATE_ERROR = -1;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_CANCELED = 2;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothPbap.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean up) {
            Log.d(BluetoothPbap.TAG, "onBluetoothStateChange: up=" + up);
            if (!up) {
                synchronized (BluetoothPbap.this.mConnection) {
                    try {
                        BluetoothPbap.this.mService = null;
                        BluetoothPbap.this.mContext.unbindService(BluetoothPbap.this.mConnection);
                    } catch (Exception re) {
                        Log.e(BluetoothPbap.TAG, "", re);
                    }
                }
                return;
            }
            synchronized (BluetoothPbap.this.mConnection) {
                try {
                    if (BluetoothPbap.this.mService == null) {
                        BluetoothPbap.this.doBind();
                    }
                } catch (Exception re2) {
                    Log.e(BluetoothPbap.TAG, "", re2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothPbap.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothPbap.log("Proxy object connected");
            BluetoothPbap.this.mService = IBluetoothPbap.Stub.asInterface(service);
            if (BluetoothPbap.this.mServiceListener != null) {
                BluetoothPbap.this.mServiceListener.onServiceConnected(BluetoothPbap.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            BluetoothPbap.log("Proxy object disconnected");
            BluetoothPbap.this.mService = null;
            if (BluetoothPbap.this.mServiceListener != null) {
                BluetoothPbap.this.mServiceListener.onServiceDisconnected();
            }
        }
    };
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

    /* loaded from: BluetoothPbap$ServiceListener.class */
    public interface ServiceListener {
        void onServiceConnected(BluetoothPbap bluetoothPbap);

        void onServiceDisconnected();
    }

    public BluetoothPbap(Context context, ServiceListener l) {
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
        Intent intent = new Intent(IBluetoothPbap.class.getName());
        ComponentName comp = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(comp);
        if (comp == null || !this.mContext.bindService(intent, this.mConnection, 0)) {
            Log.e(TAG, "Could not bind to Bluetooth Pbap Service with " + intent);
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

    public boolean disconnect() {
        log("disconnect()");
        if (this.mService != null) {
            try {
                this.mService.disconnect();
                return true;
            } catch (RemoteException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        Log.w(TAG, "Proxy not attached to service");
        log(Log.getStackTraceString(new Throwable()));
        return false;
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

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String msg) {
        Log.d(TAG, msg);
    }
}