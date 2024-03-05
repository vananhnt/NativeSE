package javax.sip.header;

import java.text.ParseException;

/* loaded from: FromHeader.class */
public interface FromHeader extends HeaderAddress, Header, Parameters {
    public static final String NAME = "From";

    String getTag();

    void setTag(String str) throws ParseException;

    boolean hasTag();

    void removeTag();

    String getDisplayName();

    String getUserAtHostPort();
}