package com.android.server;

import android.Manifest;
import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.IAlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Telephony;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Pair;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.util.LocalLog;
import gov.nist.core.Separators;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TimeZone;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: AlarmManagerService.class */
public class AlarmManagerService extends IAlarmManager.Stub {
    private static final long LATE_ALARM_THRESHOLD = 10000;
    private static final int RTC_WAKEUP_MASK = 1;
    private static final int RTC_MASK = 2;
    private static final int ELAPSED_REALTIME_WAKEUP_MASK = 4;
    private static final int ELAPSED_REALTIME_MASK = 8;
    private static final int TIME_CHANGED_MASK = 65536;
    private static final int IS_WAKEUP_MASK = 5;
    private static final int TYPE_NONWAKEUP_MASK = 1;
    private static final String TAG = "AlarmManager";
    private static final String ClockReceiver_TAG = "ClockReceiver";
    private static final boolean localLOGV = false;
    private static final boolean DEBUG_BATCH = false;
    private static final boolean DEBUG_VALIDATE = false;
    private static final int ALARM_EVENT = 1;
    private static final String TIMEZONE_PROPERTY = "persist.sys.timezone";
    private static final boolean WAKEUP_STATS = false;
    private final Context mContext;
    private PowerManager.WakeLock mWakeLock;
    private ClockReceiver mClockReceiver;
    private UninstallReceiver mUninstallReceiver;
    private final PendingIntent mTimeTickSender;
    private final PendingIntent mDateChangeSender;
    private static final long MIN_FUZZABLE_INTERVAL = 10000;
    private static final Intent mBackgroundIntent = new Intent().addFlags(4);
    private static final IncreasingTimeOrder sIncreasingTimeOrder = new IncreasingTimeOrder();
    private static final BatchTimeOrder sBatchOrder = new BatchTimeOrder();
    private final LocalLog mLog = new LocalLog(TAG);
    private Object mLock = new Object();
    private int mBroadcastRefCount = 0;
    private ArrayList<InFlight> mInFlight = new ArrayList<>();
    private final AlarmThread mWaitThread = new AlarmThread();
    private final AlarmHandler mHandler = new AlarmHandler();
    private final ResultReceiver mResultReceiver = new ResultReceiver();
    private final LinkedList<WakeupEvent> mRecentWakeups = new LinkedList<>();
    private final long RECENT_WAKEUP_PERIOD = 86400000;
    private final ArrayList<Batch> mAlarmBatches = new ArrayList<>();
    private final HashMap<String, BroadcastStats> mBroadcastStats = new HashMap<>();
    private int mDescriptor = init();
    private long mNextNonWakeup = 0;
    private long mNextWakeup = 0;

    private native int init();

    private native void close(int i);

    private native void set(int i, int i2, long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    public native int waitForAlarm(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native int setKernelTimezone(int i, int i2);

    static /* synthetic */ int access$1308(AlarmManagerService x0) {
        int i = x0.mBroadcastRefCount;
        x0.mBroadcastRefCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$1310(AlarmManagerService x0) {
        int i = x0.mBroadcastRefCount;
        x0.mBroadcastRefCount = i - 1;
        return i;
    }

    /* loaded from: AlarmManagerService$WakeupEvent.class */
    class WakeupEvent {
        public long when;
        public int uid;
        public String action;

        public WakeupEvent(long theTime, int theUid, String theAction) {
            this.when = theTime;
            this.uid = theUid;
            this.action = theAction;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AlarmManagerService$Batch.class */
    public static final class Batch {
        long start;
        long end;
        boolean standalone;
        final ArrayList<Alarm> alarms;

        Batch() {
            this.alarms = new ArrayList<>();
            this.start = 0L;
            this.end = Long.MAX_VALUE;
        }

        Batch(Alarm seed) {
            this.alarms = new ArrayList<>();
            this.start = seed.whenElapsed;
            this.end = seed.maxWhen;
            this.alarms.add(seed);
        }

        int size() {
            return this.alarms.size();
        }

        Alarm get(int index) {
            return this.alarms.get(index);
        }

        boolean canHold(long whenElapsed, long maxWhen) {
            return this.end >= whenElapsed && this.start <= maxWhen;
        }

        boolean add(Alarm alarm) {
            boolean newStart = false;
            int index = Collections.binarySearch(this.alarms, alarm, AlarmManagerService.sIncreasingTimeOrder);
            if (index < 0) {
                index = (0 - index) - 1;
            }
            this.alarms.add(index, alarm);
            if (alarm.whenElapsed > this.start) {
                this.start = alarm.whenElapsed;
                newStart = true;
            }
            if (alarm.maxWhen < this.end) {
                this.end = alarm.maxWhen;
            }
            return newStart;
        }

        boolean remove(PendingIntent operation) {
            boolean didRemove = false;
            long newStart = 0;
            long newEnd = Long.MAX_VALUE;
            int i = 0;
            while (i < this.alarms.size()) {
                Alarm alarm = this.alarms.get(i);
                if (alarm.operation.equals(operation)) {
                    this.alarms.remove(i);
                    didRemove = true;
                } else {
                    if (alarm.whenElapsed > newStart) {
                        newStart = alarm.whenElapsed;
                    }
                    if (alarm.maxWhen < newEnd) {
                        newEnd = alarm.maxWhen;
                    }
                    i++;
                }
            }
            if (didRemove) {
                this.start = newStart;
                this.end = newEnd;
            }
            return didRemove;
        }

        boolean remove(String packageName) {
            boolean didRemove = false;
            long newStart = 0;
            long newEnd = Long.MAX_VALUE;
            int i = 0;
            while (i < this.alarms.size()) {
                Alarm alarm = this.alarms.get(i);
                if (alarm.operation.getTargetPackage().equals(packageName)) {
                    this.alarms.remove(i);
                    didRemove = true;
                } else {
                    if (alarm.whenElapsed > newStart) {
                        newStart = alarm.whenElapsed;
                    }
                    if (alarm.maxWhen < newEnd) {
                        newEnd = alarm.maxWhen;
                    }
                    i++;
                }
            }
            if (didRemove) {
                this.start = newStart;
                this.end = newEnd;
            }
            return didRemove;
        }

        boolean remove(int userHandle) {
            boolean didRemove = false;
            long newStart = 0;
            long newEnd = Long.MAX_VALUE;
            int i = 0;
            while (i < this.alarms.size()) {
                Alarm alarm = this.alarms.get(i);
                if (UserHandle.getUserId(alarm.operation.getCreatorUid()) == userHandle) {
                    this.alarms.remove(i);
                    didRemove = true;
                } else {
                    if (alarm.whenElapsed > newStart) {
                        newStart = alarm.whenElapsed;
                    }
                    if (alarm.maxWhen < newEnd) {
                        newEnd = alarm.maxWhen;
                    }
                    i++;
                }
            }
            if (didRemove) {
                this.start = newStart;
                this.end = newEnd;
            }
            return didRemove;
        }

        boolean hasPackage(String packageName) {
            int N = this.alarms.size();
            for (int i = 0; i < N; i++) {
                Alarm a = this.alarms.get(i);
                if (a.operation.getTargetPackage().equals(packageName)) {
                    return true;
                }
            }
            return false;
        }

        boolean hasWakeups() {
            int N = this.alarms.size();
            for (int i = 0; i < N; i++) {
                Alarm a = this.alarms.get(i);
                if ((a.type & 1) == 0) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            StringBuilder b = new StringBuilder(40);
            b.append("Batch{");
            b.append(Integer.toHexString(hashCode()));
            b.append(" num=");
            b.append(size());
            b.append(" start=");
            b.append(this.start);
            b.append(" end=");
            b.append(this.end);
            if (this.standalone) {
                b.append(" STANDALONE");
            }
            b.append('}');
            return b.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: AlarmManagerService$BatchTimeOrder.class */
    public static class BatchTimeOrder implements Comparator<Batch> {
        BatchTimeOrder() {
        }

        @Override // java.util.Comparator
        public int compare(Batch b1, Batch b2) {
            long when1 = b1.start;
            long when2 = b2.start;
            if (when1 - when2 > 0) {
                return 1;
            }
            if (when1 - when2 < 0) {
                return -1;
            }
            return 0;
        }
    }

    static long convertToElapsed(long when, int type) {
        boolean isRtc = type == 1 || type == 0;
        if (isRtc) {
            when -= System.currentTimeMillis() - SystemClock.elapsedRealtime();
        }
        return when;
    }

    static long maxTriggerTime(long now, long triggerAtTime, long interval) {
        long futurity = interval == 0 ? triggerAtTime - now : interval;
        if (futurity < 10000) {
            futurity = 0;
        }
        return triggerAtTime + ((long) (0.75d * futurity));
    }

    static boolean addBatchLocked(ArrayList<Batch> list, Batch newBatch) {
        int index = Collections.binarySearch(list, newBatch, sBatchOrder);
        if (index < 0) {
            index = (0 - index) - 1;
        }
        list.add(index, newBatch);
        return index == 0;
    }

    int attemptCoalesceLocked(long whenElapsed, long maxWhen) {
        int N = this.mAlarmBatches.size();
        for (int i = 0; i < N; i++) {
            Batch b = this.mAlarmBatches.get(i);
            if (!b.standalone && b.canHold(whenElapsed, maxWhen)) {
                return i;
            }
        }
        return -1;
    }

    void rebatchAllAlarms() {
        synchronized (this.mLock) {
            rebatchAllAlarmsLocked(true);
        }
    }

    void rebatchAllAlarmsLocked(boolean doValidate) {
        ArrayList<Batch> oldSet = (ArrayList) this.mAlarmBatches.clone();
        this.mAlarmBatches.clear();
        long nowElapsed = SystemClock.elapsedRealtime();
        int oldBatches = oldSet.size();
        for (int batchNum = 0; batchNum < oldBatches; batchNum++) {
            Batch batch = oldSet.get(batchNum);
            int N = batch.size();
            for (int i = 0; i < N; i++) {
                Alarm a = batch.get(i);
                long whenElapsed = convertToElapsed(a.when, a.type);
                long maxElapsed = a.whenElapsed == a.maxWhen ? whenElapsed : maxTriggerTime(nowElapsed, whenElapsed, a.repeatInterval);
                setImplLocked(a.type, a.when, whenElapsed, maxElapsed, a.repeatInterval, a.operation, batch.standalone, doValidate, a.workSource);
            }
        }
    }

    /* loaded from: AlarmManagerService$InFlight.class */
    private static final class InFlight extends Intent {
        final PendingIntent mPendingIntent;
        final WorkSource mWorkSource;
        final Pair<String, ComponentName> mTarget;
        final BroadcastStats mBroadcastStats;
        final FilterStats mFilterStats;

        InFlight(AlarmManagerService service, PendingIntent pendingIntent, WorkSource workSource) {
            this.mPendingIntent = pendingIntent;
            this.mWorkSource = workSource;
            Intent intent = pendingIntent.getIntent();
            this.mTarget = intent != null ? new Pair<>(intent.getAction(), intent.getComponent()) : null;
            this.mBroadcastStats = service.getStatsLocked(pendingIntent);
            FilterStats fs = this.mBroadcastStats.filterStats.get(this.mTarget);
            if (fs == null) {
                fs = new FilterStats(this.mBroadcastStats, this.mTarget);
                this.mBroadcastStats.filterStats.put(this.mTarget, fs);
            }
            this.mFilterStats = fs;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AlarmManagerService$FilterStats.class */
    public static final class FilterStats {
        final BroadcastStats mBroadcastStats;
        final Pair<String, ComponentName> mTarget;
        long aggregateTime;
        int count;
        int numWakeup;
        long startTime;
        int nesting;

        FilterStats(BroadcastStats broadcastStats, Pair<String, ComponentName> target) {
            this.mBroadcastStats = broadcastStats;
            this.mTarget = target;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AlarmManagerService$BroadcastStats.class */
    public static final class BroadcastStats {
        final String mPackageName;
        long aggregateTime;
        int count;
        int numWakeup;
        long startTime;
        int nesting;
        final HashMap<Pair<String, ComponentName>, FilterStats> filterStats = new HashMap<>();

        BroadcastStats(String packageName) {
            this.mPackageName = packageName;
        }
    }

    public AlarmManagerService(Context context) {
        this.mContext = context;
        String tz = SystemProperties.get(TIMEZONE_PROPERTY);
        if (tz != null) {
            setTimeZone(tz);
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(1, TAG);
        this.mTimeTickSender = PendingIntent.getBroadcastAsUser(context, 0, new Intent(Intent.ACTION_TIME_TICK).addFlags(1342177280), 0, UserHandle.ALL);
        Intent intent = new Intent(Intent.ACTION_DATE_CHANGED);
        intent.addFlags(536870912);
        this.mDateChangeSender = PendingIntent.getBroadcastAsUser(context, 0, intent, 67108864, UserHandle.ALL);
        this.mClockReceiver = new ClockReceiver();
        this.mClockReceiver.scheduleTimeTickEvent();
        this.mClockReceiver.scheduleDateChangedEvent();
        this.mUninstallReceiver = new UninstallReceiver();
        if (this.mDescriptor != -1) {
            this.mWaitThread.start();
        } else {
            Slog.w(TAG, "Failed to open alarm driver. Falling back to a handler.");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.Binder
    public void finalize() throws Throwable {
        try {
            close(this.mDescriptor);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    @Override // android.app.IAlarmManager
    public void set(int type, long triggerAtTime, long windowLength, long interval, PendingIntent operation, WorkSource workSource) {
        if (workSource != null) {
            this.mContext.enforceCallingPermission(Manifest.permission.UPDATE_DEVICE_STATS, "AlarmManager.set");
        }
        set(type, triggerAtTime, windowLength, interval, operation, false, workSource);
    }

    public void set(int type, long triggerAtTime, long windowLength, long interval, PendingIntent operation, boolean isStandalone, WorkSource workSource) {
        long maxElapsed;
        if (operation == null) {
            Slog.w(TAG, "set/setRepeating ignored because there is no intent");
            return;
        }
        if (windowLength > AlarmManager.INTERVAL_HALF_DAY) {
            Slog.w(TAG, "Window length " + windowLength + "ms suspiciously long; limiting to 1 hour");
            windowLength = 3600000;
        }
        if (type < 0 || type > 3) {
            throw new IllegalArgumentException("Invalid alarm type " + type);
        }
        if (triggerAtTime < 0) {
            long who = Binder.getCallingUid();
            long what = Binder.getCallingPid();
            Slog.w(TAG, "Invalid alarm trigger time! " + triggerAtTime + " from uid=" + who + " pid=" + what);
            triggerAtTime = 0;
        }
        long nowElapsed = SystemClock.elapsedRealtime();
        long triggerElapsed = convertToElapsed(triggerAtTime, type);
        if (windowLength == 0) {
            maxElapsed = triggerElapsed;
        } else if (windowLength < 0) {
            maxElapsed = maxTriggerTime(nowElapsed, triggerElapsed, interval);
        } else {
            maxElapsed = triggerElapsed + windowLength;
        }
        synchronized (this.mLock) {
            setImplLocked(type, triggerAtTime, triggerElapsed, maxElapsed, interval, operation, isStandalone, true, workSource);
        }
    }

    private void setImplLocked(int type, long when, long whenElapsed, long maxWhen, long interval, PendingIntent operation, boolean isStandalone, boolean doValidate, WorkSource workSource) {
        boolean reschedule;
        Alarm a = new Alarm(type, when, whenElapsed, maxWhen, interval, operation, workSource);
        removeLocked(operation);
        int whichBatch = isStandalone ? -1 : attemptCoalesceLocked(whenElapsed, maxWhen);
        if (whichBatch < 0) {
            Batch batch = new Batch(a);
            batch.standalone = isStandalone;
            reschedule = addBatchLocked(this.mAlarmBatches, batch);
        } else {
            Batch batch2 = this.mAlarmBatches.get(whichBatch);
            reschedule = batch2.add(a);
            if (reschedule) {
                this.mAlarmBatches.remove(whichBatch);
                addBatchLocked(this.mAlarmBatches, batch2);
            }
        }
        if (reschedule) {
            rescheduleKernelAlarmsLocked();
        }
    }

    private void logBatchesLocked() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream(2048);
        PrintWriter pw = new PrintWriter(bs);
        long nowRTC = System.currentTimeMillis();
        long nowELAPSED = SystemClock.elapsedRealtime();
        int NZ = this.mAlarmBatches.size();
        for (int iz = 0; iz < NZ; iz++) {
            Batch bz = this.mAlarmBatches.get(iz);
            pw.append((CharSequence) "Batch ");
            pw.print(iz);
            pw.append((CharSequence) ": ");
            pw.println(bz);
            dumpAlarmList(pw, bz.alarms, "  ", nowELAPSED, nowRTC);
            pw.flush();
            Slog.v(TAG, bs.toString());
            bs.reset();
        }
    }

    private boolean validateConsistencyLocked() {
        return true;
    }

    private Batch findFirstWakeupBatchLocked() {
        int N = this.mAlarmBatches.size();
        for (int i = 0; i < N; i++) {
            Batch b = this.mAlarmBatches.get(i);
            if (b.hasWakeups()) {
                return b;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rescheduleKernelAlarmsLocked() {
        if (this.mAlarmBatches.size() > 0) {
            Batch firstWakeup = findFirstWakeupBatchLocked();
            Batch firstBatch = this.mAlarmBatches.get(0);
            if (firstWakeup != null && this.mNextWakeup != firstWakeup.start) {
                this.mNextWakeup = firstWakeup.start;
                setLocked(2, firstWakeup.start);
            }
            if (firstBatch != firstWakeup && this.mNextNonWakeup != firstBatch.start) {
                this.mNextNonWakeup = firstBatch.start;
                setLocked(3, firstBatch.start);
            }
        }
    }

    @Override // android.app.IAlarmManager
    public void setTime(long millis) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.SET_TIME, "setTime");
        SystemClock.setCurrentTimeMillis(millis);
    }

    @Override // android.app.IAlarmManager
    public void setTimeZone(String tz) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.SET_TIME_ZONE, "setTimeZone");
        long oldId = Binder.clearCallingIdentity();
        try {
            if (TextUtils.isEmpty(tz)) {
                return;
            }
            TimeZone zone = TimeZone.getTimeZone(tz);
            boolean timeZoneWasChanged = false;
            synchronized (this) {
                String current = SystemProperties.get(TIMEZONE_PROPERTY);
                if (current == null || !current.equals(zone.getID())) {
                    timeZoneWasChanged = true;
                    SystemProperties.set(TIMEZONE_PROPERTY, zone.getID());
                }
                int gmtOffset = zone.getOffset(System.currentTimeMillis());
                setKernelTimezone(this.mDescriptor, -(gmtOffset / 60000));
            }
            TimeZone.setDefault(null);
            if (timeZoneWasChanged) {
                Intent intent = new Intent(Intent.ACTION_TIMEZONE_CHANGED);
                intent.addFlags(536870912);
                intent.putExtra("time-zone", zone.getID());
                this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
            }
            Binder.restoreCallingIdentity(oldId);
        } finally {
            Binder.restoreCallingIdentity(oldId);
        }
    }

    @Override // android.app.IAlarmManager
    public void remove(PendingIntent operation) {
        if (operation == null) {
            return;
        }
        synchronized (this.mLock) {
            removeLocked(operation);
        }
    }

    public void removeLocked(PendingIntent operation) {
        boolean didRemove = false;
        for (int i = this.mAlarmBatches.size() - 1; i >= 0; i--) {
            Batch b = this.mAlarmBatches.get(i);
            didRemove |= b.remove(operation);
            if (b.size() == 0) {
                this.mAlarmBatches.remove(i);
            }
        }
        if (didRemove) {
            rebatchAllAlarmsLocked(true);
            rescheduleKernelAlarmsLocked();
        }
    }

    public void removeLocked(String packageName) {
        boolean didRemove = false;
        for (int i = this.mAlarmBatches.size() - 1; i >= 0; i--) {
            Batch b = this.mAlarmBatches.get(i);
            didRemove |= b.remove(packageName);
            if (b.size() == 0) {
                this.mAlarmBatches.remove(i);
            }
        }
        if (didRemove) {
            rebatchAllAlarmsLocked(true);
            rescheduleKernelAlarmsLocked();
        }
    }

    public void removeUserLocked(int userHandle) {
        boolean didRemove = false;
        for (int i = this.mAlarmBatches.size() - 1; i >= 0; i--) {
            Batch b = this.mAlarmBatches.get(i);
            didRemove |= b.remove(userHandle);
            if (b.size() == 0) {
                this.mAlarmBatches.remove(i);
            }
        }
        if (didRemove) {
            rebatchAllAlarmsLocked(true);
            rescheduleKernelAlarmsLocked();
        }
    }

    public boolean lookForPackageLocked(String packageName) {
        for (int i = 0; i < this.mAlarmBatches.size(); i++) {
            Batch b = this.mAlarmBatches.get(i);
            if (b.hasPackage(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void setLocked(int type, long when) {
        long alarmSeconds;
        long alarmNanoseconds;
        if (this.mDescriptor != -1) {
            if (when < 0) {
                alarmSeconds = 0;
                alarmNanoseconds = 0;
            } else {
                alarmSeconds = when / 1000;
                alarmNanoseconds = (when % 1000) * 1000 * 1000;
            }
            set(this.mDescriptor, type, alarmSeconds, alarmNanoseconds);
            return;
        }
        Message msg = Message.obtain();
        msg.what = 1;
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageAtTime(msg, when);
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump AlarmManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mLock) {
            pw.println("Current Alarm Manager state:");
            long nowRTC = System.currentTimeMillis();
            long nowELAPSED = SystemClock.elapsedRealtime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pw.print("nowRTC=");
            pw.print(nowRTC);
            pw.print(Separators.EQUALS);
            pw.print(sdf.format(new Date(nowRTC)));
            pw.print(" nowELAPSED=");
            pw.println(nowELAPSED);
            long nextWakeupRTC = this.mNextWakeup + (nowRTC - nowELAPSED);
            long nextNonWakeupRTC = this.mNextNonWakeup + (nowRTC - nowELAPSED);
            pw.print("Next alarm: ");
            pw.print(this.mNextNonWakeup);
            pw.print(" = ");
            pw.println(sdf.format(new Date(nextNonWakeupRTC)));
            pw.print("Next wakeup: ");
            pw.print(this.mNextWakeup);
            pw.print(" = ");
            pw.println(sdf.format(new Date(nextWakeupRTC)));
            if (this.mAlarmBatches.size() > 0) {
                pw.println();
                pw.print("Pending alarm batches: ");
                pw.println(this.mAlarmBatches.size());
                Iterator i$ = this.mAlarmBatches.iterator();
                while (i$.hasNext()) {
                    Batch b = i$.next();
                    pw.print(b);
                    pw.println(':');
                    dumpAlarmList(pw, b.alarms, "  ", nowELAPSED, nowRTC);
                }
            }
            pw.println();
            pw.print("  Broadcast ref count: ");
            pw.println(this.mBroadcastRefCount);
            pw.println();
            if (this.mLog.dump(pw, "  Recent problems", "    ")) {
                pw.println();
            }
            FilterStats[] topFilters = new FilterStats[10];
            Comparator<FilterStats> comparator = new Comparator<FilterStats>() { // from class: com.android.server.AlarmManagerService.1
                @Override // java.util.Comparator
                public int compare(FilterStats lhs, FilterStats rhs) {
                    if (lhs.aggregateTime < rhs.aggregateTime) {
                        return 1;
                    }
                    if (lhs.aggregateTime > rhs.aggregateTime) {
                        return -1;
                    }
                    return 0;
                }
            };
            int len = 0;
            for (Map.Entry<String, BroadcastStats> be : this.mBroadcastStats.entrySet()) {
                for (Map.Entry<Pair<String, ComponentName>, FilterStats> fe : be.getValue().filterStats.entrySet()) {
                    FilterStats fs = fe.getValue();
                    int pos = len > 0 ? Arrays.binarySearch(topFilters, 0, len, fs, comparator) : 0;
                    if (pos < 0) {
                        pos = (-pos) - 1;
                    }
                    if (pos < topFilters.length) {
                        int copylen = (topFilters.length - pos) - 1;
                        if (copylen > 0) {
                            System.arraycopy(topFilters, pos, topFilters, pos + 1, copylen);
                        }
                        topFilters[pos] = fs;
                        if (len < topFilters.length) {
                            len++;
                        }
                    }
                }
            }
            if (len > 0) {
                pw.println("  Top Alarms:");
                for (int i = 0; i < len; i++) {
                    FilterStats fs2 = topFilters[i];
                    pw.print("    ");
                    if (fs2.nesting > 0) {
                        pw.print("*ACTIVE* ");
                    }
                    TimeUtils.formatDuration(fs2.aggregateTime, pw);
                    pw.print(" running, ");
                    pw.print(fs2.numWakeup);
                    pw.print(" wakeups, ");
                    pw.print(fs2.count);
                    pw.print(" alarms: ");
                    pw.print(fs2.mBroadcastStats.mPackageName);
                    pw.println();
                    pw.print("      ");
                    if (fs2.mTarget.first != null) {
                        pw.print(" act=");
                        pw.print(fs2.mTarget.first);
                    }
                    if (fs2.mTarget.second != null) {
                        pw.print(" cmp=");
                        pw.print(fs2.mTarget.second.toShortString());
                    }
                    pw.println();
                }
            }
            pw.println(Separators.SP);
            pw.println("  Alarm Stats:");
            ArrayList<FilterStats> tmpFilters = new ArrayList<>();
            for (Map.Entry<String, BroadcastStats> be2 : this.mBroadcastStats.entrySet()) {
                BroadcastStats bs = be2.getValue();
                pw.print("  ");
                if (bs.nesting > 0) {
                    pw.print("*ACTIVE* ");
                }
                pw.print(be2.getKey());
                pw.print(Separators.SP);
                TimeUtils.formatDuration(bs.aggregateTime, pw);
                pw.print(" running, ");
                pw.print(bs.numWakeup);
                pw.println(" wakeups:");
                tmpFilters.clear();
                for (Map.Entry<Pair<String, ComponentName>, FilterStats> fe2 : bs.filterStats.entrySet()) {
                    tmpFilters.add(fe2.getValue());
                }
                Collections.sort(tmpFilters, comparator);
                for (int i2 = 0; i2 < tmpFilters.size(); i2++) {
                    FilterStats fs3 = tmpFilters.get(i2);
                    pw.print("    ");
                    if (fs3.nesting > 0) {
                        pw.print("*ACTIVE* ");
                    }
                    TimeUtils.formatDuration(fs3.aggregateTime, pw);
                    pw.print(Separators.SP);
                    pw.print(fs3.numWakeup);
                    pw.print(" wakes ");
                    pw.print(fs3.count);
                    pw.print(" alarms:");
                    if (fs3.mTarget.first != null) {
                        pw.print(" act=");
                        pw.print(fs3.mTarget.first);
                    }
                    if (fs3.mTarget.second != null) {
                        pw.print(" cmp=");
                        pw.print(fs3.mTarget.second.toShortString());
                    }
                    pw.println();
                }
            }
        }
    }

    private static final void dumpAlarmList(PrintWriter pw, ArrayList<Alarm> list, String prefix, String label, long now) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Alarm a = list.get(i);
            pw.print(prefix);
            pw.print(label);
            pw.print(" #");
            pw.print(i);
            pw.print(": ");
            pw.println(a);
            a.dump(pw, prefix + "  ", now);
        }
    }

    private static final String labelForType(int type) {
        switch (type) {
            case 0:
                return "RTC_WAKEUP";
            case 1:
                return "RTC";
            case 2:
                return "ELAPSED_WAKEUP";
            case 3:
                return "ELAPSED";
            default:
                return "--unknown--";
        }
    }

    private static final void dumpAlarmList(PrintWriter pw, ArrayList<Alarm> list, String prefix, long nowELAPSED, long nowRTC) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Alarm a = list.get(i);
            String label = labelForType(a.type);
            long now = a.type <= 1 ? nowRTC : nowELAPSED;
            pw.print(prefix);
            pw.print(label);
            pw.print(" #");
            pw.print(i);
            pw.print(": ");
            pw.println(a);
            a.dump(pw, prefix + "  ", now);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void triggerAlarmsLocked(ArrayList<Alarm> triggerList, long nowELAPSED, long nowRTC) {
        while (this.mAlarmBatches.size() > 0) {
            Batch batch = this.mAlarmBatches.get(0);
            if (batch.start <= nowELAPSED) {
                this.mAlarmBatches.remove(0);
                int N = batch.size();
                for (int i = 0; i < N; i++) {
                    Alarm alarm = batch.get(i);
                    alarm.count = 1;
                    triggerList.add(alarm);
                    if (alarm.repeatInterval > 0) {
                        alarm.count = (int) (alarm.count + ((nowELAPSED - alarm.whenElapsed) / alarm.repeatInterval));
                        long delta = alarm.count * alarm.repeatInterval;
                        long nextElapsed = alarm.whenElapsed + delta;
                        setImplLocked(alarm.type, alarm.when + delta, nextElapsed, maxTriggerTime(nowELAPSED, nextElapsed, alarm.repeatInterval), alarm.repeatInterval, alarm.operation, batch.standalone, true, alarm.workSource);
                    }
                }
            } else {
                return;
            }
        }
    }

    /* loaded from: AlarmManagerService$IncreasingTimeOrder.class */
    public static class IncreasingTimeOrder implements Comparator<Alarm> {
        @Override // java.util.Comparator
        public int compare(Alarm a1, Alarm a2) {
            long when1 = a1.when;
            long when2 = a2.when;
            if (when1 - when2 > 0) {
                return 1;
            }
            if (when1 - when2 < 0) {
                return -1;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AlarmManagerService$Alarm.class */
    public static class Alarm {
        public int type;
        public int count;
        public long when;
        public long whenElapsed;
        public long maxWhen;
        public long repeatInterval;
        public PendingIntent operation;
        public WorkSource workSource;

        public Alarm(int _type, long _when, long _whenElapsed, long _maxWhen, long _interval, PendingIntent _op, WorkSource _ws) {
            this.type = _type;
            this.when = _when;
            this.whenElapsed = _whenElapsed;
            this.maxWhen = _maxWhen;
            this.repeatInterval = _interval;
            this.operation = _op;
            this.workSource = _ws;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Alarm{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" type ");
            sb.append(this.type);
            sb.append(Separators.SP);
            sb.append(this.operation.getTargetPackage());
            sb.append('}');
            return sb.toString();
        }

        public void dump(PrintWriter pw, String prefix, long now) {
            pw.print(prefix);
            pw.print("type=");
            pw.print(this.type);
            pw.print(" whenElapsed=");
            pw.print(this.whenElapsed);
            pw.print(" when=");
            TimeUtils.formatDuration(this.when, now, pw);
            pw.print(" repeatInterval=");
            pw.print(this.repeatInterval);
            pw.print(" count=");
            pw.println(this.count);
            pw.print(prefix);
            pw.print("operation=");
            pw.println(this.operation);
        }
    }

    void recordWakeupAlarms(ArrayList<Batch> batches, long nowELAPSED, long nowRTC) {
        int numBatches = batches.size();
        for (int nextBatch = 0; nextBatch < numBatches; nextBatch++) {
            Batch b = batches.get(nextBatch);
            if (b.start <= nowELAPSED) {
                int numAlarms = b.alarms.size();
                for (int nextAlarm = 0; nextAlarm < numAlarms; nextAlarm++) {
                    Alarm a = b.alarms.get(nextAlarm);
                    WakeupEvent e = new WakeupEvent(nowRTC, a.operation.getCreatorUid(), a.operation.getIntent().getAction());
                    this.mRecentWakeups.add(e);
                }
            } else {
                return;
            }
        }
    }

    /* loaded from: AlarmManagerService$AlarmThread.class */
    private class AlarmThread extends Thread {
        public AlarmThread() {
            super(AlarmManagerService.TAG);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            ArrayList<Alarm> triggerList = new ArrayList<>();
            while (true) {
                int result = AlarmManagerService.this.waitForAlarm(AlarmManagerService.this.mDescriptor);
                triggerList.clear();
                if ((result & 65536) != 0) {
                    AlarmManagerService.this.remove(AlarmManagerService.this.mTimeTickSender);
                    AlarmManagerService.this.rebatchAllAlarms();
                    AlarmManagerService.this.mClockReceiver.scheduleTimeTickEvent();
                    Intent intent = new Intent(Intent.ACTION_TIME_CHANGED);
                    intent.addFlags(603979776);
                    AlarmManagerService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
                }
                synchronized (AlarmManagerService.this.mLock) {
                    long nowRTC = System.currentTimeMillis();
                    long nowELAPSED = SystemClock.elapsedRealtime();
                    AlarmManagerService.this.triggerAlarmsLocked(triggerList, nowELAPSED, nowRTC);
                    AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                    for (int i = 0; i < triggerList.size(); i++) {
                        Alarm alarm = triggerList.get(i);
                        try {
                            alarm.operation.send(AlarmManagerService.this.mContext, 0, AlarmManagerService.mBackgroundIntent.putExtra(Intent.EXTRA_ALARM_COUNT, alarm.count), AlarmManagerService.this.mResultReceiver, AlarmManagerService.this.mHandler);
                            if (AlarmManagerService.this.mBroadcastRefCount == 0) {
                                AlarmManagerService.this.setWakelockWorkSource(alarm.operation, alarm.workSource);
                                AlarmManagerService.this.mWakeLock.acquire();
                            }
                            InFlight inflight = new InFlight(AlarmManagerService.this, alarm.operation, alarm.workSource);
                            AlarmManagerService.this.mInFlight.add(inflight);
                            AlarmManagerService.access$1308(AlarmManagerService.this);
                            BroadcastStats bs = inflight.mBroadcastStats;
                            bs.count++;
                            if (bs.nesting == 0) {
                                bs.nesting = 1;
                                bs.startTime = nowELAPSED;
                            } else {
                                bs.nesting++;
                            }
                            FilterStats fs = inflight.mFilterStats;
                            fs.count++;
                            if (fs.nesting == 0) {
                                fs.nesting = 1;
                                fs.startTime = nowELAPSED;
                            } else {
                                fs.nesting++;
                            }
                            if (alarm.type == 2 || alarm.type == 0) {
                                bs.numWakeup++;
                                fs.numWakeup++;
                                ActivityManagerNative.noteWakeupAlarm(alarm.operation);
                            }
                        } catch (PendingIntent.CanceledException e) {
                            if (alarm.repeatInterval > 0) {
                                AlarmManagerService.this.remove(alarm.operation);
                            }
                        } catch (RuntimeException e2) {
                            Slog.w(AlarmManagerService.TAG, "Failure sending alarm.", e2);
                        }
                    }
                }
            }
        }
    }

    void setWakelockWorkSource(PendingIntent pi, WorkSource ws) {
        if (ws != null) {
            this.mWakeLock.setWorkSource(ws);
            return;
        }
        int uid = ActivityManagerNative.getDefault().getUidForIntentSender(pi.getTarget());
        if (uid >= 0) {
            this.mWakeLock.setWorkSource(new WorkSource(uid));
            return;
        }
        this.mWakeLock.setWorkSource(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: AlarmManagerService$AlarmHandler.class */
    public class AlarmHandler extends Handler {
        public static final int ALARM_EVENT = 1;
        public static final int MINUTE_CHANGE_EVENT = 2;
        public static final int DATE_CHANGE_EVENT = 3;

        public AlarmHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ArrayList<Alarm> triggerList = new ArrayList<>();
                synchronized (AlarmManagerService.this.mLock) {
                    long nowRTC = System.currentTimeMillis();
                    long nowELAPSED = SystemClock.elapsedRealtime();
                    AlarmManagerService.this.triggerAlarmsLocked(triggerList, nowELAPSED, nowRTC);
                }
                for (int i = 0; i < triggerList.size(); i++) {
                    Alarm alarm = triggerList.get(i);
                    try {
                        alarm.operation.send();
                    } catch (PendingIntent.CanceledException e) {
                        if (alarm.repeatInterval > 0) {
                            AlarmManagerService.this.remove(alarm.operation);
                        }
                    }
                }
            }
        }
    }

    /* loaded from: AlarmManagerService$ClockReceiver.class */
    class ClockReceiver extends BroadcastReceiver {
        public ClockReceiver() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_DATE_CHANGED);
            AlarmManagerService.this.mContext.registerReceiver(this, filter);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                scheduleTimeTickEvent();
            } else if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
                TimeZone zone = TimeZone.getTimeZone(SystemProperties.get(AlarmManagerService.TIMEZONE_PROPERTY));
                int gmtOffset = zone.getOffset(System.currentTimeMillis());
                AlarmManagerService.this.setKernelTimezone(AlarmManagerService.this.mDescriptor, -(gmtOffset / 60000));
                scheduleDateChangedEvent();
            }
        }

        public void scheduleTimeTickEvent() {
            long currentTime = System.currentTimeMillis();
            long nextTime = DateUtils.MINUTE_IN_MILLIS * ((currentTime / DateUtils.MINUTE_IN_MILLIS) + 1);
            long tickEventDelay = nextTime - currentTime;
            AlarmManagerService.this.set(3, SystemClock.elapsedRealtime() + tickEventDelay, 0L, 0L, AlarmManagerService.this.mTimeTickSender, true, null);
        }

        public void scheduleDateChangedEvent() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(10, 0);
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.add(5, 1);
            AlarmManagerService.this.set(1, calendar.getTimeInMillis(), 0L, 0L, AlarmManagerService.this.mDateChangeSender, true, null);
        }
    }

    /* loaded from: AlarmManagerService$UninstallReceiver.class */
    class UninstallReceiver extends BroadcastReceiver {
        public UninstallReceiver() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
            filter.addAction(Intent.ACTION_QUERY_PACKAGE_RESTART);
            filter.addDataScheme(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME);
            AlarmManagerService.this.mContext.registerReceiver(this, filter);
            IntentFilter sdFilter = new IntentFilter();
            sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
            sdFilter.addAction(Intent.ACTION_USER_STOPPED);
            AlarmManagerService.this.mContext.registerReceiver(this, sdFilter);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String pkg;
            String[] pkgList;
            synchronized (AlarmManagerService.this.mLock) {
                String action = intent.getAction();
                String[] pkgList2 = null;
                if (Intent.ACTION_QUERY_PACKAGE_RESTART.equals(action)) {
                    for (String packageName : intent.getStringArrayExtra(Intent.EXTRA_PACKAGES)) {
                        if (AlarmManagerService.this.lookForPackageLocked(packageName)) {
                            setResultCode(-1);
                            return;
                        }
                    }
                    return;
                }
                if ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
                    pkgList2 = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                } else if (Intent.ACTION_USER_STOPPED.equals(action)) {
                    int userHandle = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
                    if (userHandle >= 0) {
                        AlarmManagerService.this.removeUserLocked(userHandle);
                    }
                } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action) && intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    return;
                } else {
                    Uri data = intent.getData();
                    if (data != null && (pkg = data.getSchemeSpecificPart()) != null) {
                        pkgList2 = new String[]{pkg};
                    }
                }
                if (pkgList2 != null && pkgList2.length > 0) {
                    String[] arr$ = pkgList2;
                    for (String pkg2 : arr$) {
                        AlarmManagerService.this.removeLocked(pkg2);
                        AlarmManagerService.this.mBroadcastStats.remove(pkg2);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final BroadcastStats getStatsLocked(PendingIntent pi) {
        String pkg = pi.getTargetPackage();
        BroadcastStats bs = this.mBroadcastStats.get(pkg);
        if (bs == null) {
            bs = new BroadcastStats(pkg);
            this.mBroadcastStats.put(pkg, bs);
        }
        return bs;
    }

    /* loaded from: AlarmManagerService$ResultReceiver.class */
    class ResultReceiver implements PendingIntent.OnFinished {
        ResultReceiver() {
        }

        @Override // android.app.PendingIntent.OnFinished
        public void onSendFinished(PendingIntent pi, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
            synchronized (AlarmManagerService.this.mLock) {
                InFlight inflight = null;
                int i = 0;
                while (true) {
                    if (i >= AlarmManagerService.this.mInFlight.size()) {
                        break;
                    } else if (((InFlight) AlarmManagerService.this.mInFlight.get(i)).mPendingIntent == pi) {
                        inflight = (InFlight) AlarmManagerService.this.mInFlight.remove(i);
                        break;
                    } else {
                        i++;
                    }
                }
                if (inflight == null) {
                    AlarmManagerService.this.mLog.w("No in-flight alarm for " + pi + Separators.SP + intent);
                } else {
                    long nowELAPSED = SystemClock.elapsedRealtime();
                    BroadcastStats bs = inflight.mBroadcastStats;
                    bs.nesting--;
                    if (bs.nesting <= 0) {
                        bs.nesting = 0;
                        bs.aggregateTime += nowELAPSED - bs.startTime;
                    }
                    FilterStats fs = inflight.mFilterStats;
                    fs.nesting--;
                    if (fs.nesting <= 0) {
                        fs.nesting = 0;
                        fs.aggregateTime += nowELAPSED - fs.startTime;
                    }
                }
                AlarmManagerService.access$1310(AlarmManagerService.this);
                if (AlarmManagerService.this.mBroadcastRefCount == 0) {
                    AlarmManagerService.this.mWakeLock.release();
                    if (AlarmManagerService.this.mInFlight.size() > 0) {
                        AlarmManagerService.this.mLog.w("Finished all broadcasts with " + AlarmManagerService.this.mInFlight.size() + " remaining inflights");
                        for (int i2 = 0; i2 < AlarmManagerService.this.mInFlight.size(); i2++) {
                            AlarmManagerService.this.mLog.w("  Remaining #" + i2 + ": " + AlarmManagerService.this.mInFlight.get(i2));
                        }
                        AlarmManagerService.this.mInFlight.clear();
                    }
                } else if (AlarmManagerService.this.mInFlight.size() > 0) {
                    InFlight inFlight = (InFlight) AlarmManagerService.this.mInFlight.get(0);
                    AlarmManagerService.this.setWakelockWorkSource(inFlight.mPendingIntent, inFlight.mWorkSource);
                } else {
                    AlarmManagerService.this.mLog.w("Alarm wakelock still held but sent queue empty");
                    AlarmManagerService.this.mWakeLock.setWorkSource(null);
                }
            }
        }
    }
}