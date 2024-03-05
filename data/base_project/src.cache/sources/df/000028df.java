package libcore.io;

/* loaded from: DropBox.class */
public final class DropBox {
    private static volatile Reporter REPORTER = new DefaultReporter();

    /* loaded from: DropBox$Reporter.class */
    public interface Reporter {
        void addData(String str, byte[] bArr, int i);

        void addText(String str, String str2);
    }

    public static void setReporter(Reporter reporter) {
        if (reporter == null) {
            throw new NullPointerException("reporter == null");
        }
        REPORTER = reporter;
    }

    public static Reporter getReporter() {
        return REPORTER;
    }

    /* loaded from: DropBox$DefaultReporter.class */
    private static final class DefaultReporter implements Reporter {
        private DefaultReporter() {
        }

        @Override // libcore.io.DropBox.Reporter
        public void addData(String tag, byte[] data, int flags) {
            System.out.println(tag + ": " + Base64.encode(data));
        }

        @Override // libcore.io.DropBox.Reporter
        public void addText(String tag, String data) {
            System.out.println(tag + ": " + data);
        }
    }

    public static void addData(String tag, byte[] data, int flags) {
        getReporter().addData(tag, data, flags);
    }

    public static void addText(String tag, String data) {
        getReporter().addText(tag, data);
    }
}