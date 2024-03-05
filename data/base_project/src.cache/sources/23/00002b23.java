package org.apache.http.impl.auth;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.util.CharArrayBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NTLMScheme.class */
public class NTLMScheme extends AuthSchemeBase {

    /* loaded from: NTLMScheme$State.class */
    enum State {
        UNINITIATED,
        CHALLENGE_RECEIVED,
        MSG_TYPE1_GENERATED,
        MSG_TYPE2_RECEVIED,
        MSG_TYPE3_GENERATED,
        FAILED
    }

    public NTLMScheme(NTLMEngine engine) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.auth.AuthScheme
    public String getSchemeName() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.auth.AuthScheme
    public String getParameter(String name) {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.auth.AuthScheme
    public String getRealm() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.auth.AuthScheme
    public boolean isConnectionBased() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.auth.AuthSchemeBase
    protected void parseChallenge(CharArrayBuffer buffer, int pos, int len) throws MalformedChallengeException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.auth.AuthScheme
    public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.auth.AuthScheme
    public boolean isComplete() {
        throw new RuntimeException("Stub!");
    }
}