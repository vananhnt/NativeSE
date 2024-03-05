package android.speech.tts;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: TtsEngines.class */
public class TtsEngines {
    private static final String TAG = "TtsEngines";
    private static final boolean DBG = false;
    private static final String LOCALE_DELIMITER = "-";
    private final Context mContext;
    private static final String XML_TAG_NAME = "tts-engine";

    public TtsEngines(Context ctx) {
        this.mContext = ctx;
    }

    public String getDefaultEngine() {
        String engine = Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.TTS_DEFAULT_SYNTH);
        return isEngineInstalled(engine) ? engine : getHighestRankedEngineName();
    }

    public String getHighestRankedEngineName() {
        List<TextToSpeech.EngineInfo> engines = getEngines();
        if (engines.size() > 0 && engines.get(0).system) {
            return engines.get(0).name;
        }
        return null;
    }

    public TextToSpeech.EngineInfo getEngineInfo(String packageName) {
        PackageManager pm = this.mContext.getPackageManager();
        Intent intent = new Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE);
        intent.setPackage(packageName);
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(intent, 65536);
        if (resolveInfos != null && resolveInfos.size() == 1) {
            return getEngineInfo(resolveInfos.get(0), pm);
        }
        return null;
    }

    public List<TextToSpeech.EngineInfo> getEngines() {
        PackageManager pm = this.mContext.getPackageManager();
        Intent intent = new Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE);
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(intent, 65536);
        if (resolveInfos == null) {
            return Collections.emptyList();
        }
        List<TextToSpeech.EngineInfo> engines = new ArrayList<>(resolveInfos.size());
        for (ResolveInfo resolveInfo : resolveInfos) {
            TextToSpeech.EngineInfo engine = getEngineInfo(resolveInfo, pm);
            if (engine != null) {
                engines.add(engine);
            }
        }
        Collections.sort(engines, EngineInfoComparator.INSTANCE);
        return engines;
    }

    private boolean isSystemEngine(ServiceInfo info) {
        ApplicationInfo appInfo = info.applicationInfo;
        return (appInfo == null || (appInfo.flags & 1) == 0) ? false : true;
    }

    public boolean isEngineInstalled(String engine) {
        return (engine == null || getEngineInfo(engine) == null) ? false : true;
    }

    public Intent getSettingsIntent(String engine) {
        ServiceInfo service;
        String settings;
        PackageManager pm = this.mContext.getPackageManager();
        Intent intent = new Intent(TextToSpeech.Engine.INTENT_ACTION_TTS_SERVICE);
        intent.setPackage(engine);
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(intent, 65664);
        if (resolveInfos != null && resolveInfos.size() == 1 && (service = resolveInfos.get(0).serviceInfo) != null && (settings = settingsActivityFromServiceInfo(service, pm)) != null) {
            Intent i = new Intent();
            i.setClassName(engine, settings);
            return i;
        }
        return null;
    }

    private String settingsActivityFromServiceInfo(ServiceInfo si, PackageManager pm) {
        int type;
        XmlResourceParser parser = null;
        try {
            try {
                XmlResourceParser parser2 = si.loadXmlMetaData(pm, TextToSpeech.Engine.SERVICE_META_DATA);
                if (parser2 == null) {
                    Log.w(TAG, "No meta-data found for :" + si);
                    if (parser2 != null) {
                        parser2.close();
                    }
                    return null;
                }
                Resources res = pm.getResourcesForApplication(si.applicationInfo);
                do {
                    type = parser2.next();
                    if (type == 1) {
                        if (parser2 != null) {
                            parser2.close();
                        }
                        return null;
                    }
                } while (type != 2);
                if (!XML_TAG_NAME.equals(parser2.getName())) {
                    Log.w(TAG, "Package " + si + " uses unknown tag :" + parser2.getName());
                    if (parser2 != null) {
                        parser2.close();
                    }
                    return null;
                }
                AttributeSet attrs = Xml.asAttributeSet(parser2);
                TypedArray array = res.obtainAttributes(attrs, R.styleable.TextToSpeechEngine);
                String settings = array.getString(0);
                array.recycle();
                if (parser2 != null) {
                    parser2.close();
                }
                return settings;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Could not load resources for : " + si);
                if (0 != 0) {
                    parser.close();
                }
                return null;
            } catch (IOException e2) {
                Log.w(TAG, "Error parsing metadata for " + si + Separators.COLON + e2);
                if (0 != 0) {
                    parser.close();
                }
                return null;
            } catch (XmlPullParserException e3) {
                Log.w(TAG, "Error parsing metadata for " + si + Separators.COLON + e3);
                if (0 != 0) {
                    parser.close();
                }
                return null;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                parser.close();
            }
            throw th;
        }
    }

    private TextToSpeech.EngineInfo getEngineInfo(ResolveInfo resolve, PackageManager pm) {
        ServiceInfo service = resolve.serviceInfo;
        if (service != null) {
            TextToSpeech.EngineInfo engine = new TextToSpeech.EngineInfo();
            engine.name = service.packageName;
            CharSequence label = service.loadLabel(pm);
            engine.label = TextUtils.isEmpty(label) ? engine.name : label.toString();
            engine.icon = service.getIconResource();
            engine.priority = resolve.priority;
            engine.system = isSystemEngine(service);
            return engine;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: TtsEngines$EngineInfoComparator.class */
    public static class EngineInfoComparator implements Comparator<TextToSpeech.EngineInfo> {
        static EngineInfoComparator INSTANCE = new EngineInfoComparator();

        private EngineInfoComparator() {
        }

        @Override // java.util.Comparator
        public int compare(TextToSpeech.EngineInfo lhs, TextToSpeech.EngineInfo rhs) {
            if (lhs.system && !rhs.system) {
                return -1;
            }
            if (rhs.system && !lhs.system) {
                return 1;
            }
            return rhs.priority - lhs.priority;
        }
    }

    public String getLocalePrefForEngine(String engineName) {
        String locale = parseEnginePrefFromList(Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.TTS_DEFAULT_LOCALE), engineName);
        if (TextUtils.isEmpty(locale)) {
            locale = getV1Locale();
        }
        return locale;
    }

    public static String[] parseLocalePref(String pref) {
        String[] returnVal = {"", "", ""};
        if (!TextUtils.isEmpty(pref)) {
            String[] split = pref.split(LOCALE_DELIMITER);
            System.arraycopy(split, 0, returnVal, 0, split.length);
        }
        return returnVal;
    }

    private String getV1Locale() {
        ContentResolver cr = this.mContext.getContentResolver();
        String lang = Settings.Secure.getString(cr, Settings.Secure.TTS_DEFAULT_LANG);
        String country = Settings.Secure.getString(cr, Settings.Secure.TTS_DEFAULT_COUNTRY);
        String variant = Settings.Secure.getString(cr, Settings.Secure.TTS_DEFAULT_VARIANT);
        if (TextUtils.isEmpty(lang)) {
            return getDefaultLocale();
        }
        if (!TextUtils.isEmpty(country)) {
            String v1Locale = lang + LOCALE_DELIMITER + country;
            if (!TextUtils.isEmpty(variant)) {
                v1Locale = v1Locale + LOCALE_DELIMITER + variant;
            }
            return v1Locale;
        }
        return lang;
    }

    public String getDefaultLocale() {
        Locale locale = Locale.getDefault();
        String defaultLocale = locale.getISO3Language();
        if (TextUtils.isEmpty(defaultLocale)) {
            Log.w(TAG, "Default locale is empty.");
            return "";
        } else if (!TextUtils.isEmpty(locale.getISO3Country())) {
            String defaultLocale2 = defaultLocale + LOCALE_DELIMITER + locale.getISO3Country();
            if (!TextUtils.isEmpty(locale.getVariant())) {
                defaultLocale2 = defaultLocale2 + LOCALE_DELIMITER + locale.getVariant();
            }
            return defaultLocale2;
        } else {
            return defaultLocale;
        }
    }

    private static String parseEnginePrefFromList(String prefValue, String engineName) {
        if (TextUtils.isEmpty(prefValue)) {
            return null;
        }
        String[] prefValues = prefValue.split(Separators.COMMA);
        for (String value : prefValues) {
            int delimiter = value.indexOf(58);
            if (delimiter > 0 && engineName.equals(value.substring(0, delimiter))) {
                return value.substring(delimiter + 1);
            }
        }
        return null;
    }

    public synchronized void updateLocalePrefForEngine(String name, String newLocale) {
        String prefList = Settings.Secure.getString(this.mContext.getContentResolver(), Settings.Secure.TTS_DEFAULT_LOCALE);
        String newPrefList = updateValueInCommaSeparatedList(prefList, name, newLocale);
        Settings.Secure.putString(this.mContext.getContentResolver(), Settings.Secure.TTS_DEFAULT_LOCALE, newPrefList.toString());
    }

    private String updateValueInCommaSeparatedList(String list, String key, String newValue) {
        StringBuilder newPrefList = new StringBuilder();
        if (TextUtils.isEmpty(list)) {
            newPrefList.append(key).append(':').append(newValue);
        } else {
            String[] prefValues = list.split(Separators.COMMA);
            boolean first = true;
            boolean found = false;
            for (String value : prefValues) {
                int delimiter = value.indexOf(58);
                if (delimiter > 0) {
                    if (key.equals(value.substring(0, delimiter))) {
                        if (first) {
                            first = false;
                        } else {
                            newPrefList.append(',');
                        }
                        found = true;
                        newPrefList.append(key).append(':').append(newValue);
                    } else {
                        if (first) {
                            first = false;
                        } else {
                            newPrefList.append(',');
                        }
                        newPrefList.append(value);
                    }
                }
            }
            if (!found) {
                newPrefList.append(',');
                newPrefList.append(key).append(':').append(newValue);
            }
        }
        return newPrefList.toString();
    }
}