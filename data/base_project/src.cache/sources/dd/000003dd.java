package android.content.res;

import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: XmlBlock.class */
public final class XmlBlock {
    private static final boolean DEBUG = false;
    private final AssetManager mAssets;
    private final int mNative;
    final StringBlock mStrings;
    private boolean mOpen;
    private int mOpenCount;

    private static final native int nativeCreate(byte[] bArr, int i, int i2);

    private static final native int nativeGetStringBlock(int i);

    private static final native int nativeCreateParseState(int i);

    static final native int nativeNext(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetNamespace(int i);

    static final native int nativeGetName(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetText(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetLineNumber(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeCount(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeNamespace(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeName(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeResource(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeDataType(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeData(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeStringValue(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetIdAttribute(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetClassAttribute(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetStyleAttribute(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeIndex(int i, String str, String str2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void nativeDestroyParseState(int i);

    private static final native void nativeDestroy(int i);

    static /* synthetic */ int access$008(XmlBlock x0) {
        int i = x0.mOpenCount;
        x0.mOpenCount = i + 1;
        return i;
    }

    public XmlBlock(byte[] data) {
        this.mOpen = true;
        this.mOpenCount = 1;
        this.mAssets = null;
        this.mNative = nativeCreate(data, 0, data.length);
        this.mStrings = new StringBlock(nativeGetStringBlock(this.mNative), false);
    }

    public XmlBlock(byte[] data, int offset, int size) {
        this.mOpen = true;
        this.mOpenCount = 1;
        this.mAssets = null;
        this.mNative = nativeCreate(data, offset, size);
        this.mStrings = new StringBlock(nativeGetStringBlock(this.mNative), false);
    }

    public void close() {
        synchronized (this) {
            if (this.mOpen) {
                this.mOpen = false;
                decOpenCountLocked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void decOpenCountLocked() {
        this.mOpenCount--;
        if (this.mOpenCount == 0) {
            nativeDestroy(this.mNative);
            if (this.mAssets != null) {
                this.mAssets.xmlBlockGone(hashCode());
            }
        }
    }

    public XmlResourceParser newParser() {
        synchronized (this) {
            if (this.mNative != 0) {
                return new Parser(nativeCreateParseState(this.mNative), this);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: XmlBlock$Parser.class */
    public final class Parser implements XmlResourceParser {
        int mParseState;
        private final XmlBlock mBlock;
        private boolean mStarted = false;
        private boolean mDecNextDepth = false;
        private int mDepth = 0;
        private int mEventType = 0;

        Parser(int parseState, XmlBlock block) {
            this.mParseState = parseState;
            this.mBlock = block;
            XmlBlock.access$008(block);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setFeature(String name, boolean state) throws XmlPullParserException {
            if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name) && state) {
                return;
            }
            if ("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes".equals(name) && state) {
                return;
            }
            throw new XmlPullParserException("Unsupported feature: " + name);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean getFeature(String name) {
            if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name) || "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes".equals(name)) {
                return true;
            }
            return false;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setProperty(String name, Object value) throws XmlPullParserException {
            throw new XmlPullParserException("setProperty() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public Object getProperty(String name) {
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setInput(Reader in) throws XmlPullParserException {
            throw new XmlPullParserException("setInput() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
            throw new XmlPullParserException("setInput() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
            throw new XmlPullParserException("defineEntityReplacementText() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespacePrefix(int pos) throws XmlPullParserException {
            throw new XmlPullParserException("getNamespacePrefix() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getInputEncoding() {
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespace(String prefix) {
            throw new RuntimeException("getNamespace() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getNamespaceCount(int depth) throws XmlPullParserException {
            throw new XmlPullParserException("getNamespaceCount() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getPositionDescription() {
            return "Binary XML file line #" + getLineNumber();
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespaceUri(int pos) throws XmlPullParserException {
            throw new XmlPullParserException("getNamespaceUri() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getColumnNumber() {
            return -1;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getDepth() {
            return this.mDepth;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getText() {
            int id = XmlBlock.nativeGetText(this.mParseState);
            if (id >= 0) {
                return XmlBlock.this.mStrings.get(id).toString();
            }
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getLineNumber() {
            return XmlBlock.nativeGetLineNumber(this.mParseState);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getEventType() throws XmlPullParserException {
            return this.mEventType;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean isWhitespace() throws XmlPullParserException {
            return false;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getPrefix() {
            throw new RuntimeException("getPrefix not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public char[] getTextCharacters(int[] holderForStartAndLength) {
            String txt = getText();
            char[] chars = null;
            if (txt != null) {
                holderForStartAndLength[0] = 0;
                holderForStartAndLength[1] = txt.length();
                chars = new char[txt.length()];
                txt.getChars(0, txt.length(), chars, 0);
            }
            return chars;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespace() {
            int id = XmlBlock.nativeGetNamespace(this.mParseState);
            return id >= 0 ? XmlBlock.this.mStrings.get(id).toString() : "";
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getName() {
            int id = XmlBlock.nativeGetName(this.mParseState);
            if (id >= 0) {
                return XmlBlock.this.mStrings.get(id).toString();
            }
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getAttributeNamespace(int index) {
            int id = XmlBlock.nativeGetAttributeNamespace(this.mParseState, index);
            if (id >= 0) {
                return XmlBlock.this.mStrings.get(id).toString();
            }
            if (id == -1) {
                return "";
            }
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeName(int index) {
            int id = XmlBlock.nativeGetAttributeName(this.mParseState, index);
            if (id >= 0) {
                return XmlBlock.this.mStrings.get(id).toString();
            }
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getAttributePrefix(int index) {
            throw new RuntimeException("getAttributePrefix not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean isEmptyElementTag() throws XmlPullParserException {
            return false;
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public int getAttributeCount() {
            if (this.mEventType == 2) {
                return XmlBlock.nativeGetAttributeCount(this.mParseState);
            }
            return -1;
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeValue(int index) {
            int id = XmlBlock.nativeGetAttributeStringValue(this.mParseState, index);
            if (id >= 0) {
                return XmlBlock.this.mStrings.get(id).toString();
            }
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, index);
            if (t != 0) {
                int v = XmlBlock.nativeGetAttributeData(this.mParseState, index);
                return TypedValue.coerceToString(t, v);
            }
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getAttributeType(int index) {
            return "CDATA";
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean isAttributeDefault(int index) {
            return false;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int nextToken() throws XmlPullParserException, IOException {
            return next();
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeValue(String namespace, String name) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, name);
            if (idx >= 0) {
                return getAttributeValue(idx);
            }
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int next() throws XmlPullParserException, IOException {
            if (!this.mStarted) {
                this.mStarted = true;
                return 0;
            } else if (this.mParseState == 0) {
                return 1;
            } else {
                int ev = XmlBlock.nativeNext(this.mParseState);
                if (this.mDecNextDepth) {
                    this.mDepth--;
                    this.mDecNextDepth = false;
                }
                switch (ev) {
                    case 2:
                        this.mDepth++;
                        break;
                    case 3:
                        this.mDecNextDepth = true;
                        break;
                }
                this.mEventType = ev;
                if (ev == 1) {
                    close();
                }
                return ev;
            }
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
            if (type != getEventType() || ((namespace != null && !namespace.equals(getNamespace())) || (name != null && !name.equals(getName())))) {
                throw new XmlPullParserException("expected " + TYPES[type] + getPositionDescription());
            }
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String nextText() throws XmlPullParserException, IOException {
            if (getEventType() != 2) {
                throw new XmlPullParserException(getPositionDescription() + ": parser must be on START_TAG to read next text", this, null);
            }
            int eventType = next();
            if (eventType == 4) {
                String result = getText();
                if (next() != 3) {
                    throw new XmlPullParserException(getPositionDescription() + ": event TEXT it must be immediately followed by END_TAG", this, null);
                }
                return result;
            } else if (eventType == 3) {
                return "";
            } else {
                throw new XmlPullParserException(getPositionDescription() + ": parser must be on START_TAG or TEXT to read text", this, null);
            }
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int nextTag() throws XmlPullParserException, IOException {
            int eventType = next();
            if (eventType == 4 && isWhitespace()) {
                eventType = next();
            }
            if (eventType != 2 && eventType != 3) {
                throw new XmlPullParserException(getPositionDescription() + ": expected start or end tag", this, null);
            }
            return eventType;
        }

        @Override // android.util.AttributeSet
        public int getAttributeNameResource(int index) {
            return XmlBlock.nativeGetAttributeResource(this.mParseState, index);
        }

        @Override // android.util.AttributeSet
        public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeListValue(idx, options, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeBooleanValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeResourceValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeIntValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeUnsignedIntValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeFloatValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeListValue(int idx, String[] options, int defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            int v = XmlBlock.nativeGetAttributeData(this.mParseState, idx);
            if (t == 3) {
                return XmlUtils.convertValueToList(XmlBlock.this.mStrings.get(v), options, defaultValue);
            }
            return v;
        }

        @Override // android.util.AttributeSet
        public boolean getAttributeBooleanValue(int idx, boolean defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t < 16 || t > 31) {
                return defaultValue;
            }
            return XmlBlock.nativeGetAttributeData(this.mParseState, idx) != 0;
        }

        @Override // android.util.AttributeSet
        public int getAttributeResourceValue(int idx, int defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t == 1) {
                return XmlBlock.nativeGetAttributeData(this.mParseState, idx);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeIntValue(int idx, int defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t >= 16 && t <= 31) {
                return XmlBlock.nativeGetAttributeData(this.mParseState, idx);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeUnsignedIntValue(int idx, int defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t >= 16 && t <= 31) {
                return XmlBlock.nativeGetAttributeData(this.mParseState, idx);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public float getAttributeFloatValue(int idx, float defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t == 4) {
                return Float.intBitsToFloat(XmlBlock.nativeGetAttributeData(this.mParseState, idx));
            }
            throw new RuntimeException("not a float!");
        }

        @Override // android.util.AttributeSet
        public String getIdAttribute() {
            int id = XmlBlock.nativeGetIdAttribute(this.mParseState);
            if (id >= 0) {
                return XmlBlock.this.mStrings.get(id).toString();
            }
            return null;
        }

        @Override // android.util.AttributeSet
        public String getClassAttribute() {
            int id = XmlBlock.nativeGetClassAttribute(this.mParseState);
            if (id >= 0) {
                return XmlBlock.this.mStrings.get(id).toString();
            }
            return null;
        }

        @Override // android.util.AttributeSet
        public int getIdAttributeResourceValue(int defaultValue) {
            return getAttributeResourceValue(null, "id", defaultValue);
        }

        @Override // android.util.AttributeSet
        public int getStyleAttribute() {
            return XmlBlock.nativeGetStyleAttribute(this.mParseState);
        }

        @Override // android.content.res.XmlResourceParser, java.lang.AutoCloseable
        public void close() {
            synchronized (this.mBlock) {
                if (this.mParseState != 0) {
                    XmlBlock.nativeDestroyParseState(this.mParseState);
                    this.mParseState = 0;
                    this.mBlock.decOpenCountLocked();
                }
            }
        }

        protected void finalize() throws Throwable {
            close();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final CharSequence getPooledString(int id) {
            return XmlBlock.this.mStrings.get(id);
        }
    }

    protected void finalize() throws Throwable {
        close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XmlBlock(AssetManager assets, int xmlBlock) {
        this.mOpen = true;
        this.mOpenCount = 1;
        this.mAssets = assets;
        this.mNative = xmlBlock;
        this.mStrings = new StringBlock(nativeGetStringBlock(xmlBlock), false);
    }
}