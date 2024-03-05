package javax.sip.header;

import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

/* loaded from: UserAgentHeader.class */
public interface UserAgentHeader extends Header {
    public static final String NAME = "User-Agent";

    ListIterator getProduct();

    void setProduct(List list) throws ParseException;

    void addProductToken(String str);
}