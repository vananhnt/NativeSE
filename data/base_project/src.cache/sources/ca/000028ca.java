package libcore.icu;

import java.util.HashMap;
import java.util.Locale;
import libcore.util.Objects;

/* loaded from: LocaleData.class */
public final class LocaleData {
    private static final HashMap<String, LocaleData> localeDataCache = new HashMap<>();
    public Integer firstDayOfWeek;
    public Integer minimalDaysInFirstWeek;
    public String[] amPm;
    public String[] eras;
    public String[] longMonthNames;
    public String[] shortMonthNames;
    public String[] tinyMonthNames;
    public String[] longStandAloneMonthNames;
    public String[] shortStandAloneMonthNames;
    public String[] tinyStandAloneMonthNames;
    public String[] longWeekdayNames;
    public String[] shortWeekdayNames;
    public String[] tinyWeekdayNames;
    public String[] longStandAloneWeekdayNames;
    public String[] shortStandAloneWeekdayNames;
    public String[] tinyStandAloneWeekdayNames;
    public String yesterday;
    public String today;
    public String tomorrow;
    public String fullTimeFormat;
    public String longTimeFormat;
    public String mediumTimeFormat;
    public String shortTimeFormat;
    public String fullDateFormat;
    public String longDateFormat;
    public String mediumDateFormat;
    public String shortDateFormat;
    public String shortDateFormat4;
    public String timeFormat12;
    public String timeFormat24;
    public char zeroDigit;
    public char decimalSeparator;
    public char groupingSeparator;
    public char patternSeparator;
    public char percent;
    public char perMill;
    public char monetarySeparator;
    public char minusSign;
    public String exponentSeparator;
    public String infinity;
    public String NaN;
    public String currencySymbol;
    public String internationalCurrencySymbol;
    public String numberPattern;
    public String integerPattern;
    public String currencyPattern;
    public String percentPattern;

    static {
        get(Locale.ROOT);
        get(Locale.US);
        get(Locale.getDefault());
    }

    private LocaleData() {
    }

    public static LocaleData get(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String localeName = locale.toString();
        synchronized (localeDataCache) {
            LocaleData localeData = localeDataCache.get(localeName);
            if (localeData != null) {
                return localeData;
            }
            LocaleData newLocaleData = initLocaleData(locale);
            synchronized (localeDataCache) {
                LocaleData localeData2 = localeDataCache.get(localeName);
                if (localeData2 != null) {
                    return localeData2;
                }
                localeDataCache.put(localeName, newLocaleData);
                return newLocaleData;
            }
        }
    }

    public String toString() {
        return Objects.toString(this);
    }

    public String getDateFormat(int style) {
        switch (style) {
            case 0:
                return this.fullDateFormat;
            case 1:
                return this.longDateFormat;
            case 2:
                return this.mediumDateFormat;
            case 3:
                return this.shortDateFormat;
            default:
                throw new AssertionError();
        }
    }

    public String getTimeFormat(int style) {
        switch (style) {
            case 0:
                return this.fullTimeFormat;
            case 1:
                return this.longTimeFormat;
            case 2:
                return this.mediumTimeFormat;
            case 3:
                return this.shortTimeFormat;
            default:
                throw new AssertionError();
        }
    }

    private static LocaleData initLocaleData(Locale locale) {
        LocaleData localeData = new LocaleData();
        if (!ICU.initLocaleDataImpl(locale.toString(), localeData)) {
            throw new AssertionError("couldn't initialize LocaleData for locale " + locale);
        }
        localeData.timeFormat12 = ICU.getBestDateTimePattern("hm", locale.toString());
        localeData.timeFormat24 = ICU.getBestDateTimePattern("Hm", locale.toString());
        if (localeData.fullTimeFormat != null) {
            localeData.fullTimeFormat = localeData.fullTimeFormat.replace('v', 'z');
        }
        if (localeData.numberPattern != null) {
            localeData.integerPattern = localeData.numberPattern.replaceAll("\\.[#,]*", "");
        }
        localeData.shortDateFormat4 = localeData.shortDateFormat.replaceAll("\\byy\\b", "y");
        return localeData;
    }
}