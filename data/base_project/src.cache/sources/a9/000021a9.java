package gov.nist.javax.sip.header.ims;

import javax.sip.header.Header;
import javax.sip.header.Parameters;

/* loaded from: PUserDatabaseHeader.class */
public interface PUserDatabaseHeader extends Parameters, Header {
    public static final String NAME = "P-User-Database";

    String getDatabaseName();

    void setDatabaseName(String str);
}