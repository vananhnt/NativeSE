package com.android.server.am;

import android.app.AlarmManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.TrafficStats;
import android.os.SystemProperties;
import android.os.Trace;
import com.android.internal.R;
import com.android.internal.util.MemInfoReader;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.wm.WindowManagerService;

/* loaded from: ProcessList.class */
final class ProcessList {
    static final int MIN_CRASH_INTERVAL = 60000;
    static final int UNKNOWN_ADJ = 16;
    static final int CACHED_APP_MAX_ADJ = 15;
    static final int CACHED_APP_MIN_ADJ = 9;
    static final int SERVICE_B_ADJ = 8;
    static final int PREVIOUS_APP_ADJ = 7;
    static final int HOME_APP_ADJ = 6;
    static final int SERVICE_ADJ = 5;
    static final int HEAVY_WEIGHT_APP_ADJ = 4;
    static final int BACKUP_APP_ADJ = 3;
    static final int PERCEPTIBLE_APP_ADJ = 2;
    static final int VISIBLE_APP_ADJ = 1;
    static final int FOREGROUND_APP_ADJ = 0;
    static final int PERSISTENT_PROC_ADJ = -12;
    static final int SYSTEM_ADJ = -16;
    static final int NATIVE_ADJ = -17;
    static final int PAGE_SIZE = 4096;
    static final int MIN_CACHED_APPS = 2;
    static final int MAX_CACHED_APPS = 24;
    static final long MAX_EMPTY_TIME = 1800000;
    static final int TRIM_CRITICAL_THRESHOLD = 3;
    static final int TRIM_LOW_THRESHOLD = 5;
    private final int[] mOomAdj = {0, 1, 2, 3, 9, 15};
    private final long[] mOomMinFreeLow = {Trace.TRACE_TAG_RESOURCES, 12288, Trace.TRACE_TAG_DALVIK, 24576, 28672, Trace.TRACE_TAG_RS};
    private final long[] mOomMinFreeHigh = {49152, 61440, 73728, 86016, 98304, 122880};
    private final long[] mOomMinFree = new long[this.mOomAdj.length];
    private final long mTotalMemMb;
    private long mCachedRestoreLevel;
    private boolean mHaveDisplaySize;
    public static final int PSS_MIN_TIME_FROM_STATE_CHANGE = 15000;
    public static final int PSS_MAX_INTERVAL = 1800000;
    public static final int PSS_ALL_INTERVAL = 600000;
    private static final int PSS_SHORT_INTERVAL = 120000;
    private static final int PSS_FIRST_TOP_INTERVAL = 10000;
    private static final int PSS_FIRST_BACKGROUND_INTERVAL = 20000;
    private static final int PSS_FIRST_CACHED_INTERVAL = 30000;
    private static final int PSS_SAME_IMPORTANT_INTERVAL = 900000;
    private static final int PSS_SAME_SERVICE_INTERVAL = 1200000;
    private static final int PSS_SAME_CACHED_INTERVAL = 1800000;
    public static final int PROC_MEM_PERSISTENT = 0;
    public static final int PROC_MEM_TOP = 1;
    public static final int PROC_MEM_IMPORTANT = 2;
    public static final int PROC_MEM_SERVICE = 3;
    public static final int PROC_MEM_CACHED = 4;
    private static final int MAX_EMPTY_APPS = computeEmptyProcessLimit(24);
    static final int TRIM_EMPTY_APPS = MAX_EMPTY_APPS / 2;
    static final int TRIM_CACHED_APPS = ((24 - MAX_EMPTY_APPS) * 2) / 3;
    private static final int[] sProcStateToProcMem = {0, 0, 1, 2, 2, 2, 2, 3, 4, 4, 4, 4, 4, 4};
    private static final long[] sFirstAwakePssTimes = {120000, 120000, 10000, 20000, 20000, 20000, 20000, 20000, LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS, LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS, LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS, LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS, LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS, LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS};
    private static final long[] sSameAwakePssTimes = {AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, 120000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, 1200000, 1200000, 1800000, 1800000, 1800000, 1800000, 1800000};

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessList.writeFile(java.lang.String, java.lang.String):void, file: ProcessList.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void writeFile(java.lang.String r1, java.lang.String r2) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessList.writeFile(java.lang.String, java.lang.String):void, file: ProcessList.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessList.writeFile(java.lang.String, java.lang.String):void");
    }

    ProcessList() {
        MemInfoReader minfo = new MemInfoReader();
        minfo.readMemInfo();
        this.mTotalMemMb = minfo.getTotalSize() / TrafficStats.MB_IN_BYTES;
        updateOomLevels(0, 0, false);
    }

    void applyDisplaySize(WindowManagerService wm) {
        if (!this.mHaveDisplaySize) {
            Point p = new Point();
            wm.getBaseDisplaySize(0, p);
            if (p.x != 0 && p.y != 0) {
                updateOomLevels(p.x, p.y, true);
                this.mHaveDisplaySize = true;
            }
        }
    }

    private void updateOomLevels(int displayWidth, int displayHeight, boolean write) {
        float scaleMem = ((float) (this.mTotalMemMb - 300)) / 400.0f;
        float scaleDisp = ((displayWidth * displayHeight) - 384000) / (1024000 - 384000);
        StringBuilder adjString = new StringBuilder();
        StringBuilder memString = new StringBuilder();
        float scale = scaleMem > scaleDisp ? scaleMem : scaleDisp;
        if (scale < 0.0f) {
            scale = 0.0f;
        } else if (scale > 1.0f) {
            scale = 1.0f;
        }
        int minfree_adj = Resources.getSystem().getInteger(R.integer.config_lowMemoryKillerMinFreeKbytesAdjust);
        int minfree_abs = Resources.getSystem().getInteger(R.integer.config_lowMemoryKillerMinFreeKbytesAbsolute);
        for (int i = 0; i < this.mOomAdj.length; i++) {
            long low = this.mOomMinFreeLow[i];
            long high = this.mOomMinFreeHigh[i];
            this.mOomMinFree[i] = ((float) low) + (((float) (high - low)) * scale);
        }
        if (minfree_abs >= 0) {
            for (int i2 = 0; i2 < this.mOomAdj.length; i2++) {
                this.mOomMinFree[i2] = (minfree_abs * ((float) this.mOomMinFree[i2])) / ((float) this.mOomMinFree[this.mOomAdj.length - 1]);
            }
        }
        if (minfree_adj != 0) {
            for (int i3 = 0; i3 < this.mOomAdj.length; i3++) {
                long[] jArr = this.mOomMinFree;
                int i4 = i3;
                jArr[i4] = jArr[i4] + ((minfree_adj * ((float) this.mOomMinFree[i3])) / ((float) this.mOomMinFree[this.mOomAdj.length - 1]));
                if (this.mOomMinFree[i3] < 0) {
                    this.mOomMinFree[i3] = 0;
                }
            }
        }
        this.mCachedRestoreLevel = (getMemLevel(15) / 1024) / 3;
        for (int i5 = 0; i5 < this.mOomAdj.length; i5++) {
            if (i5 > 0) {
                adjString.append(',');
                memString.append(',');
            }
            adjString.append(this.mOomAdj[i5]);
            memString.append((this.mOomMinFree[i5] * 1024) / 4096);
        }
        int reserve = (((displayWidth * displayHeight) * 4) * 3) / 1024;
        int reserve_adj = Resources.getSystem().getInteger(R.integer.config_extraFreeKbytesAdjust);
        int reserve_abs = Resources.getSystem().getInteger(R.integer.config_extraFreeKbytesAbsolute);
        if (reserve_abs >= 0) {
            reserve = reserve_abs;
        }
        if (reserve_adj != 0) {
            reserve += reserve_adj;
            if (reserve < 0) {
                reserve = 0;
            }
        }
        if (write) {
            writeFile("/sys/module/lowmemorykiller/parameters/adj", adjString.toString());
            writeFile("/sys/module/lowmemorykiller/parameters/minfree", memString.toString());
            SystemProperties.set("sys.sysctl.extra_free_kbytes", Integer.toString(reserve));
        }
    }

    public static int computeEmptyProcessLimit(int totalProcessLimit) {
        return (totalProcessLimit * 2) / 3;
    }

    private static String buildOomTag(String prefix, String space, int val, int base) {
        if (val == base) {
            return space == null ? prefix : prefix + "  ";
        }
        return prefix + "+" + Integer.toString(val - base);
    }

    public static String makeOomAdjString(int setAdj) {
        if (setAdj >= 9) {
            return buildOomTag("cch", "  ", setAdj, 9);
        }
        if (setAdj >= 8) {
            return buildOomTag("svcb ", null, setAdj, 8);
        }
        if (setAdj >= 7) {
            return buildOomTag("prev ", null, setAdj, 7);
        }
        if (setAdj >= 6) {
            return buildOomTag("home ", null, setAdj, 6);
        }
        if (setAdj >= 5) {
            return buildOomTag("svc  ", null, setAdj, 5);
        }
        if (setAdj >= 4) {
            return buildOomTag("hvy  ", null, setAdj, 4);
        }
        if (setAdj >= 3) {
            return buildOomTag("bkup ", null, setAdj, 3);
        }
        if (setAdj >= 2) {
            return buildOomTag("prcp ", null, setAdj, 2);
        }
        if (setAdj >= 1) {
            return buildOomTag("vis  ", null, setAdj, 1);
        }
        if (setAdj >= 0) {
            return buildOomTag("fore ", null, setAdj, 0);
        }
        if (setAdj >= -12) {
            return buildOomTag("pers ", null, setAdj, -12);
        }
        if (setAdj >= -16) {
            return buildOomTag("sys  ", null, setAdj, -16);
        }
        if (setAdj >= -17) {
            return buildOomTag("ntv  ", null, setAdj, -17);
        }
        return Integer.toString(setAdj);
    }

    public static String makeProcStateString(int curProcState) {
        String procState;
        switch (curProcState) {
            case -1:
                procState = "N ";
                break;
            case 0:
                procState = "P ";
                break;
            case 1:
                procState = "PU";
                break;
            case 2:
                procState = "T ";
                break;
            case 3:
                procState = "IF";
                break;
            case 4:
                procState = "IB";
                break;
            case 5:
                procState = "BU";
                break;
            case 6:
                procState = "HW";
                break;
            case 7:
                procState = "S ";
                break;
            case 8:
                procState = "R ";
                break;
            case 9:
                procState = "HO";
                break;
            case 10:
                procState = "LA";
                break;
            case 11:
                procState = "CA";
                break;
            case 12:
                procState = "Ca";
                break;
            case 13:
                procState = "CE";
                break;
            default:
                procState = "??";
                break;
        }
        return procState;
    }

    public static void appendRamKb(StringBuilder sb, long ramKb) {
        int j = 0;
        int i = 10;
        while (true) {
            int fact = i;
            if (j < 6) {
                if (ramKb < fact) {
                    sb.append(' ');
                }
                j++;
                i = fact * 10;
            } else {
                sb.append(ramKb);
                return;
            }
        }
    }

    public static boolean procStatesDifferForMem(int procState1, int procState2) {
        return sProcStateToProcMem[procState1] != sProcStateToProcMem[procState2];
    }

    public static long computeNextPssTime(int procState, boolean first, boolean sleeping, long now) {
        long[] table = sleeping ? first ? sFirstAwakePssTimes : sSameAwakePssTimes : first ? sFirstAwakePssTimes : sSameAwakePssTimes;
        return now + table[procState];
    }

    long getMemLevel(int adjustment) {
        for (int i = 0; i < this.mOomAdj.length; i++) {
            if (adjustment <= this.mOomAdj[i]) {
                return this.mOomMinFree[i] * 1024;
            }
        }
        return this.mOomMinFree[this.mOomAdj.length - 1] * 1024;
    }

    long getCachedRestoreThresholdKb() {
        return this.mCachedRestoreLevel;
    }
}