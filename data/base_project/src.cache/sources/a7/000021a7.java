package gov.nist.javax.sip.header.ims;

/* loaded from: PServedUserHeader.class */
public interface PServedUserHeader {
    public static final String NAME = "P-Served-User";

    void setSessionCase(String str);

    String getSessionCase();

    void setRegistrationState(String str);

    String getRegistrationState();
}