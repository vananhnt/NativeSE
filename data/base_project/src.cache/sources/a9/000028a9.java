package javax.xml.xpath;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.xml.sax.InputSource;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XPath.class */
public interface XPath {
    void reset();

    void setXPathVariableResolver(XPathVariableResolver xPathVariableResolver);

    XPathVariableResolver getXPathVariableResolver();

    void setXPathFunctionResolver(XPathFunctionResolver xPathFunctionResolver);

    XPathFunctionResolver getXPathFunctionResolver();

    void setNamespaceContext(NamespaceContext namespaceContext);

    NamespaceContext getNamespaceContext();

    XPathExpression compile(String str) throws XPathExpressionException;

    Object evaluate(String str, Object obj, QName qName) throws XPathExpressionException;

    String evaluate(String str, Object obj) throws XPathExpressionException;

    Object evaluate(String str, InputSource inputSource, QName qName) throws XPathExpressionException;

    String evaluate(String str, InputSource inputSource) throws XPathExpressionException;
}