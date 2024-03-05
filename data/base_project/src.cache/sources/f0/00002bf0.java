package org.ccil.cowan.tagsoup.jaxp;

import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/* loaded from: SAXFactoryImpl.class */
public class SAXFactoryImpl extends SAXParserFactory {
    private SAXParserImpl prototypeParser = null;
    private HashMap features = null;

    @Override // javax.xml.parsers.SAXParserFactory
    public SAXParser newSAXParser() throws ParserConfigurationException {
        try {
            return SAXParserImpl.newInstance(this.features);
        } catch (SAXException se) {
            throw new ParserConfigurationException(se.getMessage());
        }
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setFeature(String name, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        getPrototype().setFeature(name, value);
        if (this.features == null) {
            this.features = new LinkedHashMap();
        }
        this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean getFeature(String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        return getPrototype().getFeature(name);
    }

    private SAXParserImpl getPrototype() {
        if (this.prototypeParser == null) {
            this.prototypeParser = new SAXParserImpl();
        }
        return this.prototypeParser;
    }
}