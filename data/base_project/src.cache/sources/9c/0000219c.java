package gov.nist.javax.sip.header.ims;

import java.text.ParseException;
import javax.sip.header.Header;
import javax.sip.header.Parameters;

/* loaded from: PChargingVectorHeader.class */
public interface PChargingVectorHeader extends Header, Parameters {
    public static final String NAME = "P-Charging-Vector";

    String getICID();

    void setICID(String str) throws ParseException;

    String getICIDGeneratedAt();

    void setICIDGeneratedAt(String str) throws ParseException;

    String getOriginatingIOI();

    void setOriginatingIOI(String str) throws ParseException;

    String getTerminatingIOI();

    void setTerminatingIOI(String str) throws ParseException;
}