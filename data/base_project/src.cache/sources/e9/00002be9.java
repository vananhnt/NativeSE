package org.ccil.cowan.tagsoup;

import java.io.IOException;
import java.io.Reader;
import org.xml.sax.SAXException;

/* loaded from: Scanner.class */
public interface Scanner {
    void scan(Reader reader, ScanHandler scanHandler) throws IOException, SAXException;

    void resetDocumentLocator(String str, String str2);

    void startCDATA();
}