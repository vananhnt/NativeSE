package android.os;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import libcore.io.Libcore;

/* loaded from: Process.class */
public class Process {
    private static final String LOG_TAG = "Process";
    private static final String ZYGOTE_SOCKET = "zygote";
    public static final String ANDROID_SHARED_MEDIA = "com.android.process.media";
    public static final String GOOGLE_SHARED_APP_CONTENT = "com.google.process.content";
    public static final int SYSTEM_UID = 1000;
    public static final int PHONE_UID = 1001;
    public static final int SHELL_UID = 2000;
    public static final int LOG_UID = 1007;
    public static final int WIFI_UID = 1010;
    public static final int MEDIA_UID = 1013;
    public static final int DRM_UID = 1019;
    public static final int VPN_UID = 1016;
    public static final int NFC_UID = 1027;
    public static final int BLUETOOTH_UID = 1002;
    public static final int MEDIA_RW_GID = 1023;
    public static final int PACKAGE_INFO_GID = 1032;
    public static final int FIRST_APPLICATION_UID = 10000;
    public static final int LAST_APPLICATION_UID = 19999;
    public static final int FIRST_ISOLATED_UID = 99000;
    public static final int LAST_ISOLATED_UID = 99999;
    public static final int FIRST_SHARED_APPLICATION_GID = 50000;
    public static final int LAST_SHARED_APPLICATION_GID = 59999;
    public static final int THREAD_PRIORITY_DEFAULT = 0;
    public static final int THREAD_PRIORITY_LOWEST = 19;
    public static final int THREAD_PRIORITY_BACKGROUND = 10;
    public static final int THREAD_PRIORITY_FOREGROUND = -2;
    public static final int THREAD_PRIORITY_DISPLAY = -4;
    public static final int THREAD_PRIORITY_URGENT_DISPLAY = -8;
    public static final int THREAD_PRIORITY_AUDIO = -16;
    public static final int THREAD_PRIORITY_URGENT_AUDIO = -19;
    public static final int THREAD_PRIORITY_MORE_FAVORABLE = -1;
    public static final int THREAD_PRIORITY_LESS_FAVORABLE = 1;
    public static final int SCHED_OTHER = 0;
    public static final int SCHED_FIFO = 1;
    public static final int SCHED_RR = 2;
    public static final int SCHED_BATCH = 3;
    public static final int SCHED_IDLE = 5;
    public static final int THREAD_GROUP_DEFAULT = -1;
    public static final int THREAD_GROUP_BG_NONINTERACTIVE = 0;
    private static final int THREAD_GROUP_FOREGROUND = 1;
    public static final int THREAD_GROUP_SYSTEM = 2;
    public static final int THREAD_GROUP_AUDIO_APP = 3;
    public static final int THREAD_GROUP_AUDIO_SYS = 4;
    public static final int SIGNAL_QUIT = 3;
    public static final int SIGNAL_KILL = 9;
    public static final int SIGNAL_USR1 = 10;
    static LocalSocket sZygoteSocket;
    static DataInputStream sZygoteInputStream;
    static BufferedWriter sZygoteWriter;
    static boolean sPreviousZygoteOpenFailed;
    static final int ZYGOTE_RETRY_MILLIS = 500;
    public static final int PROC_TERM_MASK = 255;
    public static final int PROC_ZERO_TERM = 0;
    public static final int PROC_SPACE_TERM = 32;
    public static final int PROC_TAB_TERM = 9;
    public static final int PROC_COMBINE = 256;
    public static final int PROC_PARENS = 512;
    public static final int PROC_QUOTES = 1024;
    public static final int PROC_OUT_STRING = 4096;
    public static final int PROC_OUT_LONG = 8192;
    public static final int PROC_OUT_FLOAT = 16384;

    /* loaded from: Process$ProcessStartResult.class */
    public static final class ProcessStartResult {
        public int pid;
        public boolean usingWrapper;
    }

    public static final native long getElapsedCpuTime();

    public static final native int getUidForName(String str);

    public static final native int getGidForName(String str);

    public static final native void setThreadPriority(int i, int i2) throws IllegalArgumentException, SecurityException;

    public static final native void setCanSelfBackground(boolean z);

    public static final native void setThreadGroup(int i, int i2) throws IllegalArgumentException, SecurityException;

    public static final native void setProcessGroup(int i, int i2) throws IllegalArgumentException, SecurityException;

    public static final native int getProcessGroup(int i) throws IllegalArgumentException, SecurityException;

    public static final native void setThreadPriority(int i) throws IllegalArgumentException, SecurityException;

    public static final native int getThreadPriority(int i) throws IllegalArgumentException;

    public static final native void setThreadScheduler(int i, int i2, int i3) throws IllegalArgumentException;

    public static final native boolean setOomAdj(int i, int i2);

    public static final native boolean setSwappiness(int i, boolean z);

    public static final native void setArgV0(String str);

    public static final native int setUid(int i);

    public static final native int setGid(int i);

    public static final native void sendSignal(int i, int i2);

    public static final native void sendSignalQuiet(int i, int i2);

    public static final native long getFreeMemory();

    public static final native long getTotalMemory();

    public static final native void readProcLines(String str, String[] strArr, long[] jArr);

    public static final native int[] getPids(String str, int[] iArr);

    public static final native boolean readProcFile(String str, int[] iArr, String[] strArr, long[] jArr, float[] fArr);

    public static final native boolean parseProcLine(byte[] bArr, int i, int i2, int[] iArr, String[] strArr, long[] jArr, float[] fArr);

    public static final native int[] getPidsForCommands(String[] strArr);

    public static final native long getPss(int i);

    public static final ProcessStartResult start(String processClass, String niceName, int uid, int gid, int[] gids, int debugFlags, int mountExternal, int targetSdkVersion, String seInfo, String[] zygoteArgs) {
        try {
            return startViaZygote(processClass, niceName, uid, gid, gids, debugFlags, mountExternal, targetSdkVersion, seInfo, zygoteArgs);
        } catch (ZygoteStartFailedEx ex) {
            Log.e(LOG_TAG, "Starting VM process through Zygote failed");
            throw new RuntimeException("Starting VM process through Zygote failed", ex);
        }
    }

    private static void openZygoteSocketIfNeeded() throws ZygoteStartFailedEx {
        int retryCount;
        if (sPreviousZygoteOpenFailed) {
            retryCount = 0;
        } else {
            retryCount = 10;
        }
        for (int retry = 0; sZygoteSocket == null && retry < retryCount + 1; retry++) {
            if (retry > 0) {
                try {
                    Log.i("Zygote", "Zygote not up yet, sleeping...");
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                }
            }
            try {
                sZygoteSocket = new LocalSocket();
                sZygoteSocket.connect(new LocalSocketAddress(ZYGOTE_SOCKET, LocalSocketAddress.Namespace.RESERVED));
                sZygoteInputStream = new DataInputStream(sZygoteSocket.getInputStream());
                sZygoteWriter = new BufferedWriter(new OutputStreamWriter(sZygoteSocket.getOutputStream()), 256);
                Log.i("Zygote", "Process: zygote socket opened");
                sPreviousZygoteOpenFailed = false;
                break;
            } catch (IOException e2) {
                if (sZygoteSocket != null) {
                    try {
                        sZygoteSocket.close();
                    } catch (IOException ex2) {
                        Log.e(LOG_TAG, "I/O exception on close after exception", ex2);
                    }
                }
                sZygoteSocket = null;
            }
        }
        if (sZygoteSocket == null) {
            sPreviousZygoteOpenFailed = true;
            throw new ZygoteStartFailedEx("connect failed");
        }
    }

    private static ProcessStartResult zygoteSendArgsAndGetResult(ArrayList<String> args) throws ZygoteStartFailedEx {
        openZygoteSocketIfNeeded();
        try {
            sZygoteWriter.write(Integer.toString(args.size()));
            sZygoteWriter.newLine();
            int sz = args.size();
            for (int i = 0; i < sz; i++) {
                String arg = args.get(i);
                if (arg.indexOf(10) >= 0) {
                    throw new ZygoteStartFailedEx("embedded newlines not allowed");
                }
                sZygoteWriter.write(arg);
                sZygoteWriter.newLine();
            }
            sZygoteWriter.flush();
            ProcessStartResult result = new ProcessStartResult();
            result.pid = sZygoteInputStream.readInt();
            if (result.pid < 0) {
                throw new ZygoteStartFailedEx("fork() failed");
            }
            result.usingWrapper = sZygoteInputStream.readBoolean();
            return result;
        } catch (IOException ex) {
            try {
                if (sZygoteSocket != null) {
                    sZygoteSocket.close();
                }
            } catch (IOException ex2) {
                Log.e(LOG_TAG, "I/O exception on routine close", ex2);
            }
            sZygoteSocket = null;
            throw new ZygoteStartFailedEx(ex);
        }
    }

    private static ProcessStartResult startViaZygote(String processClass, String niceName, int uid, int gid, int[] gids, int debugFlags, int mountExternal, int targetSdkVersion, String seInfo, String[] extraArgs) throws ZygoteStartFailedEx {
        ProcessStartResult zygoteSendArgsAndGetResult;
        synchronized (Process.class) {
            ArrayList<String> argsForZygote = new ArrayList<>();
            argsForZygote.add("--runtime-init");
            argsForZygote.add("--setuid=" + uid);
            argsForZygote.add("--setgid=" + gid);
            if ((debugFlags & 16) != 0) {
                argsForZygote.add("--enable-jni-logging");
            }
            if ((debugFlags & 8) != 0) {
                argsForZygote.add("--enable-safemode");
            }
            if ((debugFlags & 1) != 0) {
                argsForZygote.add("--enable-debugger");
            }
            if ((debugFlags & 2) != 0) {
                argsForZygote.add("--enable-checkjni");
            }
            if ((debugFlags & 4) != 0) {
                argsForZygote.add("--enable-assert");
            }
            if (mountExternal == 2) {
                argsForZygote.add("--mount-external-multiuser");
            } else if (mountExternal == 3) {
                argsForZygote.add("--mount-external-multiuser-all");
            }
            argsForZygote.add("--target-sdk-version=" + targetSdkVersion);
            if (gids != null && gids.length > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("--setgroups=");
                int sz = gids.length;
                for (int i = 0; i < sz; i++) {
                    if (i != 0) {
                        sb.append(',');
                    }
                    sb.append(gids[i]);
                }
                argsForZygote.add(sb.toString());
            }
            if (niceName != null) {
                argsForZygote.add("--nice-name=" + niceName);
            }
            if (seInfo != null) {
                argsForZygote.add("--seinfo=" + seInfo);
            }
            argsForZygote.add(processClass);
            if (extraArgs != null) {
                for (String arg : extraArgs) {
                    argsForZygote.add(arg);
                }
            }
            zygoteSendArgsAndGetResult = zygoteSendArgsAndGetResult(argsForZygote);
        }
        return zygoteSendArgsAndGetResult;
    }

    public static final int myPid() {
        return Libcore.os.getpid();
    }

    public static final int myPpid() {
        return Libcore.os.getppid();
    }

    public static final int myTid() {
        return Libcore.os.gettid();
    }

    public static final int myUid() {
        return Libcore.os.getuid();
    }

    public static final UserHandle myUserHandle() {
        return new UserHandle(UserHandle.getUserId(myUid()));
    }

    public static final boolean isIsolated() {
        int uid = UserHandle.getAppId(myUid());
        return uid >= 99000 && uid <= 99999;
    }

    public static final int getUidForPid(int pid) {
        String[] procStatusLabels = {"Uid:"};
        long[] procStatusValues = {-1};
        readProcLines("/proc/" + pid + "/status", procStatusLabels, procStatusValues);
        return (int) procStatusValues[0];
    }

    public static final int getParentPid(int pid) {
        String[] procStatusLabels = {"PPid:"};
        long[] procStatusValues = {-1};
        readProcLines("/proc/" + pid + "/status", procStatusLabels, procStatusValues);
        return (int) procStatusValues[0];
    }

    public static final int getThreadGroupLeader(int tid) {
        String[] procStatusLabels = {"Tgid:"};
        long[] procStatusValues = {-1};
        readProcLines("/proc/" + tid + "/status", procStatusLabels, procStatusValues);
        return (int) procStatusValues[0];
    }

    @Deprecated
    public static final boolean supportsProcesses() {
        return true;
    }

    public static final void killProcess(int pid) {
        sendSignal(pid, 9);
    }

    public static final void killProcessQuiet(int pid) {
        sendSignalQuiet(pid, 9);
    }
}