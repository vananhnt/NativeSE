package javax.xml.transform;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SourceLocator.class */
public interface SourceLocator {
    String getPublicId();

    String getSystemId();

    int getLineNumber();

    int getColumnNumber();
}