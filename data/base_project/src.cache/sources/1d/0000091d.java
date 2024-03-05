package android.net;

/* loaded from: ParseException.class */
public class ParseException extends RuntimeException {
    public String response;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParseException(String response) {
        this.response = response;
    }
}