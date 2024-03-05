package gov.nist.javax.sip.header;

import com.android.internal.location.GpsNetInitiatedHandler;
import gov.nist.core.Separators;
import gov.nist.javax.sip.address.GenericURI;
import java.text.ParseException;
import javax.sip.address.URI;
import javax.sip.header.ErrorInfoHeader;

/* loaded from: ErrorInfo.class */
public final class ErrorInfo extends ParametersHeader implements ErrorInfoHeader {
    private static final long serialVersionUID = -6347702901964436362L;
    protected GenericURI errorInfo;

    public ErrorInfo() {
        super("Error-Info");
    }

    public ErrorInfo(GenericURI errorInfo) {
        this();
        this.errorInfo = errorInfo;
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        StringBuffer retval = new StringBuffer(Separators.LESS_THAN).append(this.errorInfo.toString()).append(Separators.GREATER_THAN);
        if (!this.parameters.isEmpty()) {
            retval.append(Separators.SEMICOLON).append(this.parameters.encode());
        }
        return retval.toString();
    }

    @Override // javax.sip.header.ErrorInfoHeader
    public void setErrorInfo(URI errorInfo) {
        this.errorInfo = (GenericURI) errorInfo;
    }

    @Override // javax.sip.header.ErrorInfoHeader
    public URI getErrorInfo() {
        return this.errorInfo;
    }

    @Override // javax.sip.header.ErrorInfoHeader
    public void setErrorMessage(String message) throws ParseException {
        if (message == null) {
            throw new NullPointerException("JAIN-SIP Exception , ErrorInfoHeader, setErrorMessage(), the message parameter is null");
        }
        setParameter(GpsNetInitiatedHandler.NI_INTENT_KEY_MESSAGE, message);
    }

    @Override // javax.sip.header.ErrorInfoHeader
    public String getErrorMessage() {
        return getParameter(GpsNetInitiatedHandler.NI_INTENT_KEY_MESSAGE);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        ErrorInfo retval = (ErrorInfo) super.clone();
        if (this.errorInfo != null) {
            retval.errorInfo = (GenericURI) this.errorInfo.clone();
        }
        return retval;
    }
}