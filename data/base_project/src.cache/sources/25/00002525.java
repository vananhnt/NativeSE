package java.text;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ChoiceFormat.class */
public class ChoiceFormat extends NumberFormat {
    public ChoiceFormat(double[] limits, String[] formats) {
        throw new RuntimeException("Stub!");
    }

    public ChoiceFormat(String template) {
        throw new RuntimeException("Stub!");
    }

    public void applyPattern(String template) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat, java.text.Format
    public Object clone() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
        throw new RuntimeException("Stub!");
    }

    public Object[] getFormats() {
        throw new RuntimeException("Stub!");
    }

    public double[] getLimits() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public static final double nextDouble(double value) {
        throw new RuntimeException("Stub!");
    }

    public static double nextDouble(double value, boolean increment) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public Number parse(String string, ParsePosition position) {
        throw new RuntimeException("Stub!");
    }

    public static final double previousDouble(double value) {
        throw new RuntimeException("Stub!");
    }

    public void setChoices(double[] limits, String[] formats) {
        throw new RuntimeException("Stub!");
    }

    public String toPattern() {
        throw new RuntimeException("Stub!");
    }
}