package com.android.server.am;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.IThumbnailRetriever;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
import com.android.server.am.TaskAccessInfo;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: TaskRecord.class */
public final class TaskRecord extends ThumbnailHolder {
    final int taskId;
    final String affinity;
    Intent intent;
    Intent affinityIntent;
    ComponentName origActivity;
    ComponentName realActivity;
    int numActivities;
    long lastActiveTime;
    boolean rootWasReset;
    boolean askedCompatMode;
    String stringName;
    int userId;
    int numFullscreen;
    ActivityStack stack;
    private int mTaskType;
    final ArrayList<ActivityRecord> mActivities = new ArrayList<>();
    boolean mOnTopOfHome = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskRecord(int _taskId, ActivityInfo info, Intent _intent) {
        this.taskId = _taskId;
        this.affinity = info.taskAffinity;
        setIntent(_intent, info);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void touchActiveTime() {
        this.lastActiveTime = SystemClock.elapsedRealtime();
    }

    long getInactiveDuration() {
        return SystemClock.elapsedRealtime() - this.lastActiveTime;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setIntent(Intent _intent, ActivityInfo info) {
        this.stringName = null;
        if (info.targetActivity == null) {
            if (_intent != null && (_intent.getSelector() != null || _intent.getSourceBounds() != null)) {
                _intent = new Intent(_intent);
                _intent.setSelector(null);
                _intent.setSourceBounds(null);
            }
            this.intent = _intent;
            this.realActivity = _intent != null ? _intent.getComponent() : null;
            this.origActivity = null;
        } else {
            ComponentName targetComponent = new ComponentName(info.packageName, info.targetActivity);
            if (_intent != null) {
                Intent targetIntent = new Intent(_intent);
                targetIntent.setComponent(targetComponent);
                targetIntent.setSelector(null);
                targetIntent.setSourceBounds(null);
                this.intent = targetIntent;
                this.realActivity = targetComponent;
                this.origActivity = _intent.getComponent();
            } else {
                this.intent = null;
                this.realActivity = targetComponent;
                this.origActivity = new ComponentName(info.packageName, info.name);
            }
        }
        if (this.intent != null && (this.intent.getFlags() & 2097152) != 0) {
            this.rootWasReset = true;
        }
        if (info.applicationInfo != null) {
            this.userId = UserHandle.getUserId(info.applicationInfo.uid);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.server.am.ThumbnailHolder
    public void disposeThumbnail() {
        super.disposeThumbnail();
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            ThumbnailHolder thumb = this.mActivities.get(i).thumbHolder;
            if (thumb != this) {
                thumb.disposeThumbnail();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityRecord getTopActivity() {
        for (int i = this.mActivities.size() - 1; i >= 0; i--) {
            ActivityRecord r = this.mActivities.get(i);
            if (!r.finishing) {
                return r;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityRecord topRunningActivityLocked(ActivityRecord notTop) {
        for (int activityNdx = this.mActivities.size() - 1; activityNdx >= 0; activityNdx--) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (!r.finishing && r != notTop && this.stack.okToShow(r)) {
                return r;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void moveActivityToFrontLocked(ActivityRecord newTop) {
        getTopActivity().frontOfTask = false;
        this.mActivities.remove(newTop);
        this.mActivities.add(newTop);
        newTop.frontOfTask = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addActivityAtBottom(ActivityRecord r) {
        addActivityAtIndex(0, r);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addActivityToTop(ActivityRecord r) {
        addActivityAtIndex(this.mActivities.size(), r);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addActivityAtIndex(int index, ActivityRecord r) {
        if (!this.mActivities.remove(r) && r.fullscreen) {
            this.numFullscreen++;
        }
        if (this.mActivities.isEmpty()) {
            this.mTaskType = r.mActivityType;
        } else {
            r.mActivityType = this.mTaskType;
        }
        this.mActivities.add(index, r);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean removeActivity(ActivityRecord r) {
        if (this.mActivities.remove(r) && r.fullscreen) {
            this.numFullscreen--;
        }
        return this.mActivities.size() == 0;
    }

    final void performClearTaskAtIndexLocked(int activityNdx) {
        int numActivities = this.mActivities.size();
        while (activityNdx < numActivities) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (!r.finishing && this.stack.finishActivityLocked(r, 0, null, "clear", false)) {
                activityNdx--;
                numActivities--;
            }
            activityNdx++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performClearTaskLocked() {
        performClearTaskAtIndexLocked(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActivityRecord performClearTaskLocked(ActivityRecord newR, int launchFlags) {
        int numActivities = this.mActivities.size();
        int activityNdx = numActivities - 1;
        while (activityNdx >= 0) {
            ActivityRecord r = this.mActivities.get(activityNdx);
            if (r.finishing || !r.realActivity.equals(newR.realActivity)) {
                activityNdx--;
            } else {
                while (true) {
                    activityNdx++;
                    if (activityNdx >= numActivities) {
                        break;
                    }
                    ActivityRecord r2 = this.mActivities.get(activityNdx);
                    if (!r2.finishing) {
                        ActivityOptions opts = r2.takeOptionsLocked();
                        if (opts != null) {
                            r.updateOptionsLocked(opts);
                        }
                        if (this.stack.finishActivityLocked(r2, 0, null, "clear", false)) {
                            activityNdx--;
                            numActivities--;
                        }
                    }
                }
                if (r.launchMode == 0 && (launchFlags & 536870912) == 0 && !r.finishing) {
                    this.stack.finishActivityLocked(r, 0, null, "clear", false);
                    return null;
                }
                return r;
            }
        }
        return null;
    }

    public ActivityManager.TaskThumbnails getTaskThumbnailsLocked() {
        TaskAccessInfo info = getTaskAccessInfoLocked(true);
        ActivityRecord resumedActivity = this.stack.mResumedActivity;
        if (resumedActivity != null && resumedActivity.thumbHolder == this) {
            info.mainThumbnail = this.stack.screenshotActivities(resumedActivity);
        }
        if (info.mainThumbnail == null) {
            info.mainThumbnail = this.lastThumbnail;
        }
        return info;
    }

    public Bitmap getTaskTopThumbnailLocked() {
        ActivityRecord resumedActivity = this.stack.mResumedActivity;
        if (resumedActivity != null && resumedActivity.task == this) {
            return this.stack.screenshotActivities(resumedActivity);
        }
        TaskAccessInfo info = getTaskAccessInfoLocked(true);
        if (info.numSubThumbbails <= 0) {
            return info.mainThumbnail != null ? info.mainThumbnail : this.lastThumbnail;
        }
        return info.subtasks.get(info.numSubThumbbails - 1).holder.lastThumbnail;
    }

    public ActivityRecord removeTaskActivitiesLocked(int subTaskIndex, boolean taskRequired) {
        TaskAccessInfo info = getTaskAccessInfoLocked(false);
        if (info.root == null) {
            if (taskRequired) {
                Slog.w("ActivityManager", "removeTaskLocked: unknown taskId " + this.taskId);
                return null;
            }
            return null;
        } else if (subTaskIndex < 0) {
            performClearTaskAtIndexLocked(info.rootIndex);
            return info.root;
        } else if (subTaskIndex >= info.subtasks.size()) {
            if (taskRequired) {
                Slog.w("ActivityManager", "removeTaskLocked: unknown subTaskIndex " + subTaskIndex);
                return null;
            }
            return null;
        } else {
            TaskAccessInfo.SubTask subtask = info.subtasks.get(subTaskIndex);
            performClearTaskAtIndexLocked(subtask.index);
            return subtask.activity;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHomeTask() {
        return this.mTaskType == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isApplicationTask() {
        return this.mTaskType == 0;
    }

    public TaskAccessInfo getTaskAccessInfoLocked(boolean inclThumbs) {
        final TaskAccessInfo thumbs = new TaskAccessInfo();
        int NA = this.mActivities.size();
        int j = 0;
        ThumbnailHolder holder = null;
        while (true) {
            if (j >= NA) {
                break;
            }
            ActivityRecord ar = this.mActivities.get(j);
            if (!ar.finishing) {
                thumbs.root = ar;
                thumbs.rootIndex = j;
                holder = ar.thumbHolder;
                if (holder != null) {
                    thumbs.mainThumbnail = holder.lastThumbnail;
                }
                j++;
            } else {
                j++;
            }
        }
        if (j >= NA) {
            return thumbs;
        }
        ArrayList<TaskAccessInfo.SubTask> subtasks = new ArrayList<>();
        thumbs.subtasks = subtasks;
        while (j < NA) {
            ActivityRecord ar2 = this.mActivities.get(j);
            j++;
            if (!ar2.finishing && ar2.thumbHolder != holder && holder != null) {
                thumbs.numSubThumbbails++;
                holder = ar2.thumbHolder;
                TaskAccessInfo.SubTask sub = new TaskAccessInfo.SubTask();
                sub.holder = holder;
                sub.activity = ar2;
                sub.index = j - 1;
                subtasks.add(sub);
            }
        }
        if (thumbs.numSubThumbbails > 0) {
            thumbs.retriever = new IThumbnailRetriever.Stub() { // from class: com.android.server.am.TaskRecord.1
                @Override // android.app.IThumbnailRetriever
                public Bitmap getThumbnail(int index) {
                    if (index < 0 || index >= thumbs.subtasks.size()) {
                        return null;
                    }
                    TaskAccessInfo.SubTask sub2 = thumbs.subtasks.get(index);
                    ActivityRecord resumedActivity = TaskRecord.this.stack.mResumedActivity;
                    if (resumedActivity != null && resumedActivity.thumbHolder == sub2.holder) {
                        return TaskRecord.this.stack.screenshotActivities(resumedActivity);
                    }
                    return sub2.holder.lastThumbnail;
                }
            };
        }
        return thumbs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ActivityRecord findActivityInHistoryLocked(ActivityRecord r) {
        ComponentName realActivity = r.realActivity;
        for (int activityNdx = this.mActivities.size() - 1; activityNdx >= 0; activityNdx--) {
            ActivityRecord candidate = this.mActivities.get(activityNdx);
            if (!candidate.finishing && candidate.realActivity.equals(realActivity)) {
                return candidate;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        if (this.numActivities != 0 || this.rootWasReset || this.userId != 0 || this.numFullscreen != 0) {
            pw.print(prefix);
            pw.print("numActivities=");
            pw.print(this.numActivities);
            pw.print(" rootWasReset=");
            pw.print(this.rootWasReset);
            pw.print(" userId=");
            pw.print(this.userId);
            pw.print(" mTaskType=");
            pw.print(this.mTaskType);
            pw.print(" numFullscreen=");
            pw.print(this.numFullscreen);
            pw.print(" mOnTopOfHome=");
            pw.println(this.mOnTopOfHome);
        }
        if (this.affinity != null) {
            pw.print(prefix);
            pw.print("affinity=");
            pw.println(this.affinity);
        }
        if (this.intent != null) {
            StringBuilder sb = new StringBuilder(128);
            sb.append(prefix);
            sb.append("intent={");
            this.intent.toShortString(sb, false, true, false, true);
            sb.append('}');
            pw.println(sb.toString());
        }
        if (this.affinityIntent != null) {
            StringBuilder sb2 = new StringBuilder(128);
            sb2.append(prefix);
            sb2.append("affinityIntent={");
            this.affinityIntent.toShortString(sb2, false, true, false, true);
            sb2.append('}');
            pw.println(sb2.toString());
        }
        if (this.origActivity != null) {
            pw.print(prefix);
            pw.print("origActivity=");
            pw.println(this.origActivity.flattenToShortString());
        }
        if (this.realActivity != null) {
            pw.print(prefix);
            pw.print("realActivity=");
            pw.println(this.realActivity.flattenToShortString());
        }
        pw.print(prefix);
        pw.print("Activities=");
        pw.println(this.mActivities);
        if (!this.askedCompatMode) {
            pw.print(prefix);
            pw.print("askedCompatMode=");
            pw.println(this.askedCompatMode);
        }
        pw.print(prefix);
        pw.print("lastThumbnail=");
        pw.print(this.lastThumbnail);
        pw.print(" lastDescription=");
        pw.println(this.lastDescription);
        pw.print(prefix);
        pw.print("lastActiveTime=");
        pw.print(this.lastActiveTime);
        pw.print(" (inactive for ");
        pw.print(getInactiveDuration() / 1000);
        pw.println("s)");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        if (this.stringName != null) {
            sb.append(this.stringName);
            sb.append(" U=");
            sb.append(this.userId);
            sb.append(" sz=");
            sb.append(this.mActivities.size());
            sb.append('}');
            return sb.toString();
        }
        sb.append("TaskRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" #");
        sb.append(this.taskId);
        if (this.affinity != null) {
            sb.append(" A=");
            sb.append(this.affinity);
        } else if (this.intent != null) {
            sb.append(" I=");
            sb.append(this.intent.getComponent().flattenToShortString());
        } else if (this.affinityIntent != null) {
            sb.append(" aI=");
            sb.append(this.affinityIntent.getComponent().flattenToShortString());
        } else {
            sb.append(" ??");
        }
        this.stringName = sb.toString();
        return toString();
    }
}