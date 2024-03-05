package com.android.internal.app;

import android.accounts.GrantCredentialsPermissionActivity;
import android.app.backup.FullBackup;
import android.content.Context;
import android.hardware.Camera;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.webkit.WebViewFactory;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.LockPatternUtils;
import dalvik.system.VMRuntime;
import gov.nist.core.Separators;
import gov.nist.javax.sip.header.ims.ParameterNamesIms;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/* loaded from: ProcessStats.class */
public final class ProcessStats implements Parcelable {
    static final String TAG = "ProcessStats";
    static final boolean DEBUG = false;
    static final boolean DEBUG_PARCEL = false;
    public static final String SERVICE_NAME = "procstats";
    public static final int STATE_NOTHING = -1;
    public static final int STATE_PERSISTENT = 0;
    public static final int STATE_TOP = 1;
    public static final int STATE_IMPORTANT_FOREGROUND = 2;
    public static final int STATE_IMPORTANT_BACKGROUND = 3;
    public static final int STATE_BACKUP = 4;
    public static final int STATE_HEAVY_WEIGHT = 5;
    public static final int STATE_SERVICE = 6;
    public static final int STATE_SERVICE_RESTARTING = 7;
    public static final int STATE_RECEIVER = 8;
    public static final int STATE_HOME = 9;
    public static final int STATE_LAST_ACTIVITY = 10;
    public static final int STATE_CACHED_ACTIVITY = 11;
    public static final int STATE_CACHED_ACTIVITY_CLIENT = 12;
    public static final int STATE_CACHED_EMPTY = 13;
    public static final int STATE_COUNT = 14;
    public static final int PSS_SAMPLE_COUNT = 0;
    public static final int PSS_MINIMUM = 1;
    public static final int PSS_AVERAGE = 2;
    public static final int PSS_MAXIMUM = 3;
    public static final int PSS_USS_MINIMUM = 4;
    public static final int PSS_USS_AVERAGE = 5;
    public static final int PSS_USS_MAXIMUM = 6;
    public static final int PSS_COUNT = 7;
    public static final int ADJ_NOTHING = -1;
    public static final int ADJ_MEM_FACTOR_NORMAL = 0;
    public static final int ADJ_MEM_FACTOR_MODERATE = 1;
    public static final int ADJ_MEM_FACTOR_LOW = 2;
    public static final int ADJ_MEM_FACTOR_CRITICAL = 3;
    public static final int ADJ_MEM_FACTOR_COUNT = 4;
    public static final int ADJ_SCREEN_MOD = 4;
    public static final int ADJ_SCREEN_OFF = 0;
    public static final int ADJ_SCREEN_ON = 4;
    public static final int ADJ_COUNT = 8;
    public static final int FLAG_COMPLETE = 1;
    public static final int FLAG_SHUTDOWN = 2;
    public static final int FLAG_SYSPROPS = 4;
    static final String CSV_SEP = "\t";
    private static final int PARCEL_VERSION = 13;
    private static final int MAGIC = 1347638355;
    public String mReadError;
    public String mTimePeriodStartClockStr;
    public int mFlags;
    public final ProcessMap<PackageState> mPackages;
    public final ProcessMap<ProcessState> mProcesses;
    public final long[] mMemFactorDurations;
    public int mMemFactor;
    public long mStartTime;
    public long mTimePeriodStartClock;
    public long mTimePeriodStartRealtime;
    public long mTimePeriodEndRealtime;
    String mRuntime;
    String mWebView;
    boolean mRunning;
    static final int LONGS_SIZE = 4096;
    final ArrayList<long[]> mLongs;
    int mNextLong;
    int[] mAddLongTable;
    int mAddLongTableSize;
    ArrayMap<String, Integer> mCommonStringToIndex;
    ArrayList<String> mIndexToCommonString;
    public static long COMMIT_PERIOD = 10800000;
    public static final int[] ALL_MEM_ADJ = {0, 1, 2, 3};
    public static final int[] ALL_SCREEN_ADJ = {0, 4};
    public static final int[] NON_CACHED_PROC_STATES = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final int[] BACKGROUND_PROC_STATES = {2, 3, 4, 5, 6, 7, 8};
    static final int[] PROCESS_STATE_TO_STATE = {0, 0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13};
    public static final int[] ALL_PROC_STATES = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    static final String[] STATE_NAMES = {"Persistent", "Top       ", "Imp Fg    ", "Imp Bg    ", "Backup    ", "Heavy Wght", "Service   ", "Service Rs", "Receiver  ", "Home      ", "Last Act  ", "Cch Act   ", "Cch CliAct", "Cch Empty "};
    public static final String[] ADJ_SCREEN_NAMES_CSV = {"off", Camera.Parameters.FLASH_MODE_ON};
    public static final String[] ADJ_MEM_NAMES_CSV = {"norm", ParameterNamesIms.MOD, "low", "crit"};
    public static final String[] STATE_NAMES_CSV = {"pers", "top", "impfg", "impbg", Context.BACKUP_SERVICE, "heavy", "service", "service-rs", "receiver", CalendarContract.CalendarCache.TIMEZONE_TYPE_HOME, "lastact", "cch-activity", "cch-aclient", "cch-empty"};
    static final String[] ADJ_SCREEN_TAGS = {"0", "1"};
    static final String[] ADJ_MEM_TAGS = {"n", "m", "l", FullBackup.CACHE_TREE_TOKEN};
    static final String[] STATE_TAGS = {"p", "t", FullBackup.DATA_TREE_TOKEN, "b", "u", "w", "s", "x", FullBackup.ROOT_TREE_TOKEN, "h", "l", FullBackup.APK_TREE_TOKEN, FullBackup.CACHE_TREE_TOKEN, "e"};
    static int OFFSET_TYPE_SHIFT = 0;
    static int OFFSET_TYPE_MASK = 255;
    static int OFFSET_ARRAY_SHIFT = 8;
    static int OFFSET_ARRAY_MASK = 255;
    static int OFFSET_INDEX_SHIFT = 16;
    static int OFFSET_INDEX_MASK = 65535;
    public static final Parcelable.Creator<ProcessStats> CREATOR = new Parcelable.Creator<ProcessStats>() { // from class: com.android.internal.app.ProcessStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProcessStats createFromParcel(Parcel in) {
            return new ProcessStats(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProcessStats[] newArray(int size) {
            return new ProcessStats[size];
        }
    };
    static final int[] BAD_TABLE = new int[0];

    public ProcessStats(boolean running) {
        this.mPackages = new ProcessMap<>();
        this.mProcesses = new ProcessMap<>();
        this.mMemFactorDurations = new long[8];
        this.mMemFactor = -1;
        this.mLongs = new ArrayList<>();
        this.mRunning = running;
        reset();
    }

    public ProcessStats(Parcel in) {
        this.mPackages = new ProcessMap<>();
        this.mProcesses = new ProcessMap<>();
        this.mMemFactorDurations = new long[8];
        this.mMemFactor = -1;
        this.mLongs = new ArrayList<>();
        reset();
        readFromParcel(in);
    }

    public void add(ProcessStats other) {
        ArrayMap<String, SparseArray<PackageState>> pkgMap = other.mPackages.getMap();
        for (int ip = 0; ip < pkgMap.size(); ip++) {
            String pkgName = pkgMap.keyAt(ip);
            SparseArray<PackageState> uids = pkgMap.valueAt(ip);
            for (int iu = 0; iu < uids.size(); iu++) {
                int uid = uids.keyAt(iu);
                PackageState otherState = uids.valueAt(iu);
                int NPROCS = otherState.mProcesses.size();
                int NSRVS = otherState.mServices.size();
                for (int iproc = 0; iproc < NPROCS; iproc++) {
                    ProcessState otherProc = otherState.mProcesses.valueAt(iproc);
                    if (otherProc.mCommonProcess != otherProc) {
                        ProcessState thisProc = getProcessStateLocked(pkgName, uid, otherProc.mName);
                        if (thisProc.mCommonProcess == thisProc) {
                            thisProc.mMultiPackage = true;
                            long now = SystemClock.uptimeMillis();
                            PackageState pkgState = getPackageStateLocked(pkgName, uid);
                            thisProc = thisProc.clone(thisProc.mPackage, now);
                            pkgState.mProcesses.put(thisProc.mName, thisProc);
                        }
                        thisProc.add(otherProc);
                    }
                }
                for (int isvc = 0; isvc < NSRVS; isvc++) {
                    ServiceState otherSvc = otherState.mServices.valueAt(isvc);
                    ServiceState thisSvc = getServiceStateLocked(pkgName, uid, otherSvc.mProcessName, otherSvc.mName);
                    thisSvc.add(otherSvc);
                }
            }
        }
        ArrayMap<String, SparseArray<ProcessState>> procMap = other.mProcesses.getMap();
        for (int ip2 = 0; ip2 < procMap.size(); ip2++) {
            SparseArray<ProcessState> uids2 = procMap.valueAt(ip2);
            for (int iu2 = 0; iu2 < uids2.size(); iu2++) {
                int uid2 = uids2.keyAt(iu2);
                ProcessState otherProc2 = uids2.valueAt(iu2);
                ProcessState thisProc2 = this.mProcesses.get(otherProc2.mName, uid2);
                if (thisProc2 == null) {
                    thisProc2 = new ProcessState(this, otherProc2.mPackage, uid2, otherProc2.mName);
                    this.mProcesses.put(otherProc2.mName, uid2, thisProc2);
                    PackageState thisState = getPackageStateLocked(otherProc2.mPackage, uid2);
                    if (!thisState.mProcesses.containsKey(otherProc2.mName)) {
                        thisState.mProcesses.put(otherProc2.mName, thisProc2);
                    }
                }
                thisProc2.add(otherProc2);
            }
        }
        for (int i = 0; i < 8; i++) {
            long[] jArr = this.mMemFactorDurations;
            int i2 = i;
            jArr[i2] = jArr[i2] + other.mMemFactorDurations[i];
        }
        if (other.mTimePeriodStartClock < this.mTimePeriodStartClock) {
            this.mTimePeriodStartClock = other.mTimePeriodStartClock;
            this.mTimePeriodStartClockStr = other.mTimePeriodStartClockStr;
        }
        this.mTimePeriodEndRealtime += other.mTimePeriodEndRealtime - other.mTimePeriodStartRealtime;
    }

    private static void printScreenLabel(PrintWriter pw, int offset) {
        switch (offset) {
            case -1:
                pw.print("             ");
                return;
            case 0:
                pw.print("Screen Off / ");
                return;
            case 4:
                pw.print("Screen On  / ");
                return;
            default:
                pw.print("?????????? / ");
                return;
        }
    }

    public static void printScreenLabelCsv(PrintWriter pw, int offset) {
        switch (offset) {
            case -1:
                return;
            case 0:
                pw.print(ADJ_SCREEN_NAMES_CSV[0]);
                return;
            case 4:
                pw.print(ADJ_SCREEN_NAMES_CSV[1]);
                return;
            default:
                pw.print("???");
                return;
        }
    }

    private static void printMemLabel(PrintWriter pw, int offset) {
        switch (offset) {
            case -1:
                pw.print("       ");
                return;
            case 0:
                pw.print("Norm / ");
                return;
            case 1:
                pw.print("Mod  / ");
                return;
            case 2:
                pw.print("Low  / ");
                return;
            case 3:
                pw.print("Crit / ");
                return;
            default:
                pw.print("???? / ");
                return;
        }
    }

    public static void printMemLabelCsv(PrintWriter pw, int offset) {
        if (offset >= 0) {
            if (offset <= 3) {
                pw.print(ADJ_MEM_NAMES_CSV[offset]);
            } else {
                pw.print("???");
            }
        }
    }

    public static long dumpSingleTime(PrintWriter pw, String prefix, long[] durations, int curState, long curStartTime, long now) {
        long totalTime = 0;
        int printedScreen = -1;
        int iscreen = 0;
        while (iscreen < 8) {
            int printedMem = -1;
            int imem = 0;
            while (imem < 4) {
                int state = imem + iscreen;
                long time = durations[state];
                String running = "";
                if (curState == state) {
                    time += now - curStartTime;
                    if (pw != null) {
                        running = " (running)";
                    }
                }
                if (time != 0) {
                    if (pw != null) {
                        pw.print(prefix);
                        printScreenLabel(pw, printedScreen != iscreen ? iscreen : -1);
                        printedScreen = iscreen;
                        printMemLabel(pw, printedMem != imem ? imem : -1);
                        printedMem = imem;
                        TimeUtils.formatDuration(time, pw);
                        pw.println(running);
                    }
                    totalTime += time;
                }
                imem++;
            }
            iscreen += 4;
        }
        if (totalTime != 0 && pw != null) {
            pw.print(prefix);
            printScreenLabel(pw, -1);
            pw.print("TOTAL: ");
            TimeUtils.formatDuration(totalTime, pw);
            pw.println();
        }
        return totalTime;
    }

    static void dumpAdjTimesCheckin(PrintWriter pw, String sep, long[] durations, int curState, long curStartTime, long now) {
        for (int iscreen = 0; iscreen < 8; iscreen += 4) {
            for (int imem = 0; imem < 4; imem++) {
                int state = imem + iscreen;
                long time = durations[state];
                if (curState == state) {
                    time += now - curStartTime;
                }
                if (time != 0) {
                    printAdjTagAndValue(pw, state, time);
                }
            }
        }
    }

    static void dumpServiceTimeCheckin(PrintWriter pw, String label, String packageName, int uid, String serviceName, ServiceState svc, int serviceType, int opCount, int curState, long curStartTime, long now) {
        if (opCount <= 0) {
            return;
        }
        pw.print(label);
        pw.print(Separators.COMMA);
        pw.print(packageName);
        pw.print(Separators.COMMA);
        pw.print(uid);
        pw.print(Separators.COMMA);
        pw.print(serviceName);
        pw.print(Separators.COMMA);
        pw.print(opCount);
        boolean didCurState = false;
        for (int i = 0; i < svc.mDurationsTableSize; i++) {
            int off = svc.mDurationsTable[i];
            int type = (off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
            int memFactor = type / 4;
            if (type % 4 == serviceType) {
                long time = svc.mStats.getLong(off, 0);
                if (curState == memFactor) {
                    didCurState = true;
                    time += now - curStartTime;
                }
                printAdjTagAndValue(pw, memFactor, time);
            }
        }
        if (!didCurState && curState != -1) {
            printAdjTagAndValue(pw, curState, now - curStartTime);
        }
        pw.println();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v0, types: [com.android.internal.app.ProcessStats$ProcessDataCollection] */
    public static void computeProcessData(ProcessState proc, ProcessDataCollection data, long now) {
        data.totalTime = 0L;
        ?? r7 = 0;
        data.maxUss = 0L;
        data.avgUss = 0L;
        r7.minUss = data;
        data.maxPss = data;
        data.avgPss = 0L;
        r7.minPss = data;
        data.numPss = data;
        for (int is = 0; is < data.screenStates.length; is++) {
            for (int im = 0; im < data.memStates.length; im++) {
                for (int ip = 0; ip < data.procStates.length; ip++) {
                    int bucket = ((data.screenStates[is] + data.memStates[im]) * 14) + data.procStates[ip];
                    data.totalTime += proc.getDuration(bucket, now);
                    long samples = proc.getPssSampleCount(bucket);
                    if (samples > 0) {
                        long minPss = proc.getPssMinimum(bucket);
                        long avgPss = proc.getPssAverage(bucket);
                        long maxPss = proc.getPssMaximum(bucket);
                        long minUss = proc.getPssUssMinimum(bucket);
                        long avgUss = proc.getPssUssAverage(bucket);
                        long maxUss = proc.getPssUssMaximum(bucket);
                        if (data.numPss == 0) {
                            data.minPss = minPss;
                            data.avgPss = avgPss;
                            data.maxPss = maxPss;
                            data.minUss = minUss;
                            data.avgUss = avgUss;
                            data.maxUss = maxUss;
                        } else {
                            if (minPss < data.minPss) {
                                data.minPss = minPss;
                            }
                            data.avgPss = (long) (((data.avgPss * data.numPss) + (avgPss * samples)) / (data.numPss + samples));
                            if (maxPss > data.maxPss) {
                                data.maxPss = maxPss;
                            }
                            if (minUss < data.minUss) {
                                data.minUss = minUss;
                            }
                            data.avgUss = (long) (((data.avgUss * data.numPss) + (avgUss * samples)) / (data.numPss + samples));
                            if (maxUss > data.maxUss) {
                                data.maxUss = maxUss;
                            }
                        }
                        data.numPss += samples;
                    }
                }
            }
        }
    }

    static long computeProcessTimeLocked(ProcessState proc, int[] screenStates, int[] memStates, int[] procStates, long now) {
        long totalTime = 0;
        for (int i : screenStates) {
            for (int i2 : memStates) {
                for (int i3 : procStates) {
                    int bucket = ((i + i2) * 14) + i3;
                    totalTime += proc.getDuration(bucket, now);
                }
            }
        }
        proc.mTmpTotalTime = totalTime;
        return totalTime;
    }

    static void dumpProcessState(PrintWriter pw, String prefix, ProcessState proc, int[] screenStates, int[] memStates, int[] procStates, long now) {
        long totalTime = 0;
        int printedScreen = -1;
        for (int is = 0; is < screenStates.length; is++) {
            int printedMem = -1;
            for (int im = 0; im < memStates.length; im++) {
                for (int ip = 0; ip < procStates.length; ip++) {
                    int iscreen = screenStates[is];
                    int imem = memStates[im];
                    int bucket = ((iscreen + imem) * 14) + procStates[ip];
                    long time = proc.getDuration(bucket, now);
                    String running = "";
                    if (proc.mCurState == bucket) {
                        running = " (running)";
                    }
                    if (time != 0) {
                        pw.print(prefix);
                        if (screenStates.length > 1) {
                            printScreenLabel(pw, printedScreen != iscreen ? iscreen : -1);
                            printedScreen = iscreen;
                        }
                        if (memStates.length > 1) {
                            printMemLabel(pw, printedMem != imem ? imem : -1);
                            printedMem = imem;
                        }
                        pw.print(STATE_NAMES[procStates[ip]]);
                        pw.print(": ");
                        TimeUtils.formatDuration(time, pw);
                        pw.println(running);
                        totalTime += time;
                    }
                }
            }
        }
        if (totalTime != 0) {
            pw.print(prefix);
            if (screenStates.length > 1) {
                printScreenLabel(pw, -1);
            }
            if (memStates.length > 1) {
                printMemLabel(pw, -1);
            }
            pw.print("TOTAL     : ");
            TimeUtils.formatDuration(totalTime, pw);
            pw.println();
        }
    }

    static void dumpProcessPss(PrintWriter pw, String prefix, ProcessState proc, int[] screenStates, int[] memStates, int[] procStates) {
        boolean printedHeader = false;
        int printedScreen = -1;
        for (int is = 0; is < screenStates.length; is++) {
            int printedMem = -1;
            for (int im = 0; im < memStates.length; im++) {
                for (int ip = 0; ip < procStates.length; ip++) {
                    int iscreen = screenStates[is];
                    int imem = memStates[im];
                    int bucket = ((iscreen + imem) * 14) + procStates[ip];
                    long count = proc.getPssSampleCount(bucket);
                    if (count > 0) {
                        if (!printedHeader) {
                            pw.print(prefix);
                            pw.print("PSS/USS (");
                            pw.print(proc.mPssTableSize);
                            pw.println(" entries):");
                            printedHeader = true;
                        }
                        pw.print(prefix);
                        pw.print("  ");
                        if (screenStates.length > 1) {
                            printScreenLabel(pw, printedScreen != iscreen ? iscreen : -1);
                            printedScreen = iscreen;
                        }
                        if (memStates.length > 1) {
                            printMemLabel(pw, printedMem != imem ? imem : -1);
                            printedMem = imem;
                        }
                        pw.print(STATE_NAMES[procStates[ip]]);
                        pw.print(": ");
                        pw.print(count);
                        pw.print(" samples ");
                        printSizeValue(pw, proc.getPssMinimum(bucket) * 1024);
                        pw.print(Separators.SP);
                        printSizeValue(pw, proc.getPssAverage(bucket) * 1024);
                        pw.print(Separators.SP);
                        printSizeValue(pw, proc.getPssMaximum(bucket) * 1024);
                        pw.print(" / ");
                        printSizeValue(pw, proc.getPssUssMinimum(bucket) * 1024);
                        pw.print(Separators.SP);
                        printSizeValue(pw, proc.getPssUssAverage(bucket) * 1024);
                        pw.print(Separators.SP);
                        printSizeValue(pw, proc.getPssUssMaximum(bucket) * 1024);
                        pw.println();
                    }
                }
            }
        }
        if (proc.mNumExcessiveWake != 0) {
            pw.print(prefix);
            pw.print("Killed for excessive wake locks: ");
            pw.print(proc.mNumExcessiveWake);
            pw.println(" times");
        }
        if (proc.mNumExcessiveCpu != 0) {
            pw.print(prefix);
            pw.print("Killed for excessive CPU use: ");
            pw.print(proc.mNumExcessiveCpu);
            pw.println(" times");
        }
        if (proc.mNumCachedKill != 0) {
            pw.print(prefix);
            pw.print("Killed from cached state: ");
            pw.print(proc.mNumCachedKill);
            pw.print(" times from pss ");
            printSizeValue(pw, proc.mMinCachedKillPss * 1024);
            pw.print("-");
            printSizeValue(pw, proc.mAvgCachedKillPss * 1024);
            pw.print("-");
            printSizeValue(pw, proc.mMaxCachedKillPss * 1024);
            pw.println();
        }
    }

    static void dumpStateHeadersCsv(PrintWriter pw, String sep, int[] screenStates, int[] memStates, int[] procStates) {
        int NS = screenStates != null ? screenStates.length : 1;
        int NM = memStates != null ? memStates.length : 1;
        int NP = procStates != null ? procStates.length : 1;
        for (int is = 0; is < NS; is++) {
            for (int im = 0; im < NM; im++) {
                for (int ip = 0; ip < NP; ip++) {
                    pw.print(sep);
                    boolean printed = false;
                    if (screenStates != null && screenStates.length > 1) {
                        printScreenLabelCsv(pw, screenStates[is]);
                        printed = true;
                    }
                    if (memStates != null && memStates.length > 1) {
                        if (printed) {
                            pw.print("-");
                        }
                        printMemLabelCsv(pw, memStates[im]);
                        printed = true;
                    }
                    if (procStates != null && procStates.length > 1) {
                        if (printed) {
                            pw.print("-");
                        }
                        pw.print(STATE_NAMES_CSV[procStates[ip]]);
                    }
                }
            }
        }
    }

    static void dumpProcessStateCsv(PrintWriter pw, ProcessState proc, boolean sepScreenStates, int[] screenStates, boolean sepMemStates, int[] memStates, boolean sepProcStates, int[] procStates, long now) {
        int NSS = sepScreenStates ? screenStates.length : 1;
        int NMS = sepMemStates ? memStates.length : 1;
        int NPS = sepProcStates ? procStates.length : 1;
        for (int iss = 0; iss < NSS; iss++) {
            for (int ims = 0; ims < NMS; ims++) {
                for (int ips = 0; ips < NPS; ips++) {
                    int vsscreen = sepScreenStates ? screenStates[iss] : 0;
                    int vsmem = sepMemStates ? memStates[ims] : 0;
                    int vsproc = sepProcStates ? procStates[ips] : 0;
                    int NSA = sepScreenStates ? 1 : screenStates.length;
                    int NMA = sepMemStates ? 1 : memStates.length;
                    int NPA = sepProcStates ? 1 : procStates.length;
                    long totalTime = 0;
                    for (int isa = 0; isa < NSA; isa++) {
                        for (int ima = 0; ima < NMA; ima++) {
                            for (int ipa = 0; ipa < NPA; ipa++) {
                                int vascreen = sepScreenStates ? 0 : screenStates[isa];
                                int vamem = sepMemStates ? 0 : memStates[ima];
                                int vaproc = sepProcStates ? 0 : procStates[ipa];
                                int bucket = ((vsscreen + vascreen + vsmem + vamem) * 14) + vsproc + vaproc;
                                totalTime += proc.getDuration(bucket, now);
                            }
                        }
                    }
                    pw.print("\t");
                    pw.print(totalTime);
                }
            }
        }
    }

    static void dumpProcessList(PrintWriter pw, String prefix, ArrayList<ProcessState> procs, int[] screenStates, int[] memStates, int[] procStates, long now) {
        String innerPrefix = prefix + "  ";
        for (int i = procs.size() - 1; i >= 0; i--) {
            ProcessState proc = procs.get(i);
            pw.print(prefix);
            pw.print(proc.mName);
            pw.print(" / ");
            UserHandle.formatUid(pw, proc.mUid);
            pw.print(" (");
            pw.print(proc.mDurationsTableSize);
            pw.print(" entries)");
            pw.println(Separators.COLON);
            dumpProcessState(pw, innerPrefix, proc, screenStates, memStates, procStates, now);
            if (proc.mPssTableSize > 0) {
                dumpProcessPss(pw, innerPrefix, proc, screenStates, memStates, procStates);
            }
        }
    }

    static void dumpProcessSummaryDetails(PrintWriter pw, ProcessState proc, String prefix, String label, int[] screenStates, int[] memStates, int[] procStates, long now, long totalTime, boolean full) {
        ProcessDataCollection totals = new ProcessDataCollection(screenStates, memStates, procStates);
        computeProcessData(proc, totals, now);
        if (totals.totalTime != 0 || totals.numPss != 0) {
            if (prefix != null) {
                pw.print(prefix);
            }
            if (label != null) {
                pw.print(label);
            }
            totals.print(pw, totalTime, full);
            if (prefix != null) {
                pw.println();
            }
        }
    }

    static void dumpProcessSummaryLocked(PrintWriter pw, String prefix, ArrayList<ProcessState> procs, int[] screenStates, int[] memStates, int[] procStates, long now, long totalTime) {
        for (int i = procs.size() - 1; i >= 0; i--) {
            ProcessState proc = procs.get(i);
            pw.print(prefix);
            pw.print("* ");
            pw.print(proc.mName);
            pw.print(" / ");
            UserHandle.formatUid(pw, proc.mUid);
            pw.println(Separators.COLON);
            dumpProcessSummaryDetails(pw, proc, prefix, "         TOTAL: ", screenStates, memStates, procStates, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "    Persistent: ", screenStates, memStates, new int[]{0}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "           Top: ", screenStates, memStates, new int[]{1}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "        Imp Fg: ", screenStates, memStates, new int[]{2}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "        Imp Bg: ", screenStates, memStates, new int[]{3}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "        Backup: ", screenStates, memStates, new int[]{4}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "     Heavy Wgt: ", screenStates, memStates, new int[]{5}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "       Service: ", screenStates, memStates, new int[]{6}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "    Service Rs: ", screenStates, memStates, new int[]{7}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "      Receiver: ", screenStates, memStates, new int[]{8}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "          Home: ", screenStates, memStates, new int[]{9}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "    (Last Act): ", screenStates, memStates, new int[]{10}, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "      (Cached): ", screenStates, memStates, new int[]{11, 12, 13}, now, totalTime, true);
        }
    }

    static void printPercent(PrintWriter pw, double fraction) {
        double fraction2 = fraction * 100.0d;
        if (fraction2 < 1.0d) {
            pw.print(String.format("%.2f", Double.valueOf(fraction2)));
        } else if (fraction2 < 10.0d) {
            pw.print(String.format("%.1f", Double.valueOf(fraction2)));
        } else {
            pw.print(String.format("%.0f", Double.valueOf(fraction2)));
        }
        pw.print(Separators.PERCENT);
    }

    static void printSizeValue(PrintWriter pw, long number) {
        String value;
        float result = (float) number;
        String suffix = "";
        if (result > 900.0f) {
            suffix = "KB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "MB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "GB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "TB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "PB";
            result /= 1024.0f;
        }
        if (result < 1.0f) {
            value = String.format("%.2f", Float.valueOf(result));
        } else if (result < 10.0f) {
            value = String.format("%.1f", Float.valueOf(result));
        } else if (result < 100.0f) {
            value = String.format("%.0f", Float.valueOf(result));
        } else {
            value = String.format("%.0f", Float.valueOf(result));
        }
        pw.print(value);
        pw.print(suffix);
    }

    public static void dumpProcessListCsv(PrintWriter pw, ArrayList<ProcessState> procs, boolean sepScreenStates, int[] screenStates, boolean sepMemStates, int[] memStates, boolean sepProcStates, int[] procStates, long now) {
        pw.print("process");
        pw.print("\t");
        pw.print(GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID);
        dumpStateHeadersCsv(pw, "\t", sepScreenStates ? screenStates : null, sepMemStates ? memStates : null, sepProcStates ? procStates : null);
        pw.println();
        for (int i = procs.size() - 1; i >= 0; i--) {
            ProcessState proc = procs.get(i);
            pw.print(proc.mName);
            pw.print("\t");
            UserHandle.formatUid(pw, proc.mUid);
            dumpProcessStateCsv(pw, proc, sepScreenStates, screenStates, sepMemStates, memStates, sepProcStates, procStates, now);
            pw.println();
        }
    }

    static int printArrayEntry(PrintWriter pw, String[] array, int value, int mod) {
        int index = value / mod;
        if (index >= 0 && index < array.length) {
            pw.print(array[index]);
        } else {
            pw.print('?');
        }
        return value - (index * mod);
    }

    static void printProcStateTag(PrintWriter pw, int state) {
        printArrayEntry(pw, STATE_TAGS, printArrayEntry(pw, ADJ_MEM_TAGS, printArrayEntry(pw, ADJ_SCREEN_TAGS, state, 56), 14), 1);
    }

    static void printAdjTag(PrintWriter pw, int state) {
        printArrayEntry(pw, ADJ_MEM_TAGS, printArrayEntry(pw, ADJ_SCREEN_TAGS, state, 4), 1);
    }

    static void printProcStateTagAndValue(PrintWriter pw, int state, long value) {
        pw.print(',');
        printProcStateTag(pw, state);
        pw.print(':');
        pw.print(value);
    }

    static void printAdjTagAndValue(PrintWriter pw, int state, long value) {
        pw.print(',');
        printAdjTag(pw, state);
        pw.print(':');
        pw.print(value);
    }

    static void dumpAllProcessStateCheckin(PrintWriter pw, ProcessState proc, long now) {
        boolean didCurState = false;
        for (int i = 0; i < proc.mDurationsTableSize; i++) {
            int off = proc.mDurationsTable[i];
            int type = (off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
            long time = proc.mStats.getLong(off, 0);
            if (proc.mCurState == type) {
                didCurState = true;
                time += now - proc.mStartTime;
            }
            printProcStateTagAndValue(pw, type, time);
        }
        if (!didCurState && proc.mCurState != -1) {
            printProcStateTagAndValue(pw, proc.mCurState, now - proc.mStartTime);
        }
    }

    static void dumpAllProcessPssCheckin(PrintWriter pw, ProcessState proc) {
        for (int i = 0; i < proc.mPssTableSize; i++) {
            int off = proc.mPssTable[i];
            int type = (off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
            long count = proc.mStats.getLong(off, 0);
            long min = proc.mStats.getLong(off, 1);
            long avg = proc.mStats.getLong(off, 2);
            long max = proc.mStats.getLong(off, 3);
            long umin = proc.mStats.getLong(off, 4);
            long uavg = proc.mStats.getLong(off, 5);
            long umax = proc.mStats.getLong(off, 6);
            pw.print(',');
            printProcStateTag(pw, type);
            pw.print(':');
            pw.print(count);
            pw.print(':');
            pw.print(min);
            pw.print(':');
            pw.print(avg);
            pw.print(':');
            pw.print(max);
            pw.print(':');
            pw.print(umin);
            pw.print(':');
            pw.print(uavg);
            pw.print(':');
            pw.print(umax);
        }
    }

    public void reset() {
        resetCommon();
        this.mPackages.getMap().clear();
        this.mProcesses.getMap().clear();
        this.mMemFactor = -1;
        this.mStartTime = 0L;
    }

    public void resetSafely() {
        resetCommon();
        long now = SystemClock.uptimeMillis();
        ArrayMap<String, SparseArray<ProcessState>> procMap = this.mProcesses.getMap();
        for (int ip = procMap.size() - 1; ip >= 0; ip--) {
            SparseArray<ProcessState> uids = procMap.valueAt(ip);
            for (int iu = uids.size() - 1; iu >= 0; iu--) {
                if (uids.valueAt(iu).isInUse()) {
                    uids.valueAt(iu).resetSafely(now);
                } else {
                    uids.valueAt(iu).makeDead();
                    uids.removeAt(iu);
                }
            }
            if (uids.size() <= 0) {
                procMap.removeAt(ip);
            }
        }
        ArrayMap<String, SparseArray<PackageState>> pkgMap = this.mPackages.getMap();
        for (int ip2 = pkgMap.size() - 1; ip2 >= 0; ip2--) {
            SparseArray<PackageState> uids2 = pkgMap.valueAt(ip2);
            for (int iu2 = uids2.size() - 1; iu2 >= 0; iu2--) {
                PackageState pkgState = uids2.valueAt(iu2);
                for (int iproc = pkgState.mProcesses.size() - 1; iproc >= 0; iproc--) {
                    ProcessState ps = pkgState.mProcesses.valueAt(iproc);
                    if (ps.isInUse() || ps.mCommonProcess.isInUse()) {
                        pkgState.mProcesses.valueAt(iproc).resetSafely(now);
                    } else {
                        pkgState.mProcesses.valueAt(iproc).makeDead();
                        pkgState.mProcesses.removeAt(iproc);
                    }
                }
                for (int isvc = pkgState.mServices.size() - 1; isvc >= 0; isvc--) {
                    ServiceState ss = pkgState.mServices.valueAt(isvc);
                    if (ss.isInUse()) {
                        pkgState.mServices.valueAt(isvc).resetSafely(now);
                    } else {
                        pkgState.mServices.removeAt(isvc);
                    }
                }
                if (pkgState.mProcesses.size() <= 0 && pkgState.mServices.size() <= 0) {
                    uids2.removeAt(iu2);
                }
            }
            if (uids2.size() <= 0) {
                pkgMap.removeAt(ip2);
            }
        }
        this.mStartTime = now;
    }

    private void resetCommon() {
        this.mTimePeriodStartClock = System.currentTimeMillis();
        buildTimePeriodStartClockStr();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mTimePeriodEndRealtime = elapsedRealtime;
        this.mTimePeriodStartRealtime = elapsedRealtime;
        this.mLongs.clear();
        this.mLongs.add(new long[4096]);
        this.mNextLong = 0;
        Arrays.fill(this.mMemFactorDurations, 0L);
        this.mStartTime = 0L;
        this.mReadError = null;
        this.mFlags = 0;
        evaluateSystemProperties(true);
    }

    public boolean evaluateSystemProperties(boolean update) {
        boolean changed = false;
        String runtime = SystemProperties.get("persist.sys.dalvik.vm.lib", VMRuntime.getRuntime().vmLibrary());
        if (!Objects.equals(runtime, this.mRuntime)) {
            changed = true;
            if (update) {
                this.mRuntime = runtime;
            }
        }
        String webview = WebViewFactory.useExperimentalWebView() ? "chromeview" : "webview";
        if (!Objects.equals(webview, this.mWebView)) {
            changed = true;
            if (update) {
                this.mWebView = webview;
            }
        }
        return changed;
    }

    private void buildTimePeriodStartClockStr() {
        this.mTimePeriodStartClockStr = DateFormat.format("yyyy-MM-dd-HH-mm-ss", this.mTimePeriodStartClock).toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int[] readTableFromParcel(Parcel in, String name, String what) {
        int size = in.readInt();
        if (size < 0) {
            Slog.w(TAG, "Ignoring existing stats; bad " + what + " table size: " + size);
            return BAD_TABLE;
        } else if (size == 0) {
            return null;
        } else {
            int[] table = new int[size];
            for (int i = 0; i < size; i++) {
                table[i] = in.readInt();
                if (!validateLongOffset(table[i])) {
                    Slog.w(TAG, "Ignoring existing stats; bad " + what + " table entry: " + printLongOffset(table[i]));
                    return null;
                }
            }
            return table;
        }
    }

    private void writeCompactedLongArray(Parcel out, long[] array, int num) {
        for (int i = 0; i < num; i++) {
            long val = array[i];
            if (val < 0) {
                Slog.w(TAG, "Time val negative: " + val);
                val = 0;
            }
            if (val <= 2147483647L) {
                out.writeInt((int) val);
            } else {
                int top = ((int) ((val >> 32) & 2147483647L)) ^ (-1);
                int bottom = (int) (val & 268435455);
                out.writeInt(top);
                out.writeInt(bottom);
            }
        }
    }

    private void readCompactedLongArray(Parcel in, int version, long[] array, int num) {
        if (version <= 10) {
            in.readLongArray(array);
            return;
        }
        int alen = array.length;
        if (num > alen) {
            throw new RuntimeException("bad array lengths: got " + num + " array is " + alen);
        }
        int i = 0;
        while (i < num) {
            int val = in.readInt();
            if (val >= 0) {
                array[i] = val;
            } else {
                int bottom = in.readInt();
                array[i] = ((val ^ (-1)) << 32) | bottom;
            }
            i++;
        }
        while (i < alen) {
            array[i] = 0;
            i++;
        }
    }

    private void writeCommonString(Parcel out, String name) {
        Integer index = this.mCommonStringToIndex.get(name);
        if (index != null) {
            out.writeInt(index.intValue());
            return;
        }
        Integer index2 = Integer.valueOf(this.mCommonStringToIndex.size());
        this.mCommonStringToIndex.put(name, index2);
        out.writeInt(index2.intValue() ^ (-1));
        out.writeString(name);
    }

    private String readCommonString(Parcel in, int version) {
        if (version <= 9) {
            return in.readString();
        }
        int index = in.readInt();
        if (index >= 0) {
            return this.mIndexToCommonString.get(index);
        }
        int index2 = index ^ (-1);
        String name = in.readString();
        while (this.mIndexToCommonString.size() <= index2) {
            this.mIndexToCommonString.add(null);
        }
        this.mIndexToCommonString.set(index2, name);
        return name;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        long now = SystemClock.uptimeMillis();
        out.writeInt(MAGIC);
        out.writeInt(13);
        out.writeInt(14);
        out.writeInt(8);
        out.writeInt(7);
        out.writeInt(4096);
        this.mCommonStringToIndex = new ArrayMap<>(this.mProcesses.mMap.size());
        ArrayMap<String, SparseArray<ProcessState>> procMap = this.mProcesses.getMap();
        int NPROC = procMap.size();
        for (int ip = 0; ip < NPROC; ip++) {
            SparseArray<ProcessState> uids = procMap.valueAt(ip);
            int NUID = uids.size();
            for (int iu = 0; iu < NUID; iu++) {
                uids.valueAt(iu).commitStateTime(now);
            }
        }
        ArrayMap<String, SparseArray<PackageState>> pkgMap = this.mPackages.getMap();
        int NPKG = pkgMap.size();
        for (int ip2 = 0; ip2 < NPKG; ip2++) {
            SparseArray<PackageState> uids2 = pkgMap.valueAt(ip2);
            int NUID2 = uids2.size();
            for (int iu2 = 0; iu2 < NUID2; iu2++) {
                PackageState pkgState = uids2.valueAt(iu2);
                int NPROCS = pkgState.mProcesses.size();
                for (int iproc = 0; iproc < NPROCS; iproc++) {
                    ProcessState proc = pkgState.mProcesses.valueAt(iproc);
                    if (proc.mCommonProcess != proc) {
                        proc.commitStateTime(now);
                    }
                }
                int NSRVS = pkgState.mServices.size();
                for (int isvc = 0; isvc < NSRVS; isvc++) {
                    pkgState.mServices.valueAt(isvc).commitStateTime(now);
                }
            }
        }
        out.writeLong(this.mTimePeriodStartClock);
        out.writeLong(this.mTimePeriodStartRealtime);
        out.writeLong(this.mTimePeriodEndRealtime);
        out.writeString(this.mRuntime);
        out.writeString(this.mWebView);
        out.writeInt(this.mFlags);
        out.writeInt(this.mLongs.size());
        out.writeInt(this.mNextLong);
        for (int i = 0; i < this.mLongs.size() - 1; i++) {
            long[] array = this.mLongs.get(i);
            writeCompactedLongArray(out, array, array.length);
        }
        long[] lastLongs = this.mLongs.get(this.mLongs.size() - 1);
        writeCompactedLongArray(out, lastLongs, this.mNextLong);
        if (this.mMemFactor != -1) {
            long[] jArr = this.mMemFactorDurations;
            int i2 = this.mMemFactor;
            jArr[i2] = jArr[i2] + (now - this.mStartTime);
            this.mStartTime = now;
        }
        writeCompactedLongArray(out, this.mMemFactorDurations, this.mMemFactorDurations.length);
        out.writeInt(NPROC);
        for (int ip3 = 0; ip3 < NPROC; ip3++) {
            writeCommonString(out, procMap.keyAt(ip3));
            SparseArray<ProcessState> uids3 = procMap.valueAt(ip3);
            int NUID3 = uids3.size();
            out.writeInt(NUID3);
            for (int iu3 = 0; iu3 < NUID3; iu3++) {
                out.writeInt(uids3.keyAt(iu3));
                ProcessState proc2 = uids3.valueAt(iu3);
                writeCommonString(out, proc2.mPackage);
                proc2.writeToParcel(out, now);
            }
        }
        out.writeInt(NPKG);
        for (int ip4 = 0; ip4 < NPKG; ip4++) {
            writeCommonString(out, pkgMap.keyAt(ip4));
            SparseArray<PackageState> uids4 = pkgMap.valueAt(ip4);
            int NUID4 = uids4.size();
            out.writeInt(NUID4);
            for (int iu4 = 0; iu4 < NUID4; iu4++) {
                out.writeInt(uids4.keyAt(iu4));
                PackageState pkgState2 = uids4.valueAt(iu4);
                int NPROCS2 = pkgState2.mProcesses.size();
                out.writeInt(NPROCS2);
                for (int iproc2 = 0; iproc2 < NPROCS2; iproc2++) {
                    writeCommonString(out, pkgState2.mProcesses.keyAt(iproc2));
                    ProcessState proc3 = pkgState2.mProcesses.valueAt(iproc2);
                    if (proc3.mCommonProcess == proc3) {
                        out.writeInt(0);
                    } else {
                        out.writeInt(1);
                        proc3.writeToParcel(out, now);
                    }
                }
                int NSRVS2 = pkgState2.mServices.size();
                out.writeInt(NSRVS2);
                for (int isvc2 = 0; isvc2 < NSRVS2; isvc2++) {
                    out.writeString(pkgState2.mServices.keyAt(isvc2));
                    ServiceState svc = pkgState2.mServices.valueAt(isvc2);
                    writeCommonString(out, svc.mProcessName);
                    svc.writeToParcel(out, now);
                }
            }
        }
        this.mCommonStringToIndex = null;
    }

    private boolean readCheckedInt(Parcel in, int val, String what) {
        int got = in.readInt();
        if (got != val) {
            this.mReadError = "bad " + what + ": " + got;
            return false;
        }
        return true;
    }

    static byte[] readFully(InputStream stream, int[] outLen) throws IOException {
        int pos = 0;
        int initialAvail = stream.available();
        byte[] data = new byte[initialAvail > 0 ? initialAvail + 1 : 16384];
        while (true) {
            int amt = stream.read(data, pos, data.length - pos);
            if (amt < 0) {
                outLen[0] = pos;
                return data;
            }
            pos += amt;
            if (pos >= data.length) {
                byte[] newData = new byte[pos + 16384];
                System.arraycopy(data, 0, newData, 0, pos);
                data = newData;
            }
        }
    }

    public void read(InputStream stream) {
        try {
            int[] len = new int[1];
            byte[] raw = readFully(stream, len);
            Parcel in = Parcel.obtain();
            in.unmarshall(raw, 0, len[0]);
            in.setDataPosition(0);
            stream.close();
            readFromParcel(in);
        } catch (IOException e) {
            this.mReadError = "caught exception: " + e;
        }
    }

    public void readFromParcel(Parcel in) {
        boolean hadData = this.mPackages.getMap().size() > 0 || this.mProcesses.getMap().size() > 0;
        if (hadData) {
            resetSafely();
        }
        if (!readCheckedInt(in, MAGIC, "magic number")) {
            return;
        }
        int version = in.readInt();
        if (version != 13) {
            this.mReadError = "bad version: " + version;
        } else if (readCheckedInt(in, 14, "state count") && readCheckedInt(in, 8, "adj count") && readCheckedInt(in, 7, "pss count") && readCheckedInt(in, 4096, "longs size")) {
            this.mIndexToCommonString = new ArrayList<>();
            this.mTimePeriodStartClock = in.readLong();
            buildTimePeriodStartClockStr();
            this.mTimePeriodStartRealtime = in.readLong();
            this.mTimePeriodEndRealtime = in.readLong();
            this.mRuntime = in.readString();
            this.mWebView = in.readString();
            this.mFlags = in.readInt();
            int NLONGS = in.readInt();
            int NEXTLONG = in.readInt();
            this.mLongs.clear();
            for (int i = 0; i < NLONGS - 1; i++) {
                while (i >= this.mLongs.size()) {
                    this.mLongs.add(new long[4096]);
                }
                readCompactedLongArray(in, version, this.mLongs.get(i), 4096);
            }
            long[] longs = new long[4096];
            this.mNextLong = NEXTLONG;
            readCompactedLongArray(in, version, longs, NEXTLONG);
            this.mLongs.add(longs);
            readCompactedLongArray(in, version, this.mMemFactorDurations, this.mMemFactorDurations.length);
            int NPROC = in.readInt();
            if (NPROC < 0) {
                this.mReadError = "bad process count: " + NPROC;
                return;
            }
            while (NPROC > 0) {
                NPROC--;
                String procName = readCommonString(in, version);
                if (procName == null) {
                    this.mReadError = "bad process name";
                    return;
                }
                int NUID = in.readInt();
                if (NUID < 0) {
                    this.mReadError = "bad uid count: " + NUID;
                    return;
                }
                while (NUID > 0) {
                    NUID--;
                    int uid = in.readInt();
                    if (uid < 0) {
                        this.mReadError = "bad uid: " + uid;
                        return;
                    }
                    String pkgName = readCommonString(in, version);
                    if (pkgName == null) {
                        this.mReadError = "bad process package name";
                        return;
                    }
                    ProcessState proc = hadData ? this.mProcesses.get(procName, uid) : null;
                    if (proc != null) {
                        if (!proc.readFromParcel(in, false)) {
                            return;
                        }
                    } else {
                        proc = new ProcessState(this, pkgName, uid, procName);
                        if (!proc.readFromParcel(in, true)) {
                            return;
                        }
                    }
                    this.mProcesses.put(procName, uid, proc);
                }
            }
            int NPKG = in.readInt();
            if (NPKG < 0) {
                this.mReadError = "bad package count: " + NPKG;
                return;
            }
            while (NPKG > 0) {
                NPKG--;
                String pkgName2 = readCommonString(in, version);
                if (pkgName2 == null) {
                    this.mReadError = "bad package name";
                    return;
                }
                int NUID2 = in.readInt();
                if (NUID2 < 0) {
                    this.mReadError = "bad uid count: " + NUID2;
                    return;
                }
                while (NUID2 > 0) {
                    NUID2--;
                    int uid2 = in.readInt();
                    if (uid2 < 0) {
                        this.mReadError = "bad uid: " + uid2;
                        return;
                    }
                    PackageState pkgState = new PackageState(pkgName2, uid2);
                    this.mPackages.put(pkgName2, uid2, pkgState);
                    int NPROCS = in.readInt();
                    if (NPROCS < 0) {
                        this.mReadError = "bad package process count: " + NPROCS;
                        return;
                    }
                    while (NPROCS > 0) {
                        NPROCS--;
                        String procName2 = readCommonString(in, version);
                        if (procName2 == null) {
                            this.mReadError = "bad package process name";
                            return;
                        }
                        int hasProc = in.readInt();
                        ProcessState commonProc = this.mProcesses.get(procName2, uid2);
                        if (commonProc == null) {
                            this.mReadError = "no common proc: " + procName2;
                            return;
                        } else if (hasProc != 0) {
                            ProcessState proc2 = hadData ? pkgState.mProcesses.get(procName2) : null;
                            if (proc2 != null) {
                                if (!proc2.readFromParcel(in, false)) {
                                    return;
                                }
                            } else {
                                proc2 = new ProcessState(commonProc, pkgName2, uid2, procName2, 0L);
                                if (!proc2.readFromParcel(in, true)) {
                                    return;
                                }
                            }
                            pkgState.mProcesses.put(procName2, proc2);
                        } else {
                            pkgState.mProcesses.put(procName2, commonProc);
                        }
                    }
                    int NSRVS = in.readInt();
                    if (NSRVS < 0) {
                        this.mReadError = "bad package service count: " + NSRVS;
                        return;
                    }
                    while (NSRVS > 0) {
                        NSRVS--;
                        String serviceName = in.readString();
                        if (serviceName == null) {
                            this.mReadError = "bad package service name";
                            return;
                        }
                        String processName = version > 9 ? readCommonString(in, version) : null;
                        ServiceState serv = hadData ? pkgState.mServices.get(serviceName) : null;
                        if (serv == null) {
                            serv = new ServiceState(this, pkgName2, serviceName, processName, null);
                        }
                        if (!serv.readFromParcel(in)) {
                            return;
                        }
                        pkgState.mServices.put(serviceName, serv);
                    }
                }
            }
            this.mIndexToCommonString = null;
        }
    }

    int addLongData(int index, int type, int num) {
        int tableLen = this.mAddLongTable != null ? this.mAddLongTable.length : 0;
        if (this.mAddLongTableSize >= tableLen) {
            int newSize = ArrayUtils.idealIntArraySize(tableLen + 1);
            int[] newTable = new int[newSize];
            if (tableLen > 0) {
                System.arraycopy(this.mAddLongTable, 0, newTable, 0, tableLen);
            }
            this.mAddLongTable = newTable;
        }
        if (this.mAddLongTableSize > 0 && this.mAddLongTableSize - index != 0) {
            System.arraycopy(this.mAddLongTable, index, this.mAddLongTable, index + 1, this.mAddLongTableSize - index);
        }
        int off = allocLongData(num);
        this.mAddLongTable[index] = type | off;
        this.mAddLongTableSize++;
        return off;
    }

    int allocLongData(int num) {
        int whichLongs = this.mLongs.size() - 1;
        long[] longs = this.mLongs.get(whichLongs);
        if (this.mNextLong + num > longs.length) {
            long[] longs2 = new long[4096];
            this.mLongs.add(longs2);
            whichLongs++;
            this.mNextLong = 0;
        }
        int off = (whichLongs << OFFSET_ARRAY_SHIFT) | (this.mNextLong << OFFSET_INDEX_SHIFT);
        this.mNextLong += num;
        return off;
    }

    boolean validateLongOffset(int off) {
        int arr = (off >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK;
        if (arr >= this.mLongs.size()) {
            return false;
        }
        int idx = (off >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK;
        if (idx >= 4096) {
            return false;
        }
        return true;
    }

    static String printLongOffset(int off) {
        StringBuilder sb = new StringBuilder(16);
        sb.append(FullBackup.APK_TREE_TOKEN);
        sb.append((off >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK);
        sb.append("i");
        sb.append((off >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK);
        sb.append("t");
        sb.append((off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK);
        return sb.toString();
    }

    void setLong(int off, int index, long value) {
        long[] longs = this.mLongs.get((off >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK);
        longs[index + ((off >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK)] = value;
    }

    long getLong(int off, int index) {
        long[] longs = this.mLongs.get((off >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK);
        return longs[index + ((off >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK)];
    }

    static int binarySearch(int[] array, int size, int value) {
        int lo = 0;
        int hi = size - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int midVal = (array[mid] >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;
            }
        }
        return lo ^ (-1);
    }

    public PackageState getPackageStateLocked(String packageName, int uid) {
        PackageState as = this.mPackages.get(packageName, uid);
        if (as != null) {
            return as;
        }
        PackageState as2 = new PackageState(packageName, uid);
        this.mPackages.put(packageName, uid, as2);
        return as2;
    }

    public ProcessState getProcessStateLocked(String packageName, int uid, String processName) {
        ProcessState ps;
        PackageState pkgState = getPackageStateLocked(packageName, uid);
        ProcessState ps2 = pkgState.mProcesses.get(processName);
        if (ps2 != null) {
            return ps2;
        }
        ProcessState commonProc = this.mProcesses.get(processName, uid);
        if (commonProc == null) {
            commonProc = new ProcessState(this, packageName, uid, processName);
            this.mProcesses.put(processName, uid, commonProc);
        }
        if (!commonProc.mMultiPackage) {
            if (packageName.equals(commonProc.mPackage)) {
                ps = commonProc;
            } else {
                commonProc.mMultiPackage = true;
                long now = SystemClock.uptimeMillis();
                PackageState commonPkgState = getPackageStateLocked(commonProc.mPackage, uid);
                if (commonPkgState != null) {
                    ProcessState cloned = commonProc.clone(commonProc.mPackage, now);
                    commonPkgState.mProcesses.put(commonProc.mName, cloned);
                    for (int i = commonPkgState.mServices.size() - 1; i >= 0; i--) {
                        ServiceState ss = commonPkgState.mServices.valueAt(i);
                        if (ss.mProc == commonProc) {
                            ss.mProc = cloned;
                        }
                    }
                } else {
                    Slog.w(TAG, "Cloning proc state: no package state " + commonProc.mPackage + Separators.SLASH + uid + " for proc " + commonProc.mName);
                }
                ps = new ProcessState(commonProc, packageName, uid, processName, now);
            }
        } else {
            ps = new ProcessState(commonProc, packageName, uid, processName, SystemClock.uptimeMillis());
        }
        pkgState.mProcesses.put(processName, ps);
        return ps;
    }

    public ServiceState getServiceStateLocked(String packageName, int uid, String processName, String className) {
        PackageState as = getPackageStateLocked(packageName, uid);
        ServiceState ss = as.mServices.get(className);
        if (ss != null) {
            return ss;
        }
        ProcessState ps = processName != null ? getProcessStateLocked(packageName, uid, processName) : null;
        ServiceState ss2 = new ServiceState(this, packageName, className, processName, ps);
        as.mServices.put(className, ss2);
        return ss2;
    }

    private void dumpProcessInternalLocked(PrintWriter pw, String prefix, ProcessState proc, boolean dumpAll) {
        if (dumpAll) {
            pw.print(prefix);
            pw.print("myID=");
            pw.print(Integer.toHexString(System.identityHashCode(proc)));
            pw.print(" mCommonProcess=");
            pw.print(Integer.toHexString(System.identityHashCode(proc.mCommonProcess)));
            pw.print(" mPackage=");
            pw.println(proc.mPackage);
            if (proc.mMultiPackage) {
                pw.print(prefix);
                pw.print("mMultiPackage=");
                pw.println(proc.mMultiPackage);
            }
            if (proc != proc.mCommonProcess) {
                pw.print(prefix);
                pw.print("Common Proc: ");
                pw.print(proc.mCommonProcess.mName);
                pw.print(Separators.SLASH);
                pw.print(proc.mCommonProcess.mUid);
                pw.print(" pkg=");
                pw.println(proc.mCommonProcess.mPackage);
            }
        }
        pw.print(prefix);
        pw.print("mActive=");
        pw.println(proc.mActive);
        if (proc.mDead) {
            pw.print(prefix);
            pw.print("mDead=");
            pw.println(proc.mDead);
        }
        pw.print(prefix);
        pw.print("mNumActiveServices=");
        pw.print(proc.mNumActiveServices);
        pw.print(" mNumStartedServices=");
        pw.println(proc.mNumStartedServices);
    }

    public void dumpLocked(PrintWriter pw, String reqPackage, long now, boolean dumpSummary, boolean dumpAll, boolean activeOnly) {
        long totalTime = dumpSingleTime(null, null, this.mMemFactorDurations, this.mMemFactor, this.mStartTime, now);
        ArrayMap<String, SparseArray<PackageState>> pkgMap = this.mPackages.getMap();
        boolean printedHeader = false;
        for (int ip = 0; ip < pkgMap.size(); ip++) {
            String pkgName = pkgMap.keyAt(ip);
            if (reqPackage == null || reqPackage.equals(pkgName)) {
                SparseArray<PackageState> uids = pkgMap.valueAt(ip);
                for (int iu = 0; iu < uids.size(); iu++) {
                    int uid = uids.keyAt(iu);
                    PackageState pkgState = uids.valueAt(iu);
                    int NPROCS = pkgState.mProcesses.size();
                    int NSRVS = pkgState.mServices.size();
                    if (NPROCS > 0 || NSRVS > 0) {
                        if (!printedHeader) {
                            pw.println("Per-Package Stats:");
                            printedHeader = true;
                        }
                        pw.print("  * ");
                        pw.print(pkgName);
                        pw.print(" / ");
                        UserHandle.formatUid(pw, uid);
                        pw.println(Separators.COLON);
                    }
                    if (!dumpSummary || dumpAll) {
                        for (int iproc = 0; iproc < NPROCS; iproc++) {
                            ProcessState proc = pkgState.mProcesses.valueAt(iproc);
                            if (activeOnly && !proc.isInUse()) {
                                pw.print("      (Not active: ");
                                pw.print(pkgState.mProcesses.keyAt(iproc));
                                pw.println(Separators.RPAREN);
                            } else {
                                pw.print("      Process ");
                                pw.print(pkgState.mProcesses.keyAt(iproc));
                                pw.print(" (");
                                pw.print(proc.mDurationsTableSize);
                                pw.print(" entries)");
                                pw.println(Separators.COLON);
                                dumpProcessState(pw, "        ", proc, ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES, now);
                                dumpProcessPss(pw, "        ", proc, ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES);
                                dumpProcessInternalLocked(pw, "        ", proc, dumpAll);
                            }
                        }
                    } else {
                        ArrayList<ProcessState> procs = new ArrayList<>();
                        for (int iproc2 = 0; iproc2 < NPROCS; iproc2++) {
                            ProcessState proc2 = pkgState.mProcesses.valueAt(iproc2);
                            if (!activeOnly || proc2.isInUse()) {
                                procs.add(proc2);
                            }
                        }
                        dumpProcessSummaryLocked(pw, "      ", procs, ALL_SCREEN_ADJ, ALL_MEM_ADJ, NON_CACHED_PROC_STATES, now, totalTime);
                    }
                    for (int isvc = 0; isvc < NSRVS; isvc++) {
                        ServiceState svc = pkgState.mServices.valueAt(isvc);
                        if (activeOnly && !svc.isInUse()) {
                            pw.print("      (Not active: ");
                            pw.print(pkgState.mServices.keyAt(isvc));
                            pw.println(Separators.RPAREN);
                        } else {
                            if (dumpAll) {
                                pw.print("      Service ");
                            } else {
                                pw.print("      * ");
                            }
                            pw.print(pkgState.mServices.keyAt(isvc));
                            pw.println(Separators.COLON);
                            pw.print("        Process: ");
                            pw.println(svc.mProcessName);
                            dumpServiceStats(pw, "        ", "          ", "    ", "Running", svc, svc.mRunCount, 0, svc.mRunState, svc.mRunStartTime, now, totalTime, !dumpSummary || dumpAll);
                            dumpServiceStats(pw, "        ", "          ", "    ", "Started", svc, svc.mStartedCount, 1, svc.mStartedState, svc.mStartedStartTime, now, totalTime, !dumpSummary || dumpAll);
                            dumpServiceStats(pw, "        ", "          ", "      ", "Bound", svc, svc.mBoundCount, 2, svc.mBoundState, svc.mBoundStartTime, now, totalTime, !dumpSummary || dumpAll);
                            dumpServiceStats(pw, "        ", "          ", "  ", "Executing", svc, svc.mExecCount, 3, svc.mExecState, svc.mExecStartTime, now, totalTime, !dumpSummary || dumpAll);
                            if (dumpAll && svc.mOwner != null) {
                                pw.print("        mOwner=");
                                pw.println(svc.mOwner);
                            }
                        }
                    }
                }
            }
        }
        if (reqPackage == null) {
            ArrayMap<String, SparseArray<ProcessState>> procMap = this.mProcesses.getMap();
            boolean printedHeader2 = false;
            int numShownProcs = 0;
            int numTotalProcs = 0;
            for (int ip2 = 0; ip2 < procMap.size(); ip2++) {
                String procName = procMap.keyAt(ip2);
                SparseArray<ProcessState> uids2 = procMap.valueAt(ip2);
                for (int iu2 = 0; iu2 < uids2.size(); iu2++) {
                    int uid2 = uids2.keyAt(iu2);
                    numTotalProcs++;
                    ProcessState proc3 = uids2.valueAt(iu2);
                    if (proc3.mDurationsTableSize != 0 || proc3.mCurState != -1 || proc3.mPssTableSize != 0) {
                        numShownProcs++;
                        if (!printedHeader2) {
                            pw.println();
                            pw.println("Per-Process Stats:");
                            printedHeader2 = true;
                        }
                        if (activeOnly && !proc3.isInUse()) {
                            pw.print("      (Not active: ");
                            pw.print(procName);
                            pw.println(Separators.RPAREN);
                        } else {
                            pw.print("  * ");
                            pw.print(procName);
                            pw.print(" / ");
                            UserHandle.formatUid(pw, uid2);
                            pw.print(" (");
                            pw.print(proc3.mDurationsTableSize);
                            pw.print(" entries)");
                            pw.println(Separators.COLON);
                            dumpProcessState(pw, "        ", proc3, ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES, now);
                            dumpProcessPss(pw, "        ", proc3, ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES);
                            if (dumpAll) {
                                dumpProcessInternalLocked(pw, "        ", proc3, dumpAll);
                            }
                        }
                    }
                }
            }
            if (dumpAll) {
                pw.println();
                pw.print("  Total procs: ");
                pw.print(numShownProcs);
                pw.print(" shown of ");
                pw.print(numTotalProcs);
                pw.println(" total");
            }
            pw.println();
            if (dumpSummary) {
                pw.println("Summary:");
                dumpSummaryLocked(pw, reqPackage, now, activeOnly);
            } else {
                dumpTotalsLocked(pw, now);
            }
        } else {
            pw.println();
            dumpTotalsLocked(pw, now);
        }
        if (dumpAll) {
            pw.println();
            pw.println("Internal state:");
            pw.print("  Num long arrays: ");
            pw.println(this.mLongs.size());
            pw.print("  Next long entry: ");
            pw.println(this.mNextLong);
            pw.print("  mRunning=");
            pw.println(this.mRunning);
        }
    }

    public static long dumpSingleServiceTime(PrintWriter pw, String prefix, ServiceState service, int serviceType, int curState, long curStartTime, long now) {
        long totalTime = 0;
        int printedScreen = -1;
        int iscreen = 0;
        while (iscreen < 8) {
            int printedMem = -1;
            int imem = 0;
            while (imem < 4) {
                int state = imem + iscreen;
                long time = service.getDuration(serviceType, curState, curStartTime, state, now);
                String running = "";
                if (curState == state && pw != null) {
                    running = " (running)";
                }
                if (time != 0) {
                    if (pw != null) {
                        pw.print(prefix);
                        printScreenLabel(pw, printedScreen != iscreen ? iscreen : -1);
                        printedScreen = iscreen;
                        printMemLabel(pw, printedMem != imem ? imem : -1);
                        printedMem = imem;
                        TimeUtils.formatDuration(time, pw);
                        pw.println(running);
                    }
                    totalTime += time;
                }
                imem++;
            }
            iscreen += 4;
        }
        if (totalTime != 0 && pw != null) {
            pw.print(prefix);
            printScreenLabel(pw, -1);
            pw.print("TOTAL: ");
            TimeUtils.formatDuration(totalTime, pw);
            pw.println();
        }
        return totalTime;
    }

    void dumpServiceStats(PrintWriter pw, String prefix, String prefixInner, String headerPrefix, String header, ServiceState service, int count, int serviceType, int state, long startTime, long now, long totalTime, boolean dumpAll) {
        if (count != 0) {
            if (dumpAll) {
                pw.print(prefix);
                pw.print(header);
                pw.print(" op count ");
                pw.print(count);
                pw.println(Separators.COLON);
                dumpSingleServiceTime(pw, prefixInner, service, serviceType, state, startTime, now);
                return;
            }
            long myTime = dumpSingleServiceTime(null, null, service, serviceType, state, startTime, now);
            pw.print(prefix);
            pw.print(headerPrefix);
            pw.print(header);
            pw.print(" count ");
            pw.print(count);
            pw.print(" / time ");
            printPercent(pw, myTime / totalTime);
            pw.println();
        }
    }

    public void dumpSummaryLocked(PrintWriter pw, String reqPackage, long now, boolean activeOnly) {
        long totalTime = dumpSingleTime(null, null, this.mMemFactorDurations, this.mMemFactor, this.mStartTime, now);
        dumpFilteredSummaryLocked(pw, null, "  ", ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES, NON_CACHED_PROC_STATES, now, totalTime, reqPackage, activeOnly);
        pw.println();
        dumpTotalsLocked(pw, now);
    }

    void dumpTotalsLocked(PrintWriter pw, long now) {
        pw.println("Run time Stats:");
        dumpSingleTime(pw, "  ", this.mMemFactorDurations, this.mMemFactor, this.mStartTime, now);
        pw.println();
        pw.print("          Start time: ");
        pw.print(DateFormat.format("yyyy-MM-dd HH:mm:ss", this.mTimePeriodStartClock));
        pw.println();
        pw.print("  Total elapsed time: ");
        TimeUtils.formatDuration((this.mRunning ? SystemClock.elapsedRealtime() : this.mTimePeriodEndRealtime) - this.mTimePeriodStartRealtime, pw);
        boolean partial = true;
        if ((this.mFlags & 2) != 0) {
            pw.print(" (shutdown)");
            partial = false;
        }
        if ((this.mFlags & 4) != 0) {
            pw.print(" (sysprops)");
            partial = false;
        }
        if ((this.mFlags & 1) != 0) {
            pw.print(" (complete)");
            partial = false;
        }
        if (partial) {
            pw.print(" (partial)");
        }
        pw.print(' ');
        pw.print(this.mRuntime);
        pw.print(' ');
        pw.print(this.mWebView);
        pw.println();
    }

    void dumpFilteredSummaryLocked(PrintWriter pw, String header, String prefix, int[] screenStates, int[] memStates, int[] procStates, int[] sortProcStates, long now, long totalTime, String reqPackage, boolean activeOnly) {
        ArrayList<ProcessState> procs = collectProcessesLocked(screenStates, memStates, procStates, sortProcStates, now, reqPackage, activeOnly);
        if (procs.size() > 0) {
            if (header != null) {
                pw.println();
                pw.println(header);
            }
            dumpProcessSummaryLocked(pw, prefix, procs, screenStates, memStates, sortProcStates, now, totalTime);
        }
    }

    public ArrayList<ProcessState> collectProcessesLocked(int[] screenStates, int[] memStates, int[] procStates, int[] sortProcStates, long now, String reqPackage, boolean activeOnly) {
        ArraySet<ProcessState> foundProcs = new ArraySet<>();
        ArrayMap<String, SparseArray<PackageState>> pkgMap = this.mPackages.getMap();
        for (int ip = 0; ip < pkgMap.size(); ip++) {
            if (reqPackage == null || reqPackage.equals(pkgMap.keyAt(ip))) {
                SparseArray<PackageState> procs = pkgMap.valueAt(ip);
                for (int iu = 0; iu < procs.size(); iu++) {
                    PackageState state = procs.valueAt(iu);
                    for (int iproc = 0; iproc < state.mProcesses.size(); iproc++) {
                        ProcessState proc = state.mProcesses.valueAt(iproc);
                        if (!activeOnly || proc.isInUse()) {
                            foundProcs.add(proc.mCommonProcess);
                        }
                    }
                }
            }
        }
        ArrayList<ProcessState> outProcs = new ArrayList<>(foundProcs.size());
        for (int i = 0; i < foundProcs.size(); i++) {
            ProcessState proc2 = foundProcs.valueAt(i);
            if (computeProcessTimeLocked(proc2, screenStates, memStates, procStates, now) > 0) {
                outProcs.add(proc2);
                if (procStates != sortProcStates) {
                    computeProcessTimeLocked(proc2, screenStates, memStates, sortProcStates, now);
                }
            }
        }
        Collections.sort(outProcs, new Comparator<ProcessState>() { // from class: com.android.internal.app.ProcessStats.2
            @Override // java.util.Comparator
            public int compare(ProcessState lhs, ProcessState rhs) {
                if (lhs.mTmpTotalTime < rhs.mTmpTotalTime) {
                    return -1;
                }
                if (lhs.mTmpTotalTime > rhs.mTmpTotalTime) {
                    return 1;
                }
                return 0;
            }
        });
        return outProcs;
    }

    String collapseString(String pkgName, String itemName) {
        if (itemName.startsWith(pkgName)) {
            int ITEMLEN = itemName.length();
            int PKGLEN = pkgName.length();
            if (ITEMLEN == PKGLEN) {
                return "";
            }
            if (ITEMLEN >= PKGLEN && itemName.charAt(PKGLEN) == '.') {
                return itemName.substring(PKGLEN);
            }
        }
        return itemName;
    }

    public void dumpCheckinLocked(PrintWriter pw, String reqPackage) {
        long now = SystemClock.uptimeMillis();
        ArrayMap<String, SparseArray<PackageState>> pkgMap = this.mPackages.getMap();
        pw.println("vers,3");
        pw.print("period,");
        pw.print(this.mTimePeriodStartClockStr);
        pw.print(Separators.COMMA);
        pw.print(this.mTimePeriodStartRealtime);
        pw.print(Separators.COMMA);
        pw.print(this.mRunning ? SystemClock.elapsedRealtime() : this.mTimePeriodEndRealtime);
        boolean partial = true;
        if ((this.mFlags & 2) != 0) {
            pw.print(",shutdown");
            partial = false;
        }
        if ((this.mFlags & 4) != 0) {
            pw.print(",sysprops");
            partial = false;
        }
        if ((this.mFlags & 1) != 0) {
            pw.print(",complete");
            partial = false;
        }
        if (partial) {
            pw.print(",partial");
        }
        pw.println();
        pw.print("config,");
        pw.print(this.mRuntime);
        pw.print(',');
        pw.println(this.mWebView);
        for (int ip = 0; ip < pkgMap.size(); ip++) {
            String pkgName = pkgMap.keyAt(ip);
            if (reqPackage == null || reqPackage.equals(pkgName)) {
                SparseArray<PackageState> uids = pkgMap.valueAt(ip);
                for (int iu = 0; iu < uids.size(); iu++) {
                    int uid = uids.keyAt(iu);
                    PackageState pkgState = uids.valueAt(iu);
                    int NPROCS = pkgState.mProcesses.size();
                    int NSRVS = pkgState.mServices.size();
                    for (int iproc = 0; iproc < NPROCS; iproc++) {
                        ProcessState proc = pkgState.mProcesses.valueAt(iproc);
                        pw.print("pkgproc,");
                        pw.print(pkgName);
                        pw.print(Separators.COMMA);
                        pw.print(uid);
                        pw.print(Separators.COMMA);
                        pw.print(collapseString(pkgName, pkgState.mProcesses.keyAt(iproc)));
                        dumpAllProcessStateCheckin(pw, proc, now);
                        pw.println();
                        if (proc.mPssTableSize > 0) {
                            pw.print("pkgpss,");
                            pw.print(pkgName);
                            pw.print(Separators.COMMA);
                            pw.print(uid);
                            pw.print(Separators.COMMA);
                            pw.print(collapseString(pkgName, pkgState.mProcesses.keyAt(iproc)));
                            dumpAllProcessPssCheckin(pw, proc);
                            pw.println();
                        }
                        if (proc.mNumExcessiveWake > 0 || proc.mNumExcessiveCpu > 0 || proc.mNumCachedKill > 0) {
                            pw.print("pkgkills,");
                            pw.print(pkgName);
                            pw.print(Separators.COMMA);
                            pw.print(uid);
                            pw.print(Separators.COMMA);
                            pw.print(collapseString(pkgName, pkgState.mProcesses.keyAt(iproc)));
                            pw.print(Separators.COMMA);
                            pw.print(proc.mNumExcessiveWake);
                            pw.print(Separators.COMMA);
                            pw.print(proc.mNumExcessiveCpu);
                            pw.print(Separators.COMMA);
                            pw.print(proc.mNumCachedKill);
                            pw.print(Separators.COMMA);
                            pw.print(proc.mMinCachedKillPss);
                            pw.print(Separators.COLON);
                            pw.print(proc.mAvgCachedKillPss);
                            pw.print(Separators.COLON);
                            pw.print(proc.mMaxCachedKillPss);
                            pw.println();
                        }
                    }
                    for (int isvc = 0; isvc < NSRVS; isvc++) {
                        String serviceName = collapseString(pkgName, pkgState.mServices.keyAt(isvc));
                        ServiceState svc = pkgState.mServices.valueAt(isvc);
                        dumpServiceTimeCheckin(pw, "pkgsvc-run", pkgName, uid, serviceName, svc, 0, svc.mRunCount, svc.mRunState, svc.mRunStartTime, now);
                        dumpServiceTimeCheckin(pw, "pkgsvc-start", pkgName, uid, serviceName, svc, 1, svc.mStartedCount, svc.mStartedState, svc.mStartedStartTime, now);
                        dumpServiceTimeCheckin(pw, "pkgsvc-bound", pkgName, uid, serviceName, svc, 2, svc.mBoundCount, svc.mBoundState, svc.mBoundStartTime, now);
                        dumpServiceTimeCheckin(pw, "pkgsvc-exec", pkgName, uid, serviceName, svc, 3, svc.mExecCount, svc.mExecState, svc.mExecStartTime, now);
                    }
                }
            }
        }
        ArrayMap<String, SparseArray<ProcessState>> procMap = this.mProcesses.getMap();
        for (int ip2 = 0; ip2 < procMap.size(); ip2++) {
            String procName = procMap.keyAt(ip2);
            SparseArray<ProcessState> uids2 = procMap.valueAt(ip2);
            for (int iu2 = 0; iu2 < uids2.size(); iu2++) {
                int uid2 = uids2.keyAt(iu2);
                ProcessState procState = uids2.valueAt(iu2);
                if (procState.mDurationsTableSize > 0) {
                    pw.print("proc,");
                    pw.print(procName);
                    pw.print(Separators.COMMA);
                    pw.print(uid2);
                    dumpAllProcessStateCheckin(pw, procState, now);
                    pw.println();
                }
                if (procState.mPssTableSize > 0) {
                    pw.print("pss,");
                    pw.print(procName);
                    pw.print(Separators.COMMA);
                    pw.print(uid2);
                    dumpAllProcessPssCheckin(pw, procState);
                    pw.println();
                }
                if (procState.mNumExcessiveWake > 0 || procState.mNumExcessiveCpu > 0 || procState.mNumCachedKill > 0) {
                    pw.print("kills,");
                    pw.print(procName);
                    pw.print(Separators.COMMA);
                    pw.print(uid2);
                    pw.print(Separators.COMMA);
                    pw.print(procState.mNumExcessiveWake);
                    pw.print(Separators.COMMA);
                    pw.print(procState.mNumExcessiveCpu);
                    pw.print(Separators.COMMA);
                    pw.print(procState.mNumCachedKill);
                    pw.print(Separators.COMMA);
                    pw.print(procState.mMinCachedKillPss);
                    pw.print(Separators.COLON);
                    pw.print(procState.mAvgCachedKillPss);
                    pw.print(Separators.COLON);
                    pw.print(procState.mMaxCachedKillPss);
                    pw.println();
                }
            }
        }
        pw.print("total");
        dumpAdjTimesCheckin(pw, Separators.COMMA, this.mMemFactorDurations, this.mMemFactor, this.mStartTime, now);
        pw.println();
    }

    /* loaded from: ProcessStats$DurationsTable.class */
    public static class DurationsTable {
        public final ProcessStats mStats;
        public final String mName;
        public int[] mDurationsTable;
        public int mDurationsTableSize;

        public DurationsTable(ProcessStats stats, String name) {
            this.mStats = stats;
            this.mName = name;
        }

        void copyDurationsTo(DurationsTable other) {
            if (this.mDurationsTable != null) {
                this.mStats.mAddLongTable = new int[this.mDurationsTable.length];
                this.mStats.mAddLongTableSize = 0;
                for (int i = 0; i < this.mDurationsTableSize; i++) {
                    int origEnt = this.mDurationsTable[i];
                    int type = (origEnt >> ProcessStats.OFFSET_TYPE_SHIFT) & ProcessStats.OFFSET_TYPE_MASK;
                    int newOff = this.mStats.addLongData(i, type, 1);
                    this.mStats.mAddLongTable[i] = newOff | type;
                    this.mStats.setLong(newOff, 0, this.mStats.getLong(origEnt, 0));
                }
                other.mDurationsTable = this.mStats.mAddLongTable;
                other.mDurationsTableSize = this.mStats.mAddLongTableSize;
                return;
            }
            other.mDurationsTable = null;
            other.mDurationsTableSize = 0;
        }

        void addDurations(DurationsTable other) {
            for (int i = 0; i < other.mDurationsTableSize; i++) {
                int ent = other.mDurationsTable[i];
                int state = (ent >> ProcessStats.OFFSET_TYPE_SHIFT) & ProcessStats.OFFSET_TYPE_MASK;
                addDuration(state, other.mStats.getLong(ent, 0));
            }
        }

        void resetDurationsSafely() {
            this.mDurationsTable = null;
            this.mDurationsTableSize = 0;
        }

        void writeDurationsToParcel(Parcel out) {
            out.writeInt(this.mDurationsTableSize);
            for (int i = 0; i < this.mDurationsTableSize; i++) {
                out.writeInt(this.mDurationsTable[i]);
            }
        }

        boolean readDurationsFromParcel(Parcel in) {
            this.mDurationsTable = this.mStats.readTableFromParcel(in, this.mName, "durations");
            if (this.mDurationsTable == ProcessStats.BAD_TABLE) {
                return false;
            }
            this.mDurationsTableSize = this.mDurationsTable != null ? this.mDurationsTable.length : 0;
            return true;
        }

        void addDuration(int state, long dur) {
            int off;
            int idx = ProcessStats.binarySearch(this.mDurationsTable, this.mDurationsTableSize, state);
            if (idx >= 0) {
                off = this.mDurationsTable[idx];
            } else {
                this.mStats.mAddLongTable = this.mDurationsTable;
                this.mStats.mAddLongTableSize = this.mDurationsTableSize;
                off = this.mStats.addLongData(idx ^ (-1), state, 1);
                this.mDurationsTable = this.mStats.mAddLongTable;
                this.mDurationsTableSize = this.mStats.mAddLongTableSize;
            }
            long[] longs = this.mStats.mLongs.get((off >> ProcessStats.OFFSET_ARRAY_SHIFT) & ProcessStats.OFFSET_ARRAY_MASK);
            int i = (off >> ProcessStats.OFFSET_INDEX_SHIFT) & ProcessStats.OFFSET_INDEX_MASK;
            longs[i] = longs[i] + dur;
        }

        long getDuration(int state, long now) {
            int idx = ProcessStats.binarySearch(this.mDurationsTable, this.mDurationsTableSize, state);
            if (idx >= 0) {
                return this.mStats.getLong(this.mDurationsTable[idx], 0);
            }
            return 0L;
        }
    }

    /* loaded from: ProcessStats$ProcessState.class */
    public static final class ProcessState extends DurationsTable {
        public final ProcessState mCommonProcess;
        public final String mPackage;
        public final int mUid;
        int mCurState;
        long mStartTime;
        int mLastPssState;
        long mLastPssTime;
        int[] mPssTable;
        int mPssTableSize;
        boolean mActive;
        int mNumActiveServices;
        int mNumStartedServices;
        int mNumExcessiveWake;
        int mNumExcessiveCpu;
        int mNumCachedKill;
        long mMinCachedKillPss;
        long mAvgCachedKillPss;
        long mMaxCachedKillPss;
        boolean mMultiPackage;
        boolean mDead;
        public long mTmpTotalTime;

        public ProcessState(ProcessStats processStats, String pkg, int uid, String name) {
            super(processStats, name);
            this.mCurState = -1;
            this.mLastPssState = -1;
            this.mCommonProcess = this;
            this.mPackage = pkg;
            this.mUid = uid;
        }

        public ProcessState(ProcessState commonProcess, String pkg, int uid, String name, long now) {
            super(commonProcess.mStats, name);
            this.mCurState = -1;
            this.mLastPssState = -1;
            this.mCommonProcess = commonProcess;
            this.mPackage = pkg;
            this.mUid = uid;
            this.mCurState = commonProcess.mCurState;
            this.mStartTime = now;
        }

        ProcessState clone(String pkg, long now) {
            ProcessState pnew = new ProcessState(this, pkg, this.mUid, this.mName, now);
            copyDurationsTo(pnew);
            if (this.mPssTable != null) {
                this.mStats.mAddLongTable = new int[this.mPssTable.length];
                this.mStats.mAddLongTableSize = 0;
                for (int i = 0; i < this.mPssTableSize; i++) {
                    int origEnt = this.mPssTable[i];
                    int type = (origEnt >> ProcessStats.OFFSET_TYPE_SHIFT) & ProcessStats.OFFSET_TYPE_MASK;
                    int newOff = this.mStats.addLongData(i, type, 7);
                    this.mStats.mAddLongTable[i] = newOff | type;
                    for (int j = 0; j < 7; j++) {
                        this.mStats.setLong(newOff, j, this.mStats.getLong(origEnt, j));
                    }
                }
                pnew.mPssTable = this.mStats.mAddLongTable;
                pnew.mPssTableSize = this.mStats.mAddLongTableSize;
            }
            pnew.mNumExcessiveWake = this.mNumExcessiveWake;
            pnew.mNumExcessiveCpu = this.mNumExcessiveCpu;
            pnew.mNumCachedKill = this.mNumCachedKill;
            pnew.mMinCachedKillPss = this.mMinCachedKillPss;
            pnew.mAvgCachedKillPss = this.mAvgCachedKillPss;
            pnew.mMaxCachedKillPss = this.mMaxCachedKillPss;
            pnew.mActive = this.mActive;
            pnew.mNumActiveServices = this.mNumActiveServices;
            pnew.mNumStartedServices = this.mNumStartedServices;
            return pnew;
        }

        void add(ProcessState other) {
            addDurations(other);
            for (int i = 0; i < other.mPssTableSize; i++) {
                int ent = other.mPssTable[i];
                int state = (ent >> ProcessStats.OFFSET_TYPE_SHIFT) & ProcessStats.OFFSET_TYPE_MASK;
                addPss(state, (int) other.mStats.getLong(ent, 0), other.mStats.getLong(ent, 1), other.mStats.getLong(ent, 2), other.mStats.getLong(ent, 3), other.mStats.getLong(ent, 4), other.mStats.getLong(ent, 5), other.mStats.getLong(ent, 6));
            }
            this.mNumExcessiveWake += other.mNumExcessiveWake;
            this.mNumExcessiveCpu += other.mNumExcessiveCpu;
            if (other.mNumCachedKill > 0) {
                addCachedKill(other.mNumCachedKill, other.mMinCachedKillPss, other.mAvgCachedKillPss, other.mMaxCachedKillPss);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r3v0, types: [com.android.internal.app.ProcessStats$ProcessState] */
        void resetSafely(long now) {
            resetDurationsSafely();
            this.mStartTime = now;
            this.mLastPssState = -1;
            this.mLastPssTime = 0L;
            this.mPssTable = null;
            this.mPssTableSize = 0;
            this.mNumExcessiveWake = 0;
            this.mNumExcessiveCpu = 0;
            this.mNumCachedKill = 0;
            ?? r3 = 0;
            this.mMaxCachedKillPss = 0L;
            this.mAvgCachedKillPss = 0L;
            r3.mMinCachedKillPss = this;
        }

        void makeDead() {
            this.mDead = true;
        }

        private void ensureNotDead() {
            if (!this.mDead) {
                return;
            }
            Slog.wtfStack(ProcessStats.TAG, "ProcessState dead: name=" + this.mName + " pkg=" + this.mPackage + " uid=" + this.mUid + " common.name=" + this.mCommonProcess.mName);
        }

        void writeToParcel(Parcel out, long now) {
            out.writeInt(this.mMultiPackage ? 1 : 0);
            writeDurationsToParcel(out);
            out.writeInt(this.mPssTableSize);
            for (int i = 0; i < this.mPssTableSize; i++) {
                out.writeInt(this.mPssTable[i]);
            }
            out.writeInt(this.mNumExcessiveWake);
            out.writeInt(this.mNumExcessiveCpu);
            out.writeInt(this.mNumCachedKill);
            if (this.mNumCachedKill > 0) {
                out.writeLong(this.mMinCachedKillPss);
                out.writeLong(this.mAvgCachedKillPss);
                out.writeLong(this.mMaxCachedKillPss);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r3v2, types: [com.android.internal.app.ProcessStats$ProcessState] */
        boolean readFromParcel(Parcel in, boolean fully) {
            boolean multiPackage = in.readInt() != 0;
            if (fully) {
                this.mMultiPackage = multiPackage;
            }
            if (!readDurationsFromParcel(in)) {
                return false;
            }
            this.mPssTable = this.mStats.readTableFromParcel(in, this.mName, "pss");
            if (this.mPssTable == ProcessStats.BAD_TABLE) {
                return false;
            }
            this.mPssTableSize = this.mPssTable != null ? this.mPssTable.length : 0;
            this.mNumExcessiveWake = in.readInt();
            this.mNumExcessiveCpu = in.readInt();
            this.mNumCachedKill = in.readInt();
            if (this.mNumCachedKill > 0) {
                this.mMinCachedKillPss = in.readLong();
                this.mAvgCachedKillPss = in.readLong();
                this.mMaxCachedKillPss = in.readLong();
                return true;
            }
            ?? r3 = 0;
            this.mMaxCachedKillPss = 0L;
            this.mAvgCachedKillPss = 0L;
            r3.mMinCachedKillPss = this;
            return true;
        }

        public void makeActive() {
            ensureNotDead();
            this.mActive = true;
        }

        public void makeInactive() {
            this.mActive = false;
        }

        public boolean isInUse() {
            return this.mActive || this.mNumActiveServices > 0 || this.mNumStartedServices > 0 || this.mCurState != -1;
        }

        public void setState(int state, int memFactor, long now, ArrayMap<String, ProcessState> pkgList) {
            int state2;
            if (state < 0) {
                state2 = this.mNumStartedServices > 0 ? 7 + (memFactor * 14) : -1;
            } else {
                state2 = ProcessStats.PROCESS_STATE_TO_STATE[state] + (memFactor * 14);
            }
            this.mCommonProcess.setState(state2, now);
            if (this.mCommonProcess.mMultiPackage && pkgList != null) {
                for (int ip = pkgList.size() - 1; ip >= 0; ip--) {
                    pullFixedProc(pkgList, ip).setState(state2, now);
                }
            }
        }

        void setState(int state, long now) {
            ensureNotDead();
            if (this.mCurState != state) {
                commitStateTime(now);
                this.mCurState = state;
            }
        }

        void commitStateTime(long now) {
            if (this.mCurState != -1) {
                long dur = now - this.mStartTime;
                if (dur > 0) {
                    addDuration(this.mCurState, dur);
                }
            }
            this.mStartTime = now;
        }

        void incActiveServices(String serviceName) {
            if (this.mCommonProcess != this) {
                this.mCommonProcess.incActiveServices(serviceName);
            }
            this.mNumActiveServices++;
        }

        void decActiveServices(String serviceName) {
            if (this.mCommonProcess != this) {
                this.mCommonProcess.decActiveServices(serviceName);
            }
            this.mNumActiveServices--;
            if (this.mNumActiveServices < 0) {
                Slog.wtfStack(ProcessStats.TAG, "Proc active services underrun: pkg=" + this.mPackage + " uid=" + this.mUid + " proc=" + this.mName + " service=" + serviceName);
                this.mNumActiveServices = 0;
            }
        }

        void incStartedServices(int memFactor, long now) {
            if (this.mCommonProcess != this) {
                this.mCommonProcess.incStartedServices(memFactor, now);
            }
            this.mNumStartedServices++;
            if (this.mNumStartedServices == 1 && this.mCurState == -1) {
                setState(-1, memFactor, now, null);
            }
        }

        void decStartedServices(int memFactor, long now) {
            if (this.mCommonProcess != this) {
                this.mCommonProcess.decStartedServices(memFactor, now);
            }
            this.mNumStartedServices--;
            if (this.mNumStartedServices == 0 && this.mCurState == 7) {
                setState(-1, memFactor, now, null);
            } else if (this.mNumStartedServices < 0) {
                Slog.wtfStack(ProcessStats.TAG, "Proc started services underrun: pkg=" + this.mPackage + " uid=" + this.mUid + " name=" + this.mName);
                this.mNumStartedServices = 0;
            }
        }

        public void addPss(long pss, long uss, boolean always, ArrayMap<String, ProcessState> pkgList) {
            ensureNotDead();
            if (!always && this.mLastPssState == this.mCurState && SystemClock.uptimeMillis() < this.mLastPssTime + LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS) {
                return;
            }
            this.mLastPssState = this.mCurState;
            this.mLastPssTime = SystemClock.uptimeMillis();
            if (this.mCurState != -1) {
                this.mCommonProcess.addPss(this.mCurState, 1, pss, pss, pss, uss, uss, uss);
                if (this.mCommonProcess.mMultiPackage && pkgList != null) {
                    for (int ip = pkgList.size() - 1; ip >= 0; ip--) {
                        pullFixedProc(pkgList, ip).addPss(this.mCurState, 1, pss, pss, pss, uss, uss, uss);
                    }
                }
            }
        }

        void addPss(int state, int inCount, long minPss, long avgPss, long maxPss, long minUss, long avgUss, long maxUss) {
            int off;
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            if (idx >= 0) {
                off = this.mPssTable[idx];
            } else {
                this.mStats.mAddLongTable = this.mPssTable;
                this.mStats.mAddLongTableSize = this.mPssTableSize;
                off = this.mStats.addLongData(idx ^ (-1), state, 7);
                this.mPssTable = this.mStats.mAddLongTable;
                this.mPssTableSize = this.mStats.mAddLongTableSize;
            }
            long[] longs = this.mStats.mLongs.get((off >> ProcessStats.OFFSET_ARRAY_SHIFT) & ProcessStats.OFFSET_ARRAY_MASK);
            int idx2 = (off >> ProcessStats.OFFSET_INDEX_SHIFT) & ProcessStats.OFFSET_INDEX_MASK;
            long count = longs[idx2 + 0];
            if (count == 0) {
                longs[idx2 + 0] = inCount;
                longs[idx2 + 1] = minPss;
                longs[idx2 + 2] = avgPss;
                longs[idx2 + 3] = maxPss;
                longs[idx2 + 4] = minUss;
                longs[idx2 + 5] = avgUss;
                longs[idx2 + 6] = maxUss;
                return;
            }
            longs[idx2 + 0] = count + inCount;
            if (longs[idx2 + 1] > minPss) {
                longs[idx2 + 1] = minPss;
            }
            longs[idx2 + 2] = (long) (((longs[idx2 + 2] * count) + (avgPss * inCount)) / (count + inCount));
            if (longs[idx2 + 3] < maxPss) {
                longs[idx2 + 3] = maxPss;
            }
            if (longs[idx2 + 4] > minUss) {
                longs[idx2 + 4] = minUss;
            }
            longs[idx2 + 5] = (long) (((longs[idx2 + 5] * count) + (avgUss * inCount)) / (count + inCount));
            if (longs[idx2 + 6] < maxUss) {
                longs[idx2 + 6] = maxUss;
            }
        }

        public void reportExcessiveWake(ArrayMap<String, ProcessState> pkgList) {
            ensureNotDead();
            this.mCommonProcess.mNumExcessiveWake++;
            if (!this.mCommonProcess.mMultiPackage) {
                return;
            }
            for (int ip = pkgList.size() - 1; ip >= 0; ip--) {
                pullFixedProc(pkgList, ip).mNumExcessiveWake++;
            }
        }

        public void reportExcessiveCpu(ArrayMap<String, ProcessState> pkgList) {
            ensureNotDead();
            this.mCommonProcess.mNumExcessiveCpu++;
            if (!this.mCommonProcess.mMultiPackage) {
                return;
            }
            for (int ip = pkgList.size() - 1; ip >= 0; ip--) {
                pullFixedProc(pkgList, ip).mNumExcessiveCpu++;
            }
        }

        private void addCachedKill(int num, long minPss, long avgPss, long maxPss) {
            if (this.mNumCachedKill <= 0) {
                this.mNumCachedKill = num;
                this.mMinCachedKillPss = minPss;
                this.mAvgCachedKillPss = avgPss;
                this.mMaxCachedKillPss = maxPss;
                return;
            }
            if (minPss < this.mMinCachedKillPss) {
                this.mMinCachedKillPss = minPss;
            }
            if (maxPss > this.mMaxCachedKillPss) {
                this.mMaxCachedKillPss = maxPss;
            }
            this.mAvgCachedKillPss = (long) (((this.mAvgCachedKillPss * this.mNumCachedKill) + avgPss) / (this.mNumCachedKill + num));
            this.mNumCachedKill += num;
        }

        public void reportCachedKill(ArrayMap<String, ProcessState> pkgList, long pss) {
            ensureNotDead();
            this.mCommonProcess.addCachedKill(1, pss, pss, pss);
            if (!this.mCommonProcess.mMultiPackage) {
                return;
            }
            for (int ip = pkgList.size() - 1; ip >= 0; ip--) {
                pullFixedProc(pkgList, ip).addCachedKill(1, pss, pss, pss);
            }
        }

        ProcessState pullFixedProc(String pkgName) {
            if (this.mMultiPackage) {
                ProcessState proc = this.mStats.mPackages.get(pkgName, this.mUid).mProcesses.get(this.mName);
                if (proc == null) {
                    throw new IllegalStateException("Didn't create per-package process");
                }
                return proc;
            }
            return this;
        }

        private ProcessState pullFixedProc(ArrayMap<String, ProcessState> pkgList, int index) {
            ProcessState proc = pkgList.valueAt(index);
            if (this.mDead && proc.mCommonProcess != proc) {
                Log.wtf(ProcessStats.TAG, "Pulling dead proc: name=" + this.mName + " pkg=" + this.mPackage + " uid=" + this.mUid + " common.name=" + this.mCommonProcess.mName);
                proc = this.mStats.getProcessStateLocked(proc.mPackage, proc.mUid, proc.mName);
            }
            if (proc.mMultiPackage) {
                PackageState pkg = this.mStats.mPackages.get(pkgList.keyAt(index), proc.mUid);
                if (pkg == null) {
                    throw new IllegalStateException("No existing package " + pkgList.keyAt(index) + Separators.SLASH + proc.mUid + " for multi-proc " + proc.mName);
                }
                proc = pkg.mProcesses.get(proc.mName);
                if (proc == null) {
                    throw new IllegalStateException("Didn't create per-package process " + proc.mName + " in pkg " + pkg.mPackageName + Separators.SLASH + pkg.mUid);
                }
                pkgList.setValueAt(index, proc);
            }
            return proc;
        }

        @Override // com.android.internal.app.ProcessStats.DurationsTable
        long getDuration(int state, long now) {
            long time = super.getDuration(state, now);
            if (this.mCurState == state) {
                time += now - this.mStartTime;
            }
            return time;
        }

        long getPssSampleCount(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            if (idx >= 0) {
                return this.mStats.getLong(this.mPssTable[idx], 0);
            }
            return 0L;
        }

        long getPssMinimum(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            if (idx >= 0) {
                return this.mStats.getLong(this.mPssTable[idx], 1);
            }
            return 0L;
        }

        long getPssAverage(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            if (idx >= 0) {
                return this.mStats.getLong(this.mPssTable[idx], 2);
            }
            return 0L;
        }

        long getPssMaximum(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            if (idx >= 0) {
                return this.mStats.getLong(this.mPssTable[idx], 3);
            }
            return 0L;
        }

        long getPssUssMinimum(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            if (idx >= 0) {
                return this.mStats.getLong(this.mPssTable[idx], 4);
            }
            return 0L;
        }

        long getPssUssAverage(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            if (idx >= 0) {
                return this.mStats.getLong(this.mPssTable[idx], 5);
            }
            return 0L;
        }

        long getPssUssMaximum(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            if (idx >= 0) {
                return this.mStats.getLong(this.mPssTable[idx], 6);
            }
            return 0L;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ProcessState{").append(Integer.toHexString(System.identityHashCode(this))).append(Separators.SP).append(this.mName).append(Separators.SLASH).append(this.mUid).append(" pkg=").append(this.mPackage);
            if (this.mMultiPackage) {
                sb.append(" (multi)");
            }
            if (this.mCommonProcess != this) {
                sb.append(" (sub)");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    /* loaded from: ProcessStats$ServiceState.class */
    public static final class ServiceState extends DurationsTable {
        public final String mPackage;
        public final String mProcessName;
        ProcessState mProc;
        Object mOwner;
        public static final int SERVICE_RUN = 0;
        public static final int SERVICE_STARTED = 1;
        public static final int SERVICE_BOUND = 2;
        public static final int SERVICE_EXEC = 3;
        static final int SERVICE_COUNT = 4;
        int mRunCount;
        public int mRunState;
        long mRunStartTime;
        int mStartedCount;
        public int mStartedState;
        long mStartedStartTime;
        int mBoundCount;
        public int mBoundState;
        long mBoundStartTime;
        int mExecCount;
        public int mExecState;
        long mExecStartTime;

        public ServiceState(ProcessStats processStats, String pkg, String name, String processName, ProcessState proc) {
            super(processStats, name);
            this.mRunState = -1;
            this.mStartedState = -1;
            this.mBoundState = -1;
            this.mExecState = -1;
            this.mPackage = pkg;
            this.mProcessName = processName;
            this.mProc = proc;
        }

        public void applyNewOwner(Object newOwner) {
            if (this.mOwner != newOwner) {
                if (this.mOwner == null) {
                    this.mOwner = newOwner;
                    this.mProc.incActiveServices(this.mName);
                    return;
                }
                this.mOwner = newOwner;
                if (this.mStartedState != -1 || this.mBoundState != -1 || this.mExecState != -1) {
                    long now = SystemClock.uptimeMillis();
                    if (this.mStartedState != -1) {
                        setStarted(false, 0, now);
                    }
                    if (this.mBoundState != -1) {
                        setBound(false, 0, now);
                    }
                    if (this.mExecState != -1) {
                        setExecuting(false, 0, now);
                    }
                }
            }
        }

        public void clearCurrentOwner(Object owner, boolean silently) {
            if (this.mOwner == owner) {
                this.mProc.decActiveServices(this.mName);
                if (this.mStartedState != -1 || this.mBoundState != -1 || this.mExecState != -1) {
                    long now = SystemClock.uptimeMillis();
                    if (this.mStartedState != -1) {
                        if (!silently) {
                            Slog.wtfStack(ProcessStats.TAG, "Service owner " + owner + " cleared while started: pkg=" + this.mPackage + " service=" + this.mName + " proc=" + this.mProc);
                        }
                        setStarted(false, 0, now);
                    }
                    if (this.mBoundState != -1) {
                        if (!silently) {
                            Slog.wtfStack(ProcessStats.TAG, "Service owner " + owner + " cleared while bound: pkg=" + this.mPackage + " service=" + this.mName + " proc=" + this.mProc);
                        }
                        setBound(false, 0, now);
                    }
                    if (this.mExecState != -1) {
                        if (!silently) {
                            Slog.wtfStack(ProcessStats.TAG, "Service owner " + owner + " cleared while exec: pkg=" + this.mPackage + " service=" + this.mName + " proc=" + this.mProc);
                        }
                        setExecuting(false, 0, now);
                    }
                }
                this.mOwner = null;
            }
        }

        public boolean isInUse() {
            return this.mOwner != null;
        }

        void add(ServiceState other) {
            addDurations(other);
            this.mRunCount += other.mRunCount;
            this.mStartedCount += other.mStartedCount;
            this.mBoundCount += other.mBoundCount;
            this.mExecCount += other.mExecCount;
        }

        /* JADX WARN: Multi-variable type inference failed */
        void resetSafely(long now) {
            resetDurationsSafely();
            this.mRunCount = this.mRunState != -1 ? 1 : 0;
            this.mStartedCount = this.mStartedState != -1 ? 1 : 0;
            this.mBoundCount = this.mBoundState != -1 ? 1 : 0;
            this.mExecCount = this.mExecState != -1 ? 1 : 0;
            this.mExecStartTime = now;
            this.mBoundStartTime = now;
            now.mStartedStartTime = this;
            this.mRunStartTime = this;
        }

        void writeToParcel(Parcel out, long now) {
            writeDurationsToParcel(out);
            out.writeInt(this.mRunCount);
            out.writeInt(this.mStartedCount);
            out.writeInt(this.mBoundCount);
            out.writeInt(this.mExecCount);
        }

        boolean readFromParcel(Parcel in) {
            if (!readDurationsFromParcel(in)) {
                return false;
            }
            this.mRunCount = in.readInt();
            this.mStartedCount = in.readInt();
            this.mBoundCount = in.readInt();
            this.mExecCount = in.readInt();
            return true;
        }

        void commitStateTime(long now) {
            if (this.mRunState != -1) {
                addDuration(0 + (this.mRunState * 4), now - this.mRunStartTime);
                this.mRunStartTime = now;
            }
            if (this.mStartedState != -1) {
                addDuration(1 + (this.mStartedState * 4), now - this.mStartedStartTime);
                this.mStartedStartTime = now;
            }
            if (this.mBoundState != -1) {
                addDuration(2 + (this.mBoundState * 4), now - this.mBoundStartTime);
                this.mBoundStartTime = now;
            }
            if (this.mExecState != -1) {
                addDuration(3 + (this.mExecState * 4), now - this.mExecStartTime);
                this.mExecStartTime = now;
            }
        }

        private void updateRunning(int memFactor, long now) {
            int state = (this.mStartedState == -1 && this.mBoundState == -1 && this.mExecState == -1) ? -1 : memFactor;
            if (this.mRunState != state) {
                if (this.mRunState != -1) {
                    addDuration(0 + (this.mRunState * 4), now - this.mRunStartTime);
                } else if (state != -1) {
                    this.mRunCount++;
                }
                this.mRunState = state;
                this.mRunStartTime = now;
            }
        }

        public void setStarted(boolean started, int memFactor, long now) {
            if (this.mOwner == null) {
                Slog.wtf(ProcessStats.TAG, "Starting service " + this + " without owner");
            }
            boolean wasStarted = this.mStartedState != -1;
            int state = started ? memFactor : -1;
            if (this.mStartedState != state) {
                if (this.mStartedState != -1) {
                    addDuration(1 + (this.mStartedState * 4), now - this.mStartedStartTime);
                } else if (started) {
                    this.mStartedCount++;
                }
                this.mStartedState = state;
                this.mStartedStartTime = now;
                this.mProc = this.mProc.pullFixedProc(this.mPackage);
                if (wasStarted != started) {
                    if (started) {
                        this.mProc.incStartedServices(memFactor, now);
                    } else {
                        this.mProc.decStartedServices(memFactor, now);
                    }
                }
                updateRunning(memFactor, now);
            }
        }

        public void setBound(boolean bound, int memFactor, long now) {
            if (this.mOwner == null) {
                Slog.wtf(ProcessStats.TAG, "Binding service " + this + " without owner");
            }
            int state = bound ? memFactor : -1;
            if (this.mBoundState != state) {
                if (this.mBoundState != -1) {
                    addDuration(2 + (this.mBoundState * 4), now - this.mBoundStartTime);
                } else if (bound) {
                    this.mBoundCount++;
                }
                this.mBoundState = state;
                this.mBoundStartTime = now;
                updateRunning(memFactor, now);
            }
        }

        public void setExecuting(boolean executing, int memFactor, long now) {
            if (this.mOwner == null) {
                Slog.wtf(ProcessStats.TAG, "Executing service " + this + " without owner");
            }
            int state = executing ? memFactor : -1;
            if (this.mExecState != state) {
                if (this.mExecState != -1) {
                    addDuration(3 + (this.mExecState * 4), now - this.mExecStartTime);
                } else if (executing) {
                    this.mExecCount++;
                }
                this.mExecState = state;
                this.mExecStartTime = now;
                updateRunning(memFactor, now);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public long getDuration(int opType, int curState, long startTime, int memFactor, long now) {
            int state = opType + (memFactor * 4);
            long time = getDuration(state, now);
            if (curState == memFactor) {
                time += now - startTime;
            }
            return time;
        }

        public String toString() {
            return "ServiceState{" + Integer.toHexString(System.identityHashCode(this)) + Separators.SP + this.mName + " pkg=" + this.mPackage + " proc=" + Integer.toHexString(System.identityHashCode(this)) + "}";
        }
    }

    /* loaded from: ProcessStats$PackageState.class */
    public static final class PackageState {
        public final ArrayMap<String, ProcessState> mProcesses = new ArrayMap<>();
        public final ArrayMap<String, ServiceState> mServices = new ArrayMap<>();
        public final String mPackageName;
        public final int mUid;

        public PackageState(String packageName, int uid) {
            this.mUid = uid;
            this.mPackageName = packageName;
        }
    }

    /* loaded from: ProcessStats$ProcessDataCollection.class */
    public static final class ProcessDataCollection {
        final int[] screenStates;
        final int[] memStates;
        final int[] procStates;
        public long totalTime;
        public long numPss;
        public long minPss;
        public long avgPss;
        public long maxPss;
        public long minUss;
        public long avgUss;
        public long maxUss;

        public ProcessDataCollection(int[] _screenStates, int[] _memStates, int[] _procStates) {
            this.screenStates = _screenStates;
            this.memStates = _memStates;
            this.procStates = _procStates;
        }

        void print(PrintWriter pw, long overallTime, boolean full) {
            if (this.totalTime > overallTime) {
                pw.print("*");
            }
            ProcessStats.printPercent(pw, this.totalTime / overallTime);
            if (this.numPss > 0) {
                pw.print(" (");
                ProcessStats.printSizeValue(pw, this.minPss * 1024);
                pw.print("-");
                ProcessStats.printSizeValue(pw, this.avgPss * 1024);
                pw.print("-");
                ProcessStats.printSizeValue(pw, this.maxPss * 1024);
                pw.print(Separators.SLASH);
                ProcessStats.printSizeValue(pw, this.minUss * 1024);
                pw.print("-");
                ProcessStats.printSizeValue(pw, this.avgUss * 1024);
                pw.print("-");
                ProcessStats.printSizeValue(pw, this.maxUss * 1024);
                if (full) {
                    pw.print(" over ");
                    pw.print(this.numPss);
                }
                pw.print(Separators.RPAREN);
            }
        }
    }
}