package org.apache.harmony.xml;

import org.xml.sax.Attributes;

/* loaded from: ExpatAttributes.class */
abstract class ExpatAttributes implements Attributes {
    private static final String CDATA = "CDATA";

    @Override // org.xml.sax.Attributes
    public abstract int getLength();

    abstract long getParserPointer();

    public abstract long getPointer();

    private static native String getURI(long j, long j2, int i);

    private static native String getLocalName(long j, long j2, int i);

    private static native String getQName(long j, long j2, int i);

    private static native String getValueByIndex(long j, int i);

    private static native int getIndex(long j, String str, String str2);

    private static native int getIndexForQName(long j, String str);

    private static native String getValue(long j, String str, String str2);

    private static native String getValueForQName(long j, String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public native void freeAttributes(long j);

    @Override // org.xml.sax.Attributes
    public String getURI(int index) {
        if (index < 0 || index >= getLength()) {
            return null;
        }
        return getURI(getParserPointer(), getPointer(), index);
    }

    @Override // org.xml.sax.Attributes
    public String getLocalName(int index) {
        if (index < 0 || index >= getLength()) {
            return null;
        }
        return getLocalName(getParserPointer(), getPointer(), index);
    }

    @Override // org.xml.sax.Attributes
    public String getQName(int index) {
        if (index < 0 || index >= getLength()) {
            return null;
        }
        return getQName(getParserPointer(), getPointer(), index);
    }

    @Override // org.xml.sax.Attributes
    public String getType(int index) {
        if (index < 0 || index >= getLength()) {
            return null;
        }
        return CDATA;
    }

    @Override // org.xml.sax.Attributes
    public String getValue(int index) {
        if (index < 0 || index >= getLength()) {
            return null;
        }
        return getValueByIndex(getPointer(), index);
    }

    @Override // org.xml.sax.Attributes
    public int getIndex(String uri, String localName) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        if (localName == null) {
            throw new NullPointerException("localName == null");
        }
        long pointer = getPointer();
        if (pointer == 0) {
            return -1;
        }
        return getIndex(pointer, uri, localName);
    }

    @Override // org.xml.sax.Attributes
    public int getIndex(String qName) {
        if (qName == null) {
            throw new NullPointerException("qName == null");
        }
        long pointer = getPointer();
        if (pointer == 0) {
            return -1;
        }
        return getIndexForQName(pointer, qName);
    }

    @Override // org.xml.sax.Attributes
    public String getType(String uri, String localName) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        if (localName == null) {
            throw new NullPointerException("localName == null");
        }
        if (getIndex(uri, localName) == -1) {
            return null;
        }
        return CDATA;
    }

    @Override // org.xml.sax.Attributes
    public String getType(String qName) {
        if (getIndex(qName) == -1) {
            return null;
        }
        return CDATA;
    }

    @Override // org.xml.sax.Attributes
    public String getValue(String uri, String localName) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }
        if (localName == null) {
            throw new NullPointerException("localName == null");
        }
        long pointer = getPointer();
        if (pointer == 0) {
            return null;
        }
        return getValue(pointer, uri, localName);
    }

    @Override // org.xml.sax.Attributes
    public String getValue(String qName) {
        if (qName == null) {
            throw new NullPointerException("qName == null");
        }
        long pointer = getPointer();
        if (pointer == 0) {
            return null;
        }
        return getValueForQName(pointer, qName);
    }
}