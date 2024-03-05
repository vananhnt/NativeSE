package java.text.spi;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

/* loaded from: BreakIteratorProvider.class */
public abstract class BreakIteratorProvider extends LocaleServiceProvider {
    public abstract BreakIterator getWordInstance(Locale locale);

    public abstract BreakIterator getLineInstance(Locale locale);

    public abstract BreakIterator getCharacterInstance(Locale locale);

    public abstract BreakIterator getSentenceInstance(Locale locale);

    protected BreakIteratorProvider() {
    }
}