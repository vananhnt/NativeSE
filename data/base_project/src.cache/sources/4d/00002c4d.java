package org.xmlpull.v1;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: XmlPullParserException.class */
public class XmlPullParserException extends Exception {
    protected Throwable detail;
    protected int row;
    protected int column;

    public XmlPullParserException(String s) {
        throw new RuntimeException("Stub!");
    }

    public XmlPullParserException(String msg, XmlPullParser parser, Throwable chain) {
        throw new RuntimeException("Stub!");
    }

    public Throwable getDetail() {
        throw new RuntimeException("Stub!");
    }

    public int getLineNumber() {
        throw new RuntimeException("Stub!");
    }

    public int getColumnNumber() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        throw new RuntimeException("Stub!");
    }
}