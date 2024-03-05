package android.net.http;

import android.os.SystemClock;
import android.util.Log;
import gov.nist.core.Separators;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: HttpLog.class */
public class HttpLog {
    private static final String LOGTAG = "http";
    private static final boolean DEBUG = false;
    static final boolean LOGV = false;

    HttpLog() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void v(String logMe) {
        Log.v("http", SystemClock.uptimeMillis() + Separators.SP + Thread.currentThread().getName() + Separators.SP + logMe);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void e(String logMe) {
        Log.e("http", logMe);
    }
}