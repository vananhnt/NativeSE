package javax.sip.header;

import java.text.ParseException;

/* loaded from: PriorityHeader.class */
public interface PriorityHeader extends Header {
    public static final String NAME = "Priority";
    public static final String NON_URGENT = "Non-Urgent";
    public static final String NORMAL = "Normal";
    public static final String URGENT = "Urgent";
    public static final String EMERGENCY = "Emergency";

    String getPriority();

    void setPriority(String str) throws ParseException;
}