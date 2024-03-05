package javax.xml.transform;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ErrorListener.class */
public interface ErrorListener {
    void warning(TransformerException transformerException) throws TransformerException;

    void error(TransformerException transformerException) throws TransformerException;

    void fatalError(TransformerException transformerException) throws TransformerException;
}