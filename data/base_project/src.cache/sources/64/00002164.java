package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import javax.sip.header.Header;

/* loaded from: SIPHeader.class */
public abstract class SIPHeader extends SIPObject implements SIPHeaderNames, Header, HeaderExt {
    protected String headerName;

    protected abstract String encodeBody();

    /* JADX INFO: Access modifiers changed from: protected */
    public SIPHeader(String hname) {
        this.headerName = hname;
    }

    public SIPHeader() {
    }

    public String getHeaderName() {
        return this.headerName;
    }

    public String getName() {
        return this.headerName;
    }

    public void setHeaderName(String hdrname) {
        this.headerName = hdrname;
    }

    public String getHeaderValue() {
        try {
            String encodedHdr = encode();
            StringBuffer buffer = new StringBuffer(encodedHdr);
            while (buffer.length() > 0 && buffer.charAt(0) != ':') {
                buffer.deleteCharAt(0);
            }
            if (buffer.length() > 0) {
                buffer.deleteCharAt(0);
            }
            return buffer.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isHeaderList() {
        return false;
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public String encode() {
        return encode(new StringBuffer()).toString();
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public StringBuffer encode(StringBuffer buffer) {
        buffer.append(this.headerName).append(Separators.COLON).append(Separators.SP);
        encodeBody(buffer);
        buffer.append(Separators.NEWLINE);
        return buffer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StringBuffer encodeBody(StringBuffer buffer) {
        return buffer.append(encodeBody());
    }

    @Override // gov.nist.javax.sip.header.HeaderExt
    public String getValue() {
        return getHeaderValue();
    }

    public int hashCode() {
        return this.headerName.hashCode();
    }

    @Override // gov.nist.javax.sip.header.SIPObject, javax.sip.header.Header
    public final String toString() {
        return encode();
    }
}