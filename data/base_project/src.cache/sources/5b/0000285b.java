package javax.sip.header;

import java.text.ParseException;
import javax.sip.InvalidArgumentException;

/* loaded from: SubscriptionStateHeader.class */
public interface SubscriptionStateHeader extends ExpiresHeader, Parameters {
    public static final String NAME = "Subscription-State";
    public static final String DEACTIVATED = "Deactivated";
    public static final String GIVE_UP = "Give-Up";
    public static final String NO_RESOURCE = "No-Resource";
    public static final String PROBATION = "Probation";
    public static final String REJECTED = "Rejected";
    public static final String TIMEOUT = "Timeout";
    public static final String UNKNOWN = "Unknown";
    public static final String ACTIVE = "Active";
    public static final String PENDING = "Pending";
    public static final String TERMINATED = "Terminated";

    String getReasonCode();

    void setReasonCode(String str) throws ParseException;

    int getRetryAfter();

    void setRetryAfter(int i) throws InvalidArgumentException;

    String getState();

    void setState(String str) throws ParseException;
}