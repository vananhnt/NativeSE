package javax.sip.address;

import java.text.ParseException;
import javax.sip.header.Parameters;

/* loaded from: TelURL.class */
public interface TelURL extends URI, Parameters {
    String getIsdnSubAddress();

    void setIsdnSubAddress(String str) throws ParseException;

    String getPhoneContext();

    void setPhoneContext(String str) throws ParseException;

    String getPhoneNumber();

    void setPhoneNumber(String str) throws ParseException;

    String getPostDial();

    void setPostDial(String str) throws ParseException;

    boolean isGlobal();

    void setGlobal(boolean z);
}