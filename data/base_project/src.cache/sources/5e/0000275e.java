package java.util.regex;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: MatchResult.class */
public interface MatchResult {
    int end();

    int end(int i);

    String group();

    String group(int i);

    int groupCount();

    int start();

    int start(int i);
}