package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import gov.nist.javax.sip.address.GenericURI;
import java.text.ParseException;
import javax.sip.address.URI;
import javax.sip.header.AlertInfoHeader;

/* loaded from: AlertInfo.class */
public final class AlertInfo extends ParametersHeader implements AlertInfoHeader {
    private static final long serialVersionUID = 4159657362051508719L;
    protected GenericURI uri;
    protected String string;

    public AlertInfo() {
        super("Alert-Info");
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        StringBuffer encoding = new StringBuffer();
        if (this.uri != null) {
            encoding.append(Separators.LESS_THAN).append(this.uri.encode()).append(Separators.GREATER_THAN);
        } else if (this.string != null) {
            encoding.append(this.string);
        }
        if (!this.parameters.isEmpty()) {
            encoding.append(Separators.SEMICOLON).append(this.parameters.encode());
        }
        return encoding.toString();
    }

    @Override // javax.sip.header.AlertInfoHeader
    public void setAlertInfo(URI uri) {
        this.uri = (GenericURI) uri;
    }

    @Override // javax.sip.header.AlertInfoHeader
    public void setAlertInfo(String string) {
        this.string = string;
    }

    @Override // javax.sip.header.AlertInfoHeader
    public URI getAlertInfo() {
        URI alertInfoUri = null;
        if (this.uri != null) {
            alertInfoUri = this.uri;
        } else {
            try {
                alertInfoUri = new GenericURI(this.string);
            } catch (ParseException e) {
            }
        }
        return alertInfoUri;
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.core.GenericObject
    public Object clone() {
        AlertInfo retval = (AlertInfo) super.clone();
        if (this.uri != null) {
            retval.uri = (GenericURI) this.uri.clone();
        } else if (this.string != null) {
            retval.string = this.string;
        }
        return retval;
    }
}