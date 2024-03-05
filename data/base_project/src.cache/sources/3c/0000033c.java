package android.content;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import gov.nist.core.Separators;

/* loaded from: SearchRecentSuggestionsProvider.class */
public class SearchRecentSuggestionsProvider extends ContentProvider {
    private static final String TAG = "SuggestionsProvider";
    private String mAuthority;
    private int mMode;
    private boolean mTwoLineDisplay;
    private SQLiteOpenHelper mOpenHelper;
    private static final String sDatabaseName = "suggestions.db";
    private static final String sSuggestions = "suggestions";
    private static final String ORDER_BY = "date DESC";
    private static final String NULL_COLUMN = "query";
    private static final int DATABASE_VERSION = 512;
    public static final int DATABASE_MODE_QUERIES = 1;
    public static final int DATABASE_MODE_2LINES = 2;
    private static final int URI_MATCH_SUGGEST = 1;
    private Uri mSuggestionsUri;
    private UriMatcher mUriMatcher;
    private String mSuggestSuggestionClause;
    private String[] mSuggestionProjection;

    /* loaded from: SearchRecentSuggestionsProvider$DatabaseHelper.class */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private int mNewVersion;

        public DatabaseHelper(Context context, int newVersion) {
            super(context, SearchRecentSuggestionsProvider.sDatabaseName, null, newVersion);
            this.mNewVersion = newVersion;
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE suggestions (_id INTEGER PRIMARY KEY,display1 TEXT UNIQUE ON CONFLICT REPLACE");
            if (0 != (this.mNewVersion & 2)) {
                builder.append(",display2 TEXT");
            }
            builder.append(",query TEXT,date LONG);");
            db.execSQL(builder.toString());
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(SearchRecentSuggestionsProvider.TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS suggestions");
            onCreate(db);
        }
    }

    protected void setupSuggestions(String authority, int mode) {
        if (TextUtils.isEmpty(authority) || (mode & 1) == 0) {
            throw new IllegalArgumentException();
        }
        this.mTwoLineDisplay = 0 != (mode & 2);
        this.mAuthority = new String(authority);
        this.mMode = mode;
        this.mSuggestionsUri = Uri.parse("content://" + this.mAuthority + "/suggestions");
        this.mUriMatcher = new UriMatcher(-1);
        this.mUriMatcher.addURI(this.mAuthority, SearchManager.SUGGEST_URI_PATH_QUERY, 1);
        if (this.mTwoLineDisplay) {
            this.mSuggestSuggestionClause = "display1 LIKE ? OR display2 LIKE ?";
            this.mSuggestionProjection = new String[]{"0 AS suggest_format", "'android.resource://system/17301578' AS suggest_icon_1", "display1 AS suggest_text_1", "display2 AS suggest_text_2", "query AS suggest_intent_query", "_id"};
            return;
        }
        this.mSuggestSuggestionClause = "display1 LIKE ?";
        this.mSuggestionProjection = new String[]{"0 AS suggest_format", "'android.resource://system/17301578' AS suggest_icon_1", "display1 AS suggest_text_1", "query AS suggest_intent_query", "_id"};
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        int length = uri.getPathSegments().size();
        if (length != 1) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        String base = uri.getPathSegments().get(0);
        if (base.equals("suggestions")) {
            int count = db.delete("suggestions", selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
        throw new IllegalArgumentException("Unknown Uri");
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        if (this.mUriMatcher.match(uri) == 1) {
            return SearchManager.SUGGEST_MIME_TYPE;
        }
        int length = uri.getPathSegments().size();
        if (length >= 1) {
            String base = uri.getPathSegments().get(0);
            if (base.equals("suggestions")) {
                if (length == 1) {
                    return "vnd.android.cursor.dir/suggestion";
                }
                if (length == 2) {
                    return "vnd.android.cursor.item/suggestion";
                }
            }
        }
        throw new IllegalArgumentException("Unknown Uri");
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        int length = uri.getPathSegments().size();
        if (length < 1) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        long rowID = -1;
        String base = uri.getPathSegments().get(0);
        Uri newUri = null;
        if (base.equals("suggestions") && length == 1) {
            rowID = db.insert("suggestions", "query", values);
            if (rowID > 0) {
                newUri = Uri.withAppendedPath(this.mSuggestionsUri, String.valueOf(rowID));
            }
        }
        if (rowID < 0) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        if (this.mAuthority == null || this.mMode == 0) {
            throw new IllegalArgumentException("Provider not configured");
        }
        int mWorkingDbVersion = 512 + this.mMode;
        this.mOpenHelper = new DatabaseHelper(getContext(), mWorkingDbVersion);
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String[] myArgs;
        String suggestSelection;
        SQLiteDatabase db = this.mOpenHelper.getReadableDatabase();
        if (this.mUriMatcher.match(uri) == 1) {
            if (TextUtils.isEmpty(selectionArgs[0])) {
                suggestSelection = null;
                myArgs = null;
            } else {
                String like = Separators.PERCENT + selectionArgs[0] + Separators.PERCENT;
                myArgs = this.mTwoLineDisplay ? new String[]{like, like} : new String[]{like};
                suggestSelection = this.mSuggestSuggestionClause;
            }
            Cursor c = db.query("suggestions", this.mSuggestionProjection, suggestSelection, myArgs, null, null, "date DESC", null);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }
        int length = uri.getPathSegments().size();
        if (length != 1 && length != 2) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        String base = uri.getPathSegments().get(0);
        if (!base.equals("suggestions")) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        String[] useProjection = null;
        if (projection != null && projection.length > 0) {
            useProjection = new String[projection.length + 1];
            System.arraycopy(projection, 0, useProjection, 0, projection.length);
            useProjection[projection.length] = "_id AS _id";
        }
        StringBuilder whereClause = new StringBuilder(256);
        if (length == 2) {
            whereClause.append("(_id = ").append(uri.getPathSegments().get(1)).append(Separators.RPAREN);
        }
        if (selection != null && selection.length() > 0) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append('(');
            whereClause.append(selection);
            whereClause.append(')');
        }
        Cursor c2 = db.query(base, useProjection, whereClause.toString(), selectionArgs, null, null, sortOrder, null);
        c2.setNotificationUri(getContext().getContentResolver(), uri);
        return c2;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }
}