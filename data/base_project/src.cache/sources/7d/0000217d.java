package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.WarningHeader;

/* loaded from: Warning.class */
public class Warning extends SIPHeader implements WarningHeader {
    private static final long serialVersionUID = -3433328864230783899L;
    protected int code;
    protected String agent;
    protected String text;

    public Warning() {
        super("Warning");
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return this.text != null ? Integer.toString(this.code) + Separators.SP + this.agent + Separators.SP + Separators.DOUBLE_QUOTE + this.text + Separators.DOUBLE_QUOTE : Integer.toString(this.code) + Separators.SP + this.agent;
    }

    @Override // javax.sip.header.WarningHeader
    public int getCode() {
        return this.code;
    }

    @Override // javax.sip.header.WarningHeader
    public String getAgent() {
        return this.agent;
    }

    @Override // javax.sip.header.WarningHeader
    public String getText() {
        return this.text;
    }

    @Override // javax.sip.header.WarningHeader
    public void setCode(int code) throws InvalidArgumentException {
        if (code > 99 && code < 1000) {
            this.code = code;
            return;
        }
        throw new InvalidArgumentException("Code parameter in the Warning header is invalid: code=" + code);
    }

    @Override // javax.sip.header.WarningHeader
    public void setAgent(String host) throws ParseException {
        if (host == null) {
            throw new NullPointerException("the host parameter in the Warning header is null");
        }
        this.agent = host;
    }

    @Override // javax.sip.header.WarningHeader
    public void setText(String text) throws ParseException {
        if (text == null) {
            throw new ParseException("The text parameter in the Warning header is null", 0);
        }
        this.text = text;
    }
}