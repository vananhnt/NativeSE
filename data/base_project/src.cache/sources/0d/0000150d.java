package android.view;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

/* loaded from: IWindow.class */
public interface IWindow extends IInterface {
    void executeCommand(String str, String str2, ParcelFileDescriptor parcelFileDescriptor) throws RemoteException;

    void resized(Rect rect, Rect rect2, Rect rect3, Rect rect4, boolean z, Configuration configuration) throws RemoteException;

    void moved(int i, int i2) throws RemoteException;

    void dispatchAppVisibility(boolean z) throws RemoteException;

    void dispatchGetNewSurface() throws RemoteException;

    void dispatchScreenState(boolean z) throws RemoteException;

    void windowFocusChanged(boolean z, boolean z2) throws RemoteException;

    void closeSystemDialogs(String str) throws RemoteException;

    void dispatchWallpaperOffsets(float f, float f2, float f3, float f4, boolean z) throws RemoteException;

    void dispatchWallpaperCommand(String str, int i, int i2, int i3, Bundle bundle, boolean z) throws RemoteException;

    void dispatchDragEvent(DragEvent dragEvent) throws RemoteException;

    void dispatchSystemUiVisibilityChanged(int i, int i2, int i3, int i4) throws RemoteException;

    void doneAnimating() throws RemoteException;

    /* loaded from: IWindow$Stub.class */
    public static abstract class Stub extends Binder implements IWindow {
        private static final String DESCRIPTOR = "android.view.IWindow";
        static final int TRANSACTION_executeCommand = 1;
        static final int TRANSACTION_resized = 2;
        static final int TRANSACTION_moved = 3;
        static final int TRANSACTION_dispatchAppVisibility = 4;
        static final int TRANSACTION_dispatchGetNewSurface = 5;
        static final int TRANSACTION_dispatchScreenState = 6;
        static final int TRANSACTION_windowFocusChanged = 7;
        static final int TRANSACTION_closeSystemDialogs = 8;
        static final int TRANSACTION_dispatchWallpaperOffsets = 9;
        static final int TRANSACTION_dispatchWallpaperCommand = 10;
        static final int TRANSACTION_dispatchDragEvent = 11;
        static final int TRANSACTION_dispatchSystemUiVisibilityChanged = 12;
        static final int TRANSACTION_doneAnimating = 13;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWindow asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWindow)) {
                return (IWindow) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            DragEvent _arg0;
            Bundle _arg4;
            Rect _arg02;
            Rect _arg1;
            Rect _arg2;
            Rect _arg3;
            Configuration _arg5;
            ParcelFileDescriptor _arg22;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    String _arg12 = data.readString();
                    if (0 != data.readInt()) {
                        _arg22 = ParcelFileDescriptor.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    executeCommand(_arg03, _arg12, _arg22);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = Rect.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg1 = Rect.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg2 = Rect.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg3 = Rect.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    boolean _arg42 = 0 != data.readInt();
                    if (0 != data.readInt()) {
                        _arg5 = Configuration.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    resized(_arg02, _arg1, _arg2, _arg3, _arg42, _arg5);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg13 = data.readInt();
                    moved(_arg04, _arg13);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg05 = 0 != data.readInt();
                    dispatchAppVisibility(_arg05);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    dispatchGetNewSurface();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg06 = 0 != data.readInt();
                    dispatchScreenState(_arg06);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg07 = 0 != data.readInt();
                    boolean _arg14 = 0 != data.readInt();
                    windowFocusChanged(_arg07, _arg14);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg08 = data.readString();
                    closeSystemDialogs(_arg08);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    float _arg09 = data.readFloat();
                    float _arg15 = data.readFloat();
                    float _arg23 = data.readFloat();
                    float _arg32 = data.readFloat();
                    boolean _arg43 = 0 != data.readInt();
                    dispatchWallpaperOffsets(_arg09, _arg15, _arg23, _arg32, _arg43);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg010 = data.readString();
                    int _arg16 = data.readInt();
                    int _arg24 = data.readInt();
                    int _arg33 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg4 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    boolean _arg52 = 0 != data.readInt();
                    dispatchWallpaperCommand(_arg010, _arg16, _arg24, _arg33, _arg4, _arg52);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = DragEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    dispatchDragEvent(_arg0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _arg17 = data.readInt();
                    int _arg25 = data.readInt();
                    int _arg34 = data.readInt();
                    dispatchSystemUiVisibilityChanged(_arg011, _arg17, _arg25, _arg34);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    doneAnimating();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IWindow$Stub$Proxy.class */
        private static class Proxy implements IWindow {
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

            @Override // android.view.IWindow
            public void executeCommand(String command, String parameters, ParcelFileDescriptor descriptor) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(command);
                    _data.writeString(parameters);
                    if (descriptor != null) {
                        _data.writeInt(1);
                        descriptor.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void resized(Rect frame, Rect overscanInsets, Rect contentInsets, Rect visibleInsets, boolean reportDraw, Configuration newConfig) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (frame != null) {
                        _data.writeInt(1);
                        frame.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (overscanInsets != null) {
                        _data.writeInt(1);
                        overscanInsets.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (contentInsets != null) {
                        _data.writeInt(1);
                        contentInsets.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (visibleInsets != null) {
                        _data.writeInt(1);
                        visibleInsets.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(reportDraw ? 1 : 0);
                    if (newConfig != null) {
                        _data.writeInt(1);
                        newConfig.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void moved(int newX, int newY) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(newX);
                    _data.writeInt(newY);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void dispatchAppVisibility(boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(visible ? 1 : 0);
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void dispatchGetNewSurface() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void dispatchScreenState(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(on ? 1 : 0);
                    this.mRemote.transact(6, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void windowFocusChanged(boolean hasFocus, boolean inTouchMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(hasFocus ? 1 : 0);
                    _data.writeInt(inTouchMode ? 1 : 0);
                    this.mRemote.transact(7, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void closeSystemDialogs(String reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(reason);
                    this.mRemote.transact(8, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void dispatchWallpaperOffsets(float x, float y, float xStep, float yStep, boolean sync) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(x);
                    _data.writeFloat(y);
                    _data.writeFloat(xStep);
                    _data.writeFloat(yStep);
                    _data.writeInt(sync ? 1 : 0);
                    this.mRemote.transact(9, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras, boolean sync) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    _data.writeInt(x);
                    _data.writeInt(y);
                    _data.writeInt(z);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(sync ? 1 : 0);
                    this.mRemote.transact(10, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void dispatchDragEvent(DragEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(1);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(11, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void dispatchSystemUiVisibilityChanged(int seq, int globalVisibility, int localValue, int localChanges) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(seq);
                    _data.writeInt(globalVisibility);
                    _data.writeInt(localValue);
                    _data.writeInt(localChanges);
                    this.mRemote.transact(12, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindow
            public void doneAnimating() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}