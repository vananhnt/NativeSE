package android.bluetooth;

import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothA2dp;
import android.bluetooth.IBluetoothStateChangeCallback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.List;

/* loaded from: BluetoothA2dp.class */
public final class BluetoothA2dp implements BluetoothProfile {
    private static final String TAG = "BluetoothA2dp";
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_PLAYING_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED";
    public static final int STATE_PLAYING = 10;
    public static final int STATE_NOT_PLAYING = 11;
    private Context mContext;
    private BluetoothProfile.ServiceListener mServiceListener;
    private IBluetoothA2dp mService;
    private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub() { // from class: android.bluetooth.BluetoothA2dp.1
        @Override // android.bluetooth.IBluetoothStateChangeCallback
        public void onBluetoothStateChange(boolean up) {
            Log.d(BluetoothA2dp.TAG, "onBluetoothStateChange: up=" + up);
            if (!up) {
                synchronized (BluetoothA2dp.this.mConnection) {
                    try {
                        BluetoothA2dp.this.mService = null;
                        BluetoothA2dp.this.mContext.unbindService(BluetoothA2dp.this.mConnection);
                    } catch (Exception re) {
                        Log.e(BluetoothA2dp.TAG, "", re);
                    }
                }
                return;
            }
            synchronized (BluetoothA2dp.this.mConnection) {
                try {
                    if (BluetoothA2dp.this.mService == null) {
                        BluetoothA2dp.this.doBind();
                    }
                } catch (Exception re2) {
                    Log.e(BluetoothA2dp.TAG, "", re2);
                }
            }
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: android.bluetooth.BluetoothA2dp.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(BluetoothA2dp.TAG, "Proxy object connected");
            BluetoothA2dp.this.mService = IBluetoothA2dp.Stub.asInterface(service);
            if (BluetoothA2dp.this.mServiceListener != null) {
                BluetoothA2dp.this.mServiceListener.onServiceConnected(2, BluetoothA2dp.this);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            Log.d(BluetoothA2dp.TAG, "Proxy object disconnected");
            BluetoothA2dp.this.mService = null;
            if (BluetoothA2dp.this.mServiceListener != null) {
                BluetoothA2dp.this.mServiceListener.onServiceDisconnected(2);
            }
        }
    };
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

    /* JADX INFO: Access modifiers changed from: package-private */
    public BluetoothA2dp(Context context, BluetoothProfile.ServiceListener l) {
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
        Intent intent = new Intent(IBluetoothA2dp.class.getName());
        ComponentName comp = intent.resolveSystemService(this.mContext.getPackageManager(), 0);
        intent.setComponent(comp);
        if (comp == null || !this.mContext.bindService(intent, this.mConnection, 0)) {
            Log.e(TAG, "Could not bind to Bluetooth A2DP Service with " + intent);
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        this.mServiceListener = null;
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
    }

    public void finalize() {
        close();
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

    public boolean isAvrcpAbsoluteVolumeSupported() {
        Log.d(TAG, "isAvrcpAbsoluteVolumeSupported");
        if (this.mService != null && isEnabled()) {
            try {
                return this.mService.isAvrcpAbsoluteVolumeSupported();
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in isAvrcpAbsoluteVolumeSupported()", e);
                return false;
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
            return false;
        } else {
            return false;
        }
    }

    public void adjustAvrcpAbsoluteVolume(int direction) {
        Log.d(TAG, "adjustAvrcpAbsoluteVolume");
        if (this.mService != null && isEnabled()) {
            try {
                this.mService.adjustAvrcpAbsoluteVolume(direction);
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in adjustAvrcpAbsoluteVolume()", e);
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
    }

    public void setAvrcpAbsoluteVolume(int volume) {
        Log.d(TAG, "setAvrcpAbsoluteVolume");
        if (this.mService != null && isEnabled()) {
            try {
                this.mService.setAvrcpAbsoluteVolume(volume);
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to BT service in setAvrcpAbsoluteVolume()", e);
            }
        } else if (this.mService == null) {
            Log.w(TAG, "Proxy not attached to service");
        }
    }

    public boolean isA2dpPlaying(BluetoothDevice device) {
        if (this.mService != null && isEnabled() && isValidDevice(device)) {
            try {
                return this.mService.isA2dpPlaying(device);
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

    public boolean shouldSendVolumeKeys(BluetoothDevice device) {
        ParcelUuid[] uuids;
        if (isEnabled() && isValidDevice(device) && (uuids = device.getUuids()) != null) {
            for (ParcelUuid uuid : uuids) {
                if (BluetoothUuid.isAvrcpTarget(uuid)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static String stateToString(int state) {
        switch (state) {
            case 0:
                return "disconnected";
            case 1:
                return "connecting";
            case 2:
                return "connected";
            case 3:
                return "disconnecting";
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            default:
                return "<unknown state " + state + Separators.GREATER_THAN;
            case 10:
                return "playing";
            case 11:
                return "not playing";
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