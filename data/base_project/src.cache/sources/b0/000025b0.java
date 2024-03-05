package java.util;

import gov.nist.core.Separators;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import libcore.icu.LocaleData;
import libcore.icu.NativeDecimalFormat;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: Formatter.class */
public final class Formatter implements Closeable, Flushable {

    /* JADX WARN: Classes with same name are omitted:
      
     */
    /* loaded from: Formatter$BigDecimalLayoutForm.class */
    public enum BigDecimalLayoutForm {
        DECIMAL_FLOAT,
        SCIENTIFIC
    }

    public Formatter() {
        throw new RuntimeException("Stub!");
    }

    public Formatter(Appendable a) {
        throw new RuntimeException("Stub!");
    }

    public Formatter(Locale l) {
        throw new RuntimeException("Stub!");
    }

    public Formatter(Appendable a, Locale l) {
        throw new RuntimeException("Stub!");
    }

    public Formatter(String fileName) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public Formatter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public Formatter(String fileName, String csn, Locale l) throws FileNotFoundException, UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public Formatter(File file) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public Formatter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public Formatter(File file, String csn, Locale l) throws FileNotFoundException, UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public Formatter(OutputStream os) {
        throw new RuntimeException("Stub!");
    }

    public Formatter(OutputStream os, String csn) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public Formatter(OutputStream os, String csn, Locale l) throws UnsupportedEncodingException {
        throw new RuntimeException("Stub!");
    }

    public Formatter(PrintStream ps) {
        throw new RuntimeException("Stub!");
    }

    public Locale locale() {
        throw new RuntimeException("Stub!");
    }

    public Appendable out() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Flushable
    public void flush() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.io.Closeable
    public void close() {
        throw new RuntimeException("Stub!");
    }

    public IOException ioException() {
        throw new RuntimeException("Stub!");
    }

    public Formatter format(String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    public Formatter format(Locale l, String format, Object... args) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Formatter$CachedDecimalFormat.class */
    public static class CachedDecimalFormat {
        public NativeDecimalFormat decimalFormat;
        public LocaleData currentLocaleData;
        public String currentPattern;

        public NativeDecimalFormat update(LocaleData localeData, String pattern) {
            if (this.decimalFormat == null) {
                this.currentPattern = pattern;
                this.currentLocaleData = localeData;
                this.decimalFormat = new NativeDecimalFormat(this.currentPattern, this.currentLocaleData);
            }
            if (!pattern.equals(this.currentPattern)) {
                this.decimalFormat.applyPattern(pattern);
                this.currentPattern = pattern;
            }
            if (localeData != this.currentLocaleData) {
                this.decimalFormat.setDecimalFormatSymbols(localeData);
                this.currentLocaleData = localeData;
            }
            return this.decimalFormat;
        }
    }

    /* renamed from: java.util.Formatter$1  reason: invalid class name */
    /* loaded from: Formatter$1.class */
    static class AnonymousClass1 extends ThreadLocal<CachedDecimalFormat> {
        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public CachedDecimalFormat initialValue() {
            return new CachedDecimalFormat();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Formatter$FormatToken.class */
    public static class FormatToken {
        static final int LAST_ARGUMENT_INDEX = -2;
        static final int UNSET = -1;
        static final int FLAGS_UNSET = 0;
        static final int DEFAULT_PRECISION = 6;
        static final int FLAG_ZERO = 16;
        private int argIndex;
        boolean flagComma;
        boolean flagMinus;
        boolean flagParenthesis;
        boolean flagPlus;
        boolean flagSharp;
        boolean flagSpace;
        boolean flagZero;
        private char conversionType;
        private char dateSuffix;
        private int precision;
        private int width;
        private StringBuilder strFlags;

        private FormatToken() {
            this.argIndex = -1;
            this.conversionType = (char) 65535;
            this.precision = -1;
            this.width = -1;
        }

        /* synthetic */ FormatToken(AnonymousClass1 x0) {
            this();
        }

        boolean isDefault() {
            return (this.flagComma || this.flagMinus || this.flagParenthesis || this.flagPlus || this.flagSharp || this.flagSpace || this.flagZero || this.width != -1 || this.precision != -1) ? false : true;
        }

        boolean isPrecisionSet() {
            return this.precision != -1;
        }

        int getArgIndex() {
            return this.argIndex;
        }

        void setArgIndex(int index) {
            this.argIndex = index;
        }

        int getWidth() {
            return this.width;
        }

        void setWidth(int width) {
            this.width = width;
        }

        int getPrecision() {
            return this.precision;
        }

        void setPrecision(int precise) {
            this.precision = precise;
        }

        String getStrFlags() {
            return this.strFlags != null ? this.strFlags.toString() : "";
        }

        boolean setFlag(int ch) {
            boolean dupe;
            switch (ch) {
                case 32:
                    dupe = this.flagSpace;
                    this.flagSpace = true;
                    break;
                case 33:
                case 34:
                case 36:
                case 37:
                case 38:
                case 39:
                case 41:
                case 42:
                case 46:
                case 47:
                default:
                    return false;
                case 35:
                    dupe = this.flagSharp;
                    this.flagSharp = true;
                    break;
                case 40:
                    dupe = this.flagParenthesis;
                    this.flagParenthesis = true;
                    break;
                case 43:
                    dupe = this.flagPlus;
                    this.flagPlus = true;
                    break;
                case 44:
                    dupe = this.flagComma;
                    this.flagComma = true;
                    break;
                case 45:
                    dupe = this.flagMinus;
                    this.flagMinus = true;
                    break;
                case 48:
                    dupe = this.flagZero;
                    this.flagZero = true;
                    break;
            }
            if (dupe) {
                throw new DuplicateFormatFlagsException(String.valueOf(ch));
            }
            if (this.strFlags == null) {
                this.strFlags = new StringBuilder(7);
            }
            this.strFlags.append((char) ch);
            return true;
        }

        char getConversionType() {
            return this.conversionType;
        }

        void setConversionType(char c) {
            this.conversionType = c;
        }

        char getDateSuffix() {
            return this.dateSuffix;
        }

        void setDateSuffix(char c) {
            this.dateSuffix = c;
        }

        boolean requireArgument() {
            return (this.conversionType == '%' || this.conversionType == 'n') ? false : true;
        }

        void checkFlags(Object arg) {
            boolean allowComma = false;
            boolean allowMinus = true;
            boolean allowParenthesis = false;
            boolean allowPlus = false;
            boolean allowSharp = false;
            boolean allowSpace = false;
            boolean allowZero = false;
            boolean allowPrecision = true;
            boolean allowWidth = true;
            boolean allowArgument = true;
            switch (this.conversionType) {
                case '%':
                    allowArgument = false;
                    allowPrecision = false;
                    break;
                case '&':
                case '\'':
                case '(':
                case ')':
                case '*':
                case '+':
                case ',':
                case '-':
                case '.':
                case '/':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case ':':
                case ';':
                case '<':
                case '=':
                case '>':
                case '?':
                case '@':
                case 'D':
                case 'F':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'U':
                case 'V':
                case 'W':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'p':
                case 'q':
                case 'r':
                case 'u':
                case 'v':
                case 'w':
                default:
                    throw unknownFormatConversionException();
                case 'A':
                case 'a':
                    allowZero = true;
                    allowSpace = true;
                    allowSharp = true;
                    allowPlus = true;
                    break;
                case 'B':
                case 'H':
                case 'b':
                case 'h':
                    break;
                case 'C':
                case 'T':
                case 'c':
                case 't':
                    allowPrecision = false;
                    break;
                case 'E':
                case 'e':
                    allowZero = true;
                    allowSpace = true;
                    allowSharp = true;
                    allowPlus = true;
                    allowParenthesis = true;
                    break;
                case 'G':
                case 'g':
                    allowZero = true;
                    allowSpace = true;
                    allowPlus = true;
                    allowParenthesis = true;
                    allowComma = true;
                    break;
                case 'S':
                case 's':
                    if (arg instanceof Formattable) {
                        allowSharp = true;
                        break;
                    }
                    break;
                case 'X':
                case 'o':
                case 'x':
                    allowZero = true;
                    allowSharp = true;
                    if (arg == null || (arg instanceof BigInteger)) {
                        allowSpace = true;
                        allowPlus = true;
                        allowParenthesis = true;
                    }
                    allowPrecision = false;
                    break;
                case 'd':
                    allowZero = true;
                    allowSpace = true;
                    allowPlus = true;
                    allowParenthesis = true;
                    allowComma = true;
                    allowPrecision = false;
                    break;
                case 'f':
                    allowZero = true;
                    allowSpace = true;
                    allowSharp = true;
                    allowPlus = true;
                    allowParenthesis = true;
                    allowComma = true;
                    break;
                case 'n':
                    allowMinus = false;
                    allowWidth = false;
                    allowPrecision = false;
                    allowArgument = false;
                    break;
            }
            String mismatch = null;
            if (!allowComma && this.flagComma) {
                mismatch = Separators.COMMA;
            } else if (!allowMinus && this.flagMinus) {
                mismatch = "-";
            } else if (!allowParenthesis && this.flagParenthesis) {
                mismatch = Separators.LPAREN;
            } else if (!allowPlus && this.flagPlus) {
                mismatch = "+";
            } else if (!allowSharp && this.flagSharp) {
                mismatch = Separators.POUND;
            } else if (!allowSpace && this.flagSpace) {
                mismatch = Separators.SP;
            } else if (!allowZero && this.flagZero) {
                mismatch = "0";
            }
            if (mismatch != null) {
                if (this.conversionType == 'n') {
                    throw new IllegalFormatFlagsException(mismatch);
                }
                throw new FormatFlagsConversionMismatchException(mismatch, this.conversionType);
            } else if ((this.flagMinus || this.flagZero) && this.width == -1) {
                throw new MissingFormatWidthException("-" + this.conversionType);
            } else {
                if (!allowArgument && this.argIndex != -1) {
                    throw new IllegalFormatFlagsException(Separators.PERCENT + this.conversionType + " doesn't take an argument");
                }
                if (!allowPrecision && this.precision != -1) {
                    throw new IllegalFormatPrecisionException(this.precision);
                }
                if (!allowWidth && this.width != -1) {
                    throw new IllegalFormatWidthException(this.width);
                }
                if (this.flagPlus && this.flagSpace) {
                    throw new IllegalFormatFlagsException("the '+' and ' ' flags are incompatible");
                }
                if (this.flagMinus && this.flagZero) {
                    throw new IllegalFormatFlagsException("the '-' and '0' flags are incompatible");
                }
            }
        }

        public UnknownFormatConversionException unknownFormatConversionException() {
            if (this.conversionType == 't' || this.conversionType == 'T') {
                throw new UnknownFormatConversionException(String.format("%c%c", Character.valueOf(this.conversionType), Character.valueOf(this.dateSuffix)));
            }
            throw new UnknownFormatConversionException(String.valueOf(this.conversionType));
        }
    }

    /* loaded from: Formatter$FormatSpecifierParser.class */
    private static class FormatSpecifierParser {
        private String format;
        private int length;
        private int startIndex;
        private int i;

        FormatSpecifierParser(String format) {
            this.format = format;
            this.length = format.length();
        }

        FormatToken parseFormatToken(int offset) {
            this.startIndex = offset;
            this.i = offset;
            return parseArgumentIndexAndFlags(new FormatToken(null));
        }

        String getFormatSpecifierText() {
            return this.format.substring(this.startIndex, this.i);
        }

        private int peek() {
            if (this.i < this.length) {
                return this.format.charAt(this.i);
            }
            return -1;
        }

        private char advance() {
            if (this.i >= this.length) {
                throw unknownFormatConversionException();
            }
            String str = this.format;
            int i = this.i;
            this.i = i + 1;
            return str.charAt(i);
        }

        private UnknownFormatConversionException unknownFormatConversionException() {
            throw new UnknownFormatConversionException(getFormatSpecifierText());
        }

        private FormatToken parseArgumentIndexAndFlags(FormatToken token) {
            int position = this.i;
            int ch = peek();
            if (Character.isDigit(ch)) {
                int number = nextInt();
                if (peek() == 36) {
                    advance();
                    if (number == -1) {
                        throw new MissingFormatArgumentException(getFormatSpecifierText());
                    }
                    token.setArgIndex(Math.max(0, number - 1));
                } else if (ch == 48) {
                    this.i = position;
                } else {
                    return parseWidth(token, number);
                }
            } else if (ch == 60) {
                token.setArgIndex(-2);
                advance();
            }
            while (token.setFlag(peek())) {
                advance();
            }
            int ch2 = peek();
            if (Character.isDigit(ch2)) {
                return parseWidth(token, nextInt());
            }
            if (ch2 == 46) {
                return parsePrecision(token);
            }
            return parseConversionType(token);
        }

        private FormatToken parseWidth(FormatToken token, int width) {
            token.setWidth(width);
            int ch = peek();
            if (ch == 46) {
                return parsePrecision(token);
            }
            return parseConversionType(token);
        }

        private FormatToken parsePrecision(FormatToken token) {
            advance();
            int ch = peek();
            if (Character.isDigit(ch)) {
                token.setPrecision(nextInt());
                return parseConversionType(token);
            }
            throw unknownFormatConversionException();
        }

        private FormatToken parseConversionType(FormatToken token) {
            char conversionType = advance();
            token.setConversionType(conversionType);
            if (conversionType == 't' || conversionType == 'T') {
                char dateSuffix = advance();
                token.setDateSuffix(dateSuffix);
            }
            return token;
        }

        private int nextInt() {
            int i;
            long value = 0;
            while (this.i < this.length && Character.isDigit(this.format.charAt(this.i))) {
                String str = this.format;
                this.i = this.i + 1;
                value = (10 * value) + (str.charAt(i) - '0');
                if (value > 2147483647L) {
                    return failNextInt();
                }
            }
            return (int) value;
        }

        private int failNextInt() {
            while (Character.isDigit(peek())) {
                advance();
            }
            return -1;
        }
    }
}