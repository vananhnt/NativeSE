package java.text;

import java.math.RoundingMode;
import java.util.Currency;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DecimalFormat.class */
public class DecimalFormat extends NumberFormat {
    public DecimalFormat() {
        throw new RuntimeException("Stub!");
    }

    public DecimalFormat(String pattern) {
        throw new RuntimeException("Stub!");
    }

    public DecimalFormat(String pattern, DecimalFormatSymbols value) {
        throw new RuntimeException("Stub!");
    }

    public void applyLocalizedPattern(String pattern) {
        throw new RuntimeException("Stub!");
    }

    public void applyPattern(String pattern) {
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

    @Override // java.text.Format
    public AttributedCharacterIterator formatToCharacterIterator(Object object) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public StringBuffer format(double value, StringBuffer buffer, FieldPosition position) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public StringBuffer format(long value, StringBuffer buffer, FieldPosition position) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat, java.text.Format
    public final StringBuffer format(Object number, StringBuffer buffer, FieldPosition position) {
        throw new RuntimeException("Stub!");
    }

    public DecimalFormatSymbols getDecimalFormatSymbols() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public Currency getCurrency() {
        throw new RuntimeException("Stub!");
    }

    public int getGroupingSize() {
        throw new RuntimeException("Stub!");
    }

    public int getMultiplier() {
        throw new RuntimeException("Stub!");
    }

    public String getNegativePrefix() {
        throw new RuntimeException("Stub!");
    }

    public String getNegativeSuffix() {
        throw new RuntimeException("Stub!");
    }

    public String getPositivePrefix() {
        throw new RuntimeException("Stub!");
    }

    public String getPositiveSuffix() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public boolean isDecimalSeparatorAlwaysShown() {
        throw new RuntimeException("Stub!");
    }

    public boolean isParseBigDecimal() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public void setParseIntegerOnly(boolean value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public boolean isParseIntegerOnly() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public Number parse(String string, ParsePosition position) {
        throw new RuntimeException("Stub!");
    }

    public void setDecimalFormatSymbols(DecimalFormatSymbols value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public void setCurrency(Currency currency) {
        throw new RuntimeException("Stub!");
    }

    public void setDecimalSeparatorAlwaysShown(boolean value) {
        throw new RuntimeException("Stub!");
    }

    public void setGroupingSize(int value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public void setGroupingUsed(boolean value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public boolean isGroupingUsed() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public void setMaximumFractionDigits(int value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public void setMaximumIntegerDigits(int value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public void setMinimumFractionDigits(int value) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public void setMinimumIntegerDigits(int value) {
        throw new RuntimeException("Stub!");
    }

    public void setMultiplier(int value) {
        throw new RuntimeException("Stub!");
    }

    public void setNegativePrefix(String value) {
        throw new RuntimeException("Stub!");
    }

    public void setNegativeSuffix(String value) {
        throw new RuntimeException("Stub!");
    }

    public void setPositivePrefix(String value) {
        throw new RuntimeException("Stub!");
    }

    public void setPositiveSuffix(String value) {
        throw new RuntimeException("Stub!");
    }

    public void setParseBigDecimal(boolean newValue) {
        throw new RuntimeException("Stub!");
    }

    public String toLocalizedPattern() {
        throw new RuntimeException("Stub!");
    }

    public String toPattern() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public RoundingMode getRoundingMode() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.text.NumberFormat
    public void setRoundingMode(RoundingMode roundingMode) {
        throw new RuntimeException("Stub!");
    }
}