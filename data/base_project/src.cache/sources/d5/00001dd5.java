package com.android.server.am;

import android.app.ActivityManager;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: TaskAccessInfo.class */
public final class TaskAccessInfo extends ActivityManager.TaskThumbnails {
    public ActivityRecord root;
    public int rootIndex;
    public ArrayList<SubTask> subtasks;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: TaskAccessInfo$SubTask.class */
    public static final class SubTask {
        ThumbnailHolder holder;
        ActivityRecord activity;
        int index;
    }
}