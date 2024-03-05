package java.text.spi;

import java.text.Collator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

/* loaded from: CollatorProvider.class */
public abstract class CollatorProvider extends LocaleServiceProvider {
    public abstract Collator getInstance(Locale locale);

    protected CollatorProvider() {
    }
}