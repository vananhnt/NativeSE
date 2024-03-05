package com.android.internal.os;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.media.AudioService;
import android.net.ConnectivityManager;
import android.net.NetworkStats;
import android.os.BatteryStats;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;
import android.os.Process;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.WorkSource;
import android.telephony.SignalStrength;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.LogWriter;
import android.util.Printer;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.net.NetworkStatsFactory;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.JournaledFile;
import com.android.server.NetworkManagementSocketTagger;
import com.google.android.collect.Sets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: BatteryStatsImpl.class */
public final class BatteryStatsImpl extends BatteryStats {
    private static final String TAG = "BatteryStatsImpl";
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_HISTORY = false;
    private static final boolean USE_OLD_HISTORY = false;
    private static final int MAGIC = -1166707595;
    private static final int VERSION = 66;
    private static final int MAX_HISTORY_ITEMS = 2000;
    private static final int MAX_MAX_HISTORY_ITEMS = 3000;
    private static final int MAX_WAKELOCKS_PER_UID = 50;
    private static final String BATCHED_WAKELOCK_NAME = "*overflow*";
    private static int sNumSpeedSteps;
    private final JournaledFile mFile;
    static final int MSG_UPDATE_WAKELOCKS = 1;
    static final int MSG_REPORT_POWER_CHANGE = 2;
    static final long DELAY_UPDATE_WAKELOCKS = 5000;
    private final MyHandler mHandler;
    private BatteryCallback mCallback;
    final SparseArray<Uid> mUidStats;
    final ArrayList<StopwatchTimer> mPartialTimers;
    final ArrayList<StopwatchTimer> mFullTimers;
    final ArrayList<StopwatchTimer> mWindowTimers;
    final SparseArray<ArrayList<StopwatchTimer>> mSensorTimers;
    final ArrayList<StopwatchTimer> mWifiRunningTimers;
    final ArrayList<StopwatchTimer> mFullWifiLockTimers;
    final ArrayList<StopwatchTimer> mWifiMulticastTimers;
    final ArrayList<StopwatchTimer> mWifiScanTimers;
    final ArrayList<StopwatchTimer> mLastPartialTimers;
    final ArrayList<Unpluggable> mUnpluggables;
    boolean mShuttingDown;
    long mHistoryBaseTime;
    boolean mHaveBatteryLevel;
    boolean mRecordingHistory;
    int mNumHistoryItems;
    static final int MAX_HISTORY_BUFFER = 131072;
    static final int MAX_MAX_HISTORY_BUFFER = 147456;
    final Parcel mHistoryBuffer;
    final BatteryStats.HistoryItem mHistoryLastWritten;
    final BatteryStats.HistoryItem mHistoryLastLastWritten;
    final BatteryStats.HistoryItem mHistoryReadTmp;
    int mHistoryBufferLastPos;
    boolean mHistoryOverflow;
    long mLastHistoryTime;
    final BatteryStats.HistoryItem mHistoryCur;
    BatteryStats.HistoryItem mHistory;
    BatteryStats.HistoryItem mHistoryEnd;
    BatteryStats.HistoryItem mHistoryLastEnd;
    BatteryStats.HistoryItem mHistoryCache;
    private BatteryStats.HistoryItem mHistoryIterator;
    private boolean mReadOverflow;
    private boolean mIteratingHistory;
    int mStartCount;
    long mBatteryUptime;
    long mBatteryLastUptime;
    long mBatteryRealtime;
    long mBatteryLastRealtime;
    long mUptime;
    long mUptimeStart;
    long mLastUptime;
    long mRealtime;
    long mRealtimeStart;
    long mLastRealtime;
    boolean mScreenOn;
    StopwatchTimer mScreenOnTimer;
    int mScreenBrightnessBin;
    final StopwatchTimer[] mScreenBrightnessTimer;
    Counter mInputEventCounter;
    boolean mPhoneOn;
    StopwatchTimer mPhoneOnTimer;
    boolean mAudioOn;
    StopwatchTimer mAudioOnTimer;
    boolean mVideoOn;
    StopwatchTimer mVideoOnTimer;
    int mPhoneSignalStrengthBin;
    int mPhoneSignalStrengthBinRaw;
    final StopwatchTimer[] mPhoneSignalStrengthsTimer;
    StopwatchTimer mPhoneSignalScanningTimer;
    int mPhoneDataConnectionType;
    final StopwatchTimer[] mPhoneDataConnectionsTimer;
    final LongSamplingCounter[] mNetworkActivityCounters;
    boolean mWifiOn;
    StopwatchTimer mWifiOnTimer;
    int mWifiOnUid;
    boolean mGlobalWifiRunning;
    StopwatchTimer mGlobalWifiRunningTimer;
    boolean mBluetoothOn;
    StopwatchTimer mBluetoothOnTimer;
    BluetoothHeadset mBtHeadset;
    boolean mOnBattery;
    boolean mOnBatteryInternal;
    long mTrackBatteryPastUptime;
    long mTrackBatteryUptimeStart;
    long mTrackBatteryPastRealtime;
    long mTrackBatteryRealtimeStart;
    long mUnpluggedBatteryUptime;
    long mUnpluggedBatteryRealtime;
    int mDischargeStartLevel;
    int mDischargeUnplugLevel;
    int mDischargeCurrentLevel;
    int mLowDischargeAmountSinceCharge;
    int mHighDischargeAmountSinceCharge;
    int mDischargeScreenOnUnplugLevel;
    int mDischargeScreenOffUnplugLevel;
    int mDischargeAmountScreenOn;
    int mDischargeAmountScreenOnSinceCharge;
    int mDischargeAmountScreenOff;
    int mDischargeAmountScreenOffSinceCharge;
    long mLastWriteTime;
    private long mRadioDataUptime;
    private long mRadioDataStart;
    private int mBluetoothPingCount;
    private int mBluetoothPingStart;
    private int mPhoneServiceState;
    private int mPhoneServiceStateRaw;
    private int mPhoneSimStateRaw;
    private final HashMap<String, SamplingTimer> mKernelWakelockStats;
    private final String[] mProcWakelocksName;
    private final long[] mProcWakelocksData;
    private final Map<String, KernelWakelockStats> mProcWakelockFileStats;
    private HashMap<String, Integer> mUidCache;
    private final NetworkStatsFactory mNetworkStatsFactory;
    private NetworkStats mLastSnapshot;
    @GuardedBy("this")
    private HashSet<String> mMobileIfaces;
    @GuardedBy("this")
    private HashSet<String> mWifiIfaces;
    int mChangedBufferStates;
    int mChangedStates;
    int mWakeLockNesting;
    int mSensorNesting;
    int mGpsNesting;
    int mWifiFullLockNesting;
    int mWifiScanNesting;
    int mWifiMulticastNesting;
    private static final int BATTERY_PLUGGED_NONE = 0;
    Parcel mPendingWrite;
    final ReentrantLock mWriteLock;
    private static int sKernelWakelockUpdateVersion = 0;
    private static final int[] PROC_WAKELOCKS_FORMAT = {5129, 8201, 9, 9, 9, 8201};
    private static final int[] WAKEUP_SOURCES_FORMAT = {4105, 8457, 265, 265, 265, 265, 8457};
    public static final Parcelable.Creator<BatteryStatsImpl> CREATOR = new Parcelable.Creator<BatteryStatsImpl>() { // from class: com.android.internal.os.BatteryStatsImpl.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatteryStatsImpl createFromParcel(Parcel in) {
            return new BatteryStatsImpl(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BatteryStatsImpl[] newArray(int size) {
            return new BatteryStatsImpl[size];
        }
    };

    /* loaded from: BatteryStatsImpl$BatteryCallback.class */
    public interface BatteryCallback {
        void batteryNeedsCpuUpdate();

        void batteryPowerChanged(boolean z);
    }

    /* loaded from: BatteryStatsImpl$Unpluggable.class */
    public interface Unpluggable {
        void unplug(long j, long j2, long j3);

        void plug(long j, long j2, long j3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: BatteryStatsImpl$MyHandler.class */
    public final class MyHandler extends Handler {
        MyHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            BatteryCallback cb = BatteryStatsImpl.this.mCallback;
            switch (msg.what) {
                case 1:
                    if (cb != null) {
                        cb.batteryNeedsCpuUpdate();
                        return;
                    }
                    return;
                case 2:
                    if (cb != null) {
                        cb.batteryPowerChanged(msg.arg1 != 0);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    @Override // android.os.BatteryStats
    public Map<String, ? extends SamplingTimer> getKernelWakelockStats() {
        return this.mKernelWakelockStats;
    }

    public BatteryStatsImpl() {
        this.mUidStats = new SparseArray<>();
        this.mPartialTimers = new ArrayList<>();
        this.mFullTimers = new ArrayList<>();
        this.mWindowTimers = new ArrayList<>();
        this.mSensorTimers = new SparseArray<>();
        this.mWifiRunningTimers = new ArrayList<>();
        this.mFullWifiLockTimers = new ArrayList<>();
        this.mWifiMulticastTimers = new ArrayList<>();
        this.mWifiScanTimers = new ArrayList<>();
        this.mLastPartialTimers = new ArrayList<>();
        this.mUnpluggables = new ArrayList<>();
        this.mHaveBatteryLevel = false;
        this.mRecordingHistory = true;
        this.mHistoryBuffer = Parcel.obtain();
        this.mHistoryLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryLastLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryReadTmp = new BatteryStats.HistoryItem();
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = false;
        this.mLastHistoryTime = 0L;
        this.mHistoryCur = new BatteryStats.HistoryItem();
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[5];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[16];
        this.mNetworkActivityCounters = new LongSamplingCounter[4];
        this.mWifiOnUid = -1;
        this.mLastWriteTime = 0L;
        this.mBluetoothPingStart = -1;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mKernelWakelockStats = new HashMap<>();
        this.mProcWakelocksName = new String[3];
        this.mProcWakelocksData = new long[3];
        this.mProcWakelockFileStats = new HashMap();
        this.mUidCache = new HashMap<>();
        this.mNetworkStatsFactory = new NetworkStatsFactory();
        this.mMobileIfaces = Sets.newHashSet();
        this.mWifiIfaces = Sets.newHashSet();
        this.mChangedBufferStates = 0;
        this.mChangedStates = 0;
        this.mWifiFullLockNesting = 0;
        this.mWifiScanNesting = 0;
        this.mWifiMulticastNesting = 0;
        this.mPendingWrite = null;
        this.mWriteLock = new ReentrantLock();
        this.mFile = null;
        this.mHandler = null;
    }

    /* loaded from: BatteryStatsImpl$Counter.class */
    public static class Counter extends BatteryStats.Counter implements Unpluggable {
        final AtomicInteger mCount = new AtomicInteger();
        final ArrayList<Unpluggable> mUnpluggables;
        int mLoadedCount;
        int mLastCount;
        int mUnpluggedCount;
        int mPluggedCount;

        Counter(ArrayList<Unpluggable> unpluggables, Parcel in) {
            this.mUnpluggables = unpluggables;
            this.mPluggedCount = in.readInt();
            this.mCount.set(this.mPluggedCount);
            this.mLoadedCount = in.readInt();
            this.mLastCount = 0;
            this.mUnpluggedCount = in.readInt();
            unpluggables.add(this);
        }

        Counter(ArrayList<Unpluggable> unpluggables) {
            this.mUnpluggables = unpluggables;
            unpluggables.add(this);
        }

        public void writeToParcel(Parcel out) {
            out.writeInt(this.mCount.get());
            out.writeInt(this.mLoadedCount);
            out.writeInt(this.mUnpluggedCount);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
        public void unplug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            this.mUnpluggedCount = this.mPluggedCount;
            this.mCount.set(this.mPluggedCount);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
        public void plug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            this.mPluggedCount = this.mCount.get();
        }

        public static void writeCounterToParcel(Parcel out, Counter counter) {
            if (counter == null) {
                out.writeInt(0);
                return;
            }
            out.writeInt(1);
            counter.writeToParcel(out);
        }

        @Override // android.os.BatteryStats.Counter
        public int getCountLocked(int which) {
            int val;
            if (which == 1) {
                val = this.mLastCount;
            } else {
                val = this.mCount.get();
                if (which == 3) {
                    val -= this.mUnpluggedCount;
                } else if (which != 0) {
                    val -= this.mLoadedCount;
                }
            }
            return val;
        }

        @Override // android.os.BatteryStats.Counter
        public void logState(Printer pw, String prefix) {
            pw.println(prefix + "mCount=" + this.mCount.get() + " mLoadedCount=" + this.mLoadedCount + " mLastCount=" + this.mLastCount + " mUnpluggedCount=" + this.mUnpluggedCount + " mPluggedCount=" + this.mPluggedCount);
        }

        void stepAtomic() {
            this.mCount.incrementAndGet();
        }

        void reset(boolean detachIfReset) {
            this.mCount.set(0);
            this.mUnpluggedCount = 0;
            this.mPluggedCount = 0;
            this.mLastCount = 0;
            this.mLoadedCount = 0;
            if (detachIfReset) {
                detach();
            }
        }

        void detach() {
            this.mUnpluggables.remove(this);
        }

        void writeSummaryFromParcelLocked(Parcel out) {
            int count = this.mCount.get();
            out.writeInt(count);
        }

        void readSummaryFromParcelLocked(Parcel in) {
            this.mLoadedCount = in.readInt();
            this.mCount.set(this.mLoadedCount);
            this.mLastCount = 0;
            int i = this.mLoadedCount;
            this.mPluggedCount = i;
            this.mUnpluggedCount = i;
        }
    }

    /* loaded from: BatteryStatsImpl$SamplingCounter.class */
    public static class SamplingCounter extends Counter {
        SamplingCounter(ArrayList<Unpluggable> unpluggables, Parcel in) {
            super(unpluggables, in);
        }

        SamplingCounter(ArrayList<Unpluggable> unpluggables) {
            super(unpluggables);
        }

        public void addCountAtomic(long count) {
            this.mCount.addAndGet((int) count);
        }
    }

    /* loaded from: BatteryStatsImpl$LongSamplingCounter.class */
    public static class LongSamplingCounter implements Unpluggable {
        final ArrayList<Unpluggable> mUnpluggables;
        long mCount;
        long mLoadedCount;
        long mLastCount;
        long mUnpluggedCount;
        long mPluggedCount;

        LongSamplingCounter(ArrayList<Unpluggable> unpluggables, Parcel in) {
            this.mUnpluggables = unpluggables;
            this.mPluggedCount = in.readLong();
            this.mCount = this.mPluggedCount;
            this.mLoadedCount = in.readLong();
            this.mLastCount = 0L;
            this.mUnpluggedCount = in.readLong();
            unpluggables.add(this);
        }

        LongSamplingCounter(ArrayList<Unpluggable> unpluggables) {
            this.mUnpluggables = unpluggables;
            unpluggables.add(this);
        }

        public void writeToParcel(Parcel out) {
            out.writeLong(this.mCount);
            out.writeLong(this.mLoadedCount);
            out.writeLong(this.mUnpluggedCount);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
        public void unplug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            this.mUnpluggedCount = this.mPluggedCount;
            this.mCount = this.mPluggedCount;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
        public void plug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            this.mPluggedCount = this.mCount;
        }

        public long getCountLocked(int which) {
            long val;
            if (which == 1) {
                val = this.mLastCount;
            } else {
                val = this.mCount;
                if (which == 3) {
                    val -= this.mUnpluggedCount;
                } else if (which != 0) {
                    val -= this.mLoadedCount;
                }
            }
            return val;
        }

        void addCountLocked(long count) {
            this.mCount += count;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v0, types: [com.android.internal.os.BatteryStatsImpl$LongSamplingCounter] */
        void reset(boolean detachIfReset) {
            this.mCount = 0L;
            ?? r4 = 0;
            this.mUnpluggedCount = 0L;
            this.mPluggedCount = 0L;
            r4.mLastCount = this;
            this.mLoadedCount = this;
            if (detachIfReset) {
                detach();
            }
        }

        void detach() {
            this.mUnpluggables.remove(this);
        }

        void writeSummaryFromParcelLocked(Parcel out) {
            out.writeLong(this.mCount);
        }

        void readSummaryFromParcelLocked(Parcel in) {
            this.mLoadedCount = in.readLong();
            this.mCount = this.mLoadedCount;
            this.mLastCount = 0L;
            long j = this.mLoadedCount;
            this.mPluggedCount = j;
            this.mUnpluggedCount = j;
        }
    }

    /* loaded from: BatteryStatsImpl$Timer.class */
    public static abstract class Timer extends BatteryStats.Timer implements Unpluggable {
        final int mType;
        final ArrayList<Unpluggable> mUnpluggables;
        int mCount;
        int mLoadedCount;
        int mLastCount;
        int mUnpluggedCount;
        long mTotalTime;
        long mLoadedTime;
        long mLastTime;
        long mUnpluggedTime;

        protected abstract long computeRunTimeLocked(long j);

        protected abstract int computeCurrentCountLocked();

        Timer(int type, ArrayList<Unpluggable> unpluggables, Parcel in) {
            this.mType = type;
            this.mUnpluggables = unpluggables;
            this.mCount = in.readInt();
            this.mLoadedCount = in.readInt();
            this.mLastCount = 0;
            this.mUnpluggedCount = in.readInt();
            this.mTotalTime = in.readLong();
            this.mLoadedTime = in.readLong();
            this.mLastTime = 0L;
            this.mUnpluggedTime = in.readLong();
            unpluggables.add(this);
        }

        Timer(int type, ArrayList<Unpluggable> unpluggables) {
            this.mType = type;
            this.mUnpluggables = unpluggables;
            unpluggables.add(this);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r3v0, types: [com.android.internal.os.BatteryStatsImpl$Timer] */
        boolean reset(BatteryStatsImpl stats, boolean detachIfReset) {
            ?? r3 = 0;
            this.mLastTime = 0L;
            this.mLoadedTime = 0L;
            r3.mTotalTime = this;
            this.mLastCount = 0;
            this.mLoadedCount = 0;
            this.mCount = 0;
            if (detachIfReset) {
                detach();
                return true;
            }
            return true;
        }

        void detach() {
            this.mUnpluggables.remove(this);
        }

        public void writeToParcel(Parcel out, long batteryRealtime) {
            out.writeInt(this.mCount);
            out.writeInt(this.mLoadedCount);
            out.writeInt(this.mUnpluggedCount);
            out.writeLong(computeRunTimeLocked(batteryRealtime));
            out.writeLong(this.mLoadedTime);
            out.writeLong(this.mUnpluggedTime);
        }

        public void unplug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            this.mUnpluggedTime = computeRunTimeLocked(batteryRealtime);
            this.mUnpluggedCount = this.mCount;
        }

        public void plug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            this.mTotalTime = computeRunTimeLocked(batteryRealtime);
            this.mCount = computeCurrentCountLocked();
        }

        public static void writeTimerToParcel(Parcel out, Timer timer, long batteryRealtime) {
            if (timer == null) {
                out.writeInt(0);
                return;
            }
            out.writeInt(1);
            timer.writeToParcel(out, batteryRealtime);
        }

        @Override // android.os.BatteryStats.Timer
        public long getTotalTimeLocked(long batteryRealtime, int which) {
            long val;
            if (which == 1) {
                val = this.mLastTime;
            } else {
                val = computeRunTimeLocked(batteryRealtime);
                if (which == 3) {
                    val -= this.mUnpluggedTime;
                } else if (which != 0) {
                    val -= this.mLoadedTime;
                }
            }
            return val;
        }

        @Override // android.os.BatteryStats.Timer
        public int getCountLocked(int which) {
            int val;
            if (which == 1) {
                val = this.mLastCount;
            } else {
                val = computeCurrentCountLocked();
                if (which == 3) {
                    val -= this.mUnpluggedCount;
                } else if (which != 0) {
                    val -= this.mLoadedCount;
                }
            }
            return val;
        }

        @Override // android.os.BatteryStats.Timer
        public void logState(Printer pw, String prefix) {
            pw.println(prefix + "mCount=" + this.mCount + " mLoadedCount=" + this.mLoadedCount + " mLastCount=" + this.mLastCount + " mUnpluggedCount=" + this.mUnpluggedCount);
            pw.println(prefix + "mTotalTime=" + this.mTotalTime + " mLoadedTime=" + this.mLoadedTime);
            pw.println(prefix + "mLastTime=" + this.mLastTime + " mUnpluggedTime=" + this.mUnpluggedTime);
        }

        void writeSummaryFromParcelLocked(Parcel out, long batteryRealtime) {
            long runTime = computeRunTimeLocked(batteryRealtime);
            out.writeLong((runTime + 500) / 1000);
            out.writeInt(this.mCount);
        }

        void readSummaryFromParcelLocked(Parcel in) {
            long readLong = in.readLong() * 1000;
            this.mLoadedTime = readLong;
            this.mTotalTime = readLong;
            this.mLastTime = 0L;
            this.mUnpluggedTime = this.mTotalTime;
            int readInt = in.readInt();
            this.mLoadedCount = readInt;
            this.mCount = readInt;
            this.mLastCount = 0;
            this.mUnpluggedCount = this.mCount;
        }
    }

    /* loaded from: BatteryStatsImpl$SamplingTimer.class */
    public static final class SamplingTimer extends Timer {
        int mCurrentReportedCount;
        int mUnpluggedReportedCount;
        long mCurrentReportedTotalTime;
        long mUnpluggedReportedTotalTime;
        boolean mInDischarge;
        boolean mTrackingReportedValues;
        int mUpdateVersion;

        SamplingTimer(ArrayList<Unpluggable> unpluggables, boolean inDischarge, Parcel in) {
            super(0, unpluggables, in);
            this.mCurrentReportedCount = in.readInt();
            this.mUnpluggedReportedCount = in.readInt();
            this.mCurrentReportedTotalTime = in.readLong();
            this.mUnpluggedReportedTotalTime = in.readLong();
            this.mTrackingReportedValues = in.readInt() == 1;
            this.mInDischarge = inDischarge;
        }

        SamplingTimer(ArrayList<Unpluggable> unpluggables, boolean inDischarge, boolean trackReportedValues) {
            super(0, unpluggables);
            this.mTrackingReportedValues = trackReportedValues;
            this.mInDischarge = inDischarge;
        }

        public void setStale() {
            this.mTrackingReportedValues = false;
            this.mUnpluggedReportedTotalTime = 0L;
            this.mUnpluggedReportedCount = 0;
        }

        public void setUpdateVersion(int version) {
            this.mUpdateVersion = version;
        }

        public int getUpdateVersion() {
            return this.mUpdateVersion;
        }

        public void updateCurrentReportedCount(int count) {
            if (this.mInDischarge && this.mUnpluggedReportedCount == 0) {
                this.mUnpluggedReportedCount = count;
                this.mTrackingReportedValues = true;
            }
            this.mCurrentReportedCount = count;
        }

        public void updateCurrentReportedTotalTime(long totalTime) {
            if (this.mInDischarge && this.mUnpluggedReportedTotalTime == 0) {
                this.mUnpluggedReportedTotalTime = totalTime;
                this.mTrackingReportedValues = true;
            }
            this.mCurrentReportedTotalTime = totalTime;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.Unpluggable
        public void unplug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            super.unplug(elapsedRealtime, batteryUptime, batteryRealtime);
            if (this.mTrackingReportedValues) {
                this.mUnpluggedReportedTotalTime = this.mCurrentReportedTotalTime;
                this.mUnpluggedReportedCount = this.mCurrentReportedCount;
            }
            this.mInDischarge = true;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.Unpluggable
        public void plug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            super.plug(elapsedRealtime, batteryUptime, batteryRealtime);
            this.mInDischarge = false;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, android.os.BatteryStats.Timer
        public void logState(Printer pw, String prefix) {
            super.logState(pw, prefix);
            pw.println(prefix + "mCurrentReportedCount=" + this.mCurrentReportedCount + " mUnpluggedReportedCount=" + this.mUnpluggedReportedCount + " mCurrentReportedTotalTime=" + this.mCurrentReportedTotalTime + " mUnpluggedReportedTotalTime=" + this.mUnpluggedReportedTotalTime);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected long computeRunTimeLocked(long curBatteryRealtime) {
            return this.mTotalTime + ((this.mInDischarge && this.mTrackingReportedValues) ? this.mCurrentReportedTotalTime - this.mUnpluggedReportedTotalTime : 0L);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected int computeCurrentCountLocked() {
            return this.mCount + ((this.mInDischarge && this.mTrackingReportedValues) ? this.mCurrentReportedCount - this.mUnpluggedReportedCount : 0);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public void writeToParcel(Parcel out, long batteryRealtime) {
            super.writeToParcel(out, batteryRealtime);
            out.writeInt(this.mCurrentReportedCount);
            out.writeInt(this.mUnpluggedReportedCount);
            out.writeLong(this.mCurrentReportedTotalTime);
            out.writeLong(this.mUnpluggedReportedTotalTime);
            out.writeInt(this.mTrackingReportedValues ? 1 : 0);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        boolean reset(BatteryStatsImpl stats, boolean detachIfReset) {
            super.reset(stats, detachIfReset);
            setStale();
            return true;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        void writeSummaryFromParcelLocked(Parcel out, long batteryRealtime) {
            super.writeSummaryFromParcelLocked(out, batteryRealtime);
            out.writeLong(this.mCurrentReportedTotalTime);
            out.writeInt(this.mCurrentReportedCount);
            out.writeInt(this.mTrackingReportedValues ? 1 : 0);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        void readSummaryFromParcelLocked(Parcel in) {
            super.readSummaryFromParcelLocked(in);
            long readLong = in.readLong();
            this.mCurrentReportedTotalTime = readLong;
            this.mUnpluggedReportedTotalTime = readLong;
            int readInt = in.readInt();
            this.mCurrentReportedCount = readInt;
            this.mUnpluggedReportedCount = readInt;
            this.mTrackingReportedValues = in.readInt() == 1;
        }
    }

    /* loaded from: BatteryStatsImpl$BatchTimer.class */
    public static final class BatchTimer extends Timer {
        final Uid mUid;
        long mLastAddedTime;
        long mLastAddedDuration;
        boolean mInDischarge;

        BatchTimer(Uid uid, int type, ArrayList<Unpluggable> unpluggables, boolean inDischarge, Parcel in) {
            super(type, unpluggables, in);
            this.mUid = uid;
            this.mLastAddedTime = in.readLong();
            this.mLastAddedDuration = in.readLong();
            this.mInDischarge = inDischarge;
        }

        BatchTimer(Uid uid, int type, ArrayList<Unpluggable> unpluggables, boolean inDischarge) {
            super(type, unpluggables);
            this.mUid = uid;
            this.mInDischarge = inDischarge;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public void writeToParcel(Parcel out, long batteryRealtime) {
            super.writeToParcel(out, batteryRealtime);
            out.writeLong(this.mLastAddedTime);
            out.writeLong(this.mLastAddedDuration);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.Unpluggable
        public void plug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            recomputeLastDuration(SystemClock.elapsedRealtime() * 1000, false);
            this.mInDischarge = false;
            super.plug(elapsedRealtime, batteryUptime, batteryRealtime);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.Unpluggable
        public void unplug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            recomputeLastDuration(elapsedRealtime, false);
            this.mInDischarge = true;
            if (this.mLastAddedTime == elapsedRealtime) {
                this.mTotalTime += this.mLastAddedDuration;
            }
            super.unplug(elapsedRealtime, batteryUptime, batteryRealtime);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, android.os.BatteryStats.Timer
        public void logState(Printer pw, String prefix) {
            super.logState(pw, prefix);
            pw.println(prefix + "mLastAddedTime=" + this.mLastAddedTime + " mLastAddedDuration=" + this.mLastAddedDuration);
        }

        private long computeOverage(long curTime) {
            if (this.mLastAddedTime > 0) {
                return (this.mLastTime + this.mLastAddedDuration) - curTime;
            }
            return 0L;
        }

        private void recomputeLastDuration(long curTime, boolean abort) {
            long overage = computeOverage(curTime);
            if (overage > 0) {
                if (this.mInDischarge) {
                    this.mTotalTime -= overage;
                }
                if (abort) {
                    this.mLastAddedTime = 0L;
                    return;
                }
                this.mLastAddedTime = curTime;
                this.mLastAddedDuration -= overage;
            }
        }

        public void addDuration(BatteryStatsImpl stats, long durationMillis) {
            long now = SystemClock.elapsedRealtime() * 1000;
            recomputeLastDuration(now, true);
            this.mLastAddedTime = now;
            this.mLastAddedDuration = durationMillis * 1000;
            if (this.mInDischarge) {
                this.mTotalTime += this.mLastAddedDuration;
                this.mCount++;
            }
        }

        public void abortLastDuration(BatteryStatsImpl stats) {
            long now = SystemClock.elapsedRealtime() * 1000;
            recomputeLastDuration(now, true);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected int computeCurrentCountLocked() {
            return this.mCount;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected long computeRunTimeLocked(long curBatteryRealtime) {
            long overage = computeOverage(SystemClock.elapsedRealtime() * 1000);
            if (overage > 0) {
                this.mTotalTime = overage;
                return overage;
            }
            return this.mTotalTime;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        boolean reset(BatteryStatsImpl stats, boolean detachIfReset) {
            long now = SystemClock.elapsedRealtime() * 1000;
            recomputeLastDuration(now, true);
            boolean stillActive = this.mLastAddedTime == now;
            super.reset(stats, !stillActive && detachIfReset);
            return !stillActive;
        }
    }

    /* loaded from: BatteryStatsImpl$StopwatchTimer.class */
    public static final class StopwatchTimer extends Timer {
        final Uid mUid;
        final ArrayList<StopwatchTimer> mTimerPool;
        int mNesting;
        long mUpdateTime;
        long mAcquireTime;
        long mTimeout;
        boolean mInList;

        StopwatchTimer(Uid uid, int type, ArrayList<StopwatchTimer> timerPool, ArrayList<Unpluggable> unpluggables, Parcel in) {
            super(type, unpluggables, in);
            this.mUid = uid;
            this.mTimerPool = timerPool;
            this.mUpdateTime = in.readLong();
        }

        StopwatchTimer(Uid uid, int type, ArrayList<StopwatchTimer> timerPool, ArrayList<Unpluggable> unpluggables) {
            super(type, unpluggables);
            this.mUid = uid;
            this.mTimerPool = timerPool;
        }

        void setTimeout(long timeout) {
            this.mTimeout = timeout;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        public void writeToParcel(Parcel out, long batteryRealtime) {
            super.writeToParcel(out, batteryRealtime);
            out.writeLong(this.mUpdateTime);
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, com.android.internal.os.BatteryStatsImpl.Unpluggable
        public void plug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            if (this.mNesting > 0) {
                super.plug(elapsedRealtime, batteryUptime, batteryRealtime);
                this.mUpdateTime = batteryRealtime;
            }
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer, android.os.BatteryStats.Timer
        public void logState(Printer pw, String prefix) {
            super.logState(pw, prefix);
            pw.println(prefix + "mNesting=" + this.mNesting + " mUpdateTime=" + this.mUpdateTime + " mAcquireTime=" + this.mAcquireTime);
        }

        void startRunningLocked(BatteryStatsImpl stats) {
            int i = this.mNesting;
            this.mNesting = i + 1;
            if (i == 0) {
                this.mUpdateTime = stats.getBatteryRealtimeLocked(SystemClock.elapsedRealtime() * 1000);
                if (this.mTimerPool != null) {
                    refreshTimersLocked(stats, this.mTimerPool);
                    this.mTimerPool.add(this);
                }
                this.mCount++;
                this.mAcquireTime = this.mTotalTime;
            }
        }

        boolean isRunningLocked() {
            return this.mNesting > 0;
        }

        void stopRunningLocked(BatteryStatsImpl stats) {
            if (this.mNesting == 0) {
                return;
            }
            int i = this.mNesting - 1;
            this.mNesting = i;
            if (i == 0) {
                if (this.mTimerPool != null) {
                    refreshTimersLocked(stats, this.mTimerPool);
                    this.mTimerPool.remove(this);
                } else {
                    long realtime = SystemClock.elapsedRealtime() * 1000;
                    long batteryRealtime = stats.getBatteryRealtimeLocked(realtime);
                    this.mNesting = 1;
                    this.mTotalTime = computeRunTimeLocked(batteryRealtime);
                    this.mNesting = 0;
                }
                if (this.mTotalTime == this.mAcquireTime) {
                    this.mCount--;
                }
            }
        }

        private static void refreshTimersLocked(BatteryStatsImpl stats, ArrayList<StopwatchTimer> pool) {
            long realtime = SystemClock.elapsedRealtime() * 1000;
            long batteryRealtime = stats.getBatteryRealtimeLocked(realtime);
            int N = pool.size();
            for (int i = N - 1; i >= 0; i--) {
                StopwatchTimer t = pool.get(i);
                long heldTime = batteryRealtime - t.mUpdateTime;
                if (heldTime > 0) {
                    t.mTotalTime += heldTime / N;
                }
                t.mUpdateTime = batteryRealtime;
            }
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected long computeRunTimeLocked(long curBatteryRealtime) {
            long j;
            if (this.mTimeout > 0 && curBatteryRealtime > this.mUpdateTime + this.mTimeout) {
                curBatteryRealtime = this.mUpdateTime + this.mTimeout;
            }
            long j2 = this.mTotalTime;
            if (this.mNesting > 0) {
                j = (curBatteryRealtime - this.mUpdateTime) / (this.mTimerPool != null ? this.mTimerPool.size() : 1);
            } else {
                j = 0;
            }
            return j2 + j;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        protected int computeCurrentCountLocked() {
            return this.mCount;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        boolean reset(BatteryStatsImpl stats, boolean detachIfReset) {
            boolean canDetach = this.mNesting <= 0;
            super.reset(stats, canDetach && detachIfReset);
            if (this.mNesting > 0) {
                this.mUpdateTime = stats.getBatteryRealtimeLocked(SystemClock.elapsedRealtime() * 1000);
            }
            this.mAcquireTime = this.mTotalTime;
            return canDetach;
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        void detach() {
            super.detach();
            if (this.mTimerPool != null) {
                this.mTimerPool.remove(this);
            }
        }

        @Override // com.android.internal.os.BatteryStatsImpl.Timer
        void readSummaryFromParcelLocked(Parcel in) {
            super.readSummaryFromParcelLocked(in);
            this.mNesting = 0;
        }
    }

    private final Map<String, KernelWakelockStats> readKernelWakelockStats() {
        FileInputStream is;
        byte[] buffer = new byte[8192];
        boolean wakeup_sources = false;
        try {
            try {
                is = new FileInputStream("/proc/wakelocks");
            } catch (FileNotFoundException e) {
                try {
                    is = new FileInputStream("/d/wakeup_sources");
                    wakeup_sources = true;
                } catch (FileNotFoundException e2) {
                    return null;
                }
            }
            int len = is.read(buffer);
            is.close();
            if (len > 0) {
                int i = 0;
                while (true) {
                    if (i >= len) {
                        break;
                    } else if (buffer[i] != 0) {
                        i++;
                    } else {
                        len = i;
                        break;
                    }
                }
            }
            return parseProcWakelocks(buffer, len, wakeup_sources);
        } catch (IOException e3) {
            return null;
        }
    }

    private final Map<String, KernelWakelockStats> parseProcWakelocks(byte[] wlBuffer, int len, boolean wakeup_sources) {
        long totalTime;
        int numUpdatedWlNames = 0;
        int i = 0;
        while (i < len && wlBuffer[i] != 10 && wlBuffer[i] != 0) {
            i++;
        }
        int i2 = i + 1;
        int endIndex = i2;
        int startIndex = i2;
        synchronized (this) {
            Map<String, KernelWakelockStats> m = this.mProcWakelockFileStats;
            sKernelWakelockUpdateVersion++;
            while (endIndex < len) {
                int endIndex2 = startIndex;
                while (endIndex2 < len && wlBuffer[endIndex2] != 10 && wlBuffer[endIndex2] != 0) {
                    endIndex2++;
                }
                endIndex = endIndex2 + 1;
                if (endIndex >= len - 1) {
                    return m;
                }
                String[] nameStringArray = this.mProcWakelocksName;
                long[] wlData = this.mProcWakelocksData;
                for (int j = startIndex; j < endIndex; j++) {
                    if ((wlBuffer[j] & 128) != 0) {
                        wlBuffer[j] = 63;
                    }
                }
                boolean parsed = Process.parseProcLine(wlBuffer, startIndex, endIndex, wakeup_sources ? WAKEUP_SOURCES_FORMAT : PROC_WAKELOCKS_FORMAT, nameStringArray, wlData, null);
                String name = nameStringArray[0];
                int count = (int) wlData[1];
                if (wakeup_sources) {
                    totalTime = wlData[2] * 1000;
                } else {
                    totalTime = (wlData[2] + 500) / 1000;
                }
                if (parsed && name.length() > 0) {
                    if (!m.containsKey(name)) {
                        m.put(name, new KernelWakelockStats(count, totalTime, sKernelWakelockUpdateVersion));
                        numUpdatedWlNames++;
                    } else {
                        KernelWakelockStats kwlStats = m.get(name);
                        if (kwlStats.mVersion == sKernelWakelockUpdateVersion) {
                            kwlStats.mCount += count;
                            kwlStats.mTotalTime += totalTime;
                        } else {
                            kwlStats.mCount = count;
                            kwlStats.mTotalTime = totalTime;
                            kwlStats.mVersion = sKernelWakelockUpdateVersion;
                            numUpdatedWlNames++;
                        }
                    }
                }
                startIndex = endIndex;
            }
            if (m.size() != numUpdatedWlNames) {
                Iterator<KernelWakelockStats> itr = m.values().iterator();
                while (itr.hasNext()) {
                    if (itr.next().mVersion != sKernelWakelockUpdateVersion) {
                        itr.remove();
                    }
                }
            }
            return m;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: BatteryStatsImpl$KernelWakelockStats.class */
    public class KernelWakelockStats {
        public int mCount;
        public long mTotalTime;
        public int mVersion;

        KernelWakelockStats(int count, long totalTime, int version) {
            this.mCount = count;
            this.mTotalTime = totalTime;
            this.mVersion = version;
        }
    }

    public SamplingTimer getKernelWakelockTimerLocked(String name) {
        SamplingTimer kwlt = this.mKernelWakelockStats.get(name);
        if (kwlt == null) {
            kwlt = new SamplingTimer(this.mUnpluggables, this.mOnBatteryInternal, true);
            this.mKernelWakelockStats.put(name, kwlt);
        }
        return kwlt;
    }

    private long getCurrentRadioDataUptime() {
        try {
            File awakeTimeFile = new File("/sys/devices/virtual/net/rmnet0/awake_time_ms");
            if (awakeTimeFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(awakeTimeFile));
                String line = br.readLine();
                br.close();
                return Long.parseLong(line) * 1000;
            }
            return 0L;
        } catch (IOException | NumberFormatException e) {
            return 0L;
        }
    }

    @Override // android.os.BatteryStats
    public long getRadioDataUptimeMs() {
        return getRadioDataUptime() / 1000;
    }

    @Override // android.os.BatteryStats
    public long getRadioDataUptime() {
        if (this.mRadioDataStart == -1) {
            return this.mRadioDataUptime;
        }
        return getCurrentRadioDataUptime() - this.mRadioDataStart;
    }

    private int getCurrentBluetoothPingCount() {
        if (this.mBtHeadset != null) {
            List<BluetoothDevice> deviceList = this.mBtHeadset.getConnectedDevices();
            if (deviceList.size() > 0) {
                return this.mBtHeadset.getBatteryUsageHint(deviceList.get(0));
            }
            return -1;
        }
        return -1;
    }

    public int getBluetoothPingCount() {
        if (this.mBluetoothPingStart == -1) {
            return this.mBluetoothPingCount;
        }
        if (this.mBtHeadset != null) {
            return getCurrentBluetoothPingCount() - this.mBluetoothPingStart;
        }
        return 0;
    }

    public void setBtHeadset(BluetoothHeadset headset) {
        if (headset != null && this.mBtHeadset == null && isOnBattery() && this.mBluetoothPingStart == -1) {
            this.mBluetoothPingStart = getCurrentBluetoothPingCount();
        }
        this.mBtHeadset = headset;
    }

    void addHistoryBufferLocked(long curTime) {
        if (!this.mHaveBatteryLevel || !this.mRecordingHistory) {
            return;
        }
        long timeDiff = (this.mHistoryBaseTime + curTime) - this.mHistoryLastWritten.time;
        if (this.mHistoryBufferLastPos >= 0 && this.mHistoryLastWritten.cmd == 1 && timeDiff < 2000 && ((this.mHistoryLastWritten.states ^ this.mHistoryCur.states) & this.mChangedBufferStates) == 0) {
            this.mHistoryBuffer.setDataSize(this.mHistoryBufferLastPos);
            this.mHistoryBuffer.setDataPosition(this.mHistoryBufferLastPos);
            this.mHistoryBufferLastPos = -1;
            if (this.mHistoryLastLastWritten.cmd == 1 && timeDiff < 500 && this.mHistoryLastLastWritten.same(this.mHistoryCur)) {
                this.mHistoryLastWritten.setTo(this.mHistoryLastLastWritten);
                this.mHistoryLastLastWritten.cmd = (byte) 0;
                return;
            }
            this.mChangedBufferStates |= this.mHistoryLastWritten.states ^ this.mHistoryCur.states;
            curTime = this.mHistoryLastWritten.time - this.mHistoryBaseTime;
            this.mHistoryLastWritten.setTo(this.mHistoryLastLastWritten);
        } else {
            this.mChangedBufferStates = 0;
        }
        int dataSize = this.mHistoryBuffer.dataSize();
        if (dataSize >= 131072) {
            if (!this.mHistoryOverflow) {
                this.mHistoryOverflow = true;
                addHistoryBufferLocked(curTime, (byte) 3);
            }
            if (this.mHistoryLastWritten.batteryLevel == this.mHistoryCur.batteryLevel && (dataSize >= 147456 || ((this.mHistoryLastWritten.states ^ this.mHistoryCur.states) & BatteryStats.HistoryItem.MOST_INTERESTING_STATES) == 0)) {
                return;
            }
        }
        addHistoryBufferLocked(curTime, (byte) 1);
    }

    void addHistoryBufferLocked(long curTime, byte cmd) {
        int origPos = 0;
        if (this.mIteratingHistory) {
            origPos = this.mHistoryBuffer.dataPosition();
            this.mHistoryBuffer.setDataPosition(this.mHistoryBuffer.dataSize());
        }
        this.mHistoryBufferLastPos = this.mHistoryBuffer.dataPosition();
        this.mHistoryLastLastWritten.setTo(this.mHistoryLastWritten);
        this.mHistoryLastWritten.setTo(this.mHistoryBaseTime + curTime, cmd, this.mHistoryCur);
        this.mHistoryLastWritten.writeDelta(this.mHistoryBuffer, this.mHistoryLastLastWritten);
        this.mLastHistoryTime = curTime;
        if (this.mIteratingHistory) {
            this.mHistoryBuffer.setDataPosition(origPos);
        }
    }

    void addHistoryRecordLocked(long curTime) {
        addHistoryBufferLocked(curTime);
    }

    void addHistoryRecordLocked(long curTime, byte cmd) {
        BatteryStats.HistoryItem rec = this.mHistoryCache;
        if (rec != null) {
            this.mHistoryCache = rec.next;
        } else {
            rec = new BatteryStats.HistoryItem();
        }
        rec.setTo(this.mHistoryBaseTime + curTime, cmd, this.mHistoryCur);
        addHistoryRecordLocked(rec);
    }

    void addHistoryRecordLocked(BatteryStats.HistoryItem rec) {
        this.mNumHistoryItems++;
        rec.next = null;
        this.mHistoryLastEnd = this.mHistoryEnd;
        if (this.mHistoryEnd != null) {
            this.mHistoryEnd.next = rec;
            this.mHistoryEnd = rec;
            return;
        }
        this.mHistoryEnd = rec;
        this.mHistory = rec;
    }

    void clearHistoryLocked() {
        this.mHistoryBaseTime = 0L;
        this.mLastHistoryTime = 0L;
        this.mHistoryBuffer.setDataSize(0);
        this.mHistoryBuffer.setDataPosition(0);
        this.mHistoryBuffer.setDataCapacity(65536);
        this.mHistoryLastLastWritten.cmd = (byte) 0;
        this.mHistoryLastWritten.cmd = (byte) 0;
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = false;
    }

    public void doUnplugLocked(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
        for (int i = this.mUnpluggables.size() - 1; i >= 0; i--) {
            this.mUnpluggables.get(i).unplug(elapsedRealtime, batteryUptime, batteryRealtime);
        }
        this.mRadioDataStart = getCurrentRadioDataUptime();
        this.mRadioDataUptime = 0L;
        this.mBluetoothPingStart = getCurrentBluetoothPingCount();
        this.mBluetoothPingCount = 0;
    }

    public void doPlugLocked(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
        for (int i = this.mUnpluggables.size() - 1; i >= 0; i--) {
            this.mUnpluggables.get(i).plug(elapsedRealtime, batteryUptime, batteryRealtime);
        }
        this.mRadioDataUptime = getRadioDataUptime();
        this.mRadioDataStart = -1L;
        this.mBluetoothPingCount = getBluetoothPingCount();
        this.mBluetoothPingStart = -1;
    }

    public void noteStartWakeLocked(int uid, int pid, String name, int type) {
        if (type == 0) {
            if (this.mWakeLockNesting == 0) {
                this.mHistoryCur.states |= 1073741824;
                addHistoryRecordLocked(SystemClock.elapsedRealtime());
            }
            this.mWakeLockNesting++;
        }
        if (uid >= 0) {
            if (!this.mHandler.hasMessages(1)) {
                Message m = this.mHandler.obtainMessage(1);
                this.mHandler.sendMessageDelayed(m, 5000L);
            }
            getUidStatsLocked(uid).noteStartWakeLocked(pid, name, type);
        }
    }

    public void noteStopWakeLocked(int uid, int pid, String name, int type) {
        if (type == 0) {
            this.mWakeLockNesting--;
            if (this.mWakeLockNesting == 0) {
                this.mHistoryCur.states &= -1073741825;
                addHistoryRecordLocked(SystemClock.elapsedRealtime());
            }
        }
        if (uid >= 0) {
            if (!this.mHandler.hasMessages(1)) {
                Message m = this.mHandler.obtainMessage(1);
                this.mHandler.sendMessageDelayed(m, 5000L);
            }
            getUidStatsLocked(uid).noteStopWakeLocked(pid, name, type);
        }
    }

    public void noteStartWakeFromSourceLocked(WorkSource ws, int pid, String name, int type) {
        int N = ws.size();
        for (int i = 0; i < N; i++) {
            noteStartWakeLocked(ws.get(i), pid, name, type);
        }
    }

    public void noteStopWakeFromSourceLocked(WorkSource ws, int pid, String name, int type) {
        int N = ws.size();
        for (int i = 0; i < N; i++) {
            noteStopWakeLocked(ws.get(i), pid, name, type);
        }
    }

    public int startAddingCpuLocked() {
        Uid uid;
        this.mHandler.removeMessages(1);
        if (this.mScreenOn) {
            return 0;
        }
        int N = this.mPartialTimers.size();
        if (N == 0) {
            this.mLastPartialTimers.clear();
            return 0;
        }
        for (int i = 0; i < N; i++) {
            StopwatchTimer st = this.mPartialTimers.get(i);
            if (st.mInList && (uid = st.mUid) != null && uid.mUid != 1000) {
                return 50;
            }
        }
        return 0;
    }

    public void finishAddingCpuLocked(int perc, int utime, int stime, long[] cpuSpeedTimes) {
        Uid uid;
        Uid uid2;
        Uid uid3;
        int N = this.mPartialTimers.size();
        if (perc != 0) {
            int num = 0;
            for (int i = 0; i < N; i++) {
                StopwatchTimer st = this.mPartialTimers.get(i);
                if (st.mInList && (uid3 = st.mUid) != null && uid3.mUid != 1000) {
                    num++;
                }
            }
            if (num != 0) {
                for (int i2 = 0; i2 < N; i2++) {
                    StopwatchTimer st2 = this.mPartialTimers.get(i2);
                    if (st2.mInList && (uid2 = st2.mUid) != null && uid2.mUid != 1000) {
                        int myUTime = utime / num;
                        int mySTime = stime / num;
                        utime -= myUTime;
                        stime -= mySTime;
                        num--;
                        Uid.Proc proc = uid2.getProcessStatsLocked("*wakelock*");
                        proc.addCpuTimeLocked(myUTime, mySTime);
                        proc.addSpeedStepTimes(cpuSpeedTimes);
                    }
                }
            }
            if ((utime != 0 || stime != 0) && (uid = getUidStatsLocked(1000)) != null) {
                Uid.Proc proc2 = uid.getProcessStatsLocked("*lost*");
                proc2.addCpuTimeLocked(utime, stime);
                proc2.addSpeedStepTimes(cpuSpeedTimes);
            }
        }
        int NL = this.mLastPartialTimers.size();
        boolean diff = N != NL;
        for (int i3 = 0; i3 < NL && !diff; i3++) {
            diff |= this.mPartialTimers.get(i3) != this.mLastPartialTimers.get(i3);
        }
        if (!diff) {
            for (int i4 = 0; i4 < NL; i4++) {
                this.mPartialTimers.get(i4).mInList = true;
            }
            return;
        }
        for (int i5 = 0; i5 < NL; i5++) {
            this.mLastPartialTimers.get(i5).mInList = false;
        }
        this.mLastPartialTimers.clear();
        for (int i6 = 0; i6 < N; i6++) {
            StopwatchTimer st3 = this.mPartialTimers.get(i6);
            st3.mInList = true;
            this.mLastPartialTimers.add(st3);
        }
    }

    public void noteProcessDiedLocked(int uid, int pid) {
        Uid u = this.mUidStats.get(uid);
        if (u != null) {
            u.mPids.remove(pid);
        }
    }

    public long getProcessWakeTime(int uid, int pid, long realtime) {
        BatteryStats.Uid.Pid p;
        Uid u = this.mUidStats.get(uid);
        if (u == null || (p = u.mPids.get(pid)) == null) {
            return 0L;
        }
        return p.mWakeSum + (p.mWakeStart != 0 ? realtime - p.mWakeStart : 0L);
    }

    public void reportExcessiveWakeLocked(int uid, String proc, long overTime, long usedTime) {
        Uid u = this.mUidStats.get(uid);
        if (u != null) {
            u.reportExcessiveWakeLocked(proc, overTime, usedTime);
        }
    }

    public void reportExcessiveCpuLocked(int uid, String proc, long overTime, long usedTime) {
        Uid u = this.mUidStats.get(uid);
        if (u != null) {
            u.reportExcessiveCpuLocked(proc, overTime, usedTime);
        }
    }

    public void noteStartSensorLocked(int uid, int sensor) {
        if (this.mSensorNesting == 0) {
            this.mHistoryCur.states |= 536870912;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        this.mSensorNesting++;
        getUidStatsLocked(uid).noteStartSensor(sensor);
    }

    public void noteStopSensorLocked(int uid, int sensor) {
        this.mSensorNesting--;
        if (this.mSensorNesting == 0) {
            this.mHistoryCur.states &= -536870913;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        getUidStatsLocked(uid).noteStopSensor(sensor);
    }

    public void noteStartGpsLocked(int uid) {
        if (this.mGpsNesting == 0) {
            this.mHistoryCur.states |= 268435456;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        this.mGpsNesting++;
        getUidStatsLocked(uid).noteStartGps();
    }

    public void noteStopGpsLocked(int uid) {
        this.mGpsNesting--;
        if (this.mGpsNesting == 0) {
            this.mHistoryCur.states &= -268435457;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        getUidStatsLocked(uid).noteStopGps();
    }

    public void noteScreenOnLocked() {
        if (!this.mScreenOn) {
            this.mHistoryCur.states |= 1048576;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mScreenOn = true;
            this.mScreenOnTimer.startRunningLocked(this);
            if (this.mScreenBrightnessBin >= 0) {
                this.mScreenBrightnessTimer[this.mScreenBrightnessBin].startRunningLocked(this);
            }
            noteStartWakeLocked(-1, -1, "dummy", 0);
            if (this.mOnBatteryInternal) {
                updateDischargeScreenLevelsLocked(false, true);
            }
        }
    }

    public void noteScreenOffLocked() {
        if (this.mScreenOn) {
            this.mHistoryCur.states &= -1048577;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mScreenOn = false;
            this.mScreenOnTimer.stopRunningLocked(this);
            if (this.mScreenBrightnessBin >= 0) {
                this.mScreenBrightnessTimer[this.mScreenBrightnessBin].stopRunningLocked(this);
            }
            noteStopWakeLocked(-1, -1, "dummy", 0);
            if (this.mOnBatteryInternal) {
                updateDischargeScreenLevelsLocked(true, false);
            }
        }
    }

    public void noteScreenBrightnessLocked(int brightness) {
        int bin = brightness / 51;
        if (bin < 0) {
            bin = 0;
        } else if (bin >= 5) {
            bin = 4;
        }
        if (this.mScreenBrightnessBin != bin) {
            this.mHistoryCur.states = (this.mHistoryCur.states & (-16)) | (bin << 0);
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            if (this.mScreenOn) {
                if (this.mScreenBrightnessBin >= 0) {
                    this.mScreenBrightnessTimer[this.mScreenBrightnessBin].stopRunningLocked(this);
                }
                this.mScreenBrightnessTimer[bin].startRunningLocked(this);
            }
            this.mScreenBrightnessBin = bin;
        }
    }

    public void noteInputEventAtomic() {
        this.mInputEventCounter.stepAtomic();
    }

    public void noteUserActivityLocked(int uid, int event) {
        getUidStatsLocked(uid).noteUserActivityLocked(event);
    }

    public void notePhoneOnLocked() {
        if (!this.mPhoneOn) {
            this.mHistoryCur.states |= 262144;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mPhoneOn = true;
            this.mPhoneOnTimer.startRunningLocked(this);
        }
    }

    public void notePhoneOffLocked() {
        if (this.mPhoneOn) {
            this.mHistoryCur.states &= -262145;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mPhoneOn = false;
            this.mPhoneOnTimer.stopRunningLocked(this);
        }
    }

    void stopAllSignalStrengthTimersLocked(int except) {
        for (int i = 0; i < 5; i++) {
            if (i != except) {
                while (this.mPhoneSignalStrengthsTimer[i].isRunningLocked()) {
                    this.mPhoneSignalStrengthsTimer[i].stopRunningLocked(this);
                }
            }
        }
    }

    private int fixPhoneServiceState(int state, int signalBin) {
        if (this.mPhoneSimStateRaw == 1 && state == 1 && signalBin > 0) {
            state = 0;
        }
        return state;
    }

    private void updateAllPhoneStateLocked(int state, int simState, int bin) {
        boolean scanning = false;
        boolean newHistory = false;
        this.mPhoneServiceStateRaw = state;
        this.mPhoneSimStateRaw = simState;
        this.mPhoneSignalStrengthBinRaw = bin;
        if (simState == 1 && state == 1 && bin > 0) {
            state = 0;
        }
        if (state == 3) {
            bin = -1;
        } else if (state != 0 && state == 1) {
            scanning = true;
            bin = 0;
            if (!this.mPhoneSignalScanningTimer.isRunningLocked()) {
                this.mHistoryCur.states |= 134217728;
                newHistory = true;
                this.mPhoneSignalScanningTimer.startRunningLocked(this);
            }
        }
        if (!scanning && this.mPhoneSignalScanningTimer.isRunningLocked()) {
            this.mHistoryCur.states &= -134217729;
            newHistory = true;
            this.mPhoneSignalScanningTimer.stopRunningLocked(this);
        }
        if (this.mPhoneServiceState != state) {
            this.mHistoryCur.states = (this.mHistoryCur.states & (-3841)) | (state << 8);
            newHistory = true;
            this.mPhoneServiceState = state;
        }
        if (this.mPhoneSignalStrengthBin != bin) {
            if (this.mPhoneSignalStrengthBin >= 0) {
                this.mPhoneSignalStrengthsTimer[this.mPhoneSignalStrengthBin].stopRunningLocked(this);
            }
            if (bin >= 0) {
                if (!this.mPhoneSignalStrengthsTimer[bin].isRunningLocked()) {
                    this.mPhoneSignalStrengthsTimer[bin].startRunningLocked(this);
                }
                this.mHistoryCur.states = (this.mHistoryCur.states & (-241)) | (bin << 4);
                newHistory = true;
            } else {
                stopAllSignalStrengthTimersLocked(-1);
            }
            this.mPhoneSignalStrengthBin = bin;
        }
        if (newHistory) {
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
    }

    public void notePhoneStateLocked(int state, int simState) {
        updateAllPhoneStateLocked(state, simState, this.mPhoneSignalStrengthBinRaw);
    }

    public void notePhoneSignalStrengthLocked(SignalStrength signalStrength) {
        int bin = signalStrength.getLevel();
        updateAllPhoneStateLocked(this.mPhoneServiceStateRaw, this.mPhoneSimStateRaw, bin);
    }

    public void notePhoneDataConnectionStateLocked(int dataType, boolean hasData) {
        int bin = 0;
        if (hasData) {
            switch (dataType) {
                case 1:
                    bin = 1;
                    break;
                case 2:
                    bin = 2;
                    break;
                case 3:
                    bin = 3;
                    break;
                case 4:
                    bin = 4;
                    break;
                case 5:
                    bin = 5;
                    break;
                case 6:
                    bin = 6;
                    break;
                case 7:
                    bin = 7;
                    break;
                case 8:
                    bin = 8;
                    break;
                case 9:
                    bin = 9;
                    break;
                case 10:
                    bin = 10;
                    break;
                case 11:
                    bin = 11;
                    break;
                case 12:
                    bin = 12;
                    break;
                case 13:
                    bin = 13;
                    break;
                case 14:
                    bin = 14;
                    break;
                default:
                    bin = 15;
                    break;
            }
        }
        if (this.mPhoneDataConnectionType != bin) {
            this.mHistoryCur.states = (this.mHistoryCur.states & (-61441)) | (bin << 12);
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            if (this.mPhoneDataConnectionType >= 0) {
                this.mPhoneDataConnectionsTimer[this.mPhoneDataConnectionType].stopRunningLocked(this);
            }
            this.mPhoneDataConnectionType = bin;
            this.mPhoneDataConnectionsTimer[bin].startRunningLocked(this);
        }
    }

    public void noteWifiOnLocked() {
        if (!this.mWifiOn) {
            this.mHistoryCur.states |= 131072;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mWifiOn = true;
            this.mWifiOnTimer.startRunningLocked(this);
        }
    }

    public void noteWifiOffLocked() {
        if (this.mWifiOn) {
            this.mHistoryCur.states &= -131073;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mWifiOn = false;
            this.mWifiOnTimer.stopRunningLocked(this);
        }
        if (this.mWifiOnUid >= 0) {
            getUidStatsLocked(this.mWifiOnUid).noteWifiStoppedLocked();
            this.mWifiOnUid = -1;
        }
    }

    public void noteAudioOnLocked(int uid) {
        if (!this.mAudioOn) {
            this.mHistoryCur.states |= 4194304;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mAudioOn = true;
            this.mAudioOnTimer.startRunningLocked(this);
        }
        getUidStatsLocked(uid).noteAudioTurnedOnLocked();
    }

    public void noteAudioOffLocked(int uid) {
        if (this.mAudioOn) {
            this.mHistoryCur.states &= -4194305;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mAudioOn = false;
            this.mAudioOnTimer.stopRunningLocked(this);
        }
        getUidStatsLocked(uid).noteAudioTurnedOffLocked();
    }

    public void noteVideoOnLocked(int uid) {
        if (!this.mVideoOn) {
            this.mHistoryCur.states |= 2097152;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mVideoOn = true;
            this.mVideoOnTimer.startRunningLocked(this);
        }
        getUidStatsLocked(uid).noteVideoTurnedOnLocked();
    }

    public void noteVideoOffLocked(int uid) {
        if (this.mVideoOn) {
            this.mHistoryCur.states &= -2097153;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mVideoOn = false;
            this.mVideoOnTimer.stopRunningLocked(this);
        }
        getUidStatsLocked(uid).noteVideoTurnedOffLocked();
    }

    public void noteActivityResumedLocked(int uid) {
        getUidStatsLocked(uid).noteActivityResumedLocked();
    }

    public void noteActivityPausedLocked(int uid) {
        getUidStatsLocked(uid).noteActivityPausedLocked();
    }

    public void noteVibratorOnLocked(int uid, long durationMillis) {
        getUidStatsLocked(uid).noteVibratorOnLocked(durationMillis);
    }

    public void noteVibratorOffLocked(int uid) {
        getUidStatsLocked(uid).noteVibratorOffLocked();
    }

    public void noteWifiRunningLocked(WorkSource ws) {
        if (!this.mGlobalWifiRunning) {
            this.mHistoryCur.states |= 67108864;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mGlobalWifiRunning = true;
            this.mGlobalWifiRunningTimer.startRunningLocked(this);
            int N = ws.size();
            for (int i = 0; i < N; i++) {
                getUidStatsLocked(ws.get(i)).noteWifiRunningLocked();
            }
            return;
        }
        Log.w(TAG, "noteWifiRunningLocked -- called while WIFI running");
    }

    public void noteWifiRunningChangedLocked(WorkSource oldWs, WorkSource newWs) {
        if (this.mGlobalWifiRunning) {
            int N = oldWs.size();
            for (int i = 0; i < N; i++) {
                getUidStatsLocked(oldWs.get(i)).noteWifiStoppedLocked();
            }
            int N2 = newWs.size();
            for (int i2 = 0; i2 < N2; i2++) {
                getUidStatsLocked(newWs.get(i2)).noteWifiRunningLocked();
            }
            return;
        }
        Log.w(TAG, "noteWifiRunningChangedLocked -- called while WIFI not running");
    }

    public void noteWifiStoppedLocked(WorkSource ws) {
        if (this.mGlobalWifiRunning) {
            this.mHistoryCur.states &= -67108865;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mGlobalWifiRunning = false;
            this.mGlobalWifiRunningTimer.stopRunningLocked(this);
            int N = ws.size();
            for (int i = 0; i < N; i++) {
                getUidStatsLocked(ws.get(i)).noteWifiStoppedLocked();
            }
            return;
        }
        Log.w(TAG, "noteWifiStoppedLocked -- called while WIFI not running");
    }

    public void noteBluetoothOnLocked() {
        if (!this.mBluetoothOn) {
            this.mHistoryCur.states |= 65536;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mBluetoothOn = true;
            this.mBluetoothOnTimer.startRunningLocked(this);
        }
    }

    public void noteBluetoothOffLocked() {
        if (this.mBluetoothOn) {
            this.mHistoryCur.states &= -65537;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
            this.mBluetoothOn = false;
            this.mBluetoothOnTimer.stopRunningLocked(this);
        }
    }

    public void noteFullWifiLockAcquiredLocked(int uid) {
        if (this.mWifiFullLockNesting == 0) {
            this.mHistoryCur.states |= 33554432;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        this.mWifiFullLockNesting++;
        getUidStatsLocked(uid).noteFullWifiLockAcquiredLocked();
    }

    public void noteFullWifiLockReleasedLocked(int uid) {
        this.mWifiFullLockNesting--;
        if (this.mWifiFullLockNesting == 0) {
            this.mHistoryCur.states &= -33554433;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        getUidStatsLocked(uid).noteFullWifiLockReleasedLocked();
    }

    public void noteWifiScanStartedLocked(int uid) {
        if (this.mWifiScanNesting == 0) {
            this.mHistoryCur.states |= 16777216;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        this.mWifiScanNesting++;
        getUidStatsLocked(uid).noteWifiScanStartedLocked();
    }

    public void noteWifiScanStoppedLocked(int uid) {
        this.mWifiScanNesting--;
        if (this.mWifiScanNesting == 0) {
            this.mHistoryCur.states &= -16777217;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        getUidStatsLocked(uid).noteWifiScanStoppedLocked();
    }

    public void noteWifiMulticastEnabledLocked(int uid) {
        if (this.mWifiMulticastNesting == 0) {
            this.mHistoryCur.states |= 8388608;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        this.mWifiMulticastNesting++;
        getUidStatsLocked(uid).noteWifiMulticastEnabledLocked();
    }

    public void noteWifiMulticastDisabledLocked(int uid) {
        this.mWifiMulticastNesting--;
        if (this.mWifiMulticastNesting == 0) {
            this.mHistoryCur.states &= -8388609;
            addHistoryRecordLocked(SystemClock.elapsedRealtime());
        }
        getUidStatsLocked(uid).noteWifiMulticastDisabledLocked();
    }

    public void noteFullWifiLockAcquiredFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = 0; i < N; i++) {
            noteFullWifiLockAcquiredLocked(ws.get(i));
        }
    }

    public void noteFullWifiLockReleasedFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = 0; i < N; i++) {
            noteFullWifiLockReleasedLocked(ws.get(i));
        }
    }

    public void noteWifiScanStartedFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = 0; i < N; i++) {
            noteWifiScanStartedLocked(ws.get(i));
        }
    }

    public void noteWifiScanStoppedFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = 0; i < N; i++) {
            noteWifiScanStoppedLocked(ws.get(i));
        }
    }

    public void noteWifiMulticastEnabledFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = 0; i < N; i++) {
            noteWifiMulticastEnabledLocked(ws.get(i));
        }
    }

    public void noteWifiMulticastDisabledFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = 0; i < N; i++) {
            noteWifiMulticastDisabledLocked(ws.get(i));
        }
    }

    public void noteNetworkInterfaceTypeLocked(String iface, int networkType) {
        if (ConnectivityManager.isNetworkTypeMobile(networkType)) {
            this.mMobileIfaces.add(iface);
        } else {
            this.mMobileIfaces.remove(iface);
        }
        if (ConnectivityManager.isNetworkTypeWifi(networkType)) {
            this.mWifiIfaces.add(iface);
        } else {
            this.mWifiIfaces.remove(iface);
        }
    }

    public void noteNetworkStatsEnabledLocked() {
        updateNetworkActivityLocked();
    }

    @Override // android.os.BatteryStats
    public long getScreenOnTime(long batteryRealtime, int which) {
        return this.mScreenOnTimer.getTotalTimeLocked(batteryRealtime, which);
    }

    @Override // android.os.BatteryStats
    public long getScreenBrightnessTime(int brightnessBin, long batteryRealtime, int which) {
        return this.mScreenBrightnessTimer[brightnessBin].getTotalTimeLocked(batteryRealtime, which);
    }

    @Override // android.os.BatteryStats
    public int getInputEventCount(int which) {
        return this.mInputEventCounter.getCountLocked(which);
    }

    @Override // android.os.BatteryStats
    public long getPhoneOnTime(long batteryRealtime, int which) {
        return this.mPhoneOnTimer.getTotalTimeLocked(batteryRealtime, which);
    }

    @Override // android.os.BatteryStats
    public long getPhoneSignalStrengthTime(int strengthBin, long batteryRealtime, int which) {
        return this.mPhoneSignalStrengthsTimer[strengthBin].getTotalTimeLocked(batteryRealtime, which);
    }

    @Override // android.os.BatteryStats
    public long getPhoneSignalScanningTime(long batteryRealtime, int which) {
        return this.mPhoneSignalScanningTimer.getTotalTimeLocked(batteryRealtime, which);
    }

    @Override // android.os.BatteryStats
    public int getPhoneSignalStrengthCount(int strengthBin, int which) {
        return this.mPhoneSignalStrengthsTimer[strengthBin].getCountLocked(which);
    }

    @Override // android.os.BatteryStats
    public long getPhoneDataConnectionTime(int dataType, long batteryRealtime, int which) {
        return this.mPhoneDataConnectionsTimer[dataType].getTotalTimeLocked(batteryRealtime, which);
    }

    @Override // android.os.BatteryStats
    public int getPhoneDataConnectionCount(int dataType, int which) {
        return this.mPhoneDataConnectionsTimer[dataType].getCountLocked(which);
    }

    @Override // android.os.BatteryStats
    public long getWifiOnTime(long batteryRealtime, int which) {
        return this.mWifiOnTimer.getTotalTimeLocked(batteryRealtime, which);
    }

    @Override // android.os.BatteryStats
    public long getGlobalWifiRunningTime(long batteryRealtime, int which) {
        return this.mGlobalWifiRunningTimer.getTotalTimeLocked(batteryRealtime, which);
    }

    @Override // android.os.BatteryStats
    public long getBluetoothOnTime(long batteryRealtime, int which) {
        return this.mBluetoothOnTimer.getTotalTimeLocked(batteryRealtime, which);
    }

    @Override // android.os.BatteryStats
    public long getNetworkActivityCount(int type, int which) {
        if (type >= 0 && type < this.mNetworkActivityCounters.length) {
            return this.mNetworkActivityCounters[type].getCountLocked(which);
        }
        return 0L;
    }

    @Override // android.os.BatteryStats
    public boolean getIsOnBattery() {
        return this.mOnBattery;
    }

    @Override // android.os.BatteryStats
    public SparseArray<? extends BatteryStats.Uid> getUidStats() {
        return this.mUidStats;
    }

    /* loaded from: BatteryStatsImpl$Uid.class */
    public final class Uid extends BatteryStats.Uid {
        final int mUid;
        boolean mWifiRunning;
        StopwatchTimer mWifiRunningTimer;
        boolean mFullWifiLockOut;
        StopwatchTimer mFullWifiLockTimer;
        boolean mWifiScanStarted;
        StopwatchTimer mWifiScanTimer;
        boolean mWifiMulticastEnabled;
        StopwatchTimer mWifiMulticastTimer;
        boolean mAudioTurnedOn;
        StopwatchTimer mAudioTurnedOnTimer;
        boolean mVideoTurnedOn;
        StopwatchTimer mVideoTurnedOnTimer;
        StopwatchTimer mForegroundActivityTimer;
        BatchTimer mVibratorOnTimer;
        Counter[] mUserActivityCounters;
        LongSamplingCounter[] mNetworkActivityCounters;
        final HashMap<String, Wakelock> mWakelockStats = new HashMap<>();
        final HashMap<Integer, Sensor> mSensorStats = new HashMap<>();
        final HashMap<String, Proc> mProcessStats = new HashMap<>();
        final HashMap<String, Pkg> mPackageStats = new HashMap<>();
        final SparseArray<BatteryStats.Uid.Pid> mPids = new SparseArray<>();

        public Uid(int uid) {
            this.mUid = uid;
            this.mWifiRunningTimer = new StopwatchTimer(this, 4, BatteryStatsImpl.this.mWifiRunningTimers, BatteryStatsImpl.this.mUnpluggables);
            this.mFullWifiLockTimer = new StopwatchTimer(this, 5, BatteryStatsImpl.this.mFullWifiLockTimers, BatteryStatsImpl.this.mUnpluggables);
            this.mWifiScanTimer = new StopwatchTimer(this, 6, BatteryStatsImpl.this.mWifiScanTimers, BatteryStatsImpl.this.mUnpluggables);
            this.mWifiMulticastTimer = new StopwatchTimer(this, 7, BatteryStatsImpl.this.mWifiMulticastTimers, BatteryStatsImpl.this.mUnpluggables);
        }

        @Override // android.os.BatteryStats.Uid
        public Map<String, ? extends BatteryStats.Uid.Wakelock> getWakelockStats() {
            return this.mWakelockStats;
        }

        @Override // android.os.BatteryStats.Uid
        public Map<Integer, ? extends BatteryStats.Uid.Sensor> getSensorStats() {
            return this.mSensorStats;
        }

        @Override // android.os.BatteryStats.Uid
        public Map<String, ? extends BatteryStats.Uid.Proc> getProcessStats() {
            return this.mProcessStats;
        }

        @Override // android.os.BatteryStats.Uid
        public Map<String, ? extends BatteryStats.Uid.Pkg> getPackageStats() {
            return this.mPackageStats;
        }

        @Override // android.os.BatteryStats.Uid
        public int getUid() {
            return this.mUid;
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiRunningLocked() {
            if (!this.mWifiRunning) {
                this.mWifiRunning = true;
                if (this.mWifiRunningTimer == null) {
                    this.mWifiRunningTimer = new StopwatchTimer(this, 4, BatteryStatsImpl.this.mWifiRunningTimers, BatteryStatsImpl.this.mUnpluggables);
                }
                this.mWifiRunningTimer.startRunningLocked(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiStoppedLocked() {
            if (this.mWifiRunning) {
                this.mWifiRunning = false;
                this.mWifiRunningTimer.stopRunningLocked(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteFullWifiLockAcquiredLocked() {
            if (!this.mFullWifiLockOut) {
                this.mFullWifiLockOut = true;
                if (this.mFullWifiLockTimer == null) {
                    this.mFullWifiLockTimer = new StopwatchTimer(this, 5, BatteryStatsImpl.this.mFullWifiLockTimers, BatteryStatsImpl.this.mUnpluggables);
                }
                this.mFullWifiLockTimer.startRunningLocked(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteFullWifiLockReleasedLocked() {
            if (this.mFullWifiLockOut) {
                this.mFullWifiLockOut = false;
                this.mFullWifiLockTimer.stopRunningLocked(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiScanStartedLocked() {
            if (!this.mWifiScanStarted) {
                this.mWifiScanStarted = true;
                if (this.mWifiScanTimer == null) {
                    this.mWifiScanTimer = new StopwatchTimer(this, 6, BatteryStatsImpl.this.mWifiScanTimers, BatteryStatsImpl.this.mUnpluggables);
                }
                this.mWifiScanTimer.startRunningLocked(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiScanStoppedLocked() {
            if (this.mWifiScanStarted) {
                this.mWifiScanStarted = false;
                this.mWifiScanTimer.stopRunningLocked(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiMulticastEnabledLocked() {
            if (!this.mWifiMulticastEnabled) {
                this.mWifiMulticastEnabled = true;
                if (this.mWifiMulticastTimer == null) {
                    this.mWifiMulticastTimer = new StopwatchTimer(this, 7, BatteryStatsImpl.this.mWifiMulticastTimers, BatteryStatsImpl.this.mUnpluggables);
                }
                this.mWifiMulticastTimer.startRunningLocked(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteWifiMulticastDisabledLocked() {
            if (this.mWifiMulticastEnabled) {
                this.mWifiMulticastEnabled = false;
                this.mWifiMulticastTimer.stopRunningLocked(BatteryStatsImpl.this);
            }
        }

        public StopwatchTimer createAudioTurnedOnTimerLocked() {
            if (this.mAudioTurnedOnTimer == null) {
                this.mAudioTurnedOnTimer = new StopwatchTimer(this, 7, null, BatteryStatsImpl.this.mUnpluggables);
            }
            return this.mAudioTurnedOnTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public void noteAudioTurnedOnLocked() {
            if (!this.mAudioTurnedOn) {
                this.mAudioTurnedOn = true;
                createAudioTurnedOnTimerLocked().startRunningLocked(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteAudioTurnedOffLocked() {
            if (this.mAudioTurnedOn) {
                this.mAudioTurnedOn = false;
                if (this.mAudioTurnedOnTimer != null) {
                    this.mAudioTurnedOnTimer.stopRunningLocked(BatteryStatsImpl.this);
                }
            }
        }

        public StopwatchTimer createVideoTurnedOnTimerLocked() {
            if (this.mVideoTurnedOnTimer == null) {
                this.mVideoTurnedOnTimer = new StopwatchTimer(this, 8, null, BatteryStatsImpl.this.mUnpluggables);
            }
            return this.mVideoTurnedOnTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public void noteVideoTurnedOnLocked() {
            if (!this.mVideoTurnedOn) {
                this.mVideoTurnedOn = true;
                createVideoTurnedOnTimerLocked().startRunningLocked(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public void noteVideoTurnedOffLocked() {
            if (this.mVideoTurnedOn) {
                this.mVideoTurnedOn = false;
                if (this.mVideoTurnedOnTimer != null) {
                    this.mVideoTurnedOnTimer.stopRunningLocked(BatteryStatsImpl.this);
                }
            }
        }

        public StopwatchTimer createForegroundActivityTimerLocked() {
            if (this.mForegroundActivityTimer == null) {
                this.mForegroundActivityTimer = new StopwatchTimer(this, 10, null, BatteryStatsImpl.this.mUnpluggables);
            }
            return this.mForegroundActivityTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public void noteActivityResumedLocked() {
            createForegroundActivityTimerLocked().startRunningLocked(BatteryStatsImpl.this);
        }

        @Override // android.os.BatteryStats.Uid
        public void noteActivityPausedLocked() {
            if (this.mForegroundActivityTimer != null) {
                this.mForegroundActivityTimer.stopRunningLocked(BatteryStatsImpl.this);
            }
        }

        public BatchTimer createVibratorOnTimerLocked() {
            if (this.mVibratorOnTimer == null) {
                this.mVibratorOnTimer = new BatchTimer(this, 9, BatteryStatsImpl.this.mUnpluggables, BatteryStatsImpl.this.mOnBatteryInternal);
            }
            return this.mVibratorOnTimer;
        }

        public void noteVibratorOnLocked(long durationMillis) {
            createVibratorOnTimerLocked().addDuration(BatteryStatsImpl.this, durationMillis);
        }

        public void noteVibratorOffLocked() {
            if (this.mVibratorOnTimer != null) {
                this.mVibratorOnTimer.abortLastDuration(BatteryStatsImpl.this);
            }
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiRunningTime(long batteryRealtime, int which) {
            if (this.mWifiRunningTimer == null) {
                return 0L;
            }
            return this.mWifiRunningTimer.getTotalTimeLocked(batteryRealtime, which);
        }

        @Override // android.os.BatteryStats.Uid
        public long getFullWifiLockTime(long batteryRealtime, int which) {
            if (this.mFullWifiLockTimer == null) {
                return 0L;
            }
            return this.mFullWifiLockTimer.getTotalTimeLocked(batteryRealtime, which);
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiScanTime(long batteryRealtime, int which) {
            if (this.mWifiScanTimer == null) {
                return 0L;
            }
            return this.mWifiScanTimer.getTotalTimeLocked(batteryRealtime, which);
        }

        @Override // android.os.BatteryStats.Uid
        public long getWifiMulticastTime(long batteryRealtime, int which) {
            if (this.mWifiMulticastTimer == null) {
                return 0L;
            }
            return this.mWifiMulticastTimer.getTotalTimeLocked(batteryRealtime, which);
        }

        @Override // android.os.BatteryStats.Uid
        public long getAudioTurnedOnTime(long batteryRealtime, int which) {
            if (this.mAudioTurnedOnTimer == null) {
                return 0L;
            }
            return this.mAudioTurnedOnTimer.getTotalTimeLocked(batteryRealtime, which);
        }

        @Override // android.os.BatteryStats.Uid
        public long getVideoTurnedOnTime(long batteryRealtime, int which) {
            if (this.mVideoTurnedOnTimer == null) {
                return 0L;
            }
            return this.mVideoTurnedOnTimer.getTotalTimeLocked(batteryRealtime, which);
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getForegroundActivityTimer() {
            return this.mForegroundActivityTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public Timer getVibratorOnTimer() {
            return this.mVibratorOnTimer;
        }

        @Override // android.os.BatteryStats.Uid
        public void noteUserActivityLocked(int type) {
            if (this.mUserActivityCounters == null) {
                initUserActivityLocked();
            }
            if (type >= 0 && type < 3) {
                this.mUserActivityCounters[type].stepAtomic();
            } else {
                Slog.w(BatteryStatsImpl.TAG, "Unknown user activity type " + type + " was specified.", new Throwable());
            }
        }

        @Override // android.os.BatteryStats.Uid
        public boolean hasUserActivity() {
            return this.mUserActivityCounters != null;
        }

        @Override // android.os.BatteryStats.Uid
        public int getUserActivityCount(int type, int which) {
            if (this.mUserActivityCounters == null) {
                return 0;
            }
            return this.mUserActivityCounters[type].getCountLocked(which);
        }

        void initUserActivityLocked() {
            this.mUserActivityCounters = new Counter[3];
            for (int i = 0; i < 3; i++) {
                this.mUserActivityCounters[i] = new Counter(BatteryStatsImpl.this.mUnpluggables);
            }
        }

        void noteNetworkActivityLocked(int type, long delta) {
            if (this.mNetworkActivityCounters == null) {
                initNetworkActivityLocked();
            }
            if (type >= 0 && type < 4) {
                this.mNetworkActivityCounters[type].addCountLocked(delta);
            } else {
                Slog.w(BatteryStatsImpl.TAG, "Unknown network activity type " + type + " was specified.", new Throwable());
            }
        }

        @Override // android.os.BatteryStats.Uid
        public boolean hasNetworkActivity() {
            return this.mNetworkActivityCounters != null;
        }

        @Override // android.os.BatteryStats.Uid
        public long getNetworkActivityCount(int type, int which) {
            if (this.mNetworkActivityCounters != null && type >= 0 && type < this.mNetworkActivityCounters.length) {
                return this.mNetworkActivityCounters[type].getCountLocked(which);
            }
            return 0L;
        }

        void initNetworkActivityLocked() {
            this.mNetworkActivityCounters = new LongSamplingCounter[4];
            for (int i = 0; i < 4; i++) {
                this.mNetworkActivityCounters[i] = new LongSamplingCounter(BatteryStatsImpl.this.mUnpluggables);
            }
        }

        boolean reset() {
            boolean active = false;
            if (this.mWifiRunningTimer != null) {
                boolean active2 = false | (!this.mWifiRunningTimer.reset(BatteryStatsImpl.this, false));
                active = active2 | this.mWifiRunning;
            }
            if (this.mFullWifiLockTimer != null) {
                active = active | (!this.mFullWifiLockTimer.reset(BatteryStatsImpl.this, false)) | this.mFullWifiLockOut;
            }
            if (this.mWifiScanTimer != null) {
                active = active | (!this.mWifiScanTimer.reset(BatteryStatsImpl.this, false)) | this.mWifiScanStarted;
            }
            if (this.mWifiMulticastTimer != null) {
                active = active | (!this.mWifiMulticastTimer.reset(BatteryStatsImpl.this, false)) | this.mWifiMulticastEnabled;
            }
            if (this.mAudioTurnedOnTimer != null) {
                active = active | (!this.mAudioTurnedOnTimer.reset(BatteryStatsImpl.this, false)) | this.mAudioTurnedOn;
            }
            if (this.mVideoTurnedOnTimer != null) {
                active = active | (!this.mVideoTurnedOnTimer.reset(BatteryStatsImpl.this, false)) | this.mVideoTurnedOn;
            }
            if (this.mForegroundActivityTimer != null) {
                active |= !this.mForegroundActivityTimer.reset(BatteryStatsImpl.this, false);
            }
            if (this.mVibratorOnTimer != null) {
                if (this.mVibratorOnTimer.reset(BatteryStatsImpl.this, false)) {
                    this.mVibratorOnTimer.detach();
                    this.mVibratorOnTimer = null;
                } else {
                    active = true;
                }
            }
            if (this.mUserActivityCounters != null) {
                for (int i = 0; i < 3; i++) {
                    this.mUserActivityCounters[i].reset(false);
                }
            }
            if (this.mNetworkActivityCounters != null) {
                for (int i2 = 0; i2 < 4; i2++) {
                    this.mNetworkActivityCounters[i2].reset(false);
                }
            }
            if (this.mWakelockStats.size() > 0) {
                Iterator<Map.Entry<String, Wakelock>> it = this.mWakelockStats.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Wakelock> wakelockEntry = it.next();
                    Wakelock wl = wakelockEntry.getValue();
                    if (wl.reset()) {
                        it.remove();
                    } else {
                        active = true;
                    }
                }
            }
            if (this.mSensorStats.size() > 0) {
                Iterator<Map.Entry<Integer, Sensor>> it2 = this.mSensorStats.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<Integer, Sensor> sensorEntry = it2.next();
                    Sensor s = sensorEntry.getValue();
                    if (s.reset()) {
                        it2.remove();
                    } else {
                        active = true;
                    }
                }
            }
            if (this.mProcessStats.size() > 0) {
                for (Map.Entry<String, Proc> procEntry : this.mProcessStats.entrySet()) {
                    procEntry.getValue().detach();
                }
                this.mProcessStats.clear();
            }
            if (this.mPids.size() > 0) {
                for (int i3 = 0; !active && i3 < this.mPids.size(); i3++) {
                    BatteryStats.Uid.Pid pid = this.mPids.valueAt(i3);
                    if (pid.mWakeStart != 0) {
                        active = true;
                    }
                }
            }
            if (this.mPackageStats.size() > 0) {
                for (Map.Entry<String, Pkg> pkgEntry : this.mPackageStats.entrySet()) {
                    Pkg p = pkgEntry.getValue();
                    p.detach();
                    if (p.mServiceStats.size() > 0) {
                        for (Map.Entry<String, Pkg.Serv> servEntry : p.mServiceStats.entrySet()) {
                            servEntry.getValue().detach();
                        }
                    }
                }
                this.mPackageStats.clear();
            }
            this.mPids.clear();
            if (!active) {
                if (this.mWifiRunningTimer != null) {
                    this.mWifiRunningTimer.detach();
                }
                if (this.mFullWifiLockTimer != null) {
                    this.mFullWifiLockTimer.detach();
                }
                if (this.mWifiScanTimer != null) {
                    this.mWifiScanTimer.detach();
                }
                if (this.mWifiMulticastTimer != null) {
                    this.mWifiMulticastTimer.detach();
                }
                if (this.mAudioTurnedOnTimer != null) {
                    this.mAudioTurnedOnTimer.detach();
                    this.mAudioTurnedOnTimer = null;
                }
                if (this.mVideoTurnedOnTimer != null) {
                    this.mVideoTurnedOnTimer.detach();
                    this.mVideoTurnedOnTimer = null;
                }
                if (this.mForegroundActivityTimer != null) {
                    this.mForegroundActivityTimer.detach();
                    this.mForegroundActivityTimer = null;
                }
                if (this.mUserActivityCounters != null) {
                    for (int i4 = 0; i4 < 3; i4++) {
                        this.mUserActivityCounters[i4].detach();
                    }
                }
                if (this.mNetworkActivityCounters != null) {
                    for (int i5 = 0; i5 < 4; i5++) {
                        this.mNetworkActivityCounters[i5].detach();
                    }
                }
            }
            return !active;
        }

        void writeToParcelLocked(Parcel out, long batteryRealtime) {
            out.writeInt(this.mWakelockStats.size());
            for (Map.Entry<String, Wakelock> wakelockEntry : this.mWakelockStats.entrySet()) {
                out.writeString(wakelockEntry.getKey());
                Wakelock wakelock = wakelockEntry.getValue();
                wakelock.writeToParcelLocked(out, batteryRealtime);
            }
            out.writeInt(this.mSensorStats.size());
            for (Map.Entry<Integer, Sensor> sensorEntry : this.mSensorStats.entrySet()) {
                out.writeInt(sensorEntry.getKey().intValue());
                Sensor sensor = sensorEntry.getValue();
                sensor.writeToParcelLocked(out, batteryRealtime);
            }
            out.writeInt(this.mProcessStats.size());
            for (Map.Entry<String, Proc> procEntry : this.mProcessStats.entrySet()) {
                out.writeString(procEntry.getKey());
                Proc proc = procEntry.getValue();
                proc.writeToParcelLocked(out);
            }
            out.writeInt(this.mPackageStats.size());
            for (Map.Entry<String, Pkg> pkgEntry : this.mPackageStats.entrySet()) {
                out.writeString(pkgEntry.getKey());
                Pkg pkg = pkgEntry.getValue();
                pkg.writeToParcelLocked(out);
            }
            if (this.mWifiRunningTimer != null) {
                out.writeInt(1);
                this.mWifiRunningTimer.writeToParcel(out, batteryRealtime);
            } else {
                out.writeInt(0);
            }
            if (this.mFullWifiLockTimer != null) {
                out.writeInt(1);
                this.mFullWifiLockTimer.writeToParcel(out, batteryRealtime);
            } else {
                out.writeInt(0);
            }
            if (this.mWifiScanTimer != null) {
                out.writeInt(1);
                this.mWifiScanTimer.writeToParcel(out, batteryRealtime);
            } else {
                out.writeInt(0);
            }
            if (this.mWifiMulticastTimer != null) {
                out.writeInt(1);
                this.mWifiMulticastTimer.writeToParcel(out, batteryRealtime);
            } else {
                out.writeInt(0);
            }
            if (this.mAudioTurnedOnTimer != null) {
                out.writeInt(1);
                this.mAudioTurnedOnTimer.writeToParcel(out, batteryRealtime);
            } else {
                out.writeInt(0);
            }
            if (this.mVideoTurnedOnTimer != null) {
                out.writeInt(1);
                this.mVideoTurnedOnTimer.writeToParcel(out, batteryRealtime);
            } else {
                out.writeInt(0);
            }
            if (this.mForegroundActivityTimer != null) {
                out.writeInt(1);
                this.mForegroundActivityTimer.writeToParcel(out, batteryRealtime);
            } else {
                out.writeInt(0);
            }
            if (this.mVibratorOnTimer != null) {
                out.writeInt(1);
                this.mVibratorOnTimer.writeToParcel(out, batteryRealtime);
            } else {
                out.writeInt(0);
            }
            if (this.mUserActivityCounters != null) {
                out.writeInt(1);
                for (int i = 0; i < 3; i++) {
                    this.mUserActivityCounters[i].writeToParcel(out);
                }
            } else {
                out.writeInt(0);
            }
            if (this.mNetworkActivityCounters != null) {
                out.writeInt(1);
                for (int i2 = 0; i2 < 4; i2++) {
                    this.mNetworkActivityCounters[i2].writeToParcel(out);
                }
                return;
            }
            out.writeInt(0);
        }

        void readFromParcelLocked(ArrayList<Unpluggable> unpluggables, Parcel in) {
            int numWakelocks = in.readInt();
            this.mWakelockStats.clear();
            for (int j = 0; j < numWakelocks; j++) {
                String wakelockName = in.readString();
                Wakelock wakelock = new Wakelock();
                wakelock.readFromParcelLocked(unpluggables, in);
                this.mWakelockStats.put(wakelockName, wakelock);
            }
            int numSensors = in.readInt();
            this.mSensorStats.clear();
            for (int k = 0; k < numSensors; k++) {
                int sensorNumber = in.readInt();
                Sensor sensor = new Sensor(sensorNumber);
                sensor.readFromParcelLocked(BatteryStatsImpl.this.mUnpluggables, in);
                this.mSensorStats.put(Integer.valueOf(sensorNumber), sensor);
            }
            int numProcs = in.readInt();
            this.mProcessStats.clear();
            for (int k2 = 0; k2 < numProcs; k2++) {
                String processName = in.readString();
                Proc proc = new Proc();
                proc.readFromParcelLocked(in);
                this.mProcessStats.put(processName, proc);
            }
            int numPkgs = in.readInt();
            this.mPackageStats.clear();
            for (int l = 0; l < numPkgs; l++) {
                String packageName = in.readString();
                Pkg pkg = new Pkg();
                pkg.readFromParcelLocked(in);
                this.mPackageStats.put(packageName, pkg);
            }
            this.mWifiRunning = false;
            if (in.readInt() != 0) {
                this.mWifiRunningTimer = new StopwatchTimer(this, 4, BatteryStatsImpl.this.mWifiRunningTimers, BatteryStatsImpl.this.mUnpluggables, in);
            } else {
                this.mWifiRunningTimer = null;
            }
            this.mFullWifiLockOut = false;
            if (in.readInt() != 0) {
                this.mFullWifiLockTimer = new StopwatchTimer(this, 5, BatteryStatsImpl.this.mFullWifiLockTimers, BatteryStatsImpl.this.mUnpluggables, in);
            } else {
                this.mFullWifiLockTimer = null;
            }
            this.mWifiScanStarted = false;
            if (in.readInt() != 0) {
                this.mWifiScanTimer = new StopwatchTimer(this, 6, BatteryStatsImpl.this.mWifiScanTimers, BatteryStatsImpl.this.mUnpluggables, in);
            } else {
                this.mWifiScanTimer = null;
            }
            this.mWifiMulticastEnabled = false;
            if (in.readInt() != 0) {
                this.mWifiMulticastTimer = new StopwatchTimer(this, 7, BatteryStatsImpl.this.mWifiMulticastTimers, BatteryStatsImpl.this.mUnpluggables, in);
            } else {
                this.mWifiMulticastTimer = null;
            }
            this.mAudioTurnedOn = false;
            if (in.readInt() != 0) {
                this.mAudioTurnedOnTimer = new StopwatchTimer(this, 7, null, BatteryStatsImpl.this.mUnpluggables, in);
            } else {
                this.mAudioTurnedOnTimer = null;
            }
            this.mVideoTurnedOn = false;
            if (in.readInt() != 0) {
                this.mVideoTurnedOnTimer = new StopwatchTimer(this, 8, null, BatteryStatsImpl.this.mUnpluggables, in);
            } else {
                this.mVideoTurnedOnTimer = null;
            }
            if (in.readInt() != 0) {
                this.mForegroundActivityTimer = new StopwatchTimer(this, 10, null, BatteryStatsImpl.this.mUnpluggables, in);
            } else {
                this.mForegroundActivityTimer = null;
            }
            if (in.readInt() != 0) {
                this.mVibratorOnTimer = new BatchTimer(this, 9, BatteryStatsImpl.this.mUnpluggables, BatteryStatsImpl.this.mOnBatteryInternal, in);
            } else {
                this.mVibratorOnTimer = null;
            }
            if (in.readInt() != 0) {
                this.mUserActivityCounters = new Counter[3];
                for (int i = 0; i < 3; i++) {
                    this.mUserActivityCounters[i] = new Counter(BatteryStatsImpl.this.mUnpluggables, in);
                }
            } else {
                this.mUserActivityCounters = null;
            }
            if (in.readInt() != 0) {
                this.mNetworkActivityCounters = new LongSamplingCounter[4];
                for (int i2 = 0; i2 < 4; i2++) {
                    this.mNetworkActivityCounters[i2] = new LongSamplingCounter(BatteryStatsImpl.this.mUnpluggables, in);
                }
                return;
            }
            this.mNetworkActivityCounters = null;
        }

        /* loaded from: BatteryStatsImpl$Uid$Wakelock.class */
        public final class Wakelock extends BatteryStats.Uid.Wakelock {
            StopwatchTimer mTimerPartial;
            StopwatchTimer mTimerFull;
            StopwatchTimer mTimerWindow;

            public Wakelock() {
            }

            private StopwatchTimer readTimerFromParcel(int type, ArrayList<StopwatchTimer> pool, ArrayList<Unpluggable> unpluggables, Parcel in) {
                if (in.readInt() == 0) {
                    return null;
                }
                return new StopwatchTimer(Uid.this, type, pool, unpluggables, in);
            }

            boolean reset() {
                boolean wlactive = false;
                if (this.mTimerFull != null) {
                    wlactive = false | (!this.mTimerFull.reset(BatteryStatsImpl.this, false));
                }
                if (this.mTimerPartial != null) {
                    wlactive |= !this.mTimerPartial.reset(BatteryStatsImpl.this, false);
                }
                if (this.mTimerWindow != null) {
                    wlactive |= !this.mTimerWindow.reset(BatteryStatsImpl.this, false);
                }
                if (!wlactive) {
                    if (this.mTimerFull != null) {
                        this.mTimerFull.detach();
                        this.mTimerFull = null;
                    }
                    if (this.mTimerPartial != null) {
                        this.mTimerPartial.detach();
                        this.mTimerPartial = null;
                    }
                    if (this.mTimerWindow != null) {
                        this.mTimerWindow.detach();
                        this.mTimerWindow = null;
                    }
                }
                return !wlactive;
            }

            void readFromParcelLocked(ArrayList<Unpluggable> unpluggables, Parcel in) {
                this.mTimerPartial = readTimerFromParcel(0, BatteryStatsImpl.this.mPartialTimers, unpluggables, in);
                this.mTimerFull = readTimerFromParcel(1, BatteryStatsImpl.this.mFullTimers, unpluggables, in);
                this.mTimerWindow = readTimerFromParcel(2, BatteryStatsImpl.this.mWindowTimers, unpluggables, in);
            }

            void writeToParcelLocked(Parcel out, long batteryRealtime) {
                Timer.writeTimerToParcel(out, this.mTimerPartial, batteryRealtime);
                Timer.writeTimerToParcel(out, this.mTimerFull, batteryRealtime);
                Timer.writeTimerToParcel(out, this.mTimerWindow, batteryRealtime);
            }

            @Override // android.os.BatteryStats.Uid.Wakelock
            public Timer getWakeTime(int type) {
                switch (type) {
                    case 0:
                        return this.mTimerPartial;
                    case 1:
                        return this.mTimerFull;
                    case 2:
                        return this.mTimerWindow;
                    default:
                        throw new IllegalArgumentException("type = " + type);
                }
            }
        }

        /* loaded from: BatteryStatsImpl$Uid$Sensor.class */
        public final class Sensor extends BatteryStats.Uid.Sensor {
            final int mHandle;
            StopwatchTimer mTimer;

            public Sensor(int handle) {
                this.mHandle = handle;
            }

            private StopwatchTimer readTimerFromParcel(ArrayList<Unpluggable> unpluggables, Parcel in) {
                if (in.readInt() == 0) {
                    return null;
                }
                ArrayList<StopwatchTimer> pool = BatteryStatsImpl.this.mSensorTimers.get(this.mHandle);
                if (pool == null) {
                    pool = new ArrayList<>();
                    BatteryStatsImpl.this.mSensorTimers.put(this.mHandle, pool);
                }
                return new StopwatchTimer(Uid.this, 0, pool, unpluggables, in);
            }

            boolean reset() {
                if (this.mTimer.reset(BatteryStatsImpl.this, true)) {
                    this.mTimer = null;
                    return true;
                }
                return false;
            }

            void readFromParcelLocked(ArrayList<Unpluggable> unpluggables, Parcel in) {
                this.mTimer = readTimerFromParcel(unpluggables, in);
            }

            void writeToParcelLocked(Parcel out, long batteryRealtime) {
                Timer.writeTimerToParcel(out, this.mTimer, batteryRealtime);
            }

            @Override // android.os.BatteryStats.Uid.Sensor
            public Timer getSensorTime() {
                return this.mTimer;
            }

            @Override // android.os.BatteryStats.Uid.Sensor
            public int getHandle() {
                return this.mHandle;
            }
        }

        /* loaded from: BatteryStatsImpl$Uid$Proc.class */
        public final class Proc extends BatteryStats.Uid.Proc implements Unpluggable {
            long mUserTime;
            long mSystemTime;
            long mForegroundTime;
            int mStarts;
            long mLoadedUserTime;
            long mLoadedSystemTime;
            long mLoadedForegroundTime;
            int mLoadedStarts;
            long mLastUserTime;
            long mLastSystemTime;
            long mLastForegroundTime;
            int mLastStarts;
            long mUnpluggedUserTime;
            long mUnpluggedSystemTime;
            long mUnpluggedForegroundTime;
            int mUnpluggedStarts;
            SamplingCounter[] mSpeedBins;
            ArrayList<BatteryStats.Uid.Proc.ExcessivePower> mExcessivePower;

            Proc() {
                BatteryStatsImpl.this.mUnpluggables.add(this);
                this.mSpeedBins = new SamplingCounter[BatteryStatsImpl.this.getCpuSpeedSteps()];
            }

            @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
            public void unplug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
                this.mUnpluggedUserTime = this.mUserTime;
                this.mUnpluggedSystemTime = this.mSystemTime;
                this.mUnpluggedForegroundTime = this.mForegroundTime;
                this.mUnpluggedStarts = this.mStarts;
            }

            @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
            public void plug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            }

            void detach() {
                BatteryStatsImpl.this.mUnpluggables.remove(this);
                for (int i = 0; i < this.mSpeedBins.length; i++) {
                    SamplingCounter c = this.mSpeedBins[i];
                    if (c != null) {
                        BatteryStatsImpl.this.mUnpluggables.remove(c);
                        this.mSpeedBins[i] = null;
                    }
                }
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public int countExcessivePowers() {
                if (this.mExcessivePower != null) {
                    return this.mExcessivePower.size();
                }
                return 0;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public BatteryStats.Uid.Proc.ExcessivePower getExcessivePower(int i) {
                if (this.mExcessivePower != null) {
                    return this.mExcessivePower.get(i);
                }
                return null;
            }

            public void addExcessiveWake(long overTime, long usedTime) {
                if (this.mExcessivePower == null) {
                    this.mExcessivePower = new ArrayList<>();
                }
                BatteryStats.Uid.Proc.ExcessivePower ew = new BatteryStats.Uid.Proc.ExcessivePower();
                ew.type = 1;
                ew.overTime = overTime;
                ew.usedTime = usedTime;
                this.mExcessivePower.add(ew);
            }

            public void addExcessiveCpu(long overTime, long usedTime) {
                if (this.mExcessivePower == null) {
                    this.mExcessivePower = new ArrayList<>();
                }
                BatteryStats.Uid.Proc.ExcessivePower ew = new BatteryStats.Uid.Proc.ExcessivePower();
                ew.type = 2;
                ew.overTime = overTime;
                ew.usedTime = usedTime;
                this.mExcessivePower.add(ew);
            }

            void writeExcessivePowerToParcelLocked(Parcel out) {
                if (this.mExcessivePower == null) {
                    out.writeInt(0);
                    return;
                }
                int N = this.mExcessivePower.size();
                out.writeInt(N);
                for (int i = 0; i < N; i++) {
                    BatteryStats.Uid.Proc.ExcessivePower ew = this.mExcessivePower.get(i);
                    out.writeInt(ew.type);
                    out.writeLong(ew.overTime);
                    out.writeLong(ew.usedTime);
                }
            }

            boolean readExcessivePowerFromParcelLocked(Parcel in) {
                int N = in.readInt();
                if (N == 0) {
                    this.mExcessivePower = null;
                    return true;
                } else if (N > 10000) {
                    Slog.w(BatteryStatsImpl.TAG, "File corrupt: too many excessive power entries " + N);
                    return false;
                } else {
                    this.mExcessivePower = new ArrayList<>();
                    for (int i = 0; i < N; i++) {
                        BatteryStats.Uid.Proc.ExcessivePower ew = new BatteryStats.Uid.Proc.ExcessivePower();
                        ew.type = in.readInt();
                        ew.overTime = in.readLong();
                        ew.usedTime = in.readLong();
                        this.mExcessivePower.add(ew);
                    }
                    return true;
                }
            }

            void writeToParcelLocked(Parcel out) {
                out.writeLong(this.mUserTime);
                out.writeLong(this.mSystemTime);
                out.writeLong(this.mForegroundTime);
                out.writeInt(this.mStarts);
                out.writeLong(this.mLoadedUserTime);
                out.writeLong(this.mLoadedSystemTime);
                out.writeLong(this.mLoadedForegroundTime);
                out.writeInt(this.mLoadedStarts);
                out.writeLong(this.mUnpluggedUserTime);
                out.writeLong(this.mUnpluggedSystemTime);
                out.writeLong(this.mUnpluggedForegroundTime);
                out.writeInt(this.mUnpluggedStarts);
                out.writeInt(this.mSpeedBins.length);
                for (int i = 0; i < this.mSpeedBins.length; i++) {
                    SamplingCounter c = this.mSpeedBins[i];
                    if (c != null) {
                        out.writeInt(1);
                        c.writeToParcel(out);
                    } else {
                        out.writeInt(0);
                    }
                }
                writeExcessivePowerToParcelLocked(out);
            }

            void readFromParcelLocked(Parcel in) {
                this.mUserTime = in.readLong();
                this.mSystemTime = in.readLong();
                this.mForegroundTime = in.readLong();
                this.mStarts = in.readInt();
                this.mLoadedUserTime = in.readLong();
                this.mLoadedSystemTime = in.readLong();
                this.mLoadedForegroundTime = in.readLong();
                this.mLoadedStarts = in.readInt();
                this.mLastUserTime = 0L;
                this.mLastSystemTime = 0L;
                this.mLastForegroundTime = 0L;
                this.mLastStarts = 0;
                this.mUnpluggedUserTime = in.readLong();
                this.mUnpluggedSystemTime = in.readLong();
                this.mUnpluggedForegroundTime = in.readLong();
                this.mUnpluggedStarts = in.readInt();
                int bins = in.readInt();
                int steps = BatteryStatsImpl.this.getCpuSpeedSteps();
                this.mSpeedBins = new SamplingCounter[bins >= steps ? bins : steps];
                for (int i = 0; i < bins; i++) {
                    if (in.readInt() != 0) {
                        this.mSpeedBins[i] = new SamplingCounter(BatteryStatsImpl.this.mUnpluggables, in);
                    }
                }
                readExcessivePowerFromParcelLocked(in);
            }

            public BatteryStatsImpl getBatteryStats() {
                return BatteryStatsImpl.this;
            }

            public void addCpuTimeLocked(int utime, int stime) {
                this.mUserTime += utime;
                this.mSystemTime += stime;
            }

            public void addForegroundTimeLocked(long ttime) {
                this.mForegroundTime += ttime;
            }

            public void incStartsLocked() {
                this.mStarts++;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public long getUserTime(int which) {
                long val;
                if (which == 1) {
                    val = this.mLastUserTime;
                } else {
                    val = this.mUserTime;
                    if (which == 2) {
                        val -= this.mLoadedUserTime;
                    } else if (which == 3) {
                        val -= this.mUnpluggedUserTime;
                    }
                }
                return val;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public long getSystemTime(int which) {
                long val;
                if (which == 1) {
                    val = this.mLastSystemTime;
                } else {
                    val = this.mSystemTime;
                    if (which == 2) {
                        val -= this.mLoadedSystemTime;
                    } else if (which == 3) {
                        val -= this.mUnpluggedSystemTime;
                    }
                }
                return val;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public long getForegroundTime(int which) {
                long val;
                if (which == 1) {
                    val = this.mLastForegroundTime;
                } else {
                    val = this.mForegroundTime;
                    if (which == 2) {
                        val -= this.mLoadedForegroundTime;
                    } else if (which == 3) {
                        val -= this.mUnpluggedForegroundTime;
                    }
                }
                return val;
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public int getStarts(int which) {
                int val;
                if (which == 1) {
                    val = this.mLastStarts;
                } else {
                    val = this.mStarts;
                    if (which == 2) {
                        val -= this.mLoadedStarts;
                    } else if (which == 3) {
                        val -= this.mUnpluggedStarts;
                    }
                }
                return val;
            }

            public void addSpeedStepTimes(long[] values) {
                for (int i = 0; i < this.mSpeedBins.length && i < values.length; i++) {
                    long amt = values[i];
                    if (amt != 0) {
                        SamplingCounter c = this.mSpeedBins[i];
                        if (c == null) {
                            SamplingCounter samplingCounter = new SamplingCounter(BatteryStatsImpl.this.mUnpluggables);
                            c = samplingCounter;
                            this.mSpeedBins[i] = samplingCounter;
                        }
                        c.addCountAtomic(values[i]);
                    }
                }
            }

            @Override // android.os.BatteryStats.Uid.Proc
            public long getTimeAtCpuSpeedStep(int speedStep, int which) {
                SamplingCounter c;
                if (speedStep >= this.mSpeedBins.length || (c = this.mSpeedBins[speedStep]) == null) {
                    return 0L;
                }
                return c.getCountLocked(which);
            }
        }

        /* loaded from: BatteryStatsImpl$Uid$Pkg.class */
        public final class Pkg extends BatteryStats.Uid.Pkg implements Unpluggable {
            int mWakeups;
            int mLoadedWakeups;
            int mLastWakeups;
            int mUnpluggedWakeups;
            final HashMap<String, Serv> mServiceStats = new HashMap<>();

            Pkg() {
                BatteryStatsImpl.this.mUnpluggables.add(this);
            }

            @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
            public void unplug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
                this.mUnpluggedWakeups = this.mWakeups;
            }

            @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
            public void plug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
            }

            void detach() {
                BatteryStatsImpl.this.mUnpluggables.remove(this);
            }

            void readFromParcelLocked(Parcel in) {
                this.mWakeups = in.readInt();
                this.mLoadedWakeups = in.readInt();
                this.mLastWakeups = 0;
                this.mUnpluggedWakeups = in.readInt();
                int numServs = in.readInt();
                this.mServiceStats.clear();
                for (int m = 0; m < numServs; m++) {
                    String serviceName = in.readString();
                    Serv serv = new Serv();
                    this.mServiceStats.put(serviceName, serv);
                    serv.readFromParcelLocked(in);
                }
            }

            void writeToParcelLocked(Parcel out) {
                out.writeInt(this.mWakeups);
                out.writeInt(this.mLoadedWakeups);
                out.writeInt(this.mUnpluggedWakeups);
                out.writeInt(this.mServiceStats.size());
                for (Map.Entry<String, Serv> servEntry : this.mServiceStats.entrySet()) {
                    out.writeString(servEntry.getKey());
                    Serv serv = servEntry.getValue();
                    serv.writeToParcelLocked(out);
                }
            }

            @Override // android.os.BatteryStats.Uid.Pkg
            public Map<String, ? extends BatteryStats.Uid.Pkg.Serv> getServiceStats() {
                return this.mServiceStats;
            }

            @Override // android.os.BatteryStats.Uid.Pkg
            public int getWakeups(int which) {
                int val;
                if (which == 1) {
                    val = this.mLastWakeups;
                } else {
                    val = this.mWakeups;
                    if (which == 2) {
                        val -= this.mLoadedWakeups;
                    } else if (which == 3) {
                        val -= this.mUnpluggedWakeups;
                    }
                }
                return val;
            }

            /* loaded from: BatteryStatsImpl$Uid$Pkg$Serv.class */
            public final class Serv extends BatteryStats.Uid.Pkg.Serv implements Unpluggable {
                long mStartTime;
                long mRunningSince;
                boolean mRunning;
                int mStarts;
                long mLaunchedTime;
                long mLaunchedSince;
                boolean mLaunched;
                int mLaunches;
                long mLoadedStartTime;
                int mLoadedStarts;
                int mLoadedLaunches;
                long mLastStartTime;
                int mLastStarts;
                int mLastLaunches;
                long mUnpluggedStartTime;
                int mUnpluggedStarts;
                int mUnpluggedLaunches;

                Serv() {
                    super();
                    BatteryStatsImpl.this.mUnpluggables.add(this);
                }

                @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
                public void unplug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
                    this.mUnpluggedStartTime = getStartTimeToNowLocked(batteryUptime);
                    this.mUnpluggedStarts = this.mStarts;
                    this.mUnpluggedLaunches = this.mLaunches;
                }

                @Override // com.android.internal.os.BatteryStatsImpl.Unpluggable
                public void plug(long elapsedRealtime, long batteryUptime, long batteryRealtime) {
                }

                void detach() {
                    BatteryStatsImpl.this.mUnpluggables.remove(this);
                }

                void readFromParcelLocked(Parcel in) {
                    this.mStartTime = in.readLong();
                    this.mRunningSince = in.readLong();
                    this.mRunning = in.readInt() != 0;
                    this.mStarts = in.readInt();
                    this.mLaunchedTime = in.readLong();
                    this.mLaunchedSince = in.readLong();
                    this.mLaunched = in.readInt() != 0;
                    this.mLaunches = in.readInt();
                    this.mLoadedStartTime = in.readLong();
                    this.mLoadedStarts = in.readInt();
                    this.mLoadedLaunches = in.readInt();
                    this.mLastStartTime = 0L;
                    this.mLastStarts = 0;
                    this.mLastLaunches = 0;
                    this.mUnpluggedStartTime = in.readLong();
                    this.mUnpluggedStarts = in.readInt();
                    this.mUnpluggedLaunches = in.readInt();
                }

                void writeToParcelLocked(Parcel out) {
                    out.writeLong(this.mStartTime);
                    out.writeLong(this.mRunningSince);
                    out.writeInt(this.mRunning ? 1 : 0);
                    out.writeInt(this.mStarts);
                    out.writeLong(this.mLaunchedTime);
                    out.writeLong(this.mLaunchedSince);
                    out.writeInt(this.mLaunched ? 1 : 0);
                    out.writeInt(this.mLaunches);
                    out.writeLong(this.mLoadedStartTime);
                    out.writeInt(this.mLoadedStarts);
                    out.writeInt(this.mLoadedLaunches);
                    out.writeLong(this.mUnpluggedStartTime);
                    out.writeInt(this.mUnpluggedStarts);
                    out.writeInt(this.mUnpluggedLaunches);
                }

                long getLaunchTimeToNowLocked(long batteryUptime) {
                    return !this.mLaunched ? this.mLaunchedTime : (this.mLaunchedTime + batteryUptime) - this.mLaunchedSince;
                }

                long getStartTimeToNowLocked(long batteryUptime) {
                    return !this.mRunning ? this.mStartTime : (this.mStartTime + batteryUptime) - this.mRunningSince;
                }

                public void startLaunchedLocked() {
                    if (!this.mLaunched) {
                        this.mLaunches++;
                        this.mLaunchedSince = BatteryStatsImpl.this.getBatteryUptimeLocked();
                        this.mLaunched = true;
                    }
                }

                public void stopLaunchedLocked() {
                    if (this.mLaunched) {
                        long time = BatteryStatsImpl.this.getBatteryUptimeLocked() - this.mLaunchedSince;
                        if (time > 0) {
                            this.mLaunchedTime += time;
                        } else {
                            this.mLaunches--;
                        }
                        this.mLaunched = false;
                    }
                }

                public void startRunningLocked() {
                    if (!this.mRunning) {
                        this.mStarts++;
                        this.mRunningSince = BatteryStatsImpl.this.getBatteryUptimeLocked();
                        this.mRunning = true;
                    }
                }

                public void stopRunningLocked() {
                    if (this.mRunning) {
                        long time = BatteryStatsImpl.this.getBatteryUptimeLocked() - this.mRunningSince;
                        if (time > 0) {
                            this.mStartTime += time;
                        } else {
                            this.mStarts--;
                        }
                        this.mRunning = false;
                    }
                }

                public BatteryStatsImpl getBatteryStats() {
                    return BatteryStatsImpl.this;
                }

                @Override // android.os.BatteryStats.Uid.Pkg.Serv
                public int getLaunches(int which) {
                    int val;
                    if (which == 1) {
                        val = this.mLastLaunches;
                    } else {
                        val = this.mLaunches;
                        if (which == 2) {
                            val -= this.mLoadedLaunches;
                        } else if (which == 3) {
                            val -= this.mUnpluggedLaunches;
                        }
                    }
                    return val;
                }

                @Override // android.os.BatteryStats.Uid.Pkg.Serv
                public long getStartTime(long now, int which) {
                    long val;
                    if (which == 1) {
                        val = this.mLastStartTime;
                    } else {
                        val = getStartTimeToNowLocked(now);
                        if (which == 2) {
                            val -= this.mLoadedStartTime;
                        } else if (which == 3) {
                            val -= this.mUnpluggedStartTime;
                        }
                    }
                    return val;
                }

                @Override // android.os.BatteryStats.Uid.Pkg.Serv
                public int getStarts(int which) {
                    int val;
                    if (which == 1) {
                        val = this.mLastStarts;
                    } else {
                        val = this.mStarts;
                        if (which == 2) {
                            val -= this.mLoadedStarts;
                        } else if (which == 3) {
                            val -= this.mUnpluggedStarts;
                        }
                    }
                    return val;
                }
            }

            public BatteryStatsImpl getBatteryStats() {
                return BatteryStatsImpl.this;
            }

            public void incWakeupsLocked() {
                this.mWakeups++;
            }

            final Serv newServiceStatsLocked() {
                return new Serv();
            }
        }

        public Proc getProcessStatsLocked(String name) {
            Proc ps = this.mProcessStats.get(name);
            if (ps == null) {
                ps = new Proc();
                this.mProcessStats.put(name, ps);
            }
            return ps;
        }

        @Override // android.os.BatteryStats.Uid
        public SparseArray<? extends BatteryStats.Uid.Pid> getPidStats() {
            return this.mPids;
        }

        public BatteryStats.Uid.Pid getPidStatsLocked(int pid) {
            BatteryStats.Uid.Pid p = this.mPids.get(pid);
            if (p == null) {
                p = new BatteryStats.Uid.Pid();
                this.mPids.put(pid, p);
            }
            return p;
        }

        public Pkg getPackageStatsLocked(String name) {
            Pkg ps = this.mPackageStats.get(name);
            if (ps == null) {
                ps = new Pkg();
                this.mPackageStats.put(name, ps);
            }
            return ps;
        }

        public Pkg.Serv getServiceStatsLocked(String pkg, String serv) {
            Pkg ps = getPackageStatsLocked(pkg);
            Pkg.Serv ss = ps.mServiceStats.get(serv);
            if (ss == null) {
                ss = ps.newServiceStatsLocked();
                ps.mServiceStats.put(serv, ss);
            }
            return ss;
        }

        public StopwatchTimer getWakeTimerLocked(String name, int type) {
            Wakelock wl = this.mWakelockStats.get(name);
            if (wl == null) {
                int N = this.mWakelockStats.size();
                if (N > 50) {
                    name = BatteryStatsImpl.BATCHED_WAKELOCK_NAME;
                    wl = this.mWakelockStats.get(name);
                }
                if (wl == null) {
                    wl = new Wakelock();
                    this.mWakelockStats.put(name, wl);
                }
            }
            switch (type) {
                case 0:
                    StopwatchTimer t = wl.mTimerPartial;
                    if (t == null) {
                        t = new StopwatchTimer(this, 0, BatteryStatsImpl.this.mPartialTimers, BatteryStatsImpl.this.mUnpluggables);
                        wl.mTimerPartial = t;
                    }
                    return t;
                case 1:
                    StopwatchTimer t2 = wl.mTimerFull;
                    if (t2 == null) {
                        t2 = new StopwatchTimer(this, 1, BatteryStatsImpl.this.mFullTimers, BatteryStatsImpl.this.mUnpluggables);
                        wl.mTimerFull = t2;
                    }
                    return t2;
                case 2:
                    StopwatchTimer t3 = wl.mTimerWindow;
                    if (t3 == null) {
                        t3 = new StopwatchTimer(this, 2, BatteryStatsImpl.this.mWindowTimers, BatteryStatsImpl.this.mUnpluggables);
                        wl.mTimerWindow = t3;
                    }
                    return t3;
                default:
                    throw new IllegalArgumentException("type=" + type);
            }
        }

        public StopwatchTimer getSensorTimerLocked(int sensor, boolean create) {
            Sensor se = this.mSensorStats.get(Integer.valueOf(sensor));
            if (se == null) {
                if (!create) {
                    return null;
                }
                se = new Sensor(sensor);
                this.mSensorStats.put(Integer.valueOf(sensor), se);
            }
            StopwatchTimer t = se.mTimer;
            if (t != null) {
                return t;
            }
            ArrayList<StopwatchTimer> timers = BatteryStatsImpl.this.mSensorTimers.get(sensor);
            if (timers == null) {
                timers = new ArrayList<>();
                BatteryStatsImpl.this.mSensorTimers.put(sensor, timers);
            }
            StopwatchTimer t2 = new StopwatchTimer(this, 3, timers, BatteryStatsImpl.this.mUnpluggables);
            se.mTimer = t2;
            return t2;
        }

        public void noteStartWakeLocked(int pid, String name, int type) {
            StopwatchTimer t = getWakeTimerLocked(name, type);
            if (t != null) {
                t.startRunningLocked(BatteryStatsImpl.this);
            }
            if (pid >= 0 && type == 0) {
                BatteryStats.Uid.Pid p = getPidStatsLocked(pid);
                if (p.mWakeStart == 0) {
                    p.mWakeStart = SystemClock.elapsedRealtime();
                }
            }
        }

        public void noteStopWakeLocked(int pid, String name, int type) {
            BatteryStats.Uid.Pid p;
            StopwatchTimer t = getWakeTimerLocked(name, type);
            if (t != null) {
                t.stopRunningLocked(BatteryStatsImpl.this);
            }
            if (pid >= 0 && type == 0 && (p = this.mPids.get(pid)) != null && p.mWakeStart != 0) {
                p.mWakeSum += SystemClock.elapsedRealtime() - p.mWakeStart;
                p.mWakeStart = 0L;
            }
        }

        public void reportExcessiveWakeLocked(String proc, long overTime, long usedTime) {
            Proc p = getProcessStatsLocked(proc);
            if (p != null) {
                p.addExcessiveWake(overTime, usedTime);
            }
        }

        public void reportExcessiveCpuLocked(String proc, long overTime, long usedTime) {
            Proc p = getProcessStatsLocked(proc);
            if (p != null) {
                p.addExcessiveCpu(overTime, usedTime);
            }
        }

        public void noteStartSensor(int sensor) {
            StopwatchTimer t = getSensorTimerLocked(sensor, true);
            if (t != null) {
                t.startRunningLocked(BatteryStatsImpl.this);
            }
        }

        public void noteStopSensor(int sensor) {
            StopwatchTimer t = getSensorTimerLocked(sensor, false);
            if (t != null) {
                t.stopRunningLocked(BatteryStatsImpl.this);
            }
        }

        public void noteStartGps() {
            StopwatchTimer t = getSensorTimerLocked(-10000, true);
            if (t != null) {
                t.startRunningLocked(BatteryStatsImpl.this);
            }
        }

        public void noteStopGps() {
            StopwatchTimer t = getSensorTimerLocked(-10000, false);
            if (t != null) {
                t.stopRunningLocked(BatteryStatsImpl.this);
            }
        }

        public BatteryStatsImpl getBatteryStats() {
            return BatteryStatsImpl.this;
        }
    }

    public BatteryStatsImpl(String filename) {
        this.mUidStats = new SparseArray<>();
        this.mPartialTimers = new ArrayList<>();
        this.mFullTimers = new ArrayList<>();
        this.mWindowTimers = new ArrayList<>();
        this.mSensorTimers = new SparseArray<>();
        this.mWifiRunningTimers = new ArrayList<>();
        this.mFullWifiLockTimers = new ArrayList<>();
        this.mWifiMulticastTimers = new ArrayList<>();
        this.mWifiScanTimers = new ArrayList<>();
        this.mLastPartialTimers = new ArrayList<>();
        this.mUnpluggables = new ArrayList<>();
        this.mHaveBatteryLevel = false;
        this.mRecordingHistory = true;
        this.mHistoryBuffer = Parcel.obtain();
        this.mHistoryLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryLastLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryReadTmp = new BatteryStats.HistoryItem();
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = false;
        this.mLastHistoryTime = 0L;
        this.mHistoryCur = new BatteryStats.HistoryItem();
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[5];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[16];
        this.mNetworkActivityCounters = new LongSamplingCounter[4];
        this.mWifiOnUid = -1;
        this.mLastWriteTime = 0L;
        this.mBluetoothPingStart = -1;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mKernelWakelockStats = new HashMap<>();
        this.mProcWakelocksName = new String[3];
        this.mProcWakelocksData = new long[3];
        this.mProcWakelockFileStats = new HashMap();
        this.mUidCache = new HashMap<>();
        this.mNetworkStatsFactory = new NetworkStatsFactory();
        this.mMobileIfaces = Sets.newHashSet();
        this.mWifiIfaces = Sets.newHashSet();
        this.mChangedBufferStates = 0;
        this.mChangedStates = 0;
        this.mWifiFullLockNesting = 0;
        this.mWifiScanNesting = 0;
        this.mWifiMulticastNesting = 0;
        this.mPendingWrite = null;
        this.mWriteLock = new ReentrantLock();
        this.mFile = new JournaledFile(new File(filename), new File(filename + ".tmp"));
        this.mHandler = new MyHandler();
        this.mStartCount++;
        this.mScreenOnTimer = new StopwatchTimer(null, -1, null, this.mUnpluggables);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i] = new StopwatchTimer(null, (-100) - i, null, this.mUnpluggables);
        }
        this.mInputEventCounter = new Counter(this.mUnpluggables);
        this.mPhoneOnTimer = new StopwatchTimer(null, -2, null, this.mUnpluggables);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mPhoneSignalStrengthsTimer[i2] = new StopwatchTimer(null, AudioService.STREAM_REMOTE_MUSIC - i2, null, this.mUnpluggables);
        }
        this.mPhoneSignalScanningTimer = new StopwatchTimer(null, -199, null, this.mUnpluggables);
        for (int i3 = 0; i3 < 16; i3++) {
            this.mPhoneDataConnectionsTimer[i3] = new StopwatchTimer(null, (-300) - i3, null, this.mUnpluggables);
        }
        for (int i4 = 0; i4 < 4; i4++) {
            this.mNetworkActivityCounters[i4] = new LongSamplingCounter(this.mUnpluggables);
        }
        this.mWifiOnTimer = new StopwatchTimer(null, -3, null, this.mUnpluggables);
        this.mGlobalWifiRunningTimer = new StopwatchTimer(null, -4, null, this.mUnpluggables);
        this.mBluetoothOnTimer = new StopwatchTimer(null, -5, null, this.mUnpluggables);
        this.mAudioOnTimer = new StopwatchTimer(null, -6, null, this.mUnpluggables);
        this.mVideoOnTimer = new StopwatchTimer(null, -7, null, this.mUnpluggables);
        this.mOnBatteryInternal = false;
        this.mOnBattery = false;
        initTimes();
        this.mTrackBatteryPastUptime = 0L;
        this.mTrackBatteryPastRealtime = 0L;
        long uptimeMillis = SystemClock.uptimeMillis() * 1000;
        this.mTrackBatteryUptimeStart = uptimeMillis;
        this.mUptimeStart = uptimeMillis;
        long elapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        this.mTrackBatteryRealtimeStart = elapsedRealtime;
        this.mRealtimeStart = elapsedRealtime;
        this.mUnpluggedBatteryUptime = getBatteryUptimeLocked(this.mUptimeStart);
        this.mUnpluggedBatteryRealtime = getBatteryRealtimeLocked(this.mRealtimeStart);
        this.mDischargeStartLevel = 0;
        this.mDischargeUnplugLevel = 0;
        this.mDischargeCurrentLevel = 0;
        initDischarge();
        clearHistoryLocked();
    }

    public BatteryStatsImpl(Parcel p) {
        this.mUidStats = new SparseArray<>();
        this.mPartialTimers = new ArrayList<>();
        this.mFullTimers = new ArrayList<>();
        this.mWindowTimers = new ArrayList<>();
        this.mSensorTimers = new SparseArray<>();
        this.mWifiRunningTimers = new ArrayList<>();
        this.mFullWifiLockTimers = new ArrayList<>();
        this.mWifiMulticastTimers = new ArrayList<>();
        this.mWifiScanTimers = new ArrayList<>();
        this.mLastPartialTimers = new ArrayList<>();
        this.mUnpluggables = new ArrayList<>();
        this.mHaveBatteryLevel = false;
        this.mRecordingHistory = true;
        this.mHistoryBuffer = Parcel.obtain();
        this.mHistoryLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryLastLastWritten = new BatteryStats.HistoryItem();
        this.mHistoryReadTmp = new BatteryStats.HistoryItem();
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = false;
        this.mLastHistoryTime = 0L;
        this.mHistoryCur = new BatteryStats.HistoryItem();
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[5];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[16];
        this.mNetworkActivityCounters = new LongSamplingCounter[4];
        this.mWifiOnUid = -1;
        this.mLastWriteTime = 0L;
        this.mBluetoothPingStart = -1;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mKernelWakelockStats = new HashMap<>();
        this.mProcWakelocksName = new String[3];
        this.mProcWakelocksData = new long[3];
        this.mProcWakelockFileStats = new HashMap();
        this.mUidCache = new HashMap<>();
        this.mNetworkStatsFactory = new NetworkStatsFactory();
        this.mMobileIfaces = Sets.newHashSet();
        this.mWifiIfaces = Sets.newHashSet();
        this.mChangedBufferStates = 0;
        this.mChangedStates = 0;
        this.mWifiFullLockNesting = 0;
        this.mWifiScanNesting = 0;
        this.mWifiMulticastNesting = 0;
        this.mPendingWrite = null;
        this.mWriteLock = new ReentrantLock();
        this.mFile = null;
        this.mHandler = null;
        clearHistoryLocked();
        readFromParcel(p);
    }

    public void setCallback(BatteryCallback cb) {
        this.mCallback = cb;
    }

    public void setNumSpeedSteps(int steps) {
        if (sNumSpeedSteps == 0) {
            sNumSpeedSteps = steps;
        }
    }

    public void setRadioScanningTimeout(long timeout) {
        if (this.mPhoneSignalScanningTimer != null) {
            this.mPhoneSignalScanningTimer.setTimeout(timeout);
        }
    }

    @Override // android.os.BatteryStats
    public boolean startIteratingOldHistoryLocked() {
        this.mHistoryBuffer.setDataPosition(0);
        this.mHistoryReadTmp.clear();
        this.mReadOverflow = false;
        this.mIteratingHistory = true;
        BatteryStats.HistoryItem historyItem = this.mHistory;
        this.mHistoryIterator = historyItem;
        return historyItem != null;
    }

    @Override // android.os.BatteryStats
    public boolean getNextOldHistoryLocked(BatteryStats.HistoryItem out) {
        boolean end = this.mHistoryBuffer.dataPosition() >= this.mHistoryBuffer.dataSize();
        if (!end) {
            this.mHistoryReadTmp.readDelta(this.mHistoryBuffer);
            this.mReadOverflow |= this.mHistoryReadTmp.cmd == 3;
        }
        BatteryStats.HistoryItem cur = this.mHistoryIterator;
        if (cur == null) {
            if (!this.mReadOverflow && !end) {
                Slog.w(TAG, "Old history ends before new history!");
                return false;
            }
            return false;
        }
        out.setTo(cur);
        this.mHistoryIterator = cur.next;
        if (!this.mReadOverflow) {
            if (end) {
                Slog.w(TAG, "New history ends before old history!");
                return true;
            } else if (!out.same(this.mHistoryReadTmp)) {
                long now = getHistoryBaseTime() + SystemClock.elapsedRealtime();
                PrintWriter pw = new FastPrintWriter(new LogWriter(5, TAG));
                pw.println("Histories differ!");
                pw.println("Old history:");
                new BatteryStats.HistoryPrinter().printNextItem(pw, out, now);
                pw.println("New history:");
                new BatteryStats.HistoryPrinter().printNextItem(pw, this.mHistoryReadTmp, now);
                pw.flush();
                return true;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override // android.os.BatteryStats
    public void finishIteratingOldHistoryLocked() {
        this.mIteratingHistory = false;
        this.mHistoryBuffer.setDataPosition(this.mHistoryBuffer.dataSize());
    }

    @Override // android.os.BatteryStats
    public boolean startIteratingHistoryLocked() {
        this.mHistoryBuffer.setDataPosition(0);
        this.mReadOverflow = false;
        this.mIteratingHistory = true;
        return this.mHistoryBuffer.dataSize() > 0;
    }

    @Override // android.os.BatteryStats
    public boolean getNextHistoryLocked(BatteryStats.HistoryItem out) {
        int pos = this.mHistoryBuffer.dataPosition();
        if (pos == 0) {
            out.clear();
        }
        boolean end = pos >= this.mHistoryBuffer.dataSize();
        if (end) {
            return false;
        }
        out.readDelta(this.mHistoryBuffer);
        return true;
    }

    @Override // android.os.BatteryStats
    public void finishIteratingHistoryLocked() {
        this.mIteratingHistory = false;
        this.mHistoryBuffer.setDataPosition(this.mHistoryBuffer.dataSize());
    }

    @Override // android.os.BatteryStats
    public long getHistoryBaseTime() {
        return this.mHistoryBaseTime;
    }

    @Override // android.os.BatteryStats
    public int getStartCount() {
        return this.mStartCount;
    }

    public boolean isOnBattery() {
        return this.mOnBattery;
    }

    public boolean isScreenOn() {
        return this.mScreenOn;
    }

    void initTimes() {
        this.mTrackBatteryPastUptime = 0L;
        this.mBatteryRealtime = 0L;
        this.mTrackBatteryPastRealtime = 0L;
        this.mBatteryUptime = 0L;
        long uptimeMillis = SystemClock.uptimeMillis() * 1000;
        this.mTrackBatteryUptimeStart = uptimeMillis;
        this.mUptimeStart = uptimeMillis;
        long elapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        this.mTrackBatteryRealtimeStart = elapsedRealtime;
        this.mRealtimeStart = elapsedRealtime;
        this.mUnpluggedBatteryUptime = getBatteryUptimeLocked(this.mUptimeStart);
        this.mUnpluggedBatteryRealtime = getBatteryRealtimeLocked(this.mRealtimeStart);
    }

    void initDischarge() {
        this.mLowDischargeAmountSinceCharge = 0;
        this.mHighDischargeAmountSinceCharge = 0;
        this.mDischargeAmountScreenOn = 0;
        this.mDischargeAmountScreenOnSinceCharge = 0;
        this.mDischargeAmountScreenOff = 0;
        this.mDischargeAmountScreenOffSinceCharge = 0;
    }

    public void resetAllStatsLocked() {
        this.mStartCount = 0;
        initTimes();
        this.mScreenOnTimer.reset(this, false);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i].reset(this, false);
        }
        this.mInputEventCounter.reset(false);
        this.mPhoneOnTimer.reset(this, false);
        this.mAudioOnTimer.reset(this, false);
        this.mVideoOnTimer.reset(this, false);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mPhoneSignalStrengthsTimer[i2].reset(this, false);
        }
        this.mPhoneSignalScanningTimer.reset(this, false);
        for (int i3 = 0; i3 < 16; i3++) {
            this.mPhoneDataConnectionsTimer[i3].reset(this, false);
        }
        for (int i4 = 0; i4 < 4; i4++) {
            this.mNetworkActivityCounters[i4].reset(false);
        }
        this.mWifiOnTimer.reset(this, false);
        this.mGlobalWifiRunningTimer.reset(this, false);
        this.mBluetoothOnTimer.reset(this, false);
        int i5 = 0;
        while (i5 < this.mUidStats.size()) {
            if (this.mUidStats.valueAt(i5).reset()) {
                this.mUidStats.remove(this.mUidStats.keyAt(i5));
                i5--;
            }
            i5++;
        }
        if (this.mKernelWakelockStats.size() > 0) {
            for (SamplingTimer timer : this.mKernelWakelockStats.values()) {
                this.mUnpluggables.remove(timer);
            }
            this.mKernelWakelockStats.clear();
        }
        initDischarge();
        clearHistoryLocked();
    }

    void updateDischargeScreenLevelsLocked(boolean oldScreenOn, boolean newScreenOn) {
        if (oldScreenOn) {
            int diff = this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            if (diff > 0) {
                this.mDischargeAmountScreenOn += diff;
                this.mDischargeAmountScreenOnSinceCharge += diff;
            }
        } else {
            int diff2 = this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel;
            if (diff2 > 0) {
                this.mDischargeAmountScreenOff += diff2;
                this.mDischargeAmountScreenOffSinceCharge += diff2;
            }
        }
        if (newScreenOn) {
            this.mDischargeScreenOnUnplugLevel = this.mDischargeCurrentLevel;
            this.mDischargeScreenOffUnplugLevel = 0;
            return;
        }
        this.mDischargeScreenOnUnplugLevel = 0;
        this.mDischargeScreenOffUnplugLevel = this.mDischargeCurrentLevel;
    }

    void setOnBattery(boolean onBattery, int oldStatus, int level) {
        synchronized (this) {
            setOnBatteryLocked(onBattery, oldStatus, level);
        }
    }

    void setOnBatteryLocked(boolean onBattery, int oldStatus, int level) {
        boolean doWrite = false;
        Message m = this.mHandler.obtainMessage(2);
        m.arg1 = onBattery ? 1 : 0;
        this.mHandler.sendMessage(m);
        this.mOnBatteryInternal = onBattery;
        this.mOnBattery = onBattery;
        long uptime = SystemClock.uptimeMillis() * 1000;
        long mSecRealtime = SystemClock.elapsedRealtime();
        long realtime = mSecRealtime * 1000;
        if (onBattery) {
            if (oldStatus == 5 || level >= 90 || (this.mDischargeCurrentLevel < 20 && level >= 80)) {
                doWrite = true;
                resetAllStatsLocked();
                this.mDischargeStartLevel = level;
            }
            updateKernelWakelocksLocked();
            updateNetworkActivityLocked();
            this.mHistoryCur.batteryLevel = (byte) level;
            this.mHistoryCur.states &= -524289;
            addHistoryRecordLocked(mSecRealtime);
            this.mTrackBatteryUptimeStart = uptime;
            this.mTrackBatteryRealtimeStart = realtime;
            this.mUnpluggedBatteryUptime = getBatteryUptimeLocked(uptime);
            this.mUnpluggedBatteryRealtime = getBatteryRealtimeLocked(realtime);
            this.mDischargeUnplugLevel = level;
            this.mDischargeCurrentLevel = level;
            if (this.mScreenOn) {
                this.mDischargeScreenOnUnplugLevel = level;
                this.mDischargeScreenOffUnplugLevel = 0;
            } else {
                this.mDischargeScreenOnUnplugLevel = 0;
                this.mDischargeScreenOffUnplugLevel = level;
            }
            this.mDischargeAmountScreenOn = 0;
            this.mDischargeAmountScreenOff = 0;
            doUnplugLocked(realtime, this.mUnpluggedBatteryUptime, this.mUnpluggedBatteryRealtime);
        } else {
            updateKernelWakelocksLocked();
            updateNetworkActivityLocked();
            this.mHistoryCur.batteryLevel = (byte) level;
            this.mHistoryCur.states |= 524288;
            addHistoryRecordLocked(mSecRealtime);
            this.mTrackBatteryPastUptime += uptime - this.mTrackBatteryUptimeStart;
            this.mTrackBatteryPastRealtime += realtime - this.mTrackBatteryRealtimeStart;
            this.mDischargeCurrentLevel = level;
            if (level < this.mDischargeUnplugLevel) {
                this.mLowDischargeAmountSinceCharge += (this.mDischargeUnplugLevel - level) - 1;
                this.mHighDischargeAmountSinceCharge += this.mDischargeUnplugLevel - level;
            }
            updateDischargeScreenLevelsLocked(this.mScreenOn, this.mScreenOn);
            doPlugLocked(realtime, getBatteryUptimeLocked(uptime), getBatteryRealtimeLocked(realtime));
        }
        if ((doWrite || this.mLastWriteTime + DateUtils.MINUTE_IN_MILLIS < mSecRealtime) && this.mFile != null) {
            writeAsyncLocked();
        }
    }

    public void setBatteryState(int status, int health, int plugType, int level, int temp, int volt) {
        synchronized (this) {
            boolean onBattery = plugType == 0;
            int oldStatus = this.mHistoryCur.batteryStatus;
            if (!this.mHaveBatteryLevel) {
                this.mHaveBatteryLevel = true;
                if (onBattery == this.mOnBattery) {
                    if (onBattery) {
                        this.mHistoryCur.states &= -524289;
                    } else {
                        this.mHistoryCur.states |= 524288;
                    }
                }
                oldStatus = status;
            }
            if (onBattery) {
                this.mDischargeCurrentLevel = level;
                this.mRecordingHistory = true;
            }
            if (onBattery != this.mOnBattery) {
                this.mHistoryCur.batteryLevel = (byte) level;
                this.mHistoryCur.batteryStatus = (byte) status;
                this.mHistoryCur.batteryHealth = (byte) health;
                this.mHistoryCur.batteryPlugType = (byte) plugType;
                this.mHistoryCur.batteryTemperature = (char) temp;
                this.mHistoryCur.batteryVoltage = (char) volt;
                setOnBatteryLocked(onBattery, oldStatus, level);
            } else {
                boolean changed = false;
                if (this.mHistoryCur.batteryLevel != level) {
                    this.mHistoryCur.batteryLevel = (byte) level;
                    changed = true;
                }
                if (this.mHistoryCur.batteryStatus != status) {
                    this.mHistoryCur.batteryStatus = (byte) status;
                    changed = true;
                }
                if (this.mHistoryCur.batteryHealth != health) {
                    this.mHistoryCur.batteryHealth = (byte) health;
                    changed = true;
                }
                if (this.mHistoryCur.batteryPlugType != plugType) {
                    this.mHistoryCur.batteryPlugType = (byte) plugType;
                    changed = true;
                }
                if (temp >= this.mHistoryCur.batteryTemperature + '\n' || temp <= this.mHistoryCur.batteryTemperature - '\n') {
                    this.mHistoryCur.batteryTemperature = (char) temp;
                    changed = true;
                }
                if (volt > this.mHistoryCur.batteryVoltage + 20 || volt < this.mHistoryCur.batteryVoltage - 20) {
                    this.mHistoryCur.batteryVoltage = (char) volt;
                    changed = true;
                }
                if (changed) {
                    addHistoryRecordLocked(SystemClock.elapsedRealtime());
                }
            }
            if (!onBattery && status == 5) {
                this.mRecordingHistory = false;
            }
        }
    }

    public void updateKernelWakelocksLocked() {
        Map<String, KernelWakelockStats> m = readKernelWakelockStats();
        if (m == null) {
            Slog.w(TAG, "Couldn't get kernel wake lock stats");
            return;
        }
        for (Map.Entry<String, KernelWakelockStats> ent : m.entrySet()) {
            String name = ent.getKey();
            KernelWakelockStats kws = ent.getValue();
            SamplingTimer kwlt = this.mKernelWakelockStats.get(name);
            if (kwlt == null) {
                kwlt = new SamplingTimer(this.mUnpluggables, this.mOnBatteryInternal, true);
                this.mKernelWakelockStats.put(name, kwlt);
            }
            kwlt.updateCurrentReportedCount(kws.mCount);
            kwlt.updateCurrentReportedTotalTime(kws.mTotalTime);
            kwlt.setUpdateVersion(sKernelWakelockUpdateVersion);
        }
        if (m.size() != this.mKernelWakelockStats.size()) {
            for (Map.Entry<String, SamplingTimer> ent2 : this.mKernelWakelockStats.entrySet()) {
                SamplingTimer st = ent2.getValue();
                if (st.getUpdateVersion() != sKernelWakelockUpdateVersion) {
                    st.setStale();
                }
            }
        }
    }

    private void updateNetworkActivityLocked() {
        if (SystemProperties.getBoolean(NetworkManagementSocketTagger.PROP_QTAGUID_ENABLED, false)) {
            try {
                NetworkStats snapshot = this.mNetworkStatsFactory.readNetworkStatsDetail();
                if (this.mLastSnapshot == null) {
                    this.mLastSnapshot = snapshot;
                    return;
                }
                NetworkStats delta = snapshot.subtract(this.mLastSnapshot);
                this.mLastSnapshot = snapshot;
                NetworkStats.Entry entry = null;
                int size = delta.size();
                for (int i = 0; i < size; i++) {
                    entry = delta.getValues(i, entry);
                    if (entry.rxBytes != 0 && entry.txBytes != 0 && entry.tag == 0) {
                        Uid u = getUidStatsLocked(entry.uid);
                        if (this.mMobileIfaces.contains(entry.iface)) {
                            u.noteNetworkActivityLocked(0, entry.rxBytes);
                            u.noteNetworkActivityLocked(1, entry.txBytes);
                            this.mNetworkActivityCounters[0].addCountLocked(entry.rxBytes);
                            this.mNetworkActivityCounters[1].addCountLocked(entry.txBytes);
                        } else if (this.mWifiIfaces.contains(entry.iface)) {
                            u.noteNetworkActivityLocked(2, entry.rxBytes);
                            u.noteNetworkActivityLocked(3, entry.txBytes);
                            this.mNetworkActivityCounters[2].addCountLocked(entry.rxBytes);
                            this.mNetworkActivityCounters[3].addCountLocked(entry.txBytes);
                        }
                    }
                }
            } catch (IOException e) {
                Log.wtf(TAG, "Failed to read network stats", e);
            }
        }
    }

    public long getAwakeTimeBattery() {
        return computeBatteryUptime(getBatteryUptimeLocked(), 2);
    }

    public long getAwakeTimePlugged() {
        return (SystemClock.uptimeMillis() * 1000) - getAwakeTimeBattery();
    }

    @Override // android.os.BatteryStats
    public long computeUptime(long curTime, int which) {
        switch (which) {
            case 0:
                return this.mUptime + (curTime - this.mUptimeStart);
            case 1:
                return this.mLastUptime;
            case 2:
                return curTime - this.mUptimeStart;
            case 3:
                return curTime - this.mTrackBatteryUptimeStart;
            default:
                return 0L;
        }
    }

    @Override // android.os.BatteryStats
    public long computeRealtime(long curTime, int which) {
        switch (which) {
            case 0:
                return this.mRealtime + (curTime - this.mRealtimeStart);
            case 1:
                return this.mLastRealtime;
            case 2:
                return curTime - this.mRealtimeStart;
            case 3:
                return curTime - this.mTrackBatteryRealtimeStart;
            default:
                return 0L;
        }
    }

    @Override // android.os.BatteryStats
    public long computeBatteryUptime(long curTime, int which) {
        switch (which) {
            case 0:
                return this.mBatteryUptime + getBatteryUptime(curTime);
            case 1:
                return this.mBatteryLastUptime;
            case 2:
                return getBatteryUptime(curTime);
            case 3:
                return getBatteryUptimeLocked(curTime) - this.mUnpluggedBatteryUptime;
            default:
                return 0L;
        }
    }

    @Override // android.os.BatteryStats
    public long computeBatteryRealtime(long curTime, int which) {
        switch (which) {
            case 0:
                return this.mBatteryRealtime + getBatteryRealtimeLocked(curTime);
            case 1:
                return this.mBatteryLastRealtime;
            case 2:
                return getBatteryRealtimeLocked(curTime);
            case 3:
                return getBatteryRealtimeLocked(curTime) - this.mUnpluggedBatteryRealtime;
            default:
                return 0L;
        }
    }

    long getBatteryUptimeLocked(long curTime) {
        long time = this.mTrackBatteryPastUptime;
        if (this.mOnBatteryInternal) {
            time += curTime - this.mTrackBatteryUptimeStart;
        }
        return time;
    }

    long getBatteryUptimeLocked() {
        return getBatteryUptime(SystemClock.uptimeMillis() * 1000);
    }

    @Override // android.os.BatteryStats
    public long getBatteryUptime(long curTime) {
        return getBatteryUptimeLocked(curTime);
    }

    long getBatteryRealtimeLocked(long curTime) {
        long time = this.mTrackBatteryPastRealtime;
        if (this.mOnBatteryInternal) {
            time += curTime - this.mTrackBatteryRealtimeStart;
        }
        return time;
    }

    @Override // android.os.BatteryStats
    public long getBatteryRealtime(long curTime) {
        return getBatteryRealtimeLocked(curTime);
    }

    @Override // android.os.BatteryStats
    public int getDischargeStartLevel() {
        int dischargeStartLevelLocked;
        synchronized (this) {
            dischargeStartLevelLocked = getDischargeStartLevelLocked();
        }
        return dischargeStartLevelLocked;
    }

    public int getDischargeStartLevelLocked() {
        return this.mDischargeUnplugLevel;
    }

    @Override // android.os.BatteryStats
    public int getDischargeCurrentLevel() {
        int dischargeCurrentLevelLocked;
        synchronized (this) {
            dischargeCurrentLevelLocked = getDischargeCurrentLevelLocked();
        }
        return dischargeCurrentLevelLocked;
    }

    public int getDischargeCurrentLevelLocked() {
        return this.mDischargeCurrentLevel;
    }

    @Override // android.os.BatteryStats
    public int getLowDischargeAmountSinceCharge() {
        int i;
        synchronized (this) {
            int val = this.mLowDischargeAmountSinceCharge;
            if (this.mOnBattery && this.mDischargeCurrentLevel < this.mDischargeUnplugLevel) {
                val += (this.mDischargeUnplugLevel - this.mDischargeCurrentLevel) - 1;
            }
            i = val;
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getHighDischargeAmountSinceCharge() {
        int i;
        synchronized (this) {
            int val = this.mHighDischargeAmountSinceCharge;
            if (this.mOnBattery && this.mDischargeCurrentLevel < this.mDischargeUnplugLevel) {
                val += this.mDischargeUnplugLevel - this.mDischargeCurrentLevel;
            }
            i = val;
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenOn() {
        int i;
        synchronized (this) {
            int val = this.mDischargeAmountScreenOn;
            if (this.mOnBattery && this.mScreenOn && this.mDischargeCurrentLevel < this.mDischargeScreenOnUnplugLevel) {
                val += this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            }
            i = val;
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenOnSinceCharge() {
        int i;
        synchronized (this) {
            int val = this.mDischargeAmountScreenOnSinceCharge;
            if (this.mOnBattery && this.mScreenOn && this.mDischargeCurrentLevel < this.mDischargeScreenOnUnplugLevel) {
                val += this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            }
            i = val;
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenOff() {
        int i;
        synchronized (this) {
            int val = this.mDischargeAmountScreenOff;
            if (this.mOnBattery && !this.mScreenOn && this.mDischargeCurrentLevel < this.mDischargeScreenOffUnplugLevel) {
                val += this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel;
            }
            i = val;
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getDischargeAmountScreenOffSinceCharge() {
        int i;
        synchronized (this) {
            int val = this.mDischargeAmountScreenOffSinceCharge;
            if (this.mOnBattery && !this.mScreenOn && this.mDischargeCurrentLevel < this.mDischargeScreenOffUnplugLevel) {
                val += this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel;
            }
            i = val;
        }
        return i;
    }

    @Override // android.os.BatteryStats
    public int getCpuSpeedSteps() {
        return sNumSpeedSteps;
    }

    public Uid getUidStatsLocked(int uid) {
        Uid u = this.mUidStats.get(uid);
        if (u == null) {
            u = new Uid(uid);
            this.mUidStats.put(uid, u);
        }
        return u;
    }

    public void removeUidStatsLocked(int uid) {
        this.mUidStats.remove(uid);
    }

    public Uid.Proc getProcessStatsLocked(int uid, String name) {
        Uid u = getUidStatsLocked(uid);
        return u.getProcessStatsLocked(name);
    }

    public Uid.Proc getProcessStatsLocked(String name, int pid) {
        int uid;
        if (this.mUidCache.containsKey(name)) {
            uid = this.mUidCache.get(name).intValue();
        } else {
            uid = Process.getUidForPid(pid);
            this.mUidCache.put(name, Integer.valueOf(uid));
        }
        Uid u = getUidStatsLocked(uid);
        return u.getProcessStatsLocked(name);
    }

    public Uid.Pkg getPackageStatsLocked(int uid, String pkg) {
        Uid u = getUidStatsLocked(uid);
        return u.getPackageStatsLocked(pkg);
    }

    public Uid.Pkg.Serv getServiceStatsLocked(int uid, String pkg, String name) {
        Uid u = getUidStatsLocked(uid);
        return u.getServiceStatsLocked(pkg, name);
    }

    public void distributeWorkLocked(int which) {
        Uid wifiUid = this.mUidStats.get(1010);
        if (wifiUid != null) {
            long uSecTime = computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, which);
            for (Uid.Proc proc : wifiUid.mProcessStats.values()) {
                long totalRunningTime = getGlobalWifiRunningTime(uSecTime, which);
                for (int i = 0; i < this.mUidStats.size(); i++) {
                    Uid uid = this.mUidStats.valueAt(i);
                    if (uid.mUid != 1010) {
                        long uidRunningTime = uid.getWifiRunningTime(uSecTime, which);
                        if (uidRunningTime > 0) {
                            Uid.Proc uidProc = uid.getProcessStatsLocked("*wifi*");
                            long time = (proc.getUserTime(which) * uidRunningTime) / totalRunningTime;
                            uidProc.mUserTime += time;
                            proc.mUserTime -= time;
                            long time2 = (proc.getSystemTime(which) * uidRunningTime) / totalRunningTime;
                            uidProc.mSystemTime += time2;
                            proc.mSystemTime -= time2;
                            long time3 = (proc.getForegroundTime(which) * uidRunningTime) / totalRunningTime;
                            uidProc.mForegroundTime += time3;
                            proc.mForegroundTime -= time3;
                            for (int sb = 0; sb < proc.mSpeedBins.length; sb++) {
                                SamplingCounter sc = proc.mSpeedBins[sb];
                                if (sc != null) {
                                    long time4 = (sc.getCountLocked(which) * uidRunningTime) / totalRunningTime;
                                    SamplingCounter uidSc = uidProc.mSpeedBins[sb];
                                    if (uidSc == null) {
                                        uidSc = new SamplingCounter(this.mUnpluggables);
                                        uidProc.mSpeedBins[sb] = uidSc;
                                    }
                                    uidSc.mCount.addAndGet((int) time4);
                                    sc.mCount.addAndGet((int) (-time4));
                                }
                            }
                            totalRunningTime -= uidRunningTime;
                        }
                    }
                }
            }
        }
    }

    public void shutdownLocked() {
        writeSyncLocked();
        this.mShuttingDown = true;
    }

    public void writeAsyncLocked() {
        writeLocked(false);
    }

    public void writeSyncLocked() {
        writeLocked(true);
    }

    void writeLocked(boolean sync) {
        if (this.mFile == null) {
            Slog.w("BatteryStats", "writeLocked: no file associated with this instance");
        } else if (this.mShuttingDown) {
        } else {
            Parcel out = Parcel.obtain();
            writeSummaryToParcel(out);
            this.mLastWriteTime = SystemClock.elapsedRealtime();
            if (this.mPendingWrite != null) {
                this.mPendingWrite.recycle();
            }
            this.mPendingWrite = out;
            if (sync) {
                commitPendingDataToDisk();
                return;
            }
            Thread thr = new Thread("BatteryStats-Write") { // from class: com.android.internal.os.BatteryStatsImpl.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    Process.setThreadPriority(10);
                    BatteryStatsImpl.this.commitPendingDataToDisk();
                }
            };
            thr.start();
        }
    }

    public void commitPendingDataToDisk() {
        Parcel next;
        synchronized (this) {
            next = this.mPendingWrite;
            this.mPendingWrite = null;
            if (next == null) {
                return;
            }
            this.mWriteLock.lock();
        }
        try {
            try {
                FileOutputStream stream = new FileOutputStream(this.mFile.chooseForWrite());
                stream.write(next.marshall());
                stream.flush();
                FileUtils.sync(stream);
                stream.close();
                this.mFile.commit();
                next.recycle();
                this.mWriteLock.unlock();
            } catch (IOException e) {
                Slog.w("BatteryStats", "Error writing battery statistics", e);
                this.mFile.rollback();
                next.recycle();
                this.mWriteLock.unlock();
            }
        } catch (Throwable th) {
            next.recycle();
            this.mWriteLock.unlock();
            throw th;
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

    public void readLocked() {
        File file;
        if (this.mFile == null) {
            Slog.w("BatteryStats", "readLocked: no file associated with this instance");
            return;
        }
        this.mUidStats.clear();
        try {
            file = this.mFile.chooseForRead();
        } catch (IOException e) {
            Slog.e("BatteryStats", "Error reading battery statistics", e);
        }
        if (!file.exists()) {
            return;
        }
        FileInputStream stream = new FileInputStream(file);
        byte[] raw = readFully(stream);
        Parcel in = Parcel.obtain();
        in.unmarshall(raw, 0, raw.length);
        in.setDataPosition(0);
        stream.close();
        readSummaryFromParcel(in);
        long now = SystemClock.elapsedRealtime();
        addHistoryBufferLocked(now, (byte) 2);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    void readHistory(Parcel in, boolean andOldHistory) {
        long historyBaseTime = in.readLong();
        this.mHistoryBuffer.setDataSize(0);
        this.mHistoryBuffer.setDataPosition(0);
        int bufSize = in.readInt();
        int curPos = in.dataPosition();
        if (bufSize >= 442368) {
            Slog.w(TAG, "File corrupt: history data buffer too large " + bufSize);
        } else if ((bufSize & (-4)) != bufSize) {
            Slog.w(TAG, "File corrupt: history data buffer not aligned " + bufSize);
        } else {
            this.mHistoryBuffer.appendFrom(in, curPos, bufSize);
            in.setDataPosition(curPos + bufSize);
        }
        if (andOldHistory) {
            readOldHistory(in);
        }
        this.mHistoryBaseTime = historyBaseTime;
        if (this.mHistoryBaseTime > 0) {
            long oldnow = SystemClock.elapsedRealtime();
            this.mHistoryBaseTime = (this.mHistoryBaseTime - oldnow) + DateUtils.MINUTE_IN_MILLIS;
        }
    }

    void readOldHistory(Parcel in) {
    }

    void writeHistory(Parcel out, boolean andOldHistory) {
        out.writeLong(this.mHistoryBaseTime + this.mLastHistoryTime);
        out.writeInt(this.mHistoryBuffer.dataSize());
        out.appendFrom(this.mHistoryBuffer, 0, this.mHistoryBuffer.dataSize());
        if (andOldHistory) {
            writeOldHistory(out);
        }
    }

    void writeOldHistory(Parcel out) {
    }

    private void readSummaryFromParcel(Parcel in) {
        int version = in.readInt();
        if (version != 66) {
            Slog.w("BatteryStats", "readFromParcel: version got " + version + ", expected 66; erasing old stats");
            return;
        }
        readHistory(in, true);
        this.mStartCount = in.readInt();
        this.mBatteryUptime = in.readLong();
        this.mBatteryRealtime = in.readLong();
        this.mUptime = in.readLong();
        this.mRealtime = in.readLong();
        this.mDischargeUnplugLevel = in.readInt();
        this.mDischargeCurrentLevel = in.readInt();
        this.mLowDischargeAmountSinceCharge = in.readInt();
        this.mHighDischargeAmountSinceCharge = in.readInt();
        this.mDischargeAmountScreenOnSinceCharge = in.readInt();
        this.mDischargeAmountScreenOffSinceCharge = in.readInt();
        this.mStartCount++;
        this.mScreenOn = false;
        this.mScreenOnTimer.readSummaryFromParcelLocked(in);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i].readSummaryFromParcelLocked(in);
        }
        this.mInputEventCounter.readSummaryFromParcelLocked(in);
        this.mPhoneOn = false;
        this.mPhoneOnTimer.readSummaryFromParcelLocked(in);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mPhoneSignalStrengthsTimer[i2].readSummaryFromParcelLocked(in);
        }
        this.mPhoneSignalScanningTimer.readSummaryFromParcelLocked(in);
        for (int i3 = 0; i3 < 16; i3++) {
            this.mPhoneDataConnectionsTimer[i3].readSummaryFromParcelLocked(in);
        }
        for (int i4 = 0; i4 < 4; i4++) {
            this.mNetworkActivityCounters[i4].readSummaryFromParcelLocked(in);
        }
        this.mWifiOn = false;
        this.mWifiOnTimer.readSummaryFromParcelLocked(in);
        this.mGlobalWifiRunning = false;
        this.mGlobalWifiRunningTimer.readSummaryFromParcelLocked(in);
        this.mBluetoothOn = false;
        this.mBluetoothOnTimer.readSummaryFromParcelLocked(in);
        int NKW = in.readInt();
        if (NKW > 10000) {
            Slog.w(TAG, "File corrupt: too many kernel wake locks " + NKW);
            return;
        }
        for (int ikw = 0; ikw < NKW; ikw++) {
            if (in.readInt() != 0) {
                String kwltName = in.readString();
                getKernelWakelockTimerLocked(kwltName).readSummaryFromParcelLocked(in);
            }
        }
        sNumSpeedSteps = in.readInt();
        int NU = in.readInt();
        if (NU > 10000) {
            Slog.w(TAG, "File corrupt: too many uids " + NU);
            return;
        }
        for (int iu = 0; iu < NU; iu++) {
            int uid = in.readInt();
            Uid u = new Uid(uid);
            this.mUidStats.put(uid, u);
            u.mWifiRunning = false;
            if (in.readInt() != 0) {
                u.mWifiRunningTimer.readSummaryFromParcelLocked(in);
            }
            u.mFullWifiLockOut = false;
            if (in.readInt() != 0) {
                u.mFullWifiLockTimer.readSummaryFromParcelLocked(in);
            }
            u.mWifiScanStarted = false;
            if (in.readInt() != 0) {
                u.mWifiScanTimer.readSummaryFromParcelLocked(in);
            }
            u.mWifiMulticastEnabled = false;
            if (in.readInt() != 0) {
                u.mWifiMulticastTimer.readSummaryFromParcelLocked(in);
            }
            u.mAudioTurnedOn = false;
            if (in.readInt() != 0) {
                u.createAudioTurnedOnTimerLocked().readSummaryFromParcelLocked(in);
            }
            u.mVideoTurnedOn = false;
            if (in.readInt() != 0) {
                u.createVideoTurnedOnTimerLocked().readSummaryFromParcelLocked(in);
            }
            if (in.readInt() != 0) {
                u.createForegroundActivityTimerLocked().readSummaryFromParcelLocked(in);
            }
            if (in.readInt() != 0) {
                u.createVibratorOnTimerLocked().readSummaryFromParcelLocked(in);
            }
            if (in.readInt() != 0) {
                if (u.mUserActivityCounters == null) {
                    u.initUserActivityLocked();
                }
                for (int i5 = 0; i5 < 3; i5++) {
                    u.mUserActivityCounters[i5].readSummaryFromParcelLocked(in);
                }
            }
            if (in.readInt() != 0) {
                if (u.mNetworkActivityCounters == null) {
                    u.initNetworkActivityLocked();
                }
                for (int i6 = 0; i6 < 4; i6++) {
                    u.mNetworkActivityCounters[i6].readSummaryFromParcelLocked(in);
                }
            }
            int NW = in.readInt();
            if (NW > 100) {
                Slog.w(TAG, "File corrupt: too many wake locks " + NW);
                return;
            }
            for (int iw = 0; iw < NW; iw++) {
                String wlName = in.readString();
                if (in.readInt() != 0) {
                    u.getWakeTimerLocked(wlName, 1).readSummaryFromParcelLocked(in);
                }
                if (in.readInt() != 0) {
                    u.getWakeTimerLocked(wlName, 0).readSummaryFromParcelLocked(in);
                }
                if (in.readInt() != 0) {
                    u.getWakeTimerLocked(wlName, 2).readSummaryFromParcelLocked(in);
                }
            }
            int NP = in.readInt();
            if (NP > 1000) {
                Slog.w(TAG, "File corrupt: too many sensors " + NP);
                return;
            }
            for (int is = 0; is < NP; is++) {
                int seNumber = in.readInt();
                if (in.readInt() != 0) {
                    u.getSensorTimerLocked(seNumber, true).readSummaryFromParcelLocked(in);
                }
            }
            int NP2 = in.readInt();
            if (NP2 > 1000) {
                Slog.w(TAG, "File corrupt: too many processes " + NP2);
                return;
            }
            for (int ip = 0; ip < NP2; ip++) {
                String procName = in.readString();
                Uid.Proc p = u.getProcessStatsLocked(procName);
                long readLong = in.readLong();
                p.mLoadedUserTime = readLong;
                p.mUserTime = readLong;
                long readLong2 = in.readLong();
                p.mLoadedSystemTime = readLong2;
                p.mSystemTime = readLong2;
                long readLong3 = in.readLong();
                p.mLoadedForegroundTime = readLong3;
                p.mForegroundTime = readLong3;
                int readInt = in.readInt();
                p.mLoadedStarts = readInt;
                p.mStarts = readInt;
                int NSB = in.readInt();
                if (NSB > 100) {
                    Slog.w(TAG, "File corrupt: too many speed bins " + NSB);
                    return;
                }
                p.mSpeedBins = new SamplingCounter[NSB];
                for (int i7 = 0; i7 < NSB; i7++) {
                    if (in.readInt() != 0) {
                        p.mSpeedBins[i7] = new SamplingCounter(this.mUnpluggables);
                        p.mSpeedBins[i7].readSummaryFromParcelLocked(in);
                    }
                }
                if (!p.readExcessivePowerFromParcelLocked(in)) {
                    return;
                }
            }
            int NP3 = in.readInt();
            if (NP3 > 10000) {
                Slog.w(TAG, "File corrupt: too many packages " + NP3);
                return;
            }
            for (int ip2 = 0; ip2 < NP3; ip2++) {
                String pkgName = in.readString();
                Uid.Pkg p2 = u.getPackageStatsLocked(pkgName);
                int readInt2 = in.readInt();
                p2.mLoadedWakeups = readInt2;
                p2.mWakeups = readInt2;
                int NS = in.readInt();
                if (NS > 1000) {
                    Slog.w(TAG, "File corrupt: too many services " + NS);
                    return;
                }
                for (int is2 = 0; is2 < NS; is2++) {
                    String servName = in.readString();
                    Uid.Pkg.Serv s = u.getServiceStatsLocked(pkgName, servName);
                    long readLong4 = in.readLong();
                    s.mLoadedStartTime = readLong4;
                    s.mStartTime = readLong4;
                    int readInt3 = in.readInt();
                    s.mLoadedStarts = readInt3;
                    s.mStarts = readInt3;
                    int readInt4 = in.readInt();
                    s.mLoadedLaunches = readInt4;
                    s.mLaunches = readInt4;
                }
            }
        }
    }

    public void writeSummaryToParcel(Parcel out) {
        updateKernelWakelocksLocked();
        updateNetworkActivityLocked();
        long NOW_SYS = SystemClock.uptimeMillis() * 1000;
        long NOWREAL_SYS = SystemClock.elapsedRealtime() * 1000;
        long NOW = getBatteryUptimeLocked(NOW_SYS);
        long NOWREAL = getBatteryRealtimeLocked(NOWREAL_SYS);
        out.writeInt(66);
        writeHistory(out, true);
        out.writeInt(this.mStartCount);
        out.writeLong(computeBatteryUptime(NOW_SYS, 0));
        out.writeLong(computeBatteryRealtime(NOWREAL_SYS, 0));
        out.writeLong(computeUptime(NOW_SYS, 0));
        out.writeLong(computeRealtime(NOWREAL_SYS, 0));
        out.writeInt(this.mDischargeUnplugLevel);
        out.writeInt(this.mDischargeCurrentLevel);
        out.writeInt(getLowDischargeAmountSinceCharge());
        out.writeInt(getHighDischargeAmountSinceCharge());
        out.writeInt(getDischargeAmountScreenOnSinceCharge());
        out.writeInt(getDischargeAmountScreenOffSinceCharge());
        this.mScreenOnTimer.writeSummaryFromParcelLocked(out, NOWREAL);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i].writeSummaryFromParcelLocked(out, NOWREAL);
        }
        this.mInputEventCounter.writeSummaryFromParcelLocked(out);
        this.mPhoneOnTimer.writeSummaryFromParcelLocked(out, NOWREAL);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mPhoneSignalStrengthsTimer[i2].writeSummaryFromParcelLocked(out, NOWREAL);
        }
        this.mPhoneSignalScanningTimer.writeSummaryFromParcelLocked(out, NOWREAL);
        for (int i3 = 0; i3 < 16; i3++) {
            this.mPhoneDataConnectionsTimer[i3].writeSummaryFromParcelLocked(out, NOWREAL);
        }
        for (int i4 = 0; i4 < 4; i4++) {
            this.mNetworkActivityCounters[i4].writeSummaryFromParcelLocked(out);
        }
        this.mWifiOnTimer.writeSummaryFromParcelLocked(out, NOWREAL);
        this.mGlobalWifiRunningTimer.writeSummaryFromParcelLocked(out, NOWREAL);
        this.mBluetoothOnTimer.writeSummaryFromParcelLocked(out, NOWREAL);
        out.writeInt(this.mKernelWakelockStats.size());
        for (Map.Entry<String, SamplingTimer> ent : this.mKernelWakelockStats.entrySet()) {
            Timer kwlt = ent.getValue();
            if (kwlt != null) {
                out.writeInt(1);
                out.writeString(ent.getKey());
                ent.getValue().writeSummaryFromParcelLocked(out, NOWREAL);
            } else {
                out.writeInt(0);
            }
        }
        out.writeInt(sNumSpeedSteps);
        int NU = this.mUidStats.size();
        out.writeInt(NU);
        for (int iu = 0; iu < NU; iu++) {
            out.writeInt(this.mUidStats.keyAt(iu));
            Uid u = this.mUidStats.valueAt(iu);
            if (u.mWifiRunningTimer != null) {
                out.writeInt(1);
                u.mWifiRunningTimer.writeSummaryFromParcelLocked(out, NOWREAL);
            } else {
                out.writeInt(0);
            }
            if (u.mFullWifiLockTimer != null) {
                out.writeInt(1);
                u.mFullWifiLockTimer.writeSummaryFromParcelLocked(out, NOWREAL);
            } else {
                out.writeInt(0);
            }
            if (u.mWifiScanTimer != null) {
                out.writeInt(1);
                u.mWifiScanTimer.writeSummaryFromParcelLocked(out, NOWREAL);
            } else {
                out.writeInt(0);
            }
            if (u.mWifiMulticastTimer != null) {
                out.writeInt(1);
                u.mWifiMulticastTimer.writeSummaryFromParcelLocked(out, NOWREAL);
            } else {
                out.writeInt(0);
            }
            if (u.mAudioTurnedOnTimer != null) {
                out.writeInt(1);
                u.mAudioTurnedOnTimer.writeSummaryFromParcelLocked(out, NOWREAL);
            } else {
                out.writeInt(0);
            }
            if (u.mVideoTurnedOnTimer != null) {
                out.writeInt(1);
                u.mVideoTurnedOnTimer.writeSummaryFromParcelLocked(out, NOWREAL);
            } else {
                out.writeInt(0);
            }
            if (u.mForegroundActivityTimer != null) {
                out.writeInt(1);
                u.mForegroundActivityTimer.writeSummaryFromParcelLocked(out, NOWREAL);
            } else {
                out.writeInt(0);
            }
            if (u.mVibratorOnTimer != null) {
                out.writeInt(1);
                u.mVibratorOnTimer.writeSummaryFromParcelLocked(out, NOWREAL);
            } else {
                out.writeInt(0);
            }
            if (u.mUserActivityCounters == null) {
                out.writeInt(0);
            } else {
                out.writeInt(1);
                for (int i5 = 0; i5 < 3; i5++) {
                    u.mUserActivityCounters[i5].writeSummaryFromParcelLocked(out);
                }
            }
            if (u.mNetworkActivityCounters == null) {
                out.writeInt(0);
            } else {
                out.writeInt(1);
                for (int i6 = 0; i6 < 4; i6++) {
                    u.mNetworkActivityCounters[i6].writeSummaryFromParcelLocked(out);
                }
            }
            int NW = u.mWakelockStats.size();
            out.writeInt(NW);
            if (NW > 0) {
                for (Map.Entry<String, Uid.Wakelock> ent2 : u.mWakelockStats.entrySet()) {
                    out.writeString(ent2.getKey());
                    Uid.Wakelock wl = ent2.getValue();
                    if (wl.mTimerFull != null) {
                        out.writeInt(1);
                        wl.mTimerFull.writeSummaryFromParcelLocked(out, NOWREAL);
                    } else {
                        out.writeInt(0);
                    }
                    if (wl.mTimerPartial != null) {
                        out.writeInt(1);
                        wl.mTimerPartial.writeSummaryFromParcelLocked(out, NOWREAL);
                    } else {
                        out.writeInt(0);
                    }
                    if (wl.mTimerWindow != null) {
                        out.writeInt(1);
                        wl.mTimerWindow.writeSummaryFromParcelLocked(out, NOWREAL);
                    } else {
                        out.writeInt(0);
                    }
                }
            }
            int NSE = u.mSensorStats.size();
            out.writeInt(NSE);
            if (NSE > 0) {
                for (Map.Entry<Integer, Uid.Sensor> ent3 : u.mSensorStats.entrySet()) {
                    out.writeInt(ent3.getKey().intValue());
                    Uid.Sensor se = ent3.getValue();
                    if (se.mTimer != null) {
                        out.writeInt(1);
                        se.mTimer.writeSummaryFromParcelLocked(out, NOWREAL);
                    } else {
                        out.writeInt(0);
                    }
                }
            }
            int NP = u.mProcessStats.size();
            out.writeInt(NP);
            if (NP > 0) {
                for (Map.Entry<String, Uid.Proc> ent4 : u.mProcessStats.entrySet()) {
                    out.writeString(ent4.getKey());
                    Uid.Proc ps = ent4.getValue();
                    out.writeLong(ps.mUserTime);
                    out.writeLong(ps.mSystemTime);
                    out.writeLong(ps.mForegroundTime);
                    out.writeInt(ps.mStarts);
                    int N = ps.mSpeedBins.length;
                    out.writeInt(N);
                    for (int i7 = 0; i7 < N; i7++) {
                        if (ps.mSpeedBins[i7] != null) {
                            out.writeInt(1);
                            ps.mSpeedBins[i7].writeSummaryFromParcelLocked(out);
                        } else {
                            out.writeInt(0);
                        }
                    }
                    ps.writeExcessivePowerToParcelLocked(out);
                }
            }
            int NP2 = u.mPackageStats.size();
            out.writeInt(NP2);
            if (NP2 > 0) {
                for (Map.Entry<String, Uid.Pkg> ent5 : u.mPackageStats.entrySet()) {
                    out.writeString(ent5.getKey());
                    Uid.Pkg ps2 = ent5.getValue();
                    out.writeInt(ps2.mWakeups);
                    int NS = ps2.mServiceStats.size();
                    out.writeInt(NS);
                    if (NS > 0) {
                        for (Map.Entry<String, Uid.Pkg.Serv> sent : ps2.mServiceStats.entrySet()) {
                            out.writeString(sent.getKey());
                            Uid.Pkg.Serv ss = sent.getValue();
                            long time = ss.getStartTimeToNowLocked(NOW);
                            out.writeLong(time);
                            out.writeInt(ss.mStarts);
                            out.writeInt(ss.mLaunches);
                        }
                    }
                }
            }
        }
    }

    public void readFromParcel(Parcel in) {
        readFromParcelLocked(in);
    }

    void readFromParcelLocked(Parcel in) {
        int magic = in.readInt();
        if (magic != MAGIC) {
            throw new ParcelFormatException("Bad magic number");
        }
        readHistory(in, false);
        this.mStartCount = in.readInt();
        this.mBatteryUptime = in.readLong();
        this.mBatteryLastUptime = 0L;
        this.mBatteryRealtime = in.readLong();
        this.mBatteryLastRealtime = 0L;
        this.mScreenOn = false;
        this.mScreenOnTimer = new StopwatchTimer(null, -1, null, this.mUnpluggables, in);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i] = new StopwatchTimer(null, (-100) - i, null, this.mUnpluggables, in);
        }
        this.mInputEventCounter = new Counter(this.mUnpluggables, in);
        this.mPhoneOn = false;
        this.mPhoneOnTimer = new StopwatchTimer(null, -2, null, this.mUnpluggables, in);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mPhoneSignalStrengthsTimer[i2] = new StopwatchTimer(null, AudioService.STREAM_REMOTE_MUSIC - i2, null, this.mUnpluggables, in);
        }
        this.mPhoneSignalScanningTimer = new StopwatchTimer(null, -199, null, this.mUnpluggables, in);
        for (int i3 = 0; i3 < 16; i3++) {
            this.mPhoneDataConnectionsTimer[i3] = new StopwatchTimer(null, (-300) - i3, null, this.mUnpluggables, in);
        }
        for (int i4 = 0; i4 < 4; i4++) {
            this.mNetworkActivityCounters[i4] = new LongSamplingCounter(this.mUnpluggables, in);
        }
        this.mWifiOn = false;
        this.mWifiOnTimer = new StopwatchTimer(null, -2, null, this.mUnpluggables, in);
        this.mGlobalWifiRunning = false;
        this.mGlobalWifiRunningTimer = new StopwatchTimer(null, -2, null, this.mUnpluggables, in);
        this.mBluetoothOn = false;
        this.mBluetoothOnTimer = new StopwatchTimer(null, -2, null, this.mUnpluggables, in);
        this.mUptime = in.readLong();
        this.mUptimeStart = in.readLong();
        this.mLastUptime = 0L;
        this.mRealtime = in.readLong();
        this.mRealtimeStart = in.readLong();
        this.mLastRealtime = 0L;
        this.mOnBattery = in.readInt() != 0;
        this.mOnBatteryInternal = false;
        this.mTrackBatteryPastUptime = in.readLong();
        this.mTrackBatteryUptimeStart = in.readLong();
        this.mTrackBatteryPastRealtime = in.readLong();
        this.mTrackBatteryRealtimeStart = in.readLong();
        this.mUnpluggedBatteryUptime = in.readLong();
        this.mUnpluggedBatteryRealtime = in.readLong();
        this.mDischargeUnplugLevel = in.readInt();
        this.mDischargeCurrentLevel = in.readInt();
        this.mLowDischargeAmountSinceCharge = in.readInt();
        this.mHighDischargeAmountSinceCharge = in.readInt();
        this.mDischargeAmountScreenOn = in.readInt();
        this.mDischargeAmountScreenOnSinceCharge = in.readInt();
        this.mDischargeAmountScreenOff = in.readInt();
        this.mDischargeAmountScreenOffSinceCharge = in.readInt();
        this.mLastWriteTime = in.readLong();
        this.mRadioDataUptime = in.readLong();
        this.mRadioDataStart = -1L;
        this.mBluetoothPingCount = in.readInt();
        this.mBluetoothPingStart = -1;
        this.mKernelWakelockStats.clear();
        int NKW = in.readInt();
        for (int ikw = 0; ikw < NKW; ikw++) {
            if (in.readInt() != 0) {
                String wakelockName = in.readString();
                in.readInt();
                SamplingTimer kwlt = new SamplingTimer(this.mUnpluggables, this.mOnBattery, in);
                this.mKernelWakelockStats.put(wakelockName, kwlt);
            }
        }
        this.mPartialTimers.clear();
        this.mFullTimers.clear();
        this.mWindowTimers.clear();
        this.mWifiRunningTimers.clear();
        this.mFullWifiLockTimers.clear();
        this.mWifiScanTimers.clear();
        this.mWifiMulticastTimers.clear();
        sNumSpeedSteps = in.readInt();
        int numUids = in.readInt();
        this.mUidStats.clear();
        for (int i5 = 0; i5 < numUids; i5++) {
            int uid = in.readInt();
            Uid u = new Uid(uid);
            u.readFromParcelLocked(this.mUnpluggables, in);
            this.mUidStats.append(uid, u);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        writeToParcelLocked(out, true, flags);
    }

    public void writeToParcelWithoutUids(Parcel out, int flags) {
        writeToParcelLocked(out, false, flags);
    }

    void writeToParcelLocked(Parcel out, boolean inclUids, int flags) {
        updateKernelWakelocksLocked();
        updateNetworkActivityLocked();
        long uSecUptime = SystemClock.uptimeMillis() * 1000;
        long uSecRealtime = SystemClock.elapsedRealtime() * 1000;
        long batteryUptime = getBatteryUptimeLocked(uSecUptime);
        long batteryRealtime = getBatteryRealtimeLocked(uSecRealtime);
        out.writeInt(MAGIC);
        writeHistory(out, false);
        out.writeInt(this.mStartCount);
        out.writeLong(this.mBatteryUptime);
        out.writeLong(this.mBatteryRealtime);
        this.mScreenOnTimer.writeToParcel(out, batteryRealtime);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i].writeToParcel(out, batteryRealtime);
        }
        this.mInputEventCounter.writeToParcel(out);
        this.mPhoneOnTimer.writeToParcel(out, batteryRealtime);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mPhoneSignalStrengthsTimer[i2].writeToParcel(out, batteryRealtime);
        }
        this.mPhoneSignalScanningTimer.writeToParcel(out, batteryRealtime);
        for (int i3 = 0; i3 < 16; i3++) {
            this.mPhoneDataConnectionsTimer[i3].writeToParcel(out, batteryRealtime);
        }
        for (int i4 = 0; i4 < 4; i4++) {
            this.mNetworkActivityCounters[i4].writeToParcel(out);
        }
        this.mWifiOnTimer.writeToParcel(out, batteryRealtime);
        this.mGlobalWifiRunningTimer.writeToParcel(out, batteryRealtime);
        this.mBluetoothOnTimer.writeToParcel(out, batteryRealtime);
        out.writeLong(this.mUptime);
        out.writeLong(this.mUptimeStart);
        out.writeLong(this.mRealtime);
        out.writeLong(this.mRealtimeStart);
        out.writeInt(this.mOnBattery ? 1 : 0);
        out.writeLong(batteryUptime);
        out.writeLong(this.mTrackBatteryUptimeStart);
        out.writeLong(batteryRealtime);
        out.writeLong(this.mTrackBatteryRealtimeStart);
        out.writeLong(this.mUnpluggedBatteryUptime);
        out.writeLong(this.mUnpluggedBatteryRealtime);
        out.writeInt(this.mDischargeUnplugLevel);
        out.writeInt(this.mDischargeCurrentLevel);
        out.writeInt(this.mLowDischargeAmountSinceCharge);
        out.writeInt(this.mHighDischargeAmountSinceCharge);
        out.writeInt(this.mDischargeAmountScreenOn);
        out.writeInt(this.mDischargeAmountScreenOnSinceCharge);
        out.writeInt(this.mDischargeAmountScreenOff);
        out.writeInt(this.mDischargeAmountScreenOffSinceCharge);
        out.writeLong(this.mLastWriteTime);
        out.writeLong(getRadioDataUptime());
        out.writeInt(getBluetoothPingCount());
        if (inclUids) {
            out.writeInt(this.mKernelWakelockStats.size());
            for (Map.Entry<String, SamplingTimer> ent : this.mKernelWakelockStats.entrySet()) {
                SamplingTimer kwlt = ent.getValue();
                if (kwlt != null) {
                    out.writeInt(1);
                    out.writeString(ent.getKey());
                    Timer.writeTimerToParcel(out, kwlt, batteryRealtime);
                } else {
                    out.writeInt(0);
                }
            }
        } else {
            out.writeInt(0);
        }
        out.writeInt(sNumSpeedSteps);
        if (inclUids) {
            int size = this.mUidStats.size();
            out.writeInt(size);
            for (int i5 = 0; i5 < size; i5++) {
                out.writeInt(this.mUidStats.keyAt(i5));
                Uid uid = this.mUidStats.valueAt(i5);
                uid.writeToParcelLocked(out, batteryRealtime);
            }
            return;
        }
        out.writeInt(0);
    }

    @Override // android.os.BatteryStats
    public void prepareForDumpLocked() {
        updateKernelWakelocksLocked();
        updateNetworkActivityLocked();
    }

    @Override // android.os.BatteryStats
    public void dumpLocked(PrintWriter pw, boolean isUnpluggedOnly, int reqUid) {
        super.dumpLocked(pw, isUnpluggedOnly, reqUid);
    }
}