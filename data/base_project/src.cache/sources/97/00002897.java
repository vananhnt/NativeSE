package javax.xml.transform.dom;

import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DOMLocator.class */
public interface DOMLocator extends SourceLocator {
    Node getOriginatingNode();
}