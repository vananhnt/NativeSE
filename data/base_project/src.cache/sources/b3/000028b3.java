package javax.xml.xpath;

import javax.xml.namespace.QName;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XPathFunctionResolver.class */
public interface XPathFunctionResolver {
    XPathFunction resolveFunction(QName qName, int i);
}