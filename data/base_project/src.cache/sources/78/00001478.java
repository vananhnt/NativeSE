package android.util;

import com.android.internal.os.RuntimeInit;
import com.android.internal.util.FastPrintWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.UnknownHostException;

/* loaded from: Log.class */
public final class Log {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    private static TerribleFailureHandler sWtfHandler = new TerribleFailureHandler() { // from class: android.util.Log.1
        @Override // android.util.Log.TerribleFailureHandler
        public void onTerribleFailure(String tag, TerribleFailure what) {
            RuntimeInit.wtf(tag, what);
        }
    };
    public static final int LOG_ID_MAIN = 0;
    public static final int LOG_ID_RADIO = 1;
    public static final int LOG_ID_EVENTS = 2;
    public static final int LOG_ID_SYSTEM = 3;

    /* loaded from: Log$TerribleFailureHandler.class */
    public interface TerribleFailureHandler {
        void onTerribleFailure(String str, TerribleFailure terribleFailure);
    }

    public static native boolean isLoggable(String str, int i);

    public static native int println_native(int i, int i2, String str, String str2);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Log$TerribleFailure.class */
    public static class TerribleFailure extends Exception {
        TerribleFailure(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    private Log() {
    }

    public static int v(String tag, String msg) {
        return println_native(0, 2, tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return println_native(0, 2, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int d(String tag, String msg) {
        return println_native(0, 3, tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        return println_native(0, 3, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int i(String tag, String msg) {
        return println_native(0, 4, tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return println_native(0, 4, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int w(String tag, String msg) {
        return println_native(0, 5, tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return println_native(0, 5, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int w(String tag, Throwable tr) {
        return println_native(0, 5, tag, getStackTraceString(tr));
    }

    public static int e(String tag, String msg) {
        return println_native(0, 6, tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return println_native(0, 6, tag, msg + '\n' + getStackTraceString(tr));
    }

    public static int wtf(String tag, String msg) {
        return wtf(0, tag, msg, null, false);
    }

    public static int wtfStack(String tag, String msg) {
        return wtf(0, tag, msg, null, true);
    }

    public static int wtf(String tag, Throwable tr) {
        return wtf(0, tag, tr.getMessage(), tr, false);
    }

    public static int wtf(String tag, String msg, Throwable tr) {
        return wtf(0, tag, msg, tr, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int wtf(int logId, String tag, String msg, Throwable tr, boolean localStack) {
        TerribleFailure what = new TerribleFailure(msg, tr);
        int bytes = println_native(logId, 7, tag, msg + '\n' + getStackTraceString(localStack ? what : tr));
        sWtfHandler.onTerribleFailure(tag, what);
        return bytes;
    }

    public static TerribleFailureHandler setWtfHandler(TerribleFailureHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler == null");
        }
        TerribleFailureHandler oldHandler = sWtfHandler;
        sWtfHandler = handler;
        return oldHandler;
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        Throwable th = tr;
        while (true) {
            Throwable t = th;
            if (t != null) {
                if (t instanceof UnknownHostException) {
                    return "";
                }
                th = t.getCause();
            } else {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new FastPrintWriter((Writer) sw, false, 256);
                tr.printStackTrace(pw);
                pw.flush();
                return sw.toString();
            }
        }
    }

    public static int println(int priority, String tag, String msg) {
        return println_native(0, priority, tag, msg);
    }
}