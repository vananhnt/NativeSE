package javax.sip.header;

import java.text.ParseException;

/* loaded from: MediaType.class */
public interface MediaType {
    String getContentSubType();

    void setContentSubType(String str) throws ParseException;

    String getContentType();

    void setContentType(String str) throws ParseException;
}