package gov.nist.javax.sip.header.ims;

import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.AuthorizationHeader;

/* loaded from: AuthorizationHeaderIms.class */
public interface AuthorizationHeaderIms extends AuthorizationHeader {
    public static final String YES = "yes";
    public static final String NO = "no";

    void setIntegrityProtected(String str) throws InvalidArgumentException, ParseException;

    String getIntegrityProtected();
}