package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import gov.nist.javax.sip.Utils;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.ReasonHeader;

/* loaded from: Reason.class */
public class Reason extends ParametersHeader implements ReasonHeader {
    private static final long serialVersionUID = -8903376965568297388L;
    public final String TEXT = "text";
    public final String CAUSE = "cause";
    protected String protocol;

    @Override // javax.sip.header.ReasonHeader
    public int getCause() {
        return getParameterAsInt(ParameterNames.CAUSE);
    }

    @Override // javax.sip.header.ReasonHeader
    public void setCause(int cause) throws InvalidArgumentException {
        this.parameters.set(ParameterNames.CAUSE, Integer.valueOf(cause));
    }

    @Override // javax.sip.header.ReasonHeader
    public void setProtocol(String protocol) throws ParseException {
        this.protocol = protocol;
    }

    @Override // javax.sip.header.ReasonHeader
    public String getProtocol() {
        return this.protocol;
    }

    @Override // javax.sip.header.ReasonHeader
    public void setText(String text) throws ParseException {
        if (text.charAt(0) != '\"') {
            text = Utils.getQuotedString(text);
        }
        this.parameters.set("text", text);
    }

    @Override // javax.sip.header.ReasonHeader
    public String getText() {
        return this.parameters.getParameter("text");
    }

    public Reason() {
        super("Reason");
        this.TEXT = "text";
        this.CAUSE = ParameterNames.CAUSE;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader, javax.sip.header.Header
    public String getName() {
        return "Reason";
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    protected String encodeBody() {
        StringBuffer s = new StringBuffer();
        s.append(this.protocol);
        if (this.parameters != null && !this.parameters.isEmpty()) {
            s.append(Separators.SEMICOLON).append(this.parameters.encode());
        }
        return s.toString();
    }
}