package android.util;

/* loaded from: AttributeSet.class */
public interface AttributeSet {
    int getAttributeCount();

    String getAttributeName(int i);

    String getAttributeValue(int i);

    String getAttributeValue(String str, String str2);

    String getPositionDescription();

    int getAttributeNameResource(int i);

    int getAttributeListValue(String str, String str2, String[] strArr, int i);

    boolean getAttributeBooleanValue(String str, String str2, boolean z);

    int getAttributeResourceValue(String str, String str2, int i);

    int getAttributeIntValue(String str, String str2, int i);

    int getAttributeUnsignedIntValue(String str, String str2, int i);

    float getAttributeFloatValue(String str, String str2, float f);

    int getAttributeListValue(int i, String[] strArr, int i2);

    boolean getAttributeBooleanValue(int i, boolean z);

    int getAttributeResourceValue(int i, int i2);

    int getAttributeIntValue(int i, int i2);

    int getAttributeUnsignedIntValue(int i, int i2);

    float getAttributeFloatValue(int i, float f);

    String getIdAttribute();

    String getClassAttribute();

    int getIdAttributeResourceValue(int i);

    int getStyleAttribute();
}