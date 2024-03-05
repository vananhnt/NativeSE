package javax.sip.header;

import java.text.ParseException;

/* loaded from: AllowEventsHeader.class */
public interface AllowEventsHeader extends Header {
    public static final String NAME = "Allow-Events";

    String getEventType();

    void setEventType(String str) throws ParseException;
}