package gov.nist.javax.sip;

import java.security.cert.Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.sip.SipProvider;
import javax.sip.Transaction;

/* loaded from: TransactionExt.class */
public interface TransactionExt extends Transaction {
    @Override // javax.sip.Transaction
    SipProvider getSipProvider();

    @Override // javax.sip.Transaction
    String getPeerAddress();

    @Override // javax.sip.Transaction
    int getPeerPort();

    @Override // javax.sip.Transaction
    String getTransport();

    @Override // javax.sip.Transaction
    String getHost();

    @Override // javax.sip.Transaction
    int getPort();

    String getCipherSuite() throws UnsupportedOperationException;

    Certificate[] getLocalCertificates() throws UnsupportedOperationException;

    Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException;
}