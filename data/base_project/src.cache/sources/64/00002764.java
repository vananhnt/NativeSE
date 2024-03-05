package java.util.spi;

import java.util.Locale;

/* loaded from: CurrencyNameProvider.class */
public abstract class CurrencyNameProvider extends LocaleServiceProvider {
    public abstract String getSymbol(String str, Locale locale);

    protected CurrencyNameProvider() {
    }
}