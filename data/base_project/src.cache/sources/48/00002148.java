package gov.nist.javax.sip.header;

import java.text.ParseException;
import javax.sip.header.OrganizationHeader;

/* loaded from: Organization.class */
public class Organization extends SIPHeader implements OrganizationHeader {
    private static final long serialVersionUID = -2775003113740192712L;
    protected String organization;

    @Override // gov.nist.javax.sip.header.SIPHeader
    public String encodeBody() {
        return this.organization;
    }

    public Organization() {
        super("Organization");
    }

    @Override // javax.sip.header.OrganizationHeader
    public String getOrganization() {
        return this.organization;
    }

    @Override // javax.sip.header.OrganizationHeader
    public void setOrganization(String o) throws ParseException {
        if (o == null) {
            throw new NullPointerException("JAIN-SIP Exception, Organization, setOrganization(), the organization parameter is null");
        }
        this.organization = o;
    }
}