package android.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.android.internal.telephony.CallerInfo;
import com.android.internal.telephony.PhoneConstants;

/* loaded from: CallLog.class */
public class CallLog {
    public static final String AUTHORITY = "call_log";
    public static final Uri CONTENT_URI = Uri.parse("content://call_log");

    /* loaded from: CallLog$Calls.class */
    public static class Calls implements BaseColumns {
        public static final String LIMIT_PARAM_KEY = "limit";
        public static final String OFFSET_PARAM_KEY = "offset";
        public static final String DEFAULT_SORT_ORDER = "date DESC";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/calls";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/calls";
        public static final String TYPE = "type";
        public static final int INCOMING_TYPE = 1;
        public static final int OUTGOING_TYPE = 2;
        public static final int MISSED_TYPE = 3;
        public static final int VOICEMAIL_TYPE = 4;
        public static final String NUMBER = "number";
        public static final String NUMBER_PRESENTATION = "presentation";
        public static final int PRESENTATION_ALLOWED = 1;
        public static final int PRESENTATION_RESTRICTED = 2;
        public static final int PRESENTATION_UNKNOWN = 3;
        public static final int PRESENTATION_PAYPHONE = 4;
        public static final String COUNTRY_ISO = "countryiso";
        public static final String DATE = "date";
        public static final String DURATION = "duration";
        public static final String NEW = "new";
        public static final String CACHED_NAME = "name";
        public static final String CACHED_NUMBER_TYPE = "numbertype";
        public static final String CACHED_NUMBER_LABEL = "numberlabel";
        public static final String VOICEMAIL_URI = "voicemail_uri";
        public static final String IS_READ = "is_read";
        public static final String GEOCODED_LOCATION = "geocoded_location";
        public static final String CACHED_LOOKUP_URI = "lookup_uri";
        public static final String CACHED_MATCHED_NUMBER = "matched_number";
        public static final String CACHED_NORMALIZED_NUMBER = "normalized_number";
        public static final String CACHED_PHOTO_ID = "photo_id";
        public static final String CACHED_FORMATTED_NUMBER = "formatted_number";
        public static final Uri CONTENT_URI = Uri.parse("content://call_log/calls");
        public static final Uri CONTENT_FILTER_URI = Uri.parse("content://call_log/calls/filter");
        public static final String ALLOW_VOICEMAILS_PARAM_KEY = "allow_voicemails";
        public static final Uri CONTENT_URI_WITH_VOICEMAIL = CONTENT_URI.buildUpon().appendQueryParameter(ALLOW_VOICEMAILS_PARAM_KEY, "true").build();

        public static Uri addCall(CallerInfo ci, Context context, String number, int presentation, int callType, long start, int duration) {
            Cursor cursor;
            ContentResolver resolver = context.getContentResolver();
            int numberPresentation = 1;
            if (presentation == PhoneConstants.PRESENTATION_RESTRICTED) {
                numberPresentation = 2;
            } else if (presentation == PhoneConstants.PRESENTATION_PAYPHONE) {
                numberPresentation = 4;
            } else if (TextUtils.isEmpty(number) || presentation == PhoneConstants.PRESENTATION_UNKNOWN) {
                numberPresentation = 3;
            }
            if (numberPresentation != 1) {
                number = "";
                if (ci != null) {
                    ci.name = "";
                }
            }
            ContentValues values = new ContentValues(6);
            values.put("number", number);
            values.put(NUMBER_PRESENTATION, Integer.valueOf(numberPresentation));
            values.put("type", Integer.valueOf(callType));
            values.put("date", Long.valueOf(start));
            values.put("duration", Long.valueOf(duration));
            values.put(NEW, (Integer) 1);
            if (callType == 3) {
                values.put("is_read", (Integer) 0);
            }
            if (ci != null) {
                values.put("name", ci.name);
                values.put(CACHED_NUMBER_TYPE, Integer.valueOf(ci.numberType));
                values.put(CACHED_NUMBER_LABEL, ci.numberLabel);
            }
            if (ci != null && ci.person_id > 0) {
                if (ci.normalizedNumber != null) {
                    String normalizedPhoneNumber = ci.normalizedNumber;
                    cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{"_id"}, "contact_id =? AND data4 =?", new String[]{String.valueOf(ci.person_id), normalizedPhoneNumber}, null);
                } else {
                    String phoneNumber = ci.phoneNumber != null ? ci.phoneNumber : number;
                    cursor = resolver.query(Uri.withAppendedPath(ContactsContract.CommonDataKinds.Callable.CONTENT_FILTER_URI, Uri.encode(phoneNumber)), new String[]{"_id"}, "contact_id =?", new String[]{String.valueOf(ci.person_id)}, null);
                }
                if (cursor != null) {
                    try {
                        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                            Uri feedbackUri = ContactsContract.DataUsageFeedback.FEEDBACK_URI.buildUpon().appendPath(cursor.getString(0)).appendQueryParameter("type", "call").build();
                            resolver.update(feedbackUri, new ContentValues(), null, null);
                        }
                    } finally {
                        cursor.close();
                    }
                }
            }
            Uri result = resolver.insert(CONTENT_URI, values);
            removeExpiredEntries(context);
            return result;
        }

        public static String getLastOutgoingCall(Context context) {
            ContentResolver resolver = context.getContentResolver();
            Cursor c = null;
            try {
                c = resolver.query(CONTENT_URI, new String[]{"number"}, "type = 2", null, "date DESC LIMIT 1");
                if (c != null && c.moveToFirst()) {
                    String string = c.getString(0);
                    if (c != null) {
                        c.close();
                    }
                    return string;
                }
                if (c != null) {
                    c.close();
                }
                return "";
            } catch (Throwable th) {
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        }

        private static void removeExpiredEntries(Context context) {
            ContentResolver resolver = context.getContentResolver();
            resolver.delete(CONTENT_URI, "_id IN (SELECT _id FROM calls ORDER BY date DESC LIMIT -1 OFFSET 500)", null);
        }
    }
}