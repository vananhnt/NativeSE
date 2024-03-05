package com.android.internal.telephony;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Process;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.Rlog;
import android.telephony.TelephonyManager;
import com.android.internal.R;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/* loaded from: SmsApplication.class */
public final class SmsApplication {
    static final String LOG_TAG = "SmsApplication";
    private static final String PHONE_PACKAGE_NAME = "com.android.phone";

    /* loaded from: SmsApplication$SmsApplicationData.class */
    public static class SmsApplicationData {
        public String mApplicationName;
        public String mPackageName;
        public String mSmsReceiverClass;
        public String mMmsReceiverClass;
        public String mRespondViaMessageClass;
        public String mSendToClass;
        public int mUid;

        public boolean isComplete() {
            return (this.mSmsReceiverClass == null || this.mMmsReceiverClass == null || this.mRespondViaMessageClass == null || this.mSendToClass == null) ? false : true;
        }

        public SmsApplicationData(String applicationName, String packageName, int uid) {
            this.mApplicationName = applicationName;
            this.mPackageName = packageName;
            this.mUid = uid;
        }
    }

    public static Collection<SmsApplicationData> getApplicationCollection(Context context) {
        String packageName;
        SmsApplicationData smsApplicationData;
        SmsApplicationData smsApplicationData2;
        SmsApplicationData smsApplicationData3;
        SmsApplicationData smsApplicationData4;
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> smsReceivers = packageManager.queryBroadcastReceivers(new Intent(Telephony.Sms.Intents.SMS_DELIVER_ACTION), 0);
        HashMap<String, SmsApplicationData> receivers = new HashMap<>();
        for (ResolveInfo resolveInfo : smsReceivers) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && Manifest.permission.BROADCAST_SMS.equals(activityInfo.permission)) {
                String packageName2 = activityInfo.packageName;
                if (!receivers.containsKey(packageName2)) {
                    String applicationName = resolveInfo.loadLabel(packageManager).toString();
                    SmsApplicationData smsApplicationData5 = new SmsApplicationData(applicationName, packageName2, activityInfo.applicationInfo.uid);
                    smsApplicationData5.mSmsReceiverClass = activityInfo.name;
                    receivers.put(packageName2, smsApplicationData5);
                }
            }
        }
        Intent intent = new Intent(Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION);
        intent.setDataAndType(null, WspTypeDecoder.CONTENT_TYPE_B_MMS);
        List<ResolveInfo> mmsReceivers = packageManager.queryBroadcastReceivers(intent, 0);
        for (ResolveInfo resolveInfo2 : mmsReceivers) {
            ActivityInfo activityInfo2 = resolveInfo2.activityInfo;
            if (activityInfo2 != null && Manifest.permission.BROADCAST_WAP_PUSH.equals(activityInfo2.permission) && (smsApplicationData4 = receivers.get(activityInfo2.packageName)) != null) {
                smsApplicationData4.mMmsReceiverClass = activityInfo2.name;
            }
        }
        List<ResolveInfo> respondServices = packageManager.queryIntentServices(new Intent(TelephonyManager.ACTION_RESPOND_VIA_MESSAGE, Uri.fromParts("smsto", "", null)), 0);
        for (ResolveInfo resolveInfo3 : respondServices) {
            ServiceInfo serviceInfo = resolveInfo3.serviceInfo;
            if (serviceInfo != null && Manifest.permission.SEND_RESPOND_VIA_MESSAGE.equals(serviceInfo.permission) && (smsApplicationData3 = receivers.get(serviceInfo.packageName)) != null) {
                smsApplicationData3.mRespondViaMessageClass = serviceInfo.name;
            }
        }
        List<ResolveInfo> sendToActivities = packageManager.queryIntentActivities(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", "", null)), 0);
        for (ResolveInfo resolveInfo4 : sendToActivities) {
            ActivityInfo activityInfo3 = resolveInfo4.activityInfo;
            if (activityInfo3 != null && (smsApplicationData2 = receivers.get(activityInfo3.packageName)) != null) {
                smsApplicationData2.mSendToClass = activityInfo3.name;
            }
        }
        for (ResolveInfo resolveInfo5 : smsReceivers) {
            ActivityInfo activityInfo4 = resolveInfo5.activityInfo;
            if (activityInfo4 != null && (smsApplicationData = receivers.get((packageName = activityInfo4.packageName))) != null && !smsApplicationData.isComplete()) {
                receivers.remove(packageName);
            }
        }
        return receivers.values();
    }

    private static SmsApplicationData getApplicationForPackage(Collection<SmsApplicationData> applications, String packageName) {
        if (packageName == null) {
            return null;
        }
        for (SmsApplicationData application : applications) {
            if (application.mPackageName.contentEquals(packageName)) {
                return application;
            }
        }
        return null;
    }

    private static SmsApplicationData getApplication(Context context, boolean updateIfNeeded) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        if (tm.getPhoneType() == 0) {
            return null;
        }
        Collection<SmsApplicationData> applications = getApplicationCollection(context);
        String defaultApplication = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.SMS_DEFAULT_APPLICATION);
        SmsApplicationData applicationData = null;
        if (defaultApplication != null) {
            applicationData = getApplicationForPackage(applications, defaultApplication);
        }
        if (updateIfNeeded && applicationData == null) {
            Resources r = context.getResources();
            String defaultPackage = r.getString(R.string.default_sms_application);
            applicationData = getApplicationForPackage(applications, defaultPackage);
            if (applicationData == null && applications.size() != 0) {
                applicationData = (SmsApplicationData) applications.toArray()[0];
            }
            if (applicationData != null) {
                setDefaultApplication(applicationData.mPackageName, context);
            }
        }
        if (applicationData != null) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (updateIfNeeded || applicationData.mUid == Process.myUid()) {
                int mode = appOps.checkOp(15, applicationData.mUid, applicationData.mPackageName);
                if (mode != 0) {
                    Rlog.e(LOG_TAG, applicationData.mPackageName + " lost OP_WRITE_SMS: " + (updateIfNeeded ? " (fixing)" : " (no permission to fix)"));
                    if (updateIfNeeded) {
                        appOps.setMode(15, applicationData.mUid, applicationData.mPackageName, 0);
                    } else {
                        applicationData = null;
                    }
                }
            }
            if (updateIfNeeded) {
                PackageManager packageManager = context.getPackageManager();
                try {
                    PackageInfo info = packageManager.getPackageInfo(PHONE_PACKAGE_NAME, 0);
                    int mode2 = appOps.checkOp(15, info.applicationInfo.uid, PHONE_PACKAGE_NAME);
                    if (mode2 != 0) {
                        Rlog.e(LOG_TAG, "com.android.phone lost OP_WRITE_SMS:  (fixing)");
                        appOps.setMode(15, info.applicationInfo.uid, PHONE_PACKAGE_NAME, 0);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Rlog.e(LOG_TAG, "Phone package not found: com.android.phone");
                    applicationData = null;
                }
            }
        }
        return applicationData;
    }

    public static void setDefaultApplication(String packageName, Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        if (tm.getPhoneType() == 0) {
            return;
        }
        String oldPackageName = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.SMS_DEFAULT_APPLICATION);
        if (packageName != null && oldPackageName != null && packageName.equals(oldPackageName)) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        Collection<SmsApplicationData> applications = getApplicationCollection(context);
        SmsApplicationData applicationData = getApplicationForPackage(applications, packageName);
        if (applicationData != null) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (oldPackageName != null) {
                try {
                    PackageInfo info = packageManager.getPackageInfo(oldPackageName, 8192);
                    appOps.setMode(15, info.applicationInfo.uid, oldPackageName, 1);
                } catch (PackageManager.NameNotFoundException e) {
                    Rlog.w(LOG_TAG, "Old SMS package not found: " + oldPackageName);
                }
            }
            Settings.Secure.putString(context.getContentResolver(), Settings.Secure.SMS_DEFAULT_APPLICATION, applicationData.mPackageName);
            appOps.setMode(15, applicationData.mUid, applicationData.mPackageName, 0);
            try {
                PackageInfo info2 = packageManager.getPackageInfo(PHONE_PACKAGE_NAME, 0);
                appOps.setMode(15, info2.applicationInfo.uid, PHONE_PACKAGE_NAME, 0);
            } catch (PackageManager.NameNotFoundException e2) {
                Rlog.e(LOG_TAG, "Phone package not found: com.android.phone");
            }
        }
    }

    public static SmsApplicationData getSmsApplicationData(String packageName, Context context) {
        Collection<SmsApplicationData> applications = getApplicationCollection(context);
        return getApplicationForPackage(applications, packageName);
    }

    public static ComponentName getDefaultSmsApplication(Context context, boolean updateIfNeeded) {
        ComponentName component = null;
        SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded);
        if (smsApplicationData != null) {
            component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mSmsReceiverClass);
        }
        return component;
    }

    public static ComponentName getDefaultMmsApplication(Context context, boolean updateIfNeeded) {
        ComponentName component = null;
        SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded);
        if (smsApplicationData != null) {
            component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mMmsReceiverClass);
        }
        return component;
    }

    public static ComponentName getDefaultRespondViaMessageApplication(Context context, boolean updateIfNeeded) {
        ComponentName component = null;
        SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded);
        if (smsApplicationData != null) {
            component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mRespondViaMessageClass);
        }
        return component;
    }

    public static ComponentName getDefaultSendToApplication(Context context, boolean updateIfNeeded) {
        ComponentName component = null;
        SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded);
        if (smsApplicationData != null) {
            component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mSendToClass);
        }
        return component;
    }
}