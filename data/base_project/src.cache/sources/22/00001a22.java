package com.android.internal.os;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.LocalServerSocket;
import android.opengl.EGL14;
import android.os.Debug;
import android.os.Process;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.EventLog;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.os.ZygoteConnection;
import dalvik.system.VMRuntime;
import dalvik.system.Zygote;
import gov.nist.core.Separators;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.io.OsConstants;

/* loaded from: ZygoteInit.class */
public class ZygoteInit {
    private static final String TAG = "Zygote";
    private static final String PROPERTY_DISABLE_OPENGL_PRELOADING = "ro.zygote.disable_gl_preload";
    private static final String ANDROID_SOCKET_ENV = "ANDROID_SOCKET_zygote";
    private static final int LOG_BOOT_PROGRESS_PRELOAD_START = 3020;
    private static final int LOG_BOOT_PROGRESS_PRELOAD_END = 3030;
    private static final int PRELOAD_GC_THRESHOLD = 50000;
    public static final String USAGE_STRING = " <\"start-system-server\"|\"\" for startSystemServer>";
    private static LocalServerSocket sServerSocket;
    private static Resources mResources;
    static final int GC_LOOP_COUNT = 10;
    private static final String PRELOADED_CLASSES = "preloaded-classes";
    private static final boolean PRELOAD_RESOURCES = true;
    private static final int UNPRIVILEGED_UID = 9999;
    private static final int UNPRIVILEGED_GID = 9999;
    private static final int ROOT_UID = 0;
    private static final int ROOT_GID = 0;

    static native int setreuid(int i, int i2);

    static native int setregid(int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native int setpgid(int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native int getpgid(int i) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void reopenStdio(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void setCloseOnExec(FileDescriptor fileDescriptor, boolean z) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native long capgetPermitted(int i) throws IOException;

    static native int selectReadable(FileDescriptor[] fileDescriptorArr) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native FileDescriptor createFileDescriptor(int i) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void invokeStaticMain(ClassLoader loader, String className, String[] argv) throws MethodAndArgsCaller {
        try {
            Class<?> cl = loader.loadClass(className);
            try {
                Method m = cl.getMethod("main", String[].class);
                int modifiers = m.getModifiers();
                if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                    throw new RuntimeException("Main method is not public and static on " + className);
                }
                throw new MethodAndArgsCaller(m, argv);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("Missing static main on " + className, ex);
            } catch (SecurityException ex2) {
                throw new RuntimeException("Problem getting static main on " + className, ex2);
            }
        } catch (ClassNotFoundException ex3) {
            throw new RuntimeException("Missing class when invoking static main " + className, ex3);
        }
    }

    private static void registerZygoteSocket() {
        if (sServerSocket == null) {
            try {
                String env = System.getenv(ANDROID_SOCKET_ENV);
                int fileDesc = Integer.parseInt(env);
                try {
                    sServerSocket = new LocalServerSocket(createFileDescriptor(fileDesc));
                } catch (IOException ex) {
                    throw new RuntimeException("Error binding to local socket '" + fileDesc + Separators.QUOTE, ex);
                }
            } catch (RuntimeException ex2) {
                throw new RuntimeException("ANDROID_SOCKET_zygote unset or invalid", ex2);
            }
        }
    }

    private static ZygoteConnection acceptCommandPeer() {
        try {
            return new ZygoteConnection(sServerSocket.accept());
        } catch (IOException ex) {
            throw new RuntimeException("IOException during accept()", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void closeServerSocket() {
        try {
            if (sServerSocket != null) {
                sServerSocket.close();
            }
        } catch (IOException ex) {
            Log.e(TAG, "Zygote:  error closing sockets", ex);
        }
        sServerSocket = null;
    }

    private static void setEffectiveUser(int uid) {
        int errno = setreuid(0, uid);
        if (errno != 0) {
            Log.e(TAG, "setreuid() failed. errno: " + errno);
        }
    }

    private static void setEffectiveGroup(int gid) {
        int errno = setregid(0, gid);
        if (errno != 0) {
            Log.e(TAG, "setregid() failed. errno: " + errno);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void preload() {
        preloadClasses();
        preloadResources();
        preloadOpenGL();
    }

    private static void preloadOpenGL() {
        if (!SystemProperties.getBoolean(PROPERTY_DISABLE_OPENGL_PRELOADING, false)) {
            EGL14.eglGetDisplay(0);
        }
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.AutoCloseable, java.io.InputStream] */
    private static void preloadClasses() {
        VMRuntime runtime = VMRuntime.getRuntime();
        ?? resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream(PRELOADED_CLASSES);
        if (resourceAsStream == 0) {
            Log.e(TAG, "Couldn't find preloaded-classes.");
            return;
        }
        Log.i(TAG, "Preloading classes...");
        long startTime = SystemClock.uptimeMillis();
        setEffectiveGroup(9999);
        setEffectiveUser(9999);
        float defaultUtilization = runtime.getTargetHeapUtilization();
        runtime.setTargetHeapUtilization(0.8f);
        System.gc();
        runtime.runFinalizationSync();
        Debug.startAllocCounting();
        try {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream), 256);
                int count = 0;
                while (true) {
                    String line = br.readLine();
                    if (line != null) {
                        String line2 = line.trim();
                        if (!line2.startsWith(Separators.POUND) && !line2.equals("")) {
                            try {
                                Class.forName(line2);
                                if (Debug.getGlobalAllocSize() > 50000) {
                                    System.gc();
                                    runtime.runFinalizationSync();
                                    Debug.resetGlobalAllocSize();
                                }
                                count++;
                            } catch (ClassNotFoundException e) {
                                Log.w(TAG, "Class not found for preloading: " + line2);
                            } catch (Throwable t) {
                                Log.e(TAG, "Error preloading " + line2 + Separators.DOT, t);
                                if (t instanceof Error) {
                                    throw ((Error) t);
                                }
                                if (t instanceof RuntimeException) {
                                    throw ((RuntimeException) t);
                                }
                                throw new RuntimeException(t);
                            }
                        }
                    } else {
                        Log.i(TAG, "...preloaded " + count + " classes in " + (SystemClock.uptimeMillis() - startTime) + "ms.");
                        IoUtils.closeQuietly((AutoCloseable) resourceAsStream);
                        runtime.setTargetHeapUtilization(defaultUtilization);
                        runtime.preloadDexCaches();
                        Debug.stopAllocCounting();
                        setEffectiveUser(0);
                        setEffectiveGroup(0);
                        return;
                    }
                }
            } catch (IOException e2) {
                Log.e(TAG, "Error reading preloaded-classes.", e2);
                IoUtils.closeQuietly((AutoCloseable) resourceAsStream);
                runtime.setTargetHeapUtilization(defaultUtilization);
                runtime.preloadDexCaches();
                Debug.stopAllocCounting();
                setEffectiveUser(0);
                setEffectiveGroup(0);
            }
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) resourceAsStream);
            runtime.setTargetHeapUtilization(defaultUtilization);
            runtime.preloadDexCaches();
            Debug.stopAllocCounting();
            setEffectiveUser(0);
            setEffectiveGroup(0);
            throw th;
        }
    }

    private static void preloadResources() {
        VMRuntime runtime = VMRuntime.getRuntime();
        Debug.startAllocCounting();
        try {
            System.gc();
            runtime.runFinalizationSync();
            mResources = Resources.getSystem();
            mResources.startPreloading();
            Log.i(TAG, "Preloading resources...");
            long startTime = SystemClock.uptimeMillis();
            TypedArray ar = mResources.obtainTypedArray(R.array.preloaded_drawables);
            int N = preloadDrawables(runtime, ar);
            ar.recycle();
            Log.i(TAG, "...preloaded " + N + " resources in " + (SystemClock.uptimeMillis() - startTime) + "ms.");
            long startTime2 = SystemClock.uptimeMillis();
            TypedArray ar2 = mResources.obtainTypedArray(R.array.preloaded_color_state_lists);
            int N2 = preloadColorStateLists(runtime, ar2);
            ar2.recycle();
            Log.i(TAG, "...preloaded " + N2 + " resources in " + (SystemClock.uptimeMillis() - startTime2) + "ms.");
            mResources.finishPreloading();
        } catch (RuntimeException e) {
            Log.w(TAG, "Failure preloading resources", e);
        } finally {
            Debug.stopAllocCounting();
        }
    }

    private static int preloadColorStateLists(VMRuntime runtime, TypedArray ar) {
        int N = ar.length();
        for (int i = 0; i < N; i++) {
            if (Debug.getGlobalAllocSize() > 50000) {
                System.gc();
                runtime.runFinalizationSync();
                Debug.resetGlobalAllocSize();
            }
            int id = ar.getResourceId(i, 0);
            if (id != 0 && mResources.getColorStateList(id) == null) {
                throw new IllegalArgumentException("Unable to find preloaded color resource #0x" + Integer.toHexString(id) + " (" + ar.getString(i) + Separators.RPAREN);
            }
        }
        return N;
    }

    private static int preloadDrawables(VMRuntime runtime, TypedArray ar) {
        int N = ar.length();
        for (int i = 0; i < N; i++) {
            if (Debug.getGlobalAllocSize() > 50000) {
                System.gc();
                runtime.runFinalizationSync();
                Debug.resetGlobalAllocSize();
            }
            int id = ar.getResourceId(i, 0);
            if (id != 0 && mResources.getDrawable(id) == null) {
                throw new IllegalArgumentException("Unable to find preloaded drawable resource #0x" + Integer.toHexString(id) + " (" + ar.getString(i) + Separators.RPAREN);
            }
        }
        return N;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void gc() {
        VMRuntime runtime = VMRuntime.getRuntime();
        System.gc();
        runtime.runFinalizationSync();
        System.gc();
        runtime.runFinalizationSync();
        System.gc();
        runtime.runFinalizationSync();
    }

    private static void handleSystemServerProcess(ZygoteConnection.Arguments parsedArgs) throws MethodAndArgsCaller {
        closeServerSocket();
        Libcore.os.umask(OsConstants.S_IRWXG | OsConstants.S_IRWXO);
        if (parsedArgs.niceName != null) {
            Process.setArgV0(parsedArgs.niceName);
        }
        if (parsedArgs.invokeWith != null) {
            WrapperInit.execApplication(parsedArgs.invokeWith, parsedArgs.niceName, parsedArgs.targetSdkVersion, null, parsedArgs.remainingArgs);
        } else {
            RuntimeInit.zygoteInit(parsedArgs.targetSdkVersion, parsedArgs.remainingArgs);
        }
    }

    private static boolean startSystemServer() throws MethodAndArgsCaller, RuntimeException {
        long capabilities = posixCapabilitiesAsBits(OsConstants.CAP_KILL, OsConstants.CAP_NET_ADMIN, OsConstants.CAP_NET_BIND_SERVICE, OsConstants.CAP_NET_BROADCAST, OsConstants.CAP_NET_RAW, OsConstants.CAP_SYS_MODULE, OsConstants.CAP_SYS_NICE, OsConstants.CAP_SYS_RESOURCE, OsConstants.CAP_SYS_TIME, OsConstants.CAP_SYS_TTY_CONFIG);
        String[] args = {"--setuid=1000", "--setgid=1000", "--setgroups=1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1018,1032,3001,3002,3003,3006,3007", "--capabilities=" + capabilities + Separators.COMMA + capabilities, "--runtime-init", "--nice-name=system_server", "com.android.server.SystemServer"};
        try {
            ZygoteConnection.Arguments parsedArgs = new ZygoteConnection.Arguments(args);
            ZygoteConnection.applyDebuggerSystemProperty(parsedArgs);
            ZygoteConnection.applyInvokeWithSystemProperty(parsedArgs);
            int pid = Zygote.forkSystemServer(parsedArgs.uid, parsedArgs.gid, parsedArgs.gids, parsedArgs.debugFlags, (int[][]) null, parsedArgs.permittedCapabilities, parsedArgs.effectiveCapabilities);
            if (pid == 0) {
                handleSystemServerProcess(parsedArgs);
                return true;
            }
            return true;
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static long posixCapabilitiesAsBits(int... capabilities) {
        long result = 0;
        for (int capability : capabilities) {
            if (capability < 0 || capability > OsConstants.CAP_LAST_CAP) {
                throw new IllegalArgumentException(String.valueOf(capability));
            }
            result |= 1 << capability;
        }
        return result;
    }

    public static void main(String[] argv) {
        try {
            SamplingProfilerIntegration.start();
            registerZygoteSocket();
            EventLog.writeEvent((int) LOG_BOOT_PROGRESS_PRELOAD_START, SystemClock.uptimeMillis());
            preload();
            EventLog.writeEvent((int) LOG_BOOT_PROGRESS_PRELOAD_END, SystemClock.uptimeMillis());
            SamplingProfilerIntegration.writeZygoteSnapshot();
            gc();
            Trace.setTracingEnabled(false);
            if (argv.length != 2) {
                throw new RuntimeException(argv[0] + USAGE_STRING);
            }
            if (argv[1].equals("start-system-server")) {
                startSystemServer();
            } else if (!argv[1].equals("")) {
                throw new RuntimeException(argv[0] + USAGE_STRING);
            }
            Log.i(TAG, "Accepting command socket connections");
            runSelectLoop();
            closeServerSocket();
        } catch (MethodAndArgsCaller caller) {
            caller.run();
        } catch (RuntimeException ex) {
            Log.e(TAG, "Zygote died with exception", ex);
            closeServerSocket();
            throw ex;
        }
    }

    private static void runSelectLoop() throws MethodAndArgsCaller {
        ArrayList<FileDescriptor> fds = new ArrayList<>();
        ArrayList<ZygoteConnection> peers = new ArrayList<>();
        FileDescriptor[] fdArray = new FileDescriptor[4];
        fds.add(sServerSocket.getFileDescriptor());
        peers.add(null);
        int loopCount = 10;
        while (true) {
            if (loopCount <= 0) {
                gc();
                loopCount = 10;
            } else {
                loopCount--;
            }
            try {
                fdArray = (FileDescriptor[]) fds.toArray(fdArray);
                int index = selectReadable(fdArray);
                if (index < 0) {
                    break;
                } else if (index == 0) {
                    ZygoteConnection newPeer = acceptCommandPeer();
                    peers.add(newPeer);
                    fds.add(newPeer.getFileDesciptor());
                } else {
                    boolean done = peers.get(index).runOnce();
                    if (done) {
                        peers.remove(index);
                        fds.remove(index);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException("Error in select()", ex);
            }
        }
        throw new RuntimeException("Error in select()");
    }

    private ZygoteInit() {
    }

    /* loaded from: ZygoteInit$MethodAndArgsCaller.class */
    public static class MethodAndArgsCaller extends Exception implements Runnable {
        private final Method mMethod;
        private final String[] mArgs;

        public MethodAndArgsCaller(Method method, String[] args) {
            this.mMethod = method;
            this.mArgs = args;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.mMethod.invoke(null, this.mArgs);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex2) {
                Throwable cause = ex2.getCause();
                if (cause instanceof RuntimeException) {
                    throw ((RuntimeException) cause);
                }
                if (cause instanceof Error) {
                    throw ((Error) cause);
                }
                throw new RuntimeException(ex2);
            }
        }
    }
}