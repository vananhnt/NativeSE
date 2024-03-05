package gov.nist.javax.sip.header;

import gov.nist.core.Separators;
import javax.sip.InvalidArgumentException;
import javax.sip.header.MimeVersionHeader;

/* loaded from: MimeVersion.class */
public class MimeVersion extends SIPHeader implements MimeVersionHeader {
    private static final long serialVersionUID = -7951589626435082068L;
    protected int minorVersion;
    protected int majorVersion;

    public MimeVersion() {
        super("MIME-Version");
    }

    @Override // javax.sip.header.MimeVersionHeader
    public int getMinorVersion() {
        return this.minorVersion;
    }

    @Override // javax.sip.header.MimeVersionHeader
    public int getMajorVersion() {
        return this.majorVersion;
    }

    @Override // javax.sip.header.MimeVersionHeader
    public void setMinorVersion(int minorVersion) throws InvalidArgumentException {
        if (minorVersion < 0) {
            throw new InvalidArgumentException("JAIN-SIP Exception, MimeVersion, setMinorVersion(), the minorVersion parameter is null");
        }
        this.minorVersion = minorVersion;
    }

    @Override // javax.sip.header.MimeVersionHeader
    public void setMajorVersion(int majorVersion) throws InvalidArgumentException {
        if (majorVersion < 0) {
            throw new InvalidArgumentException("JAIN-SIP Exception, MimeVersion, setMajorVersion(), the majorVersion parameter is null");
        }
        this.majorVersion = majorVersion;
    }

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return Integer.toString(this.majorVersion) + Separators.DOT + Integer.toString(this.minorVersion);
    }
}