package android.provider;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.R;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import libcore.io.ErrnoException;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.io.OsConstants;

/* loaded from: DocumentsContract.class */
public final class DocumentsContract {
    private static final String TAG = "Documents";
    public static final String PROVIDER_INTERFACE = "android.content.action.DOCUMENTS_PROVIDER";
    public static final String EXTRA_PACKAGE_NAME = "android.content.extra.PACKAGE_NAME";
    public static final String EXTRA_ORIENTATION = "android.content.extra.ORIENTATION";
    public static final String ACTION_MANAGE_ROOT = "android.provider.action.MANAGE_ROOT";
    public static final String ACTION_MANAGE_DOCUMENT = "android.provider.action.MANAGE_DOCUMENT";
    private static final int THUMBNAIL_BUFFER_SIZE = 131072;
    public static final String EXTRA_LOADING = "loading";
    public static final String EXTRA_INFO = "info";
    public static final String EXTRA_ERROR = "error";
    public static final String METHOD_CREATE_DOCUMENT = "android:createDocument";
    public static final String METHOD_DELETE_DOCUMENT = "android:deleteDocument";
    public static final String EXTRA_THUMBNAIL_SIZE = "thumbnail_size";
    private static final String PATH_ROOT = "root";
    private static final String PATH_RECENT = "recent";
    private static final String PATH_DOCUMENT = "document";
    private static final String PATH_CHILDREN = "children";
    private static final String PATH_SEARCH = "search";
    private static final String PARAM_QUERY = "query";
    private static final String PARAM_MANAGE = "manage";

    private DocumentsContract() {
    }

    /* loaded from: DocumentsContract$Document.class */
    public static final class Document {
        public static final String COLUMN_DOCUMENT_ID = "document_id";
        public static final String COLUMN_MIME_TYPE = "mime_type";
        public static final String COLUMN_DISPLAY_NAME = "_display_name";
        public static final String COLUMN_SUMMARY = "summary";
        public static final String COLUMN_LAST_MODIFIED = "last_modified";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_FLAGS = "flags";
        public static final String COLUMN_SIZE = "_size";
        public static final String MIME_TYPE_DIR = "vnd.android.document/directory";
        public static final int FLAG_SUPPORTS_THUMBNAIL = 1;
        public static final int FLAG_SUPPORTS_WRITE = 2;
        public static final int FLAG_SUPPORTS_DELETE = 4;
        public static final int FLAG_DIR_SUPPORTS_CREATE = 8;
        public static final int FLAG_DIR_PREFERS_GRID = 16;
        public static final int FLAG_DIR_PREFERS_LAST_MODIFIED = 32;
        public static final int FLAG_DIR_HIDE_GRID_TITLES = 65536;

        private Document() {
        }
    }

    /* loaded from: DocumentsContract$Root.class */
    public static final class Root {
        public static final String COLUMN_ROOT_ID = "root_id";
        public static final String COLUMN_FLAGS = "flags";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SUMMARY = "summary";
        public static final String COLUMN_DOCUMENT_ID = "document_id";
        public static final String COLUMN_AVAILABLE_BYTES = "available_bytes";
        public static final String COLUMN_MIME_TYPES = "mime_types";
        public static final String MIME_TYPE_ITEM = "vnd.android.document/root";
        public static final int FLAG_SUPPORTS_CREATE = 1;
        public static final int FLAG_LOCAL_ONLY = 2;
        public static final int FLAG_SUPPORTS_RECENTS = 4;
        public static final int FLAG_SUPPORTS_SEARCH = 8;
        public static final int FLAG_EMPTY = 65536;
        public static final int FLAG_ADVANCED = 131072;

        private Root() {
        }
    }

    public static Uri buildRootsUri(String authority) {
        return new Uri.Builder().scheme("content").authority(authority).appendPath(PATH_ROOT).build();
    }

    public static Uri buildRootUri(String authority, String rootId) {
        return new Uri.Builder().scheme("content").authority(authority).appendPath(PATH_ROOT).appendPath(rootId).build();
    }

    public static Uri buildRecentDocumentsUri(String authority, String rootId) {
        return new Uri.Builder().scheme("content").authority(authority).appendPath(PATH_ROOT).appendPath(rootId).appendPath(PATH_RECENT).build();
    }

    public static Uri buildDocumentUri(String authority, String documentId) {
        return new Uri.Builder().scheme("content").authority(authority).appendPath(PATH_DOCUMENT).appendPath(documentId).build();
    }

    public static Uri buildChildDocumentsUri(String authority, String parentDocumentId) {
        return new Uri.Builder().scheme("content").authority(authority).appendPath(PATH_DOCUMENT).appendPath(parentDocumentId).appendPath(PATH_CHILDREN).build();
    }

    public static Uri buildSearchDocumentsUri(String authority, String rootId, String query) {
        return new Uri.Builder().scheme("content").authority(authority).appendPath(PATH_ROOT).appendPath(rootId).appendPath("search").appendQueryParameter("query", query).build();
    }

    public static boolean isDocumentUri(Context context, Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths.size() < 2 || !PATH_DOCUMENT.equals(paths.get(0))) {
            return false;
        }
        Intent intent = new Intent(PROVIDER_INTERFACE);
        List<ResolveInfo> infos = context.getPackageManager().queryIntentContentProviders(intent, 0);
        for (ResolveInfo info : infos) {
            if (uri.getAuthority().equals(info.providerInfo.authority)) {
                return true;
            }
        }
        return false;
    }

    public static String getRootId(Uri rootUri) {
        List<String> paths = rootUri.getPathSegments();
        if (paths.size() < 2) {
            throw new IllegalArgumentException("Not a root: " + rootUri);
        }
        if (!PATH_ROOT.equals(paths.get(0))) {
            throw new IllegalArgumentException("Not a root: " + rootUri);
        }
        return paths.get(1);
    }

    public static String getDocumentId(Uri documentUri) {
        List<String> paths = documentUri.getPathSegments();
        if (paths.size() < 2) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        return paths.get(1);
    }

    public static String getSearchDocumentsQuery(Uri searchDocumentsUri) {
        return searchDocumentsUri.getQueryParameter("query");
    }

    public static Uri setManageMode(Uri uri) {
        return uri.buildUpon().appendQueryParameter(PARAM_MANAGE, "true").build();
    }

    public static boolean isManageMode(Uri uri) {
        return uri.getBooleanQueryParameter(PARAM_MANAGE, false);
    }

    public static Bitmap getDocumentThumbnail(ContentResolver resolver, Uri documentUri, Point size, CancellationSignal signal) {
        ContentProviderClient client = resolver.acquireUnstableContentProviderClient(documentUri.getAuthority());
        try {
            try {
                Bitmap documentThumbnail = getDocumentThumbnail(client, documentUri, size, signal);
                ContentProviderClient.releaseQuietly(client);
                return documentThumbnail;
            } catch (Exception e) {
                Log.w(TAG, "Failed to load thumbnail for " + documentUri + ": " + e);
                ContentProviderClient.releaseQuietly(client);
                return null;
            }
        } catch (Throwable th) {
            ContentProviderClient.releaseQuietly(client);
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v0 */
    /* JADX WARN: Type inference failed for: r13v1 */
    /* JADX WARN: Type inference failed for: r13v2, types: [android.content.res.AssetFileDescriptor, java.lang.AutoCloseable] */
    public static Bitmap getDocumentThumbnail(ContentProviderClient client, Uri documentUri, Point size, CancellationSignal signal) throws RemoteException, IOException {
        Bitmap bitmap;
        Bundle openOpts = new Bundle();
        openOpts.putParcelable(EXTRA_THUMBNAIL_SIZE, size);
        ?? r13 = 0;
        try {
            r13 = client.openTypedAssetFileDescriptor(documentUri, "image/*", openOpts, signal);
            FileDescriptor fd = r13.getFileDescriptor();
            long offset = r13.getStartOffset();
            BufferedInputStream is = null;
            try {
                Libcore.os.lseek(fd, offset, OsConstants.SEEK_SET);
            } catch (ErrnoException e) {
                is = new BufferedInputStream(new FileInputStream(fd), 131072);
                is.mark(131072);
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            if (is != null) {
                BitmapFactory.decodeStream(is, null, opts);
            } else {
                BitmapFactory.decodeFileDescriptor(fd, null, opts);
            }
            int widthSample = opts.outWidth / size.x;
            int heightSample = opts.outHeight / size.y;
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = Math.min(widthSample, heightSample);
            if (is != null) {
                is.reset();
                bitmap = BitmapFactory.decodeStream(is, null, opts);
            } else {
                try {
                    Libcore.os.lseek(fd, offset, OsConstants.SEEK_SET);
                } catch (ErrnoException e2) {
                    e2.rethrowAsIOException();
                }
                bitmap = BitmapFactory.decodeFileDescriptor(fd, null, opts);
            }
            Bundle extras = r13.getExtras();
            int orientation = extras != null ? extras.getInt(EXTRA_ORIENTATION, 0) : 0;
            if (orientation != 0) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Matrix m = new Matrix();
                m.setRotate(orientation, width / 2, height / 2);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, false);
            }
            IoUtils.closeQuietly((AutoCloseable) r13);
            return bitmap;
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) r13);
            throw th;
        }
    }

    public static Uri createDocument(ContentResolver resolver, Uri parentDocumentUri, String mimeType, String displayName) {
        ContentProviderClient client = resolver.acquireUnstableContentProviderClient(parentDocumentUri.getAuthority());
        try {
            try {
                Uri createDocument = createDocument(client, parentDocumentUri, mimeType, displayName);
                ContentProviderClient.releaseQuietly(client);
                return createDocument;
            } catch (Exception e) {
                Log.w(TAG, "Failed to create document", e);
                ContentProviderClient.releaseQuietly(client);
                return null;
            }
        } catch (Throwable th) {
            ContentProviderClient.releaseQuietly(client);
            throw th;
        }
    }

    public static Uri createDocument(ContentProviderClient client, Uri parentDocumentUri, String mimeType, String displayName) throws RemoteException {
        Bundle in = new Bundle();
        in.putString("document_id", getDocumentId(parentDocumentUri));
        in.putString("mime_type", mimeType);
        in.putString("_display_name", displayName);
        Bundle out = client.call(METHOD_CREATE_DOCUMENT, null, in);
        return buildDocumentUri(parentDocumentUri.getAuthority(), out.getString("document_id"));
    }

    public static boolean deleteDocument(ContentResolver resolver, Uri documentUri) {
        ContentProviderClient client = resolver.acquireUnstableContentProviderClient(documentUri.getAuthority());
        try {
            try {
                deleteDocument(client, documentUri);
                ContentProviderClient.releaseQuietly(client);
                return true;
            } catch (Exception e) {
                Log.w(TAG, "Failed to delete document", e);
                ContentProviderClient.releaseQuietly(client);
                return false;
            }
        } catch (Throwable th) {
            ContentProviderClient.releaseQuietly(client);
            throw th;
        }
    }

    public static void deleteDocument(ContentProviderClient client, Uri documentUri) throws RemoteException {
        Bundle in = new Bundle();
        in.putString("document_id", getDocumentId(documentUri));
        client.call(METHOD_DELETE_DOCUMENT, null, in);
    }

    public static AssetFileDescriptor openImageThumbnail(File file) throws FileNotFoundException {
        ParcelFileDescriptor pfd = ParcelFileDescriptor.open(file, 268435456);
        Bundle extras = null;
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)) {
                case 3:
                    extras = new Bundle(1);
                    extras.putInt(EXTRA_ORIENTATION, 180);
                    break;
                case 6:
                    extras = new Bundle(1);
                    extras.putInt(EXTRA_ORIENTATION, 90);
                    break;
                case 8:
                    extras = new Bundle(1);
                    extras.putInt(EXTRA_ORIENTATION, R.styleable.Theme_findOnPagePreviousDrawable);
                    break;
            }
            long[] thumb = exif.getThumbnailRange();
            if (thumb != null) {
                return new AssetFileDescriptor(pfd, thumb[0], thumb[1], extras);
            }
        } catch (IOException e) {
        }
        return new AssetFileDescriptor(pfd, 0L, -1L, extras);
    }
}