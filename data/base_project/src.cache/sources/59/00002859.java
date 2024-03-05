package javax.sip.header;

import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;

/* loaded from: ServerHeader.class */
public interface ServerHeader extends Header {
    public static final String NAME = "Server";

    ListIterator getProduct();

    void setProduct(List list) throws ParseException;

    void addProductToken(String str);
}