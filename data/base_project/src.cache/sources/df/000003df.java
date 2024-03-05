package android.content.res;

import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: XmlResourceParser.class */
public interface XmlResourceParser extends XmlPullParser, AttributeSet, AutoCloseable {
    void close();
}