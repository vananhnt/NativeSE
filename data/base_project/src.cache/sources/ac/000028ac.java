package javax.xml.xpath;

import javax.xml.namespace.QName;
import org.xml.sax.InputSource;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XPathExpression.class */
public interface XPathExpression {
    Object evaluate(Object obj, QName qName) throws XPathExpressionException;

    String evaluate(Object obj) throws XPathExpressionException;

    Object evaluate(InputSource inputSource, QName qName) throws XPathExpressionException;

    String evaluate(InputSource inputSource) throws XPathExpressionException;
}