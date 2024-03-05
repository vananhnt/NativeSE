package javax.sip.header;

import java.util.Locale;

/* loaded from: ContentLanguageHeader.class */
public interface ContentLanguageHeader extends Header {
    public static final String NAME = "Content-Language";

    Locale getContentLanguage();

    void setContentLanguage(Locale locale);

    String getLanguageTag();

    void setLanguageTag(String str);
}