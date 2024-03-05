package org.apache.harmony.xml.parsers;

import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXNotRecognizedException;

/* loaded from: SAXParserFactoryImpl.class */
public class SAXParserFactoryImpl extends SAXParserFactory {
    private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String VALIDATION = "http://xml.org/sax/features/validation";
    private Map<String, Boolean> features = new HashMap();

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean getFeature(String name) throws SAXNotRecognizedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (!name.startsWith("http://xml.org/sax/features/")) {
            throw new SAXNotRecognizedException(name);
        }
        return Boolean.TRUE.equals(this.features.get(name));
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean isNamespaceAware() {
        try {
            return getFeature("http://xml.org/sax/features/namespaces");
        } catch (SAXNotRecognizedException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public boolean isValidating() {
        try {
            return getFeature("http://xml.org/sax/features/validation");
        } catch (SAXNotRecognizedException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public SAXParser newSAXParser() throws ParserConfigurationException {
        if (isValidating()) {
            throw new ParserConfigurationException("No validating SAXParser implementation available");
        }
        try {
            return new SAXParserImpl(this.features);
        } catch (Exception ex) {
            throw new ParserConfigurationException(ex.toString());
        }
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (!name.startsWith("http://xml.org/sax/features/")) {
            throw new SAXNotRecognizedException(name);
        }
        if (value) {
            this.features.put(name, Boolean.TRUE);
        } else {
            this.features.put(name, Boolean.FALSE);
        }
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setNamespaceAware(boolean value) {
        try {
            setFeature("http://xml.org/sax/features/namespaces", value);
        } catch (SAXNotRecognizedException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override // javax.xml.parsers.SAXParserFactory
    public void setValidating(boolean value) {
        try {
            setFeature("http://xml.org/sax/features/validation", value);
        } catch (SAXNotRecognizedException ex) {
            throw new AssertionError(ex);
        }
    }
}