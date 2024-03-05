package javax.sip.header;

import javax.sip.InvalidArgumentException;

/* loaded from: TimeStampHeader.class */
public interface TimeStampHeader extends Header {
    public static final String NAME = "Timestamp";

    float getDelay();

    void setDelay(float f) throws InvalidArgumentException;

    boolean hasDelay();

    void removeDelay();

    long getTime();

    void setTime(long j) throws InvalidArgumentException;

    int getTimeDelay();

    void setTimeDelay(int i) throws InvalidArgumentException;

    float getTimeStamp();

    void setTimeStamp(float f) throws InvalidArgumentException;
}