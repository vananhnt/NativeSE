package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.RetryAfterHeader;

/* loaded from: RetryAfter.class */
public class RetryAfter extends ParametersHeader implements RetryAfterHeader {
    private static final long serialVersionUID = -1029458515616146140L;
    public static final String DURATION = "duration";
    protected Integer retryAfter;
    protected String comment;

    public RetryAfter() {
        super("Retry-After");
        this.retryAfter = new Integer(0);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        StringBuffer s = new StringBuffer();
        if (this.retryAfter != null) {
            s.append(this.retryAfter);
        }
        if (this.comment != null) {
            s.append(" (" + this.comment + Separators.RPAREN);
        }
        if (!this.parameters.isEmpty()) {
            s.append(Separators.SEMICOLON + this.parameters.encode());
        }
        return s.toString();
    }

    @Override // javax.sip.header.RetryAfterHeader
    public boolean hasComment() {
        return this.comment != null;
    }

    @Override // javax.sip.header.RetryAfterHeader
    public void removeComment() {
        this.comment = null;
    }

    @Override // javax.sip.header.RetryAfterHeader
    public void removeDuration() {
        super.removeParameter("duration");
    }

    @Override // javax.sip.header.RetryAfterHeader
    public void setRetryAfter(int retryAfter) throws InvalidArgumentException {
        if (retryAfter < 0) {
            throw new InvalidArgumentException("invalid parameter " + retryAfter);
        }
        this.retryAfter = Integer.valueOf(retryAfter);
    }

    @Override // javax.sip.header.RetryAfterHeader
    public int getRetryAfter() {
        return this.retryAfter.intValue();
    }

    @Override // javax.sip.header.RetryAfterHeader
    public String getComment() {
        return this.comment;
    }

    @Override // javax.sip.header.RetryAfterHeader
    public void setComment(String comment) throws ParseException {
        if (comment == null) {
            throw new NullPointerException("the comment parameter is null");
        }
        this.comment = comment;
    }

    @Override // javax.sip.header.RetryAfterHeader
    public void setDuration(int duration) throws InvalidArgumentException {
        if (duration < 0) {
            throw new InvalidArgumentException("the duration parameter is <0");
        }
        setParameter("duration", duration);
    }

    @Override // javax.sip.header.RetryAfterHeader
    public int getDuration() {
        if (getParameter("duration") == null) {
            return -1;
        }
        return super.getParameterAsInt("duration");
    }
}