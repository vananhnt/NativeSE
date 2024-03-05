package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import gov.nist.javax.sip.SIPConstants;

/* loaded from: StatusLine.class */
public final class StatusLine extends SIPObject implements SipStatusLine {
    private static final long serialVersionUID = -4738092215519950414L;
    protected boolean matchStatusClass;
    protected int statusCode;
    protected String reasonPhrase = null;
    protected String sipVersion = SIPConstants.SIP_VERSION_STRING;

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean match(Object matchObj) {
        if (!(matchObj instanceof StatusLine)) {
            return false;
        }
        StatusLine sl = (StatusLine) matchObj;
        if (sl.matchExpression != null) {
            return sl.matchExpression.match(encode());
        }
        if (sl.sipVersion != null && !sl.sipVersion.equals(this.sipVersion)) {
            return false;
        }
        if (sl.statusCode != 0) {
            if (this.matchStatusClass) {
                int i = sl.statusCode;
                String codeString = Integer.toString(sl.statusCode);
                String mycode = Integer.toString(this.statusCode);
                if (codeString.charAt(0) != mycode.charAt(0)) {
                    return false;
                }
            } else if (this.statusCode != sl.statusCode) {
                return false;
            }
        }
        if (sl.reasonPhrase == null || this.reasonPhrase == sl.reasonPhrase) {
            return true;
        }
        return this.reasonPhrase.equals(sl.reasonPhrase);
    }

    public void setMatchStatusClass(boolean flag) {
        this.matchStatusClass = flag;
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public String encode() {
        String encoding = "SIP/2.0 " + this.statusCode;
        if (this.reasonPhrase != null) {
            encoding = encoding + Separators.SP + this.reasonPhrase;
        }
        return encoding + Separators.NEWLINE;
    }

    @Override // gov.nist.javax.sip.header.SipStatusLine
    public String getSipVersion() {
        return this.sipVersion;
    }

    @Override // gov.nist.javax.sip.header.SipStatusLine
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override // gov.nist.javax.sip.header.SipStatusLine
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    @Override // gov.nist.javax.sip.header.SipStatusLine
    public void setSipVersion(String s) {
        this.sipVersion = s;
    }

    @Override // gov.nist.javax.sip.header.SipStatusLine
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override // gov.nist.javax.sip.header.SipStatusLine
    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    @Override // gov.nist.javax.sip.header.SipStatusLine
    public String getVersionMajor() {
        if (this.sipVersion == null) {
            return null;
        }
        String major = null;
        boolean slash = false;
        for (int i = 0; i < this.sipVersion.length(); i++) {
            if (this.sipVersion.charAt(i) == '.') {
                slash = false;
            }
            if (slash) {
                if (major == null) {
                    major = "" + this.sipVersion.charAt(i);
                } else {
                    major = major + this.sipVersion.charAt(i);
                }
            }
            if (this.sipVersion.charAt(i) == '/') {
                slash = true;
            }
        }
        return major;
    }

    @Override // gov.nist.javax.sip.header.SipStatusLine
    public String getVersionMinor() {
        if (this.sipVersion == null) {
            return null;
        }
        String minor = null;
        boolean dot = false;
        for (int i = 0; i < this.sipVersion.length(); i++) {
            if (dot) {
                if (minor == null) {
                    minor = "" + this.sipVersion.charAt(i);
                } else {
                    minor = minor + this.sipVersion.charAt(i);
                }
            }
            if (this.sipVersion.charAt(i) == '.') {
                dot = true;
            }
        }
        return minor;
    }
}