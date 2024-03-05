package java.net;

import java.io.UnsupportedEncodingException;
import libcore.net.UriCodec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: URLEncoder.class */
public class URLEncoder {
    URLEncoder() {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public static String encode(String s) {
        throw new RuntimeException("Stub!");
    }

    public static String encode(String s, String charsetName) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.net.URLEncoder$1  reason: invalid class name */
    /* loaded from: URLEncoder$1.class */
    static class AnonymousClass1 extends UriCodec {
        AnonymousClass1() {
        }

        @Override // libcore.net.UriCodec
        protected boolean isRetained(char c) {
            return " .-*_".indexOf(c) != -1;
        }
    }
}