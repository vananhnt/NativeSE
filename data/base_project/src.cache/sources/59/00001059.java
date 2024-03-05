package android.support.v4.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;

/* loaded from: DocumentsContractApi19.class */
class DocumentsContractApi19 {
    private static final String TAG = "DocumentFile";

    DocumentsContractApi19() {
    }

    public static boolean canRead(Context context, Uri uri) {
        return context.checkCallingOrSelfUriPermission(uri, 1) == 0 && !TextUtils.isEmpty(getRawType(context, uri));
    }

    public static boolean canWrite(Context context, Uri uri) {
        if (context.checkCallingOrSelfUriPermission(uri, 2) != 0) {
            return false;
        }
        String rawType = getRawType(context, uri);
        int queryForInt = queryForInt(context, uri, "flags", 0);
        if (TextUtils.isEmpty(rawType)) {
            return false;
        }
        if ((queryForInt & 4) != 0) {
            return true;
        }
        if (!DocumentsContract.Document.MIME_TYPE_DIR.equals(rawType) || (queryForInt & 8) == 0) {
            return (TextUtils.isEmpty(rawType) || (queryForInt & 2) == 0) ? false : true;
        }
        return true;
    }

    private static void closeQuietly(AutoCloseable autoCloseable) {
        if (autoCloseable != null) {
            try {
                autoCloseable.close();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e2) {
            }
        }
    }

    public static boolean delete(Context context, Uri uri) {
        return DocumentsContract.deleteDocument(context.getContentResolver(), uri);
    }

    /* JADX WARN: Type inference failed for: r0v26, types: [java.lang.AutoCloseable, android.database.Cursor] */
    public static boolean exists(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        AutoCloseable autoCloseable = null;
        AutoCloseable autoCloseable2 = null;
        boolean z = false;
        try {
            try {
                ?? query = contentResolver.query(uri, new String[]{"document_id"}, null, null, null);
                autoCloseable2 = query;
                autoCloseable = query;
                if (query.getCount() > 0) {
                    z = true;
                }
                closeQuietly(query);
                return z;
            } catch (Exception e) {
                AutoCloseable autoCloseable3 = autoCloseable;
                StringBuilder sb = new StringBuilder();
                AutoCloseable autoCloseable4 = autoCloseable;
                sb.append("Failed query: ");
                AutoCloseable autoCloseable5 = autoCloseable;
                sb.append(e);
                AutoCloseable autoCloseable6 = autoCloseable;
                autoCloseable2 = autoCloseable;
                Log.w(TAG, sb.toString());
                closeQuietly(autoCloseable);
                return false;
            }
        } catch (Throwable th) {
            closeQuietly(autoCloseable2);
            throw th;
        }
    }

    public static String getName(Context context, Uri uri) {
        return queryForString(context, uri, "_display_name", null);
    }

    private static String getRawType(Context context, Uri uri) {
        return queryForString(context, uri, "mime_type", null);
    }

    public static String getType(Context context, Uri uri) {
        String rawType = getRawType(context, uri);
        if (DocumentsContract.Document.MIME_TYPE_DIR.equals(rawType)) {
            return null;
        }
        return rawType;
    }

    public static boolean isDirectory(Context context, Uri uri) {
        return DocumentsContract.Document.MIME_TYPE_DIR.equals(getRawType(context, uri));
    }

    public static boolean isDocumentUri(Context context, Uri uri) {
        return DocumentsContract.isDocumentUri(context, uri);
    }

    public static boolean isFile(Context context, Uri uri) {
        String rawType = getRawType(context, uri);
        return (DocumentsContract.Document.MIME_TYPE_DIR.equals(rawType) || TextUtils.isEmpty(rawType)) ? false : true;
    }

    public static long lastModified(Context context, Uri uri) {
        return queryForLong(context, uri, DocumentsContract.Document.COLUMN_LAST_MODIFIED, 0L);
    }

    public static long length(Context context, Uri uri) {
        return queryForLong(context, uri, "_size", 0L);
    }

    private static int queryForInt(Context context, Uri uri, String str, int i) {
        return (int) queryForLong(context, uri, str, i);
    }

    /* JADX WARN: Type inference failed for: r0v22, types: [java.lang.AutoCloseable, android.database.Cursor] */
    private static long queryForLong(Context context, Uri uri, String str, long j) {
        ContentResolver contentResolver = context.getContentResolver();
        AutoCloseable autoCloseable = null;
        AutoCloseable autoCloseable2 = null;
        try {
            try {
                ?? query = contentResolver.query(uri, new String[]{str}, null, null, null);
                if (!query.moveToFirst() || query.isNull(0)) {
                    closeQuietly(query);
                    return j;
                }
                autoCloseable2 = query;
                autoCloseable = query;
                long j2 = query.getLong(0);
                closeQuietly(query);
                return j2;
            } catch (Exception e) {
                AutoCloseable autoCloseable3 = autoCloseable;
                StringBuilder sb = new StringBuilder();
                AutoCloseable autoCloseable4 = autoCloseable;
                sb.append("Failed query: ");
                AutoCloseable autoCloseable5 = autoCloseable;
                sb.append(e);
                AutoCloseable autoCloseable6 = autoCloseable;
                Log.w(TAG, sb.toString());
                closeQuietly(autoCloseable);
                return j;
            }
        } catch (Throwable th) {
            closeQuietly(autoCloseable2);
            throw th;
        }
    }

    /* JADX WARN: Type inference failed for: r0v22, types: [java.lang.AutoCloseable, android.database.Cursor] */
    private static String queryForString(Context context, Uri uri, String str, String str2) {
        ContentResolver contentResolver = context.getContentResolver();
        AutoCloseable autoCloseable = null;
        AutoCloseable autoCloseable2 = null;
        try {
            try {
                ?? query = contentResolver.query(uri, new String[]{str}, null, null, null);
                if (!query.moveToFirst() || query.isNull(0)) {
                    closeQuietly(query);
                    return str2;
                }
                autoCloseable2 = query;
                autoCloseable = query;
                String string = query.getString(0);
                closeQuietly(query);
                return string;
            } catch (Exception e) {
                AutoCloseable autoCloseable3 = autoCloseable;
                StringBuilder sb = new StringBuilder();
                AutoCloseable autoCloseable4 = autoCloseable;
                sb.append("Failed query: ");
                AutoCloseable autoCloseable5 = autoCloseable;
                sb.append(e);
                AutoCloseable autoCloseable6 = autoCloseable;
                Log.w(TAG, sb.toString());
                closeQuietly(autoCloseable);
                return str2;
            }
        } catch (Throwable th) {
            closeQuietly(autoCloseable2);
            throw th;
        }
    }
}