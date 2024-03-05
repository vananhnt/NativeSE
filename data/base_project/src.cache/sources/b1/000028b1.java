package javax.xml.xpath;

import java.util.List;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XPathFunction.class */
public interface XPathFunction {
    Object evaluate(List list) throws XPathFunctionException;
}