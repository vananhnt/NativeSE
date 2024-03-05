package org.xml.sax;

import java.io.IOException;
import java.util.Locale;

/* JADX WARN: Classes with same name are omitted:
  
 */
@Deprecated
/* loaded from: Parser.class */
public interface Parser {
    void setLocale(Locale locale) throws SAXException;

    void setEntityResolver(EntityResolver entityResolver);

    void setDTDHandler(DTDHandler dTDHandler);

    void setDocumentHandler(DocumentHandler documentHandler);

    void setErrorHandler(ErrorHandler errorHandler);

    void parse(InputSource inputSource) throws SAXException, IOException;

    void parse(String str) throws SAXException, IOException;
}