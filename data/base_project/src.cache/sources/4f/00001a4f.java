package com.android.internal.telephony;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

/* loaded from: CallerInfoAsyncQuery.class */
public class CallerInfoAsyncQuery {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "CallerInfoAsyncQuery";
    private static final int EVENT_NEW_QUERY = 1;
    private static final int EVENT_ADD_LISTENER = 2;
    private static final int EVENT_END_OF_QUEUE = 3;
    private static final int EVENT_EMERGENCY_NUMBER = 4;
    private static final int EVENT_VOICEMAIL_NUMBER = 5;
    private CallerInfoAsyncQueryHandler mHandler;
    private static final boolean ENABLE_UNKNOWN_NUMBER_GEO_DESCRIPTION = true;

    /* loaded from: CallerInfoAsyncQuery$OnQueryCompleteListener.class */
    public interface OnQueryCompleteListener {
        void onQueryComplete(int i, Object obj, CallerInfo callerInfo);
    }

    /* loaded from: CallerInfoAsyncQuery$CookieWrapper.class */
    private static final class CookieWrapper {
        public OnQueryCompleteListener listener;
        public Object cookie;
        public int event;
        public String number;

        private CookieWrapper() {
        }
    }

    /* loaded from: CallerInfoAsyncQuery$QueryPoolException.class */
    public static class QueryPoolException extends SQLException {
        public QueryPoolException(String error) {
            super(error);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: CallerInfoAsyncQuery$CallerInfoAsyncQueryHandler.class */
    public class CallerInfoAsyncQueryHandler extends AsyncQueryHandler {
        private Context mQueryContext;
        private Uri mQueryUri;
        private CallerInfo mCallerInfo;

        /* loaded from: CallerInfoAsyncQuery$CallerInfoAsyncQueryHandler$CallerInfoWorkerHandler.class */
        protected class CallerInfoWorkerHandler extends AsyncQueryHandler.WorkerHandler {
            public CallerInfoWorkerHandler(Looper looper) {
                super(looper);
            }

            @Override // android.content.AsyncQueryHandler.WorkerHandler, android.os.Handler
            public void handleMessage(Message msg) {
                AsyncQueryHandler.WorkerArgs args = (AsyncQueryHandler.WorkerArgs) msg.obj;
                CookieWrapper cw = (CookieWrapper) args.cookie;
                if (cw == null) {
                    super.handleMessage(msg);
                    return;
                }
                switch (cw.event) {
                    case 1:
                        super.handleMessage(msg);
                        return;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        Message reply = args.handler.obtainMessage(msg.what);
                        reply.obj = args;
                        reply.arg1 = msg.arg1;
                        reply.sendToTarget();
                        return;
                    default:
                        return;
                }
            }
        }

        private CallerInfoAsyncQueryHandler(Context context) {
            super(context.getContentResolver());
        }

        @Override // android.content.AsyncQueryHandler
        protected Handler createHandler(Looper looper) {
            return new CallerInfoWorkerHandler(looper);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            CookieWrapper cw = (CookieWrapper) cookie;
            if (cw == null) {
                return;
            }
            if (cw.event == 3) {
                CallerInfoAsyncQuery.this.release();
                return;
            }
            if (this.mCallerInfo == null) {
                if (this.mQueryContext == null || this.mQueryUri == null) {
                    throw new QueryPoolException("Bad context or query uri, or CallerInfoAsyncQuery already released.");
                }
                if (cw.event == 4) {
                    this.mCallerInfo = new CallerInfo().markAsEmergency(this.mQueryContext);
                } else if (cw.event == 5) {
                    this.mCallerInfo = new CallerInfo().markAsVoiceMail();
                } else {
                    this.mCallerInfo = CallerInfo.getCallerInfo(this.mQueryContext, this.mQueryUri, cursor);
                    CallerInfo newCallerInfo = CallerInfo.doSecondaryLookupIfNecessary(this.mQueryContext, cw.number, this.mCallerInfo);
                    if (newCallerInfo != this.mCallerInfo) {
                        this.mCallerInfo = newCallerInfo;
                    }
                    if (TextUtils.isEmpty(this.mCallerInfo.name)) {
                        this.mCallerInfo.updateGeoDescription(this.mQueryContext, cw.number);
                    }
                    if (!TextUtils.isEmpty(cw.number)) {
                        this.mCallerInfo.phoneNumber = PhoneNumberUtils.formatNumber(cw.number, this.mCallerInfo.normalizedNumber, CallerInfo.getCurrentCountryIso(this.mQueryContext));
                    }
                }
                CookieWrapper endMarker = new CookieWrapper();
                endMarker.event = 3;
                startQuery(token, endMarker, null, null, null, null, null);
            }
            if (cw.listener != null) {
                cw.listener.onQueryComplete(token, cw.cookie, this.mCallerInfo);
            }
        }
    }

    private CallerInfoAsyncQuery() {
    }

    public static CallerInfoAsyncQuery startQuery(int token, Context context, Uri contactRef, OnQueryCompleteListener listener, Object cookie) {
        CallerInfoAsyncQuery c = new CallerInfoAsyncQuery();
        c.allocate(context, contactRef);
        CookieWrapper cw = new CookieWrapper();
        cw.listener = listener;
        cw.cookie = cookie;
        cw.event = 1;
        c.mHandler.startQuery(token, cw, contactRef, null, null, null, null);
        return c;
    }

    public static CallerInfoAsyncQuery startQuery(int token, Context context, String number, OnQueryCompleteListener listener, Object cookie) {
        Uri contactRef;
        String selection;
        String[] selectionArgs;
        if (PhoneNumberUtils.isUriNumber(number)) {
            contactRef = ContactsContract.Data.CONTENT_URI;
            selection = "upper(data1)=? AND mimetype='vnd.android.cursor.item/sip_address'";
            selectionArgs = new String[]{number.toUpperCase()};
        } else {
            contactRef = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            selection = null;
            selectionArgs = null;
        }
        CallerInfoAsyncQuery c = new CallerInfoAsyncQuery();
        c.allocate(context, contactRef);
        CookieWrapper cw = new CookieWrapper();
        cw.listener = listener;
        cw.cookie = cookie;
        cw.number = number;
        if (PhoneNumberUtils.isLocalEmergencyNumber(number, context)) {
            cw.event = 4;
        } else if (PhoneNumberUtils.isVoiceMailNumber(number)) {
            cw.event = 5;
        } else {
            cw.event = 1;
        }
        c.mHandler.startQuery(token, cw, contactRef, null, selection, selectionArgs, null);
        return c;
    }

    public void addQueryListener(int token, OnQueryCompleteListener listener, Object cookie) {
        CookieWrapper cw = new CookieWrapper();
        cw.listener = listener;
        cw.cookie = cookie;
        cw.event = 2;
        this.mHandler.startQuery(token, cw, null, null, null, null, null);
    }

    private void allocate(Context context, Uri contactRef) {
        if (context == null || contactRef == null) {
            throw new QueryPoolException("Bad context or query uri.");
        }
        this.mHandler = new CallerInfoAsyncQueryHandler(context);
        this.mHandler.mQueryContext = context;
        this.mHandler.mQueryUri = contactRef;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void release() {
        this.mHandler.mQueryContext = null;
        this.mHandler.mQueryUri = null;
        this.mHandler.mCallerInfo = null;
        this.mHandler = null;
    }

    private static String sanitizeUriToString(Uri uri) {
        if (uri != null) {
            String uriString = uri.toString();
            int indexOfLastSlash = uriString.lastIndexOf(47);
            if (indexOfLastSlash > 0) {
                return uriString.substring(0, indexOfLastSlash) + "/xxxxxxx";
            }
            return uriString;
        }
        return "";
    }
}