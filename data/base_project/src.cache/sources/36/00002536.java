package java.text;

import java.math.RoundingMode;
import java.text.Format;
import java.util.Currency;
import java.util.Locale;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NumberFormat.class */
public abstract class NumberFormat extends Format {
    public static final int INTEGER_FIELD = 0;
    public static final int FRACTION_FIELD = 1;

    public abstract StringBuffer format(double d, StringBuffer stringBuffer, FieldPosition fieldPosition);

    public abstract StringBuffer format(long j, StringBuffer stringBuffer, FieldPosition fieldPosition);

    public abstract Number parse(String str, ParsePosition parsePosition);

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: NumberFormat$Field.class */
    public static class Field extends Format.Field {
        public static final Field SIGN = null;
        public static final Field INTEGER = null;
        public static final Field FRACTION = null;
        public static final Field EXPONENT = null;
        public static final Field EXPONENT_SIGN = null;
        public static final Field EXPONENT_SYMBOL = null;
        public static final Field DECIMAL_SEPARATOR = null;
        public static final Field GROUPING_SEPARATOR = null;
        public static final Field PERCENT = null;
        public static final Field PERMILLE = null;
        public static final Field CURRENCY = null;

        protected Field(String fieldName) {
            super(null);
            throw new RuntimeException("Stub!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public NumberFormat() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.Format
    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    public final String format(double value) {
        throw new RuntimeException("Stub!");
    }

    public final String format(long value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.Format
    public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
        throw new RuntimeException("Stub!");
    }

    public static Locale[] getAvailableLocales() {
        throw new RuntimeException("Stub!");
    }

    public Currency getCurrency() {
        throw new RuntimeException("Stub!");
    }

    public static final NumberFormat getCurrencyInstance() {
        throw new RuntimeException("Stub!");
    }

    public static NumberFormat getCurrencyInstance(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public static final NumberFormat getIntegerInstance() {
        throw new RuntimeException("Stub!");
    }

    public static NumberFormat getIntegerInstance(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public static final NumberFormat getInstance() {
        throw new RuntimeException("Stub!");
    }

    public static NumberFormat getInstance(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public int getMaximumFractionDigits() {
        throw new RuntimeException("Stub!");
    }

    public int getMaximumIntegerDigits() {
        throw new RuntimeException("Stub!");
    }

    public int getMinimumFractionDigits() {
        throw new RuntimeException("Stub!");
    }

    public int getMinimumIntegerDigits() {
        throw new RuntimeException("Stub!");
    }

    public static final NumberFormat getNumberInstance() {
        throw new RuntimeException("Stub!");
    }

    public static NumberFormat getNumberInstance(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public static final NumberFormat getPercentInstance() {
        throw new RuntimeException("Stub!");
    }

    public static NumberFormat getPercentInstance(Locale locale) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public boolean isGroupingUsed() {
        throw new RuntimeException("Stub!");
    }

    public boolean isParseIntegerOnly() {
        throw new RuntimeException("Stub!");
    }

    public Number parse(String string) throws ParseException {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.Format
    public final Object parseObject(String string, ParsePosition position) {
        throw new RuntimeException("Stub!");
    }

    public void setCurrency(Currency currency) {
        throw new RuntimeException("Stub!");
    }

    public void setGroupingUsed(boolean value) {
        throw new RuntimeException("Stub!");
    }

    public void setMaximumFractionDigits(int value) {
        throw new RuntimeException("Stub!");
    }

    public void setMaximumIntegerDigits(int value) {
        throw new RuntimeException("Stub!");
    }

    public void setMinimumFractionDigits(int value) {
        throw new RuntimeException("Stub!");
    }

    public void setMinimumIntegerDigits(int value) {
        throw new RuntimeException("Stub!");
    }

    public void setParseIntegerOnly(boolean value) {
        throw new RuntimeException("Stub!");
    }

    public RoundingMode getRoundingMode() {
        throw new RuntimeException("Stub!");
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        throw new RuntimeException("Stub!");
    }
}