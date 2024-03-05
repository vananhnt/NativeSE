package gov.nist.javax.sip.header.ims;

import java.text.ParseException;
import javax.sip.header.WWWAuthenticateHeader;

/* loaded from: WWWAuthenticateHeaderIms.class */
public interface WWWAuthenticateHeaderIms extends WWWAuthenticateHeader {
    public static final String IK = "ik";
    public static final String CK = "ck";

    void setIK(String str) throws ParseException;

    String getIK();

    void setCK(String str) throws ParseException;

    String getCK();
}