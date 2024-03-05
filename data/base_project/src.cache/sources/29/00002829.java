package javax.sip.header;

import java.util.Locale;
import javax.sip.InvalidArgumentException;

/* loaded from: AcceptLanguageHeader.class */
public interface AcceptLanguageHeader extends Header, Parameters {
    public static final String NAME = "Accept-Language";

    Locale getAcceptLanguage();

    void setAcceptLanguage(Locale locale);

    void setLanguageRange(String str);

    float getQValue();

    void setQValue(float f) throws InvalidArgumentException;

    boolean hasQValue();

    void removeQValue();
}