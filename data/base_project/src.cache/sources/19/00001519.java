package android.view;

import android.content.ClipData;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.IWindow;
import android.view.IWindowId;
import android.view.WindowManager;

/* loaded from: IWindowSession.class */
public interface IWindowSession extends IInterface {
    int add(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, Rect rect, InputChannel inputChannel) throws RemoteException;

    int addToDisplay(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, int i3, Rect rect, InputChannel inputChannel) throws RemoteException;

    int addWithoutInputChannel(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, Rect rect) throws RemoteException;

    int addToDisplayWithoutInputChannel(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, int i3, Rect rect) throws RemoteException;

    void remove(IWindow iWindow) throws RemoteException;

    int relayout(IWindow iWindow, int i, WindowManager.LayoutParams layoutParams, int i2, int i3, int i4, int i5, Rect rect, Rect rect2, Rect rect3, Rect rect4, Configuration configuration, Surface surface) throws RemoteException;

    void performDeferredDestroy(IWindow iWindow) throws RemoteException;

    boolean outOfMemory(IWindow iWindow) throws RemoteException;

    void setTransparentRegion(IWindow iWindow, Region region) throws RemoteException;

    void setInsets(IWindow iWindow, int i, Rect rect, Rect rect2, Region region) throws RemoteException;

    void getDisplayFrame(IWindow iWindow, Rect rect) throws RemoteException;

    void finishDrawing(IWindow iWindow) throws RemoteException;

    void setInTouchMode(boolean z) throws RemoteException;

    boolean getInTouchMode() throws RemoteException;

    boolean performHapticFeedback(IWindow iWindow, int i, boolean z) throws RemoteException;

    IBinder prepareDrag(IWindow iWindow, int i, int i2, int i3, Surface surface) throws RemoteException;

    boolean performDrag(IWindow iWindow, IBinder iBinder, float f, float f2, float f3, float f4, ClipData clipData) throws RemoteException;

    void reportDropResult(IWindow iWindow, boolean z) throws RemoteException;

    void dragRecipientEntered(IWindow iWindow) throws RemoteException;

    void dragRecipientExited(IWindow iWindow) throws RemoteException;

    void setWallpaperPosition(IBinder iBinder, float f, float f2, float f3, float f4) throws RemoteException;

    void wallpaperOffsetsComplete(IBinder iBinder) throws RemoteException;

    Bundle sendWallpaperCommand(IBinder iBinder, String str, int i, int i2, int i3, Bundle bundle, boolean z) throws RemoteException;

    void wallpaperCommandComplete(IBinder iBinder, Bundle bundle) throws RemoteException;

    void setUniverseTransform(IBinder iBinder, float f, float f2, float f3, float f4, float f5, float f6, float f7) throws RemoteException;

    void onRectangleOnScreenRequested(IBinder iBinder, Rect rect, boolean z) throws RemoteException;

    IWindowId getWindowId(IBinder iBinder) throws RemoteException;

    /* loaded from: IWindowSession$Stub.class */
    public static abstract class Stub extends Binder implements IWindowSession {
        private static final String DESCRIPTOR = "android.view.IWindowSession";
        static final int TRANSACTION_add = 1;
        static final int TRANSACTION_addToDisplay = 2;
        static final int TRANSACTION_addWithoutInputChannel = 3;
        static final int TRANSACTION_addToDisplayWithoutInputChannel = 4;
        static final int TRANSACTION_remove = 5;
        static final int TRANSACTION_relayout = 6;
        static final int TRANSACTION_performDeferredDestroy = 7;
        static final int TRANSACTION_outOfMemory = 8;
        static final int TRANSACTION_setTransparentRegion = 9;
        static final int TRANSACTION_setInsets = 10;
        static final int TRANSACTION_getDisplayFrame = 11;
        static final int TRANSACTION_finishDrawing = 12;
        static final int TRANSACTION_setInTouchMode = 13;
        static final int TRANSACTION_getInTouchMode = 14;
        static final int TRANSACTION_performHapticFeedback = 15;
        static final int TRANSACTION_prepareDrag = 16;
        static final int TRANSACTION_performDrag = 17;
        static final int TRANSACTION_reportDropResult = 18;
        static final int TRANSACTION_dragRecipientEntered = 19;
        static final int TRANSACTION_dragRecipientExited = 20;
        static final int TRANSACTION_setWallpaperPosition = 21;
        static final int TRANSACTION_wallpaperOffsetsComplete = 22;
        static final int TRANSACTION_sendWallpaperCommand = 23;
        static final int TRANSACTION_wallpaperCommandComplete = 24;
        static final int TRANSACTION_setUniverseTransform = 25;
        static final int TRANSACTION_onRectangleOnScreenRequested = 26;
        static final int TRANSACTION_getWindowId = 27;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWindowSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IWindowSession)) {
                return (IWindowSession) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Rect _arg1;
            Bundle _arg12;
            Bundle _arg5;
            ClipData _arg6;
            Rect _arg2;
            Rect _arg3;
            Region _arg4;
            Region _arg13;
            WindowManager.LayoutParams _arg22;
            WindowManager.LayoutParams _arg23;
            WindowManager.LayoutParams _arg24;
            WindowManager.LayoutParams _arg25;
            WindowManager.LayoutParams _arg26;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg0 = IWindow.Stub.asInterface(data.readStrongBinder());
                    int _arg14 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg26 = WindowManager.LayoutParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg26 = null;
                    }
                    int _arg32 = data.readInt();
                    Rect _arg42 = new Rect();
                    InputChannel _arg52 = new InputChannel();
                    int _result = add(_arg0, _arg14, _arg26, _arg32, _arg42, _arg52);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    if (_arg42 != null) {
                        reply.writeInt(1);
                        _arg42.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    if (_arg52 != null) {
                        reply.writeInt(1);
                        _arg52.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg02 = IWindow.Stub.asInterface(data.readStrongBinder());
                    int _arg15 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg25 = WindowManager.LayoutParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg25 = null;
                    }
                    int _arg33 = data.readInt();
                    int _arg43 = data.readInt();
                    Rect _arg53 = new Rect();
                    InputChannel _arg62 = new InputChannel();
                    int _result2 = addToDisplay(_arg02, _arg15, _arg25, _arg33, _arg43, _arg53, _arg62);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    if (_arg53 != null) {
                        reply.writeInt(1);
                        _arg53.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    if (_arg62 != null) {
                        reply.writeInt(1);
                        _arg62.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg03 = IWindow.Stub.asInterface(data.readStrongBinder());
                    int _arg16 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg24 = WindowManager.LayoutParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg24 = null;
                    }
                    int _arg34 = data.readInt();
                    Rect _arg44 = new Rect();
                    int _result3 = addWithoutInputChannel(_arg03, _arg16, _arg24, _arg34, _arg44);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    if (_arg44 != null) {
                        reply.writeInt(1);
                        _arg44.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg04 = IWindow.Stub.asInterface(data.readStrongBinder());
                    int _arg17 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg23 = WindowManager.LayoutParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg23 = null;
                    }
                    int _arg35 = data.readInt();
                    int _arg45 = data.readInt();
                    Rect _arg54 = new Rect();
                    int _result4 = addToDisplayWithoutInputChannel(_arg04, _arg17, _arg23, _arg35, _arg45, _arg54);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    if (_arg54 != null) {
                        reply.writeInt(1);
                        _arg54.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg05 = IWindow.Stub.asInterface(data.readStrongBinder());
                    remove(_arg05);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg06 = IWindow.Stub.asInterface(data.readStrongBinder());
                    int _arg18 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg22 = WindowManager.LayoutParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    int _arg36 = data.readInt();
                    int _arg46 = data.readInt();
                    int _arg55 = data.readInt();
                    int _arg63 = data.readInt();
                    Rect _arg7 = new Rect();
                    Rect _arg8 = new Rect();
                    Rect _arg9 = new Rect();
                    Rect _arg10 = new Rect();
                    Configuration _arg11 = new Configuration();
                    Surface _arg122 = new Surface();
                    int _result5 = relayout(_arg06, _arg18, _arg22, _arg36, _arg46, _arg55, _arg63, _arg7, _arg8, _arg9, _arg10, _arg11, _arg122);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    if (_arg7 != null) {
                        reply.writeInt(1);
                        _arg7.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    if (_arg8 != null) {
                        reply.writeInt(1);
                        _arg8.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    if (_arg9 != null) {
                        reply.writeInt(1);
                        _arg9.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    if (_arg10 != null) {
                        reply.writeInt(1);
                        _arg10.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    if (_arg11 != null) {
                        reply.writeInt(1);
                        _arg11.writeToParcel(reply, 1);
                    } else {
                        reply.writeInt(0);
                    }
                    if (_arg122 != null) {
                        reply.writeInt(1);
                        _arg122.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg07 = IWindow.Stub.asInterface(data.readStrongBinder());
                    performDeferredDestroy(_arg07);
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg08 = IWindow.Stub.asInterface(data.readStrongBinder());
                    boolean _result6 = outOfMemory(_arg08);
                    reply.writeNoException();
                    reply.writeInt(_result6 ? 1 : 0);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg09 = IWindow.Stub.asInterface(data.readStrongBinder());
                    if (0 != data.readInt()) {
                        _arg13 = Region.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    setTransparentRegion(_arg09, _arg13);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg010 = IWindow.Stub.asInterface(data.readStrongBinder());
                    int _arg19 = data.readInt();
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
                    if (0 != data.readInt()) {
                        _arg4 = Region.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    setInsets(_arg010, _arg19, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg011 = IWindow.Stub.asInterface(data.readStrongBinder());
                    Rect _arg110 = new Rect();
                    getDisplayFrame(_arg011, _arg110);
                    reply.writeNoException();
                    if (_arg110 != null) {
                        reply.writeInt(1);
                        _arg110.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg012 = IWindow.Stub.asInterface(data.readStrongBinder());
                    finishDrawing(_arg012);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg013 = 0 != data.readInt();
                    setInTouchMode(_arg013);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result7 = getInTouchMode();
                    reply.writeNoException();
                    reply.writeInt(_result7 ? 1 : 0);
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg014 = IWindow.Stub.asInterface(data.readStrongBinder());
                    int _arg111 = data.readInt();
                    boolean _arg27 = 0 != data.readInt();
                    boolean _result8 = performHapticFeedback(_arg014, _arg111, _arg27);
                    reply.writeNoException();
                    reply.writeInt(_result8 ? 1 : 0);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg015 = IWindow.Stub.asInterface(data.readStrongBinder());
                    int _arg112 = data.readInt();
                    int _arg28 = data.readInt();
                    int _arg37 = data.readInt();
                    Surface _arg47 = new Surface();
                    IBinder _result9 = prepareDrag(_arg015, _arg112, _arg28, _arg37, _arg47);
                    reply.writeNoException();
                    reply.writeStrongBinder(_result9);
                    if (_arg47 != null) {
                        reply.writeInt(1);
                        _arg47.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg016 = IWindow.Stub.asInterface(data.readStrongBinder());
                    IBinder _arg113 = data.readStrongBinder();
                    float _arg29 = data.readFloat();
                    float _arg38 = data.readFloat();
                    float _arg48 = data.readFloat();
                    float _arg56 = data.readFloat();
                    if (0 != data.readInt()) {
                        _arg6 = ClipData.CREATOR.createFromParcel(data);
                    } else {
                        _arg6 = null;
                    }
                    boolean _result10 = performDrag(_arg016, _arg113, _arg29, _arg38, _arg48, _arg56, _arg6);
                    reply.writeNoException();
                    reply.writeInt(_result10 ? 1 : 0);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg017 = IWindow.Stub.asInterface(data.readStrongBinder());
                    boolean _arg114 = 0 != data.readInt();
                    reportDropResult(_arg017, _arg114);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg018 = IWindow.Stub.asInterface(data.readStrongBinder());
                    dragRecipientEntered(_arg018);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    IWindow _arg019 = IWindow.Stub.asInterface(data.readStrongBinder());
                    dragRecipientExited(_arg019);
                    reply.writeNoException();
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg020 = data.readStrongBinder();
                    float _arg115 = data.readFloat();
                    float _arg210 = data.readFloat();
                    float _arg39 = data.readFloat();
                    float _arg49 = data.readFloat();
                    setWallpaperPosition(_arg020, _arg115, _arg210, _arg39, _arg49);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg021 = data.readStrongBinder();
                    wallpaperOffsetsComplete(_arg021);
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg022 = data.readStrongBinder();
                    String _arg116 = data.readString();
                    int _arg211 = data.readInt();
                    int _arg310 = data.readInt();
                    int _arg410 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg5 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    boolean _arg64 = 0 != data.readInt();
                    Bundle _result11 = sendWallpaperCommand(_arg022, _arg116, _arg211, _arg310, _arg410, _arg5, _arg64);
                    reply.writeNoException();
                    if (_result11 != null) {
                        reply.writeInt(1);
                        _result11.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 24:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg023 = data.readStrongBinder();
                    if (0 != data.readInt()) {
                        _arg12 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    wallpaperCommandComplete(_arg023, _arg12);
                    reply.writeNoException();
                    return true;
                case 25:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg024 = data.readStrongBinder();
                    float _arg117 = data.readFloat();
                    float _arg212 = data.readFloat();
                    float _arg311 = data.readFloat();
                    float _arg411 = data.readFloat();
                    float _arg57 = data.readFloat();
                    float _arg65 = data.readFloat();
                    setUniverseTransform(_arg024, _arg117, _arg212, _arg311, _arg411, _arg57, _arg65, data.readFloat());
                    reply.writeNoException();
                    return true;
                case 26:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg025 = data.readStrongBinder();
                    if (0 != data.readInt()) {
                        _arg1 = Rect.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    boolean _arg213 = 0 != data.readInt();
                    onRectangleOnScreenRequested(_arg025, _arg1, _arg213);
                    reply.writeNoException();
                    return true;
                case 27:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg026 = data.readStrongBinder();
                    IWindowId _result12 = getWindowId(_arg026);
                    reply.writeNoException();
                    reply.writeStrongBinder(_result12 != null ? _result12.asBinder() : null);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: IWindowSession$Stub$Proxy.class */
        public static class Proxy implements IWindowSession {
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

            @Override // android.view.IWindowSession
            public int add(IWindow window, int seq, WindowManager.LayoutParams attrs, int viewVisibility, Rect outContentInsets, InputChannel outInputChannel) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeInt(seq);
                    if (attrs != null) {
                        _data.writeInt(1);
                        attrs.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(viewVisibility);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        outContentInsets.readFromParcel(_reply);
                    }
                    if (0 != _reply.readInt()) {
                        outInputChannel.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public int addToDisplay(IWindow window, int seq, WindowManager.LayoutParams attrs, int viewVisibility, int layerStackId, Rect outContentInsets, InputChannel outInputChannel) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeInt(seq);
                    if (attrs != null) {
                        _data.writeInt(1);
                        attrs.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(viewVisibility);
                    _data.writeInt(layerStackId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        outContentInsets.readFromParcel(_reply);
                    }
                    if (0 != _reply.readInt()) {
                        outInputChannel.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public int addWithoutInputChannel(IWindow window, int seq, WindowManager.LayoutParams attrs, int viewVisibility, Rect outContentInsets) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeInt(seq);
                    if (attrs != null) {
                        _data.writeInt(1);
                        attrs.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(viewVisibility);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        outContentInsets.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public int addToDisplayWithoutInputChannel(IWindow window, int seq, WindowManager.LayoutParams attrs, int viewVisibility, int layerStackId, Rect outContentInsets) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeInt(seq);
                    if (attrs != null) {
                        _data.writeInt(1);
                        attrs.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(viewVisibility);
                    _data.writeInt(layerStackId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        outContentInsets.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void remove(IWindow window) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public int relayout(IWindow window, int seq, WindowManager.LayoutParams attrs, int requestedWidth, int requestedHeight, int viewVisibility, int flags, Rect outFrame, Rect outOverscanInsets, Rect outContentInsets, Rect outVisibleInsets, Configuration outConfig, Surface outSurface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeInt(seq);
                    if (attrs != null) {
                        _data.writeInt(1);
                        attrs.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(requestedWidth);
                    _data.writeInt(requestedHeight);
                    _data.writeInt(viewVisibility);
                    _data.writeInt(flags);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        outFrame.readFromParcel(_reply);
                    }
                    if (0 != _reply.readInt()) {
                        outOverscanInsets.readFromParcel(_reply);
                    }
                    if (0 != _reply.readInt()) {
                        outContentInsets.readFromParcel(_reply);
                    }
                    if (0 != _reply.readInt()) {
                        outVisibleInsets.readFromParcel(_reply);
                    }
                    if (0 != _reply.readInt()) {
                        outConfig.readFromParcel(_reply);
                    }
                    if (0 != _reply.readInt()) {
                        outSurface.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void performDeferredDestroy(IWindow window) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public boolean outOfMemory(IWindow window) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void setTransparentRegion(IWindow window, Region region) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    if (region != null) {
                        _data.writeInt(1);
                        region.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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

            @Override // android.view.IWindowSession
            public void setInsets(IWindow window, int touchableInsets, Rect contentInsets, Rect visibleInsets, Region touchableRegion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeInt(touchableInsets);
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
                    if (touchableRegion != null) {
                        _data.writeInt(1);
                        touchableRegion.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public void getDisplayFrame(IWindow window, Rect outDisplayFrame) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        outDisplayFrame.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void finishDrawing(IWindow window) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
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

            @Override // android.view.IWindowSession
            public void setInTouchMode(boolean showFocus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(showFocus ? 1 : 0);
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

            @Override // android.view.IWindowSession
            public boolean getInTouchMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public boolean performHapticFeedback(IWindow window, int effectId, boolean always) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeInt(effectId);
                    _data.writeInt(always ? 1 : 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public IBinder prepareDrag(IWindow window, int flags, int thumbnailWidth, int thumbnailHeight, Surface outSurface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeInt(thumbnailWidth);
                    _data.writeInt(thumbnailHeight);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    if (0 != _reply.readInt()) {
                        outSurface.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public boolean performDrag(IWindow window, IBinder dragToken, float touchX, float touchY, float thumbCenterX, float thumbCenterY, ClipData data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeStrongBinder(dragToken);
                    _data.writeFloat(touchX);
                    _data.writeFloat(touchY);
                    _data.writeFloat(thumbCenterX);
                    _data.writeFloat(thumbCenterY);
                    if (data != null) {
                        _data.writeInt(1);
                        data.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void reportDropResult(IWindow window, boolean consumed) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    _data.writeInt(consumed ? 1 : 0);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public void dragRecipientEntered(IWindow window) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public void dragRecipientExited(IWindow window) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window != null ? window.asBinder() : null);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public void setWallpaperPosition(IBinder windowToken, float x, float y, float xstep, float ystep) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(windowToken);
                    _data.writeFloat(x);
                    _data.writeFloat(y);
                    _data.writeFloat(xstep);
                    _data.writeFloat(ystep);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public void wallpaperOffsetsComplete(IBinder window) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public Bundle sendWallpaperCommand(IBinder window, String action, int x, int y, int z, Bundle extras, boolean sync) throws RemoteException {
                Bundle _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window);
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
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = Bundle.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.view.IWindowSession
            public void wallpaperCommandComplete(IBinder window, Bundle result) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window);
                    if (result != null) {
                        _data.writeInt(1);
                        result.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public void setUniverseTransform(IBinder window, float alpha, float offx, float offy, float dsdx, float dtdx, float dsdy, float dtdy) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window);
                    _data.writeFloat(alpha);
                    _data.writeFloat(offx);
                    _data.writeFloat(offy);
                    _data.writeFloat(dsdx);
                    _data.writeFloat(dtdx);
                    _data.writeFloat(dsdy);
                    _data.writeFloat(dtdy);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public void onRectangleOnScreenRequested(IBinder token, Rect rectangle, boolean immediate) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    if (rectangle != null) {
                        _data.writeInt(1);
                        rectangle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(immediate ? 1 : 0);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    _reply.recycle();
                    _data.recycle();
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.view.IWindowSession
            public IWindowId getWindowId(IBinder window) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(window);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    IWindowId _result = IWindowId.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}