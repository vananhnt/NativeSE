package com.android.internal.telephony;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.sqlite.SqliteWrapper;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Downloads;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.telephony.Rlog;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: SMSDispatcher.class */
public abstract class SMSDispatcher extends Handler {
    static final String TAG = "SMSDispatcher";
    static final boolean DBG = false;
    private static final String SEND_NEXT_MSG_EXTRA = "SendNextMsg";
    private static final String SEND_SMS_NO_CONFIRMATION_PERMISSION = "android.permission.SEND_SMS_NO_CONFIRMATION";
    private static final int PREMIUM_RULE_USE_SIM = 1;
    private static final int PREMIUM_RULE_USE_NETWORK = 2;
    private static final int PREMIUM_RULE_USE_BOTH = 3;
    private final SettingsObserver mSettingsObserver;
    protected static final int EVENT_SEND_SMS_COMPLETE = 2;
    private static final int EVENT_SEND_RETRY = 3;
    private static final int EVENT_SEND_LIMIT_REACHED_CONFIRMATION = 4;
    static final int EVENT_SEND_CONFIRMED_SMS = 5;
    static final int EVENT_STOP_SENDING = 7;
    private static final int EVENT_CONFIRM_SEND_TO_POSSIBLE_PREMIUM_SHORT_CODE = 8;
    private static final int EVENT_CONFIRM_SEND_TO_PREMIUM_SHORT_CODE = 9;
    protected static final int EVENT_HANDLE_STATUS_REPORT = 10;
    protected static final int EVENT_RADIO_ON = 11;
    protected static final int EVENT_IMS_STATE_CHANGED = 12;
    protected static final int EVENT_IMS_STATE_DONE = 13;
    protected static final int EVENT_NEW_ICC_SMS = 14;
    protected static final int EVENT_ICC_CHANGED = 15;
    protected PhoneBase mPhone;
    protected final Context mContext;
    protected final ContentResolver mResolver;
    protected final CommandsInterface mCi;
    protected SmsStorageMonitor mStorageMonitor;
    protected final TelephonyManager mTelephonyManager;
    private static final int MAX_SEND_RETRIES = 3;
    private static final int SEND_RETRY_DELAY = 2000;
    private static final int SINGLE_PART_SMS = 1;
    private static final int MO_MSG_QUEUE_LIMIT = 5;
    private static int sConcatenatedRef = new Random().nextInt(256);
    private SmsUsageMonitor mUsageMonitor;
    private int mPendingTrackerCount;
    protected boolean mSmsCapable;
    protected boolean mSmsSendDisabled;
    private final AtomicInteger mPremiumSmsRule = new AtomicInteger(1);
    protected int mRemainingMessages = -1;
    protected final ArrayList<SmsTracker> deliveryPendingList = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract String getFormat();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void sendData(String str, String str2, int i, byte[] bArr, PendingIntent pendingIntent, PendingIntent pendingIntent2);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void sendText(String str, String str2, String str3, PendingIntent pendingIntent, PendingIntent pendingIntent2);

    protected abstract GsmAlphabet.TextEncodingDetails calculateLength(CharSequence charSequence, boolean z);

    protected abstract void sendNewSubmitPdu(String str, String str2, String str3, SmsHeader smsHeader, int i, PendingIntent pendingIntent, PendingIntent pendingIntent2, boolean z);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void sendSms(SmsTracker smsTracker);

    public abstract void sendRetrySms(SmsTracker smsTracker);

    public abstract boolean isIms();

    public abstract String getImsSmsFormat();

    protected static int getNextConcatenatedRef() {
        sConcatenatedRef++;
        return sConcatenatedRef;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SMSDispatcher(PhoneBase phone, SmsUsageMonitor usageMonitor) {
        this.mSmsCapable = true;
        this.mPhone = phone;
        this.mContext = phone.getContext();
        this.mResolver = this.mContext.getContentResolver();
        this.mCi = phone.mCi;
        this.mUsageMonitor = usageMonitor;
        this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mSettingsObserver = new SettingsObserver(this, this.mPremiumSmsRule, this.mContext);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(Settings.Global.SMS_SHORT_CODE_RULE), false, this.mSettingsObserver);
        this.mSmsCapable = this.mContext.getResources().getBoolean(R.bool.config_sms_capable);
        this.mSmsSendDisabled = !SystemProperties.getBoolean(TelephonyProperties.PROPERTY_SMS_SEND, this.mSmsCapable);
        Rlog.d(TAG, "SMSDispatcher: ctor mSmsCapable=" + this.mSmsCapable + " format=" + getFormat() + " mSmsSendDisabled=" + this.mSmsSendDisabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SMSDispatcher$SettingsObserver.class */
    public static class SettingsObserver extends ContentObserver {
        private final AtomicInteger mPremiumSmsRule;
        private final Context mContext;

        SettingsObserver(Handler handler, AtomicInteger premiumSmsRule, Context context) {
            super(handler);
            this.mPremiumSmsRule = premiumSmsRule;
            this.mContext = context;
            onChange(false);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            this.mPremiumSmsRule.set(Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.SMS_SHORT_CODE_RULE, 1));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePhoneObject(PhoneBase phone) {
        this.mPhone = phone;
        this.mStorageMonitor = phone.mSmsStorageMonitor;
        this.mUsageMonitor = phone.mSmsUsageMonitor;
        Rlog.d(TAG, "Active phone changed to " + this.mPhone.getPhoneName());
    }

    public void dispose() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
    }

    protected void handleStatusReport(Object o) {
        Rlog.d(TAG, "handleStatusReport() called with no subclass.");
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 2:
                handleSendComplete((AsyncResult) msg.obj);
                return;
            case 3:
                Rlog.d(TAG, "SMS retry..");
                sendRetrySms((SmsTracker) msg.obj);
                return;
            case 4:
                handleReachSentLimit((SmsTracker) msg.obj);
                return;
            case 5:
                SmsTracker tracker = (SmsTracker) msg.obj;
                if (tracker.isMultipart()) {
                    sendMultipartSms(tracker);
                } else {
                    sendSms(tracker);
                }
                this.mPendingTrackerCount--;
                return;
            case 6:
            default:
                Rlog.e(TAG, "handleMessage() ignoring message of unexpected type " + msg.what);
                return;
            case 7:
                SmsTracker tracker2 = (SmsTracker) msg.obj;
                if (tracker2.mSentIntent != null) {
                    try {
                        tracker2.mSentIntent.send(5);
                    } catch (PendingIntent.CanceledException e) {
                        Rlog.e(TAG, "failed to send RESULT_ERROR_LIMIT_EXCEEDED");
                    }
                }
                this.mPendingTrackerCount--;
                return;
            case 8:
                handleConfirmShortCode(false, (SmsTracker) msg.obj);
                return;
            case 9:
                handleConfirmShortCode(true, (SmsTracker) msg.obj);
                return;
            case 10:
                handleStatusReport(msg.obj);
                return;
        }
    }

    protected void handleSendComplete(AsyncResult ar) {
        SmsTracker tracker = (SmsTracker) ar.userObj;
        PendingIntent sentIntent = tracker.mSentIntent;
        if (ar.result != null) {
            tracker.mMessageRef = ((SmsResponse) ar.result).mMessageRef;
        } else {
            Rlog.d(TAG, "SmsResponse was null");
        }
        if (ar.exception == null) {
            String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(this.mContext);
            if (defaultSmsPackage == null || !defaultSmsPackage.equals(tracker.mAppInfo.applicationInfo.packageName)) {
                tracker.writeSentMessage(this.mContext);
            }
            if (tracker.mDeliveryIntent != null) {
                this.deliveryPendingList.add(tracker);
            }
            if (sentIntent != null) {
                try {
                    if (this.mRemainingMessages > -1) {
                        this.mRemainingMessages--;
                    }
                    if (this.mRemainingMessages == 0) {
                        Intent sendNext = new Intent();
                        sendNext.putExtra(SEND_NEXT_MSG_EXTRA, true);
                        sentIntent.send(this.mContext, -1, sendNext);
                    } else {
                        sentIntent.send(-1);
                    }
                    return;
                } catch (PendingIntent.CanceledException e) {
                    return;
                }
            }
            return;
        }
        int ss = this.mPhone.getServiceState().getState();
        if (tracker.mImsRetry > 0 && ss != 0) {
            tracker.mRetryCount = 3;
            Rlog.d(TAG, "handleSendComplete: Skipping retry:  isIms()=" + isIms() + " mRetryCount=" + tracker.mRetryCount + " mImsRetry=" + tracker.mImsRetry + " mMessageRef=" + tracker.mMessageRef + " SS= " + this.mPhone.getServiceState().getState());
        }
        if (!isIms() && ss != 0) {
            handleNotInService(ss, tracker.mSentIntent);
        } else if (((CommandException) ar.exception).getCommandError() == CommandException.Error.SMS_FAIL_RETRY && tracker.mRetryCount < 3) {
            tracker.mRetryCount++;
            Message retryMsg = obtainMessage(3, tracker);
            sendMessageDelayed(retryMsg, 2000L);
        } else if (tracker.mSentIntent != null) {
            int error = 1;
            if (((CommandException) ar.exception).getCommandError() == CommandException.Error.FDN_CHECK_FAILURE) {
                error = 6;
            }
            try {
                Intent fillIn = new Intent();
                if (ar.result != null) {
                    fillIn.putExtra(AccountManager.KEY_ERROR_CODE, ((SmsResponse) ar.result).mErrorCode);
                }
                if (this.mRemainingMessages > -1) {
                    this.mRemainingMessages--;
                }
                if (this.mRemainingMessages == 0) {
                    fillIn.putExtra(SEND_NEXT_MSG_EXTRA, true);
                }
                tracker.mSentIntent.send(this.mContext, error, fillIn);
            } catch (PendingIntent.CanceledException e2) {
            }
        }
    }

    protected static void handleNotInService(int ss, PendingIntent sentIntent) {
        if (sentIntent != null) {
            try {
                if (ss == 3) {
                    sentIntent.send(2);
                } else {
                    sentIntent.send(4);
                }
            } catch (PendingIntent.CanceledException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendMultipartText(String destAddr, String scAddr, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents) {
        int refNumber = getNextConcatenatedRef() & 255;
        int msgCount = parts.size();
        int encoding = 0;
        this.mRemainingMessages = msgCount;
        GsmAlphabet.TextEncodingDetails[] encodingForParts = new GsmAlphabet.TextEncodingDetails[msgCount];
        for (int i = 0; i < msgCount; i++) {
            GsmAlphabet.TextEncodingDetails details = calculateLength(parts.get(i), false);
            if (encoding != details.codeUnitSize && (encoding == 0 || encoding == 1)) {
                encoding = details.codeUnitSize;
            }
            encodingForParts[i] = details;
        }
        int i2 = 0;
        while (i2 < msgCount) {
            SmsHeader.ConcatRef concatRef = new SmsHeader.ConcatRef();
            concatRef.refNumber = refNumber;
            concatRef.seqNumber = i2 + 1;
            concatRef.msgCount = msgCount;
            concatRef.isEightBits = true;
            SmsHeader smsHeader = new SmsHeader();
            smsHeader.concatRef = concatRef;
            if (encoding == 1) {
                smsHeader.languageTable = encodingForParts[i2].languageTable;
                smsHeader.languageShiftTable = encodingForParts[i2].languageShiftTable;
            }
            PendingIntent sentIntent = null;
            if (sentIntents != null && sentIntents.size() > i2) {
                sentIntent = sentIntents.get(i2);
            }
            PendingIntent deliveryIntent = null;
            if (deliveryIntents != null && deliveryIntents.size() > i2) {
                deliveryIntent = deliveryIntents.get(i2);
            }
            sendNewSubmitPdu(destAddr, scAddr, parts.get(i2), smsHeader, encoding, sentIntent, deliveryIntent, i2 == msgCount - 1);
            i2++;
        }
    }

    protected void sendRawPdu(SmsTracker tracker) {
        HashMap map = tracker.mData;
        byte[] pdu = (byte[]) map.get("pdu");
        PendingIntent sentIntent = tracker.mSentIntent;
        if (this.mSmsSendDisabled) {
            if (sentIntent != null) {
                try {
                    sentIntent.send(4);
                } catch (PendingIntent.CanceledException e) {
                }
            }
            Rlog.d(TAG, "Device does not support sending sms.");
        } else if (pdu == null) {
            if (sentIntent != null) {
                try {
                    sentIntent.send(3);
                } catch (PendingIntent.CanceledException e2) {
                }
            }
        } else {
            PackageManager pm = this.mContext.getPackageManager();
            String[] packageNames = pm.getPackagesForUid(Binder.getCallingUid());
            if (packageNames == null || packageNames.length == 0) {
                Rlog.e(TAG, "Can't get calling app package name: refusing to send SMS");
                if (sentIntent != null) {
                    try {
                        sentIntent.send(1);
                        return;
                    } catch (PendingIntent.CanceledException e3) {
                        Rlog.e(TAG, "failed to send error result");
                        return;
                    }
                }
                return;
            }
            try {
                PackageInfo appInfo = pm.getPackageInfo(packageNames[0], 64);
                if (checkDestination(tracker)) {
                    if (!this.mUsageMonitor.check(appInfo.packageName, 1)) {
                        sendMessage(obtainMessage(4, tracker));
                        return;
                    }
                    int ss = this.mPhone.getServiceState().getState();
                    if (!isIms() && ss != 0) {
                        handleNotInService(ss, tracker.mSentIntent);
                    } else {
                        sendSms(tracker);
                    }
                }
            } catch (PackageManager.NameNotFoundException e4) {
                Rlog.e(TAG, "Can't get calling app package info: refusing to send SMS");
                if (sentIntent != null) {
                    try {
                        sentIntent.send(1);
                    } catch (PendingIntent.CanceledException e5) {
                        Rlog.e(TAG, "failed to send error result");
                    }
                }
            }
        }
    }

    boolean checkDestination(SmsTracker tracker) {
        int event;
        if (this.mContext.checkCallingOrSelfPermission(SEND_SMS_NO_CONFIRMATION_PERMISSION) == 0) {
            return true;
        }
        int rule = this.mPremiumSmsRule.get();
        int smsCategory = 0;
        if (rule == 1 || rule == 3) {
            String simCountryIso = this.mTelephonyManager.getSimCountryIso();
            if (simCountryIso == null || simCountryIso.length() != 2) {
                Rlog.e(TAG, "Can't get SIM country Iso: trying network country Iso");
                simCountryIso = this.mTelephonyManager.getNetworkCountryIso();
            }
            smsCategory = this.mUsageMonitor.checkDestination(tracker.mDestAddress, simCountryIso);
        }
        if (rule == 2 || rule == 3) {
            String networkCountryIso = this.mTelephonyManager.getNetworkCountryIso();
            if (networkCountryIso == null || networkCountryIso.length() != 2) {
                Rlog.e(TAG, "Can't get Network country Iso: trying SIM country Iso");
                networkCountryIso = this.mTelephonyManager.getSimCountryIso();
            }
            smsCategory = SmsUsageMonitor.mergeShortCodeCategories(smsCategory, this.mUsageMonitor.checkDestination(tracker.mDestAddress, networkCountryIso));
        }
        if (smsCategory == 0 || smsCategory == 1 || smsCategory == 2) {
            return true;
        }
        int premiumSmsPermission = this.mUsageMonitor.getPremiumSmsPermission(tracker.mAppInfo.packageName);
        if (premiumSmsPermission == 0) {
            premiumSmsPermission = 1;
        }
        switch (premiumSmsPermission) {
            case 1:
            default:
                if (smsCategory == 3) {
                    event = 8;
                } else {
                    event = 9;
                }
                sendMessage(obtainMessage(event, tracker));
                return false;
            case 2:
                Rlog.w(TAG, "User denied this app from sending to premium SMS");
                sendMessage(obtainMessage(7, tracker));
                return false;
            case 3:
                Rlog.d(TAG, "User approved this app to send to premium SMS");
                return true;
        }
    }

    private boolean denyIfQueueLimitReached(SmsTracker tracker) {
        if (this.mPendingTrackerCount >= 5) {
            try {
                if (tracker.mSentIntent != null) {
                    tracker.mSentIntent.send(5);
                }
                return true;
            } catch (PendingIntent.CanceledException e) {
                Rlog.e(TAG, "failed to send back RESULT_ERROR_LIMIT_EXCEEDED");
                return true;
            }
        }
        this.mPendingTrackerCount++;
        return false;
    }

    private CharSequence getAppLabel(String appPackage) {
        PackageManager pm = this.mContext.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(appPackage, 0);
            return appInfo.loadLabel(pm);
        } catch (PackageManager.NameNotFoundException e) {
            Rlog.e(TAG, "PackageManager Name Not Found for package " + appPackage);
            return appPackage;
        }
    }

    protected void handleReachSentLimit(SmsTracker tracker) {
        if (denyIfQueueLimitReached(tracker)) {
            return;
        }
        CharSequence appLabel = getAppLabel(tracker.mAppInfo.packageName);
        Resources r = Resources.getSystem();
        Spanned messageText = Html.fromHtml(r.getString(R.string.sms_control_message, appLabel));
        ConfirmDialogListener listener = new ConfirmDialogListener(tracker, null);
        AlertDialog d = new AlertDialog.Builder(this.mContext).setTitle(R.string.sms_control_title).setIcon(17301642).setMessage(messageText).setPositiveButton(r.getString(R.string.sms_control_yes), listener).setNegativeButton(r.getString(R.string.sms_control_no), listener).setOnCancelListener(listener).create();
        d.getWindow().setType(2003);
        d.show();
    }

    protected void handleConfirmShortCode(boolean isPremium, SmsTracker tracker) {
        int detailsId;
        if (denyIfQueueLimitReached(tracker)) {
            return;
        }
        if (isPremium) {
            detailsId = 17040456;
        } else {
            detailsId = 17040455;
        }
        CharSequence appLabel = getAppLabel(tracker.mAppInfo.packageName);
        Resources r = Resources.getSystem();
        Spanned messageText = Html.fromHtml(r.getString(R.string.sms_short_code_confirm_message, appLabel, tracker.mDestAddress));
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.sms_short_code_confirmation_dialog, (ViewGroup) null);
        ConfirmDialogListener listener = new ConfirmDialogListener(tracker, (TextView) layout.findViewById(R.id.sms_short_code_remember_undo_instruction));
        TextView messageView = (TextView) layout.findViewById(R.id.sms_short_code_confirm_message);
        messageView.setText(messageText);
        ViewGroup detailsLayout = (ViewGroup) layout.findViewById(R.id.sms_short_code_detail_layout);
        TextView detailsView = (TextView) detailsLayout.findViewById(R.id.sms_short_code_detail_message);
        detailsView.setText(detailsId);
        CheckBox rememberChoice = (CheckBox) layout.findViewById(R.id.sms_short_code_remember_choice_checkbox);
        rememberChoice.setOnCheckedChangeListener(listener);
        AlertDialog d = new AlertDialog.Builder(this.mContext).setView(layout).setPositiveButton(r.getString(R.string.sms_short_code_confirm_allow), listener).setNegativeButton(r.getString(R.string.sms_short_code_confirm_deny), listener).setOnCancelListener(listener).create();
        d.getWindow().setType(2003);
        d.show();
        listener.setPositiveButton(d.getButton(-1));
        listener.setNegativeButton(d.getButton(-2));
    }

    public int getPremiumSmsPermission(String packageName) {
        return this.mUsageMonitor.getPremiumSmsPermission(packageName);
    }

    public void setPremiumSmsPermission(String packageName, int permission) {
        this.mUsageMonitor.setPremiumSmsPermission(packageName, permission);
    }

    private void sendMultipartSms(SmsTracker tracker) {
        HashMap<String, Object> map = tracker.mData;
        String destinationAddress = (String) map.get(Downloads.Impl.COLUMN_DESTINATION);
        String scAddress = (String) map.get("scaddress");
        ArrayList<String> parts = (ArrayList) map.get("parts");
        ArrayList<PendingIntent> sentIntents = (ArrayList) map.get("sentIntents");
        ArrayList<PendingIntent> deliveryIntents = (ArrayList) map.get("deliveryIntents");
        int ss = this.mPhone.getServiceState().getState();
        if (!isIms() && ss != 0) {
            int count = parts.size();
            for (int i = 0; i < count; i++) {
                PendingIntent sentIntent = null;
                if (sentIntents != null && sentIntents.size() > i) {
                    sentIntent = sentIntents.get(i);
                }
                handleNotInService(ss, sentIntent);
            }
            return;
        }
        sendMultipartText(destinationAddress, scAddress, parts, sentIntents, deliveryIntents);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: SMSDispatcher$SmsTracker.class */
    public static final class SmsTracker {
        public final HashMap<String, Object> mData;
        public int mRetryCount;
        public int mImsRetry;
        public int mMessageRef;
        String mFormat;
        public final PendingIntent mSentIntent;
        public final PendingIntent mDeliveryIntent;
        public final PackageInfo mAppInfo;
        public final String mDestAddress;
        private long mTimestamp;
        private Uri mSentMessageUri;

        private SmsTracker(HashMap<String, Object> data, PendingIntent sentIntent, PendingIntent deliveryIntent, PackageInfo appInfo, String destAddr, String format) {
            this.mTimestamp = System.currentTimeMillis();
            this.mData = data;
            this.mSentIntent = sentIntent;
            this.mDeliveryIntent = deliveryIntent;
            this.mRetryCount = 0;
            this.mAppInfo = appInfo;
            this.mDestAddress = destAddr;
            this.mFormat = format;
            this.mImsRetry = 0;
            this.mMessageRef = 0;
        }

        boolean isMultipart() {
            return this.mData.containsKey("parts");
        }

        void writeSentMessage(Context context) {
            String text = (String) this.mData.get("text");
            if (text != null) {
                boolean deliveryReport = this.mDeliveryIntent != null;
                this.mSentMessageUri = Telephony.Sms.addMessageToUri(context.getContentResolver(), Telephony.Sms.Sent.CONTENT_URI, this.mDestAddress, text, null, Long.valueOf(this.mTimestamp), true, deliveryReport, 0L);
            }
        }

        public void updateSentMessageStatus(Context context, int status) {
            if (this.mSentMessageUri != null) {
                ContentValues values = new ContentValues(1);
                values.put("status", Integer.valueOf(status));
                SqliteWrapper.update(context, context.getContentResolver(), this.mSentMessageUri, values, null, null);
            }
        }
    }

    protected SmsTracker SmsTrackerFactory(HashMap<String, Object> data, PendingIntent sentIntent, PendingIntent deliveryIntent, String format) {
        PackageManager pm = this.mContext.getPackageManager();
        String[] packageNames = pm.getPackagesForUid(Binder.getCallingUid());
        PackageInfo appInfo = null;
        if (packageNames != null && packageNames.length > 0) {
            try {
                appInfo = pm.getPackageInfo(packageNames[0], 64);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        String destAddr = PhoneNumberUtils.extractNetworkPortion((String) data.get("destAddr"));
        return new SmsTracker(data, sentIntent, deliveryIntent, appInfo, destAddr, format);
    }

    protected HashMap SmsTrackerMapFactory(String destAddr, String scAddr, String text, SmsMessageBase.SubmitPduBase pdu) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("destAddr", destAddr);
        map.put("scAddr", scAddr);
        map.put("text", text);
        map.put("smsc", pdu.encodedScAddress);
        map.put("pdu", pdu.encodedMessage);
        return map;
    }

    protected HashMap SmsTrackerMapFactory(String destAddr, String scAddr, int destPort, byte[] data, SmsMessageBase.SubmitPduBase pdu) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("destAddr", destAddr);
        map.put("scAddr", scAddr);
        map.put("destPort", Integer.valueOf(destPort));
        map.put("data", data);
        map.put("smsc", pdu.encodedScAddress);
        map.put("pdu", pdu.encodedMessage);
        return map;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SMSDispatcher$ConfirmDialogListener.class */
    public final class ConfirmDialogListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, CompoundButton.OnCheckedChangeListener {
        private final SmsTracker mTracker;
        private Button mPositiveButton;
        private Button mNegativeButton;
        private boolean mRememberChoice;
        private final TextView mRememberUndoInstruction;

        ConfirmDialogListener(SmsTracker tracker, TextView textView) {
            this.mTracker = tracker;
            this.mRememberUndoInstruction = textView;
        }

        void setPositiveButton(Button button) {
            this.mPositiveButton = button;
        }

        void setNegativeButton(Button button) {
            this.mNegativeButton = button;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            int newSmsPermission = 1;
            if (which == -1) {
                Rlog.d(SMSDispatcher.TAG, "CONFIRM sending SMS");
                EventLog.writeEvent((int) EventLogTags.EXP_DET_SMS_SENT_BY_USER, this.mTracker.mAppInfo.applicationInfo == null ? -1 : this.mTracker.mAppInfo.applicationInfo.uid);
                SMSDispatcher.this.sendMessage(SMSDispatcher.this.obtainMessage(5, this.mTracker));
                if (this.mRememberChoice) {
                    newSmsPermission = 3;
                }
            } else if (which == -2) {
                Rlog.d(SMSDispatcher.TAG, "DENY sending SMS");
                EventLog.writeEvent((int) EventLogTags.EXP_DET_SMS_DENIED_BY_USER, this.mTracker.mAppInfo.applicationInfo == null ? -1 : this.mTracker.mAppInfo.applicationInfo.uid);
                SMSDispatcher.this.sendMessage(SMSDispatcher.this.obtainMessage(7, this.mTracker));
                if (this.mRememberChoice) {
                    newSmsPermission = 2;
                }
            }
            SMSDispatcher.this.setPremiumSmsPermission(this.mTracker.mAppInfo.packageName, newSmsPermission);
        }

        @Override // android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialog) {
            Rlog.d(SMSDispatcher.TAG, "dialog dismissed: don't send SMS");
            SMSDispatcher.this.sendMessage(SMSDispatcher.this.obtainMessage(7, this.mTracker));
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Rlog.d(SMSDispatcher.TAG, "remember this choice: " + isChecked);
            this.mRememberChoice = isChecked;
            if (isChecked) {
                this.mPositiveButton.setText(R.string.sms_short_code_confirm_always_allow);
                this.mNegativeButton.setText(R.string.sms_short_code_confirm_never_allow);
                if (this.mRememberUndoInstruction != null) {
                    this.mRememberUndoInstruction.setText(R.string.sms_short_code_remember_undo_instruction);
                    this.mRememberUndoInstruction.setPadding(0, 0, 0, 32);
                    return;
                }
                return;
            }
            this.mPositiveButton.setText(R.string.sms_short_code_confirm_allow);
            this.mNegativeButton.setText(R.string.sms_short_code_confirm_deny);
            if (this.mRememberUndoInstruction != null) {
                this.mRememberUndoInstruction.setText("");
                this.mRememberUndoInstruction.setPadding(0, 0, 0, 0);
            }
        }
    }
}