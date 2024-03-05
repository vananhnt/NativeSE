package org.xmlpull.v1;

import java.util.ArrayList;
import java.util.HashMap;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XmlPullParserFactory.class */
public class XmlPullParserFactory {
    public static final String PROPERTY_NAME = "org.xmlpull.v1.XmlPullParserFactory";
    protected ArrayList parserClasses;
    protected String classNamesLocation;
    protected ArrayList serializerClasses;
    protected HashMap features;

    protected XmlPullParserFactory() {
        throw new RuntimeException("Stub!");
    }

    public void setFeature(String name, boolean state) throws XmlPullParserException {
        throw new RuntimeException("Stub!");
    }

    public boolean getFeature(String name) {
        throw new RuntimeException("Stub!");
    }

    public void setNamespaceAware(boolean awareness) {
        throw new RuntimeException("Stub!");
    }

    public boolean isNamespaceAware() {
        throw new RuntimeException("Stub!");
    }

    public void setValidating(boolean validating) {
        throw new RuntimeException("Stub!");
    }

    public boolean isValidating() {
        throw new RuntimeException("Stub!");
    }

    public XmlPullParser newPullParser() throws XmlPullParserException {
        throw new RuntimeException("Stub!");
    }

    public XmlSerializer newSerializer() throws XmlPullParserException {
        throw new RuntimeException("Stub!");
    }

    public static XmlPullParserFactory newInstance() throws XmlPullParserException {
        throw new RuntimeException("Stub!");
    }

    public static XmlPullParserFactory newInstance(String classNames, Class context) throws XmlPullParserException {
        throw new RuntimeException("Stub!");
    }
}