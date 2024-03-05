package javax.sip.header;

import javax.sip.address.URI;

/* loaded from: CallInfoHeader.class */
public interface CallInfoHeader extends Header, Parameters {
    public static final String NAME = "Call-Info";

    URI getInfo();

    void setInfo(URI uri);

    String getPurpose();

    void setPurpose(String str);
}