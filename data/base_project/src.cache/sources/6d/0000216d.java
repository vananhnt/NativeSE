package gov.nist.javax.sip.header;

/* loaded from: SipStatusLine.class */
public interface SipStatusLine {
    String getSipVersion();

    int getStatusCode();

    String getReasonPhrase();

    void setSipVersion(String str);

    void setStatusCode(int i);

    void setReasonPhrase(String str);

    String getVersionMajor();

    String getVersionMinor();
}