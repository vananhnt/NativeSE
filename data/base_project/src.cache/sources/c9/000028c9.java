package libcore.icu;

import java.util.LinkedHashSet;
import java.util.Locale;

/* loaded from: ICU.class */
public final class ICU {
    private static String[] isoLanguages;
    private static String[] isoCountries;
    private static Locale[] availableLocalesCache;
    public static final int U_ZERO_ERROR = 0;
    public static final int U_INVALID_CHAR_FOUND = 10;
    public static final int U_TRUNCATED_CHAR_FOUND = 11;
    public static final int U_ILLEGAL_CHAR_FOUND = 12;
    public static final int U_BUFFER_OVERFLOW_ERROR = 15;

    public static native String getBestDateTimePattern(String str, String str2);

    public static native String getCldrVersion();

    public static native String getIcuVersion();

    public static native String getUnicodeVersion();

    public static native String toLowerCase(String str, String str2);

    public static native String toUpperCase(String str, String str2);

    private static native String[] getAvailableBreakIteratorLocalesNative();

    private static native String[] getAvailableCalendarLocalesNative();

    private static native String[] getAvailableCollatorLocalesNative();

    private static native String[] getAvailableDateFormatLocalesNative();

    private static native String[] getAvailableLocalesNative();

    private static native String[] getAvailableNumberFormatLocalesNative();

    public static native String[] getAvailableCurrencyCodes();

    public static native String getCurrencyCode(String str);

    public static native String getCurrencyDisplayName(String str, String str2);

    public static native int getCurrencyFractionDigits(String str);

    public static native String getCurrencySymbol(String str, String str2);

    public static native String getDisplayCountryNative(String str, String str2);

    public static native String getDisplayLanguageNative(String str, String str2);

    public static native String getDisplayVariantNative(String str, String str2);

    public static native String getISO3CountryNative(String str);

    public static native String getISO3LanguageNative(String str);

    public static native String addLikelySubtags(String str);

    public static native String getScript(String str);

    private static native String[] getISOLanguagesNative();

    private static native String[] getISOCountriesNative();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native boolean initLocaleDataImpl(String str, LocaleData localeData);

    public static String[] getISOLanguages() {
        if (isoLanguages == null) {
            isoLanguages = getISOLanguagesNative();
        }
        return (String[]) isoLanguages.clone();
    }

    public static String[] getISOCountries() {
        if (isoCountries == null) {
            isoCountries = getISOCountriesNative();
        }
        return (String[]) isoCountries.clone();
    }

    public static Locale localeFromString(String localeName) {
        int first = localeName.indexOf(95);
        int second = localeName.indexOf(95, first + 1);
        if (first == -1) {
            return new Locale(localeName);
        }
        if (second == -1) {
            return new Locale(localeName.substring(0, first), localeName.substring(first + 1));
        }
        return new Locale(localeName.substring(0, first), localeName.substring(first + 1, second), localeName.substring(second + 1));
    }

    public static Locale[] localesFromStrings(String[] localeNames) {
        LinkedHashSet<Locale> set = new LinkedHashSet<>();
        for (String localeName : localeNames) {
            set.add(localeFromString(localeName));
        }
        return (Locale[]) set.toArray(new Locale[set.size()]);
    }

    public static Locale[] getAvailableLocales() {
        if (availableLocalesCache == null) {
            availableLocalesCache = localesFromStrings(getAvailableLocalesNative());
        }
        return (Locale[]) availableLocalesCache.clone();
    }

    public static Locale[] getAvailableBreakIteratorLocales() {
        return localesFromStrings(getAvailableBreakIteratorLocalesNative());
    }

    public static Locale[] getAvailableCalendarLocales() {
        return localesFromStrings(getAvailableCalendarLocalesNative());
    }

    public static Locale[] getAvailableCollatorLocales() {
        return localesFromStrings(getAvailableCollatorLocalesNative());
    }

    public static Locale[] getAvailableDateFormatLocales() {
        return localesFromStrings(getAvailableDateFormatLocalesNative());
    }

    public static Locale[] getAvailableDateFormatSymbolsLocales() {
        return getAvailableDateFormatLocales();
    }

    public static Locale[] getAvailableDecimalFormatSymbolsLocales() {
        return getAvailableNumberFormatLocales();
    }

    public static Locale[] getAvailableNumberFormatLocales() {
        return localesFromStrings(getAvailableNumberFormatLocalesNative());
    }

    public static char[] getDateFormatOrder(String pattern) {
        char[] result = new char[3];
        int resultIndex = 0;
        boolean sawDay = false;
        boolean sawMonth = false;
        boolean sawYear = false;
        int i = 0;
        while (i < pattern.length()) {
            char ch = pattern.charAt(i);
            if (ch == 'd' || ch == 'L' || ch == 'M' || ch == 'y') {
                if (ch == 'd' && !sawDay) {
                    int i2 = resultIndex;
                    resultIndex++;
                    result[i2] = 'd';
                    sawDay = true;
                } else if ((ch == 'L' || ch == 'M') && !sawMonth) {
                    int i3 = resultIndex;
                    resultIndex++;
                    result[i3] = 'M';
                    sawMonth = true;
                } else if (ch == 'y' && !sawYear) {
                    int i4 = resultIndex;
                    resultIndex++;
                    result[i4] = 'y';
                    sawYear = true;
                }
            } else if (ch == 'G') {
                continue;
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                throw new IllegalArgumentException("Bad pattern character '" + ch + "' in " + pattern);
            } else {
                if (ch != '\'') {
                    continue;
                } else if (i < pattern.length() - 1 && pattern.charAt(i + 1) == '\'') {
                    i++;
                } else {
                    int i5 = pattern.indexOf(39, i + 1);
                    if (i5 == -1) {
                        throw new IllegalArgumentException("Bad quoting in " + pattern);
                    }
                    i = i5 + 1;
                }
            }
            i++;
        }
        return result;
    }

    public static boolean U_FAILURE(int error) {
        return error > 0;
    }
}