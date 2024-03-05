package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import java.text.ParseException;
import javax.sip.header.ContentDispositionHeader;

/* loaded from: ContentDisposition.class */
public final class ContentDisposition extends ParametersHeader implements ContentDispositionHeader {
    private static final long serialVersionUID = 835596496276127003L;
    protected String dispositionType;

    public ContentDisposition() {
        super("Content-Disposition");
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        StringBuffer encoding = new StringBuffer(this.dispositionType);
        if (!this.parameters.isEmpty()) {
            encoding.append(Separators.SEMICOLON).append(this.parameters.encode());
        }
        return encoding.toString();
    }

    @Override // javax.sip.header.ContentDispositionHeader
    public void setDispositionType(String dispositionType) throws ParseException {
        if (dispositionType == null) {
            throw new NullPointerException("JAIN-SIP Exception, ContentDisposition, setDispositionType(), the dispositionType parameter is null");
        }
        this.dispositionType = dispositionType;
    }

    @Override // javax.sip.header.ContentDispositionHeader
    public String getDispositionType() {
        return this.dispositionType;
    }

    @Override // javax.sip.header.ContentDispositionHeader
    public String getHandling() {
        return getParameter(ParameterNames.HANDLING);
    }

    @Override // javax.sip.header.ContentDispositionHeader
    public void setHandling(String handling) throws ParseException {
        if (handling == null) {
            throw new NullPointerException("JAIN-SIP Exception, ContentDisposition, setHandling(), the handling parameter is null");
        }
        setParameter(ParameterNames.HANDLING, handling);
    }

    public String getContentDisposition() {
        return encodeBody();
    }
}