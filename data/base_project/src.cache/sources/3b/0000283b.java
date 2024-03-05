package javax.sip.header;

import java.text.ParseException;

/* loaded from: EventHeader.class */
public interface EventHeader extends Header, Parameters {
    public static final String NAME = "Event";

    String getEventId();

    void setEventId(String str) throws ParseException;

    String getEventType();

    void setEventType(String str) throws ParseException;
}