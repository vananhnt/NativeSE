package org.apache.http.auth;

import org.apache.http.Header;
import org.apache.http.HttpRequest;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: AuthScheme.class */
public interface AuthScheme {
    void processChallenge(Header header) throws MalformedChallengeException;

    String getSchemeName();

    String getParameter(String str);

    String getRealm();

    boolean isConnectionBased();

    boolean isComplete();

    Header authenticate(Credentials credentials, HttpRequest httpRequest) throws AuthenticationException;
}