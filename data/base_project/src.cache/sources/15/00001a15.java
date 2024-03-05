package com.android.internal.os;

import android.accounts.GrantCredentialsPermissionActivity;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.ApplicationErrorReport;
import android.ddm.DdmRegister;
import android.os.Build;
import android.os.Debug;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.internal.logging.AndroidConfig;
import com.android.internal.os.ZygoteInit;
import com.android.server.NetworkManagementSocketTagger;
import dalvik.system.VMRuntime;
import gov.nist.core.Separators;
import java.lang.Thread;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.TimeZone;
import java.util.logging.LogManager;
import org.apache.harmony.luni.internal.util.TimezoneGetter;

/* loaded from: RuntimeInit.class */
public class RuntimeInit {
    private static final String TAG = "AndroidRuntime";
    private static final boolean DEBUG = false;
    private static boolean initialized;
    private static IBinder mApplicationObject;
    private static volatile boolean mCrashing = false;

    private static final native void nativeZygoteInit();

    private static final native void nativeFinishInit();

    private static final native void nativeSetExitWithoutCleanup(boolean z);

    static {
        DdmRegister.registerHandlers();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: RuntimeInit$UncaughtHandler.class */
    public static class UncaughtHandler implements Thread.UncaughtExceptionHandler {
        private UncaughtHandler() {
        }

        @Override // java.lang.Thread.UncaughtExceptionHandler
        public void uncaughtException(Thread t, Throwable e) {
            try {
                if (RuntimeInit.mCrashing) {
                    return;
                }
                boolean unused = RuntimeInit.mCrashing = true;
                if (RuntimeInit.mApplicationObject == null) {
                    Slog.e(RuntimeInit.TAG, "*** FATAL EXCEPTION IN SYSTEM PROCESS: " + t.getName(), e);
                } else {
                    StringBuilder message = new StringBuilder();
                    message.append("FATAL EXCEPTION: ").append(t.getName()).append(Separators.RETURN);
                    String processName = ActivityThread.currentProcessName();
                    if (processName != null) {
                        message.append("Process: ").append(processName).append(", ");
                    }
                    message.append("PID: ").append(Process.myPid());
                    Slog.e(RuntimeInit.TAG, message.toString(), e);
                }
                ActivityManagerNative.getDefault().handleApplicationCrash(RuntimeInit.mApplicationObject, new ApplicationErrorReport.CrashInfo(e));
                Process.killProcess(Process.myPid());
                System.exit(10);
            } catch (Throwable t2) {
                try {
                    try {
                        Slog.e(RuntimeInit.TAG, "Error reporting crash", t2);
                    } catch (Throwable th) {
                    }
                    Process.killProcess(Process.myPid());
                    System.exit(10);
                } finally {
                    Process.killProcess(Process.myPid());
                    System.exit(10);
                }
            }
        }
    }

    private static final void commonInit() {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler());
        TimezoneGetter.setInstance(new TimezoneGetter() { // from class: com.android.internal.os.RuntimeInit.1
            @Override // org.apache.harmony.luni.internal.util.TimezoneGetter
            public String getId() {
                return SystemProperties.get("persist.sys.timezone");
            }
        });
        TimeZone.setDefault(null);
        LogManager.getLogManager().reset();
        new AndroidConfig();
        String userAgent = getDefaultUserAgent();
        System.setProperty("http.agent", userAgent);
        NetworkManagementSocketTagger.install();
        String trace = SystemProperties.get("ro.kernel.android.tracing");
        if (trace.equals("1")) {
            Slog.i(TAG, "NOTE: emulator trace profiling enabled");
            Debug.enableEmulatorTraceOutput();
        }
        initialized = true;
    }

    private static String getDefaultUserAgent() {
        StringBuilder result = new StringBuilder(64);
        result.append("Dalvik/");
        result.append(System.getProperty("java.vm.version"));
        result.append(" (Linux; U; Android ");
        String version = Build.VERSION.RELEASE;
        result.append(version.length() > 0 ? version : "1.0");
        if ("REL".equals(Build.VERSION.CODENAME)) {
            String model = Build.MODEL;
            if (model.length() > 0) {
                result.append("; ");
                result.append(model);
            }
        }
        String id = Build.ID;
        if (id.length() > 0) {
            result.append(" Build/");
            result.append(id);
        }
        result.append(Separators.RPAREN);
        return result.toString();
    }

    private static void invokeStaticMain(String className, String[] argv) throws ZygoteInit.MethodAndArgsCaller {
        try {
            Class<?> cl = Class.forName(className);
            try {
                Method m = cl.getMethod("main", String[].class);
                int modifiers = m.getModifiers();
                if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                    throw new RuntimeException("Main method is not public and static on " + className);
                }
                throw new ZygoteInit.MethodAndArgsCaller(m, argv);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("Missing static main on " + className, ex);
            } catch (SecurityException ex2) {
                throw new RuntimeException("Problem getting static main on " + className, ex2);
            }
        } catch (ClassNotFoundException ex3) {
            throw new RuntimeException("Missing class when invoking static main " + className, ex3);
        }
    }

    public static final void main(String[] argv) {
        if (argv.length == 2 && argv[1].equals(GrantCredentialsPermissionActivity.EXTRAS_PACKAGES)) {
            redirectLogStreams();
        }
        commonInit();
        nativeFinishInit();
    }

    public static final void zygoteInit(int targetSdkVersion, String[] argv) throws ZygoteInit.MethodAndArgsCaller {
        redirectLogStreams();
        commonInit();
        nativeZygoteInit();
        applicationInit(targetSdkVersion, argv);
    }

    public static void wrapperInit(int targetSdkVersion, String[] argv) throws ZygoteInit.MethodAndArgsCaller {
        applicationInit(targetSdkVersion, argv);
    }

    private static void applicationInit(int targetSdkVersion, String[] argv) throws ZygoteInit.MethodAndArgsCaller {
        nativeSetExitWithoutCleanup(true);
        VMRuntime.getRuntime().setTargetHeapUtilization(0.75f);
        VMRuntime.getRuntime().setTargetSdkVersion(targetSdkVersion);
        try {
            Arguments args = new Arguments(argv);
            invokeStaticMain(args.startClass, args.startArgs);
        } catch (IllegalArgumentException ex) {
            Slog.e(TAG, ex.getMessage());
        }
    }

    public static void redirectLogStreams() {
        System.out.close();
        System.setOut(new AndroidPrintStream(4, "System.out"));
        System.err.close();
        System.setErr(new AndroidPrintStream(5, "System.err"));
    }

    public static void wtf(String tag, Throwable t) {
        try {
            if (ActivityManagerNative.getDefault().handleApplicationWtf(mApplicationObject, tag, new ApplicationErrorReport.CrashInfo(t))) {
                Process.killProcess(Process.myPid());
                System.exit(10);
            }
        } catch (Throwable t2) {
            Slog.e(TAG, "Error reporting WTF", t2);
            Slog.e(TAG, "Original WTF:", t);
        }
    }

    public static final void setApplicationObject(IBinder app) {
        mApplicationObject = app;
    }

    public static final IBinder getApplicationObject() {
        return mApplicationObject;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: RuntimeInit$Arguments.class */
    public static class Arguments {
        String startClass;
        String[] startArgs;

        Arguments(String[] args) throws IllegalArgumentException {
            parseArgs(args);
        }

        private void parseArgs(String[] args) throws IllegalArgumentException {
            int curArg = 0;
            while (true) {
                if (curArg >= args.length) {
                    break;
                }
                String arg = args[curArg];
                if (arg.equals("--")) {
                    curArg++;
                    break;
                } else if (!arg.startsWith("--")) {
                    break;
                } else {
                    curArg++;
                }
            }
            if (curArg == args.length) {
                throw new IllegalArgumentException("Missing classname argument to RuntimeInit!");
            }
            int i = curArg;
            int curArg2 = curArg + 1;
            this.startClass = args[i];
            this.startArgs = new String[args.length - curArg2];
            System.arraycopy(args, curArg2, this.startArgs, 0, this.startArgs.length);
        }
    }
}