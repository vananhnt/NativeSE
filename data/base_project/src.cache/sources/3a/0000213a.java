package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import javax.sip.header.ExtensionHeader;

/* loaded from: ExtensionHeaderImpl.class */
public class ExtensionHeaderImpl extends SIPHeader implements ExtensionHeader {
    private static final long serialVersionUID = -8693922839612081849L;
    protected String value;

    public ExtensionHeaderImpl() {
    }

    public ExtensionHeaderImpl(String headerName) {
        super(headerName);
    }

    public void setName(String headerName) {
        this.headerName = headerName;
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) {
        this.value = value;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String getHeaderValue() {
        if (this.value != null) {
            return this.value;
        }
        try {
            String encodedHdr = encode();
            StringBuffer buffer = new StringBuffer(encodedHdr);
            while (buffer.length() > 0 && buffer.charAt(0) != ':') {
                buffer.deleteCharAt(0);
            }
            buffer.deleteCharAt(0);
            this.value = buffer.toString().trim();
            return this.value;
        } catch (Exception e) {
            return null;
        }
    }

    @Override // gov.nist.javax.sip.header.SIPHeader, gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public String encode() {
        return new StringBuffer(this.headerName).append(Separators.COLON).append(Separators.SP).append(this.value).append(Separators.NEWLINE).toString();
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return getHeaderValue();
    }
}