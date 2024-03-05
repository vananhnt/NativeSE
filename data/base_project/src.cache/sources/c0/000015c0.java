package android.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.accessibility.AccessibilityEvent;

/* loaded from: ViewParent.class */
public interface ViewParent {
    void requestLayout();

    boolean isLayoutRequested();

    void requestTransparentRegion(View view);

    void invalidateChild(View view, Rect rect);

    ViewParent invalidateChildInParent(int[] iArr, Rect rect);

    ViewParent getParent();

    void requestChildFocus(View view, View view2);

    void recomputeViewAttributes(View view);

    void clearChildFocus(View view);

    boolean getChildVisibleRect(View view, Rect rect, Point point);

    View focusSearch(View view, int i);

    void bringChildToFront(View view);

    void focusableViewAvailable(View view);

    boolean showContextMenuForChild(View view);

    void createContextMenu(ContextMenu contextMenu);

    ActionMode startActionModeForChild(View view, ActionMode.Callback callback);

    void childDrawableStateChanged(View view);

    void requestDisallowInterceptTouchEvent(boolean z);

    boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z);

    boolean requestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

    void childHasTransientStateChanged(View view, boolean z);

    void requestFitSystemWindows();

    ViewParent getParentForAccessibility();

    void notifySubtreeAccessibilityStateChanged(View view, View view2, int i);

    boolean canResolveLayoutDirection();

    boolean isLayoutDirectionResolved();

    int getLayoutDirection();

    boolean canResolveTextDirection();

    boolean isTextDirectionResolved();

    int getTextDirection();

    boolean canResolveTextAlignment();

    boolean isTextAlignmentResolved();

    int getTextAlignment();
}