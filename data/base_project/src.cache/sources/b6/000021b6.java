package gov.nist.javax.sip.header.ims;

import java.text.ParseException;
import javax.sip.InvalidArgumentException;
import javax.sip.header.Header;
import javax.sip.header.Parameters;

/* loaded from: SecurityAgreeHeader.class */
public interface SecurityAgreeHeader extends Parameters, Header {
    void setSecurityMechanism(String str) throws ParseException;

    void setEncryptionAlgorithm(String str) throws ParseException;

    void setAlgorithm(String str) throws ParseException;

    void setProtocol(String str) throws ParseException;

    void setMode(String str) throws ParseException;

    void setSPIClient(int i) throws InvalidArgumentException;

    void setSPIServer(int i) throws InvalidArgumentException;

    void setPortClient(int i) throws InvalidArgumentException;

    void setPortServer(int i) throws InvalidArgumentException;

    void setPreference(float f) throws InvalidArgumentException;

    String getSecurityMechanism();

    String getEncryptionAlgorithm();

    String getAlgorithm();

    String getProtocol();

    String getMode();

    int getSPIClient();

    int getSPIServer();

    int getPortClient();

    int getPortServer();

    float getPreference();
}