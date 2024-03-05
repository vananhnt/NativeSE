package gov.nist.javax.sip.clientauthutils;

import javax.sip.ClientTransaction;

/* loaded from: SecureAccountManager.class */
public interface SecureAccountManager {
    UserCredentialHash getCredentialHash(ClientTransaction clientTransaction, String str);
}