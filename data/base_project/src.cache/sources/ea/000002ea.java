package android.content;

import android.content.res.AssetFileDescriptor;
import android.database.BulkCursorDescriptor;
import android.database.BulkCursorToCursorAdaptor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ContentProviderNative.java */
/* loaded from: ContentProviderProxy.class */
public final class ContentProviderProxy implements IContentProvider {
    private IBinder mRemote;

    public ContentProviderProxy(IBinder remote) {
        this.mRemote = remote;
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this.mRemote;
    }

    @Override // android.content.IContentProvider
    public Cursor query(String callingPkg, Uri url, String[] projection, String selection, String[] selectionArgs, String sortOrder, ICancellationSignal cancellationSignal) throws RemoteException {
        int length;
        BulkCursorToCursorAdaptor adaptor = new BulkCursorToCursorAdaptor();
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            try {
                try {
                    data.writeInterfaceToken(IContentProvider.descriptor);
                    data.writeString(callingPkg);
                    url.writeToParcel(data, 0);
                    int length2 = 0;
                    if (projection != null) {
                        length2 = projection.length;
                    }
                    data.writeInt(length2);
                    for (int i = 0; i < length2; i++) {
                        data.writeString(projection[i]);
                    }
                    data.writeString(selection);
                    if (selectionArgs != null) {
                        length = selectionArgs.length;
                    } else {
                        length = 0;
                    }
                    data.writeInt(length);
                    for (int i2 = 0; i2 < length; i2++) {
                        data.writeString(selectionArgs[i2]);
                    }
                    data.writeString(sortOrder);
                    data.writeStrongBinder(adaptor.getObserver().asBinder());
                    data.writeStrongBinder(cancellationSignal != null ? cancellationSignal.asBinder() : null);
                    this.mRemote.transact(1, data, reply, 0);
                    DatabaseUtils.readExceptionFromParcel(reply);
                    if (reply.readInt() != 0) {
                        BulkCursorDescriptor d = BulkCursorDescriptor.CREATOR.createFromParcel(reply);
                        adaptor.initialize(d);
                    } else {
                        adaptor.close();
                        adaptor = null;
                    }
                    return adaptor;
                } catch (RemoteException ex) {
                    adaptor.close();
                    throw ex;
                }
            } catch (RuntimeException ex2) {
                adaptor.close();
                throw ex2;
            }
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public String getType(Uri url) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            url.writeToParcel(data, 0);
            this.mRemote.transact(2, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            String out = reply.readString();
            data.recycle();
            reply.recycle();
            return out;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public Uri insert(String callingPkg, Uri url, ContentValues values) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            url.writeToParcel(data, 0);
            values.writeToParcel(data, 0);
            this.mRemote.transact(3, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            Uri out = Uri.CREATOR.createFromParcel(reply);
            data.recycle();
            reply.recycle();
            return out;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public int bulkInsert(String callingPkg, Uri url, ContentValues[] values) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            url.writeToParcel(data, 0);
            data.writeTypedArray(values, 0);
            this.mRemote.transact(13, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            int count = reply.readInt();
            data.recycle();
            reply.recycle();
            return count;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public ContentProviderResult[] applyBatch(String callingPkg, ArrayList<ContentProviderOperation> operations) throws RemoteException, OperationApplicationException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            data.writeInt(operations.size());
            Iterator i$ = operations.iterator();
            while (i$.hasNext()) {
                ContentProviderOperation operation = i$.next();
                operation.writeToParcel(data, 0);
            }
            this.mRemote.transact(20, data, reply, 0);
            DatabaseUtils.readExceptionWithOperationApplicationExceptionFromParcel(reply);
            ContentProviderResult[] results = (ContentProviderResult[]) reply.createTypedArray(ContentProviderResult.CREATOR);
            data.recycle();
            reply.recycle();
            return results;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public int delete(String callingPkg, Uri url, String selection, String[] selectionArgs) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            url.writeToParcel(data, 0);
            data.writeString(selection);
            data.writeStringArray(selectionArgs);
            this.mRemote.transact(4, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            int count = reply.readInt();
            data.recycle();
            reply.recycle();
            return count;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public int update(String callingPkg, Uri url, ContentValues values, String selection, String[] selectionArgs) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            url.writeToParcel(data, 0);
            values.writeToParcel(data, 0);
            data.writeString(selection);
            data.writeStringArray(selectionArgs);
            this.mRemote.transact(10, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            int count = reply.readInt();
            data.recycle();
            reply.recycle();
            return count;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public ParcelFileDescriptor openFile(String callingPkg, Uri url, String mode, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            url.writeToParcel(data, 0);
            data.writeString(mode);
            data.writeStrongBinder(signal != null ? signal.asBinder() : null);
            this.mRemote.transact(14, data, reply, 0);
            DatabaseUtils.readExceptionWithFileNotFoundExceptionFromParcel(reply);
            int has = reply.readInt();
            ParcelFileDescriptor fd = has != 0 ? reply.readFileDescriptor() : null;
            return fd;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public AssetFileDescriptor openAssetFile(String callingPkg, Uri url, String mode, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            url.writeToParcel(data, 0);
            data.writeString(mode);
            data.writeStrongBinder(signal != null ? signal.asBinder() : null);
            this.mRemote.transact(15, data, reply, 0);
            DatabaseUtils.readExceptionWithFileNotFoundExceptionFromParcel(reply);
            int has = reply.readInt();
            AssetFileDescriptor fd = has != 0 ? AssetFileDescriptor.CREATOR.createFromParcel(reply) : null;
            return fd;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public Bundle call(String callingPkg, String method, String request, Bundle args) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            data.writeString(method);
            data.writeString(request);
            data.writeBundle(args);
            this.mRemote.transact(21, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            Bundle bundle = reply.readBundle();
            data.recycle();
            reply.recycle();
            return bundle;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public String[] getStreamTypes(Uri url, String mimeTypeFilter) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            url.writeToParcel(data, 0);
            data.writeString(mimeTypeFilter);
            this.mRemote.transact(22, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            String[] out = reply.createStringArray();
            data.recycle();
            reply.recycle();
            return out;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public AssetFileDescriptor openTypedAssetFile(String callingPkg, Uri url, String mimeType, Bundle opts, ICancellationSignal signal) throws RemoteException, FileNotFoundException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            url.writeToParcel(data, 0);
            data.writeString(mimeType);
            data.writeBundle(opts);
            data.writeStrongBinder(signal != null ? signal.asBinder() : null);
            this.mRemote.transact(23, data, reply, 0);
            DatabaseUtils.readExceptionWithFileNotFoundExceptionFromParcel(reply);
            int has = reply.readInt();
            AssetFileDescriptor fd = has != 0 ? AssetFileDescriptor.CREATOR.createFromParcel(reply) : null;
            return fd;
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    @Override // android.content.IContentProvider
    public ICancellationSignal createCancellationSignal() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            this.mRemote.transact(24, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            ICancellationSignal cancellationSignal = ICancellationSignal.Stub.asInterface(reply.readStrongBinder());
            data.recycle();
            reply.recycle();
            return cancellationSignal;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public Uri canonicalize(String callingPkg, Uri url) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            url.writeToParcel(data, 0);
            this.mRemote.transact(25, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            Uri out = Uri.CREATOR.createFromParcel(reply);
            data.recycle();
            reply.recycle();
            return out;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }

    @Override // android.content.IContentProvider
    public Uri uncanonicalize(String callingPkg, Uri url) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(IContentProvider.descriptor);
            data.writeString(callingPkg);
            url.writeToParcel(data, 0);
            this.mRemote.transact(26, data, reply, 0);
            DatabaseUtils.readExceptionFromParcel(reply);
            Uri out = Uri.CREATOR.createFromParcel(reply);
            data.recycle();
            reply.recycle();
            return out;
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
    }
}