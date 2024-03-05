package java.net;

import java.io.Serializable;
import libcore.net.UriCodec;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: URI.class */
public final class URI implements Comparable<URI>, Serializable {
    public URI(String spec) throws URISyntaxException {
        throw new RuntimeException("Stub!");
    }

    public URI(String scheme, String schemeSpecificPart, String fragment) throws URISyntaxException {
        throw new RuntimeException("Stub!");
    }

    public URI(String scheme, String userInfo, String host, int port, String path, String query, String fragment) throws URISyntaxException {
        throw new RuntimeException("Stub!");
    }

    public URI(String scheme, String host, String path, String fragment) throws URISyntaxException {
        throw new RuntimeException("Stub!");
    }

    public URI(String scheme, String authority, String path, String query, String fragment) throws URISyntaxException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(URI uri) {
        throw new RuntimeException("Stub!");
    }

    public static URI create(String uri) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    public String getScheme() {
        throw new RuntimeException("Stub!");
    }

    public String getSchemeSpecificPart() {
        throw new RuntimeException("Stub!");
    }

    public String getRawSchemeSpecificPart() {
        throw new RuntimeException("Stub!");
    }

    public String getAuthority() {
        throw new RuntimeException("Stub!");
    }

    public String getRawAuthority() {
        throw new RuntimeException("Stub!");
    }

    public String getUserInfo() {
        throw new RuntimeException("Stub!");
    }

    public String getRawUserInfo() {
        throw new RuntimeException("Stub!");
    }

    public String getHost() {
        throw new RuntimeException("Stub!");
    }

    public int getPort() {
        throw new RuntimeException("Stub!");
    }

    public String getPath() {
        throw new RuntimeException("Stub!");
    }

    public String getRawPath() {
        throw new RuntimeException("Stub!");
    }

    public String getQuery() {
        throw new RuntimeException("Stub!");
    }

    public String getRawQuery() {
        throw new RuntimeException("Stub!");
    }

    public String getFragment() {
        throw new RuntimeException("Stub!");
    }

    public String getRawFragment() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public boolean isAbsolute() {
        throw new RuntimeException("Stub!");
    }

    public boolean isOpaque() {
        throw new RuntimeException("Stub!");
    }

    public URI normalize() {
        throw new RuntimeException("Stub!");
    }

    public URI parseServerAuthority() throws URISyntaxException {
        throw new RuntimeException("Stub!");
    }

    public URI relativize(URI relative) {
        throw new RuntimeException("Stub!");
    }

    public URI resolve(URI relative) {
        throw new RuntimeException("Stub!");
    }

    public URI resolve(String relative) {
        throw new RuntimeException("Stub!");
    }

    public String toASCIIString() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public URL toURL() throws MalformedURLException {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.net.URI$1  reason: invalid class name */
    /* loaded from: URI$1.class */
    static class AnonymousClass1 extends UriCodec {
        AnonymousClass1() {
        }

        @Override // libcore.net.UriCodec
        protected boolean isRetained(char c) {
            return c <= 127;
        }
    }

    /* loaded from: URI$PartEncoder.class */
    private static class PartEncoder extends UriCodec {
        private final String extraLegalCharacters;

        PartEncoder(String extraLegalCharacters) {
            this.extraLegalCharacters = extraLegalCharacters;
        }

        @Override // libcore.net.UriCodec
        protected boolean isRetained(char c) {
            return (URI.UNRESERVED.indexOf(c) == -1 && URI.PUNCTUATION.indexOf(c) == -1 && this.extraLegalCharacters.indexOf(c) == -1 && (c <= 127 || Character.isSpaceChar(c) || Character.isISOControl(c))) ? false : true;
        }
    }
}