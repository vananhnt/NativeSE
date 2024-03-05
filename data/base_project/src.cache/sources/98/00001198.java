package android.support.v4.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewParentCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* loaded from: ExploreByTouchHelper.class */
public abstract class ExploreByTouchHelper extends AccessibilityDelegateCompat {
    private static final String DEFAULT_CLASS_NAME = View.class.getName();
    public static final int INVALID_ID = Integer.MIN_VALUE;
    private final AccessibilityManager mManager;
    private ExploreByTouchNodeProvider mNodeProvider;
    private final View mView;
    private final Rect mTempScreenRect = new Rect();
    private final Rect mTempParentRect = new Rect();
    private final Rect mTempVisibleRect = new Rect();
    private final int[] mTempGlobalRect = new int[2];
    private int mFocusedVirtualViewId = Integer.MIN_VALUE;
    private int mHoveredVirtualViewId = Integer.MIN_VALUE;

    /* loaded from: ExploreByTouchHelper$ExploreByTouchNodeProvider.class */
    private class ExploreByTouchNodeProvider extends AccessibilityNodeProviderCompat {
        final ExploreByTouchHelper this$0;

        private ExploreByTouchNodeProvider(ExploreByTouchHelper exploreByTouchHelper) {
            this.this$0 = exploreByTouchHelper;
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat
        public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int i) {
            return this.this$0.createNode(i);
        }

        @Override // android.support.v4.view.accessibility.AccessibilityNodeProviderCompat
        public boolean performAction(int i, int i2, Bundle bundle) {
            return this.this$0.performAction(i, i2, bundle);
        }
    }

    public ExploreByTouchHelper(View view) {
        if (view == null) {
            throw new IllegalArgumentException("View may not be null");
        }
        this.mView = view;
        this.mManager = (AccessibilityManager) view.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    private boolean clearAccessibilityFocus(int i) {
        if (isAccessibilityFocused(i)) {
            this.mFocusedVirtualViewId = Integer.MIN_VALUE;
            this.mView.invalidate();
            sendEventForVirtualView(i, 65536);
            return true;
        }
        return false;
    }

    private AccessibilityEvent createEvent(int i, int i2) {
        return i != -1 ? createEventForChild(i, i2) : createEventForHost(i2);
    }

    private AccessibilityEvent createEventForChild(int i, int i2) {
        AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
        obtain.setEnabled(true);
        obtain.setClassName(DEFAULT_CLASS_NAME);
        onPopulateEventForVirtualView(i, obtain);
        if (obtain.getText().isEmpty() && obtain.getContentDescription() == null) {
            throw new RuntimeException("Callbacks must add text or a content description in populateEventForVirtualViewId()");
        }
        obtain.setPackageName(this.mView.getContext().getPackageName());
        AccessibilityEventCompat.asRecord(obtain).setSource(this.mView, i);
        return obtain;
    }

    private AccessibilityEvent createEventForHost(int i) {
        AccessibilityEvent obtain = AccessibilityEvent.obtain(i);
        ViewCompat.onInitializeAccessibilityEvent(this.mView, obtain);
        return obtain;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AccessibilityNodeInfoCompat createNode(int i) {
        return i != -1 ? createNodeForChild(i) : createNodeForHost();
    }

    private AccessibilityNodeInfoCompat createNodeForChild(int i) {
        AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain();
        obtain.setEnabled(true);
        obtain.setClassName(DEFAULT_CLASS_NAME);
        onPopulateNodeForVirtualView(i, obtain);
        if (obtain.getText() == null && obtain.getContentDescription() == null) {
            throw new RuntimeException("Callbacks must add text or a content description in populateNodeForVirtualViewId()");
        }
        obtain.getBoundsInParent(this.mTempParentRect);
        if (this.mTempParentRect.isEmpty()) {
            throw new RuntimeException("Callbacks must set parent bounds in populateNodeForVirtualViewId()");
        }
        int actions = obtain.getActions();
        if ((actions & 64) == 0) {
            if ((actions & 128) == 0) {
                obtain.setPackageName(this.mView.getContext().getPackageName());
                obtain.setSource(this.mView, i);
                obtain.setParent(this.mView);
                if (this.mFocusedVirtualViewId == i) {
                    obtain.setAccessibilityFocused(true);
                    obtain.addAction(128);
                } else {
                    obtain.setAccessibilityFocused(false);
                    obtain.addAction(64);
                }
                if (intersectVisibleToUser(this.mTempParentRect)) {
                    obtain.setVisibleToUser(true);
                    obtain.setBoundsInParent(this.mTempParentRect);
                }
                this.mView.getLocationOnScreen(this.mTempGlobalRect);
                int[] iArr = this.mTempGlobalRect;
                int i2 = iArr[0];
                int i3 = iArr[1];
                this.mTempScreenRect.set(this.mTempParentRect);
                this.mTempScreenRect.offset(i2, i3);
                obtain.setBoundsInScreen(this.mTempScreenRect);
                return obtain;
            }
            throw new RuntimeException("Callbacks must not add ACTION_CLEAR_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
        }
        throw new RuntimeException("Callbacks must not add ACTION_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
    }

    private AccessibilityNodeInfoCompat createNodeForHost() {
        AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(this.mView);
        ViewCompat.onInitializeAccessibilityNodeInfo(this.mView, obtain);
        LinkedList linkedList = new LinkedList();
        getVisibleVirtualViews(linkedList);
        Iterator<Integer> it = linkedList.iterator();
        while (it.hasNext()) {
            obtain.addChild(this.mView, it.next().intValue());
        }
        return obtain;
    }

    private boolean intersectVisibleToUser(Rect rect) {
        if (rect == null || rect.isEmpty() || this.mView.getWindowVisibility() != 0) {
            return false;
        }
        ViewParent parent = this.mView.getParent();
        while (true) {
            ViewParent viewParent = parent;
            if (!(viewParent instanceof View)) {
                if (viewParent != null && this.mView.getLocalVisibleRect(this.mTempVisibleRect)) {
                    return rect.intersect(this.mTempVisibleRect);
                }
                return false;
            }
            View view = (View) viewParent;
            if (ViewCompat.getAlpha(view) <= 0.0f || view.getVisibility() != 0) {
                return false;
            }
            parent = view.getParent();
        }
    }

    private boolean isAccessibilityFocused(int i) {
        return this.mFocusedVirtualViewId == i;
    }

    private boolean manageFocusForChild(int i, int i2, Bundle bundle) {
        if (i2 != 64) {
            if (i2 != 128) {
                return false;
            }
            return clearAccessibilityFocus(i);
        }
        return requestAccessibilityFocus(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean performAction(int i, int i2, Bundle bundle) {
        return i != -1 ? performActionForChild(i, i2, bundle) : performActionForHost(i2, bundle);
    }

    private boolean performActionForChild(int i, int i2, Bundle bundle) {
        return (i2 == 64 || i2 == 128) ? manageFocusForChild(i, i2, bundle) : onPerformActionForVirtualView(i, i2, bundle);
    }

    private boolean performActionForHost(int i, Bundle bundle) {
        return ViewCompat.performAccessibilityAction(this.mView, i, bundle);
    }

    private boolean requestAccessibilityFocus(int i) {
        if (this.mManager.isEnabled() && AccessibilityManagerCompat.isTouchExplorationEnabled(this.mManager) && !isAccessibilityFocused(i)) {
            this.mFocusedVirtualViewId = i;
            this.mView.invalidate();
            sendEventForVirtualView(i, 32768);
            return true;
        }
        return false;
    }

    private void updateHoveredVirtualView(int i) {
        if (this.mHoveredVirtualViewId == i) {
            return;
        }
        int i2 = this.mHoveredVirtualViewId;
        this.mHoveredVirtualViewId = i;
        sendEventForVirtualView(i, 128);
        sendEventForVirtualView(i2, 256);
    }

    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (this.mManager.isEnabled() && AccessibilityManagerCompat.isTouchExplorationEnabled(this.mManager)) {
            int action = motionEvent.getAction();
            if (action != 7) {
                switch (action) {
                    case 9:
                        break;
                    case 10:
                        if (this.mFocusedVirtualViewId != Integer.MIN_VALUE) {
                            updateHoveredVirtualView(Integer.MIN_VALUE);
                            return true;
                        }
                        return false;
                    default:
                        return false;
                }
            }
            int virtualViewAt = getVirtualViewAt(motionEvent.getX(), motionEvent.getY());
            updateHoveredVirtualView(virtualViewAt);
            if (virtualViewAt != Integer.MIN_VALUE) {
                z = true;
            }
            return z;
        }
        return false;
    }

    @Override // android.support.v4.view.AccessibilityDelegateCompat
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View view) {
        if (this.mNodeProvider == null) {
            this.mNodeProvider = new ExploreByTouchNodeProvider();
        }
        return this.mNodeProvider;
    }

    public int getFocusedVirtualView() {
        return this.mFocusedVirtualViewId;
    }

    protected abstract int getVirtualViewAt(float f, float f2);

    protected abstract void getVisibleVirtualViews(List<Integer> list);

    public void invalidateRoot() {
        invalidateVirtualView(-1);
    }

    public void invalidateVirtualView(int i) {
        sendEventForVirtualView(i, 2048);
    }

    protected abstract boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle);

    protected abstract void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent);

    protected abstract void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat);

    public boolean sendEventForVirtualView(int i, int i2) {
        ViewParent parent;
        if (i == Integer.MIN_VALUE || !this.mManager.isEnabled() || (parent = this.mView.getParent()) == null) {
            return false;
        }
        return ViewParentCompat.requestSendAccessibilityEvent(parent, this.mView, createEvent(i, i2));
    }
}