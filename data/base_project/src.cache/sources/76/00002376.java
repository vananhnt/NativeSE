package java.net;

import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import libcore.net.http.HttpDate;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HttpCookie.class */
public final class HttpCookie implements Cloneable {
    public HttpCookie(String name, String value) {
        throw new RuntimeException("Stub!");
    }

    public static boolean domainMatches(String domainPattern, String host) {
        throw new RuntimeException("Stub!");
    }

    public static List<HttpCookie> parse(String header) {
        throw new RuntimeException("Stub!");
    }

    public String getComment() {
        throw new RuntimeException("Stub!");
    }

    public String getCommentURL() {
        throw new RuntimeException("Stub!");
    }

    public boolean getDiscard() {
        throw new RuntimeException("Stub!");
    }

    public String getDomain() {
        throw new RuntimeException("Stub!");
    }

    public long getMaxAge() {
        throw new RuntimeException("Stub!");
    }

    public String getName() {
        throw new RuntimeException("Stub!");
    }

    public String getPath() {
        throw new RuntimeException("Stub!");
    }

    public String getPortlist() {
        throw new RuntimeException("Stub!");
    }

    public boolean getSecure() {
        throw new RuntimeException("Stub!");
    }

    public String getValue() {
        throw new RuntimeException("Stub!");
    }

    public int getVersion() {
        throw new RuntimeException("Stub!");
    }

    public boolean hasExpired() {
        throw new RuntimeException("Stub!");
    }

    public void setComment(String comment) {
        throw new RuntimeException("Stub!");
    }

    public void setCommentURL(String commentURL) {
        throw new RuntimeException("Stub!");
    }

    public void setDiscard(boolean discard) {
        throw new RuntimeException("Stub!");
    }

    public void setDomain(String pattern) {
        throw new RuntimeException("Stub!");
    }

    public void setMaxAge(long deltaSeconds) {
        throw new RuntimeException("Stub!");
    }

    public void setPath(String path) {
        throw new RuntimeException("Stub!");
    }

    public void setPortlist(String portList) {
        throw new RuntimeException("Stub!");
    }

    public void setSecure(boolean secure) {
        throw new RuntimeException("Stub!");
    }

    public void setValue(String value) {
        throw new RuntimeException("Stub!");
    }

    public void setVersion(int newVersion) {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: HttpCookie$CookieParser.class */
    static class CookieParser {
        private static final String ATTRIBUTE_NAME_TERMINATORS = ",;= \t";
        private static final String WHITESPACE = " \t";
        private final String input;
        private final String inputLowerCase;
        private int pos = 0;
        boolean hasExpires = false;
        boolean hasMaxAge = false;
        boolean hasVersion = false;

        CookieParser(String input) {
            this.input = input;
            this.inputLowerCase = input.toLowerCase(Locale.US);
        }

        public List<HttpCookie> parse() {
            List<HttpCookie> cookies = new ArrayList<>(2);
            boolean pre2965 = true;
            if (this.inputLowerCase.startsWith("set-cookie2:")) {
                this.pos += "set-cookie2:".length();
                pre2965 = false;
                this.hasVersion = true;
            } else if (this.inputLowerCase.startsWith("set-cookie:")) {
                this.pos += "set-cookie:".length();
            }
            while (true) {
                String name = readAttributeName(false);
                if (name == null) {
                    if (cookies.isEmpty()) {
                        throw new IllegalArgumentException("No cookies in " + this.input);
                    }
                    return cookies;
                } else if (!readEqualsSign()) {
                    throw new IllegalArgumentException("Expected '=' after " + name + " in " + this.input);
                } else {
                    String value = readAttributeValue(pre2965 ? Separators.SEMICOLON : ",;");
                    HttpCookie cookie = new HttpCookie(name, value);
                    HttpCookie.access$002(cookie, pre2965 ? 0 : 1);
                    cookies.add(cookie);
                    while (true) {
                        skipWhitespace();
                        if (this.pos == this.input.length()) {
                            break;
                        } else if (this.input.charAt(this.pos) == ',') {
                            this.pos++;
                            break;
                        } else {
                            if (this.input.charAt(this.pos) == ';') {
                                this.pos++;
                            }
                            String attributeName = readAttributeName(true);
                            if (attributeName != null) {
                                String terminators = (pre2965 || "expires".equals(attributeName) || "port".equals(attributeName)) ? Separators.SEMICOLON : ";,";
                                String attributeValue = null;
                                if (readEqualsSign()) {
                                    attributeValue = readAttributeValue(terminators);
                                }
                                setAttribute(cookie, attributeName, attributeValue);
                            }
                        }
                    }
                    if (this.hasExpires) {
                        HttpCookie.access$002(cookie, 0);
                    } else if (this.hasMaxAge) {
                        HttpCookie.access$002(cookie, 1);
                    }
                }
            }
        }

        private void setAttribute(HttpCookie cookie, String name, String value) {
            if (name.equals("comment") && HttpCookie.access$100(cookie) == null) {
                HttpCookie.access$102(cookie, value);
            } else if (name.equals("commenturl") && HttpCookie.access$200(cookie) == null) {
                HttpCookie.access$202(cookie, value);
            } else if (name.equals("discard")) {
                HttpCookie.access$302(cookie, true);
            } else if (name.equals("domain") && HttpCookie.access$400(cookie) == null) {
                HttpCookie.access$402(cookie, value);
            } else if (name.equals("expires")) {
                this.hasExpires = true;
                if (HttpCookie.access$500(cookie) == -1) {
                    Date date = HttpDate.parse(value);
                    if (date != null) {
                        HttpCookie.access$600(cookie, date);
                    } else {
                        HttpCookie.access$502(cookie, 0L);
                    }
                }
            } else if (name.equals("max-age") && HttpCookie.access$500(cookie) == -1) {
                this.hasMaxAge = true;
                HttpCookie.access$502(cookie, Long.parseLong(value));
            } else if (name.equals("path") && HttpCookie.access$700(cookie) == null) {
                HttpCookie.access$702(cookie, value);
            } else if (name.equals("port") && HttpCookie.access$800(cookie) == null) {
                HttpCookie.access$802(cookie, value != null ? value : "");
            } else if (name.equals("secure")) {
                HttpCookie.access$902(cookie, true);
            } else if (name.equals("version") && !this.hasVersion) {
                HttpCookie.access$002(cookie, Integer.parseInt(value));
            }
        }

        private String readAttributeName(boolean returnLowerCase) {
            skipWhitespace();
            int c = find(ATTRIBUTE_NAME_TERMINATORS);
            String forSubstring = returnLowerCase ? this.inputLowerCase : this.input;
            String result = this.pos < c ? forSubstring.substring(this.pos, c) : null;
            this.pos = c;
            return result;
        }

        private boolean readEqualsSign() {
            skipWhitespace();
            if (this.pos < this.input.length() && this.input.charAt(this.pos) == '=') {
                this.pos++;
                return true;
            }
            return false;
        }

        private String readAttributeValue(String terminators) {
            skipWhitespace();
            if (this.pos < this.input.length() && (this.input.charAt(this.pos) == '\"' || this.input.charAt(this.pos) == '\'')) {
                String str = this.input;
                int i = this.pos;
                this.pos = i + 1;
                char quoteCharacter = str.charAt(i);
                int closeQuote = this.input.indexOf(quoteCharacter, this.pos);
                if (closeQuote == -1) {
                    throw new IllegalArgumentException("Unterminated string literal in " + this.input);
                }
                String result = this.input.substring(this.pos, closeQuote);
                this.pos = closeQuote + 1;
                return result;
            }
            int c = find(terminators);
            String result2 = this.input.substring(this.pos, c);
            this.pos = c;
            return result2;
        }

        private int find(String chars) {
            for (int c = this.pos; c < this.input.length(); c++) {
                if (chars.indexOf(this.input.charAt(c)) != -1) {
                    return c;
                }
            }
            return this.input.length();
        }

        private void skipWhitespace() {
            while (this.pos < this.input.length() && WHITESPACE.indexOf(this.input.charAt(this.pos)) != -1) {
                this.pos++;
            }
        }
    }
}