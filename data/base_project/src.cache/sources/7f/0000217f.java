package gov.nist.javax.sip.header.extensions;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.CallIdentifier;
import gov.nist.javax.sip.header.ParameterNames;
import gov.nist.javax.sip.header.ParametersHeader;
import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: Join.class */
public class Join extends ParametersHeader implements ExtensionHeader, JoinHeader {
    private static final long serialVersionUID = -840116548918120056L;
    public static final String NAME = "Join";
    public CallIdentifier callIdentifier;
    public String callId;

    public Join() {
        super("Join");
    }

    public Join(String callId) throws IllegalArgumentException {
        super("Join");
        this.callIdentifier = new CallIdentifier(callId);
    }

    @Override // gov.nist.javax.sip.header.ParametersHeader, gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        if (this.callId == null) {
            return null;
        }
        String retVal = this.callId;
        if (!this.parameters.isEmpty()) {
            retVal = retVal + Separators.SEMICOLON + this.parameters.encode();
        }
        return retVal;
    }

    @Override // gov.nist.javax.sip.header.extensions.JoinHeader
    public String getCallId() {
        return this.callId;
    }

    public CallIdentifier getCallIdentifer() {
        return this.callIdentifier;
    }

    @Override // gov.nist.javax.sip.header.extensions.JoinHeader
    public void setCallId(String cid) {
        this.callId = cid;
    }

    public void setCallIdentifier(CallIdentifier cid) {
        this.callIdentifier = cid;
    }

    @Override // gov.nist.javax.sip.header.extensions.JoinHeader
    public String getToTag() {
        if (this.parameters == null) {
            return null;
        }
        return getParameter(ParameterNames.TO_TAG);
    }

    @Override // gov.nist.javax.sip.header.extensions.JoinHeader
    public void setToTag(String t) throws ParseException {
        if (t == null) {
            throw new NullPointerException("null tag ");
        }
        if (t.trim().equals("")) {
            throw new ParseException("bad tag", 0);
        }
        setParameter(ParameterNames.TO_TAG, t);
    }

    public boolean hasToTag() {
        return hasParameter(ParameterNames.TO_TAG);
    }

    public void removeToTag() {
        this.parameters.delete(ParameterNames.TO_TAG);
    }

    @Override // gov.nist.javax.sip.header.extensions.JoinHeader
    public String getFromTag() {
        if (this.parameters == null) {
            return null;
        }
        return getParameter(ParameterNames.FROM_TAG);
    }

    @Override // gov.nist.javax.sip.header.extensions.JoinHeader
    public void setFromTag(String t) throws ParseException {
        if (t == null) {
            throw new NullPointerException("null tag ");
        }
        if (t.trim().equals("")) {
            throw new ParseException("bad tag", 0);
        }
        setParameter(ParameterNames.FROM_TAG, t);
    }

    public boolean hasFromTag() {
        return hasParameter(ParameterNames.FROM_TAG);
    }

    public void removeFromTag() {
        this.parameters.delete(ParameterNames.FROM_TAG);
    }

    @Override // javax.sip.header.ExtensionHeader
    public void setValue(String value) throws ParseException {
        throw new ParseException(value, 0);
    }
}