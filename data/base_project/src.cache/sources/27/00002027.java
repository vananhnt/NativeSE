package com.android.server.wm;

import android.graphics.Rect;
import android.util.TypedValue;
import com.android.internal.R;
import java.io.PrintWriter;
import java.util.ArrayList;

/* loaded from: TaskStack.class */
public class TaskStack {
    private static final int DEFAULT_DIM_DURATION = 200;
    final int mStackId;
    private final WindowManagerService mService;
    private final DisplayContent mDisplayContent;
    private ArrayList<Task> mTasks = new ArrayList<>();
    StackBox mStackBox;
    final DimLayer mDimLayer;
    WindowStateAnimator mDimWinAnimator;
    final DimLayer mAnimationBackgroundSurface;
    WindowStateAnimator mAnimationBackgroundAnimator;
    boolean mDimmingTag;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskStack(WindowManagerService service, int stackId, DisplayContent displayContent) {
        this.mService = service;
        this.mStackId = stackId;
        this.mDisplayContent = displayContent;
        displayContent.getDisplayId();
        this.mDimLayer = new DimLayer(service, this);
        this.mAnimationBackgroundSurface = new DimLayer(service, this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DisplayContent getDisplayContent() {
        return this.mDisplayContent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<Task> getTasks() {
        return this.mTasks;
    }

    boolean isHomeStack() {
        return this.mStackId == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasSibling() {
        return this.mStackBox.mParent != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean addTask(Task task, boolean toTop) {
        int stackNdx;
        this.mStackBox.makeDirty();
        if (!toTop) {
            stackNdx = 0;
        } else {
            stackNdx = this.mTasks.size();
            int currentUserId = this.mService.mCurrentUserId;
            if (task.mUserId != currentUserId) {
                do {
                    stackNdx--;
                    if (stackNdx < 0) {
                        break;
                    }
                } while (currentUserId == this.mTasks.get(stackNdx).mUserId);
                stackNdx++;
            }
        }
        this.mTasks.add(stackNdx, task);
        task.mStack = this;
        return this.mDisplayContent.moveHomeStackBox(this.mStackId == 0);
    }

    boolean moveTaskToTop(Task task) {
        this.mTasks.remove(task);
        return addTask(task, true);
    }

    boolean moveTaskToBottom(Task task) {
        this.mTasks.remove(task);
        return addTask(task, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeTask(Task task) {
        this.mStackBox.makeDirty();
        this.mTasks.remove(task);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int remove() {
        this.mAnimationBackgroundSurface.destroySurface();
        this.mDimLayer.destroySurface();
        return this.mStackBox.remove();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetAnimationBackgroundAnimator() {
        this.mAnimationBackgroundAnimator = null;
        this.mAnimationBackgroundSurface.hide();
    }

    private long getDimBehindFadeDuration(long duration) {
        TypedValue tv = new TypedValue();
        this.mService.mContext.getResources().getValue(R.fraction.config_dimBehindFadeDuration, tv, true);
        if (tv.type == 6) {
            duration = tv.getFraction((float) duration, (float) duration);
        } else if (tv.type >= 16 && tv.type <= 31) {
            duration = tv.data;
        }
        return duration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean animateDimLayers() {
        int dimLayer;
        float dimAmount;
        if (this.mDimWinAnimator == null) {
            dimLayer = this.mDimLayer.getLayer();
            dimAmount = 0.0f;
        } else {
            dimLayer = this.mDimWinAnimator.mAnimLayer - 1;
            dimAmount = this.mDimWinAnimator.mWin.mAttrs.dimAmount;
        }
        float targetAlpha = this.mDimLayer.getTargetAlpha();
        if (targetAlpha != dimAmount) {
            if (this.mDimWinAnimator == null) {
                this.mDimLayer.hide(200L);
            } else {
                long duration = (!this.mDimWinAnimator.mAnimating || this.mDimWinAnimator.mAnimation == null) ? 200L : this.mDimWinAnimator.mAnimation.computeDurationHint();
                if (targetAlpha > dimAmount) {
                    duration = getDimBehindFadeDuration(duration);
                }
                this.mDimLayer.show(dimLayer, dimAmount, duration);
            }
        } else if (this.mDimLayer.getLayer() != dimLayer) {
            this.mDimLayer.setLayer(dimLayer);
        }
        if (this.mDimLayer.isAnimating()) {
            if (!this.mService.okToDisplay()) {
                this.mDimLayer.show();
                return false;
            }
            return this.mDimLayer.stepAnimation();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetDimmingTag() {
        this.mDimmingTag = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDimmingTag() {
        this.mDimmingTag = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean testDimmingTag() {
        return this.mDimmingTag;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDimming() {
        return this.mDimLayer.isDimming();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDimming(WindowStateAnimator winAnimator) {
        return this.mDimWinAnimator == winAnimator && this.mDimLayer.isDimming();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startDimmingIfNeeded(WindowStateAnimator newWinAnimator) {
        WindowStateAnimator existingDimWinAnimator = this.mDimWinAnimator;
        if (newWinAnimator.mSurfaceShown) {
            if (existingDimWinAnimator == null || !existingDimWinAnimator.mSurfaceShown || existingDimWinAnimator.mAnimLayer < newWinAnimator.mAnimLayer) {
                this.mDimWinAnimator = newWinAnimator;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopDimmingIfNeeded() {
        if (!this.mDimmingTag && isDimming()) {
            this.mDimWinAnimator = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAnimationBackground(WindowStateAnimator winAnimator, int color) {
        int animLayer = winAnimator.mAnimLayer;
        if (this.mAnimationBackgroundAnimator == null || animLayer < this.mAnimationBackgroundAnimator.mAnimLayer) {
            this.mAnimationBackgroundAnimator = winAnimator;
            int animLayer2 = this.mService.adjustAnimationBackground(winAnimator);
            this.mAnimationBackgroundSurface.show(animLayer2 - 1, ((color >> 24) & 255) / 255.0f, 0L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBounds(Rect bounds, boolean underStatusBar) {
        this.mDimLayer.setBounds(bounds);
        this.mAnimationBackgroundSurface.setBounds(bounds);
        ArrayList<WindowState> resizingWindows = this.mService.mResizingWindows;
        for (int taskNdx = this.mTasks.size() - 1; taskNdx >= 0; taskNdx--) {
            ArrayList<AppWindowToken> activities = this.mTasks.get(taskNdx).mAppTokens;
            for (int activityNdx = activities.size() - 1; activityNdx >= 0; activityNdx--) {
                ArrayList<WindowState> windows = activities.get(activityNdx).allAppWindows;
                for (int winNdx = windows.size() - 1; winNdx >= 0; winNdx--) {
                    WindowState win = windows.get(winNdx);
                    if (!resizingWindows.contains(win)) {
                        resizingWindows.add(win);
                    }
                    win.mUnderStatusBar = underStatusBar;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void switchUser(int userId) {
        int top = this.mTasks.size();
        for (int taskNdx = 0; taskNdx < top; taskNdx++) {
            Task task = this.mTasks.get(taskNdx);
            if (task.mUserId == userId) {
                this.mTasks.remove(taskNdx);
                this.mTasks.add(task);
                top--;
            }
        }
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("mStackId=");
        pw.println(this.mStackId);
        for (int taskNdx = 0; taskNdx < this.mTasks.size(); taskNdx++) {
            pw.print(prefix);
            pw.println(this.mTasks.get(taskNdx));
        }
        if (this.mAnimationBackgroundSurface.isDimming()) {
            pw.print(prefix);
            pw.println("mWindowAnimationBackgroundSurface:");
            this.mAnimationBackgroundSurface.printTo(prefix + "  ", pw);
        }
        if (this.mDimLayer.isDimming()) {
            pw.print(prefix);
            pw.println("mDimLayer:");
            this.mDimLayer.printTo(prefix, pw);
            pw.print(prefix);
            pw.print("mDimWinAnimator=");
            pw.println(this.mDimWinAnimator);
        }
    }

    public String toString() {
        return "{stackId=" + this.mStackId + " tasks=" + this.mTasks + "}";
    }
}