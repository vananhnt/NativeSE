package android.content;

import android.app.AppOpsManager;
import android.app.backup.FullBackup;
import android.content.pm.PathPermission;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: ContentProvider.class */
public abstract class ContentProvider implements ComponentCallbacks2 {
    private static final String TAG = "ContentProvider";
    private Context mContext;
    private int mMyUid;
    private String mReadPermission;
    private String mWritePermission;
    private PathPermission[] mPathPermissions;
    private boolean mExported;
    private boolean mNoPerms;
    private final ThreadLocal<String> mCallingPackage;
    private Transport mTransport;

    /* loaded from: ContentProvider$PipeDataWriter.class */
    public interface PipeDataWriter<T> {
        void writeDataToPipe(ParcelFileDescriptor parcelFileDescriptor, Uri uri, String str, Bundle bundle, T t);
    }

    public abstract boolean onCreate();

    public abstract Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2);

    public abstract String getType(Uri uri);

    public abstract Uri insert(Uri uri, ContentValues contentValues);

    public abstract int delete(Uri uri, String str, String[] strArr);

    public abstract int update(Uri uri, ContentValues contentValues, String str, String[] strArr);

    public ContentProvider() {
        this.mContext = null;
        this.mCallingPackage = new ThreadLocal<>();
        this.mTransport = new Transport();
    }

    public ContentProvider(Context context, String readPermission, String writePermission, PathPermission[] pathPermissions) {
        this.mContext = null;
        this.mCallingPackage = new ThreadLocal<>();
        this.mTransport = new Transport();
        this.mContext = context;
        this.mReadPermission = readPermission;
        this.mWritePermission = writePermission;
        this.mPathPermissions = pathPermissions;
    }

    public static ContentProvider coerceToLocalContentProvider(IContentProvider abstractInterface) {
        if (abstractInterface instanceof Transport) {
            return ((Transport) abstractInterface).getContentProvider();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ContentProvider$Transport.class */
    public class Transport extends ContentProviderNative {
        AppOpsManager mAppOpsManager = null;
        int mReadOp = -1;
        int mWriteOp = -1;

        Transport() {
        }

        ContentProvider getContentProvider() {
            return ContentProvider.this;
        }

        @Override // android.content.ContentProviderNative
        public String getProviderName() {
            return getContentProvider().getClass().getName();
        }

        @Override // android.content.IContentProvider
        public Cursor query(String callingPkg, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, ICancellationSignal cancellationSignal) {
            if (enforceReadPermission(callingPkg, uri) == 0) {
                String original = ContentProvider.this.setCallingPackage(callingPkg);
                try {
                    Cursor query = ContentProvider.this.query(uri, projection, selection, selectionArgs, sortOrder, CancellationSignal.fromTransport(cancellationSignal));
                    ContentProvider.this.setCallingPackage(original);
                    return query;
                } catch (Throwable th) {
                    ContentProvider.this.setCallingPackage(original);
                    throw th;
                }
            }
            return ContentProvider.this.rejectQuery(uri, projection, selection, selectionArgs, sortOrder, CancellationSignal.fromTransport(cancellationSignal));
        }

        @Override // android.content.IContentProvider
        public String getType(Uri uri) {
            return ContentProvider.this.getType(uri);
        }

        @Override // android.content.IContentProvider
        public Uri insert(String callingPkg, Uri uri, ContentValues initialValues) {
            if (enforceWritePermission(callingPkg, uri) == 0) {
                String original = ContentProvider.this.setCallingPackage(callingPkg);
                try {
                    Uri insert = ContentProvider.this.insert(uri, initialValues);
                    ContentProvider.this.setCallingPackage(original);
                    return insert;
                } catch (Throwable th) {
                    ContentProvider.this.setCallingPackage(original);
                    throw th;
                }
            }
            return ContentProvider.this.rejectInsert(uri, initialValues);
        }

        @Override // android.content.IContentProvider
        public int bulkInsert(String callingPkg, Uri uri, ContentValues[] initialValues) {
            if (enforceWritePermission(callingPkg, uri) == 0) {
                String original = ContentProvider.this.setCallingPackage(callingPkg);
                try {
                    int bulkInsert = ContentProvider.this.bulkInsert(uri, initialValues);
                    ContentProvider.this.setCallingPackage(original);
                    return bulkInsert;
                } catch (Throwable th) {
                    ContentProvider.this.setCallingPackage(original);
                    throw th;
                }
            }
            return 0;
        }

        @Override // android.content.IContentProvider
        public ContentProviderResult[] applyBatch(String callingPkg, ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
            Iterator i$ = operations.iterator();
            while (i$.hasNext()) {
                ContentProviderOperation operation = i$.next();
                if (operation.isReadOperation() && enforceReadPermission(callingPkg, operation.getUri()) != 0) {
                    throw new OperationApplicationException("App op not allowed", 0);
                }
                if (operation.isWriteOperation() && enforceWritePermission(callingPkg, operation.getUri()) != 0) {
                    throw new OperationApplicationException("App op not allowed", 0);
                }
            }
            String original = ContentProvider.this.setCallingPackage(callingPkg);
            try {
                ContentProviderResult[] applyBatch = ContentProvider.this.applyBatch(operations);
                ContentProvider.this.setCallingPackage(original);
                return applyBatch;
            } catch (Throwable th) {
                ContentProvider.this.setCallingPackage(original);
                throw th;
            }
        }

        @Override // android.content.IContentProvider
        public int delete(String callingPkg, Uri uri, String selection, String[] selectionArgs) {
            if (enforceWritePermission(callingPkg, uri) == 0) {
                String original = ContentProvider.this.setCallingPackage(callingPkg);
                try {
                    int delete = ContentProvider.this.delete(uri, selection, selectionArgs);
                    ContentProvider.this.setCallingPackage(original);
                    return delete;
                } catch (Throwable th) {
                    ContentProvider.this.setCallingPackage(original);
                    throw th;
                }
            }
            return 0;
        }

        @Override // android.content.IContentProvider
        public int update(String callingPkg, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            if (enforceWritePermission(callingPkg, uri) == 0) {
                String original = ContentProvider.this.setCallingPackage(callingPkg);
                try {
                    int update = ContentProvider.this.update(uri, values, selection, selectionArgs);
                    ContentProvider.this.setCallingPackage(original);
                    return update;
                } catch (Throwable th) {
                    ContentProvider.this.setCallingPackage(original);
                    throw th;
                }
            }
            return 0;
        }

        @Override // android.content.IContentProvider
        public ParcelFileDescriptor openFile(String callingPkg, Uri uri, String mode, ICancellationSignal cancellationSignal) throws FileNotFoundException {
            enforceFilePermission(callingPkg, uri, mode);
            String original = ContentProvider.this.setCallingPackage(callingPkg);
            try {
                ParcelFileDescriptor openFile = ContentProvider.this.openFile(uri, mode, CancellationSignal.fromTransport(cancellationSignal));
                ContentProvider.this.setCallingPackage(original);
                return openFile;
            } catch (Throwable th) {
                ContentProvider.this.setCallingPackage(original);
                throw th;
            }
        }

        @Override // android.content.IContentProvider
        public AssetFileDescriptor openAssetFile(String callingPkg, Uri uri, String mode, ICancellationSignal cancellationSignal) throws FileNotFoundException {
            enforceFilePermission(callingPkg, uri, mode);
            String original = ContentProvider.this.setCallingPackage(callingPkg);
            try {
                AssetFileDescriptor openAssetFile = ContentProvider.this.openAssetFile(uri, mode, CancellationSignal.fromTransport(cancellationSignal));
                ContentProvider.this.setCallingPackage(original);
                return openAssetFile;
            } catch (Throwable th) {
                ContentProvider.this.setCallingPackage(original);
                throw th;
            }
        }

        @Override // android.content.IContentProvider
        public Bundle call(String callingPkg, String method, String arg, Bundle extras) {
            String original = ContentProvider.this.setCallingPackage(callingPkg);
            try {
                Bundle call = ContentProvider.this.call(method, arg, extras);
                ContentProvider.this.setCallingPackage(original);
                return call;
            } catch (Throwable th) {
                ContentProvider.this.setCallingPackage(original);
                throw th;
            }
        }

        @Override // android.content.IContentProvider
        public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
            return ContentProvider.this.getStreamTypes(uri, mimeTypeFilter);
        }

        @Override // android.content.IContentProvider
        public AssetFileDescriptor openTypedAssetFile(String callingPkg, Uri uri, String mimeType, Bundle opts, ICancellationSignal cancellationSignal) throws FileNotFoundException {
            enforceFilePermission(callingPkg, uri, FullBackup.ROOT_TREE_TOKEN);
            String original = ContentProvider.this.setCallingPackage(callingPkg);
            try {
                AssetFileDescriptor openTypedAssetFile = ContentProvider.this.openTypedAssetFile(uri, mimeType, opts, CancellationSignal.fromTransport(cancellationSignal));
                ContentProvider.this.setCallingPackage(original);
                return openTypedAssetFile;
            } catch (Throwable th) {
                ContentProvider.this.setCallingPackage(original);
                throw th;
            }
        }

        @Override // android.content.IContentProvider
        public ICancellationSignal createCancellationSignal() {
            return CancellationSignal.createTransport();
        }

        @Override // android.content.IContentProvider
        public Uri canonicalize(String callingPkg, Uri uri) {
            if (enforceReadPermission(callingPkg, uri) == 0) {
                String original = ContentProvider.this.setCallingPackage(callingPkg);
                try {
                    Uri canonicalize = ContentProvider.this.canonicalize(uri);
                    ContentProvider.this.setCallingPackage(original);
                    return canonicalize;
                } catch (Throwable th) {
                    ContentProvider.this.setCallingPackage(original);
                    throw th;
                }
            }
            return null;
        }

        @Override // android.content.IContentProvider
        public Uri uncanonicalize(String callingPkg, Uri uri) {
            if (enforceReadPermission(callingPkg, uri) == 0) {
                String original = ContentProvider.this.setCallingPackage(callingPkg);
                try {
                    Uri uncanonicalize = ContentProvider.this.uncanonicalize(uri);
                    ContentProvider.this.setCallingPackage(original);
                    return uncanonicalize;
                } catch (Throwable th) {
                    ContentProvider.this.setCallingPackage(original);
                    throw th;
                }
            }
            return null;
        }

        private void enforceFilePermission(String callingPkg, Uri uri, String mode) throws FileNotFoundException, SecurityException {
            if (mode != null && mode.indexOf(119) != -1) {
                if (enforceWritePermission(callingPkg, uri) != 0) {
                    throw new FileNotFoundException("App op not allowed");
                }
            } else if (enforceReadPermission(callingPkg, uri) != 0) {
                throw new FileNotFoundException("App op not allowed");
            }
        }

        private int enforceReadPermission(String callingPkg, Uri uri) throws SecurityException {
            enforceReadPermissionInner(uri);
            if (this.mReadOp != -1) {
                return this.mAppOpsManager.noteOp(this.mReadOp, Binder.getCallingUid(), callingPkg);
            }
            return 0;
        }

        private void enforceReadPermissionInner(Uri uri) throws SecurityException {
            Context context = ContentProvider.this.getContext();
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            String missingPerm = null;
            if (!UserHandle.isSameApp(uid, ContentProvider.this.mMyUid)) {
                if (ContentProvider.this.mExported) {
                    String componentPerm = ContentProvider.this.getReadPermission();
                    if (componentPerm != null) {
                        if (context.checkPermission(componentPerm, pid, uid) == 0) {
                            return;
                        }
                        missingPerm = componentPerm;
                    }
                    boolean allowDefaultRead = componentPerm == null;
                    PathPermission[] pps = ContentProvider.this.getPathPermissions();
                    if (pps != null) {
                        String path = uri.getPath();
                        for (PathPermission pp : pps) {
                            String pathPerm = pp.getReadPermission();
                            if (pathPerm != null && pp.match(path)) {
                                if (context.checkPermission(pathPerm, pid, uid) == 0) {
                                    return;
                                }
                                allowDefaultRead = false;
                                missingPerm = pathPerm;
                            }
                        }
                    }
                    if (allowDefaultRead) {
                        return;
                    }
                }
                if (context.checkUriPermission(uri, pid, uid, 1) != 0) {
                    String failReason = ContentProvider.this.mExported ? " requires " + missingPerm + ", or grantUriPermission()" : " requires the provider be exported, or grantUriPermission()";
                    throw new SecurityException("Permission Denial: reading " + ContentProvider.this.getClass().getName() + " uri " + uri + " from pid=" + pid + ", uid=" + uid + failReason);
                }
            }
        }

        private int enforceWritePermission(String callingPkg, Uri uri) throws SecurityException {
            enforceWritePermissionInner(uri);
            if (this.mWriteOp != -1) {
                return this.mAppOpsManager.noteOp(this.mWriteOp, Binder.getCallingUid(), callingPkg);
            }
            return 0;
        }

        private void enforceWritePermissionInner(Uri uri) throws SecurityException {
            Context context = ContentProvider.this.getContext();
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            String missingPerm = null;
            if (!UserHandle.isSameApp(uid, ContentProvider.this.mMyUid)) {
                if (ContentProvider.this.mExported) {
                    String componentPerm = ContentProvider.this.getWritePermission();
                    if (componentPerm != null) {
                        if (context.checkPermission(componentPerm, pid, uid) == 0) {
                            return;
                        }
                        missingPerm = componentPerm;
                    }
                    boolean allowDefaultWrite = componentPerm == null;
                    PathPermission[] pps = ContentProvider.this.getPathPermissions();
                    if (pps != null) {
                        String path = uri.getPath();
                        for (PathPermission pp : pps) {
                            String pathPerm = pp.getWritePermission();
                            if (pathPerm != null && pp.match(path)) {
                                if (context.checkPermission(pathPerm, pid, uid) == 0) {
                                    return;
                                }
                                allowDefaultWrite = false;
                                missingPerm = pathPerm;
                            }
                        }
                    }
                    if (allowDefaultWrite) {
                        return;
                    }
                }
                if (context.checkUriPermission(uri, pid, uid, 2) != 0) {
                    String failReason = ContentProvider.this.mExported ? " requires " + missingPerm + ", or grantUriPermission()" : " requires the provider be exported, or grantUriPermission()";
                    throw new SecurityException("Permission Denial: writing " + ContentProvider.this.getClass().getName() + " uri " + uri + " from pid=" + pid + ", uid=" + uid + failReason);
                }
            }
        }
    }

    public final Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String setCallingPackage(String callingPackage) {
        String original = this.mCallingPackage.get();
        this.mCallingPackage.set(callingPackage);
        return original;
    }

    public final String getCallingPackage() {
        String pkg = this.mCallingPackage.get();
        if (pkg != null) {
            this.mTransport.mAppOpsManager.checkPackage(Binder.getCallingUid(), pkg);
        }
        return pkg;
    }

    protected final void setReadPermission(String permission) {
        this.mReadPermission = permission;
    }

    public final String getReadPermission() {
        return this.mReadPermission;
    }

    protected final void setWritePermission(String permission) {
        this.mWritePermission = permission;
    }

    public final String getWritePermission() {
        return this.mWritePermission;
    }

    protected final void setPathPermissions(PathPermission[] permissions) {
        this.mPathPermissions = permissions;
    }

    public final PathPermission[] getPathPermissions() {
        return this.mPathPermissions;
    }

    public final void setAppOps(int readOp, int writeOp) {
        if (!this.mNoPerms) {
            this.mTransport.mReadOp = readOp;
            this.mTransport.mWriteOp = writeOp;
        }
    }

    public AppOpsManager getAppOpsManager() {
        return this.mTransport.mAppOpsManager;
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
    }

    public Cursor rejectQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        String selection2;
        if (selection == null || selection.isEmpty()) {
            selection2 = "'A' = 'B'";
        } else {
            selection2 = "'A' = 'B' AND (" + selection + Separators.RPAREN;
        }
        return query(uri, projection, selection2, selectionArgs, sortOrder, cancellationSignal);
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        return query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public Uri canonicalize(Uri url) {
        return null;
    }

    public Uri uncanonicalize(Uri url) {
        return url;
    }

    public Uri rejectInsert(Uri uri, ContentValues values) {
        return uri.buildUpon().appendPath("0").build();
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numValues = values.length;
        for (ContentValues contentValues : values) {
            insert(uri, contentValues);
        }
        return numValues;
    }

    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        throw new FileNotFoundException("No files supported by provider at " + uri);
    }

    public ParcelFileDescriptor openFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
        return openFile(uri, mode);
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        ParcelFileDescriptor fd = openFile(uri, mode);
        if (fd != null) {
            return new AssetFileDescriptor(fd, 0L, -1L);
        }
        return null;
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
        return openAssetFile(uri, mode);
    }

    protected final ParcelFileDescriptor openFileHelper(Uri uri, String mode) throws FileNotFoundException {
        Cursor c = query(uri, new String[]{"_data"}, null, null, null);
        int count = c != null ? c.getCount() : 0;
        if (count != 1) {
            if (c != null) {
                c.close();
            }
            if (count == 0) {
                throw new FileNotFoundException("No entry for " + uri);
            }
            throw new FileNotFoundException("Multiple items at " + uri);
        }
        c.moveToFirst();
        int i = c.getColumnIndex("_data");
        String path = i >= 0 ? c.getString(i) : null;
        c.close();
        if (path == null) {
            throw new FileNotFoundException("Column _data not found.");
        }
        int modeBits = ParcelFileDescriptor.parseMode(mode);
        return ParcelFileDescriptor.open(new File(path), modeBits);
    }

    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        return null;
    }

    public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts) throws FileNotFoundException {
        if ("*/*".equals(mimeTypeFilter)) {
            return openAssetFile(uri, FullBackup.ROOT_TREE_TOKEN);
        }
        String baseType = getType(uri);
        if (baseType != null && ClipDescription.compareMimeTypes(baseType, mimeTypeFilter)) {
            return openAssetFile(uri, FullBackup.ROOT_TREE_TOKEN);
        }
        throw new FileNotFoundException("Can't open " + uri + " as type " + mimeTypeFilter);
    }

    public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts, CancellationSignal signal) throws FileNotFoundException {
        return openTypedAssetFile(uri, mimeTypeFilter, opts);
    }

    public <T> ParcelFileDescriptor openPipeHelper(final Uri uri, final String mimeType, final Bundle opts, final T args, final PipeDataWriter<T> func) throws FileNotFoundException {
        try {
            final ParcelFileDescriptor[] fds = ParcelFileDescriptor.createPipe();
            AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() { // from class: android.content.ContentProvider.1
                @Override // android.os.AsyncTask
                protected Object doInBackground(Object... params) {
                    func.writeDataToPipe(fds[1], uri, mimeType, opts, args);
                    try {
                        fds[1].close();
                        return null;
                    } catch (IOException e) {
                        Log.w(ContentProvider.TAG, "Failure closing pipe", e);
                        return null;
                    }
                }
            };
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            return fds[0];
        } catch (IOException e) {
            throw new FileNotFoundException("failure making pipe");
        }
    }

    protected boolean isTemporary() {
        return false;
    }

    public IContentProvider getIContentProvider() {
        return this.mTransport;
    }

    public void attachInfoForTesting(Context context, ProviderInfo info) {
        attachInfo(context, info, true);
    }

    public void attachInfo(Context context, ProviderInfo info) {
        attachInfo(context, info, false);
    }

    private void attachInfo(Context context, ProviderInfo info, boolean testing) {
        AsyncTask.init();
        this.mNoPerms = testing;
        if (this.mContext == null) {
            this.mContext = context;
            if (context != null) {
                this.mTransport.mAppOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            }
            this.mMyUid = Process.myUid();
            if (info != null) {
                setReadPermission(info.readPermission);
                setWritePermission(info.writePermission);
                setPathPermissions(info.pathPermissions);
                this.mExported = info.exported;
            }
            onCreate();
        }
    }

    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        int numOperations = operations.size();
        ContentProviderResult[] results = new ContentProviderResult[numOperations];
        for (int i = 0; i < numOperations; i++) {
            results[i] = operations.get(i).apply(this, results, i);
        }
        return results;
    }

    public Bundle call(String method, String arg, Bundle extras) {
        return null;
    }

    public void shutdown() {
        Log.w(TAG, "implement ContentProvider shutdown() to make sure all database connections are gracefully shutdown");
    }

    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.println("nothing to dump");
    }
}