package javax.sip.header;

import javax.sip.address.URI;

/* loaded from: AlertInfoHeader.class */
public interface AlertInfoHeader extends Header, Parameters {
    public static final String NAME = "Alert-Info";

    URI getAlertInfo();

    void setAlertInfo(URI uri);

    void setAlertInfo(String str);
}