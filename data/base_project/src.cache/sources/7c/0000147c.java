package android.util;

/* loaded from: LogPrinter.class */
public class LogPrinter implements Printer {
    private final int mPriority;
    private final String mTag;
    private final int mBuffer;

    public LogPrinter(int priority, String tag) {
        this.mPriority = priority;
        this.mTag = tag;
        this.mBuffer = 0;
    }

    public LogPrinter(int priority, String tag, int buffer) {
        this.mPriority = priority;
        this.mTag = tag;
        this.mBuffer = buffer;
    }

    @Override // android.util.Printer
    public void println(String x) {
        Log.println_native(this.mBuffer, this.mPriority, this.mTag, x);
    }
}