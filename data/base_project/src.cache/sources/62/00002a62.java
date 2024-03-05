package org.apache.harmony.xml.parsers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import org.apache.harmony.xml.ExpatReader;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderAdapter;

/* loaded from: SAXParserImpl.class */
final class SAXParserImpl extends SAXParser {
    private Map<String, Boolean> initialFeatures;
    private XMLReader reader;
    private Parser parser;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SAXParserImpl(Map<String, Boolean> initialFeatures) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.initialFeatures = initialFeatures.isEmpty() ? Collections.emptyMap() : new HashMap<>(initialFeatures);
        resetInternal();
    }

    private void resetInternal() throws SAXNotSupportedException, SAXNotRecognizedException {
        this.reader = new ExpatReader();
        for (Map.Entry<String, Boolean> entry : this.initialFeatures.entrySet()) {
            this.reader.setFeature(entry.getKey(), entry.getValue().booleanValue());
        }
    }

    @Override // javax.xml.parsers.SAXParser
    public void reset() {
        try {
            resetInternal();
        } catch (SAXNotRecognizedException e) {
            throw new AssertionError();
        } catch (SAXNotSupportedException e2) {
            throw new AssertionError();
        }
    }

    @Override // javax.xml.parsers.SAXParser
    public Parser getParser() {
        if (this.parser == null) {
            this.parser = new XMLReaderAdapter(this.reader);
        }
        return this.parser;
    }

    @Override // javax.xml.parsers.SAXParser
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.reader.getProperty(name);
    }

    @Override // javax.xml.parsers.SAXParser
    public XMLReader getXMLReader() {
        return this.reader;
    }

    @Override // javax.xml.parsers.SAXParser
    public boolean isNamespaceAware() {
        try {
            return this.reader.getFeature(org.ccil.cowan.tagsoup.Parser.namespacesFeature);
        } catch (SAXException e) {
            return false;
        }
    }

    @Override // javax.xml.parsers.SAXParser
    public boolean isValidating() {
        return false;
    }

    @Override // javax.xml.parsers.SAXParser
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.reader.setProperty(name, value);
    }
}