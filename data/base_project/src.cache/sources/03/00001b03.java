package com.android.internal.util;

import android.os.Handler;
import java.io.PrintWriter;
import java.io.StringWriter;

/* loaded from: DumpUtils.class */
public final class DumpUtils {

    /* loaded from: DumpUtils$Dump.class */
    public interface Dump {
        void dump(PrintWriter printWriter);
    }

    private DumpUtils() {
    }

    public static void dumpAsync(Handler handler, final Dump dump, PrintWriter pw, long timeout) {
        final StringWriter sw = new StringWriter();
        if (handler.runWithScissors(new Runnable() { // from class: com.android.internal.util.DumpUtils.1
            @Override // java.lang.Runnable
            public void run() {
                PrintWriter lpw = new FastPrintWriter(StringWriter.this);
                dump.dump(lpw);
                lpw.close();
            }
        }, timeout)) {
            pw.print(sw.toString());
        } else {
            pw.println("... timed out");
        }
    }
}