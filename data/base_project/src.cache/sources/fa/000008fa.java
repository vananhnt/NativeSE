package android.net;

import gov.nist.core.Separators;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/* loaded from: MailTo.class */
public class MailTo {
    public static final String MAILTO_SCHEME = "mailto:";
    private HashMap<String, String> mHeaders = new HashMap<>();
    private static final String TO = "to";
    private static final String BODY = "body";
    private static final String CC = "cc";
    private static final String SUBJECT = "subject";

    public static boolean isMailTo(String url) {
        if (url != null && url.startsWith("mailto:")) {
            return true;
        }
        return false;
    }

    public static MailTo parse(String url) throws ParseException {
        if (url == null) {
            throw new NullPointerException();
        }
        if (!isMailTo(url)) {
            throw new ParseException("Not a mailto scheme");
        }
        String noScheme = url.substring("mailto:".length());
        Uri email = Uri.parse(noScheme);
        MailTo m = new MailTo();
        String query = email.getQuery();
        if (query != null) {
            String[] queries = query.split(Separators.AND);
            for (String q : queries) {
                String[] nameval = q.split(Separators.EQUALS);
                if (nameval.length != 0) {
                    m.mHeaders.put(Uri.decode(nameval[0]).toLowerCase(Locale.ROOT), nameval.length > 1 ? Uri.decode(nameval[1]) : null);
                }
            }
        }
        String address = email.getPath();
        if (address != null) {
            String addr = m.getTo();
            if (addr != null) {
                address = address + ", " + addr;
            }
            m.mHeaders.put(TO, address);
        }
        return m;
    }

    public String getTo() {
        return this.mHeaders.get(TO);
    }

    public String getCc() {
        return this.mHeaders.get(CC);
    }

    public String getSubject() {
        return this.mHeaders.get("subject");
    }

    public String getBody() {
        return this.mHeaders.get("body");
    }

    public Map<String, String> getHeaders() {
        return this.mHeaders;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("mailto:");
        sb.append('?');
        for (Map.Entry<String, String> header : this.mHeaders.entrySet()) {
            sb.append(Uri.encode(header.getKey()));
            sb.append('=');
            sb.append(Uri.encode(header.getValue()));
            sb.append('&');
        }
        return sb.toString();
    }

    private MailTo() {
    }
}