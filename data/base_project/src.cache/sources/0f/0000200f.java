package com.android.server.wm;

import android.app.ActivityManager;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.Display;
import android.view.DisplayInfo;
import gov.nist.core.Separators;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: DisplayContent.class */
public class DisplayContent {
    private final int mDisplayId;
    private final Display mDisplay;
    boolean layoutNeeded;
    int pendingLayoutChanges;
    final boolean isDefaultDisplay;
    private TaskStack mHomeStack;
    StackTapPointerEventListener mTapDetector;
    final WindowManagerService mService;
    private WindowList mWindows = new WindowList();
    final Object mDisplaySizeLock = new Object();
    int mInitialDisplayWidth = 0;
    int mInitialDisplayHeight = 0;
    int mInitialDisplayDensity = 0;
    int mBaseDisplayWidth = 0;
    int mBaseDisplayHeight = 0;
    int mBaseDisplayDensity = 0;
    private final DisplayInfo mDisplayInfo = new DisplayInfo();
    Rect mBaseDisplayRect = new Rect();
    final ArrayList<WindowToken> mExitingTokens = new ArrayList<>();
    final AppTokenList mExitingAppTokens = new AppTokenList();
    private ArrayList<StackBox> mStackBoxes = new ArrayList<>();
    ArrayList<TaskStack> mStackHistory = new ArrayList<>();
    Region mTouchExcludeRegion = new Region();
    ArrayList<Task> mTmpTasks = new ArrayList<>();
    Rect mTmpRect = new Rect();

    /* JADX INFO: Access modifiers changed from: package-private */
    public DisplayContent(Display display, WindowManagerService service) {
        this.mHomeStack = null;
        this.mDisplay = display;
        this.mDisplayId = display.getDisplayId();
        display.getDisplayInfo(this.mDisplayInfo);
        this.isDefaultDisplay = this.mDisplayId == 0;
        this.mService = service;
        StackBox newBox = new StackBox(service, this, null);
        this.mStackBoxes.add(newBox);
        TaskStack newStack = new TaskStack(service, 0, this);
        newStack.mStackBox = newBox;
        newBox.mStack = newStack;
        this.mHomeStack = newStack;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDisplayId() {
        return this.mDisplayId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowList getWindowList() {
        return this.mWindows;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Display getDisplay() {
        return this.mDisplay;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DisplayInfo getDisplayInfo() {
        return this.mDisplayInfo;
    }

    public boolean hasAccess(int uid) {
        return this.mDisplay.hasAccess(uid);
    }

    boolean homeOnTop() {
        return this.mStackBoxes.get(0).mStack != this.mHomeStack;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void moveStack(TaskStack stack, boolean toTop) {
        this.mStackHistory.remove(stack);
        this.mStackHistory.add(toTop ? this.mStackHistory.size() : 0, stack);
        this.mService.moveStackWindowsLocked(stack);
    }

    public boolean isPrivate() {
        return (this.mDisplay.getFlags() & 4) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<Task> getTasks() {
        this.mTmpTasks.clear();
        int numStacks = this.mStackHistory.size();
        for (int stackNdx = 0; stackNdx < numStacks; stackNdx++) {
            this.mTmpTasks.addAll(this.mStackHistory.get(stackNdx).getTasks());
        }
        return this.mTmpTasks;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskStack getHomeStack() {
        return this.mHomeStack;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateDisplayInfo() {
        this.mDisplay.getDisplayInfo(this.mDisplayInfo);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getLogicalDisplayRect(Rect out) {
        updateDisplayInfo();
        int width = this.mDisplayInfo.logicalWidth;
        int left = (this.mBaseDisplayWidth - width) / 2;
        int height = this.mDisplayInfo.logicalHeight;
        int top = (this.mBaseDisplayHeight - height) / 2;
        out.set(left, top, left + width, top + height);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int numTokens() {
        getTasks();
        int count = 0;
        for (int taskNdx = this.mTmpTasks.size() - 1; taskNdx >= 0; taskNdx--) {
            count += this.mTmpTasks.get(taskNdx).mAppTokens.size();
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskStack createStack(int stackId, int relativeStackBoxId, int position, float weight) {
        TaskStack newStack = null;
        if (stackId == 0) {
            if (this.mStackBoxes.size() != 1) {
                throw new IllegalArgumentException("createStack: HOME_STACK_ID (0) not first.");
            }
            newStack = this.mHomeStack;
        } else {
            int stackBoxNdx = this.mStackBoxes.size() - 1;
            while (true) {
                if (stackBoxNdx < 0) {
                    break;
                }
                StackBox box = this.mStackBoxes.get(stackBoxNdx);
                if (position == 6 || position == 7) {
                    if (!box.contains(relativeStackBoxId)) {
                        stackBoxNdx--;
                    } else {
                        StackBox newBox = new StackBox(this.mService, this, null);
                        newStack = new TaskStack(this.mService, stackId, this);
                        newStack.mStackBox = newBox;
                        newBox.mStack = newStack;
                        int offset = position == 6 ? 1 : 0;
                        this.mStackBoxes.add(stackBoxNdx + offset, newBox);
                    }
                } else {
                    newStack = box.split(stackId, relativeStackBoxId, position, weight);
                    if (newStack != null) {
                        break;
                    }
                    stackBoxNdx--;
                }
            }
            if (stackBoxNdx < 0) {
                throw new IllegalArgumentException("createStack: stackBoxId " + relativeStackBoxId + " not found.");
            }
        }
        if (newStack != null) {
            this.layoutNeeded = true;
        }
        return newStack;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean resizeStack(int stackBoxId, float weight) {
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            StackBox box = this.mStackBoxes.get(stackBoxNdx);
            if (box.resize(stackBoxId, weight)) {
                this.layoutNeeded = true;
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addStackBox(StackBox box, boolean toTop) {
        if (this.mStackBoxes.size() >= 2) {
            throw new RuntimeException("addStackBox: Too many toplevel StackBoxes!");
        }
        this.mStackBoxes.add(toTop ? this.mStackBoxes.size() : 0, box);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeStackBox(StackBox box) {
        TaskStack stack = box.mStack;
        if (stack != null && stack.mStackId == 0) {
            return;
        }
        this.mStackBoxes.remove(box);
    }

    ActivityManager.StackBoxInfo getStackBoxInfo(StackBox box) {
        ActivityManager.StackBoxInfo info = new ActivityManager.StackBoxInfo();
        info.stackBoxId = box.mStackBoxId;
        info.weight = box.mWeight;
        info.vertical = box.mVertical;
        info.bounds = new Rect(box.mBounds);
        if (box.mStack != null) {
            info.stackId = box.mStack.mStackId;
        } else {
            info.stackId = -1;
            info.children = new ActivityManager.StackBoxInfo[2];
            info.children[0] = getStackBoxInfo(box.mFirst);
            info.children[1] = getStackBoxInfo(box.mSecond);
        }
        return info;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<ActivityManager.StackBoxInfo> getStackBoxInfos() {
        ArrayList<ActivityManager.StackBoxInfo> list = new ArrayList<>();
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            list.add(getStackBoxInfo(this.mStackBoxes.get(stackBoxNdx)));
        }
        return list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean moveHomeStackBox(boolean toTop) {
        switch (this.mStackBoxes.size()) {
            case 0:
                throw new RuntimeException("moveHomeStackBox: No home StackBox!");
            case 1:
                return false;
            case 2:
                if (homeOnTop() ^ toTop) {
                    this.mStackBoxes.add(this.mStackBoxes.remove(0));
                    return true;
                }
                return false;
            default:
                throw new RuntimeException("moveHomeStackBox: Too many toplevel StackBoxes!");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setStackBoxSize(Rect contentRect) {
        boolean change = false;
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            change |= this.mStackBoxes.get(stackBoxNdx).setStackBoxSizes(contentRect, true);
        }
        return change;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rect getStackBounds(int stackId) {
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            Rect bounds = this.mStackBoxes.get(stackBoxNdx).getStackBounds(stackId);
            if (bounds != null) {
                return bounds;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int stackIdFromPoint(int x, int y) {
        StackBox topBox = this.mStackBoxes.get(this.mStackBoxes.size() - 1);
        return topBox.stackIdFromPoint(x, y);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTouchExcludeRegion(TaskStack focusedStack) {
        this.mTouchExcludeRegion.set(this.mBaseDisplayRect);
        WindowList windows = getWindowList();
        for (int i = windows.size() - 1; i >= 0; i--) {
            WindowState win = windows.get(i);
            TaskStack stack = win.getStack();
            if (win.isVisibleLw() && stack != null && stack != focusedStack) {
                this.mTmpRect.set(win.mVisibleFrame);
                this.mTmpRect.intersect(win.mVisibleInsets);
                this.mTouchExcludeRegion.op(this.mTmpRect, Region.Op.DIFFERENCE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void switchUserStacks(int oldUserId, int newUserId) {
        WindowList windows = getWindowList();
        for (int i = 0; i < windows.size(); i++) {
            WindowState win = windows.get(i);
            if (win.isHiddenFromUserLocked()) {
                win.hideLw(false);
            }
        }
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            this.mStackBoxes.get(stackBoxNdx).switchUserStacks(newUserId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetAnimationBackgroundAnimator() {
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            this.mStackBoxes.get(stackBoxNdx).resetAnimationBackgroundAnimator();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean animateDimLayers() {
        boolean result = false;
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            result |= this.mStackBoxes.get(stackBoxNdx).animateDimLayers();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetDimming() {
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            this.mStackBoxes.get(stackBoxNdx).resetDimming();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDimming() {
        boolean result = false;
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            result |= this.mStackBoxes.get(stackBoxNdx).isDimming();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopDimmingIfNeeded() {
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            this.mStackBoxes.get(stackBoxNdx).stopDimmingIfNeeded();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        for (int stackBoxNdx = this.mStackBoxes.size() - 1; stackBoxNdx >= 0; stackBoxNdx--) {
            this.mStackBoxes.get(stackBoxNdx).close();
        }
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("Display: mDisplayId=");
        pw.println(this.mDisplayId);
        String subPrefix = "  " + prefix;
        pw.print(subPrefix);
        pw.print("init=");
        pw.print(this.mInitialDisplayWidth);
        pw.print("x");
        pw.print(this.mInitialDisplayHeight);
        pw.print(Separators.SP);
        pw.print(this.mInitialDisplayDensity);
        pw.print("dpi");
        if (this.mInitialDisplayWidth != this.mBaseDisplayWidth || this.mInitialDisplayHeight != this.mBaseDisplayHeight || this.mInitialDisplayDensity != this.mBaseDisplayDensity) {
            pw.print(" base=");
            pw.print(this.mBaseDisplayWidth);
            pw.print("x");
            pw.print(this.mBaseDisplayHeight);
            pw.print(Separators.SP);
            pw.print(this.mBaseDisplayDensity);
            pw.print("dpi");
        }
        pw.print(" cur=");
        pw.print(this.mDisplayInfo.logicalWidth);
        pw.print("x");
        pw.print(this.mDisplayInfo.logicalHeight);
        pw.print(" app=");
        pw.print(this.mDisplayInfo.appWidth);
        pw.print("x");
        pw.print(this.mDisplayInfo.appHeight);
        pw.print(" rng=");
        pw.print(this.mDisplayInfo.smallestNominalAppWidth);
        pw.print("x");
        pw.print(this.mDisplayInfo.smallestNominalAppHeight);
        pw.print("-");
        pw.print(this.mDisplayInfo.largestNominalAppWidth);
        pw.print("x");
        pw.println(this.mDisplayInfo.largestNominalAppHeight);
        pw.print(subPrefix);
        pw.print("layoutNeeded=");
        pw.println(this.layoutNeeded);
        for (int boxNdx = 0; boxNdx < this.mStackBoxes.size(); boxNdx++) {
            pw.print(prefix);
            pw.print("StackBox #");
            pw.println(boxNdx);
            this.mStackBoxes.get(boxNdx).dump(prefix + "  ", pw);
        }
        int ndx = numTokens();
        if (ndx > 0) {
            pw.println();
            pw.println("  Application tokens in Z order:");
            getTasks();
            for (int taskNdx = this.mTmpTasks.size() - 1; taskNdx >= 0; taskNdx--) {
                AppTokenList tokens = this.mTmpTasks.get(taskNdx).mAppTokens;
                for (int tokenNdx = tokens.size() - 1; tokenNdx >= 0; tokenNdx--) {
                    AppWindowToken wtoken = tokens.get(tokenNdx);
                    pw.print("  App #");
                    int i = ndx;
                    ndx--;
                    pw.print(i);
                    pw.print(' ');
                    pw.print(wtoken);
                    pw.println(Separators.COLON);
                    wtoken.dump(pw, "    ");
                }
            }
        }
        if (this.mExitingTokens.size() > 0) {
            pw.println();
            pw.println("  Exiting tokens:");
            for (int i2 = this.mExitingTokens.size() - 1; i2 >= 0; i2--) {
                WindowToken token = this.mExitingTokens.get(i2);
                pw.print("  Exiting #");
                pw.print(i2);
                pw.print(' ');
                pw.print(token);
                pw.println(':');
                token.dump(pw, "    ");
            }
        }
        if (this.mExitingAppTokens.size() > 0) {
            pw.println();
            pw.println("  Exiting application tokens:");
            for (int i3 = this.mExitingAppTokens.size() - 1; i3 >= 0; i3--) {
                WindowToken token2 = this.mExitingAppTokens.get(i3);
                pw.print("  Exiting App #");
                pw.print(i3);
                pw.print(' ');
                pw.print(token2);
                pw.println(':');
                token2.dump(pw, "    ");
            }
        }
        pw.println();
    }
}