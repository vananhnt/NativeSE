package android.hardware.input;

import android.hardware.input.IInputDevicesChangedListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.InputDevice;
import android.view.InputEvent;

/* loaded from: IInputManager.class */
public interface IInputManager extends IInterface {
    InputDevice getInputDevice(int i) throws RemoteException;

    int[] getInputDeviceIds() throws RemoteException;

    boolean hasKeys(int i, int i2, int[] iArr, boolean[] zArr) throws RemoteException;

    void tryPointerSpeed(int i) throws RemoteException;

    boolean injectInputEvent(InputEvent inputEvent, int i) throws RemoteException;

    KeyboardLayout[] getKeyboardLayouts() throws RemoteException;

    KeyboardLayout getKeyboardLayout(String str) throws RemoteException;

    String getCurrentKeyboardLayoutForInputDevice(String str) throws RemoteException;

    void setCurrentKeyboardLayoutForInputDevice(String str, String str2) throws RemoteException;

    String[] getKeyboardLayoutsForInputDevice(String str) throws RemoteException;

    void addKeyboardLayoutForInputDevice(String str, String str2) throws RemoteException;

    void removeKeyboardLayoutForInputDevice(String str, String str2) throws RemoteException;

    void registerInputDevicesChangedListener(IInputDevicesChangedListener iInputDevicesChangedListener) throws RemoteException;

    void vibrate(int i, long[] jArr, int i2, IBinder iBinder) throws RemoteException;

    void cancelVibrate(int i, IBinder iBinder) throws RemoteException;

    /* loaded from: IInputManager$Stub.class */
    public static abstract class Stub extends Binder implements IInputManager {
        private static final String DESCRIPTOR = "android.hardware.input.IInputManager";
        static final int TRANSACTION_getInputDevice = 1;
        static final int TRANSACTION_getInputDeviceIds = 2;
        static final int TRANSACTION_hasKeys = 3;
        static final int TRANSACTION_tryPointerSpeed = 4;
        static final int TRANSACTION_injectInputEvent = 5;
        static final int TRANSACTION_getKeyboardLayouts = 6;
        static final int TRANSACTION_getKeyboardLayout = 7;
        static final int TRANSACTION_getCurrentKeyboardLayoutForInputDevice = 8;
        static final int TRANSACTION_setCurrentKeyboardLayoutForInputDevice = 9;
        static final int TRANSACTION_getKeyboardLayoutsForInputDevice = 10;
        static final int TRANSACTION_addKeyboardLayoutForInputDevice = 11;
        static final int TRANSACTION_removeKeyboardLayoutForInputDevice = 12;
        static final int TRANSACTION_registerInputDevicesChangedListener = 13;
        static final int TRANSACTION_vibrate = 14;
        static final int TRANSACTION_cancelVibrate = 15;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInputManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IInputManager)) {
                return (IInputManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            InputEvent _arg0;
            boolean[] _arg3;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    InputDevice _result = getInputDevice(_arg02);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _result2 = getInputDeviceIds();
                    reply.writeNoException();
                    reply.writeIntArray(_result2);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    int _arg1 = data.readInt();
                    int[] _arg2 = data.createIntArray();
                    int _arg3_length = data.readInt();
                    if (_arg3_length < 0) {
                        _arg3 = null;
                    } else {
                        _arg3 = new boolean[_arg3_length];
                    }
                    boolean _result3 = hasKeys(_arg03, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    reply.writeBooleanArray(_arg3);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    tryPointerSpeed(_arg04);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = InputEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg12 = data.readInt();
                    boolean _result4 = injectInputEvent(_arg0, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    KeyboardLayout[] _result5 = getKeyboardLayouts();
                    reply.writeNoException();
                    reply.writeTypedArray(_result5, 1);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    KeyboardLayout _result6 = getKeyboardLayout(_arg05);
                    reply.writeNoException();
                    if (_result6 != null) {
                        reply.writeInt(1);
                        _result6.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    String _result7 = getCurrentKeyboardLayoutForInputDevice(_arg06);
                    reply.writeNoException();
                    reply.writeString(_result7);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    String _arg13 = data.readString();
                    setCurrentKeyboardLayoutForInputDevice(_arg07, _arg13);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    String[] _result8 = getKeyboardLayoutsForInputDevice(_arg08);
                    reply.writeNoException();
                    reply.writeStringArray(_result8);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg09 = data.readString();
                    String _arg14 = data.readString();
                    addKeyboardLayoutForInputDevice(_arg09, _arg14);
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    String _arg15 = data.readString();
                    removeKeyboardLayoutForInputDevice(_arg010, _arg15);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    IInputDevicesChangedListener _arg011 = IInputDevicesChangedListener.Stub.asInterface(data.readStrongBinder());
                    registerInputDevicesChangedListener(_arg011);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    long[] _arg16 = data.createLongArray();
                    int _arg22 = data.readInt();
                    IBinder _arg32 = data.readStrongBinder();
                    vibrate(_arg012, _arg16, _arg22, _arg32);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    IBinder _arg17 = data.readStrongBinder();
                    cancelVibrate(_arg013, _arg17);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IInputManager$Stub$Proxy.class */
        public static class Proxy implements IInputManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.hardware.input.IInputManager
            public InputDevice getInputDevice(int deviceId) throws RemoteException {
                InputDevice _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = InputDevice.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public int[] getInputDeviceIds() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean hasKeys(int deviceId, int sourceMask, int[] keyCodes, boolean[] keyExists) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(sourceMask);
                    _data.writeIntArray(keyCodes);
                    if (keyExists == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(keyExists.length);
                    }
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    _reply.readBooleanArray(keyExists);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public void tryPointerSpeed(int speed) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(speed);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public boolean injectInputEvent(InputEvent ev, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (ev != null) {
                        _data.writeInt(1);
                        ev.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(mode);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public KeyboardLayout[] getKeyboardLayouts() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    KeyboardLayout[] _result = (KeyboardLayout[]) _reply.createTypedArray(KeyboardLayout.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public KeyboardLayout getKeyboardLayout(String keyboardLayoutDescriptor) throws RemoteException {
                KeyboardLayout _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(keyboardLayoutDescriptor);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = KeyboardLayout.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.input.IInputManager
            public String getCurrentKeyboardLayoutForInputDevice(String inputDeviceDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputDeviceDescriptor);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public void setCurrentKeyboardLayoutForInputDevice(String inputDeviceDescriptor, String keyboardLayoutDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputDeviceDescriptor);
                    _data.writeString(keyboardLayoutDescriptor);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public String[] getKeyboardLayoutsForInputDevice(String inputDeviceDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputDeviceDescriptor);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public void addKeyboardLayoutForInputDevice(String inputDeviceDescriptor, String keyboardLayoutDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputDeviceDescriptor);
                    _data.writeString(keyboardLayoutDescriptor);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public void removeKeyboardLayoutForInputDevice(String inputDeviceDescriptor, String keyboardLayoutDescriptor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(inputDeviceDescriptor);
                    _data.writeString(keyboardLayoutDescriptor);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public void registerInputDevicesChangedListener(IInputDevicesChangedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public void vibrate(int deviceId, long[] pattern, int repeat, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeLongArray(pattern);
                    _data.writeInt(repeat);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.input.IInputManager
            public void cancelVibrate(int deviceId, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}