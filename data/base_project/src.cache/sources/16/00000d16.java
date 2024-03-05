package android.provider;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import java.io.FileNotFoundException;
import libcore.io.IoUtils;

/* loaded from: DocumentsProvider.class */
public abstract class DocumentsProvider extends ContentProvider {
    private static final String TAG = "DocumentsProvider";
    private static final int MATCH_ROOTS = 1;
    private static final int MATCH_ROOT = 2;
    private static final int MATCH_RECENT = 3;
    private static final int MATCH_SEARCH = 4;
    private static final int MATCH_DOCUMENT = 5;
    private static final int MATCH_CHILDREN = 6;
    private String mAuthority;
    private UriMatcher mMatcher;

    public abstract Cursor queryRoots(String[] strArr) throws FileNotFoundException;

    public abstract Cursor queryDocument(String str, String[] strArr) throws FileNotFoundException;

    public abstract Cursor queryChildDocuments(String str, String[] strArr, String str2) throws FileNotFoundException;

    public abstract ParcelFileDescriptor openDocument(String str, String str2, CancellationSignal cancellationSignal) throws FileNotFoundException;

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo info) {
        this.mAuthority = info.authority;
        this.mMatcher = new UriMatcher(-1);
        this.mMatcher.addURI(this.mAuthority, "root", 1);
        this.mMatcher.addURI(this.mAuthority, "root/*", 2);
        this.mMatcher.addURI(this.mAuthority, "root/*/recent", 3);
        this.mMatcher.addURI(this.mAuthority, "root/*/search", 4);
        this.mMatcher.addURI(this.mAuthority, "document/*", 5);
        this.mMatcher.addURI(this.mAuthority, "document/*/children", 6);
        if (!info.exported) {
            throw new SecurityException("Provider must be exported");
        }
        if (!info.grantUriPermissions) {
            throw new SecurityException("Provider must grantUriPermissions");
        }
        if (!Manifest.permission.MANAGE_DOCUMENTS.equals(info.readPermission) || !Manifest.permission.MANAGE_DOCUMENTS.equals(info.writePermission)) {
            throw new SecurityException("Provider must be protected by MANAGE_DOCUMENTS");
        }
        super.attachInfo(context, info);
    }

    public String createDocument(String parentDocumentId, String mimeType, String displayName) throws FileNotFoundException {
        throw new UnsupportedOperationException("Create not supported");
    }

    public void deleteDocument(String documentId) throws FileNotFoundException {
        throw new UnsupportedOperationException("Delete not supported");
    }

    public Cursor queryRecentDocuments(String rootId, String[] projection) throws FileNotFoundException {
        throw new UnsupportedOperationException("Recent not supported");
    }

    public Cursor queryChildDocumentsForManage(String parentDocumentId, String[] projection, String sortOrder) throws FileNotFoundException {
        throw new UnsupportedOperationException("Manage not supported");
    }

    public Cursor querySearchDocuments(String rootId, String query, String[] projection) throws FileNotFoundException {
        throw new UnsupportedOperationException("Search not supported");
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.AutoCloseable, android.database.Cursor] */
    public String getDocumentType(String documentId) throws FileNotFoundException {
        ?? queryDocument = queryDocument(documentId, null);
        try {
            if (queryDocument.moveToFirst()) {
                String string = queryDocument.getString(queryDocument.getColumnIndexOrThrow("mime_type"));
                IoUtils.closeQuietly((AutoCloseable) queryDocument);
                return string;
            }
            return null;
        } finally {
            IoUtils.closeQuietly((AutoCloseable) queryDocument);
        }
    }

    public AssetFileDescriptor openDocumentThumbnail(String documentId, Point sizeHint, CancellationSignal signal) throws FileNotFoundException {
        throw new UnsupportedOperationException("Thumbnails not supported");
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        try {
            switch (this.mMatcher.match(uri)) {
                case 1:
                    return queryRoots(projection);
                case 2:
                default:
                    throw new UnsupportedOperationException("Unsupported Uri " + uri);
                case 3:
                    return queryRecentDocuments(DocumentsContract.getRootId(uri), projection);
                case 4:
                    return querySearchDocuments(DocumentsContract.getRootId(uri), DocumentsContract.getSearchDocumentsQuery(uri), projection);
                case 5:
                    return queryDocument(DocumentsContract.getDocumentId(uri), projection);
                case 6:
                    if (DocumentsContract.isManageMode(uri)) {
                        return queryChildDocumentsForManage(DocumentsContract.getDocumentId(uri), projection, sortOrder);
                    }
                    return queryChildDocuments(DocumentsContract.getDocumentId(uri), projection, sortOrder);
            }
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Failed during query", e);
            return null;
        }
    }

    @Override // android.content.ContentProvider
    public final String getType(Uri uri) {
        try {
            switch (this.mMatcher.match(uri)) {
                case 2:
                    return DocumentsContract.Root.MIME_TYPE_ITEM;
                case 5:
                    return getDocumentType(DocumentsContract.getDocumentId(uri));
                default:
                    return null;
            }
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Failed during getType", e);
            return null;
        }
    }

    @Override // android.content.ContentProvider
    public final Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Insert not supported");
    }

    @Override // android.content.ContentProvider
    public final int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Delete not supported");
    }

    @Override // android.content.ContentProvider
    public final int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Update not supported");
    }

    @Override // android.content.ContentProvider
    public Bundle call(String method, String arg, Bundle extras) {
        Context context = getContext();
        if (!method.startsWith("android:")) {
            return super.call(method, arg, extras);
        }
        String documentId = extras.getString("document_id");
        Uri documentUri = DocumentsContract.buildDocumentUri(this.mAuthority, documentId);
        boolean callerHasManage = context.checkCallingOrSelfPermission(Manifest.permission.MANAGE_DOCUMENTS) == 0;
        if (!callerHasManage) {
            getContext().enforceCallingOrSelfUriPermission(documentUri, 2, method);
        }
        Bundle out = new Bundle();
        try {
            if (DocumentsContract.METHOD_CREATE_DOCUMENT.equals(method)) {
                String mimeType = extras.getString("mime_type");
                String displayName = extras.getString("_display_name");
                String newDocumentId = createDocument(documentId, mimeType, displayName);
                out.putString("document_id", newDocumentId);
                if (!callerHasManage) {
                    Uri newDocumentUri = DocumentsContract.buildDocumentUri(this.mAuthority, newDocumentId);
                    context.grantUriPermission(getCallingPackage(), newDocumentUri, 67);
                }
            } else if (DocumentsContract.METHOD_DELETE_DOCUMENT.equals(method)) {
                deleteDocument(documentId);
                context.revokeUriPermission(documentUri, 67);
            } else {
                throw new UnsupportedOperationException("Method not supported " + method);
            }
            return out;
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Failed call " + method, e);
        }
    }

    @Override // android.content.ContentProvider
    public final ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        return openDocument(DocumentsContract.getDocumentId(uri), mode, null);
    }

    @Override // android.content.ContentProvider
    public final ParcelFileDescriptor openFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
        return openDocument(DocumentsContract.getDocumentId(uri), mode, signal);
    }

    @Override // android.content.ContentProvider
    public final AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts) throws FileNotFoundException {
        if (opts != null && opts.containsKey(DocumentsContract.EXTRA_THUMBNAIL_SIZE)) {
            Point sizeHint = (Point) opts.getParcelable(DocumentsContract.EXTRA_THUMBNAIL_SIZE);
            return openDocumentThumbnail(DocumentsContract.getDocumentId(uri), sizeHint, null);
        }
        return super.openTypedAssetFile(uri, mimeTypeFilter, opts);
    }

    @Override // android.content.ContentProvider
    public final AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts, CancellationSignal signal) throws FileNotFoundException {
        if (opts != null && opts.containsKey(DocumentsContract.EXTRA_THUMBNAIL_SIZE)) {
            Point sizeHint = (Point) opts.getParcelable(DocumentsContract.EXTRA_THUMBNAIL_SIZE);
            return openDocumentThumbnail(DocumentsContract.getDocumentId(uri), sizeHint, signal);
        }
        return super.openTypedAssetFile(uri, mimeTypeFilter, opts, signal);
    }
}