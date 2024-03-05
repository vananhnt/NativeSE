package libcore.io;

import gov.nist.core.Separators;

/* loaded from: EventLogger.class */
public final class EventLogger {
    private static volatile Reporter REPORTER = new DefaultReporter();

    /* loaded from: EventLogger$Reporter.class */
    public interface Reporter {
        void report(int i, Object... objArr);
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

    /* loaded from: EventLogger$DefaultReporter.class */
    private static final class DefaultReporter implements Reporter {
        private DefaultReporter() {
        }

        @Override // libcore.io.EventLogger.Reporter
        public void report(int code, Object... list) {
            StringBuilder sb = new StringBuilder();
            sb.append(code);
            for (Object o : list) {
                sb.append(Separators.COMMA);
                sb.append(o.toString());
            }
            System.out.println(sb);
        }
    }

    public static void writeEvent(int code, Object... list) {
        getReporter().report(code, list);
    }
}