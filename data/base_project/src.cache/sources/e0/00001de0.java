package com.android.server.am;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.bluetooth.BluetoothInputDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.app.IUsageStats;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.PkgUsageStats;
import com.android.internal.util.FastXmlSerializer;
import gov.nist.core.Separators;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: UsageStatsService.class */
public final class UsageStatsService extends IUsageStats.Stub {
    public static final String SERVICE_NAME = "usagestats";
    private static final boolean localLOGV = false;
    private static final boolean REPORT_UNEXPECTED = false;
    private static final String TAG = "UsageStats";
    private static final int VERSION = 1008;
    private static final int CHECKIN_VERSION = 4;
    private static final String FILE_PREFIX = "usage-";
    private static final String FILE_HISTORY = "usage-history.xml";
    private static final int FILE_WRITE_INTERVAL = 1800000;
    private static final int MAX_NUM_FILES = 5;
    private static final int NUM_LAUNCH_TIME_BINS = 10;
    private static final int[] LAUNCH_TIME_BINS = {250, 500, 750, 1000, 1500, 2000, ConnectivityManager.CONNECTIVITY_CHANGE_DELAY_DEFAULT, 4000, BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED};
    static IUsageStats sService;
    private Context mContext;
    private PackageMonitor mPackageMonitor;
    private String mLastResumedPkg;
    private String mLastResumedComp;
    private boolean mIsResumed;
    private File mFile;
    private AtomicFile mHistoryFile;
    private String mFileLeaf;
    private File mDir;
    private final AtomicInteger mLastWriteDay = new AtomicInteger(-1);
    private final AtomicLong mLastWriteElapsedTime = new AtomicLong(0);
    private final AtomicBoolean mUnforcedDiskWriteRunning = new AtomicBoolean(false);
    private final ArrayMap<String, PkgUsageStatsExtended> mStats = new ArrayMap<>();
    private final ArrayMap<String, ArrayMap<String, Long>> mLastResumeTimes = new ArrayMap<>();
    final Object mStatsLock = new Object();
    final Object mFileLock = new Object();
    private Calendar mCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.UsageStatsService.readHistoryStatsFLOCK(android.util.AtomicFile):void, file: UsageStatsService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void readHistoryStatsFLOCK(android.util.AtomicFile r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.UsageStatsService.readHistoryStatsFLOCK(android.util.AtomicFile):void, file: UsageStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UsageStatsService.readHistoryStatsFLOCK(android.util.AtomicFile):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.UsageStatsService.writeStatsFLOCK(java.io.File):void, file: UsageStatsService.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void writeStatsFLOCK(java.io.File r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.UsageStatsService.writeStatsFLOCK(java.io.File):void, file: UsageStatsService.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UsageStatsService.writeStatsFLOCK(java.io.File):void");
    }

    static /* synthetic */ void access$100(UsageStatsService x0, boolean x1, boolean x2) {
        x0.writeStatsToFile(x1, x2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: UsageStatsService$TimeStats.class */
    public static class TimeStats {
        int count;
        int[] times;

        TimeStats() {
            this.times = new int[10];
        }

        void incCount() {
            this.count++;
        }

        void add(int val) {
            int[] bins = UsageStatsService.LAUNCH_TIME_BINS;
            for (int i = 0; i < 9; i++) {
                if (val < bins[i]) {
                    int[] iArr = this.times;
                    int i2 = i;
                    iArr[i2] = iArr[i2] + 1;
                    return;
                }
            }
            int[] iArr2 = this.times;
            iArr2[9] = iArr2[9] + 1;
        }

        TimeStats(Parcel in) {
            this.times = new int[10];
            this.count = in.readInt();
            int[] localTimes = this.times;
            for (int i = 0; i < 10; i++) {
                localTimes[i] = in.readInt();
            }
        }

        void writeToParcel(Parcel out) {
            out.writeInt(this.count);
            int[] localTimes = this.times;
            for (int i = 0; i < 10; i++) {
                out.writeInt(localTimes[i]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: UsageStatsService$PkgUsageStatsExtended.class */
    public class PkgUsageStatsExtended {
        final ArrayMap<String, TimeStats> mLaunchTimes;
        final ArrayMap<String, TimeStats> mFullyDrawnTimes;
        int mLaunchCount;
        long mUsageTime;
        long mPausedTime;
        long mResumedTime;

        PkgUsageStatsExtended() {
            this.mLaunchTimes = new ArrayMap<>();
            this.mFullyDrawnTimes = new ArrayMap<>();
            this.mLaunchCount = 0;
            this.mUsageTime = 0L;
        }

        PkgUsageStatsExtended(Parcel in) {
            this.mLaunchTimes = new ArrayMap<>();
            this.mFullyDrawnTimes = new ArrayMap<>();
            this.mLaunchCount = in.readInt();
            this.mUsageTime = in.readLong();
            int numLaunchTimeStats = in.readInt();
            this.mLaunchTimes.ensureCapacity(numLaunchTimeStats);
            for (int i = 0; i < numLaunchTimeStats; i++) {
                String comp = in.readString();
                TimeStats times = new TimeStats(in);
                this.mLaunchTimes.put(comp, times);
            }
            int numFullyDrawnTimeStats = in.readInt();
            this.mFullyDrawnTimes.ensureCapacity(numFullyDrawnTimeStats);
            for (int i2 = 0; i2 < numFullyDrawnTimeStats; i2++) {
                String comp2 = in.readString();
                TimeStats times2 = new TimeStats(in);
                this.mFullyDrawnTimes.put(comp2, times2);
            }
        }

        void updateResume(String comp, boolean launched) {
            if (launched) {
                this.mLaunchCount++;
            }
            this.mResumedTime = SystemClock.elapsedRealtime();
        }

        void updatePause() {
            this.mPausedTime = SystemClock.elapsedRealtime();
            this.mUsageTime += this.mPausedTime - this.mResumedTime;
        }

        void addLaunchCount(String comp) {
            TimeStats times = this.mLaunchTimes.get(comp);
            if (times == null) {
                times = new TimeStats();
                this.mLaunchTimes.put(comp, times);
            }
            times.incCount();
        }

        void addLaunchTime(String comp, int millis) {
            TimeStats times = this.mLaunchTimes.get(comp);
            if (times == null) {
                times = new TimeStats();
                this.mLaunchTimes.put(comp, times);
            }
            times.add(millis);
        }

        void addFullyDrawnTime(String comp, int millis) {
            TimeStats times = this.mFullyDrawnTimes.get(comp);
            if (times == null) {
                times = new TimeStats();
                this.mFullyDrawnTimes.put(comp, times);
            }
            times.add(millis);
        }

        void writeToParcel(Parcel out) {
            out.writeInt(this.mLaunchCount);
            out.writeLong(this.mUsageTime);
            int numLaunchTimeStats = this.mLaunchTimes.size();
            out.writeInt(numLaunchTimeStats);
            for (int i = 0; i < numLaunchTimeStats; i++) {
                out.writeString(this.mLaunchTimes.keyAt(i));
                this.mLaunchTimes.valueAt(i).writeToParcel(out);
            }
            int numFullyDrawnTimeStats = this.mFullyDrawnTimes.size();
            out.writeInt(numFullyDrawnTimeStats);
            for (int i2 = 0; i2 < numFullyDrawnTimeStats; i2++) {
                out.writeString(this.mFullyDrawnTimes.keyAt(i2));
                this.mFullyDrawnTimes.valueAt(i2).writeToParcel(out);
            }
        }

        void clear() {
            this.mLaunchTimes.clear();
            this.mFullyDrawnTimes.clear();
            this.mLaunchCount = 0;
            this.mUsageTime = 0L;
        }
    }

    UsageStatsService(String dir) {
        this.mDir = new File(dir);
        this.mDir.mkdir();
        File parentDir = this.mDir.getParentFile();
        String[] fList = parentDir.list();
        if (fList != null) {
            String prefix = this.mDir.getName() + Separators.DOT;
            int i = fList.length;
            while (i > 0) {
                i--;
                if (fList[i].startsWith(prefix)) {
                    Slog.i(TAG, "Deleting old usage file: " + fList[i]);
                    new File(parentDir, fList[i]).delete();
                }
            }
        }
        this.mFileLeaf = getCurrentDateStr(FILE_PREFIX);
        this.mFile = new File(this.mDir, this.mFileLeaf);
        this.mHistoryFile = new AtomicFile(new File(this.mDir, FILE_HISTORY));
        readStatsFromFile();
        readHistoryStatsFromFile();
        this.mLastWriteElapsedTime.set(SystemClock.elapsedRealtime());
        this.mLastWriteDay.set(this.mCal.get(6));
    }

    private String getCurrentDateStr(String prefix) {
        StringBuilder sb = new StringBuilder();
        synchronized (this.mCal) {
            this.mCal.setTimeInMillis(System.currentTimeMillis());
            if (prefix != null) {
                sb.append(prefix);
            }
            sb.append(this.mCal.get(1));
            int mm = (this.mCal.get(2) - 0) + 1;
            if (mm < 10) {
                sb.append("0");
            }
            sb.append(mm);
            int dd = this.mCal.get(5);
            if (dd < 10) {
                sb.append("0");
            }
            sb.append(dd);
        }
        return sb.toString();
    }

    private Parcel getParcelForFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        byte[] raw = readFully(stream);
        Parcel in = Parcel.obtain();
        in.unmarshall(raw, 0, raw.length);
        in.setDataPosition(0);
        stream.close();
        return in;
    }

    private void readStatsFromFile() {
        File newFile = this.mFile;
        synchronized (this.mFileLock) {
            try {
                if (newFile.exists()) {
                    readStatsFLOCK(newFile);
                } else {
                    checkFileLimitFLOCK();
                    newFile.createNewFile();
                }
            } catch (IOException e) {
                Slog.w(TAG, "Error : " + e + " reading data from file:" + newFile);
            }
        }
    }

    private void readStatsFLOCK(File file) throws IOException {
        Parcel in = getParcelForFile(file);
        int vers = in.readInt();
        if (vers != 1008) {
            Slog.w(TAG, "Usage stats version changed; dropping");
            return;
        }
        int N = in.readInt();
        while (N > 0) {
            N--;
            String pkgName = in.readString();
            if (pkgName != null) {
                PkgUsageStatsExtended pus = new PkgUsageStatsExtended(in);
                synchronized (this.mStatsLock) {
                    this.mStats.put(pkgName, pus);
                }
            } else {
                return;
            }
        }
    }

    private void readHistoryStatsFromFile() {
        synchronized (this.mFileLock) {
            if (this.mHistoryFile.getBaseFile().exists()) {
                readHistoryStatsFLOCK(this.mHistoryFile);
            }
        }
    }

    private ArrayList<String> getUsageStatsFileListFLOCK() {
        String[] fList = this.mDir.list();
        if (fList == null) {
            return null;
        }
        ArrayList<String> fileList = new ArrayList<>();
        for (String file : fList) {
            if (file.startsWith(FILE_PREFIX)) {
                if (file.endsWith(".bak")) {
                    new File(this.mDir, file).delete();
                } else {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    private void checkFileLimitFLOCK() {
        ArrayList<String> fileList = getUsageStatsFileListFLOCK();
        if (fileList == null) {
            return;
        }
        int count = fileList.size();
        if (count <= 5) {
            return;
        }
        Collections.sort(fileList);
        int count2 = count - 5;
        for (int i = 0; i < count2; i++) {
            String fileName = fileList.get(i);
            File file = new File(this.mDir, fileName);
            Slog.i(TAG, "Deleting usage file : " + fileName);
            file.delete();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeStatsToFile(boolean force, boolean forceWriteHistoryStats) {
        int curDay;
        synchronized (this.mCal) {
            this.mCal.setTimeInMillis(System.currentTimeMillis());
            curDay = this.mCal.get(6);
        }
        boolean dayChanged = curDay != this.mLastWriteDay.get();
        long currElapsedTime = SystemClock.elapsedRealtime();
        if (!force) {
            if ((dayChanged || currElapsedTime - this.mLastWriteElapsedTime.get() >= AlarmManager.INTERVAL_HALF_HOUR) && this.mUnforcedDiskWriteRunning.compareAndSet(false, true)) {
                new Thread("UsageStatsService_DiskWriter") { // from class: com.android.server.am.UsageStatsService.1
                    /*  JADX ERROR: Method load error
                        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.UsageStatsService.1.run():void, file: UsageStatsService$1.class
                        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
                        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
                        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
                        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
                        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
                        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
                        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
                        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
                        	... 6 more
                        */
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        /*
                        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.UsageStatsService.1.run():void, file: UsageStatsService$1.class
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.UsageStatsService.AnonymousClass1.run():void");
                    }
                }.start();
                return;
            }
            return;
        }
        synchronized (this.mFileLock) {
            this.mFileLeaf = getCurrentDateStr(FILE_PREFIX);
            File backupFile = null;
            if (this.mFile != null && this.mFile.exists()) {
                backupFile = new File(this.mFile.getPath() + ".bak");
                if (!backupFile.exists()) {
                    if (!this.mFile.renameTo(backupFile)) {
                        Slog.w(TAG, "Failed to persist new stats");
                        return;
                    }
                } else {
                    this.mFile.delete();
                }
            }
            try {
                writeStatsFLOCK(this.mFile);
                this.mLastWriteElapsedTime.set(currElapsedTime);
                if (dayChanged) {
                    this.mLastWriteDay.set(curDay);
                    synchronized (this.mStats) {
                        this.mStats.clear();
                    }
                    this.mFile = new File(this.mDir, this.mFileLeaf);
                    checkFileLimitFLOCK();
                }
                if (dayChanged || forceWriteHistoryStats) {
                    writeHistoryStatsFLOCK(this.mHistoryFile);
                }
                if (backupFile != null) {
                    backupFile.delete();
                }
            } catch (IOException e) {
                Slog.w(TAG, "Failed writing stats to file:" + this.mFile);
                if (backupFile != null) {
                    this.mFile.delete();
                    backupFile.renameTo(this.mFile);
                }
            }
        }
    }

    private void writeStatsToParcelFLOCK(Parcel out) {
        synchronized (this.mStatsLock) {
            out.writeInt(1008);
            Set<String> keys = this.mStats.keySet();
            out.writeInt(keys.size());
            for (String key : keys) {
                PkgUsageStatsExtended pus = this.mStats.get(key);
                out.writeString(key);
                pus.writeToParcel(out);
            }
        }
    }

    private void filterHistoryStats() {
        synchronized (this.mStatsLock) {
            IPackageManager pm = AppGlobals.getPackageManager();
            int i = 0;
            while (i < this.mLastResumeTimes.size()) {
                String pkg = this.mLastResumeTimes.keyAt(i);
                try {
                    if (pm.getPackageUid(pkg, 0) < 0) {
                        this.mLastResumeTimes.removeAt(i);
                        i--;
                    }
                } catch (RemoteException e) {
                }
                i++;
            }
        }
    }

    private void writeHistoryStatsFLOCK(AtomicFile historyFile) {
        FileOutputStream fos = null;
        try {
            fos = historyFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, "utf-8");
            out.startDocument(null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag(null, "usage-history");
            synchronized (this.mStatsLock) {
                for (int i = 0; i < this.mLastResumeTimes.size(); i++) {
                    out.startTag(null, "pkg");
                    out.attribute(null, "name", this.mLastResumeTimes.keyAt(i));
                    ArrayMap<String, Long> comp = this.mLastResumeTimes.valueAt(i);
                    for (int j = 0; j < comp.size(); j++) {
                        out.startTag(null, "comp");
                        out.attribute(null, "name", comp.keyAt(j));
                        out.attribute(null, "lrt", comp.valueAt(j).toString());
                        out.endTag(null, "comp");
                    }
                    out.endTag(null, "pkg");
                }
            }
            out.endTag(null, "usage-history");
            out.endDocument();
            historyFile.finishWrite(fos);
        } catch (IOException e) {
            Slog.w(TAG, "Error writing history stats" + e);
            if (fos != null) {
                historyFile.failWrite(fos);
            }
        }
    }

    public void publish(Context context) {
        this.mContext = context;
        ServiceManager.addService(SERVICE_NAME, asBinder());
    }

    public void monitorPackages() {
        this.mPackageMonitor = new PackageMonitor() { // from class: com.android.server.am.UsageStatsService.2
            @Override // com.android.internal.content.PackageMonitor
            public void onPackageRemovedAllUsers(String packageName, int uid) {
                synchronized (UsageStatsService.this.mStatsLock) {
                    UsageStatsService.this.mLastResumeTimes.remove(packageName);
                }
            }
        };
        this.mPackageMonitor.register(this.mContext, null, true);
        filterHistoryStats();
    }

    public void shutdown() {
        if (this.mPackageMonitor != null) {
            this.mPackageMonitor.unregister();
        }
        Slog.i(TAG, "Writing usage stats before shutdown...");
        writeStatsToFile(true, true);
    }

    public static IUsageStats getService() {
        if (sService != null) {
            return sService;
        }
        IBinder b = ServiceManager.getService(SERVICE_NAME);
        sService = asInterface(b);
        return sService;
    }

    @Override // com.android.internal.app.IUsageStats
    public void noteResumeComponent(ComponentName componentName) {
        PkgUsageStatsExtended pus;
        enforceCallingPermission();
        synchronized (this.mStatsLock) {
            if (componentName != null) {
                String pkgName = componentName.getPackageName();
                if (pkgName != null) {
                    boolean samePackage = pkgName.equals(this.mLastResumedPkg);
                    if (this.mIsResumed && this.mLastResumedPkg != null && (pus = this.mStats.get(this.mLastResumedPkg)) != null) {
                        pus.updatePause();
                    }
                    boolean sameComp = samePackage && componentName.getClassName().equals(this.mLastResumedComp);
                    this.mIsResumed = true;
                    this.mLastResumedPkg = pkgName;
                    this.mLastResumedComp = componentName.getClassName();
                    PkgUsageStatsExtended pus2 = this.mStats.get(pkgName);
                    if (pus2 == null) {
                        pus2 = new PkgUsageStatsExtended();
                        this.mStats.put(pkgName, pus2);
                    }
                    pus2.updateResume(this.mLastResumedComp, !samePackage);
                    if (!sameComp) {
                        pus2.addLaunchCount(this.mLastResumedComp);
                    }
                    ArrayMap<String, Long> componentResumeTimes = this.mLastResumeTimes.get(pkgName);
                    if (componentResumeTimes == null) {
                        componentResumeTimes = new ArrayMap<>();
                        this.mLastResumeTimes.put(pkgName, componentResumeTimes);
                    }
                    componentResumeTimes.put(this.mLastResumedComp, Long.valueOf(System.currentTimeMillis()));
                }
            }
        }
    }

    @Override // com.android.internal.app.IUsageStats
    public void notePauseComponent(ComponentName componentName) {
        enforceCallingPermission();
        synchronized (this.mStatsLock) {
            if (componentName != null) {
                String pkgName = componentName.getPackageName();
                if (pkgName != null) {
                    if (this.mIsResumed) {
                        this.mIsResumed = false;
                        PkgUsageStatsExtended pus = this.mStats.get(pkgName);
                        if (pus == null) {
                            Slog.i(TAG, "No package stats for pkg:" + pkgName);
                            return;
                        }
                        pus.updatePause();
                        writeStatsToFile(false, false);
                    }
                }
            }
        }
    }

    @Override // com.android.internal.app.IUsageStats
    public void noteLaunchTime(ComponentName componentName, int millis) {
        String pkgName;
        enforceCallingPermission();
        if (componentName == null || (pkgName = componentName.getPackageName()) == null) {
            return;
        }
        writeStatsToFile(false, false);
        synchronized (this.mStatsLock) {
            PkgUsageStatsExtended pus = this.mStats.get(pkgName);
            if (pus != null) {
                pus.addLaunchTime(componentName.getClassName(), millis);
            }
        }
    }

    public void noteFullyDrawnTime(ComponentName componentName, int millis) {
        String pkgName;
        enforceCallingPermission();
        if (componentName == null || (pkgName = componentName.getPackageName()) == null) {
            return;
        }
        writeStatsToFile(false, false);
        synchronized (this.mStatsLock) {
            PkgUsageStatsExtended pus = this.mStats.get(pkgName);
            if (pus != null) {
                pus.addFullyDrawnTime(componentName.getClassName(), millis);
            }
        }
    }

    public void enforceCallingPermission() {
        if (Binder.getCallingPid() == Process.myPid()) {
            return;
        }
        this.mContext.enforcePermission(Manifest.permission.UPDATE_DEVICE_STATS, Binder.getCallingPid(), Binder.getCallingUid(), null);
    }

    @Override // com.android.internal.app.IUsageStats
    public PkgUsageStats getPkgUsageStats(ComponentName componentName) {
        String pkgName;
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS, null);
        if (componentName == null || (pkgName = componentName.getPackageName()) == null) {
            return null;
        }
        synchronized (this.mStatsLock) {
            PkgUsageStatsExtended pus = this.mStats.get(pkgName);
            Map<String, Long> lastResumeTimes = this.mLastResumeTimes.get(pkgName);
            if (pus == null && lastResumeTimes == null) {
                return null;
            }
            int launchCount = pus != null ? pus.mLaunchCount : 0;
            long usageTime = pus != null ? pus.mUsageTime : 0L;
            return new PkgUsageStats(pkgName, launchCount, usageTime, lastResumeTimes);
        }
    }

    @Override // com.android.internal.app.IUsageStats
    public PkgUsageStats[] getAllPkgUsageStats() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS, null);
        synchronized (this.mStatsLock) {
            int size = this.mLastResumeTimes.size();
            if (size <= 0) {
                return null;
            }
            PkgUsageStats[] retArr = new PkgUsageStats[size];
            for (int i = 0; i < size; i++) {
                String pkg = this.mLastResumeTimes.keyAt(i);
                long usageTime = 0;
                int launchCount = 0;
                PkgUsageStatsExtended pus = this.mStats.get(pkg);
                if (pus != null) {
                    usageTime = pus.mUsageTime;
                    launchCount = pus.mLaunchCount;
                }
                retArr[i] = new PkgUsageStats(pkg, launchCount, usageTime, this.mLastResumeTimes.valueAt(i));
            }
            return retArr;
        }
    }

    static byte[] readFully(FileInputStream stream) throws IOException {
        int pos = 0;
        byte[] data = new byte[stream.available()];
        while (true) {
            int amt = stream.read(data, pos, data.length - pos);
            if (amt <= 0) {
                return data;
            }
            pos += amt;
            int avail = stream.available();
            if (avail > data.length - pos) {
                byte[] newData = new byte[pos + avail];
                System.arraycopy(data, 0, newData, 0, pos);
                data = newData;
            }
        }
    }

    private void collectDumpInfoFLOCK(PrintWriter pw, boolean isCompactOutput, boolean deleteAfterPrint, HashSet<String> packages) {
        List<String> fileList = getUsageStatsFileListFLOCK();
        if (fileList == null) {
            return;
        }
        Collections.sort(fileList);
        for (String file : fileList) {
            if (!deleteAfterPrint || !file.equalsIgnoreCase(this.mFileLeaf)) {
                File dFile = new File(this.mDir, file);
                String dateStr = file.substring(FILE_PREFIX.length());
                if (dateStr.length() <= 0 || (dateStr.charAt(0) > '0' && dateStr.charAt(0) < '9')) {
                    try {
                        Parcel in = getParcelForFile(dFile);
                        collectDumpInfoFromParcelFLOCK(in, pw, dateStr, isCompactOutput, packages);
                        if (deleteAfterPrint) {
                            dFile.delete();
                        }
                    } catch (FileNotFoundException e) {
                        Slog.w(TAG, "Failed with " + e + " when collecting dump info from file : " + file);
                        return;
                    } catch (IOException e2) {
                        Slog.w(TAG, "Failed with " + e2 + " when collecting dump info from file : " + file);
                    }
                }
            }
        }
    }

    private void collectDumpInfoFromParcelFLOCK(Parcel in, PrintWriter pw, String date, boolean isCompactOutput, HashSet<String> packages) {
        StringBuilder sb = new StringBuilder(512);
        if (isCompactOutput) {
            sb.append("D:");
            sb.append(4);
            sb.append(',');
        } else {
            sb.append("Date: ");
        }
        sb.append(date);
        int vers = in.readInt();
        if (vers != 1008) {
            sb.append(" (old data version)");
            pw.println(sb.toString());
            return;
        }
        pw.println(sb.toString());
        int N = in.readInt();
        while (N > 0) {
            N--;
            String pkgName = in.readString();
            if (pkgName != null) {
                sb.setLength(0);
                PkgUsageStatsExtended pus = new PkgUsageStatsExtended(in);
                if (packages == null || packages.contains(pkgName)) {
                    if (isCompactOutput) {
                        sb.append("P:");
                        sb.append(pkgName);
                        sb.append(',');
                        sb.append(pus.mLaunchCount);
                        sb.append(',');
                        sb.append(pus.mUsageTime);
                        sb.append('\n');
                        int NLT = pus.mLaunchTimes.size();
                        for (int i = 0; i < NLT; i++) {
                            sb.append("A:");
                            String activity = pus.mLaunchTimes.keyAt(i);
                            sb.append(activity);
                            TimeStats times = pus.mLaunchTimes.valueAt(i);
                            sb.append(',');
                            sb.append(times.count);
                            for (int j = 0; j < 10; j++) {
                                sb.append(Separators.COMMA);
                                sb.append(times.times[j]);
                            }
                            sb.append('\n');
                        }
                        int NFDT = pus.mFullyDrawnTimes.size();
                        for (int i2 = 0; i2 < NFDT; i2++) {
                            sb.append("A:");
                            String activity2 = pus.mFullyDrawnTimes.keyAt(i2);
                            sb.append(activity2);
                            TimeStats times2 = pus.mFullyDrawnTimes.valueAt(i2);
                            for (int j2 = 0; j2 < 10; j2++) {
                                sb.append(Separators.COMMA);
                                sb.append(times2.times[j2]);
                            }
                            sb.append('\n');
                        }
                    } else {
                        sb.append("  ");
                        sb.append(pkgName);
                        sb.append(": ");
                        sb.append(pus.mLaunchCount);
                        sb.append(" times, ");
                        sb.append(pus.mUsageTime);
                        sb.append(" ms");
                        sb.append('\n');
                        int NLT2 = pus.mLaunchTimes.size();
                        for (int i3 = 0; i3 < NLT2; i3++) {
                            sb.append("    ");
                            sb.append(pus.mLaunchTimes.keyAt(i3));
                            TimeStats times3 = pus.mLaunchTimes.valueAt(i3);
                            sb.append(": ");
                            sb.append(times3.count);
                            sb.append(" starts");
                            int lastBin = 0;
                            for (int j3 = 0; j3 < 9; j3++) {
                                if (times3.times[j3] != 0) {
                                    sb.append(", ");
                                    sb.append(lastBin);
                                    sb.append('-');
                                    sb.append(LAUNCH_TIME_BINS[j3]);
                                    sb.append("ms=");
                                    sb.append(times3.times[j3]);
                                }
                                lastBin = LAUNCH_TIME_BINS[j3];
                            }
                            if (times3.times[9] != 0) {
                                sb.append(", ");
                                sb.append(">=");
                                sb.append(lastBin);
                                sb.append("ms=");
                                sb.append(times3.times[9]);
                            }
                            sb.append('\n');
                        }
                        int NFDT2 = pus.mFullyDrawnTimes.size();
                        for (int i4 = 0; i4 < NFDT2; i4++) {
                            sb.append("    ");
                            sb.append(pus.mFullyDrawnTimes.keyAt(i4));
                            TimeStats times4 = pus.mFullyDrawnTimes.valueAt(i4);
                            sb.append(": fully drawn ");
                            boolean needComma = false;
                            int lastBin2 = 0;
                            for (int j4 = 0; j4 < 9; j4++) {
                                if (times4.times[j4] != 0) {
                                    if (needComma) {
                                        sb.append(", ");
                                    } else {
                                        needComma = true;
                                    }
                                    sb.append(lastBin2);
                                    sb.append('-');
                                    sb.append(LAUNCH_TIME_BINS[j4]);
                                    sb.append("ms=");
                                    sb.append(times4.times[j4]);
                                }
                                lastBin2 = LAUNCH_TIME_BINS[j4];
                            }
                            if (times4.times[9] != 0) {
                                if (needComma) {
                                    sb.append(", ");
                                }
                                sb.append(">=");
                                sb.append(lastBin2);
                                sb.append("ms=");
                                sb.append(times4.times[9]);
                            }
                            sb.append('\n');
                        }
                    }
                }
                pw.write(sb.toString());
            } else {
                return;
            }
        }
    }

    private static boolean scanArgs(String[] args, String value) {
        if (args != null) {
            for (String arg : args) {
                if (value.equals(arg)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static String scanArgsData(String[] args, String value) {
        if (args != null) {
            int N = args.length;
            for (int i = 0; i < N; i++) {
                if (value.equals(args[i])) {
                    int i2 = i + 1;
                    if (i2 < N) {
                        return args[i2];
                    }
                    return null;
                }
            }
            return null;
        }
        return null;
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump UsageStats from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " without permission " + Manifest.permission.DUMP);
            return;
        }
        boolean isCheckinRequest = scanArgs(args, "--checkin");
        boolean isCompactOutput = isCheckinRequest || scanArgs(args, "-c");
        boolean deleteAfterPrint = isCheckinRequest || scanArgs(args, "-d");
        String rawPackages = scanArgsData(args, "--packages");
        if (!deleteAfterPrint) {
            writeStatsToFile(true, false);
        }
        HashSet<String> packages = null;
        if (rawPackages != null) {
            if (!"*".equals(rawPackages)) {
                String[] names = rawPackages.split(Separators.COMMA);
                for (String n : names) {
                    if (packages == null) {
                        packages = new HashSet<>();
                    }
                    packages.add(n);
                }
            }
        } else if (isCheckinRequest) {
            Slog.w(TAG, "Checkin without packages");
            return;
        }
        synchronized (this.mFileLock) {
            collectDumpInfoFLOCK(pw, isCompactOutput, deleteAfterPrint, packages);
        }
    }
}