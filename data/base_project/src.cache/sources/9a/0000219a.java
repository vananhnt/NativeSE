package gov.nist.javax.sip.header.ims;

import java.text.ParseException;
import java.util.ListIterator;
import javax.sip.header.Header;
import javax.sip.header.Parameters;

/* loaded from: PChargingFunctionAddressesHeader.class */
public interface PChargingFunctionAddressesHeader extends Parameters, Header {
    public static final String NAME = "P-Charging-Function-Addresses";

    void setChargingCollectionFunctionAddress(String str) throws ParseException;

    void addChargingCollectionFunctionAddress(String str) throws ParseException;

    void removeChargingCollectionFunctionAddress(String str) throws ParseException;

    ListIterator getChargingCollectionFunctionAddresses();

    void setEventChargingFunctionAddress(String str) throws ParseException;

    void addEventChargingFunctionAddress(String str) throws ParseException;

    void removeEventChargingFunctionAddress(String str) throws ParseException;

    ListIterator getEventChargingFunctionAddresses();
}