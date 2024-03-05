package android.content.pm;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: XmlSerializerAndParser.class */
public interface XmlSerializerAndParser<T> {
    void writeAsXml(T t, XmlSerializer xmlSerializer) throws IOException;

    T createFromXml(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException;
}