package javax.sip.header;

import java.text.ParseException;
import javax.sip.InvalidArgumentException;

/* loaded from: RetryAfterHeader.class */
public interface RetryAfterHeader extends Header, Parameters {
    public static final String NAME = "Retry-After";

    String getComment();

    void setComment(String str) throws ParseException;

    boolean hasComment();

    void removeComment();

    int getDuration();

    void setDuration(int i) throws InvalidArgumentException;

    void removeDuration();

    int getRetryAfter();

    void setRetryAfter(int i) throws InvalidArgumentException;
}