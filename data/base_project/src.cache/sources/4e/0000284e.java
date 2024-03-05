package javax.sip.header;

import java.text.ParseException;
import javax.sip.InvalidArgumentException;

/* loaded from: RAckHeader.class */
public interface RAckHeader extends Header {
    public static final String NAME = "RAck";

    String getMethod();

    void setMethod(String str) throws ParseException;

    long getCSequenceNumber();

    void setCSequenceNumber(long j) throws InvalidArgumentException;

    long getRSequenceNumber();

    void setRSequenceNumber(long j) throws InvalidArgumentException;

    int getCSeqNumber();

    void setCSeqNumber(int i) throws InvalidArgumentException;

    int getRSeqNumber();

    void setRSeqNumber(int i) throws InvalidArgumentException;
}