package javax.sip.header;

import java.text.ParseException;

/* loaded from: Encoding.class */
public interface Encoding {
    String getEncoding();

    void setEncoding(String str) throws ParseException;
}