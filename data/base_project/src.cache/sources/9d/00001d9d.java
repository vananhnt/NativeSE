package com.android.server.am;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.IApplicationThread;
import android.app.IThumbnailReceiver;
import android.app.ResultInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.TimedRemoteCaller;
import com.android.internal.app.HeavyWeightSwitcherActivity;
import com.android.server.am.ActivityStack;
import com.android.server.wm.WindowManagerService;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/* loaded from: ActivityStackSupervisor.class */
public final class ActivityStackSupervisor {
    static final boolean DEBUG = false;
    static final boolean DEBUG_ADD_REMOVE = false;
    static final boolean DEBUG_APP = false;
    static final boolean DEBUG_SAVED_STATE = false;
    static final boolean DEBUG_STATES = false;
    static final boolean DEBUG_IDLE = false;
    public static final int HOME_STACK_ID = 0;
    static final int IDLE_TIMEOUT = 10000;
    static final int SLEEP_TIMEOUT = 5000;
    static final int LAUNCH_TIMEOUT = 10000;
    static final int IDLE_TIMEOUT_MSG = 100;
    static final int IDLE_NOW_MSG = 101;
    static final int RESUME_TOP_ACTIVITY_MSG = 102;
    static final int SLEEP_TIMEOUT_MSG = 103;
    static final int LAUNCH_TIMEOUT_MSG = 104;
    static final boolean VALIDATE_WAKE_LOCK_CALLER = false;
    final ActivityManagerService mService;
    final Context mContext;
    final Looper mLooper;
    final ActivityStackSupervisorHandler mHandler;
    WindowManagerService mWindowManager;
    private int mCurrentUser;
    private ActivityStack mHomeStack;
    private ActivityStack mFocusedStack;
    private static final int STACK_STATE_HOME_IN_FRONT = 0;
    private static final int STACK_STATE_HOME_TO_BACK = 1;
    private static final int STACK_STATE_HOME_IN_BACK = 2;
    private static final int STACK_STATE_HOME_TO_FRONT = 3;
    final PowerManager.WakeLock mLaunchingActivity;
    final PowerManager.WakeLock mGoingToSleep;
    boolean mDismissKeyguardOnNextActivity = false;
    private int mLastStackId = 0;
    private int mCurTaskId = 0;
    private ArrayList<ActivityStack> mStacks = new ArrayList<>();
    private int mStackState = 0;
    final ArrayList<ActivityRecord> mWaitingVisibleActivities = new ArrayList<>();
    final ArrayList<IActivityManager.WaitResult> mWaitingActivityVisible = new ArrayList<>();
    final ArrayList<IActivityManager.WaitResult> mWaitingActivityLaunched = new ArrayList<>();
    final ArrayList<ActivityRecord> mStoppingActivities = new ArrayList<>();
    final ArrayList<ActivityRecord> mFinishingActivities = new ArrayList<>();
    final ArrayList<ActivityRecord> mGoingToSleepActivities = new ArrayList<>();
    final ArrayList<ActivityRecord> mCancelledThumbnails = new ArrayList<>();
    final ArrayList<UserStartedState> mStartingUsers = new ArrayList<>();
    boolean mUserLeaving = false;
    boolean mSleepTimeout = false;
    SparseIntArray mUserStackInFront = new SparseIntArray(2);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActivityStackSupervisor.startActivities(android.app.IApplicationThread, int, java.lang.String, android.content.Intent[], java.lang.String[], android.os.IBinder, android.os.Bundle, int):int, file: ActivityStackSupervisor.class
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
    final int startActivities(android.app.IApplicationThread r1, int r2, java.lang.String r3, android.content.Intent[] r4, java.lang.String[] r5, android.os.IBinder r6, android.os.Bundle r7, int r8) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActivityStackSupervisor.startActivities(android.app.IApplicationThread, int, java.lang.String, android.content.Intent[], java.lang.String[], android.os.IBinder, android.os.Bundle, int):int, file: ActivityStackSupervisor.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityStackSupervisor.startActivities(android.app.IApplicationThread, int, java.lang.String, android.content.Intent[], java.lang.String[], android.os.IBinder, android.os.Bundle, int):int");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActivityStackSupervisor.dumpHistoryList(java.io.FileDescriptor, java.io.PrintWriter, java.util.List<com.android.server.am.ActivityRecord>, java.lang.String, java.lang.String, boolean, boolean, boolean, java.lang.String, boolean, java.lang.String, java.lang.String):boolean, file: ActivityStackSupervisor.class
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
    static boolean dumpHistoryList(java.io.FileDescriptor r0, java.io.PrintWriter r1, java.util.List<com.android.server.am.ActivityRecord> r2, java.lang.String r3, java.lang.String r4, boolean r5, boolean r6, boolean r7, java.lang.String r8, boolean r9, java.lang.String r10, java.lang.String r11) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.am.ActivityStackSupervisor.dumpHistoryList(java.io.FileDescriptor, java.io.PrintWriter, java.util.List<com.android.server.am.ActivityRecord>, java.lang.String, java.lang.String, boolean, boolean, boolean, java.lang.String, boolean, java.lang.String, java.lang.String):boolean, file: ActivityStackSupervisor.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityStackSupervisor.dumpHistoryList(java.io.FileDescriptor, java.io.PrintWriter, java.util.List, java.lang.String, java.lang.String, boolean, boolean, boolean, java.lang.String, boolean, java.lang.String, java.lang.String):boolean");
    }

    public ActivityStackSupervisor(ActivityManagerService service, Context context, Looper looper) {
        this.mService = service;
        this.mContext = context;
        this.mLooper = looper;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.mGoingToSleep = pm.newWakeLock(1, "ActivityManager-Sleep");
        this.mHandler = new ActivityStackSupervisorHandler(looper);
        this.mLaunchingActivity = pm.newWakeLock(1, "ActivityManager-Launch");
        this.mLaunchingActivity.setReferenceCounted(false);
    }

    void setWindowManager(WindowManagerService wm) {
        this.mWindowManager = wm;
        this.mHomeStack = new ActivityStack(this.mService, this.mContext, this.mLooper, 0);
        this.mStacks.add(this.mHomeStack);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dismissKeyguard() {
        if (this.mDismissKeyguardOnNextActivity) {
            this.mDismissKeyguardOnNextActivity = false;
            this.mWindowManager.dismissKeyguard();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityStack getFocusedStack() {
        if (this.mFocusedStack == null) {
            return this.mHomeStack;
        }
        switch (this.mStackState) {
            case 0:
            case 3:
                return this.mHomeStack;
            case 1:
            case 2:
            default:
                return this.mFocusedStack;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityStack getLastStack() {
        switch (this.mStackState) {
            case 0:
            case 1:
                return this.mHomeStack;
            case 2:
            case 3:
            default:
                return this.mFocusedStack;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFrontStack(ActivityStack stack) {
        return !(stack.isHomeStack() ^ getFocusedStack().isHomeStack());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void moveHomeStack(boolean toFront) {
        boolean homeInFront = isFrontStack(this.mHomeStack);
        if (homeInFront ^ toFront) {
            this.mStackState = homeInFront ? 1 : 3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void moveHomeToTop() {
        moveHomeStack(true);
        this.mHomeStack.moveHomeTaskToTop();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean resumeHomeActivity(ActivityRecord prev) {
        moveHomeToTop();
        if (prev != null) {
            prev.task.mOnTopOfHome = false;
        }
        ActivityRecord r = this.mHomeStack.topRunningActivityLocked(null);
        if (r != null && r.isHomeActivity()) {
            this.mService.setFocusedActivityLocked(r);
            return resumeTopActivitiesLocked(this.mHomeStack, prev, null);
        }
        return this.mService.startHomeActivityLocked(this.mCurrentUser);
    }

    void setDismissKeyguard(boolean dismiss) {
        this.mDismissKeyguardOnNextActivity = dismiss;
    }

    TaskRecord anyTaskForIdLocked(int id) {
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            TaskRecord task = stack.taskForIdLocked(id);
            if (task != null) {
                return task;
            }
        }
        return null;
    }

    ActivityRecord isInAnyStackLocked(IBinder token) {
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityRecord r = this.mStacks.get(stackNdx).isInStackLocked(token);
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNextTaskId() {
        do {
            this.mCurTaskId++;
            if (this.mCurTaskId <= 0) {
                this.mCurTaskId = 1;
            }
        } while (anyTaskForIdLocked(this.mCurTaskId) != null);
        return this.mCurTaskId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeTask(TaskRecord task) {
        this.mWindowManager.removeTask(task.taskId);
        ActivityStack stack = task.stack;
        ActivityRecord r = stack.mResumedActivity;
        if (r != null && r.task == task) {
            stack.mResumedActivity = null;
        }
        if (stack.removeTask(task) && !stack.isHomeStack()) {
            this.mStacks.remove(stack);
            int stackId = stack.mStackId;
            int nextStackId = this.mWindowManager.removeStack(stackId);
            if (this.mFocusedStack == null || this.mFocusedStack.mStackId == stackId) {
                this.mFocusedStack = nextStackId == 0 ? null : getStack(nextStackId);
            }
        }
    }

    ActivityRecord resumedAppLocked() {
        ActivityStack stack = getFocusedStack();
        if (stack == null) {
            return null;
        }
        ActivityRecord resumedActivity = stack.mResumedActivity;
        if (resumedActivity == null || resumedActivity.app == null) {
            resumedActivity = stack.mPausingActivity;
            if (resumedActivity == null || resumedActivity.app == null) {
                resumedActivity = stack.topRunningActivityLocked(null);
            }
        }
        return resumedActivity;
    }

    boolean attachApplicationLocked(ProcessRecord app, boolean headless) throws Exception {
        ActivityRecord hr;
        boolean didSomething = false;
        String processName = app.processName;
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (isFrontStack(stack) && (hr = stack.topRunningActivityLocked(null)) != null && hr.app == null && app.uid == hr.info.applicationInfo.uid && processName.equals(hr.processName)) {
                if (headless) {
                    try {
                        Slog.e("ActivityManager", "Starting activities not supported on headless device: " + hr);
                    } catch (Exception e) {
                        Slog.w("ActivityManager", "Exception in new application when starting activity " + hr.intent.getComponent().flattenToShortString(), e);
                        throw e;
                    }
                } else if (realStartActivityLocked(hr, app, true, true)) {
                    didSomething = true;
                }
            }
        }
        if (!didSomething) {
            ensureActivitiesVisibleLocked(null, 0);
        }
        return didSomething;
    }

    boolean allResumedActivitiesIdle() {
        ActivityRecord resumedActivity;
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (isFrontStack(stack) && ((resumedActivity = stack.mResumedActivity) == null || !resumedActivity.idle)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean allResumedActivitiesComplete() {
        ActivityRecord r;
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (isFrontStack(stack) && (r = stack.mResumedActivity) != null && r.state != ActivityStack.ActivityState.RESUMED) {
                return false;
            }
        }
        switch (this.mStackState) {
            case 1:
                this.mStackState = 2;
                return true;
            case 3:
                this.mStackState = 0;
                return true;
            default:
                return true;
        }
    }

    boolean allResumedActivitiesVisible() {
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            ActivityRecord r = stack.mResumedActivity;
            if (r != null && (!r.nowVisible || r.waitingVisible)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean pauseBackStacks(boolean userLeaving) {
        boolean someActivityPaused = false;
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (!isFrontStack(stack) && stack.mResumedActivity != null) {
                stack.startPausingLocked(userLeaving, false);
                someActivityPaused = true;
            }
        }
        return someActivityPaused;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean allPausedActivitiesComplete() {
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            ActivityRecord r = stack.mPausingActivity;
            if (r != null && r.state != ActivityStack.ActivityState.PAUSED && r.state != ActivityStack.ActivityState.STOPPED && r.state != ActivityStack.ActivityState.STOPPING) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportActivityVisibleLocked(ActivityRecord r) {
        for (int i = this.mWaitingActivityVisible.size() - 1; i >= 0; i--) {
            IActivityManager.WaitResult w = this.mWaitingActivityVisible.get(i);
            w.timeout = false;
            if (r != null) {
                w.who = new ComponentName(r.info.packageName, r.info.name);
            }
            w.totalTime = SystemClock.uptimeMillis() - w.thisTime;
            w.thisTime = w.totalTime;
        }
        this.mService.notifyAll();
        dismissKeyguard();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportActivityLaunchedLocked(boolean timeout, ActivityRecord r, long thisTime, long totalTime) {
        for (int i = this.mWaitingActivityLaunched.size() - 1; i >= 0; i--) {
            IActivityManager.WaitResult w = this.mWaitingActivityLaunched.remove(i);
            w.timeout = timeout;
            if (r != null) {
                w.who = new ComponentName(r.info.packageName, r.info.name);
            }
            w.thisTime = thisTime;
            w.totalTime = totalTime;
        }
        this.mService.notifyAll();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityRecord topRunningActivityLocked() {
        ActivityRecord r;
        ActivityStack focusedStack = getFocusedStack();
        ActivityRecord r2 = focusedStack.topRunningActivityLocked(null);
        if (r2 != null) {
            return r2;
        }
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (stack != focusedStack && isFrontStack(stack) && (r = stack.topRunningActivityLocked(null)) != null) {
                return r;
            }
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    ActivityRecord getTasksLocked(int maxNum, IThumbnailReceiver receiver, PendingThumbnailsRecord pending, List<ActivityManager.RunningTaskInfo> list) {
        ActivityRecord r = null;
        int numStacks = this.mStacks.size();
        ArrayList<ActivityManager.RunningTaskInfo>[] runningTaskLists = new ArrayList[numStacks];
        for (int stackNdx = numStacks - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            ArrayList<ActivityManager.RunningTaskInfo> stackTaskList = new ArrayList<>();
            runningTaskLists[stackNdx] = stackTaskList;
            ActivityRecord ar = stack.getTasksLocked(receiver, pending, stackTaskList);
            if (isFrontStack(stack)) {
                r = ar;
            }
        }
        while (maxNum > 0) {
            long mostRecentActiveTime = Long.MIN_VALUE;
            ArrayList<ActivityManager.RunningTaskInfo> selectedStackList = null;
            for (int stackNdx2 = 0; stackNdx2 < numStacks; stackNdx2++) {
                ArrayList<ActivityManager.RunningTaskInfo> stackTaskList2 = runningTaskLists[stackNdx2];
                if (!stackTaskList2.isEmpty()) {
                    long lastActiveTime = stackTaskList2.get(0).lastActiveTime;
                    if (lastActiveTime > mostRecentActiveTime) {
                        mostRecentActiveTime = lastActiveTime;
                        selectedStackList = stackTaskList2;
                    }
                }
            }
            if (selectedStackList == null) {
                break;
            }
            list.add(selectedStackList.remove(0));
            maxNum--;
        }
        return r;
    }

    ActivityInfo resolveActivity(Intent intent, String resolvedType, int startFlags, String profileFile, ParcelFileDescriptor profileFd, int userId) {
        ActivityInfo aInfo;
        try {
            ResolveInfo rInfo = AppGlobals.getPackageManager().resolveIntent(intent, resolvedType, 66560, userId);
            aInfo = rInfo != null ? rInfo.activityInfo : null;
        } catch (RemoteException e) {
            aInfo = null;
        }
        if (aInfo != null) {
            intent.setComponent(new ComponentName(aInfo.applicationInfo.packageName, aInfo.name));
            if ((startFlags & 2) != 0 && !aInfo.processName.equals("system")) {
                this.mService.setDebugApp(aInfo.processName, true, false);
            }
            if ((startFlags & 4) != 0 && !aInfo.processName.equals("system")) {
                this.mService.setOpenGlTraceApp(aInfo.applicationInfo, aInfo.processName);
            }
            if (profileFile != null && !aInfo.processName.equals("system")) {
                this.mService.setProfileApp(aInfo.applicationInfo, aInfo.processName, profileFile, profileFd, (startFlags & 8) != 0);
            }
        }
        return aInfo;
    }

    void startHomeActivity(Intent intent, ActivityInfo aInfo) {
        moveHomeToTop();
        startActivityLocked(null, intent, null, aInfo, null, null, 0, 0, 0, null, 0, null, false, null);
    }

    final int startActivityMayWait(IApplicationThread caller, int callingUid, String callingPackage, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int startFlags, String profileFile, ParcelFileDescriptor profileFd, IActivityManager.WaitResult outResult, Configuration config, Bundle options, int userId) {
        int callingPid;
        if (intent != null && intent.hasFileDescriptors()) {
            throw new IllegalArgumentException("File descriptors passed in Intent");
        }
        boolean componentSpecified = intent.getComponent() != null;
        Intent intent2 = new Intent(intent);
        ActivityInfo aInfo = resolveActivity(intent2, resolvedType, startFlags, profileFile, profileFd, userId);
        synchronized (this.mService) {
            if (callingUid >= 0) {
                callingPid = -1;
            } else if (caller == null) {
                callingPid = Binder.getCallingPid();
                callingUid = Binder.getCallingUid();
            } else {
                callingUid = -1;
                callingPid = -1;
            }
            ActivityStack stack = getFocusedStack();
            stack.mConfigWillChange = (config == null || this.mService.mConfiguration.diff(config) == 0) ? false : true;
            long origId = Binder.clearCallingIdentity();
            if (aInfo != null && (aInfo.applicationInfo.flags & 268435456) != 0 && aInfo.processName.equals(aInfo.applicationInfo.packageName) && this.mService.mHeavyWeightProcess != null && (this.mService.mHeavyWeightProcess.info.uid != aInfo.applicationInfo.uid || !this.mService.mHeavyWeightProcess.processName.equals(aInfo.processName))) {
                int realCallingUid = callingUid;
                if (caller != null) {
                    ProcessRecord callerApp = this.mService.getRecordForAppLocked(caller);
                    if (callerApp != null) {
                        realCallingUid = callerApp.info.uid;
                    } else {
                        Slog.w("ActivityManager", "Unable to find app for caller " + caller + " (pid=" + callingPid + ") when starting: " + intent2.toString());
                        ActivityOptions.abort(options);
                        return -4;
                    }
                }
                IIntentSender target = this.mService.getIntentSenderLocked(2, "android", realCallingUid, userId, (IBinder) null, (String) null, 0, new Intent[]{intent2}, new String[]{resolvedType}, 1342177280, (Bundle) null);
                Intent newIntent = new Intent();
                if (requestCode >= 0) {
                    newIntent.putExtra(HeavyWeightSwitcherActivity.KEY_HAS_RESULT, true);
                }
                newIntent.putExtra("intent", new IntentSender(target));
                if (this.mService.mHeavyWeightProcess.activities.size() > 0) {
                    ActivityRecord hist = this.mService.mHeavyWeightProcess.activities.get(0);
                    newIntent.putExtra(HeavyWeightSwitcherActivity.KEY_CUR_APP, hist.packageName);
                    newIntent.putExtra(HeavyWeightSwitcherActivity.KEY_CUR_TASK, hist.task.taskId);
                }
                newIntent.putExtra(HeavyWeightSwitcherActivity.KEY_NEW_APP, aInfo.packageName);
                newIntent.setFlags(intent2.getFlags());
                newIntent.setClassName("android", HeavyWeightSwitcherActivity.class.getName());
                intent2 = newIntent;
                resolvedType = null;
                caller = null;
                callingUid = Binder.getCallingUid();
                callingPid = Binder.getCallingPid();
                componentSpecified = true;
                try {
                    ResolveInfo rInfo = AppGlobals.getPackageManager().resolveIntent(intent2, null, 66560, userId);
                    aInfo = this.mService.getActivityInfoForUser(rInfo != null ? rInfo.activityInfo : null, userId);
                } catch (RemoteException e) {
                    aInfo = null;
                }
            }
            int res = startActivityLocked(caller, intent2, resolvedType, aInfo, resultTo, resultWho, requestCode, callingPid, callingUid, callingPackage, startFlags, options, componentSpecified, null);
            if (stack.mConfigWillChange) {
                this.mService.enforceCallingPermission(Manifest.permission.CHANGE_CONFIGURATION, "updateConfiguration()");
                stack.mConfigWillChange = false;
                this.mService.updateConfigurationLocked(config, (ActivityRecord) null, false, false);
            }
            Binder.restoreCallingIdentity(origId);
            if (outResult != null) {
                outResult.result = res;
                if (res == 0) {
                    this.mWaitingActivityLaunched.add(outResult);
                    do {
                        try {
                            this.mService.wait();
                        } catch (InterruptedException e2) {
                        }
                        if (outResult.timeout) {
                            break;
                        }
                    } while (outResult.who == null);
                } else if (res == 2) {
                    ActivityRecord r = stack.topRunningActivityLocked(null);
                    if (r.nowVisible) {
                        outResult.timeout = false;
                        outResult.who = new ComponentName(r.info.packageName, r.info.name);
                        outResult.totalTime = 0L;
                        outResult.thisTime = 0L;
                    } else {
                        outResult.thisTime = SystemClock.uptimeMillis();
                        this.mWaitingActivityVisible.add(outResult);
                        do {
                            try {
                                this.mService.wait();
                            } catch (InterruptedException e3) {
                            }
                            if (outResult.timeout) {
                                break;
                            }
                        } while (outResult.who == null);
                    }
                }
            }
            return res;
        }
    }

    final boolean realStartActivityLocked(ActivityRecord r, ProcessRecord app, boolean andResume, boolean checkConfig) throws RemoteException {
        r.startFreezingScreenLocked(app, 0);
        this.mWindowManager.setAppVisibility(r.appToken, true);
        r.startLaunchTickingLocked();
        if (checkConfig) {
            Configuration config = this.mWindowManager.updateOrientationFromAppTokens(this.mService.mConfiguration, r.mayFreezeScreenLocked(app) ? r.appToken : null);
            this.mService.updateConfigurationLocked(config, r, false, false);
        }
        r.app = app;
        app.waitingToKill = null;
        r.launchCount++;
        r.lastLaunchTime = SystemClock.uptimeMillis();
        int idx = app.activities.indexOf(r);
        if (idx < 0) {
            app.activities.add(r);
        }
        this.mService.updateLruProcessLocked(app, true, true);
        ActivityStack stack = r.task.stack;
        try {
            if (app.thread == null) {
                throw new RemoteException();
            }
            List<ResultInfo> results = null;
            List<Intent> newIntents = null;
            if (andResume) {
                results = r.results;
                newIntents = r.newIntents;
            }
            if (andResume) {
                EventLog.writeEvent((int) EventLogTags.AM_RESTART_ACTIVITY, Integer.valueOf(r.userId), Integer.valueOf(System.identityHashCode(r)), Integer.valueOf(r.task.taskId), r.shortComponentName);
            }
            if (r.isHomeActivity() && r.isNotResolverActivity()) {
                this.mService.mHomeProcess = r.task.mActivities.get(0).app;
            }
            this.mService.ensurePackageDexOpt(r.intent.getComponent().getPackageName());
            r.sleeping = false;
            r.forceNewConfig = false;
            this.mService.showAskCompatModeDialogLocked(r);
            r.compat = this.mService.compatibilityInfoForPackageLocked(r.info.applicationInfo);
            String profileFile = null;
            ParcelFileDescriptor profileFd = null;
            boolean profileAutoStop = false;
            if (this.mService.mProfileApp != null && this.mService.mProfileApp.equals(app.processName) && (this.mService.mProfileProc == null || this.mService.mProfileProc == app)) {
                this.mService.mProfileProc = app;
                profileFile = this.mService.mProfileFile;
                profileFd = this.mService.mProfileFd;
                profileAutoStop = this.mService.mAutoStopProfiler;
            }
            app.hasShownUi = true;
            app.pendingUiClean = true;
            if (profileFd != null) {
                try {
                    profileFd = profileFd.dup();
                } catch (IOException e) {
                    if (profileFd != null) {
                        try {
                            profileFd.close();
                        } catch (IOException e2) {
                        }
                        profileFd = null;
                    }
                }
            }
            app.forceProcessStateUpTo(2);
            app.thread.scheduleLaunchActivity(new Intent(r.intent), r.appToken, System.identityHashCode(r), r.info, new Configuration(this.mService.mConfiguration), r.compat, app.repProcState, r.icicle, results, newIntents, !andResume, this.mService.isNextTransitionForward(), profileFile, profileFd, profileAutoStop);
            if ((app.info.flags & 268435456) != 0 && app.processName.equals(app.info.packageName)) {
                if (this.mService.mHeavyWeightProcess != null && this.mService.mHeavyWeightProcess != app) {
                    Slog.w("ActivityManager", "Starting new heavy weight process " + app + " when already running " + this.mService.mHeavyWeightProcess);
                }
                this.mService.mHeavyWeightProcess = app;
                Message msg = this.mService.mHandler.obtainMessage(24);
                msg.obj = r;
                this.mService.mHandler.sendMessage(msg);
            }
            r.launchFailed = false;
            if (stack.updateLRUListLocked(r)) {
                Slog.w("ActivityManager", "Activity " + r + " being launched, but already in LRU list");
            }
            if (andResume) {
                stack.minimalResumeActivityLocked(r);
            } else {
                r.state = ActivityStack.ActivityState.STOPPED;
                r.stopped = true;
            }
            if (isFrontStack(stack)) {
                this.mService.startSetupActivityLocked();
                return true;
            }
            return true;
        } catch (RemoteException e3) {
            if (r.launchFailed) {
                Slog.e("ActivityManager", "Second failure launching " + r.intent.getComponent().flattenToShortString() + ", giving up", e3);
                this.mService.appDiedLocked(app, app.pid, app.thread);
                stack.requestFinishActivityLocked(r.appToken, 0, null, "2nd-crash", false);
                return false;
            }
            app.activities.remove(r);
            throw e3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startSpecificActivityLocked(ActivityRecord r, boolean andResume, boolean checkConfig) {
        ProcessRecord app = this.mService.getProcessRecordLocked(r.processName, r.info.applicationInfo.uid, true);
        r.task.stack.setLaunchTime(r);
        if (app != null && app.thread != null) {
            try {
                app.addPackage(r.info.packageName, this.mService.mProcessStats);
                realStartActivityLocked(r, app, andResume, checkConfig);
                return;
            } catch (RemoteException e) {
                Slog.w("ActivityManager", "Exception when starting activity " + r.intent.getComponent().flattenToShortString(), e);
            }
        }
        this.mService.startProcessLocked(r.processName, r.info.applicationInfo, true, 0, Context.ACTIVITY_SERVICE, r.intent.getComponent(), false, false, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int startActivityLocked(IApplicationThread caller, Intent intent, String resolvedType, ActivityInfo aInfo, IBinder resultTo, String resultWho, int requestCode, int callingPid, int callingUid, String callingPackage, final int startFlags, Bundle options, boolean componentSpecified, ActivityRecord[] outActivity) {
        String msg;
        int err = 0;
        ProcessRecord callerApp = null;
        if (caller != null) {
            callerApp = this.mService.getRecordForAppLocked(caller);
            if (callerApp != null) {
                callingPid = callerApp.pid;
                callingUid = callerApp.info.uid;
            } else {
                Slog.w("ActivityManager", "Unable to find app for caller " + caller + " (pid=" + callingPid + ") when starting: " + intent.toString());
                err = -4;
            }
        }
        if (err == 0) {
            int userId = aInfo != null ? UserHandle.getUserId(aInfo.applicationInfo.uid) : 0;
            Slog.i("ActivityManager", "START u" + userId + " {" + intent.toShortString(true, true, true, false) + "} from pid " + (callerApp != null ? callerApp.pid : callingPid));
        }
        ActivityRecord sourceRecord = null;
        ActivityRecord resultRecord = null;
        if (resultTo != null) {
            sourceRecord = isInAnyStackLocked(resultTo);
            if (sourceRecord != null && requestCode >= 0 && !sourceRecord.finishing) {
                resultRecord = sourceRecord;
            }
        }
        ActivityStack resultStack = resultRecord == null ? null : resultRecord.task.stack;
        int launchFlags = intent.getFlags();
        if ((launchFlags & 33554432) != 0 && sourceRecord != null) {
            if (requestCode >= 0) {
                ActivityOptions.abort(options);
                return -3;
            }
            resultRecord = sourceRecord.resultTo;
            resultWho = sourceRecord.resultWho;
            requestCode = sourceRecord.requestCode;
            sourceRecord.resultTo = null;
            if (resultRecord != null) {
                resultRecord.removeResultsLocked(sourceRecord, resultWho, requestCode);
            }
        }
        if (err == 0 && intent.getComponent() == null) {
            err = -1;
        }
        if (err == 0 && aInfo == null) {
            err = -2;
        }
        if (err != 0) {
            if (resultRecord != null) {
                resultStack.sendActivityResultLocked(-1, resultRecord, resultWho, requestCode, 0, null);
            }
            setDismissKeyguard(false);
            ActivityOptions.abort(options);
            return err;
        }
        int startAnyPerm = this.mService.checkPermission(Manifest.permission.START_ANY_ACTIVITY, callingPid, callingUid);
        int componentPerm = this.mService.checkComponentPermission(aInfo.permission, callingPid, callingUid, aInfo.applicationInfo.uid, aInfo.exported);
        if (startAnyPerm != 0 && componentPerm != 0) {
            if (resultRecord != null) {
                resultStack.sendActivityResultLocked(-1, resultRecord, resultWho, requestCode, 0, null);
            }
            setDismissKeyguard(false);
            if (!aInfo.exported) {
                msg = "Permission Denial: starting " + intent.toString() + " from " + callerApp + " (pid=" + callingPid + ", uid=" + callingUid + Separators.RPAREN + " not exported from uid " + aInfo.applicationInfo.uid;
            } else {
                msg = "Permission Denial: starting " + intent.toString() + " from " + callerApp + " (pid=" + callingPid + ", uid=" + callingUid + Separators.RPAREN + " requires " + aInfo.permission;
            }
            Slog.w("ActivityManager", msg);
            throw new SecurityException(msg);
        }
        boolean abort = !this.mService.mIntentFirewall.checkStartActivity(intent, callingUid, callingPid, resolvedType, aInfo.applicationInfo);
        if (this.mService.mController != null) {
            try {
                Intent watchIntent = intent.cloneFilter();
                abort |= !this.mService.mController.activityStarting(watchIntent, aInfo.applicationInfo.packageName);
            } catch (RemoteException e) {
                this.mService.mController = null;
            }
        }
        if (abort) {
            if (resultRecord != null) {
                resultStack.sendActivityResultLocked(-1, resultRecord, resultWho, requestCode, 0, null);
            }
            setDismissKeyguard(false);
            ActivityOptions.abort(options);
            return 0;
        }
        final ActivityRecord r = new ActivityRecord(this.mService, callerApp, callingUid, callingPackage, intent, resolvedType, aInfo, this.mService.mConfiguration, resultRecord, resultWho, requestCode, componentSpecified, this);
        if (outActivity != null) {
            outActivity[0] = r;
        }
        final ActivityStack stack = getFocusedStack();
        if ((stack.mResumedActivity == null || stack.mResumedActivity.info.applicationInfo.uid != callingUid) && !this.mService.checkAppSwitchAllowedLocked(callingPid, callingUid, "Activity start")) {
            final ActivityRecord activityRecord = sourceRecord;
            this.mService.mPendingActivityLaunches.add(new Object(r, activityRecord, startFlags, stack) { // from class: com.android.server.am.ActivityManagerService$PendingActivityLaunch
                final ActivityRecord r;
                final ActivityRecord sourceRecord;
                final int startFlags;
                final ActivityStack stack;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.r = r;
                    this.sourceRecord = activityRecord;
                    this.startFlags = startFlags;
                    this.stack = stack;
                }
            });
            setDismissKeyguard(false);
            ActivityOptions.abort(options);
            return 4;
        }
        if (this.mService.mDidAppSwitch) {
            this.mService.mAppSwitchesAllowedTime = 0L;
        } else {
            this.mService.mDidAppSwitch = true;
        }
        this.mService.doPendingActivityLaunchesLocked(false);
        int err2 = startActivityUncheckedLocked(r, sourceRecord, startFlags, true, options);
        if (allPausedActivitiesComplete()) {
            dismissKeyguard();
        }
        return err2;
    }

    ActivityStack adjustStackFocus(ActivityRecord r) {
        TaskRecord task = r.task;
        if (r.isApplicationActivity() || (task != null && task.isApplicationTask())) {
            if (task != null) {
                if (this.mFocusedStack != task.stack) {
                    this.mFocusedStack = task.stack;
                }
                return this.mFocusedStack;
            } else if (this.mFocusedStack != null) {
                return this.mFocusedStack;
            } else {
                for (int stackNdx = this.mStacks.size() - 1; stackNdx > 0; stackNdx--) {
                    ActivityStack stack = this.mStacks.get(stackNdx);
                    if (!stack.isHomeStack()) {
                        this.mFocusedStack = stack;
                        return this.mFocusedStack;
                    }
                }
                int stackId = this.mService.createStack(-1, 0, 6, 1.0f);
                this.mFocusedStack = getStack(stackId);
                return this.mFocusedStack;
            }
        }
        return this.mHomeStack;
    }

    void setFocusedStack(ActivityRecord r) {
        if (r == null) {
            return;
        }
        if (!r.isApplicationActivity() || (r.task != null && !r.task.isApplicationTask())) {
            if (this.mStackState != 0) {
                this.mStackState = 3;
                return;
            }
            return;
        }
        this.mFocusedStack = r.task.stack;
        if (this.mStackState != 2) {
            this.mStackState = 1;
        }
    }

    final int startActivityUncheckedLocked(ActivityRecord r, ActivityRecord sourceRecord, int startFlags, boolean doResume, Bundle options) {
        ActivityStack sourceStack;
        ActivityStack targetStack;
        ActivityRecord top;
        Intent intent = r.intent;
        int callingUid = r.launchedFromUid;
        int launchFlags = intent.getFlags();
        this.mUserLeaving = (launchFlags & 262144) == 0;
        if (!doResume) {
            r.delayedResume = true;
        }
        ActivityRecord notTop = (launchFlags & 16777216) != 0 ? r : null;
        if ((startFlags & 1) != 0) {
            ActivityRecord checkedCaller = sourceRecord;
            if (checkedCaller == null) {
                checkedCaller = getFocusedStack().topRunningNonDelayedActivityLocked(notTop);
            }
            if (!checkedCaller.realActivity.equals(r.realActivity)) {
                startFlags &= -2;
            }
        }
        if (sourceRecord == null) {
            if ((launchFlags & 268435456) == 0) {
                Slog.w("ActivityManager", "startActivity called from non-Activity context; forcing Intent.FLAG_ACTIVITY_NEW_TASK for: " + intent);
                launchFlags |= 268435456;
            }
        } else if (sourceRecord.launchMode == 3) {
            launchFlags |= 268435456;
        } else if (r.launchMode == 3 || r.launchMode == 2) {
            launchFlags |= 268435456;
        }
        if (sourceRecord != null) {
            if (sourceRecord.finishing) {
                if ((launchFlags & 268435456) == 0) {
                    Slog.w("ActivityManager", "startActivity called from finishing " + sourceRecord + "; forcing Intent.FLAG_ACTIVITY_NEW_TASK for: " + intent);
                    launchFlags |= 268435456;
                }
                sourceRecord = null;
                sourceStack = null;
            } else {
                sourceStack = sourceRecord.task.stack;
            }
        } else {
            sourceStack = null;
        }
        if (r.resultTo != null && (launchFlags & 268435456) != 0) {
            Slog.w("ActivityManager", "Activity is launching as a new task, so cancelling activity result.");
            r.resultTo.task.stack.sendActivityResultLocked(-1, r.resultTo, r.resultWho, r.requestCode, 0, null);
            r.resultTo = null;
        }
        boolean addingToTask = false;
        boolean movedHome = false;
        TaskRecord reuseTask = null;
        if ((((launchFlags & 268435456) != 0 && (launchFlags & 134217728) == 0) || r.launchMode == 2 || r.launchMode == 3) && r.resultTo == null) {
            ActivityRecord intentActivity = r.launchMode != 3 ? findTaskLocked(r) : findActivityLocked(intent, r.info);
            if (intentActivity != null) {
                if (r.task == null) {
                    r.task = intentActivity.task;
                }
                ActivityStack targetStack2 = intentActivity.task.stack;
                targetStack2.mLastPausedActivity = null;
                moveHomeStack(targetStack2.isHomeStack());
                if (intentActivity.task.intent == null) {
                    intentActivity.task.setIntent(intent, r.info);
                }
                ActivityStack lastStack = getLastStack();
                ActivityRecord curTop = lastStack == null ? null : lastStack.topRunningNonDelayedActivityLocked(notTop);
                if (curTop != null && (curTop.task != intentActivity.task || curTop.task != lastStack.topTask())) {
                    r.intent.addFlags(4194304);
                    if (sourceRecord == null || (sourceStack.topActivity() != null && sourceStack.topActivity().task == sourceRecord.task)) {
                        movedHome = true;
                        if ((launchFlags & 268451840) == 268451840) {
                            intentActivity.task.mOnTopOfHome = true;
                        }
                        targetStack2.moveTaskToFrontLocked(intentActivity.task, r, options);
                        options = null;
                    }
                }
                if ((launchFlags & 2097152) != 0) {
                    intentActivity = targetStack2.resetTaskIfNeededLocked(intentActivity, r);
                }
                if ((startFlags & 1) != 0) {
                    if (doResume) {
                        resumeTopActivitiesLocked(targetStack2, null, options);
                    } else {
                        ActivityOptions.abort(options);
                    }
                    if (r.task == null) {
                        Slog.v("ActivityManager", "startActivityUncheckedLocked: task left null", new RuntimeException("here").fillInStackTrace());
                        return 1;
                    }
                    return 1;
                }
                if ((launchFlags & 268468224) == 268468224) {
                    reuseTask = intentActivity.task;
                    reuseTask.performClearTaskLocked();
                    reuseTask.setIntent(r.intent, r.info);
                } else if ((launchFlags & 67108864) != 0 || r.launchMode == 2 || r.launchMode == 3) {
                    ActivityRecord top2 = intentActivity.task.performClearTaskLocked(r, launchFlags);
                    if (top2 != null) {
                        if (top2.frontOfTask) {
                            top2.task.setIntent(r.intent, r.info);
                        }
                        ActivityStack.logStartActivity(EventLogTags.AM_NEW_INTENT, r, top2.task);
                        top2.deliverNewIntentLocked(callingUid, r.intent);
                    } else {
                        addingToTask = true;
                        sourceRecord = intentActivity;
                    }
                } else if (r.realActivity.equals(intentActivity.task.realActivity)) {
                    if (((launchFlags & 536870912) != 0 || r.launchMode == 1) && intentActivity.realActivity.equals(r.realActivity)) {
                        ActivityStack.logStartActivity(EventLogTags.AM_NEW_INTENT, r, intentActivity.task);
                        if (intentActivity.frontOfTask) {
                            intentActivity.task.setIntent(r.intent, r.info);
                        }
                        intentActivity.deliverNewIntentLocked(callingUid, r.intent);
                    } else if (!r.intent.filterEquals(intentActivity.task.intent)) {
                        addingToTask = true;
                        sourceRecord = intentActivity;
                    }
                } else if ((launchFlags & 2097152) == 0) {
                    addingToTask = true;
                    sourceRecord = intentActivity;
                } else if (!intentActivity.task.rootWasReset) {
                    intentActivity.task.setIntent(r.intent, r.info);
                }
                if (!addingToTask && reuseTask == null) {
                    if (doResume) {
                        targetStack2.resumeTopActivityLocked(null, options);
                    } else {
                        ActivityOptions.abort(options);
                    }
                    if (r.task == null) {
                        Slog.v("ActivityManager", "startActivityUncheckedLocked: task left null", new RuntimeException("here").fillInStackTrace());
                        return 2;
                    }
                    return 2;
                }
            }
        }
        if (r.packageName != null) {
            ActivityStack topStack = getFocusedStack();
            ActivityRecord top3 = topStack.topRunningNonDelayedActivityLocked(notTop);
            if (top3 != null && r.resultTo == null && top3.realActivity.equals(r.realActivity) && top3.userId == r.userId && top3.app != null && top3.app.thread != null && ((launchFlags & 536870912) != 0 || r.launchMode == 1 || r.launchMode == 2)) {
                ActivityStack.logStartActivity(EventLogTags.AM_NEW_INTENT, top3, top3.task);
                topStack.mLastPausedActivity = null;
                if (doResume) {
                    resumeTopActivitiesLocked();
                }
                ActivityOptions.abort(options);
                if ((startFlags & 1) != 0) {
                    if (r.task == null) {
                        Slog.v("ActivityManager", "startActivityUncheckedLocked: task left null", new RuntimeException("here").fillInStackTrace());
                        return 1;
                    }
                    return 1;
                }
                top3.deliverNewIntentLocked(callingUid, r.intent);
                if (r.task == null) {
                    Slog.v("ActivityManager", "startActivityUncheckedLocked: task left null", new RuntimeException("here").fillInStackTrace());
                    return 3;
                }
                return 3;
            }
            boolean newTask = false;
            boolean keepCurTransition = false;
            if (r.resultTo == null && !addingToTask && (launchFlags & 268435456) != 0) {
                targetStack = adjustStackFocus(r);
                moveHomeStack(targetStack.isHomeStack());
                if (reuseTask == null) {
                    r.setTask(targetStack.createTaskRecord(getNextTaskId(), r.info, intent, true), null, true);
                } else {
                    r.setTask(reuseTask, reuseTask, true);
                }
                newTask = true;
                if (!movedHome && (launchFlags & 268451840) == 268451840) {
                    r.task.mOnTopOfHome = true;
                }
            } else if (sourceRecord != null) {
                TaskRecord sourceTask = sourceRecord.task;
                targetStack = sourceTask.stack;
                moveHomeStack(targetStack.isHomeStack());
                if (!addingToTask && (launchFlags & 67108864) != 0) {
                    ActivityRecord top4 = sourceTask.performClearTaskLocked(r, launchFlags);
                    keepCurTransition = true;
                    if (top4 != null) {
                        ActivityStack.logStartActivity(EventLogTags.AM_NEW_INTENT, r, top4.task);
                        top4.deliverNewIntentLocked(callingUid, r.intent);
                        targetStack.mLastPausedActivity = null;
                        if (doResume) {
                            targetStack.resumeTopActivityLocked(null);
                        }
                        ActivityOptions.abort(options);
                        if (r.task == null) {
                            Slog.w("ActivityManager", "startActivityUncheckedLocked: task left null", new RuntimeException("here").fillInStackTrace());
                            return 3;
                        }
                        return 3;
                    }
                } else if (!addingToTask && (launchFlags & 131072) != 0 && (top = sourceTask.findActivityInHistoryLocked(r)) != null) {
                    TaskRecord task = top.task;
                    task.moveActivityToFrontLocked(top);
                    ActivityStack.logStartActivity(EventLogTags.AM_NEW_INTENT, r, task);
                    top.updateOptionsLocked(options);
                    top.deliverNewIntentLocked(callingUid, r.intent);
                    targetStack.mLastPausedActivity = null;
                    if (doResume) {
                        targetStack.resumeTopActivityLocked(null);
                        return 3;
                    }
                    return 3;
                }
                r.setTask(sourceTask, sourceRecord.thumbHolder, false);
            } else {
                targetStack = adjustStackFocus(r);
                moveHomeStack(targetStack.isHomeStack());
                ActivityRecord prev = targetStack.topActivity();
                r.setTask(prev != null ? prev.task : targetStack.createTaskRecord(getNextTaskId(), r.info, intent, true), null, true);
            }
            this.mService.grantUriPermissionFromIntentLocked(callingUid, r.packageName, intent, r.getUriPermissionsLocked());
            if (newTask) {
                EventLog.writeEvent((int) EventLogTags.AM_CREATE_TASK, Integer.valueOf(r.userId), Integer.valueOf(r.task.taskId));
            }
            ActivityStack.logStartActivity(EventLogTags.AM_CREATE_ACTIVITY, r, r.task);
            targetStack.mLastPausedActivity = null;
            targetStack.startActivityLocked(r, newTask, doResume, keepCurTransition, options);
            this.mService.setFocusedActivityLocked(r);
            return 0;
        }
        if (r.resultTo != null) {
            r.resultTo.task.stack.sendActivityResultLocked(-1, r.resultTo, r.resultWho, r.requestCode, 0, null);
        }
        ActivityOptions.abort(options);
        if (r.task == null) {
            Slog.v("ActivityManager", "startActivityUncheckedLocked: task left null", new RuntimeException("here").fillInStackTrace());
            return -2;
        }
        return -2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void acquireLaunchWakelock() {
        this.mLaunchingActivity.acquire();
        if (!this.mHandler.hasMessages(104)) {
            this.mHandler.sendEmptyMessageDelayed(104, 10000L);
        }
    }

    final ActivityRecord activityIdleInternalLocked(final IBinder token, boolean fromTimeout, Configuration config) {
        ArrayList<ActivityRecord> thumbnails;
        ArrayList<ActivityRecord> finishes = null;
        ArrayList<UserStartedState> startingUsers = null;
        IApplicationThread sendThumbnail = null;
        boolean booting = false;
        boolean enableScreen = false;
        boolean activityRemoved = false;
        ActivityRecord r = ActivityRecord.forToken(token);
        if (r != null) {
            this.mHandler.removeMessages(100, r);
            r.finishLaunchTickingLocked();
            if (fromTimeout) {
                reportActivityLaunchedLocked(fromTimeout, r, -1L, -1L);
            }
            if (config != null) {
                r.configuration = config;
            }
            r.idle = true;
            if (r.thumbnailNeeded && r.app != null && r.app.thread != null) {
                sendThumbnail = r.app.thread;
                r.thumbnailNeeded = false;
            }
            if (!this.mService.mBooted && isFrontStack(r.task.stack)) {
                this.mService.mBooted = true;
                enableScreen = true;
            }
        }
        if (allResumedActivitiesIdle()) {
            if (r != null) {
                this.mService.scheduleAppGcsLocked();
            }
            if (this.mLaunchingActivity.isHeld()) {
                this.mHandler.removeMessages(104);
                this.mLaunchingActivity.release();
            }
            ensureActivitiesVisibleLocked(null, 0);
        }
        ArrayList<ActivityRecord> stops = processStoppingActivitiesLocked(true);
        int NS = stops != null ? stops.size() : 0;
        int NF = this.mFinishingActivities.size();
        if (NF > 0) {
            finishes = new ArrayList<>(this.mFinishingActivities);
            this.mFinishingActivities.clear();
        }
        final int NT = this.mCancelledThumbnails.size();
        if (NT > 0) {
            thumbnails = new ArrayList<>(this.mCancelledThumbnails);
            this.mCancelledThumbnails.clear();
        } else {
            thumbnails = null;
        }
        if (isFrontStack(this.mHomeStack)) {
            booting = this.mService.mBooting;
            this.mService.mBooting = false;
        }
        if (this.mStartingUsers.size() > 0) {
            startingUsers = new ArrayList<>(this.mStartingUsers);
            this.mStartingUsers.clear();
        }
        final IApplicationThread thumbnailThread = sendThumbnail;
        final ArrayList<ActivityRecord> arrayList = thumbnails;
        this.mHandler.post(new Runnable() { // from class: com.android.server.am.ActivityStackSupervisor.1
            @Override // java.lang.Runnable
            public void run() {
                if (thumbnailThread != null) {
                    try {
                        thumbnailThread.requestThumbnail(token);
                    } catch (Exception e) {
                        Slog.w("ActivityManager", "Exception thrown when requesting thumbnail", e);
                        ActivityStackSupervisor.this.mService.sendPendingThumbnail((ActivityRecord) null, token, (Bitmap) null, (CharSequence) null, true);
                    }
                }
                for (int i = 0; i < NT; i++) {
                    ActivityRecord r2 = (ActivityRecord) arrayList.get(i);
                    ActivityStackSupervisor.this.mService.sendPendingThumbnail(r2, (IBinder) null, (Bitmap) null, (CharSequence) null, true);
                }
            }
        });
        for (int i = 0; i < NS; i++) {
            r = stops.get(i);
            ActivityStack stack = r.task.stack;
            if (r.finishing) {
                stack.finishCurrentActivityLocked(r, 0, false);
            } else {
                stack.stopActivityLocked(r);
            }
        }
        for (int i2 = 0; i2 < NF; i2++) {
            r = finishes.get(i2);
            activityRemoved |= r.task.stack.destroyActivityLocked(r, true, false, "finish-idle");
        }
        if (booting) {
            this.mService.finishBooting();
        } else if (startingUsers != null) {
            for (int i3 = 0; i3 < startingUsers.size(); i3++) {
                this.mService.finishUserSwitch(startingUsers.get(i3));
            }
        }
        this.mService.trimApplications();
        if (enableScreen) {
            this.mService.enableScreenAfterBoot();
        }
        if (activityRemoved) {
            resumeTopActivitiesLocked();
        }
        return r;
    }

    boolean handleAppDiedLocked(ProcessRecord app) {
        boolean hasVisibleActivities = false;
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            hasVisibleActivities |= this.mStacks.get(stackNdx).handleAppDiedLocked(app);
        }
        return hasVisibleActivities;
    }

    void closeSystemDialogsLocked() {
        int numStacks = this.mStacks.size();
        for (int stackNdx = 0; stackNdx < numStacks; stackNdx++) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            stack.closeSystemDialogsLocked();
        }
    }

    void removeUserLocked(int userId) {
        this.mUserStackInFront.delete(userId);
    }

    boolean forceStopPackageLocked(String name, boolean doit, boolean evenPersistent, int userId) {
        boolean didSomething = false;
        int numStacks = this.mStacks.size();
        for (int stackNdx = 0; stackNdx < numStacks; stackNdx++) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (stack.forceStopPackageLocked(name, doit, evenPersistent, userId)) {
                didSomething = true;
            }
        }
        return didSomething;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updatePreviousProcessLocked(ActivityRecord r) {
        ProcessRecord fgApp = null;
        int stackNdx = this.mStacks.size() - 1;
        while (true) {
            if (stackNdx < 0) {
                break;
            }
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (!isFrontStack(stack)) {
                stackNdx--;
            } else if (stack.mResumedActivity != null) {
                fgApp = stack.mResumedActivity.app;
            } else if (stack.mPausingActivity != null) {
                fgApp = stack.mPausingActivity.app;
            }
        }
        if (r.app != null && fgApp != null && r.app != fgApp && r.lastVisibleTime > this.mService.mPreviousProcessVisibleTime && r.app != this.mService.mHomeProcess) {
            this.mService.mPreviousProcess = r.app;
            this.mService.mPreviousProcessVisibleTime = r.lastVisibleTime;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean resumeTopActivitiesLocked() {
        return resumeTopActivitiesLocked(null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean resumeTopActivitiesLocked(ActivityStack targetStack, ActivityRecord target, Bundle targetOptions) {
        if (targetStack == null) {
            targetStack = getFocusedStack();
        }
        boolean result = false;
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (isFrontStack(stack)) {
                if (stack == targetStack) {
                    result = stack.resumeTopActivityLocked(target, targetOptions);
                } else {
                    stack.resumeTopActivityLocked(null);
                }
            }
        }
        return result;
    }

    void finishTopRunningActivityLocked(ProcessRecord app) {
        int numStacks = this.mStacks.size();
        for (int stackNdx = 0; stackNdx < numStacks; stackNdx++) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            stack.finishTopRunningActivityLocked(app);
        }
    }

    void findTaskToMoveToFrontLocked(int taskId, int flags, Bundle options) {
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0 && !this.mStacks.get(stackNdx).findTaskToMoveToFrontLocked(taskId, flags, options); stackNdx--) {
        }
    }

    ActivityStack getStack(int stackId) {
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (stack.getStackId() == stackId) {
                return stack;
            }
        }
        return null;
    }

    ArrayList<ActivityStack> getStacks() {
        return new ArrayList<>(this.mStacks);
    }

    int createStack() {
        do {
            int i = this.mLastStackId + 1;
            this.mLastStackId = i;
            if (i <= 0) {
                this.mLastStackId = 1;
            }
        } while (getStack(this.mLastStackId) != null);
        this.mStacks.add(new ActivityStack(this.mService, this.mContext, this.mLooper, this.mLastStackId));
        return this.mLastStackId;
    }

    void moveTaskToStack(int taskId, int stackId, boolean toTop) {
        TaskRecord task = anyTaskForIdLocked(taskId);
        if (task == null) {
            return;
        }
        ActivityStack stack = getStack(stackId);
        if (stack == null) {
            Slog.w("ActivityManager", "moveTaskToStack: no stack for id=" + stackId);
            return;
        }
        removeTask(task);
        stack.addTask(task, toTop);
        this.mWindowManager.addTask(taskId, stackId, toTop);
        resumeTopActivitiesLocked();
    }

    ActivityRecord findTaskLocked(ActivityRecord r) {
        ActivityRecord ar;
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if ((r.isApplicationActivity() || stack.isHomeStack()) && (ar = stack.findTaskLocked(r)) != null) {
                return ar;
            }
        }
        return null;
    }

    ActivityRecord findActivityLocked(Intent intent, ActivityInfo info) {
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityRecord ar = this.mStacks.get(stackNdx).findActivityLocked(intent, info);
            if (ar != null) {
                return ar;
            }
        }
        return null;
    }

    void goingToSleepLocked() {
        scheduleSleepTimeout();
        if (!this.mGoingToSleep.isHeld()) {
            this.mGoingToSleep.acquire();
            if (this.mLaunchingActivity.isHeld()) {
                this.mLaunchingActivity.release();
                this.mService.mHandler.removeMessages(104);
            }
        }
        checkReadyForSleepLocked();
    }

    boolean shutdownLocked(int timeout) {
        boolean timedout = false;
        goingToSleepLocked();
        long endTime = System.currentTimeMillis() + timeout;
        while (true) {
            boolean cantShutdown = false;
            for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
                cantShutdown |= this.mStacks.get(stackNdx).checkReadyForSleepLocked();
            }
            if (!cantShutdown) {
                break;
            }
            long timeRemaining = endTime - System.currentTimeMillis();
            if (timeRemaining > 0) {
                try {
                    this.mService.wait(timeRemaining);
                } catch (InterruptedException e) {
                }
            } else {
                Slog.w("ActivityManager", "Activity manager shutdown timed out");
                timedout = true;
                break;
            }
        }
        this.mSleepTimeout = true;
        checkReadyForSleepLocked();
        return timedout;
    }

    void comeOutOfSleepIfNeededLocked() {
        removeSleepTimeouts();
        if (this.mGoingToSleep.isHeld()) {
            this.mGoingToSleep.release();
        }
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            stack.awakeFromSleepingLocked();
            if (isFrontStack(stack)) {
                resumeTopActivitiesLocked();
            }
        }
        this.mGoingToSleepActivities.clear();
    }

    void activitySleptLocked(ActivityRecord r) {
        this.mGoingToSleepActivities.remove(r);
        checkReadyForSleepLocked();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkReadyForSleepLocked() {
        if (!this.mService.isSleepingOrShuttingDown()) {
            return;
        }
        if (!this.mSleepTimeout) {
            boolean dontSleep = false;
            for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
                dontSleep |= this.mStacks.get(stackNdx).checkReadyForSleepLocked();
            }
            if (this.mStoppingActivities.size() > 0) {
                scheduleIdleLocked();
                dontSleep = true;
            }
            if (this.mGoingToSleepActivities.size() > 0) {
                dontSleep = true;
            }
            if (dontSleep) {
                return;
            }
        }
        for (int stackNdx2 = this.mStacks.size() - 1; stackNdx2 >= 0; stackNdx2--) {
            this.mStacks.get(stackNdx2).goToSleep();
        }
        removeSleepTimeouts();
        if (this.mGoingToSleep.isHeld()) {
            this.mGoingToSleep.release();
        }
        if (this.mService.mShuttingDown) {
            this.mService.notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean reportResumedActivityLocked(ActivityRecord r) {
        ActivityStack stack = r.task.stack;
        if (isFrontStack(stack)) {
            this.mService.updateUsageStats(r, true);
        }
        if (allResumedActivitiesComplete()) {
            ensureActivitiesVisibleLocked(null, 0);
            this.mWindowManager.executeAppTransition();
            return true;
        }
        return false;
    }

    void handleAppCrashLocked(ProcessRecord app) {
        int numStacks = this.mStacks.size();
        for (int stackNdx = 0; stackNdx < numStacks; stackNdx++) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            stack.handleAppCrashLocked(app);
        }
    }

    void ensureActivitiesVisibleLocked(ActivityRecord starting, int configChanges) {
        boolean showHomeBehindStack = false;
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            if (isFrontStack(stack)) {
                showHomeBehindStack = stack.ensureActivitiesVisibleLocked(starting, configChanges);
            }
        }
        for (int stackNdx2 = this.mStacks.size() - 1; stackNdx2 >= 0; stackNdx2--) {
            ActivityStack stack2 = this.mStacks.get(stackNdx2);
            if (!isFrontStack(stack2)) {
                stack2.ensureActivitiesVisibleLocked(starting, configChanges, showHomeBehindStack);
            }
        }
    }

    void scheduleDestroyAllActivities(ProcessRecord app, String reason) {
        int numStacks = this.mStacks.size();
        for (int stackNdx = 0; stackNdx < numStacks; stackNdx++) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            stack.scheduleDestroyActivities(app, false, reason);
        }
    }

    boolean switchUserLocked(int userId, UserStartedState uss) {
        this.mUserStackInFront.put(this.mCurrentUser, getFocusedStack().getStackId());
        int restoreStackId = this.mUserStackInFront.get(userId, 0);
        this.mCurrentUser = userId;
        this.mStartingUsers.add(uss);
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            this.mStacks.get(stackNdx).switchUserLocked(userId);
        }
        ActivityStack stack = getStack(restoreStackId);
        if (stack == null) {
            stack = this.mHomeStack;
        }
        boolean homeInFront = stack.isHomeStack();
        moveHomeStack(homeInFront);
        this.mWindowManager.moveTaskToTop(stack.topTask().taskId);
        return homeInFront;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ArrayList<ActivityRecord> processStoppingActivitiesLocked(boolean remove) {
        int N = this.mStoppingActivities.size();
        if (N <= 0) {
            return null;
        }
        ArrayList<ActivityRecord> stops = null;
        boolean nowVisible = allResumedActivitiesVisible();
        int i = 0;
        while (i < N) {
            ActivityRecord s = this.mStoppingActivities.get(i);
            if (s.waitingVisible && nowVisible) {
                this.mWaitingVisibleActivities.remove(s);
                s.waitingVisible = false;
                if (s.finishing) {
                    this.mWindowManager.setAppVisibility(s.appToken, false);
                }
            }
            if ((!s.waitingVisible || this.mService.isSleepingOrShuttingDown()) && remove) {
                if (stops == null) {
                    stops = new ArrayList<>();
                }
                stops.add(s);
                this.mStoppingActivities.remove(i);
                N--;
                i--;
            }
            i++;
        }
        return stops;
    }

    void validateTopActivitiesLocked() {
        for (int stackNdx = this.mStacks.size() - 1; stackNdx >= 0; stackNdx--) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            ActivityRecord r = stack.topRunningActivityLocked(null);
            ActivityStack.ActivityState state = r == null ? ActivityStack.ActivityState.DESTROYED : r.state;
            if (isFrontStack(stack)) {
                if (r == null) {
                    Slog.e("ActivityManager", "validateTop...: null top activity, stack=" + stack);
                } else {
                    ActivityRecord pausing = stack.mPausingActivity;
                    if (pausing != null && pausing == r) {
                        Slog.e("ActivityManager", "validateTop...: top stack has pausing activity r=" + r + " state=" + state);
                    }
                    if (state != ActivityStack.ActivityState.INITIALIZING && state != ActivityStack.ActivityState.RESUMED) {
                        Slog.e("ActivityManager", "validateTop...: activity in front not resumed r=" + r + " state=" + state);
                    }
                }
            } else {
                ActivityRecord resumed = stack.mResumedActivity;
                if (resumed != null && resumed == r) {
                    Slog.e("ActivityManager", "validateTop...: back stack has resumed activity r=" + r + " state=" + state);
                }
                if (r != null && (state == ActivityStack.ActivityState.INITIALIZING || state == ActivityStack.ActivityState.RESUMED)) {
                    Slog.e("ActivityManager", "validateTop...: activity in back resumed r=" + r + " state=" + state);
                }
            }
        }
    }

    private static String stackStateToString(int stackState) {
        switch (stackState) {
            case 0:
                return "STACK_STATE_HOME_IN_FRONT";
            case 1:
                return "STACK_STATE_HOME_TO_BACK";
            case 2:
                return "STACK_STATE_HOME_IN_BACK";
            case 3:
                return "STACK_STATE_HOME_TO_FRONT";
            default:
                return "Unknown stackState=" + stackState;
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("mDismissKeyguardOnNextActivity:");
        pw.println(this.mDismissKeyguardOnNextActivity);
        pw.print(prefix);
        pw.print("mStackState=");
        pw.println(stackStateToString(this.mStackState));
        pw.print(prefix);
        pw.println("mSleepTimeout: " + this.mSleepTimeout);
        pw.print(prefix);
        pw.println("mCurTaskId: " + this.mCurTaskId);
        pw.print(prefix);
        pw.println("mUserStackInFront: " + this.mUserStackInFront);
    }

    ArrayList<ActivityRecord> getDumpActivitiesLocked(String name) {
        return getFocusedStack().getDumpActivitiesLocked(name);
    }

    static boolean printThisActivity(PrintWriter pw, ActivityRecord activity, String dumpPackage, boolean needSep, String prefix) {
        if (activity != null) {
            if (dumpPackage == null || dumpPackage.equals(activity.packageName)) {
                if (needSep) {
                    pw.println();
                }
                pw.print(prefix);
                pw.println(activity);
                return true;
            }
            return false;
        }
        return false;
    }

    boolean dumpActivitiesLocked(FileDescriptor fd, PrintWriter pw, boolean dumpAll, boolean dumpClient, String dumpPackage) {
        boolean printed = false;
        boolean needSep = false;
        int numStacks = this.mStacks.size();
        for (int stackNdx = 0; stackNdx < numStacks; stackNdx++) {
            ActivityStack stack = this.mStacks.get(stackNdx);
            StringBuilder stackHeader = new StringBuilder(128);
            stackHeader.append("  Stack #");
            stackHeader.append(this.mStacks.indexOf(stack));
            stackHeader.append(Separators.COLON);
            printed = printed | stack.dumpActivitiesLocked(fd, pw, dumpAll, dumpClient, dumpPackage, needSep, stackHeader.toString()) | dumpHistoryList(fd, pw, stack.mLRUActivities, "    ", "Run", false, !dumpAll, false, dumpPackage, true, "    Running activities (most recent first):", null);
            boolean needSep2 = printed;
            boolean pr = printThisActivity(pw, stack.mPausingActivity, dumpPackage, needSep2, "    mPausingActivity: ");
            if (pr) {
                printed = true;
                needSep2 = false;
            }
            boolean pr2 = printThisActivity(pw, stack.mResumedActivity, dumpPackage, needSep2, "    mResumedActivity: ");
            if (pr2) {
                printed = true;
                needSep2 = false;
            }
            if (dumpAll) {
                boolean pr3 = printThisActivity(pw, stack.mLastPausedActivity, dumpPackage, needSep2, "    mLastPausedActivity: ");
                if (pr3) {
                    printed = true;
                    needSep2 = true;
                }
                printed |= printThisActivity(pw, stack.mLastNoHistoryActivity, dumpPackage, needSep2, "    mLastNoHistoryActivity: ");
            }
            needSep = printed;
        }
        return printed | dumpHistoryList(fd, pw, this.mFinishingActivities, "  ", "Fin", false, !dumpAll, false, dumpPackage, true, "  Activities waiting to finish:", null) | dumpHistoryList(fd, pw, this.mStoppingActivities, "  ", "Stop", false, !dumpAll, false, dumpPackage, true, "  Activities waiting to stop:", null) | dumpHistoryList(fd, pw, this.mWaitingVisibleActivities, "  ", "Wait", false, !dumpAll, false, dumpPackage, true, "  Activities waiting for another to become visible:", null) | dumpHistoryList(fd, pw, this.mGoingToSleepActivities, "  ", "Sleep", false, !dumpAll, false, dumpPackage, true, "  Activities waiting to sleep:", null) | dumpHistoryList(fd, pw, this.mGoingToSleepActivities, "  ", "Sleep", false, !dumpAll, false, dumpPackage, true, "  Activities waiting to sleep:", null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void scheduleIdleTimeoutLocked(ActivityRecord next) {
        Message msg = this.mHandler.obtainMessage(100, next);
        this.mHandler.sendMessageDelayed(msg, 10000L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void scheduleIdleLocked() {
        this.mHandler.sendEmptyMessage(101);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeTimeoutsForActivityLocked(ActivityRecord r) {
        this.mHandler.removeMessages(100, r);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void scheduleResumeTopActivities() {
        this.mHandler.sendEmptyMessage(102);
    }

    void removeSleepTimeouts() {
        this.mSleepTimeout = false;
        this.mHandler.removeMessages(103);
    }

    final void scheduleSleepTimeout() {
        removeSleepTimeouts();
        this.mHandler.sendEmptyMessageDelayed(103, TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: ActivityStackSupervisor$ActivityStackSupervisorHandler.class */
    public final class ActivityStackSupervisorHandler extends Handler {
        public ActivityStackSupervisorHandler(Looper looper) {
            super(looper);
        }

        void activityIdleInternal(ActivityRecord r) {
            synchronized (ActivityStackSupervisor.this.mService) {
                ActivityStackSupervisor.this.activityIdleInternalLocked(r != null ? r.appToken : null, true, null);
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    if (ActivityStackSupervisor.this.mService.mDidDexOpt) {
                        ActivityStackSupervisor.this.mService.mDidDexOpt = false;
                        Message nmsg = ActivityStackSupervisor.this.mHandler.obtainMessage(100);
                        nmsg.obj = msg.obj;
                        ActivityStackSupervisor.this.mHandler.sendMessageDelayed(nmsg, 10000L);
                        return;
                    }
                    activityIdleInternal((ActivityRecord) msg.obj);
                    return;
                case 101:
                    activityIdleInternal((ActivityRecord) msg.obj);
                    return;
                case 102:
                    synchronized (ActivityStackSupervisor.this.mService) {
                        ActivityStackSupervisor.this.resumeTopActivitiesLocked();
                    }
                    return;
                case 103:
                    synchronized (ActivityStackSupervisor.this.mService) {
                        if (ActivityStackSupervisor.this.mService.isSleepingOrShuttingDown()) {
                            Slog.w("ActivityManager", "Sleep timeout!  Sleeping now.");
                            ActivityStackSupervisor.this.mSleepTimeout = true;
                            ActivityStackSupervisor.this.checkReadyForSleepLocked();
                        }
                    }
                    return;
                case 104:
                    if (ActivityStackSupervisor.this.mService.mDidDexOpt) {
                        ActivityStackSupervisor.this.mService.mDidDexOpt = false;
                        ActivityStackSupervisor.this.mHandler.sendEmptyMessageDelayed(104, 10000L);
                        return;
                    }
                    synchronized (ActivityStackSupervisor.this.mService) {
                        if (ActivityStackSupervisor.this.mLaunchingActivity.isHeld()) {
                            Slog.w("ActivityManager", "Launch timeout has expired, giving up wake lock!");
                            ActivityStackSupervisor.this.mLaunchingActivity.release();
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    }
}