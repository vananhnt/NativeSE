package javax.sip.header;

import java.text.ParseException;

/* loaded from: ToHeader.class */
public interface ToHeader extends HeaderAddress, Header, Parameters {
    public static final String NAME = "To";

    String getTag();

    void setTag(String str) throws ParseException;

    boolean hasTag();

    void removeTag();

    String getDisplayName();

    String getUserAtHostPort();
}