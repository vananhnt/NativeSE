package com.android.server.wm;

import android.graphics.Rect;
import java.io.PrintWriter;

/* loaded from: StackBox.class */
public class StackBox {
    public static final int TASK_STACK_GOES_BEFORE = 0;
    public static final int TASK_STACK_GOES_AFTER = 1;
    public static final int TASK_STACK_TO_LEFT_OF = 2;
    public static final int TASK_STACK_TO_RIGHT_OF = 3;
    public static final int TASK_STACK_GOES_ABOVE = 4;
    public static final int TASK_STACK_GOES_BELOW = 5;
    public static final int TASK_STACK_GOES_OVER = 6;
    public static final int TASK_STACK_GOES_UNDER = 7;
    static int sCurrentBoxId = 0;
    final int mStackBoxId;
    final WindowManagerService mService;
    final DisplayContent mDisplayContent;
    StackBox mParent;
    StackBox mFirst;
    StackBox mSecond;
    TaskStack mStack;
    boolean mVertical;
    float mWeight;
    boolean layoutNeeded;
    boolean mUnderStatusBar;
    Rect mBounds = new Rect();
    Rect mTmpRect = new Rect();

    /* JADX INFO: Access modifiers changed from: package-private */
    public StackBox(WindowManagerService service, DisplayContent displayContent, StackBox parent) {
        synchronized (StackBox.class) {
            int i = sCurrentBoxId;
            sCurrentBoxId = i + 1;
            this.mStackBoxId = i;
        }
        this.mService = service;
        this.mDisplayContent = displayContent;
        this.mParent = parent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeDirty() {
        this.layoutNeeded = true;
        if (this.mParent != null) {
            this.mParent.makeDirty();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean contains(int stackBoxId) {
        return this.mStackBoxId == stackBoxId || (this.mStack == null && (this.mFirst.contains(stackBoxId) || this.mSecond.contains(stackBoxId)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int stackIdFromPoint(int x, int y) {
        if (!this.mBounds.contains(x, y)) {
            return -1;
        }
        if (this.mStack != null) {
            return this.mStack.mStackId;
        }
        int stackId = this.mFirst.stackIdFromPoint(x, y);
        if (stackId >= 0) {
            return stackId;
        }
        return this.mSecond.stackIdFromPoint(x, y);
    }

    boolean isFirstChild() {
        return this.mParent != null && this.mParent.mFirst == this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rect getStackBounds(int stackId) {
        if (this.mStack != null) {
            if (this.mStack.mStackId == stackId) {
                return new Rect(this.mBounds);
            }
            return null;
        }
        Rect bounds = this.mFirst.getStackBounds(stackId);
        if (bounds != null) {
            return bounds;
        }
        return this.mSecond.getStackBounds(stackId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskStack split(int stackId, int relativeStackBoxId, int position, float weight) {
        TaskStack firstStack;
        TaskStack secondStack;
        if (this.mStackBoxId != relativeStackBoxId) {
            if (this.mStack != null) {
                return null;
            }
            TaskStack stack = this.mFirst.split(stackId, relativeStackBoxId, position, weight);
            if (stack != null) {
                return stack;
            }
            return this.mSecond.split(stackId, relativeStackBoxId, position, weight);
        }
        TaskStack stack2 = new TaskStack(this.mService, stackId, this.mDisplayContent);
        if (position == 0) {
            position = 2;
        } else if (position == 1) {
            position = 3;
        }
        switch (position) {
            case 2:
            case 3:
            default:
                this.mVertical = false;
                if (position == 2) {
                    this.mWeight = weight;
                    firstStack = stack2;
                    secondStack = this.mStack;
                    break;
                } else {
                    this.mWeight = 1.0f - weight;
                    firstStack = this.mStack;
                    secondStack = stack2;
                    break;
                }
            case 4:
            case 5:
                this.mVertical = true;
                if (position == 4) {
                    this.mWeight = weight;
                    firstStack = stack2;
                    secondStack = this.mStack;
                    break;
                } else {
                    this.mWeight = 1.0f - weight;
                    firstStack = this.mStack;
                    secondStack = stack2;
                    break;
                }
        }
        this.mFirst = new StackBox(this.mService, this.mDisplayContent, this);
        firstStack.mStackBox = this.mFirst;
        this.mFirst.mStack = firstStack;
        this.mSecond = new StackBox(this.mService, this.mDisplayContent, this);
        secondStack.mStackBox = this.mSecond;
        this.mSecond.mStack = secondStack;
        this.mStack = null;
        return stack2;
    }

    int getStackId() {
        if (this.mStack != null) {
            return this.mStack.mStackId;
        }
        return this.mFirst.getStackId();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int remove() {
        if (this.mStack != null) {
            this.mDisplayContent.mStackHistory.remove(this.mStack);
        }
        this.mDisplayContent.layoutNeeded = true;
        if (this.mParent == null) {
            this.mDisplayContent.removeStackBox(this);
            return 0;
        }
        StackBox sibling = isFirstChild() ? this.mParent.mSecond : this.mParent.mFirst;
        StackBox grandparent = this.mParent.mParent;
        sibling.mParent = grandparent;
        if (grandparent == null) {
            this.mDisplayContent.removeStackBox(this.mParent);
            this.mDisplayContent.addStackBox(sibling, true);
        } else if (this.mParent.isFirstChild()) {
            grandparent.mFirst = sibling;
        } else {
            grandparent.mSecond = sibling;
        }
        return sibling.getStackId();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean resize(int stackBoxId, float weight) {
        if (this.mStackBoxId != stackBoxId) {
            return this.mStack == null && (this.mFirst.resize(stackBoxId, weight) || this.mSecond.resize(stackBoxId, weight));
        } else if (this.mParent != null) {
            this.mParent.mWeight = isFirstChild() ? weight : 1.0f - weight;
            return true;
        } else {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setStackBoxSizes(Rect bounds, boolean underStatusBar) {
        boolean change;
        boolean change2 = false;
        if (this.mUnderStatusBar != underStatusBar) {
            change2 = true;
            this.mUnderStatusBar = underStatusBar;
        }
        if (this.mStack != null) {
            change = change2 | (!this.mBounds.equals(bounds));
            if (change) {
                this.mBounds.set(bounds);
                this.mStack.setBounds(bounds, underStatusBar);
            }
        } else {
            this.mTmpRect.set(bounds);
            if (this.mVertical) {
                int height = bounds.height();
                int firstHeight = (int) (height * this.mWeight);
                this.mTmpRect.bottom = bounds.top + firstHeight;
                boolean change3 = change2 | this.mFirst.setStackBoxSizes(this.mTmpRect, underStatusBar);
                this.mTmpRect.top = this.mTmpRect.bottom;
                this.mTmpRect.bottom = bounds.top + height;
                change = change3 | this.mSecond.setStackBoxSizes(this.mTmpRect, false);
            } else {
                int width = bounds.width();
                int firstWidth = (int) (width * this.mWeight);
                this.mTmpRect.right = bounds.left + firstWidth;
                boolean change4 = change2 | this.mFirst.setStackBoxSizes(this.mTmpRect, underStatusBar);
                this.mTmpRect.left = this.mTmpRect.right;
                this.mTmpRect.right = bounds.left + width;
                change = change4 | this.mSecond.setStackBoxSizes(this.mTmpRect, underStatusBar);
            }
        }
        return change;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetAnimationBackgroundAnimator() {
        if (this.mStack != null) {
            this.mStack.resetAnimationBackgroundAnimator();
            return;
        }
        this.mFirst.resetAnimationBackgroundAnimator();
        this.mSecond.resetAnimationBackgroundAnimator();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean animateDimLayers() {
        if (this.mStack != null) {
            return this.mStack.animateDimLayers();
        }
        boolean result = this.mFirst.animateDimLayers();
        return result | this.mSecond.animateDimLayers();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetDimming() {
        if (this.mStack != null) {
            this.mStack.resetDimmingTag();
            return;
        }
        this.mFirst.resetDimming();
        this.mSecond.resetDimming();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDimming() {
        if (this.mStack != null) {
            return this.mStack.isDimming();
        }
        boolean result = this.mFirst.isDimming();
        return result | this.mSecond.isDimming();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopDimmingIfNeeded() {
        if (this.mStack != null) {
            this.mStack.stopDimmingIfNeeded();
            return;
        }
        this.mFirst.stopDimmingIfNeeded();
        this.mSecond.stopDimmingIfNeeded();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void switchUserStacks(int userId) {
        if (this.mStack != null) {
            this.mStack.switchUser(userId);
            return;
        }
        this.mFirst.switchUserStacks(userId);
        this.mSecond.switchUserStacks(userId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        if (this.mStack != null) {
            this.mStack.mDimLayer.mDimSurface.destroy();
            this.mStack.mAnimationBackgroundSurface.mDimSurface.destroy();
            return;
        }
        this.mFirst.close();
        this.mSecond.close();
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("mParent=");
        pw.println(this.mParent);
        pw.print(prefix);
        pw.print("mBounds=");
        pw.print(this.mBounds.toShortString());
        pw.print(" mVertical=");
        pw.print(this.mVertical);
        pw.print(" layoutNeeded=");
        pw.println(this.layoutNeeded);
        if (this.mFirst != null) {
            pw.print(prefix);
            pw.print("mFirst=");
            pw.println(System.identityHashCode(this.mFirst));
            this.mFirst.dump(prefix + "  ", pw);
            pw.print(prefix);
            pw.print("mSecond=");
            pw.println(System.identityHashCode(this.mSecond));
            this.mSecond.dump(prefix + "  ", pw);
            return;
        }
        pw.print(prefix);
        pw.print("mStack=");
        pw.println(this.mStack);
        this.mStack.dump(prefix + "  ", pw);
    }

    public String toString() {
        if (this.mStack != null) {
            return "Box{" + hashCode() + " stack=" + this.mStack.mStackId + "}";
        }
        return "Box{" + hashCode() + " parent=" + System.identityHashCode(this.mParent) + " first=" + System.identityHashCode(this.mFirst) + " second=" + System.identityHashCode(this.mSecond) + "}";
    }
}