package com.android.internal.util;

import android.util.Xml;
import gov.nist.core.Separators;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: XmlUtils.class */
public class XmlUtils {
    public static void skipCurrentTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
        }
    }

    public static final int convertValueToList(CharSequence value, String[] options, int defaultValue) {
        if (null != value) {
            for (int i = 0; i < options.length; i++) {
                if (value.equals(options[i])) {
                    return i;
                }
            }
        }
        return defaultValue;
    }

    public static final boolean convertValueToBoolean(CharSequence value, boolean defaultValue) {
        boolean result = false;
        if (null == value) {
            return defaultValue;
        }
        if (value.equals("1") || value.equals("true") || value.equals("TRUE")) {
            result = true;
        }
        return result;
    }

    public static final int convertValueToInt(CharSequence charSeq, int defaultValue) {
        if (null == charSeq) {
            return defaultValue;
        }
        String nm = charSeq.toString();
        int sign = 1;
        int index = 0;
        int len = nm.length();
        int base = 10;
        if ('-' == nm.charAt(0)) {
            sign = -1;
            index = 0 + 1;
        }
        if ('0' == nm.charAt(index)) {
            if (index == len - 1) {
                return 0;
            }
            char c = nm.charAt(index + 1);
            if ('x' == c || 'X' == c) {
                index += 2;
                base = 16;
            } else {
                index++;
                base = 8;
            }
        } else if ('#' == nm.charAt(index)) {
            index++;
            base = 16;
        }
        return Integer.parseInt(nm.substring(index), base) * sign;
    }

    public static int convertValueToUnsignedInt(String value, int defaultValue) {
        if (null == value) {
            return defaultValue;
        }
        return parseUnsignedIntAttribute(value);
    }

    public static int parseUnsignedIntAttribute(CharSequence charSeq) {
        String value = charSeq.toString();
        int index = 0;
        int len = value.length();
        int base = 10;
        if ('0' != value.charAt(0)) {
            if ('#' == value.charAt(0)) {
                index = 0 + 1;
                base = 16;
            }
        } else if (0 == len - 1) {
            return 0;
        } else {
            char c = value.charAt(0 + 1);
            if ('x' == c || 'X' == c) {
                index = 0 + 2;
                base = 16;
            } else {
                index = 0 + 1;
                base = 8;
            }
        }
        return (int) Long.parseLong(value.substring(index), base);
    }

    public static final void writeMapXml(Map val, OutputStream out) throws XmlPullParserException, IOException {
        XmlSerializer serializer = new FastXmlSerializer();
        serializer.setOutput(out, "utf-8");
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        writeMapXml(val, null, serializer);
        serializer.endDocument();
    }

    public static final void writeListXml(List val, OutputStream out) throws XmlPullParserException, IOException {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(out, "utf-8");
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        writeListXml(val, null, serializer);
        serializer.endDocument();
    }

    public static final void writeMapXml(Map val, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        Set<Map.Entry> s = val.entrySet();
        out.startTag(null, "map");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        for (Map.Entry e : s) {
            writeValueXml(e.getValue(), (String) e.getKey(), out);
        }
        out.endTag(null, "map");
    }

    public static final void writeListXml(List val, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "list");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.size();
        for (int i = 0; i < N; i++) {
            writeValueXml(val.get(i), null, out);
        }
        out.endTag(null, "list");
    }

    public static final void writeSetXml(Set val, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "set");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        for (Object v : val) {
            writeValueXml(v, null, out);
        }
        out.endTag(null, "set");
    }

    public static final void writeByteArrayXml(byte[] val, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "byte-array");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.length;
        out.attribute(null, "num", Integer.toString(N));
        StringBuilder sb = new StringBuilder(val.length * 2);
        for (byte b : val) {
            int h = b >> 4;
            sb.append(h >= 10 ? (97 + h) - 10 : 48 + h);
            int h2 = b & 255;
            sb.append(h2 >= 10 ? (97 + h2) - 10 : 48 + h2);
        }
        out.text(sb.toString());
        out.endTag(null, "byte-array");
    }

    public static final void writeIntArrayXml(int[] val, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "int-array");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.length;
        out.attribute(null, "num", Integer.toString(N));
        for (int i : val) {
            out.startTag(null, "item");
            out.attribute(null, "value", Integer.toString(i));
            out.endTag(null, "item");
        }
        out.endTag(null, "int-array");
    }

    public static final void writeValueXml(Object v, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        String typeStr;
        if (v == null) {
            out.startTag(null, "null");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.endTag(null, "null");
        } else if (v instanceof String) {
            out.startTag(null, "string");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.text(v.toString());
            out.endTag(null, "string");
        } else {
            if (v instanceof Integer) {
                typeStr = "int";
            } else if (v instanceof Long) {
                typeStr = "long";
            } else if (v instanceof Float) {
                typeStr = "float";
            } else if (v instanceof Double) {
                typeStr = "double";
            } else if (v instanceof Boolean) {
                typeStr = "boolean";
            } else if (v instanceof byte[]) {
                writeByteArrayXml((byte[]) v, name, out);
                return;
            } else if (v instanceof int[]) {
                writeIntArrayXml((int[]) v, name, out);
                return;
            } else if (v instanceof Map) {
                writeMapXml((Map) v, name, out);
                return;
            } else if (v instanceof List) {
                writeListXml((List) v, name, out);
                return;
            } else if (v instanceof Set) {
                writeSetXml((Set) v, name, out);
                return;
            } else if (v instanceof CharSequence) {
                out.startTag(null, "string");
                if (name != null) {
                    out.attribute(null, "name", name);
                }
                out.text(v.toString());
                out.endTag(null, "string");
                return;
            } else {
                throw new RuntimeException("writeValueXml: unable to write value " + v);
            }
            out.startTag(null, typeStr);
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.attribute(null, "value", v.toString());
            out.endTag(null, typeStr);
        }
    }

    public static final HashMap readMapXml(InputStream in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        return (HashMap) readValueXml(parser, new String[1]);
    }

    public static final ArrayList readListXml(InputStream in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        return (ArrayList) readValueXml(parser, new String[1]);
    }

    public static final HashSet readSetXml(InputStream in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        return (HashSet) readValueXml(parser, new String[1]);
    }

    public static final HashMap readThisMapXml(XmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        HashMap map = new HashMap();
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                Object val = readThisValueXml(parser, name);
                if (name[0] != null) {
                    map.put(name[0], val);
                } else {
                    throw new XmlPullParserException("Map value without name attribute: " + parser.getName());
                }
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return map;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final ArrayList readThisListXml(XmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        ArrayList list = new ArrayList();
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                Object val = readThisValueXml(parser, name);
                list.add(val);
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return list;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final HashSet readThisSetXml(XmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        HashSet set = new HashSet();
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                Object val = readThisValueXml(parser, name);
                set.add(val);
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return set;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final int[] readThisIntArrayXml(XmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        try {
            int num = Integer.parseInt(parser.getAttributeValue(null, "num"));
            int[] array = new int[num];
            int i = 0;
            int eventType = parser.getEventType();
            do {
                if (eventType == 2) {
                    if (parser.getName().equals("item")) {
                        try {
                            array[i] = Integer.parseInt(parser.getAttributeValue(null, "value"));
                        } catch (NullPointerException e) {
                            throw new XmlPullParserException("Need value attribute in item");
                        } catch (NumberFormatException e2) {
                            throw new XmlPullParserException("Not a number in value attribute in item");
                        }
                    } else {
                        throw new XmlPullParserException("Expected item tag at: " + parser.getName());
                    }
                } else if (eventType == 3) {
                    if (parser.getName().equals(endTag)) {
                        return array;
                    }
                    if (parser.getName().equals("item")) {
                        i++;
                    } else {
                        throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
                    }
                }
                eventType = parser.next();
            } while (eventType != 1);
            throw new XmlPullParserException("Document ended before " + endTag + " end tag");
        } catch (NullPointerException e3) {
            throw new XmlPullParserException("Need num attribute in byte-array");
        } catch (NumberFormatException e4) {
            throw new XmlPullParserException("Not a number in num attribute in byte-array");
        }
    }

    public static final Object readValueXml(XmlPullParser parser, String[] name) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        while (eventType != 2) {
            if (eventType == 3) {
                throw new XmlPullParserException("Unexpected end tag at: " + parser.getName());
            }
            if (eventType == 4) {
                throw new XmlPullParserException("Unexpected text: " + parser.getText());
            }
            eventType = parser.next();
            if (eventType == 1) {
                throw new XmlPullParserException("Unexpected end of document");
            }
        }
        return readThisValueXml(parser, name);
    }

    private static final Object readThisValueXml(XmlPullParser parser, String[] name) throws XmlPullParserException, IOException {
        Object res;
        int eventType;
        String valueName = parser.getAttributeValue(null, "name");
        String tagName = parser.getName();
        if (tagName.equals("null")) {
            res = null;
        } else if (tagName.equals("string")) {
            String value = "";
            while (true) {
                int eventType2 = parser.next();
                if (eventType2 != 1) {
                    if (eventType2 == 3) {
                        if (parser.getName().equals("string")) {
                            name[0] = valueName;
                            return value;
                        }
                        throw new XmlPullParserException("Unexpected end tag in <string>: " + parser.getName());
                    } else if (eventType2 == 4) {
                        value = value + parser.getText();
                    } else if (eventType2 == 2) {
                        throw new XmlPullParserException("Unexpected start tag in <string>: " + parser.getName());
                    }
                } else {
                    throw new XmlPullParserException("Unexpected end of document in <string>");
                }
            }
        } else {
            Object readThisPrimitiveValueXml = readThisPrimitiveValueXml(parser, tagName);
            res = readThisPrimitiveValueXml;
            if (readThisPrimitiveValueXml == null) {
                if (tagName.equals("int-array")) {
                    parser.next();
                    Object res2 = readThisIntArrayXml(parser, "int-array", name);
                    name[0] = valueName;
                    return res2;
                } else if (tagName.equals("map")) {
                    parser.next();
                    Object res3 = readThisMapXml(parser, "map", name);
                    name[0] = valueName;
                    return res3;
                } else if (tagName.equals("list")) {
                    parser.next();
                    Object res4 = readThisListXml(parser, "list", name);
                    name[0] = valueName;
                    return res4;
                } else if (tagName.equals("set")) {
                    parser.next();
                    Object res5 = readThisSetXml(parser, "set", name);
                    name[0] = valueName;
                    return res5;
                } else {
                    throw new XmlPullParserException("Unknown tag: " + tagName);
                }
            }
        }
        do {
            eventType = parser.next();
            if (eventType != 1) {
                if (eventType == 3) {
                    if (parser.getName().equals(tagName)) {
                        name[0] = valueName;
                        return res;
                    }
                    throw new XmlPullParserException("Unexpected end tag in <" + tagName + ">: " + parser.getName());
                } else if (eventType == 4) {
                    throw new XmlPullParserException("Unexpected text in <" + tagName + ">: " + parser.getName());
                }
            } else {
                throw new XmlPullParserException("Unexpected end of document in <" + tagName + Separators.GREATER_THAN);
            }
        } while (eventType != 2);
        throw new XmlPullParserException("Unexpected start tag in <" + tagName + ">: " + parser.getName());
    }

    private static final Object readThisPrimitiveValueXml(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException {
        try {
            if (tagName.equals("int")) {
                return Integer.valueOf(Integer.parseInt(parser.getAttributeValue(null, "value")));
            }
            if (tagName.equals("long")) {
                return Long.valueOf(parser.getAttributeValue(null, "value"));
            }
            if (tagName.equals("float")) {
                return new Float(parser.getAttributeValue(null, "value"));
            }
            if (tagName.equals("double")) {
                return new Double(parser.getAttributeValue(null, "value"));
            }
            if (tagName.equals("boolean")) {
                return Boolean.valueOf(parser.getAttributeValue(null, "value"));
            }
            return null;
        } catch (NullPointerException e) {
            throw new XmlPullParserException("Need value attribute in <" + tagName + Separators.GREATER_THAN);
        } catch (NumberFormatException e2) {
            throw new XmlPullParserException("Not a number in value attribute in <" + tagName + Separators.GREATER_THAN);
        }
    }

    public static final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException {
        int type;
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        if (!parser.getName().equals(firstElementName)) {
            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() + ", expected " + firstElementName);
        }
    }

    public static final void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        do {
            type = parser.next();
            if (type == 2) {
                return;
            }
        } while (type != 1);
    }

    public static boolean nextElementWithin(XmlPullParser parser, int outerDepth) throws IOException, XmlPullParserException {
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return false;
            }
            if (type == 3 && parser.getDepth() == outerDepth) {
                return false;
            }
            if (type == 2 && parser.getDepth() == outerDepth + 1) {
                return true;
            }
        }
    }

    public static int readIntAttribute(XmlPullParser in, String name) throws IOException {
        String value = in.getAttributeValue(null, name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ProtocolException("problem parsing " + name + Separators.EQUALS + value + " as int");
        }
    }

    public static void writeIntAttribute(XmlSerializer out, String name, int value) throws IOException {
        out.attribute(null, name, Integer.toString(value));
    }

    public static long readLongAttribute(XmlPullParser in, String name, long defaultValue) {
        String value = in.getAttributeValue(null, name);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long readLongAttribute(XmlPullParser in, String name) throws IOException {
        String value = in.getAttributeValue(null, name);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ProtocolException("problem parsing " + name + Separators.EQUALS + value + " as long");
        }
    }

    public static void writeLongAttribute(XmlSerializer out, String name, long value) throws IOException {
        out.attribute(null, name, Long.toString(value));
    }

    public static boolean readBooleanAttribute(XmlPullParser in, String name) {
        String value = in.getAttributeValue(null, name);
        return Boolean.parseBoolean(value);
    }

    public static void writeBooleanAttribute(XmlSerializer out, String name, boolean value) throws IOException {
        out.attribute(null, name, Boolean.toString(value));
    }
}