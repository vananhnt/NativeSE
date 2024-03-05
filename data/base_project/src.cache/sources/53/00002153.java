package gov.nist.javax.sip.header;

import android.media.videoeditor.VideoEditor;
import gov.nist.core.Separators;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.RAckHeader;

/* loaded from: RAck.class */
public class RAck extends SIPHeader implements RAckHeader {
    private static final long serialVersionUID = 743999286077404118L;
    protected long cSeqNumber;
    protected long rSeqNumber;
    protected String method;

    public RAck() {
        super("RAck");
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        return new StringBuffer().append(this.rSeqNumber).append(Separators.SP).append(this.cSeqNumber).append(Separators.SP).append(this.method).toString();
    }

    @Override // javax.sip.header.RAckHeader
    public int getCSeqNumber() {
        return (int) this.cSeqNumber;
    }

    public long getCSeqNumberLong() {
        return this.cSeqNumber;
    }

    @Override // javax.sip.header.RAckHeader
    public String getMethod() {
        return this.method;
    }

    @Override // javax.sip.header.RAckHeader
    public int getRSeqNumber() {
        return (int) this.rSeqNumber;
    }

    @Override // javax.sip.header.RAckHeader
    public void setCSeqNumber(int cSeqNumber) throws InvalidArgumentException {
        setCSequenceNumber(cSeqNumber);
    }

    @Override // javax.sip.header.RAckHeader
    public void setMethod(String method) throws ParseException {
        this.method = method;
    }

    @Override // javax.sip.header.RAckHeader
    public long getCSequenceNumber() {
        return this.cSeqNumber;
    }

    @Override // javax.sip.header.RAckHeader
    public long getRSequenceNumber() {
        return this.rSeqNumber;
    }

    @Override // javax.sip.header.RAckHeader
    public void setCSequenceNumber(long cSeqNumber) throws InvalidArgumentException {
        if (cSeqNumber <= 0 || cSeqNumber > VideoEditor.MAX_SUPPORTED_FILE_SIZE) {
            throw new InvalidArgumentException("Bad CSeq # " + cSeqNumber);
        }
        this.cSeqNumber = cSeqNumber;
    }

    @Override // javax.sip.header.RAckHeader
    public void setRSeqNumber(int rSeqNumber) throws InvalidArgumentException {
        setRSequenceNumber(rSeqNumber);
    }

    @Override // javax.sip.header.RAckHeader
    public void setRSequenceNumber(long rSeqNumber) throws InvalidArgumentException {
        if (rSeqNumber <= 0 || this.cSeqNumber > VideoEditor.MAX_SUPPORTED_FILE_SIZE) {
            throw new InvalidArgumentException("Bad rSeq # " + rSeqNumber);
        }
        this.rSeqNumber = rSeqNumber;
    }
}