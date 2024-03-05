package android.service.dreams;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: IDreamManager.class */
public interface IDreamManager extends IInterface {
    void dream() throws RemoteException;

    void awaken() throws RemoteException;

    void setDreamComponents(ComponentName[] componentNameArr) throws RemoteException;

    ComponentName[] getDreamComponents() throws RemoteException;

    ComponentName getDefaultDreamComponent() throws RemoteException;

    void testDream(ComponentName componentName) throws RemoteException;

    boolean isDreaming() throws RemoteException;

    void finishSelf(IBinder iBinder) throws RemoteException;

    /* loaded from: IDreamManager$Stub.class */
    public static abstract class Stub extends Binder implements IDreamManager {
        private static final String DESCRIPTOR = "android.service.dreams.IDreamManager";
        static final int TRANSACTION_dream = 1;
        static final int TRANSACTION_awaken = 2;
        static final int TRANSACTION_setDreamComponents = 3;
        static final int TRANSACTION_getDreamComponents = 4;
        static final int TRANSACTION_getDefaultDreamComponent = 5;
        static final int TRANSACTION_testDream = 6;
        static final int TRANSACTION_isDreaming = 7;
        static final int TRANSACTION_finishSelf = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDreamManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IDreamManager)) {
                return (IDreamManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            ComponentName _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    dream();
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    awaken();
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    ComponentName[] _arg02 = (ComponentName[]) data.createTypedArray(ComponentName.CREATOR);
                    setDreamComponents(_arg02);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    ComponentName[] _result = getDreamComponents();
                    reply.writeNoException();
                    reply.writeTypedArray(_result, 1);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    ComponentName _result2 = getDefaultDreamComponent();
                    reply.writeNoException();
                    if (_result2 != null) {
                        reply.writeInt(1);
                        _result2.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    testDream(_arg0);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result3 = isDreaming();
                    reply.writeNoException();
                    reply.writeInt(_result3 ? 1 : 0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg03 = data.readStrongBinder();
                    finishSelf(_arg03);
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IDreamManager$Stub$Proxy.class */
        private static class Proxy implements IDreamManager {
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

            @Override // android.service.dreams.IDreamManager
            public void dream() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.service.dreams.IDreamManager
            public void awaken() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
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

            @Override // android.service.dreams.IDreamManager
            public void setDreamComponents(ComponentName[] componentNames) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(componentNames, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.service.dreams.IDreamManager
            public ComponentName[] getDreamComponents() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    ComponentName[] _result = (ComponentName[]) _reply.createTypedArray(ComponentName.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.service.dreams.IDreamManager
            public ComponentName getDefaultDreamComponent() throws RemoteException {
                ComponentName _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = ComponentName.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.dreams.IDreamManager
            public void testDream(ComponentName componentName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        _data.writeInt(1);
                        componentName.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.service.dreams.IDreamManager
            public boolean isDreaming() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.dreams.IDreamManager
            public void finishSelf(IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(8, _data, _reply, 0);
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