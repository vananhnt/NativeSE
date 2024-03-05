package android.hardware.location;

import android.hardware.location.IGeofenceHardwareCallback;
import android.hardware.location.IGeofenceHardwareMonitorCallback;
import android.location.IFusedGeofenceHardware;
import android.location.IGpsGeofenceHardware;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IGeofenceHardware.class */
public interface IGeofenceHardware extends IInterface {
    void setGpsGeofenceHardware(IGpsGeofenceHardware iGpsGeofenceHardware) throws RemoteException;

    void setFusedGeofenceHardware(IFusedGeofenceHardware iFusedGeofenceHardware) throws RemoteException;

    int[] getMonitoringTypes() throws RemoteException;

    int getStatusOfMonitoringType(int i) throws RemoteException;

    boolean addCircularFence(int i, int i2, double d, double d2, double d3, int i3, int i4, int i5, int i6, IGeofenceHardwareCallback iGeofenceHardwareCallback) throws RemoteException;

    boolean removeGeofence(int i, int i2) throws RemoteException;

    boolean pauseGeofence(int i, int i2) throws RemoteException;

    boolean resumeGeofence(int i, int i2, int i3) throws RemoteException;

    boolean registerForMonitorStateChangeCallback(int i, IGeofenceHardwareMonitorCallback iGeofenceHardwareMonitorCallback) throws RemoteException;

    boolean unregisterForMonitorStateChangeCallback(int i, IGeofenceHardwareMonitorCallback iGeofenceHardwareMonitorCallback) throws RemoteException;

    /* loaded from: IGeofenceHardware$Stub.class */
    public static abstract class Stub extends Binder implements IGeofenceHardware {
        private static final String DESCRIPTOR = "android.hardware.location.IGeofenceHardware";
        static final int TRANSACTION_setGpsGeofenceHardware = 1;
        static final int TRANSACTION_setFusedGeofenceHardware = 2;
        static final int TRANSACTION_getMonitoringTypes = 3;
        static final int TRANSACTION_getStatusOfMonitoringType = 4;
        static final int TRANSACTION_addCircularFence = 5;
        static final int TRANSACTION_removeGeofence = 6;
        static final int TRANSACTION_pauseGeofence = 7;
        static final int TRANSACTION_resumeGeofence = 8;
        static final int TRANSACTION_registerForMonitorStateChangeCallback = 9;
        static final int TRANSACTION_unregisterForMonitorStateChangeCallback = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IGeofenceHardware asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGeofenceHardware)) {
                return (IGeofenceHardware) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IGpsGeofenceHardware _arg0 = IGpsGeofenceHardware.Stub.asInterface(data.readStrongBinder());
                    setGpsGeofenceHardware(_arg0);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IFusedGeofenceHardware _arg02 = IFusedGeofenceHardware.Stub.asInterface(data.readStrongBinder());
                    setFusedGeofenceHardware(_arg02);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _result = getMonitoringTypes();
                    reply.writeNoException();
                    reply.writeIntArray(_result);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    int _result2 = getStatusOfMonitoringType(_arg03);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg1 = data.readInt();
                    double _arg2 = data.readDouble();
                    double _arg3 = data.readDouble();
                    double _arg4 = data.readDouble();
                    int _arg5 = data.readInt();
                    int _arg6 = data.readInt();
                    int _arg7 = data.readInt();
                    int _arg8 = data.readInt();
                    IGeofenceHardwareCallback _arg9 = IGeofenceHardwareCallback.Stub.asInterface(data.readStrongBinder());
                    boolean _result3 = addCircularFence(_arg04, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9);
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    int _arg12 = data.readInt();
                    boolean _result4 = removeGeofence(_arg05, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? 1 : 0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    int _arg13 = data.readInt();
                    boolean _result5 = pauseGeofence(_arg06, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result5 ? 1 : 0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg14 = data.readInt();
                    int _arg22 = data.readInt();
                    boolean _result6 = resumeGeofence(_arg07, _arg14, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result6 ? 1 : 0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    IGeofenceHardwareMonitorCallback _arg15 = IGeofenceHardwareMonitorCallback.Stub.asInterface(data.readStrongBinder());
                    boolean _result7 = registerForMonitorStateChangeCallback(_arg08, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result7 ? 1 : 0);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    IGeofenceHardwareMonitorCallback _arg16 = IGeofenceHardwareMonitorCallback.Stub.asInterface(data.readStrongBinder());
                    boolean _result8 = unregisterForMonitorStateChangeCallback(_arg09, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result8 ? 1 : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IGeofenceHardware$Stub$Proxy.class */
        private static class Proxy implements IGeofenceHardware {
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

            @Override // android.hardware.location.IGeofenceHardware
            public void setGpsGeofenceHardware(IGpsGeofenceHardware service) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(service != null ? service.asBinder() : null);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public void setFusedGeofenceHardware(IFusedGeofenceHardware service) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(service != null ? service.asBinder() : null);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public int[] getMonitoringTypes() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
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

            @Override // android.hardware.location.IGeofenceHardware
            public int getStatusOfMonitoringType(int monitoringType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean addCircularFence(int id, int monitoringType, double lat, double longitude, double radius, int lastTransition, int monitorTransitions, int notificationResponsiveness, int unknownTimer, IGeofenceHardwareCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    _data.writeDouble(lat);
                    _data.writeDouble(longitude);
                    _data.writeDouble(radius);
                    _data.writeInt(lastTransition);
                    _data.writeInt(monitorTransitions);
                    _data.writeInt(notificationResponsiveness);
                    _data.writeInt(unknownTimer);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean removeGeofence(int id, int monitoringType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean pauseGeofence(int id, int monitoringType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean resumeGeofence(int id, int monitoringType, int monitorTransitions) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    _data.writeInt(monitorTransitions);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean registerForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IGeofenceHardware
            public boolean unregisterForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}