package gov.nist.javax.sip.header;

import android.media.videoeditor.VideoEditor;
import javax.sip.InvalidArgumentException;
import javax.sip.header.RSeqHeader;

/* loaded from: RSeq.class */
public class RSeq extends SIPHeader implements RSeqHeader {
    private static final long serialVersionUID = 8765762413224043394L;
    protected long sequenceNumber;

    public RSeq() {
        super("RSeq");
    }

    @Override // javax.sip.header.RSeqHeader
    public int getSequenceNumber() {
        return (int) this.sequenceNumber;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        return Long.toString(this.sequenceNumber);
    }

    @Override // javax.sip.header.RSeqHeader
    public long getSeqNumber() {
        return this.sequenceNumber;
    }

    @Override // javax.sip.header.RSeqHeader
    public void setSeqNumber(long sequenceNumber) throws InvalidArgumentException {
        if (sequenceNumber <= 0 || sequenceNumber > VideoEditor.MAX_SUPPORTED_FILE_SIZE) {
            throw new InvalidArgumentException("Bad seq number " + sequenceNumber);
        }
        this.sequenceNumber = sequenceNumber;
    }

    @Override // javax.sip.header.RSeqHeader
    public void setSequenceNumber(int sequenceNumber) throws InvalidArgumentException {
        setSeqNumber(sequenceNumber);
    }
}