package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.AcceptEncodingHeader;

/* loaded from: AcceptEncoding.class */
public final class AcceptEncoding extends ParametersHeader implements AcceptEncodingHeader {
    private static final long serialVersionUID = -1476807565552873525L;
    protected String contentCoding;

    public AcceptEncoding() {
        super("Accept-Encoding");
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        return encode(new StringBuffer()).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // gov.nist.javax.sip.header.SIPHeader
    public StringBuffer encodeBody(StringBuffer buffer) {
        if (this.contentCoding != null) {
            buffer.append(this.contentCoding);
        }
        if (this.parameters != null && !this.parameters.isEmpty()) {
            buffer.append(Separators.SEMICOLON).append(this.parameters.encode());
        }
        return buffer;
    }

    @Override // javax.sip.header.AcceptEncodingHeader
    public float getQValue() {
        return getParameterAsFloat("q");
    }

    @Override // javax.sip.header.Encoding
    public String getEncoding() {
        return this.contentCoding;
    }

    @Override // javax.sip.header.AcceptEncodingHeader
    public void setQValue(float q) throws InvalidArgumentException {
        if (q < 0.0d || q > 1.0d) {
            throw new InvalidArgumentException("qvalue out of range!");
        }
        super.setParameter("q", q);
    }

    @Override // javax.sip.header.Encoding
    public void setEncoding(String encoding) throws ParseException {
        if (encoding == null) {
            throw new NullPointerException(" encoding parameter is null");
        }
        this.contentCoding = encoding;
    }
}