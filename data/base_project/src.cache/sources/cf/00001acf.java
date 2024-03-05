package com.android.internal.telephony;

import android.app.AppGlobals;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.telephony.Rlog;
import android.util.AtomicFile;
import android.util.Xml;
import com.android.internal.R;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: SmsUsageMonitor.class */
public class SmsUsageMonitor {
    private static final String TAG = "SmsUsageMonitor";
    private static final boolean DBG = false;
    private static final boolean VDBG = false;
    private static final String SHORT_CODE_PATH = "/data/misc/sms/codes";
    private static final int DEFAULT_SMS_CHECK_PERIOD = 60000;
    private static final int DEFAULT_SMS_MAX_COUNT = 30;
    static final int CATEGORY_NOT_SHORT_CODE = 0;
    static final int CATEGORY_FREE_SHORT_CODE = 1;
    static final int CATEGORY_STANDARD_SHORT_CODE = 2;
    static final int CATEGORY_POSSIBLE_PREMIUM_SHORT_CODE = 3;
    static final int CATEGORY_PREMIUM_SHORT_CODE = 4;
    public static final int PREMIUM_SMS_PERMISSION_UNKNOWN = 0;
    public static final int PREMIUM_SMS_PERMISSION_ASK_USER = 1;
    public static final int PREMIUM_SMS_PERMISSION_NEVER_ALLOW = 2;
    public static final int PREMIUM_SMS_PERMISSION_ALWAYS_ALLOW = 3;
    private final int mCheckPeriod;
    private final int mMaxAllowed;
    private final Context mContext;
    private String mCurrentCountry;
    private ShortCodePatternMatcher mCurrentPatternMatcher;
    private final SettingsObserverHandler mSettingsObserverHandler;
    private static final String SMS_POLICY_FILE_DIRECTORY = "/data/misc/sms";
    private static final String SMS_POLICY_FILE_NAME = "premium_sms_policy.xml";
    private static final String TAG_SHORTCODES = "shortcodes";
    private static final String TAG_SHORTCODE = "shortcode";
    private static final String ATTR_COUNTRY = "country";
    private static final String ATTR_PATTERN = "pattern";
    private static final String ATTR_PREMIUM = "premium";
    private static final String ATTR_FREE = "free";
    private static final String ATTR_STANDARD = "standard";
    private AtomicFile mPolicyFile;
    private static final String TAG_SMS_POLICY_BODY = "premium-sms-policy";
    private static final String TAG_PACKAGE = "package";
    private static final String ATTR_PACKAGE_NAME = "name";
    private static final String ATTR_PACKAGE_SMS_POLICY = "sms-policy";
    private final HashMap<String, ArrayList<Long>> mSmsStamp = new HashMap<>();
    private final AtomicBoolean mCheckEnabled = new AtomicBoolean(true);
    private final File mPatternFile = new File(SHORT_CODE_PATH);
    private long mPatternFileLastModified = 0;
    private final HashMap<String, Integer> mPremiumSmsPolicy = new HashMap<>();

    public static int mergeShortCodeCategories(int type1, int type2) {
        return type1 > type2 ? type1 : type2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SmsUsageMonitor$ShortCodePatternMatcher.class */
    public static final class ShortCodePatternMatcher {
        private final Pattern mShortCodePattern;
        private final Pattern mPremiumShortCodePattern;
        private final Pattern mFreeShortCodePattern;
        private final Pattern mStandardShortCodePattern;

        ShortCodePatternMatcher(String shortCodeRegex, String premiumShortCodeRegex, String freeShortCodeRegex, String standardShortCodeRegex) {
            this.mShortCodePattern = shortCodeRegex != null ? Pattern.compile(shortCodeRegex) : null;
            this.mPremiumShortCodePattern = premiumShortCodeRegex != null ? Pattern.compile(premiumShortCodeRegex) : null;
            this.mFreeShortCodePattern = freeShortCodeRegex != null ? Pattern.compile(freeShortCodeRegex) : null;
            this.mStandardShortCodePattern = standardShortCodeRegex != null ? Pattern.compile(standardShortCodeRegex) : null;
        }

        int getNumberCategory(String phoneNumber) {
            if (this.mFreeShortCodePattern != null && this.mFreeShortCodePattern.matcher(phoneNumber).matches()) {
                return 1;
            }
            if (this.mStandardShortCodePattern != null && this.mStandardShortCodePattern.matcher(phoneNumber).matches()) {
                return 2;
            }
            if (this.mPremiumShortCodePattern != null && this.mPremiumShortCodePattern.matcher(phoneNumber).matches()) {
                return 4;
            }
            if (this.mShortCodePattern != null && this.mShortCodePattern.matcher(phoneNumber).matches()) {
                return 3;
            }
            return 0;
        }
    }

    /* loaded from: SmsUsageMonitor$SettingsObserver.class */
    private static class SettingsObserver extends ContentObserver {
        private final Context mContext;
        private final AtomicBoolean mEnabled;

        SettingsObserver(Handler handler, Context context, AtomicBoolean enabled) {
            super(handler);
            this.mContext = context;
            this.mEnabled = enabled;
            onChange(false);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            this.mEnabled.set(Settings.Global.getInt(this.mContext.getContentResolver(), Settings.Global.SMS_SHORT_CODE_CONFIRMATION, 1) != 0);
        }
    }

    /* loaded from: SmsUsageMonitor$SettingsObserverHandler.class */
    private static class SettingsObserverHandler extends Handler {
        SettingsObserverHandler(Context context, AtomicBoolean enabled) {
            ContentResolver resolver = context.getContentResolver();
            ContentObserver globalObserver = new SettingsObserver(this, context, enabled);
            resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.SMS_SHORT_CODE_CONFIRMATION), false, globalObserver);
        }
    }

    public SmsUsageMonitor(Context context) {
        this.mContext = context;
        ContentResolver resolver = context.getContentResolver();
        this.mMaxAllowed = Settings.Global.getInt(resolver, Settings.Global.SMS_OUTGOING_CHECK_MAX_COUNT, 30);
        this.mCheckPeriod = Settings.Global.getInt(resolver, Settings.Global.SMS_OUTGOING_CHECK_INTERVAL_MS, 60000);
        this.mSettingsObserverHandler = new SettingsObserverHandler(this.mContext, this.mCheckEnabled);
        loadPremiumSmsPolicyDb();
    }

    private ShortCodePatternMatcher getPatternMatcherFromFile(String country) {
        FileReader patternReader = null;
        try {
            try {
                patternReader = new FileReader(this.mPatternFile);
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(patternReader);
                ShortCodePatternMatcher patternMatcherFromXmlParser = getPatternMatcherFromXmlParser(parser, country);
                this.mPatternFileLastModified = this.mPatternFile.lastModified();
                if (patternReader != null) {
                    try {
                        patternReader.close();
                    } catch (IOException e) {
                    }
                }
                return patternMatcherFromXmlParser;
            } catch (FileNotFoundException e2) {
                Rlog.e(TAG, "Short Code Pattern File not found");
                this.mPatternFileLastModified = this.mPatternFile.lastModified();
                if (patternReader != null) {
                    try {
                        patternReader.close();
                        return null;
                    } catch (IOException e3) {
                        return null;
                    }
                }
                return null;
            } catch (XmlPullParserException e4) {
                Rlog.e(TAG, "XML parser exception reading short code pattern file", e4);
                this.mPatternFileLastModified = this.mPatternFile.lastModified();
                if (patternReader != null) {
                    try {
                        patternReader.close();
                        return null;
                    } catch (IOException e5) {
                        return null;
                    }
                }
                return null;
            }
        } catch (Throwable th) {
            this.mPatternFileLastModified = this.mPatternFile.lastModified();
            if (patternReader != null) {
                try {
                    patternReader.close();
                } catch (IOException e6) {
                }
            }
            throw th;
        }
    }

    private ShortCodePatternMatcher getPatternMatcherFromResource(String country) {
        XmlResourceParser parser = null;
        try {
            parser = this.mContext.getResources().getXml(R.xml.sms_short_codes);
            ShortCodePatternMatcher patternMatcherFromXmlParser = getPatternMatcherFromXmlParser(parser, country);
            if (parser != null) {
                parser.close();
            }
            return patternMatcherFromXmlParser;
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
            throw th;
        }
    }

    private ShortCodePatternMatcher getPatternMatcherFromXmlParser(XmlPullParser parser, String country) {
        try {
            XmlUtils.beginDocument(parser, TAG_SHORTCODES);
            while (true) {
                XmlUtils.nextElement(parser);
                String element = parser.getName();
                if (element == null) {
                    Rlog.e(TAG, "Parsing pattern data found null");
                    return null;
                } else if (element.equals(TAG_SHORTCODE)) {
                    String currentCountry = parser.getAttributeValue(null, "country");
                    if (country.equals(currentCountry)) {
                        String pattern = parser.getAttributeValue(null, ATTR_PATTERN);
                        String premium = parser.getAttributeValue(null, ATTR_PREMIUM);
                        String free = parser.getAttributeValue(null, ATTR_FREE);
                        String standard = parser.getAttributeValue(null, ATTR_STANDARD);
                        return new ShortCodePatternMatcher(pattern, premium, free, standard);
                    }
                } else {
                    Rlog.e(TAG, "Error: skipping unknown XML tag " + element);
                }
            }
        } catch (IOException e) {
            Rlog.e(TAG, "I/O exception reading short code patterns", e);
            return null;
        } catch (XmlPullParserException e2) {
            Rlog.e(TAG, "XML parser exception reading short code patterns", e2);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispose() {
        this.mSmsStamp.clear();
    }

    public boolean check(String appName, int smsWaiting) {
        boolean isUnderLimit;
        synchronized (this.mSmsStamp) {
            removeExpiredTimestamps();
            ArrayList<Long> sentList = this.mSmsStamp.get(appName);
            if (sentList == null) {
                sentList = new ArrayList<>();
                this.mSmsStamp.put(appName, sentList);
            }
            isUnderLimit = isUnderLimit(sentList, smsWaiting);
        }
        return isUnderLimit;
    }

    public int checkDestination(String destAddress, String countryIso) {
        synchronized (this.mSettingsObserverHandler) {
            if (PhoneNumberUtils.isEmergencyNumber(destAddress, countryIso)) {
                return 0;
            }
            if (!this.mCheckEnabled.get()) {
                return 0;
            }
            if (countryIso != null && (this.mCurrentCountry == null || !countryIso.equals(this.mCurrentCountry) || this.mPatternFile.lastModified() != this.mPatternFileLastModified)) {
                if (this.mPatternFile.exists()) {
                    this.mCurrentPatternMatcher = getPatternMatcherFromFile(countryIso);
                } else {
                    this.mCurrentPatternMatcher = getPatternMatcherFromResource(countryIso);
                }
                this.mCurrentCountry = countryIso;
            }
            if (this.mCurrentPatternMatcher != null) {
                return this.mCurrentPatternMatcher.getNumberCategory(destAddress);
            }
            Rlog.e(TAG, "No patterns for \"" + countryIso + "\": using generic short code rule");
            if (destAddress.length() <= 5) {
                return 3;
            }
            return 0;
        }
    }

    private void loadPremiumSmsPolicyDb() {
        synchronized (this.mPremiumSmsPolicy) {
            if (this.mPolicyFile == null) {
                File dir = new File(SMS_POLICY_FILE_DIRECTORY);
                this.mPolicyFile = new AtomicFile(new File(dir, SMS_POLICY_FILE_NAME));
                this.mPremiumSmsPolicy.clear();
                FileInputStream infile = null;
                try {
                    infile = this.mPolicyFile.openRead();
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(infile, null);
                    XmlUtils.beginDocument(parser, TAG_SMS_POLICY_BODY);
                    while (true) {
                        XmlUtils.nextElement(parser);
                        String element = parser.getName();
                        if (element == null) {
                            break;
                        } else if (element.equals("package")) {
                            String packageName = parser.getAttributeValue(null, "name");
                            String policy = parser.getAttributeValue(null, ATTR_PACKAGE_SMS_POLICY);
                            if (packageName == null) {
                                Rlog.e(TAG, "Error: missing package name attribute");
                            } else if (policy == null) {
                                Rlog.e(TAG, "Error: missing package policy attribute");
                            } else {
                                try {
                                    this.mPremiumSmsPolicy.put(packageName, Integer.valueOf(Integer.parseInt(policy)));
                                } catch (NumberFormatException e) {
                                    Rlog.e(TAG, "Error: non-numeric policy type " + policy);
                                }
                            }
                        } else {
                            Rlog.e(TAG, "Error: skipping unknown XML tag " + element);
                        }
                    }
                    if (infile != null) {
                        try {
                            infile.close();
                        } catch (IOException e2) {
                        }
                    }
                } catch (FileNotFoundException e3) {
                    if (infile != null) {
                        try {
                            infile.close();
                        } catch (IOException e4) {
                        }
                    }
                } catch (IOException e5) {
                    Rlog.e(TAG, "Unable to read premium SMS policy database", e5);
                    if (infile != null) {
                        try {
                            infile.close();
                        } catch (IOException e6) {
                        }
                    }
                } catch (NumberFormatException e7) {
                    Rlog.e(TAG, "Unable to parse premium SMS policy database", e7);
                    if (infile != null) {
                        try {
                            infile.close();
                        } catch (IOException e8) {
                        }
                    }
                } catch (XmlPullParserException e9) {
                    Rlog.e(TAG, "Unable to parse premium SMS policy database", e9);
                    if (infile != null) {
                        try {
                            infile.close();
                        } catch (IOException e10) {
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writePremiumSmsPolicyDb() {
        synchronized (this.mPremiumSmsPolicy) {
            FileOutputStream outfile = null;
            try {
                outfile = this.mPolicyFile.startWrite();
                XmlSerializer out = new FastXmlSerializer();
                out.setOutput(outfile, "utf-8");
                out.startDocument(null, true);
                out.startTag(null, TAG_SMS_POLICY_BODY);
                for (Map.Entry<String, Integer> policy : this.mPremiumSmsPolicy.entrySet()) {
                    out.startTag(null, "package");
                    out.attribute(null, "name", policy.getKey());
                    out.attribute(null, ATTR_PACKAGE_SMS_POLICY, policy.getValue().toString());
                    out.endTag(null, "package");
                }
                out.endTag(null, TAG_SMS_POLICY_BODY);
                out.endDocument();
                this.mPolicyFile.finishWrite(outfile);
            } catch (IOException e) {
                Rlog.e(TAG, "Unable to write premium SMS policy database", e);
                if (outfile != null) {
                    this.mPolicyFile.failWrite(outfile);
                }
            }
        }
    }

    public int getPremiumSmsPermission(String packageName) {
        checkCallerIsSystemOrSameApp(packageName);
        synchronized (this.mPremiumSmsPolicy) {
            Integer policy = this.mPremiumSmsPolicy.get(packageName);
            if (policy == null) {
                return 0;
            }
            return policy.intValue();
        }
    }

    public void setPremiumSmsPermission(String packageName, int permission) {
        checkCallerIsSystemOrPhoneApp();
        if (permission < 1 || permission > 3) {
            throw new IllegalArgumentException("invalid SMS permission type " + permission);
        }
        synchronized (this.mPremiumSmsPolicy) {
            this.mPremiumSmsPolicy.put(packageName, Integer.valueOf(permission));
        }
        new Thread(new Runnable() { // from class: com.android.internal.telephony.SmsUsageMonitor.1
            @Override // java.lang.Runnable
            public void run() {
                SmsUsageMonitor.this.writePremiumSmsPolicyDb();
            }
        }).start();
    }

    private static void checkCallerIsSystemOrSameApp(String pkg) {
        int uid = Binder.getCallingUid();
        if (UserHandle.getAppId(uid) == 1000 || uid == 0) {
            return;
        }
        try {
            ApplicationInfo ai = AppGlobals.getPackageManager().getApplicationInfo(pkg, 0, UserHandle.getCallingUserId());
            if (!UserHandle.isSameApp(ai.uid, uid)) {
                throw new SecurityException("Calling uid " + uid + " gave package" + pkg + " which is owned by uid " + ai.uid);
            }
        } catch (RemoteException re) {
            throw new SecurityException("Unknown package " + pkg + Separators.RETURN + re);
        }
    }

    private static void checkCallerIsSystemOrPhoneApp() {
        int uid = Binder.getCallingUid();
        int appId = UserHandle.getAppId(uid);
        if (appId == 1000 || appId == 1001 || uid == 0) {
            return;
        }
        throw new SecurityException("Disallowed call for uid " + uid);
    }

    private void removeExpiredTimestamps() {
        long beginCheckPeriod = System.currentTimeMillis() - this.mCheckPeriod;
        synchronized (this.mSmsStamp) {
            Iterator<Map.Entry<String, ArrayList<Long>>> iter = this.mSmsStamp.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, ArrayList<Long>> entry = iter.next();
                ArrayList<Long> oldList = entry.getValue();
                if (oldList.isEmpty() || oldList.get(oldList.size() - 1).longValue() < beginCheckPeriod) {
                    iter.remove();
                }
            }
        }
    }

    private boolean isUnderLimit(ArrayList<Long> sent, int smsWaiting) {
        Long ct = Long.valueOf(System.currentTimeMillis());
        long beginCheckPeriod = ct.longValue() - this.mCheckPeriod;
        while (!sent.isEmpty() && sent.get(0).longValue() < beginCheckPeriod) {
            sent.remove(0);
        }
        if (sent.size() + smsWaiting <= this.mMaxAllowed) {
            for (int i = 0; i < smsWaiting; i++) {
                sent.add(ct);
            }
            return true;
        }
        return false;
    }

    private static void log(String msg) {
        Rlog.d(TAG, msg);
    }
}