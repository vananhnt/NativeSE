package com.android.internal.telephony;

import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.videoeditor.MediaProperties;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.widget.SpellChecker;
import com.android.internal.R;
import com.android.server.MountService;
import com.android.server.NetworkManagementService;
import com.android.server.NsdService;
import gov.nist.javax.sip.header.ims.AuthorizationHeaderIms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import javax.sip.message.Response;
import libcore.icu.TimeZoneNames;

/* loaded from: MccTable.class */
public final class MccTable {
    static final String LOG_TAG = "MccTable";
    static ArrayList<MccEntry> sTable = new ArrayList<>(240);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: MccTable$MccEntry.class */
    public static class MccEntry implements Comparable<MccEntry> {
        int mMcc;
        String mIso;
        int mSmallestDigitsMnc;
        String mLanguage;

        MccEntry(int mnc, String iso, int smallestDigitsMCC) {
            this(mnc, iso, smallestDigitsMCC, null);
        }

        MccEntry(int mnc, String iso, int smallestDigitsMCC, String language) {
            this.mMcc = mnc;
            this.mIso = iso;
            this.mSmallestDigitsMnc = smallestDigitsMCC;
            this.mLanguage = language;
        }

        @Override // java.lang.Comparable
        public int compareTo(MccEntry o) {
            return this.mMcc - o.mMcc;
        }
    }

    private static MccEntry entryForMcc(int mcc) {
        MccEntry m = new MccEntry(mcc, null, 0);
        int index = Collections.binarySearch(sTable, m);
        if (index < 0) {
            return null;
        }
        return sTable.get(index);
    }

    public static String defaultTimeZoneForMcc(int mcc) {
        Locale locale;
        MccEntry entry = entryForMcc(mcc);
        if (entry == null || entry.mIso == null) {
            return null;
        }
        if (entry.mLanguage == null) {
            locale = new Locale(entry.mIso);
        } else {
            locale = new Locale(entry.mLanguage, entry.mIso);
        }
        String[] tz = TimeZoneNames.forLocale(locale);
        if (tz.length == 0) {
            return null;
        }
        return tz[0];
    }

    public static String countryCodeForMcc(int mcc) {
        MccEntry entry = entryForMcc(mcc);
        if (entry == null) {
            return "";
        }
        return entry.mIso;
    }

    public static String defaultLanguageForMcc(int mcc) {
        MccEntry entry = entryForMcc(mcc);
        if (entry == null) {
            return null;
        }
        return entry.mLanguage;
    }

    public static int smallestDigitsMccForMnc(int mcc) {
        MccEntry entry = entryForMcc(mcc);
        if (entry == null) {
            return 2;
        }
        return entry.mSmallestDigitsMnc;
    }

    public static void updateMccMncConfiguration(Context context, String mccmnc) {
        if (!TextUtils.isEmpty(mccmnc)) {
            try {
                int mcc = Integer.parseInt(mccmnc.substring(0, 3));
                int mnc = Integer.parseInt(mccmnc.substring(3));
                Slog.d(LOG_TAG, "updateMccMncConfiguration: mcc=" + mcc + ", mnc=" + mnc);
                Locale locale = null;
                if (mcc != 0) {
                    setTimezoneFromMccIfNeeded(context, mcc);
                    locale = getLocaleFromMcc(context, mcc);
                    setWifiCountryCodeFromMcc(context, mcc);
                }
                try {
                    Configuration config = new Configuration();
                    boolean updateConfig = false;
                    if (mcc != 0) {
                        config.mcc = mcc;
                        config.mnc = mnc == 0 ? 65535 : mnc;
                        updateConfig = true;
                    }
                    if (locale != null) {
                        config.setLocale(locale);
                        updateConfig = true;
                    }
                    if (updateConfig) {
                        Slog.d(LOG_TAG, "updateMccMncConfiguration updateConfig config=" + config);
                        ActivityManagerNative.getDefault().updateConfiguration(config);
                    } else {
                        Slog.d(LOG_TAG, "updateMccMncConfiguration nothing to update");
                    }
                } catch (RemoteException e) {
                    Slog.e(LOG_TAG, "Can't update configuration", e);
                }
            } catch (NumberFormatException e2) {
                Slog.e(LOG_TAG, "Error parsing IMSI");
            }
        }
    }

    public static Locale getLocaleForLanguageCountry(Context context, String language, String country) {
        Locale locale;
        String l = SystemProperties.get("persist.sys.language");
        String c = SystemProperties.get("persist.sys.country");
        if (null == language) {
            Slog.d(LOG_TAG, "getLocaleForLanguageCountry: skipping no language");
            return null;
        }
        String language2 = language.toLowerCase(Locale.ROOT);
        if (null == country) {
            country = "";
        }
        String country2 = country.toUpperCase(Locale.ROOT);
        boolean alwaysPersist = false;
        if (Build.IS_DEBUGGABLE) {
            alwaysPersist = SystemProperties.getBoolean("persist.always.persist.locale", false);
        }
        if (alwaysPersist || ((null == l || 0 == l.length()) && (null == c || 0 == c.length()))) {
            try {
                String[] locales = context.getAssets().getLocales();
                int N = locales.length;
                String bestMatch = null;
                int i = 0;
                while (true) {
                    if (i >= N) {
                        break;
                    }
                    if (locales[i] != null && locales[i].length() >= 5 && locales[i].substring(0, 2).equals(language2)) {
                        if (locales[i].substring(3, 5).equals(country2)) {
                            bestMatch = locales[i];
                            break;
                        } else if (null == bestMatch) {
                            bestMatch = locales[i];
                        }
                    }
                    i++;
                }
                if (null != bestMatch) {
                    locale = new Locale(bestMatch.substring(0, 2), bestMatch.substring(3, 5));
                    Slog.d(LOG_TAG, "getLocaleForLanguageCountry: got match");
                } else {
                    locale = null;
                    Slog.d(LOG_TAG, "getLocaleForLanguageCountry: skip no match");
                }
            } catch (Exception e) {
                locale = null;
                Slog.d(LOG_TAG, "getLocaleForLanguageCountry: exception", e);
            }
        } else {
            locale = null;
            Slog.d(LOG_TAG, "getLocaleForLanguageCountry: skipping already persisted");
        }
        Slog.d(LOG_TAG, "getLocaleForLanguageCountry: X locale=" + locale);
        return locale;
    }

    public static void setSystemLocale(Context context, String language, String country) {
        Locale locale = getLocaleForLanguageCountry(context, language, country);
        if (locale != null) {
            Configuration config = new Configuration();
            config.setLocale(locale);
            Slog.d(LOG_TAG, "setSystemLocale: updateLocale config=" + config);
            try {
                ActivityManagerNative.getDefault().updateConfiguration(config);
                return;
            } catch (RemoteException e) {
                Slog.d(LOG_TAG, "setSystemLocale exception", e);
                return;
            }
        }
        Slog.d(LOG_TAG, "setSystemLocale: no locale");
    }

    private static void setTimezoneFromMccIfNeeded(Context context, int mcc) {
        String zoneId;
        String timezone = SystemProperties.get("persist.sys.timezone");
        if ((timezone == null || timezone.length() == 0) && (zoneId = defaultTimeZoneForMcc(mcc)) != null && zoneId.length() > 0) {
            AlarmManager alarm = (AlarmManager) context.getSystemService("alarm");
            alarm.setTimeZone(zoneId);
            Slog.d(LOG_TAG, "timezone set to " + zoneId);
        }
    }

    private static Locale getLocaleFromMcc(Context context, int mcc) {
        String language = defaultLanguageForMcc(mcc);
        String country = countryCodeForMcc(mcc);
        Slog.d(LOG_TAG, "getLocaleFromMcc to " + language + "_" + country + " mcc=" + mcc);
        return getLocaleForLanguageCountry(context, language, country);
    }

    private static void setWifiCountryCodeFromMcc(Context context, int mcc) {
        String country = countryCodeForMcc(mcc);
        if (!country.isEmpty()) {
            Slog.d(LOG_TAG, "WIFI_COUNTRY_CODE set to " + country);
            WifiManager wM = (WifiManager) context.getSystemService("wifi");
            wM.setCountryCode(country, true);
        }
    }

    static {
        sTable.add(new MccEntry(202, "gr", 2));
        sTable.add(new MccEntry(204, "nl", 2, "nl"));
        sTable.add(new MccEntry(206, "be", 2));
        sTable.add(new MccEntry(208, "fr", 2, "fr"));
        sTable.add(new MccEntry(212, "mc", 2));
        sTable.add(new MccEntry(213, "ad", 2));
        sTable.add(new MccEntry(214, "es", 2, "es"));
        sTable.add(new MccEntry(216, "hu", 2));
        sTable.add(new MccEntry(218, "ba", 2));
        sTable.add(new MccEntry(219, "hr", 2));
        sTable.add(new MccEntry(220, "rs", 2));
        sTable.add(new MccEntry(222, "it", 2, "it"));
        sTable.add(new MccEntry(225, "va", 2, "it"));
        sTable.add(new MccEntry(226, "ro", 2));
        sTable.add(new MccEntry(R.styleable.Theme_dropdownListPreferredItemHeight, "ch", 2, "de"));
        sTable.add(new MccEntry(R.styleable.Theme_alertDialogButtonGroupStyle, "cz", 2, "cs"));
        sTable.add(new MccEntry(R.styleable.Theme_alertDialogCenterButtons, "sk", 2));
        sTable.add(new MccEntry(232, "at", 2, "de"));
        sTable.add(new MccEntry(234, "gb", 2, "en"));
        sTable.add(new MccEntry(235, "gb", 2, "en"));
        sTable.add(new MccEntry(238, "dk", 2));
        sTable.add(new MccEntry(240, "se", 2, "sv"));
        sTable.add(new MccEntry(242, AuthorizationHeaderIms.NO, 2));
        sTable.add(new MccEntry(244, "fi", 2));
        sTable.add(new MccEntry(246, "lt", 2));
        sTable.add(new MccEntry(247, "lv", 2));
        sTable.add(new MccEntry(248, "ee", 2));
        sTable.add(new MccEntry(250, "ru", 2));
        sTable.add(new MccEntry(255, "ua", 2));
        sTable.add(new MccEntry(257, "by", 2));
        sTable.add(new MccEntry(259, "md", 2));
        sTable.add(new MccEntry(260, "pl", 2));
        sTable.add(new MccEntry(262, "de", 2, "de"));
        sTable.add(new MccEntry(266, "gi", 2));
        sTable.add(new MccEntry(268, "pt", 2));
        sTable.add(new MccEntry(R.styleable.Theme_findOnPagePreviousDrawable, "lu", 2));
        sTable.add(new MccEntry(272, "ie", 2, "en"));
        sTable.add(new MccEntry(274, "is", 2));
        sTable.add(new MccEntry(BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA, "al", 2));
        sTable.add(new MccEntry(278, "mt", 2));
        sTable.add(new MccEntry(BluetoothClass.Device.COMPUTER_WEARABLE, "cy", 2));
        sTable.add(new MccEntry(282, "ge", 2));
        sTable.add(new MccEntry(283, "am", 2));
        sTable.add(new MccEntry(284, "bg", 2));
        sTable.add(new MccEntry(286, "tr", 2));
        sTable.add(new MccEntry(MediaProperties.HEIGHT_288, "fo", 2));
        sTable.add(new MccEntry(289, "ge", 2));
        sTable.add(new MccEntry(290, "gl", 2));
        sTable.add(new MccEntry(292, "sm", 2));
        sTable.add(new MccEntry(293, "si", 2));
        sTable.add(new MccEntry(294, "mk", 2));
        sTable.add(new MccEntry(295, "li", 2));
        sTable.add(new MccEntry(297, "me", 2));
        sTable.add(new MccEntry(302, "ca", 3, ""));
        sTable.add(new MccEntry(308, "pm", 2));
        sTable.add(new MccEntry(310, "us", 3, "en"));
        sTable.add(new MccEntry(311, "us", 3, "en"));
        sTable.add(new MccEntry(312, "us", 3, "en"));
        sTable.add(new MccEntry(313, "us", 3, "en"));
        sTable.add(new MccEntry(314, "us", 3, "en"));
        sTable.add(new MccEntry(315, "us", 3, "en"));
        sTable.add(new MccEntry(316, "us", 3, "en"));
        sTable.add(new MccEntry(330, "pr", 2));
        sTable.add(new MccEntry(332, "vi", 2));
        sTable.add(new MccEntry(334, "mx", 3));
        sTable.add(new MccEntry(338, "jm", 3));
        sTable.add(new MccEntry(340, "gp", 2));
        sTable.add(new MccEntry(342, "bb", 3));
        sTable.add(new MccEntry(344, "ag", 3));
        sTable.add(new MccEntry(346, "ky", 3));
        sTable.add(new MccEntry(348, "vg", 3));
        sTable.add(new MccEntry(SpellChecker.WORD_ITERATOR_INTERVAL, "bm", 2));
        sTable.add(new MccEntry(352, "gd", 2));
        sTable.add(new MccEntry(354, "ms", 2));
        sTable.add(new MccEntry(356, "kn", 2));
        sTable.add(new MccEntry(358, "lc", 2));
        sTable.add(new MccEntry(MediaProperties.HEIGHT_360, "vc", 2));
        sTable.add(new MccEntry(362, "ai", 2));
        sTable.add(new MccEntry(363, "aw", 2));
        sTable.add(new MccEntry(364, "bs", 2));
        sTable.add(new MccEntry(365, "ai", 3));
        sTable.add(new MccEntry(366, "dm", 2));
        sTable.add(new MccEntry(368, "cu", 2));
        sTable.add(new MccEntry(370, "do", 2));
        sTable.add(new MccEntry(372, "ht", 2));
        sTable.add(new MccEntry(374, "tt", 2));
        sTable.add(new MccEntry(376, "tc", 2));
        sTable.add(new MccEntry(400, "az", 2));
        sTable.add(new MccEntry(401, "kz", 2));
        sTable.add(new MccEntry(402, "bt", 2));
        sTable.add(new MccEntry(404, "in", 2));
        sTable.add(new MccEntry(405, "in", 2));
        sTable.add(new MccEntry(410, "pk", 2));
        sTable.add(new MccEntry(412, "af", 2));
        sTable.add(new MccEntry(413, "lk", 2));
        sTable.add(new MccEntry(414, "mm", 2));
        sTable.add(new MccEntry(415, "lb", 2));
        sTable.add(new MccEntry(416, "jo", 2));
        sTable.add(new MccEntry(417, "sy", 2));
        sTable.add(new MccEntry(418, "iq", 2));
        sTable.add(new MccEntry(419, "kw", 2));
        sTable.add(new MccEntry(420, "sa", 2));
        sTable.add(new MccEntry(Response.EXTENSION_REQUIRED, "ye", 2));
        sTable.add(new MccEntry(422, "om", 2));
        sTable.add(new MccEntry(423, "ps", 2));
        sTable.add(new MccEntry(424, "ae", 2));
        sTable.add(new MccEntry(425, "il", 2));
        sTable.add(new MccEntry(426, "bh", 2));
        sTable.add(new MccEntry(427, "qa", 2));
        sTable.add(new MccEntry(428, "mn", 2));
        sTable.add(new MccEntry(429, "np", 2));
        sTable.add(new MccEntry(430, "ae", 2));
        sTable.add(new MccEntry(431, "ae", 2));
        sTable.add(new MccEntry(432, "ir", 2));
        sTable.add(new MccEntry(434, "uz", 2));
        sTable.add(new MccEntry(436, "tj", 2));
        sTable.add(new MccEntry(437, "kg", 2));
        sTable.add(new MccEntry(438, "tm", 2));
        sTable.add(new MccEntry(440, "jp", 2, "ja"));
        sTable.add(new MccEntry(441, "jp", 2, "ja"));
        sTable.add(new MccEntry(450, "kr", 2, "ko"));
        sTable.add(new MccEntry(452, "vn", 2));
        sTable.add(new MccEntry(454, "hk", 2));
        sTable.add(new MccEntry(455, "mo", 2));
        sTable.add(new MccEntry(456, "kh", 2));
        sTable.add(new MccEntry(457, "la", 2));
        sTable.add(new MccEntry(460, "cn", 2, "zh"));
        sTable.add(new MccEntry(461, "cn", 2, "zh"));
        sTable.add(new MccEntry(466, "tw", 2));
        sTable.add(new MccEntry(467, "kp", 2));
        sTable.add(new MccEntry(470, "bd", 2));
        sTable.add(new MccEntry(472, "mv", 2));
        sTable.add(new MccEntry(502, "my", 2));
        sTable.add(new MccEntry(505, "au", 2, "en"));
        sTable.add(new MccEntry(510, "id", 2));
        sTable.add(new MccEntry(514, "tl", 2));
        sTable.add(new MccEntry(515, "ph", 2));
        sTable.add(new MccEntry(BluetoothClass.Device.PHONE_CORDLESS, "th", 2));
        sTable.add(new MccEntry(525, "sg", 2, "en"));
        sTable.add(new MccEntry(BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY, "bn", 2));
        sTable.add(new MccEntry(530, "nz", 2, "en"));
        sTable.add(new MccEntry(534, "mp", 2));
        sTable.add(new MccEntry(535, "gu", 2));
        sTable.add(new MccEntry(536, "nr", 2));
        sTable.add(new MccEntry(537, "pg", 2));
        sTable.add(new MccEntry(539, "to", 2));
        sTable.add(new MccEntry(540, "sb", 2));
        sTable.add(new MccEntry(541, "vu", 2));
        sTable.add(new MccEntry(542, "fj", 2));
        sTable.add(new MccEntry(543, "wf", 2));
        sTable.add(new MccEntry(544, "as", 2));
        sTable.add(new MccEntry(545, "ki", 2));
        sTable.add(new MccEntry(546, "nc", 2));
        sTable.add(new MccEntry(547, "pf", 2));
        sTable.add(new MccEntry(548, "ck", 2));
        sTable.add(new MccEntry(549, "ws", 2));
        sTable.add(new MccEntry(550, "fm", 2));
        sTable.add(new MccEntry(551, "mh", 2));
        sTable.add(new MccEntry(552, "pw", 2));
        sTable.add(new MccEntry(NsdService.NativeResponseCode.SERVICE_DISCOVERY_FAILED, "eg", 2));
        sTable.add(new MccEntry(603, "dz", 2));
        sTable.add(new MccEntry(604, "ma", 2));
        sTable.add(new MccEntry(605, "tn", 2));
        sTable.add(new MccEntry(606, "ly", 2));
        sTable.add(new MccEntry(NsdService.NativeResponseCode.SERVICE_RESOLUTION_FAILED, "gm", 2));
        sTable.add(new MccEntry(NsdService.NativeResponseCode.SERVICE_RESOLVED, "sn", 2));
        sTable.add(new MccEntry(NsdService.NativeResponseCode.SERVICE_UPDATED, "mr", 2));
        sTable.add(new MccEntry(NsdService.NativeResponseCode.SERVICE_UPDATE_FAILED, "ml", 2));
        sTable.add(new MccEntry(NsdService.NativeResponseCode.SERVICE_GET_ADDR_FAILED, "gn", 2));
        sTable.add(new MccEntry(NsdService.NativeResponseCode.SERVICE_GET_ADDR_SUCCESS, "ci", 2));
        sTable.add(new MccEntry(NetworkManagementService.NetdResponseCode.InterfaceClassActivity, "bf", 2));
        sTable.add(new MccEntry(NetworkManagementService.NetdResponseCode.InterfaceAddressChange, "ne", 2));
        sTable.add(new MccEntry(615, "tg", 2));
        sTable.add(new MccEntry(616, "bj", 2));
        sTable.add(new MccEntry(617, "mu", 2));
        sTable.add(new MccEntry(618, "lr", 2));
        sTable.add(new MccEntry(619, "sl", 2));
        sTable.add(new MccEntry(620, "gh", 2));
        sTable.add(new MccEntry(621, "ng", 2));
        sTable.add(new MccEntry(622, "td", 2));
        sTable.add(new MccEntry(623, "cf", 2));
        sTable.add(new MccEntry(624, "cm", 2));
        sTable.add(new MccEntry(625, "cv", 2));
        sTable.add(new MccEntry(626, Telephony.BaseMmsColumns.STATUS, 2));
        sTable.add(new MccEntry(627, "gq", 2));
        sTable.add(new MccEntry(628, "ga", 2));
        sTable.add(new MccEntry(629, "cg", 2));
        sTable.add(new MccEntry(MountService.VoldResponseCode.VolumeDiskInserted, "cg", 2));
        sTable.add(new MccEntry(MountService.VoldResponseCode.VolumeDiskRemoved, "ao", 2));
        sTable.add(new MccEntry(MountService.VoldResponseCode.VolumeBadRemoval, "gw", 2));
        sTable.add(new MccEntry(633, "sc", 2));
        sTable.add(new MccEntry(634, "sd", 2));
        sTable.add(new MccEntry(635, "rw", 2));
        sTable.add(new MccEntry(636, "et", 2));
        sTable.add(new MccEntry(637, "so", 2));
        sTable.add(new MccEntry(638, "dj", 2));
        sTable.add(new MccEntry(639, "ke", 2));
        sTable.add(new MccEntry(DisplayMetrics.DENSITY_XXXHIGH, "tz", 2));
        sTable.add(new MccEntry(641, "ug", 2));
        sTable.add(new MccEntry(642, "bi", 2));
        sTable.add(new MccEntry(643, "mz", 2));
        sTable.add(new MccEntry(645, "zm", 2));
        sTable.add(new MccEntry(646, "mg", 2));
        sTable.add(new MccEntry(647, "re", 2));
        sTable.add(new MccEntry(648, "zw", 2));
        sTable.add(new MccEntry(649, "na", 2));
        sTable.add(new MccEntry(650, "mw", 2));
        sTable.add(new MccEntry(651, "ls", 2));
        sTable.add(new MccEntry(652, "bw", 2));
        sTable.add(new MccEntry(653, "sz", 2));
        sTable.add(new MccEntry(654, "km", 2));
        sTable.add(new MccEntry(655, "za", 2, "en"));
        sTable.add(new MccEntry(657, "er", 2));
        sTable.add(new MccEntry(MediaPlayer.MEDIA_INFO_BUFFERING_END, "bz", 2));
        sTable.add(new MccEntry(704, "gt", 2));
        sTable.add(new MccEntry(706, "sv", 2));
        sTable.add(new MccEntry(708, "hn", 3));
        sTable.add(new MccEntry(710, "ni", 2));
        sTable.add(new MccEntry(712, "cr", 2));
        sTable.add(new MccEntry(714, "pa", 2));
        sTable.add(new MccEntry(716, "pe", 2));
        sTable.add(new MccEntry(722, "ar", 3));
        sTable.add(new MccEntry(724, "br", 2));
        sTable.add(new MccEntry(730, Telephony.Mms.Part.CONTENT_LOCATION, 2));
        sTable.add(new MccEntry(732, "co", 3));
        sTable.add(new MccEntry(734, "ve", 2));
        sTable.add(new MccEntry(736, "bo", 2));
        sTable.add(new MccEntry(738, "gy", 2));
        sTable.add(new MccEntry(740, "ec", 2));
        sTable.add(new MccEntry(742, "gf", 2));
        sTable.add(new MccEntry(744, "py", 2));
        sTable.add(new MccEntry(746, "sr", 2));
        sTable.add(new MccEntry(748, "uy", 2));
        sTable.add(new MccEntry(750, "fk", 2));
        Collections.sort(sTable);
    }
}