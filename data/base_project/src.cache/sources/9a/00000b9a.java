package android.os;

/* loaded from: Trace.class */
public final class Trace {
    private static final String TAG = "Trace";
    public static final long TRACE_TAG_NEVER = 0;
    public static final long TRACE_TAG_ALWAYS = 1;
    public static final long TRACE_TAG_GRAPHICS = 2;
    public static final long TRACE_TAG_INPUT = 4;
    public static final long TRACE_TAG_VIEW = 8;
    public static final long TRACE_TAG_WEBVIEW = 16;
    public static final long TRACE_TAG_WINDOW_MANAGER = 32;
    public static final long TRACE_TAG_ACTIVITY_MANAGER = 64;
    public static final long TRACE_TAG_SYNC_MANAGER = 128;
    public static final long TRACE_TAG_AUDIO = 256;
    public static final long TRACE_TAG_VIDEO = 512;
    public static final long TRACE_TAG_CAMERA = 1024;
    public static final long TRACE_TAG_HAL = 2048;
    public static final long TRACE_TAG_APP = 4096;
    public static final long TRACE_TAG_RESOURCES = 8192;
    public static final long TRACE_TAG_DALVIK = 16384;
    public static final long TRACE_TAG_RS = 32768;
    private static final long TRACE_TAG_NOT_READY = Long.MIN_VALUE;
    private static final int MAX_SECTION_NAME_LEN = 127;
    private static volatile long sEnabledTags = Long.MIN_VALUE;

    private static native long nativeGetEnabledTags();

    private static native void nativeTraceCounter(long j, String str, int i);

    private static native void nativeTraceBegin(long j, String str);

    private static native void nativeTraceEnd(long j);

    private static native void nativeAsyncTraceBegin(long j, String str, int i);

    private static native void nativeAsyncTraceEnd(long j, String str, int i);

    private static native void nativeSetAppTracingAllowed(boolean z);

    private static native void nativeSetTracingEnabled(boolean z);

    static /* synthetic */ long access$000() {
        return cacheEnabledTags();
    }

    static {
        SystemProperties.addChangeCallback(new Runnable() { // from class: android.os.Trace.1
            @Override // java.lang.Runnable
            public void run() {
                Trace.access$000();
            }
        });
    }

    private Trace() {
    }

    private static long cacheEnabledTags() {
        long tags = nativeGetEnabledTags();
        sEnabledTags = tags;
        return tags;
    }

    public static boolean isTagEnabled(long traceTag) {
        long tags = sEnabledTags;
        if (tags == Long.MIN_VALUE) {
            tags = cacheEnabledTags();
        }
        return (tags & traceTag) != 0;
    }

    public static void traceCounter(long traceTag, String counterName, int counterValue) {
        if (isTagEnabled(traceTag)) {
            nativeTraceCounter(traceTag, counterName, counterValue);
        }
    }

    public static void setAppTracingAllowed(boolean allowed) {
        nativeSetAppTracingAllowed(allowed);
        cacheEnabledTags();
    }

    public static void setTracingEnabled(boolean enabled) {
        nativeSetTracingEnabled(enabled);
        cacheEnabledTags();
    }

    public static void traceBegin(long traceTag, String methodName) {
        if (isTagEnabled(traceTag)) {
            nativeTraceBegin(traceTag, methodName);
        }
    }

    public static void traceEnd(long traceTag) {
        if (isTagEnabled(traceTag)) {
            nativeTraceEnd(traceTag);
        }
    }

    public static void asyncTraceBegin(long traceTag, String methodName, int cookie) {
        if (isTagEnabled(traceTag)) {
            nativeAsyncTraceBegin(traceTag, methodName, cookie);
        }
    }

    public static void asyncTraceEnd(long traceTag, String methodName, int cookie) {
        if (isTagEnabled(traceTag)) {
            nativeAsyncTraceEnd(traceTag, methodName, cookie);
        }
    }

    public static void beginSection(String sectionName) {
        if (isTagEnabled(4096L)) {
            if (sectionName.length() > 127) {
                throw new IllegalArgumentException("sectionName is too long");
            }
            nativeTraceBegin(4096L, sectionName);
        }
    }

    public static void endSection() {
        if (isTagEnabled(4096L)) {
            nativeTraceEnd(4096L);
        }
    }
}