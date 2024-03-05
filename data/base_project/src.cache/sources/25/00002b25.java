package org.apache.http.impl.auth;

import java.util.Map;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.util.CharArrayBuffer;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: RFC2617Scheme.class */
public abstract class RFC2617Scheme extends AuthSchemeBase {
    public RFC2617Scheme() {
        throw new RuntimeException("Stub!");
    }

    @Override // org.apache.http.impl.auth.AuthSchemeBase
    protected void parseChallenge(CharArrayBuffer buffer, int pos, int len) throws MalformedChallengeException {
        throw new RuntimeException("Stub!");
    }

    protected Map<String, String> getParameters() {
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
}