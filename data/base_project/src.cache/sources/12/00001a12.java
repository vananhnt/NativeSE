package com.android.internal.os;

import android.media.videoeditor.MediaProperties;
import android.mtp.MtpConstants;
import android.os.FileUtils;
import android.os.Process;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Slog;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.util.FastPrintWriter;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

/* loaded from: ProcessCpuTracker.class */
public class ProcessCpuTracker {
    private static final String TAG = "ProcessCpuTracker";
    private static final boolean DEBUG = false;
    private static final boolean localLOGV = false;
    static final int PROCESS_STAT_MINOR_FAULTS = 0;
    static final int PROCESS_STAT_MAJOR_FAULTS = 1;
    static final int PROCESS_STAT_UTIME = 2;
    static final int PROCESS_STAT_STIME = 3;
    static final int PROCESS_FULL_STAT_MINOR_FAULTS = 1;
    static final int PROCESS_FULL_STAT_MAJOR_FAULTS = 2;
    static final int PROCESS_FULL_STAT_UTIME = 3;
    static final int PROCESS_FULL_STAT_STIME = 4;
    static final int PROCESS_FULL_STAT_VSIZE = 5;
    private final boolean mIncludeThreads;
    private long mCurrentSampleTime;
    private long mLastSampleTime;
    private long mCurrentSampleRealTime;
    private long mLastSampleRealTime;
    private long mBaseUserTime;
    private long mBaseSystemTime;
    private long mBaseIoWaitTime;
    private long mBaseIrqTime;
    private long mBaseSoftIrqTime;
    private long mBaseIdleTime;
    private int mRelUserTime;
    private int mRelSystemTime;
    private int mRelIoWaitTime;
    private int mRelIrqTime;
    private int mRelSoftIrqTime;
    private int mRelIdleTime;
    private int[] mCurPids;
    private int[] mCurThreadPids;
    private boolean mWorkingProcsSorted;
    private long[] mCpuSpeedTimes;
    private long[] mRelCpuSpeedTimes;
    private long[] mCpuSpeeds;
    private static final int[] PROCESS_STATS_FORMAT = {32, 544, 32, 32, 32, 32, 32, 32, 32, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, 32, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, 32, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED};
    private static final int[] PROCESS_FULL_STATS_FORMAT = {32, 4640, 32, 32, 32, 32, 32, 32, 32, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, 32, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, 32, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, 32, 32, 32, 32, 32, 32, 32, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED};
    private static final int[] SYSTEM_CPU_FORMAT = {MediaProperties.HEIGHT_288, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED, MtpConstants.RESPONSE_SPECIFICATION_OF_DESTINATION_UNSUPPORTED};
    private static final int[] LOAD_AVERAGE_FORMAT = {16416, 16416, 16416};
    private static final Comparator<Stats> sLoadComparator = new Comparator<Stats>() { // from class: com.android.internal.os.ProcessCpuTracker.1
        @Override // java.util.Comparator
        public final int compare(Stats sta, Stats stb) {
            int ta = sta.rel_utime + sta.rel_stime;
            int tb = stb.rel_utime + stb.rel_stime;
            if (ta != tb) {
                return ta > tb ? -1 : 1;
            } else if (sta.added != stb.added) {
                return sta.added ? -1 : 1;
            } else if (sta.removed != stb.removed) {
                return sta.added ? -1 : 1;
            } else {
                return 0;
            }
        }
    };
    private final long[] mProcessStatsData = new long[4];
    private final long[] mSinglePidStatsData = new long[4];
    private final String[] mProcessFullStatsStringData = new String[6];
    private final long[] mProcessFullStatsData = new long[6];
    private final long[] mSystemCpuData = new long[7];
    private final float[] mLoadAverageData = new float[3];
    private float mLoad1 = 0.0f;
    private float mLoad5 = 0.0f;
    private float mLoad15 = 0.0f;
    private final ArrayList<Stats> mProcStats = new ArrayList<>();
    private final ArrayList<Stats> mWorkingProcs = new ArrayList<>();
    private boolean mFirst = true;
    private byte[] mBuffer = new byte[4096];

    /* loaded from: ProcessCpuTracker$Stats.class */
    public static class Stats {
        public final int pid;
        public final int uid;
        final String statFile;
        final String cmdlineFile;
        final String threadsDir;
        final ArrayList<Stats> threadStats;
        final ArrayList<Stats> workingThreads;
        public BatteryStatsImpl.Uid.Proc batteryStats;
        public boolean interesting;
        public String baseName;
        public String name;
        public int nameWidth;
        public long vsize;
        public long base_uptime;
        public long rel_uptime;
        public long base_utime;
        public long base_stime;
        public int rel_utime;
        public int rel_stime;
        public long base_minfaults;
        public long base_majfaults;
        public int rel_minfaults;
        public int rel_majfaults;
        public boolean active;
        public boolean working;
        public boolean added;
        public boolean removed;

        Stats(int _pid, int parentPid, boolean includeThreads) {
            this.pid = _pid;
            if (parentPid < 0) {
                File procDir = new File("/proc", Integer.toString(this.pid));
                this.statFile = new File(procDir, "stat").toString();
                this.cmdlineFile = new File(procDir, "cmdline").toString();
                this.threadsDir = new File(procDir, "task").toString();
                if (includeThreads) {
                    this.threadStats = new ArrayList<>();
                    this.workingThreads = new ArrayList<>();
                } else {
                    this.threadStats = null;
                    this.workingThreads = null;
                }
            } else {
                File taskDir = new File(new File(new File("/proc", Integer.toString(parentPid)), "task"), Integer.toString(this.pid));
                this.statFile = new File(taskDir, "stat").toString();
                this.cmdlineFile = null;
                this.threadsDir = null;
                this.threadStats = null;
                this.workingThreads = null;
            }
            this.uid = FileUtils.getUid(this.statFile.toString());
        }
    }

    public ProcessCpuTracker(boolean includeThreads) {
        this.mIncludeThreads = includeThreads;
    }

    public void onLoadChanged(float load1, float load5, float load15) {
    }

    public int onMeasureProcessName(String name) {
        return 0;
    }

    public void init() {
        this.mFirst = true;
        update();
    }

    public void update() {
        this.mLastSampleTime = this.mCurrentSampleTime;
        this.mCurrentSampleTime = SystemClock.uptimeMillis();
        this.mLastSampleRealTime = this.mCurrentSampleRealTime;
        this.mCurrentSampleRealTime = SystemClock.elapsedRealtime();
        long[] sysCpu = this.mSystemCpuData;
        if (Process.readProcFile("/proc/stat", SYSTEM_CPU_FORMAT, null, sysCpu, null)) {
            long usertime = sysCpu[0] + sysCpu[1];
            long systemtime = sysCpu[2];
            long idletime = sysCpu[3];
            long iowaittime = sysCpu[4];
            long irqtime = sysCpu[5];
            long softirqtime = sysCpu[6];
            this.mRelUserTime = (int) (usertime - this.mBaseUserTime);
            this.mRelSystemTime = (int) (systemtime - this.mBaseSystemTime);
            this.mRelIoWaitTime = (int) (iowaittime - this.mBaseIoWaitTime);
            this.mRelIrqTime = (int) (irqtime - this.mBaseIrqTime);
            this.mRelSoftIrqTime = (int) (softirqtime - this.mBaseSoftIrqTime);
            this.mRelIdleTime = (int) (idletime - this.mBaseIdleTime);
            this.mBaseUserTime = usertime;
            this.mBaseSystemTime = systemtime;
            this.mBaseIoWaitTime = iowaittime;
            this.mBaseIrqTime = irqtime;
            this.mBaseSoftIrqTime = softirqtime;
            this.mBaseIdleTime = idletime;
        }
        this.mCurPids = collectStats("/proc", -1, this.mFirst, this.mCurPids, this.mProcStats);
        float[] loadAverages = this.mLoadAverageData;
        if (Process.readProcFile("/proc/loadavg", LOAD_AVERAGE_FORMAT, null, null, loadAverages)) {
            float load1 = loadAverages[0];
            float load5 = loadAverages[1];
            float load15 = loadAverages[2];
            if (load1 != this.mLoad1 || load5 != this.mLoad5 || load15 != this.mLoad15) {
                this.mLoad1 = load1;
                this.mLoad5 = load5;
                this.mLoad15 = load15;
                onLoadChanged(load1, load5, load15);
            }
        }
        this.mWorkingProcsSorted = false;
        this.mFirst = false;
    }

    private int[] collectStats(String statsFile, int parentPid, boolean first, int[] curPids, ArrayList<Stats> allProcs) {
        int pid;
        int[] pids = Process.getPids(statsFile, curPids);
        int NP = pids == null ? 0 : pids.length;
        int NS = allProcs.size();
        int curStatsIndex = 0;
        int i = 0;
        while (i < NP && (pid = pids[i]) >= 0) {
            Stats st = curStatsIndex < NS ? allProcs.get(curStatsIndex) : null;
            if (st != null && st.pid == pid) {
                st.added = false;
                st.working = false;
                curStatsIndex++;
                if (st.interesting) {
                    long uptime = SystemClock.uptimeMillis();
                    long[] procStats = this.mProcessStatsData;
                    if (Process.readProcFile(st.statFile.toString(), PROCESS_STATS_FORMAT, null, procStats, null)) {
                        long minfaults = procStats[0];
                        long majfaults = procStats[1];
                        long utime = procStats[2];
                        long stime = procStats[3];
                        if (utime == st.base_utime && stime == st.base_stime) {
                            st.rel_utime = 0;
                            st.rel_stime = 0;
                            st.rel_minfaults = 0;
                            st.rel_majfaults = 0;
                            if (st.active) {
                                st.active = false;
                            }
                        } else {
                            if (!st.active) {
                                st.active = true;
                            }
                            if (parentPid < 0) {
                                getName(st, st.cmdlineFile);
                                if (st.threadStats != null) {
                                    this.mCurThreadPids = collectStats(st.threadsDir, pid, false, this.mCurThreadPids, st.threadStats);
                                }
                            }
                            st.rel_uptime = uptime - st.base_uptime;
                            st.base_uptime = uptime;
                            st.rel_utime = (int) (utime - st.base_utime);
                            st.rel_stime = (int) (stime - st.base_stime);
                            st.base_utime = utime;
                            st.base_stime = stime;
                            st.rel_minfaults = (int) (minfaults - st.base_minfaults);
                            st.rel_majfaults = (int) (majfaults - st.base_majfaults);
                            st.base_minfaults = minfaults;
                            st.base_majfaults = majfaults;
                            st.working = true;
                        }
                    }
                }
            } else if (st == null || st.pid > pid) {
                Stats st2 = new Stats(pid, parentPid, this.mIncludeThreads);
                allProcs.add(curStatsIndex, st2);
                curStatsIndex++;
                NS++;
                String[] procStatsString = this.mProcessFullStatsStringData;
                long[] procStats2 = this.mProcessFullStatsData;
                st2.base_uptime = SystemClock.uptimeMillis();
                if (Process.readProcFile(st2.statFile.toString(), PROCESS_FULL_STATS_FORMAT, procStatsString, procStats2, null)) {
                    st2.vsize = procStats2[5];
                    st2.interesting = true;
                    st2.baseName = procStatsString[0];
                    st2.base_minfaults = procStats2[1];
                    st2.base_majfaults = procStats2[2];
                    st2.base_utime = procStats2[3];
                    st2.base_stime = procStats2[4];
                } else {
                    Slog.w(TAG, "Skipping unknown process pid " + pid);
                    st2.baseName = MediaStore.UNKNOWN_STRING;
                    st2.base_stime = 0L;
                    st2.base_utime = 0L;
                    st2.base_majfaults = 0L;
                    st2.base_minfaults = 0L;
                }
                if (parentPid < 0) {
                    getName(st2, st2.cmdlineFile);
                    if (st2.threadStats != null) {
                        this.mCurThreadPids = collectStats(st2.threadsDir, pid, true, this.mCurThreadPids, st2.threadStats);
                    }
                } else if (st2.interesting) {
                    st2.name = st2.baseName;
                    st2.nameWidth = onMeasureProcessName(st2.name);
                }
                st2.rel_utime = 0;
                st2.rel_stime = 0;
                st2.rel_minfaults = 0;
                st2.rel_majfaults = 0;
                st2.added = true;
                if (!first && st2.interesting) {
                    st2.working = true;
                }
            } else {
                st.rel_utime = 0;
                st.rel_stime = 0;
                st.rel_minfaults = 0;
                st.rel_majfaults = 0;
                st.removed = true;
                st.working = true;
                allProcs.remove(curStatsIndex);
                NS--;
                i--;
            }
            i++;
        }
        while (curStatsIndex < NS) {
            Stats st3 = allProcs.get(curStatsIndex);
            st3.rel_utime = 0;
            st3.rel_stime = 0;
            st3.rel_minfaults = 0;
            st3.rel_majfaults = 0;
            st3.removed = true;
            st3.working = true;
            allProcs.remove(curStatsIndex);
            NS--;
        }
        return pids;
    }

    public long getCpuTimeForPid(int pid) {
        String statFile = "/proc/" + pid + "/stat";
        long[] statsData = this.mSinglePidStatsData;
        if (Process.readProcFile(statFile, PROCESS_STATS_FORMAT, null, statsData, null)) {
            long time = statsData[2] + statsData[3];
            return time;
        }
        return 0L;
    }

    public long[] getLastCpuSpeedTimes() {
        if (this.mCpuSpeedTimes == null) {
            this.mCpuSpeedTimes = getCpuSpeedTimes(null);
            this.mRelCpuSpeedTimes = new long[this.mCpuSpeedTimes.length];
            for (int i = 0; i < this.mCpuSpeedTimes.length; i++) {
                this.mRelCpuSpeedTimes[i] = 1;
            }
        } else {
            getCpuSpeedTimes(this.mRelCpuSpeedTimes);
            for (int i2 = 0; i2 < this.mCpuSpeedTimes.length; i2++) {
                long temp = this.mRelCpuSpeedTimes[i2];
                long[] jArr = this.mRelCpuSpeedTimes;
                int i3 = i2;
                jArr[i3] = jArr[i3] - this.mCpuSpeedTimes[i2];
                this.mCpuSpeedTimes[i2] = temp;
            }
        }
        return this.mRelCpuSpeedTimes;
    }

    private long[] getCpuSpeedTimes(long[] out) {
        long[] tempTimes = out;
        long[] tempSpeeds = this.mCpuSpeeds;
        if (out == null) {
            tempTimes = new long[60];
            tempSpeeds = new long[60];
        }
        int speed = 0;
        String file = readFile("/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state", (char) 0);
        if (file != null) {
            StringTokenizer st = new StringTokenizer(file, "\n ");
            while (st.hasMoreElements()) {
                String token = st.nextToken();
                try {
                    long val = Long.parseLong(token);
                    tempSpeeds[speed] = val;
                    String token2 = st.nextToken();
                    long val2 = Long.parseLong(token2);
                    tempTimes[speed] = val2;
                    speed++;
                } catch (NumberFormatException e) {
                    Slog.i(TAG, "Unable to parse time_in_state");
                }
                if (speed == 60) {
                    break;
                }
            }
        }
        if (out == null) {
            out = new long[speed];
            this.mCpuSpeeds = new long[speed];
            System.arraycopy(tempSpeeds, 0, this.mCpuSpeeds, 0, speed);
            System.arraycopy(tempTimes, 0, out, 0, speed);
        }
        return out;
    }

    public final int getLastUserTime() {
        return this.mRelUserTime;
    }

    public final int getLastSystemTime() {
        return this.mRelSystemTime;
    }

    public final int getLastIoWaitTime() {
        return this.mRelIoWaitTime;
    }

    public final int getLastIrqTime() {
        return this.mRelIrqTime;
    }

    public final int getLastSoftIrqTime() {
        return this.mRelSoftIrqTime;
    }

    public final int getLastIdleTime() {
        return this.mRelIdleTime;
    }

    public final float getTotalCpuPercent() {
        int denom = this.mRelUserTime + this.mRelSystemTime + this.mRelIrqTime + this.mRelIdleTime;
        if (denom <= 0) {
            return 0.0f;
        }
        return (((this.mRelUserTime + this.mRelSystemTime) + this.mRelIrqTime) * 100.0f) / denom;
    }

    final void buildWorkingProcs() {
        if (!this.mWorkingProcsSorted) {
            this.mWorkingProcs.clear();
            int N = this.mProcStats.size();
            for (int i = 0; i < N; i++) {
                Stats stats = this.mProcStats.get(i);
                if (stats.working) {
                    this.mWorkingProcs.add(stats);
                    if (stats.threadStats != null && stats.threadStats.size() > 1) {
                        stats.workingThreads.clear();
                        int M = stats.threadStats.size();
                        for (int j = 0; j < M; j++) {
                            Stats tstats = stats.threadStats.get(j);
                            if (tstats.working) {
                                stats.workingThreads.add(tstats);
                            }
                        }
                        Collections.sort(stats.workingThreads, sLoadComparator);
                    }
                }
            }
            Collections.sort(this.mWorkingProcs, sLoadComparator);
            this.mWorkingProcsSorted = true;
        }
    }

    public final int countStats() {
        return this.mProcStats.size();
    }

    public final Stats getStats(int index) {
        return this.mProcStats.get(index);
    }

    public final int countWorkingStats() {
        buildWorkingProcs();
        return this.mWorkingProcs.size();
    }

    public final Stats getWorkingStats(int index) {
        return this.mWorkingProcs.get(index);
    }

    public final String printCurrentLoad() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new FastPrintWriter((Writer) sw, false, 128);
        pw.print("Load: ");
        pw.print(this.mLoad1);
        pw.print(" / ");
        pw.print(this.mLoad5);
        pw.print(" / ");
        pw.println(this.mLoad15);
        pw.flush();
        return sw.toString();
    }

    public final String printCurrentState(long now) {
        buildWorkingProcs();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new FastPrintWriter((Writer) sw, false, 1024);
        pw.print("CPU usage from ");
        if (now > this.mLastSampleTime) {
            pw.print(now - this.mLastSampleTime);
            pw.print("ms to ");
            pw.print(now - this.mCurrentSampleTime);
            pw.print("ms ago");
        } else {
            pw.print(this.mLastSampleTime - now);
            pw.print("ms to ");
            pw.print(this.mCurrentSampleTime - now);
            pw.print("ms later");
        }
        long sampleTime = this.mCurrentSampleTime - this.mLastSampleTime;
        long sampleRealTime = this.mCurrentSampleRealTime - this.mLastSampleRealTime;
        long percAwake = sampleRealTime > 0 ? (sampleTime * 100) / sampleRealTime : 0L;
        if (percAwake != 100) {
            pw.print(" with ");
            pw.print(percAwake);
            pw.print("% awake");
        }
        pw.println(Separators.COLON);
        int totalTime = this.mRelUserTime + this.mRelSystemTime + this.mRelIoWaitTime + this.mRelIrqTime + this.mRelSoftIrqTime + this.mRelIdleTime;
        int N = this.mWorkingProcs.size();
        for (int i = 0; i < N; i++) {
            Stats st = this.mWorkingProcs.get(i);
            printProcessCPU(pw, st.added ? " +" : st.removed ? " -" : "  ", st.pid, st.name, ((int) (st.rel_uptime + 5)) / 10, st.rel_utime, st.rel_stime, 0, 0, 0, st.rel_minfaults, st.rel_majfaults);
            if (!st.removed && st.workingThreads != null) {
                int M = st.workingThreads.size();
                for (int j = 0; j < M; j++) {
                    Stats tst = st.workingThreads.get(j);
                    printProcessCPU(pw, tst.added ? "   +" : tst.removed ? "   -" : "    ", tst.pid, tst.name, ((int) (st.rel_uptime + 5)) / 10, tst.rel_utime, tst.rel_stime, 0, 0, 0, 0, 0);
                }
            }
        }
        printProcessCPU(pw, "", -1, "TOTAL", totalTime, this.mRelUserTime, this.mRelSystemTime, this.mRelIoWaitTime, this.mRelIrqTime, this.mRelSoftIrqTime, 0, 0);
        pw.flush();
        return sw.toString();
    }

    private void printRatio(PrintWriter pw, long numerator, long denominator) {
        long thousands = (numerator * 1000) / denominator;
        long hundreds = thousands / 10;
        pw.print(hundreds);
        if (hundreds < 10) {
            long remainder = thousands - (hundreds * 10);
            if (remainder != 0) {
                pw.print('.');
                pw.print(remainder);
            }
        }
    }

    private void printProcessCPU(PrintWriter pw, String prefix, int pid, String label, int totalTime, int user, int system, int iowait, int irq, int softIrq, int minFaults, int majFaults) {
        pw.print(prefix);
        if (totalTime == 0) {
            totalTime = 1;
        }
        printRatio(pw, user + system + iowait + irq + softIrq, totalTime);
        pw.print("% ");
        if (pid >= 0) {
            pw.print(pid);
            pw.print(Separators.SLASH);
        }
        pw.print(label);
        pw.print(": ");
        printRatio(pw, user, totalTime);
        pw.print("% user + ");
        printRatio(pw, system, totalTime);
        pw.print("% kernel");
        if (iowait > 0) {
            pw.print(" + ");
            printRatio(pw, iowait, totalTime);
            pw.print("% iowait");
        }
        if (irq > 0) {
            pw.print(" + ");
            printRatio(pw, irq, totalTime);
            pw.print("% irq");
        }
        if (softIrq > 0) {
            pw.print(" + ");
            printRatio(pw, softIrq, totalTime);
            pw.print("% softirq");
        }
        if (minFaults > 0 || majFaults > 0) {
            pw.print(" / faults:");
            if (minFaults > 0) {
                pw.print(Separators.SP);
                pw.print(minFaults);
                pw.print(" minor");
            }
            if (majFaults > 0) {
                pw.print(Separators.SP);
                pw.print(majFaults);
                pw.print(" major");
            }
        }
        pw.println();
    }

    private String readFile(String file, char endChar) {
        StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            int len = is.read(this.mBuffer);
            is.close();
            if (len <= 0) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                StrictMode.setThreadPolicy(savedPolicy);
                return null;
            }
            int i = 0;
            while (i < len && this.mBuffer[i] != endChar) {
                i++;
            }
            String str = new String(this.mBuffer, 0, i);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e2) {
                }
            }
            StrictMode.setThreadPolicy(savedPolicy);
            return str;
        } catch (FileNotFoundException e3) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
            StrictMode.setThreadPolicy(savedPolicy);
            return null;
        } catch (IOException e5) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e6) {
                }
            }
            StrictMode.setThreadPolicy(savedPolicy);
            return null;
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e7) {
                }
            }
            StrictMode.setThreadPolicy(savedPolicy);
            throw th;
        }
    }

    private void getName(Stats st, String cmdlineFile) {
        String newName = st.name;
        if (st.name == null || st.name.equals("app_process") || st.name.equals("<pre-initialized>")) {
            String cmdName = readFile(cmdlineFile, (char) 0);
            if (cmdName != null && cmdName.length() > 1) {
                newName = cmdName;
                int i = newName.lastIndexOf(Separators.SLASH);
                if (i > 0 && i < newName.length() - 1) {
                    newName = newName.substring(i + 1);
                }
            }
            if (newName == null) {
                newName = st.baseName;
            }
        }
        if (st.name == null || !newName.equals(st.name)) {
            st.name = newName;
            st.nameWidth = onMeasureProcessName(st.name);
        }
    }
}