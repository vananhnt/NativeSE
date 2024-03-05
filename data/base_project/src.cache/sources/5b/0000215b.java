package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import gov.nist.javax.sip.SIPConstants;
import gov.nist.javax.sip.address.GenericURI;
import javax.sip.address.URI;

/* loaded from: RequestLine.class */
public class RequestLine extends SIPObject implements SipRequestLine {
    private static final long serialVersionUID = -3286426172326043129L;
    protected GenericURI uri;
    protected String method;
    protected String sipVersion = SIPConstants.SIP_VERSION_STRING;

    public RequestLine() {
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public String encode() {
        return encode(new StringBuffer()).toString();
    }

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public StringBuffer encode(StringBuffer buffer) {
        if (this.method != null) {
            buffer.append(this.method);
            buffer.append(Separators.SP);
        }
        if (this.uri != null) {
            this.uri.encode(buffer);
            buffer.append(Separators.SP);
        }
        buffer.append(this.sipVersion);
        buffer.append(Separators.NEWLINE);
        return buffer;
    }

    @Override // gov.nist.javax.sip.header.SipRequestLine
    public GenericURI getUri() {
        return this.uri;
    }

    public RequestLine(GenericURI requestURI, String method) {
        this.uri = requestURI;
        this.method = method;
    }

    @Override // gov.nist.javax.sip.header.SipRequestLine
    public String getMethod() {
        return this.method;
    }

    @Override // gov.nist.javax.sip.header.SipRequestLine
    public String getSipVersion() {
        return this.sipVersion;
    }

    @Override // gov.nist.javax.sip.header.SipRequestLine
    public void setUri(URI uri) {
        this.uri = (GenericURI) uri;
    }

    @Override // gov.nist.javax.sip.header.SipRequestLine
    public void setMethod(String method) {
        this.method = method;
    }

    @Override // gov.nist.javax.sip.header.SipRequestLine
    public void setSipVersion(String version) {
        this.sipVersion = version;
    }

    @Override // gov.nist.javax.sip.header.SipRequestLine
    public String getVersionMajor() {
        if (this.sipVersion == null) {
            return null;
        }
        String major = null;
        boolean slash = false;
        for (int i = 0; i < this.sipVersion.length() && this.sipVersion.charAt(i) != '.'; i++) {
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

    @Override // gov.nist.javax.sip.header.SipRequestLine
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

    @Override // gov.nist.javax.sip.header.SIPObject, gov.nist.core.GenericObject
    public boolean equals(Object other) {
        boolean retval;
        boolean z;
        if (!other.getClass().equals(getClass())) {
            return false;
        }
        RequestLine that = (RequestLine) other;
        try {
        } catch (NullPointerException e) {
            retval = false;
        }
        if (this.method.equals(that.method) && this.uri.equals(that.uri)) {
            if (this.sipVersion.equals(that.sipVersion)) {
                z = true;
                retval = z;
                return retval;
            }
        }
        z = false;
        retval = z;
        return retval;
    }

    @Override // gov.nist.core.GenericObject
    public Object clone() {
        RequestLine retval = (RequestLine) super.clone();
        if (this.uri != null) {
            retval.uri = (GenericURI) this.uri.clone();
        }
        return retval;
    }
}