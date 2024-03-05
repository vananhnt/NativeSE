package javax.sip.header;

import java.text.ParseException;

/* loaded from: OrganizationHeader.class */
public interface OrganizationHeader extends Header {
    public static final String NAME = "Organization";

    String getOrganization();

    void setOrganization(String str) throws ParseException;
}