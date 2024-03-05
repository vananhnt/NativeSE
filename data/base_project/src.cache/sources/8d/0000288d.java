package javax.xml.transform;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Result.class */
public interface Result {
    public static final String PI_DISABLE_OUTPUT_ESCAPING = "javax.xml.transform.disable-output-escaping";
    public static final String PI_ENABLE_OUTPUT_ESCAPING = "javax.xml.transform.enable-output-escaping";

    void setSystemId(String str);

    String getSystemId();
}