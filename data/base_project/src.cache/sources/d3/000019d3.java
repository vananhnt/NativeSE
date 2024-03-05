package com.android.internal.location;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.app.NetInitiatedActivity;
import com.android.internal.telephony.GsmAlphabet;
import java.io.UnsupportedEncodingException;

/* loaded from: GpsNetInitiatedHandler.class */
public class GpsNetInitiatedHandler {
    private static final String TAG = "GpsNetInitiatedHandler";
    private static final boolean DEBUG = true;
    private static final boolean VERBOSE = false;
    public static final String ACTION_NI_VERIFY = "android.intent.action.NETWORK_INITIATED_VERIFY";
    public static final String NI_INTENT_KEY_NOTIF_ID = "notif_id";
    public static final String NI_INTENT_KEY_TITLE = "title";
    public static final String NI_INTENT_KEY_MESSAGE = "message";
    public static final String NI_INTENT_KEY_TIMEOUT = "timeout";
    public static final String NI_INTENT_KEY_DEFAULT_RESPONSE = "default_resp";
    public static final String NI_RESPONSE_EXTRA_CMD = "send_ni_response";
    public static final String NI_EXTRA_CMD_NOTIF_ID = "notif_id";
    public static final String NI_EXTRA_CMD_RESPONSE = "response";
    public static final int GPS_NI_TYPE_VOICE = 1;
    public static final int GPS_NI_TYPE_UMTS_SUPL = 2;
    public static final int GPS_NI_TYPE_UMTS_CTRL_PLANE = 3;
    public static final int GPS_NI_RESPONSE_ACCEPT = 1;
    public static final int GPS_NI_RESPONSE_DENY = 2;
    public static final int GPS_NI_RESPONSE_NORESP = 3;
    public static final int GPS_NI_NEED_NOTIFY = 1;
    public static final int GPS_NI_NEED_VERIFY = 2;
    public static final int GPS_NI_PRIVACY_OVERRIDE = 4;
    public static final int GPS_ENC_NONE = 0;
    public static final int GPS_ENC_SUPL_GSM_DEFAULT = 1;
    public static final int GPS_ENC_SUPL_UTF8 = 2;
    public static final int GPS_ENC_SUPL_UCS2 = 3;
    public static final int GPS_ENC_UNKNOWN = -1;
    private final Context mContext;
    private final LocationManager mLocationManager;
    private boolean mPlaySounds = false;
    private boolean mPopupImmediately = true;
    private static boolean mIsHexInput = true;
    private Notification mNiNotification;

    /* loaded from: GpsNetInitiatedHandler$GpsNiNotification.class */
    public static class GpsNiNotification {
        public int notificationId;
        public int niType;
        public boolean needNotify;
        public boolean needVerify;
        public boolean privacyOverride;
        public int timeout;
        public int defaultResponse;
        public String requestorId;
        public String text;
        public int requestorIdEncoding;
        public int textEncoding;
        public Bundle extras;
    }

    /* loaded from: GpsNetInitiatedHandler$GpsNiResponse.class */
    public static class GpsNiResponse {
        int userResponse;
        Bundle extras;
    }

    public GpsNetInitiatedHandler(Context context) {
        this.mContext = context;
        this.mLocationManager = (LocationManager) context.getSystemService("location");
    }

    public void handleNiNotification(GpsNiNotification notif) {
        Log.d(TAG, "handleNiNotification notificationId: " + notif.notificationId + " requestorId: " + notif.requestorId + " text: " + notif.text);
        if (notif.needNotify && notif.needVerify && this.mPopupImmediately) {
            openNiDialog(notif);
        }
        if ((notif.needNotify && !notif.needVerify) || (notif.needNotify && notif.needVerify && !this.mPopupImmediately)) {
            setNiNotification(notif);
        }
        if ((notif.needNotify && !notif.needVerify) || ((!notif.needNotify && !notif.needVerify) || notif.privacyOverride)) {
            this.mLocationManager.sendNiResponse(notif.notificationId, 1);
        }
    }

    private synchronized void setNiNotification(GpsNiNotification notif) {
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        String title = getNotifTitle(notif, this.mContext);
        String message = getNotifMessage(notif, this.mContext);
        Log.d(TAG, "setNiNotification, notifyId: " + notif.notificationId + ", title: " + title + ", message: " + message);
        if (this.mNiNotification == null) {
            this.mNiNotification = new Notification();
            this.mNiNotification.icon = R.drawable.stat_sys_gps_on;
            this.mNiNotification.when = 0L;
        }
        if (this.mPlaySounds) {
            this.mNiNotification.defaults |= 1;
        } else {
            this.mNiNotification.defaults &= -2;
        }
        this.mNiNotification.flags = 18;
        this.mNiNotification.tickerText = getNotifTicker(notif, this.mContext);
        Intent intent = !this.mPopupImmediately ? getDlgIntent(notif) : new Intent();
        PendingIntent pi = PendingIntent.getBroadcast(this.mContext, 0, intent, 0);
        this.mNiNotification.setLatestEventInfo(this.mContext, title, message, pi);
        notificationManager.notifyAsUser(null, notif.notificationId, this.mNiNotification, UserHandle.ALL);
    }

    private void openNiDialog(GpsNiNotification notif) {
        Intent intent = getDlgIntent(notif);
        Log.d(TAG, "openNiDialog, notifyId: " + notif.notificationId + ", requestorId: " + notif.requestorId + ", text: " + notif.text);
        this.mContext.startActivity(intent);
    }

    private Intent getDlgIntent(GpsNiNotification notif) {
        Intent intent = new Intent();
        String title = getDialogTitle(notif, this.mContext);
        String message = getDialogMessage(notif, this.mContext);
        intent.setFlags(268435456);
        intent.setClass(this.mContext, NetInitiatedActivity.class);
        intent.putExtra("notif_id", notif.notificationId);
        intent.putExtra("title", title);
        intent.putExtra(NI_INTENT_KEY_MESSAGE, message);
        intent.putExtra(NI_INTENT_KEY_TIMEOUT, notif.timeout);
        intent.putExtra(NI_INTENT_KEY_DEFAULT_RESPONSE, notif.defaultResponse);
        Log.d(TAG, "generateIntent, title: " + title + ", message: " + message + ", timeout: " + notif.timeout);
        return intent;
    }

    static byte[] stringToByteArray(String original, boolean isHex) {
        int length = isHex ? original.length() / 2 : original.length();
        byte[] output = new byte[length];
        if (isHex) {
            for (int i = 0; i < length; i++) {
                output[i] = (byte) Integer.parseInt(original.substring(i * 2, (i * 2) + 2), 16);
            }
        } else {
            for (int i2 = 0; i2 < length; i2++) {
                output[i2] = (byte) original.charAt(i2);
            }
        }
        return output;
    }

    static String decodeGSMPackedString(byte[] input) {
        int lengthBytes = input.length;
        int lengthSeptets = (lengthBytes * 8) / 7;
        if (lengthBytes % 7 == 0 && lengthBytes > 0 && (input[lengthBytes - 1] >> 1) == 0) {
            lengthSeptets--;
        }
        String decoded = GsmAlphabet.gsm7BitPackedToString(input, 0, lengthSeptets);
        if (null == decoded) {
            Log.e(TAG, "Decoding of GSM packed string failed");
            decoded = "";
        }
        return decoded;
    }

    static String decodeUTF8String(byte[] input) {
        try {
            String decoded = new String(input, "UTF-8");
            return decoded;
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    static String decodeUCS2String(byte[] input) {
        try {
            String decoded = new String(input, "UTF-16");
            return decoded;
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    private static String decodeString(String original, boolean isHex, int coding) {
        String decoded = original;
        byte[] input = stringToByteArray(original, isHex);
        switch (coding) {
            case -1:
                decoded = original;
                break;
            case 0:
                decoded = original;
                break;
            case 1:
                decoded = decodeGSMPackedString(input);
                break;
            case 2:
                decoded = decodeUTF8String(input);
                break;
            case 3:
                decoded = decodeUCS2String(input);
                break;
            default:
                Log.e(TAG, "Unknown encoding " + coding + " for NI text " + original);
                break;
        }
        return decoded;
    }

    private static String getNotifTicker(GpsNiNotification notif, Context context) {
        String ticker = String.format(context.getString(R.string.gpsNotifTicker), decodeString(notif.requestorId, mIsHexInput, notif.requestorIdEncoding), decodeString(notif.text, mIsHexInput, notif.textEncoding));
        return ticker;
    }

    private static String getNotifTitle(GpsNiNotification notif, Context context) {
        String title = String.format(context.getString(R.string.gpsNotifTitle), new Object[0]);
        return title;
    }

    private static String getNotifMessage(GpsNiNotification notif, Context context) {
        String message = String.format(context.getString(R.string.gpsNotifMessage), decodeString(notif.requestorId, mIsHexInput, notif.requestorIdEncoding), decodeString(notif.text, mIsHexInput, notif.textEncoding));
        return message;
    }

    public static String getDialogTitle(GpsNiNotification notif, Context context) {
        return getNotifTitle(notif, context);
    }

    private static String getDialogMessage(GpsNiNotification notif, Context context) {
        return getNotifMessage(notif, context);
    }
}