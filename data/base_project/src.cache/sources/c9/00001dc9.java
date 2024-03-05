package com.android.server.am;

import android.app.AlarmManager;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.app.IProcessStats;
import com.android.internal.app.ProcessStats;
import com.android.internal.os.BackgroundThread;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: ProcessStatsService.class */
public final class ProcessStatsService extends IProcessStats.Stub {
    static final String TAG = "ProcessStatsService";
    static final boolean DEBUG = false;
    static final int MAX_HISTORIC_STATES = 8;
    static final String STATE_FILE_PREFIX = "state-";
    static final String STATE_FILE_SUFFIX = ".bin";
    static final String STATE_FILE_CHECKIN_SUFFIX = ".ci";
    static long WRITE_PERIOD = AlarmManager.INTERVAL_HALF_HOUR;
    final ActivityManagerService mAm;
    final File mBaseDir;
    ProcessStats mProcessStats;
    AtomicFile mFile;
    boolean mCommitPending;
    boolean mShuttingDown;
    boolean mMemFactorLowered;
    AtomicFile mPendingWriteFile;
    Parcel mPendingWrite;
    boolean mPendingWriteCommitted;
    long mLastWriteTime;
    int mLastMemOnlyState = -1;
    final ReentrantLock mWriteLock = new ReentrantLock();
    final Object mPendingWriteLock = new Object();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.performWriteState():void, file: ProcessStatsService.class
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
    void performWriteState() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.performWriteState():void, file: ProcessStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessStatsService.performWriteState():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.getCurrentStats(java.util.List<android.os.ParcelFileDescriptor>):byte[], file: ProcessStatsService.class
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
    @Override // com.android.internal.app.IProcessStats
    public byte[] getCurrentStats(java.util.List<android.os.ParcelFileDescriptor> r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.getCurrentStats(java.util.List<android.os.ParcelFileDescriptor>):byte[], file: ProcessStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessStatsService.getCurrentStats(java.util.List):byte[]");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.getStatsOverTime(long):android.os.ParcelFileDescriptor, file: ProcessStatsService.class
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
    @Override // com.android.internal.app.IProcessStats
    public android.os.ParcelFileDescriptor getStatsOverTime(long r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.getStatsOverTime(long):android.os.ParcelFileDescriptor, file: ProcessStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessStatsService.getStatsOverTime(long):android.os.ParcelFileDescriptor");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: ProcessStatsService.class
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
    @Override // android.os.Binder
    protected void dump(java.io.FileDescriptor r1, java.io.PrintWriter r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: ProcessStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessStatsService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.dumpInner(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: ProcessStatsService.class
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
    private void dumpInner(java.io.FileDescriptor r1, java.io.PrintWriter r2, java.lang.String[] r3) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ProcessStatsService.dumpInner(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void, file: ProcessStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessStatsService.dumpInner(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    public ProcessStatsService(ActivityManagerService am, File file) {
        this.mAm = am;
        this.mBaseDir = file;
        this.mBaseDir.mkdirs();
        this.mProcessStats = new ProcessStats(true);
        updateFile();
        SystemProperties.addChangeCallback(new Runnable() { // from class: com.android.server.am.ProcessStatsService.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (ProcessStatsService.this.mAm) {
                    if (ProcessStatsService.this.mProcessStats.evaluateSystemProperties(false)) {
                        ProcessStatsService.this.mProcessStats.mFlags |= 4;
                        ProcessStatsService.this.writeStateLocked(true, true);
                        ProcessStatsService.this.mProcessStats.evaluateSystemProperties(true);
                    }
                }
            }
        });
    }

    @Override // com.android.internal.app.IProcessStats.Stub, android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(TAG, "Process Stats Crash", e);
            }
            throw e;
        }
    }

    public ProcessStats.ProcessState getProcessStateLocked(String packageName, int uid, String processName) {
        return this.mProcessStats.getProcessStateLocked(packageName, uid, processName);
    }

    public ProcessStats.ServiceState getServiceStateLocked(String packageName, int uid, String processName, String className) {
        return this.mProcessStats.getServiceStateLocked(packageName, uid, processName, className);
    }

    public boolean isMemFactorLowered() {
        return this.mMemFactorLowered;
    }

    public boolean setMemFactorLocked(int memFactor, boolean screenOn, long now) {
        this.mMemFactorLowered = memFactor < this.mLastMemOnlyState;
        this.mLastMemOnlyState = memFactor;
        if (screenOn) {
            memFactor += 4;
        }
        if (memFactor != this.mProcessStats.mMemFactor) {
            if (this.mProcessStats.mMemFactor != -1) {
                long[] jArr = this.mProcessStats.mMemFactorDurations;
                int i = this.mProcessStats.mMemFactor;
                jArr[i] = jArr[i] + (now - this.mProcessStats.mStartTime);
            }
            this.mProcessStats.mMemFactor = memFactor;
            this.mProcessStats.mStartTime = now;
            ArrayMap<String, SparseArray<ProcessStats.PackageState>> pmap = this.mProcessStats.mPackages.getMap();
            for (int i2 = 0; i2 < pmap.size(); i2++) {
                SparseArray<ProcessStats.PackageState> uids = pmap.valueAt(i2);
                for (int j = 0; j < uids.size(); j++) {
                    ProcessStats.PackageState pkg = uids.valueAt(j);
                    ArrayMap<String, ProcessStats.ServiceState> services = pkg.mServices;
                    for (int k = 0; k < services.size(); k++) {
                        ProcessStats.ServiceState service = services.valueAt(k);
                        if (service.isInUse()) {
                            if (service.mStartedState != -1) {
                                service.setStarted(true, memFactor, now);
                            }
                            if (service.mBoundState != -1) {
                                service.setBound(true, memFactor, now);
                            }
                            if (service.mExecState != -1) {
                                service.setExecuting(true, memFactor, now);
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public int getMemFactorLocked() {
        if (this.mProcessStats.mMemFactor != -1) {
            return this.mProcessStats.mMemFactor;
        }
        return 0;
    }

    public boolean shouldWriteNowLocked(long now) {
        if (now > this.mLastWriteTime + WRITE_PERIOD) {
            if (SystemClock.elapsedRealtime() > this.mProcessStats.mTimePeriodStartRealtime + ProcessStats.COMMIT_PERIOD) {
                this.mCommitPending = true;
                return true;
            }
            return true;
        }
        return false;
    }

    public void shutdownLocked() {
        Slog.w(TAG, "Writing process stats before shutdown...");
        this.mProcessStats.mFlags |= 2;
        writeStateSyncLocked();
        this.mShuttingDown = true;
    }

    public void writeStateAsyncLocked() {
        writeStateLocked(false);
    }

    public void writeStateSyncLocked() {
        writeStateLocked(true);
    }

    private void writeStateLocked(boolean sync) {
        if (this.mShuttingDown) {
            return;
        }
        boolean commitPending = this.mCommitPending;
        this.mCommitPending = false;
        writeStateLocked(sync, commitPending);
    }

    public void writeStateLocked(boolean sync, boolean commit) {
        synchronized (this.mPendingWriteLock) {
            long now = SystemClock.uptimeMillis();
            if (this.mPendingWrite == null || !this.mPendingWriteCommitted) {
                this.mPendingWrite = Parcel.obtain();
                this.mProcessStats.mTimePeriodEndRealtime = SystemClock.elapsedRealtime();
                if (commit) {
                    this.mProcessStats.mFlags |= 1;
                }
                this.mProcessStats.writeToParcel(this.mPendingWrite, 0);
                this.mPendingWriteFile = new AtomicFile(this.mFile.getBaseFile());
                this.mPendingWriteCommitted = commit;
            }
            if (commit) {
                this.mProcessStats.resetSafely();
                updateFile();
            }
            this.mLastWriteTime = SystemClock.uptimeMillis();
            Slog.i(TAG, "Prepared write state in " + (SystemClock.uptimeMillis() - now) + "ms");
            if (!sync) {
                BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.am.ProcessStatsService.2
                    @Override // java.lang.Runnable
                    public void run() {
                        ProcessStatsService.this.performWriteState();
                    }
                });
            } else {
                performWriteState();
            }
        }
    }

    private void updateFile() {
        this.mFile = new AtomicFile(new File(this.mBaseDir, STATE_FILE_PREFIX + this.mProcessStats.mTimePeriodStartClockStr + STATE_FILE_SUFFIX));
        this.mLastWriteTime = SystemClock.uptimeMillis();
    }

    boolean readLocked(ProcessStats stats, AtomicFile file) {
        try {
            FileInputStream stream = file.openRead();
            stats.read(stream);
            stream.close();
            if (stats.mReadError != null) {
                Slog.w(TAG, "Ignoring existing stats; " + stats.mReadError);
                return false;
            }
            return true;
        } catch (Throwable e) {
            stats.mReadError = "caught exception: " + e;
            Slog.e(TAG, "Error reading process statistics", e);
            return false;
        }
    }

    private ArrayList<String> getCommittedFiles(int minNum, boolean inclCurrent, boolean inclCheckedIn) {
        File[] files = this.mBaseDir.listFiles();
        if (files == null || files.length <= minNum) {
            return null;
        }
        ArrayList<String> filesArray = new ArrayList<>(files.length);
        String currentFile = this.mFile.getBaseFile().getPath();
        for (File file : files) {
            String fileStr = file.getPath();
            if ((inclCheckedIn || !fileStr.endsWith(STATE_FILE_CHECKIN_SUFFIX)) && (inclCurrent || !fileStr.equals(currentFile))) {
                filesArray.add(fileStr);
            }
        }
        Collections.sort(filesArray);
        return filesArray;
    }

    public void trimHistoricStatesWriteLocked() {
        ArrayList<String> filesArray = getCommittedFiles(8, false, true);
        if (filesArray == null) {
            return;
        }
        while (filesArray.size() > 8) {
            String file = filesArray.remove(0);
            Slog.i(TAG, "Pruning old procstats: " + file);
            new File(file).delete();
        }
    }

    boolean dumpFilteredProcessesCsvLocked(PrintWriter pw, String header, boolean sepScreenStates, int[] screenStates, boolean sepMemStates, int[] memStates, boolean sepProcStates, int[] procStates, long now, String reqPackage) {
        ArrayList<ProcessStats.ProcessState> procs = this.mProcessStats.collectProcessesLocked(screenStates, memStates, procStates, procStates, now, reqPackage, false);
        if (procs.size() > 0) {
            if (header != null) {
                pw.println(header);
            }
            ProcessStats.dumpProcessListCsv(pw, procs, sepScreenStates, screenStates, sepMemStates, memStates, sepProcStates, procStates, now);
            return true;
        }
        return false;
    }

    static int[] parseStateList(String[] states, int mult, String arg, boolean[] outSep, String[] outError) {
        ArrayList<Integer> res = new ArrayList<>();
        int lastPos = 0;
        int i = 0;
        while (i <= arg.length()) {
            char c = i < arg.length() ? arg.charAt(i) : (char) 0;
            if (c == ',' || c == '+' || c == ' ' || c == 0) {
                boolean isSep = c == ',';
                if (lastPos == 0) {
                    outSep[0] = isSep;
                } else if (c != 0 && outSep[0] != isSep) {
                    outError[0] = "inconsistent separators (can't mix ',' with '+')";
                    return null;
                }
                if (lastPos < i - 1) {
                    String str = arg.substring(lastPos, i);
                    int j = 0;
                    while (true) {
                        if (j < states.length) {
                            if (!str.equals(states[j])) {
                                j++;
                            } else {
                                res.add(Integer.valueOf(j));
                                str = null;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    if (str != null) {
                        outError[0] = "invalid word \"" + str + Separators.DOUBLE_QUOTE;
                        return null;
                    }
                }
                lastPos = i + 1;
            }
            i++;
        }
        int[] finalRes = new int[res.size()];
        for (int i2 = 0; i2 < res.size(); i2++) {
            finalRes[i2] = res.get(i2).intValue() * mult;
        }
        return finalRes;
    }

    @Override // com.android.internal.app.IProcessStats
    public int getCurrentMemoryState() {
        int i;
        synchronized (this.mAm) {
            i = this.mLastMemOnlyState;
        }
        return i;
    }

    private static void dumpHelp(PrintWriter pw) {
        pw.println("Process stats (procstats) dump options:");
        pw.println("    [--checkin|-c|--csv] [--csv-screen] [--csv-proc] [--csv-mem]");
        pw.println("    [--details] [--full-details] [--current] [--hours] [--active]");
        pw.println("    [--commit] [--reset] [--clear] [--write] [-h] [<package.name>]");
        pw.println("  --checkin: perform a checkin: print and delete old committed states.");
        pw.println("  --c: print only state in checkin format.");
        pw.println("  --csv: output data suitable for putting in a spreadsheet.");
        pw.println("  --csv-screen: on, off.");
        pw.println("  --csv-mem: norm, mod, low, crit.");
        pw.println("  --csv-proc: pers, top, fore, vis, precept, backup,");
        pw.println("    service, home, prev, cached");
        pw.println("  --details: dump per-package details, not just summary.");
        pw.println("  --full-details: dump all timing and active state details.");
        pw.println("  --current: only dump current state.");
        pw.println("  --hours: aggregate over about N last hours.");
        pw.println("  --active: only show currently active processes/services.");
        pw.println("  --commit: commit current stats to disk and reset to start new stats.");
        pw.println("  --reset: reset current stats, without committing.");
        pw.println("  --clear: clear all stats; does both --reset and deletes old stats.");
        pw.println("  --write: write current in-memory stats to disk.");
        pw.println("  --read: replace current stats with last-written stats.");
        pw.println("  -a: print everything.");
        pw.println("  -h: print this help text.");
        pw.println("  <package.name>: optional name of package to filter output by.");
    }
}