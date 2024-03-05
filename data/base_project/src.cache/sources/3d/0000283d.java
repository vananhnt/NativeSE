package javax.sip.header;

import java.text.ParseException;

/* loaded from: ExtensionHeader.class */
public interface ExtensionHeader extends Header {
    String getValue();

    void setValue(String str) throws ParseException;
}