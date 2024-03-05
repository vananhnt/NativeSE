package javax.xml.namespace;

import java.util.Iterator;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NamespaceContext.class */
public interface NamespaceContext {
    String getNamespaceURI(String str);

    String getPrefix(String str);

    Iterator getPrefixes(String str);
}