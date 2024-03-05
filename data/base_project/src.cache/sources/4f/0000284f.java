package javax.sip.header;

import javax.sip.InvalidArgumentException;

/* loaded from: RSeqHeader.class */
public interface RSeqHeader extends Header {
    public static final String NAME = "RSeq";

    long getSeqNumber();

    void setSeqNumber(long j) throws InvalidArgumentException;

    int getSequenceNumber();

    void setSequenceNumber(int i) throws InvalidArgumentException;
}