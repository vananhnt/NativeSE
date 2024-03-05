package android.util;

/* loaded from: PrefixPrinter.class */
public class PrefixPrinter implements Printer {
    private final Printer mPrinter;
    private final String mPrefix;

    public static Printer create(Printer printer, String prefix) {
        if (prefix == null || prefix.equals("")) {
            return printer;
        }
        return new PrefixPrinter(printer, prefix);
    }

    private PrefixPrinter(Printer printer, String prefix) {
        this.mPrinter = printer;
        this.mPrefix = prefix;
    }

    @Override // android.util.Printer
    public void println(String str) {
        this.mPrinter.println(this.mPrefix + str);
    }
}