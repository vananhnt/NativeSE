package android.hardware.input;

import android.content.Context;
import android.hardware.input.IInputDevicesChangedListener;
import android.hardware.input.IInputManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.InputDevice;
import android.view.InputEvent;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;

/* loaded from: InputManager.class */
public final class InputManager {
    private static final String TAG = "InputManager";
    private static final boolean DEBUG = false;
    private static final int MSG_DEVICE_ADDED = 1;
    private static final int MSG_DEVICE_REMOVED = 2;
    private static final int MSG_DEVICE_CHANGED = 3;
    private static InputManager sInstance;
    private final IInputManager mIm;
    private SparseArray<InputDevice> mInputDevices;
    private InputDevicesChangedListener mInputDevicesChangedListener;
    public static final String ACTION_QUERY_KEYBOARD_LAYOUTS = "android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS";
    public static final String META_DATA_KEYBOARD_LAYOUTS = "android.hardware.input.metadata.KEYBOARD_LAYOUTS";
    public static final int MIN_POINTER_SPEED = -7;
    public static final int MAX_POINTER_SPEED = 7;
    public static final int DEFAULT_POINTER_SPEED = 0;
    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;
    private final Object mInputDevicesLock = new Object();
    private final ArrayList<InputDeviceListenerDelegate> mInputDeviceListeners = new ArrayList<>();

    /* loaded from: InputManager$InputDeviceListener.class */
    public interface InputDeviceListener {
        void onInputDeviceAdded(int i);

        void onInputDeviceRemoved(int i);

        void onInputDeviceChanged(int i);
    }

    private InputManager(IInputManager im) {
        this.mIm = im;
    }

    public static InputManager getInstance() {
        InputManager inputManager;
        synchronized (InputManager.class) {
            if (sInstance == null) {
                IBinder b = ServiceManager.getService(Context.INPUT_SERVICE);
                sInstance = new InputManager(IInputManager.Stub.asInterface(b));
            }
            inputManager = sInstance;
        }
        return inputManager;
    }

    public InputDevice getInputDevice(int id) {
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            int index = this.mInputDevices.indexOfKey(id);
            if (index < 0) {
                return null;
            }
            InputDevice inputDevice = this.mInputDevices.valueAt(index);
            if (inputDevice == null) {
                try {
                    inputDevice = this.mIm.getInputDevice(id);
                    if (inputDevice != null) {
                        this.mInputDevices.setValueAt(index, inputDevice);
                    }
                } catch (RemoteException ex) {
                    throw new RuntimeException("Could not get input device information.", ex);
                }
            }
            return inputDevice;
        }
    }

    public InputDevice getInputDeviceByDescriptor(String descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException("descriptor must not be null.");
        }
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            int numDevices = this.mInputDevices.size();
            for (int i = 0; i < numDevices; i++) {
                InputDevice inputDevice = this.mInputDevices.valueAt(i);
                if (inputDevice == null) {
                    int id = this.mInputDevices.keyAt(i);
                    try {
                        inputDevice = this.mIm.getInputDevice(id);
                    } catch (RemoteException e) {
                    }
                    if (inputDevice != null) {
                        this.mInputDevices.setValueAt(i, inputDevice);
                    }
                }
                if (descriptor.equals(inputDevice.getDescriptor())) {
                    return inputDevice;
                }
            }
            return null;
        }
    }

    public int[] getInputDeviceIds() {
        int[] ids;
        synchronized (this.mInputDevicesLock) {
            populateInputDevicesLocked();
            int count = this.mInputDevices.size();
            ids = new int[count];
            for (int i = 0; i < count; i++) {
                ids[i] = this.mInputDevices.keyAt(i);
            }
        }
        return ids;
    }

    public void registerInputDeviceListener(InputDeviceListener listener, Handler handler) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mInputDevicesLock) {
            int index = findInputDeviceListenerLocked(listener);
            if (index < 0) {
                this.mInputDeviceListeners.add(new InputDeviceListenerDelegate(listener, handler));
            }
        }
    }

    public void unregisterInputDeviceListener(InputDeviceListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        synchronized (this.mInputDevicesLock) {
            int index = findInputDeviceListenerLocked(listener);
            if (index >= 0) {
                InputDeviceListenerDelegate d = this.mInputDeviceListeners.get(index);
                d.removeCallbacksAndMessages(null);
                this.mInputDeviceListeners.remove(index);
            }
        }
    }

    private int findInputDeviceListenerLocked(InputDeviceListener listener) {
        int numListeners = this.mInputDeviceListeners.size();
        for (int i = 0; i < numListeners; i++) {
            if (this.mInputDeviceListeners.get(i).mListener == listener) {
                return i;
            }
        }
        return -1;
    }

    public KeyboardLayout[] getKeyboardLayouts() {
        try {
            return this.mIm.getKeyboardLayouts();
        } catch (RemoteException ex) {
            Log.w(TAG, "Could not get list of keyboard layout informations.", ex);
            return new KeyboardLayout[0];
        }
    }

    public KeyboardLayout getKeyboardLayout(String keyboardLayoutDescriptor) {
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            return this.mIm.getKeyboardLayout(keyboardLayoutDescriptor);
        } catch (RemoteException ex) {
            Log.w(TAG, "Could not get keyboard layout information.", ex);
            return null;
        }
    }

    public String getCurrentKeyboardLayoutForInputDevice(String inputDeviceDescriptor) {
        if (inputDeviceDescriptor == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        try {
            return this.mIm.getCurrentKeyboardLayoutForInputDevice(inputDeviceDescriptor);
        } catch (RemoteException ex) {
            Log.w(TAG, "Could not get current keyboard layout for input device.", ex);
            return null;
        }
    }

    public void setCurrentKeyboardLayoutForInputDevice(String inputDeviceDescriptor, String keyboardLayoutDescriptor) {
        if (inputDeviceDescriptor == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.setCurrentKeyboardLayoutForInputDevice(inputDeviceDescriptor, keyboardLayoutDescriptor);
        } catch (RemoteException ex) {
            Log.w(TAG, "Could not set current keyboard layout for input device.", ex);
        }
    }

    public String[] getKeyboardLayoutsForInputDevice(String inputDeviceDescriptor) {
        if (inputDeviceDescriptor == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        try {
            return this.mIm.getKeyboardLayoutsForInputDevice(inputDeviceDescriptor);
        } catch (RemoteException ex) {
            Log.w(TAG, "Could not get keyboard layouts for input device.", ex);
            return (String[]) ArrayUtils.emptyArray(String.class);
        }
    }

    public void addKeyboardLayoutForInputDevice(String inputDeviceDescriptor, String keyboardLayoutDescriptor) {
        if (inputDeviceDescriptor == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.addKeyboardLayoutForInputDevice(inputDeviceDescriptor, keyboardLayoutDescriptor);
        } catch (RemoteException ex) {
            Log.w(TAG, "Could not add keyboard layout for input device.", ex);
        }
    }

    public void removeKeyboardLayoutForInputDevice(String inputDeviceDescriptor, String keyboardLayoutDescriptor) {
        if (inputDeviceDescriptor == null) {
            throw new IllegalArgumentException("inputDeviceDescriptor must not be null");
        }
        if (keyboardLayoutDescriptor == null) {
            throw new IllegalArgumentException("keyboardLayoutDescriptor must not be null");
        }
        try {
            this.mIm.removeKeyboardLayoutForInputDevice(inputDeviceDescriptor, keyboardLayoutDescriptor);
        } catch (RemoteException ex) {
            Log.w(TAG, "Could not remove keyboard layout for input device.", ex);
        }
    }

    public int getPointerSpeed(Context context) {
        int speed = 0;
        try {
            speed = Settings.System.getInt(context.getContentResolver(), Settings.System.POINTER_SPEED);
        } catch (Settings.SettingNotFoundException e) {
        }
        return speed;
    }

    public void setPointerSpeed(Context context, int speed) {
        if (speed < -7 || speed > 7) {
            throw new IllegalArgumentException("speed out of range");
        }
        Settings.System.putInt(context.getContentResolver(), Settings.System.POINTER_SPEED, speed);
    }

    public void tryPointerSpeed(int speed) {
        if (speed < -7 || speed > 7) {
            throw new IllegalArgumentException("speed out of range");
        }
        try {
            this.mIm.tryPointerSpeed(speed);
        } catch (RemoteException ex) {
            Log.w(TAG, "Could not set temporary pointer speed.", ex);
        }
    }

    public boolean[] deviceHasKeys(int[] keyCodes) {
        return deviceHasKeys(-1, keyCodes);
    }

    public boolean[] deviceHasKeys(int id, int[] keyCodes) {
        boolean[] ret = new boolean[keyCodes.length];
        try {
            this.mIm.hasKeys(id, -256, keyCodes, ret);
        } catch (RemoteException e) {
        }
        return ret;
    }

    public boolean injectInputEvent(InputEvent event, int mode) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        if (mode != 0 && mode != 2 && mode != 1) {
            throw new IllegalArgumentException("mode is invalid");
        }
        try {
            return this.mIm.injectInputEvent(event, mode);
        } catch (RemoteException e) {
            return false;
        }
    }

    private void populateInputDevicesLocked() {
        if (this.mInputDevicesChangedListener == null) {
            InputDevicesChangedListener listener = new InputDevicesChangedListener();
            try {
                this.mIm.registerInputDevicesChangedListener(listener);
                this.mInputDevicesChangedListener = listener;
            } catch (RemoteException ex) {
                throw new RuntimeException("Could not get register input device changed listener", ex);
            }
        }
        if (this.mInputDevices == null) {
            try {
                int[] ids = this.mIm.getInputDeviceIds();
                this.mInputDevices = new SparseArray<>();
                for (int i : ids) {
                    this.mInputDevices.put(i, null);
                }
            } catch (RemoteException ex2) {
                throw new RuntimeException("Could not get input device ids.", ex2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInputDevicesChanged(int[] deviceIdAndGeneration) {
        synchronized (this.mInputDevicesLock) {
            int i = this.mInputDevices.size();
            while (true) {
                i--;
                if (i <= 0) {
                    break;
                }
                int deviceId = this.mInputDevices.keyAt(i);
                if (!containsDeviceId(deviceIdAndGeneration, deviceId)) {
                    this.mInputDevices.removeAt(i);
                    sendMessageToInputDeviceListenersLocked(2, deviceId);
                }
            }
            for (int i2 = 0; i2 < deviceIdAndGeneration.length; i2 += 2) {
                int deviceId2 = deviceIdAndGeneration[i2];
                int index = this.mInputDevices.indexOfKey(deviceId2);
                if (index >= 0) {
                    InputDevice device = this.mInputDevices.valueAt(index);
                    if (device != null) {
                        int generation = deviceIdAndGeneration[i2 + 1];
                        if (device.getGeneration() != generation) {
                            this.mInputDevices.setValueAt(index, null);
                            sendMessageToInputDeviceListenersLocked(3, deviceId2);
                        }
                    }
                } else {
                    this.mInputDevices.put(deviceId2, null);
                    sendMessageToInputDeviceListenersLocked(1, deviceId2);
                }
            }
        }
    }

    private void sendMessageToInputDeviceListenersLocked(int what, int deviceId) {
        int numListeners = this.mInputDeviceListeners.size();
        for (int i = 0; i < numListeners; i++) {
            InputDeviceListenerDelegate listener = this.mInputDeviceListeners.get(i);
            listener.sendMessage(listener.obtainMessage(what, deviceId, 0));
        }
    }

    private static boolean containsDeviceId(int[] deviceIdAndGeneration, int deviceId) {
        for (int i = 0; i < deviceIdAndGeneration.length; i += 2) {
            if (deviceIdAndGeneration[i] == deviceId) {
                return true;
            }
        }
        return false;
    }

    public Vibrator getInputDeviceVibrator(int deviceId) {
        return new InputDeviceVibrator(deviceId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputManager$InputDevicesChangedListener.class */
    public final class InputDevicesChangedListener extends IInputDevicesChangedListener.Stub {
        private InputDevicesChangedListener() {
        }

        @Override // android.hardware.input.IInputDevicesChangedListener
        public void onInputDevicesChanged(int[] deviceIdAndGeneration) throws RemoteException {
            InputManager.this.onInputDevicesChanged(deviceIdAndGeneration);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: InputManager$InputDeviceListenerDelegate.class */
    public static final class InputDeviceListenerDelegate extends Handler {
        public final InputDeviceListener mListener;

        public InputDeviceListenerDelegate(InputDeviceListener listener, Handler handler) {
            super(handler != null ? handler.getLooper() : Looper.myLooper());
            this.mListener = listener;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    this.mListener.onInputDeviceAdded(msg.arg1);
                    return;
                case 2:
                    this.mListener.onInputDeviceRemoved(msg.arg1);
                    return;
                case 3:
                    this.mListener.onInputDeviceChanged(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: InputManager$InputDeviceVibrator.class */
    private final class InputDeviceVibrator extends Vibrator {
        private final int mDeviceId;
        private final Binder mToken = new Binder();

        public InputDeviceVibrator(int deviceId) {
            this.mDeviceId = deviceId;
        }

        @Override // android.os.Vibrator
        public boolean hasVibrator() {
            return true;
        }

        @Override // android.os.Vibrator
        public void vibrate(long milliseconds) {
            vibrate(new long[]{0, milliseconds}, -1);
        }

        @Override // android.os.Vibrator
        public void vibrate(long[] pattern, int repeat) {
            if (repeat < pattern.length) {
                try {
                    InputManager.this.mIm.vibrate(this.mDeviceId, pattern, repeat, this.mToken);
                    return;
                } catch (RemoteException ex) {
                    Log.w(InputManager.TAG, "Failed to vibrate.", ex);
                    return;
                }
            }
            throw new ArrayIndexOutOfBoundsException();
        }

        @Override // android.os.Vibrator
        public void vibrate(int owningUid, String owningPackage, long milliseconds) {
            vibrate(milliseconds);
        }

        @Override // android.os.Vibrator
        public void vibrate(int owningUid, String owningPackage, long[] pattern, int repeat) {
            vibrate(pattern, repeat);
        }

        @Override // android.os.Vibrator
        public void cancel() {
            try {
                InputManager.this.mIm.cancelVibrate(this.mDeviceId, this.mToken);
            } catch (RemoteException ex) {
                Log.w(InputManager.TAG, "Failed to cancel vibration.", ex);
            }
        }
    }
}