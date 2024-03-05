package javax.sip.header;

import java.text.ParseException;
import java.util.Iterator;

/* loaded from: Parameters.class */
public interface Parameters {
    String getParameter(String str);

    void setParameter(String str, String str2) throws ParseException;

    Iterator getParameterNames();

    void removeParameter(String str);
}