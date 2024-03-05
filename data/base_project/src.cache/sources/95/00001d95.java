package com.android.server.am;

import android.app.ActivityOptions;
import android.app.ResultInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.TimeUtils;
import android.view.IApplicationToken;
import com.android.internal.R;
import com.android.internal.app.ResolverActivity;
import com.android.server.AttributeCache;
import com.android.server.am.ActivityStack;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: ActivityRecord.class */
public final class ActivityRecord {
    static final String TAG = "ActivityManager";
    static final boolean DEBUG_SAVED_STATE = false;
    public static final String RECENTS_PACKAGE_NAME = "com.android.systemui.recent";
    final ActivityManagerService service;
    final ActivityInfo info;
    final int launchedFromUid;
    final String launchedFromPackage;
    final int userId;
    final Intent intent;
    final ComponentName realActivity;
    final String shortComponentName;
    final String resolvedType;
    final String packageName;
    final String processName;
    final String taskAffinity;
    final boolean stateNotNeeded;
    boolean fullscreen;
    final boolean noDisplay;
    final boolean componentSpecified;
    static final int APPLICATION_ACTIVITY_TYPE = 0;
    static final int HOME_ACTIVITY_TYPE = 1;
    static final int RECENTS_ACTIVITY_TYPE = 2;
    int mActivityType;
    final String baseDir;
    final String resDir;
    final String dataDir;
    CharSequence nonLocalizedLabel;
    int labelRes;
    int icon;
    int logo;
    int theme;
    int realTheme;
    int windowFlags;
    TaskRecord task;
    ThumbnailHolder thumbHolder;
    long displayStartTime;
    long fullyDrawnStartTime;
    long startTime;
    long lastVisibleTime;
    long cpuTimeAtResume;
    long pauseTime;
    long launchTickTime;
    Configuration configuration;
    CompatibilityInfo compat;
    ActivityRecord resultTo;
    final String resultWho;
    final int requestCode;
    ArrayList<ResultInfo> results;
    HashSet<WeakReference<PendingIntentRecord>> pendingResults;
    ArrayList<Intent> newIntents;
    ActivityOptions pendingOptions;
    HashSet<ConnectionRecord> connections;
    UriPermissionOwner uriPermissions;
    ProcessRecord app;
    Bundle icicle;
    int configChangeFlags;
    int launchMode;
    boolean sleeping;
    boolean frozenBeforeDestroy;
    boolean immersive;
    boolean forceNewConfig;
    int launchCount;
    long lastLaunchTime;
    String stringName;
    final ActivityStackSupervisor mStackSupervisor;
    final IApplicationToken.Stub appToken = new Token(this);
    ActivityStack.ActivityState state = ActivityStack.ActivityState.INITIALIZING;
    boolean frontOfTask = false;
    boolean launchFailed = false;
    boolean stopped = false;
    boolean delayedResume = false;
    boolean finishing = false;
    boolean configDestroy = false;
    boolean keysPaused = false;
    private boolean inHistory = false;
    boolean visible = true;
    boolean waitingVisible = false;
    boolean nowVisible = false;
    boolean thumbnailNeeded = false;
    boolean idle = false;
    boolean hasBeenLaunched = false;
    boolean haveState = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        long now = SystemClock.uptimeMillis();
        pw.print(prefix);
        pw.print("packageName=");
        pw.print(this.packageName);
        pw.print(" processName=");
        pw.println(this.processName);
        pw.print(prefix);
        pw.print("launchedFromUid=");
        pw.print(this.launchedFromUid);
        pw.print(" launchedFromPackage=");
        pw.print(this.launchedFromPackage);
        pw.print(" userId=");
        pw.println(this.userId);
        pw.print(prefix);
        pw.print("app=");
        pw.println(this.app);
        pw.print(prefix);
        pw.println(this.intent.toInsecureStringWithClip());
        pw.print(prefix);
        pw.print("frontOfTask=");
        pw.print(this.frontOfTask);
        pw.print(" task=");
        pw.println(this.task);
        pw.print(prefix);
        pw.print("taskAffinity=");
        pw.println(this.taskAffinity);
        pw.print(prefix);
        pw.print("realActivity=");
        pw.println(this.realActivity.flattenToShortString());
        pw.print(prefix);
        pw.print("baseDir=");
        pw.println(this.baseDir);
        if (!this.resDir.equals(this.baseDir)) {
            pw.print(prefix);
            pw.print("resDir=");
            pw.println(this.resDir);
        }
        pw.print(prefix);
        pw.print("dataDir=");
        pw.println(this.dataDir);
        pw.print(prefix);
        pw.print("stateNotNeeded=");
        pw.print(this.stateNotNeeded);
        pw.print(" componentSpecified=");
        pw.print(this.componentSpecified);
        pw.print(" mActivityType=");
        pw.println(this.mActivityType);
        pw.print(prefix);
        pw.print("compat=");
        pw.print(this.compat);
        pw.print(" labelRes=0x");
        pw.print(Integer.toHexString(this.labelRes));
        pw.print(" icon=0x");
        pw.print(Integer.toHexString(this.icon));
        pw.print(" theme=0x");
        pw.println(Integer.toHexString(this.theme));
        pw.print(prefix);
        pw.print("config=");
        pw.println(this.configuration);
        if (this.resultTo != null || this.resultWho != null) {
            pw.print(prefix);
            pw.print("resultTo=");
            pw.print(this.resultTo);
            pw.print(" resultWho=");
            pw.print(this.resultWho);
            pw.print(" resultCode=");
            pw.println(this.requestCode);
        }
        if (this.results != null) {
            pw.print(prefix);
            pw.print("results=");
            pw.println(this.results);
        }
        if (this.pendingResults != null && this.pendingResults.size() > 0) {
            pw.print(prefix);
            pw.println("Pending Results:");
            Iterator i$ = this.pendingResults.iterator();
            while (i$.hasNext()) {
                WeakReference<PendingIntentRecord> wpir = i$.next();
                PendingIntentRecord pir = wpir != null ? wpir.get() : null;
                pw.print(prefix);
                pw.print("  - ");
                if (pir == null) {
                    pw.println("null");
                } else {
                    pw.println(pir);
                    pir.dump(pw, prefix + "    ");
                }
            }
        }
        if (this.newIntents != null && this.newIntents.size() > 0) {
            pw.print(prefix);
            pw.println("Pending New Intents:");
            for (int i = 0; i < this.newIntents.size(); i++) {
                Intent intent = this.newIntents.get(i);
                pw.print(prefix);
                pw.print("  - ");
                if (intent == null) {
                    pw.println("null");
                } else {
                    pw.println(intent.toShortString(false, true, false, true));
                }
            }
        }
        if (this.pendingOptions != null) {
            pw.print(prefix);
            pw.print("pendingOptions=");
            pw.println(this.pendingOptions);
        }
        if (this.uriPermissions != null) {
            if (this.uriPermissions.readUriPermissions != null) {
                pw.print(prefix);
                pw.print("readUriPermissions=");
                pw.println(this.uriPermissions.readUriPermissions);
            }
            if (this.uriPermissions.writeUriPermissions != null) {
                pw.print(prefix);
                pw.print("writeUriPermissions=");
                pw.println(this.uriPermissions.writeUriPermissions);
            }
        }
        pw.print(prefix);
        pw.print("launchFailed=");
        pw.print(this.launchFailed);
        pw.print(" launchCount=");
        pw.print(this.launchCount);
        pw.print(" lastLaunchTime=");
        if (this.lastLaunchTime == 0) {
            pw.print("0");
        } else {
            TimeUtils.formatDuration(this.lastLaunchTime, now, pw);
        }
        pw.println();
        pw.print(prefix);
        pw.print("haveState=");
        pw.print(this.haveState);
        pw.print(" icicle=");
        pw.println(this.icicle);
        pw.print(prefix);
        pw.print("state=");
        pw.print(this.state);
        pw.print(" stopped=");
        pw.print(this.stopped);
        pw.print(" delayedResume=");
        pw.print(this.delayedResume);
        pw.print(" finishing=");
        pw.println(this.finishing);
        pw.print(prefix);
        pw.print("keysPaused=");
        pw.print(this.keysPaused);
        pw.print(" inHistory=");
        pw.print(this.inHistory);
        pw.print(" visible=");
        pw.print(this.visible);
        pw.print(" sleeping=");
        pw.print(this.sleeping);
        pw.print(" idle=");
        pw.println(this.idle);
        pw.print(prefix);
        pw.print("fullscreen=");
        pw.print(this.fullscreen);
        pw.print(" noDisplay=");
        pw.print(this.noDisplay);
        pw.print(" immersive=");
        pw.print(this.immersive);
        pw.print(" launchMode=");
        pw.println(this.launchMode);
        pw.print(prefix);
        pw.print("frozenBeforeDestroy=");
        pw.print(this.frozenBeforeDestroy);
        pw.print(" thumbnailNeeded=");
        pw.print(this.thumbnailNeeded);
        pw.print(" forceNewConfig=");
        pw.println(this.forceNewConfig);
        pw.print(prefix);
        pw.print("mActivityType=");
        pw.println(activityTypeToString(this.mActivityType));
        pw.print(prefix);
        pw.print("thumbHolder: ");
        pw.print(Integer.toHexString(System.identityHashCode(this.thumbHolder)));
        if (this.thumbHolder != null) {
            pw.print(" bm=");
            pw.print(this.thumbHolder.lastThumbnail);
            pw.print(" desc=");
            pw.print(this.thumbHolder.lastDescription);
        }
        pw.println();
        if (this.displayStartTime != 0 || this.startTime != 0) {
            pw.print(prefix);
            pw.print("displayStartTime=");
            if (this.displayStartTime == 0) {
                pw.print("0");
            } else {
                TimeUtils.formatDuration(this.displayStartTime, now, pw);
            }
            pw.print(" startTime=");
            if (this.startTime == 0) {
                pw.print("0");
            } else {
                TimeUtils.formatDuration(this.startTime, now, pw);
            }
            pw.println();
        }
        if (this.lastVisibleTime != 0 || this.waitingVisible || this.nowVisible) {
            pw.print(prefix);
            pw.print("waitingVisible=");
            pw.print(this.waitingVisible);
            pw.print(" nowVisible=");
            pw.print(this.nowVisible);
            pw.print(" lastVisibleTime=");
            if (this.lastVisibleTime == 0) {
                pw.print("0");
            } else {
                TimeUtils.formatDuration(this.lastVisibleTime, now, pw);
            }
            pw.println();
        }
        if (this.configDestroy || this.configChangeFlags != 0) {
            pw.print(prefix);
            pw.print("configDestroy=");
            pw.print(this.configDestroy);
            pw.print(" configChangeFlags=");
            pw.println(Integer.toHexString(this.configChangeFlags));
        }
        if (this.connections != null) {
            pw.print(prefix);
            pw.print("connections=");
            pw.println(this.connections);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ActivityRecord$Token.class */
    public static class Token extends IApplicationToken.Stub {
        final WeakReference<ActivityRecord> weakActivity;

        Token(ActivityRecord activity) {
            this.weakActivity = new WeakReference<>(activity);
        }

        @Override // android.view.IApplicationToken
        public void windowsDrawn() {
            ActivityRecord activity = this.weakActivity.get();
            if (activity != null) {
                activity.windowsDrawn();
            }
        }

        @Override // android.view.IApplicationToken
        public void windowsVisible() {
            ActivityRecord activity = this.weakActivity.get();
            if (activity != null) {
                activity.windowsVisible();
            }
        }

        @Override // android.view.IApplicationToken
        public void windowsGone() {
            ActivityRecord activity = this.weakActivity.get();
            if (activity != null) {
                activity.windowsGone();
            }
        }

        @Override // android.view.IApplicationToken
        public boolean keyDispatchingTimedOut(String reason) {
            ActivityRecord activity = this.weakActivity.get();
            return activity != null && activity.keyDispatchingTimedOut(reason);
        }

        @Override // android.view.IApplicationToken
        public long getKeyDispatchingTimeout() {
            ActivityRecord activity = this.weakActivity.get();
            if (activity != null) {
                return activity.getKeyDispatchingTimeout();
            }
            return 0L;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Token{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            sb.append(this.weakActivity.get());
            sb.append('}');
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ActivityRecord forToken(IBinder token) {
        if (token != null) {
            try {
                return ((Token) token).weakActivity.get();
            } catch (ClassCastException e) {
                Slog.w(TAG, "Bad activity token: " + token, e);
                return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isNotResolverActivity() {
        return !ResolverActivity.class.getName().equals(this.realActivity.getClassName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityRecord(ActivityManagerService _service, ProcessRecord _caller, int _launchedFromUid, String _launchedFromPackage, Intent _intent, String _resolvedType, ActivityInfo aInfo, Configuration _configuration, ActivityRecord _resultTo, String _resultWho, int _reqCode, boolean _componentSpecified, ActivityStackSupervisor supervisor) {
        this.service = _service;
        this.info = aInfo;
        this.launchedFromUid = _launchedFromUid;
        this.launchedFromPackage = _launchedFromPackage;
        this.userId = UserHandle.getUserId(aInfo.applicationInfo.uid);
        this.intent = _intent;
        this.shortComponentName = _intent.getComponent().flattenToShortString();
        this.resolvedType = _resolvedType;
        this.componentSpecified = _componentSpecified;
        this.configuration = _configuration;
        this.resultTo = _resultTo;
        this.resultWho = _resultWho;
        this.requestCode = _reqCode;
        this.mStackSupervisor = supervisor;
        if (aInfo != null) {
            if (aInfo.targetActivity == null || aInfo.launchMode == 0 || aInfo.launchMode == 1) {
                this.realActivity = _intent.getComponent();
            } else {
                this.realActivity = new ComponentName(aInfo.packageName, aInfo.targetActivity);
            }
            this.taskAffinity = aInfo.taskAffinity;
            this.stateNotNeeded = (aInfo.flags & 16) != 0;
            this.baseDir = aInfo.applicationInfo.sourceDir;
            this.resDir = aInfo.applicationInfo.publicSourceDir;
            this.dataDir = aInfo.applicationInfo.dataDir;
            this.nonLocalizedLabel = aInfo.nonLocalizedLabel;
            this.labelRes = aInfo.labelRes;
            if (this.nonLocalizedLabel == null && this.labelRes == 0) {
                ApplicationInfo app = aInfo.applicationInfo;
                this.nonLocalizedLabel = app.nonLocalizedLabel;
                this.labelRes = app.labelRes;
            }
            this.icon = aInfo.getIconResource();
            this.logo = aInfo.getLogoResource();
            this.theme = aInfo.getThemeResource();
            this.realTheme = this.theme;
            if (this.realTheme == 0) {
                this.realTheme = aInfo.applicationInfo.targetSdkVersion < 11 ? 16973829 : 16973931;
            }
            if ((aInfo.flags & 512) != 0) {
                this.windowFlags |= 16777216;
            }
            if ((aInfo.flags & 1) != 0 && _caller != null && (aInfo.applicationInfo.uid == 1000 || aInfo.applicationInfo.uid == _caller.info.uid)) {
                this.processName = _caller.processName;
            } else {
                this.processName = aInfo.processName;
            }
            if (this.intent != null && (aInfo.flags & 32) != 0) {
                this.intent.addFlags(8388608);
            }
            this.packageName = aInfo.applicationInfo.packageName;
            this.launchMode = aInfo.launchMode;
            AttributeCache.Entry ent = AttributeCache.instance().get(this.packageName, this.realTheme, R.styleable.Window, this.userId);
            this.fullscreen = (ent == null || ent.array.getBoolean(4, false) || ent.array.getBoolean(5, false)) ? false : true;
            this.noDisplay = ent != null && ent.array.getBoolean(10, false);
            if ((!_componentSpecified || _launchedFromUid == Process.myUid() || _launchedFromUid == 0) && Intent.ACTION_MAIN.equals(_intent.getAction()) && _intent.hasCategory(Intent.CATEGORY_HOME) && _intent.getCategories().size() == 1 && _intent.getData() == null && _intent.getType() == null && (this.intent.getFlags() & 268435456) != 0 && isNotResolverActivity()) {
                this.mActivityType = 1;
            } else if (this.realActivity.getClassName().contains(RECENTS_PACKAGE_NAME)) {
                this.mActivityType = 2;
            } else {
                this.mActivityType = 0;
            }
            this.immersive = (aInfo.flags & 2048) != 0;
            return;
        }
        this.realActivity = null;
        this.taskAffinity = null;
        this.stateNotNeeded = false;
        this.baseDir = null;
        this.resDir = null;
        this.dataDir = null;
        this.processName = null;
        this.packageName = null;
        this.fullscreen = true;
        this.noDisplay = false;
        this.mActivityType = 0;
        this.immersive = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTask(TaskRecord newTask, ThumbnailHolder newThumbHolder, boolean isRoot) {
        if (this.task != null && this.task.removeActivity(this)) {
            this.mStackSupervisor.removeTask(this.task);
        }
        if (this.inHistory && !this.finishing) {
            if (this.task != null) {
                this.task.numActivities--;
            }
            if (newTask != null) {
                newTask.numActivities++;
            }
        }
        if (newThumbHolder == null) {
            newThumbHolder = newTask;
        }
        this.task = newTask;
        if (!isRoot && (this.intent.getFlags() & 524288) != 0) {
            if (this.thumbHolder == null) {
                this.thumbHolder = new ThumbnailHolder();
                return;
            }
            return;
        }
        this.thumbHolder = newThumbHolder;
    }

    boolean changeWindowTranslucency(boolean toOpaque) {
        AttributeCache.Entry ent;
        if (this.fullscreen == toOpaque || (ent = AttributeCache.instance().get(this.packageName, this.realTheme, R.styleable.Window, this.userId)) == null || !ent.array.getBoolean(5, false) || ent.array.getBoolean(4, false)) {
            return false;
        }
        this.task.numFullscreen += toOpaque ? 1 : -1;
        this.fullscreen = toOpaque;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void putInHistory() {
        if (!this.inHistory) {
            this.inHistory = true;
            if (this.task != null && !this.finishing) {
                this.task.numActivities++;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void takeFromHistory() {
        if (this.inHistory) {
            this.inHistory = false;
            if (this.task != null && !this.finishing) {
                this.task.numActivities--;
                this.task = null;
            }
            clearOptionsLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInHistory() {
        return this.inHistory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHomeActivity() {
        return this.mActivityType == 1;
    }

    boolean isRecentsActivity() {
        return this.mActivityType == 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isApplicationActivity() {
        return this.mActivityType == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeFinishing() {
        if (!this.finishing) {
            this.finishing = true;
            if (this.task != null && this.inHistory) {
                this.task.numActivities--;
            }
            if (this.stopped) {
                clearOptionsLocked();
            }
        }
    }

    boolean isRootActivity() {
        ArrayList<ActivityRecord> activities = this.task.mActivities;
        return activities.size() == 0 || this == activities.get(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UriPermissionOwner getUriPermissionsLocked() {
        if (this.uriPermissions == null) {
            this.uriPermissions = new UriPermissionOwner(this.service, this);
        }
        return this.uriPermissions;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addResultLocked(ActivityRecord from, String resultWho, int requestCode, int resultCode, Intent resultData) {
        ActivityResult r = new ActivityResult(from, resultWho, requestCode, resultCode, resultData);
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(r);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:21:0x005b  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0065 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void removeResultsLocked(com.android.server.am.ActivityRecord r4, java.lang.String r5, int r6) {
        /*
            r3 = this;
            r0 = r3
            java.util.ArrayList<android.app.ResultInfo> r0 = r0.results
            if (r0 == 0) goto L6b
            r0 = r3
            java.util.ArrayList<android.app.ResultInfo> r0 = r0.results
            int r0 = r0.size()
            r1 = 1
            int r0 = r0 - r1
            r7 = r0
        L12:
            r0 = r7
            if (r0 < 0) goto L6b
            r0 = r3
            java.util.ArrayList<android.app.ResultInfo> r0 = r0.results
            r1 = r7
            java.lang.Object r0 = r0.get(r1)
            com.android.server.am.ActivityResult r0 = (com.android.server.am.ActivityResult) r0
            r8 = r0
            r0 = r8
            com.android.server.am.ActivityRecord r0 = r0.mFrom
            r1 = r4
            if (r0 == r1) goto L31
            goto L65
        L31:
            r0 = r8
            java.lang.String r0 = r0.mResultWho
            if (r0 != 0) goto L40
            r0 = r5
            if (r0 == 0) goto L4f
            goto L65
        L40:
            r0 = r8
            java.lang.String r0 = r0.mResultWho
            r1 = r5
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L4f
            goto L65
        L4f:
            r0 = r8
            int r0 = r0.mRequestCode
            r1 = r6
            if (r0 == r1) goto L5b
            goto L65
        L5b:
            r0 = r3
            java.util.ArrayList<android.app.ResultInfo> r0 = r0.results
            r1 = r7
            java.lang.Object r0 = r0.remove(r1)
        L65:
            int r7 = r7 + (-1)
            goto L12
        L6b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityRecord.removeResultsLocked(com.android.server.am.ActivityRecord, java.lang.String, int):void");
    }

    void addNewIntentLocked(Intent intent) {
        if (this.newIntents == null) {
            this.newIntents = new ArrayList<>();
        }
        this.newIntents.add(intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void deliverNewIntentLocked(int callingUid, Intent intent) {
        this.service.grantUriPermissionFromIntentLocked(callingUid, this.packageName, intent, getUriPermissionsLocked());
        boolean unsent = true;
        if ((this.state == ActivityStack.ActivityState.RESUMED || (this.service.mSleeping && this.task.stack.topRunningActivityLocked(null) == this)) && this.app != null && this.app.thread != null) {
            try {
                ArrayList<Intent> ar = new ArrayList<>();
                intent = new Intent(intent);
                ar.add(intent);
                this.app.thread.scheduleNewIntent(ar, this.appToken);
                unsent = false;
            } catch (RemoteException e) {
                Slog.w(TAG, "Exception thrown sending new intent to " + this, e);
            } catch (NullPointerException e2) {
                Slog.w(TAG, "Exception thrown sending new intent to " + this, e2);
            }
        }
        if (unsent) {
            addNewIntentLocked(new Intent(intent));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateOptionsLocked(Bundle options) {
        if (options != null) {
            if (this.pendingOptions != null) {
                this.pendingOptions.abort();
            }
            this.pendingOptions = new ActivityOptions(options);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateOptionsLocked(ActivityOptions options) {
        if (options != null) {
            if (this.pendingOptions != null) {
                this.pendingOptions.abort();
            }
            this.pendingOptions = options;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyOptionsLocked() {
        if (this.pendingOptions != null) {
            int animationType = this.pendingOptions.getAnimationType();
            switch (animationType) {
                case 1:
                    this.service.mWindowManager.overridePendingAppTransition(this.pendingOptions.getPackageName(), this.pendingOptions.getCustomEnterResId(), this.pendingOptions.getCustomExitResId(), this.pendingOptions.getOnAnimationStartListener());
                    break;
                case 2:
                    this.service.mWindowManager.overridePendingAppTransitionScaleUp(this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getStartWidth(), this.pendingOptions.getStartHeight());
                    if (this.intent.getSourceBounds() == null) {
                        this.intent.setSourceBounds(new Rect(this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getStartX() + this.pendingOptions.getStartWidth(), this.pendingOptions.getStartY() + this.pendingOptions.getStartHeight()));
                        break;
                    }
                    break;
                case 3:
                case 4:
                    boolean scaleUp = animationType == 3;
                    this.service.mWindowManager.overridePendingAppTransitionThumb(this.pendingOptions.getThumbnail(), this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getOnAnimationStartListener(), scaleUp);
                    if (this.intent.getSourceBounds() == null) {
                        this.intent.setSourceBounds(new Rect(this.pendingOptions.getStartX(), this.pendingOptions.getStartY(), this.pendingOptions.getStartX() + this.pendingOptions.getThumbnail().getWidth(), this.pendingOptions.getStartY() + this.pendingOptions.getThumbnail().getHeight()));
                        break;
                    }
                    break;
            }
            this.pendingOptions = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearOptionsLocked() {
        if (this.pendingOptions != null) {
            this.pendingOptions.abort();
            this.pendingOptions = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityOptions takeOptionsLocked() {
        ActivityOptions opts = this.pendingOptions;
        this.pendingOptions = null;
        return opts;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeUriPermissionsLocked() {
        if (this.uriPermissions != null) {
            this.uriPermissions.removeUriPermissionsLocked();
            this.uriPermissions = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pauseKeyDispatchingLocked() {
        if (!this.keysPaused) {
            this.keysPaused = true;
            this.service.mWindowManager.pauseKeyDispatching(this.appToken);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resumeKeyDispatchingLocked() {
        if (this.keysPaused) {
            this.keysPaused = false;
            this.service.mWindowManager.resumeKeyDispatching(this.appToken);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateThumbnail(Bitmap newThumbnail, CharSequence description) {
        if (this.thumbHolder != null) {
            if (newThumbnail != null) {
                this.thumbHolder.lastThumbnail = newThumbnail;
            }
            this.thumbHolder.lastDescription = description;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startLaunchTickingLocked() {
        if (!ActivityManagerService.IS_USER_BUILD && this.launchTickTime == 0) {
            this.launchTickTime = SystemClock.uptimeMillis();
            continueLaunchTickingLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean continueLaunchTickingLocked() {
        if (this.launchTickTime != 0) {
            ActivityStack stack = this.task.stack;
            Message msg = stack.mHandler.obtainMessage(103, this);
            stack.mHandler.removeMessages(103);
            stack.mHandler.sendMessageDelayed(msg, 500L);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finishLaunchTickingLocked() {
        this.launchTickTime = 0L;
        this.task.stack.mHandler.removeMessages(103);
    }

    public boolean mayFreezeScreenLocked(ProcessRecord app) {
        return (app == null || app.crashing || app.notResponding) ? false : true;
    }

    public void startFreezingScreenLocked(ProcessRecord app, int configChanges) {
        if (mayFreezeScreenLocked(app)) {
            this.service.mWindowManager.startAppFreezingScreen(this.appToken, configChanges);
        }
    }

    public void stopFreezingScreenLocked(boolean force) {
        if (force || this.frozenBeforeDestroy) {
            this.frozenBeforeDestroy = false;
            this.service.mWindowManager.stopAppFreezingScreen(this.appToken, force);
        }
    }

    public void reportFullyDrawnLocked() {
        long curTime = SystemClock.uptimeMillis();
        if (this.displayStartTime != 0) {
            reportLaunchTimeLocked(curTime);
        }
        if (this.fullyDrawnStartTime != 0) {
            ActivityStack stack = this.task.stack;
            long thisTime = curTime - this.fullyDrawnStartTime;
            long totalTime = stack.mFullyDrawnStartTime != 0 ? curTime - stack.mFullyDrawnStartTime : thisTime;
            Trace.asyncTraceEnd(64L, "drawing", 0);
            EventLog.writeEvent((int) EventLogTags.AM_ACTIVITY_FULLY_DRAWN_TIME, Integer.valueOf(this.userId), Integer.valueOf(System.identityHashCode(this)), this.shortComponentName, Long.valueOf(thisTime), Long.valueOf(totalTime));
            StringBuilder sb = this.service.mStringBuilder;
            sb.setLength(0);
            sb.append("Fully drawn ");
            sb.append(this.shortComponentName);
            sb.append(": ");
            TimeUtils.formatDuration(thisTime, sb);
            if (thisTime != totalTime) {
                sb.append(" (total ");
                TimeUtils.formatDuration(totalTime, sb);
                sb.append(Separators.RPAREN);
            }
            Log.i(TAG, sb.toString());
            if (totalTime > 0) {
                this.service.mUsageStatsService.noteFullyDrawnTime(this.realActivity, (int) totalTime);
            }
            this.fullyDrawnStartTime = 0L;
            stack.mFullyDrawnStartTime = 0L;
        }
    }

    private void reportLaunchTimeLocked(long curTime) {
        ActivityStack stack = this.task.stack;
        long thisTime = curTime - this.displayStartTime;
        long totalTime = stack.mLaunchStartTime != 0 ? curTime - stack.mLaunchStartTime : thisTime;
        Trace.asyncTraceEnd(64L, "launching", 0);
        EventLog.writeEvent((int) EventLogTags.AM_ACTIVITY_LAUNCH_TIME, Integer.valueOf(this.userId), Integer.valueOf(System.identityHashCode(this)), this.shortComponentName, Long.valueOf(thisTime), Long.valueOf(totalTime));
        StringBuilder sb = this.service.mStringBuilder;
        sb.setLength(0);
        sb.append("Displayed ");
        sb.append(this.shortComponentName);
        sb.append(": ");
        TimeUtils.formatDuration(thisTime, sb);
        if (thisTime != totalTime) {
            sb.append(" (total ");
            TimeUtils.formatDuration(totalTime, sb);
            sb.append(Separators.RPAREN);
        }
        Log.i(TAG, sb.toString());
        this.mStackSupervisor.reportActivityLaunchedLocked(false, this, thisTime, totalTime);
        if (totalTime > 0) {
            this.service.mUsageStatsService.noteLaunchTime(this.realActivity, (int) totalTime);
        }
        this.displayStartTime = 0L;
        stack.mLaunchStartTime = 0L;
    }

    public void windowsDrawn() {
        synchronized (this.service) {
            if (this.displayStartTime != 0) {
                reportLaunchTimeLocked(SystemClock.uptimeMillis());
            }
            this.startTime = 0L;
            finishLaunchTickingLocked();
        }
    }

    public void windowsVisible() {
        synchronized (this.service) {
            this.mStackSupervisor.reportActivityVisibleLocked(this);
            if (!this.nowVisible) {
                this.nowVisible = true;
                this.lastVisibleTime = SystemClock.uptimeMillis();
                if (!this.idle) {
                    this.mStackSupervisor.processStoppingActivitiesLocked(false);
                } else {
                    int N = this.mStackSupervisor.mWaitingVisibleActivities.size();
                    if (N > 0) {
                        for (int i = 0; i < N; i++) {
                            ActivityRecord r = this.mStackSupervisor.mWaitingVisibleActivities.get(i);
                            r.waitingVisible = false;
                        }
                        this.mStackSupervisor.mWaitingVisibleActivities.clear();
                        this.mStackSupervisor.scheduleIdleLocked();
                    }
                }
                this.service.scheduleAppGcsLocked();
            }
        }
    }

    public void windowsGone() {
        this.nowVisible = false;
    }

    private ActivityRecord getWaitingHistoryRecordLocked() {
        ActivityRecord r = this;
        ActivityStack stack = this.task.stack;
        if (r.waitingVisible) {
            r = stack.mResumedActivity;
            if (r == null) {
                r = stack.mPausingActivity;
            }
            if (r == null) {
                r = this;
            }
        }
        return r;
    }

    public boolean keyDispatchingTimedOut(String reason) {
        ActivityRecord r;
        ProcessRecord anrApp;
        synchronized (this.service) {
            r = getWaitingHistoryRecordLocked();
            anrApp = r != null ? r.app : null;
        }
        return this.service.inputDispatchingTimedOut(anrApp, r, this, false, reason);
    }

    public long getKeyDispatchingTimeout() {
        long inputDispatchingTimeoutLocked;
        synchronized (this.service) {
            ActivityRecord r = getWaitingHistoryRecordLocked();
            inputDispatchingTimeoutLocked = ActivityManagerService.getInputDispatchingTimeoutLocked(r);
        }
        return inputDispatchingTimeoutLocked;
    }

    public boolean isInterestingToUserLocked() {
        return this.visible || this.nowVisible || this.state == ActivityStack.ActivityState.PAUSING || this.state == ActivityStack.ActivityState.RESUMED;
    }

    public void setSleeping(boolean _sleeping) {
        if (this.sleeping != _sleeping && this.app != null && this.app.thread != null) {
            try {
                this.app.thread.scheduleSleeping(this.appToken, _sleeping);
                if (_sleeping && !this.mStackSupervisor.mGoingToSleepActivities.contains(this)) {
                    this.mStackSupervisor.mGoingToSleepActivities.add(this);
                }
                this.sleeping = _sleeping;
            } catch (RemoteException e) {
                Slog.w(TAG, "Exception thrown when sleeping: " + this.intent.getComponent(), e);
            }
        }
    }

    static void activityResumedLocked(IBinder token) {
        ActivityRecord r = forToken(token);
        r.icicle = null;
        r.haveState = false;
    }

    static int getTaskForActivityLocked(IBinder token, boolean onlyRoot) {
        ActivityRecord r = forToken(token);
        if (r == null) {
            return -1;
        }
        TaskRecord task = r.task;
        switch (task.mActivities.indexOf(r)) {
            case -1:
                return -1;
            case 0:
                return task.taskId;
            default:
                if (onlyRoot) {
                    return -1;
                }
                return task.taskId;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ActivityRecord isInStackLocked(IBinder token) {
        ActivityRecord r = forToken(token);
        if (r != null) {
            return r.task.stack.isInStackLocked(token);
        }
        return null;
    }

    static ActivityStack getStackLocked(IBinder token) {
        ActivityRecord r = isInStackLocked(token);
        if (r != null) {
            return r.task.stack;
        }
        return null;
    }

    private String activityTypeToString(int type) {
        switch (type) {
            case 0:
                return "APPLICATION_ACTIVITY_TYPE";
            case 1:
                return "HOME_ACTIVITY_TYPE";
            case 2:
                return "RECENTS_ACTIVITY_TYPE";
            default:
                return Integer.toString(type);
        }
    }

    public String toString() {
        if (this.stringName != null) {
            return this.stringName + " t" + (this.task == null ? -1 : this.task.taskId) + (this.finishing ? " f}" : "}");
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("ActivityRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" u");
        sb.append(this.userId);
        sb.append(' ');
        sb.append(this.intent.getComponent().flattenToShortString());
        this.stringName = sb.toString();
        return toString();
    }
}