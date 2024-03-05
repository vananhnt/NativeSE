package java.util.spi;

import java.util.Locale;

/* loaded from: LocaleNameProvider.class */
public abstract class LocaleNameProvider extends LocaleServiceProvider {
    public abstract String getDisplayLanguage(String str, Locale locale);

    public abstract String getDisplayCountry(String str, Locale locale);

    public abstract String getDisplayVariant(String str, Locale locale);

    protected LocaleNameProvider() {
    }
}