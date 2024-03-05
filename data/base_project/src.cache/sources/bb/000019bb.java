package com.android.internal.appwidget;

import android.appwidget.AppWidgetProviderInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.RemoteViews;

/* loaded from: IAppWidgetHost.class */
public interface IAppWidgetHost extends IInterface {
    void updateAppWidget(int i, RemoteViews remoteViews, int i2) throws RemoteException;

    void providerChanged(int i, AppWidgetProviderInfo appWidgetProviderInfo, int i2) throws RemoteException;

    void providersChanged(int i) throws RemoteException;

    void viewDataChanged(int i, int i2, int i3) throws RemoteException;

    /* loaded from: IAppWidgetHost$Stub.class */
    public static abstract class Stub extends Binder implements IAppWidgetHost {
        private static final String DESCRIPTOR = "com.android.internal.appwidget.IAppWidgetHost";
        static final int TRANSACTION_updateAppWidget = 1;
        static final int TRANSACTION_providerChanged = 2;
        static final int TRANSACTION_providersChanged = 3;
        static final int TRANSACTION_viewDataChanged = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAppWidgetHost asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAppWidgetHost)) {
                return (IAppWidgetHost) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            AppWidgetProviderInfo _arg1;
            RemoteViews _arg12;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg12 = RemoteViews.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    int _arg2 = data.readInt();
                    updateAppWidget(_arg0, _arg12, _arg2);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg1 = AppWidgetProviderInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _arg22 = data.readInt();
                    providerChanged(_arg02, _arg1, _arg22);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    providersChanged(_arg03);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg13 = data.readInt();
                    int _arg23 = data.readInt();
                    viewDataChanged(_arg04, _arg13, _arg23);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IAppWidgetHost$Stub$Proxy.class */
        private static class Proxy implements IAppWidgetHost {
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

            @Override // com.android.internal.appwidget.IAppWidgetHost
            public void updateAppWidget(int appWidgetId, RemoteViews views, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    if (views != null) {
                        _data.writeInt(1);
                        views.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetHost
            public void providerChanged(int appWidgetId, AppWidgetProviderInfo info, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetHost
            public void providersChanged(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetHost
            public void viewDataChanged(int appWidgetId, int viewId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    _data.writeInt(viewId);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, null, 1);
                    _data.recycle();
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }
    }
}