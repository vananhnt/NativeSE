package java.net;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: CookiePolicy.class */
public interface CookiePolicy {
    public static final CookiePolicy ACCEPT_ALL = null;
    public static final CookiePolicy ACCEPT_NONE = null;
    public static final CookiePolicy ACCEPT_ORIGINAL_SERVER = null;

    boolean shouldAccept(URI uri, HttpCookie httpCookie);

    /* renamed from: java.net.CookiePolicy$1  reason: invalid class name */
    /* loaded from: CookiePolicy$1.class */
    static class AnonymousClass1 implements CookiePolicy {
        AnonymousClass1() {
        }

        @Override // java.net.CookiePolicy
        public boolean shouldAccept(URI uri, HttpCookie cookie) {
            return true;
        }
    }

    /* renamed from: java.net.CookiePolicy$2  reason: invalid class name */
    /* loaded from: CookiePolicy$2.class */
    static class AnonymousClass2 implements CookiePolicy {
        AnonymousClass2() {
        }

        @Override // java.net.CookiePolicy
        public boolean shouldAccept(URI uri, HttpCookie cookie) {
            return false;
        }
    }

    /* renamed from: java.net.CookiePolicy$3  reason: invalid class name */
    /* loaded from: CookiePolicy$3.class */
    static class AnonymousClass3 implements CookiePolicy {
        AnonymousClass3() {
        }

        @Override // java.net.CookiePolicy
        public boolean shouldAccept(URI uri, HttpCookie cookie) {
            return HttpCookie.domainMatches(cookie.getDomain(), uri.getHost());
        }
    }
}