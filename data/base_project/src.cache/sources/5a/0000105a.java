package android.support.v4.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: DocumentsContractApi21.class */
class DocumentsContractApi21 {
    private static final String TAG = "DocumentFile";

    DocumentsContractApi21() {
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

    public static Uri createDirectory(Context context, Uri uri, String str) {
        return createFile(context, uri, DocumentsContract.Document.MIME_TYPE_DIR, str);
    }

    public static Uri createFile(Context context, Uri uri, String str, String str2) {
        return DocumentsContract.createDocument(context.getContentResolver(), uri, str, str2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v23, types: [java.lang.AutoCloseable] */
    /* JADX WARN: Type inference failed for: r0v25, types: [java.lang.AutoCloseable] */
    public static Uri[] listFiles(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri buildChildDocumentsUriUsingTree = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getDocumentId(uri));
        ArrayList arrayList = new ArrayList();
        Cursor cursor = null;
        Cursor cursor2 = null;
        try {
            try {
                Cursor query = contentResolver.query(buildChildDocumentsUriUsingTree, new String[]{"document_id"}, null, null, null);
                while (true) {
                    cursor2 = query;
                    cursor = query;
                    if (!query.moveToNext()) {
                        break;
                    }
                    arrayList.add(DocumentsContract.buildDocumentUriUsingTree(uri, query.getString(0)));
                }
                cursor = query;
            } catch (Exception e) {
                Cursor cursor3 = cursor;
                StringBuilder sb = new StringBuilder();
                Cursor cursor4 = cursor;
                sb.append("Failed query: ");
                Cursor cursor5 = cursor;
                sb.append(e);
                Cursor cursor6 = cursor;
                String sb2 = sb.toString();
                Cursor cursor7 = cursor;
                Log.w(TAG, sb2);
            }
            return (Uri[]) arrayList.toArray(new Uri[arrayList.size()]);
        } finally {
            closeQuietly(cursor2);
        }
    }

    public static Uri prepareTreeUri(Uri uri) {
        return DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
    }

    public static Uri renameTo(Context context, Uri uri, String str) {
        return DocumentsContract.renameDocument(context.getContentResolver(), uri, str);
    }
}