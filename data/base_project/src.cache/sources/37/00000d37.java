package android.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import gov.nist.core.Separators;
import java.util.concurrent.Semaphore;

/* loaded from: SearchRecentSuggestions.class */
public class SearchRecentSuggestions {
    private static final String LOG_TAG = "SearchSuggestions";
    public static final int QUERIES_PROJECTION_DATE_INDEX = 1;
    public static final int QUERIES_PROJECTION_QUERY_INDEX = 2;
    public static final int QUERIES_PROJECTION_DISPLAY1_INDEX = 3;
    public static final int QUERIES_PROJECTION_DISPLAY2_INDEX = 4;
    private static final int MAX_HISTORY_COUNT = 250;
    private final Context mContext;
    private final String mAuthority;
    private final boolean mTwoLineDisplay;
    private final Uri mSuggestionsUri;
    public static final String[] QUERIES_PROJECTION_1LINE = {"_id", "date", "query", SuggestionColumns.DISPLAY1};
    public static final String[] QUERIES_PROJECTION_2LINE = {"_id", "date", "query", SuggestionColumns.DISPLAY1, SuggestionColumns.DISPLAY2};
    private static final Semaphore sWritesInProgress = new Semaphore(0);

    /* loaded from: SearchRecentSuggestions$SuggestionColumns.class */
    private static class SuggestionColumns implements BaseColumns {
        public static final String DISPLAY1 = "display1";
        public static final String DISPLAY2 = "display2";
        public static final String QUERY = "query";
        public static final String DATE = "date";

        private SuggestionColumns() {
        }
    }

    public SearchRecentSuggestions(Context context, String authority, int mode) {
        if (TextUtils.isEmpty(authority) || (mode & 1) == 0) {
            throw new IllegalArgumentException();
        }
        this.mTwoLineDisplay = 0 != (mode & 2);
        this.mContext = context;
        this.mAuthority = new String(authority);
        this.mSuggestionsUri = Uri.parse("content://" + this.mAuthority + "/suggestions");
    }

    public void saveRecentQuery(final String queryString, final String line2) {
        if (TextUtils.isEmpty(queryString)) {
            return;
        }
        if (!this.mTwoLineDisplay && !TextUtils.isEmpty(line2)) {
            throw new IllegalArgumentException();
        }
        new Thread("saveRecentQuery") { // from class: android.provider.SearchRecentSuggestions.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                SearchRecentSuggestions.this.saveRecentQueryBlocking(queryString, line2);
                SearchRecentSuggestions.sWritesInProgress.release();
            }
        }.start();
    }

    void waitForSave() {
        do {
            sWritesInProgress.acquireUninterruptibly();
        } while (sWritesInProgress.availablePermits() > 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveRecentQueryBlocking(String queryString, String line2) {
        ContentResolver cr = this.mContext.getContentResolver();
        long now = System.currentTimeMillis();
        try {
            ContentValues values = new ContentValues();
            values.put(SuggestionColumns.DISPLAY1, queryString);
            if (this.mTwoLineDisplay) {
                values.put(SuggestionColumns.DISPLAY2, line2);
            }
            values.put("query", queryString);
            values.put("date", Long.valueOf(now));
            cr.insert(this.mSuggestionsUri, values);
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "saveRecentQuery", e);
        }
        truncateHistory(cr, 250);
    }

    public void clearHistory() {
        ContentResolver cr = this.mContext.getContentResolver();
        truncateHistory(cr, 0);
    }

    protected void truncateHistory(ContentResolver cr, int maxEntries) {
        if (maxEntries < 0) {
            throw new IllegalArgumentException();
        }
        String selection = null;
        if (maxEntries > 0) {
            try {
                selection = "_id IN (SELECT _id FROM suggestions ORDER BY date DESC LIMIT -1 OFFSET " + String.valueOf(maxEntries) + Separators.RPAREN;
            } catch (RuntimeException e) {
                Log.e(LOG_TAG, "truncateHistory", e);
                return;
            }
        }
        cr.delete(this.mSuggestionsUri, selection, null);
    }
}