package gov.nist.javax.sip.header.extensions;

import gov.nist.core.Separators;
import gov.nist.javax.sip.header.CallIdentifier;
import gov.nist.javax.sip.header.ParameterNames;
import gov.nist.javax.sip.header.ParametersHeader;
import java.text.ParseException;
import javax.sip.header.ExtensionHeader;

/* loaded from: Replaces.class */
public class Replaces extends ParametersHeader implements ExtensionHeader, ReplacesHeader {
    private static final long serialVersionUID = 8765762413224043300L;
    public static final String NAME = "Replaces";
    public CallIdentifier callIdentifier;
    public String callId;

    public Replaces() {
        super("Replaces");
    }

    public Replaces(String callId) throws IllegalArgumentException {
        super("Replaces");
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

    @Override // gov.nist.javax.sip.header.extensions.ReplacesHeader
    public String getCallId() {
        return this.callId;
    }

    public CallIdentifier getCallIdentifer() {
        return this.callIdentifier;
    }

    @Override // gov.nist.javax.sip.header.extensions.ReplacesHeader
    public void setCallId(String cid) {
        this.callId = cid;
    }

    public void setCallIdentifier(CallIdentifier cid) {
        this.callIdentifier = cid;
    }

    @Override // gov.nist.javax.sip.header.extensions.ReplacesHeader
    public String getToTag() {
        if (this.parameters == null) {
            return null;
        }
        return getParameter(ParameterNames.TO_TAG);
    }

    @Override // gov.nist.javax.sip.header.extensions.ReplacesHeader
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

    @Override // gov.nist.javax.sip.header.extensions.ReplacesHeader
    public String getFromTag() {
        if (this.parameters == null) {
            return null;
        }
        return getParameter(ParameterNames.FROM_TAG);
    }

    @Override // gov.nist.javax.sip.header.extensions.ReplacesHeader
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