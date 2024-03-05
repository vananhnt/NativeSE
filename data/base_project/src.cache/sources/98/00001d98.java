package com.android.server.am;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.IActivityController;
import android.app.IThumbnailReceiver;
import android.app.ResultInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.text.format.DateUtils;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.util.Objects;
import com.android.server.Watchdog;
import com.android.server.wm.AppTransition;
import com.android.server.wm.TaskGroup;
import com.android.server.wm.WindowManagerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ActivityStack.class */
public final class ActivityStack {
    static final int LAUNCH_TICK = 500;
    static final int PAUSE_TIMEOUT = 500;
    static final int STOP_TIMEOUT = 10000;
    static final int DESTROY_TIMEOUT = 10000;
    static final long ACTIVITY_INACTIVE_RESET_TIME = 0;
    static final long START_WARN_TIME = 5000;
    static final boolean SHOW_APP_STARTING_PREVIEW = true;
    static final long TRANSLUCENT_CONVERSION_TIMEOUT = 2000;
    static final boolean SCREENSHOT_FORCE_565;
    final ActivityManagerService mService;
    final WindowManagerService mWindowManager;
    final Context mContext;
    boolean mConfigWillChange;
    int mCurrentUser;
    final int mStackId;
    final ActivityStackSupervisor mStackSupervisor;
    static final int PAUSE_TIMEOUT_MSG = 101;
    static final int DESTROY_TIMEOUT_MSG = 102;
    static final int LAUNCH_TICK_MSG = 103;
    static final int STOP_TIMEOUT_MSG = 104;
    static final int DESTROY_ACTIVITIES_MSG = 105;
    static final int TRANSLUCENT_TIMEOUT_MSG = 106;
    final Handler mHandler;
    static final int FINISH_IMMEDIATELY = 0;
    static final int FINISH_AFTER_PAUSE = 1;
    static final int FINISH_AFTER_VISIBLE = 2;
    private ArrayList<TaskRecord> mTaskHistory = new ArrayList<>();
    final ArrayList<TaskGroup> mValidateAppTokens = new ArrayList<>();
    final ArrayList<ActivityRecord> mLRUActivities = new ArrayList<>();
    final ArrayList<ActivityRecord> mNoAnimActivities = new ArrayList<>();
    ActivityRecord mPausingActivity = null;
    ActivityRecord mLastPausedActivity = null;
    ActivityRecord mLastNoHistoryActivity = null;
    ActivityRecord mResumedActivity = null;
    ActivityRecord mLastStartedActivity = null;
    ActivityRecord mTranslucentActivityWaiting = null;
    ArrayList<ActivityRecord> mUndrawnActivitiesBelowTopTranslucent = new ArrayList<>();
    long mLaunchStartTime = 0;
    long mFullyDrawnStartTime = 0;
    private ActivityRecord mLastScreenshotActivity = null;
    private Bitmap mLastScreenshotBitmap = null;
    int mThumbnailWidth = -1;
    int mThumbnailHeight = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityStack$ActivityState.class */
    public enum ActivityState {
        INITIALIZING,
        RESUMED,
        PAUSING,
        PAUSED,
        STOPPING,
        STOPPED,
        FINISHING,
        DESTROYING,
        DESTROYED
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActivityStack.activityDestroyedLocked(android.os.IBinder):void, file: ActivityStack.class
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
    final void activityDestroyedLocked(android.os.IBinder r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActivityStack.activityDestroyedLocked(android.os.IBinder):void, file: ActivityStack.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityStack.activityDestroyedLocked(android.os.IBinder):void");
    }

    static {
        SCREENSHOT_FORCE_565 = ActivityManager.isLowRamDeviceStatic();
    }

    /* loaded from: ActivityStack$ScheduleDestroyArgs.class */
    static class ScheduleDestroyArgs {
        final ProcessRecord mOwner;
        final boolean mOomAdj;
        final String mReason;

        ScheduleDestroyArgs(ProcessRecord owner, boolean oomAdj, String reason) {
            this.mOwner = owner;
            this.mOomAdj = oomAdj;
            this.mReason = reason;
        }
    }

    /* loaded from: ActivityStack$ActivityStackHandler.class */
    final class ActivityStackHandler extends Handler {
        ActivityStackHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    ActivityRecord r = (ActivityRecord) msg.obj;
                    Slog.w("ActivityManager", "Activity pause timeout for " + r);
                    synchronized (ActivityStack.this.mService) {
                        if (r.app != null) {
                            ActivityStack.this.mService.logAppTooSlow(r.app, r.pauseTime, "pausing " + r);
                        }
                        ActivityStack.this.activityPausedLocked(r.appToken, true);
                    }
                    return;
                case 102:
                    ActivityRecord r2 = (ActivityRecord) msg.obj;
                    Slog.w("ActivityManager", "Activity destroy timeout for " + r2);
                    synchronized (ActivityStack.this.mService) {
                        ActivityStack.this.activityDestroyedLocked(r2 != null ? r2.appToken : null);
                    }
                    return;
                case 103:
                    ActivityRecord r3 = (ActivityRecord) msg.obj;
                    synchronized (ActivityStack.this.mService) {
                        if (r3.continueLaunchTickingLocked()) {
                            ActivityStack.this.mService.logAppTooSlow(r3.app, r3.launchTickTime, "launching " + r3);
                        }
                    }
                    return;
                case 104:
                    ActivityRecord r4 = (ActivityRecord) msg.obj;
                    Slog.w("ActivityManager", "Activity stop timeout for " + r4);
                    synchronized (ActivityStack.this.mService) {
                        if (r4.isInHistory()) {
                            ActivityStack.this.activityStoppedLocked(r4, null, null, null);
                        }
                    }
                    return;
                case 105:
                    ScheduleDestroyArgs args = (ScheduleDestroyArgs) msg.obj;
                    synchronized (ActivityStack.this.mService) {
                        ActivityStack.this.destroyActivitiesLocked(args.mOwner, args.mOomAdj, args.mReason);
                    }
                    return;
                case 106:
                    synchronized (ActivityStack.this.mService) {
                        ActivityStack.this.notifyActivityDrawnLocked(null);
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private int numActivities() {
        int count = 0;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            count += this.mTaskHistory.get(taskNdx).mActivities.size();
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityStack(ActivityManagerService service, Context context, Looper looper, int stackId) {
        this.mHandler = new ActivityStackHandler(looper);
        this.mService = service;
        this.mWindowManager = service.mWindowManager;
        this.mStackSupervisor = service.mStackSupervisor;
        this.mContext = context;
        this.mStackId = stackId;
        this.mCurrentUser = service.mCurrentUserId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean okToShow(ActivityRecord r) {
        return r.userId == this.mCurrentUser || (r.info.flags & 1024) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActivityRecord topRunningActivityLocked(ActivityRecord notTop) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ActivityRecord r = this.mTaskHistory.get(taskNdx).topRunningActivityLocked(notTop);
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActivityRecord topRunningNonDelayedActivityLocked(ActivityRecord notTop) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            ArrayList<ActivityRecord> activities = task.mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (!r.finishing && !r.delayedResume && r != notTop && okToShow(r)) {
                    return r;
                }
            }
        }
        return null;
    }

    final ActivityRecord topRunningActivityLocked(IBinder token, int taskId) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.taskId != taskId) {
                ArrayList<ActivityRecord> activities = task.mActivities;
                for (int i = activities.size() - 1; i >= 0; i--) {
                    ActivityRecord r = activities.get(i);
                    if (!r.finishing && token != r.appToken && okToShow(r)) {
                        return r;
                    }
                }
                continue;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActivityRecord topActivity() {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            int activityNdx = activities.size() - 1;
            if (activityNdx >= 0) {
                return activities.get(activityNdx);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TaskRecord topTask() {
        int size = this.mTaskHistory.size();
        if (size > 0) {
            return this.mTaskHistory.get(size - 1);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskRecord taskForIdLocked(int id) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.taskId == id) {
                return task;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityRecord isInStackLocked(IBinder token) {
        ActivityRecord r = ActivityRecord.forToken(token);
        if (r != null) {
            TaskRecord task = r.task;
            if (task.mActivities.contains(r) && this.mTaskHistory.contains(task)) {
                if (task.stack != this) {
                    Slog.w("ActivityManager", "Illegal state! task does not point to stack it is in.");
                }
                return r;
            }
            return null;
        }
        return null;
    }

    boolean containsApp(ProcessRecord app) {
        if (app == null) {
            return false;
        }
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (!r.finishing && r.app == app) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean updateLRUListLocked(ActivityRecord r) {
        boolean hadit = this.mLRUActivities.remove(r);
        this.mLRUActivities.add(r);
        return hadit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isHomeStack() {
        return this.mStackId == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityRecord findTaskLocked(ActivityRecord target) {
        ActivityRecord r;
        Intent intent = target.intent;
        ActivityInfo info = target.info;
        ComponentName cls = intent.getComponent();
        if (info.targetActivity != null) {
            cls = new ComponentName(info.packageName, info.targetActivity);
        }
        int userId = UserHandle.getUserId(info.applicationInfo.uid);
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.userId == userId && (r = task.getTopActivity()) != null && !r.finishing && r.userId == userId && r.launchMode != 3) {
                if (task.affinity != null) {
                    if (task.affinity.equals(info.taskAffinity)) {
                        return r;
                    }
                } else if (task.intent != null && task.intent.getComponent().equals(cls)) {
                    return r;
                } else {
                    if (task.affinityIntent != null && task.affinityIntent.getComponent().equals(cls)) {
                        return r;
                    }
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityRecord findActivityLocked(Intent intent, ActivityInfo info) {
        ComponentName cls = intent.getComponent();
        if (info.targetActivity != null) {
            cls = new ComponentName(info.packageName, info.targetActivity);
        }
        int userId = UserHandle.getUserId(info.applicationInfo.uid);
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.userId != this.mCurrentUser) {
                return null;
            }
            ArrayList<ActivityRecord> activities = task.mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (!r.finishing && r.intent.getComponent().equals(cls) && r.userId == userId) {
                    return r;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void switchUserLocked(int userId) {
        if (this.mCurrentUser == userId) {
            return;
        }
        this.mCurrentUser = userId;
        int index = this.mTaskHistory.size();
        for (int i = 0; i < index; i++) {
            TaskRecord task = this.mTaskHistory.get(i);
            if (task.userId == userId) {
                this.mTaskHistory.remove(i);
                this.mTaskHistory.add(task);
                index--;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void minimalResumeActivityLocked(ActivityRecord r) {
        r.state = ActivityState.RESUMED;
        r.stopped = false;
        this.mResumedActivity = r;
        r.task.touchActiveTime();
        this.mService.addRecentTaskLocked(r.task);
        completeResumeLocked(r);
        this.mStackSupervisor.checkReadyForSleepLocked();
        setLaunchTime(r);
    }

    private void startLaunchTraces() {
        if (this.mFullyDrawnStartTime != 0) {
            Trace.asyncTraceEnd(64L, "drawing", 0);
        }
        Trace.asyncTraceBegin(64L, "launching", 0);
        Trace.asyncTraceBegin(64L, "drawing", 0);
    }

    private void stopFullyDrawnTraceIfNeeded() {
        if (this.mFullyDrawnStartTime != 0 && this.mLaunchStartTime == 0) {
            Trace.asyncTraceEnd(64L, "drawing", 0);
            this.mFullyDrawnStartTime = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLaunchTime(ActivityRecord r) {
        if (r.displayStartTime == 0) {
            long uptimeMillis = SystemClock.uptimeMillis();
            r.displayStartTime = uptimeMillis;
            r.fullyDrawnStartTime = uptimeMillis;
            if (this.mLaunchStartTime == 0) {
                startLaunchTraces();
                long j = r.displayStartTime;
                this.mFullyDrawnStartTime = j;
                this.mLaunchStartTime = j;
            }
        } else if (this.mLaunchStartTime == 0) {
            startLaunchTraces();
            long uptimeMillis2 = SystemClock.uptimeMillis();
            this.mFullyDrawnStartTime = uptimeMillis2;
            this.mLaunchStartTime = uptimeMillis2;
        }
    }

    void clearLaunchTime(ActivityRecord r) {
        if (this.mStackSupervisor.mWaitingActivityLaunched.isEmpty()) {
            r.fullyDrawnStartTime = 0L;
            r.displayStartTime = 0L;
            return;
        }
        this.mStackSupervisor.removeTimeoutsForActivityLocked(r);
        this.mStackSupervisor.scheduleIdleTimeoutLocked(r);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void awakeFromSleepingLocked() {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                activities.get(activityNdx).setSleeping(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean checkReadyForSleepLocked() {
        if (this.mResumedActivity != null) {
            startPausingLocked(false, true);
            return true;
        } else if (this.mPausingActivity != null) {
            return true;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void goToSleep() {
        ensureActivitiesVisibleLocked(null, 0);
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.state == ActivityState.STOPPING || r.state == ActivityState.STOPPED) {
                    r.setSleeping(true);
                }
            }
        }
    }

    public final Bitmap screenshotActivities(ActivityRecord who) {
        if (who.noDisplay) {
            return null;
        }
        TaskRecord tr = who.task;
        if (this.mService.getMostRecentTask() != tr && tr.intent != null && (tr.intent.getFlags() & 8388608) != 0) {
            return null;
        }
        Resources res = this.mService.mContext.getResources();
        int w = this.mThumbnailWidth;
        int h = this.mThumbnailHeight;
        if (w < 0) {
            int dimensionPixelSize = res.getDimensionPixelSize(17104898);
            w = dimensionPixelSize;
            this.mThumbnailWidth = dimensionPixelSize;
            int dimensionPixelSize2 = res.getDimensionPixelSize(17104897);
            h = dimensionPixelSize2;
            this.mThumbnailHeight = dimensionPixelSize2;
        }
        if (w > 0) {
            if (who != this.mLastScreenshotActivity || this.mLastScreenshotBitmap == null || this.mLastScreenshotActivity.state == ActivityState.RESUMED || this.mLastScreenshotBitmap.getWidth() != w || this.mLastScreenshotBitmap.getHeight() != h) {
                this.mLastScreenshotActivity = who;
                this.mLastScreenshotBitmap = this.mWindowManager.screenshotApplications(who.appToken, 0, w, h, SCREENSHOT_FORCE_565);
            }
            if (this.mLastScreenshotBitmap != null) {
                return this.mLastScreenshotBitmap.copy(this.mLastScreenshotBitmap.getConfig(), true);
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void startPausingLocked(boolean userLeaving, boolean uiSleeping) {
        if (this.mPausingActivity != null) {
            Slog.e("ActivityManager", "Trying to pause when pause is already pending for " + this.mPausingActivity, new RuntimeException("here").fillInStackTrace());
        }
        ActivityRecord prev = this.mResumedActivity;
        if (prev == null) {
            Slog.e("ActivityManager", "Trying to pause when nothing is resumed", new RuntimeException("here").fillInStackTrace());
            this.mStackSupervisor.resumeTopActivitiesLocked();
            return;
        }
        this.mResumedActivity = null;
        this.mPausingActivity = prev;
        this.mLastPausedActivity = prev;
        this.mLastNoHistoryActivity = ((prev.intent.getFlags() & 1073741824) == 0 && (prev.info.flags & 128) == 0) ? null : prev;
        prev.state = ActivityState.PAUSING;
        prev.task.touchActiveTime();
        clearLaunchTime(prev);
        prev.updateThumbnail(screenshotActivities(prev), null);
        stopFullyDrawnTraceIfNeeded();
        this.mService.updateCpuStats();
        if (prev.app != null && prev.app.thread != null) {
            try {
                EventLog.writeEvent((int) EventLogTags.AM_PAUSE_ACTIVITY, Integer.valueOf(prev.userId), Integer.valueOf(System.identityHashCode(prev)), prev.shortComponentName);
                this.mService.updateUsageStats(prev, false);
                prev.app.thread.schedulePauseActivity(prev.appToken, prev.finishing, userLeaving, prev.configChangeFlags);
            } catch (Exception e) {
                Slog.w("ActivityManager", "Exception thrown during pause", e);
                this.mPausingActivity = null;
                this.mLastPausedActivity = null;
                this.mLastNoHistoryActivity = null;
            }
        } else {
            this.mPausingActivity = null;
            this.mLastPausedActivity = null;
            this.mLastNoHistoryActivity = null;
        }
        if (!this.mService.isSleepingOrShuttingDown()) {
            this.mStackSupervisor.acquireLaunchWakelock();
        }
        if (this.mPausingActivity != null) {
            if (!uiSleeping) {
                prev.pauseKeyDispatchingLocked();
            }
            Message msg = this.mHandler.obtainMessage(101);
            msg.obj = prev;
            prev.pauseTime = SystemClock.uptimeMillis();
            this.mHandler.sendMessageDelayed(msg, 500L);
            return;
        }
        this.mStackSupervisor.getFocusedStack().resumeTopActivityLocked(null);
    }

    final void activityPausedLocked(IBinder token, boolean timeout) {
        ActivityRecord r = isInStackLocked(token);
        if (r != null) {
            this.mHandler.removeMessages(101, r);
            if (this.mPausingActivity == r) {
                r.state = ActivityState.PAUSED;
                completePauseLocked();
                return;
            }
            Object[] objArr = new Object[4];
            objArr[0] = Integer.valueOf(r.userId);
            objArr[1] = Integer.valueOf(System.identityHashCode(r));
            objArr[2] = r.shortComponentName;
            objArr[3] = this.mPausingActivity != null ? this.mPausingActivity.shortComponentName : "(none)";
            EventLog.writeEvent((int) EventLogTags.AM_FAILED_TO_PAUSE, objArr);
        }
    }

    final void activityStoppedLocked(ActivityRecord r, Bundle icicle, Bitmap thumbnail, CharSequence description) {
        if (r.state != ActivityState.STOPPING) {
            Slog.i("ActivityManager", "Activity reported stop, but no longer stopping: " + r);
            this.mHandler.removeMessages(104, r);
            return;
        }
        if (icicle != null) {
            r.icicle = icicle;
            r.haveState = true;
            r.launchCount = 0;
            r.updateThumbnail(thumbnail, description);
        }
        if (!r.stopped) {
            this.mHandler.removeMessages(104, r);
            r.stopped = true;
            r.state = ActivityState.STOPPED;
            if (r.finishing) {
                r.clearOptionsLocked();
            } else if (r.configDestroy) {
                destroyActivityLocked(r, true, false, "stop-config");
                this.mStackSupervisor.resumeTopActivitiesLocked();
            } else {
                this.mStackSupervisor.updatePreviousProcessLocked(r);
            }
        }
    }

    private void completePauseLocked() {
        long diff;
        ActivityRecord prev = this.mPausingActivity;
        if (prev != null) {
            if (prev.finishing) {
                prev = finishCurrentActivityLocked(prev, 2, false);
            } else if (prev.app != null) {
                if (prev.waitingVisible) {
                    prev.waitingVisible = false;
                    this.mStackSupervisor.mWaitingVisibleActivities.remove(prev);
                }
                if (prev.configDestroy) {
                    destroyActivityLocked(prev, true, false, "pause-config");
                } else {
                    this.mStackSupervisor.mStoppingActivities.add(prev);
                    if (this.mStackSupervisor.mStoppingActivities.size() > 3 || (prev.frontOfTask && this.mTaskHistory.size() <= 1)) {
                        this.mStackSupervisor.scheduleIdleLocked();
                    } else {
                        this.mStackSupervisor.checkReadyForSleepLocked();
                    }
                }
            } else {
                prev = null;
            }
            this.mPausingActivity = null;
        }
        ActivityStack topStack = this.mStackSupervisor.getFocusedStack();
        if (!this.mService.isSleepingOrShuttingDown()) {
            this.mStackSupervisor.resumeTopActivitiesLocked(topStack, prev, null);
        } else {
            this.mStackSupervisor.checkReadyForSleepLocked();
            ActivityRecord top = topStack.topRunningActivityLocked(null);
            if (top == null || (prev != null && top != prev)) {
                this.mStackSupervisor.resumeTopActivitiesLocked(topStack, null, null);
            }
        }
        if (prev != null) {
            prev.resumeKeyDispatchingLocked();
            if (prev.app != null && prev.cpuTimeAtResume > 0 && this.mService.mBatteryStatsService.isOnBattery()) {
                synchronized (this.mService.mProcessCpuThread) {
                    diff = this.mService.mProcessCpuTracker.getCpuTimeForPid(prev.app.pid) - prev.cpuTimeAtResume;
                }
                if (diff > 0) {
                    BatteryStatsImpl bsi = this.mService.mBatteryStatsService.getActiveStatistics();
                    synchronized (bsi) {
                        BatteryStatsImpl.Uid.Proc ps = bsi.getProcessStatsLocked(prev.info.applicationInfo.uid, prev.info.packageName);
                        if (ps != null) {
                            ps.addForegroundTimeLocked(diff);
                        }
                    }
                }
            }
            prev.cpuTimeAtResume = 0L;
        }
    }

    private void completeResumeLocked(ActivityRecord next) {
        next.idle = false;
        next.results = null;
        next.newIntents = null;
        if (next.nowVisible) {
            this.mStackSupervisor.dismissKeyguard();
        }
        this.mStackSupervisor.scheduleIdleTimeoutLocked(next);
        this.mStackSupervisor.reportResumedActivityLocked(next);
        next.resumeKeyDispatchingLocked();
        this.mNoAnimActivities.clear();
        if (next.app != null) {
            synchronized (this.mService.mProcessCpuThread) {
                next.cpuTimeAtResume = this.mService.mProcessCpuTracker.getCpuTimeForPid(next.app.pid);
            }
            return;
        }
        next.cpuTimeAtResume = 0L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean ensureActivitiesVisibleLocked(ActivityRecord starting, int configChanges) {
        return ensureActivitiesVisibleLocked(starting, configChanges, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean ensureActivitiesVisibleLocked(ActivityRecord starting, int configChanges, boolean forceHomeShown) {
        ActivityRecord r = topRunningActivityLocked(null);
        return r != null && ensureActivitiesVisibleLocked(r, starting, null, configChanges, forceHomeShown);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    final boolean ensureActivitiesVisibleLocked(ActivityRecord top, ActivityRecord starting, String onlyThisProcess, int configChanges, boolean forceHomeShown) {
        if (this.mTranslucentActivityWaiting != top) {
            this.mUndrawnActivitiesBelowTopTranslucent.clear();
            if (this.mTranslucentActivityWaiting != null) {
                notifyActivityDrawnLocked(null);
                this.mTranslucentActivityWaiting = null;
            }
            this.mHandler.removeMessages(106);
        }
        boolean aboveTop = true;
        boolean showHomeBehindStack = false;
        boolean behindFullscreen = (this.mStackSupervisor.isFrontStack(this) || (forceHomeShown && isHomeStack())) ? false : true;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            ArrayList<ActivityRecord> activities = task.mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (!r.finishing && (!aboveTop || r == top)) {
                    aboveTop = false;
                    if (!behindFullscreen) {
                        boolean doThisProcess = onlyThisProcess == null || onlyThisProcess.equals(r.processName);
                        if (r != starting && doThisProcess) {
                            ensureActivityConfigurationLocked(r, 0);
                        }
                        if (r.app == null || r.app.thread == null) {
                            if (onlyThisProcess == null || onlyThisProcess.equals(r.processName)) {
                                if (r != starting) {
                                    r.startFreezingScreenLocked(r.app, configChanges);
                                }
                                if (!r.visible) {
                                    this.mWindowManager.setAppVisibility(r.appToken, true);
                                }
                                if (r != starting) {
                                    this.mStackSupervisor.startSpecificActivityLocked(r, false, false);
                                }
                            }
                        } else if (r.visible) {
                            r.stopFreezingScreenLocked(false);
                        } else if (onlyThisProcess == null) {
                            r.visible = true;
                            if (r.state != ActivityState.RESUMED && r != starting) {
                                try {
                                    if (this.mTranslucentActivityWaiting != null) {
                                        this.mUndrawnActivitiesBelowTopTranslucent.add(r);
                                    }
                                    this.mWindowManager.setAppVisibility(r.appToken, true);
                                    r.sleeping = false;
                                    r.app.pendingUiClean = true;
                                    r.app.thread.scheduleWindowVisibility(r.appToken, true);
                                    r.stopFreezingScreenLocked(false);
                                } catch (Exception e) {
                                    Slog.w("ActivityManager", "Exception thrown making visibile: " + r.intent.getComponent(), e);
                                }
                            }
                        }
                        configChanges |= r.configChangeFlags;
                        if (r.fullscreen) {
                            behindFullscreen = true;
                        } else if (task.mOnTopOfHome) {
                            int rIndex = task.mActivities.indexOf(r);
                            while (true) {
                                rIndex--;
                                if (rIndex >= 0) {
                                    ActivityRecord blocker = task.mActivities.get(rIndex);
                                    if (!blocker.finishing) {
                                    }
                                }
                            }
                            if (rIndex < 0) {
                                showHomeBehindStack = true;
                                behindFullscreen = true;
                            }
                        }
                    } else if (r.visible) {
                        r.visible = false;
                        try {
                            this.mWindowManager.setAppVisibility(r.appToken, false);
                            switch (r.state) {
                                case STOPPING:
                                case STOPPED:
                                    if (r.app != null && r.app.thread != null) {
                                        r.app.thread.scheduleWindowVisibility(r.appToken, false);
                                    }
                                    break;
                                case INITIALIZING:
                                case RESUMED:
                                case PAUSING:
                                case PAUSED:
                                    if (!this.mStackSupervisor.mStoppingActivities.contains(r)) {
                                        this.mStackSupervisor.mStoppingActivities.add(r);
                                    }
                                    this.mStackSupervisor.scheduleIdleLocked();
                                    break;
                            }
                        } catch (Exception e2) {
                            Slog.w("ActivityManager", "Exception thrown making hidden: " + r.intent.getComponent(), e2);
                        }
                    }
                }
            }
        }
        return showHomeBehindStack;
    }

    void convertToTranslucent(ActivityRecord r) {
        this.mTranslucentActivityWaiting = r;
        this.mUndrawnActivitiesBelowTopTranslucent.clear();
        this.mHandler.sendEmptyMessageDelayed(106, TRANSLUCENT_CONVERSION_TIMEOUT);
    }

    void notifyActivityDrawnLocked(ActivityRecord r) {
        if (r == null || (this.mUndrawnActivitiesBelowTopTranslucent.remove(r) && this.mUndrawnActivitiesBelowTopTranslucent.isEmpty())) {
            ActivityRecord waitingActivity = this.mTranslucentActivityWaiting;
            this.mTranslucentActivityWaiting = null;
            this.mUndrawnActivitiesBelowTopTranslucent.clear();
            this.mHandler.removeMessages(106);
            if (waitingActivity != null && waitingActivity.app != null && waitingActivity.app.thread != null) {
                try {
                    waitingActivity.app.thread.scheduleTranslucentConversionComplete(waitingActivity.appToken, r != null);
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean resumeTopActivityLocked(ActivityRecord prev) {
        return resumeTopActivityLocked(prev, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean resumeTopActivityLocked(ActivityRecord prev, Bundle options) {
        ActivityRecord next = topRunningActivityLocked(null);
        boolean userLeaving = this.mStackSupervisor.mUserLeaving;
        this.mStackSupervisor.mUserLeaving = false;
        if (next == null) {
            ActivityOptions.abort(options);
            return this.mStackSupervisor.resumeHomeActivity(prev);
        }
        next.delayedResume = false;
        if (this.mResumedActivity == next && next.state == ActivityState.RESUMED && this.mStackSupervisor.allResumedActivitiesComplete()) {
            this.mWindowManager.executeAppTransition();
            this.mNoAnimActivities.clear();
            ActivityOptions.abort(options);
            return false;
        }
        TaskRecord nextTask = next.task;
        TaskRecord prevTask = prev != null ? prev.task : null;
        if (prevTask != null && prevTask.mOnTopOfHome && prev.finishing && prev.frontOfTask) {
            if (prevTask == nextTask) {
                ArrayList<ActivityRecord> activities = prevTask.mActivities;
                int numActivities = activities.size();
                int activityNdx = 0;
                while (true) {
                    if (activityNdx >= numActivities) {
                        break;
                    }
                    ActivityRecord r = activities.get(activityNdx);
                    if (r.finishing) {
                        activityNdx++;
                    } else {
                        r.frontOfTask = true;
                        break;
                    }
                }
            } else if (prevTask != topTask()) {
                int taskNdx = this.mTaskHistory.indexOf(prevTask) + 1;
                this.mTaskHistory.get(taskNdx).mOnTopOfHome = true;
            } else {
                return this.mStackSupervisor.resumeHomeActivity(prev);
            }
        }
        if (this.mService.isSleepingOrShuttingDown() && this.mLastPausedActivity == next && this.mStackSupervisor.allPausedActivitiesComplete()) {
            this.mWindowManager.executeAppTransition();
            this.mNoAnimActivities.clear();
            ActivityOptions.abort(options);
            return false;
        } else if (this.mService.mStartedUsers.get(next.userId) == null) {
            Slog.w("ActivityManager", "Skipping resume of top activity " + next + ": user " + next.userId + " is stopped");
            return false;
        } else {
            this.mStackSupervisor.mStoppingActivities.remove(next);
            this.mStackSupervisor.mGoingToSleepActivities.remove(next);
            next.sleeping = false;
            this.mStackSupervisor.mWaitingVisibleActivities.remove(next);
            next.updateOptionsLocked(options);
            if (!this.mStackSupervisor.allPausedActivitiesComplete()) {
                return false;
            }
            boolean pausing = this.mStackSupervisor.pauseBackStacks(userLeaving);
            if (this.mResumedActivity != null) {
                pausing = true;
                startPausingLocked(userLeaving, false);
            }
            if (pausing) {
                if (next.app != null && next.app.thread != null) {
                    this.mService.updateLruProcessLocked(next.app, false, true);
                    return true;
                }
                return true;
            }
            if (this.mService.mSleeping && this.mLastNoHistoryActivity != null && !this.mLastNoHistoryActivity.finishing) {
                requestFinishActivityLocked(this.mLastNoHistoryActivity.appToken, 0, null, "no-history", false);
                this.mLastNoHistoryActivity = null;
            }
            if (prev != null && prev != next) {
                if (!prev.waitingVisible && next != null && !next.nowVisible) {
                    prev.waitingVisible = true;
                    this.mStackSupervisor.mWaitingVisibleActivities.add(prev);
                } else if (prev.finishing) {
                    this.mWindowManager.setAppVisibility(prev.appToken, false);
                }
            }
            try {
                AppGlobals.getPackageManager().setPackageStoppedState(next.packageName, false, next.userId);
            } catch (RemoteException e) {
            } catch (IllegalArgumentException e2) {
                Slog.w("ActivityManager", "Failed trying to unstop package " + next.packageName + ": " + e2);
            }
            boolean anim = true;
            if (prev != null) {
                if (prev.finishing) {
                    if (this.mNoAnimActivities.contains(prev)) {
                        anim = false;
                        this.mWindowManager.prepareAppTransition(0, false);
                    } else {
                        this.mWindowManager.prepareAppTransition(prev.task == next.task ? 8199 : 8201, false);
                    }
                    this.mWindowManager.setAppWillBeHidden(prev.appToken);
                    this.mWindowManager.setAppVisibility(prev.appToken, false);
                } else if (this.mNoAnimActivities.contains(next)) {
                    anim = false;
                    this.mWindowManager.prepareAppTransition(0, false);
                } else {
                    this.mWindowManager.prepareAppTransition(prev.task == next.task ? 4102 : AppTransition.TRANSIT_TASK_OPEN, false);
                }
            } else if (this.mNoAnimActivities.contains(next)) {
                anim = false;
                this.mWindowManager.prepareAppTransition(0, false);
            } else {
                this.mWindowManager.prepareAppTransition(4102, false);
            }
            if (anim) {
                next.applyOptionsLocked();
            } else {
                next.clearOptionsLocked();
            }
            ActivityStack lastStack = this.mStackSupervisor.getLastStack();
            if (next.app != null && next.app.thread != null) {
                this.mWindowManager.setAppVisibility(next.appToken, true);
                next.startLaunchTickingLocked();
                ActivityRecord lastResumedActivity = lastStack == null ? null : lastStack.mResumedActivity;
                ActivityState lastState = next.state;
                this.mService.updateCpuStats();
                next.state = ActivityState.RESUMED;
                this.mResumedActivity = next;
                next.task.touchActiveTime();
                this.mService.addRecentTaskLocked(next.task);
                this.mService.updateLruProcessLocked(next.app, true, true);
                updateLRUListLocked(next);
                boolean notUpdated = true;
                if (this.mStackSupervisor.isFrontStack(this)) {
                    Configuration config = this.mWindowManager.updateOrientationFromAppTokens(this.mService.mConfiguration, next.mayFreezeScreenLocked(next.app) ? next.appToken : null);
                    if (config != null) {
                        next.frozenBeforeDestroy = true;
                    }
                    notUpdated = !this.mService.updateConfigurationLocked(config, next, false, false);
                }
                if (notUpdated) {
                    ActivityRecord nextNext = topRunningActivityLocked(null);
                    if (nextNext != next) {
                        this.mStackSupervisor.scheduleResumeTopActivities();
                    }
                    if (this.mStackSupervisor.reportResumedActivityLocked(next)) {
                        this.mNoAnimActivities.clear();
                        return true;
                    }
                    return false;
                }
                try {
                    ArrayList<ResultInfo> a = next.results;
                    if (a != null) {
                        int N = a.size();
                        if (!next.finishing && N > 0) {
                            next.app.thread.scheduleSendResult(next.appToken, a);
                        }
                    }
                    if (next.newIntents != null) {
                        next.app.thread.scheduleNewIntent(next.newIntents, next.appToken);
                    }
                    EventLog.writeEvent((int) EventLogTags.AM_RESUME_ACTIVITY, Integer.valueOf(next.userId), Integer.valueOf(System.identityHashCode(next)), Integer.valueOf(next.task.taskId), next.shortComponentName);
                    next.sleeping = false;
                    this.mService.showAskCompatModeDialogLocked(next);
                    next.app.pendingUiClean = true;
                    next.app.forceProcessStateUpTo(2);
                    next.app.thread.scheduleResumeActivity(next.appToken, next.app.repProcState, this.mService.isNextTransitionForward());
                    this.mStackSupervisor.checkReadyForSleepLocked();
                    try {
                        next.visible = true;
                        completeResumeLocked(next);
                        next.stopped = false;
                        return true;
                    } catch (Exception e3) {
                        Slog.w("ActivityManager", "Exception thrown during resume of " + next, e3);
                        requestFinishActivityLocked(next.appToken, 0, null, "resume-exception", true);
                        return true;
                    }
                } catch (Exception e4) {
                    next.state = lastState;
                    if (lastStack != null) {
                        lastStack.mResumedActivity = lastResumedActivity;
                    }
                    Slog.i("ActivityManager", "Restarting because process died: " + next);
                    if (!next.hasBeenLaunched) {
                        next.hasBeenLaunched = true;
                    } else if (lastStack != null && this.mStackSupervisor.isFrontStack(lastStack)) {
                        this.mWindowManager.setAppStartingWindow(next.appToken, next.packageName, next.theme, this.mService.compatibilityInfoForPackageLocked(next.info.applicationInfo), next.nonLocalizedLabel, next.labelRes, next.icon, next.logo, next.windowFlags, null, true);
                    }
                    this.mStackSupervisor.startSpecificActivityLocked(next, true, false);
                    return true;
                }
            }
            if (!next.hasBeenLaunched) {
                next.hasBeenLaunched = true;
            } else {
                this.mWindowManager.setAppStartingWindow(next.appToken, next.packageName, next.theme, this.mService.compatibilityInfoForPackageLocked(next.info.applicationInfo), next.nonLocalizedLabel, next.labelRes, next.icon, next.logo, next.windowFlags, null, true);
            }
            this.mStackSupervisor.startSpecificActivityLocked(next, true, true);
            return true;
        }
    }

    private void insertTaskAtTop(TaskRecord task) {
        ActivityStack lastStack = this.mStackSupervisor.getLastStack();
        boolean fromHome = lastStack == null ? true : lastStack.isHomeStack();
        if (!isHomeStack() && (fromHome || topTask() != task)) {
            task.mOnTopOfHome = fromHome;
        }
        this.mTaskHistory.remove(task);
        int stackNdx = this.mTaskHistory.size();
        if (task.userId != this.mCurrentUser) {
            do {
                stackNdx--;
                if (stackNdx < 0) {
                    break;
                }
            } while (this.mTaskHistory.get(stackNdx).userId == this.mCurrentUser);
            stackNdx++;
        }
        this.mTaskHistory.add(stackNdx, task);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void startActivityLocked(ActivityRecord r, boolean newTask, boolean doResume, boolean keepCurTransition, Bundle options) {
        TaskRecord rTask = r.task;
        int taskId = rTask.taskId;
        if (taskForIdLocked(taskId) == null || newTask) {
            insertTaskAtTop(rTask);
            this.mWindowManager.moveTaskToTop(taskId);
        }
        TaskRecord task = null;
        if (!newTask) {
            boolean startIt = true;
            int taskNdx = this.mTaskHistory.size() - 1;
            while (true) {
                if (taskNdx < 0) {
                    break;
                }
                task = this.mTaskHistory.get(taskNdx);
                if (task == r.task) {
                    if (!startIt) {
                        task.addActivityToTop(r);
                        r.putInHistory();
                        this.mWindowManager.addAppToken(task.mActivities.indexOf(r), r.appToken, r.task.taskId, this.mStackId, r.info.screenOrientation, r.fullscreen, (r.info.flags & 1024) != 0, r.userId);
                        ActivityOptions.abort(options);
                        return;
                    }
                } else {
                    if (task.numFullscreen > 0) {
                        startIt = false;
                    }
                    taskNdx--;
                }
            }
        }
        if (task == r.task && this.mTaskHistory.indexOf(task) != this.mTaskHistory.size() - 1) {
            this.mStackSupervisor.mUserLeaving = false;
        }
        TaskRecord task2 = r.task;
        task2.addActivityToTop(r);
        r.putInHistory();
        r.frontOfTask = newTask;
        if (!isHomeStack() || numActivities() > 0) {
            boolean showStartingIcon = newTask;
            ProcessRecord proc = r.app;
            if (proc == null) {
                proc = (ProcessRecord) this.mService.mProcessNames.get(r.processName, r.info.applicationInfo.uid);
            }
            if (proc == null || proc.thread == null) {
                showStartingIcon = true;
            }
            if ((r.intent.getFlags() & 65536) != 0) {
                this.mWindowManager.prepareAppTransition(0, keepCurTransition);
                this.mNoAnimActivities.add(r);
            } else {
                this.mWindowManager.prepareAppTransition(newTask ? AppTransition.TRANSIT_TASK_OPEN : 4102, keepCurTransition);
                this.mNoAnimActivities.remove(r);
            }
            r.updateOptionsLocked(options);
            this.mWindowManager.addAppToken(task2.mActivities.indexOf(r), r.appToken, r.task.taskId, this.mStackId, r.info.screenOrientation, r.fullscreen, (r.info.flags & 1024) != 0, r.userId);
            boolean doShow = true;
            if (newTask && (r.intent.getFlags() & 2097152) != 0) {
                resetTaskIfNeededLocked(r, r);
                doShow = topRunningNonDelayedActivityLocked(null) == r;
            }
            if (doShow) {
                ActivityRecord prev = this.mResumedActivity;
                if (prev != null) {
                    if (prev.task != r.task) {
                        prev = null;
                    } else if (prev.nowVisible) {
                        prev = null;
                    }
                }
                this.mWindowManager.setAppStartingWindow(r.appToken, r.packageName, r.theme, this.mService.compatibilityInfoForPackageLocked(r.info.applicationInfo), r.nonLocalizedLabel, r.labelRes, r.icon, r.logo, r.windowFlags, prev != null ? prev.appToken : null, showStartingIcon);
            }
        } else {
            this.mWindowManager.addAppToken(task2.mActivities.indexOf(r), r.appToken, r.task.taskId, this.mStackId, r.info.screenOrientation, r.fullscreen, (r.info.flags & 1024) != 0, r.userId);
            ActivityOptions.abort(options);
        }
        if (doResume) {
            this.mStackSupervisor.resumeTopActivitiesLocked();
        }
    }

    final void validateAppTokensLocked() {
        this.mValidateAppTokens.clear();
        this.mValidateAppTokens.ensureCapacity(numActivities());
        int numTasks = this.mTaskHistory.size();
        for (int taskNdx = 0; taskNdx < numTasks; taskNdx++) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            ArrayList<ActivityRecord> activities = task.mActivities;
            if (!activities.isEmpty()) {
                TaskGroup group = new TaskGroup();
                group.taskId = task.taskId;
                this.mValidateAppTokens.add(group);
                int numActivities = activities.size();
                for (int activityNdx = 0; activityNdx < numActivities; activityNdx++) {
                    ActivityRecord r = activities.get(activityNdx);
                    group.tokens.add(r.appToken);
                }
            }
        }
        this.mWindowManager.validateAppTokens(this.mStackId, this.mValidateAppTokens);
    }

    final ActivityOptions resetTargetTaskIfNeededLocked(TaskRecord task, boolean forceReset) {
        int end;
        ActivityOptions topOptions = null;
        int replyChainEnd = -1;
        boolean canMoveOptions = true;
        ArrayList<ActivityRecord> activities = task.mActivities;
        int numActivities = activities.size();
        for (int i = numActivities - 1; i > 0; i--) {
            ActivityRecord target = activities.get(i);
            int flags = target.info.flags;
            boolean finishOnTaskLaunch = (flags & 2) != 0;
            boolean allowTaskReparenting = (flags & 64) != 0;
            boolean clearWhenTaskReset = (target.intent.getFlags() & 524288) != 0;
            if (!finishOnTaskLaunch && !clearWhenTaskReset && target.resultTo != null) {
                if (replyChainEnd < 0) {
                    replyChainEnd = i;
                }
            } else if (!finishOnTaskLaunch && !clearWhenTaskReset && allowTaskReparenting && target.taskAffinity != null && !target.taskAffinity.equals(task.affinity)) {
                ActivityRecord bottom = (this.mTaskHistory.isEmpty() || this.mTaskHistory.get(0).mActivities.isEmpty()) ? null : this.mTaskHistory.get(0).mActivities.get(0);
                if (bottom != null && target.taskAffinity != null && target.taskAffinity.equals(bottom.task.affinity)) {
                    target.setTask(bottom.task, bottom.thumbHolder, false);
                } else {
                    target.setTask(createTaskRecord(this.mStackSupervisor.getNextTaskId(), target.info, null, false), null, false);
                    target.task.affinityIntent = target.intent;
                }
                TaskRecord targetTask = target.task;
                int targetTaskId = targetTask.taskId;
                this.mWindowManager.setAppGroupId(target.appToken, targetTaskId);
                boolean noOptions = canMoveOptions;
                int start = replyChainEnd < 0 ? i : replyChainEnd;
                for (int srcPos = start; srcPos >= i; srcPos--) {
                    ActivityRecord p = activities.get(srcPos);
                    if (!p.finishing) {
                        ThumbnailHolder curThumbHolder = p.thumbHolder;
                        canMoveOptions = false;
                        if (noOptions && topOptions == null) {
                            topOptions = p.takeOptionsLocked();
                            if (topOptions != null) {
                                noOptions = false;
                            }
                        }
                        p.setTask(targetTask, curThumbHolder, false);
                        targetTask.addActivityAtBottom(p);
                        this.mWindowManager.setAppGroupId(p.appToken, targetTaskId);
                    }
                }
                this.mWindowManager.moveTaskToBottom(targetTaskId);
                replyChainEnd = -1;
            } else if (forceReset || finishOnTaskLaunch || clearWhenTaskReset) {
                if (clearWhenTaskReset) {
                    end = numActivities - 1;
                } else if (replyChainEnd < 0) {
                    end = i;
                } else {
                    end = replyChainEnd;
                }
                boolean noOptions2 = canMoveOptions;
                int srcPos2 = i;
                while (srcPos2 <= end) {
                    ActivityRecord p2 = activities.get(srcPos2);
                    if (!p2.finishing) {
                        canMoveOptions = false;
                        if (noOptions2 && topOptions == null) {
                            topOptions = p2.takeOptionsLocked();
                            if (topOptions != null) {
                                noOptions2 = false;
                            }
                        }
                        if (finishActivityLocked(p2, 0, null, "reset", false)) {
                            end--;
                            srcPos2--;
                        }
                    }
                    srcPos2++;
                }
                replyChainEnd = -1;
            } else {
                replyChainEnd = -1;
            }
        }
        return topOptions;
    }

    private int resetAffinityTaskIfNeededLocked(TaskRecord affinityTask, TaskRecord task, boolean topTaskIsHigher, boolean forceReset, int taskInsertionPoint) {
        ArrayList<ActivityRecord> taskActivities;
        int targetNdx;
        int replyChainEnd = -1;
        int taskId = task.taskId;
        String taskAffinity = task.affinity;
        ArrayList<ActivityRecord> activities = affinityTask.mActivities;
        int numActivities = activities.size();
        for (int i = numActivities - 1; i > 0; i--) {
            ActivityRecord target = activities.get(i);
            int flags = target.info.flags;
            boolean finishOnTaskLaunch = (flags & 2) != 0;
            boolean allowTaskReparenting = (flags & 64) != 0;
            if (target.resultTo != null) {
                if (replyChainEnd < 0) {
                    replyChainEnd = i;
                }
            } else if (topTaskIsHigher && allowTaskReparenting && taskAffinity != null && taskAffinity.equals(target.taskAffinity)) {
                if (forceReset || finishOnTaskLaunch) {
                    int start = replyChainEnd >= 0 ? replyChainEnd : i;
                    for (int srcPos = start; srcPos >= i; srcPos--) {
                        ActivityRecord p = activities.get(srcPos);
                        if (!p.finishing) {
                            finishActivityLocked(p, 0, null, "reset", false);
                        }
                    }
                } else {
                    if (taskInsertionPoint < 0) {
                        taskInsertionPoint = task.mActivities.size();
                    }
                    int start2 = replyChainEnd >= 0 ? replyChainEnd : i;
                    for (int srcPos2 = start2; srcPos2 >= i; srcPos2--) {
                        ActivityRecord p2 = activities.get(srcPos2);
                        p2.setTask(task, null, false);
                        task.addActivityAtIndex(taskInsertionPoint, p2);
                        this.mWindowManager.setAppGroupId(p2.appToken, taskId);
                    }
                    this.mWindowManager.moveTaskToTop(taskId);
                    if (target.info.launchMode == 1 && (targetNdx = (taskActivities = task.mActivities).indexOf(target)) > 0) {
                        ActivityRecord p3 = taskActivities.get(targetNdx - 1);
                        if (p3.intent.getComponent().equals(target.intent.getComponent())) {
                            finishActivityLocked(p3, 0, null, "replace", false);
                        }
                    }
                }
                replyChainEnd = -1;
            }
        }
        return taskInsertionPoint;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActivityRecord resetTaskIfNeededLocked(ActivityRecord taskTop, ActivityRecord newActivity) {
        ActivityRecord taskTop2;
        boolean forceReset = (newActivity.info.flags & 4) != 0;
        TaskRecord task = taskTop.task;
        boolean taskFound = false;
        ActivityOptions topOptions = null;
        int reparentInsertionPoint = -1;
        for (int i = this.mTaskHistory.size() - 1; i >= 0; i--) {
            TaskRecord targetTask = this.mTaskHistory.get(i);
            if (targetTask == task) {
                topOptions = resetTargetTaskIfNeededLocked(task, forceReset);
                taskFound = true;
            } else {
                reparentInsertionPoint = resetAffinityTaskIfNeededLocked(targetTask, task, taskFound, forceReset, reparentInsertionPoint);
            }
        }
        int taskNdx = this.mTaskHistory.indexOf(task);
        do {
            int i2 = taskNdx;
            taskNdx--;
            taskTop2 = this.mTaskHistory.get(i2).getTopActivity();
            if (taskTop2 != null) {
                break;
            }
        } while (taskNdx >= 0);
        if (topOptions != null) {
            if (taskTop2 != null) {
                taskTop2.updateOptionsLocked(topOptions);
            } else {
                topOptions.abort();
            }
        }
        return taskTop2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendActivityResultLocked(int callingUid, ActivityRecord r, String resultWho, int requestCode, int resultCode, Intent data) {
        if (callingUid > 0) {
            this.mService.grantUriPermissionFromIntentLocked(callingUid, r.packageName, data, r.getUriPermissionsLocked());
        }
        if (this.mResumedActivity == r && r.app != null && r.app.thread != null) {
            try {
                ArrayList<ResultInfo> list = new ArrayList<>();
                list.add(new ResultInfo(resultWho, requestCode, resultCode, data));
                r.app.thread.scheduleSendResult(r.appToken, list);
                return;
            } catch (Exception e) {
                Slog.w("ActivityManager", "Exception thrown sending result to " + r, e);
            }
        }
        r.addResultLocked(null, resultWho, requestCode, resultCode, data);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void stopActivityLocked(ActivityRecord r) {
        if (((r.intent.getFlags() & 1073741824) != 0 || (r.info.flags & 128) != 0) && !r.finishing && !this.mService.mSleeping) {
            requestFinishActivityLocked(r.appToken, 0, null, "no-history", false);
        }
        if (r.app != null && r.app.thread != null) {
            if (this.mStackSupervisor.isFrontStack(this) && this.mService.mFocusedActivity == r) {
                this.mService.setFocusedActivityLocked(topRunningActivityLocked(null));
            }
            r.resumeKeyDispatchingLocked();
            try {
                r.stopped = false;
                r.state = ActivityState.STOPPING;
                if (!r.visible) {
                    this.mWindowManager.setAppVisibility(r.appToken, false);
                }
                r.app.thread.scheduleStopActivity(r.appToken, r.visible, r.configChangeFlags);
                if (this.mService.isSleepingOrShuttingDown()) {
                    r.setSleeping(true);
                }
                Message msg = this.mHandler.obtainMessage(104, r);
                this.mHandler.sendMessageDelayed(msg, 10000L);
            } catch (Exception e) {
                Slog.w("ActivityManager", "Exception thrown during pause", e);
                r.stopped = true;
                r.state = ActivityState.STOPPED;
                if (r.configDestroy) {
                    destroyActivityLocked(r, true, false, "stop-except");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean requestFinishActivityLocked(IBinder token, int resultCode, Intent resultData, String reason, boolean oomAdj) {
        ActivityRecord r = isInStackLocked(token);
        if (r == null) {
            return false;
        }
        finishActivityLocked(r, resultCode, resultData, reason, oomAdj);
        return true;
    }

    final void finishSubActivityLocked(ActivityRecord self, String resultWho, int requestCode) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.resultTo == self && r.requestCode == requestCode && ((r.resultWho == null && resultWho == null) || (r.resultWho != null && r.resultWho.equals(resultWho)))) {
                    finishActivityLocked(r, 0, null, "request-sub", false);
                }
            }
        }
        this.mService.updateOomAdjLocked();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void finishTopRunningActivityLocked(ProcessRecord app) {
        ActivityRecord r = topRunningActivityLocked(null);
        if (r != null && r.app == app) {
            Slog.w("ActivityManager", "  Force finishing activity " + r.intent.getComponent().flattenToShortString());
            int taskNdx = this.mTaskHistory.indexOf(r.task);
            int activityNdx = r.task.mActivities.indexOf(r);
            finishActivityLocked(r, 0, null, "crashed", false);
            int activityNdx2 = activityNdx - 1;
            if (activityNdx2 < 0) {
                do {
                    taskNdx--;
                    if (taskNdx < 0) {
                        break;
                    }
                    activityNdx2 = this.mTaskHistory.get(taskNdx).mActivities.size() - 1;
                } while (activityNdx2 < 0);
            }
            if (activityNdx2 >= 0) {
                ActivityRecord r2 = this.mTaskHistory.get(taskNdx).mActivities.get(activityNdx2);
                if (r2.state == ActivityState.RESUMED || r2.state == ActivityState.PAUSING || r2.state == ActivityState.PAUSED) {
                    if (!r2.isHomeActivity() || this.mService.mHomeProcess != r2.app) {
                        Slog.w("ActivityManager", "  Force finishing activity " + r2.intent.getComponent().flattenToShortString());
                        finishActivityLocked(r2, 0, null, "crashed", false);
                    }
                }
            }
        }
    }

    final boolean finishActivityAffinityLocked(ActivityRecord r) {
        ArrayList<ActivityRecord> activities = r.task.mActivities;
        for (int index = activities.indexOf(r); index >= 0; index--) {
            ActivityRecord cur = activities.get(index);
            if (Objects.equal(cur.taskAffinity, r.taskAffinity)) {
                finishActivityLocked(cur, 0, null, "request-affinity", true);
            } else {
                return true;
            }
        }
        return true;
    }

    final void finishActivityResultsLocked(ActivityRecord r, int resultCode, Intent resultData) {
        ActivityRecord resultTo = r.resultTo;
        if (resultTo != null) {
            if (r.info.applicationInfo.uid > 0) {
                this.mService.grantUriPermissionFromIntentLocked(r.info.applicationInfo.uid, resultTo.packageName, resultData, resultTo.getUriPermissionsLocked());
            }
            resultTo.addResultLocked(r, r.resultWho, r.requestCode, resultCode, resultData);
            r.resultTo = null;
        }
        r.results = null;
        r.pendingResults = null;
        r.newIntents = null;
        r.icicle = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean finishActivityLocked(ActivityRecord r, int resultCode, Intent resultData, String reason, boolean oomAdj) {
        if (r.finishing) {
            Slog.w("ActivityManager", "Duplicate finish request for " + r);
            return false;
        }
        r.makeFinishing();
        EventLog.writeEvent((int) EventLogTags.AM_FINISH_ACTIVITY, Integer.valueOf(r.userId), Integer.valueOf(System.identityHashCode(r)), Integer.valueOf(r.task.taskId), r.shortComponentName, reason);
        ArrayList<ActivityRecord> activities = r.task.mActivities;
        int index = activities.indexOf(r);
        if (index < activities.size() - 1) {
            ActivityRecord next = activities.get(index + 1);
            if (r.frontOfTask) {
                next.frontOfTask = true;
            }
            if ((r.intent.getFlags() & 524288) != 0) {
                next.intent.addFlags(524288);
            }
        }
        r.pauseKeyDispatchingLocked();
        if (this.mStackSupervisor.isFrontStack(this) && this.mService.mFocusedActivity == r) {
            this.mService.setFocusedActivityLocked(this.mStackSupervisor.topRunningActivityLocked());
        }
        finishActivityResultsLocked(r, resultCode, resultData);
        if (!this.mService.mPendingThumbnails.isEmpty()) {
            this.mStackSupervisor.mCancelledThumbnails.add(r);
        }
        if (this.mResumedActivity != r) {
            return r.state != ActivityState.PAUSING && finishCurrentActivityLocked(r, 1, oomAdj) == null;
        }
        boolean endTask = index <= 0;
        this.mWindowManager.prepareAppTransition(endTask ? 8201 : 8199, false);
        this.mWindowManager.setAppVisibility(r.appToken, false);
        if (this.mPausingActivity == null) {
            startPausingLocked(false, false);
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActivityRecord finishCurrentActivityLocked(ActivityRecord r, int mode, boolean oomAdj) {
        if (mode == 2 && r.nowVisible) {
            if (!this.mStackSupervisor.mStoppingActivities.contains(r)) {
                this.mStackSupervisor.mStoppingActivities.add(r);
                if (this.mStackSupervisor.mStoppingActivities.size() > 3 || (r.frontOfTask && this.mTaskHistory.size() <= 1)) {
                    this.mStackSupervisor.scheduleIdleLocked();
                } else {
                    this.mStackSupervisor.checkReadyForSleepLocked();
                }
            }
            r.state = ActivityState.STOPPING;
            if (oomAdj) {
                this.mService.updateOomAdjLocked();
            }
            return r;
        }
        this.mStackSupervisor.mStoppingActivities.remove(r);
        this.mStackSupervisor.mGoingToSleepActivities.remove(r);
        this.mStackSupervisor.mWaitingVisibleActivities.remove(r);
        if (this.mResumedActivity == r) {
            this.mResumedActivity = null;
        }
        ActivityState prevState = r.state;
        r.state = ActivityState.FINISHING;
        if (mode == 0 || prevState == ActivityState.STOPPED || prevState == ActivityState.INITIALIZING) {
            boolean activityRemoved = destroyActivityLocked(r, true, oomAdj, "finish-imm");
            if (activityRemoved) {
                this.mStackSupervisor.resumeTopActivitiesLocked();
            }
            if (activityRemoved) {
                return null;
            }
            return r;
        }
        this.mStackSupervisor.mFinishingActivities.add(r);
        this.mStackSupervisor.getFocusedStack().resumeTopActivityLocked(null);
        return r;
    }

    final boolean navigateUpToLocked(IBinder token, Intent destIntent, int resultCode, Intent resultData) {
        ActivityRecord next;
        ActivityRecord srec = ActivityRecord.forToken(token);
        TaskRecord task = srec.task;
        ArrayList<ActivityRecord> activities = task.mActivities;
        int start = activities.indexOf(srec);
        if (!this.mTaskHistory.contains(task) || start < 0) {
            return false;
        }
        int finishTo = start - 1;
        ActivityRecord parent = finishTo < 0 ? null : activities.get(finishTo);
        boolean foundParentInTask = false;
        ComponentName dest = destIntent.getComponent();
        if (start > 0 && dest != null) {
            int i = finishTo;
            while (true) {
                if (i < 0) {
                    break;
                }
                ActivityRecord r = activities.get(i);
                if (r.info.packageName.equals(dest.getPackageName()) && r.info.name.equals(dest.getClassName())) {
                    finishTo = i;
                    parent = r;
                    foundParentInTask = true;
                    break;
                }
                i--;
            }
        }
        IActivityController controller = this.mService.mController;
        if (controller != null && (next = topRunningActivityLocked(srec.appToken, 0)) != null) {
            boolean resumeOK = true;
            try {
                resumeOK = controller.activityResuming(next.packageName);
            } catch (RemoteException e) {
                this.mService.mController = null;
                Watchdog.getInstance().setActivityController(null);
            }
            if (!resumeOK) {
                return false;
            }
        }
        long origId = Binder.clearCallingIdentity();
        for (int i2 = start; i2 > finishTo; i2--) {
            requestFinishActivityLocked(activities.get(i2).appToken, resultCode, resultData, "navigate-up", true);
            resultCode = 0;
            resultData = null;
        }
        if (parent != null && foundParentInTask) {
            int parentLaunchMode = parent.info.launchMode;
            int destIntentFlags = destIntent.getFlags();
            if (parentLaunchMode == 3 || parentLaunchMode == 2 || parentLaunchMode == 1 || (destIntentFlags & 67108864) != 0) {
                parent.deliverNewIntentLocked(srec.info.applicationInfo.uid, destIntent);
            } else {
                try {
                    ActivityInfo aInfo = AppGlobals.getPackageManager().getActivityInfo(destIntent.getComponent(), 0, srec.userId);
                    int res = this.mStackSupervisor.startActivityLocked(srec.app.thread, destIntent, null, aInfo, parent.appToken, null, 0, -1, parent.launchedFromUid, parent.launchedFromPackage, 0, null, true, null);
                    foundParentInTask = res == 0;
                } catch (RemoteException e2) {
                    foundParentInTask = false;
                }
                requestFinishActivityLocked(parent.appToken, resultCode, resultData, "navigate-up", true);
            }
        }
        Binder.restoreCallingIdentity(origId);
        return foundParentInTask;
    }

    final void cleanUpActivityLocked(ActivityRecord r, boolean cleanServices, boolean setState) {
        if (this.mResumedActivity == r) {
            this.mResumedActivity = null;
        }
        if (this.mService.mFocusedActivity == r) {
            this.mService.mFocusedActivity = null;
        }
        r.configDestroy = false;
        r.frozenBeforeDestroy = false;
        if (setState) {
            r.state = ActivityState.DESTROYED;
            r.app = null;
        }
        this.mStackSupervisor.mFinishingActivities.remove(r);
        this.mStackSupervisor.mWaitingVisibleActivities.remove(r);
        if (r.finishing && r.pendingResults != null) {
            Iterator i$ = r.pendingResults.iterator();
            while (i$.hasNext()) {
                WeakReference<PendingIntentRecord> apr = i$.next();
                PendingIntentRecord rec = apr.get();
                if (rec != null) {
                    this.mService.cancelIntentSenderLocked(rec, false);
                }
            }
            r.pendingResults = null;
        }
        if (cleanServices) {
            cleanUpActivityServicesLocked(r);
        }
        if (!this.mService.mPendingThumbnails.isEmpty()) {
            this.mStackSupervisor.mCancelledThumbnails.add(r);
        }
        removeTimeoutsForActivityLocked(r);
    }

    private void removeTimeoutsForActivityLocked(ActivityRecord r) {
        this.mStackSupervisor.removeTimeoutsForActivityLocked(r);
        this.mHandler.removeMessages(101, r);
        this.mHandler.removeMessages(104, r);
        this.mHandler.removeMessages(102, r);
        r.finishLaunchTickingLocked();
    }

    final void removeActivityFromHistoryLocked(ActivityRecord r) {
        finishActivityResultsLocked(r, 0, null);
        r.makeFinishing();
        TaskRecord task = r.task;
        if (task != null && task.removeActivity(r)) {
            if (this.mStackSupervisor.isFrontStack(this) && task == topTask() && task.mOnTopOfHome) {
                this.mStackSupervisor.moveHomeToTop();
            }
            this.mStackSupervisor.removeTask(task);
        }
        r.takeFromHistory();
        removeTimeoutsForActivityLocked(r);
        r.state = ActivityState.DESTROYED;
        r.app = null;
        this.mWindowManager.removeAppToken(r.appToken);
        cleanUpActivityServicesLocked(r);
        r.removeUriPermissionsLocked();
    }

    final void cleanUpActivityServicesLocked(ActivityRecord r) {
        if (r.connections != null) {
            Iterator<ConnectionRecord> it = r.connections.iterator();
            while (it.hasNext()) {
                ConnectionRecord c = it.next();
                this.mService.mServices.removeConnectionLocked(c, null, r);
            }
            r.connections = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void scheduleDestroyActivities(ProcessRecord owner, boolean oomAdj, String reason) {
        Message msg = this.mHandler.obtainMessage(105);
        msg.obj = new ScheduleDestroyArgs(owner, oomAdj, reason);
        this.mHandler.sendMessage(msg);
    }

    final void destroyActivitiesLocked(ProcessRecord owner, boolean oomAdj, String reason) {
        boolean lastIsOpaque = false;
        boolean activityRemoved = false;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (!r.finishing) {
                    if (r.fullscreen) {
                        lastIsOpaque = true;
                    }
                    if ((owner == null || r.app == owner) && lastIsOpaque && r.app != null && r != this.mResumedActivity && r != this.mPausingActivity && r.haveState && !r.visible && r.stopped && r.state != ActivityState.DESTROYING && r.state != ActivityState.DESTROYED && destroyActivityLocked(r, true, oomAdj, reason)) {
                        activityRemoved = true;
                    }
                }
            }
        }
        if (activityRemoved) {
            this.mStackSupervisor.resumeTopActivitiesLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean destroyActivityLocked(ActivityRecord r, boolean removeFromApp, boolean oomAdj, String reason) {
        EventLog.writeEvent((int) EventLogTags.AM_DESTROY_ACTIVITY, Integer.valueOf(r.userId), Integer.valueOf(System.identityHashCode(r)), Integer.valueOf(r.task.taskId), r.shortComponentName, reason);
        boolean removedFromHistory = false;
        cleanUpActivityLocked(r, false, false);
        boolean hadApp = r.app != null;
        if (hadApp) {
            if (removeFromApp) {
                r.app.activities.remove(r);
                if (this.mService.mHeavyWeightProcess == r.app && r.app.activities.size() <= 0) {
                    this.mService.mHeavyWeightProcess = null;
                    this.mService.mHandler.sendEmptyMessage(25);
                }
                if (r.app.activities.isEmpty()) {
                    this.mService.updateLruProcessLocked(r.app, false, false);
                    this.mService.updateOomAdjLocked();
                }
            }
            boolean skipDestroy = false;
            try {
                r.app.thread.scheduleDestroyActivity(r.appToken, r.finishing, r.configChangeFlags);
            } catch (Exception e) {
                if (r.finishing) {
                    removeActivityFromHistoryLocked(r);
                    removedFromHistory = true;
                    skipDestroy = true;
                }
            }
            r.nowVisible = false;
            if (r.finishing && !skipDestroy) {
                r.state = ActivityState.DESTROYING;
                Message msg = this.mHandler.obtainMessage(102, r);
                this.mHandler.sendMessageDelayed(msg, 10000L);
            } else {
                r.state = ActivityState.DESTROYED;
                r.app = null;
            }
        } else if (r.finishing) {
            removeActivityFromHistoryLocked(r);
            removedFromHistory = true;
        } else {
            r.state = ActivityState.DESTROYED;
            r.app = null;
        }
        r.configChangeFlags = 0;
        if (!this.mLRUActivities.remove(r) && hadApp) {
            Slog.w("ActivityManager", "Activity " + r + " being finished, but not in LRU list");
        }
        return removedFromHistory;
    }

    private void removeHistoryRecordsForAppLocked(ArrayList<ActivityRecord> list, ProcessRecord app, String listName) {
        int i = list.size();
        while (i > 0) {
            i--;
            ActivityRecord r = list.get(i);
            if (r.app == app) {
                list.remove(i);
                removeTimeoutsForActivityLocked(r);
            }
        }
    }

    boolean removeHistoryRecordsForAppLocked(ProcessRecord app) {
        boolean remove;
        removeHistoryRecordsForAppLocked(this.mLRUActivities, app, "mLRUActivities");
        removeHistoryRecordsForAppLocked(this.mStackSupervisor.mStoppingActivities, app, "mStoppingActivities");
        removeHistoryRecordsForAppLocked(this.mStackSupervisor.mGoingToSleepActivities, app, "mGoingToSleepActivities");
        removeHistoryRecordsForAppLocked(this.mStackSupervisor.mWaitingVisibleActivities, app, "mWaitingVisibleActivities");
        removeHistoryRecordsForAppLocked(this.mStackSupervisor.mFinishingActivities, app, "mFinishingActivities");
        boolean hasVisibleActivities = false;
        int i = numActivities();
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                i--;
                if (r.app == app) {
                    if ((!r.haveState && !r.stateNotNeeded) || r.finishing) {
                        remove = true;
                    } else if (r.launchCount > 2 && r.lastLaunchTime > SystemClock.uptimeMillis() - DateUtils.MINUTE_IN_MILLIS) {
                        remove = true;
                    } else {
                        remove = false;
                    }
                    if (remove) {
                        if (!r.finishing) {
                            Slog.w("ActivityManager", "Force removing " + r + ": app died, no saved state");
                            EventLog.writeEvent((int) EventLogTags.AM_FINISH_ACTIVITY, Integer.valueOf(r.userId), Integer.valueOf(System.identityHashCode(r)), Integer.valueOf(r.task.taskId), r.shortComponentName, "proc died without state saved");
                            if (r.state == ActivityState.RESUMED) {
                                this.mService.updateUsageStats(r, false);
                            }
                        }
                        removeActivityFromHistoryLocked(r);
                    } else {
                        if (r.visible) {
                            hasVisibleActivities = true;
                        }
                        r.app = null;
                        r.nowVisible = false;
                        if (!r.haveState) {
                            r.icicle = null;
                        }
                    }
                    cleanUpActivityLocked(r, true, true);
                }
            }
        }
        return hasVisibleActivities;
    }

    final void updateTransitLocked(int transit, Bundle options) {
        if (options != null) {
            ActivityRecord r = topRunningActivityLocked(null);
            if (r != null && r.state != ActivityState.RESUMED) {
                r.updateOptionsLocked(options);
            } else {
                ActivityOptions.abort(options);
            }
        }
        this.mWindowManager.prepareAppTransition(transit, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void moveHomeTaskToTop() {
        int top = this.mTaskHistory.size() - 1;
        for (int taskNdx = top; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.isHomeTask()) {
                this.mTaskHistory.remove(taskNdx);
                this.mTaskHistory.add(top, task);
                this.mWindowManager.moveTaskToTop(task.taskId);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean findTaskToMoveToFrontLocked(int taskId, int flags, Bundle options) {
        TaskRecord task = taskForIdLocked(taskId);
        if (task != null) {
            if ((flags & 2) == 0) {
                this.mStackSupervisor.mUserLeaving = true;
            }
            if ((flags & 1) != 0) {
                task.mOnTopOfHome = true;
            }
            moveTaskToFrontLocked(task, null, options);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void moveTaskToFrontLocked(TaskRecord tr, ActivityRecord reason, Bundle options) {
        int numTasks = this.mTaskHistory.size();
        int index = this.mTaskHistory.indexOf(tr);
        if (numTasks == 0 || index < 0) {
            if (reason != null && (reason.intent.getFlags() & 65536) != 0) {
                ActivityOptions.abort(options);
                return;
            } else {
                updateTransitLocked(AppTransition.TRANSIT_TASK_TO_FRONT, options);
                return;
            }
        }
        this.mStackSupervisor.moveHomeStack(isHomeStack());
        insertTaskAtTop(tr);
        if (reason != null && (reason.intent.getFlags() & 65536) != 0) {
            this.mWindowManager.prepareAppTransition(0, false);
            ActivityRecord r = topRunningActivityLocked(null);
            if (r != null) {
                this.mNoAnimActivities.add(r);
            }
            ActivityOptions.abort(options);
        } else {
            updateTransitLocked(AppTransition.TRANSIT_TASK_TO_FRONT, options);
        }
        this.mWindowManager.moveTaskToTop(tr.taskId);
        this.mStackSupervisor.resumeTopActivitiesLocked();
        EventLog.writeEvent((int) EventLogTags.AM_TASK_TO_FRONT, Integer.valueOf(tr.userId), Integer.valueOf(tr.taskId));
    }

    final boolean moveTaskToBackLocked(int taskId, ActivityRecord reason) {
        Slog.i("ActivityManager", "moveTaskToBack: " + taskId);
        if (this.mStackSupervisor.isFrontStack(this) && this.mService.mController != null) {
            ActivityRecord next = topRunningActivityLocked(null, taskId);
            if (next == null) {
                next = topRunningActivityLocked(null, 0);
            }
            if (next != null) {
                boolean moveOK = true;
                try {
                    moveOK = this.mService.mController.activityResuming(next.packageName);
                } catch (RemoteException e) {
                    this.mService.mController = null;
                    Watchdog.getInstance().setActivityController(null);
                }
                if (!moveOK) {
                    return false;
                }
            }
        }
        TaskRecord tr = taskForIdLocked(taskId);
        if (tr == null) {
            return false;
        }
        this.mTaskHistory.remove(tr);
        this.mTaskHistory.add(0, tr);
        int numTasks = this.mTaskHistory.size();
        for (int taskNdx = numTasks - 1; taskNdx >= 1; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            if (task.mOnTopOfHome) {
                break;
            }
            if (taskNdx == 1) {
                task.mOnTopOfHome = true;
            }
        }
        if (reason != null && (reason.intent.getFlags() & 65536) != 0) {
            this.mWindowManager.prepareAppTransition(0, false);
            ActivityRecord r = topRunningActivityLocked(null);
            if (r != null) {
                this.mNoAnimActivities.add(r);
            }
        } else {
            this.mWindowManager.prepareAppTransition(8203, false);
        }
        this.mWindowManager.moveTaskToBottom(taskId);
        TaskRecord task2 = this.mResumedActivity != null ? this.mResumedActivity.task : null;
        if ((task2 == tr && task2.mOnTopOfHome) || numTasks <= 1) {
            task2.mOnTopOfHome = false;
            return this.mStackSupervisor.resumeHomeActivity(null);
        }
        this.mStackSupervisor.resumeTopActivitiesLocked();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void logStartActivity(int tag, ActivityRecord r, TaskRecord task) {
        Uri data = r.intent.getData();
        String strData = data != null ? data.toSafeString() : null;
        EventLog.writeEvent(tag, Integer.valueOf(r.userId), Integer.valueOf(System.identityHashCode(r)), Integer.valueOf(task.taskId), r.shortComponentName, r.intent.getAction(), r.intent.getType(), strData, Integer.valueOf(r.intent.getFlags()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean ensureActivityConfigurationLocked(ActivityRecord r, int globalChanges) {
        if (this.mConfigWillChange) {
            return true;
        }
        Configuration newConfig = this.mService.mConfiguration;
        if (r.configuration == newConfig && !r.forceNewConfig) {
            return true;
        }
        if (r.finishing) {
            r.stopFreezingScreenLocked(false);
            return true;
        }
        Configuration oldConfig = r.configuration;
        r.configuration = newConfig;
        int changes = oldConfig.diff(newConfig);
        if (changes == 0 && !r.forceNewConfig) {
            return true;
        }
        if (r.app == null || r.app.thread == null) {
            r.stopFreezingScreenLocked(false);
            r.forceNewConfig = false;
            return true;
        } else if ((changes & (r.info.getRealConfigChanged() ^ (-1))) != 0 || r.forceNewConfig) {
            r.configChangeFlags |= changes;
            r.startFreezingScreenLocked(r.app, globalChanges);
            r.forceNewConfig = false;
            if (r.app == null || r.app.thread == null) {
                destroyActivityLocked(r, true, false, "config");
                return false;
            } else if (r.state == ActivityState.PAUSING) {
                r.configDestroy = true;
                return true;
            } else if (r.state == ActivityState.RESUMED) {
                relaunchActivityLocked(r, r.configChangeFlags, true);
                r.configChangeFlags = 0;
                return false;
            } else {
                relaunchActivityLocked(r, r.configChangeFlags, false);
                r.configChangeFlags = 0;
                return false;
            }
        } else {
            if (r.app != null && r.app.thread != null) {
                try {
                    r.app.thread.scheduleActivityConfigurationChanged(r.appToken);
                } catch (RemoteException e) {
                }
            }
            r.stopFreezingScreenLocked(false);
            return true;
        }
    }

    private boolean relaunchActivityLocked(ActivityRecord r, int changes, boolean andResume) {
        List<ResultInfo> results = null;
        List<Intent> newIntents = null;
        if (andResume) {
            results = r.results;
            newIntents = r.newIntents;
        }
        EventLog.writeEvent(andResume ? EventLogTags.AM_RELAUNCH_RESUME_ACTIVITY : EventLogTags.AM_RELAUNCH_ACTIVITY, Integer.valueOf(r.userId), Integer.valueOf(System.identityHashCode(r)), Integer.valueOf(r.task.taskId), r.shortComponentName);
        r.startFreezingScreenLocked(r.app, 0);
        try {
            r.forceNewConfig = false;
            r.app.thread.scheduleRelaunchActivity(r.appToken, results, newIntents, changes, !andResume, new Configuration(this.mService.mConfiguration));
        } catch (RemoteException e) {
        }
        if (andResume) {
            r.results = null;
            r.newIntents = null;
            r.state = ActivityState.RESUMED;
            return true;
        }
        this.mHandler.removeMessages(101, r);
        r.state = ActivityState.PAUSED;
        return true;
    }

    boolean willActivityBeVisibleLocked(IBinder token) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.appToken == token) {
                    return true;
                }
                if (r.fullscreen && !r.finishing) {
                    return false;
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeSystemDialogsLocked() {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if ((r.info.flags & 256) != 0) {
                    finishActivityLocked(r, 0, null, "close-sys", true);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean forceStopPackageLocked(String name, boolean doit, boolean evenPersistent, int userId) {
        boolean didSomething = false;
        TaskRecord lastTask = null;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            int numActivities = activities.size();
            int activityNdx = 0;
            while (activityNdx < numActivities) {
                ActivityRecord r = activities.get(activityNdx);
                boolean samePackage = r.packageName.equals(name) || (name == null && r.userId == userId);
                if ((userId == -1 || r.userId == userId) && ((samePackage || r.task == lastTask) && (r.app == null || evenPersistent || !r.app.persistent))) {
                    if (!doit) {
                        if (!r.finishing) {
                            return true;
                        }
                    } else {
                        didSomething = true;
                        Slog.i("ActivityManager", "  Force finishing activity " + r);
                        if (samePackage) {
                            if (r.app != null) {
                                r.app.removed = true;
                            }
                            r.app = null;
                        }
                        lastTask = r.task;
                        if (finishActivityLocked(r, 0, null, "force-stop", true)) {
                            numActivities--;
                            activityNdx--;
                        }
                    }
                }
                activityNdx++;
            }
        }
        return didSomething;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityRecord getTasksLocked(IThumbnailReceiver receiver, PendingThumbnailsRecord pending, List<ActivityManager.RunningTaskInfo> list) {
        ActivityRecord topRecord = null;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            ActivityRecord r = null;
            ActivityRecord top = null;
            int numActivities = 0;
            int numRunning = 0;
            ArrayList<ActivityRecord> activities = task.mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                r = activities.get(activityNdx);
                if (top == null || top.state == ActivityState.INITIALIZING) {
                    top = r;
                    numRunning = 0;
                    numActivities = 0;
                }
                numActivities++;
                if (r.app != null && r.app.thread != null) {
                    numRunning++;
                }
            }
            ActivityManager.RunningTaskInfo ci = new ActivityManager.RunningTaskInfo();
            ci.id = task.taskId;
            ci.baseActivity = r.intent.getComponent();
            ci.topActivity = top.intent.getComponent();
            ci.lastActiveTime = task.lastActiveTime;
            if (top.thumbHolder != null) {
                ci.description = top.thumbHolder.lastDescription;
            }
            ci.numActivities = numActivities;
            ci.numRunning = numRunning;
            if (receiver != null) {
                if (top.state == ActivityState.RESUMED || top.state == ActivityState.PAUSING) {
                    if (top.idle && top.app != null && top.app.thread != null) {
                        topRecord = top;
                    } else {
                        top.thumbnailNeeded = true;
                    }
                }
                pending.pendingRecords.add(top);
            }
            list.add(ci);
        }
        return topRecord;
    }

    public void unhandledBackLocked() {
        int top = this.mTaskHistory.size() - 1;
        if (top >= 0) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(top).mActivities;
            int activityTop = activities.size() - 1;
            if (activityTop > 0) {
                finishActivityLocked(activities.get(activityTop), 0, null, "unhandled-back", true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean handleAppDiedLocked(ProcessRecord app) {
        if (this.mPausingActivity != null && this.mPausingActivity.app == app) {
            this.mPausingActivity = null;
        }
        if (this.mLastPausedActivity != null && this.mLastPausedActivity.app == app) {
            this.mLastPausedActivity = null;
            this.mLastNoHistoryActivity = null;
        }
        return removeHistoryRecordsForAppLocked(app);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleAppCrashLocked(ProcessRecord app) {
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord r = activities.get(activityNdx);
                if (r.app == app) {
                    Slog.w("ActivityManager", "  Force finishing activity " + r.intent.getComponent().flattenToShortString());
                    finishActivityLocked(r, 0, null, "crashed", false);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean dumpActivitiesLocked(FileDescriptor fd, PrintWriter pw, boolean dumpAll, boolean dumpClient, String dumpPackage, boolean needSep, String header) {
        boolean printed = false;
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            TaskRecord task = this.mTaskHistory.get(taskNdx);
            printed |= ActivityStackSupervisor.dumpHistoryList(fd, pw, this.mTaskHistory.get(taskNdx).mActivities, "    ", "Hist", true, !dumpAll, dumpClient, dumpPackage, needSep, header, "    Task id #" + task.taskId);
            if (printed) {
                header = null;
            }
        }
        return printed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<ActivityRecord> getDumpActivitiesLocked(String name) {
        ArrayList<ActivityRecord> activities = new ArrayList<>();
        if ("all".equals(name)) {
            for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
                activities.addAll(this.mTaskHistory.get(taskNdx).mActivities);
            }
        } else if ("top".equals(name)) {
            int top = this.mTaskHistory.size() - 1;
            if (top >= 0) {
                ArrayList<ActivityRecord> list = this.mTaskHistory.get(top).mActivities;
                int listTop = list.size() - 1;
                if (listTop >= 0) {
                    activities.add(list.get(listTop));
                }
            }
        } else {
            ActivityManagerService$ItemMatcher matcher = new ActivityManagerService$ItemMatcher();
            matcher.build(name);
            for (int taskNdx2 = this.mTaskHistory.size() - 1; taskNdx2 >= 0; taskNdx2--) {
                Iterator i$ = this.mTaskHistory.get(taskNdx2).mActivities.iterator();
                while (i$.hasNext()) {
                    ActivityRecord r1 = i$.next();
                    if (matcher.match(r1, r1.intent.getComponent())) {
                        activities.add(r1);
                    }
                }
            }
        }
        return activities;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityRecord restartPackage(String packageName) {
        ActivityRecord starting = topRunningActivityLocked(null);
        for (int taskNdx = this.mTaskHistory.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<ActivityRecord> activities = this.mTaskHistory.get(taskNdx).mActivities;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ActivityRecord a = activities.get(activityNdx);
                if (a.info.packageName.equals(packageName)) {
                    a.forceNewConfig = true;
                    if (starting != null && a == starting && a.visible) {
                        a.startFreezingScreenLocked(starting.app, 256);
                    }
                }
            }
        }
        return starting;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean removeTask(TaskRecord task) {
        int taskNdx = this.mTaskHistory.indexOf(task);
        int topTaskNdx = this.mTaskHistory.size() - 1;
        if (task.mOnTopOfHome && taskNdx < topTaskNdx) {
            this.mTaskHistory.get(taskNdx + 1).mOnTopOfHome = true;
        }
        this.mTaskHistory.remove(task);
        return this.mTaskHistory.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskRecord createTaskRecord(int taskId, ActivityInfo info, Intent intent, boolean toTop) {
        TaskRecord task = new TaskRecord(taskId, info, intent);
        addTask(task, toTop);
        return task;
    }

    ArrayList<TaskRecord> getAllTasks() {
        return new ArrayList<>(this.mTaskHistory);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addTask(TaskRecord task, boolean toTop) {
        task.stack = this;
        if (toTop) {
            insertTaskAtTop(task);
        } else {
            this.mTaskHistory.add(0, task);
        }
    }

    public int getStackId() {
        return this.mStackId;
    }

    public String toString() {
        return "ActivityStack{" + Integer.toHexString(System.identityHashCode(this)) + " stackId=" + this.mStackId + ", " + this.mTaskHistory.size() + " tasks}";
    }
}