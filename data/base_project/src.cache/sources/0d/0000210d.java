package gov.nist.javax.sip.clientauthutils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.sip.header.AuthorizationHeader;

/* loaded from: CredentialsCache.class */
class CredentialsCache {
    private ConcurrentHashMap<String, List<AuthorizationHeader>> authorizationHeaders = new ConcurrentHashMap<>();
    private Timer timer;

    /* loaded from: CredentialsCache$TimeoutTask.class */
    class TimeoutTask extends TimerTask {
        String callId;
        String userName;

        public TimeoutTask(String userName, String proxyDomain) {
            this.callId = proxyDomain;
            this.userName = userName;
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            CredentialsCache.this.authorizationHeaders.remove(this.callId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CredentialsCache(Timer timer) {
        this.timer = timer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cacheAuthorizationHeader(String callId, AuthorizationHeader authorization, int cacheTime) {
        String user = authorization.getUsername();
        if (callId == null) {
            throw new NullPointerException("Call ID is null!");
        }
        if (authorization == null) {
            throw new NullPointerException("Null authorization domain");
        }
        List<AuthorizationHeader> authHeaders = this.authorizationHeaders.get(callId);
        if (authHeaders == null) {
            authHeaders = new LinkedList<>();
            this.authorizationHeaders.put(callId, authHeaders);
        } else {
            String realm = authorization.getRealm();
            ListIterator<AuthorizationHeader> li = authHeaders.listIterator();
            while (li.hasNext()) {
                AuthorizationHeader authHeader = li.next();
                if (realm.equals(authHeader.getRealm())) {
                    li.remove();
                }
            }
        }
        authHeaders.add(authorization);
        TimeoutTask timeoutTask = new TimeoutTask(callId, user);
        if (cacheTime != -1) {
            this.timer.schedule(timeoutTask, cacheTime * 1000);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Collection<AuthorizationHeader> getCachedAuthorizationHeaders(String callid) {
        if (callid == null) {
            throw new NullPointerException("Null arg!");
        }
        return this.authorizationHeaders.get(callid);
    }

    public void removeAuthenticationHeader(String callId) {
        this.authorizationHeaders.remove(callId);
    }
}