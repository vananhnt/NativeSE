package com.android.internal.appwidget;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.RemoteViews;
import com.android.internal.appwidget.IAppWidgetHost;
import java.util.ArrayList;
import java.util.List;

/* loaded from: IAppWidgetService.class */
public interface IAppWidgetService extends IInterface {
    int[] startListening(IAppWidgetHost iAppWidgetHost, String str, int i, List<RemoteViews> list, int i2) throws RemoteException;

    void stopListening(int i, int i2) throws RemoteException;

    int allocateAppWidgetId(String str, int i, int i2) throws RemoteException;

    void deleteAppWidgetId(int i, int i2) throws RemoteException;

    void deleteHost(int i, int i2) throws RemoteException;

    void deleteAllHosts(int i) throws RemoteException;

    RemoteViews getAppWidgetViews(int i, int i2) throws RemoteException;

    int[] getAppWidgetIdsForHost(int i, int i2) throws RemoteException;

    void updateAppWidgetIds(int[] iArr, RemoteViews remoteViews, int i) throws RemoteException;

    void updateAppWidgetOptions(int i, Bundle bundle, int i2) throws RemoteException;

    Bundle getAppWidgetOptions(int i, int i2) throws RemoteException;

    void partiallyUpdateAppWidgetIds(int[] iArr, RemoteViews remoteViews, int i) throws RemoteException;

    void updateAppWidgetProvider(ComponentName componentName, RemoteViews remoteViews, int i) throws RemoteException;

    void notifyAppWidgetViewDataChanged(int[] iArr, int i, int i2) throws RemoteException;

    List<AppWidgetProviderInfo> getInstalledProviders(int i, int i2) throws RemoteException;

    AppWidgetProviderInfo getAppWidgetInfo(int i, int i2) throws RemoteException;

    boolean hasBindAppWidgetPermission(String str, int i) throws RemoteException;

    void setBindAppWidgetPermission(String str, boolean z, int i) throws RemoteException;

    void bindAppWidgetId(int i, ComponentName componentName, Bundle bundle, int i2) throws RemoteException;

    boolean bindAppWidgetIdIfAllowed(String str, int i, ComponentName componentName, Bundle bundle, int i2) throws RemoteException;

    void bindRemoteViewsService(int i, Intent intent, IBinder iBinder, int i2) throws RemoteException;

    void unbindRemoteViewsService(int i, Intent intent, int i2) throws RemoteException;

    int[] getAppWidgetIds(ComponentName componentName, int i) throws RemoteException;

    /* loaded from: IAppWidgetService$Stub.class */
    public static abstract class Stub extends Binder implements IAppWidgetService {
        private static final String DESCRIPTOR = "com.android.internal.appwidget.IAppWidgetService";
        static final int TRANSACTION_startListening = 1;
        static final int TRANSACTION_stopListening = 2;
        static final int TRANSACTION_allocateAppWidgetId = 3;
        static final int TRANSACTION_deleteAppWidgetId = 4;
        static final int TRANSACTION_deleteHost = 5;
        static final int TRANSACTION_deleteAllHosts = 6;
        static final int TRANSACTION_getAppWidgetViews = 7;
        static final int TRANSACTION_getAppWidgetIdsForHost = 8;
        static final int TRANSACTION_updateAppWidgetIds = 9;
        static final int TRANSACTION_updateAppWidgetOptions = 10;
        static final int TRANSACTION_getAppWidgetOptions = 11;
        static final int TRANSACTION_partiallyUpdateAppWidgetIds = 12;
        static final int TRANSACTION_updateAppWidgetProvider = 13;
        static final int TRANSACTION_notifyAppWidgetViewDataChanged = 14;
        static final int TRANSACTION_getInstalledProviders = 15;
        static final int TRANSACTION_getAppWidgetInfo = 16;
        static final int TRANSACTION_hasBindAppWidgetPermission = 17;
        static final int TRANSACTION_setBindAppWidgetPermission = 18;
        static final int TRANSACTION_bindAppWidgetId = 19;
        static final int TRANSACTION_bindAppWidgetIdIfAllowed = 20;
        static final int TRANSACTION_bindRemoteViewsService = 21;
        static final int TRANSACTION_unbindRemoteViewsService = 22;
        static final int TRANSACTION_getAppWidgetIds = 23;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAppWidgetService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAppWidgetService)) {
                return (IAppWidgetService) iin;
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
            Intent _arg1;
            Intent _arg12;
            ComponentName _arg2;
            Bundle _arg3;
            ComponentName _arg13;
            Bundle _arg22;
            ComponentName _arg02;
            RemoteViews _arg14;
            RemoteViews _arg15;
            Bundle _arg16;
            RemoteViews _arg17;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IAppWidgetHost _arg03 = IAppWidgetHost.Stub.asInterface(data.readStrongBinder());
                    String _arg18 = data.readString();
                    int _arg23 = data.readInt();
                    ArrayList arrayList = new ArrayList();
                    int _arg4 = data.readInt();
                    int[] _result = startListening(_arg03, _arg18, _arg23, arrayList, _arg4);
                    reply.writeNoException();
                    reply.writeIntArray(_result);
                    reply.writeTypedList(arrayList);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg19 = data.readInt();
                    stopListening(_arg04, _arg19);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    int _arg110 = data.readInt();
                    int _arg24 = data.readInt();
                    int _result2 = allocateAppWidgetId(_arg05, _arg110, _arg24);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    int _arg111 = data.readInt();
                    deleteAppWidgetId(_arg06, _arg111);
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg112 = data.readInt();
                    deleteHost(_arg07, _arg112);
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    deleteAllHosts(_arg08);
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    int _arg113 = data.readInt();
                    RemoteViews _result3 = getAppWidgetViews(_arg09, _arg113);
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(1);
                        _result3.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    int _arg114 = data.readInt();
                    int[] _result4 = getAppWidgetIdsForHost(_arg010, _arg114);
                    reply.writeNoException();
                    reply.writeIntArray(_result4);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _arg011 = data.createIntArray();
                    if (0 != data.readInt()) {
                        _arg17 = RemoteViews.CREATOR.createFromParcel(data);
                    } else {
                        _arg17 = null;
                    }
                    int _arg25 = data.readInt();
                    updateAppWidgetIds(_arg011, _arg17, _arg25);
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg16 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg16 = null;
                    }
                    int _arg26 = data.readInt();
                    updateAppWidgetOptions(_arg012, _arg16, _arg26);
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    int _arg115 = data.readInt();
                    Bundle _result5 = getAppWidgetOptions(_arg013, _arg115);
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(1);
                        _result5.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _arg014 = data.createIntArray();
                    if (0 != data.readInt()) {
                        _arg15 = RemoteViews.CREATOR.createFromParcel(data);
                    } else {
                        _arg15 = null;
                    }
                    int _arg27 = data.readInt();
                    partiallyUpdateAppWidgetIds(_arg014, _arg15, _arg27);
                    reply.writeNoException();
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg14 = RemoteViews.CREATOR.createFromParcel(data);
                    } else {
                        _arg14 = null;
                    }
                    int _arg28 = data.readInt();
                    updateAppWidgetProvider(_arg02, _arg14, _arg28);
                    reply.writeNoException();
                    return true;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _arg015 = data.createIntArray();
                    int _arg116 = data.readInt();
                    int _arg29 = data.readInt();
                    notifyAppWidgetViewDataChanged(_arg015, _arg116, _arg29);
                    reply.writeNoException();
                    return true;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg016 = data.readInt();
                    int _arg117 = data.readInt();
                    List<AppWidgetProviderInfo> _result6 = getInstalledProviders(_arg016, _arg117);
                    reply.writeNoException();
                    reply.writeTypedList(_result6);
                    return true;
                case 16:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    int _arg118 = data.readInt();
                    AppWidgetProviderInfo _result7 = getAppWidgetInfo(_arg017, _arg118);
                    reply.writeNoException();
                    if (_result7 != null) {
                        reply.writeInt(1);
                        _result7.writeToParcel(reply, 1);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 17:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg018 = data.readString();
                    int _arg119 = data.readInt();
                    boolean _result8 = hasBindAppWidgetPermission(_arg018, _arg119);
                    reply.writeNoException();
                    reply.writeInt(_result8 ? 1 : 0);
                    return true;
                case 18:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg019 = data.readString();
                    boolean _arg120 = 0 != data.readInt();
                    int _arg210 = data.readInt();
                    setBindAppWidgetPermission(_arg019, _arg120, _arg210);
                    reply.writeNoException();
                    return true;
                case 19:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg020 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg13 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg22 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    int _arg32 = data.readInt();
                    bindAppWidgetId(_arg020, _arg13, _arg22, _arg32);
                    reply.writeNoException();
                    return true;
                case 20:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg021 = data.readString();
                    int _arg121 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg2 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg3 = Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    int _arg42 = data.readInt();
                    boolean _result9 = bindAppWidgetIdIfAllowed(_arg021, _arg121, _arg2, _arg3, _arg42);
                    reply.writeNoException();
                    reply.writeInt(_result9 ? 1 : 0);
                    return true;
                case 21:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg022 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg12 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    IBinder _arg211 = data.readStrongBinder();
                    int _arg33 = data.readInt();
                    bindRemoteViewsService(_arg022, _arg12, _arg211, _arg33);
                    reply.writeNoException();
                    return true;
                case 22:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg1 = Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _arg212 = data.readInt();
                    unbindRemoteViewsService(_arg023, _arg1, _arg212);
                    reply.writeNoException();
                    return true;
                case 23:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _arg122 = data.readInt();
                    int[] _result10 = getAppWidgetIds(_arg0, _arg122);
                    reply.writeNoException();
                    reply.writeIntArray(_result10);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: IAppWidgetService$Stub$Proxy.class */
        private static class Proxy implements IAppWidgetService {
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public int[] startListening(IAppWidgetHost host, String packageName, int hostId, List<RemoteViews> updatedViews, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(host != null ? host.asBinder() : null);
                    _data.writeString(packageName);
                    _data.writeInt(hostId);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    _reply.readTypedList(updatedViews, RemoteViews.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void stopListening(int hostId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(hostId);
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public int allocateAppWidgetId(String packageName, int hostId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(hostId);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void deleteAppWidgetId(int appWidgetId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void deleteHost(int hostId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(hostId);
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void deleteAllHosts(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public RemoteViews getAppWidgetViews(int appWidgetId, int userId) throws RemoteException {
                RemoteViews _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = RemoteViews.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public int[] getAppWidgetIdsForHost(int hostId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(hostId);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void updateAppWidgetIds(int[] appWidgetIds, RemoteViews views, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(appWidgetIds);
                    if (views != null) {
                        _data.writeInt(1);
                        views.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void updateAppWidgetOptions(int appWidgetId, Bundle extras, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public Bundle getAppWidgetOptions(int appWidgetId, int userId) throws RemoteException {
                Bundle _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void partiallyUpdateAppWidgetIds(int[] appWidgetIds, RemoteViews views, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(appWidgetIds);
                    if (views != null) {
                        _data.writeInt(1);
                        views.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void updateAppWidgetProvider(ComponentName provider, RemoteViews views, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (provider != null) {
                        _data.writeInt(1);
                        provider.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (views != null) {
                        _data.writeInt(1);
                        views.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void notifyAppWidgetViewDataChanged(int[] appWidgetIds, int viewId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(appWidgetIds);
                    _data.writeInt(viewId);
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public List<AppWidgetProviderInfo> getInstalledProviders(int categoryFilter, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(categoryFilter);
                    _data.writeInt(userId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    List<AppWidgetProviderInfo> _result = _reply.createTypedArrayList(AppWidgetProviderInfo.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public AppWidgetProviderInfo getAppWidgetInfo(int appWidgetId, int userId) throws RemoteException {
                AppWidgetProviderInfo _result;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    _data.writeInt(userId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt()) {
                        _result = AppWidgetProviderInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public boolean hasBindAppWidgetPermission(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void setBindAppWidgetPermission(String packageName, boolean permission, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(permission ? 1 : 0);
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void bindAppWidgetId(int appWidgetId, ComponentName provider, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    if (provider != null) {
                        _data.writeInt(1);
                        provider.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public boolean bindAppWidgetIdIfAllowed(String packageName, int appWidgetId, ComponentName provider, Bundle options, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(appWidgetId);
                    if (provider != null) {
                        _data.writeInt(1);
                        provider.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = 0 != _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void bindRemoteViewsService(int appWidgetId, Intent intent, IBinder connection, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(connection);
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void unbindRemoteViewsService(int appWidgetId, Intent intent, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(appWidgetId);
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
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

            @Override // com.android.internal.appwidget.IAppWidgetService
            public int[] getAppWidgetIds(ComponentName provider, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (provider != null) {
                        _data.writeInt(1);
                        provider.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(23, _data, _reply, 0);
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
        }
    }
}