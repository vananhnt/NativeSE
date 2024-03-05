package com.android.server.wm;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: Task.class */
public class Task {
    TaskStack mStack;
    final AppTokenList mAppTokens = new AppTokenList();
    final int taskId;
    final int mUserId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Task(AppWindowToken wtoken, TaskStack stack, int userId) {
        this.taskId = wtoken.groupId;
        this.mAppTokens.add(wtoken);
        this.mStack = stack;
        this.mUserId = userId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DisplayContent getDisplayContent() {
        return this.mStack.getDisplayContent();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addAppToken(int addPos, AppWindowToken wtoken) {
        this.mAppTokens.add(addPos, wtoken);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean removeAppToken(AppWindowToken wtoken) {
        this.mAppTokens.remove(wtoken);
        if (this.mAppTokens.size() == 0) {
            this.mStack.removeTask(this);
            return true;
        }
        return false;
    }

    public String toString() {
        return "{taskId=" + this.taskId + " appTokens=" + this.mAppTokens + "}";
    }
}