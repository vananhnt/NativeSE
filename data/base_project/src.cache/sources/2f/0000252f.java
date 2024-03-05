package java.text;

import java.io.Serializable;
import java.text.AttributedCharacterIterator;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Format.class */
public abstract class Format implements Serializable, Cloneable {
    public abstract StringBuffer format(Object obj, StringBuffer stringBuffer, FieldPosition fieldPosition);

    public abstract Object parseObject(String str, ParsePosition parsePosition);

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Format$Field.class */
    public static class Field extends AttributedCharacterIterator.Attribute {
        /* JADX INFO: Access modifiers changed from: protected */
        public Field(String fieldName) {
            super(null);
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Format() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public final String format(Object object) {
        throw new RuntimeException("Stub!");
    }

    public AttributedCharacterIterator formatToCharacterIterator(Object object) {
        throw new RuntimeException("Stub!");
    }

    public Object parseObject(String string) throws ParseException {
        throw new RuntimeException("Stub!");
    }
}