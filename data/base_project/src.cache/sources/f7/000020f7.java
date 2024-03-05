package gov.nist.javax.sip;

import gov.nist.core.net.AddressResolver;
import gov.nist.javax.sip.clientauthutils.AccountManager;
import gov.nist.javax.sip.clientauthutils.AuthenticationHelper;
import gov.nist.javax.sip.clientauthutils.SecureAccountManager;
import gov.nist.javax.sip.header.extensions.JoinHeader;
import gov.nist.javax.sip.header.extensions.ReplacesHeader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collection;
import javax.sip.Dialog;
import javax.sip.SipStack;
import javax.sip.header.HeaderFactory;

/* loaded from: SipStackExt.class */
public interface SipStackExt extends SipStack {
    @Override // javax.sip.SipStack
    Collection<Dialog> getDialogs();

    Dialog getReplacesDialog(ReplacesHeader replacesHeader);

    AuthenticationHelper getAuthenticationHelper(AccountManager accountManager, HeaderFactory headerFactory);

    AuthenticationHelper getSecureAuthenticationHelper(SecureAccountManager secureAccountManager, HeaderFactory headerFactory);

    void setAddressResolver(AddressResolver addressResolver);

    Dialog getJoinDialog(JoinHeader joinHeader);

    void setEnabledCipherSuites(String[] strArr);

    SocketAddress obtainLocalAddress(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) throws IOException;
}