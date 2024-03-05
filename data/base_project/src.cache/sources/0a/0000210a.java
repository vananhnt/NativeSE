package gov.nist.javax.sip.clientauthutils;

import javax.sip.ClientTransaction;

/* loaded from: AccountManager.class */
public interface AccountManager {
    UserCredentials getCredentials(ClientTransaction clientTransaction, String str);
}