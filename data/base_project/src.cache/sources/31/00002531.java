package java.text;

import java.text.AttributedCharacterIterator;
import java.text.Format;
import java.util.Locale;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: MessageFormat.class */
public class MessageFormat extends Format {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: MessageFormat$Field.class */
    public static class Field extends Format.Field {
        public static final Field ARGUMENT = null;

        protected Field(String fieldName) {
            super(null);
            throw new RuntimeException("Stub!");
        }
    }

    public MessageFormat(String template, Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public MessageFormat(String template) {
        throw new RuntimeException("Stub!");
    }

    public void applyPattern(String template) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.Format
    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.Format
    public AttributedCharacterIterator formatToCharacterIterator(Object object) {
        throw new RuntimeException("Stub!");
    }

    public final StringBuffer format(Object[] objects, StringBuffer buffer, FieldPosition field) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.Format
    public final StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
        throw new RuntimeException("Stub!");
    }

    public static String format(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public Format[] getFormats() {
        throw new RuntimeException("Stub!");
    }

    public Format[] getFormatsByArgumentIndex() {
        throw new RuntimeException("Stub!");
    }

    public void setFormatByArgumentIndex(int argIndex, Format format) {
        throw new RuntimeException("Stub!");
    }

    public void setFormatsByArgumentIndex(Format[] formats) {
        throw new RuntimeException("Stub!");
    }

    public Locale getLocale() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public Object[] parse(String string) throws ParseException {
        throw new RuntimeException("Stub!");
    }

    public Object[] parse(String string, ParsePosition position) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.Format
    public Object parseObject(String string, ParsePosition position) {
        throw new RuntimeException("Stub!");
    }

    public void setFormat(int offset, Format format) {
        throw new RuntimeException("Stub!");
    }

    public void setFormats(Format[] formats) {
        throw new RuntimeException("Stub!");
    }

    public void setLocale(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public String toPattern() {
        throw new RuntimeException("Stub!");
    }

    /* loaded from: MessageFormat$FieldContainer.class */
    private static class FieldContainer {
        int start;
        int end;
        AttributedCharacterIterator.Attribute attribute;
        Object value;

        public FieldContainer(int start, int end, AttributedCharacterIterator.Attribute attribute, Object value) {
            this.start = start;
            this.end = end;
            this.attribute = attribute;
            this.value = value;
        }
    }
}