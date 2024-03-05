package org.xml.sax;

import java.io.IOException;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: EntityResolver.class */
public interface EntityResolver {
    InputSource resolveEntity(String str, String str2) throws SAXException, IOException;
}