package gov.nist.javax.sip.header;

import android.media.videoeditor.VideoEditor;
import gov.nist.core.Separators;
import gov.nist.javax.sip.message.SIPRequest;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.CSeqHeader;

/* loaded from: CSeq.class */
public class CSeq extends SIPHeader implements CSeqHeader {
    private static final long serialVersionUID = -5405798080040422910L;
    protected Long seqno;
    protected String method;

    public CSeq() {
        super("CSeq");
    }

    public CSeq(long seqno, String method) {
        this();
        this.seqno = Long.valueOf(seqno);
        this.method = SIPRequest.getCannonicalName(method);
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        if (other instanceof CSeqHeader) {
            CSeqHeader o = (CSeqHeader) other;
            return getSeqNumber() == o.getSeqNumber() && getMethod().equals(o.getMethod());
        }
        return false;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader, gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public String encode() {
        return this.headerName + Separators.COLON + Separators.SP + encodeBody() + Separators.NEWLINE;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return encodeBody(new StringBuffer()).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.header.SIPHeader
    public StringBuffer encodeBody(StringBuffer buffer) {
        return buffer.append(this.seqno).append(Separators.SP).append(this.method.toUpperCase());
    }

    @Override // javax.sip.header.AllowHeader
    public String getMethod() {
        return this.method;
    }

    @Override // javax.sip.header.RSeqHeader
    public void setSeqNumber(long sequenceNumber) throws InvalidArgumentException {
        if (sequenceNumber < 0) {
            throw new InvalidArgumentException("JAIN-SIP Exception, CSeq, setSequenceNumber(), the sequence number parameter is < 0 : " + sequenceNumber);
        }
        if (sequenceNumber > VideoEditor.MAX_SUPPORTED_FILE_SIZE) {
            throw new InvalidArgumentException("JAIN-SIP Exception, CSeq, setSequenceNumber(), the sequence number parameter is too large : " + sequenceNumber);
        }
        this.seqno = Long.valueOf(sequenceNumber);
    }

    @Override // javax.sip.header.RSeqHeader
    public void setSequenceNumber(int sequenceNumber) throws InvalidArgumentException {
        setSeqNumber(sequenceNumber);
    }

    @Override // javax.sip.header.AllowHeader
    public void setMethod(String meth) throws ParseException {
        if (meth == null) {
            throw new NullPointerException("JAIN-SIP Exception, CSeq, setMethod(), the meth parameter is null");
        }
        this.method = SIPRequest.getCannonicalName(meth);
    }

    @Override // javax.sip.header.RSeqHeader
    public int getSequenceNumber() {
        if (this.seqno == null) {
            return 0;
        }
        return this.seqno.intValue();
    }

    @Override // javax.sip.header.RSeqHeader
    public long getSeqNumber() {
        return this.seqno.longValue();
    }
}