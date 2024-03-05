package java.text.spi;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

/* loaded from: NumberFormatProvider.class */
public abstract class NumberFormatProvider extends LocaleServiceProvider {
    public abstract NumberFormat getCurrencyInstance(Locale locale);

    public abstract NumberFormat getIntegerInstance(Locale locale);

    public abstract NumberFormat getNumberInstance(Locale locale);

    public abstract NumberFormat getPercentInstance(Locale locale);

    protected NumberFormatProvider() {
    }
}